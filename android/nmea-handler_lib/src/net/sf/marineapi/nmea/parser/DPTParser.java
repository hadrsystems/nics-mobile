
package net.sf.marineapi.nmea.parser;

import net.sf.marineapi.nmea.sentence.DPTSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;

/**
 * DPT sentence parser.
 * 
 * @author Kimmo Tuukkanen
 */
class DPTParser extends SentenceParser implements DPTSentence {

	private static final int DEPTH = 0;
	private static final int OFFSET = 1;
	private static final int MAXIMUM = 2;

	/**
	 * Creates a new instance of DPTParser.
	 * 
	 * @param nmea DPT sentence String
	 */
	public DPTParser(String nmea) {
		super(nmea, SentenceId.DPT);
	}

	/**
	 * Creates a new instance of DPTParser with empty data fields.
	 * 
	 * @param talker TalkerId to set
	 */
	public DPTParser(TalkerId talker) {
		super(talker, SentenceId.DPT, 3);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.DepthSentence#getDepth()
	 */
	public double getDepth() {
		return getDoubleValue(DEPTH);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.DPTSentence#getOffset()
	 */
	public double getOffset() {
		return getDoubleValue(OFFSET);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.DepthSentence#setDepth(double)
	 */
	public void setDepth(double depth) {
		setDoubleValue(DEPTH, depth, 1, 1);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.DPTSentence#setOffset(double)
	 */
	public void setOffset(double offset) {
		setDoubleValue(OFFSET, offset, 1, 1);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.DPTSentence#getMaximum()
	 */
	public int getMaximum() {
		return getIntValue(MAXIMUM);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.sentence.DPTSentence#setMaximum(int)
	 */
	public void setMaximum(int max) {
		setIntValue(MAXIMUM, max);
	}

}
