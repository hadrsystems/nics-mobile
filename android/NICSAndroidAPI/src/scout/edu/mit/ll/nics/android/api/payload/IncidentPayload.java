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

import java.util.ArrayList;

public class IncidentPayload {

	private long created;
	private long incidentid;
	private long parentincidentid;
	private long usersessionid;
	private String incidentname;
	private double lat;
	private double lon;
	private boolean active;
	private String description;
	private long workspaceid;
	private ArrayList<CollabroomPayload> collabrooms;
	
	public long getCreated() {
		return created;
	}
	public void setCreatedUTC(long created) {
		this.created = created;
	}

	public long getIncidentId() {
		return incidentid;
	}
	public void setIncidentId(long incidentid) {
		this.incidentid = incidentid;
	}
	public long getusersessionid() {
		return usersessionid;
	}
	public void setUserSessionId(long userSessionid) {
		this.usersessionid = userSessionid;
	}
	public String getIncidentName() {
		return incidentname;
	}
	public void setIncidentName(String incidentName) {
		this.incidentname = incidentName;
	}
	public ArrayList<CollabroomPayload> getCollabrooms() {
		return collabrooms;
	}
	public void setCollabrooms(ArrayList<CollabroomPayload> collabrooms) {
		this.collabrooms = collabrooms;
	}
	
	public long getParentincidentid() {
		return parentincidentid;
	}
	public void setParentincidentid(long parentincidentid) {
		this.parentincidentid = parentincidentid;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getWorkspaceid() {
		return workspaceid;
	}
	public void setWorkspaceid(long workspaceid) {
		this.workspaceid = workspaceid;
	}
	public void setCreated(long created) {
		this.created = created;
	}
	public boolean containsCollabroom(String collabroomName, long collabroomId) {
		for(CollabroomPayload roomPayload : collabrooms) {
			if(roomPayload.getCollabRoomId() == collabroomId && roomPayload.getName().equals(collabroomName)) {
				return true;
			}
		}
		return false;
	}
}
