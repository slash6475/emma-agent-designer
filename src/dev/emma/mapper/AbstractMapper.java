package emma.mapper;

import java.util.Iterator;
import java.util.List;

import emma.model.nodes.Network;
import emma.petri.model.Net;

public abstract class AbstractMapper implements Mapper {

	@Override
	public final List<Mapping> getAllMappings(Network netwk, Net pNet)
			throws MappingNotFoundException {
		//TODO
		throw new MappingNotFoundException("Not implemented yet");
	}

	@Override
	public final Mapping getPrefferedMapping(Network netwk, Net pNet) throws MappingNotFoundException {
		Mapping mp = null;
		double score = Double.MIN_VALUE;
		Iterator<Mapping> it = getAllMappings(netwk,pNet).iterator();
		while(it.hasNext()){
			Mapping aMap = it.next();
			Double aScore = scoreMapping(aMap);
			if(score<aScore){
				score=aScore;
				mp=aMap;
			}
		}
		return mp;
	}

	public abstract double scoreMapping(Mapping m);
	
}
