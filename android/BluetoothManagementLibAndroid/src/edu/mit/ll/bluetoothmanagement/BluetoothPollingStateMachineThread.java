package edu.mit.ll.bluetoothmanagement;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;


/**
 * This class has methods to connect, open, receive and send data over a
 * Bluetooth connection. These methods always return a state that indicates of
 * what the Bluetooth is capable after the method completes.
 * 
 * This class is a Thread whose run() method implements a state machine.
 * 
 * Its run() method is always checking to see if it is running, and if so, then
 * it processes the next state.
 * 
 * It appears that this class can be kicked off by the invocation of run() which
 * starts a polling loop that chooses the processing depending upon the state.
 * At startup the state variable has value, STATE_NEEDS_TO_CONNECT. Therefore
 * the loop inside run() responds by kicking off the appropriate method to open
 * and monitor a Bluetooth connection.
 * 
 * @author Steven Schwartz, cleanup and document.
 * 
 * @author Mark This class initializes an Android RFCOMM connection with a known
 *         BT device
 * 
 */

public class BluetoothPollingStateMachineThread extends Thread {

    private static final String TAG = "BluetoothPollingStateMachineThread";

    public static final UUID COM_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805f9b34fb");

    public static final int DEFAULT_SEM_IDENTIFIER = -1;

    /*- 
     * STATE_NEEDS_TO_CONNECT = there is NO Bluetooth connection. 
     * STATE_IS_OPEN = Bluetooth connection established, but no streams.  
     * STATE_STREAMS_ARE_OPEN = Bluetooth is connected to input and output streams.  
     * STATE_THERE_ARE_DATA_TO_SEND = Data have been received over Bluetooth and ready to broadcast.
     */
    private static final String STATE_NEEDS_TO_CONNECT = "STATE_NEEDS_TO_CONNECT";
    private static final String STATE_IS_CONNECTED = "STATE_IS_OPEN";
    private static final String STATE_STREAMS_ARE_OPEN = "STATE_STREAMS_ARE_OPEN";
    private static final String STATE_THERE_ARE_DATA_TO_SEND = "STATE_THERE_ARE_DATA_TO_SEND";

    private String m_sState = STATE_NEEDS_TO_CONNECT;

    private static int s_iTempSender = 0;

    private Context m_Context;

    // Optional m_iSEMID used in callbacks to identify SEM.
    private int m_iSEMID;

    private BluetoothAdapter m_BluetoothAdapter;
    private BluetoothDevice m_BluetoothDevice = null;
    private BluetoothSocket m_BluetoothSocket = null;
    private InputStream m_BluetoothInputStream = null;// from the blue device
    private OutputStream m_BluetoothOutputStream = null;
    private boolean m_qConnected = false;
    private boolean m_qRunning = true;// set to false to kill BT polling.

    private byte[] m_bBuffer = new byte[4096];

    private DataParser m_DataParser = null;

    private ReentrantReadWriteLock m_ReentrantReadWriteLock = new ReentrantReadWriteLock();

    private List<BluetoothThreadListener> m_arrBluetoothThreadListeners = new ArrayList<BluetoothThreadListener>();

    private LinkedList<String> m_arrStringCommands;

    /* This object is NOT used. This runnable waits 10 seconds, then checks to
     * see if BT is connected. If connected, then thread ends. If not connected,
     * then the BluetoothAdapter is turned off and then on. After turn on, this
     * method loops forever until receiving confirmation of STATE_ON. */
    Runnable runnableToRestartBluetooth = new Runnable() {
        public void run() {

            // Clean up resources first if necessary
            // cleanupResources();

            // Wait 10 seconds
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            if (!m_qConnected) {
                Log.d("Bluetooth Thread", "Bluetooth timed out. Resetting...");
                // Tell the BT adapter to turn off.
                m_BluetoothAdapter.disable();
                // Loop until the BT adapter finally turns off.
                while (m_BluetoothAdapter.getState() != BluetoothAdapter.STATE_OFF) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                Log.d("Bluetooth Thread", "Restarting bluetooth...");
                // Tell the BT adapter to turn on.
                m_BluetoothAdapter.enable();
                // Loop until the BT adapter turns on.
                while (m_BluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }// while
            }// if (!m_qConnected)
        }// run()
    };// new Runnable

    // sas This Tread is never, ever started. So it is useless.
    Thread timeoutThread = new Thread(runnableToRestartBluetooth);


    public static interface BluetoothThreadListener {
        void onStateChange(String prevState, String newState);
    }


    // Base CTOR (no SEM identifier)
    public BluetoothPollingStateMachineThread(
            Context context,
            DataParser dataParser,
            BluetoothDevice bluedevice) {
        // Chain to CTOR with default value for SEM identifier
        this(context, dataParser, bluedevice, DEFAULT_SEM_IDENTIFIER);
    }


    // CTOR with SEM identifier
    public BluetoothPollingStateMachineThread(
            Context context,
            DataParser dataParser,
            BluetoothDevice bluetoothdevice,
            int iSEMID) {
        m_iSEMID = iSEMID;
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        m_Context = context;
        m_BluetoothDevice = bluetoothdevice;
        m_DataParser = dataParser;
        // following must happen in calling routine
        // m_DataParser = new HidalgoDataParser(context, iSEMID);
        m_arrStringCommands = new LinkedList<String>();
    }


    public Boolean isRunning() {
        return m_qRunning;
    }


    public void quit() {
        // cleanupResources();
        m_qRunning = false;
    }


    public String getCurrentState() {
        return m_sState;
    }


    public void addListener(BluetoothThreadListener listener) {
        if (!m_arrBluetoothThreadListeners.contains(listener)) {
            m_arrBluetoothThreadListeners.add(listener);
        }
    }


    public void removeListener(BluetoothThreadListener listener) {
        if (m_arrBluetoothThreadListeners.contains(listener)) {
            m_arrBluetoothThreadListeners.remove(listener);
        }
    }


    /*-
     * If there is a BluetoothDevice, try to create an RF socket and then
     * connect. If this successful, return STATE_OPEN.
     * 
     * If an Exception occurs during these processes, then
     * cleanUpResources() and return STATE_CONNECT.  
     */
    private String stateConnect() {

        Log.d("Bluetooth Thread", "About to connect");

        // Clean up resources first if necessary
        // cleanupResources();

        /*-
         * If there is a BluetoothDevice, try to create an RF socket and then
         * connect. If this successful, return STATE_OPEN.
         * 
         * If an Exception occurs during these processes, then
         * cleanUpResources() and return STATE_CONNECT.  
         */
        try {
            m_ReentrantReadWriteLock.writeLock().lock();

            m_BluetoothAdapter.cancelDiscovery();

            /* If there is a BluetoothDevice, create an RF socket; if not return
             * STATE_CONNECT. */
            if (m_BluetoothDevice != null) {
                if (m_BluetoothDevice.getName() != null) {
                    Log.d("Bluetooth Thread", m_BluetoothDevice.getName());
                }
                m_BluetoothSocket = m_BluetoothDevice
                        .createRfcommSocketToServiceRecord(COM_UUID);
            } else {
                m_ReentrantReadWriteLock.writeLock().unlock();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                return STATE_NEEDS_TO_CONNECT;
            }

            /* If there is a BluetoothSocket, then connect; if not then return
             * STATE_CONNECT. */
            if (m_BluetoothSocket != null) {
                Log.d("Bluetooth Thread", "About to connect");
                // timeoutThread.start();
                m_BluetoothSocket.connect();
                m_qConnected = true;
                // timeoutThread.interrupt();
                // timeoutThread.join();
                Log.d("Bluetooth Thread", "connected");
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                m_ReentrantReadWriteLock.writeLock().unlock();
                return STATE_NEEDS_TO_CONNECT;
            }
            m_ReentrantReadWriteLock.writeLock().unlock();

            Log.d("Bluetooth Thread", "Connect success");

            return STATE_IS_CONNECTED;

        } catch (Exception e) {
            Log.d("Bluetooth Thread", "Connect failed");
            Log.e("Bluetooth Thread", String.format(
                    "Could not open socket to Bluetooth device: %s",
                    e.getMessage()), e);
            // try { timeoutThread.join(); } catch (InterruptedException e2) { }
            m_ReentrantReadWriteLock.writeLock().unlock();
            cleanupResources();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return STATE_NEEDS_TO_CONNECT;
        }
    }


    /* Prepare an open Bluetooth socket for read and write. If successful,
     * return STATE_COLLECT. If unsuccessful, return STATE_CONNECT. */
    private String stateOpenStreams() {
        try {
            if (m_BluetoothSocket != null) {
                m_BluetoothInputStream = m_BluetoothSocket.getInputStream();
                m_BluetoothOutputStream = m_BluetoothSocket.getOutputStream();
                // return STATE_SEND;
                return STATE_STREAMS_ARE_OPEN;

            } else {
                m_BluetoothInputStream = null;
                m_BluetoothOutputStream = null;
                return STATE_NEEDS_TO_CONNECT;
            }
        } catch (IOException e) {
            Log.e("Bluetooth Thread",
                    String.format("Could not open input stream: %s",
                            e.getMessage()), e);
            cleanupResources();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return STATE_NEEDS_TO_CONNECT;
        }
    }


    /* Put a HidalgoCommand into the arraylist in preparation for later sending
     * it over Bluetooth. */
    public void sendString(String command) {
        // Send a command
        Log.i("Bluetooth Thread",
                String.format("Sending Command String: %s", command));
        m_arrStringCommands.add(command);
    }


    /* Get the latest HidalgoCommand from the arraylist (and delete it there)
     * and send this command over Bluetooth. */
    private String stateSend() {
        try {
            // Switch to full disclosure (test)
            String stringCommand = m_arrStringCommands.remove();
            if (stringCommand != null) {
                m_BluetoothOutputStream.write(stringCommand.getBytes());
                Log.i("Bluetooth Thread", String.format(
                        "JRS: Sending '%s' to SEM", stringCommand));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return STATE_STREAMS_ARE_OPEN;
    }


    /* Read data from the Bluetooth input stream, put the data into a
     * HidalgoDataParser where it is parsed and broadcast. */
    private String stateReceive() {
        int size;
        try {
            size = m_BluetoothInputStream.read(m_bBuffer);
            s_iTempSender++;

            // Parse and then broadcast result as an Intent.
            m_DataParser.processBuffer(m_bBuffer, size);

            // if (tempSender % 100 == 0) return STATE_SEND;
            if (m_arrStringCommands.isEmpty())
                return STATE_STREAMS_ARE_OPEN;
            else
                return STATE_THERE_ARE_DATA_TO_SEND;
        } catch (IOException e) {
            // Log.d("Bluetooth Thread", "Collect failed");
            Log.e("Bluetooth Thread",
                    String.format("Failed to read data: %s", e.getMessage()), e);

            cleanupResources();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return STATE_NEEDS_TO_CONNECT;
        }
    }


    /* This thread runs a polling loop within the thread. Within this polling
     * loop when the state, m_sState, changes two things happen: 1, all the
     * listeners are notified; and 2, the appropriate "state" method is invoked. */
    public void run() {

        String lastState = "[None]";

        // Set thread name for easier debugging
        setName("BluetoothPollingStateMachineThread-" +
                m_BluetoothDevice.getName());

        /* SAS: appears to be a polling loop within a thread. In this loop
         * Bluedata are read by stateCollect(). The String m_State has
         * indications of new data so if m_State is different from lastState,
         * the new data are processed. */
        while (isRunning()) {
            try {
                /* If the state changed, then let all the HidalgoThreadListeners
                 * do their thing */
                if (!lastState.equals(m_sState)) {
                    Log.i("Bluetooth Thread", String.format(
                            "Changing States: %s => %s", lastState, m_sState));
                    for (BluetoothThreadListener l : m_arrBluetoothThreadListeners) {
                        l.onStateChange(lastState, m_sState);
                    }
                }

                lastState = m_sState;

                /* State machine to open and maintain BT connection, process any
                 * received data, notify listeners of state changes, and to send
                 * any data placed in arraylist. */
                if (m_sState.equals(STATE_NEEDS_TO_CONNECT)) {
                    m_sState = stateConnect();
                } else if (m_sState.equals(STATE_IS_CONNECTED)) {
                    m_sState = stateOpenStreams();
                } else if (m_sState.equals(STATE_STREAMS_ARE_OPEN)) {
                    m_sState = stateReceive();
                } else if (m_sState.equals(STATE_THERE_ARE_DATA_TO_SEND)) {
                    m_sState = stateSend();
                }

            } catch (Exception e) {
                Log.w("Bluetooth Thread",
                        String.format("Error in event loop: %s", e), e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                m_sState = STATE_NEEDS_TO_CONNECT;
            }
        }

        cleanupResources();
    }


    /* Close input/output streams, then the Bluetooth socket itself. */
    private void cleanupResources() {
        // If the bluetooth socket is open, then close and destroy it
        m_ReentrantReadWriteLock.writeLock().lock();

        if (m_BluetoothSocket != null) {

            try {
                m_BluetoothSocket.getOutputStream().close();
            } catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				m_BluetoothSocket.getInputStream().close();
			} catch (Exception ex) {
				ex.printStackTrace();
            }

            try {
                Log.d("Bluetooth Thread", "About to close");
                m_BluetoothSocket.close();
                Log.d("Bluetooth Thread", "closed");
            } catch (IOException e1) {
                Log.w("BluetoothPollingStateMachineThread.cleanupResources",
                        "Failed to close bluetooth socket: " + e1.getMessage(),
                        e1);
            }
            m_BluetoothSocket = null;
        }

        m_qConnected = false;
        m_ReentrantReadWriteLock.writeLock().unlock();
    }


}// ~
