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
package scout.edu.mit.ll.nics.android.utils;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import scout.edu.mit.ll.nics.android.BluetoothLRF;
import scout.edu.mit.ll.nics.android.LRF_HV;
import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.OnLRFDataListener;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.dialogs.ColorPickerDialog;
import scout.edu.mit.ll.nics.android.dialogs.SymbolPickerDialog;
import scout.edu.mit.ll.nics.android.fragments.MapMarkupFragment;

public class MarkupCoordinateManager implements OnLRFDataListener {
	
	private MapMarkupFragment mFragment;
	private View mCoordinatesPanel;
	
	private View mCoordinateLayout1;
	private View mCoordinateLayout2;
	private View mCoordinateLayout3;
	
	private View mCoordinateCircleLayout;
	private View mCoordinateSymbolLayout;
	
	private EditText mLatitudeInput0;
	private EditText mLatitudeInput1;
	private EditText mLatitudeInput2;
	private EditText mLatitudeInput3;
	
	private EditText mLongitudeInput0;
	private EditText mLongitudeInput1;
	private EditText mLongitudeInput2;
	private EditText mLongitudeInput3;
	
	private EditText mRadiusInput;
	
	private ImageButton mLRFButton0;
	private ImageButton mLRFButton1;
	private ImageButton mLRFButton2;
	private ImageButton mLRFButton3;
	
	private ImageButton mMyLocationButton0;
	private ImageButton mMyLocationButton1;
	private ImageButton mMyLocationButton2;
	private ImageButton mMyLocationButton3;
	
	private ImageButton mSymbolButton;
	private ImageButton mLineButton;
	private ImageButton mRectangleButton;
	private ImageButton mTrapezoidButton;
	private ImageButton mCircleButton;
	
	private Button mLRFButton;
	private Button mSymbolPickerButton;
	private Button mColorPickerButton;
	private TextView mColorPickerLabel;

	private View mButtonsPanel;
	private ImageView mSymbolView;
	
	private int mCurrentShapeType;
	
	private DataManager mDataManager;
	private BluetoothLRF mBluetoothLRF;
	private ProgressDialog mLRFDialog;
	protected AlertDialog mAlertDialog;
	
	private int mLRFId;
	private boolean btReceiverRegistered;
	private IntentFilter mBTReceiverFilterDisconnect;
	private IntentFilter mBTReceiverFilterConnect;
	private Context mContext;
	
	public MarkupCoordinateManager(MapMarkupFragment owner, View view, DataManager manager) {
		mFragment = owner;
		mContext = owner.getActivity();
		
		mDataManager = manager;

		mLRFButton0 = (ImageButton) view.findViewById(R.id.markupLRF0);
		mLRFButton1 = (ImageButton) view.findViewById(R.id.markupLRF1);
		mLRFButton2 = (ImageButton) view.findViewById(R.id.markupLRF2);
		mLRFButton3 = (ImageButton) view.findViewById(R.id.markupLRF3);

        mBluetoothLRF = BluetoothLRF.getInstance((MainActivity)mContext, this);
		if(mDataManager.isLRFEnabled() && owner.isAddingMarkupEnabled()) {
			mLRFButton0.setVisibility(View.VISIBLE);
			mLRFButton1.setVisibility(View.VISIBLE);
			mLRFButton2.setVisibility(View.VISIBLE);
			mLRFButton3.setVisibility(View.VISIBLE);
			
			mLRFButton0.setOnClickListener(lrfClickListener);
			mLRFButton1.setOnClickListener(lrfClickListener);
			mLRFButton2.setOnClickListener(lrfClickListener);
			mLRFButton3.setOnClickListener(lrfClickListener);
			
	        mBluetoothLRF.setOnLRFListener(this);
	        
			mBTReceiverFilterDisconnect = new IntentFilter(Intents.nics_BT_DISCONNECT);
			mBTReceiverFilterConnect = new IntentFilter(Intents.nics_BT_CONNECT);
			
			if(!btReceiverRegistered) {
				mContext.registerReceiver(btReceiver, mBTReceiverFilterDisconnect);
				mContext.registerReceiver(btReceiver, mBTReceiverFilterConnect);
				btReceiverRegistered = true;
			}
	        
        	if(!mBluetoothLRF.isConnectedToDevice() && mBluetoothLRF.isBluetoothAdapterEnabled()) {
      	      
	        	mBluetoothLRF.cancelConnect();
				mLRFDialog = ProgressDialog.show(mContext, mContext.getString(R.string.reconnecting_bt_device), mContext.getString(R.string.reconnecting_to_device, mBluetoothLRF.getName()), true, true);
	        	mBluetoothLRF.findBT();
	        }
		} else {
			mBluetoothLRF.closeBT();
			mLRFButton0.setVisibility(View.GONE);
			mLRFButton1.setVisibility(View.GONE);
			mLRFButton2.setVisibility(View.GONE);
			mLRFButton3.setVisibility(View.GONE);
		}
		
		mButtonsPanel = view.findViewById(R.id.markupButtonsPanel);
        mCoordinatesPanel = view.findViewById(R.id.markupCoordinatesPanel);
        
        mSymbolButton = (ImageButton) mButtonsPanel.findViewById(R.id.MarkupButtonSymbol);
        mLineButton = (ImageButton) mButtonsPanel.findViewById(R.id.MarkupButtonLine);
        mRectangleButton = (ImageButton) mButtonsPanel.findViewById(R.id.MarkupButtonRectangle);
        mTrapezoidButton = (ImageButton) mButtonsPanel.findViewById(R.id.MarkupButtonTrapezoid);
        mCircleButton = (ImageButton) mButtonsPanel.findViewById(R.id.MarkupButtonCircle);
        mLRFButton = (Button) mButtonsPanel.findViewById(R.id.MarkupButtonLRF);
        mSymbolPickerButton = (Button) mCoordinatesPanel.findViewById(R.id.markupSymbolSelectButton);
        mSymbolPickerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SymbolPickerDialog dialog = new SymbolPickerDialog(mContext);
				dialog.setTargetFragment(mFragment, 100);
				dialog.show(mFragment.getFragmentManager(), "markup_symbol_dialog");
			}
		});
        
        mColorPickerLabel = (TextView) mCoordinatesPanel.findViewById(R.id.coordinateColorPickerLabel);
        mColorPickerButton = (Button) mCoordinatesPanel.findViewById(R.id.coordinateColorPickerButton);
        mColorPickerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ColorPickerDialog dialog = new ColorPickerDialog();
				dialog.setTargetFragment(mFragment, 200);
				dialog.show(mFragment.getFragmentManager(), "markup_color_dialog");
			}
		});
        setColor(Color.WHITE);
        
        mSymbolView = (ImageView) mCoordinatesPanel.findViewById(R.id.markupSymbolView);
        
        mSymbolButton.setOnClickListener(shapeButtonListener);
        mLineButton.setOnClickListener(shapeButtonListener);
        mRectangleButton.setOnClickListener(shapeButtonListener);
        mTrapezoidButton.setOnClickListener(shapeButtonListener);
        mCircleButton.setOnClickListener(shapeButtonListener);
        mLRFButton.setOnClickListener(shapeButtonListener);
		
		mCoordinateLayout1 = view.findViewById(R.id.coordinateLinearLayout1);
		mCoordinateLayout2 = view.findViewById(R.id.coordinateLinearLayout2);
		mCoordinateLayout3 = view.findViewById(R.id.coordinateLinearLayout3);
		
		mCoordinateCircleLayout = view.findViewById(R.id.coordinateCircleLayout);
		mCoordinateSymbolLayout = view.findViewById(R.id.coordinateSymbolLayout);
		
		mLatitudeInput0 = (EditText) view.findViewById(R.id.markupLatitudeInput0);
		mLatitudeInput1 = (EditText) view.findViewById(R.id.markupLatitudeInput1);
		mLatitudeInput2 = (EditText) view.findViewById(R.id.markupLatitudeInput2);
		mLatitudeInput3 = (EditText) view.findViewById(R.id.markupLatitudeInput3);
		
		mLongitudeInput0 = (EditText) view.findViewById(R.id.markupLongitudeInput0);
		mLongitudeInput1 = (EditText) view.findViewById(R.id.markupLongitudeInput1);
		mLongitudeInput2 = (EditText) view.findViewById(R.id.markupLongitudeInput2);
		mLongitudeInput3 = (EditText) view.findViewById(R.id.markupLongitudeInput3);

		mRadiusInput = (EditText) view.findViewById(R.id.markupRadiusInput);
		
		mMyLocationButton0 = (ImageButton) view.findViewById(R.id.markupMyLocation0);
		mMyLocationButton1 = (ImageButton) view.findViewById(R.id.markupMyLocation1);
		mMyLocationButton2 = (ImageButton) view.findViewById(R.id.markupMyLocation2);
		mMyLocationButton3 = (ImageButton) view.findViewById(R.id.markupMyLocation3);
		
		mMyLocationButton0.setOnClickListener(myLocationClickListener);
		mMyLocationButton1.setOnClickListener(myLocationClickListener);
		mMyLocationButton2.setOnClickListener(myLocationClickListener);
		mMyLocationButton3.setOnClickListener(myLocationClickListener);
	}

	public void show(int id) {
		mFragment.hideListView();
		mCoordinatesPanel.setVisibility(View.VISIBLE);
		
		mCoordinateLayout1.setVisibility(View.INVISIBLE);
		mCoordinateLayout2.setVisibility(View.INVISIBLE);
		mCoordinateLayout3.setVisibility(View.INVISIBLE);

		mCoordinateSymbolLayout.setVisibility(View.GONE);
		mCoordinateCircleLayout.setVisibility(View.GONE);
		
		mColorPickerButton.setVisibility(View.VISIBLE);
		mColorPickerLabel.setVisibility(View.VISIBLE);
		
		switch (id) {
			case R.id.MarkupButtonSymbol:
				mCoordinateSymbolLayout.setVisibility(View.VISIBLE);
				mColorPickerButton.setVisibility(View.GONE);
				mColorPickerLabel.setVisibility(View.GONE);
				break;
				
			case R.id.MarkupButtonLine:
				mCoordinateLayout1.setVisibility(View.VISIBLE);
				break;
			
			case R.id.MarkupButtonRectangle:
				mCoordinateLayout1.setVisibility(View.VISIBLE);
				break;
				
			case R.id.MarkupButtonTrapezoid:
				mCoordinateLayout1.setVisibility(View.VISIBLE);
				mCoordinateLayout2.setVisibility(View.VISIBLE);
				mCoordinateLayout3.setVisibility(View.VISIBLE);
				break;
				
			case R.id.MarkupButtonCircle:
				mCoordinateCircleLayout.setVisibility(View.VISIBLE);
				break;
			default:
				break;
		}
	}
	
	private OnClickListener myLocationClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			String lat = String.valueOf(mDataManager.getMDTLatitude());
			String lng = String.valueOf(mDataManager.getMDTLongitude());
			int idx = -1;
			switch (v.getId()) {
				case R.id.markupMyLocation0:
					idx = 0;
					break;
					
				case R.id.markupMyLocation1:
					idx = 1;
					break;
					
				case R.id.markupMyLocation2:
					idx = 2;
					break;
					
				case R.id.markupMyLocation3:
					idx = 3;
					break;
	
				default:
					break;
			}
			
			setCoordinates(idx, lat, lng);
		}
	};
	
	private OnClickListener lrfClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mBluetoothLRF.isConnectedToDevice()) {
				mLRFDialog = ProgressDialog.show(mContext, mContext.getString(R.string.collecting_lrf_data), mContext.getString(R.string.press_fire_to_capture_data), true, true);
				mLRFId = v.getId();
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

	public void clearCoordinates() {
		mLatitudeInput0.setText("");
		mLongitudeInput0.setText("");
		mLatitudeInput1.setText("");
		mLongitudeInput1.setText("");
		mLatitudeInput2.setText("");
		mLongitudeInput2.setText("");
		mLatitudeInput3.setText("");
		mLongitudeInput3.setText("");	
	}
	
	public String getCoordinates() {
		String coords = ""; 
		coords += mLatitudeInput0.getText().toString() + ",";
		coords += mLongitudeInput0.getText().toString() + ",";
		coords += mLatitudeInput1.getText().toString() + ",";
		coords += mLongitudeInput1.getText().toString() + ",";
		coords += mLatitudeInput2.getText().toString() + ",";
		coords += mLongitudeInput2.getText().toString() + ",";
		coords += mLatitudeInput3.getText().toString() + ",";
		coords += mLongitudeInput3.getText().toString();
		
		return coords;
	}
	
	public ArrayList<LatLng> getCoordinatesArray() {
		ArrayList<LatLng> list = new ArrayList<LatLng>();
		
		if(mLatitudeInput0.getText().length() > 0 && mLongitudeInput0.getText().length() > 0) {
			list.add(new LatLng(validateLat(mLatitudeInput0.getText().toString()), validateLon(mLongitudeInput0.getText().toString())));
		}
		
		if(mCurrentShapeType == R.id.MarkupButtonLine || mCurrentShapeType == R.id.MarkupButtonTrapezoid) {
			if(mLatitudeInput1.getText().length() > 0 && mLongitudeInput1.getText().length() > 0) {
				list.add(new LatLng(validateLat(mLatitudeInput1.getText().toString()), validateLon(mLongitudeInput1.getText().toString())));
			}
			
		}
		
		if(mCurrentShapeType == R.id.MarkupButtonRectangle) {
			if(mLatitudeInput0.getText().length() > 0 && mLongitudeInput0.getText().length() > 0 && mLatitudeInput1.getText().length() > 0 && mLongitudeInput1.getText().length() > 0) {
				list.add(new LatLng(validateLat(mLatitudeInput0.getText().toString()), validateLon(mLongitudeInput1.getText().toString())));
				list.add(new LatLng(validateLat(mLatitudeInput1.getText().toString()), validateLon(mLongitudeInput1.getText().toString())));
				list.add(new LatLng(validateLat(mLatitudeInput1.getText().toString()), validateLon(mLongitudeInput0.getText().toString())));
				list.add(list.get(0));
			}
		}
		
		if(mCurrentShapeType == R.id.MarkupButtonTrapezoid) {
			if(mLatitudeInput2.getText().length() > 0 && mLongitudeInput2.getText().length() > 0) {
				list.add(new LatLng(validateLat(mLatitudeInput2.getText().toString()), validateLon(mLongitudeInput2.getText().toString())));
			}
			
			if(mLatitudeInput3.getText().length() > 0 && mLongitudeInput3.getText().length() > 0) {
				list.add(new LatLng(validateLat(mLatitudeInput3.getText().toString()), validateLon(mLongitudeInput3.getText().toString())));
			}
			list.add(list.get(0));
		}
		
		
		return list;
	}
	
	private double validateLat(String value){
		
		boolean containsDigit = false;
	    if (value != null && !value.isEmpty()) {
	        for (char c : value.toCharArray()) {
	            if (containsDigit = Character.isDigit(c)) {
	            	containsDigit = true;
	                break;
	            }
	        }
	    }
		
	    if(containsDigit == false){
	    	return 0.0;
	    }
	    
	    value = value.replaceAll("[^0-9.-]", "");
	    
	    double num = Double.valueOf(value);
	    if(num <= -90){
	    	num = -89.9999;
	    }else if(num >= 90){
	    	num = 89.9999;
	    }
		return num;				
	}
	
	private double validateLon(String value){
		
		boolean containsDigit = false;
	    if (value != null && !value.isEmpty()) {
	        for (char c : value.toCharArray()) {
	            if (containsDigit = Character.isDigit(c)) {
	            	containsDigit = true;
	                break;
	            }
	        }
	    }
		
	    if(containsDigit == false){
	    	return 0.0;
	    }
	    
	    value = value.replaceAll("[^0-9.-]", "");
	    
	    double num = Double.valueOf(value);
	    if(num <= -180){
	    	num = -179.9999;
	    }else if(num >= 180){
	    	num = 179.9999;
	    }
		return num;			
	}

	public void setCoordinates(String coords) {
		if(coords != null) {
			String[] coordsArray = coords.split(",", -1);
			if(coordsArray.length == 8) {
				mLatitudeInput0.setText(coordsArray[0]);
				mLongitudeInput0.setText(coordsArray[1]);
				mLatitudeInput1.setText(coordsArray[2]);
				mLongitudeInput1.setText(coordsArray[3]);
				mLatitudeInput2.setText(coordsArray[4]);
				mLongitudeInput2.setText(coordsArray[5]);
				mLatitudeInput3.setText(coordsArray[6]);
				mLongitudeInput3.setText(coordsArray[7]);
			}
		}
	}
	
	public void setRadius(double radius) {
		mRadiusInput.setText(String.valueOf(radius));
	}
	
	public void setCoordinates(int index, String lat, String lng) {
		switch(index) {
			case 0:
				mLatitudeInput0.setText(lat);
				mLongitudeInput0.setText(lng);
				break;
			case 1:
				mLatitudeInput1.setText(lat);
				mLongitudeInput1.setText(lng);
				break;
			case 2:
				mLatitudeInput2.setText(lat);
				mLongitudeInput2.setText(lng);
				break;
			case 3:
				mLatitudeInput3.setText(lat);
				mLongitudeInput3.setText(lng);
				break;
		}
	}
	
	OnClickListener shapeButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			if(v.getId() == R.id.MarkupButtonLRF) {
				mAlertDialog = new AlertDialog.Builder(mContext).create();
				mAlertDialog.setTitle(mContext.getString(R.string.not_implemented_title));
				mAlertDialog.setMessage(mContext.getString(R.string.not_implemented_desc));
				mAlertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mAlertDialog.dismiss();
						mAlertDialog = null;
					}
				});
				mAlertDialog.show();
				
			} else {
				setCurrentShapeType(v.getId());
				mFragment.setIgnoreUpdate(true);
				mButtonsPanel.setVisibility(View.GONE);
				show(v.getId());
			}
		}
	};

	public int getCurrentShapeType() {
		return mCurrentShapeType;
	}

	public void setCurrentShapeType(int shapeType) {
		this.mCurrentShapeType = shapeType;
	}

	public void setButtonsVisibility(int visibility) {
		mButtonsPanel.setVisibility(visibility);
	}

	public Drawable getSymbol() {
		return mSymbolView.getDrawable();
	}

	public void setSymbol(int symbolId) {
		this.mSymbolView.setImageDrawable(mFragment.getResources().getDrawable(symbolId));
	}

	public void hide() {
		mButtonsPanel.setVisibility(View.VISIBLE);
		mCoordinatesPanel.setVisibility(View.GONE);
	}

	@Override
	public void onLRFData(LRF_HV data) {
		
		if(data != null) {
			double[] reckoned = data.getReckonedLocation((float)mDataManager.getMDTLatitude(), (float)mDataManager.getMDTLongitude(), (float)mDataManager.getMDTAltitude());
	
			final String lat = String.valueOf(reckoned[0]);
			final String lng = String.valueOf(reckoned[1]);
			
			mFragment.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					int idx = -1;
					switch (mLRFId) {
						case R.id.markupLRF0:
							idx = 0;
							break;
							
						case R.id.markupLRF1:
							idx = 1;
							break;
							
						case R.id.markupLRF2:
							idx = 2;
							break;
							
						case R.id.markupLRF3:
							idx = 3;
							break;
				
						default:
							break;
					}

					if(idx != -1) {
						setCoordinates(idx, lat, lng);
					}
					
					if(mLRFDialog != null && mLRFDialog.isShowing()) {
						mLRFDialog.dismiss();
						mLRFDialog = null;
					}
				}
			});
		}
	}

	public void setColor(int temp) {
		mColorPickerButton.getBackground().setColorFilter(temp, PorterDuff.Mode.SRC);
	}
	
	private BroadcastReceiver btReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
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
			    	mFragment.getActivity().startActivityForResult(pairIntent, 500);
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
					mAlertDialog.show();	
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

	public void unregisterReceivers() {
		if(btReceiverRegistered) {
			mContext.unregisterReceiver(btReceiver);
			btReceiverRegistered = false;
		}
		
		if(mLRFDialog != null && mLRFDialog.isShowing()) {
			mLRFDialog.dismiss();
			mLRFDialog = null;
		}
		
		if(mAlertDialog !=  null && mAlertDialog.isShowing()) {
			mAlertDialog.dismiss();
			mAlertDialog = null;
		}
	}

}
