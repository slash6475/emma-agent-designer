package emma.view.swing.petri;

import java.awt.event.MouseEvent;

import javax.swing.table.AbstractTableModel;

import emma.petri.model.PetriElement;
import emma.view.swing.SwingController;

public interface Figure {
	public PetriElement getElement();
	public boolean isScopeContainer();
	public boolean isSubnetContainer();
	public boolean isPlaceContainer();
	public boolean isTransitionContainer();
	public boolean addPlace(int x, int y);
	public boolean addTransition(int x, int y);
	public boolean addSubnet(int x, int y);
	public boolean addScope(int x, int y);
	public boolean addInputArc(PlaceFigure p, TransitionFigure t);
	public boolean addOutputArc(PlaceFigure p, TransitionFigure t);
	public AbstractTableModel getProperties();
	public SwingController getController();
	public void leaveFocus();
	public void getFocus();
	public boolean isFocused();
	public Figure getPetriParent();
	public void mouseClicked(MouseEvent e);
	public void delete();
}
