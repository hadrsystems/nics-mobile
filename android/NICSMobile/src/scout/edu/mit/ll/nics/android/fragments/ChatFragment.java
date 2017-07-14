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
//package scout.edu.mit.ll.nics.android.fragments;
//
//import java.io.UnsupportedEncodingException;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import org.apache.commons.lang3.StringEscapeUtils;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.text.format.DateFormat;
//import android.text.method.ScrollingMovementMethod;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.EditorInfo;
//import android.widget.EditText;
//import android.widget.TabHost;
//import android.widget.TabHost.OnTabChangeListener;
//import android.widget.TabHost.TabSpec;
//import android.widget.TextView;
//import android.widget.TextView.OnEditorActionListener;
//
//import com.google.gson.Gson;
//
//import scout.edu.mit.ll.nics.android.R;
//import scout.edu.mit.ll.nics.android.api.DataManager;
//import scout.edu.mit.ll.nics.android.api.payload.ChatPayload;
//import scout.edu.mit.ll.nics.android.utils.Constants;
//import scout.edu.mit.ll.nics.android.utils.EncryptedPreferences;
//import scout.edu.mit.ll.nics.android.utils.Intents;
//
//public class ChatFragment extends Fragment {
//	private Context mContext;
//	private View mRootView;
//	private TextView mChatView;
//	private EditText mInputField;
//	private TabHost mTabHost;
//	
//	private String mDateFormatString = "MM/dd kk:mm:ss";
//	
//	private IntentFilter mChatReceiverFilter;
//	private IntentFilter mPersonalLogReceiverFilter;
//	
//	private DataManager mDataManager;
//	private boolean isChatCleared;
//	
//	private boolean chatReceiverRegistered;
//	private boolean personalLogReceiverRegistered;
//	
//	private int mChatHistoryCount = 0;
//	private TabSpec spec1;
//	private TabSpec spec2;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		mContext = getActivity();
//		mDataManager = DataManager.getInstance(mContext);
//		mChatReceiverFilter = new IntentFilter(Intents.nics_NEW_CHAT_RECEIVED);
//		mPersonalLogReceiverFilter = new IntentFilter(Intents.nics_NEW_PERSONAL_HISTORY_RECEIVED);
//		
//		
//		if(!chatReceiverRegistered) {
//			mContext.registerReceiver(chatReceiver, mChatReceiverFilter);
//			chatReceiverRegistered = true;
//		}
//		
//		if(!personalLogReceiverRegistered) {
//			mContext.registerReceiver(personalLogReceiver, mPersonalLogReceiverFilter);
//			personalLogReceiverRegistered = true;
//		}
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		super.onCreateView(inflater, container, savedInstanceState);
//
//		mRootView = inflater.inflate(R.layout.fragment_chat, container, false);
//		mChatView = (TextView) mRootView.findViewById(R.id.chatTextView);
//		mChatView.setMovementMethod(new ScrollingMovementMethod());
//		
//		mInputField = (EditText) mRootView.findViewById(R.id.chatInputField);
//		mInputField.setOnEditorActionListener(new OnEditorActionListener() {
//			
//			@Override
//			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//				switch (actionId) {
//				case EditorInfo.IME_ACTION_DONE:
//					if(mInputField.getText().length() > 0) {
//						if(!isChatCleared) {
//							mChatView.setText("");
//							isChatCleared = true;
//						}
//						
//						String collabRoomName = mDataManager.getSelectedCollabRoomName();
//						
//						if(mTabHost.getCurrentTabTag().equals(getString(R.string.chat_log))) {
//							String out = "";
//							try {
//								out = new String(mInputField.getText().toString().getBytes(Charset.forName(Constants.nics_ISO_8859_1)), Constants.nics_UTF8);
//
//								if(mChatHistoryCount == 0) {
//									mChatView.setText("");
//								}
//								
//								ChatPayload data = mDataManager.addChatMsgToStoreAndForward(out, collabRoomName);
//								mDataManager.sendChatMessages();
//								mInputField.setText("");
//								
//								mChatHistoryCount++;
//								
//								mChatView.append(DateFormat.format(mDateFormatString, new Date(data.getlastupdated())) + " - " + data.getNickname() + ": " + StringEscapeUtils.unescapeHtml4(data.getmessage()) + "\n");
//								
//							} catch (UnsupportedEncodingException e) {
//								e.printStackTrace();
//							}
//						} else if(mTabHost.getCurrentTabTag().equals(getString(R.string.debug_log))) {
//							mDataManager.addPersonalHistory(mInputField.getText().toString());
//							mInputField.setText("");
//						}
//					}
//					break;
//
//				default:
//					break;
//				}
//				return false;
//			}
//		});
//		
//        mTabHost = (TabHost) mRootView.findViewById(R.id.chatTabHost);
//        mTabHost.setup();
//      
//        spec1 = mTabHost.newTabSpec(getString(R.string.chat_log));
//        spec1.setContent(R.id.chatLogTab);
//        spec1.setIndicator(getString(R.string.chat_log));
//      
//      
//        spec2 = mTabHost.newTabSpec(getString(R.string.debug_log));
//        spec2.setContent(R.id.personalLogTab);
//        spec2.setIndicator(getString(R.string.debug_log));
//
//		return mRootView;
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//
//		if(mChatTask != null) {
//			mChatTask.cancel(true);
//			mChatTask = null;
//		}
//		mTabHost.clearAllTabs();
//        mTabHost.addTab(spec1);
//
//        if(mDataManager.isDebugEnabled()) {
//        	mTabHost.addTab(spec2);
//		}
//        
//        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
//			
//			@Override
//			public void onTabChanged(String tabId) {
//				if(mChatTask != null) {
//					mChatTask.cancel(true);
//					mChatTask = null;
//				}
//				
//				if(tabId.equals(getString(R.string.chat_log))) {
//					initializeChat();
//					mInputField.setHint(R.string.enter_chat_message);
//				} else if(tabId.equals(getString(R.string.debug_log))) {
//					initializeLog();
//					mInputField.setHint(R.string.enter_debug_log_comment);
//				}
//			}
//		});
//
//		EncryptedPreferences settings = new EncryptedPreferences(this.mContext.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE));
//		int activeTab = settings.getPreferenceLong("activeChatTab","0").intValue();
//
//        
//		if(activeTab == mTabHost.getCurrentTab()) {
//			if(activeTab == 0) {
//				initializeChat();
//				mInputField.setHint(R.string.enter_chat_message);
//			} else if(activeTab == 1) {
//				initializeLog();
//				mInputField.setHint(R.string.enter_debug_log_comment);
//			}
//		} else {
//			mTabHost.setCurrentTab(activeTab);
//		}
//		
//		if(chatReceiverRegistered = false){
//			mDataManager.sendChatMessages();
//			mContext.registerReceiver(chatReceiver, mChatReceiverFilter);
//			mContext.registerReceiver(personalLogReceiver, mPersonalLogReceiverFilter);
//			chatReceiverRegistered = true;
//		}
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		
//		if(mChatTask != null) {
//			mChatTask.cancel(true);
//			mChatTask = null;
//		}
//		
//		EncryptedPreferences settings = new EncryptedPreferences(this.mContext.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE));
//		settings.savePreferenceLong("activeChatTab", (long) mTabHost.getCurrentTab());
//
//		if(chatReceiverRegistered) {
//			mContext.unregisterReceiver(chatReceiver);
//			chatReceiverRegistered = false;
//		}
//		
//		
//		if(personalLogReceiverRegistered) {
//			mContext.unregisterReceiver(personalLogReceiver);
//			personalLogReceiverRegistered = false;
//		}
//	}
//
//	@Override
//	public void onDestroyView() {
//		super.onDestroyView();
//		
//		if(chatReceiverRegistered) {
//			mContext.unregisterReceiver(chatReceiver);
//			chatReceiverRegistered = false;
//		}
//		
//		if(personalLogReceiverRegistered) {
//			mContext.unregisterReceiver(personalLogReceiver);
//			personalLogReceiverRegistered = false;
//		}
//		
//		((ViewGroup) mRootView.getParent()).removeView(mRootView);
//	}
//	
//	private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
//		
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			try {
//				if(mTabHost.getCurrentTabTag().equals(getString(R.string.chat_log))) {
//					if(!isChatCleared) {
//						mChatView.setText("");
//						mChatHistoryCount = 0;
//						isChatCleared = true;
//					}
//					
//					if(intent.getBooleanExtra("reloadChat", false)) {
//						mChatView.setText("");
//						initializeChat();
//					} else {
//						ChatPayload payload = new Gson().fromJson(intent.getStringExtra("payload"), ChatPayload.class);
//						long time = payload.getcreated();
//						
//						mChatView.append(DateFormat.format(mDateFormatString, new Date(time)) + " - " + payload.getNickname() + ": " + StringEscapeUtils.unescapeHtml4(payload.getmessage()) + "\n");
//						mChatHistoryCount++;
//					}
//				}
//			} catch(Exception e) {
//				e.printStackTrace();
//			}
//		}
//	};
//	
//	private BroadcastReceiver personalLogReceiver = new BroadcastReceiver() {
//		
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			try {
//				if(mTabHost.getCurrentTabTag().equals(getString(R.string.debug_log))) {
//					if(!isChatCleared) {
//						mChatView.setText("");
//						isChatCleared = true;
//					}					
//					ChatPayload payload = new Gson().fromJson(intent.getStringExtra("payload"), ChatPayload.class);
//					long time = payload.getcreated();
//					mChatView.append(DateFormat.format(mDateFormatString, new Date(time)) + " - " + StringEscapeUtils.unescapeHtml4(payload.getmessage()) + "\n\n");
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	};
//	private AsyncTask<Object, Object, Void> mChatTask;
//	
//	
//	private void initializeChat() {
//		mChatView.setText("");
//		isChatCleared = true;
//		
//		if(mChatTask == null) {
//			mChatTask = new AsyncTask<Object, Object, Void>() {
//				@Override
//				protected Void doInBackground(Object... params) {
//					List<ChatPayload> chatHistory = new ArrayList<ChatPayload>(mDataManager.getRecentChatHistory());
//					chatHistory.addAll(mDataManager.getChatStoreAndForwardReadyToSend());
//					mChatHistoryCount = chatHistory.size();
//					
//					if(chatHistory != null && chatHistory.size() > 0 && mContext != null) {
//						for(final ChatPayload data : chatHistory) {
//
//							if(!this.isCancelled()) {
//								try {
//									getActivity().runOnUiThread(new Runnable() {
//										
//										@Override
//										public void run() {
//											mChatView.append(DateFormat.format(mDateFormatString, new Date(data.getcreated())) + " - " + data.getNickname() + ": " + StringEscapeUtils.unescapeHtml4(data.getmessage()) + "\n");
//										}
//									});
//									Thread.sleep(10);
//								} catch(Exception e) {
//								}
//							}
//						}
//			
//					} else {
//						getActivity().runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								mChatView.append(getString(R.string.no_messages_received));
//							}
//						});
//					}
//					return null;
//				}
//				
//				protected void onPostExecute(Void result) {
//					mChatTask.cancel(true);
//					mChatTask = null;
//				};
//			}.execute();
//		}
//	}
//	
//	
//	private void initializeLog() {
//		mChatView.setText("");
//		isChatCleared = true;
//		
//		if(mChatTask == null) {
//			mChatTask = new AsyncTask<Object, Object, Void>() {
//				@Override
//				protected Void doInBackground(Object... params) {
//					final ArrayList<ChatPayload> personalHistory = new ArrayList<ChatPayload>(mDataManager.getRecentPersonalHistory());
//					mChatHistoryCount = personalHistory.size();
//					
//					if(personalHistory != null && personalHistory.size() > 0 && mContext != null) {
//						for(final ChatPayload data : personalHistory) {
//							if(!this.isCancelled()) {
//								try {
//									getActivity().runOnUiThread(new Runnable() {
//								
//										@Override
//										public void run() {
//											mChatView.append(DateFormat.format(mDateFormatString, new Date(data.getlastupdated())) + " - " + data.getmessage() + "\n\n");
//										}
//									});
//									Thread.sleep(10);
//								} catch(Exception e) {
//								}
//							}
//						}
//					} else {
//						getActivity().runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								mChatView.append(getString(R.string.no_debug_messages_logged));
//							}
//						});
//					}
//					return null;
//				};
//			}.execute();
//		}
//	}
//}
