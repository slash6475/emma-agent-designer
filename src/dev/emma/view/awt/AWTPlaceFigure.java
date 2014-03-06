package emma.view.awt;

import java.awt.Graphics2D;

import javax.swing.table.AbstractTableModel;

import emma.petri.view.PlaceFigure;
import emma.petri.view.SubnetFigure;
import emma.petri.view.VirtualPlaceFigure;

public class AWTPlaceFigure extends PlaceFigure implements AWTDrawable{
	public AWTPlaceFigure(int posX, int posY, SubnetFigure s) {
		super(posX, posY, s);
	}

	@Override
	public void draw(Graphics2D g) {
		//On print la place
		AWTDrawTools.drawPlace(g,this);
	}

	@Override
	public AbstractTableModel getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected VirtualPlaceFigure createVirtualPlaceFigure() {
		return new AWTVirtualPlaceFigure(this);
	}
}
