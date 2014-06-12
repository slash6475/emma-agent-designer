package emma.mapper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import emma.mapper.mapobj.MapperNode;
import emma.mapper.mapobj.MapperScope;
import emma.model.nodes.Network;
import emma.petri.model.Net;
import emma.petri.model.Scope;
import emma.petri.model.Subnet;

/**
 * Simple implementation of Mapper interface
 * This class will unpack nodes from netwk and scopes from Net,
 * then call the PBSolver
 * @author pierrotws
 *
 */
public class SimpleMapper implements Mapper {

	@Override
	public Mapping getMapping(Network netwk, Net pNet) throws MappingNotFoundException {
		Set<MapperNode> mNodes = MapperNode.getMapperNodes(netwk.getNodes());
		Set<Scope> scopes = new HashSet<>();
		Iterator<Subnet> it = pNet.getSubnets().iterator();
		while(it.hasNext()){
			scopes.addAll(it.next().getScopes());
		}
		Set<MapperScope> mScopes = MapperScope.getMapperScopes(mNodes, scopes);
		PBSolver pbo = new PBSolver(mNodes,mScopes);
		
		return pbo.solve();
	}
}
