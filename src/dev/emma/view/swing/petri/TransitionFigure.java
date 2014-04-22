package emma.view.swing.petri;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;

import emma.petri.control.listener.TransitionListener;
import emma.petri.model.PetriElement;
import emma.petri.model.Transition;
import emma.view.swing.petri.table.TransitionTableModel;


public class TransitionFigure extends SwingPetriSimpleElement implements TransitionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5250062670079685248L;
	//private static final BasicStroke pointilleStroke = new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,3.0f,new float[] {2.0f,4.0f},2.0f);
	private Transition transition;
	private PlaceFigure place;
	private static final Color backgroundColor = Color.blue;
	private static final int defaultWidth = 15;
	private static final int defaultHeight = 40;
	private static final Point centerPoint = new Point(7,20);
	
	public TransitionFigure(String name, int x, int y, PlaceFigure p,ScopeFigure parent) {
		super(name, x, y, defaultWidth, defaultHeight, parent);	
		this.setBackground(backgroundColor);
		this.setBorder(new LineBorder(Color.black,2));
		this.place=p;
		this.transition= new Transition(parent.getScope(), place.getPlace());
		transition.addListener(this);
	}

	public Transition getTransition() {
		return transition;
	}
	
	@Override
	public void paintComponent(Graphics g){
		if(this.isFocused()){
			this.setBackground(Color.magenta);
		}
		else{
			this.setBackground(backgroundColor);
		}
		super.paintComponent(g);
		/*int angle = (int)(Math.toDegrees(Math.atan2(this.getX()-place.getX(),this.getY()-place.getY())-(Math.PI/2d))+360)%360;
		int ancX1, ancY1, ancX2, ancY2;
		if(angle>=315 || angle<45){
			ancX2 = place.getWidth();
			ancY2 = place.getHeight()/2;
			ancX1 = 0;
			ancY1 = this.getHeight()/2;
		}
		else if(angle>= 45 && angle < 135){
			ancX2 = place.getWidth()/2;
			ancY2 = 0;
			ancX1 = this.getWidth()/2;
			ancY1 = this.getHeight();
		}
		else if (angle>=135 && angle<225){
			ancX2 = 0;
			ancY2 = place.getHeight()/2;
			ancX1 = this.getWidth();
			ancY1 = this.getHeight()/2;
		}
		else{
			ancX2 = place.getWidth()/2;
			ancY2 = place.getHeight();
			ancX1 = this.getWidth()/2;
			ancY1 = 0;
		}*/
	}
	
	@Override
	public PetriElement getElement() {
		return this.getTransition();
	}

	@Override
	public AbstractTableModel getProperties() {
		return new TransitionTableModel(transition);
	}

	public boolean createInputArc(PlaceFigure p) {
		return this.getPetriParent().getPetriParent().addInputArc(p, this);
	}
	
	public boolean createOutputArc(PlaceFigure p){
		return this.getPetriParent().getPetriParent().addOutputArc(p, this);
	}
	
	@Override
	public Point getCenterPoint() {
		return centerPoint;
	}
}
