package net.sf.marineapi.nmea.parser;

import net.sf.marineapi.nmea.sentence.MTWSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.util.Units;

/**
 * MTW Sentence parser.
 * 
 * @author Warren Zahra, Kimmo Tuukkanen
 */
class MTWParser extends SentenceParser implements MTWSentence {

	private static final int TEMPERATURE = 0;
	private static final int UNIT_INDICATOR = 1;

	/**
	 * Creates new instance of MTWParser with specified sentence.
	 * 
	 * @param nmea MTW sentence string
	 */
	public MTWParser(String nmea) {
		super(nmea);
	}

	/**
	 * Creates new MTW parse without data.
	 * 
	 * @param tid TalkerId to set
	 */
	public MTWParser(TalkerId tid) {
		super(tid, SentenceId.MTW, 2);
		setCharValue(UNIT_INDICATOR, Units.CELSIUS.toChar());
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.MTWSentence#getTemperature()
	 */
	public double getTemperature() {
		return getDoubleValue(TEMPERATURE);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.MTWSentence#setTemperature(double)
	 */
	public void setTemperature(double temp) {
		setDoubleValue(TEMPERATURE, temp, 1, 2);
	}

}
