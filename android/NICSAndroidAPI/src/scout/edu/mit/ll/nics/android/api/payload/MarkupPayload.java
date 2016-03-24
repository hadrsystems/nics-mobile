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

package scout.edu.mit.ll.nics.android.api.payload;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import scout.edu.mit.ll.nics.android.api.data.MarkupFeature;

public class MarkupPayload {

    @Expose private long collabRoomId;
	@Expose private long createdUTC;
    @Expose private long lastUpdatedUTC;
    @Expose private ArrayList<MarkupFeature> features;
    @Expose private ArrayList<String> deletedFeatureIds;
    @Expose private ArrayList<String> movedFeatureIds;
    @Expose private long incidentId;
    @Expose private long senderUserId;
    @Expose private long seqTime;

    public MarkupPayload () {

    }
	
    public long getCollabRoomId() {
		return collabRoomId;
	}

	public void setCollabRoomId(long collabRoomId) {
		this.collabRoomId = collabRoomId;
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

	public void setDeletedFeatures(ArrayList<String> deletedFeatureIds) {
		this.deletedFeatureIds = deletedFeatureIds;
	}

	public ArrayList<String> getDeletedFeatures() {
		return deletedFeatureIds;
	}
	
	public void setMovedFeatures(ArrayList<String> movedFeatureIds) {
		this.movedFeatureIds = movedFeatureIds;
	}

	public ArrayList<String> getMovedFeatures() {
		return movedFeatureIds;
	}
	
	public void setFeatures(ArrayList<MarkupFeature> features) {
		this.features = features;
	}

	public ArrayList<MarkupFeature> getFeatures() {
		return features;
	}

	public long getIncidentId() {
		return incidentId;
	}

	public void setIncidentId(long incidentId) {
		this.incidentId = incidentId;
	}

	public long getSenderUserId() {
		return senderUserId;
	}

	public void setSenderUserId(long senderUserId) {
		this.senderUserId = senderUserId;
	}

	public long getSeqTime() {
		return seqTime;
	}

	public void setSeqTime(long seqTime) {
		this.seqTime = seqTime;
	}

	public String toJsonString() {
		Gson test = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return test.toJson(this).toString();
	}
}
