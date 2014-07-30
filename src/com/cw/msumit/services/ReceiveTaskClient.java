package com.cw.msumit.services;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.cw.msumit.objects.People;
import com.cw.msumit.objects.Task;

@SuppressWarnings("unchecked")
public class ReceiveTaskClient {

	// The hook into our service
	//private ReceiveTaskService mBoundService;
	// The context to start the service in
	private Context mContext;
	// A flag if we are connected to the service or not
	//private boolean mIsBound;

	public ReceiveTaskClient(Context context) {
		mContext = context;
	}

	/**
	 * Call this to connect your activity to your service
	 */
	/*public void doBindService() {
		// Establish a connection with our service
		mContext.bindService(new Intent(mContext, ReceiveTaskService.class),
				mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	/**
	 * When you attempt to connect to the service, this connection will be
	 * called with the result. If we have successfully connected we instantiate
	 * our service object so that we can call methods on it.
	 
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with our service has been
			// established,
			// giving us the service object we can use to interact with our
			// service.
			mBoundService = ((ReceiveTaskService.ServiceBinder) service).getService();
			checkForTasks();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
		}
	};

	
	public void checkForTasks() {
		mBoundService.checkForTasks(mContext);
	}*/
	public void checkForTasks() {
		Intent receiveTaskIntent = new Intent(mContext, ReceiveTaskIntentService.class);
		mContext.startService(receiveTaskIntent);
	}

	/**
	 * When you have finished with the service call this method to stop it
	 * releasing your connection and resources
	 */
	/*public void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			mContext.unbindService(mConnection);
			mIsBound = false;
		}
	}
	*/
	public static ArrayList<Task> generateTaskObjectList(String taskJson) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		
		try {
			JSONObject taskIdObject = new JSONObject(taskJson);
			Iterator<String> keys = taskIdObject.keys();
			while(keys.hasNext()) {
				String universal_id = keys.next();
				JSONObject taskProperties = taskIdObject.getJSONObject(universal_id);
				int important = taskProperties.getInt(Task.IMPORTANT);
				int assigned = taskProperties.getInt(Task.ASSIGNED);
				String repeat = taskProperties.getString(Task.REPEAT);
				String date = taskProperties.getString(Task.DATE);
				String time = taskProperties.getString(Task.TIME);
				String creator_id = taskProperties.getString(Task.CREATOR_ID);
				String title = taskProperties.getString(Task.TITLE);
				String category = taskProperties.getString(Task.CATEGORY);
				int completed = taskProperties.getInt(Task.COMPLETE);
				String completed_on = taskProperties.getString(Task.COMPLETED_ON);
				String note = taskProperties.getString(Task.NOTE);
				String location = taskProperties.getString(Task.LOCATION);
				String subtask = taskProperties.getString(Task.SUBTASK);
				String taskFrom = taskProperties.getString(Task.TASK_FROM);
				
				Task task = new Task();
				task.setUniversalID(universal_id);
				task.setImportant(important);
				task.setAssigned(assigned);
				task.setRepeat(repeat);
				task.setDate(date);
				task.setTime(time);
				task.setCreatorID(creator_id);
				task.setTitle(title);
				task.setCategory(category);
				task.setComplete(completed);
				task.setCompletedOn(completed_on);
				task.setNote(note);
				task.setLocationJson(location);
				task.setSubtask(subtask);
				task.setTaskFrom(taskFrom);

				tasks.add(task);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tasks;
		
	}
	
	public static ArrayList<People> generatePeopleObjectList(String peopleJson) {
		ArrayList<People> peoples = new ArrayList<People>();
		try {
			JSONObject taskIdObject = new JSONObject(peopleJson);
			Iterator<String> keys = taskIdObject.keys();
			while (keys.hasNext()) {
				
				String universal_id = (String) keys.next();
				JSONObject peopleObject = taskIdObject.getJSONObject(universal_id);
				Iterator<String> peopleKey = peopleObject.keys();
				while (peopleKey.hasNext()) {
					String peopleNo = (String) peopleKey.next();
					JSONObject peopleProperties = peopleObject.getJSONObject(peopleNo);
					
					boolean hasMeedo = false;
					try {
						if(peopleProperties.getInt(People.PEOPLE_HAS_MEEDO) !=0){
							hasMeedo = true;
						}
					} catch (JSONException e) {
						hasMeedo = peopleProperties.getBoolean(People.PEOPLE_HAS_MEEDO);
					}
					
					int accepted = peopleProperties.getInt(People.PEOPLE_HAS_ACCEPTED);
					String name = peopleProperties.getString(People.PEOPLE_NAME);
					String email = peopleProperties.getString(People.PEOPLE_EMAIL);
					String contact_id = peopleProperties.getString(People.PEOPLE_CONTACT_ID);
					
					People people = new People();
					people.setTaskId(universal_id);
					people.setHasMeedo(hasMeedo);
					people.setAccepted(accepted);
					people.setName(name);
					people.setEmail(email);
					people.setFbId(contact_id);
					peoples.add(people);
				}
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return peoples;
	}

}