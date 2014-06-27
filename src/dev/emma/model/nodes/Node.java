package emma.model.nodes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import emma.model.resources.Resource;
import emma.tools.BoundedHashMap;
import emma.tools.BoundedSet;
import emma.tools.Notifier;

public class Node {
	private static int defaultsize = 5;
	private static int COAP_PORT = 5683;
	private static String PREFIX = "aaaa";
	
	private HashSet<String> ip = new HashSet<String>();
	private int port;
	private String nsURI;
	private HashMap<String, String> properties = new HashMap<>();
	
	private BoundedSet<Node> neighbors;
	private BoundedHashMap<Node, Node> routes;
	private ResourceServices resources;

	private Notifier notifier = new Notifier();
	private static Logger logger = Logger.getLogger(Node.class);
	private boolean isEntry;
	
	public Notifier getNotifier(){
		return notifier;
	}
	
	private class ResourceServices{
		private HashMap<String,BoundedSet<Resource>> res;
		public ResourceServices(){
			res= new HashMap<String,BoundedSet<Resource>>();
		}
		
		public boolean addType(String className, int maxSize) throws ClassNotFoundException{
			Class<?> forName = Class.forName("emma.model.resources." + className);
			BoundedSet<Resource> r = new BoundedSet<Resource>(maxSize);
			return res.put(forName.getSimpleName(),r) != null;
		}
		
		public boolean add(Resource r){
			BoundedSet<Resource> re = res.get(r.getClass().getSimpleName());
			if(re!=null)
				return re.add(r);
			
			return false;
		}
		
		public Resource get(final String type, final String name){
			Iterator<Entry<String, BoundedSet<Resource>>> it = this.res.entrySet().iterator();
			while(it.hasNext()){
				Entry<String,BoundedSet<Resource>> pair = it.next();
				
				if(pair.getKey().equals(type)){
					BoundedSet<Resource> br = pair.getValue();
					Iterator<Resource> it2 = br.iterator();
					while(it2.hasNext()){
						Resource r = it2.next();
						if(r.getName().equals(name))
							return r;
					}
					return null;
				}
			}
			return null;
		}

		public Iterator<Entry<String, BoundedSet<Resource>>> iterator() {
			return res.entrySet().iterator();
		} 
		
	}

	public Node (String ip){
		this(ip, "");
	}
	
	public Node(String ip,  String nsURI){
		String[] t = ip.split("]");		
		t[0] = t[0].replace("[","");
		t[0] = t[0].replace("::", ":0:0:0:");
		
		this.ip.add(t[0]);
		
		if(t[0].contains(PREFIX))
			this.ip.add(t[0].replace(PREFIX, "fe80"));
		
		else if (t[0].contains("fe80"))
			this.ip.add(t[0].replace("fe80",PREFIX));
		
		
		if(t.length > 1){
			t = t[1].split(":");
			this.port = Integer.parseInt(t[1]);			
		}
		else this .port = COAP_PORT;
		
		this.nsURI = (nsURI);
		logger.info("New node " + ip);
		//TODO DEBUG ONLY : true value is 0 as commented...
		/*
		if(nsURI == ""){
			resources   = new ResourceServices();
			routes		= new BoundedHashMap<Node,Node>(0);
			neighbors	= new BoundedSet<>(0);
			return;
		}
		*/
		if(nsURI == ""){
			resources   = new ResourceServices();
			routes		= new BoundedHashMap<Node,Node>(5);
			neighbors	= new BoundedSet<>(5);
			return;
		}
		BufferedReader br = null;
		String objJSONString = "";
		 
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader("./"+nsURI));
			while ((sCurrentLine = br.readLine()) != null) 
				objJSONString += sCurrentLine;
			
			JSONObject obj = new JSONObject(objJSONString);
			this.fromJSON(obj);
		} catch (IOException | ClassNotFoundException | JSONException e) {
			logger.warn("**WARNING** Root resource unknown in " + nsURI+" "+e.getMessage());
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				logger.warn(ex.getMessage());
			}
		}
		this.isEntry=false;
	}
	
	public String getIp() {
		String[] t = this.ip.toArray(new String[0]);
		Arrays.sort(t);		
		return (String) t[0];
	}
	
	public String[] getIps() {
		String[] t = this.ip.toArray(new String[0]);
		Arrays.sort(t);
		return (String[]) t;
	}

	public int getPort(){
		return port;
	}
	
	public Node getNeighbor(String ip){
		ip = ip.replace("::", ":0:0:0:");
		Iterator<Node> it = this.neighbors.iterator();
		while(it.hasNext()){
			Node node =  it.next();
			String[] ips = node.getIps();
			for (int i=0; i < ips.length; i++){
				if(ips[i].equals(ip))
					return node;
			}
			
		}
		return null;
	}
	
	public boolean addNeighbor(Node n){
		if(this.neighbors.add(n)){
			logger.debug("Add neighbor " + n.getIp() +" of node " + this.getIp());
			this.notifier.fireListener(this);
			return true;
		}
		return false;
	}
	
	public Entry<Node, Node> getRoute(String ip){
		Iterator<Entry<Node, Node>> it = this.routes.entrySet().iterator();

		ip = ip.replace("::", ":0:0:0:");
		while(it.hasNext()){
			Entry<Node, Node> pair = it.next();
			Node node = pair.getKey();
			
			String[] ips = node.getIps();
			for (int i=0; i < ips.length; i++)
				if(ips[i].equals(ip))
					return pair;
		}
		return null;
	}
	
	public boolean addRoutes(Node to, Node from){
		if(this.routes.put(to, from) != null){
			logger.debug("Add routes on node "+getIp()+" to " + to.getIp() +" from " + from.getIp());
			this.notifier.fireListener(this);
			return true;
		}
		return false;
	}
	
	public boolean addResourceType(String name) throws ClassNotFoundException{
		if(addResourceType(name, defaultsize)){
			this.notifier.fireListener(this);
			logger.debug("Add resourceType " + name +" on node " + this.getIp());
			return true;
		}
		return false;
	}
	
	public boolean addResourceType(String name, int size) throws ClassNotFoundException{
		if(resources.addType(name, size)){
			this.notifier.fireListener(this);
			logger.debug("Add resource type " +name +" on node " + this.getIp());
			return true;			
		}
		return false;
	}
	
	public boolean addResource(Resource r){
		if(resources.add(r)){
			logger.debug("Add resource type " +r.getName() +" on node " + this.getIp());
			r.getNotifier().addListener(this.notifier.getListeners());
			this.notifier.fireListener(this);
			return true;
		}
		return false;
	}
	
	public boolean addResource(String type, String rName){
		if (rName == "" || type == "") return false;
		logger.debug("Add resource " +type + "/" +rName+" on node " + this.getIp());
		try {
			type = "emma.model.resources." + type;
			Constructor<?> Resourcetype = (Class.forName(type).getDeclaredConstructor(String.class));
			Resource r = (Resource) Resourcetype.newInstance(rName);
			if(resources.add(r)){
				this.notifier.fireListener(this);
				return true;				
			}
			else return false;
			
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			logger.warn(e.getMessage());
		} catch (ClassNotFoundException e) {
			logger.warn("**WARNING** Root resource " + type + " unknown");
		} 
		return false;
	}
		
	public void fromJSON(JSONObject obj) throws ClassNotFoundException, JSONException{
		JSONObject properties = obj.getJSONObject("properties");
		Iterator<?> it = properties.keys();
		while(it.hasNext()){
			String key = it.next().toString();
			this.properties.put(key, properties.getString(key));
		}
		resources   = new ResourceServices();
		routes		= new BoundedHashMap<Node,Node>(Integer.parseInt(this.properties.get("MAX_ROUTES")));
		neighbors	= new BoundedSet<>(Integer.parseInt(this.properties.get("MAX_NEIGHBORS")));

		JSONArray resourcesJSON 	= obj.getJSONArray("services");
		for (int i = 0; i < resourcesJSON.length(); i++){
			JSONObject o = resourcesJSON.getJSONObject(i);
			this.resources.addType(o.get("name").toString(), o.getInt("size"));
		}
	}
	
	public JSONObject toJSON(){
		JSONObject obj = new JSONObject();
		
		/*
		 * Add Node properties
		 */
		if(this.properties != null){
			JSONObject objProperties = new JSONObject();
			obj.put("properties", objProperties);
			Iterator<Entry<String, String>> it = this.properties.entrySet().iterator();
			while(it.hasNext()){
				Entry<String,String> pair = it.next();
				objProperties.put(pair.getKey(), pair.getValue());
			}			
		}
		/*
		 * Add Node neighbors
		 */
		if(this.neighbors != null){
			JSONArray objNeighbors = new JSONArray();
			Iterator<Node> itn = this.neighbors.iterator();
			while(itn.hasNext()){
				Node n = itn.next();
				objNeighbors.put(n.getIp());
			}		
			obj.put("neighbors", objNeighbors);	
		}
		/*
		 * Add Node routes
		 */
		if(this.routes != null){
			JSONArray objRoutes = new JSONArray();
			obj.put("routes", objRoutes);
			Iterator<Entry<Node, Node>> it = this.routes.entrySet().iterator();
			while(it.hasNext()){
				JSONObject objroute = new JSONObject();
				Entry<Node, Node> pair = it.next();
				objroute.put(pair.getKey().getIp(), pair.getValue().getIp());
				objRoutes.put(objroute);
			}
			
		}
		/*
		 * Add Node resources
		 */	
		if(this.resources != null){
			JSONArray resourcesJSON = new JSONArray();
			Iterator<Entry<String, BoundedSet<Resource>>> it = this.resources.iterator();
			while(it.hasNext()){
				Entry<String,BoundedSet<Resource>> pair = it.next();
				 JSONObject RootResourceJSON = new JSONObject();
				 RootResourceJSON.put("name", pair.getKey());
				 RootResourceJSON.put("size", pair.getValue().size());
				 
				 JSONArray ResourcesJSON = new JSONArray();
				 RootResourceJSON.put("resources", ResourcesJSON);
				 Iterator<Resource> it2 = pair.getValue().iterator();
				 while(it2.hasNext()){
					 Resource r = it2.next();
					 JSONObject ResourceJSON = new JSONObject();
					 ResourceJSON.put("name", r.getName());
					 ResourcesJSON.put(ResourceJSON);
				 }
				 
				 resourcesJSON.put(RootResourceJSON);
			}
			obj.put("services", resourcesJSON);	
		}
		return obj;
	}
	
	public Resource getResource(String type, String name){
		return this.resources.get(type, name);
	}

	public BoundedSet<Resource> getResourceRoot(String name){
		Iterator<Entry<String, BoundedSet<Resource>>> it = this.resources.iterator();
		while(it.hasNext()){
			Entry<String,BoundedSet<Resource>> pair = it.next();
			if(pair.getKey().equals(name))
				return pair.getValue();
		}
		return null;
	}

	public ArrayList<String> getResourceRoots(){
		ArrayList<String> roots = new ArrayList<String>();
		Iterator<Entry<String, BoundedSet<Resource>>> it = this.resources.iterator();
		while(it.hasNext()){
			Entry<String,BoundedSet<Resource>> pair = it.next();
			roots.add(pair.getKey());
		}
		return roots;
	}
	
	public String getNsURI() {
		return nsURI;
	}

	public int getMemorySpace(){
		//TODO return the TRUE memory space
		return 1000;
	}
	
	public int getUsedMemorySpace(){
		//TODO return the TRUE memory used
		return 500;
	}
	
	public int getAvailableMemorySpace(){
		return this.getMemorySpace()-this.getUsedMemorySpace();
	}
	
	public boolean isNeighbor(Node n){
		return neighbors.contains(n);
	}
	
	public boolean isEntryNode(){
		return isEntry;
	}
	public void setEntry(boolean b){
		this.isEntry=b;
	}
	
	public Set<Node> getNeighbors(){
		return this.neighbors;
	}
}

