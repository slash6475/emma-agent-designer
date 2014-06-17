package emma.view.swing.petri.figure;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import emma.petri.control.event.DeletionEvent;
import emma.petri.control.event.NameChangedEvent;
import emma.petri.control.event.StateChangedEvent;
import emma.petri.control.listener.PlaceListener;
import emma.petri.model.PetriElement;
import emma.petri.model.Place;
import emma.view.swing.petri.table.PlaceTableModel;

public class PlaceFigure extends SwingPetriSimpleElement implements PlaceListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7276859619276800956L;
	
	private static final int defaultWidth = 30;
	private static final int defaultHeight = 30;
	private static final Color backgroundColor = new Color(0, 0, 0, 0);
	private static final Point centerPoint = new Point(21,33);
	
	private Place place;
	
	@SuppressWarnings("serial")
	public PlaceFigure(String name, int x, int y, ScopeFigure parent) {
		super(name, defaultWidth+11, defaultHeight+24, parent);
		this.setBackground(backgroundColor);
		place = new Place(parent.getScope());
		place.addListener(this);
		this.setName(name);
		this.setContentPane(new JComponent(){
			@Override
			protected void paintComponent(Graphics g){
				super.paintComponent(g);
				if(PlaceFigure.this.isFocused()){
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
		});
		this.setBorder(BorderFactory.createTitledBorder(
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
			this.setBorder(BorderFactory.createTitledBorder(
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
	public void notity(DeletionEvent e) {
		this.dispose();
	}

	@Override
	public void delete() {
		place.delete();
	}
}
