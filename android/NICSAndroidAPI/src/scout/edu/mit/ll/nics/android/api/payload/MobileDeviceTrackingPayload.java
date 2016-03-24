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
package scout.edu.mit.ll.nics.android.api.payload;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import scout.edu.mit.ll.nics.android.api.data.UserHealth;

public class MobileDeviceTrackingPayload {

	private long id;
	@Expose private long createdUTC;
	private long lastUpdatedUTC;
    private long mdtId;
    @Expose private long userId;
    @Expose private String deviceId;
    @Expose private double latitude;
    @Expose private double longitude;
    @Expose private double altitude;
    @Expose private float course;
    @Expose private float speed;
    @Expose private double accuracy;
    private UserHealth userHealth;
    private String provider;
    private long incidentId;
    private long sensorTimestamp; // The sensor time-stamp of the data recorded (this is different than the createdUTC which is when the data is time-stamped as being recorded.

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCreatedUTC() {
		return createdUTC;
	}

	public void setCreatedUTC(long createdUTC) {
		this.createdUTC = createdUTC;
	}

	public long getLastUpdatedUTC() {
		return lastUpdatedUTC;
	}

	public void setLastUpdatedUTC(long lastUpdatedUTC) {
		this.lastUpdatedUTC = lastUpdatedUTC;
	}

	public long getMdtId() {
		return mdtId;
	}

	public void setMdtId(long mdtId) {
		this.mdtId = mdtId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public float getCourse() {
		return course;
	}

	public void setCourse(float course) {
		this.course = course;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public UserHealth getUserHealth() {
		return userHealth;
	}

	public void setUserHealth(UserHealth userHealth) {
		this.userHealth = userHealth;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public long getIncidentId() {
		return incidentId;
	}

	public void setIncidentId(long incidentId) {
		this.incidentId = incidentId;
	}

	public long getSensorTimestamp() {
		return sensorTimestamp;
	}

	public void setSensorTimestamp(long sensorTimestamp) {
		this.sensorTimestamp = sensorTimestamp;
	}

	public String toJsonString() {
		return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(this);
	}
}
