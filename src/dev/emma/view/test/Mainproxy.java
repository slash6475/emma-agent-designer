package emma.view.test;

import java.io.IOException;


import emma.control.coap.CoapProxy;
import emma.control.http.HttpRegistry;

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
