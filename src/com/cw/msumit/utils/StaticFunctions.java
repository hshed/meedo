package com.cw.msumit.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.cw.msumit.R;
import com.cw.msumit.SubtaskActivity;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.databases.DatabaseMethods;
import com.cw.msumit.fragments.LocationListsFragment;
import com.cw.msumit.objects.Actions;
import com.cw.msumit.objects.Task;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class StaticFunctions {

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	// gets the username
	public static String getName(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String name = preferences.getString("name", "NeedMeedoSignIn");
		return name;
	}
	public static String getUsername(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String username = preferences.getString("username", "NeedMeedoSignIn");
		return username;
	}
	
	public static String getUserEmail(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String username = preferences.getString("email", "NeedMeedoSignIn");
		return username;
	}
	
	public static String getUserFbId(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String username = preferences.getString("fbID", "NeedMeedoSignIn");
		return username;
	}

	/**
	 * generates a unique id for a task. 
	 * @param context
	 * @return
	 */
	public static String generateUniversalId(Context context) {
		DatabaseHandler dbHandler = new DatabaseHandler(context);
		boolean generateAgain;
		String universal_id;
		do {
			universal_id = getUsername(context) + Integer.toString(getRandomInteger(99999, 1000000, new Random()));
			if(dbHandler.checkifTaskExists(universal_id))
				generateAgain = true;
			else generateAgain = false;
		} while (generateAgain);
		
		return universal_id;
	}
	// get the last count of reminders
	public static int getLastCount(Context context) {
		DatabaseHandler dbHandler = new DatabaseHandler(context);
		String task = "tasks";
		Cursor cursor = dbHandler.getCursor(task);
		int count = cursor.getCount();
		cursor.close();
		dbHandler.close();
		return count;
	}

	// get the reminder ID of the last reminder
	public static int getLastReminder(Context context) {
		DatabaseHandler dbHandler = new DatabaseHandler(context);
		int i = dbHandler.getHighestReminderID();
		Log.d("Highest ReminderID", Integer.toString(i));
		return i;
	}

	public static int getLastCountPlusOne(Context context) {
		DatabaseHandler dbHandler = new DatabaseHandler(context);
		String task = "tasks";
		Cursor cursor = dbHandler.getCursor(task);
		int count = cursor.getCount() + 1;
		cursor.close();
		dbHandler.close();
		return count;
	}

	// setTypeface
	public static Typeface setType(int type, AssetManager a) {
		String fontPath1 = "fonts/Roboto-Light.ttf";
		String fontPath2 = "fonts/Roboto-Medium.ttf";
		String fontPath3 = "fonts/Roboto-Condensed.ttf";
		Typeface typeface;
		if (type == 1) {
			typeface = Typeface.createFromAsset(a, fontPath1);
			return typeface;
		}
		if (type == 2) {
			typeface = Typeface.createFromAsset(a, fontPath2);
			return typeface;
		}

		if (type == 3) {
			typeface = Typeface.createFromAsset(a, fontPath3);
			return typeface;
		}

		else {
			return null;
		}
	}

	// change Arraylist<HashMap<String, String>> to String[] if key of hashmap
	// is known and constant
	public static String[] changeListToStringArray(
			ArrayList<HashMap<String, String>> a, String key) {
		String[] array = new String[a.size()];
		for (int i = 0; i < a.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map = a.get(i);
			array[i] = map.get(key);
		}

		return array;

	}

	// change String Array into ArrayList
	public static ArrayList<HashMap<String, String>> changeStringArrayintoList(
			String[] a, String key) {
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < a.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(key, a[i]);
			arrayList.add(map);
		}
		return arrayList;
	}

	// order the dataset as equal to how it is to be displayed
	public static ArrayList<Task> OrderedTasks(ArrayList<Task> r) {
		return r;
	}

	// change date, time and importance into number
	public static long SortInteger(String Date, String Time, int Important) {
		long sortNumber = 0;
		long year = 0, month = 0, day = 0, hour = 0, min = 0, imp = Important;
		// change the date into integar
		if (!Date.equals("N-N-N")) {
			String[] d = Date.split("-");
			year = Long.parseLong(d[2]);
			month = Long.parseLong(d[1]);
			day = Long.parseLong(d[0]);
		}

		if (!Time.equals("N:N")) {
			String[] t = Time.split(":");
			hour = Long.parseLong(t[0]);
			min = Long.parseLong(t[1]);
		}
		if (Time.equals("N:N") && !Date.equals("N-N-N")) {
			hour = 30;
		}

		sortNumber = (year * 100000000) + (month * 1000000) + (day * 10000)
				+ (hour * 100) + min - imp;
		if (sortNumber == 0) {
			sortNumber = 300000000000L;
		}
		if (sortNumber == -1) {
			sortNumber = 300000000000L - 1;
		}

		return sortNumber;
	}

	public static String sortString(String Date, String Time, int Important) {
		return Long.toString(SortInteger(Date, Time, Important));
	}

	// return the reminderID of the orderedTask on the basis of position
	public static String reminderIDOFOrderedTask(ArrayList<Task> r, int position) {
		String reminderID = "0";
		Task task = r.get(position);
		reminderID = task.getReminderID();
		return reminderID;
	}
	
	public static String universalIdOfTask(ArrayList<Task> r, int position) {
		String universalID ;
		Task task = r.get(position);
		universalID = task.getUniversalID();
		return universalID;
	}

	// function that starts subtaskactivity with suitable intents
	public static void startSubtask(String reminderID, String[] array, Context c) {
		Intent intent = new Intent(c, SubtaskActivity.class);
		intent.putExtra("reminderID", reminderID);
		intent.putExtra("subtaskArray", array);

	}

	// check if a string is whitespace
	public static Boolean isWhiteSpace(String s) {
		boolean isWhitespace = s.matches("^\\s*$");
		return isWhitespace;

	}

	// converts a string into a JSONArray
	public static JSONArray convertJSONFromString(String s) {
		try {
			JSONArray jsonArray = new JSONArray(s);
			return jsonArray;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// converts datestring into value array
	public static int[] intArray(String value, String split) {
		String[] s = value.split(split);
		int[] date = new int[s.length];
		for (int i = 0; i < s.length; i++) {
			date[i] = Integer.parseInt(s[i]);
		}
		return date;
	}

	// return true or false on 0 and 1
	public static Boolean intToBoolean(int i) {
		if (i == 1) {
			return true;
		} else {
			return false;
		}

	}

	// return a boolean array on int array
	public static boolean[] intToBooleanArray(int[] i) {
		boolean[] bool = new boolean[7];
		for (int k = 0; k < i.length; k++) {
			if (i[k] == 0) {
				bool[k] = false;
			} else {
				bool[k] = true;
			}
		}
		return bool;
	}

	// change calendar into date string
	public static String calendarIntoDateString(Calendar c) {
		String date;
		int Date = c.get(Calendar.DAY_OF_MONTH);
		int Month = c.get(Calendar.MONTH);
		int Year = c.get(Calendar.YEAR);
		date = Integer.toString(Date) + "-" + Integer.toString(Month) + "-"
				+ Integer.toString(Year);
		return date;
	}

	public static String getTodaysDate() {
		return calendarIntoDateString(Calendar.getInstance());

	}

	// change calendar into time string
	public static String calendarIntoTimeString(Calendar c) {
		String date;
		int Hour = c.get(Calendar.HOUR_OF_DAY);
		int Minute = c.get(Calendar.MINUTE);
		date = Integer.toString(Hour) + ":" + Integer.toString(Minute);
		return date;
	}

	/**
	 * Returns the repeat string in the following format
	 * 
	 * @param noRepeat
	 *            "N"
	 * @param daily
	 *            "D"
	 * @param monthly
	 *            "M-25"
	 * @param weekly
	 *            "W-0-5-6"
	 * @return repeat String
	 */
	public static String repeatInfoIntoString(boolean noRepeat, boolean daily,
			int monthly, boolean[] weekly) {
		String rString = "N"; // default is No repeat i.e. N
		if (noRepeat) {
			rString = "N";
		} else {
			if (daily) {
				rString = "D";
			} else {
				if (monthly != 0) {
					rString = "M" + "-" + Integer.toString(monthly);
				} else {
					rString = "W";
					for (int i = 0; i < weekly.length; i++) {
						if (weekly[i] == true) {
							rString = rString + "-" + Integer.toString(i);
						}
					}
				}
			}
		}

		return rString;
	}

	/**
	 * returns boolean value of no repeat
	 * @param repeatString access it from db
	 * @return true if no repeat, false if there is a repeat
	 */
	public static boolean noRepeatfromString(String repeatString) {
		return repeatString.equals("N");
	}

	/**
	 * returns boolean value of daily repeat
	 * @param repeatString access it from db
	 * @return true if daily, false if there is no daily repeat
	 */
	public static boolean dailyRepeatfromString(String repeatString) {
		return repeatString.equals("D");
	}

	/**
	 * returns boolean value of monthly
	 * @param repeatString access it from db
	 * @return value of monthly repeat
	 */
	public static int monthlyRepeatDayfromString(String repeatString) {
		if (repeatString.startsWith("M")) {
			return Integer.parseInt(repeatString.substring(2)); // M-12 index at
																// 2
		} else
			return 0;
	}

	/**
	 * returns boolean value of weekly
	 * @param repeatString access it from db
	 * @return value of weekly repeat days in boolean[]
	 */
	public static boolean[] weeklyRepeatDaysfromString(String repeatString) {
		boolean[] weeklyRepeatDays = new boolean[] { false, false, false,
				false, false, false, false };
		if (repeatString.startsWith("W")) {
			int[] slicedString = intArray(repeatString.substring(2), "-");
			for (int i : slicedString) {
				weeklyRepeatDays[i] = true;
			}
			return weeklyRepeatDays;
		}
		return weeklyRepeatDays;
	}

	// convert a boolean array into 0-1 etc
	public static String booleanArrayintoString(Boolean[] bool) {
		String r = "";
		for (int i = 0; i < bool.length; i++) {
			if (r.equals("")) {
				r = booleanToString(bool[i]);
			} else {
				r = r + "-" + booleanToString(bool[i]);
			}
		}
		return r;
	}

	public static String booleanToString(Boolean b) {
		String r = "";
		if (b) {
			r = "1";
		} else {
			r = "0";
		}
		return r;
	}

	public static String Date(String d) {
		String dateString = "";
		if (!d.equals("N-N-N")) {
			int[] date = intArray(d, "-");
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, date[0]);
			cal.set(Calendar.MONTH, date[1]);
			cal.set(Calendar.YEAR, date[2]);
			int W = cal.get(Calendar.DAY_OF_WEEK);

			Date dayDate = cal.getTime();
			Calendar Today = Calendar.getInstance();
			//Date today = Today.getTime();
			long diffInDays = daysBetween(Today, cal);

			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
			dateString = sdf.format(dayDate);

			// day number for today
			int weekNumber = 0;
			switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				weekNumber = 1;
				break;
			case Calendar.TUESDAY:
				weekNumber = 2;
				break;
			case Calendar.WEDNESDAY:
				weekNumber = 3;
				break;
			case Calendar.THURSDAY:
				weekNumber = 4;
				break;
			case Calendar.FRIDAY:
				weekNumber = 5;
				break;
			case Calendar.SATURDAY:
				weekNumber = 6;
				break;
			case Calendar.SUNDAY:
				weekNumber = 7;
				break;
			}
			Log.d("Diff in Days", Long.toString(diffInDays));
			Log.d("weekNumber", Integer.toString(weekNumber));
			Log.d("Check Against", Integer.toString(7 - weekNumber));
			// Week String
			String weekString = "";
			switch (W) {
			case Calendar.MONDAY:
				weekString = "Mon";
				break;
			case Calendar.TUESDAY:
				weekString = "Tue";
				break;
			case Calendar.WEDNESDAY:
				weekString = "Wed";
				break;
			case Calendar.THURSDAY:
				weekString = "Thu";
				break;
			case Calendar.FRIDAY:
				weekString = "Fri";
				break;
			case Calendar.SATURDAY:
				weekString = "Sat";
				break;
			case Calendar.SUNDAY:
				weekString = "Sun";
				break;
			}

			// check if its this week or not
			if (diffInDays != 1 && diffInDays <= 7 - weekNumber
					&& !(diffInDays < 0)) {
				if (diffInDays == 0) {
					dateString = "today";
				} else {
					dateString = weekString;
				}
			}

			if (diffInDays == 1) {
				dateString = "tmrw";
			}
		}
		return dateString;
	}

	public static Boolean isBeforeToday(String Date) {
		Boolean bool = false;
		if (!Date.equals("N-N-N")) {
			int[] date = intArray(Date, "-");
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, date[0]);
			cal.set(Calendar.MONTH, date[1]);
			cal.set(Calendar.YEAR, date[2]);
			if (cal.before(Calendar.getInstance())) {
				bool = true;
			}
		}
		return bool;
	}

	public static long daysBetween(Calendar sDate, Calendar eDate) {
		long daysBetween = 0;
		while (eDate.before(sDate)) {
			eDate.add(Calendar.DAY_OF_MONTH, 1);
			daysBetween = daysBetween - 1;
		}
		while (sDate.before(eDate)) {
			sDate.add(Calendar.DAY_OF_MONTH, 1);
			daysBetween++;
		}

		return daysBetween;
	}

	public static String time(String t) {
		String timeString = "";
		if (!t.equals("N:N")) {
			int[] time = intArray(t, ":");
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, time[0]);
			cal.set(Calendar.MINUTE, time[1]);
			Date timeDate = cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("h:mm");
			SimpleDateFormat ampm = new SimpleDateFormat("aa");
			String timeOfDay = ", " + sdf.format(timeDate);
			String am = ampm.format(timeDate).toLowerCase();
			if (Integer.parseInt(timeOfDay.split(":")[1]) == 0) {
				timeString = timeOfDay.split(":")[0] + " " + am;
			} else {
				timeString = timeOfDay + am;
			}
		}

		return timeString;

	}

	public static String DateandTimeFromData(String Date, String Time) {
		String s = "";
		s = Date(Date) + time(Time);
		return s;
	}

	public static String timeWithoutComma(String t) {
		/*
		 * String timeString=""; if (!t.equals("N:N")){ int [] time= intArray(t,
		 * ":"); Calendar cal = Calendar.getInstance();
		 * cal.set(Calendar.HOUR_OF_DAY, time[0]); cal.set(Calendar.MINUTE,
		 * time[1]); Date timeDate = cal.getTime(); SimpleDateFormat sdf = new
		 * SimpleDateFormat("h:mm"); timeString = sdf.format(timeDate); } return
		 * timeString;
		 */
		String timeString = time(t);
		timeString = timeString.replace(", ", "");
		return timeString;

	}

	public static int BooleanToInt(Boolean b) {
		int i = 0;
		if (b) {
			i = 1;

		}
		return i;
	}

	static public BitmapDrawable writeOnDrawable(int drawableId, String text,
			Context context, Typeface typeface) {
		Options options = new BitmapFactory.Options();
		options.inScaled = false;
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				drawableId, options).copy(Bitmap.Config.ARGB_8888, true);

		Paint paint = new Paint();
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setColor(Color.WHITE);
		paint.setTextSize(context.getResources().getDimension(
				R.dimen.subtask_text_size));
		paint.setTypeface(typeface);
		paint.setTextAlign(Align.CENTER);

		Canvas canvas = new Canvas(bm);
		canvas.drawText(text, 73 * bm.getWidth() / 100,
				96 * bm.getHeight() / 100, paint);

		return new BitmapDrawable(context.getResources(), bm);
	}

	/**
	 * Returns appropriate subtask icon according to subtask count
	 * 
	 * @param subtaskCount
	 * @param context
	 * @return subtask drawable
	 */

	public static Drawable getSubtaskDrawable(int subtaskCount, Context context) {
		Drawable subtaskIconDrawable;
		if (subtaskCount > 5) {
			subtaskIconDrawable = context.getResources().getDrawable(
					R.drawable.subtask_six);
		} else {
			switch (subtaskCount) {
			case 1:
				subtaskIconDrawable = context.getResources().getDrawable(
						R.drawable.subtask_one);
				break;
			case 2:
				subtaskIconDrawable = context.getResources().getDrawable(
						R.drawable.subtask_two);
				break;
			case 3:
				subtaskIconDrawable = context.getResources().getDrawable(
						R.drawable.subtask_three);
				break;
			case 4:
				subtaskIconDrawable = context.getResources().getDrawable(
						R.drawable.subtask_four);
				break;
			case 5:
				subtaskIconDrawable = context.getResources().getDrawable(
						R.drawable.subtask_five);
				break;

			default:
				subtaskIconDrawable = context.getResources().getDrawable(
						R.drawable.subtask);
				break;
			}
		}

		return subtaskIconDrawable;
	}

	public static ArrayList<String> stringArraytoArrayList(String[] s) {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (String string : s) {
			arrayList.add(string);
		}
		return arrayList;
	}

	// getting category name based on ID
	public static String getCategory(Context context, String ID) {
		DatabaseHandler handler = new DatabaseHandler(context);
		String category = handler.getCategoryFromID(Integer.parseInt(ID));
		return category;
	}

	// check if category value is 3
	public static boolean ifCategoryValueThree(String Category, Context context) {
		boolean r = false;
		DatabaseHandler handler = new DatabaseHandler(context);
		int Value = handler.getCategoryValue(Category);
		if (Value == 3) {
			r = true;
		}
		return r;
	}

	// save the category if it doesn't already exists
	public static void SaveCategory(String Category, Context context) {
		DatabaseHandler handler = new DatabaseHandler(context);
		if (!handler.checkIfCategoryExists(Category))
		// category doesn't exist
		{
			handler.insertCategory(Category, "2");
		}
	}
	public static void saveReceivedCategory(String Category, Context context) {
		DatabaseHandler handler = new DatabaseHandler(context);
		if (!handler.checkIfCategoryExists(Category))
		// category doesn't exist
		{
			handler.insertCategory(Category, "3");
		}
	}
	// get note
	public static String getNote(String reminderID, Context context) {
		DatabaseHandler handler = new DatabaseHandler(context);
		String note = handler.getNote(reminderID);
		return note;
	}

	public static ArrayList<Integer> dateAndTimeIntoArraylist(String date,
			String time) {
		ArrayList<Integer> dateAndTime = new ArrayList<Integer>(5);
		int[] dateArray = intArray(date, "-");
		int[] timeArray = intArray(time, ":");
		for (int i = 0; i < dateArray.length; i++) {
			dateAndTime.add(i, dateArray[i]);
		}
		for (int i = 0; i < timeArray.length; i++) {
			dateAndTime.add(i + 3, timeArray[i]);
		}
		return dateAndTime;
	}

	// cursor to actions
	public static Actions actionsFromCursor(Cursor cursor) {
		Actions a = new Actions();
		a.setActionID(cursor.getString(cursor.getColumnIndex("action_id")));
		a.setTaskID(cursor.getString(cursor.getColumnIndex("task_id")));
		a.setUserID(cursor.getString(cursor.getColumnIndex("user_id")));
		a.setActionType(cursor.getString(cursor.getColumnIndex("action_type")));
		a.setActionValue(cursor.getString(cursor.getColumnIndex("action_type")));
		a.setSynced(cursor.getInt(cursor.getColumnIndex("synced")));

		return a;
	}

	// check if the category name is predefined one
	public static Boolean isCategoryValueOne(String category, Context c) {
		Boolean r = false;
		DatabaseHandler handler = new DatabaseHandler(c);
		int Value = handler.getCategoryValue(category);
		Log.d("Value", Integer.toString(Value));
		if (Value == 1) {
			r = true;
		}
		return r;
	}

	// getarray of reminder as per the value of the title
	public static ArrayList<Task> getReminders(String what, Context c) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		ArrayList<Task> Activetasks = new ArrayList<Task>();
		DatabaseHandler handler = new DatabaseHandler(c);
		Activetasks = handler.getActiveTasks();
		ArrayList<Task> AssignedTasks = handler.getAssignedTasks();

		if (what.equals("Active tasks")) {
			tasks = Activetasks;
		} else if (what.equals("Today")) {
			for (Task t : Activetasks) {
				if (t.getDate().equals(
						calendarIntoDateString(Calendar.getInstance()))
						|| t.getDate().equals("N-N-N")) {
					tasks.add(t);
				}
			}
		} else if (what.equals("Assigned to someone")) {
			tasks = AssignedTasks;
		} else if (what.equals("Important")) {
			for (Task t : Activetasks) {
				if (t.getImportant() == 1) {
					tasks.add(t);
				}
			}
		}

		// this code needs to be changed
		else if (what.equals("Completed Tasks")) {
			tasks = handler.getTasks("complete", "1");
		}

		return tasks;
	}

	// get the count of various categories
	public static int getCountofCategory(String what, Context c) {
		int count = 0;
		if (isCategoryValueOne(what, c)) {
			count = getReminders(what, c).size();
		} else {
			count = getTaskOnCategory(what, c).size();
		}
		return count;
	}

	// get reminders on the basis of category
	public static ArrayList<Task> getTaskOnCategory(String category, Context c) {
		DatabaseHandler handler = new DatabaseHandler(c);
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks = handler.getArrayofReminders("category", category);
		/*
		 * tasks=handler.getArrayofReminders("category", category); for (Task t
		 * : tasks) { if (t.getComplete()==0 && (t.getAssigned()==1 ||
		 * t.getAssigned()==3)) { active.add(t); }
		 * 
		 * }
		 */
		return tasks;
	}

	// check if category exist in arraylist
	public static Boolean ifCategoryExists(ArrayList<HashMap<String, Object>> d, String category) {
		Boolean isThere = false;
		for (int i = 0; i < d.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map = d.get(i);
			String c = (String) map.get("categoryName");
			if (category.equalsIgnoreCase(c)) {
				isThere = true;
			}
		}
		return isThere;
	}

	
	
	// convert subtask arraylist into simple arraylist
	public static ArrayList<String> convertIntoSimpleArrayList(
			ArrayList<HashMap<String, String>> arrayList) {
		ArrayList<String> a = new ArrayList<String>();
		for (int i = 0; i < arrayList.size(); i++) {
			HashMap<String, String> map = arrayList.get(i);
			String subtask = map.get("Subtask");
			a.add(subtask);
		}
		return a;
	}

	public static boolean servicesConnected(Context context) {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Geofence Detection", "Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else if (resultCode == ConnectionResult.SERVICE_MISSING ||
		           resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
		           resultCode == ConnectionResult.SERVICE_DISABLED) {
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					resultCode, (Activity) context, CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(((FragmentActivity) context).getSupportFragmentManager(),
						"Geofence Detection");
			}
			return false;
		} else return false;
	}
	
	public static class ErrorDialogFragment extends SherlockDialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
	
	//getting task based on title on action bar
	public static ArrayList<Task> getTasks(String what, Context c) {
		ArrayList<Task> tasks=new ArrayList<Task>();
		
		//if its not a category
		if (isCategoryValueOne
				(what, c)) {
				tasks=getReminders(what, c);	
		}
		//if its a category
		else {
			tasks=getTaskOnCategory(what, c);
		}
		
		return tasks;
	}
	
	/**
	 * checks internet connection
	 * @param context
	 * @return true if there is valid internet connection
	 */
	public static boolean hasInternet(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            if (!info.isConnected()) {
                return false;
            }
            else return true;
        }
        else return false;
	}
	
	public static String convertStringArrayToString(String[] array){
	    String str = "";
	    for (int i = 0;i<array.length; i++) {
	        str = str+array[i];
	        // Do not append comma at the end of last element
	        if(i<array.length-1){
	            str = str+",";
	        }
	    }
	    return str;
	}
	
	public static String[] convertStringToStringArray(String str){
	    String[] arr = str.split(",");
	    return arr;
	}
	public static String generateLocationJson(Context context, String locationId, int transitionType) {
		DatabaseHandler dbHandler = new DatabaseHandler(context);
		Cursor cursor = dbHandler.getLocationCursorfromId(locationId);
		JSONObject locationJsonObject = new JSONObject();
		try {

			locationJsonObject.put(Task.LOCATION_TITLE, cursor.getString(cursor.getColumnIndex(DatabaseHandler.LOCATION_FEATURE_NAME))); //title
			locationJsonObject.put(Task.LOCATION_LATITUDE,cursor.getString(cursor.getColumnIndex(Task.LOCATION_LATITUDE)));
			locationJsonObject.put(Task.LOCATION_LONGITUDE,cursor.getString(cursor.getColumnIndex(Task.LOCATION_LONGITUDE)));
			locationJsonObject.put(Task.LOCATION_TRANSITION_TYPE, transitionType); //route
			locationJsonObject.put(Task.LOCATION_ADDRESS,cursor.getString(cursor.getColumnIndex(DatabaseHandler.LOCATION_ADDRESS))); //address
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return locationJsonObject.toString();
	}
	public static String getLocationName(Context context, double latitude, double longitude) {
		Geocoder geocoder = new Geocoder(context);
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String locationName = null;
		if(addresses!=null)
			locationName = addresses.get(0).getFeatureName();
		return locationName;
	}
	
	public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    
	public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
 
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;
             
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        } 
        return TYPE_NOT_CONNECTED;
    }

	public static int getRandomInteger(int aStart, int aEnd, Random aRandom){
	    if ( aStart > aEnd ) {
	      throw new IllegalArgumentException("Start cannot exceed End.");
	    }
	    //get the range, casting to long to avoid overflow problems
	    long range = (long)aEnd - (long)aStart + 1;
	    // compute a fraction of the range, 0 <= frac < range
	    long fraction = (long)(range * aRandom.nextDouble());
	    int randomNumber =  (int)(fraction + aStart);    
	    return randomNumber;
	  }
}
