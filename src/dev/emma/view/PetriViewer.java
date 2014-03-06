package emma.view;

import emma.petri.view.PetriCanvas;

public interface PetriViewer {
	
	public void setCanvas(PetriCanvas p);
	public void setVisible(boolean visible);
	public PetriCanvas getCanvas();
}
