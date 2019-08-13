from matplotlib import pyplot

import matplotlib.pyplot as plt 
import numpy as np

data = np.loadtxt('followcpu.txt', delimiter=',')
x1 = data[:,0] #feature set 
y1 = data[:,1] #label set
x2 = data[:,2] #feature set 
y2 = data[:,3] #label set
x3 = data[:,4] #feature set 
y3 = data[:,5] #label set
# plotting the line 1 points  
yDP = np.loadtxt('acceptanceRatioDP.txt', usecols=[0])
xDP = np.loadtxt('totalLoadEdgeDP.txt', usecols=[0])
bwDP = np.loadtxt('totalBandwidthDP.txt', usecols=[0])
zDP = (bwDP + xDP)/200

x1 = np.loadtxt('totalLoadEdge.txt', usecols=[0])
y1 = np.loadtxt('totalAcceptance.txt', usecols=[0])
bw = np.loadtxt('totalBwEdge.txt', usecols=[0])
z1 = (bw + x1)/200

x2 = np.loadtxt('totalLoadEdgeGR.txt', usecols=[0])
y2 = np.loadtxt('acceptanceRatioGR.txt', usecols=[0])
bwGR = np.loadtxt('totalBandwidthGR.txt', usecols=[0])
z2 = (bwGR + x2)/200

plt.plot(z1, y1, color = 'r', label = "Proposed algorithm") 
plt.plot(z2, y2, color = 'b', label = "Greedy algorithm") 
plt.plot(zDP, yDP, color = 'g', label = "DP algorithm") 
plt.xticks(np.arange(0.0, 1.0, 0.1))
plt.yticks(np.arange(0.0, 1.0, 0.1))
# naming the x axis 
plt.xlabel("Pi's Resource Utilization") 
# naming the y axis 
plt.ylabel('Acceptance Ratio') 
# giving a title to my graph 
plt.title('Acceptance ratio per chains') 
  
# show a legend on the plot 
plt.legend() 
plt.grid(True)
# function to show the plot 
plt.show() 
