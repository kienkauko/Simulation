package fil.resource.virtual;

public class Capture extends Service {
	public Capture() {
		this.setNameService("Capture");
//		this.setChainID(chainID);
//		this.setRequestID(requestID);
		this.setCpu_pi(3); // CPU of capture in pi
		this.setCpu_server(0);
		this.setBelongToEdge(true);
		this.setBandwidth(47.35);
		this.setPower(0.49);
	}
}
