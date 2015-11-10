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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import edu.mit.ll.phinics.android.MainActivity;
import edu.mit.ll.phinics.android.R;
import edu.mit.ll.phinics.android.adapters.ResourceRequestListAdapter;
import edu.mit.ll.phinics.android.api.data.ReportStatus;
import edu.mit.ll.phinics.android.api.data.ResourceRequestData;
import edu.mit.ll.phinics.android.api.payload.forms.ResourceRequestPayload;
import edu.mit.ll.phinics.android.utils.Constants;
import edu.mit.ll.phinics.android.utils.Intents;

public class ResourceRequestListFragment extends FormListFragment {
	private Context mContext;
	private SharedPreferences settings;
	private ResourceRequestListAdapter mResourceRequestListAdapter;
	private IntentFilter mResourceRequestReceiverFilter;
	private boolean resourceRequestReceiverRegistered;
	private long mLastIncidentId = 0;
	protected boolean mIsFirstLoad = true;
	
	private int index;
	private int top;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getActivity();
		settings = mContext.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE);

		mResourceRequestReceiverFilter = new IntentFilter( Intents.PHINICS_NEW_RESOURCE_REQUEST_RECEIVED);
		
		if(!resourceRequestReceiverRegistered) {
			mContext.registerReceiver(resourceRequestReceiver, mResourceRequestReceiverFilter);
			resourceRequestReceiverRegistered = true;
		}
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.resourcerequest, menu);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		view.setPadding(10, 0, 10, 0);
		
		this.setEmptyText(getString(R.string.no_resource_requests_exist));
		
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		
		ResourceRequestPayload item = mResourceRequestListAdapter.getItem(position);
		item.parse();
		
		((MainActivity)mContext).openResourceRequest(item, item.isDraft());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(mResourceRequestListAdapter == null) {
			mResourceRequestListAdapter = new ResourceRequestListAdapter(mContext, R.layout.listitem_resourcerequest, R.id.resourceRequestTitle, new ArrayList<ResourceRequestPayload>());
			mIsFirstLoad = true;
		}
		
		index = settings.getInt("resreq_scrollIdx", -1);
		top = settings.getInt("resreq_scrollTop", -1);
		
		Editor e = settings.edit();
		e.remove("resreq_scrollIdx");
		e.remove("resreq_scrollTop");
		e.commit();
		
		
		if(mLastIncidentId != mDataManager.getActiveIncidentId()) {
			mIsFirstLoad = true;
			setListShown(false);
		}
		
		mDataManager.sendResourceRequests();
		updateData();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		if(resourceRequestReceiverRegistered) {
			mContext.unregisterReceiver(resourceRequestReceiver);
			resourceRequestReceiverRegistered = false;
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		Editor e = settings.edit();
		
		int index = getListView().getFirstVisiblePosition();
		View v = getListView().getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();

		e.putInt("resreq_scrollIdx", index);
		e.putInt("resreq_scrollTop", top);
		
		e.commit();
	}
	
	private BroadcastReceiver resourceRequestReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				Bundle bundle = intent.getExtras();
				ResourceRequestPayload payload = mBuilder.create().fromJson(bundle.getString("payload"), ResourceRequestPayload.class);
				payload.setStatus(ReportStatus.lookUp(bundle.getInt("status", 0)));
				payload.parse();
				
				ResourceRequestData data = payload.getMessageData();

				if(data.getUser().equals(mDataManager.getUsername()) && payload.getSeqTime() >= mDataManager.getLastResourceRequestTimestamp() - 10000) {
					mResourceRequestListAdapter.clear();
					mResourceRequestListAdapter.addAll(mDataManager.getResourceRequestHistoryForIncident(mDataManager.getActiveIncidentId()));
					mResourceRequestListAdapter.addAll(mDataManager.getAllResourceRequestStoreAndForwardReadyToSend(mDataManager.getActiveIncidentId()));
				} else {
					mResourceRequestListAdapter.add(payload);
				}
				mResourceRequestListAdapter.sort(reportComparator);
				
				mResourceRequestListAdapter.notifyDataSetChanged();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	protected void updateData() {
		long currentIncidentId;
		
		currentIncidentId = mDataManager.getActiveIncidentId();
		mDataManager.requestResourceRequests();
		
		mResourceRequestListAdapter.clear();
		mResourceRequestListAdapter.addAll(mDataManager.getResourceRequestHistoryForIncident(currentIncidentId));
		mResourceRequestListAdapter.addAll(mDataManager.getAllResourceRequestStoreAndForwardReadyToSend(currentIncidentId));
		mResourceRequestListAdapter.sort(reportComparator);

		if(mIsFirstLoad) {
			setListAdapter(mResourceRequestListAdapter);
			mLastIncidentId = currentIncidentId;
			mIsFirstLoad = false;
			setListShown(true);
		}
		
		mResourceRequestListAdapter.notifyDataSetChanged();
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
	
	private Comparator<? super ResourceRequestPayload> reportComparator = new Comparator<ResourceRequestPayload>() {
		
		@Override
		public int compare(ResourceRequestPayload lhs, ResourceRequestPayload rhs) {
			return (Long.valueOf(rhs.getSeqTime()).compareTo(Long.valueOf(lhs.getSeqTime())));
		}
	};
	
	@Override
	protected boolean itemIsDraft(int position) {
		ResourceRequestPayload item = mResourceRequestListAdapter.getItem(position);
	
		return item.isDraft();
	}

	@Override
	protected boolean handleItemDeletion(int position) {
		ResourceRequestPayload item = mResourceRequestListAdapter.getItem(position);
		mResourceRequestListAdapter.remove(item);
		mResourceRequestListAdapter.notifyDataSetChanged();
		
		return mDataManager.deleteResourceRequestStoreAndForward(item.getId());
	}
}
