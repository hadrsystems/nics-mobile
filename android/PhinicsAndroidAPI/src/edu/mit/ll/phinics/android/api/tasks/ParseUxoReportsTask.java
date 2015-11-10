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
import edu.mit.ll.phinics.android.api.data.UxoPriorityTypes;
import edu.mit.ll.phinics.android.api.data.UxoTypes;
import edu.mit.ll.phinics.android.api.payload.forms.UxoReportPayload;
import edu.mit.ll.phinics.android.utils.Intents;
import edu.mit.ll.phinics.android.utils.NotificationsHandler;


public class ParseUxoReportsTask extends AsyncTask<ArrayList<UxoReportPayload>, Object, Integer> {
	private Context mContext;
	private DataManager mDataManager;
	private NotificationsHandler mNotificationHandler;
	
	public ParseUxoReportsTask(Context context) {
		mContext = context;
		mDataManager = DataManager.getInstance(mContext);
		mNotificationHandler = NotificationsHandler.getInstance(mContext);
	}

	@Override
	protected Integer doInBackground(@SuppressWarnings("unchecked") ArrayList<UxoReportPayload>... uxoPayloads) {
		Integer numParsed = 0;
		for(UxoReportPayload payload : uxoPayloads[0]) {
			if(payload.getIncidentId() == mDataManager.getActiveIncidentId()) {
				payload.parse();
				payload.setStatus(ReportStatus.SENT);
			
				//fix because old data is in web database which doesn't translate to the new enums
				//doesn't hurt to have this safety net in place
				if(payload.getMessageData().getUxoType() == null){
					payload.getMessageData().setUxoType(UxoTypes.Dropped);
				}
				if(payload.getMessageData().getRecommendedPriority() == null){
					payload.getMessageData().setRecommendedPriority(UxoPriorityTypes.Immediate);
				}
				
				//this is ugly and is a rushed fix
				if(payload.getMessageData().getColor().equals("#FFFFFF")){	//white
					payload.getMessageData().setColor("0");
				}else if(payload.getMessageData().getColor().equals("#FF0000")){	//red
					payload.getMessageData().setColor("1");
				}else if(payload.getMessageData().getColor().equals("#FF8000")){	//orange
					payload.getMessageData().setColor("2");
				}else if(payload.getMessageData().getColor().equals("#FFFF00")){	//yellow
					payload.getMessageData().setColor("3");
				}else if(payload.getMessageData().getColor().equals("#00FF00")){	//green
					payload.getMessageData().setColor("4");
				}else if(payload.getMessageData().getColor().equals("#00FFFF")){	//teal
					payload.getMessageData().setColor("5");
				}else if(payload.getMessageData().getColor().equals("#0000FF")){	//blue
					payload.getMessageData().setColor("6");
				}else if(payload.getMessageData().getColor().equals("#7F00FF")){	//purple
					payload.getMessageData().setColor("7");
				}else if(payload.getMessageData().getColor().equals("#FF007F")){	//pink
					payload.getMessageData().setColor("8");
				}else if(payload.getMessageData().getColor().equals("#808080")){	//grey
					payload.getMessageData().setColor("9");
				}else if(payload.getMessageData().getColor().equals("#000000")){	//black
					payload.getMessageData().setColor("10");
				}
				
				mDataManager.addUxoReportToHistory(payload);
				
		        Intent intent = new Intent();
		        intent.setAction(Intents.PHINICS_NEW_UXO_REPORT_RECEIVED);
		        intent.putExtra("payload", payload.toJsonString());
		        intent.putExtra("status", ReportStatus.SENT.getId());
		        mContext.sendBroadcast (intent);
		        numParsed++;
			}
		}
		
		if(numParsed > 0) {
			if(!mDataManager.isPushNotificationsDisabled()){
				mNotificationHandler.createUxoReportNotification(uxoPayloads[0], mDataManager.getActiveIncidentId());
			}
	        mDataManager.addPersonalHistory("Successfully received " + numParsed + " uxo reports from " + mDataManager.getActiveIncidentName());
		}
		return numParsed;
	}
	
	@Override
	protected void onPostExecute(Integer numParsed) {
		super.onPostExecute(numParsed);
		
		RestClient.clearParseUxoReportTask();
		Log.i("PhinicsUxoReportTask", "Successfully parsed " + numParsed + " uxo reports.");
	}

}
