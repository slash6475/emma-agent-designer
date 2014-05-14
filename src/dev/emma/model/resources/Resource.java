package emma.model.resources;

import emma.tools.Notifier;

public abstract class Resource {
	
	private String name;
	
	public Resource(String name){
		this.name=name;
	}
	protected Notifier notifier = new Notifier();
	public Notifier getNotifier(){
		return notifier;
	}
	
	public void setName(String name){	
		this.name=name;
	}
	
	public String getName(){
		return name;	
	}
	public abstract String get();
	
	public abstract void post(String s);
	
	public abstract void delete();
	
	public abstract boolean isDymamic();
}
