package emma.petri.view;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.event.ScopeListener;
import emma.petri.model.Scope;

public abstract class ScopeFigure extends Figure implements ScopeListener, Container{
	
	protected Set<TransitionFigure> transitions;
	protected Set<PlaceFigure> places;
	protected int maxX,maxY,minX2,minY2;
	private boolean collapsed;
	
	public ScopeFigure(int posX, int posY, int width, int height, SubnetFigure parent){
		super(posX, posY, width, height,parent,new Scope(parent.getSubnet()));
		transitions = new HashSet<TransitionFigure>();
		places = new HashSet<PlaceFigure>();
		resetSizeExtremum();
		getScope().addListener(this);
		collapsed=false;
	}
	
	public boolean addFigure(PlaceFigure p){
		if(this.allowObjectPosition(p.getX(), p.getY(), p.getWidth(), p.getHeight())){
			if(places.add(p)){
				resetSizeExtremum(p);
				return true;
			}
		}
		return false;
	}
	
	public boolean addFigure(TransitionFigure t){
		if(this.allowObjectPosition(t.getX(), t.getY(), t.getWidth(), t.getHeight())){
			if(transitions.add(t)){
				resetSizeExtremum(t);
				return true;
			}
		}
		return false;
	}
	
	public boolean removeFigure(PlaceFigure p){
		if(places.remove(p)){
			resetSizeExtremum();
			return true;
		}
		return false;
	}
	public boolean removeFigure(TransitionFigure t){
		if(transitions.remove(t)){
			resetSizeExtremum();
			return true;
		}
		return false;
	}
	
	public Scope getScope(){
		return (Scope)getElement();
	}

	public void resetSizeExtremum(Figure f){
		if(f.getX()-15<maxX){
			maxX=f.getX()-15;
		}
		if(f.getY()-15<maxY){
			maxY=f.getY()-15;
		}
		if(f.getX()+f.getWidth()+15>minX2){
			minX2=f.getX()+f.getWidth()+15;
		}
		if(f.getY()+f.getHeight()+15>minY2){
			minY2=f.getY()+f.getHeight()+15;
		}
	}
	
	public void resetSizeExtremum(){
		maxX=99999999;
		maxY=99999999;
		minX2=0;
		minY2=0;
		Iterator<TransitionFigure> it = transitions.iterator();
		while(it.hasNext()){
			resetSizeExtremum(it.next());
		}
		Iterator<PlaceFigure> ip = places.iterator();
		while(ip.hasNext()){
			resetSizeExtremum(ip.next());
		}
	}
	
	public boolean isResizeable(int x, int y, int width, int height){
		if(x<=0 || y<=0 || width<=30 || height<=30){
			return false;
		}
		if(x>maxX || y>maxY || x+width<minX2 || y+height<minY2){
			return false;
		}
		return true;
	}
	
	public boolean allowObjectPosition(int x,int y, int width, int height){
		return allowObjectPosition(x,y,width,height,null);
	}
	
	public boolean allowObjectPosition(int x, int y, int width, int height, Drawable toIgnore){
		if(x<getX()+20 || x+width>getX()+getWidth()-20 || y<getY()+20 || y+height>getY()+getHeight()-20){
			return false;
		}
		Iterator<TransitionFigure> it = transitions.iterator();
		Iterator<PlaceFigure> ip = places.iterator();
		while(it.hasNext()){
			TransitionFigure f = it.next();
			if(f!=toIgnore){
				if(x+width>=f.getX() && x<=f.getX()+f.getWidth()){
					if(y+height>=f.getY() && y<=f.getY()+f.getHeight()){
						return false;
					}
				}
			}
		}
		while(ip.hasNext()){
			PlaceFigure f = ip.next();
			if(f!=toIgnore){
				if(x+width>=f.getX() && x<=f.getX()+f.getWidth()){
					if(y+height>=f.getY() && y<=f.getY()+f.getHeight()){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean resize(int x, int y, int width, int height){
		if(isResizeable(x,y,width,height) && getParent().allowObjectPosition(x, y, width, height, this)){
			setX(x);
			setY(y);
			setWidth(width);
			setHeight(height);
		}
		return false;
	}
	
	public void minimize(){
		setX(maxX);
		setY(maxY);
		setWidth(minX2-getX());
		setHeight(minY2-getY());
	}

	@Override
	public boolean moveBy(int x, int y, boolean b) {
		if(super.moveBy(x, y, b)){
			sons_moveBy(x,y);
			return true;
		}
		return false;
	}
	
	private void sons_moveBy(int x, int y){
		maxX+=x;
		maxY+=y;
		minX2+=x;
		minY2+=y;
		Iterator<TransitionFigure> it = transitions.iterator();
		while(it.hasNext()){
			TransitionFigure d = it.next();
			d.moveBy(x, y,false);
		}
		Iterator<PlaceFigure> ip = places.iterator();
		while(ip.hasNext()){
			PlaceFigure d = ip.next();
			d.moveBy(x, y, false);
		}
	}
	
	@Override
	public int getMaxX(){
		return maxX;
	}

	@Override
	public int getMaxY(){
		return maxY;
	}

	@Override
	public int getMinX2(){
		return minX2;
	}

	@Override
	public int getMinY2(){
		return minY2;
	}
	
	@Override
	public Drawable getDrawable(int x, int y){
		Iterator<PlaceFigure> ip = places.iterator();
		while(ip.hasNext()){
			PlaceFigure f=ip.next();
			if(x>f.getX() && x<f.getX()+f.getWidth()){
				if(y>f.getY() && y<f.getY()+f.getHeight()){
					return f;
				}
			}
		}
		Iterator<TransitionFigure> it = transitions.iterator();
		while(it.hasNext()){
			TransitionFigure f=it.next();
			if(x>f.getX() && x<f.getX()+f.getWidth()){
				if(y>f.getY() && y<f.getY()+f.getHeight()){
					return f;
				}
			}
		}
		return this;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof ScopeFigure){
			Scope a = ((ScopeFigure) o).getScope();
			return a.equals(this.getScope());
		}
		return false;
	}
	
	public Set<TransitionFigure> getTransitionFigures(){
		return transitions;
	}
	public Set<PlaceFigure> getPlaceFigures(){
		return places;
	}
	
	@Override
	protected void deleteLinks(Drawable caller){
		Iterator<TransitionFigure> it = transitions.iterator();
		if(caller==this){
			((SubnetFigure)getParent()).removeFigure(this);	
		}
		while(it.hasNext()){
			it.next().delete(this);
		}
		transitions.clear();
		Iterator<PlaceFigure> it2 = places.iterator();
		while(it2.hasNext()){
			it2.next().delete(this);
		}
		places.clear();
	}
	
	public boolean setName(String name){
		getScope().setName(name);
		return true;
	}
	
	public String getName(){
		return getScope().getName();
	}
	
	public boolean isCollapsed(){
		return collapsed;
	}
	
	public void switchCollapse(){
		collapsed=!collapsed;
	}
	

	
	public boolean addPlaceFigure(int posX, int posY) {
		return this.addFigure(createPlaceFigure(posX, posY));
	}
	
	public boolean addTransitionFigure(int x, int y) {
		PlaceFigure p = this.createPlaceFigure(x, y);
		p.getPlace().setType(emma.model.resources.A.class);
		if(this.addFigure(p)){
			TransitionFigure t = createTransitionFigure(x,y+p.getHeight()+5,p);
			if(this.addFigure(t)){
				return true;
			}
			t.delete();
			this.removeFigure(p);
		}
		return false;
	}
	
	@Override
	public boolean addArcFigure(int x1, int y1, int x2, int y2) {
		return false;
	}

	@Override
	public boolean addScopeFigure(int x, int y, int width, int height) {
		return false;
	}

	@Override
	public boolean addSubnetFigure(int x, int y, int width, int height) {
		return false;
	}
	
	public PlaceFigure getPlaceFigure(int x, int y){
		PlaceFigure d;
		Iterator<PlaceFigure> ip = places.iterator();
		while(ip.hasNext()){
			d = ip.next();
			if(x>=d.getX() && x<=d.getX()+d.getWidth() && y>=d.getY() && y<=d.getY()+d.getHeight()){
				return d;
			}
		}
		return null;
	}
	
	public TransitionFigure getTransitionFigure(int x, int y){
		TransitionFigure d;
		Iterator<TransitionFigure> it = transitions.iterator();
		while(it.hasNext()){
			d = it.next();
			if(x>=d.getX() && x<=d.getX()+d.getWidth() && y>=d.getY() && y<=d.getY()+d.getHeight()){
				return d;
			}
		}
		return null;
	}
	
	protected abstract PlaceFigure createPlaceFigure(int x, int y);
	
	protected abstract TransitionFigure createTransitionFigure(int x, int y, PlaceFigure p);
}
