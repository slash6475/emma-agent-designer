package emma.model.nodes;

public class ID extends AbstractTarget {
	
	public ID(String name) {
		super(name, "fe80::"+(int)(Math.random()*100000));
	}
	
	@Override
	public boolean setIP(String ip) {
		this.ip=ip;
		return true;
	}
	@Override
	public boolean setName(String name) {
		this.name=name;
		return true;
	}

}
