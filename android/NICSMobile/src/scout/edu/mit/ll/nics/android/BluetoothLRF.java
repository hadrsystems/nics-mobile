/*|~^~|Copyright (c) 2008-2016, Massachusetts Institute of Technology (MIT)
 |~^~|All rights reserved.
 |~^~|
 |~^~|Redistribution and use in source and binary forms, with or without
 |~^~|modification, are permitted provided that the following conditions are met:
 |~^~|
 |~^~|-1. Redistributions of source code must retain the above copyright notice, this
 |~^~|ist of conditions and the following disclaimer.
 |~^~|
 |~^~|-2. Redistributions in binary form must reproduce the above copyright notice,
 |~^~|this list of conditions and the following disclaimer in the documentation
 |~^~|and/or other materials provided with the distribution.
 |~^~|
 |~^~|-3. Neither the name of the copyright holder nor the names of its contributors
 |~^~|may be used to endorse or promote products derived from this software without
 |~^~|specific prior written permission.
 |~^~|
 |~^~|THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 |~^~|AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 |~^~|IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 |~^~|DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 |~^~|FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 |~^~|DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 |~^~|SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 |~^~|CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 |~^~|OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 |~^~|OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\*/
/**
 *
 */
package scout.edu.mit.ll.nics.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.UUID;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.LTITSentence;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import scout.edu.mit.ll.nics.android.utils.Constants;
import scout.edu.mit.ll.nics.android.utils.Intents;

public class BluetoothLRF {
	
	private static BluetoothLRF mInstance;

	public static final int REQUEST_ENABLE = 0;

	private static OnLRFDataListener mListener;
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mmDevice;
	private BluetoothSocket mmSocket;
	private OutputStream mmOutputStream;
	private InputStream mmInputStream;
	private boolean stopWorker;
	private Thread workerThread;
	private static Activity mContext;
	protected LTITSentence mSentence;
	
	private boolean mConnectedToDevice = false;

	private AsyncTask<Void, Boolean, Boolean> mBluetoothTask;
	
	public BluetoothLRF() {
    	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
    public static BluetoothLRF getInstance (final Activity context, final OnLRFDataListener listener) {
        if (mInstance == null) {
            mContext = context;
            
            if(listener != null) {
            	mListener = listener;
            }

            mInstance = new BluetoothLRF ();
        }

        return mInstance;
    }
    
	public void findBT()
	{
		mBluetoothTask = new BluetoothTask().execute();
	}

	private boolean pairDevice() {
		boolean out = false;
	    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
	    if(pairedDevices.size() > 0)
	    {
	        for(BluetoothDevice device : pairedDevices)
	        {
	        	if(device.getName().contains(Constants.nics_LRF_DEVICE_NAME)) {
	        		mmDevice = device;
		        	out = true;
	        	}
	        }
	    } 
	    
	    return out;
	}

	public boolean openBT() throws IOException
	{
		mBluetoothAdapter.cancelDiscovery();
		
		if(mmDevice == null) {
			pairDevice();
		} else {
			try {
			    UUID uuid = UUID.fromString(Constants.nics_BT_SERIALPORT_SERVICEID); //Standard SerialPortService ID
			    mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);        
			    mmSocket.connect();
			    mmOutputStream = mmSocket.getOutputStream();
			    mmInputStream = mmSocket.getInputStream();
			    beginListenForData();
			    return true;
			} catch (IOException e) {
				closeBT();
				
				if(mmDevice != null) {
	                mConnectedToDevice = false;
	                
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
					intent.setAction(Intents.nics_BT_DISCONNECT);
	                mContext.sendBroadcast(intent);
				}
				return false;
			}
		}
		return false;
	}

	void beginListenForData()
	{
	    stopWorker = false;
	    workerThread = new Thread(new Runnable()
	    {
	        public void run()
	        {                
	           while(!stopWorker)
	           {
	            	byte[] buffer = new byte[1024];
	            	int bytes;
	            	String end = "\n";
	            	StringBuilder curMsg = new StringBuilder();
	
	            	try {
						while (-1 != (bytes = mmInputStream.read(buffer))) {
						    curMsg.append(new String(buffer, 0, bytes, Charset.forName(Constants.nics_UTF8)));
						    int endIdx = curMsg.indexOf(end);
						    if (endIdx != -1) {
						        String fullMessage = curMsg.substring(0, endIdx + end.length());
						        curMsg.delete(0, endIdx + end.length());
						        // Now send fullMessage
						        
							     mSentence = (LTITSentence) SentenceFactory.getInstance().createParser(fullMessage.replace("\r\n", ""));
							     
							     if(mSentence.getMessageType().equals("HV")) {
							    	 LRF_HV lrfOut = new LRF_HV();
							    	 
							    	 lrfOut.setHorizontalDistanceValue(mSentence.getFieldDouble(1));
							    	 lrfOut.setHorizontalDistanceUnits(String.valueOf(mSentence.getFieldChar(2)));
							    	 
							    	 lrfOut.setAzimuthValue(mSentence.getFieldDouble(3));
							    	 lrfOut.setAzimuthUnits(String.valueOf(mSentence.getFieldChar(4)));

							    	 lrfOut.setInclinationValue(mSentence.getFieldDouble(5));
							    	 lrfOut.setInclinationUnits(String.valueOf(mSentence.getFieldChar(6)));
							    	 
							    	 lrfOut.setSlopeDistanceValue(mSentence.getFieldDouble(7));
							    	 lrfOut.setSlopeDistanceUnits(String.valueOf(mSentence.getFieldChar(8)));
							    	 
							    	 if(mListener != null) {
							    		 mListener.onLRFData(lrfOut);
							    	 }
							     }
						    }
						}
					} catch (Exception e) {
						stopWorker = true;
					}

	           }
	           
				if (stopWorker) {
					try {
						mmOutputStream.close();
						mmInputStream.close();
						mmSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					mConnectedToDevice = false;

					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
					intent.setAction(Intents.nics_BT_DISCONNECT);
	                mContext.sendBroadcast(intent);
				}
	        }
	    }, "BluetoothThread");

	    workerThread.start();
	}
	
	public LTITSentence getLatestData() {
		return mSentence;
	}
	
	public boolean isBluetoothAdapterEnabled() {
		if(mBluetoothAdapter != null) {
			return mBluetoothAdapter.isEnabled();
		}
		return false;
	}

	public void sendData() throws IOException
	{
	    String msg = "hello world";
	    msg += "\n";
	    mmOutputStream.write(msg.getBytes());
    	Log.e(Constants.nics_LRF_DEBUG_ANDROID_TAG, "Sent data to BT serial");
	}

	public void closeBT()
	{
	    stopWorker = true;
	    mConnectedToDevice = false;
	    
    	Log.e(Constants.nics_LRF_DEBUG_ANDROID_TAG, "Bluetooth closed");
	}
	
	 private class BluetoothTask extends AsyncTask<Void, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean connected = true;
		    if(mBluetoothAdapter == null)
		    {
		    	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		    }

		    try {
		    	if(mmOutputStream != null) {
		    		mmOutputStream.close();
		    	}
		    	if(mmInputStream != null) {
		    		mmInputStream.close();
		    	}
		    	if(mmSocket != null) {
		    		mmSocket.close();
		    	}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		    // check if adapter is still null
		    if(mBluetoothAdapter == null) {
		    	Log.e(Constants.nics_LRF_DEBUG_ANDROID_TAG, "Bluetooth not available");
		    	connected = false;
		    }

		    // check to see if adapter is enabled, if not start intent to ask user to enable it
		    if(mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled())
		    {
		    	connected = false;
		    	mmDevice = null;
		    }
		    
		    // if enabled try to connect to paired LRF device
		    if(connected) {
			    connected = pairDevice();
		    	Log.e(Constants.nics_LRF_DEBUG_ANDROID_TAG, "Bluetooth device found");
		    	
	    	    try {
			     	connected = openBT();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }

		    mConnectedToDevice = connected;

		    return connected;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			
			if(result) {
				intent.setAction(Intents.nics_BT_CONNECT);
			} else {
				intent.setAction(Intents.nics_BT_DISCONNECT);
			}
            mContext.sendBroadcast(intent);
		}

	 }

	public void setOnLRFListener(OnLRFDataListener listener) {
		mListener = listener;
	}

	public boolean isConnectedToDevice() {
		return mConnectedToDevice;
	}
	
	public void cancelConnect() {
		if(mBluetoothTask != null) {
			mBluetoothTask.cancel(true);
		}
	}

	public String getName() {
		if(mmDevice != null) {
			return mmDevice.getName();
		} else {
			return "";
		}
	}

	public BluetoothDevice getPairedDevice() {
		return mmDevice;
	}
}
