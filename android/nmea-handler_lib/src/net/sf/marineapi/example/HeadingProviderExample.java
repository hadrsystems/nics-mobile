
package net.sf.marineapi.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.provider.HeadingProvider;
import net.sf.marineapi.provider.event.HeadingEvent;
import net.sf.marineapi.provider.event.HeadingListener;

/**
 * Demonstrates the usage of HeadingProvider.
 * 
 * @author Kimmo Tuukkanen
 */
public class HeadingProviderExample implements HeadingListener {

	private SentenceReader reader;
	private HeadingProvider provider;

	/**
	 * Creates a new instance of FileExample
	 * 
	 * @param f File from which to read Checksum data
	 */
	public HeadingProviderExample(File file) throws IOException {

		// create sentence reader and provide input stream
		InputStream stream = new FileInputStream(file);
		reader = new SentenceReader(stream);

		// create provider and register listener
		provider = new HeadingProvider(reader);
		provider.addListener(this);

		reader.start();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * net.sf.marineapi.provider.event.HeadingListener#providerUpdate(net.sf
	 * .marineapi.provider.event.HeadingEvent)
	 */
	public void providerUpdate(HeadingEvent evt) {
		System.out.println(evt.toString());
	}

	/**
	 * Main method takes one command-line argument, the name of the file to
	 * read.
	 * 
	 * @param args Command-line arguments
	 */
	public static void main(String[] args) {

		if (args.length != 1) {
			String msg = "Example usage:\njava HeadingProviderExample nmea.log";
			System.out.println(msg);
			System.exit(0);
		}

		try {
			new HeadingProviderExample(new File(args[0]));
			System.out.println("Running, press CTRL-C to stop..");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
