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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;
import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.adapters.DamageReportListAdapter;
import scout.edu.mit.ll.nics.android.api.data.DamageReportData;
import scout.edu.mit.ll.nics.android.api.data.ReportSendStatus;
import scout.edu.mit.ll.nics.android.api.payload.forms.DamageReportPayload;
import scout.edu.mit.ll.nics.android.api.tasks.MarkAllReportsAsReadTask;
import scout.edu.mit.ll.nics.android.utils.Constants;
import scout.edu.mit.ll.nics.android.utils.EncryptedPreferences;
import scout.edu.mit.ll.nics.android.utils.FormType;
import scout.edu.mit.ll.nics.android.utils.Intents;
import scout.edu.mit.ll.nics.android.utils.Constants.NavigationOptions;

public class DamageReportListFragment extends FormListFragment {
	private Context mContext;
	private EncryptedPreferences settings;
	private DamageReportListAdapter mDamageReportListAdapter;
	private boolean damageReportReceiverRegistered;
	private IntentFilter mDamageReportReceiverFilter;
	private IntentFilter mDamageReportProgressReceiverFilter;
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
		
		mDamageReportReceiverFilter = new IntentFilter(Intents.nics_NEW_DAMAGE_REPORT_RECEIVED);
		mDamageReportProgressReceiverFilter = new IntentFilter(Intents.nics_DAMAGE_REPORT_PROGRESS);
		mIncidentSwitchedReceiverFilter = new IntentFilter(Intents.nics_INCIDENT_SWITCHED);
		mMarkAllAsReadReceiverFilter = new IntentFilter(Intents.nics_MARKING_ALL_REPORTS_READ_FINISHED);
		mSentReportsClearedFilter = new IntentFilter(Intents.nics_SENT_DAMAGE_REPORTS_CLEARED);
		
		if(!damageReportReceiverRegistered) {
			mContext.registerReceiver(damageReportReceiver, mDamageReportReceiverFilter);
			mContext.registerReceiver(damageReportProgressReceiver, mDamageReportProgressReceiverFilter);
			mContext.registerReceiver(incidentChangedReceiver, mIncidentSwitchedReceiverFilter);
			mContext.registerReceiver(markAllAsReadReceiver, mMarkAllAsReadReceiverFilter);
			mContext.registerReceiver(sentReportsClearedReceiver, mSentReportsClearedFilter);
			
			damageReportReceiverRegistered = true;
		}
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.damagereport, menu);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		view.setPadding(10, 0, 10, 0);

		this.setEmptyText(getString(R.string.no_damage_reports_exist));
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
			DamageReportPayload item = mDamageReportListAdapter.getItem(longPressSelectionPosition);
			String cameraPos = item.getMessageData().getPropertyLatitude() + "," + item.getMessageData().getPropertyLongitude() + "," + 13 + "," + 0 + "," + 0;
			EncryptedPreferences settings = new EncryptedPreferences(mContext.getSharedPreferences(Constants.nics_MAP_MARKUP_STATE, 0));
			settings.savePreferenceString(Constants.nics_MAP_PREVIOUS_CAMERA, cameraPos);
			
			mMainActivity.onNavigationItemSelected(NavigationOptions.MAPCOLLABORATION.getValue(), -1);
		}
	};
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		
		DamageReportPayload item = mDamageReportListAdapter.getItem(position);
		item.parse();
		
		if(item.isNew()){
			item.setNew(false);
			mDataManager.deleteDamageReportFromHistory(item.getId());
			mDataManager.addDamageReportToHistory(item);
			
			ImageView blueDot = (ImageView)view.findViewById(R.id.drBlueDotImage);
			blueDot.setVisibility(View.INVISIBLE);
		}
		
		
		((MainActivity)mContext).openDamageReport(item, item.isDraft());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mDataManager.setNewReportAvailable(false);
		
		if(mDamageReportListAdapter == null) {
			mDamageReportListAdapter = new DamageReportListAdapter(mContext, R.layout.listitem_damagereport, R.id.damageReportTitle, new ArrayList<DamageReportPayload>());
			mIsFirstLoad = true;
		}
		
		if(!damageReportReceiverRegistered) {
			mContext.registerReceiver(damageReportReceiver, mDamageReportReceiverFilter);
			mContext.registerReceiver(damageReportProgressReceiver, mDamageReportProgressReceiverFilter);
			mContext.registerReceiver(incidentChangedReceiver, mIncidentSwitchedReceiverFilter);
			mContext.registerReceiver(markAllAsReadReceiver, mMarkAllAsReadReceiverFilter);
			mContext.registerReceiver(sentReportsClearedReceiver, mSentReportsClearedFilter);
			damageReportReceiverRegistered = true;
		}
		
		index = settings.getPreferenceLong("dmgrpt_scrollIdx", "-1").intValue();
		top = settings.getPreferenceLong("dmgrpt_scrollTop", "-1").intValue();

		settings.removePreference("dmgrpt_scrollIdx");
		settings.removePreference("dmgrpt_scrollTop");
		
		if(mLastIncidentId != mDataManager.getActiveIncidentId()) {
			mIsFirstLoad = true;
			setListShown(false);
		}
		
		mDataManager.sendDamageReports();
		updateData();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		if(damageReportReceiverRegistered) {
			mContext.unregisterReceiver(damageReportReceiver);
			mContext.unregisterReceiver(damageReportProgressReceiver);
			mContext.unregisterReceiver(incidentChangedReceiver);
			mContext.unregisterReceiver(markAllAsReadReceiver);
			mContext.unregisterReceiver(sentReportsClearedReceiver);
			damageReportReceiverRegistered = false;
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		int index = getListView().getFirstVisiblePosition();
		View v = getListView().getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();

		settings.savePreferenceLong("dmgrpt_scrollIdx", (long)index);
		settings.savePreferenceLong("dmgrpt_scrollTop", (long)top);
	}
	
	private BroadcastReceiver damageReportReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				Bundle bundle = intent.getExtras();
				DamageReportPayload payload = mBuilder.create().fromJson(bundle.getString("payload"), DamageReportPayload.class);
				payload.setSendStatus(ReportSendStatus.lookUp(bundle.getInt("sendStatus", 0)));
				payload.parse();
				
				mDamageReportListAdapter.add(payload);
				
				mDamageReportListAdapter.sort(reportComparator);
				mDamageReportListAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private BroadcastReceiver damageReportProgressReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				Bundle bundle = intent.getExtras();
				long reportId = bundle.getLong("reportId");
				double progress = bundle.getDouble("progress");
				boolean failed = bundle.getBoolean("failed");

				for (DamageReportPayload payload : mDamageReportListAdapter.getItems()) {
					if (payload.getId() == reportId) {
						payload.setProgress((int) Math.round(progress));
						payload.setFailedToSend(failed);
					}
				}

				if(mDamageReportListAdapter != null) {
					mDamageReportListAdapter.notifyDataSetChanged();
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
		mDataManager.requestDamageReports();
		
		mDamageReportListAdapter.clear();
		mDamageReportListAdapter.addAll(mDataManager.getDamageReportHistoryForIncident(currentIncidentId));
		mDamageReportListAdapter.addAll(mDataManager.getAllDamageReportStoreAndForwardReadyToSend(currentIncidentId));
		mDamageReportListAdapter.addAll(mDataManager.getAllDamageReportStoreAndForwardHasSent(currentIncidentId));
		mDamageReportListAdapter.sort(reportComparator);
			
		if(mIsFirstLoad) {
			setListAdapter(mDamageReportListAdapter);
			mLastIncidentId = currentIncidentId;
			mIsFirstLoad = false;
			setListShown(true);
		}
		
		mDamageReportListAdapter.notifyDataSetChanged();
		if(index != -1 && top != -1) {
			getListView().post(new Runnable() {
				
				@Override
				public void run() {
				try{
					getListView().setSelectionFromTop(index, top);
					index = -1;
					top = -1;
				}catch(IllegalStateException e){
					Log.e("DamageReportListFragment",e.toString());
					e.printStackTrace();
				}
				}
			});
		}
	}
	
	private BroadcastReceiver sentReportsClearedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			long reportId = intent.getExtras().getLong("reportId");
			for(int i = 0; i < mDamageReportListAdapter.getCount() ; i++){
				DamageReportPayload payload = mDamageReportListAdapter.getItem(i);
				if(payload.getFormId() == reportId && payload.isNew() == false){
					mDamageReportListAdapter.remove(payload);
					mDamageReportListAdapter.notifyDataSetChanged();
					return;
				}
			}	
		}
	};
	
	private Comparator<? super DamageReportPayload> reportComparator = new Comparator<DamageReportPayload>() {
		
		@Override
		public int compare(DamageReportPayload lhs, DamageReportPayload rhs) {
			return (Long.valueOf(rhs.getSeqTime()).compareTo(Long.valueOf(lhs.getSeqTime())));
		}
	};
	
	@Override
	protected boolean itemIsDraft(int position) {
		DamageReportPayload item = mDamageReportListAdapter.getItem(position);
	
		return item.isDraft();
	}

	@Override
	protected boolean handleItemDeletion(int position) {
		DamageReportPayload item = mDamageReportListAdapter.getItem(position);
		mDamageReportListAdapter.remove(item);
		mDamageReportListAdapter.notifyDataSetChanged();
		
		return mDataManager.deleteDamageReportStoreAndForward(item.getId());
	}
	
	public void MarkAllMessagesAsRead()
	{		
		if(MarkMessagesAsReadTask == null){
			MarkMessagesAsReadTask = new MarkAllReportsAsReadTask(mContext);
			MarkMessagesAsReadTask.execute(FormType.DR);
		}
	}
	
	private BroadcastReceiver markAllAsReadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			MarkMessagesAsReadTask = null;
			updateData();	
		}
	};
}
