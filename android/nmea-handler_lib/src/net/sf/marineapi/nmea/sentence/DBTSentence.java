
package net.sf.marineapi.nmea.sentence;

/**
 * Water depth below transducer, in meters, feet and fathoms.
 * 
 * @author Kimmo Tuukkanen
 */
public interface DBTSentence extends DepthSentence {

	/**
	 * Get depth in fathoms.
	 * 
	 * @return Depth value
	 */
	double getFathoms();

	/**
	 * Get depth in feet.
	 * 
	 * @return Depth value
	 */
	double getFeet();

	/**
	 * Set depth value, in fathoms.
	 * 
	 * @param depth Depth to set
	 */
	void setFathoms(double depth);

	/**
	 * Set depth value, in feet.
	 * 
	 * @param depth Depth to set
	 */
	void setFeet(double depth);

}
