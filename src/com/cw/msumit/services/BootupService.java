package com.cw.msumit.services;

import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;

import com.cw.msumit.objects.Reminder;
import com.cw.msumit.objects.Task;

public class BootupService extends IntentService {

	ArrayList<Task> tasks;

	public BootupService() {
		super("BootupService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		ScheduleClient scheduleClient = new ScheduleClient(
				getApplicationContext());
		
		final Reminder reminder = new Reminder(scheduleClient);
		scheduleClient.doBindService(reminder);

	}

}
