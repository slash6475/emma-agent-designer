package emma.control.coap;

import java.awt.Frame;
import java.io.IOException;
import java.net.SocketException;

import emma.model.nodes.Network;
import emma.view.NetworkViewer;


public class CoapProxy extends CoapServer {
	final static String networkName = "network";
	
	public static void main(String[] args) throws IOException {

		
		// TODO Auto-generated method stub
		CoapProxy coap_server = new CoapProxy();
		coap_server.connect();
		Network net = coap_server.getNetwork();
		
		Frame win = new Frame("Agent Launcher");
		win.add(new NetworkViewer(net));
		win.setVisible(true);
		win.setSize(400, 400);
	}
	
	public CoapProxy() throws SocketException{
		this.addResource(new Network(networkName));
	}
	
	public Network getNetwork(){
		return (Network) this.getResource(networkName);
	}
	
}
