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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.formgen.FormCheckBox;
import scout.edu.mit.ll.nics.android.formgen.FormColorPicker;
import scout.edu.mit.ll.nics.android.formgen.FormCoordinate;
import scout.edu.mit.ll.nics.android.formgen.FormDamageInformation;
import scout.edu.mit.ll.nics.android.formgen.FormEditText;
import scout.edu.mit.ll.nics.android.formgen.FormImageSelector;
import scout.edu.mit.ll.nics.android.formgen.FormNumericEditText;
import scout.edu.mit.ll.nics.android.formgen.FormSection;
import scout.edu.mit.ll.nics.android.formgen.FormSpinner;
import scout.edu.mit.ll.nics.android.formgen.FormWidget;
import scout.edu.mit.ll.nics.android.utils.Constants;



/**
 * FormFragment allows you to create dynamic form layouts based upon a json schema file.
 */

public class FormFragment extends Fragment implements OnFocusChangeListener {
	public static String SCHEMA_KEY_TYPE = "type";
	public static String SCHEMA_KEY_DISPLAY_NAME = "display_name";
	public static String SCHEMA_KEY_FONT_SIZE = "font_size";
	public static String SCHEMA_KEY_BOOL = "boolean";
	public static String SCHEMA_KEY_INT = "integer";
	public static String SCHEMA_KEY_STRING = "string";
	public static String SCHEMA_KEY_DAMAGE = "damage";
	public static String SCHEMA_KEY_COORDS = "coordinate";
	public static String SCHEMA_KEY_PRIORITY = "priority";
	public static String SCHEMA_KEY_TOGGLES = "toggles";
	public static String SCHEMA_KEY_DEFAULT = "default";
	public static String SCHEMA_KEY_MODIFIERS = "modifiers";
	public static String SCHEMA_KEY_OPTIONS = "options";
	public static String SCHEMA_KEY_META = "meta";
	public static String SCHEMA_KEY_HINT = "hint";
	public static String SCHEMA_KEY_SECTION = "section";
	public static String SCHEMA_KEY_ENABLED = "enabled";
	public static String SCHEMA_FILE = "schemafile";
	public static Object SCHEMA_KEY_IMAGE_SELECTOR = "image_selector";
	public static String SCHEMA_KEY_COLOR = "color";

	public static final LayoutParams defaultLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

	// -- data
	protected Map<String, FormWidget> mMap;
	protected ArrayList<FormWidget> mWidgets;

	// -- widgets
	protected LinearLayout mContainer;
	protected LinearLayout mLayout;
	protected ScrollView mViewport;
	private String mSchemaFile;

	// -----------------------------------------------
	//
	// parse data and build view
	//
	// -----------------------------------------------

	public FormFragment() {
		super();
	}
	
	
	@Override
	public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
		super.onInflate(activity, attrs, savedInstanceState);
		
        TypedArray a = activity.obtainStyledAttributes(attrs,
                R.styleable.FormFragment);
        
        mSchemaFile = a.getString(R.styleable.FormFragment_android_label);
        a.recycle();
	}

	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		super.onCreateView(inflater, container, savedInstanceState);
		
		mWidgets = new ArrayList<FormWidget>();
		mMap = new HashMap<String, FormWidget>();
		
		Bundle args = getArguments();
		if(args != null) {
			String temp = args.getString(SCHEMA_FILE);
			if(temp != null && !temp.isEmpty()) {
				mSchemaFile = temp;
			}
		}

		for(FormWidget widget : mWidgets) {
			widget.onCreateView(this);
		}
		
		try {
			String name;
			FormWidget widget;
			JSONObject property;
			JSONObject schema = new JSONObject(FormFragment.parseFileToString(this.getActivity(), mSchemaFile));
			JSONArray names = schema.names();

			for (int i = 0; i < names.length(); i++) {
				name = names.getString(i);

				if (name.equals(SCHEMA_KEY_META))
					continue;

				property = schema.getJSONObject(name);
				
				boolean toggles = hasToggles(property);

				String defaultValue = getDefault(property);
				int priority = property.getInt(FormFragment.SCHEMA_KEY_PRIORITY);

				widget = getWidget(name, property);
				if (widget == null)
					continue;
				
				widget.setPriority(priority);
				widget.setValue(defaultValue);

				if (toggles) {
					widget.setToggles(processToggles(property));
					widget.setToggleHandler(new FormFragment.FormWidgetToggleHandler());
				}

				if (property.has(FormFragment.SCHEMA_KEY_HINT))
					widget.setHint(property.getString(FormFragment.SCHEMA_KEY_HINT));

				mWidgets.add(widget);
				mMap.put(name, widget);
			}
		} catch (JSONException e) {
			Log.i(Constants.nics_DEBUG_ANDROID_TAG, e.getMessage());
		}

		// -- sort widgets on priority
		Collections.sort(mWidgets, new PriorityComparison());

		// -- create the layout
		mContainer = new LinearLayout(this.getActivity());
		mContainer.setOrientation(LinearLayout.VERTICAL);
		mContainer.setLayoutParams(FormFragment.defaultLayoutParams);

		mViewport = new ScrollView(this.getActivity());
		mViewport.setScrollbarFadingEnabled(false);
		mViewport.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
		mViewport.setLayoutParams(FormFragment.defaultLayoutParams);

		mLayout = new LinearLayout(this.getActivity());
		mLayout.setOrientation(LinearLayout.VERTICAL);
		mLayout.setLayoutParams(FormFragment.defaultLayoutParams);

		initToggles();

		for (int i = 0; i < mWidgets.size(); i++) {
			mLayout.addView(mWidgets.get(i).getView());
		}

		mViewport.addView(mLayout);
		mContainer.addView(mViewport);
		

		return mContainer;
	}

	// -----------------------------------------------
	//
	// populate and save
	//
	// -----------------------------------------------

	/**
	 * this method fills the form with existing data get the json string stored in the record we are editing create a json object ( if this fails then we know there is now existing record ) create a list of property names from the json object loop through the map returned by the Form class that maps widgets to property names if the map contains the property name as a key that means there is a widget to populate w/ a value
	 */
	public void populate(String jsonString, boolean editable) {
		try {
			String prop;
			FormWidget widget;
			if(!jsonString.isEmpty()) {
				JSONObject data = new JSONObject(jsonString);
				JSONArray properties = data.names();
				
				ArrayList<FormWidget> temp = new ArrayList<FormWidget>(mMap.values());
				for(FormWidget formWidget : temp) {
					if(editable && formWidget.getEnabled()) {
						formWidget.setEditable(true);
					} else {
						formWidget.setEditable(false);
					}
				}
				
				clear();
				for (int i = 0; i < properties.length(); i++) {
					prop = properties.getString(i);
					if (mMap.containsKey(prop)) {
						widget = mMap.get(prop);
						if(editable && widget.getEnabled()) {
							widget.setEditable(true);
						} else {
							widget.setEditable(false);
						}
						
						widget.setValue(data.getString(prop));

					}
				}
			} else {
				clear();
			}
		} catch (JSONException e) {

		}
	}
	
	public void clear() {
		if(mMap != null) {
			for(FormWidget widget : mMap.values()) {
				widget.setValue("");
			}
		}
	}

	/**
	 * this method preps the data and saves it if there is a problem w/ creating the json string, the method fails loop through each widget and set a property on a json object to the value of the widget's getValue() method
	 */
	public JSONObject save() {
		FormWidget widget;
		JSONObject data = new JSONObject();

		boolean success = true;

		try {
			for (int i = 0; i < mWidgets.size(); i++) {
				widget = mWidgets.get(i);
				String propertyName = widget.getPropertyName();
				String value = widget.getValue();
				data.put(widget.getPropertyName(), widget.getValue());
			}
		} catch (JSONException e) {
			success = false;
			return null;
		}

		if (success) {
			return data;
		}
		return null;
	}

	// -----------------------------------------------
	//
	// toggles
	//
	// -----------------------------------------------

	/**
	 * creates the map a map of values for visibility and references to the widgets the value affects
	 */
	protected HashMap<String, ArrayList<String>> processToggles(JSONObject property) {
		try {
			ArrayList<String> toggled;
			HashMap<String, ArrayList<String>> toggleMap = new HashMap<String, ArrayList<String>>();

			JSONObject toggleList = property.getJSONObject(FormFragment.SCHEMA_KEY_TOGGLES);
			JSONArray toggleNames = toggleList.names();

			if(toggleNames != null) {
				for (int j = 0; j < toggleNames.length(); j++) {
					String toggleName = toggleNames.getString(j);
					JSONArray toggleValues = toggleList.getJSONArray(toggleName);
					toggled = new ArrayList<String>();
					toggleMap.put(toggleName, toggled);
					for (int k = 0; k < toggleValues.length(); k++) {
						toggled.add(toggleValues.getString(k));
					}
				}
			}
			return toggleMap;

		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * returns a boolean indicating that the supplied json object contains a property for toggles
	 */
	protected boolean hasToggles(JSONObject obj) {
		try {
			obj.getJSONObject(FormFragment.SCHEMA_KEY_TOGGLES);
			return true;
		} catch (JSONException e) {
			return false;
		}
	}

	/**
	 * initializes the visibility of widgets that are togglable
	 */
	protected void initToggles() {
		int i;
		FormWidget widget;

		for (i = 0; i < mWidgets.size(); i++) {
			widget = mWidgets.get(i);
			updateToggles(widget);
		}
	}

	/**
	 * updates any widgets that need to be toggled on or off
	 * 
	 * @param widget
	 */
	protected void updateToggles(FormWidget widget) {
		int i;
		String name;
		ArrayList<String> toggles;
		ArrayList<FormWidget> ignore = new ArrayList<FormWidget>();

		toggles = widget.getToggledOn();
		for (i = 0; i < toggles.size(); i++) {
			name = toggles.get(i);
			if (mMap.get(name) != null) {
				FormWidget toggle = mMap.get(name);
				ignore.add(toggle);
				toggle.setVisibility(View.VISIBLE);
			}
		}

		toggles = widget.getToggledOff();
		for (i = 0; i < toggles.size(); i++) {
			name = toggles.get(i);
			if (mMap.get(name) != null) {
				FormWidget toggle = mMap.get(name);
				if (ignore.contains(toggle))
					continue;
				toggle.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * simple callbacks for widgets to use when their values have changed
	 */
	public class FormWidgetToggleHandler {
		public void toggle(FormWidget widget) {
			updateToggles(widget);
		}
	}

	// -----------------------------------------------
	//
	// utils
	//
	// -----------------------------------------------

	protected String getDefault(JSONObject obj) {
		try {
			return obj.getString(FormFragment.SCHEMA_KEY_DEFAULT);
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * helper class for sorting widgets based on priority
	 */
	class PriorityComparison implements Comparator<FormWidget> {
		public int compare(FormWidget item1, FormWidget item2) {
			return item1.getPriority() > item2.getPriority() ? 1 : -1;
		}
	}

	/**
	 * factory method for actually instantiating widgets
	 */
	protected FormWidget getWidget(String name, JSONObject property) {
		try {
			FragmentActivity activity = this.getActivity();
			String type = property.getString(FormFragment.SCHEMA_KEY_TYPE);
			boolean enabled = property.optBoolean(SCHEMA_KEY_ENABLED, true);
			String displayText = property.getString(SCHEMA_KEY_DISPLAY_NAME);
			int fontSize = -1;
			
			if(property.has(SCHEMA_KEY_FONT_SIZE)) {
				fontSize = property.getInt(SCHEMA_KEY_FONT_SIZE);
			}

			if (type.equals(FormFragment.SCHEMA_KEY_STRING)) {
				return new FormEditText(activity, name, displayText, enabled, fontSize, this,this);
			}

			if (type.equals(FormFragment.SCHEMA_KEY_BOOL)) {
				return new FormCheckBox(activity, name, displayText, enabled, this,this);
			}
			
			if (type.equals(FormFragment.SCHEMA_KEY_SECTION)) {
				return new FormSection(activity, name, displayText, enabled, this,this);
			}

			if (type.equals(FormFragment.SCHEMA_KEY_INT)) {
				if (property.has(FormFragment.SCHEMA_KEY_OPTIONS)) {
					JSONObject options = property.getJSONObject(FormFragment.SCHEMA_KEY_OPTIONS);
					return new FormSpinner(activity, name, displayText, options, enabled, this,this);
				} else {
					return new FormNumericEditText(activity, name, displayText, enabled, this,this);
				}
			}
			if (type.equals(FormFragment.SCHEMA_KEY_DAMAGE)) {
				JSONObject options = property.getJSONObject(FormFragment.SCHEMA_KEY_OPTIONS);
				return new FormDamageInformation(activity, name, displayText, options, enabled, this,this);
			}
			
			if(type.equals(FormFragment.SCHEMA_KEY_COORDS)) {
				return new FormCoordinate(activity, name, displayText, enabled, this,this);
			}
			
			if(type.equals(FormFragment.SCHEMA_KEY_IMAGE_SELECTOR )) {
				return new FormImageSelector(activity, name, displayText, enabled, this,this);
			}
			
			if(type.equals(FormFragment.SCHEMA_KEY_COLOR )) {
				return new FormColorPicker(activity, name, displayText, enabled, this,this);
			}
		} catch (JSONException e) {
			return null;
		}
		return null;
	}
	
	public static String parseFileToString(Context context, String filename) {
		try {
			int fileId = context.getResources().getIdentifier(filename, "raw", context.getPackageName());
			InputStream stream = context.getResources().openRawResource(fileId);
			int size = stream.available();

			byte[] bytes = new byte[size];
			stream.read(bytes);
			stream.close();

			return new String(bytes);

		} catch (IOException e) {
			Log.i(Constants.nics_DEBUG_ANDROID_TAG, "IOException: " + e.getMessage());
		}
		return null;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		this.save();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(data == null){
			return;
		}
		
		if (requestCode == 200 && resultCode == 0) {
			for(FormWidget widget : mWidgets) {
				if(widget.getClass().getName().equals(FormColorPicker.class.getName())) {
					int temp = data.getExtras().getInt("pickedColor");
					
					int[] mColor = new int[] { 255, Color.red(temp), Color.green(temp), Color.blue(temp) };
					((FormColorPicker) widget).setColor(temp);
				}
			}
		}else{
		
			for(FormWidget widget : mWidgets) {
				if(widget.getClass().getName().equals(FormImageSelector.class.getName())) {
					((FormImageSelector)widget).onActivityResult(requestCode, resultCode, data);
				} else {
					widget.setValue(widget.getValue());
				}
			}
		}
	}
}
