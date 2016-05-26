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

public class WeatherReportData {
	
	private String user;
	private String userfull;
	private String status;
	
	@SerializedName("wr-dataSource")
	private WeatherSourceTypes datasource;
	
	@SerializedName("wr-latitude")
	private String latitude;
	
	@SerializedName("wr-longitude")
	private String longitude;
	
	@SerializedName("wr-elevation")
	private String elevation;
	
	@SerializedName("wr-drybulbtemp")
	private String drybulbtemp;
	
	@SerializedName("wr-wetbulbtemp")
	private String wetbulbtemp;
	
	@SerializedName("wr-relativehumidity")
	private String relativehumidity;
	
	@SerializedName("wr-winddirection")
	private WeatherWindTypes winddirection;
	
	@SerializedName("wr-windspeed")
	private String windspeed;
	
	@SerializedName("wr-aspect")
	private WeatherWindTypes aspect;
	
	@SerializedName("wr-physicallocation")
	private String physicallocation;
	
	@SerializedName("wr-timetaken")
	private String timetaken;

	public WeatherReportData() {
	}
	
	public WeatherReportData(WeatherReportFormData messageData) {
		user = messageData.getUser();
		userfull = messageData.getUserFull();
		status = messageData.getStatus();
		datasource = WeatherSourceTypes.lookUp(messageData.getDataSource());
		latitude = messageData.getLatitude();
		longitude = messageData.getLongitude();
		elevation = messageData.getElevation();
		drybulbtemp = messageData.getDryBulbTemp();
		wetbulbtemp = messageData.getWetBulbTemp();
		relativehumidity = messageData.getRelativeHumidity();
		winddirection = WeatherWindTypes.lookUp(messageData.getWindDirection());
		windspeed = messageData.getWindSpeed();
		aspect = WeatherWindTypes.lookUp(messageData.getAspect());
		physicallocation = messageData.getPhysicalLocation();
		timetaken = messageData.getTimeTaken();
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
	
	public WeatherSourceTypes getDataSource() {
		return datasource;
	}

	public void setDataSource(WeatherSourceTypes datasource) {
		this.datasource = datasource;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getElevation() {
		return elevation;
	}

	public void setElevation(String elevation) {
		this.elevation = elevation;
	}

	public String getDryBulbTemp() {
		return drybulbtemp;
	}

	public void setDryBulbTemp(String drybulbtemp) {
		this.drybulbtemp = drybulbtemp;
	}

	public String getWetBulbTemp() {
		return wetbulbtemp;
	}

	public void setWetBulbTemp(String wetbulbtemp) {
		this.wetbulbtemp = wetbulbtemp;
	}

	public String getRelativeHumidity() {
		return relativehumidity;
	}

	public void setRelativeHumidity(String relativehumidity) {
		this.relativehumidity = relativehumidity;
	}

	public WeatherWindTypes getWindDirection() {
		return winddirection;
	}

	public void setWindDirection(WeatherWindTypes winddirection) {
		this.winddirection = winddirection;
	}

	public String getWindSpeed() {
		return windspeed;
	}

	public void setWindSpeed(String windspeed) {
		this.windspeed = windspeed;
	}

	public WeatherWindTypes getAspect() {
		return aspect;
	}

	public void setAspect(WeatherWindTypes aspect) {
		this.aspect = aspect;
	}
	
	public String getPhysicalLocation() {
		return physicallocation;
	}

	public void setPhysicalLocation(String physicallocation) {
		this.physicallocation = physicallocation;
	}
	
	public String getTimeTaken() {
		return timetaken;
	}

	public void setTimeTaken(String timetaken) {
		this.timetaken = timetaken;
	}
	
	public String toJsonString() {
		try {			
			JSONObject object = new JSONObject(new Gson().toJson(this));
			
			String sourceValue = datasource.getText();
			if(sourceValue == null){
				sourceValue = WeatherSourceTypes.Belt_Weather_Kit.getText();
			}
			
			String directionValue = winddirection.getText();
			if(directionValue == null){
				directionValue = WeatherWindTypes.E.getText();
			}
			
			String aspectValue = aspect.getText();
			if(aspectValue == null){
				aspectValue = WeatherWindTypes.E.getText();
			}
			
			object.remove("wr-dataSource");
			object.remove("wr-winddirection");
			object.remove("wr-aspect");
			object.put("wr-dataSource", sourceValue);
			object.put("wr-winddirection",directionValue);
			object.put("wr-aspect",aspectValue);
			return object.toString();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Gson().toJson(this);
		}
		
	}


}















