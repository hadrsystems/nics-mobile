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
package scout.edu.mit.ll.nics.android.maps.markup.layers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.Header;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;

import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.RestClient;
import scout.edu.mit.ll.nics.android.api.data.geo.wfs.Feature;
import scout.edu.mit.ll.nics.android.api.data.geo.wfs.FeatureCollection;
import scout.edu.mit.ll.nics.android.api.data.geo.wfs.XmlParser;
import scout.edu.mit.ll.nics.android.api.payload.TrackingLayerPayload;
import scout.edu.mit.ll.nics.android.maps.markup.MarkupBaseShape;
import scout.edu.mit.ll.nics.android.maps.markup.MarkupSymbol;
import scout.edu.mit.ll.nics.android.utils.Intents;

public class MarkupLayer {
	protected Context mContext;
	protected DataManager mDataManager;
	protected GsonBuilder mBuilder;
	protected TrackingLayerPayload mLayerPayload;						// name of the layer
	protected ArrayList<MarkupBaseShape> mMarkupShapes;		// actual objects that will be rendered
	protected HashMap<String, Feature> mLayerFeatures;	
	protected AsyncTask<ArrayList<Feature>, Object, Integer> mParseFeaturesTask;
	protected long mLastFeatureTimestamp;			// do next query based off of this timestamp
	protected PendingIntent mPendingWFSLayerRequestIntent;
	protected AlarmManager mAlarmManager;
	protected GoogleMap mMap;
	protected boolean receiverRegistered = false;
	
	public MarkupLayer(Context context, TrackingLayerPayload layerPayload, GoogleMap map) {
		mContext = context;
		mMap = map;
		mDataManager = DataManager.getInstance(mContext);
		mAlarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
		mBuilder = new GsonBuilder();
		mLayerPayload = layerPayload;
		
		mLayerFeatures = new HashMap<String, Feature>();
		mMarkupShapes = new ArrayList<MarkupBaseShape>();

		setupReceiver();
	}
	
	protected void setupReceiver() {
		Intent intent = new Intent(Intents.nics_POLLING_WFS_LAYER + mLayerPayload.getLayername());
    	intent.putExtra("type", "wfslayer");
    	intent.putExtra("layerName", mLayerPayload.getLayername());
    	
    	mContext.registerReceiver(receiver, new IntentFilter(Intents.nics_POLLING_WFS_LAYER + mLayerPayload.getLayername()));
    	receiverRegistered = true;
    	
		mPendingWFSLayerRequestIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 200, (mDataManager.getWFSDataRate() * 1000), mPendingWFSLayerRequestIntent);
	}
	
	protected class ParseFeaturesTask extends AsyncTask<ArrayList<Feature>, Object, Integer> {

		@Override
		protected Integer doInBackground(@SuppressWarnings("unchecked") ArrayList<Feature>... params) {
			
			for (Feature feature: params[0]) {
				try {
					Feature existingFeature = mLayerFeatures.get(feature.getProperties().get("id").toString());
					
//					if(existingFeature != null) {
//						Date mdtDate;
//						if(existingFeature.getProperties().get("created") != null) {
//							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
//							format.setTimeZone(TimeZone.getTimeZone("UTC"));
//							mdtDate = format.parse(existingFeature.getProperties().get("created").toString());
//						} else {
//							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
//							format.setTimeZone(TimeZone.getTimeZone("UTC"));
//							mdtDate = format.parse(existingFeature.getProperties().get("timestamp").toString());
//						}
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
			Log.i("nicsDataManager", "Rendering " + result + " feature(s) in WFS Layer " + mLayerPayload.getLayername());
			
		}
	}
	
	protected MarkupBaseShape parseFeature(final Feature feature) {
		ArrayList<Double> coordinates = feature.getGeometry().getCoordinates();
		HashMap<String, Object> properties = feature.getProperties();
		
		String id = properties.get("id").toString();
		Object description = properties.get("description");
		
		float course = 0;
		float speed = 0;
		float age = 0;
		
		if(properties.containsKey("course")){
			if(!properties.get("course").toString().equalsIgnoreCase("NULL")){
				course = Float.parseFloat(properties.get("course").toString());
			}
		}
		
		if(properties.containsKey("speed")){
			if(!properties.get("speed").toString().equalsIgnoreCase("NULL")){
				speed = Float.parseFloat(properties.get("speed").toString());
			}
		}
		
		if(properties.containsKey("age")){
			if(!properties.get("age").toString().equalsIgnoreCase("NULL")){
				age = Float.parseFloat(properties.get("age").toString());
			}
		}
		//float accuracy = Float.parseFloat(android.text.Html.fromHtml(properties.get("description").toString().).toString());
		
		final Bitmap symbolBitmap;
		if(course != 0 && speed != 0 /*&& accuracy < 100*/) {
			if(age < 1440) {
				symbolBitmap = generateRotatedBitmap(R.drawable.mdt_dot_directional, course);
			} else {
				symbolBitmap = generateRotatedBitmap(R.drawable.mdt_dot_stale, course);
			}
		} else {
			if(age < 1440) {
				symbolBitmap = generateRotatedBitmap(R.drawable.mdt_dot, 0);
			} else {
				symbolBitmap = generateRotatedBitmap(R.drawable.mdt_dot_stale, 0);
			}
		}
		
		Date mdtDate = null; 
		try {
			if(properties.get("created") != null) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
				format.setTimeZone(TimeZone.getTimeZone("UTC"));
				mdtDate = format.parse(properties.get("created").toString());
			} else if(properties.get("timestamp") != null) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
				format.setTimeZone(TimeZone.getTimeZone("UTC"));
				mdtDate = format.parse(properties.get("timestamp").toString());
			}else if(properties.get("xmltime") != null){
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
				format.setTimeZone(TimeZone.getTimeZone("UTC"));
				mdtDate = format.parse(properties.get("xmltime").toString());
			}

			if(mdtDate != null){
				long featureTimestamp = mdtDate.getTime();
				
				if(featureTimestamp > mLastFeatureTimestamp) {
					mLastFeatureTimestamp = featureTimestamp;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonObject attr = new JsonObject();
		Resources resources = mContext.getResources();
		try {
			attr.addProperty("icon", R.drawable.vehicle);
			attr.addProperty(resources.getString(R.string.markup_resource_id), (String) properties.get("name"));
			attr.addProperty(resources.getString(R.string.markup_timestamp), mdtDate.getTime());
			attr.addProperty(resources.getString(R.string.markup_course), course + "&#xb0;");
			
			if(description != null) {
				attr.addProperty(resources.getString(R.string.markup_description), (String) description);
			}
			
		} catch (Exception e) {
		}
				
		final MarkupSymbol symbol = new MarkupSymbol(mDataManager,mMap, attr.toString(), new LatLng(coordinates.get(1), coordinates.get(0)), symbolBitmap, null, new int[] { 255, 255, 255, 255 });
		symbol.setFeatureId(id);
		if(!feature.isRendered()) {
			((MainActivity)mContext).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					feature.setRendered(true);
					symbolBitmap.recycle();
				}
			});
		}
		
		return symbol;
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@SuppressLint("Wakelock")
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
				PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "nics_WAKE");
	
				//Acquire the lock
				wakeLock.acquire();
				
				Bundle extras = intent.getExtras();
				if(extras != null) {
					String type = extras.getString("type", "");
					String layerName = extras.getString("layerName");
					Log.i("nicsDataManager", "Requesting Data Update: " + type + " " + layerName);

					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
					format.setTimeZone(TimeZone.getTimeZone("UTC"));
					
					RestClient.getWFSData(mLayerPayload, 500, format.format(new Date(mLastFeatureTimestamp)), new AsyncHttpResponseHandler() {
						@SuppressWarnings("unchecked")

						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							String content = (responseBody != null) ? new String(responseBody) : "error";
							
							if(mLayerPayload.shouldExpectJson()){
								try {
									FeatureCollection collection = mBuilder.create().fromJson(content, FeatureCollection.class);
									if(collection != null) {
										clearFromMap();
										mParseFeaturesTask = new ParseFeaturesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, collection.getFeatures());
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}else{
								
								XmlParser parser = new XmlParser();
					        	InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));;
					        	
					            try {
					            	FeatureCollection collection = new FeatureCollection();
					            	collection.setFeatures(parser.parse(stream));
					            	collection.setType("TRACKING TYPE");
					            	
					            	if(collection.getFeatures() != null) {
										clearFromMap();
										mParseFeaturesTask = new ParseFeaturesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, collection.getFeatures());
									}
					            	
								} catch (XmlPullParserException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}finally {
						            if (stream != null) {
						                try {
											stream.close();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
						            }
								}
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
							String content = (responseBody != null) ? new String(responseBody) : "error";
							Log.e("nicsRest", content);
							
							mLayerPayload.setAuthtoken(null);	//force a new token on next pull
						}
					});
				}
				//Release the lock
				wakeLock.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
		
	public Bitmap generateRotatedBitmap(int resourceId, float rotationDegrees) {
		Bitmap bitmap = null;
		Options opts = new BitmapFactory.Options();
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			opts.inMutable = true;
			bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId, opts);
		} else {
			bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId, opts).copy(Config.ARGB_8888, true);
		}

		Matrix rotator = new Matrix();
		rotator.postRotate(rotationDegrees, bitmap.getWidth()/2.0f, bitmap.getHeight()/2.0f);

		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotator, true);

		return bitmap;
	}
	
	public Bitmap generateTintedBitmap(int resourceId, int[] colorArray) {
		Bitmap bitmap = null;
		Options opts = new BitmapFactory.Options();
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			opts.inMutable = true;
			bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId, opts);
		} else {
			bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId, opts).copy(Config.ARGB_8888, true);
		}
		
		if(bitmap != null) {
			Canvas test = new Canvas(bitmap);
	
			Paint paint = new Paint();
			
			int color = Color.argb(255, colorArray[1], colorArray[2], colorArray[3]);
			
			if(color != -1) {
				paint.setColorFilter(new PorterDuffColorFilter(color, Mode.SRC_IN));
			} else {
				paint.setColorFilter(new PorterDuffColorFilter(Color.WHITE, Mode.SRC_IN));
			}
			test.drawBitmap(bitmap, 0, 0, paint);
		}
		
		return bitmap;
	}

	public void unregister() {
		mAlarmManager.cancel(mPendingWFSLayerRequestIntent);
		if(receiverRegistered){
			mContext.unregisterReceiver(receiver);
			receiverRegistered = false;
		}
	}

	public void clearFromMap() {
		try {
			if(mParseFeaturesTask != null) {
				mParseFeaturesTask.cancel(true);
				mParseFeaturesTask = null;
			}
		
			for(MarkupBaseShape shape : mMarkupShapes) {
				mLayerFeatures.remove(shape.getFeatureId());
				shape.removeFromMap();
			}
			mMarkupShapes.clear();
		} catch(Exception e) {
		}
	}
}
