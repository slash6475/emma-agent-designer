package emma.control.coap;

import java.net.SocketException;

import emma.model.nodes.Network;

public class CoapProxy extends CoapServer {
	final static String networkName = "network";
	
	public CoapProxy() throws SocketException{
		this.addResource(new Network(networkName));
		this.connect();
	}
	
	public Network getNetwork(){
		return (Network) this.getResource(networkName);
	}
}
