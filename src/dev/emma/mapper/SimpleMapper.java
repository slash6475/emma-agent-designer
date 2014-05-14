package emma.mapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import emma.model.nodes.Network;
import emma.petri.model.Net;
import emma.petri.model.Scope;
import emma.petri.model.Subnet;

public class SimpleMapper extends AbstractMapper {
	
	@Override
	public double scoreMapping(Mapping m) {
		// TODO Auto-generated method stub
		return 0;
	}

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
