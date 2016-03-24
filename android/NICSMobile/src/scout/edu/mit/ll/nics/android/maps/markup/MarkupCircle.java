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

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.data.MarkupFeature;
import scout.edu.mit.ll.nics.android.api.data.Vector2;


public class MarkupCircle extends MarkupBaseShape {
	
	private CircleOptions mCircleOptions;
	private Circle mCircle;
	
	
	public MarkupCircle(DataManager manager, String title, LatLng coordinate, double radius, int[] strokeColor) {
		super(manager);
		mCircleOptions = new CircleOptions();
		mCircleOptions.center(coordinate);
		mCircleOptions.radius(radius);
		mCircleOptions.strokeWidth(5);
		mCircleOptions.strokeColor(Color.rgb(strokeColor[1], strokeColor[2], strokeColor[3]));

		setTitle(title);
		setRadius(radius);
		setPoint(coordinate);
		setType(MarkupType.circle);
		setIcon(null, strokeColor);
	}

	public MarkupCircle(DataManager manager, MarkupFeature item, int[] strokeColor, int[] fillColor) {
		super(manager);

		setId(item.getId());
		
		LatLng coordinate = null;
		for(Vector2 point : item.getGeometryVector2()) {
			coordinate = new LatLng(point.x,point.y);
		}
		
		mCircleOptions = new CircleOptions();
		mCircleOptions.center(coordinate);
		mCircleOptions.radius(item.getRadius());
		mCircleOptions.strokeWidth(5);
		mCircleOptions.strokeColor(Color.rgb(strokeColor[1], strokeColor[2], strokeColor[3]));
		mCircleOptions.fillColor(Color.argb(fillColor[0], fillColor[1], fillColor[2], fillColor[3]));

		setTitle(item.getLabelText());
		
		//double radius = Math.toDegrees((mRadius / 1000.0) / 6371.0); //earth radius
		double radius = Math.toRadians(item.getRadius()) * 6371.0 * 1000.0;
		
		setRadius(radius);
		setPoint(coordinate);
		setType(MarkupType.circle);
		setTime(item.getSeqTime());
		setCreator(item.getUsername());
		setIcon(null, strokeColor);
		setDraft(false);
		setFeatureId(item.getFeatureId());
		setFeature(item);
	}

	public void setPoint(LatLng coordinate) {
		
		if(mCircle != null) {
			mCircle.setCenter(coordinate);
		}
		
		if(mPoints == null || mPoints.size() == 0) {
			addPoint(coordinate);
		} else {
			mPoints.set(0, coordinate);
		}
	}

	public CircleOptions getOptions() {
		return mCircleOptions;
	}

	public void setCircle(Circle circle) {
		mCircle = circle;
	}
	
	@Override
	public void removeFromMap() {
		if(mCircle != null) {
			mCircle.remove();
		}
	}

	@Override
	public void setIcon(Bitmap symbolBitmap, int[] color) {
		setStrokeColor(color);
	}
	
	@Override
	public void setStrokeColor(int[] color) {
		super.setStrokeColor(color);
		if(color != null && mCircle != null) {
			mCircle.setStrokeColor(Color.rgb(color[1], color[2], color[3]));
		}
	}
	
	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject json = super.toJson();
		json.put("radius", getRadius());
		
		return json;
	}

	@Override
	public boolean removeLastPoint() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNicsType() {
		return "circle";
	}

	@Override
	public void showInfoWindow() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setRadius(double radius) {
		super.setRadius(radius);
		mCircleOptions.radius(radius);
	}
}
