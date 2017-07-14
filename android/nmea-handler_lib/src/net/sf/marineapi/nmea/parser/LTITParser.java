package net.sf.marineapi.nmea.parser;

import net.sf.marineapi.nmea.sentence.LTITSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;

/**
 * LTIT Sentence parser.
 * 
 * @author Piyush Agarwal
 */
class LTITParser extends SentenceParser implements LTITSentence {

	// field indices
	private static final int MESSAGE_TYPE = 0;

	/**
	 * Creates a new instance of LTITParser.
	 * 
	 * @param nmea LTIT sentence String.
	 * @throws IllegalArgumentException If the given sentence is invalid or does
	 *             not contain GLL sentence.
	 */
	public LTITParser(String nmea) {
		super(nmea, SentenceId.LTIT);
	}

	/**
	 * Creates LTIT parser with empty sentence.
	 * 
	 * @param talker TalkerId to set
	 */
	public LTITParser(TalkerId talker) {
		super(talker, SentenceId.LTIT, 6);
	}
	
	public String getMessageType() {
		return getStringValue(MESSAGE_TYPE);
	}
	
	public String getFieldString(int fieldIdx) {
		return getStringValue(fieldIdx);
	}
	
	public int getFieldInt(int fieldIdx) {
		return getIntValue(fieldIdx);
	}
	
	public double getFieldDouble(int fieldIdx) {
		return getDoubleValue(fieldIdx);
	}
	
	public char getFieldChar(int fieldIdx) {
		return getCharValue(fieldIdx);
	}
}
