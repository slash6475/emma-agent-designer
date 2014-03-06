package emma.view.awt;

import java.awt.Graphics2D;

import javax.swing.table.AbstractTableModel;

import emma.petri.view.PlaceFigure;
import emma.petri.view.SubnetFigure;
import emma.petri.view.TransitionFigure;

public class AWTTransitionFigure extends TransitionFigure implements AWTDrawable{
	
	public AWTTransitionFigure(int posX, int posY, SubnetFigure s, PlaceFigure p) {
		super(posX, posY,s,p);
	}
	
	@Override
	public void draw(Graphics2D g) {
		//On print la transition
		AWTDrawTools.drawTransition(g,this);
		AWTDrawTools.drawVirtualLink(g, this, (AWTPlaceFigure)getPlaceFigure());
	}

	@Override
	public AbstractTableModel getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

}
