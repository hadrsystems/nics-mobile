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

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;
import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.adapters.SimpleReportListAdapter;
import scout.edu.mit.ll.nics.android.api.data.ReportSendStatus;
import scout.edu.mit.ll.nics.android.api.data.SimpleReportData;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;
import scout.edu.mit.ll.nics.android.utils.Constants;
import scout.edu.mit.ll.nics.android.utils.EncryptedPreferences;
import scout.edu.mit.ll.nics.android.utils.FormType;
import scout.edu.mit.ll.nics.android.utils.Intents;
import scout.edu.mit.ll.nics.android.utils.Constants.NavigationOptions;
import scout.edu.mit.ll.nics.android.api.tasks.MarkAllReportsAsReadTask;

public class SimpleReportListFragment extends FormListFragment {
	private Context mContext;
	private EncryptedPreferences settings;
	private SimpleReportListAdapter mSimpleReportListAdapter;
	private boolean simpleReportReceiverRegistered;
	private IntentFilter mSimpleReportReceiverFilter;
	private IntentFilter mSimpleReportProgressReceiverFilter;
	private IntentFilter mIncidentSwitchedReceiverFilter;
	private IntentFilter mMarkAllAsReadReceiverFilter;
	private IntentFilter mSentReportsClearedFilter;
	private long mLastIncidentId = 0;
	protected boolean mIsFirstLoad = true;
	protected MarkAllReportsAsReadTask MarkMessagesAsReadTask = null;
	
	private int index;
	private int top;
	private int longPressSelectionPosition;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getActivity();
		settings = new EncryptedPreferences( mContext.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE));
		
		mSimpleReportReceiverFilter = new IntentFilter(Intents.nics_NEW_SIMPLE_REPORT_RECEIVED);
		mSimpleReportProgressReceiverFilter = new IntentFilter(Intents.nics_SIMPLE_REPORT_PROGRESS);
		mIncidentSwitchedReceiverFilter = new IntentFilter(Intents.nics_INCIDENT_SWITCHED);
		mMarkAllAsReadReceiverFilter = new IntentFilter(Intents.nics_MARKING_ALL_REPORTS_READ_FINISHED);
		mSentReportsClearedFilter = new IntentFilter(Intents.nics_SENT_SIMPLE_REPORTS_CLEARED);
		
		if (!simpleReportReceiverRegistered) {
			mContext.registerReceiver(simpleReportReceiver, mSimpleReportReceiverFilter);
			mContext.registerReceiver(simpleReportProgressReceiver, mSimpleReportProgressReceiverFilter);
			mContext.registerReceiver(incidentChangedReceiver, mIncidentSwitchedReceiverFilter);
			mContext.registerReceiver(markAllAsReadReceiver, mMarkAllAsReadReceiverFilter);
			mContext.registerReceiver(sentReportsClearedReceiver, mSentReportsClearedFilter);
			
			simpleReportReceiverRegistered = true;
		}

		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.simplereport, menu);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		view.setPadding(10, 0, 10, 0);

		this.setEmptyText(mContext.getString(R.string.no_simple_reports_exist));
		super.onViewCreated(view, savedInstanceState);
		
		   getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			      @Override
			      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

			    	longPressSelectionPosition = position;
		    	  	AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder((MainActivity) getActivity());
		    	  
					mDialogBuilder.setMessage(null);
				    mDialogBuilder.setPositiveButton(null, null);
				    
				    String[] choices = {getString(R.string.go_to_report_on_map)};
				    
					mDialogBuilder.setItems(choices,dialogSelected);
					
					mDialogBuilder.create();
					mDialogBuilder.show();
			    	  
			        return true;
			      }
			    });
	}

	DialogInterface.OnClickListener dialogSelected = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {

			MainActivity mMainActivity = (MainActivity) getActivity();
			SimpleReportPayload item = mSimpleReportListAdapter.getItem(longPressSelectionPosition);
			String cameraPos = item.getMessageData().getLatitude() + "," + item.getMessageData().getLongitude() + "," + 13 + "," + 0 + "," + 0;
			EncryptedPreferences settings = new EncryptedPreferences(mContext.getSharedPreferences(Constants.nics_MAP_MARKUP_STATE, 0));
			settings.savePreferenceString(Constants.nics_MAP_PREVIOUS_CAMERA, cameraPos);
			
			mMainActivity.onNavigationItemSelected(NavigationOptions.MAPCOLLABORATION.getValue(), -1);
		}
	};
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		SimpleReportPayload item = mSimpleReportListAdapter.getItem(position);
		item.parse();
		Log.d("Long id: ", String.valueOf(id));
		
		if(item.isNew()){
			item.setNew(false);
			mDataManager.deleteSimpleReportFromHistory(item.getId());
			mDataManager.addSimpleReportToHistory(item);
			
			ImageView blueDot = (ImageView)view.findViewById(R.id.srBlueDotImage);
			blueDot.setVisibility(View.INVISIBLE);
		}
		
		((MainActivity) mContext).openSimpleReport(item, item.isDraft());
	}

	@Override
	public void onResume() {
		super.onResume();
		mDataManager.setNewGeneralMessageAvailable(false);
		
		if(mSimpleReportListAdapter == null) {
			mSimpleReportListAdapter = new SimpleReportListAdapter(mContext, R.layout.listitem_simplereport, R.id.simpleReportTitle, new ArrayList<SimpleReportPayload>());
			mIsFirstLoad = true;
			getListView().setAdapter(mSimpleReportListAdapter);
		}
		
		if (!simpleReportReceiverRegistered) {
			mContext.registerReceiver(simpleReportReceiver, mSimpleReportReceiverFilter);
			mContext.registerReceiver(simpleReportProgressReceiver, mSimpleReportProgressReceiverFilter);
			mContext.registerReceiver(incidentChangedReceiver, mIncidentSwitchedReceiverFilter);
			mContext.registerReceiver(markAllAsReadReceiver, mMarkAllAsReadReceiverFilter);
			mContext.registerReceiver(sentReportsClearedReceiver, mSentReportsClearedFilter);
			simpleReportReceiverRegistered = true;
		}
		
		index  = settings.getPreferenceLong("sr_scrollIdx", "-1").intValue();
		top  = settings.getPreferenceLong("sr_scrollTop", "-1").intValue();
		
		settings.removePreference("sr_scrollIdx");
		settings.removePreference("sr_scrollTop");
		
		if(mLastIncidentId != mDataManager.getActiveIncidentId()) {
			mIsFirstLoad = true;
			setListShown(false);
		}
		
		mDataManager.sendSimpleReports();
		updateData();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if (simpleReportReceiverRegistered) {
			mContext.unregisterReceiver(simpleReportReceiver);
			mContext.unregisterReceiver(simpleReportProgressReceiver);
			mContext.unregisterReceiver(incidentChangedReceiver);
			mContext.unregisterReceiver(markAllAsReadReceiver);
			mContext.unregisterReceiver(sentReportsClearedReceiver);
			simpleReportReceiverRegistered = false;
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

	private BroadcastReceiver simpleReportReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				Bundle bundle = intent.getExtras();
				SimpleReportPayload payload = mBuilder.create().fromJson(bundle.getString("payload"), SimpleReportPayload.class);
				payload.setSendStatus(ReportSendStatus.lookUp(bundle.getInt("sendStatus", 0)));
				payload.parse();

				SimpleReportData data = payload.getMessageData();
				mSimpleReportListAdapter.add(payload);
				
				mSimpleReportListAdapter.sort(reportComparator);
				mSimpleReportListAdapter.notifyDataSetChanged();
				mDataManager.setNewGeneralMessageAvailable(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private BroadcastReceiver simpleReportProgressReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				Bundle bundle = intent.getExtras();
				long reportId = bundle.getLong("reportId");
				double progress = bundle.getDouble("progress");
				boolean failed = bundle.getBoolean("failed");

				for (SimpleReportPayload payload : mSimpleReportListAdapter.getItems()) {
					if (payload.getId() == reportId) {
						payload.setProgress((int) Math.round(progress));
						payload.setFailedToSend(failed);
					}
				}

				if(mSimpleReportListAdapter != null) {
					mSimpleReportListAdapter.notifyDataSetChanged();
				}
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
		mDataManager.requestSimpleReports();
		
		mSimpleReportListAdapter.clear();
		
		mSimpleReportListAdapter.addAll(mDataManager.getSimpleReportHistoryForIncident(currentIncidentId));
		mSimpleReportListAdapter.addAll(mDataManager.getAllSimpleReportStoreAndForwardReadyToSend(currentIncidentId));
		mSimpleReportListAdapter.addAll(mDataManager.getAllSimpleReportStoreAndForwardHasSent(currentIncidentId));
		mSimpleReportListAdapter.sort(reportComparator);
		
		if(mIsFirstLoad) {
			setListAdapter(mSimpleReportListAdapter);
			mLastIncidentId = currentIncidentId;
			mIsFirstLoad = false;
			setListShown(true);
		}
		
		mSimpleReportListAdapter.notifyDataSetChanged();		
		if(index != -1 && top != -1) {
			getListView().post(new Runnable() {
				
				@Override
				public void run() {
					try{
						getListView().setSelectionFromTop(index, top);
						index = -1;
						top = -1;
					}catch(IllegalStateException e){
						Log.e("SimpleReportListFragment",e.toString());
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	public void MarkAllMessagesAsRead()
	{		
		if(MarkMessagesAsReadTask == null){
			MarkMessagesAsReadTask = new MarkAllReportsAsReadTask(mContext);
			MarkMessagesAsReadTask.execute(FormType.SR);
		}
	}
	
	private BroadcastReceiver markAllAsReadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			MarkMessagesAsReadTask = null;
			updateData();	
		}
	};
	
	
	private BroadcastReceiver sentReportsClearedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			long reportId = intent.getExtras().getLong("reportId");
			for(int i = 0; i < mSimpleReportListAdapter.getCount() ; i++){
				SimpleReportPayload payload = mSimpleReportListAdapter.getItem(i);
				if(payload.getFormId() == reportId && payload.isNew() == false){
					mSimpleReportListAdapter.remove(payload);
					mSimpleReportListAdapter.notifyDataSetChanged();
					return;
				}
			}	
		}
	};
	
	private Comparator<? super SimpleReportPayload> reportComparator = new Comparator<SimpleReportPayload>() {

		@Override
		public int compare(SimpleReportPayload lhs, SimpleReportPayload rhs) {
			return (Long.valueOf(rhs.getSeqTime()).compareTo(Long.valueOf(lhs.getSeqTime())));
		}
	};

	@Override
	protected boolean itemIsDraft(int position) {
		SimpleReportPayload item = mSimpleReportListAdapter.getItem(position);
		return item.isDraft();
	}

	@Override
	protected boolean handleItemDeletion(int position) {
		SimpleReportPayload item = mSimpleReportListAdapter.getItem(position);
		mSimpleReportListAdapter.remove(item);
		mSimpleReportListAdapter.notifyDataSetChanged();
		
		return mDataManager.deleteSimpleReportStoreAndForward(item.getId());
	}
}
