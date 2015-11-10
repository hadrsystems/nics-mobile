
package net.sf.marineapi.nmea.util;

import java.util.Date;

/**
 * Waypoint represents a named geographic location.
 * 
 * @author Kimmo Tuukkanen
 * @see net.sf.marineapi.nmea.util.Position
 */
public class Waypoint extends Position {

	/** ID or name of the waypoint */
	private String id;
	/** Waypoint description */
	private String description = "";
	/** Time stamp */
	private final Date timeStamp = new Date();

	/**
	 * Creates a new instance of Waypoint with default WGS84 datum.
	 * 
	 * @param id Waypoint identifier
	 * @param lat Latitude degrees of the waypoint position
	 * @param lath Hemisphere of latitude
	 * @param lon Longitude degrees of waypoint position
	 * @param lonh Hemisphere of longitude
	 */
	public Waypoint(String id, double lat, CompassPoint lath, double lon,
			CompassPoint lonh) {

		super(lat, lath, lon, lonh);
		this.id = id;
	}

	/**
	 * Creates a new instance of Waypoint with explicitly specified datum.
	 * 
	 * @param id Waypoint identifier
	 * @param lat Latitude degrees of the waypoint position
	 * @param lath Hemisphere of latitude
	 * @param lon Longitude degrees of waypoint position
	 * @param lonh Hemisphere of longitude
	 * @param datum Position datum, i.e. the coordinate system.
	 */
	public Waypoint(String id, double lat, CompassPoint lath, double lon,
			CompassPoint lonh, Datum datum) {

		super(lat, lath, lon, lonh, datum);
		this.id = id;
	}

	/**
	 * Gets the waypoint description/comment.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get id of Waypoint
	 * 
	 * @return id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Returns the time stamp when the Waypoint was created.
	 * 
	 * @return Date
	 */
	public Date getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Sets the waypoint description.
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Set the id of Waypoint
	 * 
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
}
