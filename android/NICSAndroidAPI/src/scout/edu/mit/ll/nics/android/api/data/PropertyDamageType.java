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
package scout.edu.mit.ll.nics.android.api.data;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import scout.edu.mit.ll.nics.android.api.DataManager;

public enum PropertyDamageType {
	@SerializedName("")
	BLANK(0, "dr_propertydamagetype_blank"),
	
	@SerializedName("Minor: 10 percent")
	MINOR(1, "dr_propertydamagetype_minor"),

	@SerializedName("Major: 10 to 50 percent")
	MAJOR(2, "dr_propertydamagetype_major"),

	@SerializedName("Destroyed: 50+ percent")
	DESTROYED(3, "dr_propertydamagetype_destroyed");

	int id;
	String text;

	PropertyDamageType(final int id, final String text) {
		this.id = id;
		this.text = text;
	}


	public int getId() {
		return id;
	}


	public String getTextKey() {
		return text;
	}

    public String getText () {
    Context mContext = DataManager.getInstance().getContext();
   
    String translatedText = "";
   
        android.content.res.Resources res = mContext.getResources();
        int resId = res.getIdentifier(this.text, "string", mContext.getPackageName());
        if (0 != resId) {
        translatedText = (res.getString(resId));
        }
   
        return translatedText;
    }
	
	public static PropertyDamageType lookUp(final int id) {
		PropertyDamageType value = null;

		for (PropertyDamageType simpleReportCategoryType : PropertyDamageType.values()) {
			if (simpleReportCategoryType.getId() == id) {
				value = simpleReportCategoryType;

				break;
			}
		}

		return value;
	}


	public static PropertyDamageType lookUp(final String text) {
		PropertyDamageType value = null;

		for (PropertyDamageType simpleReportCategoryType : PropertyDamageType.values()) {
			if (simpleReportCategoryType.getTextKey().equals(text)) {
				value = simpleReportCategoryType;

				break;
			}
		}

		return value;
	}

	@Override
	public String toString() {
		return text;
	}
}
