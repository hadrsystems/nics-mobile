
package net.sf.marineapi.nmea.sentence;

/**
 * Interface for sentences that are from TruPulse 360B
 * 
 * @author Piyush Agarwal
 */
public interface LTITSentence extends Sentence {

	String getMessageType();
	String getFieldString(int fieldIdx);
	int getFieldInt(int fieldIdx);
	double getFieldDouble(int fieldIdx);
	char getFieldChar(int fieldIdx);
}
