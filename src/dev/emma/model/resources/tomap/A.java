package emma.model.resources.tomap;

import emma.petri.model.InputArc;
import emma.petri.model.OutputArc;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class A extends emma.model.resources.A implements ResourceToMap {
	
	private boolean isImported;
	private String transTest;
	private String fullTest;
	private int index;
	private String address;
	private Targets targs;
	
	public A(String name){
		super(name);
		this.isImported=false;
		this.index=0;
		this.address="";
		this.targs=new Targets();
		this.transTest="1";
		this.fullTest="1";
	}

	@Override
	public boolean setImport(boolean i) {
		this.isImported=i;
		return false;
	}

	@Override
	public boolean isImported() {
		return this.isImported;
	}

	@Override
	public Color getColor() {
		return Color.blue;
	}
	public String getCondition(){
		return this.transTest;
	}
	public void setCondition(String str){
		this.transTest=str;
	}
	public String getFullCondition(){
		return this.fullTest;
	}
	public void computePRE(Collection<InputArc> input){
		StringBuffer exprbuf = new StringBuffer(this.transTest);
		Iterator<InputArc> it = input.iterator();
		while(it.hasNext()){
			InputArc a = it.next();
			System.out.println("input "+a.getExpression());
			if(!a.getExpression().equals("1")){
				exprbuf.append(" && ");
				exprbuf.append(a.getExpression());
			}
			else{
				System.out.println(a.getPlace().getName()+" -> "+a.getTransition().getPlace().getName());
			}
		}
		System.out.println("STOP");
		this.fullTest=exprbuf.toString();
	}
	
	public void computeWITH_DO(Collection<OutputArc> output){
		Iterator<OutputArc> it = output.iterator();
		targs.clear();
		while(it.hasNext()){
			OutputArc a = it.next();
			targs.add(a.getExpression(), "POST"+a.getPlace().getData().getPath());
		}
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public void setIndex(int index) {
		this.index=index;	
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public void setAddress(String address) {
		this.address=address;
	}

	@Override
	public String getPath() {
		return this.address+"/A/"+getName();
	}
	
	private class Targets { 
		private List<String> withs;
		private List<String> targs;
		
		public Targets(){
			this.withs=new ArrayList<>();
			this.targs=new ArrayList<>();
		}
		
		public void add(String with, String targ){
			withs.add(with);
			targs.add(targ);
		}
		
		public String getWith(){
			StringBuffer strbuf = new StringBuffer();
			strbuf.append("[");
			Iterator<String> it = withs.iterator();
			if(it.hasNext()){
				strbuf.append("\"");
				strbuf.append(it.next());
				strbuf.append("\"");
			}
			while(it.hasNext()){
				strbuf.append(",\"");
				strbuf.append(it.next());
				strbuf.append("\"");
			}
			strbuf.append("]");
			return strbuf.toString();
		}
		
		public String getDo(){
			StringBuffer strbuf = new StringBuffer();
			strbuf.append("[");
			Iterator<String> it = targs.iterator();
			if(it.hasNext()){
				strbuf.append("\"");
				strbuf.append(it.next());
				strbuf.append("\"");
			}
			while(it.hasNext()){
				strbuf.append(",\"");
				strbuf.append(it.next());
				strbuf.append("\"");
			}
			strbuf.append("]");
			return strbuf.toString();
		}
		
		public void clear(){
			withs.clear();
			targs.clear();
		}
	}
	
	@Override
	public String toString(){
		StringBuffer strBuf=new StringBuffer();
		strBuf.append("{\n  \"NAME\":\"");
		strBuf.append(getName());
		strBuf.append("\",\n  \"PRE\":\"");
		strBuf.append(fullTest);	
		strBuf.append("\",\n  \"POST\":");
		strBuf.append(targs.getWith());
		strBuf.append(",\n  \"TARGET\":");
		strBuf.append(targs.getDo());
		strBuf.append("\n}");
		return strBuf.toString();
	}
}
