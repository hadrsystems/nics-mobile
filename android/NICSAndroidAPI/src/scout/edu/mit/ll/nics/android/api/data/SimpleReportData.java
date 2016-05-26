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

public class SimpleReportData {
    private String assign;
    private String user;
	private String userfull;
    private String status;
    private String desc;
    private SimpleReportCategoryType cat;
    private String image;
    private String fullpath;
    private double lat;
    private double lon;
    
	public SimpleReportData() {
		cat = SimpleReportCategoryType.BLANK;
	}
	
	public SimpleReportData(SimpleReportFormData messageData) {
		
		user = messageData.getUser();
		userfull = messageData.getUserFull();
		status = messageData.getStatus();
		desc = messageData.getMessage();
		cat = SimpleReportCategoryType.lookUp(messageData.getCategory());
		
		fullpath = messageData.getFullpath();
		
		if(messageData.getLatitude() != null) {
			lat = Double.valueOf(messageData.getLatitude());
		}
		
		if(messageData.getLongitude() != null) {
			lon = Double.valueOf(messageData.getLongitude());
		}
	}
    
	public String getAssign() {
		return assign;
	}

	public void setAssign(String assign) {
		this.assign = assign;
	}

	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getUserFull() {
		return userfull;
	}
	
	public void setUserFull(String userfull) {
		this.userfull = userfull;
	}
	
	public String getStatus(){
		return status;
	}
	
	public void setStatus(String status){
		this.status = status;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public void setDescription(String description) {
		this.desc = description;
	}
	
	public SimpleReportCategoryType getCategory() {
		return cat;
	}
	
	public void setCategory(SimpleReportCategoryType _category) {
		this.cat = _category;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	
	public String getFullpath() {
		return fullpath;
	}
	
	public void setFullpath(String fullpath) {
		this.fullpath = fullpath;
	}
	
	public double getLatitude() {
		return lat;
	}
	
	public void setLatitude(double latitude) {
		this.lat = latitude;
	}
	
	public double getLongitude() {
		return lon;
	}
	
	public void setLongitude(double longitude) {
		this.lon = longitude;
	}
}
