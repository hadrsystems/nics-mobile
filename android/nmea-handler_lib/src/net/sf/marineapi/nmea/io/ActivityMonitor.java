package net.sf.marineapi.nmea.io;

/**
 * Monitor for firing state change events events, i.e. reader started, paused or
 * stopped.
 * 
 * @author Kimmo Tuukkanen
 */
class ActivityMonitor {

	private long lastUpdated = -1;
	private SentenceReader parent;

	public ActivityMonitor(SentenceReader parent) {
		this.parent = parent;
	}

	/**
	 * Resets the monitor in initial state.
	 */
	public void reset() {
		lastUpdated = -1;
	}

	/**
	 * Refreshes the monitor timestamp and fires reading started event if
	 * currently paused.
	 */
	public void refresh() {
		if (lastUpdated < 0) {
			parent.fireReadingStarted();
		}
		this.lastUpdated = System.currentTimeMillis();
	}

	/**
	 * Heartbeat method, checks the time out if not paused.
	 */
	public void tick() {
		if (lastUpdated > 0) {
			long elapsed = System.currentTimeMillis() - lastUpdated;
			int timeout = parent.getPauseTimeout();
			if (elapsed >= timeout) {
				parent.fireReadingPaused();
				reset();
			}
		}
	}
}
