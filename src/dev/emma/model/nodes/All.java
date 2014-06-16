package emma.model.nodes;

public class All extends AbstractTarget{

	public All() {
		super("*", "ff02::1"); //Adresse multicast réservé : tous les hotes sur un segment (~= broadcast limité à notre système)
	}
	
	@Override
	public boolean setIP(String ip) {
		return false;
	}
	@Override
	public boolean setName(String name) {
		return false;
	}
	
}