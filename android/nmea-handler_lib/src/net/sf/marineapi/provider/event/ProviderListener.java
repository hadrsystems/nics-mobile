
package net.sf.marineapi.provider.event;

import java.util.EventListener;

/**
 * Base interface for provider listeners.
 * 
 * @author Kimmo Tuukkanen
 */
public abstract interface ProviderListener<T extends ProviderEvent> extends
		EventListener {

	/**
	 * Invoked when provider has new data available.
	 * 
	 * @param evt ProviderEvent object
	 */
	void providerUpdate(T evt);

}
