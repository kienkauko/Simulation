package fil.algorithm.test1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fil.resource.substrate.LinkPhyEdge;
import fil.resource.substrate.PhysicalServer;
import fil.resource.substrate.SubstrateSwitch;
import fil.resource.virtual.SFC;
import fil.resource.virtual.Service;
import fil.resource.virtual.Topology;
import fil.resource.virtual.VirtualLink;

public class ServiceMapping {
	public ArrayList<SFC> listSFC;
	private boolean isSuccess;
	private Map<PhysicalServer, SubstrateSwitch> listLinksServer;
	private Map<SubstrateSwitch, LinkedList<PhysicalServer>> listPhysConEdge; // list Physical server connected to physical machine
	private Map<SFC, PhysicalServer> listSFCServer;
	private Map<PhysicalServer, ArrayList<SFC>> listServerSFC;
	private Map<Service, PhysicalServer> needLinkMapping;
	private Map<Service, PhysicalServer> needLinkMappingCopy;
	private ArrayList<PhysicalServer> listServer2Core;
	private Map<Integer, PhysicalServer> listServerUsed;
	private Map<PhysicalServer, ArrayList<SFC>> numReceiveServer;
	private boolean isSatisfiedCPU;
	private int numService;
	private double powerServer;
	private boolean separateService;
	private Topology topo;
	
	public ServiceMapping() {
		isSuccess = false;
		listSFC = new ArrayList<SFC>();
		listServer2Core = new ArrayList<>();
		listLinksServer = new HashMap<>();
		listPhysConEdge = new HashMap<>();
		listServerUsed = new HashMap<>(100);
		listSFCServer = new HashMap<>();
		listServerSFC = new HashMap<>();
		needLinkMapping = new HashMap<>();
		numReceiveServer = new HashMap<>();
		needLinkMappingCopy = new HashMap<>();
		isSatisfiedCPU = false;
		numService = 0;
		powerServer = 0;
		separateService = false;
		topo = new Topology();
	}
	
	public Topology run(ArrayList<SFC> listSFC, Topology topo) {
		
		this.listSFC = listSFC;
		this.topo = topo;
		// getNumService
		needLinkMapping = new HashMap<>();
		this.listLinksServer = topo.getListLinksServer();
		Map<Integer, PhysicalServer> listPhysical = topo.getListPhyServers();  //get list physical server
		
		//get switch connect to server
		
		for (Entry<PhysicalServer, SubstrateSwitch> entry : listLinksServer.entrySet()) {
			// stupid, get physical server
			PhysicalServer phy = listPhysical.get(Integer.parseInt(entry.getKey().getName()));
			// System.out.println("Physical server "+phy.getName()+" CPU
			// "+phy.getCpu()+" RAM "+phy.getRam());
			SubstrateSwitch edge = entry.getValue();
			if (listPhysConEdge.containsKey(edge)) {
				LinkedList<PhysicalServer> listPhy = listPhysConEdge.get(edge);
				listPhy.add(phy);
				listPhysConEdge.put(edge, listPhy);
			} else {
				LinkedList<PhysicalServer> listPhy = new LinkedList<>();
				listPhy.add(phy);
				listPhysConEdge.put(edge, listPhy);
			}
		}
		
		ArrayList<SFC> listRemainSFC = new ArrayList<>();  // save SFC not be mapped
		
		
		int previousService = 0;
		for (Entry<Integer, PhysicalServer> entry : listPhysical.entrySet()) {
			ArrayList<SFC> listMappedSFC = new ArrayList<>();
			numReceiveServer.put(entry.getValue(), new ArrayList<>());
			boolean notDoneAll = false;
			double cpuServer;
			
			
			for (SFC sfc : this.listSFC) {
				//compare cpu chain with remain cpu server
				boolean satisfied = false;
				double cpuSFC = 0;
				int serviceCount = 0; // number of service inside SFC belongs to cloud
				for(int i = 4; i >= 2; i--) {
					if(!sfc.getService(i).getBelongToEdge()) {
						cpuSFC += sfc.getService(i).getCpu_server();  //calculate all cpu used by chain
						serviceCount++;
					}
				}
				
				if(serviceCount == 1) { // only Receive is mapped to cloud
					ArrayList<SFC> listNumReceive = new ArrayList<>();
					if(!numReceiveServer.containsKey(entry.getValue())) {
						listNumReceive.add(sfc);
						numReceiveServer.put(entry.getValue(), listNumReceive);
					} else {
						int numReceiveIndepend = numReceiveServer.get(entry.getValue()).size();
						numReceiveIndepend++;
						listNumReceive = numReceiveServer.get(entry.getValue());
						listNumReceive.add(sfc);
						numReceiveServer.put(entry.getValue(), listNumReceive); // number of independent receive in a server
					}
				}
				
				if(cpuSFC <= entry.getValue().getRemainCPU()) { // enough cpu to map
					listMappedSFC.add(sfc);
					satisfied = true;
					//add to list sfc belong to a server
					//System.out.println("dit me may \n");
					listSFCServer.put(sfc, entry.getValue());
					System.out.println("This sfc is running at server " + entry.getValue().getName() + " with CPU " + cpuSFC);
					entry.getValue().setUsedCPUServer(cpuSFC);
					numService += serviceCount; // total services run on cloud
					if(!listRemainSFC.isEmpty()) {                                                                                                
						listRemainSFC.remove(sfc);
					}
					//add service need link mapping
					if(separateService == true) {
						needLinkMapping.put(sfc.getService(previousService), entry.getValue());
						separateService = false;
					}
				}
				else {
					satisfied = false;
					notDoneAll = true; //not mapping all sfc
					listRemainSFC.add(sfc); //list not mapping
				}
			}
			
			///not done all map to next server
			if(listRemainSFC.isEmpty()) {
				isSuccess = true;
				System.out.println(" tiep theo co nhay vao if ko ? ");
				ArrayList<SFC> sfcTemp = new ArrayList<>();
				if(!listServerSFC.containsKey(entry.getValue())) {
					listServerSFC.put(entry.getValue(), listMappedSFC);
					int index = Integer.parseInt(entry.getValue().getName());
					listServerUsed.put(index, entry.getValue());
					
					System.out.println("co chay den dong put 1   .");
				} else {
					sfcTemp = listServerSFC.get(entry.getValue());
					if(!sfcTemp.isEmpty()) {
						listMappedSFC.addAll(sfcTemp);
					}
					listServerSFC.put(entry.getValue(), listMappedSFC);
					int index = Integer.parseInt(entry.getValue().getName());
					listServerUsed.put(index, entry.getValue());
					
					System.out.println("co chay den dong put 2  .");
				}
				break;
			}
			else {
				//divide sfc to map
				for(SFC sfcRemain : listRemainSFC) {
					double cpuSFCRemain = 0;
					SFC sfcA = new SFC(sfcRemain.getName(), sfcRemain.getSfcID());
					for(int i = 4; i >= 2; i--) {
						if(!sfcRemain.getService(i).getBelongToEdge()) {
							cpuSFCRemain = sfcRemain.getService(i).getCpu_server();  //calculate all cpu used by chain
							if(cpuSFCRemain > entry.getValue().getRemainCPU()) {
								
								break;
							}
							else {
								//co the map dc
								separateService = true;
								numService++;
								sfcA.getService(i).setBelongToEdge(false);
								//setCPu Server
								entry.getValue().setUsedCPUServer(cpuSFCRemain);
								//change position state
								sfcRemain.setServicePosition(sfcRemain.getService(i), true);
								previousService = i-1;   //service truoc no
								//set sfc belong to server
								listSFCServer.put(sfcRemain, entry.getValue());
								needLinkMapping.put(sfcRemain.getService(i), entry.getValue());
							}
						}
					}
					
					listMappedSFC.add(sfcA);
					listSFCServer.put(sfcA, entry.getValue());
				}
			}
			listSFC = listRemainSFC;
		}
		if(!needLinkMapping.isEmpty()) {
			needLinkMappingCopy.putAll(needLinkMapping);
		}
		return topo;
	}
	
	public SubstrateSwitch getSwitchFromID(LinkedList<SubstrateSwitch> listSwitch, String id) {
		SubstrateSwitch s= new SubstrateSwitch();
		for(SubstrateSwitch sw: listSwitch)
			if(sw.getNameSubstrateSwitch().equals(id))
			{
				s= sw;
				break;
			}
		return s;
	}
	
	public void resetRpiSFC(ArrayList<SFC> listSFC, Map<LinkPhyEdge, Double> listBandwidthPhyEdge) {
		ArrayList<SFC> SFCofPhy = new ArrayList<>();
		for(SFC sfc : listSFC) {
			double cpuSFC = 0;
			for(int i = 4; i >= 2; i--) {
				if(!sfc.getService(i).getBelongToEdge()) {
					cpuSFC += sfc.getService(i).getCpu_server();
				}
			}
//			lay listPhysicalServer tu topo
			Map<Integer, PhysicalServer> listPhysicalServer = this.topo.getListPhyServers();
			
			PhysicalServer phy = listSFCServer.get(sfc);
			listSFCServer.remove(sfc);
			SFCofPhy = listServerSFC.get(phy);
			SFCofPhy.remove(sfc);
			
			phy = listPhysicalServer.get(Integer.parseInt(phy.getName()));
			phy.setUsedCPUServer(-cpuSFC);
//			gan lai cho listServer
			this.topo.getListPhyServers().put(Integer.parseInt(phy.getName()), phy);
			listServerSFC.put(phy, SFCofPhy);
			numReceiveServer.put(phy, SFCofPhy);
			
//			tra lai link mapping
			Set<Service> services =  needLinkMappingCopy.keySet();
			for(Service service : services) {
				if(sfc.getName().equals(service.getNameService())) {
					LinkedList<SubstrateSwitch> phySwitch = this.topo.getListPhySwitch();	
					for (LinkPhyEdge link : listBandwidthPhyEdge.keySet()) {
						link.setBandwidth(link.getBandwidth()+listBandwidthPhyEdge.get(link));
						link.getEdgeSwitch().setPort(getSwitchFromID(phySwitch, link.getPhysicalServer().getName()), -listBandwidthPhyEdge.get(link));
					}
				}
			}
		}
	}
	
	public boolean remappingAggrFarGroup(VirtualLink vLink) {
		
		boolean isSuccess = false;
		//remapping if 2 service connect through core switch
		
		Service sService = vLink.getsService();
		Service dService = vLink.getdService();
		
//		sService.getNameService();
//		sService.getSfcID();
		SFC sfc = new SFC(sService.getNameService(), sService.getSfcID());
		PhysicalServer phyA = needLinkMapping.get(sService);
		PhysicalServer phyB = needLinkMapping.get(dService);
		
		ArrayList<SFC> listSFCA = new ArrayList<>();
		ArrayList<SFC> listSFCB = new ArrayList<>();
		listSFCA = listServerSFC.get(phyA);
		listSFCB = listServerSFC.get(phyB);
		
		int numReceiveA = numReceiveServer.get(phyA).size();
		int numReceiveB = numReceiveServer.get(phyB).size();
		double cpuDemand = 0;
		SFC sfcB = new SFC();
		SFC sfcA = new SFC();
		int numServiceA=0;
		int element = 0;
		
		SFCA_LOOP:
		for(SFC sfc1 : listSFCA) {
			for(SFC sfc2 : listSFCB) {
				if(sfc1.equals(sfc2)) {
					sfcB = sfc2;
					sfcA = sfc1;
					element++;
					//xet cpu demand cho viec di chuyen service tu B sang A
					for(int i = 4; i >= 2; i--) {
						if(!sfc2.getService(i).isBelongToEdge()) {
							cpuDemand += sfc2.getService(i).getCpu_server();
						}
						if(!sfc1.getService(i).isBelongToEdge()) {
							numServiceA++;
						}
					}
					break SFCA_LOOP;
				}
			}
		}
		if(numServiceA == 1) {
			numReceiveA--;
		}
		
		if((numReceiveA)*5 >= cpuDemand) { //neu demand nho hon so receive doc lap thi tien hanh chuyen
			double numReceiveEvacuate = Math.ceil(cpuDemand/5);
			
			listSFCServer.remove(sfcB); // xoa sfc khoi may B
			for(int i=4; i>=2; i--) {
				if(!sfcB.getService(i).isBelongToEdge()) {
					sfcA.setServicePosition(sfcB.getService(i), false);
				}
			}
			listSFCServer.put(sfcA, phyA); //set sfcA thuoc may phyA
			//set lai listSFCA may A
			listSFCA.add((element - 1), sfcA);
			listServerSFC.put(phyA, listSFCA);
			isSuccess = true;
			
			ArrayList<SFC> sfcEvacuste = new ArrayList<>();
			ArrayList<SFC> receiveInA = numReceiveServer.get(phyA);
			
			int numReceiveEvacuateCount = 0;
			for(SFC sfcTemp : receiveInA) {
				sfcEvacuste.add(sfcTemp);
				numReceiveEvacuateCount++;
				if(numReceiveEvacuateCount > numReceiveEvacuate) {
					break;
				}
			}
			run(sfcEvacuste, this.topo); //chay ham run() de map listSFC
			if(this.isSuccess()) {
				isSuccess = true;
			} else {
				isSuccess = false;
			}
			
		}
		return isSuccess;
	}

	public Map<SFC, PhysicalServer> getListSFCServer() {
		return listSFCServer;
	}

	public void setListSFCServer(Map<SFC, PhysicalServer> listSFCServer) {
		this.listSFCServer = listSFCServer;
	}

	public boolean isSeparateService() {
		return separateService;
	}

	public void setSeparateService(boolean separateService) {
		this.separateService = separateService;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public double getPowerServer() {
		System.out.println("inside getPowerSever with " + listServerUsed.size());
		for(PhysicalServer phy : listServerUsed.values()) {
			double cpuServer = phy.getUsedCPUServer();
			double powerTemp = phy.calculatePowerServer(cpuServer);
			powerServer += powerTemp;
			System.out.println("Power server inside "+ powerServer);
		}
		return powerServer;
	}

	public void setPowerServer(double powerServer) {
		this.powerServer = powerServer;
	}

	public Map<Integer, PhysicalServer> getListServerUsed() {
		return listServerUsed;
	}

	public void setListServerUsed(Map<Integer, PhysicalServer> listServerUsed) {
		this.listServerUsed = listServerUsed;
	}

	public Map<Service, PhysicalServer> getNeedLinkMapping() {
		return needLinkMapping;
	}

	public void setNeedLinkMapping(Map<Service, PhysicalServer> needLinkMapping) {
		this.needLinkMapping = needLinkMapping;
	}

	public Map<PhysicalServer, ArrayList<SFC>> getNumReceiveServer() {
		return numReceiveServer;
	}

	public void setNumReceiveServer(Map<PhysicalServer, ArrayList<SFC>> numReceiveServer) {
		this.numReceiveServer = numReceiveServer;
	}
}
