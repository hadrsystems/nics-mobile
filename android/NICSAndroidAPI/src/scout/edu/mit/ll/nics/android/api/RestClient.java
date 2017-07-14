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
package scout.edu.mit.ll.nics.android.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;

import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import scout.edu.mit.ll.nics.android.api.data.DamageReportData;
import scout.edu.mit.ll.nics.android.api.data.ReportSendStatus;
import scout.edu.mit.ll.nics.android.api.data.MarkupFeature;
import scout.edu.mit.ll.nics.android.api.data.OperationalUnit;
import scout.edu.mit.ll.nics.android.api.data.OrgCapabilities;
import scout.edu.mit.ll.nics.android.api.data.SimpleReportCategoryType;
import scout.edu.mit.ll.nics.android.api.data.SimpleReportData;
import scout.edu.mit.ll.nics.android.api.data.geo.wfs.FeatureCollection;
import scout.edu.mit.ll.nics.android.api.handlers.ChatResponseHandler;
import scout.edu.mit.ll.nics.android.api.handlers.DamageReportNoImageResponseHandler;
import scout.edu.mit.ll.nics.android.api.handlers.DamageReportResponseHandler;
import scout.edu.mit.ll.nics.android.api.handlers.FieldReportResponseHandler;
import scout.edu.mit.ll.nics.android.api.handlers.SimpleReportNoImageResponseHandler;
import scout.edu.mit.ll.nics.android.api.handlers.WeatherReportResponseHandler;
import scout.edu.mit.ll.nics.android.api.handlers.MDTResponseHandler;
import scout.edu.mit.ll.nics.android.api.handlers.MarkupResponseHandler;
import scout.edu.mit.ll.nics.android.api.handlers.ResourceRequestResponseHandler;
import scout.edu.mit.ll.nics.android.api.handlers.SimpleReportResponseHandler;
import scout.edu.mit.ll.nics.android.api.messages.AssignmentMessage;
import scout.edu.mit.ll.nics.android.api.messages.ChatMessage;
import scout.edu.mit.ll.nics.android.api.messages.CollaborationRoomMessage;
import scout.edu.mit.ll.nics.android.api.messages.DamageReportMessage;
import scout.edu.mit.ll.nics.android.api.messages.FieldReportMessage;
import scout.edu.mit.ll.nics.android.api.messages.TrackingLayerMessage;
import scout.edu.mit.ll.nics.android.api.messages.WeatherReportMessage;
import scout.edu.mit.ll.nics.android.api.messages.IncidentMessage;
import scout.edu.mit.ll.nics.android.api.messages.LoginMessage;
import scout.edu.mit.ll.nics.android.api.messages.MarkupMessage;
import scout.edu.mit.ll.nics.android.api.messages.OrganizationMessage;
import scout.edu.mit.ll.nics.android.api.messages.ResourceRequestMessage;
import scout.edu.mit.ll.nics.android.api.messages.SimpleReportMessage;
import scout.edu.mit.ll.nics.android.api.messages.UserMessage;
import scout.edu.mit.ll.nics.android.api.payload.AssignmentPayload;
import scout.edu.mit.ll.nics.android.api.payload.ChatPayload;
import scout.edu.mit.ll.nics.android.api.payload.CollabroomPayload;
import scout.edu.mit.ll.nics.android.api.payload.IncidentPayload;
import scout.edu.mit.ll.nics.android.api.payload.LoginPayload;
import scout.edu.mit.ll.nics.android.api.payload.MarkupPayload;
import scout.edu.mit.ll.nics.android.api.payload.MobileDeviceTrackingPayload;
import scout.edu.mit.ll.nics.android.api.payload.TrackingLayerPayload;
import scout.edu.mit.ll.nics.android.api.payload.TrackingTokenPayload;
import scout.edu.mit.ll.nics.android.api.payload.WeatherPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.DamageReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.FieldReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.ResourceRequestPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.WeatherReportPayload;
import scout.edu.mit.ll.nics.android.api.tasks.ParseChatMessagesTask;
import scout.edu.mit.ll.nics.android.api.tasks.ParseDamageReportsTask;
import scout.edu.mit.ll.nics.android.api.tasks.ParseFieldReportsTask;
import scout.edu.mit.ll.nics.android.api.tasks.ParseWeatherReportsTask;
import scout.edu.mit.ll.nics.android.api.tasks.ParseMarkupFeaturesTask;
import scout.edu.mit.ll.nics.android.api.tasks.ParseResourceRequestsTask;
import scout.edu.mit.ll.nics.android.api.tasks.ParseSimpleReportsTask;
import scout.edu.mit.ll.nics.android.auth.AuthManager;
import scout.edu.mit.ll.nics.android.auth.providers.OpenAMAuthProvider;
import scout.edu.mit.ll.nics.android.utils.Constants;
import scout.edu.mit.ll.nics.android.utils.EncryptedPreferences;
import scout.edu.mit.ll.nics.android.utils.Intents;
import scout.edu.mit.ll.nics.android.utils.NotificationsHandler;

@SuppressWarnings("unchecked")
public class RestClient {
    private static Context mContext = null;
    private static DataManager mDataManager = null;
    private static GsonBuilder mBuilder = new GsonBuilder();
    private static Header[] mAuthHeader = null;
    
    private static AsyncTask<ArrayList<ChatPayload>, Object, Integer> mParseChatMessagesTask;
    private static AsyncTask<ArrayList<SimpleReportPayload>, Object, Integer> mParseSimpleReportsTask;
    private static AsyncTask<ArrayList<FieldReportPayload>, Object, Integer> mParseFieldReportsTask;
    private static AsyncTask<ArrayList<DamageReportPayload>, Object, Integer> mParseDamageReportsTask;
    private static AsyncTask<ArrayList<ResourceRequestPayload>, Object, Integer> mParseResourceRequestTask;
    private static AsyncTask<ArrayList<WeatherReportPayload>, Object, Integer> mParseWeatherReportsTask;
    private static AsyncTask<MarkupPayload, Object, Integer> mParseMarkupFeaturesTask;
    
    private static SparseArray<SimpleReportResponseHandler> mSimpleReportResponseHandlers;
    private static SparseArray<DamageReportResponseHandler> mDamageReportResponseHandlers;
    
    private static boolean firstRun = true;

	private static String mDeviceId;

	private static boolean mSendingSimpleReports;
	private static boolean mSendingFieldReports;
	private static boolean mSendingDamageReports;
	private static boolean mSendingResourceRequests;
	private static boolean mSendingWeatherReports;
	private static boolean mSendingChatMessages;
	private static boolean mSendingMarkupFeatures;

	private static boolean mFetchingSimpleReports;
	private static boolean mFetchingFieldReports;
	private static boolean mFetchingDamageReports;
	private static boolean mFetchingResourceRequests;
	private static boolean mFetchingWeatherReports;
	private static boolean mFetchingChatMessages;
	private static boolean mFetchingMarkupFeatures;
	
	private static AuthManager mAuthManager;   
    
    public static void switchOrgs(int orgId) {	
/*
		try {
	    	LoginPayload p = new LoginPayload(mDataManager.getUsername());
	    	p.setWorkspaceId(mDataManager.getWorkspaceId());
	    	p.setOrgId(orgId);
	    	
			StringEntity entity = new StringEntity(p.toJsonString());
			
	    	mAuthManager.getClient().post("login/switchOrg", entity, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
	    			Log.e(Constants.nics_DEBUG_ANDROID_TAG, "Success to switch orgs." + content);
	    			LoginMessage message = mBuilder.create().fromJson(content, LoginMessage.class);
					LoginPayload payload = message.getLoginPayload().get(0);
					mDataManager.setLoginData(payload);
					mDataManager.requestOrgCapabilitiesUpdate();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
	    			Log.e(Constants.nics_DEBUG_ANDROID_TAG, "Fail to switch orgs." + content);
				}
	    	});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		*/
    }
    
    //Test function to test JSON Parsing
    public static void getHardCodedOrgCapabilities(){// {'name':'FR-Form','capId':10}, {'name':'RES-Form','capId':14},  , {'name':'SVR-Form','capId':14}
    	String content ="{'message':'ok','count':5,'capabilities':[{'name':'Chat','capId':14}, {'name':'MapMarkup','capId':14}, {'name':'WR-Form','capId':10}, {'name':'DR-Form','capId':10}],'orgCapability':null,'capabilitiesName':null}";
    	OrgCapabilities OrgCap = new OrgCapabilities();
    	OrgCap.setCapabilitiesFromJSON(content);
    	mDataManager.setOrgCapabilites(OrgCap);
    }
    
    public static void getOrgCapabilities(long orgId){

    	mAuthManager.getClient().get("orgs/" + mDataManager.getWorkspaceId() +"/capabilities?orgId=" + orgId , new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String content = (responseBody != null) ? new String(responseBody) : "error";
				
				OrgCapabilities OrgCap = new OrgCapabilities();
				OrgCap.setCapabilitiesFromJSON(content);
				mDataManager.setOrgCapabilites(OrgCap);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				String content = (responseBody != null) ? new String(responseBody) : "error";
    			Log.e(Constants.nics_DEBUG_ANDROID_TAG, "Fail to get Org Capabilities." + content);
			}
    	});
    	
    }
    
    private static void attemptLogin(final boolean getActiveAssignment) {
    	try {
			mDataManager = DataManager.getInstance(mContext);
    		final String username = mDataManager.getUsername();
    		
			mDeviceId = Build.SERIAL;
			if(mDeviceId == null) {
				mDeviceId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
			}

			mSimpleReportResponseHandlers = new SparseArray<SimpleReportResponseHandler>();
			mDamageReportResponseHandlers = new SparseArray<DamageReportResponseHandler>();
			
	    	LoginPayload p = new LoginPayload(username);

	    	p.setWorkspaceId(mDataManager.getWorkspaceId());

//	    	p.setWorkspaceId(0);
//	    	p.setUserId(null);
//	    	p.setUserSessionId(null);
	    	
			StringEntity entity = new StringEntity(p.toJsonString());

			mAuthManager.getClient().post("login", entity, new AsyncHttpResponseHandler() {
	
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					
					//if you don't properly logout then the api doesn't handle the usersession properly and sends back "error" next time you try to log in.
					//Until the APi sends back the response that the user is already logged in. I am currently catching the error and logging out, then logging back in.
					if(content.equals("error")){
						logout(username, true, getActiveAssignment);
						return;
					}
					
					
			        mDataManager.setLoggedIn(true);
					Log.i("nicsRest", "Successfully logged in as: " + username + " status code: " + statusCode );
					
					mAuthHeader = headers;
					
					LoginMessage message = mBuilder.create().fromJson(content, LoginMessage.class);
					LoginPayload payload = message.getLoginPayload().get(0);
					
					mDataManager.setLoginData(payload);
					
			        Intent intent = new Intent();
			        intent.setAction(Intents.nics_SUCCESSFUL_LOGIN);
			        intent.putExtra("payload", message.toJsonString());
			        mContext.sendBroadcast (intent);
			        
			        getUserOrgs(payload.getUserId());
					getAllIncidents(payload.getUserId());
					getUserData(payload.getUserId());
					mDataManager.requestOrgCapabilitiesUpdate();
					
//					if(getActiveAssignment) {
//						getActiveAssignment(username, payload.getUserId());
//					}
					//getWeatherUpdate(mDataManager.getMDTLatitude(), mDataManager.getMDTLongitude());
	
					mDataManager.addPersonalHistory("User " + username + " logged in successfully. ");
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					
			        mDataManager.setLoggedIn(false);
			        mDataManager.stopPollingAlarms();
			        
					boolean broadcast = true;
					
			        Intent intent = new Intent();
			        intent.setAction(Intents.nics_FAILED_LOGIN);

					if(error.getClass() == HttpResponseException.class) {
						HttpResponseException exception = (HttpResponseException)error;
						
						if(exception.getStatusCode() == 412) {
							broadcast = false;
							LoginMessage message = mBuilder.create().fromJson(content, LoginMessage.class);
							Log.w("nicsRest", message.getMessage());
							logout(username, true, getActiveAssignment);
						} else if(exception.getStatusCode() == 401) {
					        intent.putExtra("message", "Invalid username or password");
						} else {
					        intent.putExtra("message", exception.getMessage());
						}
					} else {
						
						if(error.getMessage()!= null){
							Log.e("nicsRest", error.getMessage());
						}else{
							Log.e("nicsRest", "null error on failed login attempt");
						}
						intent.putExtra("offlineMode", true);
						
						if(error.getClass() == UnknownHostException.class) {
							 intent.putExtra("message", "Failed to connect to server. Please check your network connection.");	
						} else {
							if(error.getMessage()!= null){
								intent.putExtra("message", error.getMessage());
							}else{
								Log.e("nicsRest", "null error on failed login attempt");
							}
						}
						error.printStackTrace();
					}
					
					if(broadcast) {
				        mContext.sendBroadcast (intent);
				        
				        if(intent.getExtras() != null) {
				        	mDataManager.addPersonalHistory("User " + username + " login failed: " + intent.getExtras().get("message"));
				        } else {
				        	mDataManager.addPersonalHistory("User " + username + " login failed.");
				        }
					}
				}
			});
    	} catch (UnsupportedEncodingException e) {
    		Log.e("nicsRest", e.getLocalizedMessage());
    		
    		Intent intent = new Intent();
    	    intent.setAction(Intents.nics_FAILED_LOGIN);
    	    
    		intent.putExtra("offlineMode", true);
    	    intent.putExtra("message", "Failed to connect to server: " + e.getLocalizedMessage() + " - Please check your network connection.");
    	    mContext.sendBroadcast (intent);
    	}
    }
    
	public static void login(final Context context, final String username, final String password, final boolean getActiveAssignment) {
		mContext = context;
		
		mAuthManager = AuthManager.getInstance(username, password);
		mAuthManager.registerAuthType(new OpenAMAuthProvider(mContext));		//enable for openAM
			
//		mAuthManager.registerAuthType(new BasicAuthProvider());			//enable for basic
//		also check AuthManager getClient() for the correct auth type
		
		attemptLogin(false);
	}

	public static void logout(final String username, final boolean retryLogin, final boolean getActiveAssignment) {
		if(mAuthManager != null) {
		
			//	mAuthManager.getClient().post("login?username=" + username, new RequestParams(), new AsyncHttpResponseHandler() {
			mAuthManager.getClient().delete("login/" + username, new AsyncHttpResponseHandler() {
			
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					
					Log.i("nicsRest", "Successfully logged out: " + username);
					mFetchingChatMessages = false;
					mFetchingFieldReports = false;
					mFetchingDamageReports = false;
					mFetchingMarkupFeatures = false;
					mFetchingResourceRequests = false;
					mFetchingSimpleReports = false;
					
					mSendingChatMessages = false;
					mSendingFieldReports = false;
					mSendingDamageReports = false;
					mSendingMarkupFeatures = false;
					mSendingResourceRequests = false;
					mSendingSimpleReports = false;
					
					clearParseChatMessagesTask();
					clearParseFieldReportTask();
					clearParseDamageReportTask();
					clearParseMarkupFeaturesTask();
					clearParseResourceRequestTask();
					clearParseSimpleReportTask();
					
					mDataManager.stopPollingAlarms();
					
					if(retryLogin) {
						attemptLogin(getActiveAssignment);
					} else {
						mDataManager.addPersonalHistory("User " + username + " logged out successfully. ");
						mDataManager.setLoggedIn(false);
					}
				}
	
				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					Log.e("nicsRest", "Failed to log out: " + username);
				}
			});
		}
	}

	public static void deleteMarkup(final String featureId) {
		mAuthManager.getClient().delete("mapmarkups/" + mDataManager.getWorkspaceId() + "/" + featureId, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				
				Log.i("nicsRest", "Successfully deleted feature: " + featureId);
				mDataManager.addPersonalHistory("Successfully deleted feature: " + featureId);
				mDataManager.deleteMarkupHistoryForCollabroomByFeatureId(mDataManager.getSelectedCollabRoom().getCollabRoomId(), featureId);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

				Log.e("nicsRest", "Failed to delete out: " + featureId);
				mDataManager.addPersonalHistory("Failed to delete out: " + featureId);
			}
		});
	}
	
	public static void getUserData(long userId) {
		mAuthManager.getClient().get("users/" + mDataManager.getWorkspaceId() + "/" + userId, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String content = (responseBody != null) ? new String(responseBody) : "error";
				
				UserMessage message = mBuilder.create().fromJson(content, UserMessage.class);
				if(message.getCount() > 0) {
					mDataManager.setUserData(message.getUsers().get(0));
					Log.i("nicsRest", "Successfully received user information.");
					mDataManager.addPersonalHistory("Successfully received user information.");
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				String content = (responseBody != null) ? new String(responseBody) : "error";
				
				Log.e("nicsRest", content);
				
				Intent intent = new Intent();
			    intent.setAction(Intents.nics_FAILED_LOGIN);
			    
				intent.putExtra("offlineMode", true);
		        intent.putExtra("message", "Failed to connect to server: " + content + " - Please check your network connection.");
		        mDataManager.addPersonalHistory("Failed to receive user information.");
		        mContext.sendBroadcast (intent);
			}
		});
	}
	
	public static void getUserOrgs(long userId) {
		mAuthManager.getClient().get("orgs/" + mDataManager.getWorkspaceId() + "?userId=" + userId, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String content = (responseBody != null) ? new String(responseBody) : "error";
				
				OrganizationMessage message = mBuilder.create().fromJson(content, OrganizationMessage.class);
				if(message.getCount() > 0) {
					mDataManager.setOrganizations(message.getOrgs());
					
					EncryptedPreferences preferences;
					preferences = new EncryptedPreferences(mContext.getSharedPreferences(Constants.nics_USER_PREFERENCES, 0));
					preferences.savePreferenceString("savedOrgs", message.toJsonString());
					
			        Intent intent = new Intent();
			        intent.setAction(Intents.nics_SUCCESSFUL_GET_USER_ORGANIZATION_INFO);
			        intent.putExtra("payload", message.toJsonString());
			        mContext.sendBroadcast (intent);
			        
					Log.i("nicsRest", "Successfully received user organization information.");
					mDataManager.addPersonalHistory("Successfully received user organization information.");
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				
				if(error.getClass().equals(HttpResponseException.class)) {
					HttpResponseException exception = (HttpResponseException)error;
					if(exception.getStatusCode() == 404) {
						Intent intent = new Intent();
				        intent.setAction(Intents.nics_SUCCESSFUL_GET_USER_ORGANIZATION_INFO);
				        mContext.sendBroadcast (intent);
					}
				}
				Log.i("nicsRest", "Failed to receive user organization information.");
			}
		});
	}
	
	public static void getActiveAssignment(final String username, final long userId) {
		mAuthManager.getClient().get("assignments?username=" + username + "&activeOnly=true", new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String content = (responseBody != null) ? new String(responseBody) : "error";
				
				AssignmentMessage message = mBuilder.create().fromJson(content, AssignmentMessage.class);
				message.parse();

	        	AssignmentPayload currentAssignment = null;

	        	if(message.getCount() == 1) {
	        		currentAssignment = message.getTaskingAssignmentsList().get(0);
					boolean isCurrent = mDataManager.getCurrentAssignment().equals(currentAssignment);
					
					if(!isCurrent) {
						getIncident(currentAssignment.getPhiUnit().getIncidentId(), false, userId, true);
					} else {
						getIncident(currentAssignment.getPhiUnit().getIncidentId(), false, userId, false);
						
				        Intent intent = new Intent();
				        intent.setAction(Intents.nics_UPDATE_ASSIGNMENT_RECEIVED);
				        intent.putExtra("payload", message.toJsonString());
				        mContext.sendBroadcast (intent);
						
					}
					
//					mDataManager.setCurrentAssignmentData(currentAssignment, isCurrent);
					
	        	} else {
	        		currentAssignment = new AssignmentPayload();

					boolean isCurrent = mDataManager.getCurrentAssignment().equals(currentAssignment);
					if(!isCurrent) {
//						mDataManager.setCurrentAssignmentData(null, false);
					}
					
					if(firstRun || !mDataManager.getCurrentAssignment().getPhiOperationalPeriod().equals(currentAssignment.getPhiOperationalPeriod())) {
						getIncident(-1, true, userId, true);
						firstRun = false;
					} else {
						if(!isCurrent) {
							getIncident(-1, false, userId, false);
						} else {
					        Intent intent = new Intent();
					        intent.setAction(Intents.nics_SUCCESSFUL_GET_INCIDENT_INFO);
					        intent.putExtra("payload", message.toJsonString());
					        mContext.sendBroadcast (intent);
						}
					}
	        	}
	        	
				mDataManager.setCurrentAssignment(currentAssignment);
				
				Log.i("nicsRest", "Successfully received active assignment information.");
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				String content = (responseBody != null) ? new String(responseBody) : "error";

				Log.e("nicsRest", content);
				
				Intent intent = new Intent();
			    intent.setAction(Intents.nics_FAILED_LOGIN);
			    
				intent.putExtra("offlineMode", true);
		        intent.putExtra("message", "Failed to connect to server: " + content + " - Please check your network connection.");
		        mContext.sendBroadcast (intent);
			}
		});
	}
	
	public static void getAllIncidents(long userId) {
		mAuthManager.getClient().get("incidents/"  + mDataManager.getWorkspaceId() + "?accessibleByUserId=" + userId, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String content = (responseBody != null) ? new String(responseBody) : "error";
				
				IncidentMessage message = mBuilder.create().fromJson(content, IncidentMessage.class);
				
				HashMap<String, IncidentPayload> incidents = new HashMap<String, IncidentPayload>();
				for(IncidentPayload incident : message.getIncidents()) {
					incidents.put(incident.getIncidentName(), incident);
				}
				mDataManager.setIncidents(incidents);
				
				EncryptedPreferences preferences;
				preferences = new EncryptedPreferences(mContext.getSharedPreferences(Constants.nics_USER_PREFERENCES, 0));
				preferences.savePreferenceString("savedIncidents", message.toJsonString());
				
		        Intent intent = new Intent();
		        intent.setAction(Intents.nics_SUCCESSFUL_GET_ALL_INCIDENT_INFO);
		        intent.putExtra("payload", message.toJsonString());
				mContext.sendBroadcast (intent);
		        
		        Log.e("nicsRest", "Successfully received incident information.");
		        mDataManager.addPersonalHistory("Successfully received incident information.");
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				
				String savedIncidents = mContext.getSharedPreferences(Constants.nics_USER_PREFERENCES, 0).getString(Constants.SAVED_INCIDENTS, null);
				
				if(savedIncidents != null) {
					
					IncidentMessage message = mBuilder.create().fromJson(savedIncidents, IncidentMessage.class);
				
					HashMap<String, IncidentPayload> incidents = new HashMap<String, IncidentPayload>();
					for(IncidentPayload incident : message.getIncidents()) {
						if(incident.getCollabrooms().size() > 0) {
							incidents.put(incident.getIncidentName(), incident);
						}
					}
					mDataManager.setIncidents(incidents);
				
			        Intent intent = new Intent();
			        intent.setAction(Intents.nics_SUCCESSFUL_GET_ALL_INCIDENT_INFO);
					intent.putExtra("offlineMode", true);
			        intent.putExtra("payload", message.toJsonString());
			        mContext.sendBroadcast (intent);
			        
			        mDataManager.addPersonalHistory("Failed to receive incident information.");
				}
			    
//		        intent.putExtra("message", "Failed to connect to server: " + content + " - Please check your network connection.");
			}
		});
	}

	public static void getIncident(final long incidentId, final boolean isWorkingMap, final long userId, final boolean sendAssignmentIntent) {
		mAuthManager.getClient().get("incidents/"  + mDataManager.getWorkspaceId() + "/" + incidentId, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String content = (responseBody != null) ? new String(responseBody) : "error";
				
				IncidentMessage message = mBuilder.create().fromJson(content, IncidentMessage.class);
				IncidentPayload incident = message.getIncidents().get(0);
				
				long collabRoomId = -1;
				String collabRoomName = "";
				if(isWorkingMap) {
					for(CollabroomPayload payload : incident.getCollabrooms()) {
						if(payload.getName().contains("WorkingMap")) {
			        		Log.i("nicsRest", "Assigned to: " + payload.getName());
			        		collabRoomId = payload.getCollabRoomId();
			        		collabRoomName = payload.getName();
			        		payload.setIncidentId(incidentId);
			        		mDataManager.addCollabroom(payload);
			        		if(sendAssignmentIntent) {
			    				mDataManager.setCurrentIncidentData(null, collabRoomId, collabRoomName);
			        			//mDataManager.setSelectedCollabRoom(collabRoomName, collabRoomId);
			        		}
			        		break;
						}
					}
				} else {
					collabRoomName = mDataManager.getSelectedCollabRoom().getName();
					
					if(collabRoomName == null || collabRoomName.isEmpty()) {
						collabRoomName = mDataManager.getActiveCollabroomName();
					}
				}
				mDataManager.setCurrentIncidentData(null, collabRoomId, collabRoomName);
				
				if(userId != -1 && sendAssignmentIntent && !isWorkingMap) {
					mDataManager.clearCollabRoomList();
				}
//				getUserCollabrooms(incident.getIncidentId(), userId, false);
				
		        Intent intent = new Intent();
		        intent.setAction(Intents.nics_SUCCESSFUL_GET_INCIDENT_INFO);
		        intent.putExtra("payload", message.toJsonString());
		        mContext.sendBroadcast (intent);
		        
		        if(sendAssignmentIntent) {
			        Intent assignmentIntent = new Intent ();
			        assignmentIntent.setAction(Intents.nics_NEW_ASSIGNMENT_RECEIVED);
			        if(incidentId == -1) {
			        	assignmentIntent.putExtra("clear-task", true);
			        	
		        		OperationalUnit unit = new OperationalUnit();					
						unit.setCollabroomId(mDataManager.getActiveCollabroomId());
						unit.setCollabroomName(mDataManager.getActiveCollabroomName());
						unit.setIncidentId(mDataManager.getActiveIncidentId());
						unit.setIncidentName(mDataManager.getActiveIncidentName());
						AssignmentPayload payload = mDataManager.getCurrentAssignment();
						payload.setPhiUnit(unit);
						mDataManager.setCurrentAssignment(payload);
			        }
			        
			        AssignmentPayload payload = mDataManager.getCurrentAssignment();
			        
			        if(mDataManager.getPreviousIncidentId() != payload.getPhiUnit().getIncidentId() && mDataManager.getPreviousCollabroom().getCollabRoomId() != payload.getPhiUnit().getCollabroomId()) {
			        	NotificationsHandler.getInstance(mContext).createAssignmentChangeNotification(payload);
			        }
					
			        getSimpleReports(-1, -1, incidentId);
					getFieldReports(-1, -1, incidentId);
					getDamageReports(-1, -1, incidentId);
					getResourceRequests(-1, -1, incidentId);
					getWeatherReports(-1, -1, incidentId);
					
					if(collabRoomName.contains("-")) {
//						getChatHistory(incident.getIncidentName(), collabRoomName.split("-")[1]);
//						getMarkupHistory(collabRoomId);
					}
					
			        mContext.sendBroadcast (assignmentIntent);
		        }
		        
				Log.i("nicsRest", "Successfully received incident information.");
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				String content = (responseBody != null) ? new String(responseBody) : "error";
				
				Log.e("nicsRest", content);
				
				Intent intent = new Intent();
			    intent.setAction(Intents.nics_FAILED_LOGIN);
			    
				intent.putExtra("offlineMode", true);
		        intent.putExtra("message", "Failed to connect to server: " + content + " - Please check your network connection.");
		        mContext.sendBroadcast (intent);
			}
		});
	}

	public static void getCollabRooms(final long incidentId, final String incidentName) {
		 Log.e("nicsRest", "requesting collabrooms for " + incidentName);
			Intent intent = new Intent();
		    intent.setAction(Intents.nics_POLLING_COLLABROOMS);
		    mContext.sendBroadcast (intent);
		mAuthManager.getClient().get("collabroom/" + incidentId + "?userId=" + mDataManager.getUserId(), new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String content = (responseBody != null) ? new String(responseBody) : "error";

				 Log.e("nicsRest", "successfully pulled rooms for " + incidentName + " with code " + statusCode);
				
				CollaborationRoomMessage message = mBuilder.create().fromJson(content, CollaborationRoomMessage.class);
				
				HashMap<String, CollabroomPayload> rooms = new HashMap<String, CollabroomPayload>();
				for(CollabroomPayload room : message.getresults()) {
					rooms.put(room.getName(), room);
					
					room.setName(room.getName().replace(mDataManager.getActiveIncidentName() + "-", ""));	
					mDataManager.addCollabroom(room);
				}
		//		ArrayList<CollabroomPayload> roomList =  new ArrayList<CollabroomPayload>(rooms.values());
				
				mDataManager.setCollabRoomsForIncident(incidentName, message.getresults());

				Intent intent = new Intent();
			    intent.setAction(Intents.nics_SUCCESSFULLY_GET_COLLABROOMS);
			    mContext.sendBroadcast (intent);
				
			}
				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					 Log.e("nicsRest", "failed to pull collabrooms.");
					Intent intent = new Intent();
				    intent.setAction(Intents.nics_FAILED_GET_COLLABROOMS);
				    mContext.sendBroadcast (intent);
				
				}
			});
	}
	
	public static void getSimpleReports(int offset, int limit, final long incidentId) {
		if(!mFetchingSimpleReports && mParseSimpleReportsTask == null && incidentId != -1) {
			String url = "reports/"  + mDataManager.getActiveIncidentId() + "/SR?sortOrder=desc&fromDate=" + (mDataManager.getLastSimpleReportTimestamp() + 1);
			
//			if(incidentId != -1) {
//				url += "&incidentId=" + incidentId;
//			}
			
			mFetchingSimpleReports = true;
			
			mAuthManager.getClient().get(url, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					SimpleReportMessage message = mBuilder.create().fromJson(content, SimpleReportMessage.class);
					
					if(message != null) {
						ArrayList<SimpleReportPayload> srPayloads = message.getReports();
						
						if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							mParseSimpleReportsTask = new ParseSimpleReportsTask(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, srPayloads);
						} else {
							mParseSimpleReportsTask = new ParseSimpleReportsTask(mContext).execute(srPayloads);
						}
						Log.i("nicsRest", "Successfully received simple report information.");
					}
					mFetchingSimpleReports = false;
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					mFetchingSimpleReports = false;
				}
			});
		}
	}
	
	public static void getFieldReports(int offset, int limit, final long incidentId) {
		if(!mFetchingFieldReports && mParseFieldReportsTask == null && incidentId != -1) {
			String url = "reports/" + mDataManager.getActiveIncidentId() + "/FR?sortOrder=desc&fromDate=" + (mDataManager.getLastFieldReportTimestamp() + 1);
			
			if(incidentId != -1) {
				url += "&incidentId=" + incidentId;
			}
			
			mFetchingFieldReports = true;
			
			mAuthManager.getClient().get(url, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					
					FieldReportMessage message = mBuilder.create().fromJson(content, FieldReportMessage.class);
					
					if(message != null) {
						ArrayList<FieldReportPayload> frPayloads = message.getReports();

						if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							mParseFieldReportsTask = new ParseFieldReportsTask(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, frPayloads);
						} else {
							mParseFieldReportsTask = new ParseFieldReportsTask(mContext).execute(frPayloads);
						}
						Log.i("nicsRest", "Successfully received field report information.");
					}
					mFetchingFieldReports = false;
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					mFetchingFieldReports = false;
				}
			});
		}
	}
	
	public static void getDamageReports(int offset, int limit, final long incidentId) {
		if(!mFetchingDamageReports && mParseDamageReportsTask == null && incidentId != -1) {
			String url = "reports/" + mDataManager.getActiveIncidentId() + "/DMGRPT?sortOrder=desc&fromDate=" + (mDataManager.getLastDamageReportTimestamp() + 1);
			
			if(incidentId != -1) {
				url += "&incidentId=" + incidentId;
			}
			
			mFetchingDamageReports = true;
			
			mAuthManager.getClient().get(url, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					
					DamageReportMessage message = mBuilder.create().fromJson(content, DamageReportMessage.class);
					
					if(message != null) {
						ArrayList<DamageReportPayload> drPayloads = message.getReports();

						if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							mParseDamageReportsTask = new ParseDamageReportsTask(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, drPayloads);
						} else {
							mParseDamageReportsTask = new ParseDamageReportsTask(mContext).execute(drPayloads);
						}
						Log.i("nicsRest", "Successfully received damage report information.");
					}
					mFetchingDamageReports = false;
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					mFetchingDamageReports = false;
				}
			});
		}
	}	
	
	public static void getResourceRequests(int offset, int limit, final long incidentId) {
		if(!mFetchingResourceRequests && mParseResourceRequestTask == null && incidentId != -1) {
			String url = "reports/" + mDataManager.getActiveIncidentId() + "/RESREQ?sortOrder=desc&fromDate=" + (mDataManager.getLastResourceRequestTimestamp() + 1);
			
			if(incidentId != -1) {
				url += "&incidentId=" + incidentId;
			}
			
			mFetchingResourceRequests = true;
			
			mAuthManager.getClient().get(url , new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					
					ResourceRequestMessage message = mBuilder.create().fromJson(content, ResourceRequestMessage.class);
					
					if(message != null) {
						ArrayList<ResourceRequestPayload> resourceRequestPayloads = message.getReports();

						if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							mParseResourceRequestTask = new ParseResourceRequestsTask(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, resourceRequestPayloads);
						} else {
							mParseResourceRequestTask = new ParseResourceRequestsTask(mContext).execute(resourceRequestPayloads);
						}
						Log.i("nicsRest", "Successfully received resource request information.");
					}
					mFetchingResourceRequests = false;
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					String content = (responseBody != null) ? new String(responseBody) : "error";

					mFetchingResourceRequests = false;
					Log.i("nicsRest", "Failed to received resource request information for: " + content);
				}
			});
		}
	}
	
	public static void getWeatherReports(int offset, int limit, final long incidentId) {
		if(!mFetchingWeatherReports && mParseWeatherReportsTask == null && incidentId != -1) {
			String url = "reports/" + mDataManager.getActiveIncidentId() + "/WR?sortOrder=desc&fromDate=" + (mDataManager.getLastWeatherReportTimestamp() + 1);
			
			if(incidentId != -1) {
				url += "&incidentId=" + incidentId;
			}
			
			mFetchingWeatherReports = true;
			
			mAuthManager.getClient().get(url , new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					WeatherReportMessage message = mBuilder.create().fromJson(content, WeatherReportMessage.class);
					
					if(message != null) {
						ArrayList<WeatherReportPayload> wrPayloads = message.getReports();

						if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							mParseWeatherReportsTask = new ParseWeatherReportsTask(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, wrPayloads);
						} else {
							mParseWeatherReportsTask = new ParseWeatherReportsTask(mContext).execute(wrPayloads);
						}
						Log.i("nicsRest", "Successfully received weather report information.");
					}
					mFetchingWeatherReports = false;
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					mFetchingWeatherReports = false;
					Log.i("nicsRest", "Failed to received weather report information for: " + content);
				}
			});
		}
	}

	public static void getChatHistory(final long incidentId, final long collabRoomId) {
		if(!mFetchingChatMessages && mParseChatMessagesTask == null && incidentId != -1 && collabRoomId != -1) {

				mFetchingChatMessages = true;
				mAuthManager.getClient().get("chatmsgs/" + collabRoomId + "?sortOrder=desc&fromDate=" + (mDataManager.getLastChatTimestamp(incidentId, collabRoomId)+1) + "&dateColumn=created" , new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						String content = (responseBody != null) ? new String(responseBody) : "error";
						ChatMessage message = mBuilder.create().fromJson(content, ChatMessage.class);
								
						try {
							JSONObject jObject = new JSONObject(content);
							ArrayList<ChatPayload> chatMessages = message.getChatMsgs();
							
							for(int i = 0; i < message.getCount(); i++){
								chatMessages.get(i).setIncidentId(incidentId);								
								chatMessages.get(i).setuserId(jObject.getJSONArray("chats").getJSONObject(i).getJSONObject("userorg").getJSONObject("user").getLong("userId"));
								chatMessages.get(i).setUserOrgName(jObject.getJSONArray("chats").getJSONObject(i).getJSONObject("userorg").getJSONObject("org").getString("name"));
								chatMessages.get(i).setNickname(jObject.getJSONArray("chats").getJSONObject(i).getJSONObject("userorg").getJSONObject("user").getString("username"));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if(message != null) {
							if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								mParseChatMessagesTask = new ParseChatMessagesTask(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,  message.getChatMsgs());
							} else {
								mParseChatMessagesTask = new ParseChatMessagesTask(mContext).execute(message.getChatMsgs());
							}
							Log.i("nicsRest", "Successfully received chat information for: " + incidentId + "-" + collabRoomId);
						}
						mFetchingChatMessages = false;
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						String content = (responseBody != null) ? new String(responseBody) : "error";
						
						Log.e("nicsRest", "Failed to receive chat information for: " + incidentId + "-" + collabRoomId);
						Log.e("nicsRest", content + " " + error.getLocalizedMessage());
						mFetchingChatMessages = false;
					}
				});
			}
		}
	
	public static void getMarkupHistory(final long collabRoomId) {
		if(collabRoomId != -1){
			if(!mFetchingMarkupFeatures && mParseMarkupFeaturesTask == null && collabRoomId != -1) {
				mFetchingMarkupFeatures = true;
				mAuthManager.getClient().get("features/collabroom/" + collabRoomId + "?geoType=4326&userId=" + mDataManager.getUserId() + "&fromDate=" + (mDataManager.getLastMarkupTimestamp(collabRoomId) + 1)+"&dateColumn=seqtime", new AsyncHttpResponseHandler() {
					
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						String content = (responseBody != null) ? new String(responseBody) : "error";
						MarkupMessage message = mBuilder.create().fromJson(content, MarkupMessage.class);
	
						for(int i = 0; i < message.getFeatures().size();i++){
							message.getFeatures().get(i).buildVector2Point(true);
						}
						
						MarkupPayload payload = new MarkupPayload();
						payload.setFeatures(message.getFeatures());
						payload.setDeletedFeatures(message.getDeletedFeature());
						payload.setCollabRoomId(collabRoomId);
						
						if(message != null) {
							if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								mParseMarkupFeaturesTask = new ParseMarkupFeaturesTask(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, payload);
							} else {
								mParseMarkupFeaturesTask = new ParseMarkupFeaturesTask(mContext).execute(payload);
							}
							Log.i("nicsRest", "Successfully received markup information.");
						}
						mFetchingMarkupFeatures = false;
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						String content = (responseBody != null) ? new String(responseBody) : "error";
						
						if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							mParseMarkupFeaturesTask = new ParseMarkupFeaturesTask(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (MarkupPayload[])null);
						} else {
							mParseMarkupFeaturesTask = new ParseMarkupFeaturesTask(mContext).execute((MarkupPayload[])null);
						}
						Log.i("nicsRest", "Failed to receive markup history: " + content);
						mFetchingMarkupFeatures = false;
				}
			});
					
			}
	 	}
	}

	public static void getWeatherUpdate(final double latitude, final double longitude) {
		if(!Double.isNaN(latitude) && !Double.isNaN(longitude) ) {
			mAuthManager.getClient().get("http://forecast.weather.gov/MapClick.php" + "?lat=" + latitude + "&lon=" + longitude + "&FcstType=json&lg=english", new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					
					try {
						WeatherPayload payload = mBuilder.create().fromJson(content, WeatherPayload.class);
	
						if(payload != null) {
							mDataManager.setWeather(payload);
							
							Log.i("nicsRest", "Successfully received weather information for: " + latitude + "," + longitude);
					        Intent intent = new Intent();
					        intent.setAction(Intents.nics_NEW_WEATHER_REPORT_RECEIVED);
					        intent.putExtra("payload", payload.toJsonString());
					        mContext.sendBroadcast (intent);
						}
					} catch(Exception e) {
						Log.e("nicsRest", "Failed to load weather information for: " + latitude + "/" + longitude);
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					Log.e("nicsRest", "Failed to load weather information for: " + latitude + "/" + longitude);
					
				}
			});
		}
	}
	
	public static void getWFSLayers(){
		
			mAuthManager.getClient().get("datalayer/"+ mDataManager.getWorkspaceId() +"/tracking", new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					TrackingLayerMessage message = mBuilder.create().fromJson(content, TrackingLayerMessage.class);
					Log.i("nicsRest", "Succesfully received Tracking Layers: " + message.getCount());

					mDataManager.setTrackingLayers(message.getLayers());
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					Log.i("nicsRest", "Failed to receive Tracking Layers: " + content);
					
			}
		});	
	}
	
	public static void getWFSData(final TrackingLayerPayload layer, int numResults, String mLastFeatureTimestamp, AsyncHttpResponseHandler responseHandler) {
		
//		AsyncHttpClient mClient = new AsyncHttpClient();
//		mClient.setTimeout(60 * 1000);
//		mClient.setURLEncodingEnabled(false);
//		mClient.setMaxRetriesAndTimeout(2, 1000);
		
		if(layer.getDatasourceid() == null ){	// no token needed, pull layer
			
			if(layer.shouldExpectJson())
			{
				Log.d("NICS REST", layer.getInternalurl()+"?service=WFS&outputFormat=json&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults);
				mAuthManager.getClient().get(layer.getInternalurl()+"?service=WFS&outputFormat=json&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults, responseHandler);
				return;
			}else{
				Log.d("NICS REST", layer.getInternalurl()+"?service=WFS&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults);
				mAuthManager.getClient().get(layer.getInternalurl()+"?service=WFS&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults, responseHandler);
				return;
			}
		}else{
			if(layer.getAuthtoken() == null){	//get token for layer
				RestClient.getWFSDataToken(layer, numResults, mLastFeatureTimestamp, responseHandler);
			}else{	//already have token so pull layer
				if(layer.getAuthtoken().getExpires() <= System.currentTimeMillis()){	//token is expired so pull a new one
					RestClient.getWFSDataToken(layer, numResults, mLastFeatureTimestamp, responseHandler);
				}else{
					if(layer.getAuthtoken().getToken() != null){
						
						if(layer.shouldExpectJson())
						{
							Log.d("NICS REST", layer.getInternalurl()+"?service=WFS&outputFormat=json&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults + "&token=" + layer.getAuthtoken().getToken());
							mAuthManager.getClient().get(layer.getInternalurl()+"?service=WFS&outputFormat=json&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults + "&token=" + layer.getAuthtoken().getToken(), responseHandler);
						}else{
							Log.d("NICS REST", layer.getInternalurl()+"?service=WFS&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults + "&token=" + layer.getAuthtoken().getToken());
							mAuthManager.getClient().get(layer.getInternalurl()+"?service=WFS&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults + "&token=" + layer.getAuthtoken().getToken(), responseHandler);
						}
					}	
				}
			}
		}
	}
	
	public static void getWFSDataToken(final TrackingLayerPayload layer,final int numResults,final String mLastFeatureTimestamp,final AsyncHttpResponseHandler responseHandler) {
				
		Log.d("NICS REST","WFS Data Token: " + mDataManager.getServer() + "datalayer/1/token/" + layer.getDatasourceid());
		mAuthManager.getClient().get(mDataManager.getServer() + "datalayer/" + mDataManager.getWorkspaceId() + "/token/" + layer.getDatasourceid(),  new AsyncHttpResponseHandler() {
			@SuppressWarnings("unchecked")

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String content = (responseBody != null) ? new String(responseBody) : "error";
				try {
					Log.d("NICS REST","Successfully received WFS Data Token: " + content);
					TrackingTokenPayload token = mBuilder.create().fromJson(content, TrackingTokenPayload.class);
					
					if(token.getToken() == null){
						token.setExpires(System.currentTimeMillis() + 120000);	//set not authorized token to expire in 2 minutes so rest client tries to pull it again later
					}else{
						RestClient.getWFSData(layer, numResults, mLastFeatureTimestamp, responseHandler);
					}
					
					layer.setAuthtoken(token);
					mDataManager.UpdateTrackingLayerData(layer);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				String content = (responseBody != null) ? new String(responseBody) : "error";
				Log.e("nicsRest", "Failed to authenticate WFS Layer: " + content);
			}
		});	
	}
	
	public static Header[] getAuthData() {
		return mAuthHeader;
	}

	public static void clearParseSimpleReportTask() {
		if(mParseSimpleReportsTask != null) {
			mParseSimpleReportsTask.cancel(true);
			mParseSimpleReportsTask = null;
		}
	}

	public static void clearParseFieldReportTask() {
		if(mParseFieldReportsTask != null) {
			mParseFieldReportsTask.cancel(true);
			mParseFieldReportsTask = null;
		}
	}
	
	public static void clearParseDamageReportTask() {
		if(mParseDamageReportsTask != null) {
			mParseDamageReportsTask.cancel(true);
			mParseDamageReportsTask = null;
		}
	}
	
	public static void clearParseResourceRequestTask() {
		if(mParseResourceRequestTask != null) {
			mParseResourceRequestTask.cancel(true);
			mParseResourceRequestTask = null;
		}
	}
	
	public static void clearParseWeatherReportTask() {
		if(mParseWeatherReportsTask != null) {
			mParseWeatherReportsTask.cancel(true);
			mParseWeatherReportsTask = null;
		}
	}
	
	public static void clearParseMarkupFeaturesTask() {
		if(mParseMarkupFeaturesTask != null) {
			mParseMarkupFeaturesTask.cancel(true);
			mParseMarkupFeaturesTask = null;
		}
	}
	
	public static boolean isParsingMarkup() {
		if(mParseMarkupFeaturesTask != null) {
			return true;
		}
		return false;
	}
	
	public static boolean isFetchingMarkup() {
		return mFetchingMarkupFeatures;
	}
	
	public static void clearParseChatMessagesTask() {
		if(mParseChatMessagesTask != null) {
			mParseChatMessagesTask.cancel(true);
			mParseChatMessagesTask = null;
		}
	}
	
	
	public static void postSimpleReports() {
		ArrayList<SimpleReportPayload> simpleReports = mDataManager.getAllSimpleReportStoreAndForwardReadyToSend();
		
		if(mSimpleReportResponseHandlers == null) {
			mSimpleReportResponseHandlers = new SparseArray<SimpleReportResponseHandler>();
		}
		
		for (SimpleReportPayload report : simpleReports) {
        	if(!report.isDraft() && mSimpleReportResponseHandlers != null && mSimpleReportResponseHandlers.indexOfKey((int)report.getId()) < 0 && !mSendingSimpleReports) {
        		Log.w("nics_POST", "Adding simple report " + report.getId() + " to send queue.");
        		SimpleReportData data = report.getMessageData();
        		
        		try {
        			if(data.getFullpath() != null && data.getFullpath() != ""){
        				
    					SimpleReportResponseHandler handler =  new SimpleReportResponseHandler(mContext, mDataManager, report.getId());
                		mSimpleReportResponseHandlers.put((int)report.getId(), handler);

		        		RequestParams params = new RequestParams();
		        		params.put("deviceId", mDeviceId);
		        		params.put("incidentId", String.valueOf(report.getIncidentId()));
		        		params.put("userId", String.valueOf(mDataManager.getUserId()));
		        		params.put("usersessionid", String.valueOf(report.getUserSessionId()));
		        		params.put("latitude", String.valueOf(data.getLatitude()));
		        		params.put("longitude", String.valueOf(data.getLongitude()));
		        		params.put("altitude", "0.0");
		        		params.put("track", "0.0");
		        		params.put("speed","0.0");
		        		params.put("accuracy", "0.0");
		        		params.put("description", data.getDescription());
		        		params.put("category", data.getCategory() != null ? data.getCategory().getText() : SimpleReportCategoryType.BLANK.getText());
		        		params.put("seqtime", String.valueOf(report.getSeqTime()));
		        		params.put("image", new File(data.getFullpath()));     		
        		
                		if(mAuthManager != null && mAuthManager.getClient() != null) {
//                			Log.e("nicsRest","incident id = " +  mDataManager.getActiveIncidentId());
                			
    	            		mAuthManager.getClient().post("reports/"  + mDataManager.getActiveIncidentId() + "/SR", params, handler);
    	        			mSendingSimpleReports = true;
                		}
		        		
        			}else{	//no image
		        		StringEntity entity = new StringEntity(report.toJsonString());
		    			mAuthManager.getClient().post("reports/"  + mDataManager.getActiveIncidentId()  + "/SR", entity, new SimpleReportNoImageResponseHandler(mContext, mDataManager, report.getId()));
		    			mSendingSimpleReports = true;
        			}
        		} catch(FileNotFoundException e) {
        			Log.e("nicsRest", "Deleting: " + report.getId() + " success: " + mDataManager.deleteSimpleReportStoreAndForward(report.getId()) + " due to invalid file.");
        			mDataManager.addPersonalHistory("Deleting simple report: " + report.getId() + " success: " + mDataManager.deleteSimpleReportStoreAndForward(report.getId()) + " due to invalid/missing image file.");
        			mSendingSimpleReports = false;
        		} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
		}
	}
		
	public static void removeSimpleReportHandler(long reportId) {
		Log.w("nics_POST", "Removing simple report " + reportId + " from send queue.");
		mSimpleReportResponseHandlers.remove((int)reportId);
	}
	
	public static void removeDamageReportHandler(long reportId) {
		Log.w("nics_POST", "Removing damage report " + reportId + " from send queue.");
		mDamageReportResponseHandlers.remove((int)reportId);
	}
		
	public static void postFieldReports() {
		if(!mSendingFieldReports) {
			ArrayList<FieldReportPayload> fieldReports = mDataManager.getAllFieldReportStoreAndForwardReadyToSend();
			
			for (FieldReportPayload report : fieldReports) {
				try {
		        	if(!report.isDraft()) {
		    			StringEntity entity = new StringEntity(report.toJsonString());
		    			
		    			mAuthManager.getClient().post("reports/"  + mDataManager.getActiveIncidentId() + "/FR", entity, new FieldReportResponseHandler(mContext, mDataManager, report.getId()));
		    			mSendingFieldReports = true;
		        	}
				} catch(UnsupportedEncodingException e) {
					
				}
			}
		}
	}
	
	public static void postDamageReports() {
		ArrayList<DamageReportPayload> damageReports = mDataManager.getAllDamageReportStoreAndForwardReadyToSend();
		
		if(mDamageReportResponseHandlers == null) {
			mDamageReportResponseHandlers = new SparseArray<DamageReportResponseHandler>();
		}
		
		for (DamageReportPayload report : damageReports) {
        	if(!report.isDraft() && mDamageReportResponseHandlers != null && mDamageReportResponseHandlers.indexOfKey((int)report.getId()) < 0 && !mSendingDamageReports) {
        		Log.w("nics_POST", "Adding damage report " + report.getId() + " to send queue.");
        		DamageReportData data = report.getMessageData();
        		
        		try {
        		
	        		if(data.getFullpath() != null && data.getFullpath() != ""){
		        		
		        		RequestParams params = new RequestParams();
		        		params.put("msg", data.toJsonString());
		        		params.put("deviceId", mDeviceId);
		        		params.put("incidentId", String.valueOf(report.getIncidentId()));
		//        		params.put("userId", String.valueOf(report.getSenderUserId()));
		        		params.put("usersessionid", String.valueOf(report.getUserSessionId()));
		        		params.put("seqtime", String.valueOf(report.getSeqTime()));
	        			params.put("image", new File(data.getFullpath()));
	        			
	        			DamageReportResponseHandler handler =  new DamageReportResponseHandler(mContext, mDataManager, report.getId());
	            		mDamageReportResponseHandlers.put((int)report.getId(), handler);
	            		
	            		mAuthManager.getClient().post("reports/"  + mDataManager.getActiveIncidentId() + "/DMGRPT", params, handler);
	        		}else{	//no image
		        		StringEntity entity = new StringEntity(report.toJsonString());
		    			mAuthManager.getClient().post("reports/"  + mDataManager.getActiveIncidentId()  + "/DMGRPT", entity, new DamageReportNoImageResponseHandler(mContext, mDataManager, report.getId()));
		    			mSendingDamageReports = true;
	        		}
            		
            		
        		} catch(FileNotFoundException e) {
        			Log.e("nicsRest", "Deleting: " + report.getId() + " success: " + mDataManager.deleteDamageReportStoreAndForward(report.getId()) + " due to invalid file.");
        			mSendingDamageReports = false;
        		} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					mSendingDamageReports = false;
        		}
        		
    			mSendingDamageReports = true;
        	}
		}
	}
	
	public static void postResourceRequests() {
		if(!mSendingResourceRequests) {
			ArrayList<ResourceRequestPayload> resourceRequests = mDataManager.getAllResourceRequestStoreAndForwardReadyToSend();
			
			for (ResourceRequestPayload request : resourceRequests) {
				try {
		        	if(!request.isDraft()) {
		    			StringEntity entity = new StringEntity(request.toJsonString());
		    			
		    			mAuthManager.getClient().post("reports/"  + mDataManager.getActiveIncidentId() + "/RESREQ", entity, new ResourceRequestResponseHandler(mContext, mDataManager, request.getId()));
		    			mSendingResourceRequests = true;
		        	}
				} catch(UnsupportedEncodingException e) {
					
				}
			}
		}
	}
	
	public static void postWeatherReports() {
		if(!mSendingWeatherReports) {
			ArrayList<WeatherReportPayload> weatherReports = mDataManager.getAllWeatherReportStoreAndForwardReadyToSend();
			
			for (WeatherReportPayload report : weatherReports) {
				try {
		        	if(!report.isDraft()) {
		        		StringEntity entity = new StringEntity(report.toJsonString());
		    			String test = report.toJsonString();
		    			mAuthManager.getClient().post("reports/"  + mDataManager.getActiveIncidentId()  + "/WR", entity, new WeatherReportResponseHandler(mContext, mDataManager, report.getId()));
		    			mSendingWeatherReports = true;
		        	}
				} catch(UnsupportedEncodingException e) {
					
				}
			}
		}
	}
	
	public static void postChatMessages() {
		if(!mSendingChatMessages) {
			ArrayList<ChatPayload> chatMessages = mDataManager.getAllChatStoreAndForward();
			
			for (ChatPayload payload : chatMessages) {
				
//				payload.setUserorgid(mDataManager.getUserOrgId());
//				payload.setNickname(mDataManager.getUsername());
//				payload.setUserOrgName(null);
				
				try {
					StringEntity entity = new StringEntity(payload.toJsonString());
	    			String test =  payload.toJsonString();
//	    			mAuthManager.getClient().post("chatmsgs/" + mDataManager.getWorkspaceId() + "/" + message.getIncidentId() + "/" + message.getcollabroomid(), entity, new ChatResponseHandler(mDataManager, chatMessages));
	    			mAuthManager.getClient().post("chatmsgs/" + payload.getcollabroomid(), entity, new ChatResponseHandler(mDataManager, chatMessages));
	    			
	    			mSendingChatMessages = true;
	        	} catch(UnsupportedEncodingException e) {
					
				}
			}
		}
	}
	
	public static void postMDTs() {
		
		ArrayList<MobileDeviceTrackingPayload> mdtMessages = mDataManager.getAllMDTStoreAndForward();
		MobileDeviceTrackingPayload message = mdtMessages.get(mdtMessages.size()-1);
		
//		for (MobileDeviceTrackingPayload message : mdtMessages) {
			try {
				if(message.getDeviceId() != null && !message.getDeviceId().isEmpty()) {
					StringEntity entity = new StringEntity(message.toJsonString());
					
					mAuthManager.getClient().post("mdtracks", entity, new MDTResponseHandler(mDataManager, mdtMessages));
				} else {
					// invalid mdt due to lack of device id, so delete it
					mDataManager.deleteMDTStoreAndForward(message.getId());
				}
        	} catch(UnsupportedEncodingException e) {
				
			}
//		}
	}
	
	public static void postMarkupFeatures() {
		if(mDataManager.isOnline() && mDataManager.isLoggedIn()) {
			if(!mSendingMarkupFeatures) {
				ArrayList<MarkupFeature> features = mDataManager.getAllMarkupFeaturesStoreAndForwardReadyToSend();
				
				if(features.size() > 0) {					
			    	try {
						StringEntity entity = new StringEntity(features.get(0).toJsonStringWithWebLonLat());
						String testDebug = features.get(0).toJsonStringWithWebLonLat();
						
						mAuthManager.getClient().post("features/collabroom/" + features.get(0).getCollabRoomId() + "?geoType=4326", entity, new MarkupResponseHandler(mContext, mDataManager, features));
						mSendingMarkupFeatures = true;
					} catch(UnsupportedEncodingException e) {
						
					}
				}
			}
		}
	}
	
	public static void getGoogleMapsLegalInfo(AsyncHttpResponseHandler responseHandler){
		AsyncHttpClient mClient = new AsyncHttpClient();
		mClient.setTimeout(60 * 1000);
		mClient.setURLEncodingEnabled(false);
		mClient.setMaxRetriesAndTimeout(2, 1000);
		mClient.get("http://www.google.com/mobile/legalnotices/", responseHandler);
	}
	
	public static String getDeviceId() {
		return mDeviceId;
	}

	public static void setSendingSimpleReports(boolean isSending) {
		mSendingSimpleReports = isSending;
		if(mDataManager.isOnline()) {
			postSimpleReports();
		}
	}
	
	public static void setSendingFieldReports(boolean isSending) {
		mSendingFieldReports = isSending;
	}

	public static void setSendingDamageReports(boolean isSending) {
		mSendingDamageReports = isSending;
		if(mDataManager.isOnline()) {
			postDamageReports();
		}
	}
	
	public static void setSendingResourceRequests(boolean isSending) {
		mSendingResourceRequests = isSending;
	}
	
	public static void setSendingWeatherReports(boolean isSending) {
		mSendingWeatherReports = isSending;
	}
	
	public static void setSendingChatMessages(boolean isSending) {
		mSendingChatMessages = isSending;
	}
	
	public static void setSendingMarkupFeatures(boolean isSending) {
		mSendingMarkupFeatures = isSending;
	}

	public static void setDataManager(DataManager mInstance) {
		mDataManager = mInstance;
	}
}
