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
import java.util.Comparator;

import android.R.integer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import edu.mit.ll.phinics.android.R;
import edu.mit.ll.phinics.android.adapters.CatanRequestListAdapter;
import edu.mit.ll.phinics.android.api.data.CatanRequestData;
import edu.mit.ll.phinics.android.api.data.ReportStatus;
import edu.mit.ll.phinics.android.api.payload.forms.CatanRequestPayload;
import edu.mit.ll.phinics.android.utils.Constants;
import edu.mit.ll.phinics.android.utils.EncryptedPreferences;
import edu.mit.ll.phinics.android.utils.Intents;

public class CatanRequestListFragment extends FormListTabbedFragment {
	private Context mContext;
	private EncryptedPreferences settings;
	private CatanRequestListAdapter mCatanRequestListAdapter;
	private boolean CatanRequestReceiverRegistered;
	private IntentFilter mCatanRequestReceiverFilter;
//	private IntentFilter mCatanRequestProgressReceiverFilter;
	private long mLastIncidentId = 0;
	protected boolean mIsFirstLoad = true;
	
	//pulling from database takes time. Storing them here to save on db pulls
	ArrayList<CatanRequestPayload> currentCatanRequestPayloads = new ArrayList<CatanRequestPayload>();
	ArrayList<Integer> countOfEachRequest = new ArrayList<Integer>();
	
	private ImageView aggragateImage1;
	private ImageView aggragateImage2;
	private ImageView aggragateImage3;
	private ImageView aggragateImage4;
	private ImageView aggragateImage5;
	private ImageView aggragateImage6;
	
	private TextView requestCount1;
	private TextView requestCount2;
	private TextView requestCount3;
	private TextView requestCount4;
	private TextView requestCount5;
	private TextView requestCount6;
	
	private int index;
	private int top;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getActivity();
		settings = new EncryptedPreferences( mContext.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE));
		
		mCatanRequestReceiverFilter = new IntentFilter(Intents.PHINICS_NEW_CATAN_REQUEST_RECEIVED);
//		mCatanRequestProgressReceiverFilter = new IntentFilter(Intents.PHINICS_CATAN_REQUEST_PROGRESS);

		if (!CatanRequestReceiverRegistered) {
			mContext.registerReceiver(CatanRequestReceiver, mCatanRequestReceiverFilter);
//			mContext.registerReceiver(CatanRequestProgressReceiver, mCatanRequestProgressReceiverFilter);
			CatanRequestReceiverRegistered = true;
		}

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		aggragateImage1 = (ImageView)mRootView.findViewById(R.id.catanRequestThumbnail1);
		aggragateImage2 = (ImageView)mRootView.findViewById(R.id.catanRequestThumbnail2);
		aggragateImage3 = (ImageView)mRootView.findViewById(R.id.catanRequestThumbnail3);
		aggragateImage4 = (ImageView)mRootView.findViewById(R.id.catanRequestThumbnail4);
		aggragateImage5 = (ImageView)mRootView.findViewById(R.id.catanRequestThumbnail5);
		aggragateImage6 = (ImageView)mRootView.findViewById(R.id.catanRequestThumbnail6);
		
		requestCount1 = (TextView)mRootView.findViewById(R.id.requestCount1);
		requestCount2 = (TextView)mRootView.findViewById(R.id.requestCount2);
		requestCount3 = (TextView)mRootView.findViewById(R.id.requestCount3);
		requestCount4 = (TextView)mRootView.findViewById(R.id.requestCount4);
		requestCount5 = (TextView)mRootView.findViewById(R.id.requestCount5);
		requestCount6 = (TextView)mRootView.findViewById(R.id.requestCount6);
		
		return mRootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

//		inflater.inflate(R.menu.catanrequest, menu);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		view.setPadding(10, 0, 10, 0);

//		this.setEmptyText(mContext.getString(R.string.no_catan_requests_exist));
		super.onViewCreated(view, savedInstanceState);
	}

//	@Override
//	public void onListItemClick(ListView listView, View view, int position, long id) {
//		super.onListItemClick(listView, view, position, id);

//		CatanRequestPayload item = mCatanRequestListAdapter.getItem(position);
//		item.parse();
//		Log.d("Long id: ", String.valueOf(id));
//		((MainActivity) mContext).openCatanRequest(item, item.isDraft());
//	}

	@Override
	public void onResume() {
		super.onResume();
		
		if(mCatanRequestListAdapter == null) {
			mCatanRequestListAdapter = new CatanRequestListAdapter(mContext, R.layout.listitem_catanrequest, R.id.catanRequestTitle, new ArrayList<CatanRequestPayload>());
			mIsFirstLoad = true;
		}
		
		index  = settings.getPreferenceLong("sr_scrollIdx", "-1").intValue();
		top  = settings.getPreferenceLong("sr_scrollTop", "-1").intValue();
		
		settings.removePreference("sr_scrollIdx");
		settings.removePreference("sr_scrollTop");
		
		if(mLastIncidentId != mDataManager.getActiveIncidentId()) {
			mIsFirstLoad = true;
//			setListShown(false);
		}
		
//		mDataManager.sendCatanRequests();
		previousTab = -1;
		updateData();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if (CatanRequestReceiverRegistered) {
			mContext.unregisterReceiver(CatanRequestReceiver);
//			mContext.unregisterReceiver(CatanRequestProgressReceiver);
			CatanRequestReceiverRegistered = false;
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		int index = getListView().getFirstVisiblePosition();
		View v = getListView().getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();

		settings.savePreferenceLong("sr_scrollIdx", (long)index);
		settings.savePreferenceLong("sr_scrollTop", (long)top);
		
	}

	private BroadcastReceiver CatanRequestReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				Bundle bundle = intent.getExtras();
				CatanRequestPayload payload = mBuilder.create().fromJson(bundle.getString("payload"), CatanRequestPayload.class);
				payload.setStatus(ReportStatus.lookUp(bundle.getInt("status", 0)));
				payload.parse();

				CatanRequestData data = payload.getMessageData();

				if (data.getUser().equals(mDataManager.getUsername()) && payload.getSeqTime() >= mDataManager.getLastCatanRequestTimestamp() - 10000) {
					mCatanRequestListAdapter.clear();
					
					currentCatanRequestPayloads = mDataManager.getCatanRequestHistoryForIncident(mDataManager.getActiveIncidentId());	
					mCatanRequestListAdapter.addAll(currentCatanRequestPayloads);
//					mCatanRequestListAdapter.addAll(mDataManager.getAllCatanRequestStoreAndForwardReadyToSend(mDataManager.getActiveIncidentId()));
				} else {
					if(countOfEachRequest.size() <= 5)
					{
						countOfEachRequest.add( 0);
						countOfEachRequest.add( 0);
						countOfEachRequest.add( 0);
						countOfEachRequest.add( 0);
						countOfEachRequest.add( 0);
						countOfEachRequest.add( 0);
					}
					currentCatanRequestPayloads.add(payload);
					double serviceType = data.getCatan_service()[0].getService_type();
					if(getSelectedTab()== serviceType){
						countOfEachRequest.set( (int)data.getCatan_service()[0].getService_subtype(),countOfEachRequest.get( (int)data.getCatan_service()[0].getService_subtype()) +1);
						mCatanRequestListAdapter.add(payload);
						setAggragateData();
					}
					
				}
				mCatanRequestListAdapter.sort(reportComparator);
				mCatanRequestListAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

//	private BroadcastReceiver CatanRequestProgressReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			try {
//				Bundle bundle = intent.getExtras();
//				long reportId = bundle.getLong("reportId");
//				double progress = bundle.getDouble("progress");
//				boolean failed = bundle.getBoolean("failed");
//
//				for (CatanRequestPayload payload : mCatanRequestListAdapter.getItems()) {
//					if (payload.getId() == reportId) {
//						payload.setProgress((int) Math.round(progress));
//						payload.setFailedToSend(failed);
//					}
//				}
//
//				if(mCatanRequestListAdapter != null) {
//					mCatanRequestListAdapter.notifyDataSetChanged();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	};

	protected void updateData() {
		long currentIncidentId;

		currentIncidentId = mDataManager.getActiveIncidentId();
//		mDataManager.requestCatanRequests();
		mCatanRequestListAdapter.clear();
		
		
		if(currentCatanRequestPayloads.size() > 0){
			mCatanRequestListAdapter.addAll(currentCatanRequestPayloads);
		}else{
			currentCatanRequestPayloads = mDataManager.getCatanRequestHistoryForIncident(mDataManager.getActiveIncidentId());	
			mCatanRequestListAdapter.addAll(currentCatanRequestPayloads);
		}
		
//		mCatanRequestListAdapter.addAll(mDataManager.getAllCatanRequestStoreAndForwardReadyToSend(currentIncidentId));
		mCatanRequestListAdapter.sort(reportComparator);
		mCatanRequestListAdapter.notifyDataSetChanged();
		
//		if(mIsFirstLoad) {
			getListView().setAdapter(mCatanRequestListAdapter);
			mLastIncidentId = currentIncidentId;
			mIsFirstLoad = false;
//			setListShown(true);
//		}

		mCatanRequestListAdapter.notifyDataSetChanged();
		if(index != -1 && top != -1) {
			getListView().post(new Runnable() {
				
				@Override
				public void run() {
					getListView().setSelectionFromTop(index, top);
					
					index = -1;
					top = -1;
				}
			});
		}
	}

	private Comparator<? super CatanRequestPayload> reportComparator = new Comparator<CatanRequestPayload>() {

		@Override
		public int compare(CatanRequestPayload lhs, CatanRequestPayload rhs) {
			return (Long.valueOf(rhs.getSeqTime()).compareTo(Long.valueOf(lhs.getSeqTime())));
		}
	};

	@Override
	protected boolean itemIsDraft(int position) {
		CatanRequestPayload item = mCatanRequestListAdapter.getItem(position);
	
		return item.isDraft();
	}

	@Override
	protected boolean handleItemDeletion(int position) {
		CatanRequestPayload item = mCatanRequestListAdapter.getItem(position);
		mCatanRequestListAdapter.remove(item);
		mCatanRequestListAdapter.notifyDataSetChanged();
		
		return mDataManager.deleteCatanRequestStoreAndForward(item.getId());
	}
	
	int previousTab = -1;
	private void setAggragateData(){
		if(previousTab !=selectedTab){
			if(selectedTab == 0){
				aggragateImage1.setImageResource(R.drawable.none);
				aggragateImage2.setImageResource(R.drawable.catan_aid_water);
				aggragateImage3.setImageResource(R.drawable.catan_aid_food);
				aggragateImage4.setImageResource(R.drawable.catan_aid_shelter);
				aggragateImage5.setImageResource(R.drawable.catan_aid_cleanup);
				aggragateImage6.setImageResource(R.drawable.catan_aid_fuel);			
			}else if(selectedTab == 1){
				aggragateImage1.setImageResource(R.drawable.catan_volunteer_translator);
				aggragateImage2.setImageResource(R.drawable.catan_volunteer_guide);
				aggragateImage3.setImageResource(R.drawable.catan_volunteer_laborer);
				aggragateImage4.setImageResource(R.drawable.catan_volunteer_counselor);
				aggragateImage5.setImageResource(R.drawable.catan_volunteer_rescuer);
				aggragateImage6.setImageResource(R.drawable.catan_volunteer_transportation);
			}
		}
		requestCount1.setText(countOfEachRequest.get(0).toString());
		requestCount2.setText(countOfEachRequest.get(1).toString());
		requestCount3.setText(countOfEachRequest.get(2).toString());
		requestCount4.setText(countOfEachRequest.get(3).toString());
		requestCount5.setText(countOfEachRequest.get(4).toString());
		requestCount6.setText(countOfEachRequest.get(5).toString());
		previousTab = selectedTab;
	}
	
	public void setCountOfEachReport(ArrayList<Integer> counts){
		countOfEachRequest = counts;
		setAggragateData();
	}
}
