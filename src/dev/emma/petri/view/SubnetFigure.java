package emma.petri.view;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.event.SubnetListener;
import emma.petri.model.Subnet;

public abstract class SubnetFigure extends Figure implements SubnetListener, Resizeable{
	
	protected Set<TransitionFigure> transitions;
	protected Set<PlaceFigure> places;
	protected Set<SubnetFigure> subnets;
	protected Set<ArcFigure> arcs;
	private int maxX,maxY,minX2,minY2;
	private boolean collapsed;
	
	public SubnetFigure(int posX, int posY, int width, int height, SubnetFigure parent){
		super(posX, posY, width, height,parent,(parent==null)?new Subnet():new Subnet(parent.getSubnet()));
		subnets = new HashSet<SubnetFigure>();
		transitions = new HashSet<TransitionFigure>();
		places = new HashSet<PlaceFigure>();
		arcs = new HashSet<ArcFigure>();
		resetSizeExtremum();
		getSubnet().addListener(this);
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
	
	public boolean addFigure(SubnetFigure s){
		if(this.allowObjectPosition(s.getX(), s.getY(), s.getWidth(), s.getHeight())){
			if(subnets.add(s)){
				//recalculating size Extremum
				resetSizeExtremum(s);
				return true;
			}
		}
		return false;
	}
	
	public boolean addFigure(ArcFigure a){
		return arcs.add(a);
	}

	public boolean removeFigure(ArcFigure a) {
		return arcs.remove(a);
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
	public boolean removeFigure(SubnetFigure s){
		if(subnets.remove(s)){
			resetSizeExtremum();
			return true;
		}
		return false;
	}
	
	
	public Subnet getSubnet(){
		return (Subnet)getElement();
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
		Iterator<SubnetFigure> is = subnets.iterator();
		while(is.hasNext()){
			resetSizeExtremum(is.next());
		}
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
		Iterator<SubnetFigure> is = subnets.iterator();
		while(it.hasNext()){
			TransitionFigure f = it.next();
			if(f!=toIgnore){
				if((x<=f.getX() && x+width>=f.getX()) || (f.getX()<=x && f.getX()+f.getWidth()>=x)){
					if((y<=f.getY() && y+height>=f.getY()) || (f.getY()<=y && f.getY()+f.getHeight()>=y)){
						return false;
					}
				}
			}
		}
		while(ip.hasNext()){
			PlaceFigure f = ip.next();
			if(!(f==toIgnore || f instanceof VirtualPlaceFigure)){
				if((x<=f.getX() && x+width>=f.getX()) || (f.getX()<=x && f.getX()+f.getWidth()>=x)){
					if((y<=f.getY() && y+height>=f.getY()) || (f.getY()<=y && f.getY()+f.getHeight()>=y)){
						return false;
					}
				}
			}
		}
		while(is.hasNext()){
			SubnetFigure f = is.next();
			if(f!=toIgnore){
				if(!(toIgnore instanceof VirtualPlaceFigure)){
					if((x<=f.getX() && x+width>=f.getX()) || (f.getX()<=x && f.getX()+f.getWidth()>=x)){
						if((y<=f.getY() && y+height>=f.getY()) || (f.getY()<=y && f.getY()+f.getHeight()>=y)){
							return false;
						}
					}
				}
				else if(((VirtualPlaceFigure)toIgnore).getLinkedPlaceFigure().getParent()!=f){
					if((x<=f.getX() && x+width>=f.getX()) || (f.getX()<=x && f.getX()+f.getWidth()>=x)){
						if((y<=f.getY() && y+height>=f.getY()) || (f.getY()<=y && f.getY()+f.getHeight()>=y)){
							return false;
						}
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
			if(!d.isVirtual()) d.moveBy(x, y,false);
		}
		Iterator<PlaceFigure> ip = places.iterator();
		while(ip.hasNext()){
			PlaceFigure d = ip.next();
			if(!d.isVirtual()){
				d.moveBy(x, y, false);
				d.moveVirtualBy(x, y);
			}
		}
		Iterator<SubnetFigure> is = subnets.iterator();
		while(is.hasNext()){
			SubnetFigure d = is.next();
			if(!d.isVirtual()) d.moveBy(x, y,false);
		}
	}
	
	
	public int getMaxX(){
		return maxX;
	}

	public int getMaxY(){
		return maxY;
	}

	public int getMinX2(){
		return minX2;
	}

	public int getMinY2(){
		return minY2;
	}
	
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
		Iterator<SubnetFigure> is = subnets.iterator();
		while(is.hasNext()){
			SubnetFigure f= is.next();
			if(x>f.getX() && x<f.getX()+f.getWidth()){
				if(y>f.getY() && y<f.getY()+f.getHeight()){
					return f.getDrawable(x,y);
				}
			}
		} 
		Iterator<ArcFigure> ia = arcs.iterator();
		double a,b;
		while(ia.hasNext()){
			ArcFigure f = ia.next();
			//Si le point est compris dans le rect (X,Y,W,H) à +/-5px
			if(x>=f.getX()-5 && x<=f.getX()+f.getWidth()+5 && y>=f.getY()-5 && y<=f.getY()+f.getHeight()+5){
				//a == b -> le point appartient à la droite
				a=(double)(x-f.getX1())/(y-f.getY1());
				b=(double)(f.getX2()-f.getX1())/(f.getY2()-f.getY1());
				//Si il est dans le rect et sur la droite, il est sur le segment
				//Avec une marge d'erreur de 0.15
				if(Math.abs(a-b)<0.18){
					return f;
				}
			}
		}
		return this;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof SubnetFigure){
			Subnet a = ((SubnetFigure) o).getSubnet();
			return a.equals(this.getSubnet());
		}
		return false;
	}
	
	public Set<TransitionFigure> getTransitionFigures(){
		return transitions;
	}
	public Set<PlaceFigure> getPlaceFigures(){
		return places;
	}
	public Set<ArcFigure> getArcFigures(){
		return arcs;
	}
	
	public Set<SubnetFigure> getSubnetFigures() {
		return subnets;
	}
	
	@Override
	protected void deleteLinks(Drawable caller){
		Iterator<TransitionFigure> it = transitions.iterator();
		if(caller==this){
			getParent().removeFigure(this);	
		}
		while(it.hasNext()){
			it.next().delete(this);
		}
		transitions.clear();
		transitions=null;
		Iterator<PlaceFigure> it2 = places.iterator();
		while(it2.hasNext()){
			it2.next().delete(this);
		}
		places.clear();
		places=null;
		arcs=null;
	}
	
	public boolean setName(String name){
		getElement().setName(name);
		return true;
	}
	
	public String getName(){
		return getElement().getName();
	}
	
	public boolean isCollapsed(){
		return collapsed;
	}
	
	public void switchCollapse(){
		collapsed=!collapsed;
	}
	

	public boolean addSubnetFigure(int x, int y, int width, int height) {
		Iterator<SubnetFigure> it = subnets.iterator();
		while(it.hasNext()){
			SubnetFigure s = it.next();
			int originX = s.getX();
			int originY = s.getY();
			int endX = originX+s.getWidth();
			int endY = originY+s.getHeight();
			//Un subnet ne peut pas etre intersecté avec un autre, en revanche, on peut
			//créé un subnet dans un autre subnet.
			if(x>=originX && x<=endX){
				//Il y a inclusion en axe X
				if((x+width)<=endX){
					//Si il y a inclusion en axe Y, alors on créé le sous-sous-réseau
					if((y>=originY && y<=endY) && ((y+height)>=originY && (y+height)<=endY) ){
						return s.addSubnetFigure(x,y,width,height);
					}
				}
				//Sinon il y a intersection en axe X
				else{
					//Si il y a intersection ou inclusion en Y, alors il y a intersection
					if((y>=originY && y<=endY) || ((y+height)>=originY && (y+height)<=endY) ){
						return false;
					}
				}
			}
			else if(x+width>=originX && x+width<=endX){
				if((y>=originY && y<=endY) || ((y+height)>=originY && (y+height)<=endY) ){
					return false;
				}
			}
		}
		//Il n'y a aucune inclusion ou intersection avec un sous réseau existant, 
		//c'est donc un nouveau sous réseau
		return this.addFigure(createSubnetFigure(x,y,width,height));
	}
	
	public boolean addPlaceFigure(int posX, int posY) {
		int posX2 = posX+PlaceFigure.getDefaultWidth();
		int posY2 = posY+PlaceFigure.getDefaultHeight();
		Iterator<SubnetFigure> it = subnets.iterator();
		while(it.hasNext()){
			SubnetFigure s = it.next();
			int originX = s.getX();
			int originY = s.getY();
			int endX = originX+s.getWidth();
			int endY = originY+s.getHeight();
			//On cherche une inclusion...
			if(posX>=originX && posX2<=endX && posY>=originY && posY2<=endY){
				return s.addPlaceFigure(posX,posY);	
			}
		}
		//par defaut c'est le subnet en cours
		return this.addFigure(createPlaceFigure(posX, posY));
	}
	
	public boolean addTransitionFigure(int x, int y) {
		int posX2 = x+TransitionFigure.getDefaultWidth();
		int posY2 = y+TransitionFigure.getDefaultHeight();
		Iterator<SubnetFigure> it = subnets.iterator();
		while(it.hasNext()){
			SubnetFigure s = it.next();
			int originX = s.getX();
			int originY = s.getY();
			int endX = originX+s.getWidth();
			int endY = originY+s.getHeight();
			//On cherche une inclusion...
			if(x>=originX && posX2<=endX && y>=originY && posY2<=endY){
				return s.addTransitionFigure(x, y);
			}
		}
		PlaceFigure p = this.createPlaceFigure(x, y);
		if(this.addFigure(p)){
			return this.addFigure(createTransitionFigure(x,y+p.getHeight()+5,p));
		}
		return false;
	}
	public boolean addArcFigure(int originX, int originY, int endX, int endY){
		int x,y,width,height;
		Iterator<SubnetFigure> it = subnets.iterator();
		if(originX>endX){
			x=endX;
			width=originX-endX;
		}
		else{
			x=originX;
			width=endX-originX;
		}
		if(originY>endY){
			y=endY;
			height=originY-endY;
		}
		else{
			y=originY;
			height=endY-originY;
		}
		while(it.hasNext()){
			SubnetFigure f = it.next();
			if(x>=f.getX() && x+width<f.getX()+f.getWidth() && y>=f.getY() && y+height<=f.getY()+f.getHeight()){
				return f.addArcFigure(originX, originY, endX, endY);
			}
		}
		PlaceFigure p;
		TransitionFigure t;
		p = getPlaceFigure(originX,originY);
		if(p!=null){
			t=getTransitionFigure(endX,endY);
			if(t!=null){
				return this.addFigure(createArcFigure(p,t, true));
			}
		}
		p = getPlaceFigure(endX,endY);
		if(p!=null){
			t=getTransitionFigure(originX,originY);
			if(t!=null){
				return this.addFigure(createArcFigure(p,t,false));
			}
		}
		return false;
	}
	
	private PlaceFigure getPlaceFigure(int x, int y){
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
	
	private TransitionFigure getTransitionFigure(int x, int y){
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
	
	protected abstract SubnetFigure createSubnetFigure(int x, int y, int width, int height);
	
	protected abstract PlaceFigure createPlaceFigure(int x, int y);
	
	protected abstract TransitionFigure createTransitionFigure(int x, int y, PlaceFigure p);
	
	protected abstract ArcFigure createArcFigure(PlaceFigure p, TransitionFigure t, boolean input);

}
