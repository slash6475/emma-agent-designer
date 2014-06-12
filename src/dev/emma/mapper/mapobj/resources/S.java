package emma.mapper.mapobj.resources;

import emma.mapper.Mapping;

public class S extends emma.petri.model.resources.S implements MappedResource {
	
	public S(emma.petri.model.resources.S unmapped, Mapping m){
		super(unmapped.getName());
	}
}
