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

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class UxoReportData {
	
	private String user;
	
	@SerializedName("ur-reportingunit")
	private String ReportingUnit;
	
	@SerializedName("ur-reportinglocation")
	private String ReportingLocation;

	@SerializedName("ur-latitude")
	private String Latitude;
	
	@SerializedName("ur-longitude")
	private String Longitude;
	
	@SerializedName("ur-contactinfo")
	private String ContactInfo;
	
	@SerializedName("ur-uxotype")
	private UxoTypes UxoType;
	
	@SerializedName("ur-size")
	private String Size;
	
    @SerializedName("ur-shape")
    private String Shape;
    
    @SerializedName("ur-color")
    private String Color;
    
    @SerializedName("ur-condition")
    private String Condition;
    
    @SerializedName("ur-cbrncontamination")
    private String CbrnContamination;
    
    @SerializedName("ur-resourcethreatened")
    private String ResourceThreatened;

    @SerializedName("ur-impactonmission")
    private String ImpactOnMission;
    
    @SerializedName("ur-protectivemeasures")
    private String ProtectiveMeasures;
    
    @SerializedName("ur-recommendedpriority")
    private UxoPriorityTypes RecommendedPriority;
    
    @SerializedName("ur-fullPath")
    private String fullPath;
    
    
	public UxoReportData() {
	}
	
	public UxoReportData(UxoReportFormData messageData) {
		user = messageData.getUser();

		ReportingUnit = messageData.getReportingUnit();
		ReportingLocation = messageData.getReportingLocation();
		Latitude = messageData.getLatitude();
		Longitude = messageData.getLongitude();
		ContactInfo = messageData.getContactInfo();
		UxoType =UxoTypes.lookUp(messageData.getUxoType());
		Size = messageData.getSize();
		
		Shape = messageData.getShape();
		Color = messageData.getColor();
		Condition = messageData.getCondition();
		
		CbrnContamination = messageData.getCbrnContamination();
		ResourceThreatened = messageData.getResourceThreatened();
		ImpactOnMission = messageData.getImpactOnMission();
		ProtectiveMeasures = messageData.getProtectiveMeasures();
		RecommendedPriority = UxoPriorityTypes.lookUp(messageData.getRecommendedPriority());

		fullPath = messageData.getFullPath();

	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public String getReportingUnit() {
		return ReportingUnit;
	}

	public void setReportingUnit(String reportingUnit) {
		this.ReportingUnit = reportingUnit;
	}

	public String getReportingLocation() {
		return ReportingLocation;
	}

	public void setReportingLocation(String reportingLocation) {
		this.ReportingLocation = reportingLocation;
	}

	public void setLatitude(String lat){
		Latitude = lat;
	}
	
	public String getLatitude(){
		return Latitude;
	}
	
	public void setLongitude(String lon){
		Longitude = lon;
	}
	
	public String getLongitude(){
		return Longitude;
	}
	
	public String getContactInfo() {
		return ContactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.ContactInfo = contactInfo;
	}

	public UxoTypes getUxoType() {
		return UxoType;
	}

	public void setUxoType(UxoTypes UxoType) {
		this.UxoType = UxoType;
	}

	public String getSize() {
		return Size;
	}

	public void setSize(String Size) {
		this.Size = Size;
	}

	public String getShape() {
		return Shape;
	}

	public void setShape(String Shape) {
		this.Shape = Shape;
	}

	public String getColor() {
		return Color;
	}

	public void setColor(String Color) {
		this.Color = Color;
	}

	public String getCondition() {
		return Condition;
	}

	public void setCondition(String Condition) {
		this.Condition = Condition;
	}

	public String getCbrnContamination() {
		return CbrnContamination;
	}

	public void setCbrnContamination(String CbrnContamination) {
		this.CbrnContamination = CbrnContamination;
	}

	public String getResourceThreatened() {
		return ResourceThreatened;
	}

	public void setResourceThreatened(String ResourceThreatened) {
		this.ResourceThreatened = ResourceThreatened;
	}
	
	public String getImpactOnMission() {
		return ImpactOnMission;
	}
	
	public void setImpactOnMission(String ImpactOnMission) {
		this.ImpactOnMission = ImpactOnMission;
	}
	
	public String getProtectiveMeasures() {
		return ProtectiveMeasures;
	}

	public void setProtectiveMeasures(String ProtectiveMeasures) {
		this.ProtectiveMeasures = ProtectiveMeasures;
	}
	
	public UxoPriorityTypes getRecommendedPriority() {
		return RecommendedPriority;
	}
	
	public void setRecommendedPriority(UxoPriorityTypes RecommendedPriority) {
		this.RecommendedPriority = RecommendedPriority;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String FullPath) {
		this.fullPath = FullPath;
	}
	
	public String toJsonString() {
		try {			
			JSONObject object = new JSONObject(new Gson().toJson(this));
			object.remove("ur-recommendedpriority");
			object.remove("ur-uxotype");
			
			String priorityValue = RecommendedPriority.getText();
			if(priorityValue == null){
				priorityValue = UxoPriorityTypes.Immediate.getText();
			}
			
			String typeValue = UxoType.getText();
			if(typeValue == null){
				typeValue = UxoTypes.Dropped.getText();
			}
			
			object.put("ur-recommendedpriority", priorityValue);
			object.put("ur-uxotype",typeValue);
			return object.toString();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Gson().toJson(this);
		}

	}
	
}
