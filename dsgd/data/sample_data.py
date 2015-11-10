f = open("temp.csv")

result = dict()

# loop file to get the top
for l in f:
    t = l.strip().split(",")
    idx = int(t[0])
    if idx not in result:
        result[idx] = 1
    else:
        result[idx] += 1
f.close()


# sort ti get top 20 words
s = sorted(result.items(), key=lambda x: x[1], reverse=True)
s = s[:20]
s = set([x[0] for x in s])

f = open("temp.csv")
print s
# loop again to produce the result
for l in f:
    t = l.strip().split(",")
    idx = int(t[0])
    if idx in s:
        print l.strip() 

f.close()

