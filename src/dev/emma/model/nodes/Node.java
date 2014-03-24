package emma.model.nodes;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;
import emma.model.resources.Resource;
import emma.tools.BoundedHashMap;
import emma.tools.BoundedSet;


public class Node {
	
	private static int defaultsize = 5;
	
	private String ip;
	private String nsURI;
	private HashMap<String, String> properties = new HashMap<>();
	
	private BoundedSet<Node> neighbors;
	private BoundedHashMap<Node, Node> routes;
	private ResourceServices resources;
	
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
			// TODO Auto-generated method stub
			return res.entrySet().iterator();
		} 
		
	}

	public Node (String ip){
		this(ip, "");
	}
	
	public Node(String ip,  String nsURI){
		this.ip = ip;
		this.nsURI = (nsURI);
		
		if(nsURI == "")
			return;
		
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
			System.out.println("**WARNING** Root resource unknown in " + nsURI);
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	
	public Node getNeighbor(String ip){
		Iterator it = this.neighbors.iterator();
		while(it.hasNext()){
			Node node = (Node) it.next();
			if (node.getIp().equals(ip))
				return node;
		}
		return null;
	}
	
	public boolean addNeighbor(Node n){
		return this.neighbors.add(n);
	}
	
	public Entry<Node, Node> getRoute(String ip){
		Iterator it = this.routes.entrySet().iterator();
		while(it.hasNext()){
			Entry<Node, Node> pair = (Entry<Node, Node>) it.next();
			Node node = pair.getKey();
			if (node.getIp().equals(ip))
				return pair;
		}
		return null;
	}
	
	public boolean addRoutes(Node to, Node from){
		return this.routes.put(to, from) != null ? true: false;
	}
	
	public boolean addResourceType(String name) throws ClassNotFoundException{
		return addResourceType(name, defaultsize);
	}
	
	public boolean addResourceType(String name, int size) throws ClassNotFoundException{
		return resources.addType(name, size);
	}
	
	public boolean addResource(Resource r){
		return resources.add(r);
	}
	
	public boolean addResource(String type, String rName){
		if (rName == "" || type == "") return false;
		try {
			type = "emma.model.resources." + type;
			Constructor<Resource> Resourcetype = (Constructor<Resource>) Class.forName(type).getDeclaredConstructor(String.class);
			Resource r = (Resource) Resourcetype.newInstance(rName);
			return resources.add(r);
			
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("**WARNING** Root resource " + type + " unknown");
		} 
		return false;
	}
	

	
	public void fromJSON(JSONObject obj) throws ClassNotFoundException, JSONException{
		
		JSONObject properties = obj.getJSONObject("properties");
		Iterator it = properties.keys();
		while(it.hasNext()){
			String key = (String) it.next();
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
			Iterator it = this.properties.entrySet().iterator();
			while(it.hasNext()){
				Entry<String,String> pair = (Entry<String, String>) it.next();
				objProperties.put(pair.getKey(), pair.getValue());
			}			
		}
		/*
		 * Add Node neighbors
		 */
		if(this.neighbors != null){
			JSONArray objNeighbors = new JSONArray();
			Iterator itn = this.neighbors.iterator();
			while(itn.hasNext()){
				Node n = (Node) itn.next();
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
			Iterator it = this.routes.entrySet().iterator();
			while(it.hasNext()){
				JSONObject objroute = new JSONObject();
				Entry<Node, Node> pair = (Entry<Node, Node>) it.next();
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

	public String getNsURI() {
		return nsURI;
	}

}
