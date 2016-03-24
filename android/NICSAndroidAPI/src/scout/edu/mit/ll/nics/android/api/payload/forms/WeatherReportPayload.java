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
package scout.edu.mit.ll.nics.android.api.payload.forms;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.data.WeatherReportData;
import scout.edu.mit.ll.nics.android.api.data.WeatherSourceTypes;
import scout.edu.mit.ll.nics.android.api.data.WeatherWindTypes;

public class WeatherReportPayload extends ReportPayload {
    private transient WeatherReportData messageData;
    
	public WeatherReportData getMessageData() {
		return messageData;
	}
	
	public void setMessageData(WeatherReportData messageData) {
		this.messageData = messageData;
	}
	
    public void parse() {
    	messageData = new Gson().fromJson(getMessage(), WeatherReportData.class);
    	
      	try {
    			JSONObject object = new JSONObject(getMessage());
    			Object source = object.get("wr-dataSource");
    			Object windDirection = object.get("wr-winddirection");
    			Object aspect = object.get("wr-aspect");
    			
    			String priortityString = source.toString();
    			String windDirectionString = windDirection.toString();
    			String aspectString = aspect.toString();
    			
    			String sourceReverseLanguageResults = DataManager.getInstance().reverseLanguageLookup(priortityString);
    			String windDirectionReverseLanguageResults = DataManager.getInstance().reverseLanguageLookup(windDirectionString);
    			String aspectReverseLanguageResults = DataManager.getInstance().reverseLanguageLookup(aspectString);
    			
    			WeatherSourceTypes sourceType = WeatherSourceTypes.lookUp(sourceReverseLanguageResults);
    			WeatherWindTypes windDirectionType = WeatherWindTypes.lookUp(windDirectionReverseLanguageResults);
    			WeatherWindTypes aspectType = WeatherWindTypes.lookUp(aspectReverseLanguageResults);
    			
    			messageData.setDataSource(sourceType);
    			messageData.setWindDirection(windDirectionType);
    			messageData.setAspect(aspectType);
    		 
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
	}
    
    public String toJsonString() {
    	setMessage(new Gson().toJson(getMessageData()));
    	
    	return new Gson().toJson(this);
    }
}
