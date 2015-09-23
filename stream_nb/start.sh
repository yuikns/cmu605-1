export LC_ALL=C
make clean
make
#cat abstract.tiny.train | java -Xmx128m NBTrain | sort -k1,1 | java -Xmx128m \
#MergeCounts | java -Xmx128m NBTest abstract.tiny.test
time cat ./data/abstract.small.train | java -Xmx128m NBTrain | sort -k1,1 | java -Xmx128m \
MergeCounts | java -Xmx4096m NBTest ./data/abstract.small.test
