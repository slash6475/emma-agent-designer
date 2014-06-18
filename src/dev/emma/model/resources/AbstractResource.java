package emma.model.resources;

import emma.tools.Notifier;

public abstract class AbstractResource implements Resource{
	private String name;
	protected Notifier notifier = new Notifier();
	
	public AbstractResource(String name){
		this.name=name;
	}
	public Notifier getNotifier(){
		return notifier;
	}
	
	public void setName(String name){	
		this.name=name;
	}
	
	public String getName(){
		return name;	
	}

	@Override
	public String toString(){
		return this.get();
	}
	
	@Override
	public boolean hasInputRight(){
		return true;
	}
	
	@Override
	public boolean hasOutputRight(){
		return true;
	}
	
	@Override
	public String get() {
		return "";
	}

	@Override
	public void put(String s) {
		notifier.fireListener(this);
	}

	@Override
	public void delete() {
		notifier.fireListener(this);
	}
	
}
