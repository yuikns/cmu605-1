time cat ./data/abstract.small.train | java -Xmx128m LR 10000 0.5 0.1 20 1000 \
./data/abstract.small.test
