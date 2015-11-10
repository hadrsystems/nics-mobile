
package net.sf.marineapi.provider.event;

import java.util.List;

import net.sf.marineapi.nmea.sentence.GSASentence;
import net.sf.marineapi.nmea.util.SatelliteInfo;
import net.sf.marineapi.nmea.util.GpsFixStatus;
import net.sf.marineapi.nmea.util.FaaMode;

/**
 * SatelliteInfoEvent contains the satellite information collected by
 * {@link net.sf.marineapi.provider.SatelliteInfoProvider}.
 * 
 * @author Kimmo Tuukkanen
 * @see net.sf.marineapi.nmea.sentence.GSASentence
 * @see net.sf.marineapi.nmea.sentence.GSVSentence
 * @see net.sf.marineapi.nmea.util.SatelliteInfo
 */
public class SatelliteInfoEvent extends ProviderEvent {

	private static final long serialVersionUID = -5243047395130051907L;

	private GSASentence gsa;
	private List<SatelliteInfo> info;

	/**
	 * @param source
	 */
	public SatelliteInfoEvent(
		Object source, GSASentence gsa, List<SatelliteInfo> info) {
		super(source);
		this.gsa = gsa;
		this.info = info;
	}

	/**
	 * Returns the list of GPS satellites used for GPS fix.
	 *
	 * @return Satellite ids list as reported by GSA sentence.
	 */
	public String[] getSatelliteIds() {
		return gsa.getSatelliteIds();
	}

	/**
	 * Returns the current detailed satellite information.
	 * 
	 * @return List of SatelliteInfo objects from latest GSV sequence.
	 */
	public List<SatelliteInfo> getSatelliteInfo() {
		return this.info;
	}

	/**
	 * Returns the horizontal precision of GPS fix.
	 *	
	 * @return HDOP value as reported by GSA sentence.
	 */
	public double getHorizontalPrecision() {
		return gsa.getHorizontalDOP();
	}

	/**
	 * Returns the vertical precision of GPS fix.
	 *
	 * @return VDOP as reported by GSA sentence.
	 */
	public double getVerticalPrecision() {
		return gsa.getVerticalDOP();
	}

	/**
	 * Returns the overall precision of GPS fix.
	 *
	 * @return PDOP as reported by GSA sentence.
	 */
	public double getPositionPrecision() {
		return gsa.getPositionDOP();
	}

	/**
	 * Returns the GPS mode of operation as reported in GSA sentence.
	 *
	 * @return FaaMode enum value
	 */
	public FaaMode getGpsMode() {
		return gsa.getMode();
	}

	/**
	 * Returns the GPS fix status as reported by GSA sentence.
	 *
	 * @return GpsFixStatus enum value
	 */
	public GpsFixStatus getGpsFixStatus() {
		return gsa.getFixStatus();
	}
}
