import re
import matplotlib.pyplot as plt
import math


x_list = [1, 2, 4, 6, 8, 10]
#y_list = [547, 427, 470, 575, 775, 804]
y_list = [298, 172, 221, 311, 307, 339]

plt.plot(x_list, y_list)
plt.xlabel("the number of reducers")
plt.ylabel("the wall time")
plt.show()

