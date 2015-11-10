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
package edu.mit.ll.phinics.android.api.payload.forms;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import edu.mit.ll.phinics.android.api.DataManager;
import edu.mit.ll.phinics.android.api.data.UxoPriorityTypes;
import edu.mit.ll.phinics.android.api.data.UxoReportData;
import edu.mit.ll.phinics.android.api.data.UxoTypes;

public class UxoReportPayload extends ReportPayload {
    private transient UxoReportData messageData;
    
	public UxoReportData getMessageData() {
		return messageData;
	}
	
	public void setMessageData(UxoReportData messageData) {
		this.messageData = messageData;		
		setMessage(new Gson().toJson(getMessageData()));
	}
	
    public void parse() {
    	messageData = new Gson().fromJson(getMessage(), UxoReportData.class);
    	
    	try {
			JSONObject object = new JSONObject(getMessage());
			Object priortity = object.get("ur-recommendedpriority");
			Object type = object.get("ur-uxotype");
			
			String priortityString = priortity.toString();
			String typeString = type.toString();
			
			String priorityReverseLanguageResults = DataManager.getInstance().reverseLanguageLookup(priortityString);
			String typeReverseLanguageResults = DataManager.getInstance().reverseLanguageLookup(typeString);
			
			UxoPriorityTypes priorityType = UxoPriorityTypes.lookUp(priorityReverseLanguageResults);
			UxoTypes typeType = UxoTypes.lookUp(typeReverseLanguageResults);

			messageData.setRecommendedPriority(priorityType);
			messageData.setUxoType(typeType);
		 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public String toJsonString() {
//    	try {
//
//			JSONObject object = new JSONObject(new Gson().toJson(getMessageData()));
//			
//			String jsonValue = object.getString("ur-recommendedpriority");
//			
//			String reverseLanguageResults = DataManager.getInstance().reverseLanguageLookup(jsonValue);
//			UxoPriorityTypes recommendedPriority = UxoPriorityTypes.lookUp(reverseLanguageResults);
//	
//			String value = recommendedPriority.getText();
//			if(value == null){
//				value = UxoPriorityTypes.Immediate.getText();
//			}
//			object.remove("ur-recommendedpriority");
//			object.put("ur-recommendedpriority", value);
//	    	
//			setMessage(new Gson().toJson(object.toString()));
//		
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			setMessage(new Gson().toJson(getMessageData()));
//		}  	
    	
    	setMessage(new Gson().toJson(getMessageData()));
    	return new Gson().toJson(this);
    }
}
