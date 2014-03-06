package emma.petri.view;

import emma.petri.control.event.ArcListener;
import emma.petri.model.Arc;
import emma.petri.model.InputArc;
import emma.petri.model.OutputArc;

public abstract class ArcFigure extends Figure implements ArcListener{
	private boolean input;
	private PlaceFigure place;
	private TransitionFigure transition;
	private int angle;
	//Distance relative par rapport aux places/transitions
	private int anchorX1, anchorX2, anchorY1, anchorY2;
	
	public ArcFigure(PlaceFigure p, TransitionFigure t, boolean input){
		super(0,0,0,0,t.getParent(),(input)?new InputArc(p.getPlace(),t.getTransition()):new OutputArc(p.getPlace(),t.getTransition()));
		this.input=input;
		this.place=p;
		this.transition=t;
		this.resetAnchors();
		this.place.addArcFigure(this);
		this.transition.addArcFigure(this);
	}
	
	public void resetAnchors(){
		int a = (int)(Math.toDegrees(Math.atan2(transition.getX()-place.getX()-7.5,transition.getY()-place.getY()-5)-(Math.PI/2d))+360)%360;
		if(a!=angle){
			int ancXPlace, ancYPlace, ancXTrans, ancYTrans;
			angle=a;
			if(angle>=315 || angle<45){
				ancXPlace = 30;
				ancYPlace = 15;
				ancXTrans = 0;
				ancYTrans = 20;
			}
			else if(angle>= 45 && angle < 135){
				ancXPlace = 15;
				ancYPlace = 0;
				ancXTrans = 8;
				ancYTrans = 40;
			}
			else if (angle>=135 && angle<225){
				ancXPlace = 0;
				ancYPlace = 15;
				ancXTrans = 15;
				ancYTrans = 20;
			}
			else{
				ancXPlace = 15;
				ancYPlace = 30;
				ancXTrans = 8;
				ancYTrans = 0;
			}
			if(input){
				anchorX1=ancXPlace;
				anchorX2=ancXTrans;
				anchorY1=ancYPlace;
				anchorY2=ancYTrans;
			}
			else{
				anchorX2=ancXPlace;
				anchorX1=ancXTrans;
				anchorY2=ancYPlace;
				anchorY1=ancYTrans;
			}
		}
	}
	
	public PlaceFigure getPlaceFigure(){
		return place;
	}
	public TransitionFigure getTransitionFigure(){
		return transition;
	}
	public Arc getArc(){
		return (Arc)getElement();
	}
	
	public int getX1(){
		return (input)?place.getX()+anchorX1:transition.getX()+anchorX1;
	}
	public int getX2(){
		return (input)?transition.getX()+anchorX2:place.getX()+anchorX2;
	}
	public int getY1(){
		return (input)?place.getY()+anchorY1:transition.getY()+anchorY1;
	}
	public int getY2(){
		return (input)?transition.getY()+anchorY2:place.getY()+anchorY2;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof ArcFigure){
			Arc a = ((ArcFigure) o).getArc();
			return a.equals(this.getArc());
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return 100000 + getArc().hashCode();
	}
	
	@Override
	protected void deleteLinks(Drawable caller){
		if(!(caller instanceof TransitionFigure)){
			transition.removeArcFigure(this);
		}
		if(!(caller instanceof PlaceFigure)){
			place.removeArcFigure(this);
		}
		if(!(caller instanceof SubnetFigure)){
			getParent().removeFigure(this);
		}
	}
	
	@Override
	public int getX(){
		return (getX1()>getX2())?getX2():getX1();
	}
	
	@Override
	public int getY(){
		return (getY1()>getY2())?getY2():getY1();
	}
	
	@Override
	public int getWidth(){
		return (getX1()>getX2())?getX1()-getX2():getX2()-getX1();
	}
	
	@Override
	public int getHeight(){
		return (getY1()>getY2())?getY1()-getY2():getY2()-getY1();
	}
}