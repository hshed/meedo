package com.cw.msumit.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.utils.StaticFunctions;
/**
 * An Actions object includes Comment, changes in Date or Time, any Edit Action, changes in People action
 */
public class Actions implements Serializable {
	
	private static final long serialVersionUID = 5749439437086316253L;
	// private variables
	public String ActionID, TaskID, UserID, actionType, username, actionValue, uniqueId;

	public int Synced = 0;
	public long actionTimeStamp = 0L;

	/**
	 * Comment
	 */
	public final static String ACTION_TYPE_COMMENT = "Comment";
	/**
	 * Date
	 */
	public final static String ACTION_TYPE_DATE = "Date";
	/**
	 * Edit
	 */
	public final static String ACTION_TYPE_EDIT = "Edit";
	/**
	 * Time
	 */
	public final static String ACTION_TYPE_TIME = "Time";
	/**
	 * Note
	 */
	public final static String ACTION_TYPE_NOTE = "Note";
	/**
	 * Subtask
	 */
	public final static String ACTION_TYPE_SUBTASK = "Subtask";
	public final static String ACTION_TASK_ID = "universal_id";
	public final static String ACTION_TYPE = "action_type";
	public final static String ACTION_VALUE = "action_value";
	public final static String ACTION_AUTHOR = "name";
	public final static String ACTION_AUTHOR_ID = "contact_id";
	public static final String ACTION_SYNCED = "synced";
	public static final String ACTION_UNIQUE_ID = "action_unique_id";
	public static final String ACTION_TIMESTAMP = "timestamp";
	
	// constructor
	public Actions() {

	}

	public Actions(String ActionID, String TaskID, String UserID,
			String actionType, String actionValue) {
		this.ActionID = ActionID;
		this.TaskID = TaskID;
		this.UserID = UserID;
		this.actionType = actionType;
		this.actionValue = actionValue;
		this.Synced = 0;
		this.actionTimeStamp = 0;

	}

	public Actions(String ActionID, String TaskID, String UserID, String Username,
			String actionType, String actionValue, int Synced, long timeStamp) {
		this.ActionID = ActionID;
		this.TaskID = TaskID;
		this.UserID = UserID;
		this.actionType = actionType;
		this.actionValue = actionValue;
		this.Synced = Synced;
		this.username = Username;
		this.actionTimeStamp = timeStamp;
	}
	/**
	 * generates json for one action
	 * @param action
	 * @return json in string format
	 */
	public static String generateActionJson(Actions action) {
		JSONObject actionCountJson = new JSONObject();
		JSONObject actionJson = new JSONObject();
		JSONObject actionProperties = new JSONObject();
		try {
			actionProperties.put(ACTION_AUTHOR_ID, action.getUserID());
			actionProperties.put(ACTION_AUTHOR, action.getUsername());
			actionProperties.put(ACTION_TYPE, action.getActionType());
			actionProperties.put(ACTION_VALUE, action.getActionValue());
			actionProperties.put(ACTION_UNIQUE_ID, action.getUniqueId());
			actionProperties.put(ACTION_TIMESTAMP, action.getActionTimeStamp());
			actionCountJson.put("1", actionProperties);
			actionJson.put(action.getTaskID(), actionCountJson);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d("Action Json", actionJson.toString());
		return actionJson.toString();
		
	}
	/**
	 * generates Json of Action from arraylist of actions
	 * @param actions
	 * @return json in string
	 */
	public static String generateActionJson(ArrayList<Actions> actions) {
		JSONObject actionCountJson = new JSONObject();
		JSONObject actionJson = new JSONObject();
		int count =1;
		try {
			for(Actions action: actions) {
				JSONObject actionProperties = new JSONObject();
				actionProperties.put(ACTION_AUTHOR_ID, action.getUserID());
				actionProperties.put(ACTION_AUTHOR, action.getUsername());
				actionProperties.put(ACTION_TYPE, action.getActionType());
				actionProperties.put(ACTION_VALUE, action.getActionValue());
				actionProperties.put(ACTION_UNIQUE_ID, action.getUniqueId());
				actionProperties.put(ACTION_TIMESTAMP, action.getActionTimeStamp());
				
				actionCountJson.put(Integer.toString(count), actionProperties);
				count = count + 1;
				actionJson.put(action.getTaskID(), actionCountJson);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d("Action Json", actionJson.toString());
		return actionJson.toString();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getActionTimeStamp() {
		return actionTimeStamp;
	}

	public void setActionTimeStamp(long actionTimeStamp) {
		this.actionTimeStamp = actionTimeStamp;
	}

	// getter methods
	public String getActionID() {
		return this.ActionID;
	}

	public String getTaskID() {
		return this.TaskID;
	}

	public String getUserID() {
		return this.UserID;
	}

	public String getActionType() {
		return this.actionType;
	}

	public String getActionValue() {
		return this.actionValue;
	}

	public int getSynced() {
		return this.Synced;
	}

	// setter methods
	//
	public void setActionID(String r) {
		this.ActionID = r;
	}

	public void setTaskID(String r) {
		this.TaskID = r;
	}

	public void setUserID(String r) {
		this.UserID = r;
	}

	public void setActionType(String r) {
		this.actionType = r;
	}

	public void setActionValue(String r) {
		this.actionValue = r;
	}

	public void setSynced(int r) {
		this.Synced = r;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	/**
	 * creates a new unique id for an action 
	 * @param context
	 * @return
	 */
	public static String generateUniqueId(Context context) {
		DatabaseHandler dbHandler = new DatabaseHandler(context);
		boolean generateAgain;
		String UniqueId;
		do {
			UniqueId = StaticFunctions.getUsername(context) + Integer.toString(StaticFunctions.getRandomInteger(99999, 10000000, new Random()));
			if(dbHandler.checkifActionExists(UniqueId))
				generateAgain = true;
			else generateAgain = false;
		} while (generateAgain);
		
		return UniqueId;
	}
}
