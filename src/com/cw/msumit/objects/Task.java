package com.cw.msumit.objects;

import java.io.Serializable;

import com.cw.msumit.utils.StaticFunctions;

public class Task implements Serializable{

	/**
	 * serial version UID of Task object
	 */
	private static final long serialVersionUID = 4235251641764881030L;
	
	public static final String UNIVERSAL_ID = "universalId";
	public static final String TITLE = "title";
	public static final String DATE = "date";
	public static final String TIME = "time";
	public static final String CATEGORY = "category";
	public static final String IMPORTANT = "important";
	public static final String COMPLETE = "completed";
	public static final String COMPLETED_ON = "completed_on";
	public static final String CREATOR_ID = "creator_id";
	public static final String REPEAT = "repeat";
	public static final String NOTE = "note";
	public static final String ASSIGNED = "assigned";
	public static final String ORDER_BY = "order_by";
	public static final String SUBTASK = "subtask";
	public static final String LOCATION = "location";
	public static final String LOCATION_TITLE = "title";
	public static final String LOCATION_LATITUDE = "latitude";
	public static final String LOCATION_LONGITUDE = "longitude";
	public static final String LOCATION_ADDRESS = "address";
	public static final String LOCATION_TRANSITION_TYPE = "transitionType";
	public static final String TASK_FROM = "taskFrom";
	
	/**
	 * user has accepted the task
	 */
	public static final int TASK_ACKNOWLEDGMENT_ACCEPT = 1;
	/**
	 * user has declined the task
	 */
	public static final int TASK_ACKNOWLEDGMENT_DECLINE = -1;
	/**
	 * user has the task in pending
	 */
	public static final int TASK_ACKNOWLEDGMENT_PENDING = 2;
	/**
	 * acknowledgment sending failed
	 */
	public static final int TASK_ACKNOWLEDGMENT_FAILED = 3;
	/**
	 * user has removed himself
	 */
	public static final int TASK_ACKNOWLEDGMENT_REMOVED = -2;
	/**
	 * 0 means sync has been attempted and failed.<br/> 1 implies sync is successful.<br/> -1 means sync has never been attempted
	 */
	public static final String SYNCED = "synced";
	
	// private variables
	public String ReminderID;
	public String UniversalID;
	public String Title;
	/**
	 * format: "D-M-Y"
	 */
	public String Date="N-N-N";
	/**
	 * format: "H:M"
	 */
	public String Time="N:N";
	public String LocationID="0";
	public String LocationReminder="0";
	public int Important=0;
	public int Subtasks=0;
	public String Category="Personal";
	public String Repeat="N";
	public int Synced=-1;
	public String creatorID="0";
	public String Note="";
	
	public int Complete=0;
	public int Assigned=1;
	public int tag = 0;
	//public String Members;
	/**
	 * this stores the json string of location and subtask received from web
	 */
	public String locationJson, subtask, taskFrom;
	public String getLocationJson() {
		return locationJson;
	}

	public void setLocationJson(String locationJson) {
		this.locationJson = locationJson;
	}

	public String getSubtask() {
		return subtask;
	}

	public void setSubtask(String subtask) {
		this.subtask = subtask;
	}

	public String getTaskFrom() {
		return taskFrom;
	}

	public void setTaskFrom(String taskFrom) {
		this.taskFrom = taskFrom;
	}

	/**
	 * format: "N"
	 */
	public String completedOn="N";
	public String orderBy=StaticFunctions.sortString(Date, Time, Important);

	// constructors
	public Task() {

	}
	
	public Task(String ReminderID, String Title) {
		this.ReminderID = ReminderID;
		this.Title = Title;
	}

	public Task(String ReminderID, String Title, String Date) {
		this.ReminderID = ReminderID;
		this.Title = Title;
		this.Date = Date;
	}

	public Task(String ReminderID, String Title, String Date, String Time) {
		this.ReminderID = ReminderID;
		this.Title = Title;
		this.Date = Date;
		this.Time = Time;

	}

	public Task(String ReminderID, String UniversalID, String Title, String Date, String Time,
			String LocationID,String LocationJson, String LocationReminder, int Important,
			int Subtasks, String Category, String Repeat, int Synced, 
			String CreatorID, String Note, int Complete, int Assigned, String CompletedOn, String OrderBy ) {
		this.ReminderID=ReminderID;
		this.UniversalID = UniversalID;
		this.Title=Title;
		this.Date=Date;
		this.Time=Time;
		this.LocationID=LocationID;
		this.locationJson = LocationJson;
		this.LocationReminder=LocationReminder;
		this.Important=Important;
		this.Subtasks=Subtasks;
		this.Category=Category;
		this.Repeat=Repeat;
		this.Synced=Synced;
		this.creatorID=CreatorID;
		this.Note=Note;
		this.Complete=Complete;
		this.Assigned=Assigned;
		//this.Members = Members;
		this.completedOn=CompletedOn;
		this.orderBy=OrderBy;

	}
	public String getUniversalID() {
		return this.UniversalID;
	}

	public void setUniversalID(String universalID) {
		this.UniversalID = universalID;
	}

	/*public String getMembers() {
		return this.Members;
	}

	public void setMembers(String members) {
		this.Members = members;
	}
*/
	public Task(String ReminderID, String Title, String LocationID, 
			String LocationReminder, int Important,
			int Subtasks, String Category, String Repeat) {
		this.ReminderID=ReminderID;
		this.Title=Title;
		this.LocationID=LocationID;
		this.LocationReminder=LocationReminder;
		this.Important=Important;
		this.Subtasks=Subtasks;
		this.Category=Category;
		this.Repeat=Repeat;

	}
	
	//getters
	public String getReminderID () {
		return this.ReminderID;
		
	}
	
	public String getTitle () {
		return this.Title;
		
	}
	
	public String getDate () {
		return this.Date;
		
	}
	
	public String getTime () {
		return this.Time;
		
	}
	
	public String getLocationID () {
		return this.LocationID;
		
	}
	
	public String getLocationReminder () {
		return this.LocationReminder;
		
	}
	public int getImportant () {
		return this.Important;
		
	}
	
	public int getSubtasks () {
		return this.Subtasks;
		
	}
	
	public String getCategory () {
		return this.Category;
		
	}
	
	public String getRepeat () {
		return this.Repeat;
		
	}
	public String getCreatorID () {
		return this.creatorID;
	}
	
	public int getSynced () {
		return this.Synced; 
	}
	
	public String getNote() {
		return this.Note;
	}
	
	public int getComplete() {
		return this.Complete;
	}
	public int getAssigned() {
		return this.Assigned;
	}
	public String getCompletedOn () {
		return this.completedOn;
		
	}
	
	public String getOrderBy() {
		return this.orderBy;
	}
	
	//setters
	public void setReminderID (String r) {
		this.ReminderID = r;
		
	}
	
	public void setTitle (String r) {
		this.Title=r;
		
	}
	
	public void setDate (String r) {
		this.Date=r;
		
	}
	
	public void setTime (String r) {
		this.Time=r;
		
	}
	
	public void setLocationID (String r) {
		this.LocationID=r;
		
	}
	
	public void setLocationReminder (String r) {
		this.LocationReminder=r;
		
	}
	public void setImportant (int r) {
	this.Important=r;
		
	}
	
	public void setSubtasks (int r) {
		this.Subtasks=r;
		
	}
	
	public void setCategory (String r) {
		this.Category=r;
		
	}
	
	public void setRepeat (String r) {
		this.Repeat=r;
		
	}
	public void setCreatorID (String r) {
		this.creatorID=r;
	}
	
	public void setSynced (int r) {
		this.Synced=r; 
	}
	
	public void setNote(String r) {
		this.Note=r;
	}
	
	public void setComplete(int r) {
		this.Complete=r;
	}

	public void setAssigned(int r) {
		this.Assigned=r;
	}
	
	public void setCompletedOn (String r) {
		this.completedOn=r;
	}
	
	public void setOrderBy(String r)  {
		this.orderBy=r;
	}
	/**
	 * set tag for this object. mainly used in the "later" of reminder
	 * @param o
	 */
	public void setTag(int o){
		this.tag = o;
	}
	public int getTag(){
		return this.tag;
	}
}
