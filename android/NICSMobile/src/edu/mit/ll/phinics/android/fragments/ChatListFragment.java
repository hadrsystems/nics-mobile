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
package edu.mit.ll.phinics.android.fragments;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.gson.Gson;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import edu.mit.ll.phinics.android.R;
import edu.mit.ll.phinics.android.adapters.CatanRequestListAdapter;
import edu.mit.ll.phinics.android.adapters.ChatListAdapter;
import edu.mit.ll.phinics.android.api.DataManager;
import edu.mit.ll.phinics.android.api.payload.ChatPayload;
import edu.mit.ll.phinics.android.api.payload.forms.CatanRequestPayload;
import edu.mit.ll.phinics.android.utils.Constants;
import edu.mit.ll.phinics.android.utils.EncryptedPreferences;
import edu.mit.ll.phinics.android.utils.Intents;

public class ChatListFragment extends Fragment {

	private Context mContext;
	private View mRootView;
	private ListView mChatListView;
	private ChatListAdapter mChatListAdapter;
	private EditText mInputField;
	private TabHost mTabHost;
	
	private String mDateFormatString = "MM/dd kk:mm:ss";
	
	private IntentFilter mChatReceiverFilter;
//	private IntentFilter mCollabRoomSwitchedFilter;
	
	private DataManager mDataManager;
	private boolean isChatCleared;
	
	private boolean chatReceiverRegistered;
	
	private int mChatHistoryCount = 0;
	private TabSpec spec1;
	private TabSpec spec2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getActivity();
		mDataManager = DataManager.getInstance(mContext);
		mChatReceiverFilter = new IntentFilter(Intents.PHINICS_NEW_CHAT_RECEIVED);
//		mCollabRoomSwitchedFilter = new IntentFilter(Intents.PHINICS_COLLABROOM_SWITCHED);
		
		
		if(!chatReceiverRegistered) {
			mContext.registerReceiver(chatReceiver, mChatReceiverFilter);
//			mContext.registerReceiver(collabRoomSwitchedReceiver,mCollabRoomSwitchedFilter);
			chatReceiverRegistered = true;
		}
	
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		mRootView = inflater.inflate(R.layout.fragment_chat_list, container, false);
		mChatListView = (ListView) mRootView.findViewById(R.id.chatListView);
		
		mInputField = (EditText) mRootView.findViewById(R.id.chatInputField);
			
	
		return mRootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		if(mChatListAdapter == null) {
			mChatListAdapter = new ChatListAdapter(mContext, R.layout.listitem_chat, R.id.chatUser, new ArrayList<ChatPayload>());
//			mIsFirstLoad = true;
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
			mContext.registerReceiver(chatReceiver, mChatReceiverFilter);
//			mContext.registerReceiver(collabRoomSwitchedReceiver,mCollabRoomSwitchedFilter);
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
		
//		EncryptedPreferences settings = new EncryptedPreferences(this.mContext.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE));
//		settings.savePreferenceLong("activeChatTab", (long) mTabHost.getCurrentTab());

		if(chatReceiverRegistered) {
			mContext.unregisterReceiver(chatReceiver);
//			mContext.unregisterReceiver(collabRoomSwitchedReceiver);
			chatReceiverRegistered = false;
		}

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		if(chatReceiverRegistered) {
			mContext.unregisterReceiver(chatReceiver);
//			mContext.unregisterReceiver(collabRoomSwitchedReceiver);
			chatReceiverRegistered = false;
		}
		
		((ViewGroup) mRootView.getParent()).removeView(mRootView);
	}
	
	private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
					if(intent.getBooleanExtra("reloadChat", false)) {
						initializeChat();
					} else {
						ChatPayload payload = new Gson().fromJson(intent.getStringExtra("payload"), ChatPayload.class);
						mChatListAdapter.add(payload);
					}
				
				
				}
			 catch(Exception e) {
				e.printStackTrace();
			}
		}
	};
	
//	private BroadcastReceiver collabRoomSwitchedReceiver = new BroadcastReceiver() {
//		
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			try {
//					mChatListAdapter.clear();
//				}
//			 catch(Exception e) {
//				e.printStackTrace();
//			}
//		}
//	};
	

	private AsyncTask<Object, Object, Void> mChatTask;
	
	private void initializeChat() {
	
		if(mChatTask == null) {
			mChatTask = new AsyncTask<Object, Object, Void>() {
				@Override
				protected Void doInBackground(Object... params) {
		
					List<ChatPayload> chatHistory = new ArrayList<ChatPayload>(mDataManager.getRecentChatHistory());
					chatHistory.addAll(mDataManager.getChatStoreAndForwardReadyToSend());
					mChatHistoryCount = chatHistory.size();
					
					
					mChatListAdapter.addAll(chatHistory);
					mChatListView.setAdapter(mChatListAdapter);
					mChatListAdapter.notifyDataSetChanged();
					
					//Need to make chat loading more effecient before using. Hangs when a large number of chat
					//messages need to be loaded at once.
					
					if(!this.isCancelled()) {
						try {
							getActivity().runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									

								}
							});
							Thread.sleep(10);
						} catch(Exception e) {
						}
					}

					return null;
					
				}protected void onPostExecute(Void result) {
						mChatTask.cancel(true);
						mChatTask = null;
					};
				}.execute();
		}
	
		}
	
	
	
}
