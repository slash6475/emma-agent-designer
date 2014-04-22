package emma.petri.model;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.model.resources.Resource;
import emma.petri.control.event.NameChangedEvent;
import emma.petri.control.event.StateChangedEvent;
import emma.petri.control.listener.PlaceListener;

@SuppressWarnings("rawtypes")
public class Place  extends PT{
	private static int q=0;
	private Set<Token> tokens;
	private Resource res;
	private boolean input,output;
	private String name;
	private Set<PlaceListener> pls;
	
	public Place(Scope s){
		super(s);
		name="p"+(q++);
		s.add(this);
		res=null;
		input=true;
		output=true;
		tokens = new HashSet<Token>();
		pls = new HashSet<PlaceListener>();
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
		pls.clear();
	}
	
	public String getType(){
		if(res!=null){
			return res.getClass().getSimpleName();
		}
		return "NULL";
	}
	
	public boolean setType(Class<? extends Resource> c){
		try {
			res = c.getConstructor(String.class).newInstance(this.getName());
			Iterator<PlaceListener> it = pls.iterator();
			StateChangedEvent e = new StateChangedEvent(this);
			while(it.hasNext()){
				it.next().notify(e);
			}
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
		NameChangedEvent e = new NameChangedEvent(this);
		Iterator<PlaceListener> it = pls.iterator();
		while(it.hasNext()){
			it.next().notify(e);
		}
	}

	public void addListener(PlaceListener l){
		pls.add(l);
	}

	public Color getDataColor() {
		if(res==null){
			return Color.gray;
		}
		else if(res instanceof emma.model.resources.A){
			return Color.blue;
		}
		else if(res instanceof emma.model.resources.L){
			return Color.yellow;
		}
		else if(res instanceof emma.model.resources.S){
			return Color.red;
		}
		return Color.gray;
	}
}
