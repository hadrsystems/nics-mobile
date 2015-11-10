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
import edu.mit.ll.phinics.android.adapters.WeatherReportListAdapter;
import edu.mit.ll.phinics.android.api.data.WeatherReportData;
import edu.mit.ll.phinics.android.api.data.ReportStatus;
import edu.mit.ll.phinics.android.api.payload.forms.WeatherReportPayload;
import edu.mit.ll.phinics.android.utils.Constants;
import edu.mit.ll.phinics.android.utils.Intents;

public class WeatherReportListFragment extends FormListFragment {

	private Context mContext;
	private SharedPreferences settings;
	private WeatherReportListAdapter mWeatherReportListAdapter;
	private boolean weatherReportReceiverFilter;
	private IntentFilter mWeatherReportReceiverFilter;
	private long mLastIncidentId = 0;
	protected boolean mIsFirstLoad = true;
	
	private int index;
	private int top;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = getActivity();
		settings = mContext.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE);
		
		mWeatherReportReceiverFilter = new IntentFilter(Intents.PHINICS_NEW_WEATHER_REPORT_RECEIVED);
		
		if(!weatherReportReceiverFilter) {
			mContext.registerReceiver(weatherReportReceiver, mWeatherReportReceiverFilter);
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
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		
		WeatherReportPayload item = mWeatherReportListAdapter.getItem(position);
		item.parse();
		
		((MainActivity)mContext).openWeatherReport(item, item.isDraft());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(mWeatherReportListAdapter == null) {
			mWeatherReportListAdapter = new WeatherReportListAdapter(mContext, R.layout.listitem_weatherreport, R.id.weatherReportTitle, new ArrayList<WeatherReportPayload>());
			mIsFirstLoad = true;
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
				payload.setStatus(ReportStatus.lookUp(bundle.getInt("status", 0)));
				payload.parse();
				
				WeatherReportData data = payload.getMessageData();

				if(data.getUser().equals(mDataManager.getUsername()) && payload.getSeqTime() >= mDataManager.getLastWeatherReportTimestamp() - 10000) {
					mWeatherReportListAdapter.clear();
					mWeatherReportListAdapter.addAll(mDataManager.getWeatherReportHistoryForIncident(mDataManager.getActiveIncidentId()));
					mWeatherReportListAdapter.addAll(mDataManager.getAllWeatherReportStoreAndForwardReadyToSend(mDataManager.getActiveIncidentId()));
				} else {
					mWeatherReportListAdapter.add(payload);
				}
				mWeatherReportListAdapter.sort(reportComparator);
				
				mWeatherReportListAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	protected void updateData() {
		long currentIncidentId;

		currentIncidentId = mDataManager.getActiveIncidentId();
		mDataManager.requestWeatherReports();
		
		mWeatherReportListAdapter.clear();
		mWeatherReportListAdapter.addAll(mDataManager.getWeatherReportHistoryForIncident(currentIncidentId));
		mWeatherReportListAdapter.addAll(mDataManager.getAllWeatherReportStoreAndForwardReadyToSend(currentIncidentId));
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
						getListView().setSelectionFromTop(index, top);
						index = -1;
						top = -1;
					}
				}
			});
		}
	}
	
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
}
