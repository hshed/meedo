package com.cw.msumit.services;

import java.util.ArrayList;
import java.util.Map;

import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.objects.Actions;
import com.cw.msumit.objects.People;
import com.cw.msumit.objects.SendReminder;
import com.cw.msumit.objects.Task;
import com.cw.msumit.utils.StaticFunctions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SyncBroadcastReceiver extends BroadcastReceiver{

	DatabaseHandler dbHandler;
	ArrayList<Task> tasksToSend;
	ArrayList<People> peoplesToSend;
	ArrayList<Actions> actionsToSend;
	SendReminder sendReminder;
	@Override
	public void onReceive(Context context, Intent intent) {
		dbHandler = new DatabaseHandler(context);
		sendReminder = new SendReminder();
		if(StaticFunctions.getConnectivityStatus(context)!=StaticFunctions.TYPE_NOT_CONNECTED) {
			// network change has been observed and network is open 
			
			// do all sending operations having synced column equal to 0
			tasksToSend = dbHandler.getTasksWithSyncValue(0);
			if(tasksToSend!=null) {// there are tasks in queue to send
				// get the people involved with these tasks
				for(Task task: tasksToSend) {
					peoplesToSend = dbHandler.getPeoplesWithSyncValue(0, task.getUniversalID());
					if(peoplesToSend!=null){
						sendReminder.send(context, task, peoplesToSend, Task.TASK_ACKNOWLEDGMENT_ACCEPT);
					}
				}
			} else {// there is no task in queue to send
				//there is a chance that people were not sent but tasks were sent
				//retrieve all people with synced = 0 and send it to web
				peoplesToSend = dbHandler.getPeoplesWithSyncValue(0);
				if(peoplesToSend!=null){
					sendReminder.send(context, null, peoplesToSend, Task.TASK_ACKNOWLEDGMENT_ACCEPT);
				}
			}
			
			// do all acknowledgment operations
			SharedPreferences mPreferences = context.getSharedPreferences("com.cw.msumit.acknowledgment", Context.MODE_PRIVATE);
			Map<String,?> keys = mPreferences.getAll();

			for(Map.Entry<String,?> entry : keys.entrySet()){
			    String taskId = entry.getKey();
			    int accepted = Integer.parseInt(entry.getValue().toString());
			    Intent acknowledgeIntent = new Intent(context, AcknowledgeDeliveryIntentService.class);
			    acknowledgeIntent.putExtra(Task.UNIVERSAL_ID, taskId);
				acknowledgeIntent.putExtra(People.PEOPLE_CONTACT_ID, StaticFunctions.getUserFbId(context));
				acknowledgeIntent.putExtra(People.PEOPLE_HAS_ACCEPTED, accepted);
			    context.startService(acknowledgeIntent);
			 }
			
			actionsToSend = dbHandler.getActionsWithSyncValue(0);
			if(actionsToSend!=null) {
				Intent sendActionIntent = new Intent(context, SendActionIntentService.class);
				sendActionIntent.putExtra("actions", actionsToSend);
				context.startService(sendActionIntent);
			}
			
		}
		
	}
}
