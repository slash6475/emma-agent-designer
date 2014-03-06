package emma.petri.view;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.event.TransitionEvent;
import emma.petri.control.event.TransitionListener;
import emma.petri.control.event.TransitionStartedEvent;
import emma.petri.control.event.TransitionStoppedEvent;
import emma.petri.model.Transition;

public abstract class TransitionFigure extends Figure implements TransitionListener{

	private static final int DEFAULT_WIDTH = 15;
	private static final int DEFAULT_HEIGHT = 40;
	private PlaceFigure place;
	
	private Set<ArcFigure> arcs;
	public TransitionFigure(int posX, int posY,SubnetFigure s, PlaceFigure p) {
		super(posX, posY, DEFAULT_WIDTH, DEFAULT_HEIGHT,s,new Transition(s.getSubnet(),p.getPlace()));
		this.arcs=new HashSet<ArcFigure>();
		this.getTransition().addListener(this);
		this.place=p;
	}

	@Override
	public void notify(TransitionEvent anEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notify(TransitionStartedEvent anEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notify(TransitionStoppedEvent anEvent) {
		// TODO Auto-generated method stub
		
	}

	public Transition getTransition(){
		return (Transition)getElement();
	}
	
	public static int getDefaultWidth(){
		return DEFAULT_WIDTH;
	}
	
	public static int getDefaultHeight(){
		return DEFAULT_HEIGHT;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof TransitionFigure){
			Transition a = ((TransitionFigure) o).getTransition();
			return a.equals(this.getTransition());
		}
		return false;
	}
	
	public boolean addArcFigure(ArcFigure a){
		return arcs.add(a);
	}
	
	public boolean removeArcFigure(ArcFigure a){
		return arcs.remove(a);
	}
	
	@Override
	protected void deleteLinks(Drawable caller){
		Iterator<ArcFigure> it = arcs.iterator();
		if(!(caller instanceof SubnetFigure)){
			getParent().removeFigure(this);	
		}
		while(it.hasNext()){
			it.next().delete(this);
		}
		arcs.clear();
	}
	
	public boolean setName(String name){
		getElement().setName(name);
		return true;
	}
	
	public String getName(){
		return getElement().getName();
	}
	
	public PlaceFigure getPlaceFigure(){
		return place;
	}
}
