
package net.sf.marineapi.provider.event;

/**
 * Listener interface for GPS time/position/velocity events.
 * 
 * @author Kimmo Tuukkanen
 * @see net.sf.marineapi.provider.PositionProvider
 * @see net.sf.marineapi.provider.event.PositionEvent
 */
public interface PositionListener extends ProviderListener<PositionEvent> {

	/**
	 * Invoked when fresh time/position/velocity report is available, typically
	 * once per second.
	 * 
	 * @param evt PositionEvent
	 */
	void providerUpdate(PositionEvent evt);
}
