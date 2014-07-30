package com.cw.msumit.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cw.msumit.GCMIntentService;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.objects.Connection;
import com.cw.msumit.utils.StaticFunctions;

public class SendFreindJSONToWeb extends IntentService {

	public SendFreindJSONToWeb() {
		super("FreindJSONToWeb");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

		final String json = intent.getStringExtra("FriendsJSON");
		if (json == null) {
			Log.d("Friends", "No JSON");
		}

		else {
			Log.d("Friends", json);
			
			sendDatatoWeb(json);
			String friendJSON = pullDataFromWeb(GCMIntentService.getUsername(getApplicationContext()));
			addJSONToDB(friendJSON);
			UpdateFriendDb.addAutoContactToDB(getApplicationContext());
			updateDbOfFriends(GCMIntentService.getUsername(getApplicationContext()));
			sendRegId(getApplicationContext());
					
			
			// updates auto_table for autosuggestions

		}
	}

	public void sendDatatoWeb(String json) {
		if (SyncAll.CheckInternet(getApplicationContext())) {
			HttpClient httpClient = new DefaultHttpClient();
			
			final String PROXY_IP = "202.141.80.22";  
	        final int PROXY_PORT = 3128;  
	        CredentialsProvider credProvider = new BasicCredentialsProvider();
	        credProvider.setCredentials(new AuthScope(PROXY_IP, PROXY_PORT),
	            new UsernamePasswordCredentials("k.hrishikesh", "410326"));
			
			HttpPost httpPost = new HttpPost(
					"http://collegewires.com/wiresapp/friend_json.php");
			httpPost.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("username", GCMIntentService.getUsername(getApplicationContext())));
				nameValuePairs.add(new BasicNameValuePair("json", json));

				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				httpClient.execute(httpPost);
				Log.d("Posted", "JSON to Web");
				// syncing required
				SyncAll.CreateIntegerSharedPreference("SendFriendJSONToWeb", 1,
						getApplicationContext());

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				Log.d("Posted", "NotClientProtocol");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d("Posted", "NotIOException");
			}
		} else {
			// Data sending failed
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	public void sendRegId(Context context) {
		SharedPreferences mPreferences = context.getSharedPreferences("regid", Context.MODE_PRIVATE);
		String regId = mPreferences.getString("reg", "0");
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://collegewires.com/wiresapp/save_reg_id.php");
		httpPost.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("username", StaticFunctions.getUsername(context)));
			Log.d("sala", regId);
			nameValuePairs.add(new BasicNameValuePair("regID", regId));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			Log.d("Posted", "regID " +  httpResponse.getStatusLine());

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.d("Posted", "NotClientProtocol");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("Posted", "NotIOException");
		}
	}
	// pulls json from web
	public String pullDataFromWeb(String username) {
		String friend_json = "";
		// Create an intermediate to connect with the Internet
		HttpClient httpClient = new DefaultHttpClient();
		
		final String PROXY_IP = "202.141.80.22";  
        final int PROXY_PORT = 3128;  
        CredentialsProvider credProvider = new BasicCredentialsProvider();
        credProvider.setCredentials(new AuthScope(PROXY_IP, PROXY_PORT),
            new UsernamePasswordCredentials("k.hrishikesh", "410326"));
		// Sending a GET request to the web page that we want
		// Because of we are sending a GET request, we have to pass the values
		// through the URL
		HttpGet httpGet = new HttpGet(
				"http://www.collegewires.com/wiresapp/send_friend_json.php?username="
						+ username);
		httpGet.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);

		try {
			// execute(); executes a request using the default context.
			// Then we assign the execution result to HttpResponse
			HttpResponse httpResponse = httpClient.execute(httpGet);
			// syncing required
			SyncAll.CreateIntegerSharedPreference("GetWiresFriendJSONFromWeb",
					1, getApplicationContext());
			System.out.println("httpResponse");

			// getEntity() ; obtains the message entity of this response
			// getContent() ; creates a new InputStream object of the entity.
			// Now we need a readable source to read the byte stream that comes
			// as the httpResponse
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				friend_json = EntityUtils.toString(entity);

			}
			Log.d("GET RESPONSE", friend_json);
			return friend_json;

		} catch (ClientProtocolException cpe) {
			System.out.println("Exception generates caz of httpResponse :"
					+ cpe);
			cpe.printStackTrace();
		} catch (IOException ioe) {
			System.out
					.println("Second exception generates caz of httpResponse :"
							+ ioe);
			ioe.printStackTrace();
		}

		return null;
	}

	// called to update friends database of the user
	public void addJSONToDB(String json) {
		try {
			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				String email = array.getJSONObject(i).getString("email")
						.toString();
				String name = array.getJSONObject(i).getString("name")
						.toString();
				String userID = array.getJSONObject(i).getString("fbID")
						.toString();
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				String firstPreference = Integer.toString(preferences.getInt(
						"SendFriendJSONToWeb", 0));
				String secondPreference = Integer.toString(preferences.getInt(
						"GetWiresFriendJSONFromWeb", 0));
				String friend = preferences
						.getString("facebook_friend", "zero");
				Log.d("firstPreference", firstPreference);
				Log.d("secondPreference", secondPreference);
				Log.d("friendPreference", friend);

				Connection connection = new Connection(userID, name, email);

				DatabaseHandler dbHandler = new DatabaseHandler(
						getApplicationContext());
				if (!dbHandler.IDExists(userID)) {
					dbHandler.addConnectionFromFB(connection);

				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// called to update DB of friends
	public void updateDbOfFriends(String username) {
		HttpClient httpClient = new DefaultHttpClient();
		final String PROXY_IP = "202.141.80.22";  
        final int PROXY_PORT = 3128;  
        CredentialsProvider credProvider = new BasicCredentialsProvider();
        credProvider.setCredentials(new AuthScope(PROXY_IP, PROXY_PORT),
            new UsernamePasswordCredentials("k.hrishikesh", "410326"));
        
		HttpGet httpGet = new HttpGet(
				"http://www.collegewires.com/wiresapp/GCM_signed_up.php?username="
						+ username);
		httpGet.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		try {
			// execute(); executes a request using the default context.
			// Then we assign the execution result to HttpResponse
			HttpResponse httpResponse = httpClient.execute(httpGet);
			System.out.println("httpResponse");
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				String response = EntityUtils.toString(entity);
				Log.d("Friend's Database", response);

			}
		} catch (ClientProtocolException cpe) {
			System.out.println("Exception generates caz of httpResponse :"
					+ cpe);
			cpe.printStackTrace();
		} catch (IOException ioe) {
			System.out
					.println("Second exception generates caz of httpResponse :"
							+ ioe);
			ioe.printStackTrace();
		}
	}

}
