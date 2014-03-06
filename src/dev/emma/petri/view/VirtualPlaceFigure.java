package emma.petri.view;

import emma.petri.model.VirtualPlace;

public abstract class VirtualPlaceFigure extends PlaceFigure {

	private PlaceFigure place;
	
	public VirtualPlaceFigure(PlaceFigure p) {
		super(p.getParent().getX()-15,(p.getParent().getY()+(p.getParent().getHeight()/2))-15, p.getParent().getParent(),new VirtualPlace(p.getPlace()));
		this.place=p;
		place.setVirtualization(this);
	}
	
	@Override
	public boolean moveBy(int x, int y, boolean safely){
		if(!safely){
			return super.moveBy(x, y, safely);
		}
		SubnetFigure p = place.getParent(); 
		if(this.getX()==p.getX()-15 || this.getX()==p.getX()+p.getWidth()-15){
			if(this.getY()+y>=p.getY()-15 && this.getY()+y<=p.getY()+p.getHeight()-15){
				if(y==0 && (this.getY()==p.getY()-15 || this.getY()+y==p.getY()+p.getHeight()-15)){
					if(this.getX()+x>=p.getX()-15 && this.getX()+x<=p.getX()+p.getWidth()-15){
						return super.moveBy(x, 0,safely);
					}
				}
				return super.moveBy(0,y,safely);
			}
		}
		else if(this.getY()==p.getY()-15 || this.getY()==p.getY()+p.getHeight()-15){
			if(this.getX()+x>=p.getX()-15 && this.getX()+x<=p.getX()+p.getWidth()-15){
				return super.moveBy(x,0,safely);
			}
		}
		return false;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof VirtualPlaceFigure){
			VirtualPlaceFigure f = (VirtualPlaceFigure) o;
			if(f.getLinkedPlaceFigure() == this.place){
				return true;
			}
		}
		return false;
	}
	
	public PlaceFigure getLinkedPlaceFigure(){
		return place;
	}
	
	protected void deleteLinks(Drawable caller){
		if(caller==this){
			place.removeVirtualisation();
		}
		else{
			super.deleteLinks(caller);
		}
	}
}
