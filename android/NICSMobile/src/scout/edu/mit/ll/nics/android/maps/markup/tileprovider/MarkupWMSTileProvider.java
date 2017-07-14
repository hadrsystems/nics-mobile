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
package scout.edu.mit.ll.nics.android.maps.markup.tileprovider;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import android.util.Log;

import com.google.android.gms.maps.model.UrlTileProvider;

public class MarkupWMSTileProvider extends UrlTileProvider {
	
	//"http://nowcoast.noaa.gov/wms/com.esri.wms.Esrimap/obs?service=wms&version=1.1.1&request=GetMap&layers=us_states_gen&styles=&bbox=%f,%f,%f,%f&width=256&height=256&srs=EPSG:3857&format=image/png&transparent=true";
	 
     
	 final String OSGEO_WMS = "http://129.55.46.30:8080/geoserver/NICS/wms?service=WMS&version=1.1.0&request=GetMap&layers=planet_osm_roads&styles=&bbox=%f,%f,%f,%f&width=256&height=256&srs=EPSG:3857&format=image/png&transparent=true";
	 
//    private static final double[] EPSG4326_TILE_ORIGIN = { -180.0000, 180.0000 };
//    private static final double EPSG4326_MAP_SIZE = 180.0000 * 2;
    
	  // Web Mercator n/w corner of the map.
    private static final double[] TILE_ORIGIN = { -20037508.34789244, 20037508.34789244 };
    // array indexes for that data
    private static final int ORIG_X = 0;
    private static final int ORIG_Y = 1; // "

    // Size of square world map in meters, using WebMerc projection.
    private static final double MAP_SIZE = 20037508.34789244 * 2;

    // array indexes for array to hold bounding boxes.
    protected static final int MINX = 0;
    protected static final int MAXX = 1;
    protected static final int MINY = 2;
    protected static final int MAXY = 3;

    // cql filters
    private String cqlString = "";

    // Construct with tile size in pixels, normally 256, see parent class.
    public MarkupWMSTileProvider(int x, int y) {
        super(x, y);
    }

    protected String getCql() {
        return URLEncoder.encode(cqlString);
    }

    public void setCql(String c) {
        cqlString = c;
    }

    // Return a web Mercator bounding box given tile x/y indexes and a zoom
    // level.
    protected double[] getBoundingBox(int x, int y, int zoom) {
        double tileSize = MAP_SIZE / Math.pow(2, zoom);
        double minx = TILE_ORIGIN[ORIG_X] + x * tileSize;
        double maxx = TILE_ORIGIN[ORIG_X] + (x + 1) * tileSize;
        double miny = TILE_ORIGIN[ORIG_Y] - (y + 1) * tileSize;
        double maxy = TILE_ORIGIN[ORIG_Y] - y * tileSize;

        double[] bbox = new double[4];
        bbox[MINX] = minx;
        bbox[MINY] = miny;
        bbox[MAXX] = maxx;
        bbox[MAXY] = maxy;

        return bbox;
    }

	@Override
	public URL getTileUrl(int x, int y, int zoom) {
		double[] bbox = getBoundingBox(x, y, zoom);
        String s = String.format(Locale.US, OSGEO_WMS, bbox[MINX], bbox[MINY], bbox[MAXX], bbox[MAXY]);
        Log.d("WMSDEMO", s);
        URL url = null;
        try {
            url = new URL(s);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
        return url;
	}

}
