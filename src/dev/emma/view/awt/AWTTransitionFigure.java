package emma.view.awt;

import java.awt.Graphics2D;

import javax.swing.table.AbstractTableModel;

import emma.petri.control.event.PetriEvent;
import emma.petri.view.PlaceFigure;
import emma.petri.view.ScopeFigure;
import emma.petri.view.TransitionFigure;

public class AWTTransitionFigure extends TransitionFigure implements AWTDrawable{
	
	public AWTTransitionFigure(int posX, int posY, ScopeFigure s, PlaceFigure p) {
		super(posX, posY,s,p);
	}
	
	@Override
	public void draw(Graphics2D g, double zoom) {
		//On print la transition
		AWTDrawTools.drawTransition(g,zoom,this);
		AWTDrawTools.drawVirtualLink(g,zoom,this, (AWTPlaceFigure)getPlaceFigure());
	}

	@Override
	public AbstractTableModel getProperties() {
		return new AWTTransitionTableModel(this);
	}

	@Override
	public void handle(PetriEvent e) {
		// TODO Auto-generated method stub
		
	}

}
