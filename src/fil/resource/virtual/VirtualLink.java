package fil.resource.virtual;



/**
 * Builds virtual link
 * 
 * @author Van Huynh Nguyen
 *
 */
public class VirtualLink {
	private Service sService;
	private Service dService;
	private double bandwidthRequest;
	
	/**
	 * Constructs virtual link
	 */
	
	public VirtualLink() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public VirtualLink(Service sService, Service dService, double bandwidthRequest) {
		super();
		this.sService = sService;
		this.dService = dService;
		this.bandwidthRequest = bandwidthRequest;
	}
	
	
	public Service getsService() {
		return sService;
	}
	public void setsService(Service sService) {
		this.sService = sService;
	}
	public Service getdService() {
		return dService;
	}
	public void setdService(Service dService) {
		this.dService = dService;
	}
	public double getBandwidthRequest() {
		return bandwidthRequest;
	}
	public void setBandwidthRequest(double bandwidthRequest) {
		this.bandwidthRequest = bandwidthRequest;
	}
}
