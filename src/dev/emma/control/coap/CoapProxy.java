package emma.control.coap;

import java.net.SocketException;

import emma.model.nodes.Network;


public class CoapProxy extends CoapServer {

	public CoapProxy() throws SocketException{
		this.addResource(new Network("network"));
	}
	
}
