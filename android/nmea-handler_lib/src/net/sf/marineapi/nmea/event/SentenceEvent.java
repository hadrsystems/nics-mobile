
package net.sf.marineapi.nmea.event;

import java.util.EventObject;

import net.sf.marineapi.nmea.sentence.Sentence;

/**
 * Sentence events occur when a valid NMEA 0183 sentence has been read from the
 * data source.
 * 
 * @author Kimmo Tuukkanen
 * @see SentenceListener
 * @see net.sf.marineapi.nmea.io.SentenceReader
 */
public class SentenceEvent extends EventObject {

	private static final long serialVersionUID = -2756954014186470514L;
	private final long timestamp = System.currentTimeMillis();
	private final Sentence sentence;

	/**
	 * Creates a new SentenceEvent object.
	 * 
	 * @param src Object that fired the event
	 * @param s Sentence that triggered the event
	 * @throws IllegalArgumentException If specified sentence is
	 *             <code>null</code>
	 */
	public SentenceEvent(Object src, Sentence s) {
		super(src);
		if (s == null) {
			throw new IllegalArgumentException("Sentence cannot be null");
		}
		this.sentence = s;
	}

	/**
	 * Gets the Sentence object that triggered the event.
	 * 
	 * @return Sentence object
	 */
	public Sentence getSentence() {
		return sentence;
	}

	/**
	 * Get system time when this event was created.
	 * 
	 * @return Milliseconds timestamp
	 */
	public long getTimeStamp() {
		return timestamp;
	}
}
