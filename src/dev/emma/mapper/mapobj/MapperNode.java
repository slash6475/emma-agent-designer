package emma.mapper.mapobj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import emma.model.nodes.Node;
import emma.model.resources.Resource;
import emma.petri.model.Place;
import emma.petri.model.resources.UnmappedResource;

/**
 * MapperNode is made from a classical node and contains 
 * additional computed info used by the mapping process
 * @author pierrotws
 *
 */

public class MapperNode {
	private Node n;
	private HashMap<String,Integer> dynamicCapa;
	private HashMap<String,List<String>> resLists;
	private HashMap<Node, Integer> distance;
	private HashMap<String,List<UnmappedResource>> resToMapLists;
	
	public static Set<MapperNode> getMapperNodes(Set<Node> nCol){
		//On créé l'ensemble des noeuds (mapper)
		Set<MapperNode> mNodeCol = new HashSet<>();
		Iterator<Node> itNode = nCol.iterator();
		while(itNode.hasNext()){
			//Pour tous les noeuds réels, on créé un MapperNoeud
			mNodeCol.add(new MapperNode(itNode.next()));
		}
		//Pour tous les noeuds (mapper), on calcule la table de distance 
		Iterator<MapperNode> itMNode = mNodeCol.iterator();
		while(itMNode.hasNext()){
			itMNode.next().feedDistanceMap(nCol);
		}
		return mNodeCol;
	}
	
	private MapperNode(Node n){
		this.n = n;
		this.distance=new HashMap<>();
		this.dynamicCapa=new HashMap<>();
		this.resToMapLists=new HashMap<>();
		this.resLists=new HashMap<>();
		Iterator<String> itType = n.getResourceRoots().iterator();
		while(itType.hasNext()){
			String type = itType.next();
			resToMapLists.put(type, new ArrayList<UnmappedResource>());
			Iterator<Resource> itRes = n.getResourceRoot(type).iterator();
			int capa=0;
			List<String> list = new ArrayList<>();
			while(itRes.hasNext()){
				Resource r = itRes.next();
				//Si la ressource n'a pas de nom, c'est qu'elle est disponible
				if(r.getName()==""){
					capa++;
				}
				else{
					//Sinon on l'ajoute dans une liste
					list.add(r.getName());
				}
			}
			if(capa>0){
				dynamicCapa.put(type, capa);
			}
			resLists.put(type, list);
		}
	}
	
	private void feedDistanceMap(Set<Node> mNodeCol){
		Iterator<Node> it = mNodeCol.iterator();
		while(it.hasNext()){
			Node nodeTo = it.next();
			distance.put(nodeTo, this.getDistance(n,nodeTo));
		}
	}
	
	private int getDistance(Node from, Node to){
		if(from==to){
			return 0;
		}
		if(from.isNeighbor(to)){
			return 1;
		}
		Entry<Node,Node> route = from.getRoute(to.getIp());
		if(route==null){
			return -1;
		}
		else{
			return getDistance(route.getValue(),to)+1;
		}
	}
	
	public Node getNode(){
		return n;
	}

	public boolean isAuthorized(MapperScope s){
		HashMap<String,Integer> needs = new HashMap<>();
		Iterator<Place> it = s.getScope().getPlaces().iterator();
		while(it.hasNext()){
			UnmappedResource r = it.next().getData();
			String type = r.getClass().getSimpleName();
			String name = r.getName();
			if(!n.getResourceRoots().contains(type)){
				return false;
			}
			if(r.isImported()){
				if(!this.getResourceNameList(type).contains(name)){
					return false;
				}
			}
			else{
				int qty = 1;
				if(needs.containsKey(type)){
					qty += needs.get(type);
				}
				needs.put(type, qty);
			}
		}
		Iterator<String> itType = needs.keySet().iterator();
		while(itType.hasNext()){
			String type = itType.next();
			if(needs.get(type)>dynamicCapa.get(type)){
				return false;
			}
		}
		return true;
	}
	
	public int getDistance(MapperNode n){
		if(n==this){
			return 0;
		}
		else{
			return distance.get(n.getNode());
		}
	}
	
	public void addResourceToMap(UnmappedResource r){
		resToMapLists.get(r.getClass().getSimpleName()).add(r);
	}
	
	public List<UnmappedResource> getResourcesToMap(String type){
		return resToMapLists.get(type);
	}
	
	public int getDisponibility(String type){
		return dynamicCapa.get(type);
	}
	
	public Set<String> getDisponibilityTypes(){
		return dynamicCapa.keySet();
	}
	
	public List<String> getResourceNameList(String type){
		return resLists.get(type);
	}
}
