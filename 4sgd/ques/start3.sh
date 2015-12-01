N=$1;
for ((i=1;i<20;i++));
do shuf ../data/train;
done | java -Xmx1024m LR2 $N 0.5 0.01 10 500000 ../data/test
