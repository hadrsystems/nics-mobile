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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import edu.mit.ll.phinics.android.MainActivity;
import edu.mit.ll.phinics.android.R;
import edu.mit.ll.phinics.android.api.DataManager;
import edu.mit.ll.phinics.android.api.data.CatanRequestData;
import edu.mit.ll.phinics.android.api.data.CatanRequestFormData;
import edu.mit.ll.phinics.android.api.payload.forms.CatanRequestPayload;
import edu.mit.ll.phinics.android.utils.Constants.NavigationOptions;
import edu.mit.ll.phinics.android.utils.FormType;

public class CatanRequestFragment extends Fragment {

	private FormFragment mFormFragment;
	private LinearLayout mFormButtons;
	private Button mSaveDraftButton;
	private Button mSubmitButton;
	private Button mClearAllButton;

	private View mRootView;
	private DataManager mDataManager;
	private boolean isDraft = true;
	private long mReportId;
	
	private CatanRequestPayload mCurrentPayload;
	private CatanRequestData mCurrentData;
	private Menu mMenu;
	private boolean mHideCopy;
	private MainActivity mContext;
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = (MainActivity) getActivity();
		
		mReportId = -1;
		isDraft  = true;
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		mFormFragment = (FormFragment) getFragmentManager().findFragmentById(R.id.catanRequestFragment);

		if (mFormFragment == null) {
			int id = -1;
			if (savedInstanceState != null) {
				id = savedInstanceState.getInt("fragmentId", -1);
			}

			if (id == -1) {
				mRootView = inflater.inflate(R.layout.fragment_catanrequest, container, false);
			} else {
				mFormFragment = (FormFragment) getFragmentManager().findFragmentById(id);

				if (mFormFragment != null) {
					mRootView = container.findViewById(R.layout.fragment_catanrequest);
				}
			}
		} else if(mRootView == null) {
			mRootView = container.findViewById(R.layout.fragment_catanrequest);
		}
		
		mSaveDraftButton = (Button) mRootView.findViewById(R.id.catanRequestSaveButton);
		mSubmitButton = (Button) mRootView.findViewById(R.id.catanRequestSubmitButton);
		mClearAllButton = (Button) mRootView.findViewById(R.id.catanRequestClearButton);
		
		mFormButtons = (LinearLayout) mRootView.findViewById(R.id.catanRequestButtons);

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
			mFormFragment = (FormFragment) getFragmentManager().findFragmentById(R.id.catanRequestFragment);
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
			mFormFragment = (FormFragment) getFragmentManager().findFragmentById(R.id.catanRequestFragment);
		}
		
		
//		if(id >= 0){	//new messages have a default id of -1
//		((MainActivity) mContext).mUnreadMessageManager.AddMessageToList(id);
//		}
		
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
	}
	
	public void hideCopy(boolean hide) {
		mHideCopy = hide;
		getActivity().supportInvalidateOptionsMenu();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.catanrequest_copy, menu);
		
		mMenu = menu;
		
		MenuItem item = mMenu.findItem(R.id.copyCatanRequestOption);
		if(mHideCopy) {
			item.setVisible(false);
		} else {
			item.setVisible(true);
		}
		
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		((ViewGroup) mRootView.getParent()).removeView(mRootView);
	}

	private OnClickListener onActionButtonClick = new OnClickListener() {

		private AlertDialog mAlertDialog;

		@Override
		public void onClick(View v) {
			CatanRequestPayload payload;
			CatanRequestData messageData;
	    	long currentTime = System.currentTimeMillis ();
			
	    	switch (v.getId()) {
				case R.id.catanRequestSaveButton:
					isDraft = true;
					
					if(isDraft) {
						mDataManager.deleteCatanRequestStoreAndForward(mReportId);
					}
					
					messageData = new CatanRequestData((new Gson().fromJson(mFormFragment.save().toString(), CatanRequestFormData.class)));
					messageData.setUser(mDataManager.getUsername());
					
					payload = new CatanRequestPayload();
					
					payload.setId(mReportId);
					payload.setDraft(isDraft);
					payload.setIncidentId(mDataManager.getActiveIncidentId());
					payload.setIncidentName(mDataManager.getActiveIncidentName());
					payload.setFormTypeId(FormType.DR.ordinal());
//					payload.setSenderUserId(mDataManager.getUserId());
					payload.setUserSessionId(mDataManager.getUserSessionId());
					payload.setMessageData(messageData);
//					payload.setCreatedUTC(currentTime);
//					payload.setLastUpdatedUTC(currentTime);
					payload.setSeqTime(currentTime);
					
					mDataManager.addCatanRequestToStoreAndForward(payload);

					mContext.onNavigationItemSelected(NavigationOptions.GENERALMESSAGE.getValue(), -2);
					break;
	
				case R.id.generalMessageSubmitButton:						
					messageData = new CatanRequestData((new Gson().fromJson(mFormFragment.save().toString(), CatanRequestFormData.class)));
					messageData.setUser(mDataManager.getUsername());
					
//					if(messageData.getFullpath() != null && !messageData.getFullpath().isEmpty()) {
						if(isDraft) {
							mDataManager.deleteCatanRequestStoreAndForward(mReportId);
							isDraft = false;
						}
						
						payload = new CatanRequestPayload();
	
						payload.setId(mReportId);
						payload.setDraft(isDraft);
						payload.setIncidentId(mDataManager.getActiveIncidentId());
						payload.setIncidentName(mDataManager.getActiveIncidentName());
						payload.setFormTypeId(FormType.DR.ordinal());
//						payload.setSenderUserId(mDataManager.getUserId());
						payload.setUserSessionId(mDataManager.getUserSessionId());
						payload.setMessageData(messageData);
//						payload.setCreatedUTC(currentTime);
//						payload.setLastUpdatedUTC(currentTime);
						payload.setSeqTime(currentTime);
						
						mDataManager.addCatanRequestToStoreAndForward(payload);
						
						mDataManager.sendSimpleReports();
	
						mContext.onNavigationItemSelected(NavigationOptions.CATANREQUEST.getValue(), -2);
//					} else {
//						AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//						
//						builder.setMessage(R.string.missing_image_capture_description);
//						builder.setTitle(R.string.missing_image_capture_title);
//						builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//						   public void onClick(DialogInterface dialog, int id) {
//						   }
//						});
//						
//						mAlertDialog = builder.create();
//	                    mAlertDialog.show();
//					}
	
				case R.id.generalMessageClearButton:
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

	public void setPayload(CatanRequestPayload catanRequestPayload, boolean editable) {
		this.isDraft = catanRequestPayload.isDraft();
		mReportId = catanRequestPayload.getId();
		
		catanRequestPayload.parse();
		CatanRequestData data = catanRequestPayload.getMessageData();
		
		mCurrentPayload = catanRequestPayload;
		mCurrentData = data;
		
		populate(new CatanRequestFormData(data).toJsonString(), mReportId, editable);
	}
	
	public String getFormString() {
		return new CatanRequestFormData(mCurrentData).toJsonString();
	}

	public CatanRequestPayload getPayload() {
    	long currentTime = System.currentTimeMillis ();
    	
		if(mCurrentPayload != null) {
			mCurrentPayload.setId(mReportId);
			mCurrentPayload.setDraft(isDraft);
//			mCurrentPayload.setSenderUserId(mDataManager.getUserId());
			mCurrentPayload.setUserSessionId(mDataManager.getUserSessionId());
			mCurrentPayload.setSeqTime(currentTime);
//			mCurrentPayload.setCreatedUTC(currentTime);
//			mCurrentPayload.setLastUpdatedUTC(currentTime);
			
			mCurrentData = new CatanRequestData(new Gson().fromJson(mFormFragment.save().toString(), CatanRequestFormData.class));
			mCurrentData.setUser(mDataManager.getUsername());
			
			mCurrentPayload.setMessageData(mCurrentData);
		} else {
			mCurrentPayload = new CatanRequestPayload();
			mCurrentData = new CatanRequestData();
			mCurrentPayload.setMessageData(mCurrentData);
		}
		
		return mCurrentPayload;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		mFormFragment.onActivityResult(requestCode, resultCode, data);
	}

}
