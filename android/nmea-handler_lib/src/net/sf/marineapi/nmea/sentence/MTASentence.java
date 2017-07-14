
package net.sf.marineapi.nmea.sentence;

/**
 * Air temperature in degrees Celsius.
 * 
 * @author Kimmo Tuukkanen
 */
public interface MTASentence extends Sentence {

	/**
	 * Returns the air temperature.
	 * 
	 * @return Temperature in degrees Celsius.
	 */
	double getTemperature();

	/**
	 * Sets the air temperature.
	 * 
	 * @param temp Temperature in degrees Celsius.
	 */
	void setTemperature(double temp);

}
