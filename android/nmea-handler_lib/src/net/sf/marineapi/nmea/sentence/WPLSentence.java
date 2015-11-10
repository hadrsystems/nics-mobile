
package net.sf.marineapi.nmea.sentence;

import net.sf.marineapi.nmea.util.Waypoint;

/**
 * Destination waypoint location and ID. This sentence is transmitted by some
 * GPS models in GOTO mode.
 * <p>
 * Example: <br>
 * <code>$GPWPL,5536.200,N,01436.500,E,RUSKI*1F</code>
 * 
 * @author Kimmo Tuukkanen
 */
public interface WPLSentence extends Sentence {

	/**
	 * Get the destination waypoint.
	 * 
	 * @return Waypoint
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If any of the
	 *             waypoint related data is not available.
	 * @throws net.sf.marineapi.parser.ParseException If any of the waypoint
	 *             related fields contain unexpected or illegal value.
	 */
	Waypoint getWaypoint();

	/**
	 * Set the destination waypoint.
	 * 
	 * @param wp Waypoint to set
	 */
	void setWaypoint(Waypoint wp);

}
