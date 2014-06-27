package emma.mapper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import emma.mapper.mapobj.resources.MappedResource;
import emma.model.nodes.Node;
import emma.petri.model.Place;
import emma.petri.model.Scope;
import emma.petri.model.resources.UnmappedResource;

/**
 * Class representing a Mapping
 * A mapping is, for each node, 
 * the list of resource to put on.
 * @author pierrotws
 *
 */
public class Mapping extends HashMap<Node,List<MappedResource>>{
	private static final long serialVersionUID = -4614938823972938264L;
	private static Logger logger = Logger.getLogger(Mapping.class);
	
	private HashMap<Scope,List<Node>> mappedScopes;	
	//Utilisé lorsqu'un scope à une adresse IP spécifique (Broadcast ou Multicast, pas encore implémenté)
	private HashMap<Scope,String> uniqueAdresses;
	//Pour chaque noeud la liste des Noeuds dont il faut gérer le déploiement (AD en poupées russes)
	private HashMap<Node,List<Node>> deployList;
	//Pour chaque noeud, l'indice de l'AD correspondant
	private HashMap<Node,Integer> deployNumber;
	
	private int counter;
	//Le point d'entrée du déploiement (AD0)
	private Node entryPoint;
	
	public Mapping(HashMap<Scope,String> addresses){
		super();
		this.mappedScopes=new HashMap<>();
		this.deployList = new HashMap<>();
		this.deployNumber=new HashMap<>();
		this.uniqueAdresses=addresses;
	}
	
	/**
	 * 
	 * @param n a node key
	 * @param r a resource
	 * @return true if r should be mapped on n, false otherwise
	 */
	public boolean contains(Node n, MappedResource r){
		return this.get(n).contains(r);
	}
	
	/**
	 * Function to call just after adding all the mapped scopes
	 */
	public void finalize(){
		Iterator<Scope> itScope = mappedScopes.keySet().iterator();
		while(itScope.hasNext()){
			Scope scope = itScope.next();
			Iterator<Node> itNode = mappedScopes.get(scope).iterator();
			while(itNode.hasNext()){
				Node n = itNode.next();
				if(!this.containsKey(n)){
					this.put(n, new LinkedList<MappedResource>());
				}
				List<MappedResource> list = this.get(n);
				Iterator<Place> itPlace = scope.getPlaces().iterator();
				while(itPlace.hasNext()){
					Place p = itPlace.next();
					UnmappedResource res = p.getData();
					if(!res.isImported()){
						try {
							list.add((MappedResource)Class.forName("emma.mapper.mapobj.resources."+p.getType()).getConstructor(Class.forName("emma.petri.model.resources."+p.getType()),Mapping.class).newInstance(p.getData(),this));
						} catch (InstantiationException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException
								| NoSuchMethodException | SecurityException
								| ClassNotFoundException e) {
							logger.warn(e.getMessage());
						}
					}
				}
			}
		}
		Iterator<Node> itNode = this.keySet().iterator();
		while(itNode.hasNext()){
			Node n = itNode.next();
			if(n.isEntryNode()){
				this.entryPoint=n;
				break;
			}
		}
		List<Node> marks = new LinkedList<Node>();
		marks.add(this.entryPoint);
		this.setDeployNode(marks, this.entryPoint);
	}
	
	/**
	 * @param marks the already-marked node list
	 * @param node the node we need to set list of node to handle deployment (russian dolls deployment)
	 */
	private void setDeployNode(List<Node> marks, Node node){
		//On liste tous les noeuds voisins, si il ne sont pas déjà managés, on les ajoute à la liste
		//et on les marque
		Iterator<Node> itNode = node.getNeighbors().iterator();
		List<Node> list = new LinkedList<>();
		while(itNode.hasNext()){
			Node n = itNode.next();
			if(!marks.contains(n)){
				list.add(n);
				marks.add(n);
			}
		}
		deployList.put(node, list);
		itNode = list.iterator();
		while(itNode.hasNext()){
			this.setDeployNode(marks,itNode.next());
		}
	}
	/**
	 * 
	 * @param s a scope key
	 * @param n a node
	 * @return true if r should be mapped on n, false otherwise
	 */
	public boolean add(Scope s, Node n){
		if(this.containsKey(n)){
			this.put(n, new LinkedList<MappedResource>());
		}
		if(mappedScopes.containsKey(s)){
			return mappedScopes.get(s).add(n);
		}
		else{
			List<Node> nl = new LinkedList<Node>();
			nl.add(n);
			return (mappedScopes.put(s, nl)==n);
		}
	}
	
	/**
	 * @return the hashmap scope -> List of node where the scope is mapped (generally only one)
	 */
	public HashMap<Scope,List<Node>> getScopeMapping(){
		return mappedScopes;
	}
	/**
	 * 
	 * @return the HashMap scope -> address (when scope has a  special multicast/broadcast address).
	 * This Map is used by Agent Resource to determine the IP Adress used to send data. When not set,
	 * Agent Resource use the addresses of nodes where the scope is mapped
	 */
	public HashMap<Scope, String> getScopeAddresses(){
		return this.uniqueAdresses;
	}
	
	private String getDeploymentAgentByNode(StringBuffer strBuf,Node node){
		int count = counter++;
		this.deployNumber.put(node, count);
		strBuf.append("{\"NAME\":\"AD");
		strBuf.append(count);
		strBuf.append("\",\"PRE\":\"1\",\"POST\":[");
		Iterator<MappedResource> itRes = this.get(node).iterator();
		if(itRes.hasNext()){
			strBuf.append(itRes.next());
		}
		while(itRes.hasNext()){
			strBuf.append(',');
			strBuf.append(itRes.next());
		}
		Iterator<Node> itNode = this.deployList.get(node).iterator();//;
		while(itNode.hasNext()){
			Node to = itNode.next();
			if(this.containsKey(to) || this.deployList.get(to).size()>0){
				strBuf.append(',');
				this.getDeploymentAgentByNode(strBuf,to);
			}
		}
		strBuf.append(",\"\"],\"TARGET\":[");
		itRes = this.get(node).iterator();
		if(itRes.hasNext()){
			MappedResource res = itRes.next();
			strBuf.append("\"POST[0::1]:");
			strBuf.append(node.getPort());
			strBuf.append("/");
			strBuf.append(res.getClass().getSimpleName());
			strBuf.append("/");
			strBuf.append(res.getName());
			strBuf.append("/\"");
		}
		while(itRes.hasNext()){
			MappedResource res = itRes.next();
			strBuf.append(",\"POST[0::1]:");
			strBuf.append(node.getPort());
			strBuf.append("/");
			strBuf.append(res.getClass().getSimpleName());
			strBuf.append("/");
			strBuf.append(res.getName());
			strBuf.append("/\"");
		}
		itNode = this.deployList.get(node).iterator();
		while(itNode.hasNext()){
			Node to = itNode.next();
			if(this.containsKey(to) || this.deployList.get(to).size()>0){
				strBuf.append(",\"POST[");
				strBuf.append(to.getIp());
				strBuf.append("]:");
				strBuf.append(to.getPort());
				strBuf.append("/A/AD");
				strBuf.append(deployNumber.get(to));
				strBuf.append("/\"");
			}
		}
		strBuf.append(",\"DEL[0::1]:");
		strBuf.append(node.getPort());
		strBuf.append("/A/AD");
		strBuf.append(count);
		strBuf.append("/\"");
		strBuf.append("]}");
		return strBuf.toString();
	}
	/**
	 * 
	 * @return the russian doll agent deployment
	 */
	public String getDeploymentAgent(){
		return this.getDeploymentAgentByNode(new StringBuffer(),entryPoint);
	}
}
