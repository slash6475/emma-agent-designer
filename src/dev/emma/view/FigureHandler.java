package emma.view;

import emma.petri.view.PetriCanvas;
import emma.view.test.BorderPanel;

public interface FigureHandler {

	public void transitionSelect();

	public void placeSelect();

	public void arrowSelect();

	public void subnetSelect();

	public void scopeSelect();
	
	public void cursorSelect();
	
	public void playStopSelect();
	
	public void playSelect();
	
	public void stopSelect();
	
	public void setCanvas(PetriCanvas p);
	
	public PetriCanvas getCanvas();

	public void setPropertiesPanel(BorderPanel borderPanel);
}
