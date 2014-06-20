package emma.control.http;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import emma.control.Server;

public class HttpRegistry implements Server {
	private HttpServer server;
	private int PORT = 8000;
	private String registryUri = "/emma/registry";
	private String registryPath = "registry";
	private boolean isConnected;
	
	class MyHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			String response = "";
			
			/*
			 * REQUEST FOR NODE DESCRIPTION
			 */
			if(t.getRequestMethod().toString().equals("GET")){
				String path = t.getRequestURI().toString().substring(registryUri.length());
				BufferedReader br = null;
				
				try {
					 
					String sCurrentLine;
					br = new BufferedReader(new FileReader(registryPath + path));
					while ((sCurrentLine = br.readLine()) != null) {
						response += sCurrentLine;
					}
					t.sendResponseHeaders(200, response.length());
		 
				} catch (IOException e) {
					response = "404 Not Found";
					t.sendResponseHeaders(404, response.length());
				} finally {
					try {
						if (br != null)br.close();
					} catch (IOException ex) {
						response = ex.getMessage();
						t.sendResponseHeaders(404, response.length());
					}
				}				
			}
			
			/*
			 * OTHER REQUEST
			 */
			else {
				response = "405 Method Not Allowed";
				t.sendResponseHeaders(405, response.length());
			}

			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
	       }
	   }

	public HttpRegistry() throws IOException{  
		server = HttpServer.create(new InetSocketAddress(PORT), 0);
		server.createContext(registryUri, new MyHandler());
		server.setExecutor(null); // creates a default executor
		this.isConnected=false;
	}

	@Override
	public void connect() {
		server.start();
		System.out.println("Registry server listening on port " + PORT);
		this.isConnected=true;
	}

	@Override
	public void disconnect() {
		server.stop(0);
		this.isConnected=false;
	}

	@Override
	public boolean isConnected() {
		return this.isConnected;
	}
	
	
}
