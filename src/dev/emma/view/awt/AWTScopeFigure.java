package emma.view.awt;

import java.awt.Graphics2D;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import emma.petri.control.event.PetriEvent;
import emma.petri.view.PlaceFigure;
import emma.petri.view.ScopeFigure;
import emma.petri.view.TransitionFigure;

public class AWTScopeFigure extends ScopeFigure implements AWTDrawable{


	public AWTScopeFigure(int posX, int posY, int width, int height, AWTSubnetFigure parent) {
		super(posX, posY, width, height, parent);
	}

	@Override
	protected PlaceFigure createPlaceFigure(int x, int y) {
		return new AWTPlaceFigure(x,y,this);
	}
	
	@Override
	protected TransitionFigure createTransitionFigure(int x, int y, PlaceFigure p) {
		return new AWTTransitionFigure(x, y, this, p);
	}

	
	@Override
	public AbstractTableModel getProperties() {
		return new AWTScopeTableModel(this);
		
	}

	@Override
	public void handle(PetriEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D g, double zoom) {
		if(!isCollapsed()){
			AWTDrawTools.drawScope(g,zoom,this);
			Iterator<PlaceFigure> ip = places.iterator();
			Iterator<TransitionFigure> it = transitions.iterator();
			while(ip.hasNext()){
				((AWTDrawable)ip.next()).draw(g,zoom);
			}
			while(it.hasNext()){
				((AWTDrawable)it.next()).draw(g,zoom);
			}
		}
	}
}

