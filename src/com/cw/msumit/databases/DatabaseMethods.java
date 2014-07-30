package com.cw.msumit.databases;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.cw.msumit.objects.Task;
import com.cw.msumit.utils.StaticFunctions;
import com.google.android.gms.maps.model.LatLng;

public class DatabaseMethods {

	public static void InsertRecords(int o, Context context) {
		
		// create a string array and add the title
		/*
		 * String[] titles={"Do yoga","Study Chemistry","Code logic backend",
		 * "Repair the computer","Watch glee","Tell someone to download meedo"};
		 */
		
		String[] titles = { "Swipe to delete", "An important task",
				"Task with subtask", "Task from someone else",
				"Buy food for tommy" };

		for (int k = 0; k < titles.length; k++) {
			// String id1 =
			// GCMIntentService.getUsername(context)+"_"+StaticFunctions.getLastCountPlusOne(context);
			String id = Integer.toString(k + 1);
			String title = titles[k];
			String date = "25-11-2012";
			String time = "09:00";
			String lo_id = "0";
			String lo_json = "";
			String lo_r = "0";
			String category = "Personal";
			int Imp = 0;
			int s_task = 0;
			int complete=0;
			int Assigned=1;
			String CompletedOn="N";
			
			if (k == 1) {
				Imp = 1;
			}
			if (k == 2) {
				s_task = 3;
			}
			if (k == 1) {
				category = "Work";
			}

			if (k == 3) {
				category = "From Sumit";
			}
			if (k == 4) {
				category = "Shopping";
			}
			String repeat = "N";
			int Synced = -1;
			String orderBy=StaticFunctions.sortString(date, time, Imp);
			String creatorID = "0";
			String note = "";
			String universalId = StaticFunctions.getUsername(context) + Integer.toString(k+1);
			//String members = "";
			Task reminders = new Task(id, universalId, title, date, time, lo_id,lo_json, lo_r, Imp,
					s_task, category, repeat, Synced, creatorID, note, complete, Assigned, CompletedOn, orderBy);
			DatabaseHandler dbHandler = new DatabaseHandler(context);
			if(!dbHandler.checkIfTaskExist(id)) {
			dbHandler.addReminders(reminders);
			}
			//Log.d("Task values", "Being Added");

		}
		InsertSubtask(context);
		//Log.d("Insert Category", "Called");
		InsertCategory(context);
		InsertPreCategory(context);
		//InsertActionRecords(context);
		
	}

	// insert action records
	/*public static void InsertActionRecords(Context c) {
		//Action 1
		Actions a1= new Actions("1", "1", "100001222664989", "Comment", "In fact I consider this " +
				"more than anything " +
				"an opportunity to learn android and nail it down.", 0, Calendar.getInstance().getTimeInMillis());
		Actions a11= new Actions("1", "1", "100001222664989", "Comment", "more than anything ", 0, Calendar.getInstance().getTimeInMillis());
		Actions a12= new Actions("1", "1", "100001222664989", "Comment", "In fact I consider this", 0, Calendar.getInstance().getTimeInMillis());
		Actions a13= new Actions("1", "1", "100001222664989", "Comment","an opportunity to learn android and nail it down.", 0, Calendar.getInstance().getTimeInMillis());
		Actions a14= new Actions("1", "1", "100001222664989", "Comment", "In fact I consider this " +
				"more than anything " +
				"an opportunity to learn android and nail it down.", 0, Calendar.getInstance().getTimeInMillis());
		Actions a2=new Actions("2", "1", "2", "Comment","equal to Time spent learning. Period.", 0, Calendar.getInstance().getTimeInMillis());
		Actions a21=new Actions("2", "1", "2", "Comment", "Time spent learning is " +
				"equal to Time spent learning. Period.", 0, Calendar.getInstance().getTimeInMillis());
		Actions a22=new Actions("2", "1", "2", "Comment", "rning is " +
				"equal to Time ", 0, Calendar.getInstance().getTimeInMillis());
		Actions a23=new Actions("2", "1", "2", "Comment", "Time spent learning is " +
				"e", 0, Calendar.getInstance().getTimeInMillis());
		Actions a24=new Actions("2", "1", "2", "Comment", "Time spent learning is ", 0, Calendar.getInstance().getTimeInMillis());
		Actions a3=new Actions("3", "1", "2", "Date", "17th Feb to 18th Feb", 0, Calendar.getInstance().getTimeInMillis());
		DatabaseHandler handler=new DatabaseHandler(c);
		for(int i=0; i<30; i++) {
			Log.d("being called this one", Integer.toString(i));
			handler.addActions(a1);
			handler.addActions(a11);
			handler.addActions(a21);
			handler.addActions(a12);
			handler.addActions(a14);
			handler.addActions(a22);
			handler.addActions(a23);
			handler.addActions(a13);
			handler.addActions(a24);
			handler.addActions(a2);
			handler.addActions(a3);
		}
//dbedfe f7f7f7
	}*/

	// insert subtasks record
	public static void InsertSubtask(Context c) {

		String[] subtasks = { "Chocolate", "Apple", "Grocery" };
		// insert these subtasks to only those reminder items where there is
		// subtasks
		// identifying the reminder id where subtask is to be inserted
		DatabaseHandler handler = new DatabaseHandler(c);
		if (!handler.checkIfSubtaskExists("Chocolate")) {
			handler.addArrayofSubtask(subtasks, "3");
		}
	}
	
	// insert categories record
	public static void InsertCategory (Context c) {
		String [] array= {"TASKS", "Active tasks", "Today", "Assigned to someone",
				"Important", "Completed Tasks", "Pending Tasks"};
		DatabaseHandler handler=new DatabaseHandler(c);
		if (!handler.checkIfCategoryExists("Today")) {
			handler.insertArrayOfCategory(array, "1");
		}
	
	}
	
	public static void InsertPreCategory (Context c) {
		String [] array= { "LISTS", "Personal", "Home", "Work", "Shopping"};
		String from="From Sumit";
		DatabaseHandler handler=new DatabaseHandler(c);
		if (!handler.checkIfCategoryExists("Personal")) {
			handler.insertArrayOfCategory(array, "2");
			handler.insertCategory(from, "3");
		}
	
	}
	
	public static int InsertLocation (Context c, LatLng latLng, String locationName) {
		double lat = latLng.latitude;
		double lng = latLng.longitude;
		Geocoder geocoder = new Geocoder(c);
		String address = null;
		try {
			List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
			address = getAddress(addresses.get(0));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			address = "unknown";
			e.printStackTrace();
		}
		DatabaseHandler dbHandler = new DatabaseHandler(c);
		if(dbHandler.checkIfLocationExists(lat, lng)==0) {
			dbHandler.insertLocation(lat, lng, locationName, address);
			return 0;
		} else {
			return dbHandler.checkIfLocationExists(lat, lng);
		}
	}
	
	public static String getAddress(Address add) {
		String f = add.getFeatureName();
		String l = add.getLocality();
		String sA = add.getSubAdminArea();
		String aA = add.getAdminArea();
		String c = add.getCountryName();
		String n = add.getPremises();
		String address = "";
		if (f!=null){
			address = f;
		}
		if (l!=null){
			address = address + ", " +l ;
		}
		if (sA!=null){
			address = address + ", " + sA ;
		}
		if (aA!=null){
			address = address + ", "+ aA ;
		}
		if (c!=null){
			address = address + ", "+ c;
		}
		if (n!=null){
			address = address + ", Near:"+ n;
		}
		return address;
	}

	public static void deleteLocation(Context c, int id) {
		// TODO Auto-generated method stub
		DatabaseHandler dbHandler = new DatabaseHandler(c);
		dbHandler.deleteLocation(id);
	}
}