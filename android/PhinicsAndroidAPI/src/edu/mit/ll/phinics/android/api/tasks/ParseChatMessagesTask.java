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
import edu.mit.ll.phinics.android.api.payload.ChatPayload;
import edu.mit.ll.phinics.android.utils.Intents;


public class ParseChatMessagesTask extends AsyncTask<ArrayList<ChatPayload>, Object, Integer> {
	private Context mContext;
	private DataManager mDataManager;
	
	public ParseChatMessagesTask(Context context) {
		mContext = context;
		mDataManager = DataManager.getInstance(mContext);
	}

	@Override
	protected Integer doInBackground(@SuppressWarnings("unchecked") ArrayList<ChatPayload>... chatPayloads) {
		Integer numParsed = 0;
		
//		mDataManager.deleteAllChatHistory();
//		mDataManager.addAllChatHistory(chatPayloads[0]);

		
		//temp fix until I can pull from server by timestamp
		
		 ArrayList<ChatPayload> chats = mDataManager.getRecentChatHistory();
		
		long lastMessageTime = mDataManager.getLastChatTimestamp(mDataManager.getActiveIncidentId(), mDataManager.getSelectedCollabRoomId()) ;
		
		for(ChatPayload payload : chatPayloads[0]) {
			if(payload.getcreated()>lastMessageTime){//temp fix until I can pull from server by timestamp
				mDataManager.addChatHistory(payload);
				
				numParsed++;
		        
		        payload.setIncidentId(mDataManager.getActiveIncidentId());
		        
		        Intent intent = new Intent();
		        intent.setAction(Intents.PHINICS_NEW_CHAT_RECEIVED);
		        intent.putExtra("payload", payload.toFullJsonString());
		        intent.putExtra("newMessageCount", numParsed);
		        
		        if(!payload.getNickname().equals(mDataManager.getUsername())) {
			        mContext.sendBroadcast (intent);
		        }
			}
		}
		
		if(numParsed > 0) {
	        Intent intent = new Intent();
	        intent.setAction(Intents.PHINICS_LAST_CHAT_RECEIVED);
	        intent.putExtra("payload", chatPayloads[0].get(chatPayloads[0].size() - 1).toJsonString());
	        intent.putExtra("newMessageCount", numParsed);
	        mContext.sendBroadcast (intent);
	        
	        mDataManager.addPersonalHistory("Successfully received " + numParsed + " chat messages from " + mDataManager.getSelectedCollabRoomName());
		}
		
		return numParsed;
	}
	
	@Override
	protected void onPostExecute(Integer numParsed) {
		super.onPostExecute(numParsed);
		
		RestClient.clearParseChatMessagesTask();
		Log.i("PhinicsParseChatMessagesTask", "Successfully parsed " + numParsed + " chat messages.");
	}

}
