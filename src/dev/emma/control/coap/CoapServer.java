package emma.control.coap;

import java.net.SocketException;
import emma.control.Server;
import ch.ethz.inf.vs.californium.endpoint.ServerEndpoint;

public class CoapServer  extends ServerEndpoint  implements Server {
	
	private static int PORT = 5683;
	public boolean isConnected;
	
	public CoapServer() throws SocketException {
		super(PORT);
		this.isConnected=false;
	}

	@Override
	public void connect() {
		this.createCommunicator();
		this.isConnected=true;
		System.out.println("CoAP server listening on port " + PORT);
	}

	
	@Override
	public void disconnect() {
		this.isConnected=false;
	}

	@Override
	public boolean isConnected() {
		return isConnected;
	}
}