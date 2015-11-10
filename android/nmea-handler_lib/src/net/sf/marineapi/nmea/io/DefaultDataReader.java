package net.sf.marineapi.nmea.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceValidator;

/**
 * The default data reader implementation using InputStream as data source.
 * 
 * @author Kimmo Tuukkanen
 */
class DefaultDataReader implements DataReader {

	private SentenceReader parent;
	private ActivityMonitor monitor;
	private BufferedReader input;
	private volatile boolean isRunning = true;

	/**
	 * Creates a new instance of DefaultDataReader.
	 * 
	 * @param source InputStream to be used as data source.
	 * @param parent SentenceReader dispatching events for this reader.
	 */
	public DefaultDataReader(InputStream source, SentenceReader parent) {
		InputStreamReader isr = new InputStreamReader(source);
		this.input = new BufferedReader(isr);
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.io.DataReader#isRunning()
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.io.DataReader#stop()
	 */
	public void stop() {
		isRunning = false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		monitor = new ActivityMonitor(parent);
		SentenceFactory factory = SentenceFactory.getInstance();

		while (isRunning) {
			try {
				if (input.ready()) {
					String data = input.readLine();
					if (SentenceValidator.isValid(data)) {
						monitor.refresh();
						Sentence s = factory.createParser(data);
						parent.fireSentenceEvent(s);
					}
				}
				monitor.tick();
				Thread.sleep(50);
			} catch (Exception e) {
				// nevermind, keep trying..
			}
		}
		monitor.reset();
		parent.fireReadingStopped();
	}
}
