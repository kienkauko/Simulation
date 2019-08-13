package fil.resource.virtual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SFC {
	private ArrayList<Boolean> servicePosition;
	private Map<Service, Boolean> listService;
	private int sfcID;
	private String name;
	private Service service;
	private boolean position;
	private double totalChainCpu;
	private double totalChainBandwidth;

	private Capture capture;
	private Decode decode ;
	private Density density;
	private ReceiveDensity receive;
	
	public SFC(String name, int sfcID) {
		listService = new HashMap<>(4);
		capture = new Capture(name, sfcID);
		decode = new Decode(name, sfcID);
		density = new Density(name, sfcID);
		receive = new ReceiveDensity(name, sfcID);
		this.setSfcID(sfcID);
		this.setName(name);
	}
	
	public SFC() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<Boolean> getServicePosition() {
		return servicePosition;
	}
	
	public void setServicePosition (Service service, boolean position) {
		String type = service.getServiceType();
		switch(type) {
		case "capture": this.capture.setBelongToEdge(position);break;
		case "decode": this.decode.setBelongToEdge(position);break;
		case "density": this.density.setBelongToEdge(position);break;
		case "receive": this.receive.setBelongToEdge(position);break;
		default: System.out.println("error at set position"); break;
		}
	}

//	public void setServicePosition(Capture capture, boolean position) {
//		this.capture = capture;
//		this.capture.setBelongToEdge(position);
//	}
//	public void setServicePosition(Decode decode, boolean position) {
//		this.decode = decode;
//		this.decode.setBelongToEdge(position);
//	}
//	public void setServicePosition(Density density, boolean position) {
//		this.density = density;
//		this.density.setBelongToEdge(position);
//	}
//	public void setServicePosition(ReceiveDensity receive, boolean position) {
//		this.receive = receive;
//		this.receive.setBelongToEdge(position);
//	}
	
	public double getCpuDD(int dec, int den) {

		totalChainCpu = dec*decode.getCpu_pi() + den*density.getCpu_pi()
		+ capture.getCpu_pi() + receive.getCpu_pi();
		
		return totalChainCpu;
	}
	
	public double getBandwidthDD(int dec, int den) {
		
		if(dec != den)
			totalChainBandwidth = dec*this.decode.getBandwidth();
		else if(dec*den == 1)
			totalChainBandwidth = this.density.getBandwidth();
		else
			totalChainBandwidth = this.capture.getBandwidth();
		
		return totalChainBandwidth;
	}
	
	public Service getService(int number) {
		if(number == 1) return capture;
		if(number == 2) return decode;
		if(number == 3) return density;
		if(number == 4) return receive;
		else return null;
	}

	public int getSfcID() {
		return sfcID;
	}

	public void setSfcID(int sfcID) {
		this.sfcID = sfcID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
