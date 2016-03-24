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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import scout.edu.mit.ll.nics.android.api.messages.TaskFormMessage;

public class TaskFormPayload {
	
    private String message;
    private TaskFormMessage formMessage;
	private int formtypeid;
	private long usersessionid;
	private String incidentname;
	private long incidentid;
	private long seqtime;
	private long seqnum;
	private long formid;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getFormtypeid() {
		return formtypeid;
	}

	public void setFormtypeid(int formtypeid) {
		this.formtypeid = formtypeid;
	}

	public long getUsersessionid() {
		return usersessionid;
	}

	public void setUsersessionid(long usersessionid) {
		this.usersessionid = usersessionid;
	}

	public String getIncidentname() {
		return incidentname;
	}

	public void setIncidentname(String incidentname) {
		this.incidentname = incidentname;
	}

	public long getIncidentid() {
		return incidentid;
	}

	public void setIncidentid(long incidentid) {
		this.incidentid = incidentid;
	}

	public long getSeqtime() {
		return seqtime;
	}

	public void setSeqtime(long seqtime) {
		this.seqtime = seqtime;
	}

	public long getSeqnum() {
		return seqnum;
	}

	public void setSeqnum(long seqnum) {
		this.seqnum = seqnum;
	}

	public long getFormid() {
		return formid;
	}

	public void setFormid(long formid) {
		this.formid = formid;
	}

	public void parse() {
		GsonBuilder builder = new GsonBuilder();
		builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES);
		setFormMessage(builder.create().fromJson(getMessage(), TaskFormMessage.class));
	}

	public TaskFormMessage getFormMessage() {
		return formMessage;
	}

	public void setFormMessage(TaskFormMessage formMessage) {
		this.formMessage = formMessage;
	}
}
