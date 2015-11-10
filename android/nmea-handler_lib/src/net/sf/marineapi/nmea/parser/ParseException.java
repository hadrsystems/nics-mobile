package net.sf.marineapi.nmea.parser;

/**
 * Thrown to indicate that parser is unable interpret the contents of requested
 * data field. For example, when a field contains invalid value that cannot be
 * parsed to expected native data type.
 * 
 * @author Kimmo Tuukkanen
 */
public class ParseException extends DataNotAvailableException {

	/** serialVersionUID */
	private static final long serialVersionUID = 6203761984607273569L;

	/**
	 * Constructor with description.
	 * 
	 * @param msg Description of the Exception
	 */
	public ParseException(String msg) {
		super(msg);
	}

	/**
	 * Constructor with message and cause.
	 * 
	 * @param msg Description of the Exception
	 * @param cause Throwable that caused this exception
	 */
	public ParseException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
