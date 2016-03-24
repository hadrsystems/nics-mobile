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
package scout.edu.mit.ll.nics.android.fragments;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.data.DamageReportData;
import scout.edu.mit.ll.nics.android.api.data.SimpleReportData;
import scout.edu.mit.ll.nics.android.api.data.WeatherReportData;
import scout.edu.mit.ll.nics.android.api.payload.forms.DamageReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.WeatherReportPayload;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MapMarkupLocationPickerFragment extends MapMarkupFragment{

	private MarkerOptions markerOptions = null;
	private Marker marker = null;
	
	private String PreviousFragment;
	private MainActivity mMainActivity;
	
	private SimpleReportPayload mSimpleReportPayload;
	private DamageReportPayload mDamageReportPayload;
	private WeatherReportPayload mWeatherReportPayload;
	
	public  MapMarkupLocationPickerFragment(String _previousFragment){
		PreviousFragment = _previousFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mMainActivity = (MainActivity) mContext;
	}
	
	@Override
	public void onResume() {
		super.onResume();
				
		mPickerCompleteButton.setVisibility(View.INVISIBLE);
		mPickerCompleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			setPayload();
			mMainActivity.mViewMapLocationPicker = false;
			
			if(marker!= null){
				marker.remove();
			}
			marker = null;
			markerOptions = null;
			 
			}
		});
	}
	
	private void setPayload(){

		if(PreviousFragment == getString(R.string.GENERALMESSAGE)){
						
			mSimpleReportPayload = mMainActivity.mSimpleReportFragment.getPayload();
			
			SimpleReportData reportData = mSimpleReportPayload.getMessageData();
			reportData.setLatitude(markerOptions.getPosition().latitude);
			reportData.setLongitude(markerOptions.getPosition().longitude);
			mSimpleReportPayload.setMessageData(reportData);
			mMainActivity.openSimpleReport(mSimpleReportPayload, true);

		}else if(PreviousFragment == getString(R.string.DAMAGESURVEY)){
		
			mDamageReportPayload = mMainActivity.mDamageReportFragment.getPayload();
			
			DamageReportData reportData = mDamageReportPayload.getMessageData();
			reportData.setPropertyLatitude( Double.toString( markerOptions.getPosition().latitude));
			reportData.setPropertyLongitude(Double.toString( markerOptions.getPosition().longitude));
			mDamageReportPayload.setMessageData(reportData);
			mMainActivity.openDamageReport(mDamageReportPayload, true);
			
		}else if(PreviousFragment == getString(R.string.WEATHERREPORT)){
		
			mWeatherReportPayload = mMainActivity.mWeatherReportFragment.getPayload();
			
			WeatherReportData reportData = mWeatherReportPayload.getMessageData();
			reportData.setLatitude(Double.toString(markerOptions.getPosition().latitude));
			reportData.setLongitude(Double.toString(markerOptions.getPosition().longitude));
			mWeatherReportPayload.setMessageData(reportData);
			mMainActivity.openWeatherReport(mWeatherReportPayload, true);
		}
	}
	
	/*
	@Override
	public boolean onMarkerClick(Marker arg0) {
		
		return false;
	}
	*/
	@Override
	public void onMapClick(LatLng coordinate) {
	
		if(markerOptions == null){
			markerOptions = new MarkerOptions();
			markerOptions.position(new LatLng(coordinate.latitude, coordinate.longitude));
			markerOptions.draggable(true);
			
			marker = mMap.addMarker(markerOptions);
			marker.setPosition(new LatLng(coordinate.latitude, coordinate.longitude));
			
			mPickerCompleteButton.setVisibility(View.VISIBLE);
			
		}else{
			markerOptions.position(new LatLng(coordinate.latitude, coordinate.longitude));
			marker.setPosition(new LatLng(coordinate.latitude, coordinate.longitude));
			marker.setTitle(Double.toString(coordinate.latitude));

		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if(marker!= null){
			marker.remove();
		}
		marker = null;
		markerOptions = null;
		
	}
	
	public void BackButtonPressed(){
		mMainActivity.mViewMapLocationPicker = false;
		
		if(PreviousFragment == getString(R.string.GENERALMESSAGE)){
			mSimpleReportPayload = mMainActivity.mSimpleReportFragment.getPayload();
			mMainActivity.openSimpleReport(mSimpleReportPayload, true);	
		}else if(PreviousFragment == getString(R.string.DAMAGESURVEY)){
			mDamageReportPayload = mMainActivity.mDamageReportFragment.getPayload();
			mMainActivity.openDamageReport(mDamageReportPayload, true);
		}else if(PreviousFragment == getString(R.string.WEATHERREPORT)){
			mWeatherReportPayload = mMainActivity.mWeatherReportFragment.getPayload();
			mMainActivity.openWeatherReport(mWeatherReportPayload, true);
		}
	}
	
	public void setPreviousReport(String fragment){	
		PreviousFragment = fragment;
	}
}
