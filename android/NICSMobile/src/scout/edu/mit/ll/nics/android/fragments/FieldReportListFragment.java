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
import java.util.Comparator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.adapters.FieldReportListAdapter;
import scout.edu.mit.ll.nics.android.api.data.FieldReportData;
import scout.edu.mit.ll.nics.android.api.data.ReportSendStatus;
import scout.edu.mit.ll.nics.android.api.payload.forms.FieldReportPayload;
import scout.edu.mit.ll.nics.android.utils.Constants;
import scout.edu.mit.ll.nics.android.utils.EncryptedPreferences;
import scout.edu.mit.ll.nics.android.utils.Intents;

public class FieldReportListFragment extends FormListFragment {

	private Context mContext;
//	private SharedPreferences settings;
	private EncryptedPreferences settings;
	private FieldReportListAdapter mFieldReportListAdapter;
	private boolean fieldReportReceiverRegistered;
	private IntentFilter mFieldReportReceiverFilter;
	private IntentFilter mIncidentSwitchedReceiverFilter;
	private IntentFilter mSentReportsClearedFilter;
	private long mLastIncidentId = 0;
	protected boolean mIsFirstLoad = true;
	
	private int index;
	private int top;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = getActivity();
//		settings = mContext.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE);
		settings = new EncryptedPreferences(mContext.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE));
		
		mFieldReportReceiverFilter = new IntentFilter(Intents.nics_NEW_FIELD_REPORT_RECEIVED);
		mIncidentSwitchedReceiverFilter = new IntentFilter(Intents.nics_INCIDENT_SWITCHED);
		mSentReportsClearedFilter = new IntentFilter(Intents.nics_SENT_FIELD_REPORTS_CLEARED);
		
		if(!fieldReportReceiverRegistered) {
			mContext.registerReceiver(fieldReportReceiver, mFieldReportReceiverFilter);
			mContext.registerReceiver(incidentChangedReceiver, mIncidentSwitchedReceiverFilter);
			mContext.registerReceiver(sentReportsClearedReceiver, mSentReportsClearedFilter);
			fieldReportReceiverRegistered = true;
		}
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.fieldreport, menu);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		view.setPadding(10, 0, 10, 0);
		
		this.setEmptyText(getString(R.string.no_field_reports_exist));
		
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		
		FieldReportPayload item = mFieldReportListAdapter.getItem(position);
		item.parse();
		
		((MainActivity)mContext).openFieldReport(item, item.isDraft());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(mFieldReportListAdapter == null) {
			mFieldReportListAdapter = new FieldReportListAdapter(mContext, R.layout.listitem_fieldreport, R.id.fieldReportTitle, new ArrayList<FieldReportPayload>());
			mIsFirstLoad = true;
		}
		
		if(!fieldReportReceiverRegistered) {
			mContext.registerReceiver(fieldReportReceiver, mFieldReportReceiverFilter);
			mContext.registerReceiver(incidentChangedReceiver, mIncidentSwitchedReceiverFilter);
			mContext.registerReceiver(sentReportsClearedReceiver, mSentReportsClearedFilter);
			fieldReportReceiverRegistered = true;
		}
		
		index = settings.getPreferenceLong("fr_scrollIdx", "-1").intValue();
		top = settings.getPreferenceLong("fr_scrollTop", "-1").intValue();

		settings.removePreference("fr_scrollIdx");
		settings.removePreference("fr_scrollTop");
		
		if(mLastIncidentId != mDataManager.getActiveIncidentId()) {
			mIsFirstLoad = true;
			setListShown(false);
		}
		
		mDataManager.sendFieldReports();
		updateData();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		if(fieldReportReceiverRegistered) {
			mContext.unregisterReceiver(fieldReportReceiver);
			mContext.unregisterReceiver(incidentChangedReceiver);
			mContext.unregisterReceiver(sentReportsClearedReceiver);
			fieldReportReceiverRegistered = false;
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		int index = getListView().getFirstVisiblePosition();
		View v = getListView().getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();

		settings.savePreferenceLong("fr_scrollIdx",(long) index);
		settings.savePreferenceLong("fr_scrollTop",(long) top);
	}
	
	private BroadcastReceiver fieldReportReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				Bundle bundle = intent.getExtras();
				FieldReportPayload payload = mBuilder.create().fromJson(bundle.getString("payload"), FieldReportPayload.class);
				payload.setSendStatus(ReportSendStatus.lookUp(bundle.getInt("sendStatus", 0)));
				payload.parse();

				mFieldReportListAdapter.add(payload);
				mFieldReportListAdapter.sort(reportComparator);
				
				mFieldReportListAdapter.notifyDataSetChanged();
				
				updateData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private BroadcastReceiver incidentChangedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			updateData();
		}
	};
	
	protected void updateData() {
		long currentIncidentId;

		currentIncidentId = mDataManager.getActiveIncidentId();
		mDataManager.requestFieldReports();
		
		mFieldReportListAdapter.clear();
		mFieldReportListAdapter.addAll(mDataManager.getFieldReportHistoryForIncident(currentIncidentId));
		mFieldReportListAdapter.addAll(mDataManager.getAllFieldReportStoreAndForwardReadyToSend(currentIncidentId));
		mFieldReportListAdapter.addAll(mDataManager.getAllFieldReportStoreAndForwardHasSent(currentIncidentId));
		mFieldReportListAdapter.sort(reportComparator);

		if(mIsFirstLoad) {
			setListAdapter(mFieldReportListAdapter);
			mLastIncidentId = currentIncidentId;
			mIsFirstLoad = false;
			setListShown(true);
		}
		
		mFieldReportListAdapter.notifyDataSetChanged();
		if(index != -1 && top != -1) {
			getListView().post(new Runnable() {
				
				@Override
				public void run() {
					if(getListView() != null) {
					try{
						getListView().setSelectionFromTop(index, top);
						index = -1;
						top = -1;
					}catch(IllegalStateException e){
						Log.e("FieldReportListFragment",e.toString());
						e.printStackTrace();
					}
					}
				}
			});
		}
	}
	
	private BroadcastReceiver sentReportsClearedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			long reportId = intent.getExtras().getLong("reportId");
			for(int i = 0; i < mFieldReportListAdapter.getCount() ; i++){
				FieldReportPayload payload = mFieldReportListAdapter.getItem(i);
				if(payload.getFormId() == reportId && payload.isNew() == false){
					mFieldReportListAdapter.remove(payload);
					mFieldReportListAdapter.notifyDataSetChanged();
					return;
				}
			}	
		}
	};
	
	private Comparator<? super FieldReportPayload> reportComparator = new Comparator<FieldReportPayload>() {
		
		@Override
		public int compare(FieldReportPayload lhs, FieldReportPayload rhs) {
			return (Long.valueOf(rhs.getSeqTime()).compareTo(Long.valueOf(lhs.getSeqTime())));
		}
	};
	
	@Override
	protected boolean itemIsDraft(int position) {
		FieldReportPayload item = mFieldReportListAdapter.getItem(position);
	
		return item.isDraft();
	}

	@Override
	protected boolean handleItemDeletion(int position) {
		FieldReportPayload item = mFieldReportListAdapter.getItem(position);
		mFieldReportListAdapter.remove(item);
		mFieldReportListAdapter.notifyDataSetChanged();
		
		return mDataManager.deleteFieldReportStoreAndForward(item.getId());
	}
}
