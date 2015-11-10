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

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class UxoReportFormData {

	private String user;
	
	// Property Owner information
	@SerializedName("reportingunit")
	private String ReportingUnit;
	
	@SerializedName("reportinglocation")
	private String ReportingLocation;
	
	private String coordinate;
	
	@SerializedName("latitude")
	private String Latitude;
	
	@SerializedName("longitude")
	private String Longitude;
	
	@SerializedName("contactinfo")
	private String ContactInfo;
	
	@SerializedName("uxotype")
	private int UxoType;
	
	@SerializedName("size")
	private String Size;

	// Property Information
	@SerializedName("shape")
    private String Shape;
	
	@SerializedName("color")
    private String Color;
    
	@SerializedName("condition")
    private String Condition;
    
	@SerializedName("cbrncontamination")
	private String CbrnContamination;
	
	@SerializedName("resourcethreatened")
    private String ResourceThreatened;
	
	@SerializedName("impactonmission")
    private String ImpactOnMission;
	
	@SerializedName("protectivemeasures")
    private String ProtectiveMeasures;
	
	@SerializedName("recommendedpriority")
    private int RecommendedPriority;

	@SerializedName("fullPath")
	private String fullPath;
    
		
	public UxoReportFormData(UxoReportData messageData) {
		user = messageData.getUser();

		ReportingUnit = messageData.getReportingUnit();
		ReportingLocation = messageData.getReportingLocation();
		Latitude = messageData.getLatitude();
		Longitude = messageData.getLongitude();
		coordinate = Latitude + ";" + Longitude;
		ContactInfo = messageData.getContactInfo();
		UxoType = messageData.getUxoType().getId();
		Size = messageData.getSize();
		
		Shape = messageData.getShape();
		Color = messageData.getColor();
		Condition = messageData.getCondition();
		
		CbrnContamination = messageData.getCbrnContamination();
		ResourceThreatened = messageData.getResourceThreatened();
		ImpactOnMission = messageData.getImpactOnMission();
		ProtectiveMeasures = messageData.getProtectiveMeasures();
		RecommendedPriority = messageData.getRecommendedPriority().getId();

		if(messageData.getRecommendedPriority() != null) {
			RecommendedPriority = messageData.getRecommendedPriority().getId();
		} else {
			RecommendedPriority = UxoPriorityTypes.Immediate.getId();
		}
		
		
		fullPath = messageData.getFullPath();
	}
	
	public String getUser() {
		return user;
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

	public String getLatitude() {
		if(coordinate != null && !coordinate.isEmpty()) {
			String[] coordinateArray = coordinate.split(";");
			this.Latitude = coordinateArray[0];
		}
		return Latitude;
	}

	public void setLatitude(String latitude) {
		this.Latitude = latitude;
	}
	
	public String getLongitude() {
		if(coordinate != null && !coordinate.isEmpty()) {
			String[] coordinateArray = coordinate.split(";");
			this.Longitude = coordinateArray[1];
		}
		return Longitude;
	}

	public void setLongitude(String longitude) {
		this.Longitude = longitude;
	}
	
	public String getCoordinate() {
		return coordinate;
	}

	public void setPropertyCoordinate(String propertyCoordinate) {
		if(propertyCoordinate != null && !propertyCoordinate.isEmpty()) {
			String[] coordinateArray = propertyCoordinate.split(";");
			
			this.Latitude = coordinateArray[0];
			this.Longitude = coordinateArray[1];
			
			this.coordinate = propertyCoordinate;
		}
	}
	
	public String getContactInfo() {
		return ContactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.ContactInfo = contactInfo;
	}

	public int getUxoType() {
		return UxoType;
	}

	public void setUxoType(int UxoType) {
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
	
	public int getRecommendedPriority() {
		return RecommendedPriority;
	}
	
	public void setRecommendedPriority(int RecommendedPriority) {
		this.RecommendedPriority = RecommendedPriority;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String FullPath) {
		this.fullPath = FullPath;
	}
	
	public String toJsonString() {
		return new Gson().toJson(this);
	}
}
