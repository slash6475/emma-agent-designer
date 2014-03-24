package emma.petri.view;

import emma.petri.model.PetriElement;

public interface Drawable {
	public int getX();
	public int getY();
	public int getWidth();
	public int getHeight();
	public boolean isSelected();
	public boolean select();
	public boolean unselect();
	public Container getParent();
	public PetriElement getElement();
	public boolean moveBy(int x, int y);
	public boolean moveBy(int x, int y, boolean safely);
	public void delete();
	public void delete(Drawable caller);
	public boolean getNotationsVisibility();
	public boolean switchNotationsVisibility();
}
