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
package scout.edu.mit.ll.nics.android.api.messages;

import com.google.gson.Gson;


public class TaskFormMessage {
	
	private String taskName;
	private String taskLocdescription;
	private String taskWorkassignment;
	private String taskSpecialinstructions;
	private String reporttm;
	private String taskLocation;
	
	public String getTaskName() {
		return taskName;
	}
	
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public String getTaskLocdescription() {
		return taskLocdescription;
	}
	
	public void setTaskLocdescription(String taskLocdescription) {
		this.taskLocdescription = taskLocdescription;
	}
	
	public String getTaskWorkassignment() {
		return taskWorkassignment;
	}
	
	public void setTaskWorkassignment(String taskWorkassignment) {
		this.taskWorkassignment = taskWorkassignment;
	}
	
	public String getTaskSpecialinstructions() {
		return taskSpecialinstructions;
	}
	
	public void setTaskSpecialinstructions(String taskSpecialinstructions) {
		this.taskSpecialinstructions = taskSpecialinstructions;
	}
	
	public String getReporttm() {
		return reporttm;
	}
	
	public void setReporttm(String reporttm) {
		this.reporttm = reporttm;
	}
	
	public String getTaskLocation() {
		return taskLocation;
	}
	
	public void setTaskLocation(String taskLocation) {
		this.taskLocation = taskLocation;
	}

	public String toJsonString() {
		return new Gson().toJson(this);
	}
	
}
