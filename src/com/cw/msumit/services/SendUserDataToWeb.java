package com.cw.msumit.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class SendUserDataToWeb extends IntentService{

	public SendUserDataToWeb() {
		super("SendUserDataToWeb");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String username = intent.getStringExtra("username");
		String name = intent.getStringExtra("name");
		String email = intent.getStringExtra("email");
		String fbID = intent.getStringExtra("fbID");
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://collegewires.com/wiresapp/save.php");
		httpPost.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("fbID", fbID));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httpClient.execute(httpPost);
			Log.d("Posted", "To Websss");

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.d("Posted", "NotClientProtocol");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("Posted", "NotIOException");
		}
		
	}

}
