package fil.resource.virtual;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import fil.resource.substrate.PhysicalServer;
import fil.resource.substrate.SubstrateSwitch;

/**
 * @author Van Huynh Nguyen
 *
 */
public class FatTree {
	private int k;
	final private double BANDWIDTH = 1000; // default bandwidth = 1000;
	private int totalCore = 0;
	private int totalAgg = 0;
	private int totalEdge = 0;
	private int totalHost = 0;

	private Map<Integer, SubstrateSwitch> listEdgeSwitch;
	private Map<Integer, Integer> listPhyConnectEdge;
	private Map<Integer, SubstrateSwitch> listAggSwitch;
	private Map<Integer, SubstrateSwitch> listCoreSwitch;
	private Map<Integer, PhysicalServer> listPhysicalServer;
	private Map<Integer, LinkedList<SubstrateSwitch>> listPod; // list edge
																// switch in pod
	private Map<Integer, LinkedList<SubstrateSwitch>> listPodAgg; // list agg
																	// switch in
																	// pod
	// for link mapping
	private Map<SubstrateSwitch, LinkedList<SubstrateSwitch>> listAggConnectEdge; // list
																					// agg
																					// switch
																					// connect
																					// to
																					// edge
																					// switch

	private Map<SubstrateSwitch, LinkedList<SubstrateSwitch>> listCoreConnectAgg; // list
																					// core
																					// switch
																					// connect
																					// to
																					// agg
																					// switch

	public FatTree() {
		this.k = 0;
		listEdgeSwitch = new HashMap<>();
		listAggSwitch = new HashMap<>();
		listCoreSwitch = new HashMap<>();
		listPhysicalServer = new HashMap<>();
		listPod = new HashMap<>();
		listAggConnectEdge = new HashMap<>();
		listCoreConnectAgg = new HashMap<>();
		listPodAgg = new HashMap<>();
		listPhyConnectEdge = new HashMap<>();
	}

	public FatTree(Map<Integer, SubstrateSwitch> listEdgeSwitch, Map<Integer, SubstrateSwitch> listAggSwitch,
			Map<Integer, SubstrateSwitch> listCoreSwitch, Map<Integer, PhysicalServer> listPhysicalServer,
			Map<Integer, LinkedList<SubstrateSwitch>> listPod, Map<Integer, LinkedList<SubstrateSwitch>> listPodAgg,
			int k, int totalCore, int totalAgg, int totalEdge, int totalHost,
			Map<SubstrateSwitch, LinkedList<SubstrateSwitch>> listAggConnectEdge,
			Map<SubstrateSwitch, LinkedList<SubstrateSwitch>> listCoreConnectAgg) {
		this.k = k;
		this.listAggConnectEdge = listAggConnectEdge;
		this.listAggSwitch = listAggSwitch;
		this.listCoreConnectAgg = listCoreConnectAgg;
		this.listPhysicalServer = listPhysicalServer;
		this.listPod = listPod;
		this.listPodAgg = listPodAgg;
		this.listAggConnectEdge = listAggConnectEdge;
		this.listCoreConnectAgg = listCoreConnectAgg;
		this.totalAgg = totalAgg;
		this.totalCore = totalCore;
		this.totalEdge = totalEdge;
		this.totalHost = totalHost;
	}

	public Topology genFatTree(int k) {
		Topology topo = new Topology();
		this.k = k;
		totalCore = k * k / 4;
		totalAgg = k * k / 2;
		totalEdge = k * k / 2;
		totalHost = k * k * k / 4;

		// add list Physical Server
		for (int i = 1; i <= totalHost; i++) {
			PhysicalServer server = new PhysicalServer(String.valueOf(i));
			listPhysicalServer.put(i, server);
		}
		for (int i = 0; i < k; i++) {
			int aggSwitchIndexBegin = totalHost + totalCore + 1 + i * k;
			// add list agg switch
			for (int j = aggSwitchIndexBegin; j < aggSwitchIndexBegin + k / 2; j++) {
				SubstrateSwitch aggSwitch = new SubstrateSwitch(String.valueOf(j), 100, false);
				aggSwitch.setType(2);
				listAggSwitch.put(j, aggSwitch);
			}
			// add list edge switch
			for (int j = aggSwitchIndexBegin + k / 2; j < aggSwitchIndexBegin + k; j++) {
				SubstrateSwitch edgeSwitch = new SubstrateSwitch(String.valueOf(j), 100, false);
				edgeSwitch.setType(1);
				listEdgeSwitch.put(j, edgeSwitch);
			}
		}

		// add list core switch
		for (int i = 1; i <= totalCore; i++) {
			SubstrateSwitch coreSwitch = new SubstrateSwitch(String.valueOf(i + totalHost), 100, false);
			coreSwitch.setType(3);
			listCoreSwitch.put(i + totalHost, coreSwitch);
		}

		/**************** Add links ****************/

		// add links: physical -> edgeswitch -> aggswitch
		// Iterator pod=1,2...k
		for (int i = 0; i < k; i++) {
			int aggSwitchIndexBegin = totalHost + totalCore + 1 + i * k;
			// add links from coreSwitch to aggSwitch
			int coreSwitchIndex = totalHost + 1;
			for (int j = aggSwitchIndexBegin; j < aggSwitchIndexBegin + k / 2; j++) {
				for (int l = coreSwitchIndex; l < coreSwitchIndex + k / 2; l++) {
					// aggSwitch j connect to coreSwitch j in each group

					topo.addEdge(listAggSwitch.get(j), listCoreSwitch.get(l), BANDWIDTH);
					topo.addEdge(listCoreSwitch.get(l), listAggSwitch.get(j), BANDWIDTH);

					// get list core switch connect to agg switch
					if (listCoreConnectAgg.containsKey(listAggSwitch.get(j))) {
						LinkedList<SubstrateSwitch> temp = new LinkedList<>();
						temp = listCoreConnectAgg.get(listAggSwitch.get(j));
						temp.add(listCoreSwitch.get(l));
						listCoreConnectAgg.put(listAggSwitch.get(j), temp);
					} else {

						LinkedList<SubstrateSwitch> temp = new LinkedList<>();
						temp.add(listCoreSwitch.get(l));
						listCoreConnectAgg.put(listAggSwitch.get(j), temp);
					}
					// stupid :))
					if (l == coreSwitchIndex + k / 2 - 1) {
						coreSwitchIndex = coreSwitchIndex + k / 2;
						break;
					}
				}
			}

			// add links from aggSwitch to edgeSwitch
			for (int j = aggSwitchIndexBegin; j < aggSwitchIndexBegin + k / 2; j++)
				for (int l = aggSwitchIndexBegin + k / 2; l < aggSwitchIndexBegin + k; l++) {
					// just one direction
					topo.addEdge(listAggSwitch.get(j), listEdgeSwitch.get(l), BANDWIDTH);
					topo.addEdge(listEdgeSwitch.get(l), listAggSwitch.get(j), BANDWIDTH);
					// get list agg switch connect to edge switch
					if (listAggConnectEdge.containsKey(listEdgeSwitch.get(l))) {
						LinkedList<SubstrateSwitch> temp = new LinkedList<>();
						temp = listAggConnectEdge.get(listEdgeSwitch.get(l));
						temp.add(listAggSwitch.get(j));
						listAggConnectEdge.put(listEdgeSwitch.get(l), temp);
					} else {

						LinkedList<SubstrateSwitch> temp = new LinkedList<>();
						temp.add(listAggSwitch.get(j));
						listAggConnectEdge.put(listEdgeSwitch.get(l), temp);
					}
				}

			// add links from edgeSwitch to Physical server
			int hostIndexBegin = k * k * i / 4 + 1;
			for (int j = aggSwitchIndexBegin + k / 2; j < aggSwitchIndexBegin + k; j++) {
				for (int l = 0; l < k / 2; l++) {
					// just one direction
					topo.addPhysicalServer(listEdgeSwitch.get(j), listPhysicalServer.get(hostIndexBegin), BANDWIDTH);
					listPhyConnectEdge.put(hostIndexBegin, j);
					hostIndexBegin++;
				}

			}
			LinkedList<SubstrateSwitch> listEdgePod = new LinkedList<>();
			LinkedList<SubstrateSwitch> listAggPod = new LinkedList<>();
			for (int l = aggSwitchIndexBegin + k / 2; l < aggSwitchIndexBegin + k; l++) {
				listEdgePod.add(listEdgeSwitch.get(l));
			}
			for (int l = aggSwitchIndexBegin; l < aggSwitchIndexBegin + k / 2; l++) {
				listAggPod.add(listAggSwitch.get(l));
			}
			listPod.put(i, listEdgePod);
			listPodAgg.put(i, listAggPod);

		}
		for (SubstrateSwitch sw : topo.getListSwitch()) {
			Map<SubstrateSwitch, Double> bwport = sw.getBandwidthPort();
			LinkedList<SubstrateSwitch> neighbor = topo.adjacentNodes(sw);
			for (SubstrateSwitch s : neighbor) {
				bwport.put(s, (double) 0);
			}
		}
		LinkedList<SubstrateSwitch> listPhySw = topo.getListPhySwitch();
		for (SubstrateSwitch swPhy : listPhySw) {
			Integer idPhy = Integer.parseInt(swPhy.getNameSubstrateSwitch());
			Integer idEdge = listPhyConnectEdge.get(idPhy);
			SubstrateSwitch swEdge = listEdgeSwitch.get(idEdge);
			swEdge.getBandwidthPort().put(swPhy, (double) 0);
		}

		topo.setListAggConnectEdge(listAggConnectEdge);
		topo.setListCoreConnectAgg(listCoreConnectAgg);
		topo.setListEdgeSwitchInPod(listPod);
		topo.setListAggSwitchInPod(listPodAgg);
		return topo;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public Map<Integer, LinkedList<SubstrateSwitch>> getListPod() {
		return listPod;
	}

	public Object clone() {

		FatTree fat = new FatTree(listEdgeSwitch, listAggSwitch, listCoreSwitch, listPhysicalServer, listPod,
				listPodAgg, k, totalCore, totalAgg, totalEdge, totalHost, listAggConnectEdge, listCoreConnectAgg);
		return fat;
	}

	public Map<Integer, SubstrateSwitch> getListEdgeSwitch() {
		return listEdgeSwitch;
	}

	public void setListEdgeSwitch(Map<Integer, SubstrateSwitch> listEdgeSwitch) {
		this.listEdgeSwitch = listEdgeSwitch;
	}

	public Map<Integer, SubstrateSwitch> getListAggSwitch() {
		return listAggSwitch;
	}

	public void setListAggSwitch(Map<Integer, SubstrateSwitch> listAggSwitch) {
		this.listAggSwitch = listAggSwitch;
	}

	public Map<Integer, SubstrateSwitch> getListCoreSwitch() {
		return listCoreSwitch;
	}

	public void setListCoreSwitch(Map<Integer, SubstrateSwitch> listCoreSwitch) {
		this.listCoreSwitch = listCoreSwitch;
	}

}
