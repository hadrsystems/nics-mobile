
package net.sf.marineapi.nmea.parser;

import net.sf.marineapi.nmea.sentence.HDGSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.util.CompassPoint;

/**
 * HDG sentence parser.
 * 
 * @author Kimmo Tuukkanen
 */
class HDGParser extends SentenceParser implements HDGSentence {

	private static final int HEADING = 0;
	private static final int DEVIATION = 1;
	private static final int DEV_DIRECTION = 2;
	private static final int VARIATION = 3;
	private static final int VAR_DIRECTION = 4;

	/**
	 * Creates a new HDG parser.
	 * 
	 * @param nmea HDG sentence String
	 */
	public HDGParser(String nmea) {
		super(nmea, SentenceId.HDG);
	}

	/**
	 * Creates a new empty HDG parser.
	 * 
	 * @param talker Talker id to set
	 */
	public HDGParser(TalkerId talker) {
		super(talker, SentenceId.HDG, 5);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.HDGSentence#getDeviation()
	 */
	public double getDeviation() {
		double dev = getDoubleValue(DEVIATION);
		if (dev == 0) {
			return dev;
		}
		CompassPoint dir = CompassPoint.valueOf(getCharValue(DEV_DIRECTION));
		return dir == CompassPoint.WEST ? -dev : dev;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.HDGSentence#getHeading()
	 */
	public double getHeading() {
		return getDoubleValue(HEADING);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.HDGSentence#getVariation()
	 */
	public double getVariation() {
		double var = getDoubleValue(VARIATION);
		if (var == 0) {
			return var;
		}
		CompassPoint dir = CompassPoint.valueOf(getCharValue(VAR_DIRECTION));
		return dir == CompassPoint.WEST ? -var : var;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.HeadingSentence#isTrue()
	 */
	public boolean isTrue() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.HDGSentence#setDeviation(double)
	 */
	public void setDeviation(double deviation) {
		if (deviation < -180 || deviation > 180) {
			throw new IllegalArgumentException("Value out of range [-180..180]");
		}
		if (deviation > 0) {
			setCharValue(DEV_DIRECTION, CompassPoint.EAST.toChar());
		} else if (deviation < 0) {
			setCharValue(DEV_DIRECTION, CompassPoint.WEST.toChar());
		} else {
			setStringValue(DEV_DIRECTION, "");
		}
		setDoubleValue(DEVIATION, Math.abs(deviation), 3, 1);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.HDGSentence#setHeading(double)
	 */
	public void setHeading(double heading) {
		setDegreesValue(HEADING, heading);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.HDGSentence#setVariation(double)
	 */
	public void setVariation(double variation) {
		if (variation < -180 || variation > 180) {
			throw new IllegalArgumentException("Value out of range [-180..180]");
		}
		if (variation > 0) {
			setCharValue(VAR_DIRECTION, CompassPoint.EAST.toChar());
		} else if (variation < 0) {
			setCharValue(VAR_DIRECTION, CompassPoint.WEST.toChar());
		} else {
			setStringValue(VAR_DIRECTION, "");
		}
		setDoubleValue(VARIATION, Math.abs(variation), 3, 1);
	}
}
