
package net.sf.marineapi.provider.event;

import java.util.EventObject;

/**
 * Abstract base class for provider events.
 * 
 * @author Kimmo Tuukkanen
 */
public abstract class ProviderEvent extends EventObject {

	private static final long serialVersionUID = -5207967682036248721L;

	/**
	 * Constructor
	 * 
	 * @param source
	 */
	public ProviderEvent(Object source) {
		super(source);
	}
}
