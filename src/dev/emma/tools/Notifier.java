package emma.tools;

import javax.swing.event.EventListenerList;



public class Notifier {
	private final EventListenerList listeners = new EventListenerList();
	
	public void addListener(Listener listener){
		this.listeners.add(Listener.class, listener);
	}
	
	public void addListener(Listener[] listener){
		for(int i=0; i < listener.length; i++)
			this.listeners.add(Listener.class, listener[i]);
	}
	
	public void removeListener(Listener listener){
		this.removeListener(listener);
	}
	
	public Listener[] getListeners(){
		return listeners.getListeners(Listener.class);
	}
	
	public void fireListener(Object e){
		 for(Listener listener : getListeners()){
			 listener.changed(e);
		 }
	}
}
