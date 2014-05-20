package emma.view.swing.petri;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import emma.petri.model.PetriElement;
import emma.view.swing.DesktopFrame;
import emma.view.swing.SwingController;

public abstract class SwingPetriFigure extends DesktopFrame implements Figure{
	/**
	 * 
	 */
	private static final long serialVersionUID = -818690048054725558L;

	protected SwingController control;
	protected int x,y,width,height;
	private Figure parent;
	private boolean isFocus;
	
	public SwingPetriFigure(String name, int x, int y, int width, int height,
			boolean resizable, boolean maximizable, boolean iconifiable,Figure parent){
		super(name, resizable, true, maximizable, iconifiable);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.parent=parent;
		Dimension d = new Dimension(width,height);
		this.setPreferredSize(d);
		this.setMinimumSize(d);
		this.setSize(d);
		this.control=parent.getController();
		this.resizable=resizable;
		this.isFocus=false;
		this.getContentPane().addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				SwingPetriFigure.this.mouseClicked(e);
			}
		});
	}
	
	@Override
	public void setContentPane(Container pane){
		for(MouseListener l : this.getContentPane().getMouseListeners()){
			pane.addMouseListener(l);
		}
		for(MouseMotionListener l : this.getContentPane().getMouseMotionListeners()){
			pane.addMouseMotionListener(l);
		}
		for(KeyListener l : this.getContentPane().getKeyListeners()){
			pane.addKeyListener(l);
		}
		super.setContentPane(pane);
	}
	
	public Figure getPetriParent(){
		return parent;
	}
	
	@Override
	public int getCanvasX(){
		return x;
	}

	@Override
	public int getCanvasY(){
		return y;
	}

	@Override
	public int getCanvasWidth(){
		return width;
	}

	@Override
	public int getCanvasHeight(){
		return height;
	}
	
	@Override
	public abstract PetriElement getElement();
	
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
	public boolean addPlace(int x, int y){
		return false;
	}
	
	@Override
	public boolean addTransition(int x, int y){
		return false;
	}
	
	@Override
	public boolean addScope(int x, int y){
		return false;
	}
	
	@Override
	public boolean addSubnet(int x, int y){
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
	public SwingController getController(){
		return control;
	}
	
	public void moveTo(int x, int y){
		((DesktopFrame)parent).getDesktopPane().getDesktopManager().setBoundsForFrame(this,x,y, this.getWidth(), this.getHeight());
	}
	
	public void moveBy(int x, int y){
		((DesktopFrame)parent).getDesktopPane().getDesktopManager().setBoundsForFrame(this,this.getX()+x,this.getY()+y, this.getWidth(), this.getHeight());
	}
	
	@Override
	public void leaveFocus(){
		this.isFocus=false;
		this.repaint();
	}
	
	@Override
	public void getFocus(){
		this.isFocus=true;
		this.repaint();
	}
	
	@Override
	public boolean isFocused() {
		return this.isFocus;
	}
	
	protected void refreshView(){
		if(isFocused()){
			control.putFocusOn(this);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e){
		moveToBack();
		if(e.getButton()==MouseEvent.BUTTON1){
			control.addFigure(SwingPetriFigure.this, e.getX(),e.getY());
		}
		else if(e.getButton()==MouseEvent.BUTTON3){
			Point p1 = SwingPetriFigure.this.getLocationOnScreen();
			Point p2 = SwingPetriFigure.this.getContentPane().getLocationOnScreen();
			control.showPopup(SwingPetriFigure.this,e.getX()+p2.x-p1.x,e.getY()+p2.y-p1.y,e.getX(),e.getY());
		}
		control.putFocusOn(SwingPetriFigure.this);
	}
}
