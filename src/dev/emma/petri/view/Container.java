package emma.petri.view;

public interface Container extends Drawable {
	
	public boolean resize(int x, int y,int width, int height);

	public boolean allowObjectPosition(int x, int y, int width, int height);
	
	public boolean allowObjectPosition(int x, int y, int width, int height,Drawable toIgnore);
	
	public void resetSizeExtremum();
	
	public void resetSizeExtremum(Figure figure);
	
	public Container getParent();

	public void minimize();
	
	public int getMaxX();

	public int getMaxY();

	public int getMinX2();

	public int getMinY2();
	
	public boolean addTransitionFigure(int posX, int posY);
	
	public boolean addPlaceFigure(int posX, int posY);
	
	public boolean addArcFigure(int x1, int y1, int x2, int y2);
	
	public boolean addScopeFigure(int x, int y, int width, int height);
	
	public boolean addSubnetFigure(int x, int y, int width, int height);
	
	public Drawable getDrawable(int x, int y);

	public PlaceFigure getPlaceFigure(int x, int y);
	
	public TransitionFigure getTransitionFigure(int x, int y);

}
