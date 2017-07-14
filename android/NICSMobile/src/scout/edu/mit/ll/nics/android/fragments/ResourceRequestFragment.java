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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.data.DamageReportFormData;
import scout.edu.mit.ll.nics.android.api.data.ResourceRequestData;
import scout.edu.mit.ll.nics.android.api.data.ResourceRequestFormData;
import scout.edu.mit.ll.nics.android.api.payload.forms.ResourceRequestPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;
import scout.edu.mit.ll.nics.android.utils.Constants.NavigationOptions;
import scout.edu.mit.ll.nics.android.utils.FormType;
import scout.edu.mit.ll.nics.android.utils.Intents;

public class ResourceRequestFragment extends Fragment {

	private FormFragment mFormFragment;
	private LinearLayout mFormButtons;
	private Button mSaveDraftButton;
	private Button mSubmitButton;
	private Button mClearAllButton;

	private View mRootView;
	private DataManager mDataManager;
	private boolean isDraft = true;
	private long mReportId;
	
	private ResourceRequestPayload mCurrentPayload;
	private ResourceRequestData mCurrentData;
	private MainActivity mContext;

	private boolean receiverRegistered = false;
	private IntentFilter mIncidentSwitchedReceiverFilter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = (MainActivity) getActivity();
		
		mReportId = -1;
		isDraft  = true;
		
		mIncidentSwitchedReceiverFilter = new IntentFilter(Intents.nics_INCIDENT_SWITCHED);
		
		if (!receiverRegistered) {
			mContext.registerReceiver(incidentChangedReceiver, mIncidentSwitchedReceiverFilter);
			receiverRegistered = true;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		mFormFragment = (FormFragment) getFragmentManager().findFragmentById(R.id.resourceRequestFragment);

		if (mFormFragment == null) {
			int id = -1;
			if (savedInstanceState != null) {
				id = savedInstanceState.getInt("fragmentId", -1);
			}

			if (id == -1) {
				mRootView = inflater.inflate(R.layout.fragment_resourcerequest, container, false);
			} else {
				mFormFragment = (FormFragment) getFragmentManager().findFragmentById(id);

				if (mFormFragment != null) {
					mRootView = container.findViewById(R.layout.fragment_resourcerequest);
				}
			}

			mSaveDraftButton = (Button) mRootView.findViewById(R.id.resourceRequestSaveButton);
			mSubmitButton = (Button) mRootView.findViewById(R.id.resourceRequestSubmitButton);
			mClearAllButton = (Button) mRootView.findViewById(R.id.resourceRequestClearButton);
			
			mFormButtons = (LinearLayout) mRootView.findViewById(R.id.resourceRequestButtons);

			mSaveDraftButton.setOnClickListener(onActionButtonClick);
			mSubmitButton.setOnClickListener(onActionButtonClick);
			mClearAllButton.setOnClickListener(onActionButtonClick);
			mDataManager = DataManager.getInstance(getActivity());
		}

		return mRootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mFormFragment == null) {
			mFormFragment = (FormFragment) getFragmentManager().findFragmentById(R.id.resourceRequestFragment);
		}
		
		if (!receiverRegistered) {
			mContext.registerReceiver(incidentChangedReceiver, mIncidentSwitchedReceiverFilter);
			receiverRegistered = true;
		}
	}

	public void populate(String incidentInfoJson, long id, boolean editable) {
		if (mFormFragment == null) {
			mFormFragment = (FormFragment) getFragmentManager().findFragmentById(R.id.resourceRequestFragment);
		}
		
		mFormFragment.populate(incidentInfoJson, editable);
		mReportId = id;
		
		if(!editable) {
			mFormButtons.setVisibility(View.GONE);
		} else {
			mFormButtons.setVisibility(View.VISIBLE);
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
			ResourceRequestPayload payload;
			ResourceRequestData messageData;
			long currentTime = System.currentTimeMillis ();
			
			switch (v.getId()) {
				case R.id.resourceRequestSaveButton:
					isDraft = true;
					if(isDraft) {
						mDataManager.deleteResourceRequestStoreAndForward(mReportId);
					}
					
					messageData = new ResourceRequestData((new Gson().fromJson(mFormFragment.save().toString(), ResourceRequestFormData.class)));
					messageData.setUser(mDataManager.getUsername());
					messageData.setUserFull(mDataManager.getUserNickname());
					
					payload = new ResourceRequestPayload();
					payload.setId(mReportId);
					payload.setDraft(isDraft);
					payload.setIncidentId(mDataManager.getActiveIncidentId());
					payload.setIncidentName(mDataManager.getActiveIncidentName());
					payload.setFormTypeId(FormType.RESREQ.ordinal());
//					payload.setSenderUserId(mDataManager.getUserId());
					payload.setUserSessionId(mDataManager.getUserSessionId());
					payload.setMessageData(messageData);
//					payload.setCreatedUTC(currentTime);
//					payload.setLastUpdatedUTC(currentTime);
					payload.setSeqTime(currentTime);
					
					mDataManager.addResourceRequestToStoreAndForward(payload);

					mContext.onNavigationItemSelected(NavigationOptions.RESOURCEREQUEST.getValue(), -2);
					break;
	
				case R.id.resourceRequestSubmitButton:
					if(isDraft) {
						mDataManager.deleteResourceRequestStoreAndForward(mReportId);
						isDraft = false;
					}
					messageData = new ResourceRequestData((new Gson().fromJson(mFormFragment.save().toString(), ResourceRequestFormData.class)));
					messageData.setUser(mDataManager.getUsername());
					messageData.setUserFull(mDataManager.getUserNickname());
					
					payload = new ResourceRequestPayload();
					payload.setId(mReportId);
					payload.setDraft(isDraft);
					payload.setIncidentId(mDataManager.getActiveIncidentId());
					payload.setIncidentName(mDataManager.getActiveIncidentName());
					payload.setFormTypeId(FormType.RESREQ.ordinal());
//					payload.setSenderUserId(mDataManager.getUserId());
					payload.setUserSessionId(mDataManager.getUserSessionId());
					payload.setMessageData(messageData);
//					payload.setCreatedUTC(currentTime);
//					payload.setLastUpdatedUTC(currentTime);
					payload.setSeqTime(currentTime);
					
					mDataManager.addResourceRequestToStoreAndForward(payload);
					
					mDataManager.sendResourceRequests();

					mContext.onNavigationItemSelected(NavigationOptions.RESOURCEREQUEST.getValue(), -2);
	
				case R.id.resourceRequestClearButton:
					mFormFragment.populate("", true);
					break;
			}
		}
	};

	public long getReportId() {
		return mReportId;
	}

	public String getFormString() {
		return new ResourceRequestFormData(mCurrentData).toJsonString();
	}
	
	public void setPayload(ResourceRequestPayload resourceRequestPayload, boolean editable) {
		this.isDraft = resourceRequestPayload.isDraft();
		mReportId = resourceRequestPayload.getId();
		
		resourceRequestPayload.parse();
		ResourceRequestData data = resourceRequestPayload.getMessageData();
		
		mCurrentPayload = resourceRequestPayload;
		mCurrentData = data;
		
		populate(new ResourceRequestFormData(data).toJsonString(), mReportId, editable);
	}

	public ResourceRequestPayload getPayload() {
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
			mCurrentData = new ResourceRequestData(new Gson().fromJson(mFormFragment.save().toString(), ResourceRequestFormData.class));
			
			mCurrentPayload.setMessageData(mCurrentData);
		} else {
			mCurrentPayload = new ResourceRequestPayload();
			mCurrentData = new ResourceRequestData();
			mCurrentPayload.setMessageData(mCurrentData);
		}
		
		return mCurrentPayload;
	}
	
	private BroadcastReceiver incidentChangedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			ResourceRequestPayload payload = mDataManager.getLastResourceRequestPayload();
			if(payload != null){
				setPayload(payload,payload.isDraft());
			}else{
				mContext.addResourceRequestToDetailView(false);
			}
		}
	};

}
