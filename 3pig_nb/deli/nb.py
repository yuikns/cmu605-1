from guineapig import *
import re
import math

# supporting routines can go here

# split and tokenize doc into words
def doc_token(line):
    words = re.split("\\s+", line)
    if len(words) > 2:
        labels = words[1].split(",")
        for label in labels:
            yield ("Y=*", 1)
            yield ("Y="+label, 1)
            for i in range(2, len(words)):
                words[i] = re.sub('\\W', '', words[i])
                yield ("Y="+label+",X="+words[i], 1)
            yield ("Y="+label+",X*", len(words)-2)

# convert event count to k,v pairs
def convert_event(eventCount):
    event_parts = eventCount[0].split(",X=")
    if len(event_parts) > 1:
        word = event_parts[1]
        value = (event_parts[0], eventCount[1])
        return (word, value)
    else:
        return eventCount

# generate word docid
def word_docid(line):
    words = line.strip().split("\t")
    for word in words[2].split():
        word = re.sub("\\W",'',word)
        yield (word, words[0])


# classification
def classify(joinedDoc):
    alpha = 1
    doc_index = joinedDoc[0][0]
    words = joinedDoc[0][1]
    paras = joinedDoc[1][1]
    total_label, words_dict, label_dict = transfer_paras(paras)
    label_score = dict()
    for label in label_dict:
        label_cnt = label_dict[label]
        label_word_cnt = words_dict[label+",X*"]
        label_score[label] = math.log(label_cnt) - math.log(total_label)
        fenmu = math.log(label_word_cnt + 100000 * alpha)
        for word in words:
            existed_label_dict = transfet_existed_label(word[1])
            if label in existed_label_dict:
                temp = existed_label_dict[label] + alpha
                label_score[label] += math.log(temp) - fenmu
            else:
                label_score[label] += math.log(alpha) - fenmu

    max_label = "null"
    max_score = -10000000.0
    for l in label_score:
        if label_score[l] > max_score:
            max_label = l
            max_score = label_score[l]

    return doc_index, max_label[2:], max_score

# transfer paras into map
def transfer_paras(paras):
    total_label = 0
    words_dict = dict()
    label_dict = dict()
    for p in paras:
        if "Y=*" == p[0]:
            total_label = p[1]
        elif "X*" in p[0]:
            words_dict[p[0]] = p[1]
        else:
            label_dict[p[0]] = p[1]
    return total_label, words_dict, label_dict

def transfet_existed_label(words):
    result = dict()
    for w in words:
        result[w[0]] = w[1]
    return result

#always subclass Planner
class NB(Planner):
    # params is a dictionary of params given on the command line.
    # e.g. trainFile = params['trainFile']
    params = GPig.getArgvParams()

    # step 1, generate event counts from tranning data
    eventCount = ReadLines(params.get('trainFile')) | Flatten(by=doc_token) | Group(by=lambda (s,n):s, reducingTo=ReduceTo(int, lambda accum,(s,n):accum+n))

    # step 2, convert event counts to k,v pairs
    kvPairs = Map(eventCount, by=convert_event) | Group(by=lambda pair: pair[0], reducingTo=ReduceTo(list, by=lambda accum,val:accum+[val[1]]))

    # get parameters
    parameters = Filter(eventCount, by=lambda pair:"X=" not in pair[0]) | Group(by=lambda x:'c', reducingTo=ReduceTo(list, lambda accum,x:accum+[x]))

    # step 3, generate requests from testing file
    testRequest = ReadLines(params.get('testFile')) | Flatten(by=word_docid)

    # step 4, group join the flatten test requests with k,v pairs
    joinedTable = Join(Jin(kvPairs, by=lambda (key,value):key), Jin(testRequest, by=lambda (key,value):key)) | Group(by=lambda (count,request):request[1], reducingTo=ReduceTo(list, by=lambda accum, (count, request):accum+[count]))

    joinedDoc = Augment(joinedTable, sideview=parameters, loadedBy=lambda v:GPig.onlyRowOf(v))

    # step 5, classify the doc with labels

    output = Map(joinedDoc, by=classify)

# always end like this
if __name__ == "__main__":
    NB().main(sys.argv)

# supporting routines can go here
