package emma.view.awt;

import java.awt.Graphics2D;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import emma.petri.view.NetFigure;
import emma.petri.view.PlaceFigure;
import emma.petri.view.SubnetFigure;

public class AWTNetFigure extends NetFigure implements AWTDrawable {

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		Iterator<SubnetFigure> is = subnets.iterator();
		while(is.hasNext()){
			((AWTDrawable)is.next()).draw(g);
		}
		Iterator<PlaceFigure> ip = places.iterator();
		while(ip.hasNext()){
			((AWTDrawable)ip.next()).draw(g);
		}
	}

	@Override
	public AbstractTableModel getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SubnetFigure createSubnetFigure(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return new AWTSubnetFigure(x,y,width,height,this);
	}

}
