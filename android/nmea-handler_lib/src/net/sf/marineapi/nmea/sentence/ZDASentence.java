
package net.sf.marineapi.nmea.sentence;

import java.util.Date;

/**
 * UTC time and date with local time zone offset.
 * <p>
 * Example: <br>
 * <code>$GPZDA,032915,07,08,2004,00,00*4D</code>
 * 
 * @author Kimmo Tuukkanen
 */
public interface ZDASentence extends TimeSentence, DateSentence {

	/**
	 * Get offset to local time zone in hours, from +/- 0 to +/- 13 hours.
	 * 
	 * @return Time zone offset
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If the data is
	 *             not available.
	 * @throws net.sf.marineapi.parser.ParseException If the field contains
	 *             unexpected or illegal value.
	 */
	int getLocalZoneHours();

	/**
	 * Get offset to local time zone in minutes, from 0 to +/- 59.
	 * 
	 * @return Time zone offset
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If the data is
	 *             not available.
	 * @throws net.sf.marineapi.parser.ParseException If the field contains
	 *             unexpected or illegal value.
	 */
	int getLocalZoneMinutes();

	/**
	 * Set offset to local time zone in hours.
	 * 
	 * @param hours Offset, from 0 to +/- 13 hours.
	 */
	void setLocalZoneHours(int hours);

	/**
	 * Set offset to local time zone in minutes.
	 * 
	 * @param minutes Offset, from 0 to +/- 59 minutes.
	 */
	void setLocalZoneMinutes(int minutes);

	/**
	 * Get date and time as {@link java.util.Date}.
	 * 
	 * @return {@link java.util.Date}
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If any of the
	 *             date/time values is not available.
	 * @throws net.sf.marineapi.parser.ParseException If the any of the
	 *             date/time fields contains invalid value.
	 */
	Date toDate();
}
