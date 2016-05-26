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
package scout.edu.mit.ll.nics.android.fragments;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.adapters.ChatListAdapter;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.RestClient;
import scout.edu.mit.ll.nics.android.api.payload.ChatPayload;
import scout.edu.mit.ll.nics.android.utils.Constants;
import scout.edu.mit.ll.nics.android.utils.EncryptedPreferences;
import scout.edu.mit.ll.nics.android.utils.Intents;

public class ChatListFragment extends Fragment {

	private Context mContext;
	private View mRootView;
	private ListView mChatListView;
	private ChatListAdapter mChatListAdapter;
	private EditText mInputField;
	private Button mSendButton;
	
	private String mDateFormatString = "MM/dd kk:mm:ss";
	
	private IntentFilter mChatReceiverFilter;
	private IntentFilter mLastChateReceivedFilter;
	private IntentFilter mCollabRoomSwitchedFilter;
	private IntentFilter mIncidentSwitchedFilter;
	private IntentFilter mLocalChatClearedFilter;
	
	private DataManager mDataManager;
	
	private boolean chatReceiverRegistered;
	boolean chatEmpty = true;
	boolean chatInitialized = false;
	
	private ArrayList<ChatPayload> localChatList = new ArrayList<ChatPayload>();
	private long oldestTimestampInList;
	private long newestTimestampInList;
	private boolean entireHistoryHasBeenLoaded = false;
	private int limitPerChatPull = 25;
	List<ChatPayload> mostRecentPulledChatHistory; 
	
	int previousFirstVisibleItem = -1;
	boolean isListScrollingUp = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getActivity();
		mDataManager = DataManager.getInstance(mContext);
		mChatReceiverFilter = new IntentFilter(Intents.nics_NEW_CHAT_RECEIVED);
		mLastChateReceivedFilter = new IntentFilter(Intents.nics_LAST_CHAT_RECEIVED);
		mCollabRoomSwitchedFilter = new IntentFilter(Intents.nics_COLLABROOM_SWITCHED);
		mIncidentSwitchedFilter = new IntentFilter(Intents.nics_INCIDENT_SWITCHED);
		mLocalChatClearedFilter = new IntentFilter(Intents.nics_LOCAL_CHAT_CLEARED);
		
		if(!chatReceiverRegistered) {
			Log.d("chatList_debug", "register chatReceiver in onCreate");
			mContext.registerReceiver(chatReceiver, mChatReceiverFilter);
			mContext.registerReceiver(lastChatReceived, mLastChateReceivedFilter);
			mContext.registerReceiver(collabRoomSwitchedReceiver,mCollabRoomSwitchedFilter);
			mContext.registerReceiver(incidentSwitchedReceiver,mIncidentSwitchedFilter);
			mContext.registerReceiver(localChatClearedReceiver,mLocalChatClearedFilter);
			chatReceiverRegistered = true;
		}
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.chat, menu);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		mRootView = inflater.inflate(R.layout.fragment_chat_list, container, false);
		mChatListView = (ListView) mRootView.findViewById(R.id.chatListView);
		
		chatInitialized = false;
		entireHistoryHasBeenLoaded = false;
		isListScrollingUp = false;
		newestTimestampInList = 0;
		
		mChatListView.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				
				if(mChatListView.getFirstVisiblePosition() < 1){
					if(!entireHistoryHasBeenLoaded && chatInitialized && isListScrollingUp){
						loadOlderChatMessages();
					}
				}
				
				if(firstVisibleItem < previousFirstVisibleItem){
					isListScrollingUp = true;
				}else{
					isListScrollingUp = false;
				}
				previousFirstVisibleItem = firstVisibleItem;
			}
		});
		
		mInputField = (EditText) mRootView.findViewById(R.id.chatInputField);
		mSendButton = (Button) mRootView.findViewById(R.id.chatSendButton);
	
		mSendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				String chatMessage =  mInputField.getText().toString();
				if(chatMessage.length()>0){
					
					if(chatEmpty){
						mChatListAdapter.clear();
						chatEmpty = false;
					}
					
					String collabRoomName = mDataManager.getSelectedCollabRoom().getName();
					mInputField.setText("");
					
					ChatPayload data = mDataManager.addChatMsgToStoreAndForward(chatMessage, collabRoomName);
					mDataManager.sendChatMessages();
					RestClient.getChatHistory(data.getIncidentId(), data.getcollabroomid());
					localChatList.add(data);
					mChatListAdapter.add(data);
				}
			}
		});
		
		return mRootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		mDataManager.setNewchatAvailable(false);
		
		if(mChatListAdapter == null) {
			mChatListAdapter = new ChatListAdapter(mContext, R.layout.listitem_chat, R.id.chatUser, new ArrayList<ChatPayload>());
		}else{
			mChatListAdapter.clear();
		}
		
		if(mChatTask != null) {
			mChatTask.cancel(true);
			mChatTask = null;
		}

		EncryptedPreferences settings = new EncryptedPreferences(this.mContext.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE));

		initializeChat();
		mInputField.setHint(R.string.enter_chat_message);

		mDataManager.sendChatMessages();

		if(!chatReceiverRegistered) {
			Log.d("chatList_debug", "register chatReceiver in onResume");
			mContext.registerReceiver(chatReceiver, mChatReceiverFilter);
			mContext.registerReceiver(lastChatReceived, mLastChateReceivedFilter);
			mContext.registerReceiver(collabRoomSwitchedReceiver,mCollabRoomSwitchedFilter);
			mContext.registerReceiver(incidentSwitchedReceiver,mIncidentSwitchedFilter);
			mContext.registerReceiver(localChatClearedReceiver,mIncidentSwitchedFilter);
			chatReceiverRegistered = true;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		
		if(mChatTask != null) {
			mChatTask.cancel(true);
			mChatTask = null;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		if(chatReceiverRegistered) {
			Log.d("chatList_debug", "un register chatReceiver in destroy view");
			mContext.unregisterReceiver(chatReceiver);
			mContext.unregisterReceiver(lastChatReceived);
			mContext.unregisterReceiver(collabRoomSwitchedReceiver);
			mContext.unregisterReceiver(incidentSwitchedReceiver);
			mContext.unregisterReceiver(localChatClearedReceiver);
			chatReceiverRegistered = false;
		}
		
		((ViewGroup) mRootView.getParent()).removeView(mRootView);
	}
	
	private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
            //currently not being fired from parse chat task
		}
	};
	
	private BroadcastReceiver lastChatReceived = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				for(int i = 0; i < localChatList.size();i++){
					mChatListAdapter.remove(localChatList.get(i));
				}
				localChatList.clear();
				
				if(chatEmpty){
					mChatListAdapter.clear();
					chatEmpty = false;
					
					mostRecentPulledChatHistory = new ArrayList<ChatPayload>(mDataManager.getRecentChatHistoryStartingFromAndGoingBack(System.currentTimeMillis() + 3600000, String.valueOf(limitPerChatPull)));
				
					oldestTimestampInList = mostRecentPulledChatHistory.get(mostRecentPulledChatHistory.size()-1).getcreated();
					newestTimestampInList = mostRecentPulledChatHistory.get(0).getcreated();
					
		    		 for(int i = 0; i < mostRecentPulledChatHistory.size(); i++){
		    			 mChatListAdapter.insert(mostRecentPulledChatHistory.get(i), 0);
		    		 }
		    		 mChatListView.setSelection(mChatListAdapter.getCount() - 1);
		    		 
		    		 if(mostRecentPulledChatHistory.size() < limitPerChatPull){
		    			 entireHistoryHasBeenLoaded=true;
		    		 }else{
		    			 entireHistoryHasBeenLoaded=false;
		    		 }
		    		 
				}else{
				
					ArrayList<ChatPayload> payloads = mDataManager.getNewChatMessages(newestTimestampInList);
					if(payloads.size() > 0){
						newestTimestampInList = payloads.get(0).getcreated();
						
						for(int i = payloads.size()-1; i >= 0; i--){
							mChatListAdapter.add(payloads.get(i));
						}
						
						if(mChatListView.getLastVisiblePosition() > mChatListAdapter.getCount() - 6){	//checks to see if used is scrolled near bottom of the view before
							mChatListView.smoothScrollToPosition(mChatListAdapter.getCount() - 1);		//autoscrolling screen down to new message
						}
					}
				}
			}
			 catch(Exception e) {
				e.printStackTrace();
			}
		}
	};

	private AsyncTask<Object, Object, Void> mChatTask;
	
	private void initializeChat() {
	
		if(mChatTask == null) {
			mChatTask = new AsyncTask<Object, Object, Void>() {
				@Override
				protected Void doInBackground(Object... params) {
		
					localChatList.clear();																									//check one hour in the future to account for different clocks on devices
					mostRecentPulledChatHistory = new ArrayList<ChatPayload>(mDataManager.getRecentChatHistoryStartingFromAndGoingBack(System.currentTimeMillis() + 3600000, String.valueOf(limitPerChatPull)));
					
					if(mostRecentPulledChatHistory.size() > 0){
						oldestTimestampInList = mostRecentPulledChatHistory.get(mostRecentPulledChatHistory.size()-1).getcreated();
						newestTimestampInList = mostRecentPulledChatHistory.get(0).getcreated();
						chatEmpty = false;
					}else if(mostRecentPulledChatHistory.size() <= 0){
						ChatPayload emptyChat = new ChatPayload();
						emptyChat.setNickname("");
						if(mDataManager.getSelectedCollabRoom().getCollabRoomId() == -1){
							emptyChat.setmessage(getString(R.string.select_collabroom_to_join_your_teams_chat));
						}else{
							emptyChat.setmessage(getString(R.string.no_chat_messages_have_been_posted_in_this_room_yet));
						}
						mostRecentPulledChatHistory.add(emptyChat);
						chatEmpty = true;
					}
					
			    	if(mostRecentPulledChatHistory.size() < limitPerChatPull){
			    		entireHistoryHasBeenLoaded = true;
			    	}
			    	 
					getActivity().runOnUiThread(new Runnable(){
					     public void run() {
					    	 
					    	 mChatListAdapter.clear();

				    		 for(int i = 0; i < mostRecentPulledChatHistory.size(); i++){
				    			 mChatListAdapter.insert(mostRecentPulledChatHistory.get(i), 0);
				    		 }

							mChatListView.setAdapter(mChatListAdapter);
							mChatListAdapter.notifyDataSetChanged();
							
							mChatListView.setSelection(mChatListAdapter.getCount() - 1);
					     }
					});
					
					return null;
					
				}protected void onPostExecute(Void result) {
						mChatTask.cancel(true);
						mChatTask = null;
						chatInitialized = true;
					};
				}.execute();
		}
	}
	
	private void loadOlderChatMessages(){
		if(mChatTask == null) {
			mChatTask = new AsyncTask<Object, Object, Void>() {
				
				@Override
				protected Void doInBackground(Object... params) {
					
			    	mostRecentPulledChatHistory = mDataManager.getRecentChatHistoryStartingFromAndGoingBack(oldestTimestampInList, String.valueOf(limitPerChatPull));

					getActivity().runOnUiThread(new Runnable(){
					     public void run() {
					    	 
					    	 if(mostRecentPulledChatHistory.size() < limitPerChatPull){
					    		 entireHistoryHasBeenLoaded = true;
					    	 }else{
					    		 
					    		 int previousSelectedIndex = mChatListView.getSelectedItemPosition();
					    		 
					    		 oldestTimestampInList = mostRecentPulledChatHistory.get(mostRecentPulledChatHistory.size()-1).getcreated();
					    		 for(int i = 0; i < mostRecentPulledChatHistory.size(); i++){
					    			 mChatListAdapter.insert(mostRecentPulledChatHistory.get(i), 0);
					    		 }
					    		 mChatListView.setSelection(previousSelectedIndex + mostRecentPulledChatHistory.size()+1);
					    	 }
					    	 
					    	 if(mChatTask != null){
								mChatTask.cancel(true);
					    	 }
							mChatTask = null;
					     }
					});
										
					return null;
					
				}protected void onPostExecute(Void result) {

					};
				}.execute();
		}
	}
				
	private BroadcastReceiver collabRoomSwitchedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
	    	 if(mChatTask != null){
				mChatTask.cancel(true);
	    	 }
			mChatTask = null;
			initializeChat();
		}
	};
	
	private BroadcastReceiver incidentSwitchedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
	    	 if(mChatTask != null){
				mChatTask.cancel(true);
	    	 }
			mChatTask = null;
			initializeChat();
		}
	};
	
	private BroadcastReceiver localChatClearedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
	    	 if(mChatTask != null){
				mChatTask.cancel(true);
	    	 }
			mChatTask = null;
			initializeChat();
		}
	};
}
