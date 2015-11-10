package net.sf.marineapi.nmea.io;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceValidator;

/**
 * DataReader implementation using DatagramSocket as data source.
 * 
 * @author Kimmo Tuukkanen, Ludovic Drouineau
 */
class UDPDataReader implements DataReader {

	private SentenceReader parent;
	private ActivityMonitor monitor;
	private DatagramSocket socket;
	private volatile boolean isRunning = true;
	byte[] buffer = new byte[1024];

	/**
	 * Creates a new instance of StreamReader.
	 * 
	 * @param socket DatagramSocket to be used as data source.
	 * @param parent SentenceReader dispatching events for this reader.
	 */
	public UDPDataReader(DatagramSocket socket, SentenceReader parent) {
		this.socket = socket;
		this.parent = parent;
	}

	/**
	 * Tells if the reader is currently running, i.e. actively scanning the
	 * input stream for new data.
	 * 
	 * @return <code>true</code> if running, otherwise <code>false</code>.
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Reads the input stream and fires SentenceEvents
	 */
	public void run() {

		monitor = new ActivityMonitor(parent);
		SentenceFactory factory = SentenceFactory.getInstance();

		while (isRunning) {
			try {
				DatagramPacket pkg = new DatagramPacket(buffer, buffer.length);
				socket.receive(pkg);
				String data = new String(pkg.getData(), 0, pkg.getLength());
				if (SentenceValidator.isValid(data)) {
					monitor.refresh();
					Sentence s = factory.createParser(data);
					parent.fireSentenceEvent(s);
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

	/**
	 * Stops the run loop.
	 */
	public void stop() {
		isRunning = false;
	}

}
