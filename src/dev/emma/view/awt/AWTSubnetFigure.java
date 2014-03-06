package emma.view.awt;

import java.awt.Graphics2D;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import emma.petri.view.ArcFigure;
import emma.petri.view.PlaceFigure;
import emma.petri.view.SubnetFigure;
import emma.petri.view.TransitionFigure;

public class AWTSubnetFigure extends SubnetFigure implements AWTDrawable{
	
	public AWTSubnetFigure(int posX, int posY, int width, int height, SubnetFigure parent) {
		super(posX, posY, width, height,parent);
	}

	@Override
	public void draw(Graphics2D g) {
		g.clearRect(getX(), getY(), getWidth(),getHeight());
		//On print le subnet
		if(!isCollapsed()){
			AWTDrawTools.drawSubnet(g,this);
			Iterator<PlaceFigure> itP = places.iterator();
			Iterator<TransitionFigure> itT = transitions.iterator();
			Iterator<ArcFigure> itA = arcs.iterator();
			Iterator<SubnetFigure> itS = this.getSubnetFigures().iterator();
			while(itS.hasNext()){
				((AWTDrawable)itS.next()).draw(g);
			}
			while(itP.hasNext()){
				((AWTDrawable)itP.next()).draw(g);
			}
			while(itT.hasNext()){
				((AWTDrawable)itT.next()).draw(g);
			}
			while(itA.hasNext()){
				((AWTDrawable)itA.next()).draw(g);
			}
		}
		else{
			int x = getX()+((getWidth()-TransitionFigure.getDefaultWidth())/2);
			int y = getY()+((getHeight()-TransitionFigure.getDefaultHeight())/2);
			AWTDrawTools.drawTransition(g, x, y,TransitionFigure.getDefaultWidth(),TransitionFigure.getDefaultHeight(),true);
			if(isSelected()){
				AWTDrawTools.drawRectSelection(g,getX(),getY(),getWidth(),getHeight());
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
		// TODO Auto-generated method stub
		return new AWTSubnetFigure(x,y,width,height,this);
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
	protected ArcFigure createArcFigure(PlaceFigure p, TransitionFigure t, boolean input) {
		// TODO Auto-generated method stub
		return new AWTArcFigure(p,t,input);
	}
}