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

public class Intents {

	public static final String nics_SUCCESSFUL_LOGIN = "nics_SUCCESSFUL_LOGIN";
	public static final String nics_FAILED_LOGIN = "nics_FAILED_LOGIN";
	
	public static final String nics_SUCCESSFUL_GET_INCIDENT_INFO = "nics_SUCCESSFUL_GET_INCIDENT_INFO";
	public static final String nics_SUCCESSFUL_GET_ALL_INCIDENT_INFO = "nics_SUCCESSFUL_GET_ALL_INCIDENT_INFO";
	public static final String nics_SUCCESSFUL_GET_ASSIGNMENT_INFO = "nics_SUCCESSFUL_GET_ASSIGNMENT_INFO";

	public static final String nics_COLLABROOM_SWITCHED = "nics_COLLABROOM_SWITCHED";
	public static final String nics_INCIDENT_SWITCHED = "nics_INCIDENT_SWITCHED";
	
	public static final String nics_BT_CONNECT = "nics_BT_CONNECT";
	public static final String nics_BT_DISCONNECT = "nics_BT_DISCONNECT";

	public static final String nics_NEW_TASK_RECEIVED = "nics_NEW_TASK_RECEIVED";
	public static final String nics_NEW_ASSIGNMENT_RECEIVED = "nics_NEW_ASSIGNMENT_RECEIVED";
	public static final String nics_UPDATE_ASSIGNMENT_RECEIVED = "nics_UPDATE_ASSIGNMENT_RECEIVED";
	public static final String nics_NEW_CHAT_RECEIVED = "nics_NEW_CHAT_RECEIVED";
	public static final String nics_LAST_CHAT_RECEIVED = "nics_LAST_CHAT_RECEIVED";
	public static final String nics_NEW_TASK_RECEIVED_OVERVIEW = "nics_NEW_TASK_RECEIVED_OVERVIEW";
	public static final String nics_NEW_PERSONAL_HISTORY_RECEIVED = "nics_NEW_PERSONAL_HISTORY_RECEIVED";
	public static final String nics_NEW_FIELD_REPORT_RECEIVED = "nics_NEW_FIELD_REPORT_RECEIVED";
	public static final String nics_NEW_DAMAGE_REPORT_RECEIVED = "nics_NEW_DAMAGE_REPORT_RECEIVED";
	public static final String nics_NEW_MARKUP_RECEIVED = "nics_NEW_MARKUP_RECEIVED";
	public static final String nics_NEW_RESOURCE_REQUEST_RECEIVED = "nics_NEW_RESOURCE_REQUEST_RECEIVED";
	public static final String nics_NEW_SIMPLE_REPORT_RECEIVED = "nics_NEW_SIMPLE_REPORT_RECEIVED";
	public static final String nics_NEW_WEATHER_REPORT_RECEIVED = "nics_NEW_WEATHER_REPORT_RECEIVED";
		
	public static final String nics_SENT_SIMPLE_REPORTS_CLEARED = "nics_SENT_SIMPLE_REPORTS_CLEARED";
	public static final String nics_SENT_DAMAGE_REPORTS_CLEARED = "nics_SENT_DAMAGE_REPORTS_CLEARED";
	public static final String nics_SENT_FIELD_REPORTS_CLEARED = "nics_SENT_FIELD_REPORTS_CLEARED";
	public static final String nics_SENT_RESOURCE_REQUESTS_CLEARED = "nics_SENT_RESOURCE_REQUESTS_CLEARED";
	public static final String nics_SENT_WEATHER_REPORTS_CLEARED = "nics_SENT_WEATHER_REPORTS_CLEARED";
	
	public static final String nics_VIEW_OVERVIEW = "nics_VIEW_OVERVIEW";
	public static final String nics_VIEW_SIMPLE_REPORTS_LIST = "nics_VIEW_SIMPLE_REPORTS_LIST";
	public static final String nics_VIEW_FIELD_REPORTS_LIST = "nics_VIEW_FIELD_REPORTS_LIST";
	public static final String nics_VIEW_DAMAGE_REPORTS_LIST = "nics_VIEW_DAMAGE_REPORTS_LIST";
	public static final String nics_VIEW_RESOURCE_REQUESTS_LIST = "nics_VIEW_RESOURCE_REQUESTS_LIST";
	public static final String nics_VIEW_WEATHER_REPORTS_LIST = "nics_VIEW_WEATHER_REPORTS_LIST";
	
	public static final String nics_MARKING_ALL_REPORTS_READ_FINISHED = "nics_MARKING_ALL_REPORTS_READ_FINISHED";
	
	public static final String nics_POLLING_TASK = "nics_POLLING_TASK";
	public static final String nics_POLLING_TASK_ASSIGNMENTS = "nics_POLLING_TASK_ASSIGNMENTS";
	public static final String nics_POLLING_TASK_DAMAGE_REPORT = "nics_POLLING_TASK_DAMAGE_REPORT";
	public static final String nics_POLLING_TASK_FIELD_REPORT = "nics_POLLING_TASK_FIELD_REPORT";
	public static final String nics_POLLING_TASK_SIMPLE_REPORT = "nics_POLLING_TASK_SIMPLE_REPORT";
	public static final String nics_POLLING_TASK_RESOURCE_REQUEST = "nics_POLLING_TASK_RESOURCE_REQUEST";
	public static final String nics_POLLING_TASK_WEATHER_REPORT = "nics_POLLING_TASK_WEATHER_REPORT";
	public static final String nics_POLLING_TASK_CHAT_MESSAGES = "nics_POLLING_TASK_CHAT_MESSAGES";
	public static final String nics_POLLING_MARKUP_REQUEST = "nics_POLLING_MARKUP_REQUEST";
	public static final String nics_POLLING_WFS_LAYER = "nics_POLLING_WFS_LAYER_";
	public static final String nics_POLLING_COLLABROOMS = "nics_POLLING_COLLABROOMS";
	public static final String nics_POLLING_INCIDENTS = "nics_POLLING_INCIDENTS";
	
	public static final String nics_SUCCESSFULLY_GET_COLLABROOMS = "nics_SUCCESSFULLY_GET_COLLABROOMS";
	public static final String nics_SUCCESSFULLY_GET_INCIDENTS = "nics_SUCCESSFULLY_GET_INCIDENTS";
	public static final String nics_FAILED_GET_COLLABROOMS = "nics_FAILED_GET_COLLABROOMS";
	public static final String nics_FAILED_GET_INCIDENTS = "nics_FAILED_GET_INCIDENTS";
	public static final String nics_FAILED_TO_POST_MARKUP = "nics_FAILED_TO_POST_MARKUP";
	
	public static final String nics_SHOW_INCIDENT_SELECT = "nics_SHOW_INCIDENT_SELECT";
	public static final String nics_SIMPLE_REPORT_PROGRESS = "nics_SIMPLE_REPORT_PROGRESS";
	public static final String nics_DAMAGE_REPORT_PROGRESS = "nics_DAMAGE_REPORT_PROGRESS";
	public static final String nics_WEATHER_REPORT_PROGRESS = "nics_WEATHER_REPORT_PROGRESS";
	public static final String nics_FIELD_REPORT_PROGRESS = "nics_FIELD_REPORT_PROGRESS";
	public static final String nics_RESOURCE_REQUEST_PROGRESS = "nics_RESOURCE_REQUEST_PROGRESS";
	public static final String nics_SUCCESSFUL_GET_USER_ORGANIZATION_INFO = "nics_SUCCESSFUL_GET_USER_ORGANIZATION_INFO";
	public static final String nics_OPENAM_AUTH_TIMEOUT = "nics_OPENAM_AUTH_TIMEOUT";
	
	public static final String nics_LOCAL_MAP_FEATURES_CLEARED = "nics_LOCAL_MAP_FEATURES_CLEARED";
	public static final String nics_LOCAL_CHAT_CLEARED = "nics_LOCAL_CHAT_CLEARED";
	public static final String nics_LOCAL_REPORTS_CLEARED = "nics_LOCAL_REPORTS_CLEARED";
}
