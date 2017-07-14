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
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.util.Log;

import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.data.MarkupFeature;
import scout.edu.mit.ll.nics.android.api.data.Vector2;


public class MarkupFireLine extends MarkupBaseShape {
	
	public MarkupFireLine(DataManager manager, String title, LatLng coordinate, int[] strokeColor) {
		super(manager);

		setTitle(title);
		setPoint(coordinate);
		setType(MarkupType.sketch);

		
		setStrokeColor(strokeColor);
	}

	public MarkupFireLine(DataManager manager, MarkupFeature item, int[] strokeColor, float zoom) {
		super(manager);
	
		mFeature = item;

		setTitle(item.getLabelText());
		setType(MarkupType.sketch);
		setTime(item.getSeqTime());
		
		if (item.getDashStyle().equals("fire-edge-line")) {
			int[] redStrokeColor = new int[] {255, 255, 0, 0};
			setStrokeColor(redStrokeColor);
		} else {
			Log.v("IMPORTANT", item.getDashStyle());
			setStrokeColor(strokeColor);
		}
		
		setCreator(item.getUsername());
		setDraft(false);
		setFeatureId(item.getFeatureId());
		setFeature(item);
		
		ArrayList<Vector2> points = item.getGeometryVector2();
		Vector2 point;
		LatLng coordinate;

		final ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
		for (int i = 0; i < points.size(); i++) {
			point = points.get(i);
			coordinate = new LatLng(point.x, point.y);
			coordinates.add(coordinate);
		}
		
		setPoints(coordinates);
	}

	public void setPoint(LatLng coordinate) {
		
		if(mPoints == null || mPoints.size() == 0) {
			addPoint(coordinate);
		} else {
			mPoints.set(0, coordinate);
		}
	}

	public GroundOverlayOptions getOptions() {
		return null;
	}

	@Override
	public void removeFromMap() {
	}

	@Override
	public boolean removeLastPoint() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNicsType() {
		return "sketch";
	}

	public Path makeCircle(float radius) {
		Path p = new Path();
		p.addCircle(0, 0, radius, Path.Direction.CCW);
		return p;
	}

	public Path makeRectangle(float width, float length) {
		Path p = new Path();
		p.addRect(0 - width / 2, length / 2, width / 2, -length / 2, Direction.CCW);
		p.close();
		return p;
	}

	public Path makeDash(float width) {
		Path p = new Path();
		p.addRect(5, -5, 1, -width + 5, Direction.CCW);
		p.close();
		return p;
	}

	public Path makeCross(float size) {
		Path p = new Path();
		int i = -5;
		p.moveTo(5, 5);
		while (i < 5) {
			p.addCircle(i, -i, size, Direction.CCW);
			p.addCircle(-i, -i, size, Direction.CCW);
			i++;
		}

		return p;
	}

	public Path makeCrossWithCircle(float size) {
		Path p = new Path();
		int i = -5;
		p.moveTo(5, 5);
		while (i < 5) {
			p.addCircle(i, -i, size, Direction.CCW);
			p.addCircle(-i, -i, size, Direction.CCW);
			i++;
		}

		p.addCircle(15, -2, size * 2, Direction.CCW);
		return p;
	}

	@Override
	public void setIcon(Bitmap bitmap, int[] color) {
	}

	@Override
	public void showInfoWindow() {
	}

}
