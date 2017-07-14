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
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.data.MarkupFeature;
import scout.edu.mit.ll.nics.android.api.data.Vector2;


public class MarkupRectangle extends MarkupBaseShape {
	
	private PolygonOptions mPolygonOptions;
	private Polygon mPolygon;
	
	public MarkupRectangle(DataManager manager, String title, LatLng coordinate, int[] strokeColor) {
		super(manager);
		addPoint(coordinate);
		
		mPolygonOptions = new PolygonOptions();
		mPolygonOptions.addAll(mPoints);
		mPolygonOptions.strokeWidth(5);
		mPolygonOptions.strokeColor(Color.rgb(strokeColor[1], strokeColor[2], strokeColor[3]));

		setTitle(title);
		setType(MarkupType.square);
		setIcon(null, strokeColor);
	}
	
	public MarkupRectangle(DataManager manager, MarkupFeature item, int[] strokeColor, int[] fillColor) {
		super(manager);

		setId(item.getId());
		for(Vector2 point : item.getGeometryVector2()) {
			LatLng coordinate = new LatLng(point.x, point.y);
			addPoint(coordinate);		
		}
		
		mPolygonOptions = new PolygonOptions();
		mPolygonOptions.addAll(mPoints);
		mPolygonOptions.strokeWidth(5);
		mPolygonOptions.strokeColor(Color.rgb(strokeColor[1], strokeColor[2], strokeColor[3]));
		mPolygonOptions.fillColor(Color.argb(fillColor[0], fillColor[1], fillColor[2], fillColor[3]));

		setTitle(item.getLabelText());
		setType(MarkupType.square);
		setTime(item.getSeqTime());
		setCreator(item.getUsername());
		setDraft(false);
		setFeatureId(item.getFeatureId());
		setFeature(item);
	}

	public void addPoint(LatLng point) {
		mPoints.add(point);
		
		if(mPolygon != null) {
			mPolygon.setPoints(mPoints);
		}
	}

	public PolygonOptions getOptions() {
		if(mPolygonOptions != null) {
			PolygonOptions updatedOptions = new PolygonOptions();
			updatedOptions.addAll(mPoints);
			updatedOptions.strokeWidth(5);
			updatedOptions.strokeColor(mPolygonOptions.getStrokeColor());
			updatedOptions.fillColor(mPolygonOptions.getFillColor());
			mPolygonOptions = updatedOptions;
		}
		return mPolygonOptions;
	}

	public void setPolygon(Polygon polygon) {
		mPolygon = polygon;
	}
	
	@Override
	public void removeFromMap() {
		if(mPolygon != null) {
			mPolygon.remove();
		}
		
		clearMarkers();
	}
	
	@Override
	public void setStrokeColor(int[] color) {
		super.setStrokeColor(color);
		if(color != null && mPolygon != null) {
			mPolygon.setStrokeColor(Color.rgb(color[1], color[2], color[3]));
		}
	}

	@Override
	public void setIcon(Bitmap symbolBitmap, int[] color) {
		setStrokeColor(color);
	}

	@Override
	public void showInfoWindow() {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public boolean removeLastPoint() {
		boolean success = false;
		if(mPoints != null && mPoints.size() > 0) {
			int idx = mPoints.size()-1;
			

			if(mType == MarkupType.square && idx > 0) { 
				while(idx > 0) {
					mPoints.remove(idx);
					removeMarker(idx);
					idx = mPoints.size()-1;
				}
			} else {
				mPoints.remove(idx);
				removeMarker(idx);
			}
			
			
			if(mPolygon != null) {
				if(mPoints.size() > 0) {
					mPolygon.setPoints(mPoints);
				} else  {
					mPolygon.remove();
				}
				success = true;
			}
		}
		
		if(mPoints.size() == 0) {
			success = false;
		}
		
		return success;
	}

	@Override
	public String getNicsType() {
		return "square";
	}
	
	@Override
	public void setPoints(ArrayList<LatLng> points) {
		super.setPoints(points);
		mPolygon.setPoints(points);
	}
}
