package fil.resource.virtual;

public class Decode extends Service {
	public Decode(String name, int sfcID) {
		this.setNameService(name);
		this.setServiceType("decode");
		this.setCpu_pi(8);
		this.setCpu_server(1.5);
//		this.setCPU(8);
//		}
//		else this.setCPU(1.5);
		this.setBelongToEdge(true);
		this.setBandwidth(16.32);
		this.setPower(0.1);
		this.setSfcID(sfcID);
	}
	public Decode() {
		this.setServiceType("decode");
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
