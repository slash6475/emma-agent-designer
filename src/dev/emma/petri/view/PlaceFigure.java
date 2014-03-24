package emma.petri.view;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.event.PlaceListener;
import emma.petri.model.Place;

public abstract class PlaceFigure extends Figure implements PlaceListener{
	
	/** The DEFAULT_SIZE of the circle representing the place. */
	private final static int DEFAULT_SIZE = 30;
	
	private Set<ArcFigure> arcs;
	
	public PlaceFigure (int x, int y, ScopeFigure s) {
		this(x, y,s,new Place(s.getScope()));
	}
	
	private PlaceFigure(int x, int y, ScopeFigure s, Place p){
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
	
	public Place getPlace() {
		return (Place)getElement();
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
		if(!(getPlace().getData() instanceof emma.model.resources.A)){
			super.delete();
		}
	}
	
	@Override
	protected void deleteLinks(Drawable caller){
		if(!(caller instanceof SubnetFigure)){
			((ScopeFigure)getParent()).removeFigure(this);
		}
		Iterator<ArcFigure> it = arcs.iterator();
		while(it.hasNext()){
			it.next().delete(this);
		}
		arcs.clear();
		arcs=null;
	}
	
	public boolean setName(String name){
		getPlace().setName(name);
		return true;
	}
	public String getName(){
		return getPlace().getName();
	}
}
