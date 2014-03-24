package emma.petri.view;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.event.SubnetListener;
import emma.petri.model.Subnet;

public abstract class SubnetFigure extends Figure implements SubnetListener, Container{
	
	protected Set<ScopeFigure> scopes;
	protected Set<ArcFigure> arcs;
	protected int maxX,maxY,minX2,minY2;
	private boolean collapsed;
	
	public SubnetFigure(int posX, int posY, int width, int height, NetFigure parent){
		super(posX, posY, width, height,parent,new Subnet(parent.getNet()));
		this.scopes = new HashSet<ScopeFigure>();
		this.arcs = new HashSet<ArcFigure>();
		this.resetSizeExtremum();
		this.getSubnet().addListener(this);
		this.collapsed=false;
	}
	
	public boolean addFigure(ArcFigure a){
		return arcs.add(a);
	}

	public boolean removeFigure(ArcFigure a) {
		return arcs.remove(a);
	}
	
	public boolean addFigure(ScopeFigure s){
		if(scopes.add(s)){
			resetSizeExtremum(s);
		}
		return false;
	}
	
	public boolean removeFigure(ScopeFigure s) {
		if(scopes.remove(s)){
			resetSizeExtremum();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean addPlaceFigure(int posX, int posY) {
		int posX2 = posX+PlaceFigure.getDefaultWidth();
		int posY2 = posY+PlaceFigure.getDefaultHeight();
		Iterator<ScopeFigure> is = scopes.iterator();
		while(is.hasNext()){
			ScopeFigure s = is.next();
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
		return false;
	}

	@Override
	public boolean addTransitionFigure(int posX, int posY) {
		int posX2 = posX+PlaceFigure.getDefaultWidth();
		int posY2 = posY+PlaceFigure.getDefaultHeight();
		Iterator<ScopeFigure> is = scopes.iterator();
		while(is.hasNext()){
			ScopeFigure s = is.next();
			int originX = s.getX();
			int originY = s.getY();
			int endX = originX+s.getWidth();
			int endY = originY+s.getHeight();
			//On cherche une inclusion...
			if(posX>=originX && posX2<=endX && posY>=originY && posY2<=endY){
				return s.addTransitionFigure(posX,posY);	
			}
		}
		//par defaut c'est le subnet en cours
		return false;
	}
	
	@Override
	public boolean addSubnetFigure(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
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
		Iterator<ScopeFigure> is = scopes.iterator();
		while(is.hasNext()){
			resetSizeExtremum(is.next());
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
		Iterator<ScopeFigure> is = scopes.iterator();
		while(is.hasNext()){
			ScopeFigure f = is.next();
			if(f!=toIgnore){				
				if(x+width>=f.getX()-20 && x<=f.getX()+f.getWidth()+20){
					if(y+height>=f.getY()-20 && y<=f.getY()+f.getHeight()+20){
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
		Iterator<ScopeFigure> it = scopes.iterator();
		while(it.hasNext()){
			ScopeFigure d = it.next();
			d.moveBy(x, y,false);
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
	
	
	@Override
	public Drawable getDrawable(int x, int y){
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
		Iterator<ScopeFigure> it = scopes.iterator();
		while(it.hasNext()){
			ScopeFigure f=it.next();
			if(x>f.getX() && x<f.getX()+f.getWidth()){
				if(y>f.getY() && y<f.getY()+f.getHeight()){
					return f.getDrawable(x, y);
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
	
	public Set<ArcFigure> getArcFigures(){
		return arcs;
	}
	
	@Override
	protected void deleteLinks(Drawable caller){
		Iterator<ScopeFigure> it = scopes.iterator();
		while(it.hasNext()){
			it.next().delete(this);
		}
		scopes.clear();
	}
	
	public boolean setName(String name){
		getSubnet().setName(name);
		return true;
	}
	
	public String getName(){
		return getSubnet().getName();
	}
	
	public boolean isCollapsed(){
		return collapsed;
	}
	
	public void switchCollapse(){
		collapsed=!collapsed;
	}

	public boolean addScopeFigure(int x, int y, int width, int height) {
		Iterator<ScopeFigure> it = scopes.iterator();
		while(it.hasNext()){
			ScopeFigure s = it.next();
			int originX = s.getX();
			int originY = s.getY();
			int endX = originX+s.getWidth();
			int endY = originY+s.getHeight();
			//Un scope ne peut pas etre intersecté avec un autre
			if((x>=originX && x<=endX) || (x+width>=originX && x+width<=endX)){
				//Si il y a intersection ou inclusion en Y, alors il y a intersection
				if((y>=originY && y<=endY) || ((y+height)>=originY && (y+height)<=endY) ){
					return false;
				}
			}
		}
		//Il n'y a aucune inclusion ou intersection avec un scope existant, 
		//c'est donc un nouveau scope
		return this.addFigure(createScopeFigure(x,y,width,height));
	}
	
	public boolean addArcFigure(int originX, int originY, int endX, int endY){
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
	
	@Override
	public PlaceFigure getPlaceFigure(int x, int y){
		ScopeFigure d;
		Iterator<ScopeFigure> it = scopes.iterator();
		while(it.hasNext()){
			d = it.next();
			if(x>=d.getX() && x<=d.getX()+d.getWidth() && y>=d.getY() && y<=d.getY()+d.getHeight()){
				return d.getPlaceFigure(x,y);
			}
		}
		return null;
	}
	
	@Override
	public TransitionFigure getTransitionFigure(int x, int y){
		ScopeFigure d;
		Iterator<ScopeFigure> it = scopes.iterator();
		while(it.hasNext()){
			d = it.next();
			if(x>=d.getX() && x<=d.getX()+d.getWidth() && y>=d.getY() && y<=d.getY()+d.getHeight()){
				return d.getTransitionFigure(x,y);
			}
		}
		return null;
	}
	
	protected abstract ScopeFigure createScopeFigure(int x, int y, int width, int height);
	
	protected abstract ArcFigure createArcFigure(PlaceFigure p, TransitionFigure t, boolean input);
}
