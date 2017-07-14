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
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class ChatPayload {
	private transient long id;
	private long created;
	private long lastupdated;
	@Expose private long chatid;
	@Expose private String message;
    private long collabroomid;
    private transient long incidentId;
    private long userId;
//    private long seqTime;
    @Expose private long seqnum;
    private String topic;
    private String nickname;
    private String userOrgName;
    @Expose private Long userorgid;
    


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getcreated() {
		return created;
	}

	public void setcreated(long created) {
		this.created = created;
	}

	public long getlastupdated() {
		return lastupdated;
	}

	public void setlastupdated(long lastupdated) {
		this.lastupdated = lastupdated;
	}

	public long getchatid() {
		return chatid;
	}
	
	public void setchatid(long chatid) {
		this.chatid = chatid;
	}
	
	public String getmessage() {
		return message;
	}
	
	public void setmessage(String message) {
		this.message = message;
	}
	
	public long getcollabroomid() {
		return collabroomid;
	}
	
	public void setcollabroomid(long collabroomid) {
		this.collabroomid = collabroomid;
	}
	
	public long getIncidentId() {
		return incidentId;
	}

	public void setIncidentId(long incidentId) {
		this.incidentId = incidentId;
	}

	public long getuserId() {
		return userId;
	}
	
	public void setuserId(long userId) {
		this.userId = userId;
	}
/*	
	public long getSeqTime() {
		return seqTime;
	}
	
	public void setSeqTime(long seqTime) {
		this.seqTime = seqTime;
	}
	*/
	public long getseqnum() {
		return seqnum;
	}
	public void setseqnum(long seqnum) {
		this.seqnum = seqnum;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getUserOrgName() {
		return userOrgName;
	}
	
	public void setUserOrgName(String userOrgName) {
		this.userOrgName = userOrgName;
	}
	
	public Long getUserorgid() {
		return userorgid;
	}

	public void setUserorgid(Long userorgid) {
		this.userorgid = userorgid;
	}
	
	public String toJsonString() {
		Gson test = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return test.toJson(this).toString();
	}
	public String toFullJsonString() {
		return new Gson().toJson(this);
	}
}
