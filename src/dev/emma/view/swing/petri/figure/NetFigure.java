package emma.view.swing.petri.figure;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.AbstractTableModel;

import emma.petri.control.event.ActivationEvent;
import emma.petri.control.event.DeletionEvent;
import emma.petri.control.event.DesactivationEvent;
import emma.petri.control.listener.NetListener;
import emma.petri.model.Net;
import emma.petri.model.PetriElement;
import emma.view.swing.petri.ScrollableDesktopPane;
import emma.view.swing.petri.SwingController;
import emma.view.swing.petri.table.NetTableModel;

public class NetFigure extends ScrollableDesktopPane implements Figure, NetListener{

	private static final long serialVersionUID = -211453138961388457L;

	private Net net;
	private final SwingController control;

	private boolean isFocus;
	
	public NetFigure(final SwingController control){
		super(Color.white);
		this.control=control;
		this.isFocus=false;
		this.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				NetFigure.this.mouseClicked(e);
			}
		});
		this.net = new Net();
		net.addListener(this);
	}
	
	public Net getNet(){
		return net;
	}
	
	@Override
	public PetriElement getElement() {
		return getNet();
	}
	
	@Override
	public boolean isScopeContainer() {
		return false;
	}

	@Override
	public boolean isSubnetContainer() {
		return true;
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
	public boolean addInputArc(PlaceFigure p, TransitionFigure t){
		return false;
	}
	
	@Override
	public boolean addOutputArc(PlaceFigure p, TransitionFigure t){
		return false;
	}

	@Override
	public boolean addSubnet(int x, int y) {
		new SubnetFigure("aSubnet",x,y,350,300,this);
		return true;
	}

	@Override
	public boolean addScope(int x, int y) {
		return false;
	}

	@Override
	public SwingController getController() {
		return control;
	}

	@Override
	public AbstractTableModel getProperties() {
		return new NetTableModel(net);
	}

	@Override
	public void leaveFocus() {
		this.isFocus=false;
	}

	@Override
	public void getFocus() {
		this.isFocus=true;
	}

	@Override
	public boolean isFocused() {
		return this.isFocus;
	}

	@Override
	public Figure getPetriParent() {
		return null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON1){
			control.addFigure(NetFigure.this, e.getX(),e.getY());
		}
		else if(e.getButton()==MouseEvent.BUTTON3){
			control.showPopup(NetFigure.this,e.getX(),e.getY());
		}
		control.putFocusOn(NetFigure.this);
	}

	@Override
	public void notify(DeletionEvent e) {
		this.net = new Net();
	}

	@Override
	public void delete() {
		net.delete();
	}

	@Override
	public void notify(ActivationEvent e){}

	@Override
	public void notify(DesactivationEvent e){}
}
