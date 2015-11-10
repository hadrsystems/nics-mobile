package net.sf.marineapi.nmea.parser;

import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.sentence.VTGSentence;
import net.sf.marineapi.nmea.util.FaaMode;

/**
 * VTG sentence parser.
 * 
 * @author Kimmo Tuukkanen
 */
class VTGParser extends SentenceParser implements VTGSentence {

	private static final int TRUE_COURSE = 0;
	private static final int TRUE_INDICATOR = 1;
	private static final int MAGNETIC_COURSE = 2;
	private static final int MAGNETIC_INDICATOR = 3;
	private static final int SPEED_KNOTS = 4;
	private static final int KNOTS_INDICATOR = 5;
	private static final int SPEED_KMPH = 6;
	private static final int KMPH_INDICATOR = 7;
	private static final int MODE = 8;

	/**
	 * Creates a new instance of VTGParser.
	 * 
	 * @param nmea VTG sentence String
	 * @throws IllegalArgumentException If specified sentence is invalid
	 */
	public VTGParser(String nmea) {
		super(nmea, SentenceId.VTG);
	}

	/**
	 * Creates VTG parser with empty sentence.
	 * 
	 * @param talker TalkerId to set
	 */
	public VTGParser(TalkerId talker) {
		super(talker, SentenceId.VTG, 9);
		setCharValue(TRUE_INDICATOR, VTGSentence.TRUE);
		setCharValue(MAGNETIC_INDICATOR, VTGSentence.MAGNETIC);
		setCharValue(KNOTS_INDICATOR, VTGSentence.KNOT);
		setCharValue(KMPH_INDICATOR, VTGSentence.KMPH);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.VTGSentence#getMagneticCourse()
	 */
	public double getMagneticCourse() {
		return getDoubleValue(MAGNETIC_COURSE);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.VTGSentence#getMode()
	 */
	public FaaMode getMode() {
		return FaaMode.valueOf(getCharValue(MODE));
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.VTGSentence#getSpeedKmh()
	 */
	public double getSpeedKmh() {
		return getDoubleValue(SPEED_KMPH);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.VTGSentence#getSpeedKnots()
	 */
	public double getSpeedKnots() {
		return getDoubleValue(SPEED_KNOTS);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.VTGSentence#getTrueCourse()
	 */
	public double getTrueCourse() {
		return getDoubleValue(TRUE_COURSE);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.VTGSentence#setMagneticCourse(double)
	 */
	public void setMagneticCourse(double mcog) {
		setDegreesValue(MAGNETIC_COURSE, mcog);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * net.sf.marineapi.nmea.sentence.VTGSentence#setMode(net.sf.marineapi.nmea
	 * .util.FaaMode)
	 */
	public void setMode(FaaMode mode) {
		setCharValue(MODE, mode.toChar());
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.VTGSentence#setSpeedKmh(double)
	 */
	public void setSpeedKmh(double kmh) {
		if (kmh < 0) {
			throw new IllegalArgumentException("Speed cannot be negative");
		}
		setDoubleValue(SPEED_KMPH, kmh, 1, 2);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.VTGSentence#setSpeedKnots(double)
	 */
	public void setSpeedKnots(double knots) {
		if (knots < 0) {
			throw new IllegalArgumentException("Speed cannot be negative");
		}
		setDoubleValue(SPEED_KNOTS, knots, 1, 2);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.VTGSentence#setTrueCourse(double)
	 */
	public void setTrueCourse(double tcog) {
		setDegreesValue(TRUE_COURSE, tcog);
	}
}
