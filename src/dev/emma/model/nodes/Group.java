package emma.model.nodes;

public class Group extends AbstractTarget{

	public Group(String name) {
		super(name, "ff02::"+(int)((Math.random()*100000)+5)); //adresse aléatoire de multicast
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
