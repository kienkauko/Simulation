package fil.resource.virtual;

/**
 * Builds Virtual Machine
 * 
 * @author Van Huynh Nguyen
 *
 */
public class Service {

	private String nameService;
	private String serviceType;
	private double cpu;
	private double bandwidth;
	private double power;
	private int requestID;
	private int sfcID;
	private boolean belongToEdge;
	private double cpu_pi;
	private double cpu_server;
	
	public Service() {
		this.setNameService("");
		this.cpu = 0;
		this.setRequestID(0);
		this.setBelongToEdge(true);
	}
	
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

	public int getSfcID() {
		return sfcID;
	}

	public void setSfcID(int sfcID) {
		this.sfcID = sfcID;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

}
