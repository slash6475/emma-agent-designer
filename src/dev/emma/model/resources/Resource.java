package emma.model.resources;

import emma.tools.Notifier;

public class Resource {
	
	protected Notifier notifier = new Notifier();
	public Notifier getNotifier(){
		return notifier;
	}
	
	public String getName(){
		return "";	
	}
	public String get(){
		return "";
	}
	public void post(String s){
		;
	}
	public void delete(){
		;
	}
}
