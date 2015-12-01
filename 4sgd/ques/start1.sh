for ((i=1;i<20;i++));
do shuf ../data/abstract.small.train;
done | java -Xmx128m LR1 10000 0.5 0.1 20 10000 ./data/abstract.small.test
