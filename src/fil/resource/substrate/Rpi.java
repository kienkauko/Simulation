package fil.resource.substrate;


import java.util.LinkedList;
import fil.resource.virtual.*;
public class Rpi {
	final static double CPU = 102;
	final static double BW = 100;
	final static double CPU_TH = 80;
	private double remainCPU;
	private double usedCPU;
	private double usedBandwidth;
	private double remainBandwidth;
	private double currentPower;
	private double cpuTH;
	//private String name;
	private double state;
	private LinkedList<Service> listService;
	
	public Rpi() {
//		this.setRemainBandwidth();
		//this.setRemainCPU();
		state = 1;
		cpuTH = CPU_TH;
		//this.listService = new LinkedList<>();
		this.usedCPU = 0;
		this.usedBandwidth = 0;
		this.remainCPU = CPU;
		this.currentPower = 0;
		this.remainBandwidth = BW;
		this.setUsedBandwidth(0);
		this.setCurrentPower(1.28);
		//this.setName(name);
	}
	public Rpi(double state, LinkedList<Service> listService) {
//		this.setRemainBandwidth();
//		this.setRemainCPU();
		this.setState(state);
		this.setListService(listService);
	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
	public double getState() {
		return state;
	}
	public void setState(double state) {
		this.state = state;
	}
	public LinkedList<Service> getListService() {
		return listService;
	}
	public void setListService(LinkedList<Service> listService) {
		this.listService = listService;
	}
	public void addService(Service service) {
		this.listService.add(service);
	}
	
	public void removeService(Service service) {
		if(this.listService.contains(service))
			this.listService.remove(service);
	}
	
	public double getRemainCPU() {
		return this.remainCPU;
	}

	public double getRemainBandwidth() {
		return this.remainBandwidth;
	}
//	public void setRemainBandwidth() {
//		this.remainBandwidth = BW - this.usedBandwidth;
//	}
	public double getCurrentPower() {
		return this.currentPower;
	}
	public void setCurrentPower(double currentPower) {
		this.currentPower += currentPower;
	}
	public double getUsedCPU() {
		return this.usedCPU;
	}
	public void setUsedCPU(double usedCPU) {
		this.usedCPU += usedCPU;
		this.remainCPU -= usedCPU;
	}
	public void resetCPU() {
		this.usedCPU = 0;
		this.remainCPU = CPU;
		this.remainBandwidth = BW;
		this.usedBandwidth = 0;
	}
	public double getUsedBandwidth() {
		return usedBandwidth;
	}
	public void setUsedBandwidth(double usedBandwidth) {
		this.usedBandwidth += usedBandwidth;
		this.remainBandwidth -= usedBandwidth;
	}
	public double getCpu_threshold() {
		return this.cpuTH;
	}
}
