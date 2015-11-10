/*|~^~|Copyright (c) 2008-2015, Massachusetts Institute of Technology (MIT)
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
package edu.mit.ll.phinics.android.api.data;

import java.util.ArrayList;
import java.util.Date;

import android.text.format.DateFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class MarkupFeature {
	
	private long collabRoomId;
	private long id;
	@Expose private String dashStyle;
	@Expose private String featureattributes;
	private String featureId;
	@Expose private String fillColor;
	@Expose private String graphic;
	@Expose private double graphicHeight;
	@Expose private double graphicWidth;
	@Expose private boolean gesture;
	@Expose private String ip;
	@Expose private double labelSize;
	@Expose private String labelText;
	@Expose private String username;
	@Expose private long seqNum;
	@Expose private String strokeColor;
	@Expose private double strokeWidth;
	@Expose private long seqTime;
	private long lastUpdateUtm;
	@Expose private String lastupdate;
	@Expose private String topic;
	@Expose private String type;
	@Expose private Double opacity;
	@Expose private String geometry;
	private ArrayList<Vector2> geometryVector2;
	@Expose private double radius;
	@Expose private double rotation;
	private boolean seen = false;
	private boolean isRendered = false;
	
	public long getCollabRoomId() {
		return collabRoomId;
	}

	public void setCollabRoomId(long collabRoomId) {
		this.collabRoomId = collabRoomId;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getDashStyle() {
		return dashStyle;
	}
	
	public void setDashStyle(String dashStyle) {
		this.dashStyle = dashStyle;
	}
	
	public String getfeatureattributes() {
		return featureattributes;
	}
	
	public void setfeatureattributes(String featureattributes) {
		this.featureattributes = featureattributes;
	}
	public String getFeatureId() {
		return featureId;
	}
	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}
	public String getFillColor() {
		return fillColor;
	}
	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}
	public String getGraphic() {
		return graphic;
	}
	public void setGraphic(String graphic) {
		this.graphic = graphic;
	}
	public Double getGraphicHeight() {
		return graphicHeight;
	}
	public void setGraphicHeight(Double graphicHeight) {
		this.graphicHeight = graphicHeight;
	}
	public Double getGraphicWidth() {
		return graphicWidth;
	}
	public void setGraphicWidth(Double graphicWidth) {
		this.graphicWidth = graphicWidth;
	}
	public Boolean getgesture() {
		return gesture;
	}
	public void setgesture(Boolean gesture) {
		this.gesture = gesture;
	}
	public String getip() {
		return ip;
	}
	public void setip(String ip) {
		this.ip = ip;
	}
	public Double getLabelSize() {
		return labelSize;
	}
	public void setLabelSize(Double labelSize) {
		this.labelSize = labelSize;
	}
	public String getLabelText() {
		return labelText;
	}
	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Long getSeqNum() {
		return seqNum;
	}
	public void setSeqNum(Long seqNum) {
		this.seqNum = seqNum;
	}
	public String getStrokeColor() {
		return strokeColor;
	}
	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}
	public Double getStrokeWidth() {
		return strokeWidth;
	}
	public void setStrokeWidth(Double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
	public Long getTime() {
		return seqTime;
	}
	public void setTime(Long time) {
		this.seqTime = time;
	}
	
	public String getlastupdate() {
		return lastupdate;
	}
	public void setlastupdate(String time) {
		this.lastupdate = time;
	}
	
	public Long getlastupdateUtm() {
		return lastUpdateUtm;
	}
	public void setlastupdateUtm(Long time) {
		this.lastUpdateUtm = time;
	}
	
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Double getOpacity() {
		if(opacity == null) {
			opacity = -1.0;
		}
		return opacity;
	}
	public void setOpacity(Double opacity) {
		this.opacity = opacity;
	}
	
	public ArrayList<Vector2>  getGeometryVector2() {
		return geometryVector2;
	}
	
	public String getPointsString() {
		boolean first = true;
		
		StringBuilder builder = new StringBuilder();
		
		for(Vector2 coords : geometryVector2) {
			if(!first) {
				builder.append(',');
			} else {
				first = false;
			}
			builder.append(coords.x);
			builder.append(' ');
			builder.append(coords.y);
		}
		return builder.toString();
	}
	
	public void setPointsString(String geometryVecotr2) {
		if(this.geometryVector2 == null){
			this.geometryVector2 = new ArrayList<Vector2>();
		}
		
		this.geometryVector2.clear();
		String[] coords = geometry.split(",");
		for(String coord : coords) {
			String[] splitCoord = coord.split(" ");
//			ArrayList<Vector2> array = new ArrayList<Vector2>();
			if(!splitCoord[0].equals("") && !splitCoord[1].equals("")) {
				this.geometryVector2.add(new Vector2(
									Double.valueOf(splitCoord[0]),
									Double.valueOf(splitCoord[1]) ));
				
			}
		}
	}
	public void setGeometryVector2(ArrayList<Vector2> geometryVector2) {
		this.geometryVector2 = geometryVector2;
	}
	public String getGeometryString(){
		return this.geometry;
	}
	public void setGeometryString(String geometry){
		this.geometry = geometry;
	}
	public Double getRadius() {
		return radius;
	}
	public void setRadius(Double radius) {
		this.radius = radius;
	}
	public Double getRotation() {
		return rotation;
	}
	public void setRotation(Double rotation) {
		this.rotation = rotation;
	}
	
	public String toJsonString() {
		return new GsonBuilder().serializeSpecialFloatingPointValues().excludeFieldsWithoutExposeAnnotation().create().toJson(this);
	}
	
	public String toFullJsonString() {
		return new GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(this);
	}

	public boolean isSeen() {
		return seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	public boolean isRendered() {
		return isRendered;
	}

	public void setRendered(boolean isRendered) {
		this.isRendered = isRendered;
	}
	
	public void buildVector2Point(){
		String geomTemp = geometry;
		
		geomTemp = geomTemp.replace("(","");
		geomTemp = geomTemp.replace(")","");

		geomTemp = geomTemp.replace("POLYGON","");
		geomTemp = geomTemp.replace("POINT","");
		geomTemp = geomTemp.replace("LINESTRING","");
		
		String[] separatedCommas = geomTemp.split(",");
		
		ArrayList<Vector2> fullySeperated = new ArrayList<Vector2>();
		for(int i = 0; i < separatedCommas.length;i++){
		
			String[] seperateSpaces = separatedCommas[i].split(" ");
			fullySeperated.add(new Vector2(
											Double.valueOf(seperateSpaces[1]),
											Double.valueOf(seperateSpaces[0])
//					44.12682020538505,-73.2795182710489
											));
		}
		
		lastUpdateUtm = System.currentTimeMillis();
		setGeometryVector2(fullySeperated);
	}
	
	@Override
	public String toString() {
		String attributesString = getfeatureattributes();
		
		if(attributesString != null && !attributesString.isEmpty()) {
			FeatureAttributes attributes = new Gson().fromJson(getfeatureattributes(), FeatureAttributes.class);

			return /*(isDraft ? "<Draft> " : "") + */ DateFormat.format("MM/dd kk:mm:ss", new Date(lastUpdateUtm)) + " - " + username + "\nType: " + type.toString().toLowerCase() + attributes.toString();
		}
		
		return /*(isDraft ? "<Draft> " : "") + */ DateFormat.format("MM/dd kk:mm:ss", new Date(lastUpdateUtm)) + " - " + username + "\nType: " + type.toString().toLowerCase();
	}
}
