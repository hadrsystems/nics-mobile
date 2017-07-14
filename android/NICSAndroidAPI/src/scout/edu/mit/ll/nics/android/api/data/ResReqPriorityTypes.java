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

import com.google.gson.annotations.SerializedName;

import android.content.Context;
import scout.edu.mit.ll.nics.android.api.DataManager;

/**
 * 
 *
 * This defines the enumeration of the different Weather Source Types.
 */
public enum ResReqPriorityTypes {
	@SerializedName("Urgent")
    Urgent(0, "resreq_priority_urgent"),
    
    @SerializedName("Regular")
	Regular(1, "resreq_priority_regular");
    
    int    id;
    String text;

    ResReqPriorityTypes (final int    id,
              final String text) {
        this.id   = id;
        this.text = text;
    }
    public int getId () {
        return id;
    }

    public String getTextKey () {
    	return this.text;
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

    public static ResReqPriorityTypes lookUp (final int id) {
        ResReqPriorityTypes value = null;

        for (ResReqPriorityTypes formType : ResReqPriorityTypes.values ()) {
            if (formType.getId () == id) {
                value = formType;

                break;
            }
        }

        return value;
    }

    public static ResReqPriorityTypes lookUp (final String text) {
        ResReqPriorityTypes value = null;

        for (ResReqPriorityTypes formType : ResReqPriorityTypes.values ()) {
            if (formType.getTextKey().equals(text)) {
                value = formType;

                break;
            }
        }

        return value;
    }
}
