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

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class ResourceRequestData {
	
	private String user;
	private String userfull;
	
	@SerializedName("resreq-quantity")
	private String quantity;
	
	@SerializedName("resreq-priority")
	private String priority;
	
	@SerializedName("resreq-description")
	private String description;
	
	@SerializedName("resreq-type")
	private ResReqType type;
	
	@SerializedName("resreq-source")
	private String source;
	
	@SerializedName("resreq-location")
	private String location;
	
	@SerializedName("resreq-eta")
	private String eta;
	
	@SerializedName("resreq-status")
	private String status;
	
	private String reporttm;

	public ResourceRequestData() {
	}
	
	public ResourceRequestData(ResourceRequestFormData messageData) {
		user = messageData.getUser();
		userfull = messageData.getUserFull();
		quantity = messageData.getQuantity();
		priority = messageData.getPriority();
		description = messageData.getDescription();
		type = ResReqType.lookUp(messageData.getType());
		source = messageData.getSource();
		location = messageData.getLocation();
		eta = messageData.getEta();
		status = messageData.getStatus();
		reporttm = messageData.getReportTime();
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

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ResReqType getType() {
		return type;
	}

	public void setType(ResReqType type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getEta() {
		return eta;
	}

	public void setEta(String eta) {
		this.eta = eta;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReportTime() {
		return reporttm;
	}

	public void setReportTime(String reporttm) {
		this.reporttm = reporttm;
	}
	
	public String toJsonString() {
//		return new Gson().toJson(this);

		try {			
			JSONObject object = new JSONObject(new Gson().toJson(this));
			object.remove("resreq-type");
			
			String priorityValue = type.getText();
			if(priorityValue == null){
				priorityValue = ResReqPriorityTypes.Urgent.getText();
			}
			
			object.put("ur-recommendedpriority", priorityValue);
			return object.toString();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Gson().toJson(this);
		}
	}
}
