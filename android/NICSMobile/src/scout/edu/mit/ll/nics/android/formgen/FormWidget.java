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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.fragments.FormFragment;

public abstract class FormWidget {
	protected View mView;
	protected String mProperty;
	protected String mDisplayTextKey;
	protected FragmentActivity mContext;
	protected int mPriority;
	protected LinearLayout mLayout;
	protected FormFragment.FormWidgetToggleHandler mHandler;
	protected boolean mEnabled = true;
	protected Fragment mFragment;
	
	protected LayoutInflater mLayoutInflater;

	protected HashMap<String, ArrayList<String>> mToggles;

	private DataManager mDataManager;
	
	public FormWidget(FragmentActivity context, String name, String displayText,Fragment fragment) {
		mContext = context;
		
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mLayout = new LinearLayout(context);
		mLayout.setLayoutParams(FormFragment.defaultLayoutParams);
		mLayout.setOrientation(LinearLayout.VERTICAL);

		mFragment = fragment;
		
		mProperty = name;
		mDisplayTextKey = displayText;
	}
	
	public void onCreateView(Fragment fragment){
		
		mDataManager = DataManager.getInstance();
		mFragment = fragment;
	}
	
	// -----------------------------------------------
	//
	// view
	//
	// -----------------------------------------------
	/**
	 * return LinearLayout containing this widget's view elements
	 */
	public View getView() {
		return mLayout;
	}

	/**
	 * toggles the visibility of this widget
	 * 
	 * @param value
	 */
	public void setVisibility(int value) {
		mLayout.setVisibility(value);
	}

	// -----------------------------------------------
	//
	// set / get value
	//
	// -----------------------------------------------

	/**
	 * returns value of this widget as String
	 */
	public String getValue() {
		return "";
	}

	/**
	 * sets value of this widget, method should be overridden in sub-class
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		// -- override
	}

	// -----------------------------------------------
	//
	// modifiers
	//
	// -----------------------------------------------

	/**
	 * sets the hint for the widget, method should be overriden in sub-class
	 */
	public void setHint(String value) {
		// -- override
	}

	/**
	 * sets an object that contains keys for special properties on an object
	 * 
	 * @param modifiers
	 */
	public void setModifiers(JSONObject modifiers) {
		// -- override
	}

	// -----------------------------------------------
	//
	// set / get priority
	//
	// -----------------------------------------------

	/**
	 * sets the visual priority of this widget essentially this means it's physical location in the form
	 */
	public void setPriority(int value) {
		mPriority = value;
	}

	/**
	 * returns visual priority
	 * 
	 * @return
	 */
	public int getPriority() {
		return mPriority;
	}

	// -----------------------------------------------
	//
	// property name mods
	//
	// -----------------------------------------------

	/**
	 * returns the un-modified name of the property this widget represents
	 */
	public String getPropertyName() {
		return mProperty;
	}

	/**
	 * returns a title case version of this property
	 * 
	 * @return
	 */
	public String getDisplayText() {
		
		Resources res = mContext.getResources();
        int resId = res.getIdentifier(mDisplayTextKey, "string", mContext.getPackageName());
        return (res.getString(resId));
	}

	/**
	 * takes a property name and modifies
	 * 
	 * @param s
	 * @return
	 */
	public static String toTitleCase(String s) {
		if(s.split(" ").length != 1) {
			char[] chars = s.trim().toLowerCase().toCharArray();
			boolean found = false;
	
			for (int i = 0; i < chars.length; i++) {
				if (!found && Character.isLetter(chars[i])) {
					chars[i] = Character.toUpperCase(chars[i]);
					found = true;
				} else if (Character.isWhitespace(chars[i])) {
					found = false;
				}
			}
	
			return String.valueOf(chars);
		} else {
			char[] chars = s.trim().toCharArray();
			chars[0] = Character.toUpperCase(chars[0]);
			
			return String.valueOf(chars);
		}
	}

	// -----------------------------------------------
	//
	// toggles
	//
	// -----------------------------------------------

	/**
	 * sets the list of toggles for this widgets the structure of the data looks like this: HashMap<value of property for visibility, ArrayList<list of properties to toggle on>>
	 */
	public void setToggles(HashMap<String, ArrayList<String>> toggles) {
		mToggles = toggles;
	}

	/**
	 * return list of widgets to toggle on
	 * 
	 * @param value
	 * @return
	 */
	public ArrayList<String> getToggledOn() {
		if (mToggles == null)
			return new ArrayList<String>();

		if (mToggles.get(getValue()) != null) {
			return mToggles.get(getValue());
		} else {
			return new ArrayList<String>();
		}
	}

	/**
	 * return list of widgets to toggle off
	 * 
	 * @param value
	 * @return
	 */
	public ArrayList<String> getToggledOff() {
		ArrayList<String> result = new ArrayList<String>();
		if (mToggles == null)
			return result;

		Set<String> set = mToggles.keySet();

		for (String key : set) {
			if (!key.equals(getValue())) {
				ArrayList<String> list = mToggles.get(key);
				if (list == null)
					return new ArrayList<String>();
				for (int i = 0; i < list.size(); i++) {
					result.add(list.get(i));
				}
			}
		}

		return result;
	}

	/**
	 * sets a handler for value changes
	 * 
	 * @param handler
	 */
	public void setToggleHandler(FormFragment.FormWidgetToggleHandler handler) {
		mHandler = handler;
	}

	public abstract void setEditable(boolean b);

	public boolean getEnabled() {
		return mEnabled;
	}
}
