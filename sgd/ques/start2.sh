mu=$1;
for ((i=1;i<20;i++));
do shuf ../data/train;
done | java -Xmx1024m LR2 100000 0.5 $mu 19 500000 ../data/test
