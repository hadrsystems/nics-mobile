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

import java.util.Arrays;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.dialogs.SplashScreenDialog;
import scout.edu.mit.ll.nics.android.utils.Constants;
import scout.edu.mit.ll.nics.android.utils.EncryptedPreferences;
import scout.edu.mit.ll.nics.android.utils.Intents;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends ActionBarActivity implements OnCheckedChangeListener, OnFocusChangeListener {

	private DataManager mDataManager;

	private String mEmail;
	private String mPassword;

	private EditText mEmailView;
	private EditText mPasswordView;
	
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private TextView mServerLabel;
	private TextView mVersionNumber;
	private RadioButton mIncidentRadio;
	private RadioButton mTrainingRadio;
	
	private CheckBox mRememberUserName;
	private CheckBox mAutoLogin;
	private CheckBox mTabletLayoutToggle;
	
	private boolean isAutoLogin;
	
	private EncryptedPreferences mUserPreferences;

	private SplashScreenDialog mSplashScreenDialog;

	private boolean mReceiversRegistered = false;
	private boolean mIncidentsReceived = false;
	private boolean mOrganizaitonsRecieved = false;
	private boolean mIsLoggingIn = false;
	private MenuItem settingsMenuItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        	
		mDataManager = DataManager.getInstance(getApplicationContext(), this);
		mDataManager.stopPollingAlarms();
		
		mDataManager.setSupportedLanguages(getResources().getStringArray(R.array.pref_language_list_values));
		
		mDataManager.setLoggedIn(false);
		mDataManager.loadLanguageConvertStorage();
		
		setContentView(R.layout.activity_login);
			
		mUserPreferences = new EncryptedPreferences( this.getSharedPreferences(Constants.nics_USER_PREFERENCES, Context.MODE_PRIVATE));
				
		// Set up the login form.
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					if(mIncidentRadio.isChecked()) {
						mDataManager.setWorkspaceId(1);
					} else if(mTrainingRadio.isChecked()) {
						mDataManager.setWorkspaceId(2);
					}
					attemptLogin(); 
					return true;
				}
				return false;
			}
		});
		
		int serverNameIdx = Arrays.asList(getResources().getStringArray(R.array.config_server_list_values)).indexOf(mDataManager.getServer());
		mServerLabel = (TextView) findViewById(R.id.serverLabel);
		
		if(serverNameIdx < 0) {
			mServerLabel.setText(getString(R.string.server_, mDataManager.getServer()));
		} else {
			mServerLabel.setText(getString(R.string.server_, getResources().getStringArray(R.array.config_server_list_titles)[serverNameIdx]));
		}
		
		mVersionNumber = (TextView) findViewById(R.id.VersionNumberLabel);
		try {
			mVersionNumber.setText(getString(R.string.version_login_activity) + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		mIncidentRadio = (RadioButton) findViewById(R.id.workspaceIncidentRadio);
		mTrainingRadio = (RadioButton) findViewById(R.id.workspaceTrainingRadio);
		
		if(mDataManager.getWorkspaceId() == 1) {
			mIncidentRadio.setChecked(true);
		} else if(mDataManager.getWorkspaceId() == 2) {
			mTrainingRadio.setChecked(true);
		}
		
		isAutoLogin = mUserPreferences.getPreferenceBoolean(Constants.nics_AUTO_LOGIN, "false");
	
		mRememberUserName = (CheckBox) findViewById(R.id.login_remember_user);
		mRememberUserName.setChecked(mUserPreferences.getPreferenceBoolean(Constants.nics_REMEMBER_USER, "false"));
		mRememberUserName.setOnCheckedChangeListener(this);
		
		mAutoLogin = (CheckBox) findViewById(R.id.login_auto);
		mAutoLogin.setChecked(isAutoLogin);
		mAutoLogin.setOnCheckedChangeListener(this);

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		mTabletLayoutToggle = (CheckBox)findViewById(R.id.tablet_layout_toggle);
		int screenSize = getResources().getConfiguration().screenLayout &
		        Configuration.SCREENLAYOUT_SIZE_MASK;

		if(screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE){
			mTabletLayoutToggle.setChecked(mDataManager.getTabletLayoutOn());
		}else{
			mTabletLayoutToggle.setChecked(false);
			mTabletLayoutToggle.setVisibility(View.GONE);
		}
		
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						view.setEnabled(false);
						if(mIncidentRadio.isChecked()) {
							mDataManager.setWorkspaceId(1);
						} else if(mTrainingRadio.isChecked()) {
							mDataManager.setWorkspaceId(2);
						}
						attemptLogin();
					}
				});
		
		boolean hideSplash = false;
		Bundle extras = getIntent().getExtras();
		
		if(extras != null) {
			hideSplash = extras.getBoolean("hideSplash");
		}
		
		if(mRememberUserName.isChecked()) {
			String username = mUserPreferences.getPreferenceString(Constants.nics_USER_NAME, "");
			if(username != null) {
				mEmailView.setText(username);
			}
		}
		
		if((isAutoLogin|| mDataManager.isLoggedIn())) {
			mEmailView.setText(mUserPreferences.getPreferenceString(Constants.nics_USER_NAME, ""));
			mPasswordView.setText(mUserPreferences.getPreferenceString(Constants.nics_USER_PASSWORD, ""));
			mPasswordView.requestFocus();
//			attemptLogin();
		} else {
			mDataManager.setAuthToken(null);
		}
		
		if(savedInstanceState == null && !hideSplash && !isAutoLogin) {
			mSplashScreenDialog = new SplashScreenDialog(this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
			mSplashScreenDialog.show();

			final Handler handler = new Handler();
            // Hide the splash screen after 5s (5000ms)
	        handler.postDelayed(new Runnable() {
	            @Override
	            public void run() {
	            	try {
		            	if(mSplashScreenDialog != null && mSplashScreenDialog.isShowing()) {
		            		mSplashScreenDialog.dismiss();
		            	}
	            	} catch (Exception e) {
	            		
	            	}
	            }
	        }, 5000);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		settingsMenuItem = menu.findItem(R.id.action_settings);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_settings:
				Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
		        intent.putExtra("currentServer", mDataManager.getServer());
		        intent.putExtra("hideGar", true);
				startActivityForResult(intent, 1002);
		        break;
		        
	        case R.id.action_about:
	        	intent = new Intent(LoginActivity.this, AboutActivity.class);
				startActivity(intent);
		        break;
	    }
	    
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 1002 && data != null) {
			if(data.getBooleanExtra("logoutAndClear", false)) {
				mDataManager.setCurrentIncidentData(null, -1, "");	
				mDataManager.setSelectedCollabRoom(null);
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		
		if(!mIsLoggingIn) {
			// Reset errors.
			mEmailView.setError(null);
			mPasswordView.setError(null);
	
			// Store values at the time of the login attempt.
			mEmail = mEmailView.getText().toString().toLowerCase(mDataManager.getLocale());
			mPassword = mPasswordView.getText().toString();
	
			boolean cancel = false;
			View focusView = null;
	
			// Check for a valid password.
			if (TextUtils.isEmpty(mPassword)) {
				mPasswordView.setError(getString(R.string.error_field_required));
				focusView = mPasswordView;
				cancel = true;
			} else if (mPassword.length() < 4) {
				mPasswordView.setError(getString(R.string.error_invalid_password));
				focusView = mPasswordView;
				cancel = true;
			}
	
			// Check for a valid email address.
			if (TextUtils.isEmpty(mEmail)) {
				mEmailView.setError(getString(R.string.error_field_required));
				focusView = mEmailView;
				cancel = true;
			} else if (!mEmail.contains("@")) {
				mEmailView.setError(getString(R.string.error_invalid_email));
				focusView = mEmailView;
				cancel = true;
			}
	
			if (cancel) {
				mIsLoggingIn = false;
				// There was an error; don't attempt login and focus the first
				// form field with an error.
				focusView.requestFocus();
				findViewById(R.id.sign_in_button).setEnabled(true);
			} else {
				mIsLoggingIn = true;
				// Show a progress spinner, and kick off a background task to
				// perform the user login attempt.
				mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
				showProgress(true);
								
				mDataManager.clearCollabRoomList();
				mDataManager.requestLogin(mEmail, mPassword, false);
				 
				if(getCurrentFocus() != null) {
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				}
			}
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
		
		
		if(show) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		} else {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		}
		
		if(settingsMenuItem != null) {
			settingsMenuItem.setEnabled(!show);
		}
		
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView.getId() == R.id.login_remember_user) {
			mUserPreferences.savePreferenceBoolean(Constants.nics_REMEMBER_USER, mRememberUserName.isChecked());
		} else if(buttonView.getId() == R.id.login_auto) {
			mUserPreferences.savePreferenceBoolean(Constants.nics_AUTO_LOGIN, mAutoLogin.isChecked());
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(mRememberUserName.isChecked() && v.getId() == R.id.email) {
			mUserPreferences.savePreferenceString(Constants.nics_USER_NAME, mEmailView.getText().toString().toLowerCase(mDataManager.getLocale()));
		} else if(mAutoLogin.isChecked() && v.getId() == R.id.password) {
			mUserPreferences.savePreferenceString(Constants.nics_USER_PASSWORD, mPasswordView.getText().toString());
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if(mRememberUserName.isChecked()) {
			mUserPreferences.savePreferenceBoolean(Constants.nics_REMEMBER_USER, mRememberUserName.isChecked());
			mUserPreferences.savePreferenceString(Constants.nics_USER_NAME, mEmailView.getText().toString().toLowerCase(mDataManager.getLocale()));
		}
		
		if(mAutoLogin.isChecked()) {
			mUserPreferences.savePreferenceBoolean(Constants.nics_AUTO_LOGIN, mAutoLogin.isChecked());
			mUserPreferences.savePreferenceString(Constants.nics_USER_PASSWORD, mPasswordView.getText().toString());
		}
	}
	
	BroadcastReceiver loginSuccessReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("Login_Debug", "Login Receiver called");
			mLoginStatusMessageView.setText(R.string.loading_incidents_and_collabrooms);
			onSaveInstanceState(new Bundle());
		}
	};
	
	BroadcastReceiver loginFailedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mIsLoggingIn = false;
			mDataManager.stopPollingAlarms();
			if(intent != null) {
				String message = intent.getStringExtra("message");
				mPasswordView.setError(message);
			}
			
			findViewById(R.id.sign_in_button).setEnabled(true);
			
			mIncidentsReceived = false;
			mOrganizaitonsRecieved = false;
			
			showProgress(false);
			mPasswordView.requestFocus();
		}
	};

	BroadcastReceiver dataReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			if(!mIncidentsReceived) {
				mIncidentsReceived = intent.getAction().equals(Intents.nics_SUCCESSFUL_GET_ALL_INCIDENT_INFO);
			}
			
			if(!mOrganizaitonsRecieved) {
				mOrganizaitonsRecieved = intent.getAction().equals(Intents.nics_SUCCESSFUL_GET_USER_ORGANIZATION_INFO);
			}
			
			
			
			Handler handler = new Handler();
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					if(mIncidentsReceived && mOrganizaitonsRecieved) {
						mDataManager.setTabletLayoutOn(mTabletLayoutToggle.isChecked());
						Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
						mainIntent.putExtra("showOrgSelector", true);
						startActivity(mainIntent);
						if(mSplashScreenDialog != null && mSplashScreenDialog.isShowing()) {
							mSplashScreenDialog.dismiss();
							mSplashScreenDialog = null;
						}
						finish();
						
						mLoginFormView.setVisibility(View.GONE);
						mLoginStatusView.setVisibility(View.GONE);
						
						mDataManager.requestWfsLayers();
					}
				}
			});
		}
	};
	
	protected void onResume() {
		super.onResume();
		
		if(mDataManager == null) {
			mDataManager = DataManager.getInstance(getApplicationContext());
		}
		mDataManager.stopPollingAlarms();
		
		if(mReceiversRegistered == false){
			mReceiversRegistered = true;
			Log.d("Login_Debug", "register Login Receiver AttemptLogin");
			registerReceiver(loginSuccessReceiver, new IntentFilter(Intents.nics_SUCCESSFUL_LOGIN));
			registerReceiver(loginFailedReceiver, new IntentFilter(Intents.nics_FAILED_LOGIN));
			registerReceiver(dataReceiver, new IntentFilter(Intents.nics_SUCCESSFUL_GET_INCIDENT_INFO));
			registerReceiver(dataReceiver, new IntentFilter(Intents.nics_SUCCESSFUL_GET_ALL_INCIDENT_INFO));
			registerReceiver(dataReceiver, new IntentFilter(Intents.nics_SUCCESSFUL_GET_USER_ORGANIZATION_INFO));
		}
		
		int serverNameIdx = Arrays.asList(getResources().getStringArray(R.array.config_server_list_values)).indexOf(mDataManager.getServer());
		mServerLabel = (TextView) findViewById(R.id.serverLabel);
		
		if(serverNameIdx < 0) {
			mServerLabel.setText(getString(R.string.server_, mDataManager.getServer()));
		} else {
			mServerLabel.setText(getString(R.string.server_, getResources().getStringArray(R.array.config_server_list_titles)[serverNameIdx]));
		}
		
		if(mDataManager.isLoggedIn() && mDataManager.getIncidents() != null && mDataManager.getOrganizations() != null) {
			mIncidentsReceived = true;
			mOrganizaitonsRecieved = true;
			dataReceiver.onReceive(this, null);
		} else if(isAutoLogin) {
			if(mIncidentRadio.isChecked()) {
				mDataManager.setWorkspaceId(1);
			} else if(mTrainingRadio.isChecked()) {
				mDataManager.setWorkspaceId(2);
			}
			attemptLogin();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		if(mReceiversRegistered) {
			Log.d("Login_Debug", "Unregister Login Receiver onPause");
			unregisterReceiver(loginSuccessReceiver);
			unregisterReceiver(loginFailedReceiver);
			unregisterReceiver(dataReceiver);
			mReceiversRegistered = false;
		}
		
		String username = mEmailView.getText().toString().toLowerCase(mDataManager.getLocale());
		String password =  mPasswordView.getText().toString();
		
		if(username != null && username.length() > 0) {
			mUserPreferences.savePreferenceString(Constants.nics_USER_NAME, username);
		} 
		
		if(password !=null && password.length() > 0) {
			mUserPreferences.savePreferenceString(Constants.nics_USER_PASSWORD, password);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mReceiversRegistered) {
			Log.d("Login_Debug", "Unregister Login Receiver onDestroy");
			unregisterReceiver(loginSuccessReceiver);
			unregisterReceiver(loginFailedReceiver);
			unregisterReceiver(dataReceiver);
			mReceiversRegistered = false;
		}
	};
	
	@Override
	public void onBackPressed() {
		//disable backbutton on login screen
	}
	
}
