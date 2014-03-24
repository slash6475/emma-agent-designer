package emma.petri.view;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.petri.control.event.PetriEvent;
import emma.petri.model.Net;

public abstract class NetFigure extends Figure implements Container{

	private Set<SubnetFigure> subnets;
	public NetFigure() {
		super(0, 0, 1200, 900, null,new Net());
		super.switchNotationsVisibility();
		this.subnets = new HashSet<SubnetFigure>();
	}

	public Net getNet(){
		return (Net)getElement();
	}

	public Set<SubnetFigure> getSubnetFigures() {
		return subnets;
	}
	
	@Override
	public void handle(PetriEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean resize(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetSizeExtremum() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void minimize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinX2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinY2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean addTransitionFigure(int x, int y) {
		int width = PlaceFigure.getDefaultWidth();
		int height = PlaceFigure.getDefaultHeight()+TransitionFigure.getDefaultHeight()+5;
		Iterator<SubnetFigure> is = subnets.iterator();
		while(is.hasNext()){
			SubnetFigure f = is.next();
			if(x>=f.getX() && x+width<=f.getX()+f.getWidth()){
				if(y>=f.getY() && y+height<=f.getY()+f.getHeight()){
					return f.addTransitionFigure(x, y);
				}
			}
		}
		return false;
	}

	@Override
	public boolean addPlaceFigure(int x, int y) {
		int width = PlaceFigure.getDefaultWidth();
		int height = PlaceFigure.getDefaultHeight();
		Iterator<SubnetFigure> is = subnets.iterator();
		while(is.hasNext()){
			SubnetFigure f = is.next();
			if(x>=f.getX() && x+width<=f.getX()+f.getWidth()){
				if(y>=f.getY() && y+height<=f.getY()+f.getHeight()){
					return f.addPlaceFigure(x, y);
				}
			}
		}
		return false;
	}


	@Override
	public boolean addArcFigure(int originX, int originY, int endX, int endY) {
		int x,y,width,height;
		Iterator<SubnetFigure> it = getSubnetFigures().iterator();
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
		return false;
	}

	@Override
	public boolean addScopeFigure(int x, int y, int width, int height) {
		Iterator<SubnetFigure> is = subnets.iterator();
		while(is.hasNext()){
			SubnetFigure f = is.next();
			if(x>=f.getX() && x+width<=f.getX()+f.getWidth()){
				if(y>=f.getY() && y+height<=f.getY()+f.getHeight()){
					return f.addScopeFigure(x, y, width, height);
				}
			}
		}
		return false;
	}

	@Override
	public boolean addSubnetFigure(int x, int y, int width, int height) {
		if(this.allowObjectPosition(x, y, width, height)){
			return subnets.add(this.createSubnetFigure(x, y, width, height));
		}
		return false;
	}
	
	@Override
	public Drawable getDrawable(int x, int y) {
		Iterator<SubnetFigure> is = subnets.iterator();
		while(is.hasNext()){
			SubnetFigure f = is.next();
			if(x>=f.getX() && x<=f.getX()+f.getWidth()){
				if(y>=f.getY() && y<=f.getY()+f.getHeight()){
					return f.getDrawable(x, y);
				}
			}
		}
		return null;
	}

	@Override
	public PlaceFigure getPlaceFigure(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransitionFigure getTransitionFigure(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void deleteLinks(Drawable caller) {
		// TODO Auto-generated method stub		
	}	
	@Override
	public boolean moveBy(int x, int y, boolean b) {
		return false;
	}
	
	@Override
	public boolean switchNotationsVisibility(){
		return false;
	}
	
	@Override
	public boolean allowObjectPosition(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return this.allowObjectPosition(x, y, width, height, null);
	}
	
	@Override
	public boolean allowObjectPosition(int x, int y, int width, int height, Drawable toIgnore){
		if(x<20 || y<20){
			return false;
		}
		Iterator<SubnetFigure> is = subnets.iterator();
		while(is.hasNext()){
			SubnetFigure f = is.next();
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
	
	@Override
	public void resetSizeExtremum(Figure f){
		
		this.minimize();
	}

	protected abstract SubnetFigure createSubnetFigure(int x, int y, int width, int height);
}
