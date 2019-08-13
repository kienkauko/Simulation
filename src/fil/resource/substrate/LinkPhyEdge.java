package fil.resource.substrate;

public class LinkPhyEdge {
	private PhysicalServer physicalServer;
	private SubstrateSwitch edgeSwitch;
	private double bandwidth;
//	private int type; //1: up, 0: down
	public LinkPhyEdge(PhysicalServer phy, SubstrateSwitch edge, double bandwidth)
	{
		this.physicalServer = phy;
		this.edgeSwitch = edge;
		this.bandwidth = bandwidth;
//		this.type= type;
	}
	public PhysicalServer getPhysicalServer() {
		return physicalServer;
	}
	public void setPhysicalServer(PhysicalServer physicalServer) {
		this.physicalServer = physicalServer;
	}
	public SubstrateSwitch getEdgeSwitch() {
		return edgeSwitch;
	}
	public void setEdgeSwitch(SubstrateSwitch edgeSwitch) {
		this.edgeSwitch = edgeSwitch;
	}
	public double getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
	}
//	public int getType() {
//		return type;
//	}
//	public void setType(int type) {
//		this.type = type;
//	}

}
