package com.cw.msumit;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.databases.DatabaseMethods;
import com.cw.msumit.services.SendFreindJSONToWeb;
import com.cw.msumit.services.SendUserDataToWeb;
import com.cw.msumit.services.UpdateFriendDb;
import com.cw.msumit.services.Utils;
import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gcm.GCMRegistrar;

public class SignupActivity extends SherlockActivity {

	private UiLifecycleHelper uiHelper;
	SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getSupportActionBar().hide();
		//final TextView skip = (TextView) findViewById(R.id.Skip);
		//Typeface mFont = Typeface.createFromAsset(getAssets(),
			//	"fonts/Roboto-Regular.ttf");
		/*skip.setTypeface(mFont);
		skip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SignupActivity.this, ListOfReminders.class);
				startActivity(intent);
				finish();
			}
		});*/
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(SignupActivity.this);
		boolean loggedin = mPrefs.getBoolean("loggedin", false);
		if(!loggedin) {
			addReminders(); // remove later in production
			final LoginButton fbsignupButton = (LoginButton) findViewById(R.id.signupButton);
			fbsignupButton.setReadPermissions(Arrays.asList("email"));
			uiHelper = new UiLifecycleHelper(SignupActivity.this, callback);
			uiHelper.onCreate(savedInstanceState);			
		} else {
			getFriendsList();
			Intent intent = new Intent(SignupActivity.this,ListOfReminders.class);
			startActivity(intent);
			finish();
		}
		
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception, boolean resume) {
		if (state.isOpened()) {
			Log.i(getClass().toString(), "Logged in...");
			
			SharedPreferences.Editor editor = mPrefs.edit();
			editor.putBoolean("loggedin", true);
			editor.commit();
			if(resume) {
				Intent intent = new Intent(SignupActivity.this,ListOfReminders.class);
				startActivity(intent);
				finish();
			}

		} else if (state.isClosed()) {
			Log.i(getClass().toString(), "Logged out...");
			SharedPreferences.Editor editor = mPrefs.edit();
			editor.putBoolean("loggedin", false);
			editor.commit();
		}
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception, false);
			getProfileInformation(session);
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null, true);
		}
		if(uiHelper != null) {
			uiHelper.onResume();
		}
		addContactToDb();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(uiHelper != null)
			uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		if(uiHelper != null)
			uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(uiHelper != null)
			uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(uiHelper != null)
			uiHelper.onSaveInstanceState(outState);
	}
	
	private void getProfileInformation(final Session activeSession) {
		Log.d("calledled", "called");
		Request request = Request.newMeRequest(activeSession, new Request.GraphUserCallback() {
		        @Override
		        public void onCompleted(GraphUser user, Response response) {
		            if(user != null && activeSession == Session.getActiveSession()){
		            	String json = response.getGraphObject().getInnerJSONObject().toString();
		            	Log.d("jsonsjo", json);
		            	
						try {
							JSONObject profile = response.getGraphObject().getInnerJSONObject();
							// getting name of the user
							final String name = profile.getString("name");
							// getting email of the user
							final String email = profile.getString("email");
							final String username = profile.getString("username");
							final String fbID = profile.getString("id");

							// save in sharedpreferences
							SharedPreferences.Editor editor = mPrefs.edit();
							editor.putString("username", username);
							editor.putString("email", email);
							editor.putString("name", name);
							editor.putString("fbID", fbID);
							editor.putBoolean("savedprofile", true);
							editor.commit();
							Log.d("Profile Data", "Saved Internally");
							//save the data in the table
							DatabaseHandler dbHandler=new DatabaseHandler(SignupActivity.this);
							if (!dbHandler.checkIfValuesExists(name, email ,4)) {
								dbHandler.addAutoContact(name, email, 4);
							}
							// start the service that will save userinfo in online
							// database
							//sendDataToWeb(username, name, email, fbID);
							Intent sendUserDataIntent = new Intent(SignupActivity.this, SendUserDataToWeb.class);
							sendUserDataIntent.putExtra("username", username);
							sendUserDataIntent.putExtra("email", email);
							sendUserDataIntent.putExtra("name", name);
							sendUserDataIntent.putExtra("fbID", fbID);
							startService(sendUserDataIntent);
							//new AsyncSendDataToWeb().execute(new String[]{username, name, email, fbID});
							getFriendsList();

							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(SignupActivity.this, "Name: " + name + "\nEmail: " + email
													+ "\nUsername: " + username, Toast.LENGTH_LONG).show();
									//registerDevice(SignupActivity.this);
								}

							});
							registerDevice(getApplicationContext());

						} catch (JSONException e) {
							e.printStackTrace();
						}

		            }
		            if(response.getError() !=null){

		            }
		        }
		    });
		    request.executeAsync();
		    
	}
	
	private void getFriendsList() {
		Session activeSession = Session.getActiveSession();
		if(activeSession.getState().isOpened()){
	        Request friendRequest = Request.newMyFriendsRequest(activeSession, new GraphUserListCallback() {
				
				@Override
				public void onCompleted(List<GraphUser> users, Response response) {
					// TODO Auto-generated method stub
					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SignupActivity.this);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					if(response!=null){
						Log.i("INFO", response.toString());
						try {
							editor.putString("facebook_friend", response.getGraphObject().getInnerJSONObject().toString());
							editor.commit();
							// send the response to the web through a service
							StartSendFreindJSONService(response.getGraphObject().getInnerJSONObject().toString());
						} catch (NullPointerException e) {
							e.printStackTrace();
						}
						
					}
					// get the response back from web
				}
			});
	        Bundle params = new Bundle();
	        params.putString("fields", "id, name");
	        friendRequest.setParameters(params);
	        friendRequest.executeAsync();
	    }
	}
	
	public static void registerDevice(Context context) {
		GCMRegistrar.checkDevice(context);
		GCMRegistrar.checkManifest(context);
		final String regId = GCMRegistrar.getRegistrationId(context);
		if (regId.equals("")) {
			GCMRegistrar.register(context, Utils.GCMSenderId);
			Log.v("Registered", "Registering");
		} else {
			
			Log.v("Registered", "Already registered");
		}
	}
	
	public void StartSendFreindJSONService(String json) {
		Intent intent = new Intent(SignupActivity.this, SendFreindJSONToWeb.class);
		intent.putExtra("FriendsJSON", json);
		startService(intent);

	}

	protected void addContactToDb() {
		Intent intent = new Intent(SignupActivity.this,
				UpdateFriendDb.class);
		startService(intent);
		Log.d("service_add", "Started");
	}
	
	public void addReminders() {
		DatabaseMethods.InsertRecords(10, SignupActivity.this);
	}
	/*private class AsyncSendDataToWeb extends AsyncTask<String[], Void, Void> {

		@Override
		protected Void doInBackground(String[]... params) {
			sendDataToWeb(params[0][0], params[0][1], params[0][2], params[0][3]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
		
	}*/
}
