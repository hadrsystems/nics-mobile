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
package scout.edu.mit.ll.nics.android.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.payload.WeatherPayload;
import scout.edu.mit.ll.nics.android.maps.markup.Symbols;
import scout.edu.mit.ll.nics.android.utils.Intents;

public class WeatherFragment extends Fragment implements OnClickListener {

	private View mRootView;
	private DataManager mDataManager;
	private IntentFilter mWeatherReceiverFilter;
	private boolean weatherReceiverRegistered;
	
	private TextView currentLocationLabel;
	private TextView currentTempFarenheight;
	private TextView currentTempCelcius;
	private TextView currentDescription;
	
	private ImageView currentWeatherImage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mWeatherReceiverFilter = new IntentFilter(Intents.nics_NEW_WEATHER_REPORT_RECEIVED);
		
		if(!weatherReceiverRegistered) {
			getActivity().registerReceiver(weatherReceiver, mWeatherReceiverFilter);
			weatherReceiverRegistered = true;
		}
		
		mDataManager = DataManager.getInstance(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		mRootView = inflater.inflate(R.layout.fragment_weather, container, false);
		
		currentLocationLabel = (TextView) mRootView.findViewById(R.id.weather_current_location_label);
		currentTempFarenheight = (TextView) mRootView.findViewById(R.id.weather_current_farenheight_label);
		currentTempCelcius = (TextView) mRootView.findViewById(R.id.weather_current_celcius_label);
		currentDescription = (TextView) mRootView.findViewById(R.id.weather_current_description);
		currentWeatherImage = (ImageView) mRootView.findViewById(R.id.weather_current_image_view);
		
		populateWeather(mDataManager.getWeather());
		
		return mRootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if(weatherReceiverRegistered) {
			getActivity().unregisterReceiver(weatherReceiver);
			weatherReceiverRegistered = false;
		}
		
		((ViewGroup) mRootView.getParent()).removeView(mRootView);
	}
	
	private BroadcastReceiver weatherReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				populateWeather(mDataManager.getWeather());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private void populateWeather(WeatherPayload payload) {
		try {
			if(payload != null && payload.currentobservation != null) {
				currentLocationLabel.setText(payload.currentobservation.name);
				currentTempFarenheight.setText(payload.currentobservation.Temp + " °F");
				
				int temp = -999;
				try {
					temp = Integer.valueOf(payload.currentobservation.Temp);
				} catch (NumberFormatException e) {
					
				}
				if(temp != -999) {
					currentTempCelcius.setText(Math.round(((temp - 32) * 5)/9) + " °C");
				}
				currentDescription.setText(payload.currentobservation.Weather);
				if(Symbols.WEATHER.get(payload.currentobservation.Weatherimage) != null) {
					currentWeatherImage.setImageResource(Symbols.WEATHER.get(payload.currentobservation.Weatherimage));
				} else {
					mDataManager.addPersonalHistory("Warning: nics failed to fetch an image for: " + payload.currentobservation.Weatherimage);
				}
			}
		} catch (Exception e) {
			
		}
	}

	@Override
	public void onClick(View v) {
	}
}
