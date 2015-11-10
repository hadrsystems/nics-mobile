
package net.sf.marineapi.nmea.sentence;

import net.sf.marineapi.nmea.util.DataStatus;

/**
 * Current geographic position and time.
 * <p>
 * Example: <br>
 * <code>$GPGLL,6011.552,N,02501.941,E,120045,A*26</code>
 * 
 * @author Kimmo Tuukkanen
 */
public interface GLLSentence extends PositionSentence, TimeSentence {

	/**
	 * Get the data quality status, valid or invalid.
	 * 
	 * @return {@link DataStatus#ACTIVE} or {@link DataStatus#VOID}
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If the data is
	 *             not available.
	 * @throws net.sf.marineapi.parser.ParseException If the field contains
	 *             unexpected or illegal value.
	 */
	DataStatus getStatus();

	/**
	 * Set the data quality status, valid or invalid.
	 * 
	 * @param status DataStatus to set
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If the data is
	 *             not available.
	 * @throws net.sf.marineapi.parser.ParseException If the field contains
	 *             unexpected or illegal value.
	 */
	void setStatus(DataStatus status);

}
