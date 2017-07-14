package net.sf.marineapi.provider.event;

import net.sf.marineapi.nmea.sentence.HeadingSentence;

/**
 * HeadingProvider event, reports the current magnetic/true heading of vessel in
 * degrees.
 * 
 * @author Kimmo Tuukkanen
 * @see net.sf.marineapi.provider.HeadingProvider
 */
public class HeadingEvent extends ProviderEvent {

	private static final long serialVersionUID = 5706774741081575448L;
	private double heading;
	private boolean isTrue;

	/**
	 * @param source
	 */
	public HeadingEvent(Object source, HeadingSentence s) {
		super(source);
		heading = s.getHeading();
		isTrue = s.isTrue();
	}

	/**
	 * Returns the current heading.
	 * 
	 * @return Heading in degrees.
	 */
	public double getHeading() {
		return heading;
	}

	/**
	 * Tells if the heading is relative to true or magnetic north.
	 * 
	 * @return true if true heading, otherwise false (magnetic).
	 */
	public boolean isTrue() {
		return isTrue;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.EventObject#toString()
	 */
	public String toString() {
		return "[" + getHeading() + ", " + (isTrue() ? "T" : "M") + "]";
	}
}
