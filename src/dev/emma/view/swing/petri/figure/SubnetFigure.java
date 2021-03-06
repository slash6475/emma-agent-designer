package emma.view.swing.petri.figure;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import emma.petri.control.event.ActivationEvent;
import emma.petri.control.event.DeletionEvent;
import emma.petri.control.event.DesactivationEvent;
import emma.petri.control.event.NameChangedEvent;
import emma.petri.control.listener.SubnetListener;
import emma.petri.model.ArcException;
import emma.petri.model.PetriElement;
import emma.petri.model.Subnet;
import emma.view.swing.petri.SwingPetriContainer;
import emma.view.swing.petri.table.SubnetTableModel;

public class SubnetFigure extends SwingPetriContainer implements SubnetListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7121549120432610365L;
	
	private Subnet sub;
	private Set<ArcFigure> arcs;
	private ArcHandler arcHandler;
	private ArcFigure selectedArc;
	
	public SubnetFigure(String name,int x, int y, int width, int height, NetFigure parent){
		super(name, width, height, true, true, parent,Color.white);
		MouseListener[] tab1 = this.getContentPane().getMouseListeners();
		for(MouseListener l : tab1){
			this.getContentPane().removeMouseListener(l);
		}
		this.sub=new Subnet(parent.getNet(),name);
		this.sub.addListener(this);
		this.arcs = new HashSet<ArcFigure>();
		arcHandler=new ArcHandler(tab1);
		this.getContentPane().addMouseListener(arcHandler);
		this.getContentPane().addMouseMotionListener(arcHandler);
		this.getContentPane().addKeyListener(arcHandler);
		if(parent.add(this)!=null){
			this.moveTo(x, y);
		}
	}
	
	@Override
	public void addPainting(Graphics g){
		Iterator<ArcFigure> it = arcs.iterator();
		while(it.hasNext()){
			it.next().paintComponent(g);
		}
	}
	
	private boolean selectArc(Point point) {
		selectedArc=null;
		Iterator<ArcFigure> it = arcs.iterator();
		while(it.hasNext()){
			ArcFigure a = it.next();
			if(a.isInBounds(point)){
				selectedArc=a;
				control.putFocusOn(a);
				return true;
			}
		}
		return false;
	}
	
	public class ArcHandler implements MouseListener, MouseMotionListener, KeyListener{
		private MouseListener[] mouses;
		public ArcHandler(MouseListener[] mouses){
			this.mouses=mouses;
		}
		@Override
		public void mousePressed(MouseEvent e) {
			Point p1 = SubnetFigure.this.getContentPane().getLocationOnScreen();
			Point p2 = e.getLocationOnScreen();
			if(!selectArc(new Point(p2.x-p1.x,p2.y-p1.y))){
				for(MouseListener l : mouses){
					l.mouseClicked(e);
				}
				SubnetFigure.this.repaint();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if(selectedArc!=null){
				selectedArc.dragPoint(e.getPoint());
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseMoved(MouseEvent e) {}
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount()>1){
				this.removePoint();
			}
		}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void keyTyped(KeyEvent e) {}
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_DELETE){
				this.removePoint();
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {}
		
		private void removePoint(){
			if(selectedArc!=null){
				if(!selectedArc.removeAPoint()){
					selectedArc.delete();
				}
				SubnetFigure.this.repaint();
			}
		}
	}
	
	@Override
	public boolean isScopeContainer() {
		return true;
	}
	
	@Override
	public boolean addScope(int x, int y){
		new ScopeFigure("aScope",x,y,150,150,this);
		return true;
	}

	public boolean addInputArc(PlaceFigure p, TransitionFigure t){
		try {
			ArcFigure a = new ArcFigure(p,t,true);
			if(arcs.add(a)){
				this.repaint();
				return true;
			}
		} catch (ArcException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	public boolean addOutputArc(PlaceFigure p, TransitionFigure t){
		try {
			ArcFigure a = new ArcFigure(p,t,false);
			if(arcs.add(a)){
				this.repaint();
				return true;
			}
		} catch(ArcException e){
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	public boolean removeArc(ArcFigure a){
		return arcs.remove(a);
	}
	public Subnet getSubnet() {
		return sub;
	}

	@Override
	public PetriElement getElement() {
		return getSubnet();
	}

	@Override
	public AbstractTableModel getProperties() {
		return new SubnetTableModel(sub);
	}

	@Override
	public void notify(NameChangedEvent e) {
		if(sub==e.getSource()){
			this.setTitle(sub.getName());
		}
	}

	public ArcFigure getArcFigure(PlaceFigure p, TransitionFigure t, boolean input){
		Iterator<ArcFigure> it = arcs.iterator();
		while(it.hasNext()){
			ArcFigure a = it.next();
			if(a.getArc().getPlace()==p.getPlace() && a.getArc().getTransition()==t.getTransition() && input==a.getArc().isInput()){
				return a;
			}
		}
		return null;
	}
	
	public ArcHandler getArcHandler(){
		return arcHandler;
	}
	
	public boolean hasSelectedArc(){
		return (selectedArc!=null);
	}

	@Override
	public void notify(DeletionEvent e) {
		this.dispose();
	}
	
	public void delete(){
		sub.delete();
	}

	@Override
	public void notify(ActivationEvent e){}

	@Override
	public void notify(DesactivationEvent e){}

	public void scopeMoved(ScopeFigure scopeFigure, int oldX, int oldY) {
		Iterator<ArcFigure> it = arcs.iterator();
		int x1 = scopeFigure.getX();
		int x2 = x1+scopeFigure.getWidth();
		int y1 = scopeFigure.getY();
		int y2 = y1+scopeFigure.getHeight();
		//If a point is in the scope that has been moved,
		//then we should move this point from the same decay
		while(it.hasNext()){
			ArcFigure a = it.next();
			Iterator<Point> itP = a.getPoints().iterator();
			while(itP.hasNext()){
				Point p = itP.next();
				if(p.x>=x1 && p.x<=x2 && p.y>=y1 && p.y<=y2){
					p.move(p.x+(x1-oldX), p.y+(y1-oldY));
				}
			}
		}
		this.repaint();
	}
}
