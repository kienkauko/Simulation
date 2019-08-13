package fil.resource.substrate;


public class SubstrateLink {
	
	private SubstrateSwitch startSwitch;
	private SubstrateSwitch endSwitch;
	private double bandwidth;
	
	public SubstrateLink()
	{
		startSwitch = new SubstrateSwitch();
		endSwitch = new SubstrateSwitch();
		bandwidth =0;
	}
	public SubstrateLink(SubstrateSwitch start, SubstrateSwitch end, double bw)
	{
		this.startSwitch = start;
		this.endSwitch = end;
		this.bandwidth = bw;
	}
	public SubstrateSwitch getStartSwitch() {
		return startSwitch;
	}
	public void setStartSwitch(SubstrateSwitch startSwitch) {
		this.startSwitch = startSwitch;
	}
	public SubstrateSwitch getEndSwitch() {
		return endSwitch;
	}
	public void setEndSwitch(SubstrateSwitch endSwitch) {
		this.endSwitch = endSwitch;
	}
	public double getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
	}

}
