package emma.petri.control.event;

public interface OutputArcListener extends PetriEventListener {
	public void notify(OutputArcEvent e);
}
