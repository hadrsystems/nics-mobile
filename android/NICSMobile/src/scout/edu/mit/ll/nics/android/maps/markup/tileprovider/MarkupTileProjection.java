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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MarkupTileProjection {

	private int x;
	private int y;
	private int zoom;
	private int TILE_SIZE;

	private DoublePoint pixelOrigin_;
	private double pixelsPerLonDegree_;
	private double pixelsPerLonRadian_;

	MarkupTileProjection(int tileSize, int x, int y, int zoom) {
		this.TILE_SIZE = tileSize;
		this.x = x;
		this.y = y;
		this.zoom = zoom;
		
		pixelOrigin_ = new DoublePoint(TILE_SIZE / 2, TILE_SIZE / 2);
		pixelsPerLonDegree_ = TILE_SIZE / 360d;
		pixelsPerLonRadian_ = TILE_SIZE / (2 * Math.PI);
	}

	/** Get the dimensions of the Tile in LatLng coordinates */
	public LatLngBounds getTileBounds() {
		DoublePoint tileSW = new DoublePoint(x * TILE_SIZE, (y + 1) * TILE_SIZE);
		DoublePoint worldSW = pixelToWorldCoordinates(tileSW);
		LatLng SW = worldCoordToLatLng(worldSW);
		
		DoublePoint tileNE = new DoublePoint((x + 1) * TILE_SIZE, y * TILE_SIZE);
		DoublePoint worldNE = pixelToWorldCoordinates(tileNE);
		LatLng NE = worldCoordToLatLng(worldNE);
		
		return new LatLngBounds(SW, NE);
	}

	/**
	 * Calculate the pixel coordinates inside a tile, relative to the left upper
	 * corner (origin) of the tile.
	 */
	public void latLngToPoint(LatLng latLng, DoublePoint result) {
		latLngToWorldCoordinates(latLng, result);
		worldToPixelCoordinates(result, result);
		result.x -= x * TILE_SIZE;
		result.y -= y * TILE_SIZE;
	}

	private DoublePoint pixelToWorldCoordinates(DoublePoint pixelCoord) {
		int numTiles = 1 << zoom;
		DoublePoint worldCoordinate = new DoublePoint(pixelCoord.x / numTiles, pixelCoord.y / numTiles);
		return worldCoordinate;
	}

	/**
	 * Transform the world coordinates into pixel-coordinates relative to the
	 * whole tile-area. (i.e. the coordinate system that spans all tiles.)
	 * 
	 * 
	 * Takes the resulting point as parameter, to avoid creation of new objects.
	 */
	private void worldToPixelCoordinates(DoublePoint worldCoord, DoublePoint result) {
		int numTiles = 1 << zoom;
		result.x = worldCoord.x * numTiles;
		result.y = worldCoord.y * numTiles;
	}

	private LatLng worldCoordToLatLng(DoublePoint worldCoordinate) {
		DoublePoint origin = pixelOrigin_;
		double lng = (worldCoordinate.x - origin.x) / pixelsPerLonDegree_;
		double latRadians = (worldCoordinate.y - origin.y) / -pixelsPerLonRadian_;
		double lat = Math.toDegrees(2 * Math.atan(Math.exp(latRadians)) - Math.PI / 2);
		
		return new LatLng(lat, lng);
	}

	/**
	 * Get the coordinates in a system describing the whole globe in a
	 * coordinate range from 0 to TILE_SIZE (type double).
	 * 
	 * Takes the resulting point as parameter, to avoid creation of new objects.
	 */
	private void latLngToWorldCoordinates(LatLng latLng, DoublePoint result) {
		DoublePoint origin = pixelOrigin_;

		result.x = origin.x + latLng.longitude * pixelsPerLonDegree_;

		// Truncating to 0.9999 effectively limits latitude to 89.189. This is
		// about a third of a tile past the edge of the world tile.
		double siny = bound(Math.sin(Math.toRadians(latLng.latitude)), -0.9999, 0.9999);
		result.y = origin.y + 0.5 * Math.log((1 + siny) / (1 - siny)) * -pixelsPerLonRadian_;
	};

	/** Return value reduced to min and max if outside one of these bounds. */
	private double bound(double value, double min, double max) {
		value = Math.max(value, min);
		value = Math.min(value, max);
		
		return value;
	}

	/** A Point in an x/y coordinate system with coordinates of type double */
	public static class DoublePoint {
		double x;
		double y;

		public DoublePoint(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

}
