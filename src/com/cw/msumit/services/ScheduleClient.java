package com.cw.msumit.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.objects.Reminder;
import com.cw.msumit.objects.Task;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * This is our service client, it is the 'middle-man' between the service and
 * any activity that wants to connect to the service
 */
public class ScheduleClient implements Serializable{

	private static final long serialVersionUID = 5497389864340084420L;
	// The hook into our service
	private ScheduleService mBoundService;
	// The context to start the service in
	private Context mContext;
	// A flag if we are connected to the service or not
	private boolean mIsBound;
	private Reminder reminder;

	public ScheduleClient(Context context) {
		mContext = context;
	}

	/**
	 * Call this to connect your activity to your service
	 */
	public void doBindService() {
		// Establish a connection with our service
		mContext.bindService(new Intent(mContext, ScheduleService.class),
				mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}
	
	public void doBindService(Reminder reminder) {
		// Establish a connection with our service
		mContext.bindService(new Intent(mContext, ScheduleService.class),
				mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
		this.reminder = reminder;
	}

	/**
	 * When you attempt to connect to the service, this connection will be
	 * called with the result. If we have successfully connected we instantiate
	 * our service object so that we can call methods on it.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with our service has been
			// established,
			// giving us the service object we can use to interact with our
			// service.
			mBoundService = ((ScheduleService.ServiceBinder) service)
					.getService();
			if(reminder!=null) {
				DatabaseHandler dbHandler = new DatabaseHandler(mContext);
				final ArrayList<Task> tasks = dbHandler.getActiveTasks();
				reminder.remind(tasks);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
		}
	};

	/**
	 * Tell our service to set an alarm for the given date
	 * @param c a date to set the notification for
	 */
	public void setAlarmForNotification(Calendar c, Task task) {
		mBoundService.setAlarm(c,task);
	}

	/**
	 * When you have finished with the service call this method to stop it
	 * releasing your connection and resources
	 */
	public void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			mContext.unbindService(mConnection);
			mIsBound = false;
		}
	}

	public void removeAlarmForNotification(Task task) {
		// TODO Auto-generated method stub
		mBoundService.removeAlarm(task);
	}
}