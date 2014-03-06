package emma.petri.view;

import java.util.Iterator;

import emma.view.awt.AWTSubnetFigure;

public abstract class NetFigure extends SubnetFigure{

	public NetFigure() {
		super(0, 0, 10000, 10000, null);
		super.switchNotationsVisibility();
	}

	@Override
	public void minimize(){
		return;
	}
	
	@Override
	public boolean moveBy(int x, int y, boolean b) {
		return false;
	}
	
	@Override
	public boolean select(boolean s){
		return false;
	}
	
	@Override
	public boolean switchNotationsVisibility(){
		return false;
	}
	
	@Override
	public void switchCollapse(){
		return;
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
				if((x<=f.getX()-20 && x+width>=f.getX()+20) || (f.getX()<=x-20 && f.getX()+f.getWidth()>=x+20)){
					if((y<=f.getY()-20 && y+height>=f.getY()+20) || (f.getY()<=y-20 && f.getY()+f.getHeight()>=y+20)){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean addPlaceFigure(int posX, int posY) {
		int posX2 = posX+PlaceFigure.getDefaultWidth();
		int posY2 = posY+PlaceFigure.getDefaultHeight();
		
		Iterator<SubnetFigure> it = getSubnetFigures().iterator();
		while(it.hasNext()){
			AWTSubnetFigure s = (AWTSubnetFigure)it.next();
			int originX = s.getX();
			int originY = s.getY();
			int endX = originX+s.getWidth();
			int endY = originY+s.getHeight();
			//On cherche une inclusion...
			if(posX>=originX && posX2<=endX && posY>=originY && posY2<=endY){
				return s.addPlaceFigure(posX,posY);	
			}
		}
		return false;
	}
	
	public boolean addTransitionFigure(int x, int y) {
		int posX2 = x+TransitionFigure.getDefaultWidth();
		int posY2 = y+TransitionFigure.getDefaultHeight();
		Iterator<SubnetFigure> it = getSubnetFigures().iterator();
		while(it.hasNext()){
			AWTSubnetFigure s = (AWTSubnetFigure)it.next();
			int originX = s.getX();
			int originY = s.getY();
			int endX = originX+s.getWidth();
			int endY = originY+s.getHeight();
			//On cherche une inclusion...
			if(x>=originX && posX2<=endX && y>=originY && posY2<=endY){
				return s.addTransitionFigure(x, y);
			}
		}
		return false;
	}
	
	public boolean addArcFigure(int originX, int originY, int endX, int endY){
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
	protected final PlaceFigure createPlaceFigure(int x, int y){
		return null;
	}
	
	@Override
	protected final TransitionFigure createTransitionFigure(int x, int y, PlaceFigure p){
		return null;
	}
	
	@Override
	protected final ArcFigure createArcFigure(PlaceFigure p, TransitionFigure t, boolean input){
		return null;
	}
}
