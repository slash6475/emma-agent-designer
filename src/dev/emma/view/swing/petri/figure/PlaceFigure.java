package emma.view.swing.petri.figure;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import emma.petri.control.event.ActivationEvent;
import emma.petri.control.event.DeletionEvent;
import emma.petri.control.event.DesactivationEvent;
import emma.petri.control.event.NameChangedEvent;
import emma.petri.control.event.StateChangedEvent;
import emma.petri.control.listener.PlaceListener;
import emma.petri.model.PetriElement;
import emma.petri.model.Place;
import emma.view.swing.petri.FixedDesktopPane;
import emma.view.swing.petri.SimpleElementBorder;
import emma.view.swing.petri.SwingPetriContainer;
import emma.view.swing.petri.SwingPetriSimpleElement;
import emma.view.swing.petri.table.PlaceTableModel;

public class PlaceFigure extends SwingPetriSimpleElement implements PlaceListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7276859619276800956L;
	
	private static final int defaultWidth = 30;
	private static final int defaultHeight = 30;
	private static final Color backgroundColor = new Color(0, 0, 0, 0);
	private static final Point centerPoint = new Point(defaultWidth,defaultHeight+12);
	private boolean isActivated;
	private Place place;
	
	public PlaceFigure(String name, int x, int y, ScopeFigure parent) {
		super(name, defaultWidth+30, defaultHeight+42, parent);
		this.setBackground(backgroundColor);
		this.place = new Place(parent.getScope());
		this.place.addListener(this);
		this.isActivated=false;
		this.setName(name);
		this.setBorder(new SimpleElementBorder(
				null, place.getName(),
                TitledBorder.CENTER,
                TitledBorder.ABOVE_TOP));
		if(parent.getContentPane().add(this)!=null){
			this.moveTo(x, y);
		}
		
	}

	@Override
	public void setName(String name){
		place.setName(name);
	}
		
	@Override
	public PetriElement getElement() {
		return getPlace();
	}

	public Place getPlace() {
		return place;
	}

	@Override
	public AbstractTableModel getProperties() {
		return new PlaceTableModel(place);
	}

	@Override
	public void notify(NameChangedEvent e) {
		if(this.place==e.getSource()){
			this.setBorder(new SimpleElementBorder(
					null, place.getName(),
	                TitledBorder.CENTER,
	                TitledBorder.ABOVE_TOP));
		}
		this.refreshView();
	}

	@Override
	public void notify(StateChangedEvent e) {
		this.refreshView();
		this.repaint();
	}

	@Override
	public Point getCenterPoint() {
		return centerPoint;
	}

	@Override
	public void notify(DeletionEvent e) {
		this.dispose();
	}

	@Override
	public void delete() {
		place.delete();
	}

	@Override
	public void notify(ActivationEvent e) {
		if(e.getSource()==place){
			this.isActivated=true;
			this.repaint();
		}
	}

	@Override
	public void notify(DesactivationEvent e) {
		if(e.getSource()==place){
			this.isActivated=false;
			this.repaint();
		}
	}

	@Override
	public void paintSimpleElement(Graphics g) {
		if(PlaceFigure.this.isFocused() || isActivated){
			g.setColor(Color.magenta);
		}
		else{
			g.setColor(place.getDataColor());
		}
		g.fillOval(0,0,defaultWidth,defaultHeight);
		g.setColor(Color.black);
		if(place.hasToken()){
			g.fillOval((defaultWidth/2)-5,(defaultHeight/2)-5,10,10);
		}
		g.drawOval(0,0,defaultWidth,defaultHeight);
	}
	
	@Override
	public void moveTo(int x, int y){
		FixedDesktopPane fdp = (FixedDesktopPane)((SwingPetriContainer)this.getPetriParent()).getContentPane();
		fdp.setBoundsForFrame(this, x-10, y-10, this.getWidth(), this.getHeight());
	}
}
