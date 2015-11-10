
package net.sf.marineapi.nmea.sentence;

/**
 * True and magnetic bearing from origin to destination in degrees. This
 * sentence is transmitted by a GPS in the GOTO mode (with or without active
 * route).
 * <p>
 * Example:<br>
 * <code>$GPBOD,234.9,T,228.8,M,RUSKI,*1D</code>
 * 
 * @author Kimmo Tuukkanen
 */
public interface BODSentence extends Sentence {

	/**
	 * Get the ID of destination waypoint. This field should be always available
	 * in GOTO mode.
	 * 
	 * @return waypoint id
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If the data is
	 *             not available.
	 * @throws net.sf.marineapi.parser.ParseException If the field contains
	 *             unexpected or illegal value.
	 */
	String getDestinationWaypointId();

	/**
	 * Get the magnetic bearing from origin to destination.
	 * <p>
	 * <i>Notice: The bearing is calculated from the origin when GOTO is
	 * activated and it is <b>not</b> updated dynamically.</i>
	 * 
	 * @return magnetic bearing value
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If the data is
	 *             not available.
	 * @throws net.sf.marineapi.parser.ParseException If the field contains
	 *             unexpected or illegal value.
	 */
	double getMagneticBearing();

	/**
	 * Get the ID of origin waypoint. This field is available only when route is
	 * active.
	 * 
	 * @return waypoint id
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If the data is
	 *             not available.
	 * @throws net.sf.marineapi.parser.ParseException If the field contains
	 *             unexpected or illegal value.
	 */
	String getOriginWaypointId();

	/**
	 * Get the true bearing from origin to destination.
	 * <p>
	 * <i>Notice: Typically the bearing is calculated when GOTO mode is
	 * activated and it is <strong>not</strong> updated dynamically.</i>
	 * 
	 * @return True bearing
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If the data is
	 *             not available.
	 * @throws net.sf.marineapi.parser.ParseException If the field contains
	 *             unexpected or illegal value.
	 */
	double getTrueBearing();

	/**
	 * Sets the destination waypoint ID.
	 * 
	 * @param id ID to set
	 */
	void setDestinationWaypointId(String id);

	/**
	 * Sets the true bearing from origin to destination, in degrees.
	 * 
	 * @param bearing Bearing value
	 * @throws IllegalArgumentException If bearing value out range 0..360
	 *             degrees.
	 */
	void setMagneticBearing(double bearing);

	/**
	 * Sets the ID of origin waypoint.
	 * 
	 * @param id ID to set.
	 */
	void setOriginWaypointId(String id);

	/**
	 * Sets the true bearing from origin to destination, in degrees.
	 * 
	 * @param bearing Bearing value
	 * @throws IllegalArgumentException If bearing value out range 0..360
	 *             degrees.
	 */
	void setTrueBearing(double bearing);

}
