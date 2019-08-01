package fil.resource.virtual;

public class Decode extends Service {
	public Decode() {
		this.setNameService("Density");
		this.setCpu_pi(8);
		this.setCpu_server(1.5);
//		this.setCPU(8);
//		}
//		else this.setCPU(1.5);
		this.setBelongToEdge(true);
		this.setBandwidth(16.32);
		this.setPower(0.1);
	}
}
