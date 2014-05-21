package emma.petri.model;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.model.resources.tomap.ResourceToMap;
import emma.petri.control.event.DeletionEvent;
import emma.petri.control.event.NameChangedEvent;
import emma.petri.control.event.StateChangedEvent;
import emma.petri.control.listener.PlaceListener;

public class Place  extends PT{
	private static int q=0;
	private ResourceToMap res;
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
		pls = new HashSet<PlaceListener>();
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
		return "NULL";
	}
	
	public boolean setType(Class<? extends ResourceToMap> c){
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
	
	public ResourceToMap getData(){
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
		pls.add(l);
	}

	public Color getDataColor() {
		return(res==null)?Color.gray:res.getColor();
	}
	
	public Set<PlaceListener> getListeners(){
		return pls;
	}
	
	@Override
	protected void notifyDeletion() {
		// TODO Auto-generated method stub
		DeletionEvent e = new DeletionEvent(this);
		Iterator<PlaceListener> it = pls.iterator();
		while(it.hasNext()){
			it.next().notity(e);
		}
	}
}
