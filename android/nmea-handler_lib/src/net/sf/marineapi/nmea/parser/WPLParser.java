package net.sf.marineapi.nmea.parser;

import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.sentence.WPLSentence;
import net.sf.marineapi.nmea.util.CompassPoint;
import net.sf.marineapi.nmea.util.Waypoint;

/**
 * WPL sentence parser.
 * 
 * @author Kimmo Tuukkanen
 */
class WPLParser extends PositionParser implements WPLSentence {

	// field ids
	private static final int LATITUDE = 0;
	private static final int LAT_HEMISPHERE = 1;
	private static final int LONGITUDE = 2;
	private static final int LON_HEMISPHERE = 3;
	private static final int WAYPOINT_ID = 4;

	/**
	 * Creates a new instance of WPLParser.
	 * 
	 * @param nmea WPL sentence String.
	 * @throws IllegalArgumentException If specified sentence is invalid.
	 */
	public WPLParser(String nmea) {
		super(nmea, SentenceId.WPL);
	}

	/**
	 * Creates WPL parser with empty sentence.
	 * 
	 * @param talker TalkerId to set
	 */
	public WPLParser(TalkerId talker) {
		super(talker, SentenceId.WPL, 5);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.WPLSentence#getWaypoint()
	 */
	public Waypoint getWaypoint() {
		String id = getStringValue(WAYPOINT_ID);
		double lat = parseLatitude(LATITUDE);
		double lon = parseLongitude(LONGITUDE);
		CompassPoint lath = parseHemisphereLat(LAT_HEMISPHERE);
		CompassPoint lonh = parseHemisphereLon(LON_HEMISPHERE);
		return new Waypoint(id, lat, lath, lon, lonh);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * net.sf.marineapi.nmea.sentence.WPLSentence#setWaypoint(net.sf.marineapi
	 * .nmea.util.Waypoint)
	 */
	public void setWaypoint(Waypoint wp) {
		setStringValue(WAYPOINT_ID, wp.getId());
		setLatitude(LATITUDE, wp.getLatitude());
		setLongitude(LONGITUDE, wp.getLongitude());
		setLatHemisphere(LAT_HEMISPHERE, wp.getLatHemisphere());
		setLonHemisphere(LON_HEMISPHERE, wp.getLonHemisphere());
	}
}
