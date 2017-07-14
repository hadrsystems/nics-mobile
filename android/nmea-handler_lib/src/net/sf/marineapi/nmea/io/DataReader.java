package net.sf.marineapi.nmea.io;

/**
 * Interface for data readers.
 * 
 * @author Kimmo Tuukkanen
 */
interface DataReader extends Runnable {

	/**
	 * Tells if the reader is running and actively scanning the data source for
	 * new data.
	 * 
	 * @return <code>true</code> if running, otherwise <code>false</code>.
	 */
	public abstract boolean isRunning();

	/**
	 * Stops the reader permanently.
	 */
	public abstract void stop();
}
