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
package scout.edu.mit.ll.nics.android.maps.markup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.data.FeatureAttributes;
import scout.edu.mit.ll.nics.android.api.data.MarkupFeature;


public abstract class MarkupBaseShape {
	public Long mId;
	public String mTitle;
	public Long mTime;
	public int[] mStrokeColor;
	public int[] mFillColor;
	private double mRadius;
	private String mCreator;
	private boolean isDraft = true;
	private String mFeatureId;
	protected MarkupFeature mFeature;
	protected Context mContext;
	
	private DataManager mDataManager;
	
	public MarkupType mType;
	public ArrayList<LatLng> mPoints;
	public ArrayList<Marker> mLineMarkers;
	
	public MarkupBaseShape(DataManager manager) {
		mDataManager = manager;
		mContext = manager.getContext();
		
		mTime = new Date().getTime();
		mLineMarkers = new ArrayList<Marker>();
		mPoints = new ArrayList<LatLng>();
		mCreator = mDataManager.getUserNickname();
		isDraft = true;
	}
	
	public Long getId() {
		return mId;
	}
	
	public void setId(Long id) {
		mId = id;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public void setTitle(String title) {
		mTitle = title;
	}
	
	public MarkupType getType() {
		return mType;
	}
	public void setType(MarkupType type) {
		mType = type;
	}
	
	public long getTime() {
		return mTime;
	}
	
	public void setTime(long time) {
		mTime = time;
	}
	
	public int[] getStrokeColor() {
		return mStrokeColor;
	}

	public void setStrokeColor(int[] color) {
		this.mStrokeColor = color;
	}
	
	public List<LatLng> getPoints() {
		return mPoints;
	}
	public void setPoints(ArrayList<LatLng> points) {
		mPoints.clear();
		mPoints.addAll(points);
	}
	
	public void addPoint(LatLng point) {
		mPoints.add(point);
	}
	
	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("type", mType);
		json.put("created", mTime);
		
		if(mStrokeColor != null) {
			json.put("strokeColor", colorToHexString(mStrokeColor));
		}
		
		if(mFillColor != null) {
			json.put("fillColor", colorToHexString(mFillColor));
		}
		
		JSONArray pointsArray = new JSONArray();
		for (LatLng latlng : mPoints) {
			pointsArray.put(new JSONArray(Arrays.asList(new String[] {String.valueOf(latlng.latitude), String.valueOf(latlng.longitude)})));
		}
		json.put("points", pointsArray);
		
		return json;
	}
	
	public String toJsonString() throws JSONException {
		return toJson().toString();
	}
	
	private String colorToHexString(int[] color) {
		if(color != null){
			return String.format("#%06X", 0xFFFFFF &  Color.argb(color[0], color[1], color[2], color[3]));
		} else {
			return "";
		}
	}

	public String toString() {
		if(mFeature != null) {
			String attributesString = mFeature.getfeatureattributes();
			
			if(attributesString != null && !attributesString.isEmpty()) {
				FeatureAttributes attributes = new Gson().fromJson(mFeature.getfeatureattributes(), FeatureAttributes.class);
	
				return (isDraft ? "<Draft> " : "") + DateFormat.format("MM/dd kk:mm:ss", new Date(mTime)) + " - " + mCreator + "\nType: " + mType.toString().toLowerCase(mDataManager.getLocale()) + attributes.toString();
			}
		}
		
		return (isDraft ? "<Draft> " : "") + DateFormat.format("MM/dd kk:mm:ss", new Date(mTime)) + " - " + mCreator + "\nType: " + mType.toString().toLowerCase(mDataManager.getLocale());
	}
	
	public abstract void removeFromMap();
	
	public abstract void setIcon(Bitmap bitmap, int[] color);
	
	public abstract void showInfoWindow();
	
	public abstract Object getOptions();
	
	public void addMarker(Marker marker) {
		mLineMarkers.add(marker);
	}
	
	public void removeMarker(int idx) {
		if(idx < mLineMarkers.size()) {
			mLineMarkers.remove(idx).remove();
		}
	}

	public abstract boolean removeLastPoint();

	public void clearPoints() {
		mPoints.clear();
		clearMarkers();
	}
	
	public void clearMarkers() {
		for (Marker marker : mLineMarkers) {
			marker.remove();
		}
		
		mLineMarkers.clear();
	}
	
	public String getPointsString() {
		String temp = "";
		
		boolean first = true;
		for(LatLng coords : mPoints) {
			if(first) {
				temp += coords.latitude + " " + coords.longitude;
				first = false;
			} else {
				temp += "," + coords.latitude + " " + coords.longitude;
			}
		}
		return temp;
	}

	public MarkupFeature toFeature() {
		Integer graphicHeight = null;
		Integer graphicWidth = null;
		double opacity = 0.2;
		String type = getNicsType();
		if(type.equals("sketch") && mPoints.size() == 2) {
			opacity = 1;
		}
		String graphic = null;
		if(getNicsType().equals("marker")) {
			Bitmap img = ((MarkupSymbol)this).getSymbolBitmap();
			graphicHeight = img.getScaledHeight(DisplayMetrics.DENSITY_HIGH);
			graphicWidth = img.getScaledWidth(DisplayMetrics.DENSITY_HIGH);
			graphic = ((MarkupSymbol)this).getSymbolPath();
			opacity = 1;
		}
		
		Double radius = null;
		if(type.equals("circle")) {
			radius = Math.toDegrees((mRadius / 1000.0) / 6371.0); //earth radius
		}
			
		MarkupFeature feature = new MarkupFeature();
		
//		long testCollabRoom = mDataManager.getSelectedCollabRoomId();
//		long testCollabRoom2 = mDataManager.getActiveCollabroomId();
		
		feature.setCollabRoomId(mDataManager.getSelectedCollabRoom().getCollabRoomId());
		feature.setUsersessionId(mDataManager.getUserSessionId());
//		feature.setDashStyle("solid"); //set for firelines only
		feature.setFillColor(colorToHexString(mStrokeColor));
		feature.setGraphic(graphic);
		feature.setGraphicHeight(graphicHeight);
		feature.setGraphicWidth(graphicWidth);
		feature.setgesture(false);
		feature.setUsername(mDataManager.getUsername());
		feature.setStrokeColor(colorToHexString(mStrokeColor));
		feature.setStrokeWidth(2.0);
		feature.setip("127.0.0.1");
		feature.setSeqTime(System.currentTimeMillis()/1000);
		feature.setFeatureId(mFeatureId);
		
		feature.setTopic("NICS.incidents." + mDataManager.getActiveIncidentName() + ".collab." + mDataManager.getSelectedCollabRoom().getName());
		
		feature.setType(type);
		feature.setOpacity(opacity);
		feature.setGeometryVector2FromString(getPointsString());
		
		if(type.equals("circle")){
			LatLng circleMid = new LatLng(feature.getGeometryVector2().get(0).x,feature.getGeometryVector2().get(0).y);
			mPoints = getPolygonForCircle(circleMid,radius);
		}
		
		feature.setGeometryStringForNICS(getPointsString(),type);
//		feature.setGeometryString("POINT(-7859560.993408391 5291888.341869146)");	//debug
	//	feature.setRadius(radius);
		feature.setRotation(0.0);

		return feature;
	}

	public double getRadius() {
		return mRadius;
	}

	public void setRadius(double mRadius) {
		this.mRadius = mRadius;
	}

	public ArrayList<LatLng> getPolygonForCircle(LatLng coord, Double radius){
		
		ArrayList<LatLng> circleGeom = new ArrayList<LatLng>();
		
		int numberOfPoints = 20;
		float currentAngle = 0;
		
		while(currentAngle < (Math.PI*2)){
			
			double lon = (radius*Math.cos(currentAngle)) + coord.longitude;
			double lat = (radius*Math.sin(currentAngle)) + coord.latitude;
			
			circleGeom.add(new LatLng(lat,lon));
			
			currentAngle += (Math.PI*2)/numberOfPoints;
		}
		circleGeom.add(circleGeom.get(0));
		return circleGeom;
	}
	
	public abstract String getNicsType();

	public String getCreator() {
		return mCreator;
	}

	public void setCreator(String mCreator) {
		this.mCreator = mCreator;
	}

	public boolean isDraft() {
		return isDraft;
	}

	public void setDraft(boolean isDraft) {
		this.isDraft = isDraft;
	}

	public String getFeatureId() {
		return mFeatureId;
	}

	public void setFeatureId(String mFeatureId) {
		this.mFeatureId = mFeatureId;
	}

	public MarkupFeature getFeature() {
		return mFeature;
	}

	public void setFeature(MarkupFeature mFeature) {
		this.mFeature = mFeature;
	}
}
