package net.sf.marineapi.provider.event;

/**
 * Listener interface for {@link net.sf.marineapi.provider.HeadingProvider}.
 * 
 * @author Kimmo Tuukkanen
 */
public interface HeadingListener extends ProviderListener<HeadingEvent> {

	/*
	 * (non-Javadoc)
	 * @see
	 * net.sf.marineapi.provider.event.ProviderListener#providerUpdate(net.sf
	 * .marineapi.provider.event.ProviderEvent)
	 */
	public void providerUpdate(HeadingEvent evt);
}
