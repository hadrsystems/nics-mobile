
package net.sf.marineapi.nmea.sentence;

/**
 * Provides Sentence checksum calculation and utilities.
 * 
 * @author Kimmo Tuukkanen
 */
public final class Checksum {

	private Checksum() {
	}

	/**
	 * Calculates and adds a checksum to specified sentence String. Existing
	 * checksum will be replaced with the new one.
	 * <p>
	 * For example, <br>
	 * <code>$GPGLL,6011.552,N,02501.941,E,120045,A</code><br>
	 * results in <br>
	 * <code>$GPGLL,6011.552,N,02501.941,E,120045,A*26</code>
	 * <p>
	 * <code>$GPGLL,6011.552,N,02501.941,E,120045,A*00</code><br>
	 * results in <br>
	 * <code>$GPGLL,6011.552,N,02501.941,E,120045,A*26</code>
	 * 
	 * @param sentence Sentence in String representation
	 * @return The specified String with checksum added.
	 */
	public static String add(String sentence) {

		String str = sentence;

		int i = str.indexOf(Sentence.CHECKSUM_DELIMITER);
		if (i != -1) {
			str = str.substring(0, i);
		}

		return str + Sentence.CHECKSUM_DELIMITER + calculate(str);
	}

	/**
	 * Calculates the checksum of sentence String. Checksum is a XOR of each
	 * character between, but not including, the $ and * characters. The
	 * resulting hex value is returned as a String in two digit format, padded
	 * with a leading zero if necessary. The method will calculate the checksum
	 * for any given String and the sentence validity is not checked.
	 * 
	 * @param nmea NMEA Sentence with or without checksum.
	 * @return Checksum hex value, padded with leading zero if necessary.
	 */
	public static String calculate(String nmea) {
		char ch;
		int sum = 0;
		for (int i = 0; i < nmea.length(); i++) {
			ch = nmea.charAt(i);
			if (i == 0
					&& (ch == Sentence.BEGIN_CHAR || ch == Sentence.ALTERNATIVE_BEGIN_CHAR)) {
				continue;
			} else if (ch == Sentence.CHECKSUM_DELIMITER) {
				break;
			} else if (sum == 0) {
				sum = (byte) ch;
			} else {
				sum ^= (byte) ch;
			}
		}
		return String.format("%02X", sum);
	}

}
