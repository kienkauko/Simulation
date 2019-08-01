package fil.resource.virtual;

public class Density extends Service {
	public Density() {
		this.setNameService("Density");
//		this.setChainID(chainID);
//		this.setRequestID(requestID);
//		if(belongToEdge == true) {
		this.setCpu_pi(13.6); //CPU usage when running on Pi

		this.setCpu_server(6.5);; //CPU usage when running on server
		this.setBelongToEdge(true);
		this.setBandwidth(0.6);
		this.setPower(0.13);
	}
}
