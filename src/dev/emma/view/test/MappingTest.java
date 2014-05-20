package emma.view.test;

import java.util.HashSet;
import java.util.Set;

import emma.mapper.MappingNotFoundException;
import emma.mapper.PBSolver;
import emma.mapper.mapobj.MapperNode;
import emma.mapper.mapobj.MapperScope;
import emma.model.nodes.Node;
import emma.model.resources.tomap.A;
import emma.model.resources.tomap.L;
import emma.model.resources.Resource;
import emma.model.resources.tomap.S;
import emma.petri.model.InputArc;
import emma.petri.model.Net;
import emma.petri.model.OutputArc;
import emma.petri.model.Place;
import emma.petri.model.Scope;
import emma.petri.model.Subnet;
import emma.petri.model.Transition;

public class MappingTest {
	public static void main(String[] args){
		PBSolver solver;
		try {
			Set<MapperNode> nodes = MapperNode.getMapperNodes(feedNodes());
			Set<MapperScope> scopes = MapperScope.getMapperScopes(nodes, feedScopes());
			solver = new PBSolver(nodes, scopes);
			solver.solve();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Resource r = new L("");
		n1.addResource(r);
		r = new A("");
		n1.addResource(r);
		r = new S("systest");
		n1.addResource(r);
		nodes.add(n1);
		
		Node n2 = new Node("0002");
		try {
			n2.addResourceType("L");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		r = new L("");
		n2.addResource(r);
		nodes.add(n2);
		
		Node n3 = new Node("0003");
		try {
			n3.addResourceType("L");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		r = new L("");
		n3.addResource(r);
		r = new L("");
		n3.addResource(r);
		nodes.add(n3);
		
		return nodes;
	}
	
	private static Set<Scope> feedScopes(){
		Set<Scope> scopes = new HashSet<>();
		Net net = new Net();
		Subnet sub = new Subnet(net);
		Scope s = new Scope(sub);
		Scope s2 = new Scope(sub);
		Place p = new Place(s);
		Transition t = new Transition(s,p);
		p = new Place(s);
		p.setType(L.class);
		p.setName("ltest");
		s.setMultiplicity("3");
		Place p2 = new Place(s);
		p2.setType(S.class);
		p2.setName("systest");
		p2.getData().setImport(true);
		Place p3 = new Place(s2);
		p3.setType(L.class);
		p3.setName("dest");
		t.addInputArc(new InputArc(p, t));
		t.addInputArc(new InputArc(p2,t));
		t.addOutputArc(new OutputArc(p3, t));
		scopes.add(s);
		scopes.add(s2);
		return scopes;
	}
}
