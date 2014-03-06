package emma.petri.control.event;

public interface TransitionListener extends PetriEventListener {

  void notify(final TransitionEvent anEvent);
  void notify(final TransitionStartedEvent anEvent);
  void notify(final TransitionStoppedEvent anEvent);

}