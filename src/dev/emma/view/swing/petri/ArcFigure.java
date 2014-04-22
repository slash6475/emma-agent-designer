package emma.view.swing.petri;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

import emma.petri.model.Arc;
import emma.petri.model.InputArc;
import emma.petri.model.OutputArc;
import emma.petri.model.PetriElement;
import emma.view.swing.SwingController;
import emma.view.swing.petri.table.ArcTableModel;

public class ArcFigure implements Figure{

	private Arc arc;
	private Point placePt;
	private Point transPt;
	private ComponentListener placeListener;
	private ComponentListener transListener;
	private PlaceFigure p;
	private TransitionFigure t;
	private LinkedList<Point> arcPts;
	private Point selectedPoint;
	private Point selectedPosition;
	private SubnetFigure parent;
	private boolean input;
	private int boundX, boundY, boundX2, boundY2;
	private boolean isFocused;
	
	public ArcFigure(PlaceFigure p, TransitionFigure t, boolean isInputArc){
		this.input=isInputArc;
		this.arc=(input)?new InputArc(p.getPlace(),t.getTransition()):new OutputArc(p.getPlace(),t.getTransition());
		this.parent=(SubnetFigure)t.getPetriParent().getPetriParent();
		this.p=p;
		this.t=t;
		this.isFocused=false;
		this.arcPts = new LinkedList<Point>();
		this.placeListener = new ComponentAdapter(){
			@Override
			public void componentMoved(ComponentEvent e) {
				setPlacePosition();
				parent.repaint();
			}
		};
		this.transListener = new ComponentAdapter(){
			@Override
			public void componentMoved(ComponentEvent e) {
				setTransitionPosition();
				parent.repaint();
			}
		};
		this.p.addComponentListener(placeListener);
		((Component)this.p.getPetriParent()).addComponentListener(placeListener);
		this.t.addComponentListener(transListener);
		((Component)this.t.getPetriParent()).addComponentListener(transListener);
		Point parentpos = parent.getContentPane().getLocationOnScreen();
		Point placepos = p.getLocationOnScreen();
		Point transpos = t.getLocationOnScreen();
		placePt = new Point(placepos.x-parentpos.x+p.getCenterPoint().x,placepos.y-parentpos.y+p.getCenterPoint().y);
		transPt = new Point(transpos.x-parentpos.x+t.getCenterPoint().x,transpos.y-parentpos.y+t.getCenterPoint().y);
		if(placePt.x>transPt.x){
			this.boundX=transPt.x;
			this.boundX2=placePt.x;
		}
		else{
			this.boundX2=transPt.x;
			this.boundX=placePt.x;
		}
		if(placePt.y>transPt.y){
			this.boundY=transPt.y;
			this.boundY2=placePt.y;
		}
		else{
			this.boundY2=transPt.y;
			this.boundY=placePt.y;
		}
	}
	
	public Arc getArc(){
		return arc;
	}
	
	private void setPlacePosition(){
		Point parentpos = parent.getContentPane().getLocationOnScreen();
		Point placepos = p.getLocationOnScreen();
		placePt = new Point(placepos.x-parentpos.x+p.getCenterPoint().x,placepos.y-parentpos.y+p.getCenterPoint().y);
		if(placePt.x<boundX){
			boundX=placePt.x;
		}
		if(placePt.x>boundX2){
			boundX2=placePt.x;
		}
		if(placePt.y<boundY){
			boundY=placePt.y;
		}
		if(placePt.y>boundY2){
			boundY2=placePt.y;
		}
	}
	
	private void setTransitionPosition(){
		Point parentpos = parent.getContentPane().getLocationOnScreen();
		Point transpos = t.getLocationOnScreen();
		transPt = new Point(transpos.x-parentpos.x+t.getCenterPoint().x,transpos.y-parentpos.y+t.getCenterPoint().y);
		if(transPt.x<boundX){
			boundX=transPt.x;
		}
		if(transPt.x>boundX2){
			boundX2=transPt.x;
		}
		if(transPt.y<boundY){
			boundY=transPt.y;
		}
		if(transPt.y>boundY2){
			boundY2=transPt.y;
		}
	}
	
	public PetriElement getElement() {
		return getArc();
	}
	
	public void paintComponent(Graphics g){
		Point oldPt, newPt;
		oldPt=placePt;
		Iterator<Point> it=arcPts.iterator();
		g.setColor((isFocused)?Color.magenta:Color.black);
		while(it.hasNext()){
			newPt=it.next();
			g.drawLine(oldPt.x, oldPt.y, newPt.x, newPt.y);
			drawPoint(g,newPt);
			oldPt=newPt;
		}
		newPt=transPt;
		g.drawLine(oldPt.x, oldPt.y, newPt.x, newPt.y);
		if(input){
			drawPoint(g,placePt);
			g.fillRect(transPt.x-5, transPt.y-5, 10, 10);
		}
		else{
			drawPoint(g,transPt);
			g.fillRect(placePt.x-5, placePt.y-5, 10, 10);
		}
	}

	private void drawPoint(Graphics g, Point p) {
		g.fillOval(p.x-4,p.y-4, 8, 8);
	}
	
	@Override
	public int hashCode(){
		return arc.hashCode();
	}

	public boolean isInBounds(Point point) {
		if(point.x>=boundX && point.x<=boundX2){
			if(point.y>=boundY && point.y<=boundY2){
				Point oldPt, newPt;
				oldPt=placePt;
				Iterator<Point> it=arcPts.iterator();
				while(it.hasNext()){
					newPt=it.next();
					if(point.x>newPt.x-5 && point.x<newPt.x+5){
						if(point.y>newPt.y-5 && point.y<newPt.y+5){
							this.selectedPoint=newPt;
							return true;
						}
					}
					if(isAlign(point,oldPt,newPt)){
						this.selectedPoint=null;
						this.selectedPosition=point;
						return true;
					}
					oldPt=newPt;
				}
				newPt=transPt;
				if(isAlign(point,oldPt,newPt)){
					this.selectedPoint=null;
					this.selectedPosition=point;
					return true;
				}
			}
		}
		return false;
	}
	
	public void removeAPoint(){
		if(arcPts.size()>0){
			arcPts.removeLast();
		}
		this.recalculateBounds();
	}
	
	public void dragPoint(Point dragBy){
		if(selectedPoint!=null){
			selectedPoint.setLocation(dragBy);
			if(selectedPoint.x<boundX){
				boundX=selectedPoint.x;
			}
			else if(selectedPoint.x>boundX2){
				boundX2=selectedPoint.x;
			}
			if(selectedPoint.y<boundY){
				boundY=selectedPoint.y;
			}
			else if(selectedPoint.y>boundY2){
				boundY2=selectedPoint.y;
			}
		}
		else{
			int index=0;
			Iterator<Point> it = arcPts.iterator();
			while(it.hasNext()){
				Point p = it.next();
				if(p.x<selectedPosition.x){
					index++;
				}
				else{
					break;
				}
			}
			if(index==arcPts.size()){
				arcPts.add(selectedPosition);
			}
			else{
				arcPts.add(index,selectedPosition);
			}
			selectedPoint=selectedPosition;
		}
		parent.repaint();
	}
	
	//On calcule le determinant. S'il vaut 0, les points sont alignés
	//On met un seuil d'erreur de +/- 400
	private boolean isAlign(Point a, Point b, Point c){
		int det = (a.x-b.x)*(c.y-b.y) - (c.x-b.x)*(a.y-b.y);
		return (det>-400 && det<400)?true:false;
	}

	@Override
	public int getCanvasX() {
		return this.boundX;
	}

	@Override
	public int getCanvasY() {
		return this.boundY;
	}

	@Override
	public int getCanvasWidth() {
		return this.boundX2-this.boundX;
	}

	@Override
	public int getCanvasHeight() {
		return this.boundY2-this.boundY;
	}

	@Override
	public boolean isScopeContainer() {
		return false;
	}

	@Override
	public boolean isSubnetContainer() {
		return false;
	}

	@Override
	public boolean isPlaceContainer() {
		return false;
	}

	@Override
	public boolean isTransitionContainer() {
		return false;
	}

	@Override
	public boolean addPlace(int x, int y) {
		return false;
	}

	@Override
	public boolean addTransition(int x, int y) {
		return false;
	}

	@Override
	public boolean addSubnet(int x, int y) {
		return false;
	}

	@Override
	public boolean addScope(int x, int y){
		return false;
	}

	@Override
	public boolean addInputArc(PlaceFigure p, TransitionFigure t) {
		return false;
	}

	@Override
	public boolean addOutputArc(PlaceFigure p, TransitionFigure t) {
		return false;
	}

	@Override
	public AbstractTableModel getProperties() {
		return new ArcTableModel(this.arc);
	}

	@Override
	public SwingController getController() {
		return parent.getController();
	}

	@Override
	public void leaveFocus() {
		this.isFocused=false;
		this.selectedPoint=null;
		this.selectedPosition=null;
	}

	@Override
	public void getFocus() {
		this.isFocused=true;
	}

	@Override
	public boolean isFocused() {
		return this.isFocused;
	}

	@Override
	public Figure getPetriParent() {
		return parent;
	}
	
	private void recalculateBounds(){
		this.boundX=placePt.x;
		this.boundX2=placePt.x;
		this.boundY=placePt.y;
		this.boundY2=placePt.y;
		Iterator<Point> it = arcPts.iterator();
		while(it.hasNext()){
			Point p = it.next();
			if(boundX>p.x){
				boundX=p.x;
			}
			else if(boundX2<p.x){
				boundX2=p.x;
			}
			if(boundY>p.y){
				boundY=p.y;
			}
			else if(boundY2<p.y){
				boundY2=p.y;
			}
		}
		if(boundX>transPt.x){
			boundX=transPt.x;
		}
		else if(boundX2<transPt.x){
			boundX2=transPt.x;
		}
		if(boundY>transPt.y){
			boundY=transPt.y;
		}
		else if(boundY2<transPt.y){
			boundY2=transPt.y;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
