package emma.view.swing.petri;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;

import emma.petri.control.event.DeletionEvent;
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

	@Override
	public void notity(DeletionEvent e) {
		this.dispose();
	}
	
	@Override
	public void delete(){
		transition.delete();
	}
}
