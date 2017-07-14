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

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.LocationSource;

import scout.edu.mit.ll.nics.android.api.DataManager;

public class LocationHandler implements LocationListener, LocationSource,
		ConnectionCallbacks, OnConnectionFailedListener {

	private Context mContext;
	private OnLocationChangedListener mOnLocationChangedListener;
	private Location mLastLocation;
	private DataManager mDataManager;

	private LocationClient mLocationClient;

	public LocationHandler(Context context) {
		mContext = context;
		mDataManager = DataManager.getInstance(mContext);
		mLocationClient = new LocationClient(mContext, this, this);
		mLocationClient.connect();
	}

	@Override
	public void activate(OnLocationChangedListener onLocationChangedListener) {

		mOnLocationChangedListener = onLocationChangedListener;
		forceUpdate();
	}

	@Override
	public void deactivate() {
		if(mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(this);
			mOnLocationChangedListener = null;
		}
	}

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;
		
		// Determine location quality using a combination of timeliness and
		// accuracy
		if (currentBestLocation.getLatitude() == location.getLatitude() && currentBestLocation.getLongitude() == location.getLongitude() && currentBestLocation.getAltitude() == location.getAltitude() && accuracyDelta == 0) {
			return false;
		} else if (isMoreAccurate || !isLessAccurate || !isSignificantlyLessAccurate) {
			return true;
		}
		
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		
		
		if (location != null/* && isBetterLocation(location, mLastLocation)*/ || mDataManager.getWeather() == null) {
			mLastLocation = location;
			
			long lastTime = mDataManager.getMDTTime();
			long curTime = location.getTime();
			
			if(curTime >= lastTime + ((mDataManager.getMDTDataRate() - 5) * 1000)) {
				mDataManager.setMDT(location);
			}
			
//			if(isBetterLocation(location, mLastLocation)) {
//				mDataManager.requestWeatherUpdate(location.getLatitude(), location.getLongitude());
//			}
		}

		if (mOnLocationChangedListener != null) {
			mOnLocationChangedListener.onLocationChanged(mLastLocation);
		}
	}

	public void forceUpdate() {
		if (mOnLocationChangedListener != null && mLastLocation != null || (mOnLocationChangedListener != null && mLastLocation != null && mDataManager.getWeather() == null)) {
			mOnLocationChangedListener.onLocationChanged(mLastLocation);
		}
	}

	public Location getLastLocation() {
		return mLastLocation;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}
	
	public void setUpdateRate(int rate) {
		if(mLocationClient.isConnected()) {
			LocationRequest request = LocationRequest.create();
			request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			request.setInterval(1000 * rate);

			mLocationClient.requestLocationUpdates(request, this);
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		mDataManager.addPersonalHistory("Location client connected.");

		Location clientLocation = mLocationClient.getLastLocation();
		if (clientLocation != null
				&& isBetterLocation(clientLocation, mLastLocation)) {
			mLastLocation = clientLocation;
		}
		
		setUpdateRate(mDataManager.getMDTDataRate());
		forceUpdate();
	}

	@Override
	public void onDisconnected() {
		mDataManager.addPersonalHistory("Location client disconnected.");

	}
}
