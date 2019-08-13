from matplotlib import pyplot

import matplotlib.pyplot as plt 
import numpy as np

data = np.loadtxt('totalChainSystem.txt', usecols=[0])
x1 = data #feature set  
#plt.plot(x3, y3, marker = 'd', color = 'g', label = "All cloud") 
#plt.xticks(np.arange(0, 100, 10))
data1 = np.loadtxt('totalPowerSystem.txt', usecols=[0])
y1 = data1
datax2 = np.loadtxt('totalChainAcceptDP.txt', usecols=[0])
datay2 = np.loadtxt('totalPowerDP.txt', usecols=[0])

xGR = np.loadtxt('totalChainAcceptGR.txt', usecols=[0])
yGR = np.loadtxt('totalPowerGR.txt', usecols=[0])

plt.plot(x1, y1, color = 'r', label = "Proposed algorithm")
plt.plot(datax2, datay2, color = 'g', label = "DP algorithm")
plt.plot(xGR, yGR, color = 'b', label = "Greedy algorithm")
#plt.plot(x2, y2, marker = 's', color = 'b', label = "DP algorithm")
#plt.yticks(np.arange(0.0, 1.0, 0.1))
# naming the x axis 
plt.xlabel("Number of Accepted Chains")
# naming the y axis 
plt.ylabel('Total Power Consumption (W)') 
# giving a title to my graph 
plt.title('Relationship between power consumption and number of accepted chains') 
  
# show a legend on the plot 
plt.legend() 
plt.grid(True)
# function to show the plot 
plt.show() 
