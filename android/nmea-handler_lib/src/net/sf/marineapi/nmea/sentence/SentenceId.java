
package net.sf.marineapi.nmea.sentence;

/**
 * Defines the supported NMEA 0831 sentence types. Sentence address field is a
 * combination of talker and sentence IDs, for example GPBOD, GPGGA or GPGGL.
 * 
 * @author Kimmo Tuukkanen
 * @see net.sf.marineapi.nmea.sentence.TalkerId
 */
public enum SentenceId {

	/** Bearing Origin to Destination */
	BOD,
	/** Depth of water below transducer; in meters, feet and fathoms */
	DBT,
	/** Depth of water below transducer; in meters. */
	DPT,
	/** Global Positioning System fix data */
	GGA,
	/** Geographic position (latitude/longitude) */
	GLL,
	/** Dilution of precision (DOP) of GPS fix and active satellites */
	GSA,
	/** Detailed satellite data */
	GSV,
	/** Vessel heading in degrees with magnetic variation and deviation. */
	HDG,
	/** Vessel heading in degrees with respect to true north. */
	HDM,
	/** Vessel heading in degrees true */
	HDT,
	/** LRF TruPulse 360B **/
	LTIT,
	/** Air temperature in degrees centigrade (Celsius). */
	MTA,
	/** Water temperature in degrees centigrade (Celsius). */
	MTW,
	/** Wind speed and angle */
	MWV,
	/** Recommended minimum navigation information */
	RMB,
	/** Recommended minimum specific GPS/TRANSIT data */
	RMC,
	/** Route data and waypoint list */
	RTE,
	/** Track made good and ground speed */
	VTG,
	/** Water speed and heading */
	VHW,
	/** Waypoint location (latitude/longitude) */
	WPL,
	/** UTC time and date with local time zone offset */
	ZDA;

	/**
	 * Parses the sentence id from specified sentence String and returns a
	 * corresponding <code>SentenceId</code> enum (assuming it exists).
	 * 
	 * @param nmea Sentence String
	 * @return SentenceId enum
	 * @throws IllegalArgumentException If specified String is not valid
	 *             sentence
	 */
	public static SentenceId parse(String nmea) {
		String sid = parseStr(nmea);
		return SentenceId.valueOf(sid);
	}

	/**
	 * Parses the sentence id from specified sentence String and returns it as
	 * String.
	 * 
	 * @param nmea Sentence String
	 * @return Sentence Id, e.g. "GGA" or "GLL"
	 * @throws IllegalArgumentException If specified String is not recognized as
	 *             NMEA sentence
	 */
	public static String parseStr(String nmea) {

		if (!SentenceValidator.isSentence(nmea)) {
			throw new IllegalArgumentException("String is not a sentence");
		}

		String id = null;
		if (nmea.startsWith("$P")) {
			id = nmea.substring(2, 6);
		} else {
			id = nmea.substring(3, 6);
		}
		return id;
	}
}
