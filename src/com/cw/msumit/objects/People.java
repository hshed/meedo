package com.cw.msumit.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class People implements Serializable{

	/**
	 * do not delete
	 */
	private static final long serialVersionUID = -518132443764372902L;
	
	public static final String PEOPLE_TASK_ID = "universal_id";
	public static final String PEOPLE_HAS_MEEDO = "hasMeedo";
	public static final String PEOPLE_CONTACT_ID = "contact_id";
	public static final String PEOPLE_EMAIL = "email";
	public static final String PEOPLE_NAME = "name";
	public static final String PEOPLE_HAS_ACCEPTED = "accepted";
	public static final String PEOPLE_SYNCED = "synced";
	
	private String name, email, fbId, taskId;
	private int accepted, synced=-1;
	private boolean hasMeedo;
	/**
	 * default constructor for the User object
	 */
	public People() {
	}
	
	/**
	 * @param hasMeedo
	 * @param name
	 * @param email
	 * @param username
	 * @param contactId
	 */
	public People(boolean hasMeedo, String name, String email, String contactId ) {
		this.hasMeedo = hasMeedo;
		this.name = name;
		this.email = email;
		this.fbId = contactId;
	}
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public boolean hasMeedo() {
		return hasMeedo;
	}
	public void setHasMeedo(boolean hasMeedo) {
		this.hasMeedo = hasMeedo;
	}
	public String getName() {
		return name;
	}
	public int getAccepted() {
		return accepted;
	}
	public void setAccepted(int assigned) {
		this.accepted = assigned;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFbId() {
		return fbId;
	}

	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	public int getSynced() {
		return synced;
	}

	public void setSynced(int synced) {
		this.synced = synced;
	}
	/**
	 * generates hashmap of the acknowledgement json received
	 * @param acknowledgmentJson
	 */
	public static HashMap<String, String> generateHashMapOfAccepted(String acknowledgmentJson) {
		HashMap<String, String> hashMapOfAccepted = new HashMap<String, String>();
		
		JSONObject acknowledgmentObject;
		try {
			acknowledgmentObject = new JSONObject(acknowledgmentJson);
			@SuppressWarnings("unchecked")
			Iterator<String> keys = acknowledgmentObject.keys();
			while(keys.hasNext()) {
				String key = (String) keys.next();
				hashMapOfAccepted.put(key, acknowledgmentObject.getString(key));
			}
		} catch (JSONException e) {
			Log.e("People generateHashMapOfAccepted", "JsonException");
			e.printStackTrace();
		}
		return hashMapOfAccepted;
	}

}
