package net.sf.marineapi.nmea.sentence;

import net.sf.marineapi.nmea.util.Date;

/**
 * Sentences that contains date information. Notice that some sentences may
 * contain only time without the date.
 * 
 * @author Kimmo Tuukkanen
 * @see net.sf.marineapi.nmea.sentence.TimeSentence
 */
public interface DateSentence extends Sentence {

	/**
	 * Parses the date information from sentence fields and returns a
	 * {@link Date}.
	 * 
	 * @return Date object
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If the data is
	 *             not available.
	 * @throws net.sf.marineapi.parser.ParseException If the field contains
	 *             unexpected or illegal value.
	 */
	Date getDate();

	/**
	 * Set date. Depending on the sentence type, the values may be inserted to
	 * multiple fields or combined into one. Four-digit year value may also be
	 * reduced into two-digit format.
	 * 
	 * @param date {@link Date}
	 */
	void setDate(Date date);
}
