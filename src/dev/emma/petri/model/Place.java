package emma.petri.model;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.event.NameChangedEvent;
import emma.petri.control.event.StateChangedEvent;
import emma.petri.control.listener.PlaceListener;
import emma.petri.model.resources.UnmappedResource;

public class Place extends PT{
	private static int q=0;
	private UnmappedResource res;
	private boolean input,output;
	private String name;
	private Set<PlaceListener> pls;
	private boolean tokens;
	
	public Place(Scope s){
		super(s);
		this.name="p"+(q++);
		this.tokens=false;
		s.add(this);
		this.res=null;
		this.input=true;
		this.output=true;
		this.pls = new HashSet<PlaceListener>();
	}
	
	@Override
	protected void deleteLinks(PetriElement caller){
		Iterator<InputArc> it = getInputArcs().iterator();
		Iterator<OutputArc> it2 = getOutputArcs().iterator();
		if(!(caller == getParent())){
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
	}
	
	public String getType(){
		if(res!=null){
			return res.getClass().getSimpleName();
		}
		return "null";
	}
	
	public void setData(UnmappedResource res){
		this.tokens=false;
		this.res=res;
	}
	
	public boolean setData(Class<? extends UnmappedResource> c){
		try {
			res = c.getConstructor(String.class).newInstance(this.getName());
			this.tokens=false;
			Iterator<PlaceListener> it = pls.iterator();
			StateChangedEvent e = new StateChangedEvent(this);
			while(it.hasNext()){
				it.next().notify(e);
			}
			return true;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public UnmappedResource getData(){
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
		if(res!=null){
			res.setName(name);
		}
		NameChangedEvent e = new NameChangedEvent(this);
		Iterator<PlaceListener> it = pls.iterator();
		while(it.hasNext()){
			it.next().notify(e);
		}
	}

	public void addListener(PlaceListener l){
		this.addPetriEventListener(l);
		pls.add(l);
	}

	public Color getDataColor() {
		return(res==null)?Color.gray:res.getColor();
	}
	
	public Set<PlaceListener> getListeners(){
		return pls;
	}
	
	public boolean putToken(){
		if(!this.tokens && res.hasInputRight()){
			this.tokens=true;
			return true;
		}
		return false;
	}
	
	public boolean removeToken(){
		if(this.tokens){
			this.tokens=false;
			return true;
		}
		return false;
	}

	public boolean hasToken() {
		return tokens;
	}
	
	public boolean setToken(boolean t){
		return (t)?putToken():removeToken();
	}
	
	public boolean hasInputRight(){
		if(this.res==null){
			return false;
		}
		return this.res.hasInputRight();
	}
	
	public boolean hasOutputRight(){
		if(this.res==null){
			return false;
		}
		return this.res.hasOutputRight();
	}
}
