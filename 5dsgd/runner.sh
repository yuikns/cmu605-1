N=$1;
spark-submit --driver-memory 4G dsgd_mf.py 20 10 50 $N ./data/RCV1.small_train.csv ./data/w3.csv ./data/h3.csv
