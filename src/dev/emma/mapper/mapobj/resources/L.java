package emma.mapper.mapobj.resources;

import emma.mapper.Mapping;

public class L extends emma.petri.model.resources.L implements MappedResource {

	public String address;
	
	public L(emma.petri.model.resources.L unmapped, Mapping m){
		super(unmapped.getName());
	}
}
