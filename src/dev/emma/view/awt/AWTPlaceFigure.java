package emma.view.awt;

import java.awt.Graphics2D;

import javax.swing.table.AbstractTableModel;

import emma.petri.control.event.PetriEvent;
import emma.petri.view.PlaceFigure;
import emma.petri.view.ScopeFigure;

public class AWTPlaceFigure extends PlaceFigure implements AWTDrawable{
	
	public AWTPlaceFigure(int posX, int posY, ScopeFigure s) {
		super(posX, posY, s);
	}

	@Override
	public void draw(Graphics2D g,double zoom) {
		AWTDrawTools.drawPlace(g,zoom,this);
	}

	@Override
	public AbstractTableModel getProperties() {
		return new AWTPlaceTableModel(this);
	}

	@Override
	public void handle(PetriEvent e) {
		// TODO Auto-generated method stub
		
	}
}
