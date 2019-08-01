package fil.algorithm.test1;

import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import fil.resource.virtual.*;
import fil.resource.substrate.*;

public class DynamicProgram {
	final static int NUM_PI = 100;
	final static int NUM_SERVER = 27; // 2 cluster = 54 servers 
	final static int numChainMax = 5;
	final static int NUM_REQUEST = 8;
	final static int K_SERVERS_SWITCH = 3; // 3 server/edge switch
	static int num_pi;
	static int num_server;
	static ArrayList<Integer> ArrayChain;
	static double limitLatency = 2.3;
	
	public DynamicProgram() {
		num_pi = NUM_PI;
		num_server = NUM_SERVER;
	}
	
//	public void setPositionMultiChain(int numService, Service service, ArrayList<SFC> listChain) {
//		for (int i = 1; i <= numService; i++) {
//			listChain.addAllsetServicePosition(service, 1);
//		}
//	}
	
	public static void write_integer (String filename, int [] x) throws IOException{ //write result to file
		 BufferedWriter outputWriter = null;
		 outputWriter = new BufferedWriter(new FileWriter(filename));
 		for (int i = 0; i < x.length; i++) {
			// Maybe:
			//outputWriter.write(x.get(i));
			// Or:
			outputWriter.write(Integer.toString(x[i]));
			outputWriter.newLine();
 		}
		outputWriter.flush();  
		outputWriter.close();  
	}

	public static void write_double (String filename, double [] x) throws IOException { //write result to file
		 BufferedWriter outputWriter = null;
		 outputWriter = new BufferedWriter(new FileWriter(filename));
 		for (int i = 0; i < x.length; i++) {
			// Maybe:
//			outputWriter.write(x[i]);
			// Or:
			outputWriter.write(Double.toString(x[i]));
			outputWriter.newLine();
 		}
		outputWriter.flush(); 
		outputWriter.close();  
	}
	
	
	
	public static void main(String[] args) throws IOException {
		int numChain = 0; //capture
		int numCapture= 0;
		int numDecode = 0; //decode
		int numDensity = 0; //density
		int offload_temp = 0;
		double latency;
//		int j = 0;
		int reject=0;
		double totalCpuChain = 0;
		double totalBandwidthChain = 0, totalCPUServer=0;
		double totalPower, minPower, finalUsedPowerServer, finalUsedPowerPi = 0;
		double powerChainServer = 0, powerChainPi = 0;
		double cpuServer = 0;
		int decPosition = 0, denPosition = 0;
		double [] acceptanceRatio = new double [NUM_REQUEST];
		double [] totalLoadEdge = new double [NUM_REQUEST];
		double [] finalPower = new double [10000];
		double [] totalBandwidth = new double [NUM_REQUEST];
		int [] totalChain = new int[100];
		int [] numAccept =new int [100];
		int [] numReject = new int [100];
		double cpuAllServer=0;
		int [] numChainAccept= new int [10000];
		int [] offload= new int [10000];
		
		for(int i=0; i<NUM_REQUEST;i++) {
			acceptanceRatio[i] = 0;
			totalLoadEdge[i] = 0;
			finalPower[i] = 0;
			numChainAccept[i] = 0;
			offload[i] =0;
			totalBandwidth[i] = 0;
		}
		
		for(int i=0; i<NUM_PI;i++) {
			totalChain[i] = 0;
			numAccept[i] = 0;
			numReject[i] = 0;
			
		}
		
		ArrayList<Rpi> listRpi = new ArrayList<Rpi>(); //create  a number of Pi
		for(int i = 0; i < NUM_PI; i++ ) {
			Rpi rpi = new Rpi();
			listRpi.add(rpi);
		}
		
//		ArrayList<PhysicalServer> listServer = new ArrayList<PhysicalServer>();
//		for(int i = 0; i < NUM_SERVER; i++) {
		PhysicalServer physicalServer = new PhysicalServer();
//			listServer.add(physicalServer);
//		}
		double totalPowerTemp=0;
		int totalNumChainPi = 0;
		int countRequest;
		int k = 0;
		int count = 0;
		while (k < NUM_REQUEST) { // number of request
		
		ChainRequest chainRequest = new ChainRequest(0, 3); // random number of chain from 0 to 2
		ArrayChain = new ArrayList<Integer>();
		ArrayChain = chainRequest.getNumChain();
		
		double totalUsedBandwidth =0;
		double finalPowerTemp=0;
		double totalLoadEdgeTemp=0;
		double totalBandwidthTemp=0;
		ArrayList<SFC> listChain = new ArrayList<>(); //number of chain in a RPi
		
		
		for (int i = 0; i < NUM_PI; i++) { //change i < 1 to i < num_pi for mapping every pi
			
			boolean  accept = false;
			numCapture=0;
			numDecode=0;
			numDensity=0;
			totalCPUServer=0;
			finalPowerTemp=0;
			
			System.out.println("\n\nCpu " + (i+1) +" before mapping : "+ listRpi.get(i).getUsedCPU() + "\n");
			System.out.println("CPu pi " + (i+1) + " remain: "+listRpi.get(i).getRemainCPU()+"\n");
			numChain = ArrayChain.get(i);
			System.out.println("Request for Pi number " + (i+1) + " is: " + numChain + "\n");
			
			totalChain[k] += numChain;
			
//			if(numChain == 0) {continue;}
			
			for (int currentChain = 0; currentChain < numChain; currentChain++) {
				// declare service objects here
				accept = false;
				Capture capture = new Capture();
				Decode decode = new Decode();
				Density density = new Density();
				ReceiveDensity receive = new ReceiveDensity();
				minPower = 100000;
				double powerServer=0;
				double finalCPUChainPi=0;
				double finalBandwidthChainPi=0;
				totalCPUServer=0;
				//other variables
				totalCpuChain = 0;
				totalBandwidthChain = 0;
				//add 1 more object sfc
				listChain.add(new SFC());  
				
				decPosition = 1; //decode position
				denPosition = 1; //density position
				
				for (int trial = 0; trial < 3; trial ++){ // run 3 cases of chain
					if(trial == 1) {
						decPosition = 1; denPosition = 0;
					}
					else if(trial == 2) {
						decPosition = 0; denPosition = 0;
					}
					totalCpuChain = listChain.get(currentChain).getCpuDD(decPosition, denPosition);
					System.out.println("CPUchain is" + totalCpuChain + "..\n");
					totalBandwidthChain = listChain.get(currentChain).getBandwidthDD(decPosition, denPosition);
					//now go through CPU bW test
					if(totalCpuChain <= listRpi.get(i).getRemainCPU() && totalBandwidthChain <= listRpi.get(i).getRemainBandwidth()){
						// now go through latency test
						System.out.println("Everything went aight so far..\n");
						latency = 1.52296153 + 0.003317*(totalBandwidthChain + listRpi.get(i).getUsedBandwidth()) 
						+ 0.00537148*(totalCpuChain + listRpi.get(i).getUsedCPU());
						if (latency < limitLatency) {
							// now go through power test
							
							double expectedPower;
							powerChainPi = capture.getPower() + decPosition*decode.getPower() + denPosition*density.getPower();
							cpuServer = receive.getCpu_server() + (1 - decPosition)*decode.getCpu_server() + (1 - denPosition)*density.getCpu_server();
							expectedPower = physicalServer.calCurrentPowerServer(cpuServer);  //expected, not current, only the min could be plus to become current
//							powerChainServer = physicalServer.setExpectedPower(cpuServer); 
							totalPower = powerChainPi + expectedPower;
							System.out.println("totalPower is " + totalPower + " Min power is "+ minPower + "..\n");
							// only the min will be chosen
							if (totalPower < minPower) {
								minPower = totalPower;
								finalPowerTemp = totalPower;
								powerServer = expectedPower;
								finalCPUChainPi = totalCpuChain;
								finalBandwidthChainPi = totalBandwidthChain;
								finalUsedPowerServer = powerChainServer;
								finalUsedPowerPi = powerChainPi;
								totalCPUServer = cpuServer;
								accept = true;
								if(decPosition*denPosition == 0) { // record if offload happens
									offload_temp +=2;
								}
							}
						}
						else {
							continue;
						}
					}
					else {
						//reject = 1;
//						System.out.println("Case " + decPosition +" " + denPosition + " CPU BW unacceptable! \n");
						numReject[i]++;
						continue;
					}
				}
				
				if(accept == true) {
					
					numAccept[k]++;
					numCapture++;
					totalNumChainPi ++;
					numChainAccept[count] = totalNumChainPi;
					numDecode += decPosition;
					numDensity += denPosition;
					offload_temp++; // receivedensity
					
					offload[count] = offload_temp;
					
					listRpi.get(i).setUsedBandwidth(finalBandwidthChainPi);
					System.out.println("BW  is : "+ totalBandwidthChain +" ...\n");
					System.out.println("Power server is : "+powerServer+" ...\n");
					physicalServer.setPowerServer(powerServer);
					totalPowerTemp = physicalServer.getPowerServer();
					
					//adding power of switch in DC
					totalUsedBandwidth += listRpi.get(i).getUsedBandwidth();
					double powerSwitch = 39.9*Math.ceil(totalUsedBandwidth/(K_SERVERS_SWITCH*1000));
					double powerSwitchCore = 39.9*Math.ceil(totalUsedBandwidth/(2*K_SERVERS_SWITCH*1000));
					
					System.out.println("Power of total edge switch "+powerSwitch+"\n");
					System.out.println("Power of total core switch "+powerSwitchCore+"\n");
					
					totalPowerTemp += (powerSwitch*2 + powerSwitchCore + finalUsedPowerPi); // powerSwitch*2 + powerSwitchCore +
					finalPower[count] = totalPowerTemp;
					count ++;
					System.out.println("Power after 1 chain "+totalPowerTemp+" finalPowerTemp "+ finalPowerTemp +" \n");
					
					//set resource used
					listRpi.get(i).setUsedCPU(finalCPUChainPi);
					System.out.println("Done 1 chain!!!!!!!!!!!!!!!!!!!!!!! ...\n");
					listRpi.get(i).setCurrentPower(finalUsedPowerPi);
					physicalServer.setUsedCPUServer(totalCPUServer);
				}
			}
			
			cpuAllServer = physicalServer.getUsedCPUServer();
			totalLoadEdgeTemp += (listRpi.get(i).getUsedCPU());
			totalBandwidthTemp += listRpi.get(i).getUsedBandwidth();
			System.out.println("CPU all server "+cpuAllServer+"...\n");
			System.out.println("CPU aPi remain "+listRpi.get(i).getRemainCPU()+"...\n");
			System.out.println("BW Pi remain "+listRpi.get(i).getRemainBandwidth()+"...\n");
			System.out.println("num capture " + numCapture + "\n");
			System.out.println("num decode " + numDecode + "\n");
			System.out.println("num density " + numDensity + "\n");
			System.out.println("cpu rpi "+ (i+1) +" used after mapping " + listRpi.get(i).getUsedCPU() + "\n");
			////Kien add 29.7.2019///////////////////////
			
			
		} // end Pi -loop
		totalLoadEdge[k] = (totalLoadEdgeTemp/100);
		totalBandwidth[k] = totalBandwidthTemp/100;
		acceptanceRatio[k] = numAccept[k]*1.0/totalChain[k];
		
		System.out.println("cpu server used " + physicalServer.getUsedCPUServer() + "\n");
		System.out.println("Final power after 1 request ... "+totalPowerTemp+"\n");
		System.out.println("acceptance ratio request " + (k+1) + " is " + acceptanceRatio[k]);
		//k++;
		System.out.println("CPU all server " + cpuAllServer + " \n");
		write_double("./plot/Chain-power/totalPowerDP.txt",finalPower);
		write_double("./plot/acceptance-ratio/acceptanceRatioDP.txt",acceptanceRatio);
		write_double("./plot/acceptance-ratio/totalLoadEdgeDP.txt",totalLoadEdge);
		write_double("./plot/acceptance-ratio/totalBandwidthDP.txt",totalBandwidth);
		write_integer("./plot/Chain-power/totalChainAcceptDP.txt",numChainAccept);
		write_integer("./plot/CloudService-Chain/totalChainAcceptDP.txt",numChainAccept);
		write_integer("./plot/CloudService-Chain/totalChainOffloadDP.txt",offload);
//			write_integer("totalDenOffload.txt",totalDenOffload);
//			write_double("totalPowerSystem.txt",totalPowerSystem);
		k++;
		
		
		} // request loop
	}
}