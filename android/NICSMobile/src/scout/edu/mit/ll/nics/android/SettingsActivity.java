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

import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.utils.Intents;
import scout.edu.mit.ll.nics.android.utils.LocationHandler;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean ALWAYS_SIMPLE_PREFS = false;	
	private static SettingsActivity mActivity;
	private static Intent mIntent;
	
	private static String mCurrentServer;
	private static CheckBoxPreference mUseCustomServer;
	private static Preference mCustomServerPref;
	private static Preference mServerListPref;
	
	private static String mCurrentGeoServer;
	private static CheckBoxPreference mUseCustomGeoServer;
	private static Preference mCustomGeoServerPref;
	private static Preference mGeoServerListPref;
	
	private static String mCurrentAuthServer;
	private static CheckBoxPreference mUseCustomAuthServer;
	private static Preference mCustomAuthServerPref;
	private static Preference mAuthServerListPref;
	
	private static CheckBoxPreference mUseCustomDomain;
	private static Preference mCustomDomainPref;
	
	private static boolean mServerChanged = false;
	private static DataManager mDataManager;
	
	private static Preference mLanguageListPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDataManager = DataManager.getInstance(this);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mActivity = this;
		
		setupSimplePreferencesScreen();
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}
		
		mServerChanged = false;
		mCurrentServer = mDataManager.getServer();
		mCurrentGeoServer = mDataManager.getGeoServerURL();
		mCurrentAuthServer = mDataManager.getAuthServerURL();
		
		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		// Add 'general' preferences.

		addPreferencesFromResource(R.xml.pref_general);
		addPreferencesFromResource(R.xml.pref_data_sync);
		//addPreferencesFromResource(R.xml.pref_other);

		mUseCustomServer = (CheckBoxPreference) findPreference("custom_server_enabled");
		mUseCustomServer.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		
		mCustomServerPref = findPreference("custom_server_url");
		mServerListPref = findPreference("server_list");
		
		
		mUseCustomGeoServer = (CheckBoxPreference) findPreference("custom_geo_server_enabled");
		mUseCustomGeoServer.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		
		mCustomGeoServerPref = findPreference("custom_geo_server_url");
		mGeoServerListPref = findPreference("geo_server_list");
		
		
		mUseCustomAuthServer = (CheckBoxPreference) findPreference("custom_auth_server_enabled");
		mUseCustomAuthServer.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		
		mCustomAuthServerPref = findPreference("custom_auth_server_url");
		mAuthServerListPref = findPreference("auth_server_list");
		
		mUseCustomDomain = (CheckBoxPreference) findPreference("custom_domain_name_enabled");
		mUseCustomDomain.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		mCustomDomainPref = findPreference("custom_cookie_domain");
		
		mLanguageListPref = findPreference("language_select_list");
		
		bindPreferenceSummaryToValue(findPreference("incident_sync_frequency"));
		bindPreferenceSummaryToValue(findPreference("collabroom_sync_frequency"));
		bindPreferenceSummaryToValue(findPreference("mdt_sync_frequency"));
		bindPreferenceSummaryToValue(findPreference("wfs_sync_frequency"));
		bindPreferenceSummaryToValue(findPreference("server_list"));
		bindPreferenceSummaryToValue(findPreference("geo_server_list"));
		bindPreferenceSummaryToValue(findPreference("auth_server_list"));
		bindPreferenceSummaryToValue(findPreference("language_select_list"));
		
		if(mUseCustomServer.isChecked()) {
			mCustomServerPref.setEnabled(true);
			mServerListPref.setEnabled(false);
		} else {
			mCustomServerPref.setEnabled(false);
			mServerListPref.setEnabled(true);
		}
		
		if(mUseCustomGeoServer.isChecked()) {
			mCustomGeoServerPref.setEnabled(true);
			mGeoServerListPref.setEnabled(false);
		} else {
			mCustomGeoServerPref.setEnabled(false);
			mGeoServerListPref.setEnabled(true);
		}
		
		if(mUseCustomAuthServer.isChecked()) {
			mCustomAuthServerPref.setEnabled(true);
			mAuthServerListPref.setEnabled(false);
		} else {
			mCustomAuthServerPref.setEnabled(false);
			mAuthServerListPref.setEnabled(true);
		}
		
		if(mUseCustomDomain.isChecked()) {
			mCustomDomainPref.setEnabled(true);
		} else {
			mCustomDomainPref.setEnabled(false);
		}
		

		Preference garButton = findPreference("gar_button");

		if(garButton != null) {
			garButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
	
				@Override
				public boolean onPreferenceClick(Preference preference) {
	
					Intent intent = new Intent();
					intent.putExtra("loadGAR", true);
					mActivity.setResult(RESULT_OK, intent);
					mActivity.finish();
					return true;
				}
			});
				
			if (mIntent.getExtras() != null && mIntent.getBooleanExtra("hideGar", false)) {
				garButton.setEnabled(false);
				garButton.setSummary(R.string.log_in_and_join_collabroom_to_enable_risk_assessment);
			}
	
			if (mIntent.getExtras() != null && mIntent.getBooleanExtra("hideGarCollab", false)) {
				garButton.setEnabled(false);
				garButton.setSummary(R.string.join_collabroom_to_enable_risk_assessment);
			}
		}
		
		Preference clearLocalMapFeaturesButton = (Preference)findPreference("clear_local_map_data");
		if(clearLocalMapFeaturesButton != null){
			
		clearLocalMapFeaturesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {   
                	mDataManager.deleteAllMarkupFeatureHistory();
                	mDataManager.deleteAllMarkupFeatureStoreAndForward();
                	
        	        Intent intent = new Intent();
        	        intent.setAction(Intents.nics_LOCAL_MAP_FEATURES_CLEARED);
        	        mDataManager.getContext().sendBroadcast(intent);
        	        
                    return true;
                }
            });
		}
		
		Preference clearLocalChatFeaturesButton = (Preference)findPreference("clear_local_chat_data");
		if(clearLocalChatFeaturesButton != null){
			
			clearLocalChatFeaturesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {   
                	mDataManager.deleteAllChatHistory();
                	mDataManager.deleteAllChatStoreAndForward();
                	
        	        Intent intent = new Intent();
        	        intent.setAction(Intents.nics_LOCAL_CHAT_CLEARED);
        	        mDataManager.getContext().sendBroadcast(intent);
                	
                    return true;
                }
            });
		}
		
		Preference clearLocalReportsFeaturesButton = (Preference)findPreference("clear_local_reports_data");
		if(clearLocalReportsFeaturesButton != null){
			
			clearLocalReportsFeaturesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {   
                	mDataManager.deleteAllReportsFromLocalStorage();
                	
                    return true;
                }
            });
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				if (preference.getKey().equals("server_list")) {
					preference.setEnabled(true);
					String selectServerNote = preference.getContext().getString(R.string.pref_description_select_server);
					preference.setSummary(index >= 0 ? listPreference.getEntries()[index] + "\n\n" + selectServerNote : selectServerNote);

					if (mCurrentServer != null && !mCurrentServer.equals(listPreference.getEntryValues()[index]) && !mUseCustomServer.isChecked()) {
						mDataManager.stopPollingAlarms();
						mDataManager.requestLogout();
						mServerChanged = true;
						
						String domain = mDataManager.getContext().getResources().getStringArray(R.array.config_iplanet_cookie_domains)[index];
						mDataManager.setIplanetCookieDomain(domain);
						
						domain = mDataManager.getContext().getResources().getStringArray(R.array.config_amauth_cookie_domains)[index];
						mDataManager.setAmAuthCookieDomain(domain);
					}
					
					
				}else if (preference.getKey().equals("geo_server_list")) {
					preference.setEnabled(true);
					String selectGeoServerNote = preference.getContext().getString(R.string.pref_description_select_geo_server);
					preference.setSummary(index >= 0 ? listPreference.getEntries()[index] + "\n\n" + selectGeoServerNote : selectGeoServerNote);

					if (mCurrentGeoServer != null && !mCurrentGeoServer.equals(listPreference.getEntryValues()[index]) && !mUseCustomGeoServer.isChecked()) {
						mDataManager.stopPollingAlarms();
						mDataManager.requestLogout();
						mServerChanged = true;
					}
					
				}else if (preference.getKey().equals("auth_server_list")) {
					preference.setEnabled(true);
					String selectAuthServerNote = preference.getContext().getString(R.string.pref_description_select_auth_server);
					preference.setSummary(index >= 0 ? listPreference.getEntries()[index] + "\n\n" + selectAuthServerNote : selectAuthServerNote);

					if (mCurrentAuthServer != null && !mCurrentAuthServer.equals(listPreference.getEntryValues()[index]) && !mUseCustomAuthServer.isChecked()) {
						mDataManager.stopPollingAlarms();
						mDataManager.requestLogout();
						mServerChanged = true;
					}
				
				}else if (preference.getKey().equals("language_select_list")) {
		
					String newLanguage = listPreference.getEntryValues()[index].toString();
					if(newLanguage.equals("Device Default")){
						newLanguage = Locale.getDefault().getISO3Language().substring(0,2);
					}
					String storedLanguage = mDataManager.getSelectedLanguage();
					if(!newLanguage.equals(storedLanguage)){

						mDataManager.setCurrentLocale(newLanguage);
					    Intent refresh = new Intent(mActivity ,LoginActivity.class);
					    mActivity.startActivity(refresh); 
					    mActivity.finish();
					}
					
					
			}else if(preference.getKey().equals("mdt_sync_frequency")) {
					String selectMDTNote = preference.getContext().getString(R.string.pref_description_mdt_sync_rate);
					preference.setSummary(index >= 0 ? listPreference.getEntries()[index] + "\n\n" + selectMDTNote : selectMDTNote);
				} else {
					preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
				}
			} else if (preference instanceof RingtonePreference) {
				// For ringtone preferences, look up the correct display value
				// using RingtoneManager.
				if (TextUtils.isEmpty(stringValue)) {
					// Empty values correspond to 'silent' (no ringtone).
					preference.setSummary(R.string.pref_ringtone_silent);

				} else {
					Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));

					if (ringtone == null) {
						// Clear the summary if there was a lookup error.
						preference.setSummary(null);
					} else {
						// Set the summary to reflect the new ringtone display
						// name.
						String name = ringtone.getTitle(preference.getContext());
						preference.setSummary(name);
					}
				}

			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				if(preference.getKey().equals("custom_server_enabled")) {
					mDataManager.stopPollingAlarms();
					mDataManager.requestLogout();
					mServerChanged = true;
					
					if(!mUseCustomServer.isChecked()) {
						mCustomServerPref.setEnabled(true);
						mServerListPref.setEnabled(false);
					} else {
						mCustomServerPref.setEnabled(false);
						mServerListPref.setEnabled(true);
					}
					
				}else if(preference.getKey().equals("custom_geo_server_enabled")) {
						mDataManager.stopPollingAlarms();
						mDataManager.requestLogout();
						mServerChanged = true;
						
						if(!mUseCustomGeoServer.isChecked()) {
							mCustomGeoServerPref.setEnabled(true);
							mGeoServerListPref.setEnabled(false);
						} else {
							mCustomGeoServerPref.setEnabled(false);
							mGeoServerListPref.setEnabled(true);
						}
					
				}else if(preference.getKey().equals("custom_auth_server_enabled")) {
					mDataManager.stopPollingAlarms();
					mDataManager.requestLogout();
					mServerChanged = true;
					
					if(!mUseCustomAuthServer.isChecked()) {
						mCustomAuthServerPref.setEnabled(true);
						mAuthServerListPref.setEnabled(false);
					} else {
						mCustomAuthServerPref.setEnabled(false);
						mAuthServerListPref.setEnabled(true);
					}
				}else if(preference.getKey().equals("custom_domain_name_enabled")) {
					
					mDataManager.stopPollingAlarms();
					mDataManager.requestLogout();
					mServerChanged = true;
					
					if(!mUseCustomDomain.isChecked()) {
						mCustomDomainPref.setEnabled(true);
					} else {
						mCustomDomainPref.setEnabled(false);
					}
						
				} else {
					preference.setSummary(stringValue);
				}
			}

			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 * 
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		if(preference != null){
			// Set the listener to watch for value changes.
			preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
	
			// Trigger the listener immediately with the preference's
			// current value.
			sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
		}
	}

	/**
	 * This fragment shows general preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);
			
			mServerChanged = false;
			mCurrentServer = mDataManager.getServer();
			mCurrentGeoServer = mDataManager.getGeoServerURL();
			mCurrentAuthServer = mDataManager.getAuthServerURL();
			
			Preference mCategory = findPreference("header");
			getPreferenceScreen().removePreference(mCategory);

			mUseCustomServer = (CheckBoxPreference) findPreference("custom_server_enabled");
			mUseCustomServer.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

			mCustomServerPref = findPreference("custom_server_url");
			mServerListPref = findPreference("server_list");
			
			bindPreferenceSummaryToValue(findPreference("server_list"));
			
			if(mUseCustomServer.isChecked()) {
				mCustomServerPref.setEnabled(true);
				mServerListPref.setEnabled(false);
			} else {
				mCustomServerPref.setEnabled(false);
				mServerListPref.setEnabled(true);
			}
			
			mLanguageListPref = findPreference("language_select_list");
			bindPreferenceSummaryToValue(findPreference("language_select_list"));
			
			mUseCustomGeoServer = (CheckBoxPreference) findPreference("custom_geo_server_enabled");
			mUseCustomGeoServer.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

			mCustomGeoServerPref = findPreference("custom_geo_server_url");
			mGeoServerListPref = findPreference("geo_server_list");
			
			bindPreferenceSummaryToValue(findPreference("geo_server_list"));
			
			if(mUseCustomGeoServer.isChecked()) {
				mCustomGeoServerPref.setEnabled(true);
				mGeoServerListPref.setEnabled(false);
			} else {
				mCustomGeoServerPref.setEnabled(false);
				mGeoServerListPref.setEnabled(true);
			}
			
			mUseCustomAuthServer = (CheckBoxPreference) findPreference("custom_auth_server_enabled");
			mUseCustomAuthServer.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

			mCustomAuthServerPref = findPreference("custom_auth_server_url");
			mAuthServerListPref = findPreference("auth_server_list");
			
			bindPreferenceSummaryToValue(findPreference("auth_server_list"));
			
			if(mUseCustomAuthServer.isChecked()) {
				mCustomAuthServerPref.setEnabled(true);
				mAuthServerListPref.setEnabled(false);
			} else {
				mCustomAuthServerPref.setEnabled(false);
				mAuthServerListPref.setEnabled(true);
			}
			
			mUseCustomDomain = (CheckBoxPreference) findPreference("custom_domain_name_enabled");
			mUseCustomDomain.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

			mCustomDomainPref = findPreference("custom_cookie_domain");
			bindPreferenceSummaryToValue(findPreference("custom_cookie_domain"));
			if(mUseCustomDomain.isChecked()) {
				mCustomDomainPref.setEnabled(true);
			} else {
				mCustomDomainPref.setEnabled(false);
			}
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class SyncPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_data_sync);
			
			Preference mCategory = findPreference("header");
			getPreferenceScreen().removePreference(mCategory);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("incident_sync_frequency"));
			bindPreferenceSummaryToValue(findPreference("collabroom_sync_frequency"));
			bindPreferenceSummaryToValue(findPreference("mdt_sync_frequency"));
			bindPreferenceSummaryToValue(findPreference("wfs_sync_frequency"));
			bindPreferenceSummaryToValue(findPreference("language_select_list"));
			
			Preference clearLocalMapFeaturesButton = (Preference)findPreference("clear_local_map_data");
			if(clearLocalMapFeaturesButton != null){
				
			clearLocalMapFeaturesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
	                @Override
	                public boolean onPreferenceClick(Preference preference) {   
	                	mDataManager.deleteAllMarkupFeatureHistory();
	                	mDataManager.deleteAllMarkupFeatureStoreAndForward();
	                	
	        	        Intent intent = new Intent();
	        	        intent.setAction(Intents.nics_LOCAL_MAP_FEATURES_CLEARED);
	        	        mDataManager.getContext().sendBroadcast(intent);
	                	
	                    return true;
	                }
	            });
			}
			
			Preference clearLocalChatFeaturesButton = (Preference)findPreference("clear_local_chat_data");
			if(clearLocalChatFeaturesButton != null){
				
				clearLocalChatFeaturesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
	                @Override
	                public boolean onPreferenceClick(Preference preference) {   
	                	mDataManager.deleteAllChatHistory();
	                	mDataManager.deleteAllChatStoreAndForward();
	                	
	        	        Intent intent = new Intent();
	        	        intent.setAction(Intents.nics_LOCAL_CHAT_CLEARED);
	        	        mDataManager.getContext().sendBroadcast(intent);
	                	
	                    return true;
	                }
	            });
			}
			
			Preference clearLocalReportsFeaturesButton = (Preference)findPreference("clear_local_reports_data");
			if(clearLocalReportsFeaturesButton != null){
				
				clearLocalReportsFeaturesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
	                @Override
	                public boolean onPreferenceClick(Preference preference) {   
	                	mDataManager.deleteAllReportsFromLocalStorage();
	                	
	                    return true;
	                }
	            });
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class OtherPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_other);
			
			Preference mCategory = findPreference("header");
			getPreferenceScreen().removePreference(mCategory);

			Preference garButton = findPreference("gar_button");

			if(garButton != null) {
				garButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
	
					@Override
					public boolean onPreferenceClick(Preference preference) {
	
						Intent intent = new Intent();
						intent.putExtra("loadGAR", true);
						mActivity.setResult(RESULT_OK, intent);
						mActivity.finish();
						return true;
					}
				});
	
				if (mIntent.getExtras() != null && mIntent.getBooleanExtra("hideGar", false)) {
					garButton.setEnabled(false);
					garButton.setSummary(R.string.log_in_and_join_collabroom_to_enable_risk_assessment);
				}
	
				if (mIntent.getExtras() != null && mIntent.getBooleanExtra("hideGarCollab", false)) {
					garButton.setEnabled(false);
					garButton.setSummary(R.string.join_collabroom_to_enable_risk_assessment);
				}
			}
		}
	}
	
	@Override
	protected boolean isValidFragment(String fragmentName) {
		if(GeneralPreferenceFragment.class.getName().equals(fragmentName) || SyncPreferenceFragment.class.getName().equals(fragmentName) /*|| OtherPreferenceFragment.class.getName().equals(fragmentName)*/) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public void onBackPressed() {
		if(mServerChanged) {
			Intent intent = new Intent();
			intent.putExtra("logoutAndClear", true);
			mActivity.setResult(RESULT_OK, intent);
			mActivity.finish();
		}
		
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		LocationHandler locationHandler = mDataManager.getLocationSource();
		
		if(locationHandler != null) {
			locationHandler.setUpdateRate(mDataManager.getMDTDataRate());
		}
		
		mDataManager.requestMarkupRepeating(mDataManager.getCollabroomDataRate(), true);
		mDataManager.requestChatMessagesRepeating(mDataManager.getCollabroomDataRate(), true);
		mDataManager.requestDamageReportRepeating(mDataManager.getIncidentDataRate(), true);
		mDataManager.requestFieldReportRepeating(mDataManager.getIncidentDataRate(), true);
		mDataManager.requestSimpleReportRepeating(mDataManager.getIncidentDataRate(), true);
		mDataManager.requestResourceRequestRepeating(mDataManager.getIncidentDataRate(), true);
		super.onDestroy();
	}

}
