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

public class CollabroomPayload {

	private transient long incidentid;
	private long collabRoomId;
	private long usersessionid;
	private String name;
	private String created;
	private long[] adminUsers;
	private long[] readWriteUsers;
	
	public boolean doIHaveMarkupPermission(long userId){
		
		if(name.equals("Working Map")){
			return true;
		}
		
		if(adminUsers == null && readWriteUsers == null){
			return false;
		}
		
		for(int i =0; i < adminUsers.length;i++){
			if(adminUsers[i] == userId){
				return true;
			}
		}
		for(int i =0; i < readWriteUsers.length;i++){
			if(readWriteUsers[i] == userId){
				return true;
			}
		}
		return false;
	}
	
	public long getIncidentid() {
		return incidentid;
	}
	
	public void setIncidentId(long incidentid) {
		this.incidentid = incidentid;
	}
	
	public long getCollabRoomId() {
		return collabRoomId;
	}
	
	public void setCollabRoomId(long collabRoomId) {
		this.collabRoomId = collabRoomId;
	}
	
	public long getUserSessionId() {
		return usersessionid;
	}
	
	public void setUserSessionId(long userSessionId) {
		this.usersessionid = userSessionId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getcreated() {
		return created;
	}
	
	public void setcreated(String created) {
		this.created = created;
	}

	public long[] getAdminUsers() {
		return adminUsers;
	}

	public void setAdminUsers(long[] adminUsers) {
		this.adminUsers = adminUsers;
	}

	public long[] getReadWriteUsers() {
		return readWriteUsers;
	}

	public void setReadWriteUsers(long[] readWriteUsers) {
		this.readWriteUsers = readWriteUsers;
	}
	
	public String toJsonString() {
		return new Gson().toJson(this);
	}
}
