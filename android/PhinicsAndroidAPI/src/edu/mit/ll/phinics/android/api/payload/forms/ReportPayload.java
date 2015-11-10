/*|~^~|Copyright (c) 2008-2015, Massachusetts Institute of Technology (MIT)
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
package edu.mit.ll.phinics.android.api.payload.forms;

import edu.mit.ll.phinics.android.api.data.ReportStatus;

public class ReportPayload {
	private transient long id;
	private transient boolean isDraft;
//	private long createdUTC;
//	private long lastUpdatedUTC;
//    private long senderUserId;
	private long formId;
	private long formtypeid;
	private long incidentid;
    private long usersessionid;
    private long seqtime;
    private long seqnum;
    private transient ReportStatus status = ReportStatus.WAITING_TO_SEND;
    private String message;
	private String incidentname;
    private transient int progress;
    private transient boolean failedToSend;
    
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isDraft() {
		return isDraft;
	}
	
	public void setDraft(boolean isDraft) {
		this.isDraft = isDraft;
	}
	
//	public long getCreatedUTC() {
//		return createdUTC;
//	}
//
//	public void setCreatedUTC(long createdUTC) {
//		this.createdUTC = createdUTC;
//	}
//
//	public long getLastUpdatedUTC() {
//		return lastUpdatedUTC;
//	}
//
//	public void setLastUpdatedUTC(long lastUpdatedUTC) {
//		this.lastUpdatedUTC = lastUpdatedUTC;
//	}
	
//	public long getSenderUserId() {
//		return senderUserId;
//	}
//	
//	public void setSenderUserId(long senderUserId) {
//		this.senderUserId = senderUserId;
//	}

	public long getFormId() {
		return formId;
	}
	
	public void setFormId(long formId) {
		this.formId = formId;
	}
	
	public long getFormTypeId() {
		return formtypeid;
	}

	public void setFormTypeId(long formTypeId) {
		this.formtypeid = formTypeId;
	}

	public long getIncidentId() {
		return incidentid;
	}

	public void setIncidentId(long incidentId) {
		this.incidentid = incidentId;
	}

	public long getUserSessionId() {
		return usersessionid;
	}
	
	public void setUserSessionId(long userSessionId) {
		this.usersessionid = userSessionId;
	}
	
	public long getSeqTime() {
		return seqtime;
	}
	
	public void setSeqTime(long seqTime) {
		this.seqtime = seqTime;
	}
	
	public long getSeqNum() {
		return seqnum;
	}
	
	public void setSeqNum(long seqNum) {
		this.seqnum = seqNum;
	}
	
	public ReportStatus getStatus() {
		return status;
	}
	
	public void setStatus(ReportStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getIncidentName() {
		return incidentname;
	}

	public void setIncidentName(String incidentName) {
		this.incidentname = incidentName;
	}
	
	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public boolean isFailedToSend() {
		return failedToSend;
	}

	public void setFailedToSend(boolean failedToSend) {
		this.failedToSend = failedToSend;
	}
}
