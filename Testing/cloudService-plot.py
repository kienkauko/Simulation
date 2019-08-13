from matplotlib import pyplot

import matplotlib.pyplot as plt 
import numpy as np

data = np.loadtxt('totalDenOffload.txt', usecols=[0])
x1 = data #feature set totalDenOffload
data1 = np.loadtxt('totalChainSystem.txt', usecols=[0])
y1 = data1

datax2 = np.loadtxt('totalChainOffloadDP.txt', usecols=[0])
datay2 = np.loadtxt('totalChainAcceptDP.txt', usecols=[0])

dataAC = np.loadtxt('totalChainAcceptGR.txt', usecols=[0])
serviceAC = np.loadtxt('totalChainOffloadGR.txt', usecols=[0])

plt.plot(y1, x1, color = 'r', label = "Proposed algorithm")
plt.plot(datay2, datax2, color = 'k', label = "DP algorithm")
plt.plot(dataAC, serviceAC, color = 'b', label = "Greedy algorithm")
#plt.plot(x2, y2, marker = 's', color = 'b', label = "DP algorithm") 
#plt.plot(x3, y3, marker = 'd', color = 'g', label = "All cloud") 
#plt.xticks(np.arange(0, 100, 10))
#plt.yticks(np.arange(0.0, 1.0, 0.1))
# naming the x axis 
plt.xlabel("Number of Accepted Chains")
# naming the y axis 
plt.ylabel('Number of Services at Cloud')
# giving a title to my graph 
plt.title('Relationship between number of services at cloud and number of chains')
  
# show a legend on the plot 
plt.legend() 
plt.grid(True)
# function to show the plot 
plt.show() 
