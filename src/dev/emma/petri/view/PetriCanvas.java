package emma.petri.view;

import java.io.File;


public interface PetriCanvas{
	
	public void render();
	public Drawable getDrawable(int x, int y);
	public boolean drawSubnet(int posX, int posY, int width, int length);
	public boolean drawPlace(int posX, int posY);
	public boolean drawTransition(int posX, int posY);
	public boolean drawArc(int originX, int originY, int endX, int endY);
	public void removeDraw();
	public boolean addSubnet(int x, int y, int width, int height);
	public boolean addPlace(int posX, int posY);
	public boolean addTransition(int posX, int posY);
	public boolean addArc(int originX, int originY, int endX, int endY);
	public void moveSelectionBy(int x, int y);
	public boolean select(int x, int y);
	public Drawable getSelection();
	public boolean removeDrawable(Drawable d);
	public boolean switchNotationsVisibilityOfSelection();
	public boolean virtualizeSelection();
	public boolean importFile(int x,int y, java.io.File f);
	public boolean saveFile(File f);
}
