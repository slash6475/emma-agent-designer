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
import emma.model.resources.tomap.ResourceToMap;
import emma.petri.model.Place;

public class MapperNode {
	private Node n;
	private HashMap<String,Integer> dynamicCapa;
	private HashMap<Node, Integer> distance;
	private HashMap<String,List<ResourceToMap>> resToMapLists;
	
	public static Set<MapperNode> getMapperNodes(Set<Node> nCol){
		Set<MapperNode> mNodeCol = new HashSet<>();
		Iterator<Node> itNode = nCol.iterator();
		while(itNode.hasNext()){
			mNodeCol.add(new MapperNode(itNode.next()));
		}
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
		Iterator<String> itType = n.getResourceRoots().iterator();
		while(itType.hasNext()){
			String type = itType.next();
			resToMapLists.put(type, new ArrayList<ResourceToMap>());
			Iterator<Resource> itRes = n.getResourceRoot(type).iterator();
			int capa=0;
			List<String> list = new ArrayList<>();
			while(itRes.hasNext()){
				Resource r = itRes.next();
				if(r.getName()==""){
					capa++;
				}
				else{
					list.add(r.getName());
				}
			}
			if(capa>0){
				dynamicCapa.put(type, capa);
			}
		}
	}
	
	private void feedDistanceMap(Set<Node> mNodeCol){
		Iterator<Node> it = mNodeCol.iterator();
		while(it.hasNext()){
			Node mNode = it.next();
			if(mNode!=n){
				Entry<Node,Node> route = n.getRoute(mNode.getIp());
				if(route == null){
					distance.put(mNode, -1);
				}
				else{
					int dist = 1;
					while(route.getKey()!=route.getValue() && route!=null){
						dist++;
						route = route.getValue().getRoute(mNode.getIp());
					}
					if(route==null){
						distance.put(mNode, -1);
					}
					else{
						distance.put(mNode, dist);
					}
				}
			}
		}
	}
	
	public Node getNode(){
		return n;
	}
	
	public boolean isAuthorized(MapperScope s){
		HashMap<String,Integer> needs = new HashMap<>();
		Iterator<Place> it = s.getScope().getPlaces().iterator();
		while(it.hasNext()){
			ResourceToMap r = it.next().getData();
			String type = r.getClass().getSimpleName();
			String name = r.getName();
			if(!n.getResourceRoots().contains(type)){
				return false;
			}
			if(r.isImported()){
				Iterator<Resource> itR = n.getResourceRoot(type).iterator();
				boolean has=false;
				while(itR.hasNext()){
					if(itR.next().getName().equals(name)){
						has=true;
						break;
					}
				}
				if(!has){
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
				System.out.println("Node has "+dynamicCapa.get(type)+" and need "+needs.get(type)+" for "+type);
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
	
	public void addResourceToMap(ResourceToMap r){
		resToMapLists.get(r.getClass().getSimpleName()).add(r);
	}
	
	public List<ResourceToMap> getResourcesToMap(String type){
		return resToMapLists.get(type);
	}
	
	public int getDisponibility(String type){
		return dynamicCapa.get(type);
	}
	
	public Set<String> getDisponibilityTypes(){
		return dynamicCapa.keySet();
	}
}
