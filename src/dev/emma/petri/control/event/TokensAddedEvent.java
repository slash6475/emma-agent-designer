package emma.petri.control.event;

import java.util.Set;

import emma.petri.model.Place;
import emma.petri.model.Token;

@SuppressWarnings("rawtypes")
public class TokensAddedEvent extends PlaceEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3915441281042655013L;
	private Set<Token> set;
	
	public TokensAddedEvent(final Place place, final Set<Token> tokens) {
		super(place);
	    this.set = tokens;
	}

	public Set<Token> getTokens() {
		return this.set;
	}  
}