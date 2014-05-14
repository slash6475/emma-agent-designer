package emma.petri.view;

import emma.view.swing.petri.Figure;

public interface FigureHandler {

	public void transitionSelect();

	public void placeSelect();

	public void arrowSelect();

	public void subnetSelect();

	public void scopeSelect();
	
	public void setPropertiesView(PropertiesView p);
	
	public void putFocusOn(Figure f);
}
