
package net.sf.marineapi.nmea.util;

/**
 * Defines the supported compass and relative directions.
 * 
 * @author Kimmo Tuukkanen
 */
public enum CompassPoint {

	/** North */
	NORTH('N'),
	/** East */
	EAST('E'),
	/** South */
	SOUTH('S'),
	/** West */
	WEST('W');

	private char ch;

	private CompassPoint(char c) {
		this.ch = c;
	}

	/**
	 * Returns the corresponding char constant.
	 * 
	 * @return Char indicator for Direction
	 */
	public char toChar() {
		return ch;
	}

	/**
	 * Get the enum corresponding to specified char.
	 * 
	 * @param c Char indicator for Direction
	 * @return Direction
	 */
	public static CompassPoint valueOf(char c) {
		for (CompassPoint d : values()) {
			if (d.toChar() == c) {
				return d;
			}
		}
		return valueOf(String.valueOf(c));
	}
}
