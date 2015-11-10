
package net.sf.marineapi.nmea.parser;

/**
 * Thrown to indicate that requested data is not available. For example, when
 * invoking a getter for sentence data field that contains no value.
 * 
 * @author Kimmo Tuukkanen
 */
public class DataNotAvailableException extends RuntimeException {

	private static final long serialVersionUID = -3672061046826633631L;

	/**
	 * Constructor
	 * 
	 * @param msg Exception message
	 */
	public DataNotAvailableException(String msg) {
		super(msg);
	}

	/**
	 * Constructor
	 * 
	 * @param msg Exception message
	 * @param cause Throwable that caused the exception
	 */
	public DataNotAvailableException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
