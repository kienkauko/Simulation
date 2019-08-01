package fil.resource.substrate;

public class PhysicalServer {
	final double CPU = 100*64; //100%
	private String name;
	private double remainCPU;
	private double usedCPUServer;
	private double powerServer;
	private int state ; // state =0:  off, state=1:  on
	//private LinkedList<Service> listService;
	
	public PhysicalServer() {
		this.remainCPU = CPU;
		this.usedCPUServer = 0;
		this.setUsedCPUServer(0);
		this.powerServer = 0;
//		this.ram = RAM;
//		this.name=name;
		//this.listService = new LinkedList<>();
	}

	public void setCpu(double cpu) {
		this.remainCPU = cpu;
		if(this.remainCPU == 0)   // neu may khong chay thi ko bat len
		{
			this.state = 0;
		}
		else
			this.state = 1;
	}
	
	public double calculatePowerServer(double cpuServer) {
		return 380*(cpuServer/100) + 120;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
//	public void addService(Service service) {
//		this.listService.add(service);
//	}
//	
//	public void removeService(Service service) {
//		if(this.listService.contains(service))
//			this.listService.remove(service);
//	}
//	
//	public LinkedList<Service> getListService() {
//		return this.listService;
//	}


	public double getRemainCPU() {
		return this.remainCPU;
	}


	public void setRemainCPU(double remainCPU) {
		this.remainCPU = remainCPU;
	}


	public double getCPU() {
		return CPU;
	}
	
	public void setPowerServer(double powerServer) {
		this.powerServer = powerServer;
	}
	
	public double getPowerServer() {
		return powerServer;
	}

	public double calCurrentPowerServer(double cpuServer) {
		double cpuTotal = this.usedCPUServer + cpuServer;
		System.out.println("Total CPU Server right now is " + cpuTotal + " ....... \n");
		double numServer = Math.floor(cpuTotal/100);
		double cpuFragment = cpuTotal - 100*numServer;
		 return numServer*this.calculatePowerServer(100) +
				this.calculatePowerServer(cpuFragment);
	}
	
	public double getUsedCPUServer() {
		return this.usedCPUServer;
	}

	public void setUsedCPUServer(double usedCPUServer) {
		this.usedCPUServer += usedCPUServer;
		this.remainCPU -= usedCPUServer;
	}
}