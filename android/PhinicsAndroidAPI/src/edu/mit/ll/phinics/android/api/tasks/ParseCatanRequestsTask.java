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
package edu.mit.ll.phinics.android.api.tasks;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import edu.mit.ll.phinics.android.api.DataManager;
import edu.mit.ll.phinics.android.api.RestClient;
import edu.mit.ll.phinics.android.api.data.ReportStatus;
import edu.mit.ll.phinics.android.api.payload.forms.CatanRequestPayload;
import edu.mit.ll.phinics.android.utils.Intents;
import edu.mit.ll.phinics.android.utils.NotificationsHandler;


public class ParseCatanRequestsTask extends AsyncTask<ArrayList<CatanRequestPayload>, Object, Integer> {
	private Context mContext;
	private DataManager mDataManager;
	private NotificationsHandler mNotificationHandler;
	
	public ParseCatanRequestsTask(Context context) {
		mContext = context;
		mDataManager = DataManager.getInstance(mContext);
		mNotificationHandler = NotificationsHandler.getInstance(mContext);
	}

	@Override
	protected Integer doInBackground(@SuppressWarnings("unchecked") ArrayList<CatanRequestPayload>... crPayloads) {
		Integer numParsed = 0;
		for(CatanRequestPayload payload : crPayloads[0]) {
			if(payload.getIncidentId() == mDataManager.getActiveIncidentId()) {
				payload.parse();
				payload.setStatus(ReportStatus.SENT);
				
				mDataManager.addCatanRequestToHistory(payload);
				
		        Intent intent = new Intent();
		        intent.setAction(Intents.PHINICS_NEW_CATAN_REQUEST_RECEIVED);
		        intent.putExtra("payload", payload.toJsonString());
		        intent.putExtra("status", ReportStatus.SENT.getId());
		        mContext.sendBroadcast (intent);
		        numParsed++;
			}
		}

		if(numParsed > 0) {
			if(!mDataManager.isPushNotificationsDisabled()){
//				mNotificationHandler.createCatanRequestNotification(crPayloads[0], mDataManager.getActiveIncidentId());
			}
	        mDataManager.addPersonalHistory("Successfully received " + numParsed + " catan requests from " + mDataManager.getActiveIncidentName());
		}
		
		return numParsed;
	}
	
	@Override
	protected void onPostExecute(Integer numParsed) {
		super.onPostExecute(numParsed);
		
		RestClient.clearParseCatanRequestTask();
		Log.i("PhinicsCatanRequestTask", "Successfully parsed " + numParsed + " catan requests.");
	}

}
