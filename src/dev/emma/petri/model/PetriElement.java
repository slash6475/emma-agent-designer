package emma.petri.model;

public abstract class PetriElement {
	
	private static int q=1;
	private int id;
	
	public PetriElement(){
		this.id=q++;
	}
	
	public int getID(){
		return id;
	}
	
	@Override
	public int hashCode(){
		return id;
	}
	
	public void delete(){
		this.delete(this);
	}
	
	protected abstract void deleteLinks(PetriElement caller);
	
	protected final void delete(PetriElement caller){
		this.deleteLinks(caller);
		this.notifyDeletion();
	}
	
	protected abstract void notifyDeletion();
}
