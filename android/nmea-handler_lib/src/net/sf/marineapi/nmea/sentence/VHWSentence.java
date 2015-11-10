package net.sf.marineapi.nmea.sentence;

/**
 * Water speed and heading in respect to true and magnetic north.
 * 
 * @author Warren Zahra, Kimmo Tuukkanen
 */
public interface VHWSentence extends HeadingSentence {

	/**
	 * Returns the current heading.
	 * 
	 * @return Heading in degrees magnetic.
	 */
	double getMagneticHeading();

	/**
	 * Returns the current water speed.
	 * 
	 * @return Speed in km/h (kilmetres per hour)
	 */
	double getSpeedKmh();

	/**
	 * Returns the current water speed.
	 * 
	 * @return Speed in knots (nautical miles per hour)
	 */
	double getSpeedKnots();

	/**
	 * Sets the magnetic heading.
	 * 
	 * @param hdg Heading in degrees magnetic.
	 * @throws IllegalArgumentException If value is out of bounds [0..360]
	 */
	void setMagneticHeading(double hdg);

	/**
	 * Sets the water speed in km/h.
	 * 
	 * @param kmh Speed in kilmetres per hour.
	 */
	void setSpeedKmh(double kmh);

	/**
	 * Sets the water speed in knots.
	 * 
	 * @param knots Speed in knots (nautical miles per hour)
	 */
	void setSpeedKnots(double knots);
}
