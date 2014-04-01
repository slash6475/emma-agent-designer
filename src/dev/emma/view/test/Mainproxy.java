package emma.view.test;

import java.io.IOException;
import emma.control.coap.CoapProxy;
import emma.control.http.HttpRegistry;
import emma.model.nodes.Network;
import emma.view.NetworkViewer;

public class Mainproxy {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		
		// TODO Auto-generated method stub
		CoapProxy coap_server = new CoapProxy();
		coap_server.connect();
		Network net = coap_server.getNetwork();
		new NetworkViewer(net);
		
		(new HttpRegistry()).connect();
	}
}
