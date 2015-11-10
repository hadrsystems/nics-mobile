/*|~^~|Copyright (c) 2008-2015, Massachusetts Institute of Technology (MIT)
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
package edu.mit.ll.phinics.android.maps.markup.layers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import edu.mit.ll.phinics.android.MainActivity;
import edu.mit.ll.phinics.android.R;
import edu.mit.ll.phinics.android.api.data.DamageReportData;
import edu.mit.ll.phinics.android.api.payload.forms.DamageReportPayload;
import edu.mit.ll.phinics.android.maps.markup.MarkupBaseShape;
import edu.mit.ll.phinics.android.maps.markup.MarkupSymbol;
import edu.mit.ll.phinics.android.utils.Intents;

public class DamageReportLayer extends MarkupLayer {
	
	protected HashMap<String, DamageReportPayload> mLayerFeatures;	
	protected AsyncTask<ArrayList<DamageReportPayload>, Object, Integer> mParseFeaturesTask;
	private BroadcastReceiver srReceiver;
	private boolean receiverRegistered = false;
	
	public DamageReportLayer(Context context, String name, GoogleMap map) {
		super(context, name, map);
		mLayerFeatures = new HashMap<String, DamageReportPayload>();
	}

	@Override
	protected void setupReceiver() {
		srReceiver = new BroadcastReceiver() {
			@SuppressWarnings("unchecked")
			@SuppressLint("Wakelock")
			@Override
			public void onReceive(Context context, Intent intent) {
				try {
					PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
					PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PHINICS_WAKE");
		
					//Acquire the lock
					wakeLock.acquire();
					
					Bundle extras = intent.getExtras();
					if(extras != null) {
						String type = extras.getString("type", "");
						String layerName = extras.getString("layerName");
						Log.i("PhinicsDataManager", "Requesting Data Update: " + type + " " + layerName);

						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
						format.setTimeZone(TimeZone.getTimeZone("UTC"));
						
						ArrayList<DamageReportPayload> damageReports = mDataManager.getDamageReportHistoryForIncident(mDataManager.getActiveIncidentId());

						clearFromMap();
						mParseFeaturesTask = new ParseFeaturesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, damageReports);
					}
					
					//Release the lock
					wakeLock.release();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		Intent intent = new Intent(Intents.PHINICS_POLLING_WFS_LAYER + mLayerName);
    	intent.putExtra("type", "wfslayer");
    	intent.putExtra("layerName", mLayerName);
    	
    	mContext.registerReceiver(srReceiver, new IntentFilter(Intents.PHINICS_POLLING_WFS_LAYER + mLayerName));
    	receiverRegistered = true;
    	
		mPendingWFSLayerRequestIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10, (mDataManager.getWFSDataRate() * 1000), mPendingWFSLayerRequestIntent);
	}
	
	protected class ParseFeaturesTask extends AsyncTask<ArrayList<DamageReportPayload>, Object, Integer> {

		@Override
		protected Integer doInBackground(@SuppressWarnings("unchecked") ArrayList<DamageReportPayload>... params) {
			
			for (DamageReportPayload feature: params[0]) {
				try {
					DamageReportPayload existingFeature = mLayerFeatures.get(feature.getFormId());
					
//					if(existingFeature != null) {
//						Date mdtDate = new Date(existingFeature.getCreatedUTC());
//					}
					
					if(existingFeature == null) {
						MarkupBaseShape featureShape = parseFeature(feature);
						mLayerFeatures.put(featureShape.getFeatureId(), feature);
						
						mMarkupShapes.add(featureShape);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			return params[0].size();
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			Log.i("PhinicsDataManager", "Rendering " + result + " feature(s) in WFS Layer " + mLayerName);
			
		}
	}
	
	protected MarkupBaseShape parseFeature(final DamageReportPayload payload) {
		payload.parse();
		DamageReportData data = payload.getMessageData();
		
		JsonObject attr = new JsonObject();
		Resources resources = mContext.getResources();
		try {
			attr.addProperty("title", resources.getString(R.string.DAMAGESURVEY));
			attr.addProperty("reportId", payload.getId());
			attr.addProperty("payload", payload.toJsonString());
			attr.addProperty("type", "dmgrpt");
			attr.addProperty("icon", R.drawable.damage_advisory);
			attr.addProperty(resources.getString(R.string.markup_user), data.getUser());
			attr.addProperty(resources.getString(R.string.markup_timestamp), payload.getSeqTime());
			attr.addProperty(resources.getString(R.string.markup_message), data.getDamageInformationAsString());			
		} catch (Exception e) {
		}
		
		LatLng coordinate = null;
		try {
			coordinate = new LatLng(Double.valueOf(data.getPropertyLatitude()), Double.valueOf(data.getPropertyLongitude()));
		} catch (Exception e) {
			coordinate = new LatLng(0, 0);
		}
		final MarkupSymbol symbol = new MarkupSymbol(mDataManager, attr.toString(), coordinate, generateTintedBitmap(R.drawable.damage_advisory, new int[] { 20, 20, 20, 20 }), null, new int[] { 255, 255, 255, 255 });
		symbol.setFeatureId(String.valueOf(payload.getFormId()));
		((MainActivity)mContext).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				symbol.setMarker(mMap.addMarker(symbol.getOptions()));
			}
		});
		
		return symbol;
	}

	public void unregister() {
		mAlarmManager.cancel(mPendingWFSLayerRequestIntent);
		if(receiverRegistered){
			mContext.unregisterReceiver(srReceiver);
			receiverRegistered = false;
		}
	}
}
