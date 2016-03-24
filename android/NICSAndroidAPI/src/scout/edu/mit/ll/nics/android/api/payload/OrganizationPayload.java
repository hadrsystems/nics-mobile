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

import com.google.gson.Gson;

import scout.edu.mit.ll.nics.android.api.data.userorg;

public class OrganizationPayload {
	private int orgId;
	private String name;
	private String county;
	private String state;
	private String timezone;
	private String prefix;
	private String distribution;
	private double defaultlatitude;
	private double defaultlongitude;
	private Integer parentorgid;
	private String country;
	private long created;
	private userorg[] userorgs;
	
	public int getOrgid() {
		return orgId;
	}
	
	public void setOrgid(int orgid) {
		this.orgId = orgid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getCounty() {
		return county;
	}
	
	public void setCounty(String county) {
		this.county = county;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getTimezone() {
		return timezone;
	}
	
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public String getDistribution() {
		return distribution;
	}
	
	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}
	
	public double getDefaultlatitude() {
		return defaultlatitude;
	}
	
	public void setDefaultlatitude(double defaultlatitude) {
		this.defaultlatitude = defaultlatitude;
	}
	
	public double getDefaultlongitude() {
		return defaultlongitude;
	}
	
	public void setDefaultlongitude(double defaultlongitude) {
		this.defaultlongitude = defaultlongitude;
	}
	
	public Integer getParentorgid() {
		return parentorgid;
	}
	
	public void setParentorgid(Integer parentorgid) {
		this.parentorgid = parentorgid;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public long getCreated() {
		return created;
	}
	
	public void setCreated(long created) {
		this.created = created;
	}
	
    public String toJsonString() {
    	return new Gson().toJson(this);
    }
	public userorg[] getUserorgs() {
		return userorgs;
	}
	public void setUserorgs(userorg[] userorgs) {
		this.userorgs = userorgs;
	}
}
