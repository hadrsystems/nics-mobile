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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import scout.edu.mit.ll.nics.android.fragments.FormFragment;

public class FormCheckBox extends FormWidget {

	protected int mPriority;
	protected CheckBox mCheckbox;

	public FormCheckBox(FragmentActivity context, String property, String displayText, boolean enabled, OnFocusChangeListener listener,Fragment fragment) {
		super(context, property, displayText, fragment);

		mEnabled = enabled;
		mCheckbox = new CheckBox(context);
		mCheckbox.setText(this.getDisplayText());
		mCheckbox.setEnabled(enabled);
		mCheckbox.setOnFocusChangeListener(listener);
		
		mLayout.addView(mCheckbox);
	}

	@Override
	public String getValue() {
		return String.valueOf(mCheckbox.isChecked() ? "1" : "0");
	}

	public void setValue(String value) {
		mCheckbox.setChecked(value.equals("1"));
	}

	@Override
	public void setToggleHandler(FormFragment.FormWidgetToggleHandler handler) {
		super.setToggleHandler(handler);
		mCheckbox.setOnCheckedChangeListener(new ChangeHandler(this));
	}

	class ChangeHandler implements CompoundButton.OnCheckedChangeListener {
		protected FormWidget mWidget;

		public ChangeHandler(FormWidget widget) {
			mWidget = widget;
		}

		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (mHandler != null) {
				mHandler.toggle(mWidget);
			}
		}

	}

	@Override
	public void setEditable(boolean isEditable) {
		mCheckbox.setEnabled(isEditable);
	}
}
