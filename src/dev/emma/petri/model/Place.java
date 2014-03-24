package emma.petri.model;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.model.resources.Resource;

@SuppressWarnings("rawtypes")
public class Place  extends PT{
	private static int q=0;
	private Set<Token> tokens;
	private Resource res;
	private boolean input,output;
	private String name;

	
	public Place(Scope s){
		super(s);
		name="p"+(q++);
		s.add(this);
		res=null;
		input=true;
		output=true;
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
	}
	
	public String getType(){
		if(res!=null){
			return res.getClass().getSimpleName();
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
		return true;
	}
	
	public boolean setOutput(boolean b){
		output=b;
		return true;
	}	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
}
