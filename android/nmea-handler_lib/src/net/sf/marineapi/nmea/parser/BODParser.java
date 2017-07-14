package net.sf.marineapi.nmea.parser;

import net.sf.marineapi.nmea.sentence.BODSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;

/**
 * BOD sentence parser.
 * 
 * @author Kimmo Tuukkanen
 * @see net.sf.marineapi.nmea.sentence.BODSentence
 */
class BODParser extends SentenceParser implements BODSentence {

	// field indices
	private static final int BEARING_TRUE = 0;
	private static final int TRUE_INDICATOR = 1;
	private static final int BEARING_MAGN = 2;
	private static final int MAGN_INDICATOR = 3;
	private static final int DESTINATION = 4;
	private static final int ORIGIN = 5;

	/**
	 * Creates a new instance of BOD parser.
	 * 
	 * @param nmea BOD sentence String
	 * @throws IllegalArgumentException If specified String is invalid or does
	 *             not contain a BOD sentence.
	 */
	public BODParser(String nmea) {
		super(nmea, SentenceId.BOD);
	}

	/**
	 * Creates GSA parser with empty sentence.
	 * 
	 * @param talker TalkerId to set
	 */
	public BODParser(TalkerId talker) {
		super(talker, SentenceId.BOD, 6);
		setCharValue(TRUE_INDICATOR, 'T');
		setCharValue(MAGN_INDICATOR, 'M');
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * net.sf.marineapi.nmea.sentence.BODSentence#getDestinationWaypointId()
	 */
	public String getDestinationWaypointId() {
		return getStringValue(DESTINATION);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.BODSentence#getMagneticBearing()
	 */
	public double getMagneticBearing() {
		return getDoubleValue(BEARING_MAGN);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.BODSentence#getOriginWaypointId()
	 */
	public String getOriginWaypointId() {
		return getStringValue(ORIGIN);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.BODSentence#getTrueBearing()
	 */
	public double getTrueBearing() {
		return getDoubleValue(BEARING_TRUE);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * net.sf.marineapi.nmea.sentence.BODSentence#setDestinationWaypointId(java
	 * .lang.String)
	 */
	public void setDestinationWaypointId(String id) {
		setStringValue(DESTINATION, id);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * net.sf.marineapi.nmea.sentence.BODSentence#setMagneticBearing(double)
	 */
	public void setMagneticBearing(double bearing) {
		setDegreesValue(BEARING_MAGN, bearing);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * net.sf.marineapi.nmea.sentence.BODSentence#setOriginWaypointId(java.lang
	 * .String)
	 */
	public void setOriginWaypointId(String id) {
		setStringValue(ORIGIN, id);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.BODSentence#setTrueBearing(double)
	 */
	public void setTrueBearing(double bearing) {
		setDegreesValue(BEARING_TRUE, bearing);
	}
}
