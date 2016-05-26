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
package scout.edu.mit.ll.nics.android.api.data;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

public class DamageReportFormData {

	private String user;
	private String userfull;
	private String status;
	
	// Property Owner information
	@SerializedName("last_name")
	private String ownerLastName;
	
	@SerializedName("first_name")
	private String ownerFirstName;
	
	@SerializedName("landline_phone")
	private String ownerLandlinePhone;
	
	@SerializedName("cell_phone")
	private String ownerCellPhone;
	
	@SerializedName("email")
	private String ownerEmail;

	// Property Information
	@SerializedName("address")
    private String propertyAddress;
	
	@SerializedName("city")
    private String propertyCity;
    
	@SerializedName("zip_code")
    private String propertyZipCode;
    
	@SerializedName("coordinate")
	private String propertyCoordinate;
	
    private String propertyLatitude;
    private String propertyLongitude;
	
	@SerializedName("damage_information_data")
	private String damageInformationData;
	
	@SerializedName("fullpath")
	private String fullpath;
    
		
	public DamageReportFormData(DamageReportData messageData) {
		user = messageData.getUser();
		userfull = messageData.getUserFull();
		status = messageData.getStatus();

		ownerLastName = messageData.getOwnerLastName();
		ownerFirstName = messageData.getOwnerFirstName();
		ownerLandlinePhone = messageData.getOwnerLandlinePhone();
		ownerCellPhone = messageData.getOwnerCellPhone();
		ownerEmail = messageData.getOwnerEmail();
		
		propertyAddress = messageData.getPropertyAddress();
		propertyCity = messageData.getPropertyCity();
		propertyZipCode = messageData.getPropertyZipCode();
		
		propertyLatitude = messageData.getPropertyLatitude();
		propertyLongitude = messageData.getPropertyLongitude();
		
		propertyCoordinate = propertyLatitude + ";" + propertyLongitude;
		
		damageInformationData = new Gson().toJson(messageData.getDamageInformation());
		
		fullpath = messageData.getFullpath();
	}
	
	public String getUser() {
		return user;
	}
	
	public String getUserFull() {
		return userfull;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public String getStatus(){
		return status;
	}
	
	public void setStatus(String status){
		this.status = status;
	}

	public String getOwnerLastName() {
		return ownerLastName;
	}

	public void setOwnerLastName(String ownerLastName) {
		this.ownerLastName = ownerLastName;
	}

	public String getOwnerFirstName() {
		return ownerFirstName;
	}

	public void setOwnerFirstName(String ownerFirstName) {
		this.ownerFirstName = ownerFirstName;
	}

	public String getOwnerLandlinePhone() {
		return ownerLandlinePhone;
	}

	public void setOwnerLandlinePhone(String ownerLandlinePhone) {
		this.ownerLandlinePhone = ownerLandlinePhone;
	}

	public String getOwnerCellPhone() {
		return ownerCellPhone;
	}

	public void setOwnerCellPhone(String ownerCellPhone) {
		this.ownerCellPhone = ownerCellPhone;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public String getPropertyAddress() {
		return propertyAddress;
	}

	public void setPropertyAddress(String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}

	public String getPropertyCity() {
		return propertyCity;
	}

	public void setPropertyCity(String propertyCity) {
		this.propertyCity = propertyCity;
	}

	public String getPropertyZipCode() {
		return propertyZipCode;
	}

	public void setPropertyZipCode(String propertyZipCode) {
		this.propertyZipCode = propertyZipCode;
	}

	public String getPropertyLatitude() {
		if(propertyCoordinate != null && !propertyCoordinate.isEmpty()) {
			String[] coordinateArray = propertyCoordinate.split(";");
			this.propertyLatitude = coordinateArray[0];
		}
		
		return propertyLatitude;
	}

	public void setPropertyLatitude(String propertyLatitude) {
		this.propertyLatitude = propertyLatitude;
	}

	public String getPropertyLongitude() {
		if(propertyCoordinate != null && !propertyCoordinate.isEmpty()) {
			String[] coordinateArray = propertyCoordinate.split(";");
			this.propertyLongitude = coordinateArray[1];
		}
		
		return propertyLongitude;
	}

	public void setPropertyLongitude(String propertyLongitude) {
		this.propertyLongitude = propertyLongitude;
	}
	
	public String getPropertyCoordinate() {
		return propertyCoordinate;
	}

	public void setPropertyCoordinate(String propertyCoordinate) {
		if(propertyCoordinate != null && !propertyCoordinate.isEmpty()) {
			String[] coordinateArray = propertyCoordinate.split(";");
			
			this.propertyLatitude = coordinateArray[0];
			this.propertyLongitude = coordinateArray[1];
			
			this.propertyCoordinate = propertyCoordinate;
		}
	}

	public String toJsonString() {
		return new Gson().toJson(this);
	}

	public ArrayList<DamageInformation> getDamageInformation() {
		return new Gson().fromJson(this.damageInformationData, new TypeToken<ArrayList<DamageInformation>>(){}.getType());
	}

	public String getFullpath() {
		return fullpath;
	}

	public void setFullpath(String fullpath) {
		this.fullpath = fullpath;
	}

}
