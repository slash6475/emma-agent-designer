package emma.mapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
		List<Scope> list = new ArrayList<>();
		Iterator<Subnet> it = pNet.getSubnets().iterator();
		while(it.hasNext()){
			list.addAll(it.next().getScopes());
		}
		PBSolver pbo = new PBSolver(netwk.getNodes(),list);
		return pbo.solve();
	}
}
