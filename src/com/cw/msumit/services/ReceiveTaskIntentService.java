package com.cw.msumit.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.cw.msumit.utils.StaticFunctions;

public class ReceiveTaskIntentService extends IntentService{

	public ReceiveTaskIntentService() {
		super("ReceiveTaskIntent");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (StaticFunctions.hasInternet(getApplicationContext())) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://collegewires.com/wiresapp/send_tasks_to_phone.php");
			httpPost.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
			try {
							
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				//nameValuePairs.add(new BasicNameValuePair("username", StaticFunctions.getUsername(getApplicationContext())));
				//Log.d("Receive tasksuser", StaticFunctions.getUsername(getApplicationContext()));
				nameValuePairs.add(new BasicNameValuePair("contact_id", StaticFunctions.getUserFbId(getApplicationContext())));
				Log.d("Receive tasksuserid", StaticFunctions.getUserFbId(getApplicationContext()));

				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpClient.execute(httpPost);
				Log.d("Receive tasks", response.getStatusLine().toString());					

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				Log.d("Receive tasks", "ClientProtocol: " + e.getStackTrace());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d("Receive tasks", "IOException:"+ e.getStackTrace());
			}
		} else {
			// Data sending failed
			
		}
		
	}

}
