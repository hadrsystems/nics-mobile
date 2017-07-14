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
package scout.edu.mit.ll.nics.android;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.RestClient;
import scout.edu.mit.ll.nics.android.api.data.UserData;
import scout.edu.mit.ll.nics.android.api.payload.CollabroomPayload;
import scout.edu.mit.ll.nics.android.api.payload.OrganizationPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.DamageReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.FieldReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.WeatherReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.ResourceRequestPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;
import scout.edu.mit.ll.nics.android.fragments.ChatListFragment;
import scout.edu.mit.ll.nics.android.fragments.DamageReportFragment;
import scout.edu.mit.ll.nics.android.fragments.DamageReportListFragment;
import scout.edu.mit.ll.nics.android.fragments.FieldReportFragment;
import scout.edu.mit.ll.nics.android.fragments.FieldReportListFragment;
import scout.edu.mit.ll.nics.android.fragments.MapMarkupLocationPickerFragment;
import scout.edu.mit.ll.nics.android.fragments.WeatherReportFragment;
import scout.edu.mit.ll.nics.android.fragments.WeatherReportListFragment;
import scout.edu.mit.ll.nics.android.fragments.FormFragment;
import scout.edu.mit.ll.nics.android.fragments.GarFragment;
import scout.edu.mit.ll.nics.android.fragments.GeneralMessageFragment;
import scout.edu.mit.ll.nics.android.fragments.MapMarkupFragment;
import scout.edu.mit.ll.nics.android.fragments.OverviewFragment;
import scout.edu.mit.ll.nics.android.fragments.ResourceRequestFragment;
import scout.edu.mit.ll.nics.android.fragments.ResourceRequestListFragment;
import scout.edu.mit.ll.nics.android.fragments.SimpleReportListFragment;
import scout.edu.mit.ll.nics.android.utils.Constants;
import scout.edu.mit.ll.nics.android.utils.Constants.NavigationOptions;
import scout.edu.mit.ll.nics.android.utils.EncryptedPreferences;

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
	
	private int mLastPosition = NavigationOptions.OVERVIEW.getValue();
	private FragmentManager mFragmentManager;
	private Stack<Integer> mBackStack;
	
	private OverviewFragment mOverviewFragment;
	public MapMarkupFragment mMapMarkupFragment;
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
	
//	private ChatFragment mChatFragment;
	private ChatListFragment mChatFragment;
	
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
	
	private boolean mMapMarkupOpenTablet = false;
	
	private String[] navDropdownOptions;
	
	protected boolean mPreventNavigation = false;
	
	private TextView mBreadcrumbTextView;
	private boolean mReportOpenedFromMap;
	private String[] mOrgArray;
	
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
		mDataManager = DataManager.getInstance(getApplicationContext(), this);
		
		if(mDataManager.getTabletLayoutOn()){
			setContentView(R.layout.activity_main_tablet);
		}else{
			setContentView(R.layout.activity_main);
		}
		
//		Thread.setDefaultUncaughtExceptionHandler(this);
		
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
			Log.e(Constants.nics_DEBUG_ANDROID_TAG, selectedOrg.toJsonString());
			
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
			
			CollabroomPayload payload = mDataManager.getSelectedCollabRoom();
			
			if(payload.getCollabRoomId() > -1) {
				mBreadcrumbTextView.setText(payload.getName());
			} else {
				mBreadcrumbTextView.setText(mDataManager.getActiveIncidentName());
			}
			
			mBreadcrumbTextView.setGravity(Gravity.CENTER_VERTICAL| Gravity.RIGHT);
			mBreadcrumbTextView.setTextSize(18);
			if(!mDataManager.getTabletLayoutOn()){
				getSupportActionBar().setCustomView(mBreadcrumbTextView);
				getSupportActionBar().setDisplayShowCustomEnabled(true);
			}
		}

		if(mLastPosition != NavigationOptions.OVERVIEW.getValue()) {
			
			CollabroomPayload payload = mDataManager.getSelectedCollabRoom();
			
			if(payload.getCollabRoomId() > -1) {
				mBreadcrumbTextView.setText(payload.getName());
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
		
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			mLastPosition = savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM);
			if(mDataManager.getTabletLayoutOn()){
				onNavigationItemSelected(NavigationOptions.OVERVIEW.getValue(), -1);
			}
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

	public void addSimpleReportToDetailView(boolean isCopy){
		if(mSimpleReportFragment == null) {
			mSimpleReportFragment = new GeneralMessageFragment();
		}
		if(mDataManager.getTabletLayoutOn() && mMapMarkupOpenTablet){
			animateFragmentReplace(R.id.container2, mSimpleReportFragment,false);
		}else{
			animateFragmentReplace(R.id.container, mSimpleReportFragment,false);
		}
		mFragmentManager.executePendingTransactions();
		
		if(isCopy){
			mSimpleReportFragment.populate(mSimpleReportFragment.getFormString(), -1L, true);
		}else{
			mSimpleReportFragment.populate(mDataManager.getIncidentInfoJson(), -1L, true);
		}
		mOpenedSimpleReportPayload = mSimpleReportFragment.getPayload().toJsonString();
		mOpenedSimpleReportId = -1;
		
		mEditSimpleReport = true;
		mViewSimpleReport = false;
	}
	
	public void addDamageReportToDetailView(boolean isCopy){
		if(mDamageReportFragment == null) {
			mDamageReportFragment = new DamageReportFragment();
		}
		if(mDataManager.getTabletLayoutOn() && mMapMarkupOpenTablet){
			animateFragmentReplace(R.id.container2, mDamageReportFragment,false);
		}else{
			animateFragmentReplace(R.id.container, mDamageReportFragment,false);
		}
		mFragmentManager.executePendingTransactions();
		
		if(isCopy){
			mDamageReportFragment.populate(mDamageReportFragment.getFormString(), -1L, true);
		}else{
			mDamageReportFragment.populate(mDataManager.getIncidentInfoJson(), -1L, true);
		}
		mOpenedDamageReportPayload = mDamageReportFragment.getPayload().toJsonString();
		mOpenedDamageReportId = -1;
		
		mEditDamageReport = true;
		mViewDamageReport = false;
	}
	
	public void addWeatherReportToDetailView(boolean isCopy){
		if(mWeatherReportFragment == null) {
			mWeatherReportFragment = new WeatherReportFragment();
		}
		if(mDataManager.getTabletLayoutOn() && mMapMarkupOpenTablet){
			animateFragmentReplace(R.id.container2, mWeatherReportFragment,false);
		}else{
			animateFragmentReplace(R.id.container, mWeatherReportFragment,false);
		}
		mFragmentManager.executePendingTransactions();
		
		if(isCopy){
			mWeatherReportFragment.populate(mWeatherReportFragment.getFormString(), -1L, true);
		}else{
			mWeatherReportFragment.populate(mDataManager.getIncidentInfoJson(), -1L, true);
		}
		mOpenedWeatherReportPayload = mWeatherReportFragment.getPayload().toJsonString();
		mOpenedWeatherReportId = -1;
		
		mEditWeatherReport = true;
		mViewWeatherReport = false;
	}
	
	public void addFieldReportToDetailView(boolean isCopy){
		if(mFieldReportFragment == null) {
			mFieldReportFragment = new FieldReportFragment();
		}
		if(mDataManager.getTabletLayoutOn() && mMapMarkupOpenTablet){
			animateFragmentReplace(R.id.container2, mFieldReportFragment,false);
		}else{
			animateFragmentReplace(R.id.container, mFieldReportFragment,false);
		}
		mFragmentManager.executePendingTransactions();
		
		if(isCopy){
			mFieldReportFragment.populate(mFieldReportFragment.getFormString(), -1L, true);
		}else{
			mFieldReportFragment.populate(mDataManager.getIncidentInfoJson(), -1L, true);
		}
		mOpenedFieldReportPayload = mFieldReportFragment.getPayload().toJsonString();
		mOpenedFieldReportId = -1;
		
		mEditFieldReport = true;
		mViewFieldReport = false;
	}
	
	public void addResourceRequestToDetailView(boolean isCopy){
		if(mResourceRequestFragment == null) {
			mResourceRequestFragment = new ResourceRequestFragment();
		}
		
		if(mDataManager.getTabletLayoutOn() && mMapMarkupOpenTablet){
			animateFragmentReplace(R.id.container2, mResourceRequestFragment,false);
		}else{
			animateFragmentReplace(R.id.container, mResourceRequestFragment,false);
		}
		mFragmentManager.executePendingTransactions();
		
		if(isCopy){
			mResourceRequestFragment.populate(mResourceRequestFragment.getFormString(), -1L, true);
		}else{
			mResourceRequestFragment.populate(mDataManager.getIncidentInfoJson(), -1L, true);
		}
		mOpenedResourceRequestPayload = mResourceRequestFragment.getPayload().toJsonString();
		mOpenedResourceRequestId = -1;
		
		mEditResourceRequest = true;
		mViewResourceRequest = false;
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
				if(mDataManager.getSelectedCollabRoom().getCollabRoomId() == -1) {
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
	        	addDamageReportToDetailView(false);
				break;
				
	        case R.id.addFieldReportOption:
	        	addFieldReportToDetailView(false);
				break;
				
	        case R.id.addSimpleReportOption:
	        	addSimpleReportToDetailView(false);
				break;
				
	        case R.id.addWeatherReportOption:
	        	addWeatherReportToDetailView(false);
				break;
												
	        case R.id.markAllSrAsRead:
	        	mSimpleReportListFragment.MarkAllMessagesAsRead();
	        	break;
	        case R.id.markAllDrAsRead:
	        	mDamageReportListFragment.MarkAllMessagesAsRead();
	        	break;
	        case R.id.markAllWrAsRead:
	        	mWeatherReportListFragment.MarkAllMessagesAsRead();
	        	break;
	        case R.id.markAllFrAsRead:
//	        	mFieldReportListFragment.MarkAllMessagesAsRead();
	        	break;
	        case R.id.markAllResReqAsRead:
//	        	mResourceRequestListFragment.MarkAllMessagesAsRead();
	        	break;
	        case R.id.addResourceRequestOption:
	        	addResourceRequestToDetailView(false);
				break;
				
	        case R.id.copyDamageReportOption:
	        	addDamageReportToDetailView(true);
				break;
				
	        case R.id.copyGeneralMessageOption:
	        	addSimpleReportToDetailView(true);
				break;
				
	        case R.id.copyFieldReportOption:
	        	addFieldReportToDetailView(true);
				break;
				
	        case R.id.copyWeatherReportOption:
	        	addWeatherReportToDetailView(true);
				break;
								
	        case R.id.refreshSimpleReportOption:
	        	mDataManager.requestSimpleReports();
				break;
	        case R.id.refreshDamageReportOption:
	        	mDataManager.requestDamageReports();
				break;
	        case R.id.refreshWeatherReportOption:
	        	mDataManager.requestWeatherReports();
				break;
	        case R.id.refreshFieldReportOption:
	        	mDataManager.requestFieldReports();
				break;
	        case R.id.refreshResourceRequestOption:
	        	mDataManager.requestResourceRequests();
				break;
	        case R.id.refreshChatMessagesOption:
	        	mDataManager.requestChatHistory(mDataManager.getActiveIncidentId(), mDataManager.getSelectedCollabRoom().getCollabRoomId());
				break;
	        case R.id.refreshMapOption:
	        	mDataManager.requestMarkupUpdate();
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
				mDataManager.setSelectedCollabRoom(null);
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
			Fragment fragment2 = null;
			Fragment fragmentOverview = null;
			
			Fragment currentFragment = (Fragment) mFragmentManager.findFragmentById(R.id.container);
			Fragment currentFragment2 = (Fragment) mFragmentManager.findFragmentById(R.id.container2);
			
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
			
			boolean showIncidentName = false;
			
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "New view is: " +  navDropdownOptions[position]);
			
			switch(NavigationOptions.values()[position]) {
				case OVERVIEW:
					if(mOverviewFragment == null) {
						mOverviewFragment = new OverviewFragment();
					}
					fragmentOverview = mOverviewFragment;
					break;
				case GENERALMESSAGE:
					if(currentFragment != null && currentFragment2 != null ){
						if(currentFragment == mSimpleReportFragment && currentFragment2 ==  mSimpleReportListFragment){
							break;
						}
					}
					if(mSimpleReportListFragment == null) {
						mSimpleReportListFragment = new SimpleReportListFragment();
					}
					fragment2 = mSimpleReportListFragment;
					
					//if using tablet view and map is closed
					//set detail view on left side
					if(mDataManager.getTabletLayoutOn() && !mMapMarkupOpenTablet){
						
						SimpleReportPayload payload = new Gson().fromJson(mOpenedSimpleReportPayload, SimpleReportPayload.class);
						if(payload == null){
							payload = mDataManager.getLastSimpleReportPayload();
						}
						if(payload == null){
							addSimpleReportToDetailView(false);
						}else{
							
							payload.parse();
							openSimpleReport(payload, mEditSimpleReport);
						}
						
						mBackStack.clear();
					}else if((mViewSimpleReport || mEditSimpleReport) && mOpenedSimpleReportPayload != null) {
						SimpleReportPayload payload = new Gson().fromJson(mOpenedSimpleReportPayload, SimpleReportPayload.class);
						payload.parse();
						
						openSimpleReport(payload, this.mEditSimpleReport);
						if(!mMapMarkupOpenTablet && mDataManager.getTabletLayoutOn()){
							fragment = mSimpleReportListFragment;
							mBackStack.clear();
						}else{
							fragment2 = mSimpleReportFragment;
						}
					}
					mDataManager.requestSimpleReportRepeating(mDataManager.getIncidentDataRate(), false);
					showIncidentName = true;
					
					break;
					
				case FIELDREPORT:
					if(currentFragment != null && currentFragment2 != null ){
						if(currentFragment == mFieldReportFragment && currentFragment2 ==  mFieldReportListFragment){
							break;
						}
					}
					
					if(mFieldReportListFragment == null) {
						mFieldReportListFragment = new FieldReportListFragment();
					}
					fragment2 = mFieldReportListFragment;
					
					//if using tablet view and map is closed
					//set detail view on left side
					if(mDataManager.getTabletLayoutOn() && !mMapMarkupOpenTablet){
						
						FieldReportPayload payload = new Gson().fromJson(mOpenedFieldReportPayload, FieldReportPayload.class);
						if(payload == null){
							payload = mDataManager.getLastFieldReportPayload();
						}
						if(payload == null){
							addFieldReportToDetailView(false);
						}else{
							payload.parse();
							openFieldReport(payload, mEditFieldReport);
						}
						
						mBackStack.clear();
					}else if((mViewFieldReport || mEditFieldReport) && mOpenedFieldReportPayload != null) {
						FieldReportPayload payload = new Gson().fromJson(mOpenedFieldReportPayload, FieldReportPayload.class);
						payload.parse();
						
						openFieldReport(payload, mEditFieldReport);
						if(!mMapMarkupOpenTablet && mDataManager.getTabletLayoutOn()){
							fragment = mFieldReportListFragment;
							mBackStack.clear();
						}else{
							fragment2 = mFieldReportFragment;
						}
					}
					mDataManager.requestFieldReportRepeating(mDataManager.getIncidentDataRate(), false);
					showIncidentName = true;
					
					break;
					
				case DAMAGESURVEY:
					
					if(currentFragment != null && currentFragment2 != null ){
						if(currentFragment == mDamageReportFragment && currentFragment2 ==  mDamageReportListFragment){
							break;
						}
					}
					
					if(mDamageReportListFragment == null) {
						mDamageReportListFragment = new DamageReportListFragment();
					}
					fragment2 = mDamageReportListFragment;
					
					//if using tablet view and map is closed
					//set detail view on left side
					if(mDataManager.getTabletLayoutOn() && !mMapMarkupOpenTablet){
						
						DamageReportPayload payload = new Gson().fromJson(mOpenedDamageReportPayload, DamageReportPayload.class);
						if(payload == null){
							payload = mDataManager.getLastDamageReportPayload();
						}
						if(payload == null){
							addDamageReportToDetailView(false);
						}else{
							payload.parse();
							openDamageReport(payload, mEditDamageReport);
						}
						
						mBackStack.clear();
					}else if((mViewDamageReport || mEditDamageReport) && mOpenedDamageReportPayload != null) {
						DamageReportPayload payload = new Gson().fromJson(mOpenedDamageReportPayload, DamageReportPayload.class);
						payload.parse();
						
						openDamageReport(payload, mEditDamageReport);
						if(!mMapMarkupOpenTablet && mDataManager.getTabletLayoutOn()){
							fragment = mDamageReportListFragment;
							mBackStack.clear();
						}else{
							fragment2 = mDamageReportFragment;
						}
					}
					mDataManager.requestDamageReportRepeating(mDataManager.getIncidentDataRate(), false);
					showIncidentName = true;
					
					break;
					
				case WEATHERREPORT:
					
					if(currentFragment != null && currentFragment2 != null ){
						if(currentFragment == mWeatherReportFragment && currentFragment2 ==  mWeatherReportListFragment){
							break;
						}
					}
					if(mWeatherReportListFragment == null) {
						mWeatherReportListFragment = new WeatherReportListFragment();
					}
					fragment2 = mWeatherReportListFragment;
					
					//if using tablet view and map is closed
					//set detail view on left side
					if(mDataManager.getTabletLayoutOn() && !mMapMarkupOpenTablet){
						
						WeatherReportPayload payload = new Gson().fromJson(mOpenedWeatherReportPayload, WeatherReportPayload.class);
						if(payload == null){
							payload = mDataManager.getLastWeatherReportPayload();
						}
						if(payload == null){
							addWeatherReportToDetailView(false);
						}else{
							payload.parse();
							openWeatherReport(payload, mEditWeatherReport);
						}
						
						mBackStack.clear();
						
					}else if((mViewWeatherReport || mEditWeatherReport) && mOpenedWeatherReportPayload != null) {
						WeatherReportPayload payload = new Gson().fromJson(mOpenedWeatherReportPayload, WeatherReportPayload.class);
						payload.parse();
						
						openWeatherReport(payload, mEditWeatherReport);
						if(!mMapMarkupOpenTablet && mDataManager.getTabletLayoutOn()){
							fragment = mWeatherReportListFragment;
							mBackStack.clear();
						}else{
							fragment2 = mWeatherReportFragment;
						}
					}
					mDataManager.requestWeatherReportRepeating(mDataManager.getIncidentDataRate(), false);
					showIncidentName = true;
					
					break;
				case MAPCOLLABORATION:
					if(mMapMarkupFragment == null) {
						mMapMarkupFragment = new MapMarkupFragment();
					}
					fragment = mMapMarkupFragment;
					
					if(mDataManager.getTabletLayoutOn()){
						mMapMarkupOpenTablet = !mMapMarkupOpenTablet;
					}
					
					break;
				case USERINFO:
					if(mUserInfoFragment == null) {
						Bundle args = new Bundle();
						args.putString(FormFragment.SCHEMA_FILE, "userinfo_schema.json");
						
						mUserInfoFragment = new FormFragment();
						mUserInfoFragment.setArguments(args);
					}
					fragment2 = mUserInfoFragment;
					break;
				case CHATLOG:
					if(currentFragment2 != null && mDataManager.getTabletLayoutOn()){
						if(currentFragment2 == mChatFragment){
							break;
						}
					}
					if(mChatFragment == null) {
//						mChatFragment = new ChatFragment();
						mChatFragment = new ChatListFragment();
					}
					mDataManager.requestChatMessagesRepeating(mDataManager.getCollabroomDataRate(), true);
					fragment2 = mChatFragment;
					
					if(mDataManager.getTabletLayoutOn()){
						if(currentFragment != null && currentFragment != mMapMarkupFragment){
							animateFragmentRemove(currentFragment,false);
							mFragmentManager.executePendingTransactions();
						}
					}
					
					break;
				case RESOURCEREQUEST:
					
					if(currentFragment != null && currentFragment2 != null ){
						if(currentFragment == mResourceRequestFragment && currentFragment2 ==  mResourceRequestListFragment){
							break;
						}
					}
					if(mResourceRequestListFragment == null) {
						mResourceRequestListFragment = new ResourceRequestListFragment();
					}
					fragment2 = mResourceRequestListFragment;
					
					//if using tablet view and map is closed
					//set detail view on left side
					if(mDataManager.getTabletLayoutOn() && !mMapMarkupOpenTablet){
						
						ResourceRequestPayload payload = new Gson().fromJson(mOpenedResourceRequestPayload, ResourceRequestPayload.class);
						if(payload == null){
							payload = mDataManager.getLastResourceRequestPayload();
						}
						if(payload == null){
							addResourceRequestToDetailView(false);
						}else{
							payload.parse();
							openResourceRequest(payload, mEditResourceRequest);
						}
						
						mBackStack.clear();
						
					}else if((mViewResourceRequest || mEditResourceRequest) && mOpenedResourceRequestPayload != null) {
						ResourceRequestPayload payload = new Gson().fromJson(mOpenedResourceRequestPayload, ResourceRequestPayload.class);
						payload.parse();
						
						openResourceRequest(payload, mEditResourceRequest);
						if(!mMapMarkupOpenTablet && mDataManager.getTabletLayoutOn()){
							fragment = mResourceRequestListFragment;
							mBackStack.clear();
						}else{
							fragment2 = mResourceRequestFragment;
						}
					}
					mDataManager.requestResourceRequestRepeating(mDataManager.getIncidentDataRate(), false);
					showIncidentName = true;
					
					break;
				case GAR:
					if(mGarFragment == null) {
						mGarFragment = new GarFragment();
					}
					fragment2 = mGarFragment;
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
					
					EncryptedPreferences userPreferences = new EncryptedPreferences(this.getSharedPreferences(Constants.nics_USER_PREFERENCES, 0));
					userPreferences.savePreferenceBoolean(Constants.nics_AUTO_LOGIN, false);
					
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
					if( mDataManager.getTabletLayoutOn()){
						if(mMapMarkupOpenTablet){
							animateFragmentReplace(R.id.container, fragment,true);
						}else{
							if(fragment == mMapMarkupFragment){
								checkForOpenReportsAndSwapContainer();
							}
						}
					}else{
						animateFragmentReplace(R.id.container, fragment,true);
					}
				}
				if(fragment2 != null) {
					if(mDataManager.getTabletLayoutOn()){
						animateFragmentReplace(R.id.container2, fragment2,true);
					}else{
						animateFragmentReplace(R.id.container, fragment2,true);
					}
				}
				if(fragmentOverview != null){
					if(mDataManager.getTabletLayoutOn()){
						animateFragmentReplace(R.id.containerOverview, fragmentOverview,true);
					}else{
						animateFragmentReplace(R.id.container, fragmentOverview,true);
					}					
				}
			} catch (Exception ex) {
				Log.e("nics", ex.toString());
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
						CollabroomPayload collabRoom = mDataManager.getSelectedCollabRoom();
						if(collabRoom.getName().equals(getString(R.string.no_selection))){
							setBreadcrumbText("No Room Selected");
						}else{
							setBreadcrumbText(collabRoom.getName());
						}
					}
					mBreadcrumbTextView.setVisibility(View.VISIBLE);
				}
			}
		}
		return true;
	}
	
	void animateFragmentReplace(int container, Fragment frag, boolean stateLose){
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
		transaction.replace(container, frag);
		if(stateLose){
			transaction.commitAllowingStateLoss();
		}else{
			transaction.commit();
		}
	}
	
	void animateFragmentRemove(Fragment frag, boolean stateLose){
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
		transaction.remove(frag);
		if(stateLose){
			transaction.commitAllowingStateLoss();
		}else{
			transaction.commit();
		}
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
		
		if(mDataManager.getTabletLayoutOn()){
			tabletBackButtonPressed();
			return;
		}
		
		mIsBackKey = true;
		if(mViewMapLocationPicker){
			mMapMarkupLocationPickerFragment.BackButtonPressed();
		}else if(mEditSimpleReport || mViewSimpleReport) {
			mViewSimpleReport = false;
			mOpenedSimpleReportPayload = null;
			mOpenedSimpleReportId = -1;
			if( mReportOpenedFromMap) {
				mReportOpenedFromMap = false;
				mBackStack.pop();
				onNavigationItemSelected(NavigationOptions.MAPCOLLABORATION.getValue(), -1);
			} else {
				onNavigationItemSelected(NavigationOptions.GENERALMESSAGE.getValue(), NavigationOptions.GENERALMESSAGE.getValue());
			}
		} else if(mEditFieldReport || mViewFieldReport) {
			mViewFieldReport = false;
			mOpenedFieldReportPayload = null;
			mOpenedFieldReportId = -1;
			if( mReportOpenedFromMap) {
				mReportOpenedFromMap = false;
				mBackStack.pop();
				onNavigationItemSelected(NavigationOptions.MAPCOLLABORATION.getValue(), -1);
			} else {
				onNavigationItemSelected(NavigationOptions.FIELDREPORT.getValue(), NavigationOptions.FIELDREPORT.getValue());
			}
		} else if(mEditWeatherReport || mViewWeatherReport) {
			mViewWeatherReport = false;
			mOpenedWeatherReportPayload = null;
			mOpenedWeatherReportId = -1;
			if( mReportOpenedFromMap) {
				mReportOpenedFromMap = false;
				mBackStack.pop();
				onNavigationItemSelected(NavigationOptions.MAPCOLLABORATION.getValue(), -1);
			} else {
				onNavigationItemSelected(NavigationOptions.WEATHERREPORT.getValue(), NavigationOptions.WEATHERREPORT.getValue());
			}
		}  else if(mEditResourceRequest || mViewResourceRequest) {
			mViewResourceRequest = false;
			mOpenedResourceRequestPayload = null;
			mOpenedResourceRequestId = -1;
			if( mReportOpenedFromMap) {
				mReportOpenedFromMap = false;
				mBackStack.pop();
				onNavigationItemSelected(NavigationOptions.MAPCOLLABORATION.getValue(), -1);
			} else {
				onNavigationItemSelected(NavigationOptions.RESOURCEREQUEST.getValue(), NavigationOptions.RESOURCEREQUEST.getValue());
			}
		} else if(mEditDamageReport || mViewDamageReport) {
			mViewDamageReport = false;
			mOpenedDamageReportPayload = null;
			mOpenedDamageReportId = -1;
			if( mReportOpenedFromMap) {
				mReportOpenedFromMap = false;
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
	
	void tabletBackButtonPressed(){
		Fragment currentFragment2 = (Fragment) mFragmentManager.findFragmentById(R.id.container2);
	
		if(currentFragment2 == mSimpleReportFragment){
			mViewSimpleReport = false;
			mOpenedSimpleReportPayload = null;
			onNavigationItemSelected(NavigationOptions.GENERALMESSAGE.getValue(), -1);
		}else if(currentFragment2 == mDamageReportFragment){
			mViewDamageReport = false;
			mOpenedDamageReportPayload = null;
			onNavigationItemSelected(NavigationOptions.DAMAGESURVEY.getValue(), -1);
		}else if(currentFragment2 == mWeatherReportFragment){
			mViewWeatherReport = false;
			mOpenedWeatherReportPayload = null;
			onNavigationItemSelected(NavigationOptions.WEATHERREPORT.getValue(), -1);
		}else if(currentFragment2 == mResourceRequestFragment){
			mViewResourceRequest = false;
			mOpenedResourceRequestPayload = null;
			onNavigationItemSelected(NavigationOptions.RESOURCEREQUEST.getValue(), -1);
		}else if(currentFragment2 == mFieldReportFragment){
			mViewFieldReport = false;
			mOpenedFieldReportPayload = null;
			onNavigationItemSelected(NavigationOptions.FIELDREPORT.getValue(), -1);
		}
	}
	
	void checkForOpenReportsAndSwapContainer(){
		Fragment currentFragment = (Fragment) mFragmentManager.findFragmentById(R.id.container);
		Fragment currentFragment2 = (Fragment) mFragmentManager.findFragmentById(R.id.container2);
		
		if(mSimpleReportFragment == currentFragment2 && currentFragment2 != null){
			
			animateFragmentRemove(currentFragment2,false);
			mFragmentManager.executePendingTransactions();
			
			animateFragmentReplace(R.id.container2, mSimpleReportListFragment,false);
			SimpleReportPayload payload = mSimpleReportFragment.getPayload();
			if(payload == null){
				payload = mDataManager.getLastSimpleReportPayload();
			}
			openSimpleReport(payload,mEditSimpleReport);	
		}else if(mSimpleReportListFragment == currentFragment2 && currentFragment2 != null){
			
			if(mSimpleReportFragment == null){
				mSimpleReportFragment = new GeneralMessageFragment();
			}
			
			SimpleReportPayload payload = mSimpleReportFragment.getPayload();
			if(payload == null){
				payload = mDataManager.getLastSimpleReportPayload();
			}
			openSimpleReport(payload,mEditSimpleReport);
		}else if(mDamageReportFragment == currentFragment2 && currentFragment2 != null){
			
			animateFragmentRemove(currentFragment2,false);
			mFragmentManager.executePendingTransactions();
			
			animateFragmentReplace(R.id.container2, mDamageReportListFragment,false);
			DamageReportPayload payload = mDamageReportFragment.getPayload();
			if(payload == null){
				payload = mDataManager.getLastDamageReportPayload();
			}
			openDamageReport(payload,mEditDamageReport);	
		}else if(mDamageReportListFragment == currentFragment2 && currentFragment2 != null){
			
			if(mDamageReportFragment == null){
				mDamageReportFragment = new DamageReportFragment();
			}
			
			DamageReportPayload payload = mDamageReportFragment.getPayload();
			if(payload == null){
				payload = mDataManager.getLastDamageReportPayload();
			}
			openDamageReport(payload,mEditDamageReport);
		}else if(mWeatherReportFragment == currentFragment2 && currentFragment2 != null){
			
			animateFragmentRemove(currentFragment2,false);
			mFragmentManager.executePendingTransactions();
			
			animateFragmentReplace(R.id.container2, mWeatherReportListFragment,false);
			
			WeatherReportPayload payload = mWeatherReportFragment.getPayload();
			if(payload == null){
				payload = mDataManager.getLastWeatherReportPayload();
			}
			openWeatherReport(payload,mEditWeatherReport);	
		}else if(mWeatherReportListFragment == currentFragment2 && currentFragment2 != null){
			
			if(mWeatherReportFragment == null){
				mWeatherReportFragment = new WeatherReportFragment();
			}
			
			WeatherReportPayload payload = mWeatherReportFragment.getPayload();
			if(payload == null){
				payload = mDataManager.getLastWeatherReportPayload();
			}
			openWeatherReport(payload,mEditWeatherReport);
		}else if(mFieldReportFragment == currentFragment2 && currentFragment2 != null){
			
			animateFragmentRemove(currentFragment2,false);
			mFragmentManager.executePendingTransactions();
			
			animateFragmentReplace(R.id.container2, mFieldReportListFragment,false);
			
			FieldReportPayload payload = mFieldReportFragment.getPayload();
			if(payload == null){
				payload = mDataManager.getLastFieldReportPayload();
			}
			openFieldReport(payload,mEditFieldReport);	
		}else if(mFieldReportListFragment == currentFragment2 && currentFragment2 != null){
			
			if(mFieldReportFragment == null){
				mFieldReportFragment = new FieldReportFragment();
			}
			
			FieldReportPayload payload = mFieldReportFragment.getPayload();
			if(payload == null){
				payload = mDataManager.getLastFieldReportPayload();
			}
			openFieldReport(payload,mEditFieldReport);
		}else if(mResourceRequestFragment == currentFragment2 && currentFragment2 != null){
			
			animateFragmentRemove(currentFragment2,false);
			mFragmentManager.executePendingTransactions();
			
			animateFragmentReplace(R.id.container2, mResourceRequestListFragment,false);
			
			ResourceRequestPayload payload = mResourceRequestFragment.getPayload();
			if(payload == null){
				payload = mDataManager.getLastResourceRequestPayload();
			}
			openResourceRequest(payload,mEditResourceRequest);	
		}else if(mResourceRequestListFragment == currentFragment2 && currentFragment2 != null){
			
			if(mResourceRequestFragment == null){
				mResourceRequestFragment = new ResourceRequestFragment();
			}
			
			ResourceRequestPayload payload = mResourceRequestFragment.getPayload();
			if(payload == null){
				payload = mDataManager.getLastResourceRequestPayload();
			}
			openResourceRequest(payload,mEditResourceRequest);
		}else{
			if(currentFragment == mMapMarkupFragment){
				animateFragmentRemove(currentFragment,false);
				mFragmentManager.executePendingTransactions();
			}
		}
	}
	
	public void openDamageReport(DamageReportPayload damageReportPayload, boolean editable) {
		if(mDamageReportFragment == null) {
			mDamageReportFragment = new DamageReportFragment();
		}
		
		if(!mDataManager.getTabletLayoutOn() || !mMapMarkupOpenTablet){
			if(mDamageReportFragment != (Fragment) mFragmentManager.findFragmentById(R.id.container)){
				animateFragmentReplace(R.id.container, mDamageReportFragment,false);
				mFragmentManager.executePendingTransactions();
			}
		}else{
			animateFragmentReplace(R.id.container2, mDamageReportFragment,false);
			mFragmentManager.executePendingTransactions();
		}
		
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
	}

	public void openFieldReport(FieldReportPayload fieldReportPayload, boolean editable) {
		if(mFieldReportFragment == null) {
			mFieldReportFragment = new FieldReportFragment();
		}
		if(!mDataManager.getTabletLayoutOn() || !mMapMarkupOpenTablet){
			if(mFieldReportFragment != (Fragment) mFragmentManager.findFragmentById(R.id.container)){
				animateFragmentReplace(R.id.container, mFieldReportFragment,false);
				mFragmentManager.executePendingTransactions();
			}
		}else{
			animateFragmentReplace(R.id.container2, mFieldReportFragment,false);
			mFragmentManager.executePendingTransactions();
		}
		
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
		if(!mDataManager.getTabletLayoutOn() || !mMapMarkupOpenTablet){
			if(mResourceRequestFragment != (Fragment) mFragmentManager.findFragmentById(R.id.container)){
				animateFragmentReplace(R.id.container, mResourceRequestFragment,false);
				mFragmentManager.executePendingTransactions();
			}
		}else{
			animateFragmentReplace(R.id.container2, mResourceRequestFragment,false);
			mFragmentManager.executePendingTransactions();
		}

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
		
		if(!mDataManager.getTabletLayoutOn() || !mMapMarkupOpenTablet){
			if(mSimpleReportFragment != (Fragment) mFragmentManager.findFragmentById(R.id.container)){
				animateFragmentReplace(R.id.container, mSimpleReportFragment,false);
				mFragmentManager.executePendingTransactions();
			}
		}else{
			animateFragmentReplace(R.id.container2, mSimpleReportFragment,false);
			mFragmentManager.executePendingTransactions();
		}

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
	}
	
	public void openWeatherReport(WeatherReportPayload weatherReportPayload, boolean editable) {
		if(mWeatherReportFragment == null) {
			mWeatherReportFragment = new WeatherReportFragment();
		}
		
		if(!mDataManager.getTabletLayoutOn() || !mMapMarkupOpenTablet){
			if(mWeatherReportFragment != (Fragment) mFragmentManager.findFragmentById(R.id.container)){
				
				animateFragmentReplace(R.id.container, mWeatherReportFragment,false);
				mFragmentManager.executePendingTransactions();
			}
		}else{
			animateFragmentReplace(R.id.container2, mWeatherReportFragment,false);
			mFragmentManager.executePendingTransactions();
		}

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
		}
		
		if(mMapMarkupLocationPickerFragment == null) {
			mMapMarkupLocationPickerFragment = new MapMarkupLocationPickerFragment(previousReport);
		}else{
			mMapMarkupLocationPickerFragment.setPreviousReport(previousReport);
		}
		animateFragmentReplace(R.id.container, mMapMarkupLocationPickerFragment,false);
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
		return mEditFieldReport || mEditSimpleReport || mEditResourceRequest || mEditDamageReport || mEditWeatherReport;
	}

	public boolean isViewReport() {
		return mViewFieldReport || mViewSimpleReport || mViewResourceRequest || mViewDamageReport || mViewWeatherReport;
	}

//	@Override
//	public void uncaughtException(Thread thread, Throwable ex) {
//		String error = ex.getClass() + " - " + ex.getLocalizedMessage() + "\n";
//		
//		StackTraceElement[] traceArray = ex.getStackTrace();
//		for(StackTraceElement element : traceArray) {
//			if(element.getClassName().contains("nics")) {
//				error += element.getClassName() + " - Line: " + element.getLineNumber() + "\n";
//			}
//		}
//
//		mDataManager.addPersonalHistory(error);
//		Log.e("nicsError", error);
//		ex.printStackTrace();
//		System.exit(1);
//
//	}
	
	public void setBreadcrumbText(CharSequence charSequence) {
		mBreadcrumbTextView.setText(charSequence);
	}
	
}
