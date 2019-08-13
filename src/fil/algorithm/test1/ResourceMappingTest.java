package fil.algorithm.test1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
//import java.util.LinkedList;
//import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import fil.resource.virtual.*;
import fil.resource.substrate.*;

public class ResourceMappingTest {
	final static int NUM_PI = 100;
	final static int NUM_SERVER = 32;
	final static int REQUEST = 1;
	final static int K_PORT_SWITCH = 8; // 3 server/edge switch
	final static int ARRAY_SIZE = 100; //100
	//final static int numChainMax = 5;
	static int num_pi;
	static int num_server;
	static ArrayList<Integer> ArrayChain;
	static double limitLatency = 5;
	private static Map<Rpi, ArrayList<SFC>> listRpiSFC;
	private static int numSFCTotal;
	
	public ResourceMappingTest() {;
		listRpiSFC = new HashMap<>();
		numSFCTotal = 0;
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
	
	public static void main(String[] args) {
		num_pi = NUM_PI;
		num_server = NUM_SERVER;
		
//		recently add
		Topology topo = new Topology();
		FatTree fatTree = new FatTree();
		topo = fatTree.genFatTree(K_PORT_SWITCH);
		setListRpiSFC(new HashMap<>());
		numSFCTotal = 0;
		MappingServer mappingServer = new MappingServer();
		
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
		ArrayList<Double> totalEdgePowerSystem = new ArrayList<Double>();
		ArrayList<Double> totalChainAcceptance = new ArrayList<Double>();
		ArrayList<Integer> totalChainSystem = new ArrayList<Integer>();
		ArrayList<Integer> totalDecOffload = new ArrayList<Integer>();
		ArrayList<Integer> totalDenOffload = new ArrayList<Integer>();
		ArrayList<Integer> totalChainReject = new ArrayList<Integer>();
		ArrayList<Double> totalLoadEdge = new ArrayList<Double>();
		ArrayList<Double> totalBwEdge = new ArrayList<Double>();
		ArrayList<Double> totalrpiAcceptance = new ArrayList<Double>();
		
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
		
		double [] sumLoadNumPi= new double [REQUEST];  ///for storing load of numPi
		double [] sumBwNumPi = new double [REQUEST];
		Arrays.fill(sumLoadNumPi, 0);
		Arrays.fill(sumBwNumPi, 0);

		double acceptance = 0;
		int rejectLatency = 0;
		int numRequest = 0; // number of request
		
		ArrayList<Rpi> listRpi = new ArrayList<Rpi>(); //create  a number of Pi
		for(int i = 0; i < NUM_PI; i++ ) {
			Rpi rpi = new Rpi();
			listRpi.add(rpi);
		}
		
		ArrayList<PhysicalServer> listServer = new ArrayList<PhysicalServer>();
		for(int i = 0; i < num_server; i++) {
			PhysicalServer physicalServer = new PhysicalServer();
			listServer.add(physicalServer);
		}
//		PhysicalServer physicalServer = new PhysicalServer();
		
		PoissonDistribution getRequest = new PoissonDistribution();
		Random rand = new Random();
		
		int piMapping = 0;
		int countRequest = 0;
		
		Capture capture = new Capture();
		Decode decode = new Decode();
		Density density = new Density();
		ReceiveDensity receive = new ReceiveDensity();
		
		int rpiAccept = 0;
		double rpiAcceptance = 0;
		int totalRequestPi = 0;
		REQUEST_LOOP:
		while (numRequest < REQUEST) { //////////////////////////////////////////////////////////////////////////////////////////
		int numPiReceive = rand.nextInt(100);		
		
		
		int sumAllRequest = 0;
		int sumMapRequest = 0;
		double sumLoadPi = 0;
		
		double sumBwPi = 0;
		double totalPowerPi=0;
		
		if(numPiReceive == 0) {continue;}  //back to new request for number of Pi
		
		double [] loadEdgeNumPi = new double [numPiReceive];
		double [] bwEdgeNumPi = new double [numPiReceive];
		int [] checkPi = new int [numPiReceive];
		
		
		
		for (int j = 0; j < numPiReceive; j++) { //change i < 1 to i < num_pi for mapping every pi/////////////////////////////////
			
			System.out.println("Number of Pis receive request "+ numPiReceive +"\n");
			
			int i = rand.nextInt(99); // choose specific Pi that will receive request
			checkPi [j] = i;
			int flag = 0;
			//check random number
			for(int checkpi = 0; checkpi < j && checkpi != j; checkpi++) {
				if(checkPi[checkpi] == i) {
					j--;
					flag=1;
					break;
				}
			}
			
			if(flag == 1) {
				continue;
			}
			
			//sfc request to server
			ArrayList<SFC> listSFC = new ArrayList<SFC>(); //create  a number of Pi
			MappingServer mappingServerTemp = new MappingServer();
			MappingServer mappingServerFinal = new MappingServer();
			mappingServerTemp = mappingServer;
			Topology topoTemp = new Topology();
			Topology topoFinal = new Topology();
			
			topoTemp = topo;
			
			double totalCpuChain = 0;
			double totalBandwidth = 0;
			double totalPower = 0;
			double minPower = 1000000;
			double finalCPUServer = 0;
			double finalUsedPowerPi = 0;
			double finalPower = 0;
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
			System.out.println("\n \n \n Pi " + (i+1) +" start mapping!!!! \n");
			System.out.println("Cpu " + (i+1) +" before mapping : "+ listRpi.get(i).getRemainCPU() + "\n");
			
			/////////////////////Poisson Distribution Request///////////////////////////////////////
			double cpu_temp = listRpi.get(i).getRemainCPU();
			double bw_temp = listRpi.get(i).getRemainBandwidth();
			double resource_condition = (cpu_temp + bw_temp)/200;
			double lamda = 0;
			if (resource_condition >= 0 && resource_condition <= 0.13) lamda = 0;
			else if (resource_condition > 0.13 && resource_condition <= 0.25) lamda = 1;
			else if (resource_condition > 0.25 && resource_condition <= 0.5) lamda = 2;
			else if (resource_condition > 0.5 && resource_condition <= 1.0) lamda = 3;
			else {
				System.out.println("Error occurs at lamda process \n");
				return;
			}
			int requestNumChain = 0;
			do {
				requestNumChain = getRequest.sample(lamda);
			} while (requestNumChain > 3);
				
			///int requestNumChain = getRequest.sample(lamda);
			if (requestNumChain != 0) totalRequestPi++;
			
			sumAllRequest += requestNumChain;
			/////////////////////////////////////////////////////////////////////////////////////////
			numChain = requestNumChain;
			
			//System.out.println("Total chain request for Pi "+(i+1)+" is " +totalChainRequest[i]+"\n");
			System.out.println("Pi number " + (i+1)+ " with " +numChain+ " chain \n");
			
			boolean doneFlag = false;
			boolean doneMap = false;
			int remapLoopCount = 0;
			int numChain_temp = 0;
			int offDecode = 0;
			int offDensity = 0;
			MAP_LOOP:
			while (doneFlag == false) {//////////////////////////////////////////////////////TRIAL LOOP////////////////////////
				
				if (piState[i] == false) { // this Pi cannot map more chain
					System.out.println("Pi number "+(i+1)+" is out of order ...\n");
					rejectPi = 1;
					break;
				}
				
				rejectServer = 0;
				rejectPi = 0;
				offDecode = 0;
				offDensity = 0;
				
				if (remapping == true && doneMap == false){ //remapping
										
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
					}
					
					Map<LinkPhyEdge, Double> listBandwidthPhyEdge = mappingServerTemp.getLinkMapping().getListBandwidthPhyEdge();
					mappingServerTemp.getServiceMapping().resetRpiSFC(listRpiSFC.get(listRpi.get(i)), listBandwidthPhyEdge);; // reset mapped chains of Pi_index
					
					listRpi.get(i).resetCPU();
					cpu_server_used[i] = 0;
					remapping_count++; // count number of remapping in rasppi i 
					doneMap = true;
					remapLoopCount ++; // count number of remapping times in MAP_LOOP
				}
				
				if(numChain == 0)  break MAP_LOOP; // if no chain need to be mapped then no need to run MAP 
				
				OFFDECODE_LOOP:
				for (offDecode = 0; offDecode <= numChain; offDecode++) {
					
					OFFDENSITY_LOOP:
					for(offDensity = offDecode; offDensity >= offDecode && offDensity <= numChain; offDensity++) {
						
						ArrayList<SFC> listSFCTemp = new ArrayList<>();
//						set position for all chains of a pi
						int numOffDecode = 0;
						int numOffDensity = 0;
						
						totalBandwidth = (offDecode)*capture.getBandwidth() + (offDensity - offDecode)*decode.getBandwidth() + (numChain - offDensity)*density.getBandwidth();
						cpuPi = numChain*capture.getCpu_pi() + (numChain - offDecode)*decode.getCpu_pi() + (numChain - offDensity)*density.getCpu_pi();
						//listRpi.get(i).setUsedCPU(cpuPi);
						//cpuPi *= cpu_cof[i];
						//System.out.println("cpu pi calculate is " + cpuPi + " numchain " + numChain + "  \n");
						cpuPiExpect = cpuPi + listRpi.get(i).getUsedCPU();
						
						
						for(int numSFC = 0; numSFC < numChain; numSFC++ ) {
							SFC sfc = new SFC(String.valueOf(numSFCTotal), i);
							sfc.setSfcID(i);
							listSFCTemp.add(sfc);
							numSFCTotal++;
							//System.out.println("Size of list " +listSFCTemp.size()+"\n");
							listSFCTemp.get(numSFC).setServicePosition(capture, true);
							listSFCTemp.get(numSFC).setServicePosition(receive, false);
							if(numOffDecode < offDecode) {
								listSFCTemp.get(numSFC).setServicePosition(decode, false);
								System.out.println("Off Decode may lan???");
							}
							if(numOffDensity < offDensity) {
								listSFCTemp.get(numSFC).setServicePosition(density, false);
								System.out.println("Off Density may lan????????????");
							}
							numOffDecode++;
							numOffDensity++;
						}
						
						
						if(totalBandwidth > listRpi.get(i).getRemainBandwidth()) {
							
							if (remapping == true ) {
								//remapping = false; // turn off remapping
								numChain --; // remapping is not working then reduce number of chain
								if (numChain < finalNumChain) { // prevent system continue loop even final result has been selected
									break MAP_LOOP;
								} else {
									doneMap = false;
									break OFFDECODE_LOOP; // try to map with lower num of chain
								}
							} else if (finalNumChain == 0) {
								remapping = true;
								doneMap = false;
//								System.out.println("Trying to remap !!! \n ");
								break OFFDECODE_LOOP; // try to remap
							} else {
								//rejectBandwidth = 1;
								break MAP_LOOP; // ofload ngu, out mapping
							}

						} else if(cpuPi > listRpi.get(i).getRemainCPU()){
							System.out.println("Request number " + numRequest + " !!! \n");
							if (offDecode == numChain && remapping == false  ) {
								remapping = true; //turn off remap
								break OFFDECODE_LOOP;
							} else
							continue; // try to offload service 
						} else {
							System.out.println("Thoa man gan het dieu kien! \n");
							//System.out.println("Numchain " + numChain + " \n");
							System.out.println("CPU Pi ask is " + cpuPi + " remain " + listRpi.get(i).getRemainCPU() + " !!! \n");
							System.out.println("CPU Pi used is " + (cpuPi + listRpi.get(i).getUsedCPU()) + " bandwidth used is " + (totalBandwidth + listRpi.get(i).getUsedBandwidth()) + " !!! \n");
							System.out.println("Bandwidth remain is " + listRpi.get(i).getRemainBandwidth() + "\n");
							//Routing algorithm
							//latency
							latency = 1.52296153 + 0.003317*(totalBandwidth + listRpi.get(i).getUsedBandwidth()) 
							+ 0.00537148*(cpuPi + listRpi.get(i).getUsedCPU());
							if (latency < limitLatency && numChain >= totalNumChain) { //QoS and acceptance rate priority
//								 now go through power test
								totalNumChain = numChain;
								powerChainPi = totalNumChain*capture.getPower() +(totalNumChain - offDecode)*decode.getPower() + (totalNumChain - offDensity)*density.getPower();
								
								mappingServerTemp = mappingServer;
								topoTemp = topo;
//								run mapping server
								topoTemp = mappingServerTemp.runMapping(listSFCTemp, topoTemp);
								
								if(mappingServerTemp.isSuccess()) {
									/////////////
									System.out.println("Mapping successed!!\n");
									powerChainServer = mappingServerTemp.getPower();
								} else {
									///////////////
									System.out.println("Mapping failed\n");
									if (numChain != 0) {
										numChain --;
										numSFCTotal--;
										break OFFDECODE_LOOP;
									}
									else {
										rejectServer = 1;
										break MAP_LOOP;
									}
								}
								
								System.out.println("Power server "+ powerChainServer +" Power Pi: " +powerChainPi+ "\n");
								totalPower = powerChainPi + powerChainServer;
								System.out.println("Total sysyem power is " + totalPower + " \n");
								if (totalPower <= minPower) {
									System.out.println("Map Ok!!!! " + "\n" + "CPU Pi used " + cpuPiExpect +"  remain  " + (100 - cpuPiExpect) + " ....... \n");
									doneFlag = true; // used to break MAP_LOOP
									minPower = totalPower;
									
									if (totalNumChain <= numChain_temp) { // case after remap nothing changes
										piState[i] = false;
									}
									topoFinal = topoTemp;
									mappingServerFinal = mappingServerTemp;
									finalNumChain = totalNumChain;
									finalChainReject = requestNumChain - finalNumChain;
									finalCPUServer = cpuServer;
									finalUsedPowerPi = powerChainPi;
									finalBandWidth = totalBandwidth;
									finalPowerServer = powerChainServer;
									finalPower = totalPower;
									finalCPUPi = cpuPi;
									finalOffDecode = offDecode;
									finalOffDensity = offDensity;
									
									//mapped listSFC
									
									listSFC = listSFCTemp;
								}
							} else {
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
				
				
				totalChainMap[i] += finalNumChain; // for remapping purpose
				cpu_server_used[i] += finalCPUServer; // CPU server used by RPI i
				totalOffDecPi[i] += finalOffDecode; //
				totalOffDenPi[i] += (finalOffDensity+finalNumChain); // 
				totalChainRequest[i] += requestNumChain; //
				totalPowerEdge[i] += finalUsedPowerPi;
				
				totalDecOffLoad_temp += finalOffDecode;
				totalDenOffLoad_temp += (finalOffDensity+finalNumChain);
				
				totalPowerEdge_temp += finalUsedPowerPi; //calculate System Power
				totalPowerSystem_temp = totalPowerEdge_temp + finalPowerServer;
				
				sumMapRequest += finalNumChain; //num of accepted request of a Pi
				
//				gan lai mapping server
				mappingServer = mappingServerFinal;
				topo = topoFinal;
				topoTemp = topoFinal;
				
				System.out.println("Final power finalPower " + finalPower);
//				System.out.println("sumLoadPi get Used "+ listRpi.get(i).getUsedCPU() + " \n");
//				System.out.println("sumLoadPi ..." +sumLoadPi+" finalCpuPi " + finalCPUPi + "\n");
				totalChainSystem_temp += finalNumChain; // num of accepted request all over the systen
				totalChainReject_temp += finalChainReject;
				
				if(!listRpiSFC.isEmpty()) {
					ArrayList<SFC> listSFCTemp = new ArrayList<>();
					listSFCTemp = listRpiSFC.get(listRpi.get(i));
					listSFC.addAll(listSFCTemp);
					listRpiSFC.put(listRpi.get(i), listSFC);
				}
			}
			
			if (rejectServer == 1) {
				finalChainReject = requestNumChain; // reject all chain
				totalChainReject_temp += finalChainReject;
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
			totalEdgePowerSystem.add(count, totalPowerEdge_temp);
			totalChainSystem.add(count, totalChainSystem_temp);
			totalChainReject.add(count, totalChainReject_temp);
			
			count++;
			countRequest++;
			
//			calculate total CPU, bandwidth for number of Pi
//			array for saving
			loadEdgeNumPi[j] = listRpi.get(i).getUsedCPU();
			bwEdgeNumPi[j] = listRpi.get(i).getUsedBandwidth();		
			System.out.println("Pi number " + (+1) + " is out of duty \n");
			
		} //end Rpi for loop
		
		//a for loop to get result of CPU, bw of number of Pis
		for(int j=0; j < numPiReceive; j++) {
			sumLoadNumPi[numRequest] += (loadEdgeNumPi[j]/numPiReceive);
			sumBwNumPi[numRequest] += (bwEdgeNumPi[j]/numPiReceive);
		}
		
		//a for loop to get result total cpu bw of all Pi
		for (int index = 0; index < 100; index++) {
			sumLoadPi += listRpi.get(index).getUsedCPU();
			sumBwPi += listRpi.get(index).getUsedBandwidth();
		}
	//calculate power and number of chain relation
		totalLoadEdge.add(numRequest,(sumLoadPi/(100)));
		totalBwEdge.add(numRequest,(sumBwPi/100));
		System.out.println("sumLoadPi ..." + sumLoadPi + "\n");
		acceptance = (sumMapRequest*1.0)/sumAllRequest; //after a request
		totalChainAcceptance.add(numRequest, acceptance);
		numRequest++;
	} // end while loop (request)
	
	try {
		write_integer("./Testing/totalDecOffload.txt",totalDecOffload);
		write_integer("./Testing/totalDenOffload.txt",totalDenOffload);
		write_double("./Testing/totalPowerSystem.txt",totalPowerSystem);
		write_double("./Testing/totalEdgePowerSystem.txt", totalEdgePowerSystem);
		write_double("./Testing/totalLoadEdge.txt",totalLoadEdge);
		write_double("./Testing/totalBwEdge.txt",totalBwEdge);
		write_double("./Testing/totalChainAcceptance.txt",totalChainAcceptance);
		write_double("./Testing/sumLoadNumPi.txt", sumLoadNumPi);
		write_double("./Testing/sumBwNumPi.txt", sumBwNumPi);
		write_integer("./Testing/totalChainSystem.txt",totalChainSystem);
		write_integer("./Testing/totalChainSystem.txt",totalChainSystem);
		write_integer("./Testing/totalChainReject.txt",totalChainReject);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
//	System.out.println("Server remain " + physicalServer.getRemainCPU() + " \n");
	}

	public static Map<Rpi, ArrayList<SFC>> getListRpiSFC() {
		return listRpiSFC;
	}

	public static void setListRpiSFC(Map<Rpi, ArrayList<SFC>> listRpiSFC) {
		ResourceMappingTest.listRpiSFC = listRpiSFC;
	}
}
