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
package scout.edu.mit.ll.nics.android.maps.markup.tileprovider;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PathDashPathEffect;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import scout.edu.mit.ll.nics.android.maps.markup.MarkupFireLine;
import scout.edu.mit.ll.nics.android.maps.markup.tileprovider.MarkupTileProjection.DoublePoint;

public class MarkupFeatureTileProvider extends MarkupCanvasTileProvider {

	private CopyOnWriteArrayList<MarkupFireLine> mFirelineFeatures;

	public MarkupFeatureTileProvider() {
		mFirelineFeatures = new CopyOnWriteArrayList<MarkupFireLine>();
	}

	public void setFirelineFeatures(ArrayList<MarkupFireLine> features) {
		mFirelineFeatures.clear();
		mFirelineFeatures.addAll(features);
	}

	@Override
	void onDraw(Canvas canvas, MarkupTileProjection projection) {

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setColor(Color.RED);

		for (MarkupFireLine feature : mFirelineFeatures) {
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setStyle(Style.STROKE);
			int red = feature.getStrokeColor()[1];
			int green = feature.getStrokeColor()[2];
			int blue = feature.getStrokeColor()[3];
			paint.setColor(Color.rgb(red, green, blue));
			LatLngBounds bounds = projection.getTileBounds();

			DoublePoint sw = new DoublePoint(0, 0);
			DoublePoint ne = new DoublePoint(0, 0);

			projection.latLngToPoint(bounds.southwest, sw);
			projection.latLngToPoint(bounds.northeast, ne);

			ArrayList<LatLng> coordinates = new ArrayList<LatLng>(feature.getPoints());

			Path path = new Path();
			float[] floatPoints = new float[feature.getPoints().size() * 2];
			DoublePoint pt = new DoublePoint(0, 0);
			for (int i = 0; i < coordinates.size(); i++) {
				LatLng coord = coordinates.get(i);
				projection.latLngToPoint(coord, pt);
				floatPoints[(i * 2)] = (float) pt.x;
				floatPoints[(i * 2) + 1] = (float) pt.y;

				if (i == 0) {
					path.moveTo(floatPoints[0], floatPoints[1]);
				} else {
					path.lineTo(floatPoints[i * 2], floatPoints[(i * 2) + 1]);
				}
			}

			String dashStyle = feature.getFeature().getDashStyle();

			if (dashStyle.equals("plannedFireline")) {
				paint.setPathEffect(new PathDashPathEffect(makeRectangle(10, 10), 30, 0, PathDashPathEffect.Style.ROTATE));
			} else if (dashStyle.equals("secondaryFireline")) {
				paint.setPathEffect(new PathDashPathEffect(makeCircle(4), 30, 0, PathDashPathEffect.Style.ROTATE));
			} else if (dashStyle.equals("fireSpreadPrediction")) {
				paint.setColor(Color.rgb(247, 148, 30));
				paint.setStrokeWidth(5);
			} else if (dashStyle.equals("completedDozer")) {
				paint.setPathEffect(new PathDashPathEffect(makeCross(2), 15, 0, PathDashPathEffect.Style.ROTATE));
			} else if (dashStyle.equals("proposedDozer")) {
				paint.setPathEffect(new PathDashPathEffect(makeCrossWithCircle(2), 30, 0, PathDashPathEffect.Style.ROTATE));
			} else if (dashStyle.equals("fire-edge-line")) {
				paint.setColor(Color.RED);
				paint.setStrokeWidth(5);
				canvas.drawPath(path, paint);
				paint.setPathEffect(new PathDashPathEffect(makeDash(25), 10, 0, PathDashPathEffect.Style.ROTATE));
			} else if (dashStyle.equals("map")) {
				path.addCircle(floatPoints[0], floatPoints[1] + 2, 4, Direction.CW);
				path.addCircle(floatPoints[floatPoints.length - 2], floatPoints[floatPoints.length - 1] - 2, 4, Direction.CCW);
				paint.setStrokeWidth(8);
				canvas.drawPath(path, paint);
				paint.setStrokeWidth(5);
				paint.setColor(Color.rgb(247, 148, 30));
			} else if(dashStyle.equals("dash")) {
				int[] strokeColor = feature.getStrokeColor();
				paint.setColor(Color.rgb(strokeColor[1], strokeColor[2], strokeColor[3]));
				paint.setStrokeWidth(feature.getFeature().getStrokeWidth().floatValue());
				paint.setPathEffect(new DashPathEffect(new float[]{20,10}, 0));
			}

			canvas.drawPath(path, paint);
		}
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

}
