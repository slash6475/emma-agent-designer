package emma.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import emma.mapper.Mapping;
import emma.mapper.MappingNotFoundException;
import emma.mapper.PBSolver;
import emma.mapper.mapobj.MapperNode;
import emma.mapper.mapobj.MapperScope;
import emma.mapper.mapobj.resources.MappedResource;
import emma.model.nodes.Node;
import emma.model.resources.Resource;
import emma.petri.model.InputArc;
import emma.petri.model.Net;
import emma.petri.model.OutputArc;
import emma.petri.model.Place;
import emma.petri.model.Scope;
import emma.petri.model.Subnet;
import emma.petri.model.Transition;
import emma.petri.model.resources.L;
import emma.petri.model.resources.S;
import emma.petri.model.resources.UnmappedResource;

public class MappingTest {
	public static void main(String[] args){
		PBSolver solver;
		try {
			Set<MapperNode> nodes = MapperNode.getMapperNodes(feedNodes());
			Set<MapperScope> scopes = MapperScope.getMapperScopes(nodes, feedScopes());
			solver = new PBSolver(nodes, scopes);
			Mapping m = solver.solve();
			Iterator<Entry<Node, List<MappedResource>>> it = m.entrySet().iterator();
			while(it.hasNext()){
				Entry<Node, List<MappedResource>> e = it.next();
				System.out.println("Node "+e.getKey().getIp()+" :");
				Iterator<MappedResource> itRes = e.getValue().iterator();
				while(itRes.hasNext()){
					System.out.println("RES:"+it.next());
				}
			}
		} catch (MappingNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static Set<Node> feedNodes(){
		Set<Node> nodes = new HashSet<>();
		Node n1 = new Node("0001");
		try {
			n1.addResourceType("L");
			n1.addResourceType("A");
			n1.addResourceType("S");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Resource r = new emma.model.resources.L("");
		n1.addResource(r);
		r = new emma.model.resources.A("");
		n1.addResource(r);
		r = new emma.model.resources.S("systest");
		n1.addResource(r);
		nodes.add(n1);
		
		Node n2 = new Node("0002");
		try {
			n2.addResourceType("L");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		r = new emma.model.resources.L("");
		n2.addResource(r);
		nodes.add(n2);
		
		Node n3 = new Node("0003");
		try {
			n3.addResourceType("L");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		r = new emma.model.resources.L("");
		n3.addResource(r);
		r = new emma.model.resources.L("");
		n3.addResource(r);
		nodes.add(n3);
		
		return nodes;
	}
	
	private static Set<Scope> feedScopes(){
		UnmappedResource r;
		Set<Scope> scopes = new HashSet<>();
		Net net = new Net();
		Subnet sub = new Subnet(net);
		Scope s = new Scope(sub);
		s.setTarget("1");
		Scope s2 = new Scope(sub);
		s2.setTarget("1");
		Place p = new Place(s);
		Transition t = new Transition(s,p);
		r = p.getData();
		p = new Place(s);
		p.setData(L.class);
		p.setName("ltest");
		s.setTarget("1");
		Place p2 = new Place(s);
		p2.setData(S.class);
		p2.setName("systest");
		p2.getData().setImport(true);
		Place p3 = new Place(s2);
		p3.setData(L.class);
		p3.setName("dest");
		InputArc ia = new InputArc(p, t);
		ia = new InputArc(p2,t);
		t.addArc(new OutputArc(p3, t));
		scopes.add(s);
		scopes.add(s2);
		return scopes;
	}
}
