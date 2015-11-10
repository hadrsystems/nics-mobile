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
import edu.mit.ll.phinics.android.adapters.UxoReportListAdapter;
import edu.mit.ll.phinics.android.api.data.UxoReportData;
import edu.mit.ll.phinics.android.api.data.ReportStatus;
import edu.mit.ll.phinics.android.api.payload.forms.UxoReportPayload;
import edu.mit.ll.phinics.android.utils.Constants;
import edu.mit.ll.phinics.android.utils.Intents;

public class UxoReportListFragment extends FormListFragment {
	private Context mContext;
	private SharedPreferences settings;
	private UxoReportListAdapter mUxoReportListAdapter;
	private boolean uxoReportReceiverRegistered;	
	private IntentFilter mUxoReportReceiverFilter;
	private IntentFilter mUxoReportProgressReceiverFilter;
	private long mLastIncidentId = 0;
	protected boolean mIsFirstLoad = true;
	
	private int index;
	private int top;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getActivity();
		settings = mContext.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE);
		
		mUxoReportReceiverFilter = new IntentFilter(Intents.PHINICS_NEW_UXO_REPORT_RECEIVED);
		mUxoReportProgressReceiverFilter = new IntentFilter(Intents.PHINICS_UXO_REPORT_PROGRESS);
		
		if(!uxoReportReceiverRegistered) {
			mContext.registerReceiver(uxoReportReceiver, mUxoReportReceiverFilter);
			mContext.registerReceiver(uxoReportProgressReceiver, mUxoReportProgressReceiverFilter);
			uxoReportReceiverRegistered = true;
		}
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.uxoreport, menu);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		view.setPadding(10, 0, 10, 0);

		this.setEmptyText(getString(R.string.no_uxo_reports_exist));
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		
		UxoReportPayload item = mUxoReportListAdapter.getItem(position);
		item.parse();
		
		((MainActivity)mContext).openUxoReport(item, item.isDraft());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(mUxoReportListAdapter == null) {
			mUxoReportListAdapter = new UxoReportListAdapter(mContext, R.layout.listitem_uxoreport, R.id.uxoReportTitle, new ArrayList<UxoReportPayload>());
			mIsFirstLoad = true;
		}
		
		index = settings.getInt("uxorpt_scrollIdx", -1);
		top = settings.getInt("uxorpt_scrollTop", -1);
		
		Editor e = settings.edit();
		e.remove("uxorpt_scrollIdx");
		e.remove("uxorpt_scrollTop");
		e.commit();
		
		if(mLastIncidentId != mDataManager.getActiveIncidentId()) {
			mIsFirstLoad = true;
			setListShown(false);
		}
		
		mDataManager.sendUxoReports();
		updateData();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		if(uxoReportReceiverRegistered) {
			mContext.unregisterReceiver(uxoReportReceiver);
			mContext.unregisterReceiver(uxoReportProgressReceiver);
			uxoReportReceiverRegistered = false;
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		Editor e = settings.edit();
		
		int index = getListView().getFirstVisiblePosition();
		View v = getListView().getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();

		e.putInt("uxorpt_scrollIdx", index);
		e.putInt("uxorpt_scrollTop", top);
		
		e.commit();
	}
	
	private BroadcastReceiver uxoReportReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				Bundle bundle = intent.getExtras();
				UxoReportPayload payload = mBuilder.create().fromJson(bundle.getString("payload"), UxoReportPayload.class);
				payload.setStatus(ReportStatus.lookUp(bundle.getInt("status", 0)));
				payload.parse();
				
				UxoReportData data = payload.getMessageData();
				
				if(data.getUser().equals(mDataManager.getUsername()) && payload.getSeqTime() >= mDataManager.getLastUxoReportTimestamp() - 10000) {
					mUxoReportListAdapter.clear();
					mUxoReportListAdapter.addAll(mDataManager.getUxoReportHistoryForIncident(mDataManager.getActiveIncidentId()));
					mUxoReportListAdapter.addAll(mDataManager.getAllUxoReportStoreAndForwardReadyToSend(mDataManager.getActiveIncidentId()));
				} else {
					mUxoReportListAdapter.add(payload);
				}
				mUxoReportListAdapter.sort(reportComparator);
				mUxoReportListAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private BroadcastReceiver uxoReportProgressReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				Bundle bundle = intent.getExtras();
				long reportId = bundle.getLong("reportId");
				double progress = bundle.getDouble("progress");
				boolean failed = bundle.getBoolean("failed");

				for (UxoReportPayload payload : mUxoReportListAdapter.getItems()) {
					if (payload.getId() == reportId) {
						payload.setProgress((int) Math.round(progress));
						payload.setFailedToSend(failed);
					}
				}

				if(mUxoReportListAdapter != null) {
					mUxoReportListAdapter.notifyDataSetChanged();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	protected void updateData() {
		long currentIncidentId;

		currentIncidentId = mDataManager.getActiveIncidentId();
		mDataManager.requestUxoReports();
		
		mUxoReportListAdapter.clear();
		mUxoReportListAdapter.addAll(mDataManager.getUxoReportHistoryForIncident(currentIncidentId));
		mUxoReportListAdapter.addAll(mDataManager.getAllUxoReportStoreAndForwardReadyToSend(currentIncidentId));
		mUxoReportListAdapter.sort(reportComparator);
			
		if(mIsFirstLoad) {
			setListAdapter(mUxoReportListAdapter);
			mLastIncidentId = currentIncidentId;
			mIsFirstLoad = false;
			setListShown(true);
		}
		
		mUxoReportListAdapter.notifyDataSetChanged();
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
	
	private Comparator<? super UxoReportPayload> reportComparator = new Comparator<UxoReportPayload>() {
		
		@Override
		public int compare(UxoReportPayload lhs, UxoReportPayload rhs) {
			return (Long.valueOf(rhs.getSeqTime()).compareTo(Long.valueOf(lhs.getSeqTime())));
		}
	};
	
	@Override
	protected boolean itemIsDraft(int position) {
		UxoReportPayload item = mUxoReportListAdapter.getItem(position);
	
		return item.isDraft();
	}

	@Override
	protected boolean handleItemDeletion(int position) {
		UxoReportPayload item = mUxoReportListAdapter.getItem(position);
		mUxoReportListAdapter.remove(item);
		mUxoReportListAdapter.notifyDataSetChanged();
		
		return mDataManager.deleteUxoReportStoreAndForward(item.getId());
	}
}
