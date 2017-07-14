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

public class WeatherReportFormData {
	
	private String user;
	private String userfull;
	private String status;
	private int datasource;
	
	private String coordinate;
	private String latitude;
	private String longitude;
	
	private String elevation;
	private String drybulbtemp;
	private String wetbulbtemp;
	private String relativehumidity;
	private int winddirection;
	private String windspeed;
	private int aspect;
	private String physicallocation;
	private String timetaken;
	
	public WeatherReportFormData(WeatherReportData messageData) {
		user = messageData.getUser();
		userfull = messageData.getUserFull();
		status = messageData.getStatus();
		if(messageData.getDataSource() != null){
			datasource = messageData.getDataSource().getId();
		}else{
			datasource = 0;
		}
		latitude = messageData.getLatitude();
		longitude = messageData.getLongitude();
		coordinate = latitude + ";" + longitude;
		elevation = messageData.getElevation();
		drybulbtemp = messageData.getDryBulbTemp();
		wetbulbtemp = messageData.getWetBulbTemp();
		relativehumidity = messageData.getRelativeHumidity();
		
		if(messageData.getWindDirection() != null){
			winddirection = messageData.getWindDirection().getId();
		}else{
			winddirection = 0;
		}
		windspeed = messageData.getWindSpeed();
		if(messageData.getAspect() != null){
			aspect = messageData.getAspect().getId();
		}else{
			aspect = 0;
		}
		physicallocation = messageData.getPhysicalLocation();
		timetaken = messageData.getTimeTaken();
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
	
	public int getDataSource() {
		return datasource;
	}

	public void setDataSource(int datasource) {
		this.datasource = datasource;
	}

	public String getLatitude() {
		if(coordinate != null && !coordinate.isEmpty()) {
			String[] coordinateArray = coordinate.split(";");
			this.latitude = coordinateArray[0];
		}
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getLongitude() {
		if(coordinate != null && !coordinate.isEmpty()) {
			String[] coordinateArray = coordinate.split(";");
			this.longitude = coordinateArray[1];
		}
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public String getCoordinate() {
		return coordinate;
	}

	public void setPropertyCoordinate(String propertyCoordinate) {
		if(propertyCoordinate != null && !propertyCoordinate.isEmpty()) {
			String[] coordinateArray = propertyCoordinate.split(";");
			
			this.latitude = coordinateArray[0];
			this.longitude = coordinateArray[1];
			
			this.coordinate = propertyCoordinate;
		}
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

	public int getWindDirection() {
		return winddirection;
	}

	public void setWindDirection(int winddirection) {
		this.winddirection = winddirection;
	}

	public String getWindSpeed() {
		return windspeed;
	}

	public void setWindSpeed(String windspeed) {
		this.windspeed = windspeed;
	}

	public int getAspect() {
		return aspect;
	}

	public void setAspect(int aspect) {
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
		return new Gson().toJson(this);
	}
}

