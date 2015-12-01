mu=$1;
for ((i=1;i<20;i++));
#do shuf ../data/train;
do shuf ../data/abstract.small.train;
#done | java -Xmx1024m LR2 100000 0.5 $mu 19 500000 ../data/test
done | java -Xmx1024m LR2 10000 0.5 $mu 19 10000 ../data/abstract.small.test
