from guineapig import *
import re

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
                words[i] = words[i].replace("\\W", "")
                yield ("Y="+label+",X="+words[i], 1)
            yield ("Y="+label+",X=*", len(words)-2)

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
        word = word.replace("\\W","")
        yield (word, words[0])



#always subclass Planner
class NB(Planner):
    # params is a dictionary of params given on the command line.
    # e.g. trainFile = params['trainFile']
    params = GPig.getArgvParams()

    # step 1, generate event counts from tranning data
    # output format: Y=y,X=x cnt
    eventCount = ReadLines(params.get('trainFile')) | Flatten(by=doc_token) | Group(by=lambda (s,n):s, reducingTo=ReduceTo(int, lambda accum,(s,n):accum+n))

    # step 2, convert event counts to k,v pairs
    # output format: x, [Y=y1 1, Y=y2 2]
    kvPairs = Map(eventCount, by=convert_event) | Group(by=lambda pair: pair[0], reducingTo=ReduceTo(list, by=lambda accum,val:accum+[val[1]]))

    # step 3, generate requests from testing file
    # output format: x, ~docId1
    testRequest = ReadLines(params.get('testFile')) | Flatten(by=word_docid)

    # step 4, join the flatten test requests with k,v pairs
    joinedTable = Join(Jin(kvPairs, by=lambda (key,value):key), Jin(testRequest, by=lambda (key,value):key))

    # step 5, classify the doc with labels


    output = joinedTable

# always end like this
if __name__ == "__main__":
    NB().main(sys.argv)

# supporting routines can go here
