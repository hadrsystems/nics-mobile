
package net.sf.marineapi.provider.event;

/**
 * @author Kimmo Tuukkanen
 */
public interface SatelliteInfoListener extends ProviderListener<SatelliteInfoEvent> {

	/**
	 * Invoked when new satellite data is available.
	 */
	public void providerUpdate(SatelliteInfoEvent event);
}
