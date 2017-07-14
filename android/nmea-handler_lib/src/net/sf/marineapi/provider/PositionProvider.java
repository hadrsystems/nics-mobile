
package net.sf.marineapi.provider;

import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GLLSentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.FaaMode;
import net.sf.marineapi.nmea.util.GpsFixQuality;
import net.sf.marineapi.nmea.util.Position;
import net.sf.marineapi.nmea.util.Time;
import net.sf.marineapi.provider.event.PositionEvent;

/**
 * <p>
 * Provides Time, Position & Velocity reports from GPS. Data is captured from
 * RMC, GGA and GLL sentences. RMC is used for date/time, speed and course. GGA
 * is used as primary source for position as it contains also the altitude. When
 * GGA is not available, position may be taken from GLL or RMC. If this is the
 * case, there is no altitude included in the
 * {@link net.sf.marineapi.nmea.util.Position}. GPS data statuses are also
 * captured and events are dispatched only when sentences report
 * {@link net.sf.marineapi.nmea.util.DataStatus#ACTIVE}.
 * <p>
 * When constructing {@link net.sf.marineapi.provider.event.PositionEvent}, the
 * maximum age of captured sentences is 1000 ms, i.e. all sentences are from
 * within the default NMEA update rate (1/s).
 * 
 * @author Kimmo Tuukkanen
 * @see net.sf.marineapi.provider.event.PositionListener
 * @see net.sf.marineapi.provider.event.PositionEvent
 * @see net.sf.marineapi.nmea.io.SentenceReader
 */
public class PositionProvider extends AbstractProvider<PositionEvent> {

	/**
	 * Creates a new instance of PositionProvider.
	 * 
	 * @param reader SentenceReader that provides the required sentences.
	 */
	public PositionProvider(SentenceReader reader) {
		super(reader, SentenceId.RMC, SentenceId.GGA, SentenceId.GLL);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.provider.AbstractProvider#createProviderEvent()
	 */
	@Override
	protected PositionEvent createProviderEvent() {
		Position p = null;
		Double sog = null;
		Double cog = null;
		Date d = null;
		Time t = null;
		FaaMode mode = null;
		GpsFixQuality fix = null;

		for (Sentence s : getSentences()) {
			if (s instanceof RMCSentence) {
				RMCSentence rmc = (RMCSentence) s;
				sog = rmc.getSpeed();
				cog = rmc.getCourse();
				d = rmc.getDate();
				t = rmc.getTime();
				mode = rmc.getMode();
				if (p == null) {
					p = rmc.getPosition();
				}
			} else if (s instanceof GGASentence) {
				// Using GGA as primary position source as it contains both
				// position and altitude
				GGASentence gga = (GGASentence) s;
				p = gga.getPosition();
				fix = gga.getFixQuality();
			} else if (s instanceof GLLSentence && p == null) {
				GLLSentence gll = (GLLSentence) s;
				p = gll.getPosition();
			}
		}

		return new PositionEvent(this, p, sog, cog, d, t, mode, fix);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.provider.AbstractProvider#isReady()
	 */
	@Override
	protected boolean isReady() {
		return hasOne("RMC") && hasOne("GGA", "GLL");
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.provider.AbstractProvider#isValid()
	 */
	@Override
	protected boolean isValid() {

		for (Sentence s : getSentences()) {

			if (s instanceof RMCSentence) {
				DataStatus ds = ((RMCSentence) s).getStatus();
				FaaMode gm = ((RMCSentence) s).getMode();
				if (DataStatus.VOID.equals(ds) || FaaMode.NONE.equals(gm)) {
					return false;
				}
			} else if (s instanceof GGASentence) {
				GpsFixQuality fq = ((GGASentence) s).getFixQuality();
				if (GpsFixQuality.INVALID.equals(fq)) {
					return false;
				}
			} else if (s instanceof GLLSentence) {
				DataStatus ds = ((GLLSentence) s).getStatus();
				if (DataStatus.VOID.equals(ds)) {
					return false;
				}
			}
		}

		return true;
	}
}
