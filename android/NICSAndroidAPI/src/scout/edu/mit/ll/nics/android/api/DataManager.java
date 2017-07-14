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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.util.LongSparseArray;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.Gson;

import scout.edu.mit.ll.nics.android.api.data.MarkupFeature;
import scout.edu.mit.ll.nics.android.api.data.OrgCapabilities;
import scout.edu.mit.ll.nics.android.api.data.PropertyDamageType;
import scout.edu.mit.ll.nics.android.api.data.PropertyType;
import scout.edu.mit.ll.nics.android.api.data.ResReqPriorityTypes;
import scout.edu.mit.ll.nics.android.api.data.ResReqType;
import scout.edu.mit.ll.nics.android.api.data.SimpleReportCategoryType;
import scout.edu.mit.ll.nics.android.api.data.UserHealth;
import scout.edu.mit.ll.nics.android.api.data.WeatherSourceTypes;
import scout.edu.mit.ll.nics.android.api.data.WeatherWindTypes;
import scout.edu.mit.ll.nics.android.api.database.DatabaseManager;
import scout.edu.mit.ll.nics.android.api.payload.AssignmentPayload;
import scout.edu.mit.ll.nics.android.api.payload.ChatPayload;
import scout.edu.mit.ll.nics.android.api.payload.CollabroomPayload;
import scout.edu.mit.ll.nics.android.api.payload.IncidentPayload;
import scout.edu.mit.ll.nics.android.api.payload.LoginPayload;
import scout.edu.mit.ll.nics.android.api.payload.MobileDeviceTrackingPayload;
import scout.edu.mit.ll.nics.android.api.payload.OrganizationPayload;
import scout.edu.mit.ll.nics.android.api.payload.TrackingLayerPayload;
import scout.edu.mit.ll.nics.android.api.payload.UserPayload;
import scout.edu.mit.ll.nics.android.api.payload.WeatherPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.DamageReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.FieldReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.WeatherReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.ResourceRequestPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;
import scout.edu.mit.ll.nics.android.utils.Constants;
import scout.edu.mit.ll.nics.android.utils.EncryptedPreferences;
import scout.edu.mit.ll.nics.android.utils.Intents;
import scout.edu.mit.ll.nics.android.utils.LocationHandler;
import scout.edu.mit.ll.nics.android.utils.NotificationsHandler;

public class DataManager {

	public String PACKAGE_NAME;
	
	private AlarmManager mAlarmManager;
	private static DataManager mInstance;
	private DatabaseManager mDatabaseManager;
	private LocationHandler mLocationHandler;
	private Locale mLocale;
	
	private static Context mContext;
	private static Activity mActiveActivity;
	
	private EncryptedPreferences mSharedPreferences;
	
	private boolean isLoggedIn;
	private boolean isOnline;
	
	private PendingIntent mPendingAssignmentRequestIntent;
	private PendingIntent mPendingResourceRequestIntent;
	private PendingIntent mPendingSimpleReportIntent;
	private PendingIntent mPendingFieldReportIntent;
	private PendingIntent mPendingDamageReportIntent;
	private PendingIntent mPendingWeatherReportIntent;
	private PendingIntent mPendingChatMessagesRequestIntent;
	private PendingIntent mPendingMarkupRequestIntent;
	
	private AssignmentPayload mCurrentAssignment;
	private OrgCapabilities mCurrentOrgCapabilities;
	private OrganizationPayload mCurrentOrganization;
	
	private LongSparseArray<CollabroomPayload> mCollabRoomList;
	private SharedPreferences mGlobalPreferences;
	
	private ConnectivityManager mConnectivityManager;
	
	private ArrayList<MarkupFeature> mMarkupFeatures;
	private HashMap<String, IncidentPayload> mIncidents;
	private HashMap<String, OrganizationPayload> mOrganizations;
	
	private boolean LowDataMode = false;
	private int LowDataRate = 120;
	private String currentNavigationView = "Login";
	
	private HashMap<String, String> translationReverseLookup = new HashMap<String, String>();
	private String[] supportedLanguages;
	
	private Boolean isTabletLayout = null;
	
	private boolean newGeneralMessageAvailable = false;
	private boolean newReportAvailable = false;
	private boolean newchatAvailable = false;
	private boolean newMapAvailable = false;
	
	private ArrayList<TrackingLayerPayload> TrackingLayers;
	
	private DataManager() {
		mAlarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
		
		mCurrentAssignment = new AssignmentPayload();
		mCurrentOrgCapabilities = new OrgCapabilities();
		mDatabaseManager = new DatabaseManager(mContext);
		mGlobalPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		mLocale = mContext.getResources().getConfiguration().locale;
		
		mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		mContext.registerReceiver(connectivityReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
//		mContext.registerReceiver(receiver, new IntentFilter(Intents.nics_POLLING_TASK_ASSIGNMENTS));
		mContext.registerReceiver(receiver, new IntentFilter(Intents.nics_POLLING_TASK_DAMAGE_REPORT));
		mContext.registerReceiver(receiver, new IntentFilter(Intents.nics_POLLING_TASK_FIELD_REPORT));
		mContext.registerReceiver(receiver, new IntentFilter(Intents.nics_POLLING_TASK_WEATHER_REPORT));
		mContext.registerReceiver(receiver, new IntentFilter(Intents.nics_POLLING_TASK_RESOURCE_REQUEST));
		mContext.registerReceiver(receiver, new IntentFilter(Intents.nics_POLLING_TASK_SIMPLE_REPORT));
		mContext.registerReceiver(receiver, new IntentFilter(Intents.nics_POLLING_MARKUP_REQUEST));
		mContext.registerReceiver(receiver, new IntentFilter(Intents.nics_POLLING_TASK_CHAT_MESSAGES));
		
		mSharedPreferences = new EncryptedPreferences( mContext.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE));
		
		if(getLocale().getLanguage() != getSelectedLanguage()){
			setCurrentLocale(getSelectedLanguage());
		}
	}
	
	public Locale getLocale() {
		return mLocale;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	public static DataManager getInstance() {
		if(mInstance != null) {
			return mInstance;
		}
		return null;
	}
	public static DataManager getInstance(Context context, Activity currentActivity) {
		mActiveActivity = currentActivity;
		return getInstance(context);
	}
	
	public static DataManager getInstance(Context context) {
		if(mInstance == null) {
			mContext = context;
			mInstance = new DataManager();
		}
		
		RestClient.setDataManager(mInstance);
		
		return mInstance;
	}

	public void requestLogin(String username, String password, boolean getActiveAssignment) {
		RestClient.login(mContext, username, password, false);
	}

	public void requestLogout() {
		RestClient.logout(getUsername(), false, false);
		if(mLocationHandler != null) {
			mLocationHandler.deactivate();
			mLocationHandler = null;
		}

		NotificationsHandler.getInstance(mContext).cancelAllNotifications();

		mSharedPreferences.savePreferenceLong(Constants.INCIDENT_ID, (long)-1);	
		mSharedPreferences.savePreferenceString(Constants.INCIDENT_NAME, getContext().getResources().getString(R.string.no_selection));
		mSharedPreferences.removePreference(Constants.SELECTED_COLLABROOM);

		setAuthToken(null);
		
		stopPollingAlarms();
	}
	
	public void requestActiveAssignment() {
		RestClient.getActiveAssignment(getUsername(), getUserId());
	}
	
	public void requestSimpleReports() {
		RestClient.getSimpleReports(-1, -1, getActiveIncidentId());
	}
	
	public void requestFieldReports() {
		RestClient.getFieldReports(-1, -1, getActiveIncidentId());
	}
	
	public void requestDamageReports() {
		RestClient.getDamageReports(-1, -1, getActiveIncidentId());
	}

	public void requestResourceRequests() {
		RestClient.getResourceRequests(-1, -1, getActiveIncidentId());
	}
	
	public void requestWeatherReports() {
		RestClient.getWeatherReports(-1, -1, getActiveIncidentId());
	}
		
	public void requestChatHistory(long incidentId, long collabRoomId) {
		RestClient.getChatHistory(incidentId, collabRoomId);
	}
	
	public void requestWeatherUpdate(double latitude, double longitude) {
		RestClient.getWeatherUpdate(latitude, longitude);
	}
	
	public void requestMarkupUpdate() {
		RestClient.getMarkupHistory(getSelectedCollabRoom().getCollabRoomId());
	}
	
	public void requestCollabrooms(long incidentId, String incidentName){
		RestClient.getCollabRooms(incidentId, incidentName);
	}
	
	public void requestWfsLayers(){
		RestClient.getWFSLayers();
	}
	
	public void setCollabRoomsForIncident(String incidentName, ArrayList<CollabroomPayload> rooms){
		getIncidents().get(incidentName).setCollabrooms(rooms);
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}
	
	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	
	public void requestOrgCapabilitiesUpdate(){
//		RestClient.getOrgCapabilities(getUserOrgId());
		RestClient.getHardCodedOrgCapabilities();		//good for debugging so you can manually toggle or enable everything
	}
	
	public void setOrgCapabilites(OrgCapabilities orgCap){
		mCurrentOrgCapabilities = orgCap;
	}
	
	public OrgCapabilities getOrgCapabilities(){
		return mCurrentOrgCapabilities;
	}

	public void deleteAllReportsFromLocalStorage(){
		mDatabaseManager.deleteAllDamageReportStoreAndForward();
		mDatabaseManager.deleteAllDamageReportHistory();
		mDatabaseManager.deleteAllFieldReportStoreAndForward();
		mDatabaseManager.deleteAllFieldReportHistory();
		mDatabaseManager.deleteAllResourceRequestHistory();
		mDatabaseManager.deleteAllResourceRequestStoreAndForward();
		mDatabaseManager.deleteAllWeatherReportsHistory();
		mDatabaseManager.deleteAllWeatherReportsStoreAndForward();
		mDatabaseManager.deleteAllSimpleReportsHistory();
		mDatabaseManager.deleteAllSimpleReportsStoreAndForward();
	}
	
	// Simple Report Functions
	
	public ArrayList<SimpleReportPayload> getSimpleReportHistoryForIncident(long incidentId) {
		return mDatabaseManager.getSimpleReportHistoryForIncident(incidentId);
	}
	
	public ArrayList<SimpleReportPayload> getAllSimpleReportHistory() {
		return mDatabaseManager.getAllSimpleReportHistory();
	}
	
	public ArrayList<SimpleReportPayload> getAllSimpleReportStoreAndForwardReadyToSend() {
		return mDatabaseManager.getAllSimpleReportStoreAndForwardReadyToSend();
	}

	public ArrayList<SimpleReportPayload> getAllSimpleReportStoreAndForwardReadyToSend(long incidentId) {
		return mDatabaseManager.getAllSimpleReportStoreAndForwardReadyToSend(incidentId);
	}
	
	public ArrayList<SimpleReportPayload> getAllSimpleReportStoreAndForwardHasSent() {
		return mDatabaseManager.getAllSimpleReportStoreAndForwardHasSent();
	}

	public ArrayList<SimpleReportPayload> getAllSimpleReportStoreAndForwardHasSent(long incidentId) {
		return mDatabaseManager.getAllSimpleReportStoreAndForwardHasSent(incidentId);
	}

	public void addSimpleReportToHistory(SimpleReportPayload payload) {
		mDatabaseManager.addSimpleReportHistory(payload);
	}

	public boolean deleteSimpleReportFromHistory(long mReportId) {
		return mDatabaseManager.deleteSimpleReportHistory(mReportId);
	}
	
	public boolean deleteSimpleReportFromHistoryByIncident(long incidentId) {
		return mDatabaseManager.deleteSimpleReportHistoryByIncident(incidentId);
	}
	
	public boolean deleteSimpleReportStoreAndForward(long mReportId) {
		return mDatabaseManager.deleteSimpleReportStoreAndForward(mReportId);
	}

	public boolean addSimpleReportToStoreAndForward(SimpleReportPayload payload) {
		return mDatabaseManager.addSimpleReportToStoreAndForward(payload);
	}

	public long getLastSimpleReportTimestamp() {
		return mDatabaseManager.getLastSimpleReportTimestamp(getActiveIncidentId());
	}
	
	public SimpleReportPayload getLastSimpleReportPayload() {
		return mDatabaseManager.getLastSimpleReportPayload(getActiveIncidentId());
	}
	
	// Field Report Functions
	public ArrayList<FieldReportPayload> getFieldReportHistoryForIncident(long incidentId) {
		return mDatabaseManager.getFieldReportHistoryForIncident(incidentId);
	}
	
	public ArrayList<FieldReportPayload> getAllFieldReportHistory() {
		return mDatabaseManager.getAllFieldReportHistory();
	}

	public ArrayList<FieldReportPayload> getAllFieldReportStoreAndForwardReadyToSend() {
		return mDatabaseManager.getAllFieldReportStoreAndForwardReadyToSend();
	}
	
	public ArrayList<FieldReportPayload> getAllFieldReportStoreAndForwardReadyToSend(long incidentId) {
		return mDatabaseManager.getAllFieldReportStoreAndForwardReadyToSend(incidentId);
	}
	
	public ArrayList<FieldReportPayload> getAllFieldReportStoreAndForwardHasSent() {
		return mDatabaseManager.getAllFieldReportStoreAndForwardHasSent();
	}
	
	public ArrayList<FieldReportPayload> getAllFieldReportStoreAndForwardHasSent(long incidentId) {
		return mDatabaseManager.getAllFieldReportStoreAndForwardHasSent(incidentId);
	}

	public boolean addFieldReportToHistory(FieldReportPayload payload) {
		return mDatabaseManager.addFieldReportHistory(payload);
	}
	
	public long getLastFieldReportTimestamp() {
		return mDatabaseManager.getLastFieldReportTimestamp(getActiveIncidentId());
	}
	
	public FieldReportPayload getLastFieldReportPayload() {
		return mDatabaseManager.getLastFieldReportPayload(getActiveIncidentId());
	}
	
	public boolean deleteFieldReportFromHistoryByIncident(long incidentId) {
		return mDatabaseManager.deleteFieldReportHistoryByIncident(incidentId);
	}
	
	public boolean deleteFieldReportFromHistory(long mReportId) {
		return mDatabaseManager.deleteFieldReportHistory(mReportId);
	}
	
	public boolean deleteFieldReportStoreAndForward(long mReportId) {
		return mDatabaseManager.deleteFieldReportStoreAndForward(mReportId);
	}

	public boolean addFieldReportToStoreAndForward(FieldReportPayload payload) {
		return mDatabaseManager.addFieldReportToStoreAndForward(payload);
	}
	
	// Damage Report Functions
	public ArrayList<DamageReportPayload> getDamageReportHistoryForIncident(long incidentId) {
		return mDatabaseManager.getDamageReportHistoryForIncident(incidentId);
	}
	
	public ArrayList<DamageReportPayload> getAllDamageReportHistory() {
		return mDatabaseManager.getAllDamageReportHistory();
	}

	public ArrayList<DamageReportPayload> getAllDamageReportStoreAndForwardReadyToSend() {
		return mDatabaseManager.getAllDamageReportStoreAndForwardReadyToSend();
	}

	public ArrayList<DamageReportPayload> getAllDamageReportStoreAndForwardReadyToSend(long incidentId) {
		return mDatabaseManager.getAllDamageReportStoreAndForwardReadyToSend(incidentId);
	}
	
	public ArrayList<DamageReportPayload> getAllDamageReportStoreAndForwardHasSent() {
		return mDatabaseManager.getAllDamageReportStoreAndForwardHasSent();
	}

	public ArrayList<DamageReportPayload> getAllDamageReportStoreAndForwardHasSent(long incidentId) {
		return mDatabaseManager.getAllDamageReportStoreAndForwardHasSent(incidentId);
	}
	
	public boolean addDamageReportToHistory(DamageReportPayload payload) {
		return mDatabaseManager.addDamageReportHistory(payload);
	}
	
	public long getLastDamageReportTimestamp() {
		return mDatabaseManager.getLastDamageReportTimestamp(getActiveIncidentId());
	}
	
	public DamageReportPayload getLastDamageReportPayload() {
		return mDatabaseManager.getLastDamageReportPayload(getActiveIncidentId());
	}
	
	public boolean deleteDamageReportFromHistoryByIncident(long incidentId) {
		return mDatabaseManager.deleteDamageReportHistoryByIncident(incidentId);
	}
	
	public boolean deleteDamageReportFromHistory(long mReportId) {
		return mDatabaseManager.deleteDamageReportHistory(mReportId);
	}
	
	public boolean deleteDamageReportStoreAndForward(long mReportId) {
		return mDatabaseManager.deleteDamageReportStoreAndForward(mReportId);
	}

	public boolean addDamageReportToStoreAndForward(DamageReportPayload payload) {
		return mDatabaseManager.addDamageReportToStoreAndForward(payload);
	}
	
	// Weather Report Functions
	public ArrayList<WeatherReportPayload> getWeatherReportHistoryForIncident(long incidentId) {
		return mDatabaseManager.getWeatherReportHistoryForIncident(incidentId);
	}
	
	public ArrayList<WeatherReportPayload> getAllWeatherReportHistory() {
		return mDatabaseManager.getAllWeatherReportHistory();
	}

	public ArrayList<WeatherReportPayload> getAllWeatherReportStoreAndForwardReadyToSend() {
		return mDatabaseManager.getAllWeatherReportStoreAndForwardReadyToSend();
	}
	
	public ArrayList<WeatherReportPayload> getAllWeatherReportStoreAndForwardReadyToSend(long incidentId) {
		return mDatabaseManager.getAllWeatherReportStoreAndForwardReadyToSend(incidentId);
	}
	
	public ArrayList<WeatherReportPayload> getAllWeatherReportStoreAndForwardHasSent() {
		return mDatabaseManager.getAllWeatherReportStoreAndForwardHasSent();
	}
	
	public ArrayList<WeatherReportPayload> getAllWeatherReportStoreAndForwardHasSent(long incidentId) {
		return mDatabaseManager.getAllWeatherReportStoreAndForwardHasSent(incidentId);
	}

	public boolean addWeatherReportToHistory(WeatherReportPayload payload) {
		return mDatabaseManager.addWeatherReportHistory(payload);
	}
	
	public long getLastWeatherReportTimestamp() {
		return mDatabaseManager.getLastWeatherReportTimestamp(getActiveIncidentId());
	}
	
	public WeatherReportPayload getLastWeatherReportPayload() {
		return mDatabaseManager.getLastWeatherReportPayload(getActiveIncidentId());
	}
	
	public boolean deleteWeatherReportFromHistoryByIncident(long incidentId) {
		return mDatabaseManager.deleteWeatherReportHistoryByIncident(incidentId);
	}
	
	public boolean deleteWeatherReportFromHistory(long mReportId) {
		return mDatabaseManager.deleteWeatherReportHistory(mReportId);
	}
	
	public boolean deleteWeatherReportStoreAndForward(long mReportId) {
		return mDatabaseManager.deleteWeatherReportStoreAndForward(mReportId);
	}

	public boolean addWeatherReportToStoreAndForward(WeatherReportPayload payload) {
		return mDatabaseManager.addWeatherReportToStoreAndForward(payload);
	}
	
	public Header[] getAuthData() {
		return RestClient.getAuthData();
	}
	
	public boolean addChatToStoreAndForward(ChatPayload payload) {
		return mDatabaseManager.addChatToStoreAndForward(payload);
	}
	
	public long getLastChatTimestamp(long incidentId, long collabRoomId) {
		long timestamp = -1;
		if(mDatabaseManager != null && mCollabRoomList != null && mCollabRoomList.size() > 0) {
			CollabroomPayload payload = mCollabRoomList.get(collabRoomId);
			if(payload != null) {
				timestamp = mDatabaseManager.getLastChatHistoryTimestamp(payload.getCollabRoomId());
			}
		} else {
			timestamp = -99;
		}
		
		return timestamp;
	}
	
	public ChatPayload getLastChatMessage(long collabroomId) {
		return mDatabaseManager.getLastChatHistory(collabroomId);
	}
	
	public ChatPayload addChatMsgToStoreAndForward(String msg, String selectedCollabroomName) {
    	ChatPayload data = null;
    	
    	long currentTime = System.currentTimeMillis ();
        String incidentName = getActiveIncidentName();

        if(selectedCollabroomName != null && incidentName != null) {
	        
	        data = new ChatPayload();
	        data.setcreated(currentTime);
	        data.setlastupdated(currentTime);
	        data.setIncidentId(getActiveIncidentId());
	        data.setchatid(currentTime);
	        data.setmessage(msg);
	        data.setuserId(getUserId());
	        data.setcollabroomid(getSelectedCollabRoom().getCollabRoomId());
//	        data.setSeqTime(currentTime);
	        data.setseqnum(currentTime);
	        data.setchatid(-1);
//	        data.setTopic(topic);
	        data.setNickname(getUserNickname());
	        data.setUserOrgName(getCurrentOrganziation().getName());
	        data.setUserorgid((long)getCurrentOrganziation().getUserorgs()[0].getUserorgid());
        }
	    
        if(data != null) {
        	mDatabaseManager.addChatToStoreAndForward(data);
        }
        
    	return data;
	}

	public ChatPayload addPersonalHistory(String msg) {
    	ChatPayload data = null;
    	
    	long currentTime = System.currentTimeMillis ();
        data = new ChatPayload();
        data.setcreated(currentTime);
        data.setlastupdated(currentTime);
        data.setchatid(currentTime);
        data.setmessage(msg);
        data.setuserId(getUserId());
        data.setcollabroomid(-1);
//        data.setSeqTime(currentTime);
        data.setseqnum(currentTime);
        data.setTopic("debugLog");
        data.setNickname(getUserNickname());
	    
        if(data != null) {
        	mDatabaseManager.addPersonalHistory(data);
        }
        
        Intent intent = new Intent();
        intent.setAction(Intents.nics_NEW_PERSONAL_HISTORY_RECEIVED);
        intent.putExtra("payload", data.toJsonString());
        mContext.sendBroadcast (intent);
        
    	return data;
	}

	public ArrayList<ChatPayload> getRecentChatHistory() {
		return mDatabaseManager.getRecentChatHistory(getSelectedCollabRoom().getCollabRoomId());
	}
	
	public ArrayList<ChatPayload> getNewChatMessages(long timestamp) {
		return mDatabaseManager.getNewChatMessagesFromDate(getSelectedCollabRoom().getCollabRoomId(), timestamp);
	}
	
	public ArrayList<ChatPayload> getRecentChatHistoryStartingFromAndGoingBack(long timestamp, String limit) {
		return mDatabaseManager.getRecentChatHistoryStartingFromAndGoingBack(getSelectedCollabRoom().getCollabRoomId(), timestamp, limit);
	}
	
	public ArrayList<ChatPayload> getChatStoreAndForwardReadyToSend() {
		return mDatabaseManager.getChatStoreAndForwardReadyToSend(getSelectedCollabRoom().getCollabRoomId());
	}

	public ArrayList<ChatPayload> getRecentPersonalHistory() {
		return mDatabaseManager.getRecentPersonalHistory();
	}

	public boolean addChatHistory(ChatPayload payload) {
		return mDatabaseManager.addChatHistory(payload);
	}

	public boolean addResourceRequestToStoreAndForward(ResourceRequestPayload data) {
		return mDatabaseManager.addResourceRequestToStoreAndForward(data);
	}
	
	public boolean deleteResourceRequestFromHistoryByIncident(long incidentId) {
		return mDatabaseManager.deleteResourceRequestHistoryByIncident(incidentId);
	}

	public boolean deleteResourceRequestFromHistory(long mReportId) {
		return mDatabaseManager.deleteResourceRequestHistory(mReportId);
	}
	
	public boolean deleteResourceRequestStoreAndForward(long mReportId) {
		return mDatabaseManager.deleteResourceRequestStoreAndForward(mReportId);
	}
	
	public boolean addResourceRequestToHistory(ResourceRequestPayload data) {
		return mDatabaseManager.addResourceRequestHistory(data);
	}

	public ArrayList<ResourceRequestPayload> getResourceRequestHistoryForIncident(long incidentId) {
		return mDatabaseManager.getResourceRequestHistoryForIncident(incidentId);
	}
	
	public ArrayList<ResourceRequestPayload> getAllResourceRequestHistory() {
		return mDatabaseManager.getAllResourceRequestHistory();
	}

	public ArrayList<ResourceRequestPayload> getAllResourceRequestStoreAndForwardReadyToSend() {
		return mDatabaseManager.getAllResourceRequestStoreAndForwardReadyToSend();
	}
	
	public ArrayList<ResourceRequestPayload> getAllResourceRequestStoreAndForwardReadyToSend(long incidentId) {
		return mDatabaseManager.getAllResourceRequestStoreAndForwardReadyToSend(incidentId);
	}
	
	public ArrayList<ResourceRequestPayload> getAllResourceRequestStoreAndForwardHasSent() {
		return mDatabaseManager.getAllResourceRequestStoreAndForwardHasSent();
	}
	
	public ArrayList<ResourceRequestPayload> getAllResourceRequestStoreAndForwardHasSent(long incidentId) {
		return mDatabaseManager.getAllResourceRequestStoreAndForwardHasSent(incidentId);
	}

	public long getLastResourceRequestTimestamp() {
		return mDatabaseManager.getLastResourceRequestTimestamp(getActiveIncidentId());
	}

	public ResourceRequestPayload getLastResourceRequestPayload() {
		return mDatabaseManager.getLastResourceRequestPayload(getActiveIncidentId());
	}
	
    public String getIncidentInfoJson() {
		JSONObject incidentInfo = new JSONObject();
		
		try {
			incidentInfo.put("incident_id", this.getActiveIncidentId());
			incidentInfo.put("incident_name", this.getActiveIncidentName());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return incidentInfo.toString();
    }
    
    public void requestActiveAssignmentRepeating(int seconds) {
    	Intent intent = new Intent(Intents.nics_POLLING_TASK_ASSIGNMENTS);
    	intent.putExtra("type", "assignment");
    	
    	mPendingAssignmentRequestIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    	long secondsFromNow =  SystemClock.elapsedRealtime() + (seconds * 1000);
    	mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, secondsFromNow, (seconds * 1000), mPendingAssignmentRequestIntent);
    	
    	Log.i("nicsDataManager", "Set assignment repeating fetch interval:" + seconds + " seconds.");
    }
    
    public void requestFieldReportRepeating(int seconds, boolean immediately) {
    	Intent intent = new Intent(Intents.nics_POLLING_TASK_FIELD_REPORT);
    	intent.putExtra("type", "fieldreport");
    	
    	mPendingFieldReportIntent = PendingIntent.getBroadcast(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    	long secondsFromNow = SystemClock.elapsedRealtime() + 200;
    	
    	if(!immediately) {
    		secondsFromNow += (seconds * 1000);
    	}
    	mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, secondsFromNow, (seconds * 1000), mPendingFieldReportIntent);
    	
    	Log.i("nicsDataManager", "Set field report repeating fetch interval:" + seconds + " seconds.");
    }
    
    public void requestDamageReportRepeating(int seconds, boolean immediately) {
    	Intent intent = new Intent(Intents.nics_POLLING_TASK_DAMAGE_REPORT);
    	intent.putExtra("type", "damagereport");
    	
    	mPendingDamageReportIntent = PendingIntent.getBroadcast(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    	long secondsFromNow = SystemClock.elapsedRealtime() + 200;
    	
    	if(!immediately) {
    		secondsFromNow += (seconds * 1000);
    	}
    	mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, secondsFromNow, (seconds * 1000), mPendingDamageReportIntent);
    	
    	Log.i("nicsDataManager", "Set damage report repeating fetch interval:" + seconds + " seconds.");
    }
    
    public void requestSimpleReportRepeating(int seconds, boolean immediately) {
    	Intent intent = new Intent(Intents.nics_POLLING_TASK_SIMPLE_REPORT);
    	intent.putExtra("type", "simplereport");
    	
    	mPendingSimpleReportIntent = PendingIntent.getBroadcast(mContext, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    	long secondsFromNow = SystemClock.elapsedRealtime() + 200;
    	
    	if(!immediately) {
    		secondsFromNow += (seconds * 1000);
    	}
    	mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, secondsFromNow, (seconds * 1000), mPendingSimpleReportIntent);
    	
    	Log.i("nicsDataManager", "Set simple report repeating fetch interval:" + seconds + " seconds.");
    }
    
    public void requestResourceRequestRepeating(int seconds, boolean immediately) {
    	Intent intent = new Intent(Intents.nics_POLLING_TASK_RESOURCE_REQUEST);
    	intent.putExtra("type", "resourcerequest");
    	
    	mPendingResourceRequestIntent = PendingIntent.getBroadcast(mContext, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    	long secondsFromNow = SystemClock.elapsedRealtime() + 200;
    	
    	if(!immediately) {
    		secondsFromNow += (seconds * 1000);
    	}
    	mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, secondsFromNow, (seconds * 1000), mPendingResourceRequestIntent);
    	
    	Log.i("nicsDataManager", "Set resource request repeating fetch interval:" + seconds + " seconds.");
    }
    
    public void requestWeatherReportRepeating(int seconds, boolean immediately) {
    	Intent intent = new Intent(Intents.nics_POLLING_TASK_WEATHER_REPORT);
    	intent.putExtra("type", "weatherreport");
    	
    	mPendingWeatherReportIntent = PendingIntent.getBroadcast(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    	long secondsFromNow = SystemClock.elapsedRealtime() + 200;
    	
    	if(!immediately) {
    		secondsFromNow += (seconds * 1000);
    	}
    	mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, secondsFromNow, (seconds * 1000), mPendingWeatherReportIntent);
    	
    	Log.i("nicsDataManager", "Set weather report repeating fetch interval:" + seconds + " seconds.");
    }
            
    public void requestMarkupRepeating(int seconds, boolean immediately) {
    	Intent intent = new Intent(Intents.nics_POLLING_MARKUP_REQUEST);
    	intent.putExtra("type", "markup");
    	
    	mPendingMarkupRequestIntent = PendingIntent.getBroadcast(mContext, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    	long secondsFromNow = SystemClock.elapsedRealtime() + 200;
    	
    	if(!immediately) {
    		secondsFromNow += (seconds * 1000);
    	}
    	mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, secondsFromNow, (seconds * 1000), mPendingMarkupRequestIntent);
    	
    	Log.i("nicsDataManager", "Set map markup repeating fetch interval:" + seconds + " seconds.");
    }
    
    public void requestChatMessagesRepeating(int seconds, boolean immediately) {
    	Intent intent = new Intent(Intents.nics_POLLING_TASK_CHAT_MESSAGES);
    	intent.putExtra("type", "chatmessages");
    	
    	mPendingChatMessagesRequestIntent = PendingIntent.getBroadcast(mContext, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	long secondsFromNow = SystemClock.elapsedRealtime() + 200;
    	
    	if(!immediately) {
    		secondsFromNow += (seconds * 1000);
    	}
    	mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, secondsFromNow, (seconds * 1000), mPendingChatMessagesRequestIntent);
    	
    	Log.i("nicsDataManager", "Set chat message request repeating fetch interval:" + seconds + " seconds.");
    }
    
    public void stopPollingAlarms() {
    	mAlarmManager.cancel(mPendingAssignmentRequestIntent);
    	mAlarmManager.cancel(mPendingSimpleReportIntent);
    	mAlarmManager.cancel(mPendingDamageReportIntent);
    	mAlarmManager.cancel(mPendingFieldReportIntent);
    	mAlarmManager.cancel(mPendingResourceRequestIntent);
    	mAlarmManager.cancel(mPendingWeatherReportIntent);
    	mAlarmManager.cancel(mPendingChatMessagesRequestIntent);
    	mAlarmManager.cancel(mPendingMarkupRequestIntent);
    }
    
    public void stopPollingMarkup() {
    	mAlarmManager.cancel(mPendingMarkupRequestIntent);
    	RestClient.clearParseMarkupFeaturesTask();
    }
    
    public void stopPollingChat() {
    	mAlarmManager.cancel(mPendingChatMessagesRequestIntent);
    	RestClient.clearParseChatMessagesTask();
    }
    
    public void stopPollingAssignment() {
    	mAlarmManager.cancel(mPendingAssignmentRequestIntent);
    }
    
    BroadcastReceiver receiver = new BroadcastReceiver() {
		@SuppressLint("Wakelock")
		@Override
		public void onReceive(Context context, Intent intent) {
					
			if(CheckWifiStatus() && isLoggedIn()){
				try {
					PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
					PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "nics_WAKE");
		
					//Acquire the lock
					wakeLock.acquire();
					
					Bundle extras = intent.getExtras();
					if(extras != null) {
						String type = extras.getString("type");
		
						if(type != null) {
							Log.i("nicsDataManager", "Requesting Data Update: " + type);
							if(type.equals("assignment")) {
								requestActiveAssignment();
							
								// Send any pending send items when checking for assignment ~ every minute
								sendSimpleReports();
								sendFieldReports();
								sendResourceRequests();
								sendDamageReports();
								sendWeatherReports();
								
							} else if(type.equals("fieldreport")) {
								requestFieldReports();
							} else if(type.equals("weatherreport")) {
								requestWeatherReports();
							} else if(type.equals("damagereport")) {
								requestDamageReports();
							} else if(type.equals("simplereport")) {
								requestSimpleReports();
							} else if(type.equals("resourcerequest")) {
								requestResourceRequests();
							} else if(type.equals("chatmessages")) {
								requestChatHistory(getActiveIncidentId(), getSelectedCollabRoom().getCollabRoomId());
							} else if(type.equals("markup")) {
								requestMarkupUpdate();
							}
						}
					}
					
					//Release the lock
					wakeLock.release();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				Bundle extras = intent.getExtras();
				String type = extras.getString("type");
				Log.i("nicsDataManager", "Cancel Data Request, not in wifi: " + type);
			}
		}
	};
	
	BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
		
			if(info != null) {
				isOnline = info.isConnectedOrConnecting();
				if(isOnline) {
					sendChatMessages();
					sendDamageReports();
					sendSimpleReports();
					sendFieldReports();
					sendWeatherReports();
					sendMarkupFeatures();
					sendResourceRequests();
					
					addPersonalHistory("Device reconnected to " + info.getTypeName() + " network.");
				}
			} else {
				isOnline = false;
				addPersonalHistory("Device disconnected from the data network.");
			}
		}	
	};
	
	public LocationHandler getLocationSource() {
		if(mLocationHandler == null && isMDTEnabled()) {
			mLocationHandler = new LocationHandler(mContext);
		}
		return mLocationHandler;
	}
	
	public void forceLocationUpdate() {
		if(mLocationHandler != null) {
			mLocationHandler.forceUpdate();
		}
	}
	
    public void addMarkupFeatureToStoreAndForward(MarkupFeature feature) {
		mDatabaseManager.addMarkupToStoreAndForward(feature);
    }
    
    public boolean deleteMarkupFeatureStoreAndForward(long featureId) {
		return mDatabaseManager.deleteMarkupStoreAndForward(featureId);
	}
    
	public ArrayList<MarkupFeature> getAllMarkupFeaturesStoreAndForwardReadyToSend() {
		return mDatabaseManager.getAllMarkupStoreAndForward();
	}

	public long getLastMarkupTimestamp(long collabRoomId) {
		return mDatabaseManager.getLastMarkupTimestamp(collabRoomId);
	}

	public boolean addMarkupFeatureToHistory(MarkupFeature feature) {
		return mDatabaseManager.addMarkupFeatureToHistory(feature);
	}
	
	public void addAllMarkupFeaturesToHistory(ArrayList<MarkupFeature> featureSet) {
		mDatabaseManager.addAllMarkupFeatureToHistory(featureSet);
	}
	
	public ArrayList<MarkupFeature> getMarkupHistory() {
		return mDatabaseManager.getAllMarkupFeatureHistory();
	}
	
	public ArrayList<MarkupFeature> getMarkupHistoryForCollabroom(long collabroomId) {
		return mDatabaseManager.getMarkupHistoryForCollabroom(collabroomId);
	}
	
	public ArrayList<MarkupFeature> getMarkupHistoryForCollabroomWithFeatureIds(long collabroomId, ArrayList<String> featureIds) {
		return mDatabaseManager.getMarkupHistoryForCollabroomWithFeatureIds(collabroomId, featureIds);
	}

	public void clearCollabRoomList() {
		if(mCollabRoomList == null) {
			mCollabRoomList = new LongSparseArray<CollabroomPayload>();
		}
		
		mCollabRoomList.clear();
	}
	public void addCollabroom(CollabroomPayload payload) {
		if(mCollabRoomList == null) {
			mCollabRoomList = new LongSparseArray<CollabroomPayload>();
		}
		
		mCollabRoomList.put(payload.getCollabRoomId(), payload);
	}
	
	public LongSparseArray<CollabroomPayload> getCollabRoomList() {
		return mCollabRoomList;
	}
	
	public HashMap<Long, CollabroomPayload> getCollabRoomMapById() {
		HashMap<Long, CollabroomPayload> collabRooms = new HashMap<Long, CollabroomPayload>();
		for(int i = 0; i < mCollabRoomList.size(); i++) {
			CollabroomPayload payload = mCollabRoomList.valueAt(i);
			collabRooms.put(payload.getCollabRoomId(), payload);
		}
		
		return collabRooms;
	}
	
	public void setSelectedCollabRoom(CollabroomPayload newCollabroomPayload) {
		if(newCollabroomPayload != null){
			mSharedPreferences.savePreferenceString(Constants.SELECTED_COLLABROOM, newCollabroomPayload.toJsonString());
		}else{
			newCollabroomPayload = new CollabroomPayload();
			newCollabroomPayload.setCollabRoomId(-1);
			newCollabroomPayload.setName(getContext().getResources().getString(R.string.no_selection));
			mSharedPreferences.savePreferenceString(Constants.SELECTED_COLLABROOM, newCollabroomPayload.toJsonString());
		}
		
		if(newCollabroomPayload.getCollabRoomId() != -1) {
			requestChatMessagesRepeating(getCollabroomDataRate(), true);
		} else {
			stopPollingMarkup();
			stopPollingChat();
		}
	}
	
	public CollabroomPayload getSelectedCollabRoom() {
		
		String collabroomString = mSharedPreferences.getPreferenceString(Constants.SELECTED_COLLABROOM, "");
		if(!collabroomString.equals("")){
			return new Gson().fromJson(collabroomString, CollabroomPayload.class);
		}
		else{
			CollabroomPayload newPayload = new CollabroomPayload();
			newPayload.setCollabRoomId(-1);
			newPayload.setName(getContext().getResources().getString(R.string.no_selection));
			return newPayload;
		}
	}
		
	public boolean CheckWifiStatus(){
		boolean ProceedWithSync = true;
		
		if(isSyncWifiOnlyEnabled()){
			ConnectivityManager connManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (!mWifi.isConnected()) {
				ProceedWithSync = false;
			}
		}
		return ProceedWithSync;
	}
	
	public void sendSimpleReports() {
		if(CheckWifiStatus()){
			RestClient.postSimpleReports();
		}
	}
	
	public void sendFieldReports() {
		if(CheckWifiStatus()){
			RestClient.postFieldReports();
		}
	}
	
	public void sendDamageReports() {
		if(CheckWifiStatus()){
			RestClient.postDamageReports();
		}
	}
	
	public void sendResourceRequests() {
		if(CheckWifiStatus()){
			RestClient.postResourceRequests();
		}
	}
	
	public void sendWeatherReports() {
		if(CheckWifiStatus()){
			RestClient.postWeatherReports();
		}
	}
	
	public void sendMarkupFeatures() {
		if(CheckWifiStatus()){
			RestClient.postMarkupFeatures();
		}
	}
	
	public void sendChatMessages() {
		if(CheckWifiStatus()){
			RestClient.postChatMessages();
		}
	}
	
	public void deleteAllMarkupFeatureStoreAndForward() {
		mDatabaseManager.deleteAllMarkupFeatureStoreAndForward();
	}

	public void deleteAllMarkupFeatureHistory() {
		mDatabaseManager.deleteAllMarkupFeatureHistory();
	}

	public ArrayList<ChatPayload> getAllChatStoreAndForward() {
		return mDatabaseManager.getAllChatStoreAndForward();
	}

	public void deleteChatStoreAndForward(long id) {
		mDatabaseManager.deleteChatStoreAndForward(id);
	}
	
	public void deleteAllChatStoreAndForward() {
		mDatabaseManager.deleteAllChatStoreAndForward();
	}
	
	 public void deleteAllChatHistory () {
		 mDatabaseManager.deleteAllChatHistory();
	 }

	public ArrayList<MobileDeviceTrackingPayload> getAllMDTStoreAndForward() {
		return mDatabaseManager.getAllMobileDeviceTrackingStoreAndForward();
	}

	public void deleteMDTStoreAndForward(long id) {
		mDatabaseManager.deleteMobileDeviceTrackingStoreAndForward(id);
	}

	public void setMarkupFeatures(ArrayList<MarkupFeature> features) {
		if(mMarkupFeatures == null) {
			mMarkupFeatures = new ArrayList<MarkupFeature>();
		}
		mMarkupFeatures.clear();
		mMarkupFeatures.addAll(features);
	}

	public void deleteMarkupHistoryForCollabroom(long collabroomId) {
		mDatabaseManager.deleteMarkupHistoryForCollabroom(collabroomId);
	}

	public void deleteMarkupHistoryForCollabroomByFeatureIds(long collabroomId, ArrayList<String> featuresToRemove) {
		mDatabaseManager.deleteMarkupHistoryForCollabroomByFeatureIds(collabroomId, featuresToRemove);
	}
	
	public void deleteMarkupHistoryForCollabroomByFeatureId(long collabroomId, String featureToRemove) {
		mDatabaseManager.deleteMarkupHistoryForCollabroomByFeatureId(collabroomId, featureToRemove);
	}

	public void addAllChatHistory(ArrayList<ChatPayload> payloads) {
		mDatabaseManager.addAllChatHistory(payloads);
	}

	public HashMap<String, IncidentPayload> getIncidents() {
		return mIncidents;
	}
	public void setIncidents(HashMap<String, IncidentPayload> incidents) {
		mIncidents = incidents;
	}

	public HashMap<String, OrganizationPayload> getOrganizations() {
		return mOrganizations;
	}
	public void setOrganizations(ArrayList<OrganizationPayload> organizations) {
		if(mOrganizations == null) {
			mOrganizations = new HashMap<String, OrganizationPayload>();
		} else {
			mOrganizations.clear();
		}
		
		for(OrganizationPayload payload : organizations) {
			mOrganizations.put(payload.getName(), payload);
		}
	}
	
	public Context getActiveActivity() {
		return mActiveActivity;
	}

	
	public void setCurrentOrganization(OrganizationPayload mCurrentOrganization){
		this.mCurrentOrganization = mCurrentOrganization;
	}
	
	public OrganizationPayload getCurrentOrganziation(){
		return mCurrentOrganization;
	}
	
	public void setCurrentAssignment(AssignmentPayload mCurrentAssignment) {
		this.mCurrentAssignment = mCurrentAssignment;
	}
	
	public AssignmentPayload getCurrentAssignment() {
		return mCurrentAssignment;
	}

	public void setLoginData(LoginPayload loginPayload) {
		
		setUserId( loginPayload.getUserId());
		setUsername(loginPayload.getUsername());
		setWorkspaceId(loginPayload.getWorkspaceId());
		mSharedPreferences.savePreferenceLong(Constants.USER_SESSION_ID, loginPayload.getUserSessionId());
		mSharedPreferences.savePreferenceLong(Constants.USER_ORG_ID, loginPayload.getOrgId());
	}
	
	public void setWorkspaceId(long workspaceId) {
		mSharedPreferences.savePreferenceLong(Constants.WORKSPACE_ID, (long)workspaceId);
	}

	public void setUserData(UserPayload userPayload) {
		mSharedPreferences.savePreferenceString(Constants.USER_DATA, userPayload.toJsonString());
	}
	
	public UserPayload getUserPayload() {
		UserPayload userDataPayload = null;
		
		String userDataString = mSharedPreferences.getPreferenceString(Constants.USER_DATA, null);
		
		if(userDataString != null) {
			userDataPayload = new Gson().fromJson(userDataString, UserPayload.class);
		}
		
		return userDataPayload;
	}
	
	public void setCurrentIncidentData(IncidentPayload incident, long collabRoomId, String collabRoomName) {
		if(incident != null) {
			mSharedPreferences.savePreferenceLong(Constants.INCIDENT_ID, incident.getIncidentId());
			mSharedPreferences.savePreferenceString(Constants.INCIDENT_NAME, incident.getIncidentName());
			mSharedPreferences.savePreferenceString(Constants.INCIDENT_LATITUDE, String.valueOf(incident.getLat()));
			mSharedPreferences.savePreferenceString(Constants.INCIDENT_LONGITUDE, String.valueOf(incident.getLon()));
		} else {
			mSharedPreferences.removePreference(Constants.INCIDENT_ID);
			mSharedPreferences.removePreference(Constants.INCIDENT_NAME);
			mSharedPreferences.removePreference(Constants.INCIDENT_LATITUDE);
			mSharedPreferences.removePreference(Constants.INCIDENT_LONGITUDE);
		}
		
		if(collabRoomId != -1) {
			mSharedPreferences.savePreferenceLong(Constants.COLLABROOM_ID, collabRoomId);
			mSharedPreferences.savePreferenceString(Constants.COLLABROOM_NAME, collabRoomName);
		} else {
			mSharedPreferences.removePreference(Constants.COLLABROOM_ID);
			mSharedPreferences.removePreference(Constants.COLLABROOM_NAME);
		}
	}
	
	public long getUserId() {
		return mSharedPreferences.getPreferenceLong(Constants.USER_ID);
	}
	
	public void setUserId(long userId){
		mSharedPreferences.savePreferenceLong(Constants.USER_ID, userId);
	}

	public long getUserOrgId() {
		return mSharedPreferences.getPreferenceLong(Constants.USER_ORG_ID);
	}
	
	public long getWorkspaceId() {
		return mSharedPreferences.getPreferenceLong(Constants.WORKSPACE_ID);
	}
	
	public long getUserSessionId() {
		return mSharedPreferences.getPreferenceLong(Constants.USER_SESSION_ID);
	}

	public void setUsername(String username) {
		mSharedPreferences.savePreferenceString(Constants.USER_NAME, username);
	}
	
	public String getUsername() {
		return mSharedPreferences.getPreferenceString(Constants.USER_NAME);
	}

	public String getUserDataJsonString() {
		return new Gson().toJson(getUserPayload(), UserPayload.class);
	}
	
	public long getActiveCollabroomId() {
		return mSharedPreferences.getPreferenceLong(Constants.COLLABROOM_ID);
	}
	
	public CollabroomPayload getPreviousCollabroom() {
	
		String collabroomString = mSharedPreferences.getPreferenceString(Constants.PREVIOUS_COLLABROOM, "");
		if(!collabroomString.equals("")){
			return new Gson().fromJson(collabroomString, CollabroomPayload.class);
		}
		else{
			CollabroomPayload newPayload = new CollabroomPayload();
			newPayload.setCollabRoomId(-1);
			newPayload.setName(getContext().getResources().getString(R.string.no_selection));
			return newPayload;
		}
	}
	
	public String getActiveCollabroomName() {
		return mSharedPreferences.getPreferenceString(Constants.COLLABROOM_NAME);
	}
	
	public long getActiveIncidentId() {
		return mSharedPreferences.getPreferenceLong(Constants.INCIDENT_ID);
	}
	
	public long getPreviousIncidentId() {
		return mSharedPreferences.getPreferenceLong(Constants.PREVIOUS_INCIDENT_ID);
	}
	
	public String getActiveIncidentName() {
		return mSharedPreferences.getPreferenceString(Constants.INCIDENT_NAME, getContext().getResources().getString(R.string.no_selection));
	}

	
	public String getAssignmentStart() {
		Long startTime = mSharedPreferences.getPreferenceLong(Constants.ASSIGNMENT_START);
		String startTimeString = getContext().getResources().getString(R.string.no_selection);
		if(startTime != -1) {
			startTimeString = new Date(startTime).toString();
		}
		
		return startTimeString;
	}
	
	public String getAssignmentEnd() {
		Long startTime = mSharedPreferences.getPreferenceLong(Constants.ASSIGNMENT_END);
		String startTimeString =  getContext().getResources().getString(R.string.no_selection);
		if(startTime != -1) {
			startTimeString = new Date(startTime).toString();
		}
		
		return startTimeString;
	}

	public Object getAssignmentUnitName() {
		return mSharedPreferences.getPreferenceString(Constants.ASSIGNMENT_UNIT_NAME, getContext().getResources().getString(R.string.no_selection));
	}

	public void setHeartRate(Double hr, Integer confidence) {
		if(hr != null) {
			mSharedPreferences.savePreferenceFloat(Constants.LAST_HR, hr.floatValue());
		} else {
			mSharedPreferences.removePreference(Constants.LAST_HR);
		}
	}
	
	public double getHeartRate() {
		return mSharedPreferences.getPreferenceFloat(Constants.LAST_HR, "-1.0");
	}
	
	public void setHSI(Double hsi) {
		if(hsi != null) {
			mSharedPreferences.savePreferenceFloat(Constants.LAST_HSI, hsi.floatValue());
		} else {
			mSharedPreferences.removePreference(Constants.LAST_HSI);
		}
	}
	
	public double getHSI() {
		return mSharedPreferences.getPreferenceFloat(Constants.LAST_HSI, "-1.0");
	}
	
	public void setMDT(Location location) {
		
		if(isMDTEnabled()) {
			
//			Editor editor = mSharedPreferences.edit();
//			editor.putLong(Constants.LAST_MDT_TIME, location.getTime());
//			editor.putString(Constants.LAST_LATITUDE, String.valueOf(location.getLatitude()));
//			editor.putString(Constants.LAST_LONGITUDE, String.valueOf(location.getLongitude()));
//			editor.putString(Constants.LAST_ALTITUDE, String.valueOf(location.getAltitude()));
//			editor.putString(Constants.LAST_ACCURACY, String.valueOf(location.getAccuracy()));
//			editor.putString(Constants.LAST_COURSE, String.valueOf(location.getBearing()));
			
			mSharedPreferences.savePreferenceLong(Constants.LAST_MDT_TIME, location.getTime());
			mSharedPreferences.savePreferenceString(Constants.LAST_LATITUDE, String.valueOf(location.getLatitude()));
			mSharedPreferences.savePreferenceString(Constants.LAST_LONGITUDE, String.valueOf(location.getLongitude()));
			mSharedPreferences.savePreferenceString(Constants.LAST_ALTITUDE, String.valueOf(location.getAltitude()));
			mSharedPreferences.savePreferenceString(Constants.LAST_ACCURACY, String.valueOf(location.getAccuracy()));
			mSharedPreferences.savePreferenceString(Constants.LAST_COURSE, String.valueOf(location.getBearing()));
			
//			editor.commit();
			
			MobileDeviceTrackingPayload payload = new MobileDeviceTrackingPayload();
			payload.setLatitude(location.getLatitude());
			payload.setLongitude(location.getLongitude());
			payload.setAltitude(location.getAltitude());
			payload.setAccuracy(location.getAccuracy());
			payload.setCourse(location.getBearing());
			payload.setProvider(location.getProvider());
			payload.setSpeed(location.getSpeed());
			payload.setUserId(getUserId());
			payload.setDeviceId(RestClient.getDeviceId());
			payload.setIncidentId(getActiveIncidentId());
			payload.setSensorTimestamp(location.getTime());
			payload.setUserHealth(new UserHealth(getHeartRate(), getHSI()));
			
			long timeNow = System.currentTimeMillis();
			payload.setCreatedUTC(timeNow);
			payload.setLastUpdatedUTC(timeNow);
			
			if(getMDTLatitude() != location.getLatitude() && getMDTLongitude() != location.getLongitude() && getMDTAccuracy() != location.getAccuracy()) {
				this.addPersonalHistory("Sending MDT: " + getUserNickname() + " - Source: " + location.getProvider() + " (" + location.getLatitude() + ", " + location.getLongitude() + ", " + location.getAltitude() + ", " + location.getBearing() + ") - Accuracy: +/- " + location.getAccuracy() + " meters.");
			}
			
			if(isLoggedIn) {
				mDatabaseManager.addMobileDeviceTrackingStoreAndForward(payload);
				RestClient.postMDTs();
			}
		}
	}
	
	public long getMDTTime() {
		return mSharedPreferences.getPreferenceLong(Constants.LAST_MDT_TIME);
	}
	
	public double getMDTLatitude() {
		return Double.valueOf(mSharedPreferences.getPreferenceString(Constants.LAST_LATITUDE,"NaN"));
	}

	public double getMDTLongitude() {
		return Double.valueOf(mSharedPreferences.getPreferenceString(Constants.LAST_LONGITUDE,"NaN"));
	}

	public double getMDTAltitude() {
		return Double.valueOf(mSharedPreferences.getPreferenceString(Constants.LAST_ALTITUDE,"NaN"));
	}
	
	public double getMDTCourse() {
		return Double.valueOf(mSharedPreferences.getPreferenceString(Constants.LAST_COURSE,"NaN"));
	}
	
	public double getMDTAccuracy() {
		return Double.valueOf(mSharedPreferences.getPreferenceString(Constants.LAST_ACCURACY,"NaN"));
	}
	
	public double getIncidentPositionLatitude() {
		return Double.valueOf(mSharedPreferences.getPreferenceString(Constants.INCIDENT_LATITUDE,"NaN"));
	}

	public double getIncidentPositionLongitude() {
		return Double.valueOf(mSharedPreferences.getPreferenceString(Constants.INCIDENT_LONGITUDE,"NaN"));
	}

	public String getUserNickname() {
		UserPayload userData = getUserPayload();

		if(userData != null) {
			return userData.getFirstName() + " " + userData.getLastName();
		} else {
			return "Unknown User";
		}
	}

	public void setWeather(WeatherPayload payload) {
		mSharedPreferences.savePreferenceString(Constants.WEATHER_PAYLOAD, payload.toJsonString());
	}
	
	public WeatherPayload getWeather() {
		String payloadJson = mSharedPreferences.getPreferenceString(Constants.WEATHER_PAYLOAD);
		
		if(payloadJson != null) {
			return new Gson().fromJson(payloadJson, WeatherPayload.class);
		} else {
			return null;
		}
	}
	
	public String getServer() {
		if(mGlobalPreferences.getBoolean("custom_server_enabled", false)) {
			return mGlobalPreferences.getString("custom_server_url", getContext().getResources().getString(R.string.config_server_default));
		}
		
		return mGlobalPreferences.getString("server_list", getContext().getResources().getString(R.string.config_server_default));
	}
	
	public void setIplanetCookieDomain(String value){
		mSharedPreferences.savePreferenceString(Constants.IPLANET_COOKIE_DOMAIN, value);
	}
	public String getIplanetCookieDomain(){
		if(isCustomDomainEnabled()){
			String test = mGlobalPreferences.getString("custom_cookie_domain","");
			return mGlobalPreferences.getString("custom_cookie_domain","");
		}else{
			return mSharedPreferences.getPreferenceString(Constants.IPLANET_COOKIE_DOMAIN, getContext().getResources().getString(R.string.config_iplanet_cookie_domain_default));
		}
	}
	
	public void setAmAuthCookieDomain(String value){
		mSharedPreferences.savePreferenceString(Constants.AMAUTH_COOKIE_DOMAIN, value);
	}
	public String getAmAuthCookieDomain(){
		if(isCustomDomainEnabled()){
			return mGlobalPreferences.getString("custom_cookie_domain","");
		}else{
			return mSharedPreferences.getPreferenceString(Constants.AMAUTH_COOKIE_DOMAIN, getContext().getResources().getString(R.string.config_amauth_cookie_domain_default));	
		}
	}
	
	public boolean isCustomDomainEnabled() {
		return mGlobalPreferences.getBoolean("custom_domain_name_enabled", false);
	}
	
	public boolean isMDTEnabled() {
		return mGlobalPreferences.getBoolean("tracking_checkbox", true);
	}
	
	public boolean isLRFEnabled() {
		return mGlobalPreferences.getBoolean("lrf_checkbox", false);
	}
	
	public boolean isPushNotificationsDisabled(){
		return mGlobalPreferences.getBoolean("disable_notifications_checkbox", false);
	}
	
	public boolean isDebugEnabled() {
		return mGlobalPreferences.getBoolean("debug_checkbox", false);
	}
	public boolean isSyncWifiOnlyEnabled(){
		return mGlobalPreferences.getBoolean("tracking_sync_over_wifi_only_checkbox", false);
	}
	
	public int getIncidentDataRate() {
		if(LowDataMode){
			return LowDataRate;
		}else{
			return Integer.parseInt(mGlobalPreferences.getString("incident_sync_frequency", "60"));
		}
	}
	
	public int getCollabroomDataRate() {
		if(LowDataMode){
			return LowDataRate;
		}else{
			return Integer.parseInt(mGlobalPreferences.getString("collabroom_sync_frequency", "30"));
		}
	}
	
	public int getMDTDataRate() {
	if(LowDataMode){
			return LowDataRate;
		}else{
			return Integer.parseInt(mGlobalPreferences.getString("mdt_sync_frequency", "60"));
		}
	}
	
	public int getWFSDataRate() {
	if(LowDataMode){
			return LowDataRate;
		}else{
			return Integer.parseInt(mGlobalPreferences.getString("wfs_sync_frequency", "90"));
		}
	}
	
	public int getCoordinateRepresentation() {
		return Integer.parseInt(mGlobalPreferences.getString("coordinate_representation", "0"));
	}
	
	public String getGeoServerURL() {
		
		if(mGlobalPreferences.getBoolean("custom_geo_server_enabled", false)) {
			return mGlobalPreferences.getString("custom_geo_server_url", getContext().getResources().getString(R.string.config_geo_server_default));
		}
		
		return mGlobalPreferences.getString("geo_server_list", getContext().getResources().getString(R.string.config_geo_server_default));
	}
	
	public String getAuthServerURL() {
	
		if(mGlobalPreferences.getBoolean("custom_auth_server_enabled", false)) {
			return mGlobalPreferences.getString("custom_auth_server_url", getContext().getResources().getString(R.string.config_auth_server_default));
		}
		
		return mGlobalPreferences.getString("auth_server_list", getContext().getResources().getString(R.string.config_auth_server_default));
		
	}
	
	public String getAuthToken() {
		return mSharedPreferences.getPreferenceString("auth_token", null);
	}

	public void setAuthToken(String tokenId) {
		if(tokenId == null) {
			mSharedPreferences.removePreference("auth_token");
		} else {
			mSharedPreferences.savePreferenceString("auth_token", tokenId);
		}
	}
	
	public String getSelectedLanguage(){
		String code = mGlobalPreferences.getString("language_select_list","Device Default");
		if(code.equals("Device Default")){
			code = Locale.getDefault().getISO3Language().substring(0,2);
		}
		return code;
	}

	public void setLowDataMode(boolean value){
		LowDataMode = value;
		
		//refresh rates
		requestFieldReportRepeating(getIncidentDataRate(),false);
		requestDamageReportRepeating(getIncidentDataRate(),false);
		requestSimpleReportRepeating(getIncidentDataRate(),false);
		requestResourceRequestRepeating(getIncidentDataRate(),false);
		requestWeatherReportRepeating(getIncidentDataRate(),false);
		requestMarkupRepeating(getCollabroomDataRate(),false);
		requestChatMessagesRepeating(getCollabroomDataRate(),false);
	}
	public boolean getLowDataMode(){
		return LowDataMode;
	}
	
	public void setCurrentNavigationView(String newView){
		currentNavigationView = newView;
	}
	public String getCurrentNavigationView(){
		return currentNavigationView;
	}
	public void setCurrentLocale(String newLanguage){
		mLocale = new Locale(newLanguage); 
	    Resources res = getContext().getResources(); 
	    DisplayMetrics dm = res.getDisplayMetrics(); 
	    Configuration conf = res.getConfiguration(); 
	    conf.locale = mLocale; 
	    res.updateConfiguration(conf, dm);
	}
	
	//Loaded only once upon app start
	public void loadLanguageConvertStorage(){
	
		translationReverseLookup = new HashMap<String, String>(); 

		Resources res = mContext.getResources();
		Configuration conf = res.getConfiguration();
		Locale savedLocale = conf.locale;
		
		for(int i = 1; i < supportedLanguages.length; i++){	//Start at one to skip over "Device Default"
		
			Locale tempLocale = new Locale(supportedLanguages[i]); 
			conf.locale = tempLocale;
			res.updateConfiguration(conf, null);
			
	        for (SimpleReportCategoryType simpleReportCategoryType : SimpleReportCategoryType.values ()) {
	            
	            int resId = res.getIdentifier(simpleReportCategoryType.getTextKey(), "string", mContext.getPackageName());
	            String key = (res.getString(resId));
	            
	            String value = simpleReportCategoryType.getTextKey();
	            translationReverseLookup.put(key, value);
	        }
	          
	        for (WeatherWindTypes windType : WeatherWindTypes.values ()) {
	            
	            int resId = res.getIdentifier(windType.getTextKey(), "string", mContext.getPackageName());
	            String key = (res.getString(resId));
	            
	            String value = windType.getTextKey();
	            translationReverseLookup.put(key, value);
	        }
	        
	        for (WeatherSourceTypes sourceType : WeatherSourceTypes.values ()) {
	            
	            int resId = res.getIdentifier(sourceType.getTextKey(), "string", mContext.getPackageName());
	            String key = (res.getString(resId));
	            
	            String value = sourceType.getTextKey();
	            translationReverseLookup.put(key, value);
	        }
	        
	        for (PropertyType propertyType : PropertyType.values ()) {
	            
	            int resId = res.getIdentifier(propertyType.getTextKey(), "string", mContext.getPackageName());
	            String key = (res.getString(resId));
	            
	            String value = propertyType.getTextKey();
	            translationReverseLookup.put(key, value);
	        }
	        
	        for (PropertyDamageType propertyDamageType : PropertyDamageType.values ()) {
	            
	            int resId = res.getIdentifier(propertyDamageType.getTextKey(), "string", mContext.getPackageName());
	            String key = (res.getString(resId));
	            
	            String value = propertyDamageType.getTextKey();
	            translationReverseLookup.put(key, value);
	        }
	        
	        for (ResReqPriorityTypes resReqPriorityType : ResReqPriorityTypes.values ()) {
	            
	            int resId = res.getIdentifier(resReqPriorityType.getTextKey(), "string", mContext.getPackageName());
	            String key = (res.getString(resId));
	            
	            String value = resReqPriorityType.getTextKey();
	            translationReverseLookup.put(key, value);
	        }
	        
	        for (ResReqType resReqType : ResReqType.values ()) {
	            
	            int resId = res.getIdentifier(resReqType.getTextKey(), "string", mContext.getPackageName());
	            String key = (res.getString(resId));
	            
	            String value = resReqType.getTextKey();
	            translationReverseLookup.put(key, value);
	        }
		}
		
		// restore original locale
		conf.locale = savedLocale;
		res.updateConfiguration(conf, null);
    }
	
	public String reverseLanguageLookup(String key){
		
		return translationReverseLookup.get(key);
	}
	
	public void setSupportedLanguages(String[] languages){
		supportedLanguages = languages;
	}
	public String[] getSupportedLanguages(){
		return supportedLanguages;
	}
	public boolean getTabletLayoutOn(){
		if(isTabletLayout == null){
			isTabletLayout = mSharedPreferences.getPreferenceBoolean("tablet_layout", "false");
		}
		return isTabletLayout;
	}
	public void setTabletLayoutOn(boolean value){
		isTabletLayout = value;
		mSharedPreferences.savePreferenceBoolean("tablet_layout", value);
	}
	public boolean isNewGeneralMessageAvailable(){
		return newGeneralMessageAvailable;
	}
	public void setNewGeneralMessageAvailable(boolean available){
		newGeneralMessageAvailable = available;
	}
	public boolean isNewReportAvailable() {
		return newReportAvailable;
	}
	public void setNewReportAvailable(boolean newReportAvailable) {
		this.newReportAvailable = newReportAvailable;
	}
	public boolean isNewchatAvailable() {
		return newchatAvailable;
	}
	public void setNewchatAvailable(boolean newchatAvailable) {
		this.newchatAvailable = newchatAvailable;
	}
	public boolean isNewMapAvailable() {
		return newMapAvailable;
	}
	public void setNewMapAvailable(boolean newMapAvailable) {
		this.newMapAvailable = newMapAvailable;
	}
	private void initializeTrackingLayers(){
		
			TrackingLayers = new ArrayList<TrackingLayerPayload>();
			
			TrackingLayerPayload damagePayload = new TrackingLayerPayload();
			damagePayload.setDisplayname(mContext.getString(R.string.wfslayer_nics_damage_report_title));
			damagePayload.setLayername("nics_dmgrpt");
			damagePayload.setInternalurl(getGeoServerURL());
			
			TrackingLayerPayload generalPayload = new TrackingLayerPayload();
			generalPayload.setDisplayname(mContext.getString(R.string.wfslayer_nics_simple_report_title));
			generalPayload.setLayername("nics_sr");
			generalPayload.setInternalurl(getGeoServerURL());
						
			TrackingLayers.add(generalPayload);
			TrackingLayers.add(damagePayload);
	}
	public ArrayList<TrackingLayerPayload>  getTrackingLayers() {
		if(TrackingLayers == null){
			initializeTrackingLayers();
		}
		return TrackingLayers;
	}
	public void setTrackingLayers(ArrayList<TrackingLayerPayload>  trackingLayers) {
		initializeTrackingLayers();
		if(trackingLayers != null){
			TrackingLayers.addAll(trackingLayers);
		}
	}
	public boolean UpdateTrackingLayerData(TrackingLayerPayload updatedTrackingLayer){
	
		for(int i = 0; i < TrackingLayers.size();i++){
			TrackingLayerPayload layer = TrackingLayers.get(i);
			if(layer.getLayername().equals(updatedTrackingLayer.getDisplayname())){
				TrackingLayers.set(i, updatedTrackingLayer);
				return true;
			}
		}
		return false;
	}
}
