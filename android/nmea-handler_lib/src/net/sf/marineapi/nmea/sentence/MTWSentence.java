package net.sf.marineapi.nmea.sentence;

/**
 * Water temperature in degrees Celsius.
 * 
 * @author Warren Zahra, Kimmo Tuukkanen
 */
public interface MTWSentence extends Sentence {

	/**
	 * Get the water temperature.
	 * 
	 * @return Temperature in degrees Celsius.
	 */
	double getTemperature();

	/**
	 * Set the water temperature.
	 * 
	 * @param temp Water temperature in degrees Celsius.
	 */
	void setTemperature(double temp);
}
