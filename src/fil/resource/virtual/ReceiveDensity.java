package fil.resource.virtual;

public class ReceiveDensity extends Service {
	public ReceiveDensity(String name, int sfcID) {
		this.setNameService(name);
		this.setCpu_pi(0); // CPU of capture in pi
		this.setCpu_server(5);
		this.setBelongToEdge(false);
		this.setSfcID(sfcID);
	}
	public ReceiveDensity() {
		this.setCpu_pi(0); // CPU of capture in pi
		this.setCpu_server(5);
		this.setBelongToEdge(false);
	}
}
