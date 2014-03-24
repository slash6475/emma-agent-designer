package emma.view.awt;

import java.awt.Graphics2D;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import emma.petri.view.NetFigure;
import emma.petri.view.SubnetFigure;

public class AWTNetFigure extends NetFigure implements AWTDrawable {

	public void draw(Graphics2D g, double zoom, int x, int y, int width, int height) {
		Iterator<SubnetFigure> is = getSubnetFigures().iterator();
		x=(int)(x/zoom);
		y=(int)(y/zoom);
		width=(int)(width/zoom);
		height=(int)(height/zoom);
		while(is.hasNext()){
			AWTDrawable d = (AWTDrawable)is.next();
			if(d.getX()+d.getWidth()>x && d.getX()<x+width){
				if(d.getY()+d.getHeight()>y && d.getY()<y+height){
					d.draw(g, zoom);
				}
			}
		}
		
	}

	@Override
	public AbstractTableModel getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SubnetFigure createSubnetFigure(int x, int y, int width, int height) {
		return new AWTSubnetFigure(x,y,width,height,this);
	}

	@Override
	public void draw(Graphics2D g, double zoom) {
		Iterator<SubnetFigure> is = getSubnetFigures().iterator();
		while(is.hasNext()){
			((AWTDrawable)is.next()).draw(g, zoom);
		}
	}

}
