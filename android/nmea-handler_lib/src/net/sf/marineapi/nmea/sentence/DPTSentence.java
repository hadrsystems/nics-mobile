
package net.sf.marineapi.nmea.sentence;

/**
 * Depth of water, measured in meters. Includes offset to transducer, positive
 * values for distance from transducer to water line and negative values for
 * distance from transducer to keel. The maximum value is included since NMEA
 * v3.0 and may thus be missing.
 * 
 * @author Kimmo Tuukkanen
 */
public interface DPTSentence extends DepthSentence {

	/**
	 * Get offset to transducer.
	 * 
	 * @return Offset in meters.
	 */
	double getOffset();

	/**
	 * Set offset to transducer.
	 * 
	 * @param offset Offset in meters
	 */
	void setOffset(double offset);

	/**
	 * Get maximum depth value the sounder can detect.
	 * 
	 * @return Maximum depth, in meters.
	 */
	int getMaximum();

	/**
	 * Set maximum depth value the sounder can detect.
	 * 
	 * @param max Maximum depth, in meters.
	 */
	void setMaximum(int max);
}
