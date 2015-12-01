import re
import matplotlib.pyplot as plt
import math


#y_list = [547, 427, 470, 575, 775, 804]
#y_list = [298, 172, 221, 311, 307, 339]
#x_list = [0, 0.000001, 0.00001, 0.0001, 0.001, 0.01, 0.1, 0.2, 0.3, 0.5, 1]
#y_list = [0.775718, 0.778381, 0.810781 , 0.814281, 0.817085, 0.802001, 0.794471, 0.787505, 0.794736, 0.786761, 0.760488]

x_list = [0.5,0.6,0.7,0.8,0.9]
y_list = [0.8406, 0.8242, 0.8249, 0.8282, 0.8218]

plt.plot(x_list, y_list)
plt.xlabel("running iterations")
plt.ylabel("reconstruction error")
plt.show()

