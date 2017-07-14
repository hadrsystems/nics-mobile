
package net.sf.marineapi.nmea.sentence;

/**
 * Interface for sentences that provide vessel's true or magnetic heading.
 * 
 * @author Kimmo Tuukkanen
 */
public interface HeadingSentence extends Sentence {

	/**
	 * Returns the vessel's current heading.
	 * 
	 * @return Heading in degrees.
	 * @see #isTrue()
	 */
	double getHeading();

	/**
	 * Tells if the heading returned and set by {@link #getHeading()} and
	 * {@link #setHeading(double)} methods is <em>true</em> or <em>magnetic</em>
	 * .
	 * 
	 * @return <code>true</code> if true heading, otherwise <code>false</code>
	 *         for magnetic heading.
	 */
	boolean isTrue();

	/**
	 * Sets the heading value.
	 * 
	 * @param hdt Heading in degrees
	 * @see #isTrue()
	 * @throws IllegalArgumentException If heading value out of range [0..360]
	 */
	void setHeading(double hdt);
}
