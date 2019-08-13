from matplotlib import pyplot

import matplotlib.pyplot as plt 
import numpy as np

# plotting the line 1 points  
#yDP = np.loadtxt('acceptanceRatioDP.txt', usecols=[0])
#xDP = np.loadtxt('totalLoadEdgeDP.txt', usecols=[0])
#bwDP = np.loadtxt('totalBandwidthDP.txt', usecols=[0])
#zDP = (bwDP + xDP)/200

x1 = np.loadtxt('sumLoadNumPi.txt', usecols=[0])
y1 = np.loadtxt('totalChainAcceptance.txt', usecols=[0])
bw1 = np.loadtxt('sumBwNumPi.txt', usecols=[0])
z1 = (bw1 + x1)/200

x2 = np.loadtxt('sumLoadNumPiGR.txt', usecols=[0])
y2 = np.loadtxt('acceptanceRatioGR.txt', usecols=[0])
bw2 = np.loadtxt('sumBwNumPiGR.txt', usecols=[0])
z2 = (bw2 + x2)/200

x3 = np.loadtxt('sumLoadNumPiDP.txt', usecols=[0])
y3 = np.loadtxt('acceptanceRatioDP.txt', usecols=[0])
bw3 = np.loadtxt('sumBwNumPiDP.txt', usecols=[0])
z3 = (bw3 + x3)/200

plt.plot(z1, y1, color = 'r', label = "Proposed algorithm") 
plt.plot(z2, y2, color = 'b', label = "Greedy algorithm") 
plt.plot(z3, y3, color = 'k', label = "DP algorithm") 

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
