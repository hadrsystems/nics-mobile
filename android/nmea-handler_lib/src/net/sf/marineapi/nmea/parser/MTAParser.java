
package net.sf.marineapi.nmea.parser;

import net.sf.marineapi.nmea.sentence.MTASentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.util.Units;

/**
 * MTA sentence parser.
 * 
 * @author Kimmo Tuukkanen
 */
class MTAParser extends SentenceParser implements MTASentence {

	private static final int TEMPERATURE = 0;
	private static final int UNIT_INDICATOR = 1;

	/**
	 * Constructor.
	 */
	public MTAParser(String mta) {
		super(mta, SentenceId.MTA);
	}

	/**
	 * Constructor.
	 */
	public MTAParser(TalkerId talker) {
		super(talker, SentenceId.MTA, 2);
		setCharValue(UNIT_INDICATOR, Units.CELSIUS.toChar());
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.MTASentence#getTemperature()
	 */
	public double getTemperature() {
		return getDoubleValue(TEMPERATURE);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.MTASentence#setTemperature(double)
	 */
	public void setTemperature(double temp) {
		setDoubleValue(TEMPERATURE, temp, 1, 2);
	}

}
