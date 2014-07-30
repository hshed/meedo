package com.cw.msumit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.fragments.ReceivedTaskFragment;
import com.cw.msumit.objects.Actions;
import com.cw.msumit.objects.Connection;
import com.cw.msumit.objects.People;
import com.cw.msumit.objects.Task;
import com.cw.msumit.services.ReceiveTaskClient;
import com.cw.msumit.services.UpdateFriendDb;
import com.cw.msumit.services.Utils;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	String TASK_COUNT = "";
	/**
	 * RECEIVE_TASK_INTENT
	 */
	public static final String RECEIVE_TASK_INTENT = "RECEIVE_TASK_INTENT";
	/**
	 * RECEIVE_PEOPLE_INTENT
	 */
	public static final String RECEIVE_PEOPLE_INTENT = "RECEIVE_PEOPLE_INTENT";
	DatabaseHandler dbHandler;
	
	public GCMIntentService() {
		super(Utils.GCMSenderId);
	}

	@Override
	protected void onError(Context context, String regId) {
		// TODO Auto-generated method stub
		Log.e("", "error registration id : " + regId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d("handleMessage", "Called1");
		dbHandler = new DatabaseHandler(context);
		handleMessage(context, intent);

	}

	@Override
	protected void onRegistered(final Context context, final String regId) {
		// TODO Auto-generated method stub
		 Log.e("registration", "registration id : "+regId);
		 handleRegistration(context, regId);	
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		// TODO Auto-generated method stub

	}

	private void handleMessage(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
		wl.acquire();
		wl.release();
		Log.d("handleMessage", "Called");
		// coming from
		String where = intent.getStringExtra("where");
		// Strings to store values of where
		String signed_up = "signed_up";
		// If message came if someone signed up
		if (where.equals(signed_up)) {
			Log.d("Friend", "Adding to DB");
			String email = intent.getStringExtra("email");
			String name = intent.getStringExtra("name");
			String fbID = intent.getStringExtra("fbID");
			Connection connection = new Connection(fbID, name, email);
			DatabaseHandler handler = new DatabaseHandler(context);
			if (!handler.IDExists(fbID)) {
				handler.addConnectionFromFB(connection);
				Log.d("Friend", "Added in DB");
				UpdateFriendDb.addAutoContactToDB(getApplicationContext());
			}
		} 
		//dbHandler.deleteAll();
		boolean showNoti = false;
		if(where.equals("task")) {
			Log.d("Received", "Yay!");
			String taskJson = intent.getStringExtra("tasks");
			Log.d("Received", "Yay! " + taskJson);
			String peopleJson = intent.getStringExtra("people");
			Log.d("Received", "Yay! Yay! people " + peopleJson);
			ArrayList<People> peoples = null;
			ArrayList<Task> tasks = null;
			if(taskJson!=null){
				tasks = ReceiveTaskClient.generateTaskObjectList(taskJson);
				if(peopleJson!=null) {
					peoples = ReceiveTaskClient.generatePeopleObjectList(peopleJson);
				}
				@SuppressWarnings("unchecked")
				ArrayList<People> tempPeoples = (ArrayList<People>) peoples.clone();
				@SuppressWarnings("unchecked")
				ArrayList<Task> tempTasks = (ArrayList<Task>) tasks.clone();
				for(Task task: tempTasks) {
					if(dbHandler.checkifTaskExists(task.getUniversalID())){
						Log.d("Task Exists?", "Yes");
						// remove this task from list
						tasks.remove(task);
						ReceivedTaskFragment.acknowledgeDelivery(context, task, Task.TASK_ACKNOWLEDGMENT_ACCEPT);
						//add people who don't exist
						if(peoples!=null) {
							for(People people: tempPeoples) {
								if(!dbHandler.checkIfPeopleExist(task.getUniversalID(), people.getFbId())) {
									dbHandler.addPeopleToDb(people);
								}
								//remove these people from list to increase efficiency
								if(people.getTaskId().equals(task.getUniversalID())){
									peoples.remove(people);
								}
							}
						}
						
					} else {
						Log.d("Task Exists?", "No");
						String thistaskjsonString = "";
						try {
							JSONObject jsonString = new JSONObject(taskJson).getJSONObject(task.getUniversalID());
							thistaskjsonString = new JSONObject().put(task.getUniversalID(), jsonString).toString();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						ReceivedTaskFragment.acknowledgeDelivery(context, task, Task.TASK_ACKNOWLEDGMENT_PENDING);
						
						if(dbHandler.checkInPending(task.getUniversalID())) {
							Log.d("Task Exists?", "Yes in pending");
							// if task exists in pending, update people and task json
							ArrayList<People> pendingPeoples = new ArrayList<People>();
							tasks.remove(task);
							if(peoples!=null) {
								for(People people: peoples) {
									if(people.getTaskId().equals(task.getUniversalID())){
										pendingPeoples.add(people);
									}
								}							
							}
							
							dbHandler.updatePendingValues(context, task.getUniversalID(), thistaskjsonString, pendingPeoples);
							// remove already added people to increase efficiency
							for(People people: pendingPeoples){
								peoples.remove(people);
							}
							
						} else { // task doesn't exist in pending, add people and task json to pending
							Log.d("Task Exists?", "Not in pending. adding...");
							//TODO: acknowledgment
							tasks.remove(task);
							ArrayList<People> pendingPeoples = new ArrayList<People>();
							if(peoples!=null) {
								for(People people: peoples) {
									if(people.getTaskId().equals(task.getUniversalID())){
										pendingPeoples.add(people);
									}
								}							
							}
							dbHandler.addToPending(context, task.getUniversalID(), thistaskjsonString, pendingPeoples);
							// remove already added people to increase efficiency
							for(People people: pendingPeoples)
								peoples.remove(people);
							
							showNoti = true;
						}
					}
					
				}
			}
			
			if(showNoti) {
				ArrayList<Task> pendingTasks = dbHandler.getPendingTasks();
				ArrayList<People> pendingPeoples = dbHandler.getPendingPeoples();
				if(pendingTasks!=null){
					Log.d("recei", "pending tasks not null " + Integer.toString(tasks.size()));
				} else Log.d("recei", "pending tasks null");
				if(pendingPeoples!=null){
					peoples.addAll(pendingPeoples);
					Log.d("recei", "pending not null " + Integer.toString(peoples.size()));
				} else Log.d("recei", "pending null");
				TASK_COUNT = Integer.toString(pendingTasks.size());
				//show notification to accept or decline
				Intent newTaskIntent = new Intent(context, ReceivedTaskActivity.class);
				newTaskIntent.putExtra(RECEIVE_TASK_INTENT, pendingTasks);
				newTaskIntent.putExtra(RECEIVE_PEOPLE_INTENT, peoples);
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newTaskIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
				Notification notification = createNotification(context, pendingIntent);
				NotificationManager notificationManager = (NotificationManager) getApplicationContext()
						.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(8888, notification);
			}
		
		}
		
		if(where.equals("acknowledgment")){
			Log.d("Received acknowledgment", "Yay!");
			String acknowledgmentJson = intent.getStringExtra("accepted");
			HashMap<String, String> hashMapOfAcknowledgment = People.generateHashMapOfAccepted(acknowledgmentJson);
			for(Map.Entry<String,?> entry : hashMapOfAcknowledgment.entrySet()) {
				String key = entry.getKey();
				String universal_id = key.split("-")[0];
				String contact_id = key.split("-")[1];
				int accepted = Integer.parseInt((String) entry.getValue());
				//code to update accepted value in people table
				dbHandler.updateAPeopleColumn(universal_id, contact_id, People.PEOPLE_HAS_ACCEPTED, accepted);
			}
		}
		
		if(where.equals("action")) {
			Log.d("Received action", "Yay!");
			String actionJson = intent.getStringExtra("action_json");
			Log.d("Received action json", actionJson);
			JSONObject actionObject;
			try {
				actionObject = new JSONObject(actionJson);
				@SuppressWarnings("unchecked")
				Iterator<String> keys = actionObject.keys();
				while(keys.hasNext()) {
					String key = (String) keys.next();  // key  is the universal_id of task
					JSONObject actionPropertiesCount = actionObject.getJSONObject(key);
					@SuppressWarnings("unchecked")
					Iterator<String> counts = actionPropertiesCount.keys();
					while(counts.hasNext()) {
						String count = (String) counts.next();
						JSONObject actionProperties = actionPropertiesCount.getJSONObject(count);
						String action_author_id = actionProperties.getString(Actions.ACTION_AUTHOR_ID);
						String action_author_name = actionProperties.getString(Actions.ACTION_AUTHOR);
						String action_type = actionProperties.getString(Actions.ACTION_TYPE);
						String action_unique_id = actionProperties.getString(Actions.ACTION_UNIQUE_ID);
						String action_value = actionProperties.getString(Actions.ACTION_VALUE);
						long action_timestamp = Long.parseLong(actionProperties.getString(Actions.ACTION_TIMESTAMP));
						Actions actions = new Actions();
						actions.setActionTimeStamp(action_timestamp);
						actions.setActionType(action_type);
						actions.setActionValue(action_value);
						actions.setSynced(1);
						actions.setTaskID(key);
						actions.setUniqueId(action_unique_id);
						actions.setUserID(action_author_id);
						actions.setUsername(action_author_name);
						actions.setActionID("2"); // kisi kaam ka nahi hai ye
						
						if(!dbHandler.checkifActionExists(action_unique_id)) {
							dbHandler.addActions(actions);
						}
					}
					
				}
			} catch (JSONException e) {
				Log.e("Action Json error", "JsonException");
				e.printStackTrace();
			}
		}
		
	}
	
	private Notification createNotification(Context context,PendingIntent pendingIntent) {
		Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.meedolocation);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.bell)
				.setLargeIcon(largeIcon)
				.setContentTitle(TASK_COUNT + " new task")
				.setContentText("Your notification time is upon us!")
				.setContentIntent(pendingIntent)
				.setWhen(System.currentTimeMillis())
				.setDefaults(Notification.DEFAULT_ALL);
		Notification notification = builder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;

		return notification;
	}

	private void handleRegistration(Context context, String regId) {
		// TODO Auto-generated method stub
		Utils.registrationId = regId;
		Log.e("", "registration id : " + regId);

		final String PROXY_IP = "202.141.80.22";  
        final int PROXY_PORT = 3128;  

        CredentialsProvider credProvider = new BasicCredentialsProvider();
        credProvider.setCredentials(new AuthScope(PROXY_IP, PROXY_PORT),
            new UsernamePasswordCredentials("k.hrishikesh", "410326"));
        SharedPreferences mPreferences = context.getSharedPreferences("regid", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("reg", regId);
        editor.commit();
		/*HttpPost httpPost = new HttpPost(
				"http://collegewires.com/wiresapp/save_reg_id.php");
		httpPost.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("username", getUsername(getApplicationContext())));
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
*/
	}
	

	public static String getUsername(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String username = preferences.getString("username", "user");
		return username;
	}
}