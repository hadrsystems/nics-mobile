
package net.sf.marineapi.provider.event;

import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.FaaMode;
import net.sf.marineapi.nmea.util.GpsFixQuality;
import net.sf.marineapi.nmea.util.Position;
import net.sf.marineapi.nmea.util.Time;

/**
 * GPS time/position/velocity report with current position, altitude, speed,
 * course and a time stamp. Notice that altitude may be missing, depending on
 * the source sentence of position (only
 * {@link net.sf.marineapi.nmea.sentence.GGASentence} contains altitude).
 * 
 * @author Kimmo Tuukkanen
 * @see net.sf.marineapi.provider.PositionProvider
 * @see net.sf.marineapi.provider.event.PositionListener
 */
public class PositionEvent extends ProviderEvent implements Cloneable {

	private static final long serialVersionUID = 1L;
	private Double course;
	private Date date;
	private Position position;
	private Double speed;
	private Time time;
	private FaaMode mode;
	private GpsFixQuality fix;

	/**
	 * Creates a new instance of PositionEvent.
	 * 
	 * @param source Source object of event
	 */
	public PositionEvent(Object source, Position p, double sog, double cog, Date d,
			Time t, FaaMode m, GpsFixQuality fq) {
		super(source);
		position = p;
		speed = sog;
		course = cog;
		date = d;
		time = t;
		mode = m;
		fix = fq;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public PositionEvent clone() {
		return new PositionEvent(getSource(), position, speed, course, date, time,
				mode, fix);
	}

	/**
	 * Returns the current (true) course over ground.
	 * 
	 * @return the course
	 */
	public Double getCourse() {
		return course;
	}

	/**
	 * Returns the date.
	 * 
	 * @return Date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Returns the current GPS fix quality.
	 * 
	 * @return GpsFixQuality
	 */
	public GpsFixQuality getFixQuality() {
		return fix;
	}

	/**
	 * Returns the current FAA operating mode of GPS receiver.
	 * 
	 * @return FaaMode
	 */
	public FaaMode getMode() {
		return mode;
	}

	/**
	 * Returns the current position.
	 * 
	 * @return Position
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Returns the current speed over ground, in km/h.
	 * 
	 * @return the speed
	 */
	public Double getSpeed() {
		return speed;
	}

	/**
	 * Returns the time.
	 * 
	 * @return Time
	 */
	public Time getTime() {
		return time;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.EventObject#toString()
	 */
	@Override
	public String toString() {
		String ptr = "t[%s %s] p%s v[%.01f, %.01f]";
		return String.format(ptr, date, time, position, speed, course);
	}
}
