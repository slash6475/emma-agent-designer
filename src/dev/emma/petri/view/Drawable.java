package emma.petri.view;

import emma.petri.model.PetriElement;

public interface Drawable {
	public int getX();
	public int getY();
	public int getWidth();
	public int getHeight();
	public SubnetFigure getParent();
	public PetriElement getElement();
	public boolean isSelected();
	public boolean moveBy(int x, int y);
	public boolean moveBy(int x, int y, boolean safely);
	public boolean select(boolean s);
	public void delete();
	public void delete(Drawable caller);
	public boolean getNotationsVisibility();
	public boolean switchNotationsVisibility();
	public boolean isVirtual();
	public boolean isVirtualizeable();
	public boolean isVirtualized();
	public boolean createVirtualization();
}
