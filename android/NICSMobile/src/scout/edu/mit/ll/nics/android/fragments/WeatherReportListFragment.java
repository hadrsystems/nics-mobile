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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import scout.edu.mit.ll.nics.android.adapters.WeatherReportListAdapter;
import scout.edu.mit.ll.nics.android.api.data.ReportSendStatus;
import scout.edu.mit.ll.nics.android.api.payload.forms.WeatherReportPayload;
import scout.edu.mit.ll.nics.android.api.tasks.MarkAllReportsAsReadTask;
import scout.edu.mit.ll.nics.android.utils.Constants;
import scout.edu.mit.ll.nics.android.utils.EncryptedPreferences;
import scout.edu.mit.ll.nics.android.utils.FormType;
import scout.edu.mit.ll.nics.android.utils.Intents;
import scout.edu.mit.ll.nics.android.utils.Constants.NavigationOptions;

public class WeatherReportListFragment extends FormListFragment {

	private Context mContext;
	private SharedPreferences settings;
	private WeatherReportListAdapter mWeatherReportListAdapter;
	private boolean weatherReportReceiverFilter;
	private IntentFilter mWeatherReportReceiverFilter;
	private IntentFilter mWeatherReportProgressReceiverFilter;
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
		settings = mContext.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE);
		
		mWeatherReportReceiverFilter = new IntentFilter(Intents.nics_NEW_WEATHER_REPORT_RECEIVED);
		mWeatherReportProgressReceiverFilter = new IntentFilter(Intents.nics_WEATHER_REPORT_PROGRESS);
		mIncidentSwitchedReceiverFilter = new IntentFilter(Intents.nics_INCIDENT_SWITCHED);
		mMarkAllAsReadReceiverFilter = new IntentFilter(Intents.nics_MARKING_ALL_REPORTS_READ_FINISHED);
		mSentReportsClearedFilter = new IntentFilter(Intents.nics_SENT_WEATHER_REPORTS_CLEARED);
		
		if(!weatherReportReceiverFilter) {
			mContext.registerReceiver(weatherReportReceiver, mWeatherReportReceiverFilter);
			mContext.registerReceiver(weatherReportProgressReceiver, mWeatherReportProgressReceiverFilter);
			mContext.registerReceiver(incidentChangedReceiver, mIncidentSwitchedReceiverFilter);
			mContext.registerReceiver(markAllAsReadReceiver, mMarkAllAsReadReceiverFilter);
			mContext.registerReceiver(sentReportsClearedReceiver, mSentReportsClearedFilter);
			weatherReportReceiverFilter = true;
		}
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.weatherreport, menu);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		view.setPadding(10, 0, 10, 0);
		
		this.setEmptyText(getString(R.string.no_weather_reports_exist));
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
			WeatherReportPayload item = mWeatherReportListAdapter.getItem(longPressSelectionPosition);
			String cameraPos = item.getMessageData().getLatitude() + "," + item.getMessageData().getLongitude() + "," + 13 + "," + 0 + "," + 0;
			EncryptedPreferences settings = new EncryptedPreferences(mContext.getSharedPreferences(Constants.nics_MAP_MARKUP_STATE, 0));
			settings.savePreferenceString(Constants.nics_MAP_PREVIOUS_CAMERA, cameraPos);
			
			mMainActivity.onNavigationItemSelected(NavigationOptions.MAPCOLLABORATION.getValue(), -1);
		}
	};

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		
		WeatherReportPayload item = mWeatherReportListAdapter.getItem(position);
		item.parse();
		
		if(item.isNew()){
			item.setNew(false);
			mDataManager.deleteWeatherReportFromHistory(item.getId());
			mDataManager.addWeatherReportToHistory(item);
			
			ImageView blueDot = (ImageView)view.findViewById(R.id.wrBlueDotImage);
			blueDot.setVisibility(View.INVISIBLE);
		}
		
		((MainActivity)mContext).openWeatherReport(item, item.isDraft());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mDataManager.setNewReportAvailable(false);
		
		if(mWeatherReportListAdapter == null) {
			mWeatherReportListAdapter = new WeatherReportListAdapter(mContext, R.layout.listitem_weatherreport, R.id.weatherReportTitle, new ArrayList<WeatherReportPayload>());
			mIsFirstLoad = true;
		}
		
		if(!weatherReportReceiverFilter) {
			mContext.registerReceiver(weatherReportReceiver, mWeatherReportReceiverFilter);
			mContext.registerReceiver(weatherReportProgressReceiver, mWeatherReportProgressReceiverFilter);
			mContext.registerReceiver(incidentChangedReceiver, mIncidentSwitchedReceiverFilter);
			mContext.registerReceiver(markAllAsReadReceiver, mMarkAllAsReadReceiverFilter);
			mContext.registerReceiver(sentReportsClearedReceiver, mSentReportsClearedFilter);
			weatherReportReceiverFilter = true;
		}
		
		index = settings.getInt("fr_scrollIdx", -1);
		top = settings.getInt("fr_scrollTop", -1);
		
		Editor e = settings.edit();
		e.remove("fr_scrollIdx");
		e.remove("fr_scrollTop");
		e.commit();
		
		if(mLastIncidentId != mDataManager.getActiveIncidentId()) {
			mIsFirstLoad = true;
			setListShown(false);
		}
		
		mDataManager.sendWeatherReports();
		updateData();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		if(weatherReportReceiverFilter) {
			mContext.unregisterReceiver(weatherReportReceiver);
			mContext.unregisterReceiver(weatherReportProgressReceiver);
			mContext.unregisterReceiver(incidentChangedReceiver);
			mContext.unregisterReceiver(markAllAsReadReceiver);
			mContext.unregisterReceiver(sentReportsClearedReceiver);
			weatherReportReceiverFilter = false;
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		Editor e = settings.edit();
		
		int index = getListView().getFirstVisiblePosition();
		View v = getListView().getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();

		e.putInt("fr_scrollIdx", index);
		e.putInt("fr_scrollTop", top);
		
		e.commit();
	}
	
	private BroadcastReceiver weatherReportReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				Bundle bundle = intent.getExtras();
				WeatherReportPayload payload = mBuilder.create().fromJson(bundle.getString("payload"), WeatherReportPayload.class);
				payload.setSendStatus(ReportSendStatus.lookUp(bundle.getInt("sendStatus", 0)));
				payload.parse();
				
				mWeatherReportListAdapter.add(payload);
				mWeatherReportListAdapter.sort(reportComparator);
				mWeatherReportListAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private BroadcastReceiver weatherReportProgressReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				Bundle bundle = intent.getExtras();
				long reportId = bundle.getLong("reportId");
				double progress = bundle.getDouble("progress");
				boolean failed = bundle.getBoolean("failed");

				for (WeatherReportPayload payload : mWeatherReportListAdapter.getItems()) {
					if (payload.getId() == reportId) {
						payload.setProgress((int) Math.round(progress));
						payload.setFailedToSend(failed);
					}
				}

				if(mWeatherReportListAdapter != null) {
					mWeatherReportListAdapter.notifyDataSetChanged();
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
		mDataManager.requestWeatherReports();
		
		mWeatherReportListAdapter.clear();
		mWeatherReportListAdapter.addAll(mDataManager.getWeatherReportHistoryForIncident(currentIncidentId));
		mWeatherReportListAdapter.addAll(mDataManager.getAllWeatherReportStoreAndForwardReadyToSend(currentIncidentId));
		mWeatherReportListAdapter.addAll(mDataManager.getAllWeatherReportStoreAndForwardHasSent(currentIncidentId));
		mWeatherReportListAdapter.sort(reportComparator);

		if(mIsFirstLoad) {
			setListAdapter(mWeatherReportListAdapter);
			mLastIncidentId = currentIncidentId;
			mIsFirstLoad = false;
			setListShown(true);
		}
		
		mWeatherReportListAdapter.notifyDataSetChanged();
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
						Log.e("WeatherReportListFragment",e.toString());
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
			for(int i = 0; i < mWeatherReportListAdapter.getCount() ; i++){
				WeatherReportPayload payload = mWeatherReportListAdapter.getItem(i);
				if(payload.getFormId() == reportId && payload.isNew() == false){
					mWeatherReportListAdapter.remove(payload);
					mWeatherReportListAdapter.notifyDataSetChanged();
					return;
				}
			}	
		}
	};
	
	private Comparator<? super WeatherReportPayload> reportComparator = new Comparator<WeatherReportPayload>() {
		
		@Override
		public int compare(WeatherReportPayload lhs, WeatherReportPayload rhs) {
			return (Long.valueOf(rhs.getSeqTime()).compareTo(Long.valueOf(lhs.getSeqTime())));
		}
	};
	
	@Override
	protected boolean itemIsDraft(int position) {
		WeatherReportPayload item = mWeatherReportListAdapter.getItem(position);
		return item.isDraft();
	}

	@Override
	protected boolean handleItemDeletion(int position) {
		WeatherReportPayload item = mWeatherReportListAdapter.getItem(position);
		mWeatherReportListAdapter.remove(item);
		mWeatherReportListAdapter.notifyDataSetChanged();
		
		return mDataManager.deleteWeatherReportStoreAndForward(item.getId());
	}
	
	public void MarkAllMessagesAsRead()
	{		
		if(MarkMessagesAsReadTask == null){
			MarkMessagesAsReadTask = new MarkAllReportsAsReadTask(mContext);
			MarkMessagesAsReadTask.execute(FormType.WR);
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
