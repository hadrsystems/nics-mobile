
package net.sf.marineapi.nmea.parser;

import net.sf.marineapi.nmea.sentence.HDTSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;

/**
 * HDT sentence parser.
 * 
 * @author Kimmo Tuukkanen
 */
class HDTParser extends SentenceParser implements HDTSentence {

	private static final int HEADING = 0;
	private static final int TRUE_INDICATOR = 1;

	/**
	 * Creates a new HDT parser.
	 * 
	 * @param nmea HDT sentence String to parse.
	 */
	public HDTParser(String nmea) {
		super(nmea, SentenceId.HDT);
	}

	/**
	 * Creates a new empty HDT sentence.
	 * 
	 * @param talker Talker id to set
	 */
	public HDTParser(TalkerId talker) {
		super(talker, SentenceId.HDT, 2);
		setCharValue(TRUE_INDICATOR, 'T');
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.parser.HeadingSentence#getHeading()
	 */
	public double getHeading() {
		return getDoubleValue(HEADING);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.HeadingSentence#isTrue()
	 */
	public boolean isTrue() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.parser.HeadingSentence#setHeading(double)
	 */
	public void setHeading(double hdt) {
		setDegreesValue(HEADING, hdt);
	}
}
