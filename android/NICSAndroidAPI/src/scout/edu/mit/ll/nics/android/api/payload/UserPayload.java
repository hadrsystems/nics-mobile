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

import java.util.List;

import com.google.gson.Gson;

public class UserPayload {
	
    private long          userId;
    private String		  username;
	private String        firstname;
    private String        lastname;
    private String        rank;
    private String        primaryMobilePhone;
    private String        primaryHomePhone;
    private String        primaryEmailAddr;
    private String        homeBaseName;
    private String        homeBaseStreet;
    private String        homeBaseCity;
    private String        homeBaseState;
    private String        homeBaseZip;
    private List<Integer> qualifiedPositions;
    private String        agency;
    private int           approxWeight;
    private String        remarks;
    
	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getFirstName() {
		return firstname;
	}
	
	public void setFirstName(String firstName) {
		this.firstname = firstName;
	}
	
	public String getLastName() {
		return lastname;
	}
	
	public void setLastName(String lastName) {
		this.lastname = lastName;
	}
	
	public String getRank() {
		return rank;
	}
	
	public void setRank(String rank) {
		this.rank = rank;
	}
	
	public String getPrimaryMobilePhone() {
		return primaryMobilePhone;
	}
	
	public void setPrimaryMobilePhone(String primaryMobilePhone) {
		this.primaryMobilePhone = primaryMobilePhone;
	}
	
	public String getPrimaryHomePhone() {
		return primaryHomePhone;
	}
	
	public void setPrimaryHomePhone(String primaryHomePhone) {
		this.primaryHomePhone = primaryHomePhone;
	}
	
	public String getPrimaryEmailAddr() {
		return primaryEmailAddr;
	}
	
	public void setPrimaryEmailAddr(String primaryEmailAddr) {
		this.primaryEmailAddr = primaryEmailAddr;
	}
	
	public String getHomeBaseName() {
		return homeBaseName;
	}
	
	public void setHomeBaseName(String homeBaseName) {
		this.homeBaseName = homeBaseName;
	}
	
	public String getHomeBaseStreet() {
		return homeBaseStreet;
	}
	
	public void setHomeBaseStreet(String homeBaseStreet) {
		this.homeBaseStreet = homeBaseStreet;
	}
	
	public String getHomeBaseCity() {
		return homeBaseCity;
	}
	
	public void setHomeBaseCity(String homeBaseCity) {
		this.homeBaseCity = homeBaseCity;
	}
	
	public String getHomeBaseState() {
		return homeBaseState;
	}
	
	public void setHomeBaseState(String homeBaseState) {
		this.homeBaseState = homeBaseState;
	}
	
	public String getHomeBaseZip() {
		return homeBaseZip;
	}
	
	public void setHomeBaseZip(String homeBaseZip) {
		this.homeBaseZip = homeBaseZip;
	}
	
	public List<Integer> getQualifiedPositions() {
		return qualifiedPositions;
	}
	
	public void setQualifiedPositions(List<Integer> qualifiedPositions) {
		this.qualifiedPositions = qualifiedPositions;
	}
	
	public String getAgency() {
		return agency;
	}
	
	public void setAgency(String agency) {
		this.agency = agency;
	}
	
	public int getApproxWeight() {
		return approxWeight;
	}
	
	public void setApproxWeight(int approxWeight) {
		this.approxWeight = approxWeight;
	}
	
	public String getRemarks() {
		return remarks;
	}
	
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String toJsonString() {
		return new Gson().toJson(this);
	}
    
}
