
package net.sf.marineapi.nmea.parser;

import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Units;

/**
 * MWV sentence parser.
 * 
 * @author Kimmo Tuukkanen
 */
class MWVParser extends SentenceParser implements MWVSentence {

	private static final int WIND_ANGLE = 0;
	private static final int REFERENCE = 1;
	private static final int WIND_SPEED = 2;
	private static final int SPEED_UNITS = 3;
	private static final int DATA_STATUS = 4;

	/**
	 * Creates a new instance of MWVParser.
	 * 
	 * @param nmea MWV sentence String
	 */
	public MWVParser(String nmea) {
		super(nmea, SentenceId.MWV);
	}

	/**
	 * Creates a new empty instance of MWVParser.
	 * 
	 * @param talker Talker id to set
	 */
	public MWVParser(TalkerId talker) {
		super(talker, SentenceId.MWV, 5);
		setCharValue(DATA_STATUS, DataStatus.VOID.toChar());
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.MWVSentence#getAngle()
	 */
	public double getAngle() {
		return getDoubleValue(WIND_ANGLE);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.MWVSentence#getSpeed()
	 */
	public double getSpeed() {
		return getDoubleValue(WIND_SPEED);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.MWVSentence#getSpeedUnit()
	 */
	public Units getSpeedUnit() {
		return Units.valueOf(getCharValue(SPEED_UNITS));
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.MWVSentence#getStatus()
	 */
	public DataStatus getStatus() {
		return DataStatus.valueOf(getCharValue(DATA_STATUS));
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.MWVSentence#isTrue()
	 */
	public boolean isTrue() {
		char ch = getCharValue(REFERENCE);
		return ch == 'T';
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.MWVSentence#setAngle(double)
	 */
	public void setAngle(double angle) {
		setDegreesValue(WIND_ANGLE, angle);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.MWVSentence#setSpeed(double)
	 */
	public void setSpeed(double speed) {
		if (speed < 0) {
			throw new IllegalArgumentException("Speed must be positive");
		}
		setDoubleValue(WIND_SPEED, speed, 1, 1);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * net.sf.marineapi.nmea.sentence.MWVSentence#setSpeedUnit(net.sf.marineapi
	 * .nmea.util.Units)
	 */
	public void setSpeedUnit(Units unit) {
		if (unit == Units.METER || unit == Units.KMH || unit == Units.KNOT) {
			setCharValue(SPEED_UNITS, unit.toChar());
			return;
		}
		throw new IllegalArgumentException("Invalid unit for speed");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * net.sf.marineapi.nmea.sentence.MWVSentence#setStatus(net.sf.marineapi
	 * .nmea.util.DataStatus)
	 */
	public void setStatus(DataStatus status) {
		setCharValue(DATA_STATUS, status.toChar());
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.MWVSentence#setTrue(boolean)
	 */
	public void setTrue(boolean isTrue) {
		if (isTrue) {
			setCharValue(REFERENCE, 'T');
		} else {
			setCharValue(REFERENCE, 'R');
		}
	}

}
