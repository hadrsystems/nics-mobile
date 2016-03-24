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

import com.google.common.io.Resources;
import com.google.gson.annotations.SerializedName;

import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.R;

/**
 * @author Glenn L. Primmer
 *
 * This defines the different types of simple report categories.
 */
public enum SimpleReportCategoryType {
	@SerializedName("None")
    BLANK	(0, "sr_categorytype_none"),
    
	@SerializedName("Agency Representative")
    AR		(1, "sr_categorytype_agency_representative"),
	
    @SerializedName("Comp/Claim/Damage Advisory")
	COMP	(2, "sr_categorytype_comp_claim_damage_advisory"),

    @SerializedName("Finance Section")
    FS		(3, "sr_categorytype_finance_section"),
	
    @SerializedName("Ground Support Request")
    GSR		(4, "sr_categorytype_ground_support_request"),
	
    @SerializedName("Incident Commander")
    IC		(5, "sr_categorytype_incident_commander"),
	
    @SerializedName("Liaison Officer")
    LO		(6, "sr_categorytype_liason_officer"),
	
    @SerializedName("Logistics Section")
    LS		(7, "sr_categorytype_logistics_section"),
    			
    @SerializedName("Operations Section")
	OS		(8, "sr_categorytype_operations_section"),
	
    @SerializedName("Other (See Message)")
    OTHR	(9, "sr_categorytype_other"),

	@SerializedName("Plans Section")
    PS 		(10, "sr_categorytype_plans_section"),

	@SerializedName("Public Information Officer")
    PIO		(11, "sr_categorytype_public_information_officer"),
	
    @SerializedName("Safety Officer")
    SO		(12, "sr_categorytype_safety_officer"),
	
    @SerializedName("Suppression Repair")
    SR		(13, "sr_categorytype_suppression_repair");

    /**
     * The numerical representation of the simple report category type.
     */
    int    id;

    /**
     * The textual representation of the simple report category type.
     */
    String text;

    /**
     * Constructor.
     *
     * @param id The numerical representation of the simple report category type.
     *
     * @param text The textual representation of the simple report category type.
     */
    SimpleReportCategoryType (final int    id,
                              final String text) {
        this.id   = id;
        this.text = text;
    }

    /**
     * Returns the numerical representation of the simple report category type.
     *
     * @return The numerical representation of the simple report category type.
     */
    public int getId () {
        return id;
    }

    /**
     * Returns the textual representation of the simple report category type.
     *
     * @return The textual representation of the simple report category type.
     */
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

    public String getTextKey () {
    	return this.text;
    }
    
    /**
     * Looks up an simple report category type for the provided numerical representation of an simple report category
     * type.
     *
     * @param id Numerical representation of an ICS position type.
     *
     * @return The simple report category type that corresponds to the numerical representation provided.  If there
     * is no simple report category type that corresponds to the numerical representation provided then 'null' will be
     * returned.
     */
    public static SimpleReportCategoryType lookUp (final int id) {
        SimpleReportCategoryType value = null;

        for (SimpleReportCategoryType simpleReportCategoryType : SimpleReportCategoryType.values ()) {
            if (simpleReportCategoryType.getId () == id) {
                value = simpleReportCategoryType;

                break;
            }
        }
        
        if(value == null) {
        	value = BLANK;
        }

        return value;
    }

    /**
     * Looks up an simple report category type for the provided textual representation of an simple report category
     * type.
     *
     * @param text Textual representation of an ICS position type.
     *
     * @return The simple report category type that corresponds to the textual representation provided.  If there
     * is no simple report category type that corresponds to the textual representation provided then 'null' will be returned.
     */
    public static SimpleReportCategoryType lookUp (final String text) {
        SimpleReportCategoryType value = null;

        for (SimpleReportCategoryType simpleReportCategoryType : SimpleReportCategoryType.values ()) {
            if (simpleReportCategoryType.getTextKey().equals(text)) {
                value = simpleReportCategoryType;

                break;
            }
        }

        return value;
    }
    
    public static SimpleReportCategoryType lookUpByName (final String text) {
        SimpleReportCategoryType value = null;

        for (SimpleReportCategoryType simpleReportCategoryType : SimpleReportCategoryType.values ()) {
            if (simpleReportCategoryType.name().equals(text)) {
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
