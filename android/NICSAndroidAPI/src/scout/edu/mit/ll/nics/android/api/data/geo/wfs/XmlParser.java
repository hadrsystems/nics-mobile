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
package scout.edu.mit.ll.nics.android.api.data.geo.wfs;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class XmlParser {

	    public ArrayList<Feature> parse(InputStream in) throws XmlPullParserException, IOException {
	        try {
	            XmlPullParser parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(in, null);
	            parser.nextTag();
	            return readFeed(parser);
	        } finally {
	            in.close();
	        }
	    }

	    private ArrayList<Feature> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
	    	ArrayList<Feature> features = new ArrayList<Feature>();

//	        parser.require(XmlPullParser.START_TAG, ns, "feed");
	        while (parser.next() != XmlPullParser.END_DOCUMENT) {
	            if (parser.getEventType() != XmlPullParser.START_TAG) {
	                continue;
	            }
	            String name = parser.getName();
	            // Starts by looking for the entry tag
	            if (name.equals("gml:featureMember")) {
	              //  entries.add(readEntry(parser));
	            	
	            	Feature feature = new Feature();
	            		
	            	parser.next();
	            	feature.setType(parser.getName());
	            	
            		HashMap<String, Object> map = new HashMap<String, Object>();
            		
            		int eventType = parser.next();
            		String title = "";
            		Object value = "";
            		
            		feature.setGeometry(new Geometry());
            		feature.getGeometry().setCoordinates(new ArrayList<Double>());
            		feature.getGeometry().getCoordinates().add(0.0);
            		feature.getGeometry().getCoordinates().add(0.0);
            		
            		boolean parsing = true;
            		while (parsing) {	
            			
            			if(eventType == XmlPullParser.START_TAG){
            				title = parser.getName();
            			}else if(eventType == XmlPullParser.END_TAG){
            				if(Pattern.compile(Pattern.quote("objectid"), Pattern.CASE_INSENSITIVE).matcher(title).find()){
            					feature.setId(value.toString());
            					map.put("id", value.toString());
            				}else if(Pattern.compile(Pattern.quote("lat"), Pattern.CASE_INSENSITIVE).matcher(title).find()){
            					double lat = Double.parseDouble(value.toString());
            					feature.getGeometry().getCoordinates().set(1,lat);
            				}else if(Pattern.compile(Pattern.quote("lon"), Pattern.CASE_INSENSITIVE).matcher(title).find()){
            					double lon = Double.parseDouble(value.toString());
            					feature.getGeometry().getCoordinates().set(0,lon);
            				}else if(Pattern.compile(Pattern.quote("heading"), Pattern.CASE_INSENSITIVE).matcher(title).find()){
            					map.put("course", value.toString());
            				}else if(Pattern.compile(Pattern.quote("speed"), Pattern.CASE_INSENSITIVE).matcher(title).find()){
            					map.put("speed", value.toString());
            				}else if(Pattern.compile(Pattern.quote("datetime"), Pattern.CASE_INSENSITIVE).matcher(title).find()){
            					map.put("xmltime", value.toString());
            				}else if(Pattern.compile(Pattern.quote("last_updated"), Pattern.CASE_INSENSITIVE).matcher(title).find()){
            					map.put("xmltime", value.toString());
            				}else if(Pattern.compile(Pattern.quote("name"), Pattern.CASE_INSENSITIVE).matcher(title).find() &&
            						Pattern.compile(Pattern.quote("location"), Pattern.CASE_INSENSITIVE).matcher(title).find() == false){
            					map.put("name", value.toString());
            				}else{
            					map.put(title, value.toString());
            				}
            				title = "";
            				value = "";
            			}else if(eventType == XmlPullParser.TEXT){
            				value = parser.getText();
            			}
            			
            			eventType = parser.next();
            			
            			if(eventType == XmlPullParser.END_TAG && parser.getName().equals("gml:featureMember")){
            				parsing = false;
            			}
            		}
            		
            		feature.setProperties(map);
            		features.add(feature);
	            		
	            } else {
	                skip(parser);
	            }
	        }
	        return features;
	    }

	    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
	    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
	    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
	    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            throw new IllegalStateException();
	        }
	        int depth = 1;
	        while (depth != 0) {
	            switch (parser.next()) {
	            case XmlPullParser.END_TAG:
	                    depth--;
	                    break;
	            case XmlPullParser.START_TAG:
	                    depth++;
	                    break;
	            }
	        }
	    }
}
