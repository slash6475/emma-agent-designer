package emma.petri.model;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.model.resources.Resource;

@SuppressWarnings("rawtypes")
public class Place  extends PT implements Virtualizeable{
	
	private Set<Token> tokens;
	private Resource res;
	private boolean input,output;
	private VirtualPlace virtual;
	
	public Place(Subnet s){
		super(s);
		s.add(this);
		res=null;
		input=true;
		output=true;
		virtual=null;
		tokens = new HashSet<Token>();
	}
	
	public Set<Token> getTokens(){
		return tokens;
	}
	
	public boolean addToken(Token token){
		return this.tokens.add(token);
	}
	
	public boolean addTokens(Set<Token> tokens){
		return this.tokens.addAll(tokens);
	}
	
	public boolean removeToken(Token token){
		return this.tokens.remove(token);
	}
	
	public boolean removeTokens(Set<Token> tokens){
		return this.tokens.removeAll(tokens);
	}
	
	public void removeAllTokens(){
		this.tokens.clear();
	}
	
	@Override
	protected void deleteLinks(PetriElement caller){
		Iterator<InputArc> it = getInputArcs().iterator();
		Iterator<OutputArc> it2 = getOutputArcs().iterator();
		if(!(caller instanceof Subnet)){
			getParent().remove(this);
		}
		while(it.hasNext()){
			it.next().delete(this);
		}
		getInputArcs().clear();
		while(it2.hasNext()){
			it2.next().delete(this);
		}
		getOutputArcs().clear();
		this.removeAllTokens();
		this.unvirtualize();
	}
	
	public String getType(){
		if(res!=null){
			return res.getClass().toString();
		}
		return "NULL";
	}
	
	public boolean setType(Class<? extends Resource> c){
		try {
			res = c.getConstructor().newInstance();
			return true;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public Resource getData(){
		return res;
	}

	public boolean isInput(){
		return input;
	}
	
	public boolean isOutput(){
		return output;
	}
	
	public boolean setInput(boolean b){
		input=b;
		if(isVirtualized() && !b){
			if(virtual.isInput()){
				virtual.setInput(false);
			}
		}
		return true;
	}
	
	public boolean setOutput(boolean b){
		output=b;
		if(isVirtualized() && !b){
			if(virtual.isOutput()){
				virtual.setOutput(false);
			}
		}
		return true;
	}
	
	public boolean setVirtualization(VirtualPlace p){
		if(this.virtualize()){
			virtual=p;
			return true;
		}
		return false;
	}
	
	@Override
	public void removeVirtualisation(){
		virtual.delete(this);
		virtual=null;
	}
}
