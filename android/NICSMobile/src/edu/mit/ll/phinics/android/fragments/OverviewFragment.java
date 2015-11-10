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
package edu.mit.ll.phinics.android.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.renderscript.Script.FieldBase;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import edu.mit.ll.phinics.android.MainActivity;
import edu.mit.ll.phinics.android.R;
import edu.mit.ll.phinics.android.api.DataManager;
import edu.mit.ll.phinics.android.api.RestClient;
import edu.mit.ll.phinics.android.api.data.OrgCapabilities;
import edu.mit.ll.phinics.android.api.payload.CollabroomPayload;
import edu.mit.ll.phinics.android.api.payload.IncidentPayload;
import edu.mit.ll.phinics.android.utils.Intents;
import edu.mit.ll.phinics.android.utils.Constants.NavigationOptions;

public class OverviewFragment extends Fragment {
	
	private View mRootView;
	private MainActivity mMainActivity;
	private DataManager mDataManager;
	
	private ImageButton mGeneralMessageButton;
	private ImageButton mReportButton;
	private ImageButton mChatButton;
	private ImageButton mMapButton;
	private LinearLayout mChatButtonLayout;
	
	private Button mJoinIncidentButton;
	private Button mJoinRoomButton;
	private FrameLayout mJoinRoomButtonLayout;
	
	private AlertDialog.Builder mDialogBuilder;
	private AlertDialog mIncidentPopupMenu;
	private AlertDialog mRoomPopupMenu;
	private AlertDialog mReportPopupMenu;

	private View mIncidentFrameLayout;
	private View mRoomFrameLayout;
	
	private String[] incidentArray;
	private String[] collabroomArray;
	private LinearLayout mIncidentFrameButtonLayout;
	private LinearLayout mRoomFrameButtonLayout;

	List<String> activeReports = new ArrayList<String>();
	Resources resources;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mMainActivity = (MainActivity) getActivity();
		mDataManager = DataManager.getInstance(mMainActivity);
		resources = mMainActivity.getResources();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		mRootView = inflater.inflate(R.layout.fragment_overview, container, false);

		mDialogBuilder = new AlertDialog.Builder(mMainActivity);
		
		mJoinIncidentButton = (Button) mRootView.findViewById(R.id.joinIncidentButton);
		mJoinRoomButton = (Button) mRootView.findViewById(R.id.joinRoomButton);
		
		mJoinRoomButtonLayout = (FrameLayout) mRootView.findViewById(R.id.joinRoomButtonLayout);
		
		mGeneralMessageButton = (ImageButton) mRootView.findViewById(R.id.generalMessageButton);
		mReportButton = (ImageButton) mRootView.findViewById(R.id.reportsButton);
		mChatButton = (ImageButton) mRootView.findViewById(R.id.chatButton);
		mMapButton = (ImageButton) mRootView.findViewById(R.id.mapButton);
		mChatButtonLayout = (LinearLayout) mRootView.findViewById(R.id.chatButtonLayout);
		
		mIncidentFrameButtonLayout = (LinearLayout) mRootView.findViewById(R.id.incidentFrameButtonLayout);
		mRoomFrameButtonLayout = (LinearLayout) mRootView.findViewById(R.id.roomFrameButtonLayout);
		
		mJoinIncidentButton.setOnClickListener(showIncidentPopupMenu);
		mJoinRoomButton.setOnClickListener(showRoomPopupMenu);
		
		mReportButton.setOnClickListener(showReportPopupMenu);
		mGeneralMessageButton.setOnClickListener(clickNavigationListener);
		mChatButton.setOnClickListener(clickNavigationListener);
		mMapButton.setOnClickListener(clickNavigationListener);

		mIncidentFrameLayout = mRootView.findViewById(R.id.incidentFrameLayoutBorder);
		mRoomFrameLayout = mRootView.findViewById(R.id.roomFrameLayoutBorder);
		
		setupActiveReportsFromOrgProfile();
		if(mDataManager.getOrgCapabilities().getChat() == false){
			mChatButton.setClickable(false);
//			mChatButtonLayout.setVisibility(View.INVISIBLE);
			mChatButtonLayout.setAlpha(0.3f);
		}
		
		if(!mDataManager.getActiveIncidentName().equals(getString(R.string.no_selection))) {
			mJoinIncidentButton.setText(getString(R.string.incident_active, mDataManager.getActiveIncidentName()));
		} else {
			mJoinIncidentButton.setText(getString(R.string.incident_join));
		}

		IncidentPayload activeIncident = null;
		
		if(mDataManager.getActiveIncidentName().equals(getString(R.string.no_selection))) {
//			mIncidentFrameButtonLayout.setVisibility(View.INVISIBLE);
//			mRoomFrameLayout.setVisibility(View.INVISIBLE);
//			mRoomFrameButtonLayout.setVisibility(View.VISIBLE);
			mIncidentFrameButtonLayout.setAlpha(0.3f);
			mRoomFrameLayout.setAlpha(0.3f);
			mRoomFrameButtonLayout.setAlpha(1f);
			
			
			mGeneralMessageButton.setClickable(false);
			mReportButton.setClickable(false);
			
			mChatButton.setClickable(false);
//			mChatButtonLayout.setVisibility(View.INVISIBLE);
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
					mDataManager.requestCatanRequestRepeating(mDataManager.getIncidentDataRate(), true);
					mDataManager.requestUxoReportRepeating(mDataManager.getIncidentDataRate(), true);
					
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
					mDataManager.setSelectedCollabRoom(null, -1);

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
			
			boolean noRoomSelected = mDataManager.getSelectedCollabRoomName().equals(getString(R.string.no_selection));
			mJoinRoomButton.setText(getString(R.string.room_join));
			
			if(noRoomSelected) {
				mMapButton.setClickable(true);
				mChatButton.setClickable(false);
//				mChatButtonLayout.setVisibility(View.INVISIBLE);
//				mRoomFrameLayout.setVisibility(View.INVISIBLE);
//				mRoomFrameButtonLayout.setVisibility(View.VISIBLE);
				
				mChatButtonLayout.setAlpha(0.3f);
				mRoomFrameLayout.setAlpha(0.3f);
				mRoomFrameButtonLayout.setAlpha(1f);
				
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
				mJoinRoomButton.setText(getString(R.string.room_active, mDataManager.getSelectedCollabRoomName().replace(mDataManager.getActiveIncidentName() + "-", "")));
				mMapButton.setClickable(true);
				
				if(mDataManager.getOrgCapabilities().getChat()){
					mChatButton.setClickable(true);
//					mChatButtonLayout.setVisibility(View.VISIBLE);
					mChatButtonLayout.setAlpha(1f);
				}
//				mRoomFrameLayout.setVisibility(View.VISIBLE);
//				mRoomFrameButtonLayout.setVisibility(View.VISIBLE);
				mRoomFrameLayout.setAlpha(1f);
				mRoomFrameButtonLayout.setAlpha(1f);
				
				
				mDataManager.requestMarkupRepeating(mDataManager.getCollabroomDataRate(), true);
				mDataManager.requestChatMessagesRepeating(mDataManager.getCollabroomDataRate(), true);
			}
		} else {
//			mJoinRoomButtonLayout.setVisibility(View.INVISIBLE);
//			mJoinRoomButton.setVisibility(View.INVISIBLE);
//			mIncidentFrameLayout.setVisibility(View.INVISIBLE);
//			mRoomFrameLayout.setVisibility(View.INVISIBLE);
//			mRoomFrameButtonLayout.setVisibility(View.VISIBLE);
			
			mJoinRoomButton.setClickable(false);
			mJoinRoomButtonLayout.setAlpha(0.3f);
//			mJoinRoomButton.setAlpha(0.3f);
			mIncidentFrameLayout.setAlpha(0.0f);
			mRoomFrameLayout.setAlpha(0.0f);
			mRoomFrameButtonLayout.setAlpha(1f);
			
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
		if(orgCapabilities.getUxoReportForm()){activeReports.add( resources.getString(R.string.UXOREPORT));}
		if(orgCapabilities.getCatanRequestForm()){activeReports.add( resources.getString(R.string.CATANREQUEST));}
	}
	
    @Override
	public void onResume() {
        super.onResume();
    
		RestClient.getAllIncidents(mDataManager.getUserId());
		
        mDataManager.sendChatMessages();
        mDataManager.sendFieldReports();
        mDataManager.sendDamageReports();
        mDataManager.sendMarkupFeatures();
        mDataManager.sendResourceRequests();
        mDataManager.sendSimpleReports();
        mDataManager.sendUxoReports();
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
		
		((ViewGroup)mRootView.getParent()).removeView(mRootView);
	}
	
	OnClickListener clickNavigationListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int navigationId = NavigationOptions.OVERVIEW.getValue();
			int viewId = v.getId();
			
			if(viewId == R.id.generalMessageButton) {
				navigationId = NavigationOptions.GENERALMESSAGE.getValue();
			} else if(viewId == R.id.chatButton) {
				navigationId = NavigationOptions.CHATLOG.getValue();
			} else if(viewId == R.id.mapButton) {
				navigationId = NavigationOptions.MAPCOLLABORATION.getValue();
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
//			mIncidentFrameLayout.setVisibility(View.VISIBLE);
//			mIncidentFrameButtonLayout.setVisibility(View.VISIBLE);
//			mJoinRoomButtonLayout.setVisibility(View.VISIBLE);
//			mJoinRoomButton.setVisibility(View.VISIBLE);
			
			mIncidentFrameLayout.setAlpha(1f);
			mIncidentFrameButtonLayout.setAlpha(1f);
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
			mDataManager.setSelectedCollabRoom(getString(R.string.no_selection), -1);

			mDataManager.stopPollingAssignment();
			mDataManager.stopPollingChat();
			mDataManager.stopPollingMarkup();
			mDataManager.requestFieldReportRepeating(mDataManager.getIncidentDataRate(), true);
			mDataManager.requestDamageReportRepeating(mDataManager.getIncidentDataRate(), true);
			mDataManager.requestSimpleReportRepeating(mDataManager.getIncidentDataRate(), true);
			mDataManager.requestResourceRequestRepeating(mDataManager.getIncidentDataRate(), true);
			mDataManager.requestUxoReportRepeating(mDataManager.getIncidentDataRate(), true);
			mDataManager.requestCatanRequestRepeating(mDataManager.getIncidentDataRate(), true);
			
			mJoinIncidentButton.setText(getString(R.string.incident_active, mDataManager.getActiveIncidentName()));
			mJoinRoomButton.setText(getString(R.string.room_join));
			
			mMapButton.setClickable(true);
			mChatButton.setClickable(false);
//			mChatButtonLayout.setVisibility(View.INVISIBLE);
//			mRoomFrameButtonLayout.setVisibility(View.VISIBLE);
//			mRoomFrameLayout.setVisibility(View.INVISIBLE);
			
			mChatButtonLayout.setAlpha(0.3f);
			mRoomFrameButtonLayout.setAlpha(1f);
			mRoomFrameLayout.setAlpha(0.3f);
		}

	};
	
	OnClickListener showReportPopupMenu = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mDialogBuilder.setTitle(R.string.select_report_type);
			mDialogBuilder.setMessage(null);
		    mDialogBuilder.setPositiveButton(null, null);
		    
		    for(int i = 0; i < activeReports.size(); i++) {
		    	activeReports.set(i, activeReports.get(i).replace(mDataManager.getActiveIncidentName() + "-", ""));
		    }
		    
		    if(activeReports.size() == 0)
		    {
		    	activeReports.add("Reports are not available for your organization");
		    }

			mDialogBuilder.setItems(activeReports.toArray(new String[activeReports.size()]), reportSelected);
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
			else if(activeReports.get(which).equals( resources.getString(R.string.UXOREPORT)) ){
				mMainActivity.onNavigationItemSelected(NavigationOptions.UXOREPORT.getValue(), -1);
			}
			else if(activeReports.get(which).equals( resources.getString(R.string.CATANREQUEST)) ){
				mMainActivity.onNavigationItemSelected(NavigationOptions.CATANREQUEST.getValue(), -1);
			}
		}
	};
	
	OnClickListener showRoomPopupMenu = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mDialogBuilder.setTitle(R.string.select_a_room);
			mDialogBuilder.setMessage(null);
		    mDialogBuilder.setPositiveButton(null, null);
		    HashMap<String, Long> collabRoomsMap = mDataManager.getCollabRoomNamesList();	//not getting collabrooms
		    collabroomArray = new String[collabRoomsMap.size()];
		    collabRoomsMap.keySet().toArray(collabroomArray); 
		    Arrays.sort(collabroomArray);
		    
		    for(int i = 0; i < collabroomArray.length; i++) {
		    	
		    	collabroomArray[i] = collabroomArray[i].replace(mDataManager.getActiveIncidentName() + "-", "");
		    }

		    if(collabroomArray.length > 0) {
		    	mDialogBuilder.setMessage(null);
		    	mDialogBuilder.setItems(collabroomArray, roomSelected);
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

			CollabroomPayload currentRoom = mDataManager.getCollabRoomList().get(mDataManager.getCollabRoomNamesList().get(collabroomArray[which]));

			mDataManager.stopPollingChat();
			mDataManager.stopPollingMarkup();
			
			mDataManager.setSelectedCollabRoom(currentRoom.getName(), currentRoom.getCollabRoomId());
			
			
			mDataManager.stopPollingAssignment();
			mDataManager.requestMarkupRepeating(mDataManager.getCollabroomDataRate(), true);

			mJoinRoomButton.setText(getString(R.string.room_active, mDataManager.getSelectedCollabRoomName().replace(mDataManager.getActiveIncidentName() + "-", "")));

//			mRoomFrameLayout.setVisibility(View.VISIBLE);
//			mRoomFrameButtonLayout.setVisibility(View.VISIBLE);
			mRoomFrameLayout.setAlpha(1.0f);
			mRoomFrameButtonLayout.setAlpha(1.0f);
			
			
			mMapButton.setClickable(true);
			
			if(mDataManager.getOrgCapabilities().getChat()){
				mChatButton.setClickable(true);
//				mChatButtonLayout.setVisibility(View.VISIBLE);
				mChatButtonLayout.setAlpha(1.0f);
			}
			
//	        Intent intent = new Intent();
//	        intent.setAction(Intents.PHINICS_COLLABROOM_SWITCHED);
//	        mDataManager.getContext().sendBroadcast(intent);
			
		}
	};
}
