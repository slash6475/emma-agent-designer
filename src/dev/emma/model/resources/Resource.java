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
	
	public void setName(String name);
	
	public String getName();
	
	public String get();
	
	public void post(String s);
	
	public void delete();

}
