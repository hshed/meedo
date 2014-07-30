package com.cw.msumit.objects;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.services.SendReminderIntentService;

public class SendReminder {

	//public static final String TASK_TO_SEND = "taskToSend";
	public static final String PEOPLE_TO_SEND = "peopleToSend";
	
	//private SendReminderClient sendReminderClient;
	private Task task;
	private ArrayList<People> peopleList;
	
	/*public SendReminder(SendReminderClient sendReminderClient) {
		this.sendReminderClient = sendReminderClient;
	}*/
	
	public SendReminder() {
	}

	/*public void send(Task task, ArrayList<People> peopleList){
		this.task = task;
		this.peopleList = peopleList;
		this.sendReminderClient.sendReminder(task, peopleList);
	}*/
	/**
	 * task can be null if only people is intended to send
	 * @param context
	 * @param task
	 * @param peopleList
	 */
	public void send(Context context, Task task, ArrayList<People> peopleList, int acknowledgment){
		this.task = task;
		this.peopleList = peopleList;
		Intent sendReminderIntent = new Intent(context, SendReminderIntentService.class);
		sendReminderIntent.putExtra("tasks", task);
		sendReminderIntent.putExtra("peoples", peopleList);
		sendReminderIntent.putExtra("acknowledgment", acknowledgment);
		context.startService(sendReminderIntent);
	}
	
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public ArrayList<People> getPeopleList() {
		return peopleList;
	}

	public void setPeopleList(ArrayList<People> peopleList) {
		this.peopleList = peopleList;
	}

	/**
	 * generates json from the task properties
	 * @param context
	 * @param task
	 * @return json of task e.g. {"hrishikesh.kr2":{"subtask":{},
	 * "location":{"address":"15, Chennai, Chennai, Tamil Nadu, India","longitude":"80.249583","title":"Chennai","latitude":"13.060422"},
	 * "important":1,"assigned":3,"repeat":"N","date":"25-11-2012","creator_id":"0",
	 * "category":"Work","time":"09:00","title":"An important task","completed_on":"N",
	 * "complete":0,"order_by":"201211250899","note":""}}
	 */
	public static String generateTaskJson(Context context, Task task) {
		
		DatabaseHandler dbHandler = new DatabaseHandler(context);
		ArrayList<HashMap<String, String>> subtasks = dbHandler.getListofSubtasks(task.getReminderID());
		Log.d("jsonjson", subtasks.toString());
		Log.d("JSONJSONsize", Integer.toString(subtasks.size()));
		//HashMap<String, String> location = dbHandler.getLocationHashMap(task.getUniversalID());
		
		
		JSONObject taskJson = new JSONObject();
		JSONObject universalTaskId = new JSONObject();
		JSONObject subtaskJsonObject = new JSONObject();
		
		try {
			for(HashMap<String, String> subt:subtasks) {
				Log.d("JSONJSON", subt.get("subtask"));
				Log.d("JSONJSON", subt.get("value"));
				subtaskJsonObject.put(subt.get("subtask"), subt.get("value"));
			}
			JSONObject locationJsonObject;
			try {
				locationJsonObject = new JSONObject(dbHandler.getLocationJson(task.getUniversalID()));
			} catch (Exception e) {
				locationJsonObject = new JSONObject();
			}
			
			/*locationJsonObject.put(Task.LOCATION_TITLE,location.get(LocationListsFragment.LOCATION_TITLE));
			locationJsonObject.put(Task.LOCATION_LATITUDE,location.get(LocationListsFragment.LOCATION_LATITUDE));
			locationJsonObject.put(Task.LOCATION_LONGITUDE,location.get(LocationListsFragment.LOCATION_LONGITUDE));
			locationJsonObject.put(Task.LOCATION_ADDRESS,location.get(LocationListsFragment.LOCATION_ADDRESS));*/
			
			universalTaskId.put(Task.TITLE, task.getTitle());
			universalTaskId.put(Task.CREATOR_ID, task.getCreatorID());
			universalTaskId.put(Task.DATE, task.getDate());
			universalTaskId.put(Task.TIME, task.getTime());
			universalTaskId.put(Task.CATEGORY, task.getCategory());
			universalTaskId.put(Task.IMPORTANT, task.getImportant());
			universalTaskId.put(Task.COMPLETE, task.getComplete());
			universalTaskId.put(Task.COMPLETED_ON, task.getCompletedOn());
			universalTaskId.put(Task.REPEAT, task.getRepeat());
			universalTaskId.put(Task.NOTE, task.getNote());
			universalTaskId.put(Task.ASSIGNED, task.getAssigned());
			universalTaskId.put(Task.ORDER_BY, task.getOrderBy());
			universalTaskId.put(Task.SUBTASK, subtaskJsonObject);
			universalTaskId.put(Task.LOCATION, locationJsonObject);
			
			taskJson.put(task.getUniversalID(), universalTaskId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("JSONJSON", taskJson.toString());
		return taskJson.toString();
	}
	/**
	 * generates json of people involved in a task
	 * @param peopleList
	 * @return JSON of people involved e.g. {"hrishikesh.kr2":{"2":{"hasMeedo":false,"contactId":"1522",
	 * "accepted":0,"email":"nitisha21rashi@gmail.com","name":"Nitisha Singh Bhadoria"},
	 * "1":{"hasMeedo":true,"contactId":"100001222664989","accepted":0,"email":"fallingbridges@gmail.com","name":"Sumit Sinha"},
	 * "0":{"hasMeedo":true,"contactId":"1808287718","accepted":0,"email":"hrskumar92@gmail.com","name":"Hrishikesh Kumar"}}}
	 */
	public static String generatePeopleJson(ArrayList<People> peopleList) {
		JSONObject peopleJsonObject = new JSONObject();
		
		JSONObject countObject = new JSONObject();
		for(People people:peopleList) {
			try {
				JSONObject universalIdObject = new JSONObject();
				universalIdObject.put(People.PEOPLE_CONTACT_ID, people.getFbId());
				universalIdObject.put(People.PEOPLE_EMAIL, people.getEmail());
				universalIdObject.put(People.PEOPLE_HAS_MEEDO, people.hasMeedo());
				universalIdObject.put(People.PEOPLE_NAME, people.getName());
				universalIdObject.put(People.PEOPLE_HAS_ACCEPTED, people.getAccepted());
				
				countObject.put(Integer.toString(peopleList.indexOf(people)), universalIdObject);
				
				peopleJsonObject.put(people.getTaskId(), countObject);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.i("JSONJSON", peopleJsonObject.toString());
		return peopleJsonObject.toString();
	}
}
