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

public enum PropertyType {
	@SerializedName("Not Set")
	NOTSET(0, "dr_propertytype_notset"),
	
	@SerializedName("Car")
	CAR(1, "dr_propertytype_car"),
	
	@SerializedName("Commercial - Multi-Story")
	COMMERCIAL_MULTISTORY(2, "dr_propertytype_commercialmultistory"),
	
	@SerializedName("Commercial - Single Story")
	COMMERCIAL_SINGLESTORY(3, "dr_propertytype_commercialsinglestory"),

	@SerializedName("Double Wide Trailer")
	DOUBLE_WIDE_TRAILER(4, "dr_propertytype_doublewidetrailer"),
	
	@SerializedName("Motorhome")
	MOTORHOME(5, "dr_propertytype_motorhome"),

	@SerializedName("Other - Barn")
	OTHER_BARN(6, "dr_propertytype_otherbarn"),
	
	@SerializedName("Other - Garage")
	OTHER_GARAGE(7, "dr_propertytype_othergarage"),
	
	@SerializedName("Other - Out Building")
	OTHER_OUTBUILDING(8, "dr_propertytype_otheroutbuilding"),
	
	@SerializedName("Residence - Multi-Family")
	RESIDENCE_MULTIFAMILY(9, "dr_propertytype_residencemultifamily"),
	
	@SerializedName("Residence - Multi-Story Multi-Family-Family")
	RESIDENCE_MULTISTORY_MULTIFAMILY(10, "dr_propertytype_residencemultistorymultifamily"),
	
	@SerializedName("Residence - Multi-Story Single Family")
	RESIDENCE_MULTISTORY_SINGLEFAMILY(11, "dr_propertytype_residencemultistorysinglefamily"),

	@SerializedName("Residence - Single Family")
	RESIDENCE_SINGLEFAMILY(12, "dr_propertytype_residencesinglefamily"),
	
	@SerializedName("Single Wide Trailer")
	SINGLE_WIDE_TRAILER(13, "dr_propertytype_singlewidetrailer"),

	@SerializedName("Trailer")
	TRAILER(14, "dr_propertytype_trailer"),
	
	@SerializedName("Truck")
	TRUCK(15, "dr_propertytype_truck"),
	
	@SerializedName("Van")
	VAN(16, "dr_propertytype_van");

	int id;
	String text;

	PropertyType(final int id, final String text) {
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
	
	public static PropertyType lookUp(final int id) {
		PropertyType value = null;

		for (PropertyType simpleReportCategoryType : PropertyType.values()) {
			if (simpleReportCategoryType.getId() == id) {
				value = simpleReportCategoryType;

				break;
			}
		}

		return value;
	}


	public static PropertyType lookUp(final String text) {
		PropertyType value = null;

		for (PropertyType simpleReportCategoryType : PropertyType.values()) {
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
