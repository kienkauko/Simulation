package fil.resource.virtual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SFC {
	private ArrayList<Boolean> servicePosition;
	private Map<Service, Boolean> listService;
	private int chainID;
	private Service service;
	private boolean position;
	private double totalChainCpu;
	private double totalChainBandwidth;

	private Capture capture;
	private Decode decode ;
	private Density density;
	private ReceiveDensity receive;
	
	public SFC() {
		listService = new HashMap<>(4);
		capture = new Capture();
		decode = new Decode();
		density = new Density();
		receive = new ReceiveDensity();	
	}
	
	public ArrayList<Boolean> getServicePosition() {
		return servicePosition;
	}

	public void setServicePosition(Service service, boolean position) {
		this.service = service;
		this.position = position;
		this.listService.put(this.service,this.position);
	}

	public int getChainID() {
		return chainID;
	}

	public void setChainID(int chainID) {
		this.chainID = chainID;
	}
	
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
}
