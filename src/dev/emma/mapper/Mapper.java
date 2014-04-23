package emma.mapper;

import java.util.List;

import emma.model.nodes.Network;
import emma.petri.model.Net;

public interface Mapper {
	/**
	 * 
	 * @param netwk the real sensor network 
	 * @param pNet the petri network to map on
	 * @return A list of all possible mappings
	 */
	List<Mapping> getAllMappings(Network netwk,Net pNet) throws MappingNotFoundException;
	
	/**
	 * 
	 * @param netwk the real sensor network 
	 * @param pNet the petri network to map on
	 * @return The best mapping (depend of the implementation)
	 */
	Mapping getPrefferedMapping(Network netwk, Net pNet) throws MappingNotFoundException;
	/**
	 * 
	 * @param netwk the real sensor network 
	 * @param pNet the petri network to map on
	 * @return Only one mapping, should be the fastest way to map (depend of the implementation)
	 */
	Mapping getMapping(Network netwk, Net pNet) throws MappingNotFoundException;
}
