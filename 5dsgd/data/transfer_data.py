f = open("sample.csv")

transfer = dict()
idx = 0

for l in f:
    word, doc, score = l.strip().split(",")
    if word in transfer:
        print "%s,%s,%s" %(transfer[word],doc,score)
    else:
        transfer[word] = idx
        print "%s,%s,%s" %(transfer[word],doc,score)
        idx += 1


f.close()
