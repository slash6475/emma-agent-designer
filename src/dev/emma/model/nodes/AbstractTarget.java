package emma.model.nodes;

public abstract class AbstractTarget implements Target{
	String name;
	String ip;
	protected AbstractTarget(String name, String ip){
		this.setName(name);
		this.setIP(ip);
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	@Override
	public String getIP() {
		// TODO Auto-generated method stub
		return ip;
	}
}
