package com.cw.msumit.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.objects.People;
import com.cw.msumit.objects.Task;
import com.cw.msumit.utils.StaticFunctions;

public class AcknowledgeDeliveryIntentService extends IntentService{

	DatabaseHandler dbHandler;
	public AcknowledgeDeliveryIntentService() {
		super("AcknowledgeDeliveryIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		dbHandler = new DatabaseHandler(getApplicationContext());
		String taskId = intent.getStringExtra(Task.UNIVERSAL_ID);
		String contactId = intent.getStringExtra(People.PEOPLE_CONTACT_ID);
		int accepted = intent.getIntExtra(People.PEOPLE_HAS_ACCEPTED, 2);
		if (StaticFunctions.hasInternet(getApplicationContext())) {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://collegewires.com/wiresapp/acknowledge_task_accepted.php");
			httpPost.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair(People.PEOPLE_TASK_ID, taskId));
				nameValuePairs.add(new BasicNameValuePair(People.PEOPLE_CONTACT_ID, contactId));
				nameValuePairs.add(new BasicNameValuePair(People.PEOPLE_HAS_ACCEPTED, Integer.toString(accepted)));
				
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpClient.execute(httpPost);
				
				if(response.getStatusLine().getStatusCode()==200){ //everything OK. data has been sent to Internet
					// acknowledgment has been well received and sent back thus, update "accepted" value in shared prefs
					// according to accepted column value, we will do things in SyncBroadcastReceiver
					clearAcknowledgeSharedPref(taskId);
					
				} else {
					// 3 means POST has failed. we should re-attempt it in receiver
					
					//save into shared preferences
					saveAcknowledgeIntoSharedPref(taskId, accepted);
				}
			} catch (ClientProtocolException e) {
				Log.d("Acknowledge", "ClientProtocolException");
			} catch (IOException e) {
				Log.d("Acknowledge", "IOException");
			}
		} else {
			// no internet connection=> data sending failed
			// 3 means POST has failed. we should re-attempt it in receiver
			
			//save into shared preferences
			saveAcknowledgeIntoSharedPref(taskId, accepted);
		}
	}
	
	private void saveAcknowledgeIntoSharedPref(String taskId, int accepted) {
		SharedPreferences mPreferences = this.getSharedPreferences("com.cw.msumit.acknowledgment", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = mPreferences.edit();
		
		String key = taskId;
		editor.putInt(key, accepted);	
		editor.commit();
	}
	
	private void clearAcknowledgeSharedPref(String taskId) {
		SharedPreferences mPreferences = this.getSharedPreferences("com.cw.msumit.acknowledgment", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = mPreferences.edit();
		
		String key = taskId;
		editor.remove(key);
		editor.commit();
	}

}
