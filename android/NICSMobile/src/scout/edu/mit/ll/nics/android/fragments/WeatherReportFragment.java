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

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.data.WeatherReportData;
import scout.edu.mit.ll.nics.android.api.data.WeatherReportFormData;
import scout.edu.mit.ll.nics.android.api.payload.forms.WeatherReportPayload;
import scout.edu.mit.ll.nics.android.utils.Constants.NavigationOptions;
import scout.edu.mit.ll.nics.android.utils.FormType;
import scout.edu.mit.ll.nics.android.utils.Intents;

public class WeatherReportFragment extends Fragment {

	private FormFragment mFormFragment;
	private LinearLayout mFormButtons;
	private Button mSaveDraftButton;
	private Button mSubmitButton;
	private Button mClearAllButton;

	private EditText mTimeTakenInput;
	
	private View mRootView;
	private DataManager mDataManager;
	private boolean isDraft = true;
	private long mReportId;
	
	private WeatherReportPayload mCurrentPayload;
	private WeatherReportData mCurrentData;
	private Menu mMenu;
	private boolean mHideCopy;
	private MainActivity mContext;
	
	private boolean receiverRegistered = false;
	private IntentFilter mIncidentSwitchedReceiverFilter;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = (MainActivity) getActivity();
		
		mReportId = -1;
		isDraft  = true;
		setHasOptionsMenu(true);
		
		mIncidentSwitchedReceiverFilter = new IntentFilter(Intents.nics_INCIDENT_SWITCHED);
		
		if (!receiverRegistered) {
			mContext.registerReceiver(incidentChangedReceiver, mIncidentSwitchedReceiverFilter);
			receiverRegistered = true;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		mFormFragment = (FormFragment) getFragmentManager().findFragmentById(R.id.weatherReportFragment);
		
		if (mFormFragment == null) {
			int id = -1;
			if (savedInstanceState != null) {
				id = savedInstanceState.getInt("fragmentId", -1);
			}

			if (id == -1) {
				mRootView = inflater.inflate(R.layout.fragment_weatherreport, container, false);
			} else {
				mFormFragment = (FormFragment) getFragmentManager().findFragmentById(id);

				if (mFormFragment != null) {
					mRootView = container.findViewById(R.layout.fragment_weatherreport);
				}
			}
		} else if(mRootView == null) {
			mRootView = container.findViewById(R.layout.fragment_weatherreport);
		}
		
		mSaveDraftButton = (Button) mRootView.findViewById(R.id.weatherReportSaveButton);
		mSubmitButton = (Button) mRootView.findViewById(R.id.weatherReportSubmitButton);
		mClearAllButton = (Button) mRootView.findViewById(R.id.weatherReportClearButton);
		
		mFormButtons = (LinearLayout) mRootView.findViewById(R.id.weatherReportButtons);

		mSaveDraftButton.setOnClickListener(onActionButtonClick);
		mSubmitButton.setOnClickListener(onActionButtonClick);
		mClearAllButton.setOnClickListener(onActionButtonClick);
		mDataManager = DataManager.getInstance(getActivity());

		return mRootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mFormFragment == null) {
			mFormFragment = (FormFragment) getFragmentManager().findFragmentById(R.id.weatherReportFragment);
		}
		
		if (!receiverRegistered) {
			mContext.registerReceiver(incidentChangedReceiver, mIncidentSwitchedReceiverFilter);
			receiverRegistered = true;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the ID of the existing MapFragment so it can properly be restored when app resumes
		if (mFormFragment != null) {
			outState.putInt("fragmentId", mFormFragment.getId());
		}
	}

	public void removeAssignmentFragment() {
		if (mFormFragment != null) {
			getFragmentManager().beginTransaction().remove(mFormFragment).commit();
			mFormFragment = null;
		}
	}

	public void populate(String incidentInfoJson, long id, boolean editable) {
		if (mFormFragment == null) {
			mFormFragment = (FormFragment) getFragmentManager().findFragmentById(R.id.weatherReportFragment);
		}
		
		mFormFragment.populate(incidentInfoJson, editable);
		mReportId = id;

		if(!editable) {
			mFormButtons.setVisibility(View.GONE);
			mHideCopy = false;
			getActivity().supportInvalidateOptionsMenu();

		} else {
			mFormButtons.setVisibility(View.VISIBLE);
			mHideCopy = true;
			getActivity().supportInvalidateOptionsMenu();
		}
		
		
		//set the time editText field to the current system time if there is no time already entered (reopening a report would have a time saved)
		mTimeTakenInput = (EditText) mRootView.findViewWithTag("timetaken");
		if(mTimeTakenInput.getText().length() <= 1)
		{
			String delegate = "hh:mm aaa"; 
	        mTimeTakenInput.setText((String) DateFormat.format(delegate,Calendar.getInstance().getTime()));
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.weatherreport_copy, menu);
		
		mMenu = menu;
		
		MenuItem item = mMenu.findItem(R.id.copyWeatherReportOption);
		if(mHideCopy) {
			item.setVisible(false);
		} else {
			item.setVisible(true);
		}
		
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if (receiverRegistered) {
			mContext.unregisterReceiver(incidentChangedReceiver);
			receiverRegistered = false;
		}
		
		((ViewGroup) mRootView.getParent()).removeView(mRootView);
	}

	private OnClickListener onActionButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			WeatherReportPayload payload;
			WeatherReportData messageData;
	    	long currentTime = System.currentTimeMillis ();
			
	    	switch (v.getId()) {
				case R.id.weatherReportSaveButton:
					isDraft = true;
					
					if(isDraft) {
						mDataManager.deleteWeatherReportStoreAndForward(mReportId);
					}
					
					messageData = new WeatherReportData((new Gson().fromJson(mFormFragment.save().toString(), WeatherReportFormData.class)));
					messageData.setUser(mDataManager.getUsername());
					messageData.setUserFull(mDataManager.getUserNickname());
					
					payload = new WeatherReportPayload();
					
					payload.setId(mReportId);
					payload.setDraft(isDraft);
					payload.setIncidentId(mDataManager.getActiveIncidentId());
					payload.setIncidentName(mDataManager.getActiveIncidentName());
					payload.setFormTypeId(FormType.WR.ordinal());
//					payload.setSenderUserId(mDataManager.getUserId());
					payload.setUserSessionId(mDataManager.getUserSessionId());
					payload.setMessageData(messageData);
//					payload.setCreatedUTC(currentTime);
//					payload.setLastUpdatedUTC(currentTime);
					payload.setSeqTime(currentTime);
					
					mDataManager.addWeatherReportToStoreAndForward(payload);

					mContext.onNavigationItemSelected(NavigationOptions.WEATHERREPORT.getValue(), -2);
					break;
	
				case R.id.weatherReportSubmitButton:
					if(isDraft) {
						mDataManager.deleteWeatherReportStoreAndForward(mReportId);
						isDraft = false;
					}
					
					messageData = new WeatherReportData((new Gson().fromJson(mFormFragment.save().toString(), WeatherReportFormData.class)));
					messageData.setUser(mDataManager.getUsername());
					messageData.setUserFull(mDataManager.getUserNickname());
//					messageData.setTransactionId(UUID.randomUUID().toString());
					
					if(messageData.getLatitude() == null){
						messageData.setLatitude("0");
					}
					if(messageData.getLongitude() == null){
						messageData.setLongitude("0");
					}
					
					messageData.setStatus("Open");
					
					payload = new WeatherReportPayload();
					
					payload.setId(mReportId);
					payload.setDraft(isDraft);
					payload.setIncidentId(mDataManager.getActiveIncidentId());
					payload.setIncidentName(mDataManager.getActiveIncidentName());
					payload.setFormTypeId(FormType.WR.ordinal());
//					payload.setSenderUserId(mDataManager.getUserId());
					payload.setUserSessionId(mDataManager.getUserSessionId());
					payload.setMessageData(messageData);
//					payload.setCreatedUTC(currentTime);
//					payload.setLastUpdatedUTC(currentTime);
					payload.setSeqTime(currentTime);
					
					mDataManager.addWeatherReportToStoreAndForward(payload);
					
					mDataManager.sendWeatherReports();

					mContext.onNavigationItemSelected(NavigationOptions.WEATHERREPORT.getValue(), -2);
	
				case R.id.weatherReportClearButton:
					mFormFragment.populate("", true);
					break;
			}
		}
	};

	public String toJson() {
		return mFormFragment.save().toString();
	}

	public long getReportId() {
		return mReportId;
	}

	public void setPayload(WeatherReportPayload weatherReportPayload, boolean editable) {
		this.isDraft = weatherReportPayload.isDraft();
		mReportId = weatherReportPayload.getId();
		
		weatherReportPayload.parse();
		WeatherReportData data = weatherReportPayload.getMessageData();
		
		mCurrentPayload = weatherReportPayload;
		mCurrentData = data;
		
		populate(new WeatherReportFormData(data).toJsonString(), mReportId, editable);
	}
	
	public String getFormString() {
		return new WeatherReportFormData(mCurrentData).toJsonString();
	}

	public WeatherReportPayload getPayload() {
    	long currentTime = System.currentTimeMillis ();
    	
		if(mCurrentPayload != null) {
			mCurrentPayload.setId(mReportId);
			mCurrentPayload.setDraft(isDraft);
//			mCurrentPayload.setSenderUserId(mDataManager.getUserId());
			mCurrentPayload.setUserSessionId(mDataManager.getUserSessionId());
//			mCurrentPayload.setCreatedUTC(currentTime);
//			mCurrentPayload.setLastUpdatedUTC(currentTime);
			mCurrentPayload.setSeqTime(currentTime);
			
			mCurrentData.setUser(mDataManager.getUsername());
			mCurrentData.setUserFull(mDataManager.getUserNickname());
			mCurrentData = new WeatherReportData(new Gson().fromJson(mFormFragment.save().toString(), WeatherReportFormData.class));
			
			mCurrentPayload.setMessageData(mCurrentData);
		} else {
			mCurrentPayload = new WeatherReportPayload();
			mCurrentData = new WeatherReportData();
			mCurrentPayload.setMessageData(mCurrentData);
		}
		
		return mCurrentPayload;
	}

	private BroadcastReceiver incidentChangedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			WeatherReportPayload payload = mDataManager.getLastWeatherReportPayload();
			if(payload != null){
				setPayload(payload,payload.isDraft());
			}else{
				mContext.addWeatherReportToDetailView(false);
			}
		}
	};
	
}
