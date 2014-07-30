package com.cw.msumit.services;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.cw.msumit.objects.Task;
import com.cw.msumit.utils.StaticFunctions;

/**
 * Set an alarm for the date passed into the constructor When the alarm is
 * raised it will start the NotifyService This uses the android build in alarm
 * manager *NOTE* if the phone is turned off this alarm will be cancelled This
 * will run on it's own thread.
 */
public class AlarmTask implements Runnable {
	// The date selected for the alarm
	private final Calendar date;
	// The android system alarm manager
	private final AlarmManager am;
	// Your context to retrieve the alarm manager from
	private final Context context;
	private int requestCode;
	private Task task;
	private boolean NO_REPEAT, DAILY_REPEAT, MONTHLY_REPEAT, WEEKLY_REPEAT;
	public static final String DAILY_REPEAT_ACTION = "com.cw.msumit.DAILY_REPEAT";
	public static final String WEEKLY_REPEAT_ACTION = "com.cw.msumit.WEEKLY_REPEAT_";
	public static final String MONTHLY_REPEAT_ACTION = "com.cw.msumit.MONTHLY_REPEAT_";

	/**
	 * Creates a new AlarmTask Object
	 * 
	 * @param context
	 * @param date pass null while creating new AlarmTask object to remove an
	 *            alarm
	 * @param requestCode
	 */
	public AlarmTask(Context context, Calendar date, Task task) {
		this.context = context;
		this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		this.date = date;
		this.requestCode = Integer.parseInt(task.getReminderID());
		this.task = task;
	}

	@Override
	public void run() {
		String repeat = this.task.getRepeat();
		NO_REPEAT = StaticFunctions.noRepeatfromString(repeat);
		DAILY_REPEAT = StaticFunctions.dailyRepeatfromString(repeat);
		int monthlyRepeatDay = StaticFunctions.monthlyRepeatDayfromString(repeat);
		if(monthlyRepeatDay!=0)
			MONTHLY_REPEAT = true;
		
		int[] weeklyRDays = null;
		if(repeat.startsWith("W")) {
			WEEKLY_REPEAT = true;
			int[] weeklyDays = StaticFunctions.intArray(repeat.substring(2), "-");	
			weeklyRDays = new int[weeklyDays.length];
			for(int i=0; i<weeklyDays.length; i++) {
				if(weeklyDays[i]==0) {
					weeklyRDays[i]= Calendar.MONDAY;
				}
				if(weeklyDays[i]==1) {
					weeklyRDays[i]= Calendar.TUESDAY;
				}
				if(weeklyDays[i]==2) {
					weeklyRDays[i]= Calendar.WEDNESDAY;
				}
				if(weeklyDays[i]==3) {
					weeklyRDays[i]= Calendar.THURSDAY;
				}
				if(weeklyDays[i]==4) {
					weeklyRDays[i]= Calendar.FRIDAY;
				}
				if(weeklyDays[i]==5) {
					weeklyRDays[i]= Calendar.SATURDAY;
				}
				if(weeklyDays[i]==6) {
					weeklyRDays[i]= Calendar.SUNDAY;
				}
			}
		}
			
		// Request to start are service when the alarm date is upon us
		// We don't start an activity as we just want to pop up a notification
		// into the system bar not a full activity
		Intent intent = new Intent(context, NotifyService.class);
		intent.setAction("com.cw.msumit.REMIND");
		intent.putExtra(NotifyService.INTENT_NOTIFY, true);
		intent.putExtra(NotifyService.NOTIFICATION_ID, this.requestCode);
		intent.putExtra(NotifyService.TASK_TITLE, task.getTitle());
		
		Intent repeatIntent = new Intent(intent);
		
		PendingIntent pendingIntent = PendingIntent.getService(context, this.requestCode, intent, 0);
		PendingIntent repeatPendingIntent = null;
		
		if (this.date != null) {
			if(this.date.compareTo(Calendar.getInstance()) >=0 ) {
				am.set(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), pendingIntent);
			}
			
			if(DAILY_REPEAT) {
				repeatIntent.setAction(DAILY_REPEAT_ACTION);
				Calendar calendar = this.date;
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				repeatPendingIntent = PendingIntent.getService(context, this.requestCode, repeatIntent, 0);
				am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, repeatPendingIntent);
			}
			
			if(MONTHLY_REPEAT) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.DAY_OF_MONTH, monthlyRepeatDay);
				if(calendar.compareTo(Calendar.getInstance()) <=0) {
					calendar.add(Calendar.MONTH, 1);
				}
				
				repeatIntent.setAction(MONTHLY_REPEAT_ACTION + Integer.toString(monthlyRepeatDay));
				repeatPendingIntent = PendingIntent.getService(context, this.requestCode, repeatIntent, 0);
				am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatPendingIntent);
			}
			
			if(WEEKLY_REPEAT) {
				// remove any previous pending intents
				for(int i=0; i<7; i++) {
					repeatIntent.setAction(WEEKLY_REPEAT_ACTION + Integer.toString(i));
					repeatPendingIntent = PendingIntent.getService(context, this.requestCode, repeatIntent, 0);
					am.cancel(repeatPendingIntent);
				}
				for(int i=0; i<weeklyRDays.length; i++) {
					Calendar calendar = this.date;
					calendar.set(Calendar.DAY_OF_WEEK, weeklyRDays[i]);
					if(calendar.compareTo(Calendar.getInstance()) <=0) {
						calendar.add(Calendar.DAY_OF_WEEK, 7);
					}
					
					repeatIntent.setAction(WEEKLY_REPEAT_ACTION + Integer.toString(i));
					repeatPendingIntent = PendingIntent.getService(context, this.requestCode, repeatIntent, 0);
					am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 7*AlarmManager.INTERVAL_DAY, repeatPendingIntent);
				}
			}
			
		} else {
			am.cancel(pendingIntent);
			if(!NO_REPEAT) {
				am.cancel(repeatPendingIntent);
			}
		}
	}
}
