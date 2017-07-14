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
package scout.edu.mit.ll.nics.android.api.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import scout.edu.mit.ll.nics.android.utils.Constants;
import android.util.Log;

public class OrgCapabilities {

	private boolean FieldReportForm;
	private boolean DamageReportForm;
	private boolean ResourceRequestForm;
	private boolean WeatherReportForm;
	private boolean SettingsTab;
	private boolean MapMarkup;
	private boolean Chat;
	
	
	public void setCapabilitiesFromJSON(String content) {

		FieldReportForm = false;
		DamageReportForm = false;
		ResourceRequestForm = false;
		WeatherReportForm = false;
		SettingsTab = false;
		MapMarkup = false;
		Chat = false;

		try {
			JSONObject JsonContent = new JSONObject(content);
			JSONArray capabilities = JsonContent.getJSONArray("capabilities");
			
			for(int i = 0; i < capabilities.length(); i++)
			{
				JSONObject currentCapabilitiy = new JSONObject(capabilities.getString(i));
			   String name = currentCapabilitiy.getString("name");
			   
			   if(name.equals("FR-Form")){
			       FieldReportForm = true;
			   }else if(name.equals("DR-Form")){
			         DamageReportForm = true; 
			   }else if(name.equals("RES-Form")){
			       	ResourceRequestForm = true; 
			   }else if(name.equals("WR-Form")){
				   WeatherReportForm = true; 
			   }else if(name.equals("SettingsTab")){
			        ResourceRequestForm = true; 
			   }else if(name.equals("MapMarkup")){
				   	MapMarkup = true; 
			   }else if(name.equals("Chat")){
			      Chat = true; 
			   }
		   } 
		} catch (JSONException e) {
			
			Log.e(Constants.nics_DEBUG_ANDROID_TAG, "Failed to convert orgs JSON." + content);
			e.printStackTrace();
		}
     }
	
	public boolean getFieldReportForm(){
		return FieldReportForm;
	}
	public boolean getDamageReportForm(){
		return DamageReportForm;
	}
	public boolean getResourceRequestForm(){
		return ResourceRequestForm;
	}
	public boolean getWeatherReportForm(){
		return WeatherReportForm;
	}
	public boolean getSettingsTab(){
		return SettingsTab;
	}
	public boolean getMapMarkup(){
		return MapMarkup;
	}
	public boolean getChat(){
		return Chat;
	}
	
	//these shouldn't ever need to be individually set
/*	
	public void setFieldReportForm(boolean canView){
		FieldReportForm = canView;
	}
	public void setDamageReportForm(boolean canView){
		DamageReportForm = canView;
	}
	public void setResourceRequestForm(boolean canView){
		ResourceRequestForm = canView;
	}
	public void setSettingsTab(boolean canView){
		SettingsTab = canView;
	}
	public void setMapMarkup(boolean canView){
		MapMarkup = canView;
	}
	public void setChat(boolean canView){
		Chat = canView;
	}
	*/
}
