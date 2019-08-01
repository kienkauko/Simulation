#from matplotlib import pyplot

#import matplotlib.pyplot as plt 
import numpy as np

data = np.loadtxt('totalChainSystem.txt', usecols=[0])
x1 = data #feature set 
print(x1)
data1 = np.loadtxt('totalChainReject.txt', usecols=[0])
y1 = data1
z = x1 - y1
print(z)
