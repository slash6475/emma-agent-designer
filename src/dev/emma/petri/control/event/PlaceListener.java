package emma.petri.control.event;

public interface PlaceListener extends PetriEventListener {
    

  /**
   * Notifies the listener that the marking of the place has changed. 
   * It may be caused by either adding or removing a token from 
   * a Place.
   *@param anEvent a <code>PlaceEvent</code> value
   */
  void notify(final PlaceEvent anEvent);

  /**
   * Notifies the listener that the marking of the place has changed. 
   * It is fired when tokens are removed from the place.
   * @param anEvent a <code>TokensRemovedEvent</code> value
   */
  void notify(final TokensRemovedEvent anEvent);

  /**
   * Notifies the listener that the marking of the place has changed. 
   * It is fired when tokens are added to the place.
   * @param anEvent a <code>TokensAddedEvent</code> value
   */
  void notify(final TokensAddedEvent anEvent);

}