package emma.model.resources;

/**
 * Class representing System resources
 * The only "static" resource
 * (determined by hardware and not by software)
 * @author pierrotws
 */
public class S extends AbstractResource {
	
	public S(String name){
		super(name);
	}
	
	public int getSize(){
		return 0;
	}
}
