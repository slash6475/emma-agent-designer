package emma.model.nodes;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.EventListenerList;

import ch.ethz.inf.vs.californium.coap.DELETERequest;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import emma.model.nodes.Node;
import emma.model.resources.Resource;
import emma.tools.Notifier;

public class Network extends LocalResource {
	
	private static Logger logger = Logger.getLogger(Network.class);
	
	private Set<Node> nodes;
	private String nsURI = "";
	
	public Network(String resourceIdentifier) {
		super(resourceIdentifier);
		this.nodes = new HashSet<Node>();
	}

	private Notifier notifier = new Notifier();
	public Notifier getNotifier(){
		return notifier;
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
		String payload 	= request.getPayloadString().replace("\0", "");
		JSONObject Obj;
		JSONArray resources, routes, neighbors;
		
		String ip		= request.getPeerAddress().toString();

		logger.debug("[REQUEST] PUT /"+this.getName()+" from: " +ip + " Payload length :"+payload.length());

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
			logger.warn("JSON Serialization\nRequest from  " + ip +"\n" + e.getMessage() + "\n" + payload);
			
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
					node.getNotifier().addListener(this.notifier.getListeners());
					this.nodes.add(node);
					notifier.fireListener(this);
			}
		}
		else {
			node = new Node(ip, nsURI);
			node.getNotifier().addListener(this.notifier.getListeners());
			this.nodes.add(node);
			notifier.fireListener(this);
		}

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
					logger.warn("Services "+ resourcePath[0] + " should not exist on this node " + nsURI);
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
					n.getNotifier().addListener(this.notifier.getListeners());
					this.nodes.add(n);
					notifier.fireListener(this);
				}
				if(!node.addNeighbor(n)){
					logger.error("Unable to add neighbor " + ipN);
				} 
			}
		}
		/*
		 * Routes list update 
		 */
			
		for(int i=0; i < routes.length(); i++){
			JSONObject r = routes.getJSONObject(i);
			String ipN = JSONObject.getNames(r)[0];
			
			if(node.getRoute(ipN) == null){
				Node n1 = this.getNode(ipN);
				Node n2 = this.getNode(r.getString(ipN));
				if(n1 == null){
					n1 = new Node(ipN);
					n1.getNotifier().addListener(this.notifier.getListeners());
					this.nodes.add(n1);
					notifier.fireListener(this);					
				}
				if(n2 == null){
					n2 = new Node(r.getString(ipN));
					n2.getNotifier().addListener(this.notifier.getListeners());
					this.nodes.add(n2);
					notifier.fireListener(this);					
				}
				
				if(!node.addRoutes(n1, n2)){
					logger.error("Unable to add route " + n1.getIp() + " from " + n2.getIp());
				}
			}
		}
		
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
		
		String[] t = ip.split("]");
		ip = t[0].replace("[","");
		ip = ip.replace("::", ":0:0:0:");
		
		Iterator<Node> itr = this.nodes.iterator();
		while(itr.hasNext()){
			node = itr.next();
			String[] ips = node.getIps();
			for(int i=0; i < ips.length; i++ ){
				if(ips[i].equals(ip))
					return node;
			}
		}
		return null;
	}
	
	public Node[] getNodes(){
		return this.nodes.toArray(new Node[0]);
		}
}
