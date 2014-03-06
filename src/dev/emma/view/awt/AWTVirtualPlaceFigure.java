package emma.view.awt;

import java.awt.Graphics2D;

import javax.swing.table.AbstractTableModel;

import emma.petri.view.VirtualPlaceFigure;

public class AWTVirtualPlaceFigure extends VirtualPlaceFigure implements AWTDrawable {
	
	public AWTVirtualPlaceFigure(AWTVirtualPlaceFigure p){
		super(p);
	}
	
	public AWTVirtualPlaceFigure(AWTPlaceFigure p) {
		super(p);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		AWTDrawTools.drawPlace(g,this);
		AWTDrawTools.drawVirtualLink(g,this,(AWTDrawable)this.getLinkedPlaceFigure());
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
