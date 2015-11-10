
package net.sf.marineapi.nmea.util;

/**
 * Defines the supported datums, i.e. the coordinate systems used to specify
 * geographic positions.
 * 
 * @author Kimmo Tuukkanen
 * @see net.sf.marineapi.nmea.util.Position
 */
public enum Datum {

	/** World Geodetic System 1984, the default datum in GPS systems. */
	WGS84,
	/** North American Datum 1983 */
	NAD83,
	/** North American Datum 1927 */
	NAD27;
}
