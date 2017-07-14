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
package scout.edu.mit.ll.nics.android.formgen;

import com.google.android.gms.maps.model.LatLng;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import scout.edu.mit.ll.nics.android.BluetoothLRF;
import scout.edu.mit.ll.nics.android.LRF_HV;
import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.OnLRFDataListener;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.utils.Constants;
import scout.edu.mit.ll.nics.android.utils.Intents;
import scout.edu.mit.ll.nics.android.utils.Constants.NavigationOptions;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.MGRSCoord;
import gov.nasa.worldwind.geom.coords.UTMCoord;

public class FormCoordinate extends FormWidget implements OnLRFDataListener {
	
	private DataManager mDataManager;
	private BluetoothLRF mBluetoothLRF;

	private AlertDialog mAlertDialog;
	private ProgressDialog mLRFDialog;

	private boolean btReceiverRegistered;
	private IntentFilter mBTReceiverFilterDisconnect;
	private IntentFilter mBTReceiverFilterConnect;
	
	private EditText mLatitudeInput;
	private EditText mLongitudeInput;
	private EditText mOtherInput;
	
	private ImageButton mLRFButton;
	private Button mMapButton;
	private ImageButton mMyLocationButton;
	
	private TextView mFormCoordinateTitle;
	
	public FormCoordinate(FragmentActivity context, String name, String displayText, boolean enabled, OnFocusChangeListener listener, Fragment fragment) {
		super(context, name, displayText,fragment);

		mEnabled = enabled;
		
		mDataManager = DataManager.getInstance(context);
		mLayoutInflater.inflate(R.layout.form_coordinate, mLayout);
		
		mFormCoordinateTitle = (TextView) mLayout.findViewById(R.id.formCoordinateTitle);
		mFormCoordinateTitle.setText(getDisplayText());
		
		mLatitudeInput = (EditText) mLayout.findViewById(R.id.formCoordinateLatitudeInput);
		mLongitudeInput = (EditText) mLayout.findViewById(R.id.formCoordinateLongitudeInput);
		mOtherInput = (EditText) mLayout.findViewById(R.id.formCoordinateOtherInput);
		
		mLRFButton = (ImageButton) mLayout.findViewById(R.id.formCoordinateLRF);
		mMapButton = (Button) mLayout.findViewById(R.id.formCoordinateMap);
		mMyLocationButton = (ImageButton) mLayout.findViewById(R.id.formCoordinateMyLocation);
		
		mLRFButton.setOnClickListener(lrfClickListener);
		mMapButton.setOnClickListener(mapClickListener);
		mMyLocationButton.setOnClickListener(myLocationClickListener);
		
		mBluetoothLRF = BluetoothLRF.getInstance(mContext, this);
			if (mDataManager.isLRFEnabled()) {
			mLRFButton.setVisibility(View.VISIBLE);
			mBluetoothLRF.setOnLRFListener(this);

			mBTReceiverFilterDisconnect = new IntentFilter(Intents.nics_BT_DISCONNECT);
			mBTReceiverFilterConnect = new IntentFilter(Intents.nics_BT_CONNECT);

			if (!btReceiverRegistered) {
				mContext.registerReceiver(btSRReceiver, mBTReceiverFilterDisconnect);
				mContext.registerReceiver(btSRReceiver, mBTReceiverFilterConnect);
				btReceiverRegistered = true;
			}

			if (!mBluetoothLRF.isConnectedToDevice() && mBluetoothLRF.isBluetoothAdapterEnabled()) {

				mBluetoothLRF.cancelConnect();
				mLRFDialog = ProgressDialog.show(mContext, mContext.getString(R.string.reconnecting_bt_device), mContext.getString(R.string.reconnecting_to_device, mBluetoothLRF.getName()), true, true);
				mBluetoothLRF.findBT();
			}
		} else {
		mBluetoothLRF.closeBT();
		mLRFButton.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void setValue(String value) {
		double lat = -360;
		double lng = -360;
		
		if(mDataManager.getCoordinateRepresentation() == Constants.LOCATION_MGRS) {
			mOtherInput.setHint(R.string.markup_mgrs);
		} else if(mDataManager.getCoordinateRepresentation() == Constants.LOCATION_UTM) {
			mOtherInput.setHint(R.string.markup_utm);
		}
		
		if(mDataManager.getCoordinateRepresentation() < 3) {
			mLatitudeInput.setVisibility(View.VISIBLE);
			mLongitudeInput.setVisibility(View.VISIBLE);
			mOtherInput.setVisibility(View.GONE);
		} else {
			mLatitudeInput.setVisibility(View.GONE);
			mLongitudeInput.setVisibility(View.GONE);
			mOtherInput.setVisibility(View.VISIBLE);
		}
				
		if(value != null && !value.isEmpty()) {
			String[] coordinateArray = value.split(";");
			
			if(coordinateArray.length == 2) {
				try {
					
					if(!coordinateArray[0].equals("null") && !coordinateArray[1].equals("null)")) {
						lat = Double.valueOf(coordinateArray[0]);
						lng = Double.valueOf(coordinateArray[1]);
					}
					
					if(mDataManager.getCoordinateRepresentation() == Constants.LOCATION_MGRS) {
						MGRSCoord mgrs = MGRSCoord.fromLatLon(Angle.fromDegrees(lat), Angle.fromDegrees(lng));
						mOtherInput.setText(mgrs.toString());
						mLatitudeInput.setText(String.valueOf(lat));
						mLongitudeInput.setText(String.valueOf(lng));
					} else if(mDataManager.getCoordinateRepresentation() == Constants.LOCATION_UTM) {
						UTMCoord utm = UTMCoord.fromLatLon(Angle.fromDegrees(lat), Angle.fromDegrees(lng));
						mOtherInput.setText(utm.toString());
						mLatitudeInput.setText(String.valueOf(lat));
						mLongitudeInput.setText(String.valueOf(lng));
					} else {
						mLatitudeInput.setText(Location.convert(lat, mDataManager.getCoordinateRepresentation()));
						mLongitudeInput.setText(Location.convert(lng, mDataManager.getCoordinateRepresentation()));
					}
				} catch (Exception e) {
				}
			}
		}
		
		if(lat == -360 || lng == -360){
			mOtherInput.setText("");
			mLatitudeInput.setText("");
			mLongitudeInput.setText("");
		}
	}
	
	@Override
	public String getValue() {
		int currentRepresentation = -1;
		double lat = -360;
		double lng = -360;
		try {
			MGRSCoord mgrs = MGRSCoord.fromString(mOtherInput.getText().toString());

			lat = mgrs.getLatitude().degrees; 
			lng = mgrs.getLongitude().degrees;
			currentRepresentation = Constants.LOCATION_MGRS;
		} catch (Exception e) {
		}
		
		if(currentRepresentation == -1) {
			try {
				String[] utmParts = mOtherInput.getText().toString().split(" ");
				if(utmParts[1].equals("N")) {
					utmParts[1] = AVKey.NORTH;
				} else if(utmParts[1].equals("S")) {
					utmParts[1] = AVKey.SOUTH;
				}
	
				
				UTMCoord utm = UTMCoord.fromUTM(Integer.valueOf(utmParts[0]), utmParts[1], Double.parseDouble(utmParts[2].substring(0, utmParts[2].length() - 1)), Double.parseDouble(utmParts[3].substring(0, utmParts[3].length() - 1)));
				
				lat = utm.getLatitude().degrees;
				lng = utm.getLongitude().degrees;
				currentRepresentation = Constants.LOCATION_UTM; 
			} catch (Exception e) {
			}
		}
		
		if(currentRepresentation == -1) {
			try {
				lat = Location.convert(mLatitudeInput.getText().toString());
				lng = Location.convert(mLongitudeInput.getText().toString());
				currentRepresentation = Location.FORMAT_DEGREES;
			} catch (Exception e) {
			}
		}
		
		if(currentRepresentation == -1) {
			try {
				String[] coordinateArray = mOtherInput.getText().toString().split(";");
				
				if(coordinateArray.length == 2 && !coordinateArray[0].equals("null") && !coordinateArray[1].equals("null)")) {
					lat = Double.valueOf(coordinateArray[0]);
					lng = Double.valueOf(coordinateArray[1]);
					currentRepresentation = Location.FORMAT_DEGREES;
				}
			} catch (Exception e) {
			}
		}
		
		String value = lat + ";" + lng;
		
		if(lat == -360 || lng == -360) {
			value = "";
		}
		if(currentRepresentation != mDataManager.getCoordinateRepresentation()) {
			setValue(value);
		}
		
		return value;
	}

	@Override
	public void setEditable(boolean editable) {
		mLatitudeInput.setEnabled(editable);
		mLongitudeInput.setEnabled(editable);
		mOtherInput.setEnabled(editable);
		
		if(editable) {
			mMyLocationButton.setVisibility(View.VISIBLE);
			mMapButton.setVisibility(View.VISIBLE);
			mLRFButton.setVisibility(View.VISIBLE);
			mLatitudeInput.setTextColor(Color.WHITE);
			mLongitudeInput.setTextColor(Color.WHITE);
			mOtherInput.setTextColor(Color.WHITE);
		} else {
			if (btReceiverRegistered) {
				mBluetoothLRF.cancelConnect();
				mContext.unregisterReceiver(btSRReceiver);
				btReceiverRegistered = false;
			}
			
			mMyLocationButton.setVisibility(View.GONE);
			mMapButton.setVisibility(View.GONE);
			mLRFButton.setVisibility(View.GONE);
			mLatitudeInput.setTextColor(Color.GRAY);
			mLongitudeInput.setTextColor(Color.GRAY);
			mOtherInput.setTextColor(Color.GRAY);
		}
	}
	
	private OnClickListener myLocationClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mDataManager.getCoordinateRepresentation() < 3) {
				if(!Double.isNaN(mDataManager.getMDTLatitude())) {
					mLatitudeInput.setText(Location.convert(mDataManager.getMDTLatitude(), mDataManager.getCoordinateRepresentation()));
				} else {
					mLatitudeInput.setText("0.0");
				}
				
				if(!Double.isNaN(mDataManager.getMDTLongitude())) {
					mLongitudeInput.setText(Location.convert(mDataManager.getMDTLongitude(), mDataManager.getCoordinateRepresentation()));
				} else {
					mLongitudeInput.setText("0.0");
				}
			} else if(mDataManager.getCoordinateRepresentation() == Constants.LOCATION_MGRS) {
				MGRSCoord mgrs;
				if(!Double.isNaN(mDataManager.getMDTLatitude()) && !Double.isNaN(mDataManager.getMDTLongitude())) {
					mgrs = MGRSCoord.fromLatLon(Angle.fromDegrees(mDataManager.getMDTLatitude()), Angle.fromDegrees(mDataManager.getMDTLongitude()));
				} else {
					mgrs = MGRSCoord.fromLatLon(Angle.fromDegrees(0), Angle.fromDegrees(0));
				}
				mOtherInput.setText(mgrs.toString());
			} else if(mDataManager.getCoordinateRepresentation() == Constants.LOCATION_UTM) {
				UTMCoord utm;
				if(!Double.isNaN(mDataManager.getMDTLatitude()) && !Double.isNaN(mDataManager.getMDTLongitude())) {
					utm = UTMCoord.fromLatLon(Angle.fromDegrees(mDataManager.getMDTLatitude()), Angle.fromDegrees(mDataManager.getMDTLongitude()));
				} else {
					utm = UTMCoord.fromLatLon(Angle.fromDegrees(0), Angle.fromDegrees(0));
				}
				mOtherInput.setText(utm.toString());
			}
		}
	};
	
	private BroadcastReceiver btSRReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, final Intent intent) {
			try {
				if(mLRFDialog != null && mLRFDialog.isShowing()) {
					mLRFDialog.dismiss();
				}
				mLRFDialog = null;
				
				if(mAlertDialog != null && mAlertDialog.isShowing()) {
					mAlertDialog.dismiss();
				}
				mAlertDialog = null;
				
				Bundle extras = intent.getExtras();
				if(extras != null && extras.getBoolean("notpaired")) {
			    	Intent pairIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
			        mContext.startActivityForResult(pairIntent, 500);
				} else if(intent.getAction().equals(Intents.nics_BT_DISCONNECT)) {		
					if(mBluetoothLRF.getPairedDevice() != null){
						mAlertDialog = new AlertDialog.Builder(mContext).create();
						mAlertDialog.setTitle(mContext.getString(R.string.disconnected_device, mBluetoothLRF.getName()));
						mAlertDialog.setMessage(mContext.getString(R.string.disconnected_device_desc));
						mAlertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								dialog = null;
								
								mLRFDialog = ProgressDialog.show(mContext, mContext.getString(R.string.reconnecting_bt_device), mContext.getString(R.string.reconnecting_to_device, mBluetoothLRF.getName()), true, true);
								mBluetoothLRF.findBT();
							}
						});
						mAlertDialog.show();
					} else if(mBluetoothLRF.isBluetoothAdapterEnabled()) {
						Intent btDisconnectIntent = new Intent();
						btDisconnectIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
						btDisconnectIntent.setAction(Intents.nics_BT_DISCONNECT);
						btDisconnectIntent.putExtra("notpaired", true);
						
		                mContext.sendBroadcast(btDisconnectIntent);
					}
				} else if(intent.getAction().equals(Intents.nics_BT_CONNECT)) {
					mAlertDialog = new AlertDialog.Builder(mContext).create();
					mAlertDialog.setTitle(mContext.getString(R.string.connected_device, mBluetoothLRF.getName()));
					mAlertDialog.setMessage(mContext.getString(R.string.connected_device_desc));
					mAlertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mAlertDialog.dismiss();
							mAlertDialog = null;
						}
					});
					mAlertDialog.show();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private OnClickListener mapClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			MainActivity main = (MainActivity) mContext;
			if(mDataManager.getTabletLayoutOn()){
					if( main.mMapMarkupFragment != null){
					LatLng coord = main.mMapMarkupFragment.getReportMarkerCoordinates();
					mLatitudeInput.setText(Location.convert(coord.latitude, mDataManager.getCoordinateRepresentation()));
					mLongitudeInput.setText(Location.convert(coord.longitude, mDataManager.getCoordinateRepresentation()));
					
					if(coord.latitude == 0 && coord.longitude == 0 ){
						Toast toast = Toast.makeText(mContext, mContext.getString(R.string.open_map_and_tap_to_place_marker), Toast.LENGTH_LONG);
						toast.show();
					}
				}else{
					Toast toast = Toast.makeText(mContext, mContext.getString(R.string.open_map_and_tap_to_place_marker), Toast.LENGTH_LONG);
					toast.show();
				}
			}else{
				main.openMapLocationPicker();
			}
		}
	};	
	
	private OnClickListener lrfClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(mBluetoothLRF.isConnectedToDevice()) {
				mLRFDialog = ProgressDialog.show(mContext, mContext.getString(R.string.collecting_lrf_data), mContext.getString(R.string.press_fire_to_capture_data), true, true);
			} else if(mBluetoothLRF.getPairedDevice() != null){
				mAlertDialog = new AlertDialog.Builder(mContext).create();
				mAlertDialog.setTitle(mContext.getString(R.string.disconnected_device, mBluetoothLRF.getName()));
				mAlertDialog.setMessage(mContext.getString(R.string.disconnected_device_desc));
				mAlertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						dialog = null;
						
						mLRFDialog = ProgressDialog.show(mContext, mContext.getString(R.string.reconnecting_bt_device), mContext.getString(R.string.reconnecting_to_device, mBluetoothLRF.getName()), true, true);
						mBluetoothLRF.findBT();
					}
				});
				mAlertDialog.show();
			} else {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
				intent.setAction(Intents.nics_BT_DISCONNECT);
				intent.putExtra("notpaired", true);
				
                mContext.sendBroadcast(intent);
			}
		}
	};

	@Override
	public void onLRFData(LRF_HV data) {
		final double[] reckoned = data.getReckonedLocation((float)mDataManager.getMDTLatitude(), (float)mDataManager.getMDTLongitude(), (float)mDataManager.getMDTAltitude());
		
		if(mLRFDialog != null && mLRFDialog.isShowing()) {
			mContext.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					setValue(reckoned[0] + ";" + reckoned[1]);
				}
			});
			
			mLRFDialog.dismiss();
			mLRFDialog = null;
		}
	}
}
