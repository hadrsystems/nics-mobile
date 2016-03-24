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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.data.MarkupFeature;
import scout.edu.mit.ll.nics.android.api.data.Vector2;


public class MarkupText extends MarkupBaseShape {
	
	private MarkerOptions mMarkerOptions;
	private Marker mMarker;
	private Bitmap mMarkerBitmap;
	private String mImagePath;
	private Context mContext;
	
	public MarkupText(DataManager manager, String title, LatLng coordinate, int[] strokeColor) {
		super(manager);
		
		mMarkerOptions = new MarkerOptions();
		mMarkerOptions.title(title);
		mMarkerOptions.anchor(0.5f, 0.5f);
		mMarkerOptions.position(coordinate);

		setTitle(title);
		setPoint(coordinate);
		setType(MarkupType.label);

		
		setStrokeColor(strokeColor);
	}

	public MarkupText(DataManager manager, MarkupFeature item, int[] strokeColor) {
		super(manager);
		
		mContext = manager.getContext();
		
		LatLng coordinate = null;
		
		for(Vector2 point : item.getGeometryVector2()) {
			coordinate = new LatLng(point.x,point.y);
			addPoint(coordinate);		
		}
		
		JsonObject attr = new JsonObject();
		Resources resources = mContext.getResources();
		try {
			attr.addProperty("icon", R.drawable.text);
			attr.addProperty(resources.getString(R.string.markup_user), item.getUsername());
			attr.addProperty(resources.getString(R.string.markup_timestamp), item.getSeqTime());
			attr.addProperty(resources.getString(R.string.markup_message), item.getLabelText());
		} catch (Exception e) {
		}
		
		mMarkerOptions = new MarkerOptions();
		mMarkerOptions.title(attr.toString());
		mMarkerOptions.anchor(0.5f, 0.5f);
		mMarkerOptions.position(coordinate);

		setTitle(item.getLabelText());
		setType(MarkupType.label);
		setTime(item.getSeqTime());
		setCreator(item.getUsername());
		setDraft(false);
		setFeatureId(item.getFeatureId());
		setFeature(item);
		
		int tempStrokeColor = 0;
		if(item.getStrokeColor()!=null){
			tempStrokeColor =  Color.parseColor(item.getStrokeColor());
		}
		
		Bitmap textBitmap = generateText(item.getLabelText(), item.getLabelSize().intValue() + 8,  tempStrokeColor, Typeface.DEFAULT);

		mMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(textBitmap));
		setSymbolBitmap(textBitmap);
		setIcon(textBitmap, strokeColor);
		setStrokeColor(strokeColor);
		
		textBitmap.recycle();
	}

	public void setTitle(String title) {
		mTitle = title;
		
		if(mMarker != null) {
			mMarker.setTitle(title);
		}
	}

	public void setPoint(LatLng coordinate) {
		
		if(mMarker != null) {
			mMarker.setPosition(coordinate);
		}
		
		if(mPoints == null || mPoints.size() == 0) {
			addPoint(coordinate);
		} else {
			mPoints.set(0, coordinate);
		}
	}
	
	private void setSymbolBitmap(Bitmap symbolBitmap) {
		mMarkerBitmap = symbolBitmap;
	}
	
	public void setSymbolPath(String symbolPath) {
		mImagePath = symbolPath;
	}

	public Bitmap getSymbolBitmap() {
		return mMarkerBitmap;
	}
	
	public String getSymbolPath() {
		return mImagePath;
	}

	public MarkerOptions getOptions() {
		return mMarkerOptions;
	}

	public void setMarker(Marker marker) {
		mMarker = marker;
	}
	
	@Override
	public void removeFromMap() {
		if(mMarker != null) {
			mMarker.remove();
		}
	}
	
	@Override
	public void setIcon(Bitmap symbolBitmap, int[] color) {
		setStrokeColor(color);
		setSymbolBitmap(symbolBitmap);
		
		if(mMarker != null) {
			mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(symbolBitmap));
		}
	}

	public void showInfoWindow() {
		if(mMarker != null) {
			mMarker.showInfoWindow();
		}
	}

	@Override
	public void addMarker(Marker marker) {
		setMarker(marker);
	}

	@Override
	public boolean removeLastPoint() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNicsType() {
		return "label";
	}
	

	public Bitmap generateText(String text, int textSize, int textColor, Typeface typeface) {
		Paint paint = new Paint();
		paint.setTextSize(textSize);
		
		if(textColor == 0){
			textColor = mContext.getResources().getColor(R.color.black);
			paint.setColor(textColor);
		}else{
			paint.setColor(textColor);
		}
		
		
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setTypeface(typeface);

		int width = (int) (paint.measureText(text, 0, text.length()) + 0.5f);
		int height = textSize + 24;

		Bitmap textBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(textBitmap);
		c.drawText(text, width / 2, height / 2, paint);

		return textBitmap;
	}
}
