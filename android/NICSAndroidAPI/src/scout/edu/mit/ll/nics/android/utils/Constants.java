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
package scout.edu.mit.ll.nics.android.utils;

import android.content.Context;
import android.content.res.Resources;

public class Constants {

	public static final String nics_UTF8 = "UTF-8";
	public static final String nics_ISO_8859_1 = "ISO-8859-1";
	public static final String nics_LRF_DEVICE_NAME = "TP360B";
	public static final String nics_BT_SERIALPORT_SERVICEID = "00001101-0000-1000-8000-00805F9B34FB";
	public static final String nics_USER_PREFERENCES = "nics_USER_PREFERENCES";
	public static final String nics_REMEMBER_USER = "nics_REMEMBER_USER";
	public static final String nics_AUTO_LOGIN = "nics_AUTO_LOGIN";
	public static final String nics_USER_NAME = "nics_USER_NAME";
	public static final String nics_USER_PASSWORD = "nics_USER_PASSWORD";
	public static final String nics_USER_KEY = "VAYHxGeIOY4lQ7J55mYoJw==:IUtPpNtl2yRmqGLqbE4QVBo6VID00J+7lc42oWSJdMY=:BxbKRpeWgehf3fK1yfbZcLbsZWw6Ec/7TKnetMOb5Bs=";
	public static final String nics_FIRST_LOGIN = "nics_FIRST_LOGIN";
	
	public static final String PREFERENCES_NAME = "nics.pref";
	public static final int PREFERENCES_MODE = 0;
	public static final String USER_DATA = "user_data";
	public static final String USER_ID = "user_id";
	public static final String USER_ORG_ID = "user_org_id";
	public static final String USER_SESSION_ID = "user_session_id";
	public static final String USER_NAME = "user_name";
	
	public static final String ASSIGNMENT_START = "assignment_start";
	public static final String ASSIGNMENT_END = "assignment_end";
	public static final String ASSIGNMENT_UNIT_NAME = "assignment_unit_name";
	public static final String ASSIGNMENT_PERSONNEL = "assignment_personnel";
	public static final String ASSIGNMENT_TASKS = "assignment_task";
	
	public static final String INCIDENT_ID = "incident_id";
	public static final String INCIDENT_NAME = "incident_name";
	public static final String INCIDENT_LATITUDE = "incident_latitude";
	public static final String INCIDENT_LONGITUDE = "incident_longitude";
	
	public static final String COLLABROOM_ID = "collabroom_id";
	public static final String COLLABROOM_NAME = "collabroom_name";
	public static final String WORKSPACE_ID = "workspace_id";
	public static final String nics_DEBUG_ANDROID_TAG = "nics";
	public static final String nics_LRF_DEBUG_ANDROID_TAG = "nics_LRF";
	public static final String nics_DATABASE_NAME = "nics.db";
	public static final int nics_DATABASE_VERSION = 6;

	public static final int LOCATION_UTM = 3;
	public static final int LOCATION_MGRS = 4;
	
	public static final String LAST_LATITUDE = "last_latitude";
	public static final String LAST_LONGITUDE = "last_longitude";
	public static final String LAST_ALTITUDE = "last_altitude";
	public static final String LAST_ACCURACY = "last_accuracy";
	public static final String LAST_COURSE = "last_course";
	public static final String LAST_MDT_TIME = "last_mdt_time";
	public static final String LAST_HR = "last_hr";
	public static final String LAST_HSI = "last_hsi";
	
	public static final String WEATHER_PAYLOAD = "weather_payload";
	
	public static final String PREVIOUS_INCIDENT_ID = "previous_incident_id";
	public static final String PREVIOUS_INCIDENT_NAME = "previous_incident_name";

	public static final String PREVIOUS_COLLABROOM = "previous_collabroom";
//	public static final String PREVIOUS_COLLABROOM_ID = "previous_collabroom_id";
//	public static final String PREVIOUS_COLLABROOM_NAME = "previous_collabroom_name";
	
	public static final String SELECTED_COLLABROOM = "selected_collabroom";
//	public static final String SELECTED_COLLABROOM_ID = "selected_collabroom_id";
//	public static final String SELECTED_COLLABROOM_NAME = "selected_collabroom_name";

	public static final String SAVED_INCIDENTS = "saved_incidents";
	
	public static final String nics_MAP_MARKUP_STATE = "nics_MAP_MARKUP_STATE";
	public static final String nics_MAP_MARKUP_COORDINATES = "nics_MAP_MARKUP_COORDINATES";
	public static final String nics_MAP_PREVIOUS_CAMERA = "nics_MAP_PREVIOUS_CAMERA";
	public static final String nics_MAP_TRAFFIC_ENABLED = "nics_MAP_TRAFFIC_ENABLED";
	public static final String nics_MAP_INDOOR_ENABLED = "nics_MAP_INDOOR_ENABLED";
	public static final String nics_MAP_TYPE = "nics_MAP_TYPE";
	public static final String nics_MAP_CURRENT_SHAPE_TYPE = "nics_MAP_CURRENT_SHAPE_TYPE";
	public static final String nics_MAP_COORDINATES_COLOR_RED = "nics_MAP_COORDINATES_RED";
	public static final String nics_MAP_COORDINATES_COLOR_GREEN = "nics_MAP_COORDINATES_GREEN";
	public static final String nics_MAP_COORDINATES_COLOR_BLUE = "nics_MAP_COORDINATES_BLUE";
	public static final String nics_MAP_CURRENT_SYMBOL_RESOURCE_ID = "nics_MAP_CURRENT_SYMBOL_RESOURCE_ID";
	public static final String nics_MAP_ACTIVE_WFS_LAYERS = "nics_MAP_ACTIVE_WFS_LAYERS";
	
	public static final String IPLANET_COOKIE_DOMAIN = "IPLANET_COOKIE_DOMAIN";
	public static final String AMAUTH_COOKIE_DOMAIN = "AMAUTH_COOKIE_DOMAIN";
	public static final String CUSTOM_COOKIE_DOMAIN = "CUSTOM_COOKIE_DOMAIN";
	
	public static final String nics_TIME_FORMAT = "MM/dd kk:mm:ss";
	
	public enum NavigationOptions {
		SELECTINCIDENT(0),
		OVERVIEW(1),
		GENERALMESSAGE(2),
		FIELDREPORT(3),
		RESOURCEREQUEST(4),
		WEATHERREPORT(5),
		MAPCOLLABORATION(6),
		CHATLOG(7),
		GAR(8),
		USERINFO(9),
		LOGOUT(10),
		DAMAGESURVEY(11),
		CATANREQUEST(12),
		UXOREPORT(13),
		UXOFILTER(14);
		
		private final int value;
		
	    private NavigationOptions(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	    public String getLabel(Context context) {
	        Resources res = context.getResources();
	        int resId = res.getIdentifier(this.name(), "string", context.getPackageName());
	        if (0 != resId) {
	            return (res.getString(resId));
	        }
	        return (name());
	    }

	}
}
