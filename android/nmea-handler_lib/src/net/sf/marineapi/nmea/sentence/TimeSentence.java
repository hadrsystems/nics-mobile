package net.sf.marineapi.nmea.sentence;

import net.sf.marineapi.nmea.util.Time;

/**
 * Interface for sentences that provide UTC time. Notice that some sentences
 * contain only UTC time, while others may provide also date.
 * 
 * @author Kimmo Tuukkanen
 * @see net.sf.marineapi.nmea.sentence.DateSentence
 */
public interface TimeSentence extends Sentence {

	/**
	 * Get the time of day.
	 * 
	 * @return Time
	 */
	Time getTime();

	/**
	 * Set the time of day.
	 * 
	 * @param t Time to set
	 */
	void setTime(Time t);
}
