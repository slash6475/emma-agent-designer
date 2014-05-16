package emma.mapper;

import emma.model.nodes.Network;
import emma.petri.model.Net;

/**
 *
 * @author pierrotws
 *
 */
public interface Mapper {
	/**
	 * This function will create a mapping (pNet deployed on netwk)
	 * @param netwk the real sensor network 
	 * @param pNet the petri network to map
	 * @return A mapping.
	 */
	Mapping getMapping(Network netwk, Net pNet) throws MappingNotFoundException;
}
