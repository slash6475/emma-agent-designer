package emma.view.swing.petri;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import emma.petri.control.event.DeletionEvent;
import emma.petri.control.event.NameChangedEvent;
import emma.petri.control.listener.SubnetListener;
import emma.petri.model.PetriElement;
import emma.petri.model.Subnet;
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
		super(name, x, y, width, height, true, true, parent);
		this.sub=new Subnet(parent.getNet(),name);
		this.sub.addListener(this);
		this.arcs = new HashSet<ArcFigure>();
		arcHandler=new ArcHandler();
		this.getContentPane().addMouseListener(arcHandler);
		this.getContentPane().addMouseMotionListener(arcHandler);
	}
	
	@Override
	public void addPainting(Graphics g){
		Iterator<ArcFigure> it = arcs.iterator();
		while(it.hasNext()){
			it.next().paintComponent(g);
		}
	}
	
	private void selectArc(Point point) {
		selectedArc=null;
		Iterator<ArcFigure> it = arcs.iterator();
		while(it.hasNext()){
			ArcFigure a = it.next();
			if(a.isInBounds(point)){
				selectedArc=a;
				control.putFocusOn(a);
				return;
			}
		}
	}
	
	public class ArcHandler implements MouseListener, MouseMotionListener{
		@Override
		public void mousePressed(MouseEvent e) {
			Point p1 = SubnetFigure.this.getContentPane().getLocationOnScreen();
			Point p2 = e.getLocationOnScreen();
			selectArc(new Point(p2.x-p1.x,p2.y-p1.y));
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if(selectedArc!=null){
				selectedArc.dragPoint(e.getPoint());
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {}
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount()>1 && selectedArc!=null){
				selectedArc.removeAPoint();
			}
		}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}
	
	@Override
	public boolean isScopeContainer() {
		return true;
	}
	
	@Override
	public boolean addScope(int x, int y){
		ScopeFigure s = new ScopeFigure("aScope",x,y,150,150,this);
		if(this.getContentPane().add(s)!=null){
			s.moveTo(x, y);
			return true;
		}
		return false;
	}

	public boolean addInputArc(PlaceFigure p, TransitionFigure t){
		ArcFigure a = new ArcFigure(p,t,true);
		if(arcs.add(a)){
			this.repaint();
			return true;
		}
		return false;
	}
	
	public boolean addOutputArc(PlaceFigure p, TransitionFigure t){
		ArcFigure a = new ArcFigure(p,t,false);
		if(arcs.add(a)){
			this.repaint();
			return true;
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

	public ArcHandler getArcHandler(){
		return arcHandler;
	}
	
	public boolean hasSelectedArc(){
		return (selectedArc!=null);
	}

	@Override
	public void notity(DeletionEvent e) {
		this.dispose();
	}
	
	public void delete(){
		sub.delete();
	}
}
