package fil.algorithm.test1;

import java.util.ArrayList;
import java.util.Map;

import fil.resource.substrate.PhysicalServer;
import fil.resource.virtual.SFC;
import fil.resource.virtual.Service;
import fil.resource.virtual.Topology;

public class MappingServer {
	public ArrayList<SFC> listSFC;
	private double power;
	private ServiceMapping serviceMapping;
	private LinkMapping linkMapping;
	private boolean isSuccess;
	//private Topology topo;
	
	public MappingServer() {
		this.listSFC = new ArrayList<SFC>();
		serviceMapping = new ServiceMapping();
		linkMapping = new LinkMapping();
		this.setPower(0);
		isSuccess = false;
		//topo = new Topology();
	}
	
	public Topology runMapping(ArrayList<SFC> listSFC, Topology topo, double bandwidthDemand) {
//		topo = fatTree.genFatTree(K);
		serviceMapping = new ServiceMapping();
		linkMapping = new LinkMapping();
		serviceMapping.run(listSFC, topo);
		Map<Service, PhysicalServer> resultsServiceMapping = serviceMapping.getNeedLinkMapping();
		if(serviceMapping.isSuccess()) {
			System.out.println("Success service mapping! \n");
			isSuccess = true;
			Map<Integer, PhysicalServer> listPhy = serviceMapping.getListServerUsed();
			linkMapping.linkMappingCoreServer(topo, listPhy, bandwidthDemand);
			
			if(!serviceMapping.getNeedLinkMapping().isEmpty()) {
				linkMapping.linkMappingOurAlgorithm(topo, listSFC, resultsServiceMapping, serviceMapping);
				if(linkMapping.isSuccess()) {
					setPower(serviceMapping.getPowerServer() + linkMapping.getPowerConsumed());
					System.out.println("Link power: " + linkMapping.getPowerConsumed());
					isSuccess = true;
				} else {
					isSuccess = false;
				}
			} else {
				setPower(serviceMapping.getPowerServer() + linkMapping.getPowerConsumed());
				System.out.println("Link power: " + linkMapping.getPowerConsumed());
			}
		}
		else {
			System.out.println("failed cmm \n");
			isSuccess = false;
		}
		return topo;
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public LinkMapping getLinkMapping() {
		return linkMapping;
	}

	public void setLinkMapping(LinkMapping linkMapping) {
		this.linkMapping = linkMapping;
	}

	public ServiceMapping getServiceMapping() {
		return serviceMapping;
	}

	public void setServiceMapping(ServiceMapping serviceMapping) {
		this.serviceMapping = serviceMapping;
	}
}
