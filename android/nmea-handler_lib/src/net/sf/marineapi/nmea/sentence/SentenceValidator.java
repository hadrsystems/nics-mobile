
package net.sf.marineapi.nmea.sentence;

import java.util.regex.Pattern;

/**
 * SentenceValidator checks any String against NMEA 0183 format.
 * 
 * @author Kimmo Tuukkanen
 */
public final class SentenceValidator {

	private static final Pattern reChecksum = Pattern.compile(
		"^[$|!]{1}[A-Z0-9]{5}[,][\\x20-\\x7F]{0,72}[*][A-F0-9]{2}$");
	
	private static final Pattern reNoChecksum = Pattern.compile(
		"^[$|!]{1}[A-Z0-9]{5}[,][\\x20-\\x7F]{0,75}$");

	private SentenceValidator() {
	}

	/**
	 * Tells if the specified String matches the NMEA 0183 sentence format.
	 * <p>
	 * String is considered as a sentence if it meets the following criteria:
	 * <ul>
	 * <li>Starts with '$' character
	 * <li>'$' is followed by 5 upper-case chars and a comma (sentence type id)
	 * <li>Length is max. 80 chars long (excluding &lt;CR&gt;&lt;LF&gt;)
	 * <li>String contains only printable ASCII characters
	 * <li>Checksum is correct and separated by '*' char (unless omitted)
	 * </ul>
	 * 
	 * @param nmea String to inspect
	 * @return true if recognized as sentence, otherwise false.
	 */
	public static boolean isSentence(String nmea) {

		if (nmea == null || "".equals(nmea)) {
			return false;
		}

		if (nmea.indexOf(Sentence.CHECKSUM_DELIMITER) < 0) {
			return reNoChecksum.matcher(nmea).matches();
		}

		return reChecksum.matcher(nmea).matches();
	}

	/**
	 * Tells if the specified String is a valid NMEA 0183 sentence. String is
	 * considered as valid sentence if it passes the {@link #isSentence(String)}
	 * test and contains a valid checksum. Sentences without checksum are
	 * validated only by checking the general sentence characteristics.
	 * 
	 * @param nmea String to validate
	 * @return <code>true</code> if valid, otherwise <code>false</code>.
	 */
	public static boolean isValid(String nmea) {

		boolean isValid = false;

		if (SentenceValidator.isSentence(nmea)) {
			int i = nmea.indexOf(Sentence.CHECKSUM_DELIMITER);
			if (i > 0) {
				String sum = nmea.substring(++i, nmea.length());
				isValid = sum.equals(Checksum.calculate(nmea));
			} else {
				// no checksum
				isValid = true;
			}
		}

		return isValid;
	}
}
