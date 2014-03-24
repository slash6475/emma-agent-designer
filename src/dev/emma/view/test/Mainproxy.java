package emma.view.test;

import java.beans.XMLEncoder;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;


import emma.control.coap.CoapProxy;
import emma.control.http.HttpRegistry;
import emma.model.nodes.Node;
import emma.tools.BoundedSet;

public class Mainproxy {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		
		// TODO Auto-generated method stub
		(new CoapProxy()).connect();
		(new HttpRegistry()).connect();
	}

}
