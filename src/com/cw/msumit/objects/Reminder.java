package com.cw.msumit.objects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


import com.cw.msumit.services.ScheduleClient;

public class Reminder {
	
	private int DAY, MONTH, YEAR, HOUR, MINUTE, LOCATION_DB_ID;
	private boolean TIME_TRUE_FORMAT, DATE_TRUE_FORMAT;
	private ScheduleClient scheduleClient;
	HashMap<String, Calendar> calendarHashMap = new HashMap<String, Calendar>();
	private Task TASK;
	/**
	 * Constructor for Reminder object. It copies the value of DAY,
	 * MONTH, YEAR, HOUR, MINUTE, LOCATION_DB_ID, REPEAT values 
	 * from the Task object provided. <br/>
	 * <b>Use this constructor for a single task object</b>
	 * @param task a new task object
	 */
	public Reminder(ScheduleClient scheduleClient, Task task) {
		
		this.scheduleClient = scheduleClient;
		this.TASK = task;
		
		String[] dString = task.getDate().split("-");
		try {
			this.DAY = Integer.parseInt(dString[0]);
			this.MONTH = Integer.parseInt(dString[1]);
			this.YEAR = Integer.parseInt(dString[2]);
			this.DATE_TRUE_FORMAT = true;
		} catch (Exception e) {
			// TODO: handle exception
			this.DATE_TRUE_FORMAT = false;
		}
		
		String[] tString = task.getTime().split(":");
		try {
			this.HOUR = Integer.parseInt(tString[0]);
			this.MINUTE = Integer.parseInt(tString[1]);
			this.TIME_TRUE_FORMAT = true;
		} catch (NumberFormatException e) {
			// TODO: handle exception
			this.TIME_TRUE_FORMAT = false;
		}
		
		
		
		this.LOCATION_DB_ID = Integer.parseInt(task.getLocationID());
		
	}
	
	private Calendar retrieveCalendar() {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		if(DATE_TRUE_FORMAT) {
			if(TIME_TRUE_FORMAT) {
				calendar.set(YEAR, MONTH, DAY, HOUR, MINUTE,0);
			} else calendar.set(YEAR, MONTH, DAY);
		} 
		return calendar;
	}

	/**
	 * method to add reminder for single task. <br/>
	 * <b>Note: </b>Use constructor {@code Reminder(ScheduleClient scheduleClient, Task task) }
	 * before using this method
	 */
	public void remind() {
		Calendar c = retrieveCalendar();
		//if(calendar.before(c)) {
			this.scheduleClient.setAlarmForNotification(c, this.TASK);
		//}
	}
	/**
	 * used for "later" reminder
	 * @param later
	 */
	public void remind(int later) {
		Calendar c = Calendar.getInstance();
		if(later==0) { //later today
			c.setTimeInMillis(c.getTimeInMillis() + 3*60*60*1000);
		} else c.setTimeInMillis(c.getTimeInMillis() + 24*60*60*1000); // later tmrw
		this.scheduleClient.setAlarmForNotification(c, this.TASK);
	}
	
	/**
	 * method to remove reminder for single task. <br/>
	 * <b>Note: </b>Use constructor {@code Reminder(ScheduleClient scheduleClient, Task task) }
	 * before using this method
	 */
	public void remove() {
		this.scheduleClient.removeAlarmForNotification(this.TASK);
	}
	
	/**
	 * Constructor for Reminder object. <br/>
	 * <b>Use this constructor for adding reminder 
	 * to multiple tasks at once</b>
	 * @param task a new task object
	 */
	public Reminder(ScheduleClient scheduleClient) {
		this.scheduleClient = scheduleClient;
	}
	
	/**
	 * method to add reminder for each task in the task ArrayList
	 * @param tasks ArrayList object of multiple tasks 
	 * 		for which reminder is to be added.
	 */
	public void remind(ArrayList<Task> tasks) {
		for(Task task: tasks) {
			String[] dString = task.getDate().split("-");
			try {
				this.DAY = Integer.parseInt(dString[0]);
				this.MONTH = Integer.parseInt(dString[1]);
				this.YEAR = Integer.parseInt(dString[2]);
				this.DATE_TRUE_FORMAT = true;
			} catch (Exception e) {
				// TODO: handle exception
				this.DATE_TRUE_FORMAT = false;
			}
			
			String[] tString = task.getTime().split(":");
			try {
				this.HOUR = Integer.parseInt(tString[0]);
				this.MINUTE = Integer.parseInt(tString[1]);
				this.TIME_TRUE_FORMAT = true;
			} catch (NumberFormatException e) {
				// TODO: handle exception
				this.TIME_TRUE_FORMAT = false;
			}
			
			this.LOCATION_DB_ID = Integer.parseInt(task.getLocationID());
			//Calendar c = Calendar.getInstance();
			//if(c.before(calendar)) {
				this.scheduleClient.setAlarmForNotification(retrieveCalendar(), task);
			//}
		}
	}
	
	/**
	 * method to remove reminder for each task in the task ArrayList
	 * @param tasks ArrayList object of multiple tasks 
	 * 		for which reminder is to be removed.
	 */
	public void remove(ArrayList<Task> tasks) {
		for(Task task : tasks) {
			this.scheduleClient.removeAlarmForNotification(task);
		}
	}
	
	
	public int getDAY() {
		return this.DAY;
	}

	public void setDAY(int dAY) {
		this.DAY = dAY;
	}

	public int getMONTH() {
		return this.MONTH;
	}

	public void setMONTH(int mONTH) {
		this.MONTH = mONTH;
	}

	public int getYEAR() {
		return this.YEAR;
	}

	public void setYEAR(int yEAR) {
		this.YEAR = yEAR;
	}

	public int getHOUR() {
		return this.HOUR;
	}

	public void setHOUR(int hOUR) {
		this.HOUR = hOUR;
	}

	public int getMINUTE() {
		return this.MINUTE;
	}

	public void setMINUTE(int mINUTE) {
		this.MINUTE = mINUTE;
	}

	public int getLOCATION_DB_ID() {
		return this.LOCATION_DB_ID;
	}

	public void setLOCATION_DB_ID(int lOCATION_DB_ID) {
		this.LOCATION_DB_ID = lOCATION_DB_ID;
	}

	
}