list1 = []
f1 = open('data/abstract.small.test')
lines1 = f1.readlines()
for l1 in lines1 :
    list1.append(l1.strip().split("\t")[0])
f1.close()

list2 = []
f2 = open('result/result1')
line2 = f2.readlines()
read_index = 0
for l2 in line2 :
    if read_index > 4:
        list2.append(l2.strip().split("\t")[0])
    read_index += 1
f2.close()

right = 0
index = 0
for w1, w2 in zip(list1, list2) :
    if w2 in w1:
        #print index
        right += 1
    index += 1

print float(right) / float(len(list1))
