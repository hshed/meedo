package com.cw.msumit.services;

import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cw.msumit.R;
import com.cw.msumit.TaskReminderActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

/**
 * This service is started when an Alarm has been raised
 * We pop a notification into the status bar for the user to click on When the
 * user clicks the notification a new activity is opened
 */
public class NotifyService extends Service {
	public static final String NOTIFICATION_ID = "notificationId";
	public static final String TASK_TITLE = "taskTitle";
	/**
	 * Class for clients to access
	 */
	public class ServiceBinder extends Binder {
		NotifyService getService() {
			return NotifyService.this;
		}
	}
	// Name of an intent extra we can use to identify if this service was
	// started to create a notification
	public static final String INTENT_NOTIFY = "com.cw.msumit.service.INTENT_NOTIFY";
	// The system notification manager
	public int REMINDER_NOTIFICATION_ID;
	public String REMINDER_TASK_TITLE;

	@Override
	public void onCreate() {
		Log.i("NotifyService", "onCreate()");
		// mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		
		// If this service was started by out AlarmTask intent then we want to
		// show our notification
		if (intent.getBooleanExtra(INTENT_NOTIFY, false)){
			REMINDER_NOTIFICATION_ID = intent.getIntExtra(NOTIFICATION_ID, 2000);
			REMINDER_TASK_TITLE = intent.getStringExtra(TASK_TITLE);
			//this.task = (Task) intent.getSerializableExtra(TASK);
			//Log.d("tasker", this.task.toString());
			//this.date = (Calendar) intent.getSerializableExtra(DATE);
			//Log.d("tasker", this.date.toString());
			showNotification();
		} else { // location thing
			if (LocationClient.hasError(intent)) {
				// Get the error code with a static method
				int errorCode = LocationClient.getErrorCode(intent);
				// Log the error
				Log.e("ReceiveTransitionsIntentService",
						"Location Services error: " + Integer.toString(errorCode));
			} else {
				REMINDER_NOTIFICATION_ID = intent.getIntExtra(NOTIFICATION_ID, 2000);
				REMINDER_TASK_TITLE = intent.getStringExtra(TASK_TITLE);
				
				// Get the type of transition (entry or exit)
				int transitionType = LocationClient.getGeofenceTransition(intent);
				// Test that a valid transition was reported
				if ((transitionType == Geofence.GEOFENCE_TRANSITION_ENTER)
						|| (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT)) {
					List<Geofence> triggerList = LocationClient
							.getTriggeringGeofences(intent);

					String[] triggerIds = new String[triggerList.size()];

					for (int i = 0; i < triggerIds.length; i++) {
						triggerIds[i] = triggerList.get(i).getRequestId();
					}
					Log.e("ReceiveTransitionsIntentService", "Everything good");
					NotificationManager notificationManager = (NotificationManager) getApplicationContext()
							.getSystemService(Context.NOTIFICATION_SERVICE);
					PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
					Notification notification = createNotification(getApplicationContext(), pendingIntent);
					notificationManager.notify(REMINDER_NOTIFICATION_ID, notification);
					stopSelf();

				} else {
					Log.e("ReceiveTransitionsIntentService","Geofence transition error: "
									+ Integer.toString(transitionType));
				}
			}
		}

		// We don't care if this service is stopped as we have already delivered
		// our notification
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients
	private final IBinder mBinder = new ServiceBinder();

	/**
	 * Creates a notification and shows it in the OS drag-down status bar
	 */
	private void showNotification() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(getApplicationContext(), TaskReminderActivity.class);
		intent.putExtra(TASK_TITLE, REMINDER_TASK_TITLE);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
		Notification notification = createNotification(this, pendingIntent);
		notificationManager.notify(REMINDER_NOTIFICATION_ID, notification);
		// Stop the service when we are finished
		stopSelf();
	}

	private Notification createNotification(Context context,
			PendingIntent pendingIntent) {
		Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.meedolocation);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.bell)
				.setLargeIcon(largeIcon)
				.setContentTitle(REMINDER_TASK_TITLE)
				.setContentText("Your notification time is upon us!")
				.setContentIntent(pendingIntent)
				.setWhen(System.currentTimeMillis())
				.setDefaults(Notification.DEFAULT_ALL);
		Notification notification = builder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;

		return notification;
	}
	
}
