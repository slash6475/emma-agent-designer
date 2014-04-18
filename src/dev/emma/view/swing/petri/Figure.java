package emma.view.swing.petri;

import java.awt.event.MouseEvent;

import javax.swing.table.AbstractTableModel;

import emma.petri.model.PetriElement;
import emma.view.swing.SwingController;

public interface Figure {
	int getCanvasX();
	int getCanvasY();
	int getCanvasWidth();
	int getCanvasHeight();
	PetriElement getElement();
	
	boolean isScopeContainer();
	boolean isSubnetContainer();
	boolean isPlaceContainer();
	boolean isTransitionContainer();
	boolean addPlace(int x, int y);
	boolean addTransition(int x, int y);
	boolean addSubnet(int x, int y);
	boolean addScope(int x, int y);
	boolean addInputArc(PlaceFigure p, TransitionFigure t);
	boolean addOutputArc(PlaceFigure p, TransitionFigure t);
	AbstractTableModel getProperties();
	SwingController getController();
	void leaveFocus();
	void getFocus();
	boolean isFocused();
	Figure getPetriParent();
	public void mouseClicked(MouseEvent e);
}
