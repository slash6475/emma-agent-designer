package emma.model.nodes;

import java.beans.XMLDecoder;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ch.ethz.inf.vs.californium.coap.DELETERequest;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import emma.model.nodes.Node;
import emma.model.resources.L;
import emma.model.resources.Resource;

public class Network extends LocalResource {
	private String prefix = "bbbb::";
	private Set<Node> nodes;
	private String nsURI = "";
	
	public Network(String resourceIdentifier) {
		super(resourceIdentifier);
		this.nodes = new HashSet<Node>();
	}
	
	/*
	 * 
	 */
	
	/*
	 * (non-Javadoc)
	 * 
	 */

	@Override
	public void performGET(GETRequest request) {
		String rsp = "coucou ohoh \n";
		request.respond(CodeRegistry.RESP_CONTENT, rsp);
	}

	@Override
	public void performPUT(PUTRequest request) {
		JSONObject Obj;
		JSONArray resources, routes, neighbors;
		
		String ip		= request.getPeerAddress().toString();
		String payload 	= request.getPayloadString();

		//System.out.println("From: " +ip + "\nPayload:" + payload);
		/*
		 * JSON Serialization and request validation
		 */
		try{
			Obj = new JSONObject(payload);
			resources 	= (JSONArray) Obj.get("resources");
			routes		= (JSONArray) Obj.get("routes");
			neighbors 	= (JSONArray) Obj.get("neighbors");
			nsURI		= Obj.getString("ns-uri");
			}
		catch(Exception e){
			System.out.println("**ERROR** JSON Serialization\nRequest from  " + ip +"\n" + e.getMessage());
			request.respond(CodeRegistry.RESP_METHOD_NOT_ALLOWED);
			return;
		}
		
		/*
		 * Looking for node if it already exist
		 * If its type is different, it is remove
		 */
		Node node = getNode(ip);
		if(node != null){
			if(!node.getNsURI().equals(nsURI)){
					this.nodes.remove(node);
					node = new Node(ip, nsURI);
			}
		}
		else node = new Node(ip, nsURI);
		/*
		 * Resource list update
		 */
		for (int i=0; i < resources.length(); i++){
			String[] resourcePath = resources.getString(i).split("/");
			if(resourcePath.length != 2) continue;
			/*
			 * Get or create the resource if necessary
			 */
			Resource r = node.getResource(resourcePath[0], resourcePath[1]);
			if(r == null){
				if(!node.addResource(resourcePath[0], resourcePath[1])){
					System.out.println("** WARNING ** Services "+ resourcePath[0] + " should not exist on this node " + nsURI);
					continue ;
				}
			}
		}
		/*
		 * Neighbors list update
		 */		
		for(int i=0; i < neighbors.length(); i++){
			String ipN = neighbors.getString(i);
			if(node.getNeighbor(ipN) == null){
				Node n = this.getNode(ipN);
				if(n == null){
					n = new Node(ipN);
				}
				if(!node.addNeighbor(n)){
					System.out.println("**WARNING** Unable to add neighbor" + ipN);
				}
			}
		}
		/*
		 * Routes list update => Il faut gérer des pairs !
		 */
			
		for(int i=0; i < routes.length(); i++){
			JSONObject r = routes.getJSONObject(i);
			String ipN = r.getNames(r)[0];
			
			if(node.getRoute(ipN) == null){
				Node n1 = this.getNode(ipN);
				Node n2 = this.getNode(r.getString(ipN));
				if(n1 == null)	n1 = new Node(ipN);
				if(n2 == null)	n2 = new Node(r.getString(ipN));
				
				if(!node.addRoutes(n1, n2)){
					System.out.println("**WARNING** Unable to add route " + n1.getIp() + " from " + n2.getIp());
				}
			}
		}
		
		System.out.println(node.toJSON().toString());
		request.respond(CodeRegistry.RESP_CONTENT);
	}

	@Override
	public void performPOST(POSTRequest request) {
		request.respond(CodeRegistry.RESP_METHOD_NOT_ALLOWED);
	}

	@Override
	public void performDELETE(DELETERequest request) {
		request.respond(CodeRegistry.RESP_METHOD_NOT_ALLOWED);
	}
	
	
	public Node getNode(String ip){
		Node node = null;
		Iterator<Node> itr = this.nodes.iterator();
		while(itr.hasNext()){
			node = itr.next();
			if(node.getIp().equals(ip))
				break;
		}
		return node;
	}
}
