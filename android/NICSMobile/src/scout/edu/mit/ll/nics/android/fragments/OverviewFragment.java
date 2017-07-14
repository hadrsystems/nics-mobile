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
package scout.edu.mit.ll.nics.android.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.RestClient;
import scout.edu.mit.ll.nics.android.api.data.OrgCapabilities;
import scout.edu.mit.ll.nics.android.api.payload.CollabroomPayload;
import scout.edu.mit.ll.nics.android.api.payload.IncidentPayload;
import scout.edu.mit.ll.nics.android.utils.Intents;
import scout.edu.mit.ll.nics.android.utils.Constants.NavigationOptions;

public class OverviewFragment extends Fragment {
	
	private View mRootView;
	private MainActivity mMainActivity;
	private DataManager mDataManager;
	
	private ImageButton mGeneralMessageButton;
	private LinearLayout mGeneralMessageButtonLayout;
	private ImageButton mReportButton;
	private LinearLayout mReportButtonLayout;
	private ImageButton mChatButton;
	private ImageButton mMapButton;
	private LinearLayout mChatButtonLayout;
	
	private ImageView mNewGeneralMessageImageView;
	private ImageView mNewReportImageView;
	private ImageView mNewChatImageView;
	private ImageView mNewMapImageView;
	
	private Button mJoinIncidentButton;
	private Button mJoinRoomButton;
	private FrameLayout mJoinRoomButtonLayout;
	
	private ProgressBar mRoomsLoadingProgressBar;
	
	private AlertDialog.Builder mDialogBuilder;
	private AlertDialog mIncidentPopupMenu;
	private AlertDialog mRoomPopupMenu;
	private AlertDialog mReportPopupMenu;

	private View mIncidentFrameLayout;
	private View mRoomFrameLayout;
	
	private String[] incidentArray;
	private String[] dialogNameArray;
	private HashMap<Long, CollabroomPayload> collabRoomsIncidentIdMap;
	private Long[] dialogIndexToCollabIdLookup;
	
	private LinearLayout mIncidentFrameButtonLayout;
	private LinearLayout mRoomFrameButtonLayout;

	List<String> activeReports = new ArrayList<String>();
	Resources resources;
	
	private boolean collabroomsReceiverRegistered = false;
	private IntentFilter mCollabroomsBeganPolling;
	private IntentFilter mCollabroomsSuccessReceiverFilter;
	private IntentFilter mCollabroomsFailReceiverFilter;
	private IntentFilter mSimpleReportReceivedFilter;
	private IntentFilter mChatReceivedFilter;
	private IntentFilter mMapReceivedFilter;
	private IntentFilter mDamageReportReceivedFilter;
	private IntentFilter mWeatherReportReceivedFilter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mMainActivity = (MainActivity) getActivity();
		mDataManager = DataManager.getInstance(mMainActivity);
		resources = mMainActivity.getResources();
		
		mCollabroomsBeganPolling = new IntentFilter(Intents.nics_POLLING_COLLABROOMS);
		mCollabroomsSuccessReceiverFilter = new IntentFilter(Intents.nics_SUCCESSFULLY_GET_COLLABROOMS);
		mCollabroomsFailReceiverFilter = new IntentFilter(Intents.nics_FAILED_GET_COLLABROOMS);
		mSimpleReportReceivedFilter = new IntentFilter(Intents.nics_NEW_SIMPLE_REPORT_RECEIVED);
		mChatReceivedFilter = new IntentFilter(Intents.nics_LAST_CHAT_RECEIVED);
		mMapReceivedFilter = new IntentFilter(Intents.nics_NEW_MARKUP_RECEIVED);
		
		mDamageReportReceivedFilter = new IntentFilter(Intents.nics_NEW_DAMAGE_REPORT_RECEIVED);
		mWeatherReportReceivedFilter = new IntentFilter(Intents.nics_NEW_WEATHER_REPORT_RECEIVED);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		if(mDataManager.getTabletLayoutOn()){
			mRootView = inflater.inflate(R.layout.fragment_overview_tablet, container, false);
		}else{
			mRootView = inflater.inflate(R.layout.fragment_overview, container, false);
		}

		mDialogBuilder = new AlertDialog.Builder(mMainActivity);
		
		mJoinIncidentButton = (Button) mRootView.findViewById(R.id.joinIncidentButton);
		mJoinRoomButton = (Button) mRootView.findViewById(R.id.joinRoomButton);
		
		mJoinRoomButtonLayout = (FrameLayout) mRootView.findViewById(R.id.joinRoomButtonLayout);
		
		mGeneralMessageButton = (ImageButton) mRootView.findViewById(R.id.generalMessageButton);
		mGeneralMessageButtonLayout = (LinearLayout) mRootView.findViewById(R.id.generalMessageButtonLayout);
		mReportButton = (ImageButton) mRootView.findViewById(R.id.reportsButton);
		mReportButtonLayout = (LinearLayout) mRootView.findViewById(R.id.damageReportButtonLayout);
		mChatButton = (ImageButton) mRootView.findViewById(R.id.chatButton);
		
		mChatButtonLayout = (LinearLayout) mRootView.findViewById(R.id.chatButtonLayout);
		
		mIncidentFrameButtonLayout = (LinearLayout) mRootView.findViewById(R.id.incidentFrameButtonLayout);
		mRoomFrameButtonLayout = (LinearLayout) mRootView.findViewById(R.id.roomFrameButtonLayout);
		
		mRoomsLoadingProgressBar = (ProgressBar) mRootView.findViewById(R.id.roomsLoadingProgressBar);
		
		mJoinIncidentButton.setOnClickListener(showIncidentPopupMenu);
		mJoinRoomButton.setOnClickListener(showRoomPopupMenu);
		
		mReportButton.setOnClickListener(showReportPopupMenu);
		mGeneralMessageButton.setOnClickListener(clickNavigationListener);
		mChatButton.setOnClickListener(clickNavigationListener);
		
		mIncidentFrameLayout = mRootView.findViewById(R.id.incidentFrameLayoutBorder);
		mRoomFrameLayout = mRootView.findViewById(R.id.roomFrameLayoutBorder);
		
		mMapButton = (ImageButton) mRootView.findViewById(R.id.mapButton);
		mMapButton.setOnClickListener(clickNavigationListener);
		
		mNewGeneralMessageImageView = (ImageView) mRootView.findViewById(R.id.generalMessageNotificationImage);
		mNewReportImageView = (ImageView) mRootView.findViewById(R.id.reportsNotificationImage);
		mNewChatImageView = (ImageView) mRootView.findViewById(R.id.chatNotificationImage);
		mNewMapImageView = (ImageView) mRootView.findViewById(R.id.mapNotificationImage);
		
		if(mDataManager.isNewGeneralMessageAvailable()){
			mNewGeneralMessageImageView.setVisibility(View.VISIBLE);
		}else{
			mNewGeneralMessageImageView.setVisibility(View.INVISIBLE);
		}
		if(mDataManager.isNewReportAvailable()){
			mNewReportImageView.setVisibility(View.VISIBLE);
		}else{
			mNewReportImageView.setVisibility(View.INVISIBLE);
		}
		if(mDataManager.isNewchatAvailable()){
			mNewChatImageView.setVisibility(View.VISIBLE);
		}else{
			mNewChatImageView.setVisibility(View.INVISIBLE);
		}
		if(mDataManager.isNewMapAvailable()){
			mNewMapImageView.setVisibility(View.VISIBLE);
		}else{
			mNewMapImageView.setVisibility(View.INVISIBLE);
		}
		
		setupActiveReportsFromOrgProfile();
		if(mDataManager.getOrgCapabilities().getChat() == false){
			mChatButton.setClickable(false);
			mChatButtonLayout.setAlpha(0.3f);
		}
		
		if(!mDataManager.getActiveIncidentName().equals(getString(R.string.no_selection))) {
			mJoinIncidentButton.setText(getString(R.string.incident_active, mDataManager.getActiveIncidentName()));
		} else {
			mJoinIncidentButton.setText(getString(R.string.incident_join));
		}

		IncidentPayload activeIncident = null;
		
		if(mDataManager.getActiveIncidentName().equals(getString(R.string.no_selection))) {
			if(mIncidentFrameButtonLayout!=null){
				mIncidentFrameButtonLayout.setAlpha(0.3f);
			}else{
				mGeneralMessageButtonLayout.setAlpha(0.3f);
				mReportButtonLayout.setAlpha(0.3f);
			}
			
			if(mRoomFrameLayout!=null){
				mRoomFrameLayout.setAlpha(0.3f);
			}
			if(mRoomFrameButtonLayout!=null){
				mRoomFrameButtonLayout.setAlpha(1f);
			}
			
			mGeneralMessageButton.setClickable(false);
			mReportButton.setClickable(false);
			
			mChatButton.setClickable(false);
			mChatButtonLayout.setAlpha(0.3f);
		} else {
			HashMap<String, IncidentPayload> incidents = mDataManager.getIncidents();
			
			if(incidents != null) {
				if((activeIncident = incidents.get(mDataManager.getActiveIncidentName())) != null) {
					ArrayList<CollabroomPayload> incidentRooms = activeIncident.getCollabrooms();
					Collections.sort(incidentRooms, new Comparator<CollabroomPayload>() {
		
						@Override
						public int compare(CollabroomPayload lhs, CollabroomPayload rhs) {
							return lhs.getName().compareTo(rhs.getName());
						}
					});
		
					mDataManager.clearCollabRoomList();
					for(CollabroomPayload room : incidentRooms) {
						mDataManager.addCollabroom(room);
					}
					mDataManager.requestDamageReportRepeating(mDataManager.getIncidentDataRate(), true);
					mDataManager.requestFieldReportRepeating(mDataManager.getIncidentDataRate(), true);
					mDataManager.requestSimpleReportRepeating(mDataManager.getIncidentDataRate(), true);
					mDataManager.requestResourceRequestRepeating(mDataManager.getIncidentDataRate(), true);
					
				} else {
					RestClient.getAllIncidents(mDataManager.getUserId());
					mDialogBuilder.setTitle(R.string.incident_error_not_found);
					mDialogBuilder.setMessage(getString(R.string.incident_no_longer_exists, mDataManager.getActiveIncidentName()));
					mDialogBuilder.setItems(null, null);
					mDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					mDialogBuilder.create().show();
					
					mDataManager.setCurrentIncidentData(null, -1, "");
					mDataManager.setSelectedCollabRoom(null);

					mJoinIncidentButton.setText(getString(R.string.incident_join));
				}
			}
		}

		if(activeIncident != null) {
			
			mDataManager.requestCollabrooms(activeIncident.getIncidentId(),  activeIncident.getIncidentName());		
			
			ArrayList<CollabroomPayload> incidentRooms = activeIncident.getCollabrooms();
			Collections.sort(incidentRooms, new Comparator<CollabroomPayload>() {

				@Override
				public int compare(CollabroomPayload lhs, CollabroomPayload rhs) {
					return lhs.getName().compareTo(rhs.getName());
				}
			});
			
			CollabroomPayload defaultRoom = null;
			String roomName = "";
			for(CollabroomPayload room : incidentRooms) {
				mDataManager.addCollabroom(room);
				roomName = room.getName();
				if(roomName.contains("WorkingMap") || (roomName.isEmpty() && roomName.contains("IncidentMap")) ) {
					defaultRoom = room;
				}
			}
			
			if(defaultRoom == null && incidentRooms.size() > 0) {
				defaultRoom = incidentRooms.get(0);
			}
			
			boolean noRoomSelected = mDataManager.getSelectedCollabRoom().getName().equals(getString(R.string.no_selection));
			mJoinRoomButton.setText(getString(R.string.room_join));
			
			if(noRoomSelected) {
				if(mMapButton!=null){
					mMapButton.setClickable(true);
				}
				mChatButton.setClickable(false);
				
				mChatButtonLayout.setAlpha(0.3f);
				if(mRoomFrameLayout!=null){
					mRoomFrameLayout.setAlpha(0.3f);
				}
				if(mRoomFrameButtonLayout!=null){
					mRoomFrameButtonLayout.setAlpha(1f);
				}
				
			} 
			/*else if(!activeIncident.containsCollabroom(mDataManager.getSelectedCollabRoomName(), mDataManager.getSelectedCollabRoomId())){
				mMapButton.setEnabled(true);
				mChatButton.setEnabled(false);
				mChatButtonLayout.setVisibility(View.INVISIBLE);
				mRoomFrameLayout.setVisibility(View.INVISIBLE);
				mRoomFrameButtonLayout.setVisibility(View.VISIBLE);
				
				mDialogBuilder.setTitle(R.string.room_error_not_found);
				mDialogBuilder.setMessage(getString(R.string.room_no_longer_exists, mDataManager.getSelectedCollabRoomName()));
				mDialogBuilder.setItems(null, null);
				mDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				mDialogBuilder.create().show();
				
				mDataManager.setSelectedCollabRoom(null, -1);
			}*/
			 else {
				mJoinRoomButton.setText(getString(R.string.room_active, mDataManager.getSelectedCollabRoom().getName().replace(mDataManager.getActiveIncidentName() + "-", "")));
				if(mMapButton != null){
					mMapButton.setClickable(true);
				}
				
				if(mDataManager.getOrgCapabilities().getChat()){
					mChatButton.setClickable(true);
					mChatButtonLayout.setAlpha(1f);
				}
				if(mRoomFrameLayout != null){
					mRoomFrameLayout.setAlpha(1f);
				}
				if(mRoomFrameButtonLayout != null){
					mRoomFrameButtonLayout.setAlpha(1f);
				}
				
				mDataManager.requestMarkupRepeating(mDataManager.getCollabroomDataRate(), true);
				mDataManager.requestChatMessagesRepeating(mDataManager.getCollabroomDataRate(), true);
			}
		} else {
			
			mJoinRoomButton.setClickable(false);
			mJoinRoomButtonLayout.setAlpha(0.3f);
//			mJoinRoomButton.setAlpha(0.3f);
			if(mIncidentFrameLayout != null){
				mIncidentFrameLayout.setAlpha(0.0f);
			}
			if(mRoomFrameLayout != null){
				mRoomFrameLayout.setAlpha(0.0f);
			}
			if(mRoomFrameButtonLayout != null){
				mRoomFrameButtonLayout.setAlpha(1f);
			}
			
			mJoinRoomButton.setText(getString(R.string.room_join));
		}
		
		mDataManager.stopPollingAssignment();
		
		setHasOptionsMenu(true);
		
		return mRootView;
	}
	
	private void setupActiveReportsFromOrgProfile()
	{
		//init reports based on org profile
		//this should probably get moved into datamanager maybe
		
	    OrgCapabilities orgCapabilities = mDataManager.getOrgCapabilities();

		activeReports.clear();
		
		if(orgCapabilities.getDamageReportForm()){activeReports.add( resources.getString(R.string.DAMAGESURVEY));}
		if(orgCapabilities.getFieldReportForm()){activeReports.add( resources.getString(R.string.FIELDREPORT));}
		if(orgCapabilities.getResourceRequestForm()){activeReports.add( resources.getString(R.string.RESOURCEREQUEST));}
		if(orgCapabilities.getWeatherReportForm()){activeReports.add( resources.getString(R.string.WEATHERREPORT));}
	}
	
    @Override
	public void onResume() {
        super.onResume();
    
		if(!collabroomsReceiverRegistered) {
			mDataManager.getContext().registerReceiver(CollabroomPollingReceiver, mCollabroomsBeganPolling);
			mDataManager.getContext().registerReceiver(CollabroomReceiver, mCollabroomsSuccessReceiverFilter);
			mDataManager.getContext().registerReceiver(CollabroomReceiver, mCollabroomsFailReceiverFilter);
			mDataManager.getContext().registerReceiver(simpleReportReceived, mSimpleReportReceivedFilter);
			mDataManager.getContext().registerReceiver(chatReceived, mChatReceivedFilter);
			mDataManager.getContext().registerReceiver(mapReceived, mMapReceivedFilter);
			
			mDataManager.getContext().registerReceiver(reportReceived, mDamageReportReceivedFilter);
			mDataManager.getContext().registerReceiver(reportReceived, mWeatherReportReceivedFilter);
			collabroomsReceiverRegistered = true;
		}
        
		RestClient.getAllIncidents(mDataManager.getUserId());
		
        mDataManager.sendChatMessages();
        mDataManager.sendFieldReports();
        mDataManager.sendDamageReports();
        mDataManager.sendMarkupFeatures();
        mDataManager.sendResourceRequests();
        mDataManager.sendSimpleReports();
    }
    
	@Override
	public void onPause() {
		super.onPause();

		if(mIncidentPopupMenu != null) {
			mIncidentPopupMenu.dismiss();
		}
		
		if(mRoomPopupMenu != null) {
			mRoomPopupMenu.dismiss();
		}
		
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		if(!collabroomsReceiverRegistered) {
			mDataManager.getContext().unregisterReceiver(CollabroomPollingReceiver);
			mDataManager.getContext().unregisterReceiver(CollabroomReceiver);
			mDataManager.getContext().unregisterReceiver(CollabroomReceiver);
			mDataManager.getContext().unregisterReceiver(simpleReportReceived);
			mDataManager.getContext().unregisterReceiver(chatReceived);
			mDataManager.getContext().unregisterReceiver(mapReceived);			
			collabroomsReceiverRegistered = false;
		}
		
		((ViewGroup)mRootView.getParent()).removeView(mRootView);
	}
	
	OnClickListener clickNavigationListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int navigationId = NavigationOptions.OVERVIEW.getValue();
			int viewId = v.getId();
			
			if(viewId == R.id.generalMessageButton) {
				navigationId = NavigationOptions.GENERALMESSAGE.getValue();
				mNewGeneralMessageImageView.setVisibility(View.INVISIBLE);
			} else if(viewId == R.id.chatButton) {
				navigationId = NavigationOptions.CHATLOG.getValue();
				mNewChatImageView.setVisibility(View.INVISIBLE);
			} else if(viewId == R.id.mapButton) {
				navigationId = NavigationOptions.MAPCOLLABORATION.getValue();
				mNewMapImageView.setVisibility(View.INVISIBLE);
			}
			mMainActivity.onNavigationItemSelected(navigationId, -1);
		}
		
	};
	
	OnClickListener showIncidentPopupMenu = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mDialogBuilder.setTitle(R.string.select_an_incident);
			mDialogBuilder.setMessage(null);
		    mDialogBuilder.setPositiveButton(null, null);
		    HashMap<String, IncidentPayload> incidentsMap = mDataManager.getIncidents();
		    incidentArray = new String[incidentsMap.size()];
		    incidentsMap.keySet().toArray(incidentArray);
		    Arrays.sort(incidentArray);
			mDialogBuilder.setItems(incidentArray, incidentSelected);
			mIncidentPopupMenu = mDialogBuilder.create();
			mIncidentPopupMenu.show();
		}
	};
	
	DialogInterface.OnClickListener incidentSelected = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			mDataManager.clearCollabRoomList();
			mGeneralMessageButton.setClickable(true);
			mReportButton.setClickable(true);
			
			if(mIncidentFrameLayout != null){
				mIncidentFrameLayout.setAlpha(1f);
			}
			if(mIncidentFrameButtonLayout != null){
				mIncidentFrameButtonLayout.setAlpha(1f);
			}else{
				mGeneralMessageButtonLayout.setAlpha(1f);
				mReportButtonLayout.setAlpha(1f);
			}
			mJoinRoomButtonLayout.setAlpha(1f);
//			mJoinRoomButton.setAlpha(1f);
			mJoinRoomButton.setClickable(true);
			
			IncidentPayload currentIncident = mDataManager.getIncidents().get(incidentArray[which]);
			
			mDataManager.requestCollabrooms(currentIncident.getIncidentId(),  currentIncident.getIncidentName());		
			
			ArrayList<CollabroomPayload> incidentRooms = currentIncident.getCollabrooms();
			Collections.sort(incidentRooms, new Comparator<CollabroomPayload>() {

				@Override
				public int compare(CollabroomPayload lhs, CollabroomPayload rhs) {
					return lhs.getName().compareTo(rhs.getName());
				}
			});
			
			CollabroomPayload defaultRoom = null;
			String roomName = "";
			for(CollabroomPayload room : incidentRooms) {
				mDataManager.addCollabroom(room);
				roomName = room.getName();
				if(roomName.contains("WorkingMap") || (roomName.isEmpty() && roomName.contains("IncidentMap")) ) {
					defaultRoom = room;
				}
			}
			
			if(defaultRoom == null && incidentRooms.size() > 0) {
				defaultRoom = incidentRooms.get(0);
			}
			
			mDataManager.setCurrentIncidentData(currentIncident, -1, "");	
			mDataManager.setSelectedCollabRoom(null);

			mDataManager.stopPollingAssignment();
			mDataManager.stopPollingChat();
			mDataManager.stopPollingMarkup();
			mDataManager.requestFieldReportRepeating(mDataManager.getIncidentDataRate(), true);
			mDataManager.requestDamageReportRepeating(mDataManager.getIncidentDataRate(), true);
			mDataManager.requestSimpleReportRepeating(mDataManager.getIncidentDataRate(), true);
			mDataManager.requestResourceRequestRepeating(mDataManager.getIncidentDataRate(), true);
			
			mJoinIncidentButton.setText(getString(R.string.incident_active, mDataManager.getActiveIncidentName()));
			mJoinRoomButton.setText(getString(R.string.room_join));
			
			if(mMapButton != null){
				mMapButton.setClickable(true);
			}
			mChatButton.setClickable(false);
			
			mChatButtonLayout.setAlpha(0.3f);
			if(mRoomFrameButtonLayout != null){
				mRoomFrameButtonLayout.setAlpha(1f);
			}
			if(mRoomFrameLayout != null){
				mRoomFrameLayout.setAlpha(0.3f);
			}
			
	        Intent intent = new Intent();
	        intent.setAction(Intents.nics_INCIDENT_SWITCHED);
	        mDataManager.getContext().sendBroadcast(intent);
		}

	};
	
	OnClickListener showReportPopupMenu = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mDialogBuilder.setTitle(R.string.select_report_type);
			mDialogBuilder.setMessage(R.string.feature_coming_soon);
			mDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		    
//		    for(int i = 0; i < activeReports.size(); i++) {
//		    	activeReports.set(i, activeReports.get(i).replace(mDataManager.getActiveIncidentName() + "-", ""));
//		    }
//		    
//		    if(activeReports.size() == 0)
//		    {
//		    	activeReports.add(getString(R.string.reports_not_available_for_your_organization));
//		    }
//
//			mDialogBuilder.setItems(activeReports.toArray(new String[activeReports.size()]), reportSelected);
			mReportPopupMenu = mDialogBuilder.create();
			
		    mReportPopupMenu.show();
		}
	};
	
	DialogInterface.OnClickListener reportSelected = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(activeReports.get(which).equals( resources.getString(R.string.DAMAGESURVEY)) ){
				mMainActivity.onNavigationItemSelected(NavigationOptions.DAMAGESURVEY.getValue(), -1);
			}
			else if(activeReports.get(which).equals( resources.getString(R.string.FIELDREPORT)) ){
				mMainActivity.onNavigationItemSelected(NavigationOptions.FIELDREPORT.getValue(), -1);
			}
			else if(activeReports.get(which).equals( resources.getString(R.string.RESOURCEREQUEST)) ){
				mMainActivity.onNavigationItemSelected(NavigationOptions.RESOURCEREQUEST.getValue(), -1);
			}
			else if(activeReports.get(which).equals( resources.getString(R.string.WEATHERREPORT)) ){
				mMainActivity.onNavigationItemSelected(NavigationOptions.WEATHERREPORT.getValue(), -1);
			}
			mNewReportImageView.setVisibility(View.INVISIBLE);
		}
	};
	
	OnClickListener showRoomPopupMenu = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mDialogBuilder.setTitle(R.string.select_a_room);
			mDialogBuilder.setMessage(null);
		    mDialogBuilder.setPositiveButton(null, null);
		    collabRoomsIncidentIdMap = mDataManager.getCollabRoomMapById();	//not getting collabrooms
		     
		    dialogNameArray = new String[collabRoomsIncidentIdMap.size()];
		    dialogIndexToCollabIdLookup = new Long[collabRoomsIncidentIdMap.size()];
		    
		    int indexCount = 0;
		    for (Map.Entry< Long , CollabroomPayload> entry : collabRoomsIncidentIdMap.entrySet()) {
		        Long key = entry.getKey();
		        CollabroomPayload payload = entry.getValue();

		        dialogNameArray[indexCount] = payload.getName().replace(mDataManager.getActiveIncidentName() + "-", "");
		        dialogIndexToCollabIdLookup[indexCount] = key;
		        indexCount++;
		    }

		    if(dialogNameArray.length > 0) {
		    	mDialogBuilder.setMessage(null);
		    	mDialogBuilder.setItems(dialogNameArray, roomSelected);
		    } else {
		    	mDialogBuilder.setMessage(R.string.no_rooms_accessible);
		    	mDialogBuilder.setItems(null, null);
		    }
			mRoomPopupMenu = mDialogBuilder.create();
			
		    mRoomPopupMenu.show();
		}
	};
	
	DialogInterface.OnClickListener roomSelected = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {

			CollabroomPayload currentRoom = collabRoomsIncidentIdMap.get(dialogIndexToCollabIdLookup[which]);
//			CollabroomPayload currentRoom = mDataManager.getCollabRoomList().get(mDataManager.getCollabRoomMapById().get(collabroomArray[which].getCollabRoomId()));

			mDataManager.stopPollingChat();
			mDataManager.stopPollingMarkup();
			
			mDataManager.setSelectedCollabRoom(currentRoom);
			
			mDataManager.stopPollingAssignment();
			mDataManager.requestMarkupRepeating(mDataManager.getCollabroomDataRate(), true);

			mJoinRoomButton.setText(getString(R.string.room_active, mDataManager.getSelectedCollabRoom().getName().replace(mDataManager.getActiveIncidentName() + "-", "")));

			if(mRoomFrameLayout != null){
				mRoomFrameLayout.setAlpha(1.0f);
			}
			if(mRoomFrameButtonLayout != null){
				mRoomFrameButtonLayout.setAlpha(1.0f);
			}
			
			if(mMapButton != null){
				mMapButton.setClickable(true);
			}
			
			if(mDataManager.getOrgCapabilities().getChat()){
				mChatButton.setClickable(true);
				mChatButtonLayout.setAlpha(1.0f);
			}
			
	        Intent intent = new Intent();
	        intent.setAction(Intents.nics_COLLABROOM_SWITCHED);
	        mDataManager.getContext().sendBroadcast(intent);
		}
	};
	
	private BroadcastReceiver CollabroomReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				mRoomsLoadingProgressBar.setVisibility(View.INVISIBLE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private BroadcastReceiver CollabroomPollingReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				mRoomsLoadingProgressBar.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	
	private BroadcastReceiver simpleReportReceived = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mNewGeneralMessageImageView.setVisibility(View.VISIBLE);
		}
	};
	
	private BroadcastReceiver chatReceived = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mNewChatImageView.setVisibility(View.VISIBLE);
		}
	};
	
	private BroadcastReceiver mapReceived = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mNewMapImageView.setVisibility(View.VISIBLE);
		}
	};

	private BroadcastReceiver reportReceived = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mNewReportImageView.setVisibility(View.VISIBLE);
		}
	};
	
}
