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

import com.google.gson.Gson;

public class SimpleReportFormData {

	private String user;
	private String userfull;
	private String status;
	private String fullpath;
	private int category;
	private String message;
	private String location;
    private String latitude;
    private String longitude;
		
	public SimpleReportFormData(SimpleReportData messageData) {
		user = messageData.getUser();
		userfull = messageData.getUserFull();
		status = messageData.getStatus();
		fullpath = messageData.getFullpath();
		
		if(messageData.getCategory() != null) {
			category = messageData.getCategory().getId();
		} else {
			category = 0;
		}
		
		message = messageData.getDescription();
		
		latitude = String.valueOf(messageData.getLatitude());
		longitude = String.valueOf(messageData.getLongitude());
		location = latitude + ";" + longitude;
		
		setFullpath(messageData.getFullpath());
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
	
	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		if(location != null && !location.isEmpty()) {
			String[] locationArray = location.split(";");
			
			this.latitude = locationArray[0];
			this.longitude = locationArray[1];
			
			this.location = location;
		}
	}
	
	public String getLatitude() {
		if(location != null && !location.isEmpty()) {
			String[] coordinateArray = location.split(";");
			this.latitude = coordinateArray[0];
		}
		
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		if(location != null && !location.isEmpty()) {
			String[] coordinateArray = location.split(";");
			this.longitude = coordinateArray[1];
		}
		
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String toJsonString() {
		return new Gson().toJson(this);
	}

	public String getFullpath() {
		return fullpath;
	}

	public void setFullpath(String fullpath) {
		this.fullpath = fullpath;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
