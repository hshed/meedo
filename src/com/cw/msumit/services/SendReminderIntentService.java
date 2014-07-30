package com.cw.msumit.services;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.fragments.ReceivedTaskFragment;
import com.cw.msumit.objects.People;
import com.cw.msumit.objects.SendReminder;
import com.cw.msumit.objects.Task;

public class SendReminderIntentService extends IntentService{

	Intent receivedIntent;
	Task task;
	ArrayList<People> peopleList;
	DatabaseHandler dbHandler;
	public SendReminderIntentService() {
		super("SendReminderIntent");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onHandleIntent(Intent intent) {
		dbHandler = new DatabaseHandler(getApplicationContext());
		task = (Task) intent.getSerializableExtra("tasks");
		peopleList = (ArrayList<People>) intent.getSerializableExtra("peoples");
		
		String taskJson = SendReminder.generateTaskJson(getApplicationContext(), task);		
		String peopleJson = SendReminder.generatePeopleJson(peopleList);
		
		if(task!=null){
			sendTaskJsonToWeb(taskJson);
		}
		sendPeopletoWeb(peopleJson);
		//send acknowledgment too for others to see, you are in it.
		ReceivedTaskFragment.acknowledgeDelivery(getApplicationContext(), task, 
				intent.getIntExtra("acknowledgment", Task.TASK_ACKNOWLEDGMENT_ACCEPT));
		
	}
	
	public void sendPeopletoWeb(String peopleJson){
		Context context = getApplicationContext();
		if (SyncAll.CheckInternet(context)) {
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost("http://collegewires.com/wiresapp/people_json.php");
			httpPost.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
			try {
				httpPost.setHeader("json",peopleJson.toString());
				httpPost.getParams().setParameter("jsonpost",peopleJson);
				HttpResponse response = httpClient.execute(httpPost);
				if(response.getStatusLine().getStatusCode()==200){// means successful in posting json to web
					Log.d("People Json", "People JSON to Web");
					//we should now update Synced column of people table to 1
					for(People people: peopleList){
						dbHandler.updateAPeopleColumn(people.getTaskId(), people.getFbId(), People.PEOPLE_SYNCED, 1);
					}
					
				} else {
					Log.d("People json", response.getStatusLine().toString()); // reason for failure
					// we should now update Synced column of people table to 0
					for(People people: peopleList){
						dbHandler.updateAPeopleColumn(people.getTaskId(), people.getFbId() , People.PEOPLE_SYNCED, 0);
					}
				}
			} catch (ClientProtocolException e) {
				Log.d("People Json", "NotClientProtocol");
			} catch (IOException e) {
				Log.d("People Json", "NotIOException");
			}
		} else {
			// Data sending failed
			// we should now update Synced column of people table to 0
			for(People people: peopleList){
				dbHandler.updateAPeopleColumn(people.getTaskId(), people.getFbId() , People.PEOPLE_SYNCED, 0);
			}
		}
	}
	/**
	 * sends task json to web 
	 * @param taskjson
	 */
	public void sendTaskJsonToWeb(String taskjson) {
		Context context = getApplicationContext();
		if (SyncAll.CheckInternet(context)) {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://collegewires.com/wiresapp/task_json.php");
			httpPost.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
			try {
				httpPost.setHeader("json",taskjson.toString());
				httpPost.getParams().setParameter("jsonpost",taskjson);
				HttpResponse response = httpClient.execute(httpPost);
				if(response.getStatusLine().getStatusCode()==200){// means successful in posting json to web
					Log.d("Task Json", "Task JSON to Web");
					//we should now update Synced column of task table to 1
					dbHandler.updateAColumn(task.getReminderID(), Task.SYNCED, 1);
				} else {
					Log.d("Task Json", response.getStatusLine().toString()); // reason for failure
					// we should now update Synced column of task table to 0
					dbHandler.updateAColumn(task.getReminderID(), Task.SYNCED, 0);
				}
			} catch (ClientProtocolException e) {
				Log.d("Task Json", "NotClientProtocol");
			} catch (IOException e) {
				Log.d("Task Json", "NotIOException");
			}
		} else {
			// Data sending failed
			// we should now update Synced column of task table to 0
			dbHandler.updateAColumn(task.getReminderID(), Task.SYNCED, 0);
		}
	}

}
