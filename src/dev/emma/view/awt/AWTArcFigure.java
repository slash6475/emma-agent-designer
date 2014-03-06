package emma.view.awt;

import java.awt.Graphics2D;

import javax.swing.table.AbstractTableModel;

import emma.petri.view.ArcFigure;
import emma.petri.view.PlaceFigure;
import emma.petri.view.TransitionFigure;

public class AWTArcFigure extends ArcFigure implements AWTDrawable{
	
	public AWTArcFigure(PlaceFigure p, TransitionFigure t, boolean input) {
		super(p,t,input);
	}

	@Override
	public void draw(Graphics2D g){
		resetAnchors();
		AWTDrawTools.drawArc(g,this);
	}
	
	@Override
	public boolean moveBy(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AbstractTableModel getProperties() {
		// TODO Auto-generated method stub
		return null;
	}
}
