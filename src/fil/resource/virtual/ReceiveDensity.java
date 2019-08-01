package fil.resource.virtual;

public class ReceiveDensity extends Service {
	public ReceiveDensity() {
		this.setNameService("ReceiveDensity");
		this.setCpu_pi(0); // CPU of capture in pi
		this.setCpu_server(5);
		this.setBelongToEdge(false);
	}
}
