package emma.petri.model;

import emma.model.resources.Resource;

public class VirtualPlace extends Place {

	private Place p;
	
	public VirtualPlace(Place placeToVirtualize) {
		super(placeToVirtualize.getParent().getParent());
		this.p=placeToVirtualize;
		p.setVirtualization(this);
	}

	public Place getLinkedPlace(){
		return p;
	}
	
	@Override
	public Resource getData(){
		return p.getData();
	}
	
	@Override
	public void delete(){
		p.unvirtualize();
	}
	
	//On ne peut pas s'accorder plus de droit 
	//que la place qu'on virtualise
	@Override
	public boolean setInput(boolean b){
		if(b && !p.isInput()){
			return false;
		}
		return super.setInput(b);
	}
	
	//On ne peut pas s'accorder plus de droit 
	//que la place qu'on virtualise
	@Override
	public boolean setOutput(boolean b){
		if(b && !p.isOutput()){
			return false;
		}
		return super.setOutput(b);
	}
	
	@Override
	public boolean isVirtual(){
		return true;
	}
}
