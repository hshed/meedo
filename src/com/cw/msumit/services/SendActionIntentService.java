package com.cw.msumit.services;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.objects.Actions;
import com.cw.msumit.utils.StaticFunctions;

public class SendActionIntentService extends IntentService{

	private Actions action;
	private ArrayList<Actions> actions;
	private DatabaseHandler dbHandler;
	public SendActionIntentService() {
		super("SendActionIntent");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onHandleIntent(Intent intent) {
		action = (Actions) intent.getSerializableExtra("action");
		actions = (ArrayList<Actions>) intent.getSerializableExtra("actions");
		dbHandler = new DatabaseHandler(getApplicationContext());
		String actionJson ;
		if(actions!=null) {
			actionJson = Actions.generateActionJson(actions);
		} else {
			actionJson = Actions.generateActionJson(action);
		}
		
		if(StaticFunctions.hasInternet(getApplicationContext())) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://collegewires.com/wiresapp/send_action.php");
			httpPost.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
			
			try {
				httpPost.setHeader("json", actionJson);
				httpPost.getParams().setParameter("jsonpost",actionJson);
				HttpResponse response = httpClient.execute(httpPost);
				if(response.getStatusLine().getStatusCode()==200){// means successful in posting json to web
					Log.d("Action Json", "Action JSON to Web");
					//we should now update Synced column of taskActions table to 1
					if(action!=null) {
						dbHandler.updateActionColumn(action.getUniqueId(), Actions.ACTION_SYNCED, 1);
					} else {
						for(Actions action: actions) {
							dbHandler.updateActionColumn(action.getUniqueId(), Actions.ACTION_SYNCED, 1);
						}
					}
				} else {
					Log.d("Action Json", response.getStatusLine().toString()); // reason for failure
					// we should now update Synced column of taskActions table to 0
					if(action!=null) {
						dbHandler.updateActionColumn(action.getUniqueId(), Actions.ACTION_SYNCED, 0);
					} else {
						for(Actions action: actions) {
							dbHandler.updateActionColumn(action.getUniqueId(), Actions.ACTION_SYNCED, 0);
						}
					}
				}
			} catch (ClientProtocolException e) {
				Log.d("Action Json", "Client Protocol Exception");
			} catch (IOException e) {
				Log.d("Action Json", "IO Exception");
			}
		} else {
			Log.d("Action Json", "data sending failed"); 
			// we should now update Synced column of taskActions table to 0
			if(action!=null) {
				dbHandler.updateActionColumn(action.getUniqueId(), Actions.ACTION_SYNCED, 0);
			} else {
				for(Actions action: actions) {
					dbHandler.updateActionColumn(action.getUniqueId(), Actions.ACTION_SYNCED, 0);
				}
			}
		}
		
	}

}
