
package net.sf.marineapi.nmea.sentence;

import net.sf.marineapi.nmea.util.FaaMode;
import net.sf.marineapi.nmea.util.GpsFixStatus;

/**
 * Precision of GPS fix and list of active satellites. Dilution of precision
 * (DOP) is an indication of the effect of satellite geometry on the accuracy of
 * the fix. It is a unitless number where smaller is better.
 * <p>
 * Example:<br>
 * <code>$GPGSA,A,3,02,,,07,,09,24,26,,,,,1.6,1.6,1.0*3D</code>
 * 
 * @author Kimmo Tuukkanen
 */
public interface GSASentence extends Sentence {

	/**
	 * Get the GPS fix mode; 2D, 3D or no fix.
	 * 
	 * @return GpsFixStatus enum
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If the data is
	 *             not available.
	 * @throws net.sf.marineapi.parser.ParseException If the field contains
	 *             unexpected or illegal value.
	 */
	GpsFixStatus getFixStatus();

	/**
	 * Get the horizontal dilution Of precision (HDOP).
	 * 
	 * @return double
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If the data is
	 *             not available.
	 * @throws net.sf.marineapi.parser.ParseException If the field contains
	 *             unexpected or illegal value.
	 */
	double getHorizontalDOP();

	/**
	 * Get the FAA operation mode of GPS.
	 * 
	 * @return FaaMode enum
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If the data is
	 *             not available.
	 * @throws net.sf.marineapi.parser.ParseException If the field contains
	 *             unexpected or illegal value.
	 */
	FaaMode getMode();

	/**
	 * Get the dilution of precision (PDOP) for position.
	 * 
	 * @return double
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If the data is
	 *             not available.
	 * @throws net.sf.marineapi.parser.ParseException If the field contains
	 *             unexpected or illegal value.
	 */
	double getPositionDOP();

	/**
	 * Get list of satellites used for acquiring the GPS fix.
	 * 
	 * @return String array containing satellite IDs.
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If the data is
	 *             not available.
	 * @throws net.sf.marineapi.parser.ParseException If the field contains
	 *             unexpected or illegal value.
	 */
	String[] getSatelliteIds();

	/**
	 * Get the vertical dilution of precision (VDOP).
	 * 
	 * @return double
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If the data is
	 *             not available.
	 * @throws net.sf.marineapi.parser.ParseException If the field contains
	 *             unexpected or illegal value.
	 */
	double getVerticalDOP();

	/**
	 * Set the GPS fix mode; 2D, 3D or no fix.
	 * 
	 * @param status Status to set
	 */
	void setFixStatus(GpsFixStatus status);

	/**
	 * Set the horizontal dilution of precision (HDOP).
	 * 
	 * @param hdop Precision value to set
	 */
	void setHorizontalDOP(double hdop);

	/**
	 * Set the FAA operation mode of GPS.
	 * 
	 * @param mode Mode to set
	 */
	void setMode(FaaMode mode);

	/**
	 * Set the dilution of precision for position.
	 * 
	 * @param pdop Precision value to set
	 */
	void setPositionDOP(double pdop);

	/**
	 * Set list of satellites used for acquiring the GPS fix.
	 * 
	 * @param ids List of satellite IDs, maximum length of array is 12.
	 */
	void setSatelliteIds(String[] ids);

	/**
	 * Set the vertical dilution of precision (VDOP).
	 * 
	 * @param vdop Precision value to set
	 */
	void setVerticalDOP(double vdop);
}
