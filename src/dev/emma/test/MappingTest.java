package emma.test;

import java.util.HashSet;
import java.util.Set;

import emma.mapper.Mapping;
import emma.mapper.MappingNotFoundException;
import emma.mapper.PBSolver;
import emma.mapper.mapobj.MapperNode;
import emma.mapper.mapobj.MapperScope;
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
			System.out.println(m.getDeploymentAgent());
		} catch (MappingNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static Set<Node> feedNodes(){
		Set<Node> nodes = new HashSet<>();
		Node n1 = new Node("0001");
		n1.setEntry(true);
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
		n1.addNeighbor(n2);
		n2.addNeighbor(n1);
		try {
			n2.addResourceType("L");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		r = new emma.model.resources.L("");
		n2.addResource(r);
		nodes.add(n2);
		Node n3 = new Node("0003");
		n2.addNeighbor(n3);
		n3.addNeighbor(n2);
		n1.addRoutes(n3, n2);
		//n3.addRoutes(n1, n2);
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
		OutputArc oa = new OutputArc(p3, t);
		oa.setExpression("S:systest + ?x + L:ltest");
		t.addArc(oa);
		scopes.add(s);
		scopes.add(s2);
		return scopes;
	}
}
