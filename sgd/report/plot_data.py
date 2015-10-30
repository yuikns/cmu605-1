import re
import matplotlib.pyplot as plt
import math


#y_list = [547, 427, 470, 575, 775, 804]
#y_list = [298, 172, 221, 311, 307, 339]
#x_list = [0, 0.000001, 0.00001, 0.0001, 0.001, 0.01, 0.1, 0.2, 0.3, 0.5, 1]
#y_list = [0.775718, 0.778381, 0.810781 , 0.814281, 0.817085, 0.802001, 0.794471, 0.787505, 0.794736, 0.786761, 0.760488]

x_list = [100, 10000, 100000]
y_list = [0.726941, 0.779731, 0.788013]

plt.plot(x_list, y_list)
plt.xlabel("the value of mu")
plt.ylabel("the accuracy")
plt.show()

