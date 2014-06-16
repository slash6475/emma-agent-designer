package emma.mapper.mapobj.resources;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import emma.mapper.Mapping;
import emma.model.nodes.Node;
import emma.petri.model.OutputArc;
import emma.petri.model.Scope;

public class A extends emma.petri.model.resources.A implements MappedResource {
	
	private Mapping mapping;
	private Targets targs;
	
	public A(emma.petri.model.resources.A unmapped, Mapping m) throws ScopeAddressesException{
		super(unmapped.getName(),unmapped.getTransition());
		this.mapping=m;
		this.targs=new Targets(unmapped.getTransition().getOutputArcs());
	}
	
	@Override
	public String toString(){
		StringBuffer strBuf=new StringBuffer();
		strBuf.append("{\"NAME\":\"");
		strBuf.append(getName());
		strBuf.append("\",\"PRE\":\"");
		strBuf.append(this.getCondition());	
		strBuf.append("\",\"POST\":[");
		strBuf.append(targs.getWith());
		strBuf.append("],\"TARGET\":[");
		strBuf.append(targs.getDo());
		strBuf.append("]}");
		return strBuf.toString();
	}
	
	private class Targets {
		private List<String> withs;
		private List<String> dos;
		
		public Targets(Collection<OutputArc> arcs) throws ScopeAddressesException{
			HashMap<Scope, String> scopesAddresses = A.this.mapping.getScopeAddresses();
			HashMap<Scope, List<Node>> nodes = A.this.mapping.getScopeMapping();
			this.withs=new LinkedList<>();
			this.dos=new LinkedList<>();
			Iterator<OutputArc> it = arcs.iterator();
			while(it.hasNext()){
				OutputArc a = it.next();
				Scope s = a.getPlace().getParent();
				List<String> addrList = new LinkedList<>();
				if(scopesAddresses.containsKey(s)){
					addrList.add(scopesAddresses.get(s));
				}
				else if(nodes.containsKey(s)){
					Iterator<Node> itNode = nodes.get(s).iterator();
					while(itNode.hasNext()){
						Node n = itNode.next();
						addrList.add("["+n.getIp()+"]:"+n.getPort());
					}
				}
				else{
					throw new ScopeAddressesException("Address not found for scope :"+a.getPlace().getParent().getName());
				}
				Iterator<String> itS = addrList.iterator();
				while(itS.hasNext()){
					withs.add("\""+a.getExpression().replace(':', '#').replace("?x", "R#"+a.getPlace().getName())+"\"");
					dos.add("\"PUT"+itS.next()+"/"+a.getPlace().getType()+"/"+a.getPlace().getName()+"\"");
				}					
			}
		}
		
		public String getWith(){
			Iterator<String> it = withs.iterator();
			if(it.hasNext()){
				StringBuffer strBuf = new StringBuffer();
				strBuf.append(it.next());
				while(it.hasNext()){
					strBuf.append(it.next());
				}
				return strBuf.toString();
			}
			return "";
		}
		
		public String getDo(){
			Iterator<String> it = dos.iterator();
			if(it.hasNext()){
				StringBuffer strBuf = new StringBuffer();
				strBuf.append(it.next());
				while(it.hasNext()){
					strBuf.append(it.next());
				}
				return strBuf.toString();
			}
			return "";
		}
	}
	
	@Override
	public int getSize(){
		return this.toString().length();
	}
}