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
package scout.edu.mit.ll.nics.android.formgen;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.fragments.FormFragment;
import scout.edu.mit.ll.nics.android.utils.Constants;

public class FormSpinner extends FormWidget {
	protected JSONObject mOptions;
	protected TextView mLabel;
	protected Spinner mSpinner;
	protected Map<String, String> mPropmap;
	protected ArrayAdapter<String> mAdapter;

	public FormSpinner(FragmentActivity context, String property, String displayText, JSONObject options, boolean enabled, OnFocusChangeListener listener,Fragment fragment) {
		super(context, property, displayText, fragment);

		mEnabled = enabled;
		mOptions = options;

		mLabel = new TextView(context);
		mLabel.setText(getDisplayText());
		mLabel.setLayoutParams(FormFragment.defaultLayoutParams);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mSpinner = new Spinner(context, Spinner.MODE_DIALOG);
		} else {
			mSpinner = new Spinner(context);
		}
		
		mSpinner.setLayoutParams(FormFragment.defaultLayoutParams);
		mSpinner.setEnabled(enabled);
		mSpinner.setOnFocusChangeListener(listener);

		String p;
		String name;

		mPropmap = new HashMap<String, String>();
		mAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		mAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(mAdapter);
		mSpinner.setSelection(0);

		try {
			for (int i = 0; i < options.length(); i++) {
				name = String.valueOf(i);			
				p = options.getString(name);

				Resources res = mContext.getResources();
		        int resId = res.getIdentifier(p, "string", mContext.getPackageName());
				
				mAdapter.add((res.getString(resId)));
				mPropmap.put(p, name);
			}
		} catch (JSONException e) {

		}

		mLayout.addView(mLabel);
		mLayout.addView(mSpinner);
	}

	@Override
	public String getValue() {

		String getItemResults = mAdapter.getItem(mSpinner.getSelectedItemPosition());
		String keyFromReverseLanguage = DataManager.getInstance().reverseLanguageLookup(getItemResults);

		return mPropmap.get(keyFromReverseLanguage); 	//(res.getString(resId)));
	}

	@Override
	public void setValue(String value) {
		try {
			String name;
			JSONArray names = mOptions.names();
			
			if(value != null && !value.isEmpty()) {
				for (int i = 0; i < names.length(); i++) {
					name = names.getString(i);
	
					if (name.equals(value)) {
						String item = mOptions.getString(name);
						
						Resources res = mContext.getResources();
				        int resId = res.getIdentifier(item, "string", mContext.getPackageName());

						mSpinner.setSelection(mAdapter.getPosition(res.getString(resId)));
					}
				}
			} else {
				mSpinner.setSelection(0);
			}
		} catch (JSONException e) {
			Log.i(Constants.nics_DEBUG_ANDROID_TAG, e.getMessage());
		}
	}

	@Override
	public void setToggleHandler(FormFragment.FormWidgetToggleHandler handler) {
		super.setToggleHandler(handler);
		mSpinner.setOnItemSelectedListener(new SelectionHandler(this));
	}

	class SelectionHandler implements AdapterView.OnItemSelectedListener {
		protected FormWidget mWidget;

		public SelectionHandler(FormWidget widget) {
			mWidget = widget;
		}

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (mHandler != null) {
				mHandler.toggle(mWidget);
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}

	}

	@Override
	public void setEditable(boolean isEditable) {
		mSpinner.setEnabled(isEditable);
	}
}
