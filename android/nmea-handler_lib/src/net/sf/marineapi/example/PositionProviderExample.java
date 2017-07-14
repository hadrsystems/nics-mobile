
package net.sf.marineapi.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.provider.PositionProvider;
import net.sf.marineapi.provider.event.PositionEvent;
import net.sf.marineapi.provider.event.PositionListener;

/**
 * Demonstrates the usage of PositionProvider.
 * 
 * @author Kimmo Tuukkanen
 * @see PositionProvider
 */
public class PositionProviderExample implements PositionListener {

	PositionProvider provider;

	public PositionProviderExample(File f) throws FileNotFoundException {
		InputStream stream = new FileInputStream(f);
		SentenceReader reader = new SentenceReader(stream);
		provider = new PositionProvider(reader);
		provider.addListener(this);
		reader.start();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * net.sf.marineapi.provider.event.PositionListener#providerUpdate(net.sf.marineapi
	 * .provider.event.PositionEvent)
	 */
	public void providerUpdate(PositionEvent evt) {
		// do something with the data..
		System.out.println("TPV: " + evt.toString());
	}

	/**
	 * Startup method.
	 * 
	 * @param args NMEA log file
	 */
	public static void main(String[] args) {

		if (args.length != 1) {
			System.out.println("Usage:\njava PositionProviderExample nmea.log");
			System.exit(1);
		}

		try {
			new PositionProviderExample(new File(args[0]));
			System.out.println("Running, press CTRL-C to stop..");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
