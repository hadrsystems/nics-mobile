package net.sf.marineapi.nmea.sentence;

import net.sf.marineapi.nmea.util.Position;

/**
 * Common interface for sentences that contain geographic position.
 * 
 * @author Kimmo Tuukkanen
 */
public interface PositionSentence extends Sentence {

	/**
	 * Gets the geographic position.
	 * 
	 * @return Position
	 * @throws net.sf.marineapi.parser.DataNotAvailableException If any of the
	 *             position related fields is empty.
	 * @throws net.sf.marineapi.parser.ParseException If any of the position
	 *             related fields contains unexpected value.
	 */
	Position getPosition();

	/**
	 * Set the geographic position.
	 * 
	 * @param pos Position to set
	 */
	void setPosition(Position pos);
}
