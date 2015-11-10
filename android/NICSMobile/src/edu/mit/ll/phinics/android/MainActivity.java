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
package edu.mit.ll.phinics.android;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;

import edu.mit.ll.phinics.android.api.DataManager;
import edu.mit.ll.phinics.android.api.RestClient;
import edu.mit.ll.phinics.android.api.data.UserData;
import edu.mit.ll.phinics.android.api.payload.OrganizationPayload;
import edu.mit.ll.phinics.android.api.payload.forms.CatanRequestPayload;
import edu.mit.ll.phinics.android.api.payload.forms.DamageReportPayload;
import edu.mit.ll.phinics.android.api.payload.forms.FieldReportPayload;
import edu.mit.ll.phinics.android.api.payload.forms.UxoReportPayload;
import edu.mit.ll.phinics.android.api.payload.forms.WeatherReportPayload;
import edu.mit.ll.phinics.android.api.payload.forms.ResourceRequestPayload;
import edu.mit.ll.phinics.android.api.payload.forms.SimpleReportPayload;
import edu.mit.ll.phinics.android.fragments.CatanRequestFragment;
import edu.mit.ll.phinics.android.fragments.CatanRequestListFragment;
import edu.mit.ll.phinics.android.fragments.ChatFragment;
import edu.mit.ll.phinics.android.fragments.ChatListFragment;
import edu.mit.ll.phinics.android.fragments.DamageReportFragment;
import edu.mit.ll.phinics.android.fragments.DamageReportListFragment;
import edu.mit.ll.phinics.android.fragments.FieldReportFragment;
import edu.mit.ll.phinics.android.fragments.FieldReportListFragment;
import edu.mit.ll.phinics.android.fragments.FormListTabbedFragment;
import edu.mit.ll.phinics.android.fragments.MapMarkupLocationPickerFragment;
import edu.mit.ll.phinics.android.fragments.UxoReportFragment;
import edu.mit.ll.phinics.android.fragments.UxoReportListFragment;
import edu.mit.ll.phinics.android.fragments.WeatherReportFragment;
import edu.mit.ll.phinics.android.fragments.WeatherReportListFragment;
import edu.mit.ll.phinics.android.fragments.FormFragment;
import edu.mit.ll.phinics.android.fragments.GarFragment;
import edu.mit.ll.phinics.android.fragments.GeneralMessageFragment;
import edu.mit.ll.phinics.android.fragments.MapMarkupFragment;
import edu.mit.ll.phinics.android.fragments.OverviewFragment;
import edu.mit.ll.phinics.android.fragments.ResourceRequestFragment;
import edu.mit.ll.phinics.android.fragments.ResourceRequestListFragment;
import edu.mit.ll.phinics.android.fragments.SimpleReportListFragment;
import edu.mit.ll.phinics.android.utils.Constants;
import edu.mit.ll.phinics.android.utils.Constants.NavigationOptions;
import edu.mit.ll.phinics.android.utils.EncryptedPreferences;
import edu.mit.ll.phinics.android.utils.UnreadMessageManager;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener{	//, UncaughtExceptionHandler {
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final String STATE_BACK_STACK = "back_stack";
	private static final String STATE_REPORT_OPENED_FROM_MAP = "report_opened_from_map";
	private static final String STATE_IS_EDIT_FIELD_REPORT = "is_edit_field_report";
	private static final String STATE_IS_VIEW_FIELD_REPORT = "is_view_field_report";
	private static final String STATE_IS_EDIT_DAMAGE_REPORT = "is_edit_damage_report";
	private static final String STATE_IS_VIEW_DAMAGE_REPORT = "is_view_damage_report";
	private static final String STATE_IS_EDIT_SIMPLE_REPORT = "is_edit_simple_report";
	private static final String STATE_IS_VIEW_SIMPLE_REPORT = "is_view_simple_report";
	private static final String STATE_IS_EDIT_RESOURCE_REQUEST = "is_edit_resource_request";
	private static final String STATE_IS_VIEW_RESOURCE_REQUEST = "is_view_resource_request";
	private static final String STATE_IS_EDIT_WEATHER_REPORT = "is_edit_weather_report";
	private static final String STATE_IS_VIEW_WEATHER_REPORT = "is_view_weather_report";
	private static final String STATE_IS_EDIT_UXO_REPORT = "is_edit_uxo_report";
	private static final String STATE_IS_VIEW_UXO_REPORT = "is_view_uxo_report";
	
	private int mLastPosition = NavigationOptions.OVERVIEW.getValue();
	private FragmentManager mFragmentManager;
	private Stack<Integer> mBackStack;
	
	private OverviewFragment mOverviewFragment;
	private MapMarkupFragment mMapMarkupFragment;
	private MapMarkupLocationPickerFragment mMapMarkupLocationPickerFragment;
	
	private DamageReportListFragment mDamageReportListFragment;
	public DamageReportFragment mDamageReportFragment;
	
	private SimpleReportListFragment mSimpleReportListFragment;
	public GeneralMessageFragment mSimpleReportFragment;
	
	private FieldReportListFragment mFieldReportListFragment;
	public FieldReportFragment mFieldReportFragment;
	
	private ResourceRequestListFragment mResourceRequestListFragment;
	public ResourceRequestFragment mResourceRequestFragment;
	
	private WeatherReportListFragment mWeatherReportListFragment;
	public WeatherReportFragment mWeatherReportFragment;
	
	private UxoReportListFragment mUxoReportListFragment;
	public UxoReportFragment mUxoReportFragment;
	
	public CatanRequestListFragment mCatanRequestListFragment;
	public CatanRequestFragment mCatanRequestFragment;
	
	private ChatFragment mChatFragment;
//	private ChatListFragment mChatFragment;
	
	private FormFragment mUserInfoFragment;
	
	private GarFragment mGarFragment;

	private static Context mContext;
	
	private boolean mIsBackKey = false;
	public boolean mEditDamageReport = false;
	public boolean mViewDamageReport = false;
	public boolean mEditFieldReport = false;
	public boolean mViewFieldReport = false;
	public boolean mEditSimpleReport = false;
	public boolean mViewSimpleReport = false;
	public boolean mEditResourceRequest = false;
	public boolean mViewResourceRequest = false;
	public boolean mEditWeatherReport = false;
	public boolean mViewWeatherReport = false;
	public boolean mViewMapLocationPicker = false;
	public boolean mEditUxoReport = false;
	public boolean mViewUxoReport = false;
	public boolean mViewCatanRequest = false;
	public boolean mEditCatanRequest = false;
	
	private DataManager mDataManager;
	
	public String mOpenedDamageReportPayload;
	public long mOpenedDamageReportId;
	
	public String mOpenedSimpleReportPayload;
	public long mOpenedSimpleReportId;
	
	private String mOpenedFieldReportPayload;
	private long mOpenedFieldReportId;
	
	private String mOpenedResourceRequestPayload;
	private long mOpenedResourceRequestId;
	
	private String mOpenedWeatherReportPayload;
	private long mOpenedWeatherReportId;
	
	public String mOpenedUxoReportPayload;
	public long mOpenedUxoReportId;
	
	private String mOpenedCatanRequestPayload;
	private long mOpenedCatanRequestId;
	
	private String[] navDropdownOptions;
	
	protected boolean mPreventNavigation = false;
	
	private TextView mBreadcrumbTextView;
	private boolean mReportOpenedFromMap;
	private String[] mOrgArray;
	
	public UnreadMessageManager mUnreadMessageManager;
	public static MenuItem LowDataModeIcon;
	
	private String[] navOptionsAsArray() {
	    NavigationOptions[] states = NavigationOptions.values();
	    String[] names = new String[states.length];

	    for (int i = 0; i < states.length; i++) {
	        names[i] = states[i].getLabel(this);
	    }

	    return names;
	}
	
	public static Context getAppContext() {
		return mContext;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		
		setContentView(R.layout.activity_main);
		
//		Thread.setDefaultUncaughtExceptionHandler(this);
		
		mDataManager = DataManager.getInstance(getApplicationContext(), this);
		
		mUnreadMessageManager = new UnreadMessageManager(mDataManager);
		mUnreadMessageManager.LoadFromFile();
		
		if(mDataManager.isMDTEnabled()) {
			mDataManager.getLocationSource();
		}
		
		mFragmentManager = getSupportFragmentManager();
		mBackStack = new Stack<Integer>();
		
		// Set up the action bar to show a dropdown list.
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setTitle(R.string.app_name);
		navDropdownOptions = navOptionsAsArray();
		
//		actionBar.setBackgroundDrawable(new ColorDrawable(0xffffffff));
		
		
		Intent intent = getIntent();
		
		if(intent != null && savedInstanceState == null) {
			int position = intent.getIntExtra(STATE_SELECTED_NAVIGATION_ITEM, NavigationOptions.OVERVIEW.getValue());
			if(mDataManager.isLoggedIn()) {
				onNavigationItemSelected(position, -1);
				mLastPosition = position;
			} else {
				onNavigationItemSelected(NavigationOptions.LOGOUT.getValue(), -1);
				mLastPosition = NavigationOptions.LOGOUT.getValue();
			}
			
			mOpenedDamageReportPayload = intent.getStringExtra("dr_edit_json");
			if(mOpenedDamageReportPayload != null) {
				mViewDamageReport = true;
				mEditDamageReport = false;
			}
			
			mOpenedSimpleReportPayload = intent.getStringExtra("sr_edit_json");
			if(mOpenedSimpleReportPayload != null) {
				mViewSimpleReport = true;
				mEditSimpleReport = false;
			}
			
			mOpenedFieldReportPayload = intent.getStringExtra("fr_edit_json");
			if(mOpenedFieldReportPayload != null) {
				mViewFieldReport = true;
				mEditFieldReport = false;
			}
			
			mOpenedResourceRequestPayload = intent.getStringExtra("resreq_edit_json");
			if(mOpenedResourceRequestPayload != null) {
				mViewResourceRequest = true;
				mEditResourceRequest = false;
			}
			
			mOpenedWeatherReportPayload = intent.getStringExtra("wr_edit_json");
			if(mOpenedWeatherReportPayload != null) {
				mViewWeatherReport = true;
				mEditWeatherReport = false;
			}
			
			mOpenedUxoReportPayload = intent.getStringExtra("uxo_edit_json");
			if(mOpenedUxoReportPayload != null) {
				mViewUxoReport = true;
				mEditUxoReport = false;
			}

			if(position != NavigationOptions.OVERVIEW.getValue()) {
				mBackStack = new Stack<Integer>();
				mBackStack.add(NavigationOptions.OVERVIEW.getValue());
			}
			
			if(intent.getBooleanExtra("showOrgSelector", false)) {
				showOrgSelector();
			}
		}
	}
	
	private void showOrgSelector() {
		Builder mDialogBuilder = new AlertDialog.Builder(this);
		mDialogBuilder.setTitle(R.string.select_an_organization);
		mDialogBuilder.setMessage(null);
	    mDialogBuilder.setPositiveButton(null, null);
	    HashMap<String, OrganizationPayload> orgMap = mDataManager.getOrganizations();
	    if(orgMap != null) {
		    mOrgArray = new String[orgMap.size()];
		    orgMap.keySet().toArray(mOrgArray);
		    Arrays.sort(mOrgArray);
			mDialogBuilder.setItems(mOrgArray, orgSelected);
			mDialogBuilder.create().show();
	    }
	}
	
	DialogInterface.OnClickListener orgSelected = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			OrganizationPayload selectedOrg = mDataManager.getOrganizations().get(mOrgArray[which]);
			
			mDataManager.setCurrentOrganization(selectedOrg);
			
			TextView orgTextView = (TextView) findViewById(R.id.selectedOrg);

			if(orgTextView != null) {
				orgTextView.setText(selectedOrg.getName());
			}
			Log.e(Constants.PHINICS_DEBUG_ANDROID_TAG, selectedOrg.toJsonString());
			
			RestClient.switchOrgs(selectedOrg.getOrgid());
		}
	};

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getSupportActionBar().getThemedContext();
		} else {
			return this;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(mDataManager.isMDTEnabled()) {
			mDataManager.getLocationSource();
		}
		
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && !((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE)) {
			mBreadcrumbTextView = (TextView) findViewById(R.id.breadcrumbTextView);
			mBreadcrumbTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			mBreadcrumbTextView.setGravity(Gravity.CENTER_HORIZONTAL);
			mBreadcrumbTextView.setTextSize(20);
		} else {
			TextView other = (TextView) findViewById(R.id.breadcrumbTextView);
			other.setText("");
			other.setHeight(0);
			mBreadcrumbTextView = new TextView(this);
			mBreadcrumbTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			
			if(mDataManager.getSelectedCollabRoomId() > -1) {
				mBreadcrumbTextView.setText(mDataManager.getSelectedCollabRoomName());
			} else {
				mBreadcrumbTextView.setText(mDataManager.getActiveIncidentName());
			}
			
			mBreadcrumbTextView.setGravity(Gravity.CENTER_VERTICAL| Gravity.RIGHT);
			mBreadcrumbTextView.setTextSize(18);
			getSupportActionBar().setCustomView(mBreadcrumbTextView);
			getSupportActionBar().setDisplayShowCustomEnabled(true);
		}

		if(mLastPosition != NavigationOptions.OVERVIEW.getValue()) {
			
			if(mDataManager.getSelectedCollabRoomId() > -1) {
				mBreadcrumbTextView.setText(mDataManager.getSelectedCollabRoomName());
			} else {
				mBreadcrumbTextView.setText(mDataManager.getActiveIncidentName());
			}
			
			mBreadcrumbTextView.setVisibility(View.VISIBLE);
		} else {
			mBreadcrumbTextView.setText("");
			mBreadcrumbTextView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if(savedInstanceState.containsKey(STATE_BACK_STACK)) {
			mBackStack = new Stack<Integer>();
			mBackStack.addAll(savedInstanceState.getIntegerArrayList(STATE_BACK_STACK));
		}
		
		if(savedInstanceState.containsKey(STATE_REPORT_OPENED_FROM_MAP)) {
			mReportOpenedFromMap = savedInstanceState.getBoolean(STATE_REPORT_OPENED_FROM_MAP);
		}
		
		if(savedInstanceState.containsKey(STATE_IS_VIEW_DAMAGE_REPORT)) {
			mViewDamageReport = savedInstanceState.getBoolean(STATE_IS_VIEW_DAMAGE_REPORT);
		}
		
		if(savedInstanceState.containsKey(STATE_IS_EDIT_DAMAGE_REPORT)) {
			mEditDamageReport = savedInstanceState.getBoolean(STATE_IS_EDIT_DAMAGE_REPORT);
		}
		
		if(savedInstanceState.containsKey(STATE_IS_EDIT_FIELD_REPORT)) {
			mEditFieldReport = savedInstanceState.getBoolean(STATE_IS_EDIT_FIELD_REPORT);
		}
		
		if(savedInstanceState.containsKey(STATE_IS_VIEW_FIELD_REPORT)) {
			mViewFieldReport = savedInstanceState.getBoolean(STATE_IS_VIEW_FIELD_REPORT);
		}
		
		if(savedInstanceState.containsKey(STATE_IS_EDIT_SIMPLE_REPORT)) {
			mEditSimpleReport = savedInstanceState.getBoolean(STATE_IS_EDIT_SIMPLE_REPORT);
		}
		
		if(savedInstanceState.containsKey(STATE_IS_VIEW_SIMPLE_REPORT)) {
			mViewSimpleReport = savedInstanceState.getBoolean(STATE_IS_VIEW_SIMPLE_REPORT);
		}
		
		if(savedInstanceState.containsKey(STATE_IS_EDIT_RESOURCE_REQUEST)) {
			mEditResourceRequest = savedInstanceState.getBoolean(STATE_IS_EDIT_RESOURCE_REQUEST);
		}
		
		if(savedInstanceState.containsKey(STATE_IS_VIEW_RESOURCE_REQUEST)) {
			mViewResourceRequest = savedInstanceState.getBoolean(STATE_IS_VIEW_RESOURCE_REQUEST);
		}
		
		if(savedInstanceState.containsKey(STATE_IS_EDIT_WEATHER_REPORT)) {
			mEditWeatherReport = savedInstanceState.getBoolean(STATE_IS_EDIT_WEATHER_REPORT);
		}
		
		if(savedInstanceState.containsKey(STATE_IS_VIEW_WEATHER_REPORT)) {
			mViewWeatherReport = savedInstanceState.getBoolean(STATE_IS_VIEW_WEATHER_REPORT);
		}
		
		if(savedInstanceState.containsKey(STATE_IS_VIEW_UXO_REPORT)) {
			mViewUxoReport = savedInstanceState.getBoolean(STATE_IS_VIEW_UXO_REPORT);
		}
		
		if(savedInstanceState.containsKey(STATE_IS_EDIT_UXO_REPORT)) {
			mEditUxoReport = savedInstanceState.getBoolean(STATE_IS_EDIT_UXO_REPORT);
		}
		
		if(savedInstanceState.containsKey("dr_edit_json")) {
			mOpenedDamageReportPayload = savedInstanceState.getString("dr_edit_json");
		}
		
		if(savedInstanceState.containsKey("dr_edit_id")) {
			mOpenedDamageReportId = savedInstanceState.getLong("dr_edit_id");
		}
		
		if(savedInstanceState.containsKey("sr_edit_json")) {
			mOpenedSimpleReportPayload = savedInstanceState.getString("sr_edit_json");
		}
		
		if(savedInstanceState.containsKey("sr_edit_id")) {
			mOpenedSimpleReportId = savedInstanceState.getLong("sr_edit_id");
		}
		
		if(savedInstanceState.containsKey("fr_edit_json")) {
			mOpenedFieldReportPayload = savedInstanceState.getString("fr_edit_json");
		}
		
		if(savedInstanceState.containsKey("fr_edit_id")) {
			mOpenedFieldReportId = savedInstanceState.getLong("fr_edit_id");
		}
		
		if(savedInstanceState.containsKey("resreq_edit_json")) {
			mOpenedResourceRequestPayload = savedInstanceState.getString("resreq_edit_json");
		}
		
		if(savedInstanceState.containsKey("resreq_edit_id")) {
			mOpenedResourceRequestId = savedInstanceState.getLong("resreq_edit_id");
		}
		
		if(savedInstanceState.containsKey("wr_edit_json")) {
			mOpenedWeatherReportPayload = savedInstanceState.getString("wr_edit_json");
		}
		
		if(savedInstanceState.containsKey("wr_edit_id")) {
			mOpenedWeatherReportId = savedInstanceState.getLong("wr_edit_id");
		}
		
		if(savedInstanceState.containsKey("uxo_edit_json")) {
			mOpenedUxoReportPayload = savedInstanceState.getString("uxo_edit_json");
		}
		
		if(savedInstanceState.containsKey("uxo_edit_id")) {
			mOpenedUxoReportId = savedInstanceState.getLong("uxo_edit_id");
		}
		
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			mLastPosition = savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM);
			onNavigationItemSelected(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM), -1);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, mLastPosition);
		
		ArrayList<Integer> list = new ArrayList<Integer>(mBackStack);
		outState.putIntegerArrayList(STATE_BACK_STACK, list);
		
		outState.putBoolean(STATE_REPORT_OPENED_FROM_MAP, mReportOpenedFromMap);
		outState.putBoolean(STATE_IS_EDIT_DAMAGE_REPORT, mEditDamageReport);
		outState.putBoolean(STATE_IS_VIEW_DAMAGE_REPORT, mViewDamageReport);
		outState.putBoolean(STATE_IS_EDIT_FIELD_REPORT, mEditFieldReport);
		outState.putBoolean(STATE_IS_VIEW_FIELD_REPORT, mViewFieldReport);
		outState.putBoolean(STATE_IS_EDIT_SIMPLE_REPORT, mEditSimpleReport);
		outState.putBoolean(STATE_IS_VIEW_SIMPLE_REPORT, mViewSimpleReport);
		outState.putBoolean(STATE_IS_EDIT_RESOURCE_REQUEST, mEditResourceRequest);
		outState.putBoolean(STATE_IS_VIEW_RESOURCE_REQUEST, mViewResourceRequest);
		outState.putBoolean(STATE_IS_EDIT_WEATHER_REPORT, mEditWeatherReport);
		outState.putBoolean(STATE_IS_VIEW_WEATHER_REPORT, mViewWeatherReport);
		outState.putBoolean(STATE_IS_EDIT_UXO_REPORT, mEditUxoReport);
		outState.putBoolean(STATE_IS_VIEW_UXO_REPORT, mViewUxoReport);
		
		if(mSimpleReportFragment != null) {
			mOpenedSimpleReportPayload = mSimpleReportFragment.getPayload().toJsonString();
			mOpenedSimpleReportId = mSimpleReportFragment.getReportId();
		}
		
		if(mOpenedSimpleReportPayload != null) {
			outState.putString("sr_edit_json", mOpenedSimpleReportPayload);
			outState.putLong("sr_edit_id", mOpenedSimpleReportId);
		}
		
		if(mDamageReportFragment != null) {
			mOpenedDamageReportPayload = mDamageReportFragment.getPayload().toJsonString();
			mOpenedDamageReportId = mDamageReportFragment.getReportId();
		}
		
		if(mOpenedDamageReportPayload != null) {
			outState.putString("dr_edit_json", mOpenedDamageReportPayload);
			outState.putLong("dr_edit_id", mOpenedDamageReportId);
		}
		
		if(mFieldReportFragment != null) {
			mOpenedFieldReportPayload = mFieldReportFragment.getPayload().toJsonString();
			mOpenedFieldReportId = mFieldReportFragment.getReportId();
		}
		
		if(mOpenedFieldReportPayload != null) {
			outState.putString("fr_edit_json", mOpenedFieldReportPayload);
			outState.putLong("fr_edit_id", mOpenedFieldReportId);
		}
		
		if(mResourceRequestFragment != null) {
			mOpenedResourceRequestPayload = mResourceRequestFragment.getPayload().toJsonString();
			mOpenedResourceRequestId = mResourceRequestFragment.getReportId();
		}
		
		if(mOpenedResourceRequestPayload != null) {
			outState.putString("resreq_edit_json", mOpenedResourceRequestPayload);
			outState.putLong("resreq_edit_id", mOpenedResourceRequestId);
		}
		
		if(mWeatherReportFragment != null) {
			mOpenedWeatherReportPayload = mWeatherReportFragment.getPayload().toJsonString();
			mOpenedWeatherReportId = mWeatherReportFragment.getReportId();
		}
		
		if(mOpenedWeatherReportPayload != null) {
			outState.putString("wr_edit_json", mOpenedWeatherReportPayload);
			outState.putLong("wr_edit_id", mOpenedWeatherReportId);
		}
		
		if(mUxoReportFragment != null) {
			mOpenedUxoReportPayload = mUxoReportFragment.getPayload().toJsonString();
			mOpenedUxoReportId = mUxoReportFragment.getReportId();
		}
		
		if(mOpenedUxoReportPayload != null) {
			outState.putString("uxo_edit_json", mOpenedUxoReportPayload);
			outState.putLong("uxo_edit_id", mOpenedUxoReportId);
		}
		
		if(mCatanRequestFragment != null) {
			mOpenedCatanRequestPayload = mCatanRequestFragment.getPayload().toJsonString();
			mOpenedCatanRequestId = mCatanRequestFragment.getReportId();
		}
		
		if(mOpenedCatanRequestPayload != null) {
			outState.putString("cr_edit_json", mOpenedCatanRequestPayload);
			outState.putLong("cr_edit_id", mOpenedCatanRequestId);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_logout, menu);
		LowDataModeIcon = menu.findItem(R.id.action_low_data_mode);
		if(mDataManager.getLowDataMode()){
			LowDataModeIcon.setIcon(R.drawable.stat_sys_r_signal_1_cdma);
		}else{
			LowDataModeIcon.setIcon(R.drawable.stat_sys_r_signal_4_cdma);
		}
		
		
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		onNavigationItemSelected(NavigationOptions.OVERVIEW.getValue(), -1);
	    		break;
	        case R.id.action_settings:
				Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
				if(mDataManager.getSelectedCollabRoomId() == -1) {
			        intent.putExtra("hideGarCollab", true);
				}
				
		        intent.putExtra("currentServer", mDataManager.getServer());
				startActivityForResult(intent, 1001);
		        break;
		        
	        case R.id.action_logout:
	        	onNavigationItemSelected(NavigationOptions.LOGOUT.getValue(), -1);
	        	break;
	        	
	        case R.id.action_help:
	        	
	        	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://public.nics.ll.mit.edu/nicshelp/"));
	        	startActivity(browserIntent);
	        	
	        	break;
	        	
	        case R.id.action_about:
	        	intent = new Intent(MainActivity.this, AboutActivity.class);
				startActivity(intent);
				break;
		        
	        case R.id.addDamageReportOption:
				if(mDamageReportFragment == null) {
					mDamageReportFragment = new DamageReportFragment();
				}
				mFragmentManager.beginTransaction().replace(R.id.container, mDamageReportFragment).commit();
				mFragmentManager.executePendingTransactions();
				
				mDamageReportFragment.populate(mDataManager.getIncidentInfoJson(), -1L, true);
				mOpenedDamageReportPayload = mDamageReportFragment.getPayload().toJsonString();
				mOpenedDamageReportId = -1;
				
				mEditDamageReport = true;
				mViewDamageReport = false;
				
				break;
				
	        case R.id.addFieldReportOption:
				if(mFieldReportFragment == null) {
					mFieldReportFragment = new FieldReportFragment();
				}
				mFragmentManager.beginTransaction().replace(R.id.container, mFieldReportFragment).commit();
				mFragmentManager.executePendingTransactions();
				
				mFieldReportFragment.populate(mDataManager.getIncidentInfoJson(), -1L, true);
				mOpenedFieldReportPayload = mFieldReportFragment.getPayload().toJsonString();
				mOpenedFieldReportId = -1;
				
				mEditFieldReport = true;
				mViewFieldReport = false;
				
				break;
	        case R.id.addSimpleReportOption:
				if(mSimpleReportFragment == null) {
					mSimpleReportFragment = new GeneralMessageFragment();
				}
				mFragmentManager.beginTransaction().replace(R.id.container, mSimpleReportFragment).commit();
				mFragmentManager.executePendingTransactions();
				
				mSimpleReportFragment.populate(mDataManager.getIncidentInfoJson(), -1L, true);
				mOpenedSimpleReportPayload = mSimpleReportFragment.getPayload().toJsonString();
				mOpenedSimpleReportId = -1;
				
				mEditSimpleReport = true;
				mViewSimpleReport = false;
				
				break;
	        case R.id.addWeatherReportOption:
				if(mWeatherReportFragment == null) {
					mWeatherReportFragment = new WeatherReportFragment();
				}
				mFragmentManager.beginTransaction().replace(R.id.container, mWeatherReportFragment).commit();
				mFragmentManager.executePendingTransactions();
				
				mWeatherReportFragment.populate(mDataManager.getIncidentInfoJson(), -1L, true);
				mOpenedWeatherReportPayload = mWeatherReportFragment.getPayload().toJsonString();
				mOpenedWeatherReportId = -1;
				
				mEditWeatherReport = true;
				mViewWeatherReport = false;
				
				break;
				
	        case R.id.addUxoReportOption:
				if(mUxoReportFragment == null) {
					mUxoReportFragment = new UxoReportFragment();
				}
				mFragmentManager.beginTransaction().replace(R.id.container, mUxoReportFragment).commit();
				mFragmentManager.executePendingTransactions();
				
				mUxoReportFragment.populate(mDataManager.getIncidentInfoJson(), -1L, true);
				mOpenedUxoReportPayload = mUxoReportFragment.getPayload().toJsonString();
				mOpenedUxoReportId = -1;
				
				mEditUxoReport = true;
				mViewUxoReport = false;
				
				break;
				
	        case R.id.markAllAsRead:
	        	mUnreadMessageManager.MarkAllMessagesAsRead();
	        	mSimpleReportListFragment.MarkAllMessagesAsRead();
	        	break;
	        case R.id.addResourceRequestOption:
				if(mResourceRequestFragment == null) {
					mResourceRequestFragment = new ResourceRequestFragment();
				}
				mFragmentManager.beginTransaction().replace(R.id.container, mResourceRequestFragment).commit();
				mFragmentManager.executePendingTransactions();
				
				mResourceRequestFragment.populate(mDataManager.getIncidentInfoJson(), -1L, true);
				mOpenedResourceRequestPayload = mResourceRequestFragment.getPayload().toJsonString();
				mOpenedResourceRequestId = -1;
				
				mEditResourceRequest = true;
				mViewResourceRequest = false;
				
				break;
	        case R.id.copyDamageReportOption:
				if(mDamageReportFragment == null) {
					mDamageReportFragment = new DamageReportFragment();
				}
				mFragmentManager.beginTransaction().replace(R.id.container, mDamageReportFragment).commit();
				mFragmentManager.executePendingTransactions();
				
				mDamageReportFragment.populate(mDamageReportFragment.getFormString(), -1L, true);
				mOpenedDamageReportPayload = mDamageReportFragment.getPayload().toJsonString();
				mOpenedDamageReportId = -1;
				
				mEditDamageReport = true;
				mViewDamageReport = false;
				
				break;
	        case R.id.copyGeneralMessageOption:
				if(mSimpleReportFragment == null) {
					mSimpleReportFragment = new GeneralMessageFragment();
				}
				mFragmentManager.beginTransaction().replace(R.id.container, mSimpleReportFragment).commit();
				mFragmentManager.executePendingTransactions();
				
				mSimpleReportFragment.populate(mSimpleReportFragment.getFormString(), -1L, true);
				mOpenedSimpleReportPayload = mSimpleReportFragment.getPayload().toJsonString();
				mOpenedSimpleReportId = -1;
				
				mEditSimpleReport = true;
				mViewSimpleReport = false;
				
				break;
	        case R.id.copyFieldReportOption:
				if(mFieldReportFragment == null) {
					mFieldReportFragment = new FieldReportFragment();
				}
				mFragmentManager.beginTransaction().replace(R.id.container, mFieldReportFragment).commit();
				mFragmentManager.executePendingTransactions();
				
				mFieldReportFragment.populate(mFieldReportFragment.getFormString(), -1L, true);
				mOpenedFieldReportPayload = mFieldReportFragment.getPayload().toJsonString();
				mOpenedFieldReportId = -1;
				
				mEditFieldReport = true;
				mViewFieldReport = false;
				
				break;
	        case R.id.copyWeatherReportOption:
				if(mWeatherReportFragment == null) {
					mWeatherReportFragment = new WeatherReportFragment();
				}
				mFragmentManager.beginTransaction().replace(R.id.container, mWeatherReportFragment).commit();
				mFragmentManager.executePendingTransactions();
				
				mWeatherReportFragment.populate(mWeatherReportFragment.getFormString(), -1L, true);
				mOpenedWeatherReportPayload = mWeatherReportFragment.getPayload().toJsonString();
				mOpenedWeatherReportId = -1;
				
				mEditWeatherReport = true;
				mViewWeatherReport = false;
				
				break;
				
	        case R.id.copyUxoReportOption:
				if(mUxoReportFragment == null) {
					mUxoReportFragment = new UxoReportFragment();
				}
				mFragmentManager.beginTransaction().replace(R.id.container, mUxoReportFragment).commit();
				mFragmentManager.executePendingTransactions();
				
				mUxoReportFragment.populate(mUxoReportFragment.getFormString(), -1L, true);
				mOpenedUxoReportPayload = mUxoReportFragment.getPayload().toJsonString();
				mOpenedUxoReportId = -1;
				
				mEditUxoReport = true;
				mViewUxoReport = false;
				
				break;
				
			case R.id.action_switch_orgs:
	        	showOrgSelector();
	        	break;
	        	
			case R.id.action_low_data_mode:
					
				mDataManager.setLowDataMode(!mDataManager.getLowDataMode());
				if(mDataManager.getLowDataMode()){
					LowDataModeIcon.setIcon(R.drawable.stat_sys_r_signal_1_cdma);
				}else{
					LowDataModeIcon.setIcon(R.drawable.stat_sys_r_signal_4_cdma);
				}
		        break;
	    }

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 500) {
			BluetoothLRF.getInstance(this, null).findBT();
		}
		
		if(requestCode == 1001 && data != null) {
			if(data.getBooleanExtra("logoutAndClear", false)) {
				mDataManager.setCurrentIncidentData(null, -1, "");	
				mDataManager.setSelectedCollabRoom("N/A", -1);
				onNavigationItemSelected(NavigationOptions.LOGOUT.getValue(), 0);
			} else if(data.getBooleanExtra("loadGAR", false)) {
				onNavigationItemSelected(NavigationOptions.GAR.getValue(), -1);
			}
		}
		
		if(mEditDamageReport || mViewDamageReport) {
			if(mDamageReportFragment == null && mOpenedDamageReportPayload != null) {
				DamageReportPayload payload = new Gson().fromJson(mOpenedDamageReportPayload, DamageReportPayload.class);
				payload.parse();
				
				openDamageReport(payload, mEditDamageReport);
			}
			
			if(mDamageReportFragment != null) {
				mDamageReportFragment.onActivityResult(requestCode, resultCode, data);
			}
		}
		if(mEditSimpleReport || mViewSimpleReport) {
			if(mSimpleReportFragment == null && mOpenedSimpleReportPayload != null) {
				SimpleReportPayload payload = new Gson().fromJson(mOpenedSimpleReportPayload, SimpleReportPayload.class);
				payload.parse();
				
				openSimpleReport(payload, mEditSimpleReport);
			}
			
			if(mSimpleReportFragment != null) {
				mSimpleReportFragment.onActivityResult(requestCode, resultCode, data);
			}
		} 
		if(mEditUxoReport || mViewUxoReport) {
			if(mUxoReportFragment == null && mOpenedUxoReportPayload != null) {
				UxoReportPayload payload = new Gson().fromJson(mOpenedUxoReportPayload, UxoReportPayload.class);
				payload.parse();
				
				openUxoReport(payload, mEditUxoReport);
			}
			
			if(mUxoReportFragment != null) {
				mUxoReportFragment.onActivityResult(requestCode, resultCode, data);
			}
		}else{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	@Override
	public boolean onNavigationItemSelected(final int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.

		boolean prompt = false;
		
		if(position == mLastPosition && id == -2) {
			if(mEditFieldReport) {
				mEditFieldReport = false;
			} else if(mEditSimpleReport) {
				mEditSimpleReport = false;
			} else if(mEditResourceRequest) {
				mEditResourceRequest = false;
			} else if(mEditDamageReport) {
				mEditDamageReport = false;
			}else if(mEditWeatherReport) {
				mEditWeatherReport = false;
			} else if(mEditUxoReport) {
				mEditUxoReport = false;
			}
		}
		
		if(!isEditReport()) {
	    	mPreventNavigation = false;
		} else if(position == mLastPosition && position == id) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			String title = getString(R.string.confirm_continue_to_title, navDropdownOptions[position]);
			String message = getString(R.string.confirm_continue_to_description);
			
			if(mEditFieldReport) {
				prompt = true;
				message = String.format(message, getString(R.string.FIELDREPORT));
			} else if(mEditSimpleReport) {
				prompt = true;
				message = String.format(message, getString(R.string.GENERALMESSAGE));
			} else if(mEditResourceRequest) {
				prompt = true;
				message = String.format(message, getString(R.string.RESOURCEREQUEST));
			} else if(mEditDamageReport) {
				prompt = true;
				message = String.format(message, getString(R.string.DAMAGESURVEY));
			}else if(mEditWeatherReport) {
				prompt = true;
				message = String.format(message, getString(R.string.WEATHERREPORT));
			}else if(mEditUxoReport) {
				prompt = true;
				message = String.format(message, getString(R.string.UXOREPORT));
			}
				
			if(prompt) {
				builder.setTitle(title);
				builder.setMessage(message);
				
				builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		            	mPreventNavigation = false;
		            	
		            	mEditDamageReport = false;
		            	mEditFieldReport = false;
		            	mEditResourceRequest = false;
		            	mEditSimpleReport = false;
		            	mEditWeatherReport = false; 
		            	mEditUxoReport = false;
		            	onNavigationItemSelected(position, id);
		            }
		        });
				builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
		            	dialog.dismiss();
		            	mPreventNavigation  = true;

						onNavigationItemSelected(mLastPosition, -1);
		            }
		        });
				
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		}
		
		
		if(!prompt && !mPreventNavigation) {
			Fragment fragment = null;
			
			if(mMapMarkupFragment != null && !mMapMarkupFragment.isVisible()) {
				mMapMarkupFragment.removeMapFragment();
			}
			
			mDataManager.requestMarkupRepeating(mDataManager.getCollabroomDataRate(), false);
			mDataManager.requestChatMessagesRepeating(mDataManager.getCollabroomDataRate(), false);
			mDataManager.requestDamageReportRepeating(mDataManager.getIncidentDataRate(), false);
			mDataManager.requestFieldReportRepeating(mDataManager.getIncidentDataRate(), false);
			mDataManager.requestSimpleReportRepeating(mDataManager.getIncidentDataRate(), false);
			mDataManager.requestResourceRequestRepeating(mDataManager.getIncidentDataRate(), false);
			mDataManager.requestWeatherReportRepeating(mDataManager.getIncidentDataRate(), false);
			mDataManager.requestUxoReportRepeating(mDataManager.getIncidentDataRate(), false);
			mDataManager.requestCatanRequestRepeating(mDataManager.getIncidentDataRate(), false);
			
			boolean showIncidentName = false;
			
			Log.w(Constants.PHINICS_DEBUG_ANDROID_TAG, "New view is: " +  navDropdownOptions[position]);
			
			switch(NavigationOptions.values()[position]) {
				case OVERVIEW:
					if(mOverviewFragment == null) {
						mOverviewFragment = new OverviewFragment();
					}
					fragment = mOverviewFragment;
					mUnreadMessageManager.SaveToFile();
					break;
				case GENERALMESSAGE:
					if(mSimpleReportListFragment == null) {
						mSimpleReportListFragment = new SimpleReportListFragment();
					}
					fragment = mSimpleReportListFragment;
					
					if((mViewSimpleReport || mEditSimpleReport) && mOpenedSimpleReportPayload != null) {
						SimpleReportPayload payload = new Gson().fromJson(mOpenedSimpleReportPayload, SimpleReportPayload.class);
						payload.parse();
						
						openSimpleReport(payload, this.mEditSimpleReport);
						fragment = mSimpleReportFragment;
					}
					mDataManager.requestSimpleReportRepeating(mDataManager.getIncidentDataRate(), false);
					showIncidentName = true;
					
					break;
					
				case FIELDREPORT:
					if(mFieldReportListFragment == null) {
						mFieldReportListFragment = new FieldReportListFragment();
					}
					fragment = mFieldReportListFragment;
					
					if((mViewFieldReport || mEditFieldReport) && mOpenedFieldReportPayload != null) {
						FieldReportPayload payload = new Gson().fromJson(mOpenedFieldReportPayload, FieldReportPayload.class);
						payload.parse();
						
						openFieldReport(payload, mEditFieldReport);
						fragment = mFieldReportFragment;
					}
					mDataManager.requestFieldReportRepeating(mDataManager.getIncidentDataRate(), false);
					showIncidentName = true;
					
					break;
					
				case DAMAGESURVEY:
					if(mDamageReportListFragment == null) {
						mDamageReportListFragment = new DamageReportListFragment();
					}
					fragment = mDamageReportListFragment;
					
					if((mViewDamageReport || mEditDamageReport) && mOpenedDamageReportPayload != null) {
						DamageReportPayload payload = new Gson().fromJson(mOpenedDamageReportPayload, DamageReportPayload.class);
						payload.parse();
						
						openDamageReport(payload, mEditDamageReport);
						fragment = mDamageReportFragment;
					}
					mDataManager.requestDamageReportRepeating(mDataManager.getIncidentDataRate(), false);
					showIncidentName = true;
					
					break;
					
				case WEATHERREPORT:
					if(mWeatherReportListFragment == null) {
						mWeatherReportListFragment = new WeatherReportListFragment();
					}
					fragment = mWeatherReportListFragment;
					
					if((mViewWeatherReport || mEditWeatherReport) && mOpenedWeatherReportPayload != null) {
						WeatherReportPayload payload = new Gson().fromJson(mOpenedWeatherReportPayload, WeatherReportPayload.class);
						payload.parse();
						
						openWeatherReport(payload, mEditWeatherReport);
						fragment = mWeatherReportFragment;
					}
					mDataManager.requestWeatherReportRepeating(mDataManager.getIncidentDataRate(), false);
					showIncidentName = true;
					
					break;
				
				case UXOREPORT:
					if(mUxoReportListFragment == null) {
						mUxoReportListFragment = new UxoReportListFragment();
					}
					fragment = mUxoReportListFragment;
					
					if((mViewUxoReport || mEditUxoReport) && mOpenedUxoReportPayload != null) {
						UxoReportPayload payload = new Gson().fromJson(mOpenedUxoReportPayload, UxoReportPayload.class);
						payload.parse();
						
						openUxoReport(payload, mEditUxoReport);
						fragment = mUxoReportFragment;
					}
					mDataManager.requestUxoReportRepeating(mDataManager.getIncidentDataRate(), false);
					showIncidentName = true;
					
					break;
					
				case CATANREQUEST:
					if(mCatanRequestListFragment == null) {
						mCatanRequestListFragment = new CatanRequestListFragment();
					}
					fragment = mCatanRequestListFragment;
					
					if((mViewCatanRequest || mEditCatanRequest) && mOpenedCatanRequestPayload != null) {
						CatanRequestPayload payload = new Gson().fromJson(mOpenedCatanRequestPayload, CatanRequestPayload.class);
						payload.parse();
						
						openCatanRequest(payload, this.mEditCatanRequest);
						fragment = mCatanRequestFragment;
					}
					mDataManager.requestCatanRequestRepeating(mDataManager.getIncidentDataRate(), false);
					showIncidentName = true;
					
					break;
					
				case MAPCOLLABORATION:
					if(mMapMarkupFragment == null) {
						mMapMarkupFragment = new MapMarkupFragment();
					}
					fragment = mMapMarkupFragment;
					
					break;
				case USERINFO:
					if(mUserInfoFragment == null) {
						Bundle args = new Bundle();
						args.putString(FormFragment.SCHEMA_FILE, "userinfo_schema.json");
						
						mUserInfoFragment = new FormFragment();
						mUserInfoFragment.setArguments(args);
					}
					fragment = mUserInfoFragment;
					break;
				case CHATLOG:
					if(mChatFragment == null) {
						mChatFragment = new ChatFragment();
//						mChatFragment = new ChatListFragment();
					}
					mDataManager.requestChatMessagesRepeating(mDataManager.getCollabroomDataRate(), true);
					fragment = mChatFragment;
					
					break;
				case RESOURCEREQUEST:
					if(mResourceRequestListFragment == null) {
						mResourceRequestListFragment = new ResourceRequestListFragment();
					}
					fragment = mResourceRequestListFragment;
					
					if((mViewResourceRequest || mEditResourceRequest) && mOpenedResourceRequestPayload != null) {
						ResourceRequestPayload payload = new Gson().fromJson(mOpenedResourceRequestPayload, ResourceRequestPayload.class);
						payload.parse();
						
						openResourceRequest(payload, mEditResourceRequest);
						fragment = null;
					}
					mDataManager.requestResourceRequestRepeating(mDataManager.getIncidentDataRate(), false);
					showIncidentName = true;
					
					break;
				case GAR:
					if(mGarFragment == null) {
						mGarFragment = new GarFragment();
					}
					fragment = mGarFragment;
					break;
				
				case SELECTINCIDENT:
					mBackStack.clear();
					Intent selectIncidentIntent = new Intent(MainActivity.this, LoginActivity.class);
					selectIncidentIntent.putExtra("hideSplash", true);
					selectIncidentIntent.putExtra("showIncidentSelect", true);
			        startActivity(selectIncidentIntent);
			        finish();
			        break;
					
				case LOGOUT:
					mDataManager.setLoggedIn(false);
					mDataManager.requestLogout();
					mBackStack.clear();
					
					EncryptedPreferences userPreferences = new EncryptedPreferences(this.getSharedPreferences(Constants.PHINICS_USER_PREFERENCES, 0));
					userPreferences.savePreferenceBoolean(Constants.PHINICS_AUTO_LOGIN, false);
					
//					Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//					intent.putExtra("hideSplash", true);
//			        startActivity(intent);
					
//					if(id == -1) {
//						finish();
//					} else {
						Intent intent = new Intent(MainActivity.this, LoginActivity.class);
						intent.putExtra("hideSplash", true);
				        startActivity(intent);
//					}
			        break;
			        
				default:
					break;
			}
			
			try {
				if(fragment != null) {
					mFragmentManager.beginTransaction().replace(R.id.container, fragment).commitAllowingStateLoss();
					//mFragmentManager.executePendingTransactions();
				}
			} catch (Exception ex) {
				Log.e("PHINICS", ex.toString());
			}
			
			// Have to wait for view to exist before populating data
			if(position == NavigationOptions.USERINFO.getValue()) {
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
				  @Override
				  public void run() {
					  UserData data = new UserData(mDataManager.getUserPayload());
					  mUserInfoFragment.populate(data.toJsonString(), false);
				  }
				}, 200);
			}
			
			if(position != NavigationOptions.MAPCOLLABORATION.getValue()) {
				mDataManager.stopPollingMarkup();
			}
	
			if(position != mLastPosition && !mIsBackKey ) {
				mBackStack.push(mLastPosition);
				mLastPosition = position;
			} else {
				mIsBackKey = false;
				mLastPosition = position;
			}

			if(mBreadcrumbTextView != null) {
				if(position == NavigationOptions.OVERVIEW.getValue()) {
					setBreadcrumbText("");
					mBreadcrumbTextView.setVisibility(View.GONE);
	
				} else {
					if(showIncidentName) {
						setBreadcrumbText(mDataManager.getActiveIncidentName() + "\n" + NavigationOptions.values()[position]);
						mDataManager.setCurrentNavigationView(NavigationOptions.values()[position].toString());
					} else {
						String collabRoom = mDataManager.getSelectedCollabRoomName();
						if(collabRoom.equals(getString(R.string.no_selection))){
							setBreadcrumbText("No Room Selected");
						}else{
							setBreadcrumbText(collabRoom);
						}
					}
					mBreadcrumbTextView.setVisibility(View.VISIBLE);
				}
			}
		}
		return true;
	}
	
	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
		if(fragment.getTag() != null) {
			try {
				mLastPosition = Integer.valueOf(fragment.getTag());
			} catch(NumberFormatException e) {
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		mIsBackKey = true;
		if(mViewMapLocationPicker){
			mMapMarkupLocationPickerFragment.BackButtonPressed();
		}else if(mEditSimpleReport || mViewSimpleReport) {
			mViewSimpleReport = false;
			mOpenedSimpleReportPayload = null;
			mOpenedDamageReportId = -1;
			if( mReportOpenedFromMap) {
				mReportOpenedFromMap = false;
				mFragmentManager.beginTransaction().remove(mSimpleReportFragment).commit();
				mFragmentManager.executePendingTransactions();
				mBackStack.pop();
				onNavigationItemSelected(NavigationOptions.MAPCOLLABORATION.getValue(), -1);
			} else {
				onNavigationItemSelected(NavigationOptions.GENERALMESSAGE.getValue(), NavigationOptions.GENERALMESSAGE.getValue());
			}
		} else if(mEditFieldReport || mViewFieldReport) {
			mViewFieldReport = false;
			mOpenedFieldReportPayload = null;
			mOpenedFieldReportId = -1;
			onNavigationItemSelected(NavigationOptions.FIELDREPORT.getValue(), NavigationOptions.FIELDREPORT.getValue());
		} else if(mEditWeatherReport || mViewWeatherReport) {
			mViewWeatherReport = false;
			mOpenedWeatherReportPayload = null;
			mOpenedWeatherReportId = -1;
			onNavigationItemSelected(NavigationOptions.WEATHERREPORT.getValue(), NavigationOptions.WEATHERREPORT.getValue());
		}  else if(mEditResourceRequest || mViewResourceRequest) {
			mViewResourceRequest = false;
			mOpenedResourceRequestPayload = null;
			mOpenedResourceRequestId = -1;
			onNavigationItemSelected(NavigationOptions.RESOURCEREQUEST.getValue(), NavigationOptions.RESOURCEREQUEST.getValue());
		} else if(mEditUxoReport || mViewUxoReport) {
			mViewUxoReport = false;
			mOpenedUxoReportPayload = null;
			mOpenedUxoReportId = -1;
			onNavigationItemSelected(NavigationOptions.UXOREPORT.getValue(), NavigationOptions.UXOREPORT.getValue());
		} else if(mEditCatanRequest|| mViewCatanRequest) {
			mViewCatanRequest = false;
			mOpenedCatanRequestPayload = null;
			mOpenedCatanRequestId = -1;
			onNavigationItemSelected(NavigationOptions.CATANREQUEST.getValue(), NavigationOptions.CATANREQUEST.getValue());		
		} else if(mEditDamageReport || mViewDamageReport) {
			mViewDamageReport = false;
			mOpenedDamageReportPayload = null;
			mOpenedDamageReportId = -1;
			if( mReportOpenedFromMap) {
				mReportOpenedFromMap = false;
				mFragmentManager.beginTransaction().remove(mDamageReportFragment).commit();
				mFragmentManager.executePendingTransactions();
				mBackStack.pop();
				onNavigationItemSelected(NavigationOptions.MAPCOLLABORATION.getValue(), -1);
			} else {
				onNavigationItemSelected(NavigationOptions.DAMAGESURVEY.getValue(), NavigationOptions.DAMAGESURVEY.getValue());
			}
		} else if(mBackStack.size() > 0) {
			onNavigationItemSelected(mBackStack.pop(), -1);
		} else {
			if(mDataManager.isLoggedIn()) {
				mLastPosition = NavigationOptions.OVERVIEW.getValue();
			} else {
				mLastPosition = NavigationOptions.LOGOUT.getValue();
			}
			mIsBackKey = false;
			moveTaskToBack(true);
		}
	}
	
	public void openDamageReport(DamageReportPayload damageReportPayload, boolean editable) {
		if(mDamageReportFragment == null) {
			mDamageReportFragment = new DamageReportFragment();
		}

		boolean hideCopy = false;
		if(mMapMarkupFragment != null && mMapMarkupFragment.isVisible() || mReportOpenedFromMap) {
			mFragmentManager.beginTransaction().add(R.id.container, mDamageReportFragment).commit();
			mReportOpenedFromMap = true;
			hideCopy = true;
		} else {
			mFragmentManager.beginTransaction().replace(R.id.container, mDamageReportFragment).commit();
		}
		mFragmentManager.executePendingTransactions();
		
		damageReportPayload.setDraft(editable);
		mOpenedDamageReportPayload = damageReportPayload.toJsonString();
		mDamageReportFragment.setPayload(damageReportPayload, editable);

		if(editable) {
			mEditDamageReport = true;
			mViewDamageReport = false;
		} else {
			mEditDamageReport = false;
			mViewDamageReport = true;
		}
		
		mDamageReportFragment.hideCopy(hideCopy);
	}

	public void openFieldReport(FieldReportPayload fieldReportPayload, boolean editable) {
		if(mFieldReportFragment == null) {
			mFieldReportFragment = new FieldReportFragment();
		}
		mFragmentManager.beginTransaction().replace(R.id.container, mFieldReportFragment).commit();
		mFragmentManager.executePendingTransactions();

		fieldReportPayload.setDraft(editable);
		mOpenedFieldReportPayload = fieldReportPayload.toJsonString();
		mFieldReportFragment.setPayload(fieldReportPayload, editable);
		
		if(editable) {
			mEditFieldReport = true;
			mViewFieldReport = false;
		} else {
			mEditFieldReport = false;
			mViewFieldReport = true;
		}
	}
	
	public void openResourceRequest(ResourceRequestPayload resourceRequestPayload, boolean editable) {
		if(mResourceRequestFragment == null) {
			mResourceRequestFragment = new ResourceRequestFragment();
		}
		mFragmentManager.beginTransaction().replace(R.id.container, mResourceRequestFragment).commit();
		mFragmentManager.executePendingTransactions();

		resourceRequestPayload.setDraft(editable);
		mOpenedResourceRequestPayload = resourceRequestPayload.toJsonString();
		mResourceRequestFragment.setPayload(resourceRequestPayload, editable);
		
		if(editable) {
			mEditResourceRequest = true;
			mViewResourceRequest = false;
		} else {
			mEditResourceRequest = false;
			mViewResourceRequest = true;
		}
	}

	public void openSimpleReport(SimpleReportPayload simpleReportPayload, boolean editable) {
		if(mSimpleReportFragment == null) {
			mSimpleReportFragment = new GeneralMessageFragment();
		}
		
		boolean hideCopy = false;
		if(mMapMarkupFragment != null && mMapMarkupFragment.isVisible() || mReportOpenedFromMap) {
			mFragmentManager.beginTransaction().add(R.id.container, mSimpleReportFragment).commit();
			mReportOpenedFromMap = true;
			hideCopy = true;
		} else {
			mFragmentManager.beginTransaction().replace(R.id.container, mSimpleReportFragment).commit();
		}
		
		mFragmentManager.executePendingTransactions();

		simpleReportPayload.setDraft(editable);
		mOpenedSimpleReportPayload = simpleReportPayload.toJsonString();
		mSimpleReportFragment.setPayload(simpleReportPayload, editable);
		
		if(editable) {
			mEditSimpleReport = true;
			mViewSimpleReport = false;
		} else {
			mEditSimpleReport = false;
			mViewSimpleReport = true;
		}
		
		mSimpleReportFragment.hideCopy(hideCopy);
	}
	
	public void openWeatherReport(WeatherReportPayload weatherReportPayload, boolean editable) {
		if(mWeatherReportFragment == null) {
			mWeatherReportFragment = new WeatherReportFragment();
		}
		mFragmentManager.beginTransaction().replace(R.id.container, mWeatherReportFragment).commit();
		mFragmentManager.executePendingTransactions();

		weatherReportPayload.setDraft(editable);
		mOpenedWeatherReportPayload = weatherReportPayload.toJsonString();
		mWeatherReportFragment.setPayload(weatherReportPayload, editable);
		
		if(editable) {
			mEditWeatherReport = true;
			mViewWeatherReport = false;
		} else {
			mEditWeatherReport = false;
			mViewWeatherReport = true;
		}
	}
	
	public void openUxoReport(UxoReportPayload uxoReportPayload, boolean editable) {
		if(mUxoReportFragment == null) {
			mUxoReportFragment = new UxoReportFragment();
		}

		boolean hideCopy = false;
		if(mMapMarkupFragment != null && mMapMarkupFragment.isVisible() || mReportOpenedFromMap) {
			mFragmentManager.beginTransaction().add(R.id.container, mUxoReportFragment).commit();
			mReportOpenedFromMap = true;
			hideCopy = true;
		} else {
			mFragmentManager.beginTransaction().replace(R.id.container, mUxoReportFragment).commit();
		}
		mFragmentManager.executePendingTransactions();
		
		uxoReportPayload.setDraft(editable);
		mOpenedUxoReportPayload = uxoReportPayload.toJsonString();
		mUxoReportFragment.setPayload(uxoReportPayload, editable);

		if(editable) {
			mEditUxoReport = true;
			mViewUxoReport = false;
		} else {
			mEditUxoReport = false;
			mViewUxoReport = true;
		}
		
		mUxoReportFragment.hideCopy(hideCopy);
	}
	
	public void openCatanRequest(CatanRequestPayload catanRequestPayload, boolean editable) {
		if(mCatanRequestFragment == null) {
			mCatanRequestFragment = new CatanRequestFragment();
		}
		mFragmentManager.beginTransaction().replace(R.id.container, mCatanRequestFragment).commit();
		
		mFragmentManager.executePendingTransactions();

		catanRequestPayload.setDraft(editable);
		mOpenedCatanRequestPayload = catanRequestPayload.toJsonString();
		mCatanRequestFragment.setPayload(catanRequestPayload, editable);
		
		mViewCatanRequest = true;
	}
	
	public void openMapLocationPicker() {

		int fragmentID =((Fragment) mFragmentManager.findFragmentById(R.id.container)).getId();
		String previousReport = "";
		
		if(mSimpleReportFragment != null){
			if(fragmentID == mSimpleReportFragment.getId()){
				previousReport = getString(R.string.GENERALMESSAGE);
			}
		}if(mDamageReportFragment != null){
			if( fragmentID == mDamageReportFragment.getId()){
				previousReport = getString(R.string.DAMAGESURVEY);
			}
		}if(mWeatherReportFragment != null){
			if(fragmentID == mWeatherReportFragment.getId()){
				previousReport = getString(R.string.WEATHERREPORT);	
			}
		}if(mUxoReportFragment != null){
			if( fragmentID == mUxoReportFragment.getId()){
				previousReport = getString(R.string.UXOREPORT);
			}
		}
		
		if(mMapMarkupLocationPickerFragment == null) {
			mMapMarkupLocationPickerFragment = new MapMarkupLocationPickerFragment(previousReport);
		}else{
			mMapMarkupLocationPickerFragment.setPreviousReport(previousReport);
		}
		mFragmentManager.beginTransaction().replace(R.id.container, mMapMarkupLocationPickerFragment).commit();
		mFragmentManager.executePendingTransactions();
		
		mViewMapLocationPicker = true;
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		mDataManager.stopPollingAlarms();
	}

	public void showLRFError(String name) {
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(getString(R.string.disconnected_device, name));
		alertDialog.setMessage(getString(R.string.disconnected_device_desc));
		alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		});
		alertDialog.show();
	}

	public boolean isEditReport() {
		return mEditFieldReport || mEditSimpleReport || mEditResourceRequest || mEditDamageReport || mEditWeatherReport || mEditUxoReport || mEditCatanRequest;
	}

	public boolean isViewReport() {
		return mViewFieldReport || mViewSimpleReport || mViewResourceRequest || mViewDamageReport || mViewWeatherReport || mEditUxoReport || mEditCatanRequest;
	}

//	@Override
//	public void uncaughtException(Thread thread, Throwable ex) {
//		String error = ex.getClass() + " - " + ex.getLocalizedMessage() + "\n";
//		
//		StackTraceElement[] traceArray = ex.getStackTrace();
//		for(StackTraceElement element : traceArray) {
//			if(element.getClassName().contains("phinics")) {
//				error += element.getClassName() + " - Line: " + element.getLineNumber() + "\n";
//			}
//		}
//
//		mDataManager.addPersonalHistory(error);
//		Log.e("PhinicsError", error);
//		ex.printStackTrace();
//		System.exit(1);
//
//	}
	
	public void setBreadcrumbText(CharSequence charSequence) {
		mBreadcrumbTextView.setText(charSequence);
	}
	
}
