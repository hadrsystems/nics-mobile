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
package edu.mit.ll.phinics.android.api.data;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import edu.mit.ll.phinics.android.api.DataManager;

/**
 * 
 *
 * This defines the enumeration of the different Weather Source Types.
 */
public enum UxoColorTypes {
	@SerializedName("White")
    White(0, "ur_color_white"),
    
    @SerializedName("Red")
	Red(1, "ur_color_red"),
    
    @SerializedName("Orange")
	Orange(2, "ur_color_orange"),
    
    @SerializedName("Yellow")
	Yellow(3, "ur_color_yellow"),
    
    @SerializedName("Green")
	Green(4, "ur_color_green"),

    @SerializedName("Teal")
    Teal(5, "ur_color_teal"),
    
    @SerializedName("Blue")
    Blue(6, "ur_color_blue"),
    
    @SerializedName("Purple")
    Purple(7, "ur_color_purple"),
    
    @SerializedName("Pink")
    Pink(8, "ur_color_pink"),
    
    @SerializedName("Grey")
    Grey(9, "ur_color_grey"),
    
    @SerializedName("Black")
    Black(10, "ur_color_black");
	
    int    id;
    String text;

    UxoColorTypes (final int    id,
              final String text) {
        this.id   = id;
        this.text = text;
    }
    public int getId () {
        return id;
    }

    public String getTextKey () {
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

    public static UxoColorTypes lookUp (final int id) {
        UxoColorTypes value = null;

        for (UxoColorTypes formType : UxoColorTypes.values ()) {
            if (formType.getId () == id) {
                value = formType;

                break;
            }
        }

        return value;
    }

    public static UxoColorTypes lookUp (final String text) {
        UxoColorTypes value = null;

        for (UxoColorTypes formType : UxoColorTypes.values ()) {
            if (formType.getTextKey ().equals (text)) {
                value = formType;

                break;
            }
        }

        return value;
    }
}
