package emma.petri.view;

import emma.petri.control.event.PetriEventListener;
import emma.petri.model.PetriElement;

public abstract class Figure implements Drawable, PetriEventListener{
	private int x,y;
	private int width;
	private int height;
	private PetriElement elmt;
	private boolean notationsVisibility;
	private Container parent;
	private boolean selected;
	
	public Figure(int posX, int posY, int width, int height, Container parent, PetriElement element){
		this.x = posX;
		this.y = posY;
		this.width=width;
		this.height=height;
		this.elmt=element;
		this.parent=parent;
		elmt.addListener(this);
		notationsVisibility=true;
		selected=false;
	}

	@Override
	public boolean isSelected(){
		return selected;
	}
	public boolean select(){
		if(!selected){
			selected=true;
			return true;
		}
		return false;
	}
	@Override
	public boolean unselect(){
		if(selected){
			selected=false;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean moveBy(int x, int y){
		return moveBy(x,y,true);
	}
	
	@Override
	public boolean moveBy(int x, int y, boolean safely){
		if(safely){
			if(!getParent().allowObjectPosition(getX()+x, getY()+y, getWidth(), getHeight(), this)){
				return false;
			}
			this.x+=x;
			this.y+=y;
			getParent().resetSizeExtremum(this);
			return true;
		}
		this.x+=x;
		this.y+=y;
		return true;
	}
	
	@Override
	public int getWidth(){
		return width;
	}
	
	@Override
	public int getHeight(){
		return height;
	}
	
	@Override
	public int getX(){
		return x;
	}
	
	@Override
	public int getY(){
		return y;
	}
	
	@Override
	public Container getParent(){
		return parent;
	}
	
	public int getID(){
		return elmt.getID();
	}
	
	protected void setX(int x){
		this.x=x;
	}
	protected void setY(int y){
		this.y=y;
	}
	protected void setWidth(int w){
		this.width=w;
	}
	protected void setHeight(int h){
		this.height=h;
	}
	
	@Override
	public PetriElement getElement(){
		return elmt;
	}
	
	@Override
	public void delete(){
		elmt.delete();
		this.delete(this);
	}
	
	@Override
	public final void delete(Drawable caller){
		this.deleteLinks(caller);
	}
	
	protected abstract void deleteLinks(Drawable caller);
	
	@Override
	public boolean getNotationsVisibility(){
		return notationsVisibility;
	}
	
	@Override
	public boolean switchNotationsVisibility(){
		notationsVisibility=!notationsVisibility;
		return true;
	}
}
