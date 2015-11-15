#spark-submit --driver-memory 2G temp.py 30 10 30 0.9 ./data/RCV1.small_train.csv ./data/w1.csv ./data/h1.csv
spark-submit --driver-memory 4G dsgd_mf.py 30 3 30 0.9 ./data/RCV1.small_train.csv ./data/w.csv ./data/h.csv
#spark-submit --driver-memory 2G temp.py 10 5 10 0.95 ./data/transfer.csv ./data/w.csv ./data/h.csv
