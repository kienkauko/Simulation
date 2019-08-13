package fil.algorithm.test1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import fil.resource.substrate.LinkPhyEdge;
import fil.resource.substrate.PhysicalServer;
import fil.resource.substrate.SubstrateLink;
import fil.resource.substrate.SubstrateSwitch;
import fil.resource.virtual.SFC;
import fil.resource.virtual.Service;
import fil.resource.virtual.Topology;
import fil.resource.virtual.VirtualLink;
import fil.resource.virtual.modelHP;

public class LinkMapping {
	private boolean isSuccess;
	private LinkedList<VirtualLink> listVirLink;
	private Map<LinkedList<SubstrateSwitch>, Double> resultsLinkMapping;
	private Map<VirtualLink, LinkedList<SubstrateSwitch>> listPathMapped;
	private Map<VirtualLink, LinkedList<LinkPhyEdge>> listPhyEdgeMapped;
	private Map<LinkPhyEdge, Double> listBandwidthPhyEdge;
	private int numLinkSuccess;
	private double powerConsumed;

	public LinkMapping() {
		isSuccess = false;
		listVirLink = new LinkedList<>();
		resultsLinkMapping = new HashMap<>();
		listPathMapped = new HashMap<>();
		listBandwidthPhyEdge = new HashMap<>();
		listPhyEdgeMapped = new HashMap<>();
		powerConsumed =0;
		numLinkSuccess=0;
	}
	
	public Topology linkMappingOurAlgorithm(Topology topo, ArrayList<SFC> listSFC,
			Map<Service, PhysicalServer> resultsServiceMapping, ServiceMapping serviceMapping) {
		double powerTemp = getPower(topo);
		LinkedList<SubstrateLink> listLinkBandwidth = topo.getLinkBandwidth();
		LinkedList<LinkPhyEdge> listPhyEdge = topo.getListLinkPhyEdge();
		LinkedList<SubstrateSwitch> listPhySwitch = topo.getListPhySwitch();
		
		Service serviceA = new Service();
		Service serviceB = new Service();
		VirtualLink vLink = new VirtualLink(serviceB, serviceA, serviceB.getBandwidth());
		listVirLink.add(vLink);
		int i = 0;
		for(Entry<Service, PhysicalServer> entry : resultsServiceMapping.entrySet()) {
			System.out.println("dit me may lan 2 \n");
			if(i == 0) {
				serviceA = entry.getKey();
			}
			if(i == 1) {
				serviceB = entry.getKey();
				PhysicalServer phy1 = resultsServiceMapping.get(serviceA);
				PhysicalServer phy2 = resultsServiceMapping.get(serviceB);
				
				Map<PhysicalServer, SubstrateSwitch> listLinkServers = topo.getListLinksServer();
				
				SubstrateSwitch edgeSwitch1 = listLinkServers.get(phy1);
				SubstrateSwitch edgeSwitch2 = listLinkServers.get(phy2);
				
				if (!checkPhyEdge(topo, phy1, edgeSwitch1, phy2, edgeSwitch2, serviceB.getBandwidth(),listPhyEdge))
				{
					break;
//					continue;
				}
				
				Map<SubstrateSwitch, LinkedList<SubstrateSwitch>> listAggConnectEdge = topo.getListAggConnectEdge();
				LinkedList<SubstrateSwitch> listAggConnectStartEdge = new LinkedList<>();
				LinkedList<SubstrateSwitch> listAggConnectEndEdge = new LinkedList<>();
				
				LinkPhyEdge phy2Edge1=null, phy2Edge2=null;
				int countP2E = 0;
				for(int in=0; in < listPhyEdge.size();in++) {
					LinkPhyEdge link = listPhyEdge.get(in);
					if(link.getEdgeSwitch().equals(edgeSwitch1)&&link.getPhysicalServer().equals(phy1)) {
						phy2Edge1 = link;
						countP2E++;
					}
					if(link.getEdgeSwitch().equals(edgeSwitch2)&&link.getPhysicalServer().equals(phy2)) {
						phy2Edge2 = link;
						countP2E++;
					}
					if(countP2E==2)
						break;
				}
				
				// near groups		
				if (edgeSwitch1.equals(edgeSwitch2)) {
					///////////////////////////////////////////////////////
					LinkedList<LinkPhyEdge> phyEdge = new LinkedList<>();
					phyEdge.add(phy2Edge1);
					phyEdge.add(phy2Edge2);
					listPhyEdgeMapped.put(vLink, phyEdge);
					phy2Edge1.setBandwidth(phy2Edge1.getBandwidth() - vLink.getBandwidthRequest());
					phy2Edge2.setBandwidth(phy2Edge2.getBandwidth() - vLink.getBandwidthRequest());
					edgeSwitch1.setPort(getSwitchFromID(listPhySwitch, phy1.getName()), vLink.getBandwidthRequest());
					edgeSwitch1.setPort(getSwitchFromID(listPhySwitch, phy2.getName()), vLink.getBandwidthRequest());
					if(listBandwidthPhyEdge.containsKey(phy2Edge1)){
						listBandwidthPhyEdge.put(phy2Edge1, listBandwidthPhyEdge.get(phy2Edge1) + vLink.getBandwidthRequest());
					} else {
						listBandwidthPhyEdge.put(phy2Edge1, vLink.getBandwidthRequest());
					}
					
					if(listBandwidthPhyEdge.containsKey(phy2Edge2)) {
						listBandwidthPhyEdge.put(phy2Edge2, listBandwidthPhyEdge.get(phy2Edge2) + vLink.getBandwidthRequest());
					}else {
						listBandwidthPhyEdge.put(phy2Edge2, vLink.getBandwidthRequest());
					}
					
					LinkedList<SubstrateSwitch> list = new LinkedList<>();
					list.add(edgeSwitch1);
					double temp=0;
					if(resultsLinkMapping.containsKey(list))
						temp= resultsLinkMapping.get(list);

					resultsLinkMapping.put(list, vLink.getBandwidthRequest()+temp);
					listPathMapped.put(vLink, list);
					numLinkSuccess++;
				} else {
					///check if aggregation or core
					for (Entry<SubstrateSwitch, LinkedList<SubstrateSwitch>> entry1 : listAggConnectEdge.entrySet()) {
						if (entry1.getKey().equals(edgeSwitch1))
							listAggConnectStartEdge = entry1.getValue();
						if (entry1.getKey().equals(edgeSwitch2))
							listAggConnectEndEdge = entry1.getValue();
					}
					// sort list Agg
					listAggConnectStartEdge = sortListSwitch(listAggConnectStartEdge);
					listAggConnectEndEdge = sortListSwitch(listAggConnectEndEdge);

					// check middle groups
					if (listAggConnectStartEdge.equals(listAggConnectEndEdge)) {
						
						System.out.println("Remapping Aggr \n");
						if(serviceMapping.remappingAggrFarGroup(vLink)) {
							isSuccess = true;
						} else {
							isSuccess= false;
						}
						
//						for (SubstrateSwitch sw : listAggConnectStartEdge) {
//							LinkedList<SubstrateSwitch> path = new LinkedList<>();
//							path.add(edgeSwitch1);
//							path.add(sw);
//							path.add(edgeSwitch2);
//							if (vLink.getBandwidthRequest() > getBanwidthOfPath(path, listLinkBandwidth))
//								continue;
//							else {
//								double temp=0;
//								listPathMapped.put(vLink, path);
//								if(resultsLinkMapping.containsKey(path))
//									temp= resultsLinkMapping.get(path);
//								resultsLinkMapping.put(path, vLink.getBandwidthRequest()+temp);
//								listLinkBandwidth = MapLink(path, listLinkBandwidth, vLink.getBandwidthRequest());
//								numLinkSuccess++;
//
//								LinkedList<LinkPhyEdge> phyEdge = new LinkedList<>();
//								phyEdge.add(phy2Edge1);
//								phyEdge.add(phy2Edge2);
//								listPhyEdgeMapped.put(vLink, phyEdge);
//								phy2Edge1.setBandwidth(phy2Edge1.getBandwidth()-vLink.getBandwidthRequest());
//								phy2Edge2.setBandwidth(phy2Edge2.getBandwidth()-vLink.getBandwidthRequest());
//								edgeSwitch1.setPort(getSwitchFromID(listPhySwitch, phy1.getName()), vLink.getBandwidthRequest());
//								edgeSwitch2.setPort(getSwitchFromID(listPhySwitch, phy2.getName()), vLink.getBandwidthRequest());
//								if(listBandwidthPhyEdge.containsKey(phy2Edge1)){
//									listBandwidthPhyEdge.put(phy2Edge1, listBandwidthPhyEdge.get(phy2Edge1) + vLink.getBandwidthRequest());
//								} else {
//									listBandwidthPhyEdge.put(phy2Edge1, vLink.getBandwidthRequest());
//								}
//								
//								if(listBandwidthPhyEdge.containsKey(phy2Edge2)) {
//									listBandwidthPhyEdge.put(phy2Edge2, listBandwidthPhyEdge.get(phy2Edge2) + vLink.getBandwidthRequest());
//								}else {
//									listBandwidthPhyEdge.put(phy2Edge2, vLink.getBandwidthRequest());
//								}
//								
//								break;
//							}
//						}
					} else {
						//far group
						System.out.println("Remapping far group\n");
						if(serviceMapping.remappingAggrFarGroup(vLink)) {
							isSuccess = true;
						} else {
							isSuccess = false;
						}
					}
					
				}
				i=0;
			}
		}
		
		topo.setLinkBandwidth(listLinkBandwidth);
		topo.setListLinkPhyEdge(listPhyEdge);
		if (numLinkSuccess == listVirLink.size()) {
			isSuccess = true;
			//topo = updatePortSwitch(topo, resultsLinkMapping);
			//topo= updatePortPhyEdge(topo);
			powerConsumed = getPower(topo)-powerTemp;
		} else {
			isSuccess = false;
			topo= reverseLinkMapping(topo, resultsLinkMapping);
			reversePhyLinkMapping(topo);
		}
		return topo;
	}
	
	public LinkedList<SubstrateLink> MapLink(LinkedList<SubstrateSwitch> path, LinkedList<SubstrateLink> listLinkBandwidthTemp, double bandwidth)
	{
		for (int i = 0; i < path.size() - 1; i++) {
		
			SubstrateSwitch switch1 = path.get(i);
			SubstrateSwitch switch2 = path.get(i + 1);
			for (int j = 0; j < listLinkBandwidthTemp.size(); j++) {
				SubstrateLink link = listLinkBandwidthTemp.get(j);
				// update bandwidth, two-direction
				if (link.getStartSwitch().equals(switch1) && link.getEndSwitch().equals(switch2)) {
					link.setBandwidth(link.getBandwidth() - bandwidth);
					listLinkBandwidthTemp.set(j, link);
					// break;
				}
				//Vm1-> Vm2 == Vm2-Vm1
				if (link.getStartSwitch().equals(switch2) && link.getEndSwitch().equals(switch1)) {
					link.setBandwidth(link.getBandwidth() - bandwidth);
					listLinkBandwidthTemp.set(j, link);
					// break;
				}
			}
			switch1.setPort(switch2, bandwidth);
			switch2.setPort(switch1, bandwidth);
			
		}
		return listLinkBandwidthTemp;
	}
	
	// sort List switch in increasing order by ID
	public LinkedList<SubstrateSwitch> sortListSwitch(LinkedList<SubstrateSwitch> list) {
		Collections.sort(list, new Comparator<SubstrateSwitch>() {
			@Override
			public int compare(SubstrateSwitch o1, SubstrateSwitch o2) {
				if (Integer.parseInt(o1.getNameSubstrateSwitch()) < Integer.parseInt(o2.getNameSubstrateSwitch())) {
					return -1;
				}
				if (Integer.parseInt(o1.getNameSubstrateSwitch()) > Integer.parseInt(o2.getNameSubstrateSwitch())) {
					return 1;
				}
				return 0;
			}
		});
		return list;
	}

	public double getBanwidthOfPath(LinkedList<SubstrateSwitch> path, LinkedList<SubstrateLink> listLinkBandwidth) {
		double bandwidth = Integer.MAX_VALUE;
		for (int i = 0; i < path.size() - 1; i++) {
			SubstrateSwitch switch1 = path.get(i);
			SubstrateSwitch switch2 = path.get(i + 1);
			for (int j = 0; j < listLinkBandwidth.size(); j++) {
				SubstrateLink link = listLinkBandwidth.get(j);
				if (link.getStartSwitch().equals(switch1) && link.getEndSwitch().equals(switch2)) {
					if (link.getBandwidth() < bandwidth)
						bandwidth = link.getBandwidth();
					break;
				}
			}

		}
		return bandwidth;
	}
	
	public boolean checkPhyEdge(Topology topo, PhysicalServer phy1, SubstrateSwitch edge1, PhysicalServer phy2,
			SubstrateSwitch edge2, double bandwidth, LinkedList<LinkPhyEdge> listPhyEdgeTemp) {
		boolean check = false;
		boolean Satisfied = false;
		for (LinkPhyEdge link : listPhyEdgeTemp) {
			if ((link.getPhysicalServer().equals(phy1) && link.getEdgeSwitch().equals(edge1))) {
				if (link.getBandwidth() >= bandwidth)
					Satisfied = true;
			}
			if ((link.getPhysicalServer().equals(phy2) && link.getEdgeSwitch().equals(edge2))) {
				if (link.getBandwidth() >= bandwidth)
					check = true;
			}

		}
		
		return (Satisfied&&check);
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
	
	public void reversePhyLinkMapping(Topology topo) {
		LinkedList<SubstrateSwitch> phySwitch = topo.getListPhySwitch();		
		for (LinkPhyEdge link : listBandwidthPhyEdge.keySet()) {
			link.setBandwidth(link.getBandwidth()+listBandwidthPhyEdge.get(link));
			link.getEdgeSwitch().setPort(getSwitchFromID(phySwitch, link.getPhysicalServer().getName()), -listBandwidthPhyEdge.get(link));
		}
	}
	
	public Topology reverseLinkMapping(Topology topo, Map<LinkedList<SubstrateSwitch>, Double> resultsLinkMapping) {

		LinkedList<SubstrateLink> listLinkBandwidth = topo.getLinkBandwidth();
		LinkedList<SubstrateSwitch> listSwitch = topo.getListSwitch();
		for (Entry<LinkedList<SubstrateSwitch>, Double> entry : resultsLinkMapping.entrySet()) {
			LinkedList<SubstrateSwitch> path = entry.getKey();
			double bandwidth = entry.getValue();
			if(path.size()<=1)
				continue;
			for (int i = 0; i < path.size() - 1; i++) {
				
				SubstrateSwitch switch1 = path.get(i);
				SubstrateSwitch switch2 = path.get(i + 1);
				for (int j = 0; j < listLinkBandwidth.size(); j++) {
					SubstrateLink link = listLinkBandwidth.get(j);
					double bw = link.getBandwidth();
					// update bandwidth, two-direction
					//Vm1-> Vm2
					if (link.getStartSwitch().equals(switch1) && link.getEndSwitch().equals(switch2)) {
						
						link.setBandwidth(bw + bandwidth);
						listLinkBandwidth.set(j, link);
						// break;
					}
					//Vm2-> Vm1
					if (link.getStartSwitch().equals(switch2) && link.getEndSwitch().equals(switch1)) {
						link.setBandwidth(bw + bandwidth);
						listLinkBandwidth.set(j, link);
						// break;
					}
				}
				switch1.setPort(switch2, -bandwidth);
				switch2.setPort(switch1, -bandwidth);
			}
		}
		topo.setLinkBandwidth(listLinkBandwidth);
		topo.setListSwitch(listSwitch);
		return topo;
	}
	
	public double getPower(Topology topo)
	{
		double power = 0;
		modelHP HP = new modelHP();	
		LinkedList<SubstrateSwitch> listSwitch = topo.getListPhySwitch();
//		for(SubstrateLink link: topo.getLinkBandwidth())
//		{
//			double bw = link.getBandwidth();
//			SubstrateSwitch s = link.getStartSwitch();
//			if(listSwitch.containsKey(s.getNameSubstrateSwitch()))
//			{
//				SubstrateSwitch sw = listSwitch.get(s.getNameSubstrateSwitch());
//				sw.setPort(link.getEndSwitch(), 1000-bw);
//				listSwitch.put(s.getNameSubstrateSwitch(), sw);
//			}
//			else				
//			{
//				s.setPort(link.getEndSwitch(), 1000-bw);
//				listSwitch.put(s.getNameSubstrateSwitch(), s);
//			}
//			
//		}
		for(SubstrateSwitch entry: listSwitch)
		{
			power+= HP.getPowerOfSwitch(entry);
		}
			
		return power;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public int getNumLinkSuccess() {
		return numLinkSuccess;
	}

	public void setNumLinkSuccess(int numLinkSuccess) {
		this.numLinkSuccess = numLinkSuccess;
	}

	public double getPowerConsumed() {
		return powerConsumed;
	}

	public void setPowerConsumed(double powerConsumed) {
		this.powerConsumed = powerConsumed;
	}

	public Map<LinkPhyEdge, Double> getListBandwidthPhyEdge() {
		return listBandwidthPhyEdge;
	}

	public void setListBandwidthPhyEdge(Map<LinkPhyEdge, Double> listBandwidthPhyEdge) {
		this.listBandwidthPhyEdge = listBandwidthPhyEdge;
	}
}
