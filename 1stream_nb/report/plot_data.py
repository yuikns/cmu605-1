import re
import matplotlib.pyplot as plt
import math

f = open('abstract.train')

word_set = set()

doc_list = []
word_list = []
sqrt_doc_list = []
cnt = 0

for l in f:
    temp = l.strip().split("\t")
    for x in temp[1].split(" "):
        word_set.add(x)

    if cnt % 2000 < 0.00000001:
        sqrt_doc_list.append(math.sqrt(cnt))
        word_list.append(len(word_set))
    cnt += 1

plt.plot(sqrt_doc_list, word_list)
plt.ylabel("the vocabulary size")
plt.xlabel("the sqrt doc size")
plt.show()
f.close()

