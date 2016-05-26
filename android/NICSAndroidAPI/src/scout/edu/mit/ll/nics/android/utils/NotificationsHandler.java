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

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.support.v4.app.TaskStackBuilder;
import scout.edu.mit.ll.nics.android.api.R;
import scout.edu.mit.ll.nics.android.api.data.DamageReportData;
import scout.edu.mit.ll.nics.android.api.data.FieldReportData;
import scout.edu.mit.ll.nics.android.api.data.ResourceRequestData;
import scout.edu.mit.ll.nics.android.api.data.SimpleReportData;
import scout.edu.mit.ll.nics.android.api.data.WeatherReportData;
import scout.edu.mit.ll.nics.android.api.payload.AssignmentPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.DamageReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.FieldReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.ResourceRequestPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.WeatherReportPayload;
import scout.edu.mit.ll.nics.android.utils.Constants.NavigationOptions;

public class NotificationsHandler {
	private static NotificationsHandler mNotificationsHandler;
	
	private static Context mContext;
	private Builder mBuilder;
	private NotificationManager mNotificationManager;

	private int numberOfNewSimpleReports = 0;
	private int numberOfNewFieldReports = 0;
	private int numberOfNewDamageReports = 0;
	private int numberOfNewResourceRequests = 0;
	private int numberOfNewWeatherReports = 0;
	
	private InboxStyle mSimpleReportInboxStyle;
	private InboxStyle mFieldReportInboxStyle;
	private InboxStyle mDamageReportInboxStyle;
	private InboxStyle mResourceRequestInboxStyle;
	private InboxStyle mWeatherReportInboxStyle;

	private static final int SIMPLE_REPORT_NOTIFICATION_ID = 0;
	private static final int FIELD_REPORT_NOTIFICATION_ID = 1;
	private static final int RESOURCE_REQUEST_NOTIFICATION_ID = 2;
	private static final int ASSIGNMENT_CHANGE_NOTIFICATION_ID = 3;
	private static final int DAMAGE_REPORT_NOTIFICATION_ID = 4;
	private static final int WEATHER_REPORT_NOTIFICATION_ID = 5;

	private NotificationsHandler() {
		
		if(mSimpleReportInboxStyle == null) {
			mSimpleReportInboxStyle = new InboxStyle();
			mSimpleReportInboxStyle.setBigContentTitle("Report Details: ");
		}
		
		if(mFieldReportInboxStyle == null) {
			mFieldReportInboxStyle = new InboxStyle();
			mFieldReportInboxStyle.setBigContentTitle("Report Details: ");
		}
		
		if(mDamageReportInboxStyle == null) {
			mDamageReportInboxStyle = new InboxStyle();
			mDamageReportInboxStyle.setBigContentTitle("Report Details: ");
		}
		
		if(mResourceRequestInboxStyle == null) {
			mResourceRequestInboxStyle = new InboxStyle();
			mResourceRequestInboxStyle.setBigContentTitle("Report Details: ");
		}
		
		if(mWeatherReportInboxStyle == null) {
			mWeatherReportInboxStyle = new InboxStyle();
			mWeatherReportInboxStyle.setBigContentTitle("Report Details: ");
		}
				
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(mContext);
		mBuilder.setSmallIcon(R.drawable.ic_launcher);
		mBuilder.setAutoCancel(true);
	}
	
	public void createSimpleReportNotification(ArrayList<SimpleReportPayload> payloads, long activeIncidentId) {
		mBuilder.setContentTitle("NICS");
		mBuilder.setContentText("General Message(s) Receieved");
		
		for(SimpleReportPayload payload : payloads) {
			if(payload.getIncidentId() == activeIncidentId) {
				SimpleReportData data = payload.getMessageData();
				mSimpleReportInboxStyle.addLine(data.getUser() + " - " + data.getDescription());
				numberOfNewSimpleReports++;
			}
		}
		
		mBuilder.setNumber(numberOfNewSimpleReports);
		mBuilder.setStyle(mSimpleReportInboxStyle);
		
		mBuilder.setAutoCancel(true);
		
		mBuilder.setDefaults(Notification.DEFAULT_ALL);
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		
		Intent activityIntent = new Intent(Intents.nics_VIEW_SIMPLE_REPORTS_LIST);
		activityIntent.setClassName("scout.edu.mit.ll.nics.android", "scout.edu.mit.ll.nics.android.MainActivity");
		
		if(payloads.size() == 1) {
			activityIntent.putExtra("sr_edit_json", payloads.get(0).toJsonString());
		}
		activityIntent.putExtra("selected_navigation_item", NavigationOptions.GENERALMESSAGE.getValue());
		stackBuilder.addNextIntent(activityIntent);
		
		mBuilder.setContentIntent(stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));
		
		mNotificationManager.notify(SIMPLE_REPORT_NOTIFICATION_ID, mBuilder.build());
		
	}
	
	public void createFieldReportNotification(ArrayList<FieldReportPayload> payloads, long activeIncidentId) {
		mBuilder.setContentTitle("NICS");
		mBuilder.setContentText("Field Report(s) Receieved");

		for(FieldReportPayload payload : payloads) {
			if(payload.getIncidentId() == activeIncidentId) {
				FieldReportData data = payload.getMessageData();
				mFieldReportInboxStyle.addLine(data.getUser() + " - " + data.getIncidentName());
				numberOfNewFieldReports++;
			}
		}
		
		mBuilder.setNumber(numberOfNewFieldReports);
		mBuilder.setStyle(mFieldReportInboxStyle);
		
		mBuilder.setAutoCancel(true);
		
		mBuilder.setDefaults(Notification.DEFAULT_ALL);
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		
		Intent activityIntent = new Intent(Intents.nics_VIEW_FIELD_REPORTS_LIST);
		activityIntent.setClassName("scout.edu.mit.ll.nics.android", "scout.edu.mit.ll.nics.android.MainActivity");
		activityIntent.putExtra("selected_navigation_item", NavigationOptions.FIELDREPORT.getValue());
		if(payloads.size() == 1) {
			activityIntent.putExtra("fr_edit_json", payloads.get(0).toJsonString());
		}
		stackBuilder.addNextIntent(activityIntent);
		
		mBuilder.setContentIntent(stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));
		
		mNotificationManager.notify(FIELD_REPORT_NOTIFICATION_ID, mBuilder.build());
	}
	
	public void createDamageReportNotification(ArrayList<DamageReportPayload> payloads, long activeIncidentId) {
		mBuilder.setContentTitle("NICS");
		mBuilder.setContentText("Damage Report(s) Receieved");

		for(DamageReportPayload payload : payloads) {
			if(payload.getIncidentId() == activeIncidentId) {
				DamageReportData data = payload.getMessageData();
				mDamageReportInboxStyle.addLine(data.getUser() + " - " + data.getPropertyAddress());
				numberOfNewDamageReports++;
			}
		}
		
		mBuilder.setNumber(numberOfNewDamageReports);
		mBuilder.setStyle(mDamageReportInboxStyle);
		
		mBuilder.setAutoCancel(true);
		
		mBuilder.setDefaults(Notification.DEFAULT_ALL);
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		
		Intent activityIntent = new Intent(Intents.nics_VIEW_DAMAGE_REPORTS_LIST);
		activityIntent.setClassName("scout.edu.mit.ll.nics.android", "scout.edu.mit.ll.nics.android.MainActivity");
		activityIntent.putExtra("selected_navigation_item", NavigationOptions.DAMAGESURVEY.getValue());
		if(payloads.size() == 1) {
			activityIntent.putExtra("dr_edit_json", payloads.get(0).toJsonString());
		}
		stackBuilder.addNextIntent(activityIntent);
		
		mBuilder.setContentIntent(stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));
		
		mNotificationManager.notify(DAMAGE_REPORT_NOTIFICATION_ID, mBuilder.build());
	}
	
	public void createResourceRequestNotification(ArrayList<ResourceRequestPayload> payloads, long activeIncidentId) {
		mBuilder.setContentTitle("NICS");
		mBuilder.setContentText("Resource Request(s) Receieved");

		for(ResourceRequestPayload payload : payloads) {
			if(payload.getIncidentId() == activeIncidentId) {
				ResourceRequestData data = payload.getMessageData();
				mResourceRequestInboxStyle.addLine(data.getUser() + " - " + data.getDescription());
				numberOfNewResourceRequests++;
			}
		}
		
		mBuilder.setNumber(numberOfNewResourceRequests);
		mBuilder.setStyle(mResourceRequestInboxStyle);
		
		mBuilder.setAutoCancel(true);
		
		mBuilder.setDefaults(Notification.DEFAULT_ALL);
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		
		Intent activityIntent = new Intent(Intents.nics_VIEW_RESOURCE_REQUESTS_LIST);
		activityIntent.setClassName("scout.edu.mit.ll.nics.android", "scout.edu.mit.ll.nics.android.MainActivity");
		activityIntent.putExtra("selected_navigation_item", NavigationOptions.RESOURCEREQUEST.getValue());
		if(payloads.size() == 1) {
			activityIntent.putExtra("resreq_edit_json", payloads.get(0).toJsonString());
		}
		stackBuilder.addNextIntent(activityIntent);
		
		mBuilder.setContentIntent(stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));
		
		mNotificationManager.notify(RESOURCE_REQUEST_NOTIFICATION_ID, mBuilder.build());
	}

	public void createAssignmentChangeNotification(AssignmentPayload payload) {
		mBuilder.setContentTitle("NICS Assignment Change");
		
		String name = payload.getPhiUnit().getCollabroomName();
		if(name != null && !name.equals("")) {
			mBuilder.setContentText("Assigned to " + name);
		}
		mBuilder.setAutoCancel(true);
		mBuilder.setDefaults(Notification.DEFAULT_ALL);
		mBuilder.setNumber(1);
		mBuilder.setStyle(null);
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		
		Intent activityIntent = new Intent(Intents.nics_VIEW_OVERVIEW);
		activityIntent.setClassName("scout.edu.mit.ll.nics.android", "scout.edu.mit.ll.nics.android.MainActivity");
		activityIntent.putExtra("selected_navigation_item", NavigationOptions.OVERVIEW.getValue());
		stackBuilder.addNextIntent(activityIntent);
		
		mBuilder.setContentIntent(stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));
		
		mNotificationManager.notify(ASSIGNMENT_CHANGE_NOTIFICATION_ID, mBuilder.build());
	}
	
	public void createWeatherReportNotification(ArrayList<WeatherReportPayload> payloads, long activeIncidentId) {
		mBuilder.setContentTitle("NICS");
		mBuilder.setContentText("Weather Report(s) Receieved");

		for(WeatherReportPayload payload : payloads) {
			if(payload.getIncidentId() == activeIncidentId) {
				WeatherReportData data = payload.getMessageData();
				mWeatherReportInboxStyle.addLine(data.getUser());
				numberOfNewWeatherReports++;
			}
		}
		
		mBuilder.setNumber(numberOfNewWeatherReports);
		mBuilder.setStyle(mWeatherReportInboxStyle);
		
		mBuilder.setAutoCancel(true);
		
		mBuilder.setDefaults(Notification.DEFAULT_ALL);
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		
		Intent activityIntent = new Intent(Intents.nics_VIEW_WEATHER_REPORTS_LIST);
		activityIntent.setClassName("scout.edu.mit.ll.nics.android", "scout.edu.mit.ll.nics.android.MainActivity");
		activityIntent.putExtra("selected_navigation_item", NavigationOptions.WEATHERREPORT.getValue());
		if(payloads.size() == 1) {
			activityIntent.putExtra("wr_edit_json", payloads.get(0).toJsonString());
		}
		stackBuilder.addNextIntent(activityIntent);
		
		mBuilder.setContentIntent(stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));
		
		mNotificationManager.notify(WEATHER_REPORT_NOTIFICATION_ID, mBuilder.build());
	}
	
	public static NotificationsHandler getInstance(Context context) {
		if(mNotificationsHandler == null) {
			mContext = context;
			mNotificationsHandler = new NotificationsHandler();
		}
		
		return mNotificationsHandler;
	}
	
	public void cancelAllNotifications() {
		mNotificationManager.cancel(ASSIGNMENT_CHANGE_NOTIFICATION_ID);
		mNotificationManager.cancel(DAMAGE_REPORT_NOTIFICATION_ID);
		mNotificationManager.cancel(FIELD_REPORT_NOTIFICATION_ID);
		mNotificationManager.cancel(RESOURCE_REQUEST_NOTIFICATION_ID);
		mNotificationManager.cancel(SIMPLE_REPORT_NOTIFICATION_ID);
		mNotificationManager.cancel(WEATHER_REPORT_NOTIFICATION_ID);
	}

}
