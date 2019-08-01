package fil.algorithm.test1;

import java.util.ArrayList;
import java.util.Arrays;
//import java.util.LinkedList;
//import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import fil.resource.virtual.*;
import fil.resource.substrate.*;

public class ResourceMapping {
	final static int NUM_PI = 100;
	final static int NUM_SERVER = 32;
	final static int REQUEST = 8;
	final static int K_SERVERS_SWITCH = 3; // 3 server/edge switch
	final static int ARRAY_SIZE = 100; //100
	//final static int numChainMax = 5;
	static int num_pi;
	static int num_server;
	static ArrayList<Integer> ArrayChain;
	static double limitLatency = 2.3;
	
	public ResourceMapping() {
//		num_pi = NUM_PI;
//		num_server = NUM_SERVER;
	}
	
//	public void setPositionMultiChain(int numService, Service service, ArrayList<SFC> listChain) {
//		for (int i = 1; i <= numService; i++) {
//			listChain.addAllsetServicePosition(service, 1);
//		}
//	}
	public static void write_integer (String filename, ArrayList<Integer> x) throws IOException{ //write result to file
 		 BufferedWriter outputWriter = null;
 		 outputWriter = new BufferedWriter(new FileWriter(filename));
  		for (int i = 0; i < x.size(); i++) {
			// Maybe:
			//outputWriter.write(x.get(i));
			// Or:
			outputWriter.write(Integer.toString(x.get(i)));
			outputWriter.newLine();
  		}
		outputWriter.flush();  
		outputWriter.close();  
	}

	public static void write_double (String filename, ArrayList<Double> x) throws IOException { //write result to file
 		 BufferedWriter outputWriter = null;
 		 outputWriter = new BufferedWriter(new FileWriter(filename));
  		for (int i = 0; i < x.size(); i++) {
			// Maybe:
//			outputWriter.write(x[i]);
			// Or:
			outputWriter.write(Double.toString(x.get(i)));
			outputWriter.newLine();
  		}
		outputWriter.flush(); 
		outputWriter.close();  
	}
	
	public static void main(String[] args) {
		num_pi = NUM_PI;
		num_server = NUM_SERVER;
		
		int numChain = 0; //capture
		int remapping_count = 0;
		double latency;
		int reject=0;
		
		int [] totalChainMap = new int [ARRAY_SIZE]; //total mapping chain of RPI i
		int [] totalChainRequest = new int [ARRAY_SIZE];
		int [] numAccept = new int [ARRAY_SIZE];
		int [] numReject = new int [ARRAY_SIZE];
		int [] governor = new int [ARRAY_SIZE]; //governor state of RPI i
		int [] totalOffDecPi = new int [ARRAY_SIZE];
		int [] totalOffDenPi = new int [ARRAY_SIZE];
		boolean [] piState = new boolean [ARRAY_SIZE];
		double [] totalPowerEdge = new double [ARRAY_SIZE];
		//double [] totalPowerServer = new double [100];
		//double [] totalPowerSystem = new double [100];
		
		Arrays.fill(piState, true);
		Arrays.fill(totalChainMap, 0);
		Arrays.fill(totalChainRequest, 0);
		Arrays.fill(numAccept, 0);
		Arrays.fill(numReject, 0);
		Arrays.fill(governor, 0);
		

		ArrayList<Double> totalPowerSystem = new ArrayList<Double>();
		ArrayList<Double> totalAcceptance = new ArrayList<Double>();
		ArrayList<Integer> totalChainSystem = new ArrayList<Integer>();
		ArrayList<Integer> totalDecOffload = new ArrayList<Integer>();
		ArrayList<Integer> totalDenOffload = new ArrayList<Integer>();
		ArrayList<Integer> totalChainReject = new ArrayList<Integer>();
		ArrayList<Double> totalLoadEdge = new ArrayList<Double>();
		ArrayList<Double> totalBwEdge = new ArrayList<Double>();

		int totalDecOffLoad_temp = 0;
		int totalDenOffLoad_temp = 0;
		double totalPowerSystem_temp = 0;
		int totalChainSystem_temp = 0;
		int totalChainReject_temp = 0;
		double totalPowerEdge_temp = 0;
		double finalPowerServer = 0;

		int count = 0;

		double [] cpu_cof = new double [ARRAY_SIZE];
		Arrays.fill(cpu_cof, 1);
		double [] power_cof = new double [ARRAY_SIZE];
		Arrays.fill(power_cof, 1);
		double [] cpu_server_used = new double [ARRAY_SIZE];
		double [] cpu_pi_used = new double [ARRAY_SIZE];

		double acceptance = 0;
		int rejectLatency = 0;
		int numRequest = 0; // number of request
		
		ArrayList<Rpi> listRpi = new ArrayList<Rpi>(); //create  a number of Pi
		for(int i = 0; i < NUM_PI; i++ ) {
			Rpi rpi = new Rpi();
			listRpi.add(rpi);
		}
		
//		ArrayList<PhysicalServer> listServer = new ArrayList<PhysicalServer>();
//		for(int i = 0; i < num_server; i++) {
		PhysicalServer physicalServer = new PhysicalServer();
//			listServer.add(physicalServer);
//		}
		
		
		
		int piMapping = 0;
		int countRequest = 0;
		//double sumLoadPi = 0;
		
		
		REQUEST_LOOP:
		while (numRequest < REQUEST) { //////////////////////////////////////////////////////////////////////////////////////////
			
		int sumAllRequest = 0;
		int sumMapRequest = 0;
		double sumLoadPi = 0;
		double sumBwPi = 0;
		double totalPowerPi=0;
		
		ChainRequest chainRequest = new ChainRequest(0, 3);
		ArrayChain = new ArrayList<Integer>(100);
		ArrayChain = chainRequest.getNumChain();  //return array with 100 objects
		
		for (int i = 0; i < ArrayChain.size(); i++) {
			sumAllRequest += ArrayChain.get(i);
		}
		System.out.println("Request number " + numRequest + " ! \n");

		for (int i = 0; i < NUM_PI; i++) { //change i < 1 to i < num_pi for mapping every pi/////////////////////////////////
			double totalCpuChain = 0;
			double totalBandwidth = 0;
			double totalPower = 0;
			double minPower = 1000000;
			double finalCPUServer = 0;
			double finalUsedPowerPi = 0;
			double finalTotalPower = 0;
			double finalBandWidth = 0;
			double powerChainServer = 0;
			int finalChainReject = 0;
			double powerChainPi = 0;
			double cpuServer = 0;
			double finalCPUPi = 0;
			double cpuPi = 0;
			double cpuPiExpect = 0;
			int totalNumChain = 0;
			
			int rejectServer = 0;
			int rejectPi = 0;
			int finalNumChain = 0;
			int finalOffDecode = 0;
			int finalOffDensity = 0;
			reject=0;
			boolean remapping = false;
			
			System.out.println("\n\nCpu " + (i+1) +" before mapping : "+ listRpi.get(i).getRemainCPU() + "\n");
			
			int requestNumChain = ArrayChain.get(i); //????
			numChain = requestNumChain;
			
			Capture capture = new Capture();
			Decode decode = new Decode();
			Density density = new Density();
			ReceiveDensity receive = new ReceiveDensity();
			System.out.println("Total chain request for Pi number "+(i+1)+" is " +totalChainRequest[i]+"\n");
			System.out.println("Pi number " + (i+1)+ " with " +numChain+ " chain \n");
			
			boolean doneFlag = false;
			boolean doneMap = false;
			// int trial = 0;
			int remapLoopCount = 0;
			int numChain_temp = 0;
			MAP_LOOP:
			while (doneFlag == false) {//////////////////////////////////////////////////////TRIAL LOOP////////////////////////
				
				if (piState[i] == false) {
					System.out.println("Pi number "+(i+1)+" is out of order ...\n");
					rejectPi = 1;
					break;
				}
				
				
				rejectServer = 0;
				rejectPi = 0;
				
				if (remapping == true && doneMap == false){ //remapping
					
					//numChain += totalChainMap[i]; //sum of all previous successful mapping chain
					
					if (remapLoopCount == 0) {
						numChain_temp = totalChainMap[i];
						numChain += totalChainMap[i]; //sum of all previous successful mapping chain
						requestNumChain += totalChainRequest[i]; // sum of all request mapping chain
						sumMapRequest -= totalChainMap[i];
						totalChainSystem_temp -= totalChainMap[i];
						totalDecOffLoad_temp -= totalOffDecPi[i];
						totalDenOffLoad_temp -= totalOffDenPi[i];
						totalPowerEdge_temp -= totalPowerEdge[i];
						
						totalOffDecPi[i] =0; // sai
						totalOffDenPi[i] =0;
						totalPowerEdge[i]=0;
						totalChainMap[i] = 0; // reset all mapped chains before
						physicalServer.setUsedCPUServer(-cpu_server_used[i]);
					}
					
					//totalChainMap[i] = 0; // reset all mapped chains before
					//physicalServer.setUsedCPUServer(-cpu_server_used[i]);
					listRpi.get(i).resetCPU();
					cpu_server_used[i] = 0;
//					System.out.println("Pi number " +(i+1)+ " is remapping.. with " +  numChain + " chains " + "\n\n");
					remapping_count++; // count number of remapping in rasppi i 
					//doneFlag = true; // remapping once only
					doneMap = true;
					remapLoopCount ++; // count number of remapping times in MAP_LOOP
				}
				
				if(numChain == 0)  break MAP_LOOP; // if no chain need to be mapped then no need to run MAP 
//				System.out.println("Rerun mapping loop ....... \n");
				OFFDECODE_LOOP:
				for (int offDecode = 0; offDecode <= numChain; offDecode++) {
					
//					System.out.println("Offdecode is " + offDecode + " ....... \n");
					OFFDENSITY_LOOP:
					for(int offDensity = offDecode; offDensity >= offDecode && offDensity <= numChain; offDensity++) {
						
						cpuServer = numChain*receive.getCpu_server() + offDensity*density.getCpu_server() + offDecode*decode.getCpu_server();
//						System.out.println("CPU Server test is " + cpuServer + " ....... \n");
						totalBandwidth = (offDecode)*capture.getBandwidth() + (offDensity - offDecode)*decode.getBandwidth() + (numChain - offDensity)*density.getBandwidth();
						cpuPi = numChain*capture.getCpu_pi() + (numChain - offDecode)*decode.getCpu_pi() + (numChain - offDensity)*density.getCpu_pi();
						//listRpi.get(i).setUsedCPU(cpuPi);
						//cpuPi *= cpu_cof[i];
						cpuPiExpect = cpuPi + listRpi.get(i).getUsedCPU();

						if(cpuServer > physicalServer.getRemainCPU()){
//							System.out.println("CPU Server expected used " + cpuServer +"  remain  " + physicalServer.getRemainCPU() + " ....... \n");
							
							if (numChain != 0) {
								numChain --;
//								System.out.println("Numchain has been reduced because of CPU Server " + numChain + "\n");
								
								break OFFDECODE_LOOP;
							}
							else {
								rejectServer = 1;
								break MAP_LOOP;
							}
							//numChain --;
							
						}
						
						if(totalBandwidth > listRpi.get(i).getRemainBandwidth()) {
//							System.out.println("Bandwidth expected used " + totalBandwidth +"  remain  " + listRpi.get(i).getRemainBandwidth() + " ....... \n");
							
							if (remapping == true ) {
								//remapping = false; // turn off remapping
								numChain --; // remapping is not working then reduce number of chain
//								System.out.println("Num chain has been reduced to " + numChain +  "!!! \n ");
								if (numChain < finalNumChain) { // prevent system continue loop even final result has been selected
									break MAP_LOOP;
								}
								else {
									doneMap = false;
									break OFFDECODE_LOOP; // try to map with lower num of chain
								}
							}
							else if (finalNumChain == 0) {
								remapping = true;
								doneMap = false;
//								System.out.println("Trying to remap !!! \n ");
								break OFFDECODE_LOOP; // try to remap
							}
							else {
								//rejectBandwidth = 1;
								break MAP_LOOP; // ofload ngu, out mapping
							}

						}
						else if(cpuPi > listRpi.get(i).getRemainCPU()){
							//listRpi.get(i).setUsedCPU(-cpuPi);
//							System.out.println("Pi number " + (i+1) + " is not enough CPU! \n");
//							System.out.println("CPU Pi ask is " + cpuPi + " remain " + listRpi.get(i).getRemainCPU() + " !!! \n");
//							System.out.println("CPU Pi used is " + listRpi.get(i).getUsedCPU() + " !!! \n");
							System.out.println("Request number " + numRequest + " !!! \n");
//							System.out.println("Offdensity number " + offDensity + " numChain " + numChain + " !!! \n");
							if (offDecode == numChain && remapping == false  ) {
								remapping = true; //turn off remap
//								System.out.println("Trying to remap because CPU Pi isnt enough !!! \n ");
								break OFFDECODE_LOOP;
							}
							else
							continue; // try to offload service 
						}
						
						else {
							System.out.println("Thoa man gan het dieu kien! \n");
							System.out.println("CPU Pi ask is " + cpuPi + " remain " + listRpi.get(i).getRemainCPU() + " !!! \n");
							System.out.println("CPU Pi used is " + (cpuPi + listRpi.get(i).getUsedCPU()) + " bandwidth used is " + (totalBandwidth + listRpi.get(i).getUsedBandwidth()) + " !!! \n");
							System.out.println("Bandwidth remain is " + listRpi.get(i).getRemainBandwidth() + "\n");
							//Routing algorithm
							//latency
							latency = 1.52296153 + 0.003317*(totalBandwidth + listRpi.get(i).getUsedBandwidth()) 
							+ 0.00537148*(cpuPi + listRpi.get(i).getUsedCPU());
							if (latency < limitLatency && numChain >= totalNumChain) { //QoS and acceptance rate priority
								// now go through power test
								totalNumChain = numChain;
								powerChainPi = totalNumChain*capture.getPower() +(totalNumChain - offDecode)*decode.getPower() + (totalNumChain - offDensity)*density.getPower();
								//powerChainPi *= power_cof[i]; //multiply with coefficient
								double powerSwitch = 39.9*Math.ceil(totalBandwidth/(K_SERVERS_SWITCH*1000));
								double powerSwitchCore = 39.9*Math.ceil(totalBandwidth/(2*K_SERVERS_SWITCH*1000));
								
								powerChainServer = physicalServer.calCurrentPowerServer(cpuServer) + powerSwitch*2 + powerSwitchCore;  //expected, not officially, only the min could be plus to become current

								totalPower = powerChainPi + powerChainServer;
//								System.out.println("Total Power Pi used " + powerChainPi + "   ToTal Power "  + totalPower + " ....... \n");
								// only the min will be chosen
								if (totalPower <= minPower) {
									System.out.println("Map Ok!!!! " + "\n" + "CPU Pi used " + cpuPiExpect +"  remain  " + (100 - cpuPiExpect) + " ....... \n");
									//System.out.println("Total Power Pi used " + listRpi.get(i).getCurrentPower()  + "   ToTal Power "  + totalPower + " ....... \n");
//									System.out.println("Pi num " + (i+1) + " off Density " + offDensity + " ....... \n");
									doneFlag = true; // used to break MAP_LOOP
									minPower = totalPower;
									
									if (totalNumChain <= numChain_temp) { // case after remap nothing changes
										piState[i] = false;
									}
									
									//finalTotalPower = totalPower;
									finalNumChain = totalNumChain;
									finalChainReject = requestNumChain - finalNumChain;
									//acceptance = (finalNumChain*1.0)/(requestNumChain*1.0);
									finalCPUServer = cpuServer;
									finalUsedPowerPi = powerChainPi;
									finalBandWidth = totalBandwidth;
									finalPowerServer = powerChainServer;
									finalCPUPi = cpuPi;
									finalOffDecode = offDecode;
									finalOffDensity = offDensity;
								}
							}
							else {
								System.out.println("Latency failed! \n");
								if (offDecode == numChain ) { // last loop
									break MAP_LOOP;
								}
								continue;
							}
						}
					}	 // OFF_DENSITY LOOP
				} // OFF_DECODE LOOP
				
			} // MAP_LOOP
			
			if(finalNumChain != 0 || piState[i] == false) { // new set of chain has been mapped
				
				listRpi.get(i).setUsedCPU(finalCPUPi); // change CPU pi-server
				listRpi.get(i).setUsedBandwidth(finalBandWidth); //change Bandwidth used by Pi
				listRpi.get(i).setCurrentPower(finalUsedPowerPi);
				physicalServer.setUsedCPUServer(finalCPUServer);
//				System.out.println("sServer get Used "+ physicalServer.getUsedCPUServer() + " \n");
				totalChainMap[i] += finalNumChain; // for remapping purpose
				cpu_server_used[i] += finalCPUServer; // CPU server used by RPI i
				totalOffDecPi[i] += finalOffDecode; // sai
				totalOffDenPi[i] += (finalOffDensity+finalNumChain); // sai
				totalChainRequest[i] += requestNumChain; // sai
				totalPowerEdge[i] += finalUsedPowerPi;
				//totalPowerServer[i] = finalPowerServer;
				
				totalDecOffLoad_temp += finalOffDecode;
				totalDenOffLoad_temp += (finalOffDensity+finalNumChain);
				
				totalPowerEdge_temp += finalUsedPowerPi; //calculate System Power
				totalPowerSystem_temp = totalPowerEdge_temp + finalPowerServer;
				
				sumMapRequest += finalNumChain; //num of accepted request of a Pi
//				sumLoadPi += listRpi.get(i).getUsedCPU();
//				sumBwPi += listRpi.get(i).getUsedBandwidth();
				physicalServer.setPowerServer(finalPowerServer);
				
				System.out.println("sumLoadPi get Used "+ listRpi.get(i).getUsedCPU() + " \n");
				System.out.println("sumLoadPi ..." +sumLoadPi+" finalCpuPi " + finalCPUPi + "\n");
				totalChainSystem_temp += finalNumChain; // num of accepted request all over the systen
				totalChainReject_temp += finalChainReject;
				//System.out.println("Power " + (i+1) + " is:" +totalPowerSystem_temp +" \n");
			}
			
			if (rejectServer == 1) {
				System.out.println("Server is out of resource " + physicalServer.getRemainCPU() + " ...Ending. \n");
				finalChainReject = requestNumChain; // reject all chain
				totalChainReject_temp += finalChainReject;
				if(physicalServer.getRemainCPU() < 4) {
					System.out.println("CPU server is out of resource. End of mapping...");
					break REQUEST_LOOP;
				}
			}
			if (rejectPi == 1) { // this means CPU is also not enough
				System.out.println(" Raspberry Pi number " + (i+1) + " is out of resource \n");
				System.out.println(" Raspberry Pi number " + (i+1) + " CPU Pi " + listRpi.get(i).getRemainCPU() +"\n");
				finalChainReject = requestNumChain; // reject all chain
				totalChainReject_temp += finalChainReject;
			}
			
			totalDecOffload.add(count, totalDecOffLoad_temp); // sum of all case offloading decode
			totalDenOffload.add(count, totalDenOffLoad_temp);
			totalPowerSystem.add(count, totalPowerSystem_temp);
			totalChainSystem.add(count, totalChainSystem_temp);
			totalChainReject.add(count, totalChainReject_temp);
			
			
			/////calculate acceptance ratio
//			totalPowerPi += listRpi.get(i).getCurrentPower();
			sumLoadPi += listRpi.get(i).getUsedCPU();
			sumBwPi += listRpi.get(i).getUsedBandwidth();
//			totalPowerSystem.add(count, (totalPowerPi+physicalServer.getPowerServer()));
			count++;
			countRequest++;
		} //end Rpi for loop
		
	//calculate power and number of chain relation
		
		totalLoadEdge.add(numRequest,(sumLoadPi/(100)));
		totalBwEdge.add(numRequest,(sumBwPi/100));
		System.out.println("sumLoadPi ..." + sumLoadPi + "\n");
//		acceptance = (finalNumChain*1.0)/requestNumChain;
		acceptance = (sumMapRequest*1.0)/sumAllRequest; //after a request
		totalAcceptance.add(numRequest, acceptance);
		numRequest++;
	} // end while loop (request)
	
	try {
		write_integer("./plot/CloudService-Chain/totalDecOffload.txt",totalDecOffload);
		write_integer("./plot/CloudService-Chain/totalDenOffload.txt",totalDenOffload);
		write_double("./plot/Chain-power/totalPowerSystem.txt",totalPowerSystem);
		write_double("./plot/acceptance-ratio/totalLoadEdge.txt",totalLoadEdge);
		write_double("./plot/acceptance-ratio/totalBwEdge.txt",totalBwEdge);
		write_double("./plot/acceptance-ratio/totalAcceptance.txt",totalAcceptance);
		write_integer("./plot/Chain-power/totalChainSystem.txt",totalChainSystem);
		write_integer("./plot/CloudService-Chain/totalChainSystem.txt",totalChainSystem);
		write_integer("./KienHoaAlgorithm/totalChainReject.txt",totalChainReject);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println("Server remain " + physicalServer.getRemainCPU() + " \n");
	}
}
	// write_integer("totalDenOffload.txt",totalDenOffload);
	// write_integer("totalDenOffload.txt",totalDenOffload);
	// write_integer("totalDenOffload.txt",totalDenOffload)
