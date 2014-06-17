package emma.model.resources;

import emma.tools.Notifier;

/**
 * Abstract class representing the Resource :
 * defines REST compliant abstract methods
 * @author pierrotws
 *
 */
public interface Resource {
	
	public Notifier getNotifier();
	
	public String getName();
	
	public void setName(String name);
	
	public int getSize();
	
	public String get();
	
	public void put(String s);
	
	public void delete();

	public boolean hasInputRight();
	
	public boolean hasOutputRight();
}
