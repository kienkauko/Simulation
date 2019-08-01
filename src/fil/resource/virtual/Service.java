package fil.resource.virtual;

/**
 * Builds Virtual Machine
 * 
 * @author Van Huynh Nguyen
 *
 */
public class Service {

	private String nameService;
	private double cpu;
	private double bandwidth;
	private double power;
//	private double ram;
//	private double memory;
	private int requestID;
	private int chainID;
	private boolean belongToEdge;
	private double cpu_pi;
	public double getCpu_pi() {
		return cpu_pi;
	}

	public void setCpu_pi(double cpu_pi) {
		this.cpu_pi = cpu_pi;
	}

	public double getCpu_server() {
		return cpu_server;
	}

	public void setCpu_server(double cpu_server) {
		this.cpu_server = cpu_server;
	}

	private double cpu_server;
	/**
	 * Constructs virtual machine
	 */
	public Service() {
		this.setNameService("");
		this.cpu = 0;
		this.setChainID(0);
		this.setRequestID(0);
		this.setBelongToEdge(true);
	}

	/**
	 * Constructs virtual machine
	 * 
	 * @param nameVM
	 *            Name of virtual machine
	 * @param CPU
	 *            CPU capacity of virtual machine
	 * @param memory
	 *            Memory capacity of virtual machine
	 */

//	public Service(String nameService, double cpu, int requestID, int chainID, boolean belongToEdge, double bandwidth) {
//		this.setNameService(nameService);
//		this.cpu = cpu;
//		this.setRequestID(requestID);
//		this.setChainID(chainID);
//		this.setBelongToEdge(belongToEdge);
//		this.setBandwidth(bandwidth);
//	}


	public double getCPU() {
		return cpu;
	}

	public void setCPU(double cPU) {
		cpu = cPU;
	}

	public String getNameService() {
		return nameService;
	}

	public void setNameService(String nameService) {
		this.nameService = nameService;
	}

	public int getRequestID() {
		return requestID;
	}

	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}

	public int getChainID() {
		return chainID;
	}

	public void setChainID(int chainID) {
		this.chainID = chainID;
	}

	public boolean isBelongToEdge() {
		return belongToEdge;
	}

	public void setBelongToEdge(boolean belongToEdge) {
		this.belongToEdge = belongToEdge;
	}
	
	public boolean getBelongToEdge() {
		return belongToEdge;
	}
	public double getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

}