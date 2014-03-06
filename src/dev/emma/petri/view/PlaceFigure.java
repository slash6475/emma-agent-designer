package emma.petri.view;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.event.PlaceEvent;
import emma.petri.control.event.PlaceListener;
import emma.petri.control.event.TokensAddedEvent;
import emma.petri.control.event.TokensRemovedEvent;
import emma.petri.model.Place;

public abstract class PlaceFigure extends Figure implements PlaceListener{
	
	/** The DEFAULT_SIZE of the circle representing the place. */
	private final static int DEFAULT_SIZE = 30;
	
	private Set<ArcFigure> arcs;
	private VirtualPlaceFigure virtual;
	
	public PlaceFigure (int x, int y, SubnetFigure s) {
		this(x, y,s,new Place(s.getSubnet()));
		virtual=null;
	}
	
	public PlaceFigure(int x, int y, SubnetFigure s, Place p){
		super(x, y, DEFAULT_SIZE, DEFAULT_SIZE,s,p);
		arcs = new HashSet<ArcFigure>();
		getPlace().addListener(this);
	}

	public boolean addArcFigure(ArcFigure a){
		return arcs.add(a);
	}
	
	public boolean removeArcFigure(ArcFigure a){
		return arcs.remove(a);
	}
	
	/**
	 * Returns the place that is represented by this figure.
	 * @return a {@link org.rakiura.cpn.Place Place}
	 */
	public Place getPlace() {
		return (Place)getElement();
	}

	@Override
	public void notify(PlaceEvent anEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notify(TokensRemovedEvent anEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notify(TokensAddedEvent anEvent) {
		// TODO Auto-generated method stub
		
	}

	public static int getDefaultWidth(){
		return DEFAULT_SIZE;
	}
	
	public static int getDefaultHeight(){
		return DEFAULT_SIZE;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof PlaceFigure){
			Place a = ((PlaceFigure) o).getPlace();
			return a.equals(this.getPlace());
		}
		return false;
	}
	
	@Override
	public void delete(){
		if(!(getPlace().getData() instanceof emma.model.resources.Agent)){
			super.delete();
		}
	}
	
	@Override
	protected void deleteLinks(Drawable caller){
		if(!(caller instanceof SubnetFigure)){
			getParent().removeFigure(this);
		}
		Iterator<ArcFigure> it = arcs.iterator();
		while(it.hasNext()){
			it.next().delete(this);
		}
		this.removeVirtualisation();
		arcs.clear();
		arcs=null;
	}
	
	public boolean setName(String name){
		getElement().setName(name);
		return true;
	}
	public String getName(){
		return getElement().getName();
	}
	
	@Override
	public boolean moveBy(int x, int y, boolean safely){
		return super.moveBy(x, y, safely);
	}
	
	public boolean moveVirtualBy(int x, int y){
		if(this.isVirtualized()){
			return virtual.moveBy(x, y,false);
		}
		return false;
	}
	
	public boolean createVirtualization(){
		if(!this.isVirtualized()){
			if(getParent().getParent()==null) return false;
			virtual=createVirtualPlaceFigure();
			if(getParent().getParent().addFigure(virtual)){
				return true;
			}
			else{
				virtual=null;
			}
		}
		return false;
	}
	
	public boolean removeVirtualisation(){
		if(virtual!=null){
			virtual.delete(this);
			return true;
		}
		return false;
	}
	
	
	public boolean setVirtualization(VirtualPlaceFigure p){
		virtual=p;
		return true;
	}
	
	protected abstract VirtualPlaceFigure createVirtualPlaceFigure();
}
