package emma.view.swing.petri.figure;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.table.AbstractTableModel;

import emma.petri.control.event.ActivationEvent;
import emma.petri.control.event.DeletionEvent;
import emma.petri.control.event.DesactivationEvent;
import emma.petri.control.event.NameChangedEvent;
import emma.petri.control.listener.ScopeListener;
import emma.petri.model.PetriElement;
import emma.petri.model.Scope;
import emma.view.swing.petri.SwingPetriContainer;
import emma.view.swing.petri.table.ScopeTableModel;

public class ScopeFigure extends SwingPetriContainer  implements ScopeListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 558198387759281261L;
	private static final Color backgroundColor = new Color(240,240,240);
	private int placeCounter;
	private SubnetFigure parent;
	private Scope scope;
	
	public ScopeFigure(String name, int x, int y, int width, int height, SubnetFigure parent) {
		super(name, width, height, false, false, parent);
		scope=new Scope(parent.getSubnet());
		scope.addListener(this);
		scope.setName(name);
		this.setBackground(backgroundColor);
		Container cp = this.getContentPane();
		cp.setBackground(backgroundColor);
		cp.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				//Forcing Scope to be at the lowest z index
				ScopeFigure.this.moveToBack();
			}
		});
		cp.addMouseListener(parent.getArcHandler());
		cp.addMouseMotionListener(parent.getArcHandler());
		this.parent=parent;
		if(parent.getContentPane().add(this)!=null){
			this.moveTo(x, y);
		}
		this.placeCounter=0;
	}
	
	@Override
	public void addPainting(Graphics g){
		String m = scope.getTarget();
		g.setColor(Color.red);
		g.drawChars(m.toCharArray(), 0, m.length(),5,15);
	}

	@Override
	public PetriElement getElement() {
		return getScope();
	}

	public Scope getScope(){
		return scope;
	}
	@Override
	public boolean isPlaceContainer() {
		return true;
	}

	@Override
	public boolean isTransitionContainer() {
		return true;
	}
	
	@Override
	public boolean addPlace(int x, int y){
		new PlaceFigure("P"+placeCounter++,x,y,this);
		return true;
	}
	
	@Override
	public boolean addTransition(int x, int y){
		//Il doit créé une place de stockage, puis la transition
		PlaceFigure p = new PlaceFigure("PT"+placeCounter++,x,y,this);
		new TransitionFigure("",x, y+5+p.getHeight(),p,this);
		return true;
	}
	
	@Override
	public AbstractTableModel getProperties() {
		return new ScopeTableModel(scope);
	
	}
	
	@Override
	public void notify(NameChangedEvent e) {
		if(scope==e.getSource())
			this.setTitle(scope.getName());
	}
	
	@Override
	public void mouseClicked(MouseEvent e){
		if(!this.parent.hasSelectedArc()){
			super.mouseClicked(e);
		}
	}

	@Override
	public void notify(DeletionEvent e) {
		super.dispose();
	}
	
	@Override
	public void dispose(){
		this.delete();
	}

	@Override
	public void delete() {
		scope.delete();
	}

	@Override
	public void notify(ActivationEvent e) {}

	@Override
	public void notify(DesactivationEvent e) {}
}
