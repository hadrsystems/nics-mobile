package edu.mit.ll.bluetoothmanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Should be named BluetoothDiscoveryActivity.java
 * 
 * See select_device.xml
 * 
 * Start Bluetooth discovery to detect any Bluetooth devices with minimum signal
 * strength. Put these devices in an ordered list (ordered by signal strength).
 * Accept User selection of a device from list and store that selection in
 * SharedPreferences.
 */
public class BluetoothDiscoveryActivity extends Activity {

	private static final String TAG = "BluetoothDiscoveryActivity";
	protected static final int REFRESH = 0;
	private static final String HIDALGO_BLUETOOH_ADR_PREFIX = "00:07:80";
	private boolean mVerbose = false;

	ListView lstDevices;
	Button btnScan;
	Button btnCancel;
	LinearLayout pnlScanning;

	private String m_SelectedAddress;

	private Map<String, BluetoothDevice> m_AvailableDevices = new HashMap<String, BluetoothDevice>();
	private ArrayAdapter<String> m_DeviceListAdapter; // names of found devices
	private List<Short> m_RssiValues = new ArrayList<Short>();

	private BluetoothAdapter mBluetooth = BluetoothAdapter.getDefaultAdapter();

	public static final String PREFS = "PSM_PREFS";
	public static final String SELECTED_DEVICE_KEY = "SELECTED_DEVICE_ADDRESS";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.setContentView(R.layout.select_device);

		// Load the saved address of last device that was selected.
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
		m_SelectedAddress = settings.getString(SELECTED_DEVICE_KEY, "");

		// Setup listeners to scanForDevices(), cancel(), or accept selection.
		loadViews();
	}

	@Override
	public void onStop() {
		super.onStop();

		unregisterReceiver(mReceiver);
	}

	// onResume() always and must begin after onCreate.
	@Override
	public void onResume() {
		super.onResume();

		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister
												// during onDestroy

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(mReceiver, filter); // Don't forget to unregister
												// during onDestroy

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter); // Don't forget to unregister
												// during onDestroy

		scanForDevices();
	}

	@Override
	public void onPause() {
		super.onPause();

		mBluetooth.cancelDiscovery();
	}

	private void scanForDevices() {
		this.m_AvailableDevices.clear();
		this.m_RssiValues.clear();
		m_DeviceListAdapter.clear();
		/*
		 * Remember to register for ACTION_XYZ for notifications of start, find,
		 * finish
		 */
		mBluetooth.startDiscovery();
	}

	/*- SAS
	 * Setup the following listeners:
	 * 1. If the User clicks on an item on the device-list, save the device address
	 * in the SharedPreferences object, then finish this activity.     
	 * 2. Setup a button that, when clicked, searches for all devices, using
	 * scanForDevices()
	 * 3. A Cancel button to cancel scan and finish this activity.  
	 */
	private void loadViews() {
		// List of available Hildago devices
		lstDevices = (ListView) this.findViewById(R.id.lstDevices);
		m_DeviceListAdapter = new ArrayAdapter<String>((Context) this, android.R.layout.simple_list_item_1);
		lstDevices.setAdapter(m_DeviceListAdapter);
		lstDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			// User has selected a devices from list of available
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String name = m_DeviceListAdapter.getItem(position);
				BluetoothDevice device = m_AvailableDevices.get(name);

				m_SelectedAddress = device.getAddress();

				// Update the stored preferences
				SharedPreferences settings = getSharedPreferences(PREFS, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(SELECTED_DEVICE_KEY, m_SelectedAddress);
				editor.commit();

				finish();
			}
		});

		pnlScanning = (LinearLayout) this.findViewById(R.id.pnlScanning);
		pnlScanning.setVisibility(View.GONE);

		// Button to close selection view
		btnCancel = (Button) this.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				BluetoothDiscoveryActivity.this.finish();
			}
		});

		// Button to open the Bluetooth settings to pair new devices
		btnScan = (Button) this.findViewById(R.id.btnScan);
		btnScan.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				scanForDevices();
			}
		});
	}// loadViews

	/*
	 * Create a BroadcastReceiver for ACTION_XYZ that simply puts acceptable
	 * (Hidalgo type, strong signal) devices into a (filtered) list of
	 * "available" devices that later will be exposed to the User for selection.
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
				String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);

				// Determine if this is a Hidalgo device
				// Log.v("ME", device.getAddress());// show any device for debug
				if (device.getAddress().startsWith(HIDALGO_BLUETOOH_ADR_PREFIX)) {
					Log.i("Hidalgo Device Found", String.format("%s(%s):%d", name, device.getAddress(), rssi));

					/*
					 * Add the name and address to an array adapter to show in a
					 * ListView but only if signal strength > rssi. Insert to
					 * replace any device with a weaker signal
					 */
					int ndx;
					for (ndx = 0; ndx < m_RssiValues.size(); ++ndx) {
						if (rssi > m_RssiValues.get(ndx)) {
							break;
						}
					}

					m_RssiValues.add(ndx, rssi);
					m_DeviceListAdapter.insert(device.getName(), ndx);
					m_AvailableDevices.put(device.getName(), device);
				}

			} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				pnlScanning.setVisibility(View.VISIBLE);
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				pnlScanning.setVisibility(View.GONE);
			}
		}
	};

	public static BluetoothDevice getLastBluetoothDevice(Context context, BluetoothAdapter blueadapter, String key, String prefs) {

		// Get previous bluetooth device address
		SharedPreferences settings = context.getSharedPreferences(prefs, 0);
		String defaultblueaddress = "";
		String lastblueaddress = settings.getString(key, defaultblueaddress);

		BluetoothDevice bluedevice = null;
		if (BluetoothAdapter.checkBluetoothAddress(lastblueaddress)) {
			bluedevice = blueadapter.getRemoteDevice(lastblueaddress);
			reportOnBluetoothDevice(bluedevice);
		}

		return bluedevice;
	}

	public static void reportOnBluetoothDevice(BluetoothDevice bluedevice) {
		msgLog(String.format("Connecting to %s [%s]", bluedevice.getName(), bluedevice.getAddress()), 1);
	}

	private static void msgLog(String msg, int val) {
		if (0 <= val) {
			Log.v(TAG, msg);
		}
	}
}// ~
