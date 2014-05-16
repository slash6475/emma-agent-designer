package emma.mapper;

/**
 * Exception thrown when No Mapping was found
 * @author pierrotws
 *
 */
public class MappingNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1766136498451061256L;

	public MappingNotFoundException(){
		super();
	}
	
	public MappingNotFoundException(String message){
		super(message);
	}
}
