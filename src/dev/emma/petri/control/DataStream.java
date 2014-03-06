package emma.petri.control;

import emma.petri.control.event.PetriEvent;

/**
 * Fournit les actions à visualiser au Player. En fonction de la source (log, évaluée, sniffée)
 * @author  pierrotws
 */
public interface DataStream {

	public PetriEvent getNextEvent();
}
