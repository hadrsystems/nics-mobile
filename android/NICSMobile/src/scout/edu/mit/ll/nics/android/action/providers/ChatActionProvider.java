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
package scout.edu.mit.ll.nics.android.action.providers;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.preference.PreferenceManager.OnActivityDestroyListener;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;

import com.google.gson.Gson;

import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.payload.ChatPayload;
import scout.edu.mit.ll.nics.android.utils.Constants.NavigationOptions;
import scout.edu.mit.ll.nics.android.utils.Intents;

public class ChatActionProvider extends ActionProvider implements OnMenuItemClickListener, OnActivityDestroyListener {
	
	private static MenuItem mMenuItem;
	
	private static IntentFilter mChatReceiverFilter;
	public static boolean chatReceiverRegistered;
	private static int mNewMessageCount = 0;
	private static Context mContext;

	public ChatActionProvider(Context context) {
		super(context);
		
		mContext = context;
		mChatReceiverFilter = new IntentFilter( Intents.nics_LAST_CHAT_RECEIVED);
		
		
		if(!chatReceiverRegistered) {
			context.registerReceiver(chatReceiver, mChatReceiverFilter);
			chatReceiverRegistered = true;
		}
	}

	@Override
	public boolean onMenuItemClick(final MenuItem item) {
		
		final MainActivity ref = (MainActivity) mContext;
		
		if(!ref.isEditReport()) {
			item.setChecked(!item.isChecked());
			
			if(ref.isViewReport()) {
            	ref.mViewFieldReport = false;
            	ref.mViewResourceRequest = false;
            	ref.mViewSimpleReport = false; 
			}
			
			mNewMessageCount = 0;
			mMenuItem.setIcon(mContext.getResources().getDrawable(R.drawable.chat_no_notification));
			ref.onNavigationItemSelected(NavigationOptions.CHATLOG.getValue(), -1);
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			String title = mContext.getString(R.string.confirm_continue_to_title, mContext.getString(R.string.chat_log));
			String message = mContext.getString(R.string.confirm_continue_to_description);
			boolean prompt = false;
			
			if(ref.mEditDamageReport) {
				prompt = true;
				message = String.format(message, mContext.getString(R.string.DAMAGESURVEY));
			} else if(ref.mEditFieldReport) {
				prompt = true;
				message = String.format(message, mContext.getString(R.string.FIELDREPORT));
			} else if(ref.mEditSimpleReport) {
				prompt = true;
				message = String.format(message, mContext.getString(R.string.GENERALMESSAGE));
			} else if(ref.mEditResourceRequest) {
				prompt = true;
				message = String.format(message, mContext.getString(R.string.RESOURCEREQUEST));
			}
			
			message += mContext.getString(R.string.confirm_form_cancel);	
			
			if(prompt) {
				builder.setTitle(title);
				builder.setMessage(message);
				
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		            	
		            	ref.mEditFieldReport = false;
		            	ref.mEditResourceRequest = false;
		            	ref.mEditSimpleReport = false;
		            	ref.mEditDamageReport = false;
		            	
		    			item.setChecked(!item.isChecked());
		    			
		    			mNewMessageCount = 0;
		    			mMenuItem.setIcon(mContext.getResources().getDrawable(R.drawable.chat_no_notification));
		    			ref.getSupportActionBar().setSelectedNavigationItem(NavigationOptions.CHATLOG.getValue());
		            }
		        });
				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		            	dialog.dismiss();
		            }
		        });
				
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		}
			
		
		return false;
	}

	@Override
	public View onCreateActionView(MenuItem item) {
		item.setOnMenuItemClickListener(this);
		mMenuItem = item;
		
		return super.onCreateActionView(item);
	}

	@Override
	@Deprecated
	public View onCreateActionView() {
		return null;
	}
	
	public int getNewMessageCount() {
		return mNewMessageCount;
	}

	public void setNewMessageCount(int mNewMessageCount) {
		ChatActionProvider.mNewMessageCount = mNewMessageCount;
	}

	private static BroadcastReceiver chatReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				mNewMessageCount = intent.getIntExtra("newMessageCount", 0);
	
				String text = "";
				if(mNewMessageCount < 99) {
					text = String.valueOf(mNewMessageCount);
				} else {
					text = "*";
				}
				
				Bitmap bitmap = null;
				Options opts = new BitmapFactory.Options();

				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					opts.inMutable = true;
					bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.chat_notification, opts);
				} else {
					bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.chat_notification, opts).copy(Config.ARGB_8888, true);
				}
				
				Canvas notificationCanvas = new Canvas(bitmap);
			
				Paint paint = new Paint();
				paint.setColor(Color.WHITE);
				paint.setTextAlign(Paint.Align.CENTER);
				paint.setAntiAlias(true);
				paint.setTypeface(Typeface.DEFAULT_BOLD);
				
				if(!isTablet(mContext)) {
					paint.setTextSize(20);
					notificationCanvas.drawText(text, 48, 56, paint);
				} else {
					paint.setTextSize(12);
					notificationCanvas.drawText(text, 24, 28, paint);
				}
	
				mMenuItem.setIcon(new BitmapDrawable(mContext.getResources(), bitmap));
				
				sendNotification(intent.getStringExtra("payload"));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	public static boolean isTablet(Context context) {
	    return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
    
	private static void sendNotification(String string) {
		ChatPayload payload = new Gson().fromJson(string, ChatPayload.class);
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(mContext)
		        .setSmallIcon(R.drawable.chat_no_notification)
		        .setContentTitle(payload.getNickname())
		        .setContentText(payload.getmessage());
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(mContext, MainActivity.class);
		resultIntent.putExtra("selected_navigation_item", NavigationOptions.CHATLOG.getValue());

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(100, mBuilder.build());
	}

	@Override
	public void onActivityDestroy() {
		if(chatReceiverRegistered) {
			mContext.unregisterReceiver(chatReceiver);
			chatReceiverRegistered = false;
		}
	}
	
	public static BroadcastReceiver getChatReceiver() {
		return chatReceiver;
	}

	public static IntentFilter getIntentFilter() {
		return mChatReceiverFilter;
	}
	
	
	
}
