package fil.algorithm.test1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import fil.resource.substrate.PhysicalServer;
import fil.resource.substrate.Rpi;
import fil.resource.virtual.Capture;
import fil.resource.virtual.ChainRequest;
import fil.resource.virtual.Decode;
import fil.resource.virtual.Density;
import fil.resource.virtual.ReceiveDensity;

public class AllCloud {
	final static int NUM_PI = 100;
	//final static int NUM_SERVER = 32;
	final static int REQUEST = 8;
	final static int K_SERVERS_SWITCH = 3; // 3 server/edge switch
	//final static int numChainMax = 5;
	static int num_pi;
	static int num_server;
	static ArrayList<Integer> ArrayChain;
	static double limitLatency = 2.3;
	
	public AllCloud() {
		//num_pi = NUM_PI;
		//num_server = NUM_SERVER;
	}
	
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
		
		ArrayList<Double> totalAcceptance = new ArrayList<Double>();
		ArrayList<Integer> totalMapChainSystem = new ArrayList<Integer>();
		ArrayList<Integer> totalRejectChainSystem = new ArrayList<Integer>();
		ArrayList<Integer> totalNumChainSystem = new ArrayList<Integer>();
		ArrayList<Double> totalPowerSystem = new ArrayList<Double>();
		ArrayList<Double> totalPowerPi = new ArrayList<Double>();
		ArrayList<Double> totalPowerServer = new ArrayList<Double>();
		ArrayList<Double> totalLoadPi = new ArrayList<Double>();
		ArrayList<Integer> totalServiceCloud = new ArrayList<Integer>();
		
		boolean [] statePi = new boolean [100];
		Arrays.fill(statePi, true);
		
		int totalNumChainSystem_temp = 0;
		int totalMapChainSystem_temp = 0;
		int totalRejectChainSystem_temp = 0;
		double totalPowerSystem_temp = 0;
		double totalPowerServer_temp = 0;
		double totalCpuServer_temp = 0;
		double totalLoadPi_temp = 0;

		ArrayList<Rpi> listRpi = new ArrayList<Rpi>(); //create  a number of Pi
		for(int i = 0; i < NUM_PI; i++ ) {
			Rpi rpi = new Rpi();
			listRpi.add(rpi);
		}
		
		PhysicalServer physicalServer = new PhysicalServer();
		
		Capture capture = new Capture();
		Decode decode = new Decode();
		Density density = new Density();
		ReceiveDensity receive = new ReceiveDensity();
		
		int numRequest = 0;
		boolean stateServer = true;
		int count = 0;
		int totalServiceOffload=0;

		REQUEST_LOOP:
			while (numRequest < REQUEST) { //////////////////////////////////////////////////////////////////////////////////////////
			
			double totalPowerPi_temp = 0;
			int totalMapChain = 0;
			int totalNumChain = 0;
		//	double loadPi = 0;
			
			ChainRequest chainRequest = new ChainRequest(0, 2);
			ArrayChain = new ArrayList<Integer>(100);
			ArrayChain = chainRequest.getNumChain();  //return array with 100 objects
			
			//System.out.println("Request number " + numRequest + " ! \n");
			
			PI_LOOP:
			for (int i = 0; i < NUM_PI; i++) {
				
				double cpuServer = 0;
				double cpuPi = 0;
				double bw = 0;
				double latency = 0;
				
				int numChain = ArrayChain.get(i); //????
				int numChainRequest = numChain;
				totalNumChain += numChain; // of request
				totalNumChainSystem_temp += numChain; // of all over system
				
				System.out.println("Pi number " + i + " request number " + numRequest + " with " + numChain +" chains  \n");
				
				// check cpu, bw edge && cpu server
				// if false - numChain --, 
					// if numChain == 0; State == false; break;
					// else rerun
				// else true - set cpu, bw, power
				WHILE_LOOP:
				while (true) { ////////////////////////////////////////////////////////////////////////////
					
					if (stateServer == false) {
						System.out.println("Server failed, reject all! \n");
						totalRejectChainSystem_temp += numChainRequest;
						break;
					}
					
					if (statePi[i] == false) {
						System.out.println("This Pi is out of order, reject all! \n");
						totalRejectChainSystem_temp += numChainRequest;
						break;
					}
					if (numChain == 0) {
						break;
					}
					
					cpuServer = numChain*(decode.getCpu_server() + density.getCpu_server() + receive.getCpu_server());
					System.out.println("CPU server need is " + cpuServer + " \n");
					cpuPi = numChain*capture.getCpu_pi();
					bw = numChain*capture.getBandwidth();
					// routing latency here
					latency = 1.52296153 + 0.003317*(bw + listRpi.get(i).getUsedBandwidth()) 
							+ 0.00537148*(cpuPi + listRpi.get(i).getUsedCPU());
					//cpuPi = numChain*(capture.getCpu_pi() + decode.getCpu_pi() + density.getCpu_pi());
					if (cpuPi > listRpi.get(i).getRemainCPU() || bw > listRpi.get(i).getRemainBandwidth() || 
						cpuServer > physicalServer.getRemainCPU() || latency > limitLatency) {
						numChain --;
						if (numChain == 0 && cpuServer > physicalServer.getRemainCPU()) {
							stateServer = false;
							totalRejectChainSystem_temp += numChainRequest;
							break;
						}
						if (numChain == 0 && (cpuServer <= physicalServer.getRemainCPU() || latency > limitLatency)) {
							statePi[i] = false;
							totalRejectChainSystem_temp += numChainRequest;
							break;
						}
			
					}
					else {
						// every constraints are valid
						totalRejectChainSystem_temp += (numChainRequest - numChain);
						
						double powerPi = numChain*capture.getPower();
						listRpi.get(i).setCurrentPower(powerPi);
						
						//cpuServerTotal += cpuServer;
						//powerChainPi *= power_cof[i]; //multiply with coefficient
					//	powerServer = physicalServer.setCurrentPowerServer(cpuServer);  //expected, not officially, only the min could be plus to become current
					//	totalPower = listRpi.get(i).getCurrentPower() + powerServer;
						
						listRpi.get(i).setUsedCPU(cpuPi); // change CPU pi-server
						listRpi.get(i).setUsedBandwidth(bw); //change Bandwidth used by Pi
						
						totalPowerPi_temp += listRpi.get(i).getCurrentPower(); // global - double 
						totalCpuServer_temp += cpuServer; // global - double - no print
						
						double powerSwitch = 39.9*Math.ceil(bw/(K_SERVERS_SWITCH*1000));
						double powerSwitchCore = 39.9*Math.ceil(bw/(2*K_SERVERS_SWITCH*1000));
						totalPowerServer_temp = physicalServer.calCurrentPowerServer(cpuServer)+ powerSwitch*2 + powerSwitchCore; //global - double
						System.out.println("Total CPU server " + totalCpuServer_temp + "total Power server" + totalPowerServer_temp +"  \n");
						
						totalPowerSystem_temp = totalPowerPi_temp + totalPowerServer_temp; // global - double
						//loadPi += listRpi.get(i).getUsedCPU(); // loadPi only in request loop
						physicalServer.setUsedCPUServer(cpuServer);
						System.out.println("Remained CPU server " + physicalServer.getRemainCPU()  +"  \n");
						totalLoadPi_temp += cpuPi;
						totalMapChain += numChain;	//request loop - int
						
						
						totalMapChainSystem_temp += numChain; // global - double - print
						totalServiceOffload = totalMapChainSystem_temp*3;
						totalRejectChainSystem_temp = totalNumChainSystem_temp - totalMapChainSystem_temp; // global - double - print
						
						break WHILE_LOOP;
						
					}
				} // while loop
//				if (stateServer != false && statePi[i] != false) {
//					totalPowerPi_temp += listRpi.get(i).getCurrentPower(); // global - double 
//					totalCpuServer_temp += cpuServer; // global - double - no print
//					totalPowerServer_temp = physicalServer.setCurrentPowerServer(cpuServerTotal); //global - double
//					totalPowerSystem_temp = totalPowerPi_temp + totalPowerServer_temp; // global - double
//					
//					
//					totalMapChain += numChain;	//request loop - int
//					
//					totalMapChainSystem_temp += numChain; // global - double - print
//					
//					totalRejectChainSystem_temp = totalNumChainSystem_temp - totalMapChainSystem_temp; // global - double - print
//				}
				totalPowerPi.add(count, totalPowerPi_temp);
				totalPowerServer.add(count, totalPowerServer_temp);
				totalPowerSystem.add(count, totalPowerSystem_temp);
				totalMapChainSystem.add(count, totalMapChainSystem_temp);
				totalRejectChainSystem.add(count, totalRejectChainSystem_temp);
				totalNumChainSystem.add(count, totalNumChainSystem_temp);
				totalServiceCloud.add(count,totalServiceOffload);
				count ++;
			} //Pi loop		
			double acceptance = (totalMapChain*1.0)/totalNumChain; // request - double - print
			totalAcceptance.add(numRequest, acceptance);
			totalLoadPi.add(numRequest, totalLoadPi_temp/100);
			numRequest++;
			
			
//			totalMapChainSystem_temp += totalMapChain; // global - double - print
//			totalMapChainSystem.add(count, totalMapChainSystem);
			
			
		} // request
		
		try {
			
			write_double("./AllCloud/totalPowerSystem.txt",totalPowerSystem);
			write_double("./AllCloud/totalLoadPi.txt",totalLoadPi);
			write_double("./AllCloud/totalAcceptance.txt",totalAcceptance);
			write_integer("./AllCloud/totalChainSystem.txt",totalNumChainSystem);
			write_integer("./AllCloud/totalChainReject.txt",totalRejectChainSystem);
			write_integer("./AllCloud/totalServiceCloud.txt", totalServiceCloud);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


