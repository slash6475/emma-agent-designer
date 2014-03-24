package emma.view.awt;

import java.awt.Graphics2D;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import emma.petri.control.event.PetriEvent;
import emma.petri.view.ArcFigure;
import emma.petri.view.NetFigure;
import emma.petri.view.PlaceFigure;
import emma.petri.view.ScopeFigure;
import emma.petri.view.SubnetFigure;
import emma.petri.view.TransitionFigure;

public class AWTSubnetFigure extends SubnetFigure implements AWTDrawable{
	
	public AWTSubnetFigure(int posX, int posY, int width, int height, NetFigure parent) {
		super(posX, posY, width, height,parent);
	}

	@Override
	public void draw(Graphics2D g, double zoom) {
		//On print le subnet
		if(!isCollapsed()){
			AWTDrawTools.drawSubnet(g,zoom,this);
			Iterator<ScopeFigure> itS = scopes.iterator();
			Iterator<ArcFigure> itA = arcs.iterator();
			while(itS.hasNext()){
				((AWTDrawable)itS.next()).draw(g,zoom);
			}
			while(itA.hasNext()){
				((AWTDrawable)itA.next()).draw(g,zoom);
			}
		}/*
		else{
			int x = getX()+((getWidth()-TransitionFigure.getDefaultWidth())/2);
			int y = getY()+((getHeight()-TransitionFigure.getDefaultHeight())/2);
			AWTDrawTools.drawTransition(g,zoom, x, y,TransitionFigure.getDefaultWidth(),TransitionFigure.getDefaultHeight(),true);
			if(isSelected()){
				AWTDrawTools.drawRectSelection(g,zoom,getX(),getY(),getWidth(),getHeight());
			}
		}*/
	}
	
	@Override
	public AbstractTableModel getProperties() {
		// TODO Auto-generated method stub
		return new AWTSubnetTableModel(this);
	}

	@Override
	protected ArcFigure createArcFigure(PlaceFigure p, TransitionFigure t, boolean input) {
		// TODO Auto-generated method stub
		return new AWTArcFigure(p,t,input);
	}

	@Override
	public void handle(PetriEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected ScopeFigure createScopeFigure(int x, int y, int width, int height){
		return new AWTScopeFigure(x, y, width, height, this);
	}
}