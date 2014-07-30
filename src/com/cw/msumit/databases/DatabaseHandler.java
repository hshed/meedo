package com.cw.msumit.databases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cw.msumit.objects.Actions;
import com.cw.msumit.objects.Connection;
import com.cw.msumit.objects.People;
import com.cw.msumit.objects.SendReminder;
import com.cw.msumit.objects.Task;
import com.cw.msumit.services.ReceiveTaskClient;
import com.cw.msumit.utils.StaticFunctions;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "userFriendsList";

	// table names
	private static final String TABLE_CONNECTIONS = "connections";
	private static final String TABLE_AUTO = "autocontacts";
	private static final String TABLE_SYNC = "sync";
	private static final String TABLE_R_DETAILS = "tasks";
	private static final String TABLE_R_SUBTASKS = "subtasks";
	private static final String TABLE_R_CATEGORY = "taskCategory";
	private static final String TABLE_TASK_USER = "taskUsers";
	private static final String TABLE_TASK_ACTION = "taskActions";
	private static final String TABLE_LOCATION = "location";
	private static final String TABLE_PEOPLE = "people";
	private static final String TABLE_PENDING = "pendingStuff";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_USER_FRIEND_ID = "user_friend_id";
	private static final String KEY_USER_FRIEND_NAME = "user_friend_name";
	private static final String KEY_USER_EMAIL = "user_friend_emails";
	// private static final String KEY_USER_PHONE = "user_freind_phones";
	public static final String KEY_WIRES = "hasWires";

	// AutoContacts Table Column Names
	private static final String KEY_NAME = "contact_name";
	private static final String KEY_CONTACT_VALUE = "contact_value";
	private static final String KEY_VALUE_DECODE = "value_decode";

	// Sync Table Column names
	private static final String KEY_SYNC_TITLE = "sync_title";
	private static final String KEY_SYNC_VALUE = "sync_value";

	// Column names for table T_R_details
	private static final String REMINDER_ID = "reminder_id";
	private static final String UNIVERSAL_REMINDER_ID = "universal_id";
	private static final String TITLE = "title";
	private static final String DATE = "date";
	private static final String TIME = "time";
	private static final String LOCATION_ID = "location_id";
	private static final String LOCATION_JSON = "location_json";
	private static final String LOCATION_REMINDER = "locaction_reminder";
	private static final String IMPORTANT = "important";
	private static final String CATEGORY = "category";
	private static final String REPEAT = "repeat";
	private static final String CREATOR_ID = "creator_id";
	private static final String SUBTASK_COUNT = "subtask_count";
	private static final String TABLE_TASKS_SYNCED = "synced";
	private static final String NOTE = "note";
	private static final String COMPLETE = "complete";
	private static final String ASSIGNED = "assigned";// value 1= not assigned, 2=assigned, 3=shared
	//private static final String MEMBERS_IDS = "members_id";
	private static final String COMPLETED_ON = "completed_on";
	private static final String ORDER = "order_by";

	// Column names for table TABLE_R_SUBTASKS
	private static final String SUBTASK_ID = "subtask_id";
	private static final String SUBTASK_VALUE = "subtask_value";
	private static final String T_REMINDER_ID = "reminder_id";
	private static final String COMPLETED_SUBTASK = "subtask_completed";

	// column names for TABLE_R_CATEGORY
	private static final String CATEGORY_ID = "category_id";
	public static final String CATEGORY_NAME = "category_name";
	public static final String CATEGORY_VALUE = "category_value";

	// column names for TABLE_TASK_USER
	private static final String TABLE_TASK_USER_TASK_ID = "task_id";
	private static final String USER_ID = "userd_id";
	private static final String TABLE_TASK_USER_SYNCED = "synced";

	// column names for TABLE_TASK_ACTION
	private static final String ACTION_ROW_ID = "_id";
	public static final String ACTION_ID = "action_id";
	public static final String ACTION_UNIQUE_ID = "action_unique_id";
	public static final String TABLE_TASK_ACTION_TASK_ID = "task_id";
	public static final String TABLE_TASK_ACTION_USER_ID = "user_id";
	public static final String ACTION_USER_NAME = "user_name";
	public static final String ACTION_TYPE = "action_type";
	public static final String ACTION_VALUE = "action_value";
	public static final String TABLE_TASK_ACTION_SYNCED = "synced";
	public static final String ACTION_TIMESTAMP = "action_timestamp";

	// column names for TABLE_LOCATION
	private static final String LOCATION_COL_ID = "_id";
	/**
	 * latitude of the location
	 */
	public static final String LOCATION_LATITUDE = "latitude";
	/**
	 * longitude of the location
	 */
	public static final String LOCATION_LONGITUDE = "longitude";
	/**
	 * feature name of the location
	 */
	public static final String LOCATION_FEATURE_NAME = "featureName";
	/**
	 * complete address of the location
	 */
	public static final String LOCATION_ADDRESS = "address";
	public static final String LOCATION_ASSOCIATED_TASKS ="tasksOfLocation";
	
	public static final String PEOPLE_KEY_ID = "_id";
	public static final String PEOPLE_TASK_ID ="universal_id";
	public static final String PEOPLE_NAME = "people_name";
	public static final String PEOPLE_EMAIL = "people_email";
	public static final String PEOPLE_HASMEEDO = "hasMeedo";
	public static final String PEOPLE_CONTACT_ID = "contact_id";
	public static final String PEOPLE_ACCEPTED = "accepted";
	public static final String PEOPLE_SYNCED ="synced"; //0 or 1
	
	private static final String PENDING_ID = "_id";
	public static final String PENDING_TASK_ID = "universal_id";
	public static final String PENDING_TASK_JSON = "task_json";
	public static final String PENDING_PEOPLE_JSON = "people_json";
	/*
	 * private static final String PREVIOUS_REMINDER_ID =
	 * "previous_reminder_id"; private static final String NEW_REMINDER_ID =
	 * "new_reminder_id";
	 */

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Creating Table TABLE_CONNECTION Items are KEY_ID, KEY_USER_FRIEND_ID,
	 * KEY_USER_FRIEND_NAME
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONNECTION_TABLE = "CREATE TABLE " + TABLE_CONNECTIONS
				+ "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ KEY_USER_FRIEND_ID + " TEXT," + KEY_USER_FRIEND_NAME
				+ " TEXT NOT NULL, " + KEY_USER_EMAIL + " TEXT," + KEY_WIRES
				+ " INTEGER" + ")";
		String CREATE_AUTOCONTACTS_TABLE = "CREATE TABLE " + TABLE_AUTO + "("
				+ KEY_NAME + " TEXT NOT NULL," + KEY_CONTACT_VALUE
				+ " TEXT NOT NULL, " + KEY_VALUE_DECODE + " INTEGER" + ")";
		String CREATE_SYNC_TABLE = "CREATE TABLE " + TABLE_SYNC + "("

		+ KEY_SYNC_TITLE + " TEXT NOT NULL," + KEY_SYNC_VALUE + " INTEGER "
				+ ")";
		String CREATE_TABLE_R_DETAILS = "CREATE TABLE " + TABLE_R_DETAILS + "("

		+ REMINDER_ID + " TEXT PRIMARY KEY NOT NULL," 
				+ UNIVERSAL_REMINDER_ID + " TEXT NOT NULL," 
				+ TITLE + " TEXT NOT NULL, " 
				+ DATE + " TEXT, " 
				+ TIME + " TEXT, "
				+ LOCATION_ID + " TEXT, "
				+ LOCATION_JSON + " TEXT, " 
				+ LOCATION_REMINDER + ", "
				+ IMPORTANT + " INTEGER NOT NULL, " 
				+ CATEGORY + " TEXT NOT NULL, " 
				+ CREATOR_ID + " TEXT NOT NULL, "
				+ TABLE_TASKS_SYNCED + " INTEGER DEFAULT -1, " 
				+ SUBTASK_COUNT + " INTEGER, " 
				+ REPEAT + " TEXT, " + NOTE + " TEXT, "
				+ COMPLETE + " INTEGER, " + ASSIGNED + " INTEGER, "
				//+ MEMBERS_IDS + " TEXT, "
				+ COMPLETED_ON + " TEXT, " + ORDER + " TEXT)";

		String CREATE_TABLE_R_SUBTASKS = "CREATE TABLE " + TABLE_R_SUBTASKS
				+ "("

				+ SUBTASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ SUBTASK_VALUE + " TEXT NOT NULL, " + T_REMINDER_ID
				+ " TEXT NOT NULL, " + COMPLETED_SUBTASK
				+ " INTEGER DEFAULT 0 " + ")";

		String CREATE_TABLE_R_CATEGORY = "CREATE TABLE " + TABLE_R_CATEGORY
				+ "("

				+ CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ CATEGORY_NAME + " TEXT NOT NULL, " + CATEGORY_VALUE
				+ " TEXT NOT NULL" + ")";

		String CREATE_TABLE_TASK_USER = "CREATE TABLE " + TABLE_TASK_USER + "("
				+ TABLE_TASK_USER_TASK_ID + " TEXT NOT NULL," + USER_ID
				+ " TEXT NOT NULL, " + TABLE_TASK_USER_SYNCED
				+ " INTEGER DEFAULT 0 " + ")";
		String CREATE_TABLE_TASK_ACTION = "CREATE TABLE " + TABLE_TASK_ACTION
				+ "(" + ACTION_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ ACTION_ID + " TEXT NOT NULL,"
				+ ACTION_UNIQUE_ID + " TEXT NOT NULL, "
				+ TABLE_TASK_ACTION_TASK_ID + " TEXT NOT NULL, "
				+ TABLE_TASK_ACTION_USER_ID + " TEXT NOT NULL, "
				+ ACTION_USER_NAME+" TEXT NOT NULL, "
				+ACTION_TYPE + " TEXT NOT NULL, " + ACTION_VALUE + " TEXT NOT NULL, "
				+ TABLE_TASK_ACTION_SYNCED + " INTEGER DEFAULT 0, " + ACTION_TIMESTAMP + " INTEGER " + ")";

		// create location table
		String CREATE_TABLE_LOCATION = "CREATE TABLE " + TABLE_LOCATION + "("
				+ LOCATION_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ LOCATION_LATITUDE + " TEXT NOT NULL, " + LOCATION_LONGITUDE
				+ " TEXT NOT NULL, " + LOCATION_FEATURE_NAME
				+ " TEXT NOT NULL, " + LOCATION_ADDRESS + " TEXT, " + LOCATION_ASSOCIATED_TASKS + " TEXT " +")";

		String CREATE_TABLE_PEOPLE = "CREATE TABLE " + TABLE_PEOPLE + "("
				+ PEOPLE_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ PEOPLE_TASK_ID + " TEXT NOT NULL, " + PEOPLE_HASMEEDO
				+ " INTEGER DEFAULT 0, " + PEOPLE_CONTACT_ID
				+ " TEXT NOT NULL, " + PEOPLE_EMAIL + " TEXT NOT NULL, " 
				+ PEOPLE_NAME + " TEXT NOT NULL, "
				+ PEOPLE_ACCEPTED + " INTEGER DEFAULT 0, "
				+ PEOPLE_SYNCED + " INTEGER DEFAULT -1" + ")";
		//String CREATE_TABLE_PENDING_TASKS = "CREATE TABLE " + TABLE_PENDING_TASKS 
				//+ " AS SELECT * FROM " + TABLE_R_DETAILS;
		
		String CREATE_PENDING_TABLE = "CREATE TABLE " + TABLE_PENDING + "("
				+ PENDING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ PENDING_TASK_ID + " TEXT NOT NULL, "
				+ PENDING_TASK_JSON + " TEXT, " + PENDING_PEOPLE_JSON + " TEXT" + ")";
		
		db.execSQL(CREATE_CONNECTION_TABLE);
		db.execSQL(CREATE_AUTOCONTACTS_TABLE);
		db.execSQL(CREATE_SYNC_TABLE);
		db.execSQL(CREATE_TABLE_R_DETAILS);
		db.execSQL(CREATE_TABLE_R_SUBTASKS);
		db.execSQL(CREATE_TABLE_R_CATEGORY);
		db.execSQL(CREATE_TABLE_TASK_USER);
		db.execSQL(CREATE_TABLE_TASK_ACTION);
		db.execSQL(CREATE_TABLE_LOCATION);
		db.execSQL(CREATE_TABLE_PEOPLE);
		//db.execSQL(CREATE_TABLE_PENDING_TASKS);
		db.execSQL(CREATE_PENDING_TABLE);
	}

	/**
	 * Upgrading Database
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONNECTIONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYNC);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK_ACTION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_R_CATEGORY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_R_SUBTASKS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_R_DETAILS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK_USER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEOPLE);

		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations for TABLE_CONNECTIONS
	 */

	public// Adding new contact
	void addConnectionFromContacts(Connection friend) {
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(TABLE_CONNECTIONS,
				new String[] { KEY_USER_EMAIL }, KEY_USER_EMAIL + " = '"
						+ friend.getEmails() + "'", null, null, null, null);
		if (!(cursor.getCount() > 0)) {

			ContentValues values = new ContentValues();
			values.put(KEY_USER_FRIEND_NAME, friend.getName()); // Contact Name
			values.put(KEY_USER_EMAIL, friend.getEmails());
			values.put(KEY_USER_FRIEND_ID, friend.getUserID());
			// values.put(KEY_USER_PHONE, friend.getPhones());
			values.put(KEY_WIRES, 0);
			// Inserting Row
			db.insert(TABLE_CONNECTIONS, null, values);
		}
		cursor.close();
		db.close(); // Closing database connection
	}

	public void addConnectionFromFB(Connection friend) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_USER_FRIEND_NAME, friend.getName()); // Contact Name
		values.put(KEY_USER_FRIEND_ID, friend.getUserID());
		values.put(KEY_USER_EMAIL, friend.getEmails());

		values.put(KEY_WIRES, 1);

		db.insert(TABLE_CONNECTIONS, null, values);
		db.close();
	}

	// Getting Cursor pointed at first connections
	public Cursor getCursorOfConnection() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_CONNECTIONS, null, null, null, null,
				null, null);
		cursor.moveToFirst();
		return cursor;
	}

	public Cursor getAutoCompleteCursor(String args) {
		SQLiteDatabase db = this.getWritableDatabase();
		// Cursor result = db.query(TABLE_CONNECTIONS, [], selection,
		// selectionArgs, groupBy, having, orderBy)
		Cursor cursor = db.query(TABLE_CONNECTIONS, new String[] {
				"id" + " as _id", "user_friend_id", "user_friend_name",
				"user_friend_emails", "hasWires" }, "user_friend_name LIKE '%"
				+ args + "%'", null, null, null, null);
		cursor.moveToFirst();
		db.close();
		return cursor;
	}

	// Getting All Connections
	public List<Connection> getAllConnections() {
		List<Connection> contactList = new ArrayList<Connection>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONNECTIONS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Connection contact = new Connection();
				contact.setID(Integer.parseInt(cursor.getString(cursor
						.getColumnIndex(KEY_ID))));
				contact.setUserID(cursor.getString(cursor
						.getColumnIndex(KEY_USER_FRIEND_ID)));
				contact.setName(cursor.getString(cursor
						.getColumnIndex(KEY_USER_FRIEND_NAME)));
				contact.setEmail(cursor.getString(cursor
						.getColumnIndex(KEY_USER_EMAIL)));
				// contact.setPhones(cursor.getString(4));
				contact.setWires(Integer.parseInt(cursor.getString(cursor
						.getColumnIndex(KEY_WIRES))));
				// Adding contact to list
				contactList.add(contact);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		/*
		 * // Contacts Table Columns names private static final String KEY_ID =
		 * "id"; private static final String KEY_USER_FRIEND_ID =
		 * "user_friend_id"; private static final String KEY_USER_FRIEND_NAME =
		 * "user_friend_name"; private static final String KEY_USER_EMAIL =
		 * "user_friend_emails"; //private static final String KEY_USER_PHONE =
		 * "user_freind_phones"; public static final String KEY_WIRES =
		 * "hasWires";
		 */

		// return contact list
		return contactList;
	}

	public String[] getItems(String KEY_ITEM) {
		String selectQuery = "SELECT " + "*" + " FROM " + TABLE_CONNECTIONS;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		String[] items = new String[cursor.getCount()];
		int i = 0;
		if (cursor.moveToFirst()) {
			do {

				String item = cursor.getString(cursor.getColumnIndex(KEY_ITEM));
				items[i] = item;
				i++;

			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
		return items;
	}

	public int[] getWires(String KEY_ITEM) {
		String selectQuery = "SELECT " + "*" + " FROM " + TABLE_CONNECTIONS;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int[] items = new int[cursor.getCount()];
		int i = 0;
		if (cursor.moveToFirst()) {
			do {

				int item = cursor.getInt(3);
				items[i] = item;
				i++;

			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
		return items;
	}

	// Updating single contact
	public int updateConnection(Connection contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_USER_FRIEND_ID, contact.getUserID());
		values.put(KEY_USER_FRIEND_NAME, contact.getName());
		values.put(KEY_WIRES, 0);

		// updating row
		return db.update(TABLE_CONNECTIONS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(contact.getID()) });
	}

	// Deleting single contact
	public void deleteConnection(Connection contact) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONNECTIONS, KEY_ID + " = ?",
				new String[] { String.valueOf(contact.getID()) });
		db.close();
	}

	// Getting contacts Count
	public int getConnectionCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CONNECTIONS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}

	// checking if ID exists
	public Boolean IDExists(String id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONNECTIONS, new String[] { KEY_ID,
				KEY_USER_FRIEND_NAME }, KEY_USER_FRIEND_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor.getCount() > 0) // means contact already exists
		{
			cursor.close();
			db.close();
			return true;
		} else {
			cursor.close();
			db.close();

			return false;
		}
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations for TABLE_AUTO
	 */

	// adding a single contact
	public void addAutoContact(String Name, String contactValue, Integer ValueDecode) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, Name); // Contact Name
		values.put(KEY_CONTACT_VALUE, contactValue);
		values.put(KEY_VALUE_DECODE, ValueDecode);
		Log.d("Added Auto", "In Database");

		// Inserting Row
		db.insert(TABLE_AUTO, null, values);
		db.close(); // Closing database connection
	}

	public Boolean checkIfSubtaskExists(String subtask) {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_R_SUBTASKS + " WHERE "
				+ SUBTASK_VALUE + "= '" + subtask + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			// value does exists
			cursor.close();
			db.close();
			return true;

		} else {
			cursor.close();
			db.close();
			return false;
		}

	}

	public Boolean checkIfTaskExist(String reminderID) {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_R_DETAILS + " WHERE "
				+ REMINDER_ID + "= '" + reminderID + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			// value does exists
			cursor.close();
			db.close();
			return true;

		} else {
			cursor.close();
			db.close();
			return false;
		}

	}
	
	/**
	 * this checks task using universal id
	 * @param universalId
	 * @return
	 */
	public boolean checkifTaskExists(String universalId){
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_R_DETAILS + " WHERE "
				+ UNIVERSAL_REMINDER_ID + "= '" + universalId + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			// value does exists
			cursor.close();
			db.close();
			return true;

		} else {
			cursor.close();
			db.close();
			return false;
		}
	}
	public boolean checkInPending(String universalId){
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_PENDING, null, PENDING_TASK_ID + "='" + universalId + "'", null, null, null, null);
		if(cursor.getCount()>0) {
			cursor.close();
			db.close();
			return true;
		} else {
			cursor.close();
			db.close();
			return false;
		}
	}
	
	public boolean updatePendingValues(Context c, String universalId, String taskjson, ArrayList<People> peoples){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PENDING_TASK_JSON, taskjson);
		values.put(PENDING_PEOPLE_JSON,  SendReminder.generatePeopleJson(peoples));
		int r = db.update(TABLE_PENDING, values, PENDING_TASK_ID + "=?", new String[]{universalId});

		db.close();
		if(r!=0)
			return true;
		else return false;
	}
	/**
	 * deletes all pending tasks
	 * @deprecated
	 */
	public void deleteAllPending(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PENDING, null, null);
	}
	
	public void deletePendingTask(String universalId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PENDING, PENDING_TASK_ID + " = '" + universalId + "'", null);
		db.close();
	}
	public void addToPending(Context c, String universalId, String taskjson, ArrayList<People> peoples) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(PENDING_TASK_ID, universalId);
		values.put(PENDING_TASK_JSON, taskjson);
		values.put(PENDING_PEOPLE_JSON, SendReminder.generatePeopleJson(peoples));
		
		db.insert(TABLE_PENDING, null, values);
		db.close();
	}
	
	public ArrayList<Task> getPendingTasks() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_PENDING, new String[]{PENDING_ID, PENDING_TASK_JSON}, null, null, null, null, PENDING_ID + " DESC");
		ArrayList<Task> tasks = new ArrayList<Task>();
		if(cursor.moveToFirst()) {
			do {
				String taskjson = cursor.getString(cursor.getColumnIndex(PENDING_TASK_JSON));
				if(taskjson!=null){
					tasks.addAll(ReceiveTaskClient.generateTaskObjectList(taskjson));
				}
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return tasks;
	}
	
	public ArrayList<People> getPendingPeoples() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_PENDING, new String[]{PENDING_PEOPLE_JSON}, null, null, null, null, null);
		ArrayList<People> peoples = new ArrayList<People>();
		if(cursor.moveToFirst()) {
			do {
				String peoplejson = cursor.getString(cursor.getColumnIndex(PENDING_PEOPLE_JSON));
				if(peoplejson!=null){
					peoples.addAll(ReceiveTaskClient.generatePeopleObjectList(peoplejson));
				}
				
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return peoples;
	}

	public Boolean checkIfValuesExists(String Name, String contactValue,
			Integer ValueDecode) {
		SQLiteDatabase db = this.getWritableDatabase();
		Name = DatabaseUtils.sqlEscapeString(Name);
		contactValue = DatabaseUtils.sqlEscapeString(contactValue);

		Cursor cursor = db.query(TABLE_AUTO, new String[] { KEY_NAME },
				KEY_NAME + "= " + Name + " AND " + KEY_CONTACT_VALUE + "= "
						+ contactValue + " AND " + KEY_VALUE_DECODE + "= "
						+ ValueDecode, null, null, null, null);
		/*
		 * String sql = "SELECT * FROM " + TABLE_AUTO + " WHERE " + KEY_NAME +
		 * "= '" + Name + "' AND " + KEY_CONTACT_VALUE + "= '" + contactValue +
		 * "' AND " + KEY_VALUE_DECODE + "= '" + ValueDecode + "'";
		 */

		// Cursor cursor = db.rawQuery(sql, null);
		// DatabaseUtils.sqlEscapeString("'");

		if (cursor.getCount() > 0) {
			// value does exists
			cursor.close();
			db.close();
			return true;

		} else {
			cursor.close();
			db.close();
			return false;
		}

	}

	public String[] getAutoNames(String KEY_ITEM) {
		String selectQuery = "SELECT " + "*" + " FROM " + TABLE_AUTO;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		String[] items = new String[cursor.getCount()];
		int i = 0;
		if (cursor.moveToFirst()) {
			do {

				String item = cursor.getString(cursor.getColumnIndex(KEY_ITEM));
				items[i] = item;
				i++;

			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
		return items;
	}

	public int[] getAutoWires(String KEY_ITEM) {
		String selectQuery = "SELECT " + "*" + " FROM " + TABLE_CONNECTIONS;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int[] items = new int[cursor.getCount()];
		int i = 0;
		if (cursor.moveToFirst()) {
			do {

				int item = cursor.getInt(3);
				items[i] = item;
				i++;

			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
		return items;
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations for TABLE_R_CATEGORY
	 */

	// insert single row

	public void insertCategory(String Category, String Value) {
		// value=1 : for precreated
		// value=2 : for user added
		// value=3 : for other people
		// Log.d("Category" ,"Being Inserted");
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_R_CATEGORY,
				new String[] { CATEGORY_NAME }, "LOWER(" + CATEGORY_NAME
						+ ") = LOWER('" + Category + "')", null, null, null,
				null);
		if (!(cursor.getCount() > 0)) {
			ContentValues values = new ContentValues();
			values.put(CATEGORY_NAME, Category);
			values.put(CATEGORY_VALUE, Value);
			db.insert(TABLE_R_CATEGORY, null, values);
		}
		cursor.close();
		db.close();

	}

	public void updateCategory(String Category, int CategoryID) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(CATEGORY_NAME, Category);
		String whereClause = CATEGORY_ID + "= '" + CategoryID + "'";
		db.update(TABLE_R_CATEGORY, values, whereClause, null);
		db.close();

	}

	// updates the category based on its position in the arraylist in adapter
	public void updateCategoryOnPosition(String Category, int pos) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_R_CATEGORY, null, CATEGORY_VALUE
				+ " = 2 OR " + CATEGORY_VALUE + " = 1", null, null, null, null);
		int ID = -1;
		if (cursor.moveToPosition(pos)) {
			ID = cursor.getInt(cursor.getColumnIndex(CATEGORY_ID));
			Log.d("position and id", pos + " " + ID);
		}
		updateCategory(Category, ID);
	}

	public void deleteCategory(int ID) {
		SQLiteDatabase db = this.getWritableDatabase();
		String whereClause = CATEGORY_ID + "= '" + ID + "'";
		db.delete(TABLE_R_CATEGORY, whereClause, null);
		db.close();
	}
public void deleteAllCategory() {
	SQLiteDatabase db = this.getWritableDatabase();
	db.delete(TABLE_R_CATEGORY, null, null);
	db.close();
}
	public void deleteCategoryOnPosition(int pos) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_R_CATEGORY, null, CATEGORY_VALUE
				+ " = 2 OR " + CATEGORY_VALUE + " = 1", null, null, null, null);
		int ID = -1;
		if (cursor.moveToPosition(pos)) {

			ID = cursor.getInt(cursor.getColumnIndex(CATEGORY_ID));
			Log.d("position and id", pos + " " + ID);
		}
		deleteCategory(ID);
	}

	// check if category is of value 1 or other
	/*
	 * public Boolean checkifCategoryPredefined (String Category) {
	 * SQLiteDatabase db = this.getReadableDatabase(); Cursor
	 * cursor=db.query(TABLE_R_CATEGORY, new String [] {CATEGORY_NAME,
	 * CATEGORY_VALUE}, CATEGORY_NAME + "=? AND " + CATEGORY_VALUE + "=?", new
	 * String [] {Category, "1"},null, null, null); Boolean isThere= false; if
	 * (cursor.getCount()>0) { isThere=true; } cursor.close(); db.close();
	 * return isThere; }
	 */
	public boolean ifCategoryExists(String category) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_R_CATEGORY, null, CATEGORY_NAME + "= '"
				+ category + "'", null, null, null, null);
		if (cursor.moveToNext()) {
			return true;
		} else {
			return false;
		}
	}

	// insert array of category
	public void insertArrayOfCategory(String[] array, String value) {
		for (int i = 0; i < array.length; i++) {
			insertCategory(array[i], value);
		}
	}

	// check if task exists
	public Boolean checkIfCategoryExists(String categoryName) {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_R_CATEGORY + " WHERE "
				+ CATEGORY_NAME + "= '" + categoryName + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			// value does exists
			cursor.close();
			db.close();
			return true;

		} else {
			cursor.close();
			db.close();
			return false;
		}
	}

	// get category value
	public int getCategoryValue(String Category) {

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_R_CATEGORY,
				new String[] { CATEGORY_VALUE }, CATEGORY_NAME + " = '"
						+ Category + "'", null, null, null, null);
		int Value = 0;
		if (cursor.moveToFirst()) {
			Value = cursor.getInt(cursor.getColumnIndex(CATEGORY_VALUE));
		}
		cursor.close();
		db.close();
		return Value;
	}

	// return array of categories
	public String[] categoryArray() {
		String selectQuery = "SELECT " + "*" + " FROM " + TABLE_R_CATEGORY
				+ " WHERE " + CATEGORY_VALUE + " = 1 OR " + CATEGORY_VALUE
				+ " = 2 ";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		String[] items = new String[cursor.getCount()];
		int i = 0;
		if (cursor.moveToFirst()) {
			do {

				String item = cursor.getString(cursor
						.getColumnIndex(CATEGORY_NAME));
				items[i] = item;
				i++;

			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
		return items;
	}

	// get category array based on value
	public String[] categoryArrayonValue(String value) {
		String selectQuery = "SELECT " + "*" + " FROM " + TABLE_R_CATEGORY
				+ " WHERE " + CATEGORY_VALUE + "= '" + value + "'";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		String[] items = new String[cursor.getCount()];
		int i = 0;
		if (cursor.moveToFirst()) {
			do {

				String item = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME));
				items[i] = item;
				i++;

			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
		return items;
	}

	public Cursor getCategoryCursor() {
		/*
		 * String selectQuery = "SELECT " + "*" + " FROM " + TABLE_R_CATEGORY +
		 * " WHERE " + CATEGORY_VALUE + " = 1 OR " + CATEGORY_VALUE + " = 2 " ;
		 */
		SQLiteDatabase db = this.getWritableDatabase();
		// Cursor cursor = db.rawQuery(selectQuery, null);
		Cursor cursor = db.query(TABLE_R_CATEGORY, new String[] {
				CATEGORY_ID + " as _id", CATEGORY_NAME, CATEGORY_VALUE },
				CATEGORY_VALUE + "= 1 OR " + CATEGORY_VALUE + " =2 ", null,
				null, null, null);
		cursor.moveToFirst();
		db.close();
		return cursor;
	}

	// get category on ID
	public String getCategoryFromID(int id) {
		String selectQuery = "SELECT " + "*" + " FROM " + TABLE_R_CATEGORY
				+ " WHERE " + CATEGORY_ID + "= '" + id + "'";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			String category = cursor.getString(cursor
					.getColumnIndex(CATEGORY_NAME));
			db.close();
			cursor.close();
			return category;
		} else {
			return "";
		}
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations for TABLE_SYNC
	 */

	// adding single row
	void addSyncRow(String SyncTitle, Integer SyncValue) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_SYNC_TITLE, SyncTitle); // Contact Name
		values.put(KEY_SYNC_VALUE, SyncValue);

		Log.d("Added Sync Value", "In Database");

		// Inserting Row
		db.insert(TABLE_SYNC, null, values);
		db.close(); // Closing database connection
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations for TABLE_REMINDERS
	 */
	// adding single row
	public void addReminders(Task reminders) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(REMINDER_ID, reminders.ReminderID);
		values.put(UNIVERSAL_REMINDER_ID, reminders.UniversalID);
		values.put(TITLE, reminders.Title);
		values.put(DATE, reminders.Date);
		values.put(TIME, reminders.Time);
		values.put(LOCATION_ID, reminders.LocationID);
		values.put(LOCATION_JSON, reminders.LocationID);
		values.put(LOCATION_REMINDER, reminders.LocationReminder);
		values.put(IMPORTANT, reminders.Important);
		values.put(CATEGORY, reminders.Category);
		values.put(REPEAT, reminders.Repeat);
		values.put(CREATOR_ID, reminders.creatorID);
		values.put(TABLE_TASKS_SYNCED, reminders.Synced);
		values.put(SUBTASK_COUNT, reminders.Subtasks);
		values.put(NOTE, reminders.Note);
		values.put(COMPLETE, reminders.Complete);
		values.put(ASSIGNED, reminders.Assigned);
		values.put(LOCATION_JSON, reminders.locationJson);
		//values.put(MEMBERS_IDS, reminders.Members);
		values.put(COMPLETED_ON, reminders.completedOn);
		values.put(ORDER, reminders.orderBy);

		db.insert(TABLE_R_DETAILS, null, values);

		db.close();
	}

	public String[] getStringItems(String KEY_ITEM, String Table) {
		String selectQuery = "SELECT " + "*" + " FROM " + Table;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		String[] items = new String[cursor.getCount()];
		int i = 0;
		if (cursor.moveToFirst()) {
			do {

				String item = cursor.getString(cursor.getColumnIndex(KEY_ITEM));
				items[i] = item;
				i++;

			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
		return items;
	}

	// get note
	public String getNote(String ReminderID) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db
				.query(TABLE_R_DETAILS, new String[] { NOTE }, REMINDER_ID
						+ " = '" + ReminderID + "'", null, null, null, null);
		String note = "";
		if (cursor.moveToFirst()) {
			note = cursor.getString(cursor.getColumnIndex(NOTE));
		}
		db.close();
		cursor.close();
		return note;
	}

	public String getRepeat(String reminderID) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_R_DETAILS, new String[] { REPEAT }, REMINDER_ID
						+ " = '" + reminderID + "'", null, null, null, null);
		String repeat = "N";
		if (cursor.moveToFirst()) {
			repeat = cursor.getString(cursor.getColumnIndex(REPEAT));
		}
		db.close();
		cursor.close();
		return repeat;
	}
	
	public int[] getIntegerItems(String KEY_ITEM, String Table) {
		String selectQuery = "SELECT " + "*" + " FROM " + Table;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int[] items = new int[cursor.getCount()];
		int i = 0;
		if (cursor.moveToFirst()) {
			do {

				int item = cursor.getInt(cursor.getColumnIndex(KEY_ITEM));
				items[i] = item;
				i++;

			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
		return items;
	}

	// get the highest reminder ID
	public int getHighestReminderID() {
		// String sql;
		int ReminderID = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		String orderBy = "ABS(" + DatabaseHandler.REMINDER_ID + ")";
		Cursor cursor = db.query(TABLE_R_DETAILS, new String[] { REMINDER_ID },
				null, null, null, null, orderBy, null);
		if (cursor.moveToLast()) {
			ReminderID = cursor.getInt(cursor.getColumnIndex(REMINDER_ID));
		}

		db.close();
		cursor.close();
		return ReminderID;
	}

	public ArrayList<Task> getTasks(String ColumnName, String ColumnValue) {
		SQLiteDatabase db = this.getWritableDatabase();
		String orderBy = "ABS(" + DatabaseHandler.ORDER + ")";
		Cursor cursor = db.query(TABLE_R_DETAILS, null, ColumnName + " = '"
				+ ColumnValue + "'", null, null, null, orderBy);
		ArrayList<Task> d = new ArrayList<Task>();

		if (cursor.moveToFirst()) {
			do {
				// add the dataset to arraylist of reminders
				Task reminders = new Task();

				reminders.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
				reminders.setCreatorID(cursor.getString(cursor.getColumnIndex(CREATOR_ID)));
				reminders.setImportant(cursor.getInt(cursor.getColumnIndex(IMPORTANT)));
				reminders.setReminderID(cursor.getString(cursor.getColumnIndex(REMINDER_ID)));
				reminders.setUniversalID(cursor.getString(cursor.getColumnIndex(UNIVERSAL_REMINDER_ID)));
				reminders.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
				reminders.setTime(cursor.getString(cursor.getColumnIndex(TIME)));
				reminders.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
				reminders.setSubtasks(cursor.getInt(cursor.getColumnIndex(SUBTASK_COUNT)));
				reminders.setNote(cursor.getString(cursor.getColumnIndex(NOTE)));
				reminders.setSynced(cursor.getInt(cursor.getColumnIndex(TABLE_TASKS_SYNCED)));
				reminders.setComplete(cursor.getInt(cursor.getColumnIndex(COMPLETE)));
				reminders.setAssigned(cursor.getInt(cursor.getColumnIndex(ASSIGNED)));
				reminders.setCompletedOn(cursor.getString(cursor.getColumnIndex(COMPLETED_ON)));
				reminders.setOrderBy(cursor.getString(cursor.getColumnIndex(ORDER)));
				reminders.setRepeat(cursor.getString(cursor.getColumnIndex(REPEAT)));
				reminders.setLocationJson(cursor.getString(cursor.getColumnIndex(LOCATION_JSON)));
				reminders.setLocationID(cursor.getString(cursor.getColumnIndex(LOCATION_ID)));

				d.add(reminders); // reminders added to the arraylist

			}

			while (cursor.moveToNext());

		}
		db.close();
		cursor.close();
		return d;
	}

	// get array of tasks on the basis of a column value
	public ArrayList<Task> getArrayofReminders(String ColumnName,
			String ColumnValue) {
		SQLiteDatabase db = this.getWritableDatabase();
		String orderBy = "ABS(" + DatabaseHandler.ORDER + ")";
		Cursor cursor = db.query(TABLE_R_DETAILS, null, COMPLETE + " = 0 AND ("
				+ ASSIGNED + " = 1 OR " + ASSIGNED + " = 3) AND " + ColumnName
				+ " = '" + ColumnValue + "'", null, null, null, orderBy);
		ArrayList<Task> d = new ArrayList<Task>();

		if (cursor.moveToFirst()) {
			do {
				// add the dataset to arraylist of reminders
				Task reminders = new Task();

				reminders.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
				reminders.setCreatorID(cursor.getString(cursor.getColumnIndex(CREATOR_ID)));
				reminders.setImportant(cursor.getInt(cursor.getColumnIndex(IMPORTANT)));
				reminders.setReminderID(cursor.getString(cursor.getColumnIndex(REMINDER_ID)));
				reminders.setUniversalID(cursor.getString(cursor.getColumnIndex(UNIVERSAL_REMINDER_ID)));
				reminders.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
				reminders.setTime(cursor.getString(cursor.getColumnIndex(TIME)));
				reminders.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
				reminders.setSubtasks(cursor.getInt(cursor.getColumnIndex(SUBTASK_COUNT)));
				reminders.setNote(cursor.getString(cursor.getColumnIndex(NOTE)));
				reminders.setSynced(cursor.getInt(cursor.getColumnIndex(TABLE_TASKS_SYNCED)));
				reminders.setComplete(cursor.getInt(cursor.getColumnIndex(COMPLETE)));
				reminders.setAssigned(cursor.getInt(cursor.getColumnIndex(ASSIGNED)));
				reminders.setCompletedOn(cursor.getString(cursor.getColumnIndex(COMPLETED_ON)));
				reminders.setOrderBy(cursor.getString(cursor.getColumnIndex(ORDER)));
				reminders.setRepeat(cursor.getString(cursor.getColumnIndex(REPEAT)));
				reminders.setLocationJson(cursor.getString(cursor.getColumnIndex(LOCATION_JSON)));
				reminders.setLocationID(cursor.getString(cursor.getColumnIndex(LOCATION_ID)));

				d.add(reminders); // reminders added to the arraylist

			}

			while (cursor.moveToNext());

		}
		db.close();
		cursor.close();
		return d;

	}

	// get Active tasks
	public ArrayList<Task> getActiveTasks() {
		SQLiteDatabase db = this.getWritableDatabase();
		ArrayList<Task> d = new ArrayList<Task>();
		String orderBy = "ABS(" + DatabaseHandler.ORDER + ")";
		Cursor cursor = db.query(TABLE_R_DETAILS, null, COMPLETE + " = 0 AND ("
				+ ASSIGNED + " = 1 OR " + ASSIGNED + " = 3)", null, null, null,
				orderBy);
		if (cursor.moveToFirst()) {
			do {
				// add the dataset to arraylist of reminders
				Task reminders = new Task();
			
				reminders.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
				reminders.setCreatorID(cursor.getString(cursor.getColumnIndex(CREATOR_ID)));
				reminders.setImportant(cursor.getInt(cursor.getColumnIndex(IMPORTANT)));
				reminders.setReminderID(cursor.getString(cursor.getColumnIndex(REMINDER_ID)));
				reminders.setUniversalID(cursor.getString(cursor.getColumnIndex(UNIVERSAL_REMINDER_ID)));
				reminders.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
				reminders.setTime(cursor.getString(cursor.getColumnIndex(TIME)));
				reminders.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
				reminders.setSubtasks(cursor.getInt(cursor.getColumnIndex(SUBTASK_COUNT)));
				reminders.setNote(cursor.getString(cursor.getColumnIndex(NOTE)));
				reminders.setSynced(cursor.getInt(cursor.getColumnIndex(TABLE_TASKS_SYNCED)));
				reminders.setComplete(cursor.getInt(cursor.getColumnIndex(COMPLETE)));
				reminders.setAssigned(cursor.getInt(cursor.getColumnIndex(ASSIGNED)));
				reminders.setCompletedOn(cursor.getString(cursor.getColumnIndex(COMPLETED_ON)));
				reminders.setOrderBy(cursor.getString(cursor.getColumnIndex(ORDER)));
				reminders.setRepeat(cursor.getString(cursor.getColumnIndex(REPEAT)));
				reminders.setLocationJson(cursor.getString(cursor.getColumnIndex(LOCATION_JSON)));
				reminders.setLocationID(cursor.getString(cursor.getColumnIndex(LOCATION_ID)));

				d.add(reminders); // reminders added to the arraylist

			}

			while (cursor.moveToNext());

		}
		db.close();
		cursor.close();
		return d;
	}

	public ArrayList<Task> getAssignedTasks(){
		SQLiteDatabase db = this.getWritableDatabase();
		ArrayList<Task> d = new ArrayList<Task>();
		String orderBy = "ABS(" + DatabaseHandler.ORDER + ")";
		Cursor cursor = db.query(TABLE_R_DETAILS, null, COMPLETE + " = 0 AND "
				+ ASSIGNED + " = 2 OR " + ASSIGNED + " = 3", null, null, null,
				orderBy);
		if (cursor.moveToFirst()) {
			do {
				// add the dataset to arraylist of reminders
				Task reminders = new Task();
			
				reminders.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
				reminders.setCreatorID(cursor.getString(cursor.getColumnIndex(CREATOR_ID)));
				reminders.setImportant(cursor.getInt(cursor.getColumnIndex(IMPORTANT)));
				reminders.setReminderID(cursor.getString(cursor.getColumnIndex(REMINDER_ID)));
				reminders.setUniversalID(cursor.getString(cursor.getColumnIndex(UNIVERSAL_REMINDER_ID)));
				reminders.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
				reminders.setTime(cursor.getString(cursor.getColumnIndex(TIME)));
				reminders.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
				reminders.setSubtasks(cursor.getInt(cursor.getColumnIndex(SUBTASK_COUNT)));
				reminders.setNote(cursor.getString(cursor.getColumnIndex(NOTE)));
				reminders.setSynced(cursor.getInt(cursor.getColumnIndex(TABLE_TASKS_SYNCED)));
				reminders.setComplete(cursor.getInt(cursor.getColumnIndex(COMPLETE)));
				reminders.setAssigned(cursor.getInt(cursor.getColumnIndex(ASSIGNED)));
				reminders.setCompletedOn(cursor.getString(cursor.getColumnIndex(COMPLETED_ON)));
				reminders.setOrderBy(cursor.getString(cursor.getColumnIndex(ORDER)));
				reminders.setRepeat(cursor.getString(cursor.getColumnIndex(REPEAT)));
				reminders.setLocationJson(cursor.getString(cursor.getColumnIndex(LOCATION_JSON)));
				reminders.setLocationID(cursor.getString(cursor.getColumnIndex(LOCATION_ID)));

				d.add(reminders); // reminders added to the arraylist

			}

			while (cursor.moveToNext());

		}
		db.close();
		cursor.close();
		return d;
	}
	public ArrayList<Task> getTaskswithRepeat() {
		SQLiteDatabase db = this.getWritableDatabase();
		ArrayList<Task> d = new ArrayList<Task>();
		String orderBy = "ABS(" + DatabaseHandler.ORDER + ")";
		Cursor cursor = db.query(TABLE_R_DETAILS, null, COMPLETE + " = 0 AND ("
				+ ASSIGNED + " = 1 OR " + ASSIGNED + " = 3)" + " AND "+ REPEAT + " != 'N'", null, null, null,
				orderBy);
		if (cursor.moveToFirst()) {
			do {
				// add the dataset to arraylist of reminders
				Task reminders = new Task();
				reminders.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
				reminders.setCreatorID(cursor.getString(cursor.getColumnIndex(CREATOR_ID)));
				reminders.setImportant(cursor.getInt(cursor.getColumnIndex(IMPORTANT)));
				reminders.setReminderID(cursor.getString(cursor.getColumnIndex(REMINDER_ID)));
				reminders.setUniversalID(cursor.getString(cursor.getColumnIndex(UNIVERSAL_REMINDER_ID)));
				reminders.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
				reminders.setTime(cursor.getString(cursor.getColumnIndex(TIME)));
				reminders.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
				reminders.setSubtasks(cursor.getInt(cursor.getColumnIndex(SUBTASK_COUNT)));
				reminders.setNote(cursor.getString(cursor.getColumnIndex(NOTE)));
				reminders.setSynced(cursor.getInt(cursor.getColumnIndex(TABLE_TASKS_SYNCED)));
				reminders.setComplete(cursor.getInt(cursor.getColumnIndex(COMPLETE)));
				reminders.setAssigned(cursor.getInt(cursor.getColumnIndex(ASSIGNED)));
				reminders.setCompletedOn(cursor.getString(cursor.getColumnIndex(COMPLETED_ON)));
				reminders.setOrderBy(cursor.getString(cursor.getColumnIndex(ORDER)));
				reminders.setRepeat(cursor.getString(cursor.getColumnIndex(REPEAT)));
				reminders.setLocationJson(cursor.getString(cursor.getColumnIndex(LOCATION_JSON)));
				reminders.setLocationID(cursor.getString(cursor.getColumnIndex(LOCATION_ID)));

				d.add(reminders); // reminders added to the arraylist

			}

			while (cursor.moveToNext());

		}
		db.close();
		cursor.close();
		return d;
	}

	// update subtask count
	public void updateSubtaskCount(String reminderID, int newCount) {
		String sql = "UPDATE " + TABLE_R_DETAILS + " SET " + SUBTASK_COUNT
				+ " = " + newCount + " WHERE " + REMINDER_ID + " = "
				+ reminderID;
		SQLiteDatabase db = this.getWritableDatabase();
		db.rawQuery(sql, null).moveToFirst();
		db.close();
	}

	// update a column-String
	public void updateAColumn(String ReminderID, String ColumnName,
			String ColumnValue) {
		// String sql= "UPDATE " +TABLE_R_DETAILS + " SET " + ColumnName + " = "
		// + ColumnValue
		// + " WHERE " + REMINDER_ID + " = " + ReminderID;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ColumnName, ColumnValue);
		db.update(TABLE_R_DETAILS, values, REMINDER_ID + " = " + ReminderID,
				null);
		db.close();
	}

	// update a column-Int
	public void updateAColumn(String ReminderID, String ColumnName,
			int ColumnValue) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ColumnName, ColumnValue);
		db.update(TABLE_R_DETAILS, values, REMINDER_ID + " = " + ReminderID,
				null);
		db.close();
	}

	// get a single task from its id
	public Task getTask(String reminderID) {

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_R_DETAILS, null, REMINDER_ID + " = '"
				+ reminderID + "'", null, null, null, null);
		Task reminders = new Task();
		if (cursor.moveToFirst()) {
			reminders.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
			reminders.setImportant(cursor.getInt(cursor.getColumnIndex(IMPORTANT)));
			reminders.setCreatorID(cursor.getString(cursor.getColumnIndex(CREATOR_ID)));
			reminders.setReminderID(cursor.getString(cursor.getColumnIndex(REMINDER_ID)));
			reminders.setUniversalID(cursor.getString(cursor.getColumnIndex(UNIVERSAL_REMINDER_ID)));
			reminders.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
			reminders.setTime(cursor.getString(cursor.getColumnIndex(TIME)));
			reminders.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
			reminders.setSubtasks(cursor.getInt(cursor.getColumnIndex(SUBTASK_COUNT)));
			reminders.setNote(cursor.getString(cursor.getColumnIndex(NOTE)));
			reminders.setSynced(cursor.getInt(cursor.getColumnIndex(TABLE_TASKS_SYNCED)));
			reminders.setComplete(cursor.getInt(cursor.getColumnIndex(COMPLETE)));
			reminders.setAssigned(cursor.getInt(cursor.getColumnIndex(ASSIGNED)));
			reminders.setCompletedOn(cursor.getString(cursor.getColumnIndex(COMPLETED_ON)));
			reminders.setOrderBy(cursor.getString(cursor.getColumnIndex(ORDER)));
			reminders.setRepeat(cursor.getString(cursor.getColumnIndex(REPEAT)));
			reminders.setLocationJson(cursor.getString(cursor.getColumnIndex(LOCATION_JSON)));
			reminders.setLocationID(cursor.getString(cursor.getColumnIndex(LOCATION_ID)));

		}
		db.close();
		cursor.close();
		return reminders;

		/*
		 * ArrayList<Task> tasks=getArrayofReminders("reminder_id", reminderID);
		 * return tasks.get(0);
		 */
	}
	/**
	 * returns arraylist of tasks with synced column equal to <b>syncValue</b>
	 * @param syncValue
	 * @param universal_id
	 * @return arraylist of tasks
	 */
	public ArrayList<Task> getTasksWithSyncValue(int syncValue) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_R_DETAILS, null, TABLE_TASKS_SYNCED + " = '"
				+ syncValue + "'", null, null, null, null);
		ArrayList<Task> taskList = new ArrayList<Task>();
		if (cursor.moveToFirst()) {
			do {
				Task task = new Task();
				task.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
				task.setImportant(cursor.getInt(cursor.getColumnIndex(IMPORTANT)));
				task.setCreatorID(cursor.getString(cursor.getColumnIndex(CREATOR_ID)));
				task.setReminderID(cursor.getString(cursor.getColumnIndex(REMINDER_ID)));
				task.setUniversalID(cursor.getString(cursor.getColumnIndex(UNIVERSAL_REMINDER_ID)));
				task.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
				task.setTime(cursor.getString(cursor.getColumnIndex(TIME)));
				task.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
				task.setSubtasks(cursor.getInt(cursor.getColumnIndex(SUBTASK_COUNT)));
				task.setNote(cursor.getString(cursor.getColumnIndex(NOTE)));
				task.setSynced(cursor.getInt(cursor.getColumnIndex(TABLE_TASKS_SYNCED)));
				task.setComplete(cursor.getInt(cursor.getColumnIndex(COMPLETE)));
				task.setAssigned(cursor.getInt(cursor.getColumnIndex(ASSIGNED)));
				task.setCompletedOn(cursor.getString(cursor.getColumnIndex(COMPLETED_ON)));
				task.setOrderBy(cursor.getString(cursor.getColumnIndex(ORDER)));
				task.setRepeat(cursor.getString(cursor.getColumnIndex(REPEAT)));
				task.setLocationJson(cursor.getString(cursor.getColumnIndex(LOCATION_JSON)));
				task.setLocationID(cursor.getString(cursor.getColumnIndex(LOCATION_ID)));
				
				taskList.add(task);
			} while (cursor.moveToNext());
			

		}
		db.close();
		cursor.close();
		return taskList;
	}

	// delete a single row
	public void deleteTask(Task task) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_R_DETAILS, REMINDER_ID + " = '" + task.getReminderID() + "'", null);
		db.delete(TABLE_PEOPLE, UNIVERSAL_REMINDER_ID +" = '"+ task.getUniversalID()+ "'",null);
		db.close();
	}
	

	public void deleteTaskOnCategory(String Category) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_R_DETAILS, CATEGORY + " = '" + Category + "'", null);
		db.close();
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations for TABLE_ACTIONS
	 */

	// insert single row
	public void addActions(Actions action) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		//String value = DatabaseUtils.sqlEscapeString(action.getActionValue());
		values.put(ACTION_ID, action.getActionID());
		values.put(ACTION_UNIQUE_ID, action.getUniqueId());
		values.put(TABLE_TASK_ACTION_TASK_ID, action.getTaskID());
		values.put(TABLE_TASK_ACTION_USER_ID, action.getUserID());
		values.put(ACTION_USER_NAME, action.getUsername());
		values.put(ACTION_TYPE, action.getActionType());
		values.put(ACTION_VALUE, action.getActionValue());
		values.put(TABLE_TASK_USER_SYNCED, action.getSynced());
		values.put(ACTION_TIMESTAMP, action.getActionTimeStamp());
		db.insert(TABLE_TASK_ACTION, null, values);
		db.close();

	}
	/**
	 * adds all the actions in the arraylist of actions
	 * @param actions
	 */
	public void addAllActions(ArrayList<Actions> actions) {
		for(Actions action: actions) {
			addActions(action);
		}
	}
	/**
	 * updates a column of table taskActions with int value if supported by the column
	 * @param uniqueId
	 * @param columnName
	 * @param columnValue
	 * @return true if successful else false
	 */
	public boolean updateActionColumn(String uniqueId, String columnName, int columnValue) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(columnName, columnValue);
		int rows = db.update(TABLE_TASK_ACTION, values, ACTION_UNIQUE_ID +"='"+ uniqueId + "'", null);
		db.close();	
		if(rows!=0)
			return true;
		else return false;
	}
	
	public ArrayList<Actions> getActionsWithSyncValue(int syncValue) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_TASK_ACTION, null, TABLE_TASK_ACTION_SYNCED + " = '"
				+ syncValue + "'", null, null, null, null);
		ArrayList<Actions> actions = new ArrayList<Actions>();
		if(cursor.moveToFirst()) {
			do {
				Actions action = new Actions();
				action.setActionTimeStamp(cursor.getLong(cursor.getColumnIndex(ACTION_TIMESTAMP)));
				action.setActionType(cursor.getString(cursor.getColumnIndex(ACTION_TYPE)));
				action.setActionValue(cursor.getString(cursor.getColumnIndex(ACTION_VALUE)));
				action.setTaskID(cursor.getString(cursor.getColumnIndex(TABLE_TASK_ACTION_TASK_ID)));
				action.setUniqueId(cursor.getString(cursor.getColumnIndex(ACTION_UNIQUE_ID)));
				action.setUserID(cursor.getString(cursor.getColumnIndex(TABLE_TASK_ACTION_USER_ID)));
				action.setUsername(cursor.getString(cursor.getColumnIndex(ACTION_USER_NAME)));
				action.setActionID("2"); // kisi kaam ka nahi hai ye
				
				actions.add(action);
			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
		return actions;
	}
	
	public boolean checkifActionExists(String uniqueId){
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_TASK_ACTION + " WHERE "
				+ ACTION_UNIQUE_ID + "= '" + uniqueId + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			// value does exists
			cursor.close();
			db.close();
			return true;

		} else {
			cursor.close();
			db.close();
			return false;
		}
	}

	/*public ArrayList<Actions> getAction(String columnName, String columnValue) {
		ArrayList<Actions> actions = new ArrayList<Actions>();
		SQLiteDatabase db = this.getWritableDatabase();
		columnValue = DatabaseUtils.sqlEscapeString(columnValue);
		Cursor cursor = db.query(TABLE_TASK_ACTION, new String[] { ACTION_ID,
				TABLE_TASK_ACTION_TASK_ID, TABLE_TASK_ACTION_USER_ID,
				ACTION_TYPE, ACTION_VALUE, TABLE_TASK_ACTION_SYNCED },
				columnName + "= '" + columnValue + "'", null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Actions a = new Actions();
				a.setActionID(cursor.getString(cursor.getColumnIndex(ACTION_ID)));
				a.setTaskID(cursor.getString(cursor
						.getColumnIndex(TABLE_TASK_ACTION_TASK_ID)));
				a.setUserID(cursor.getString(cursor
						.getColumnIndex(TABLE_TASK_ACTION_USER_ID)));
				a.setActionType(cursor.getString(cursor
						.getColumnIndex(ACTION_TYPE)));
				a.setActionValue(cursor.getString(cursor
						.getColumnIndex(ACTION_VALUE)));
				a.setSynced(cursor.getInt(cursor
						.getColumnIndex(TABLE_TASK_ACTION_SYNCED)));
				// add to list
				actions.add(a);
			} while (cursor.moveToNext());
		}
		return actions;
	}*/

	public Cursor getActionCursor(String columnName, String columnValue) {
		SQLiteDatabase db = this.getWritableDatabase();
		columnValue = DatabaseUtils.sqlEscapeString(columnValue);
		String orderBy = ACTION_TIMESTAMP + " ASC";
		Cursor cursor = db.query(TABLE_TASK_ACTION, null, columnName + "= " + columnValue
				+ "", null, null, null, orderBy);
		cursor.moveToFirst();
		db.close();
		return cursor;

	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations for TABLE_SUBTASKS
	 */
	// adding a subtask
	public void addSubtasks(String Subtask, String ReminderID) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SUBTASK_VALUE, Subtask);
		values.put(T_REMINDER_ID, ReminderID);
		db.insert(TABLE_R_SUBTASKS, null, values);
		db.close();
	}

	// updating a subtask_complete
	public void updateSubtaskComplete(String Subtask, String reminderID,
			int Complete) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COMPLETED_SUBTASK, Complete);
		db.update(TABLE_R_SUBTASKS, values, SUBTASK_VALUE + " = '" + Subtask
				+ "' AND " + T_REMINDER_ID + " = '" + reminderID + "'", null);
		db.close();
	}

	// update the subtask booleans
	public void updateSubtaskBooleans(String reminderID, ArrayList<Boolean> b,
			ArrayList<String> s) {
		for (int i = 0; i < b.size(); i++) {
			int isComplete = StaticFunctions.BooleanToInt(b.get(i));
			String subtask = s.get(i);
			updateSubtaskComplete(subtask, reminderID, isComplete);
		}
	}

	// get the arrayList of Booleans checked or unchecked
	public ArrayList<Boolean> getSubtaskCheckedBooleans(String ReminderID) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_R_SUBTASKS,
				new String[] { COMPLETED_SUBTASK }, T_REMINDER_ID + " =?",
				new String[] { ReminderID }, null, null, null);
		ArrayList<Boolean> isChecked = new ArrayList<Boolean>();
		if (cursor.moveToFirst()) {
			do {
				Boolean b = StaticFunctions.intToBoolean(cursor.getInt(cursor
						.getColumnIndex(COMPLETED_SUBTASK)));
				isChecked.add(b);
			} while (cursor.moveToNext());
		}

		db.close();
		cursor.close();
		return isChecked;
	}

	// adding an arraylist of subtask
	public void addArrayListofSubtask(ArrayList<HashMap<String, String>> data,
			String reminderID) {
		for (int i = 0; i < data.size(); i++) {
			String subtask = new String();
			HashMap<String, String> map = new HashMap<String, String>();
			map = data.get(i);
			subtask = map.get("Subtask");
			addSubtasks(subtask, reminderID);

		}

	}

	// adding an array of Subtask
	public void addArrayofSubtask(String[] array, String reminderID) {
		for (int i = 0; i < array.length; i++) {
			addSubtasks(array[i], reminderID);
		}
	}

	// returning an array of subtask pertaining to a perticular reminderID
	public String[] getSubtasks(String ReminderID) {
		String selectQuery = "SELECT * FROM " + TABLE_R_SUBTASKS + " WHERE "
				+ T_REMINDER_ID + " = " + ReminderID;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		String[] subtasks = new String[cursor.getCount()];
		int i = 0;
		if (cursor.moveToFirst()) {
			do {

				String item = cursor.getString(cursor
						.getColumnIndex(SUBTASK_VALUE));
				subtasks[i] = item;
				i++;

			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
		return subtasks;

	}

	// returning an arraylist of subtask pertaining to a perticular reminderID
	public ArrayList<HashMap<String, String>> getArrayListOfSubtask(String ReminderID) {
		String selectQuery = "SELECT * FROM " + TABLE_R_SUBTASKS + " WHERE "
				+ T_REMINDER_ID + " = " + ReminderID;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// String [] subtasks= new String [cursor.getCount()];
		ArrayList<HashMap<String, String>> subtaskArrayList = new ArrayList<HashMap<String, String>>();
		// int i=0;
		if (cursor.moveToFirst()) {
			do {

				String item = cursor.getString(cursor
						.getColumnIndex(SUBTASK_VALUE));
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("Subtask", item);
				subtaskArrayList.add(hashMap);

			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
		return subtaskArrayList;

	}
	
	public ArrayList<HashMap<String, String>> getListofSubtasks(String reminderID) {
		String selectQuery = "SELECT * FROM " + TABLE_R_SUBTASKS + " WHERE "
				+ T_REMINDER_ID + " = " + reminderID;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		ArrayList<HashMap<String, String>> subtaskArrayList = new ArrayList<HashMap<String,String>>();
		
		if (cursor.moveToFirst()) {
			do {

				String item = cursor.getString(cursor.getColumnIndex(SUBTASK_VALUE));
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("subtask", item);
				map.put("value", cursor.getString(cursor.getColumnIndex(COMPLETED_SUBTASK)));
				subtaskArrayList.add(map);

			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
		return subtaskArrayList;
	}

	public void deleteAllSubtask(String ReminderID) {
		String deleteQuery = "DELETE FROM " + TABLE_R_SUBTASKS + " WHERE "
				+ T_REMINDER_ID + " = " + ReminderID;
		SQLiteDatabase db = this.getWritableDatabase();
		db.rawQuery(deleteQuery, null).moveToFirst();

		Log.d("Delete Query", "Executed");
		db.close();
	}

	public int checkIfLocationExists(double lat, double lng) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_LOCATION, new String[]{LOCATION_COL_ID},
				LOCATION_LATITUDE + "=? AND " + LOCATION_LONGITUDE + "=? " ,
				new String[]{Double.toString(lat), Double.toString(lng)}, null, null, null);
		
		if (cursor.moveToNext()) {
			// value does exists
			int id = cursor.getInt(cursor.getColumnIndex(LOCATION_COL_ID));
			cursor.close();
			db.close();
			return id;

		} else {
			//doesn't exist
			cursor.close();
			db.close();
			return 0;
		}
	}

	public void insertLocation(double lat, double lng, String featurename,
			String address) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(LOCATION_LATITUDE, lat);
		values.put(LOCATION_LONGITUDE, lng);
		values.put(LOCATION_FEATURE_NAME, featurename);
		values.put(LOCATION_ADDRESS, address);
		db.insert(TABLE_LOCATION, null, values);
		db.close();
	}

	public Cursor getLocationCursor() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_LOCATION, null, null, null, null, null,
				null);
		cursor.moveToFirst();
		db.close();
		return cursor;
	}
	public Cursor getLocationCursorfromId(String locationId){
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_LOCATION, null, LOCATION_COL_ID + "=?", new String[]{locationId}, null, null,
				null);
		cursor.moveToFirst();
		db.close();
		return cursor;
	}

	public void deleteLocation(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_LOCATION, null, null, null, null, null,
				null);
		int ID = -1;
		if (cursor.moveToPosition(id)) {
			ID = cursor.getInt(cursor.getColumnIndex(LOCATION_COL_ID));
		}
		db.delete(TABLE_LOCATION, LOCATION_COL_ID + " = ?",
				new String[] { Integer.toString(ID) });
		db.close();
		cursor.close();
	}
	
	/*public HashMap<String, String> getLocationHashMap(String taskID) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.query(TABLE_LOCATION, null, null,null, null, null, null);
		String associatedTasks = null;
		HashMap<String, String> locationHashMap = new HashMap<String, String>();
		if(cursor.moveToFirst()) {
			do{
				associatedTasks = cursor.getString(cursor.getColumnIndex(LOCATION_ASSOCIATED_TASKS));
				if(associatedTasks.contains(taskID)) {
					locationHashMap.put(LocationListsFragment.LOCATION_TITLE,
							cursor.getString(cursor.getColumnIndex(LOCATION_FEATURE_NAME)));
					locationHashMap.put(LocationListsFragment.LOCATION_LATITUDE,
							cursor.getString(cursor.getColumnIndex(LOCATION_LATITUDE)));
					locationHashMap.put(LocationListsFragment.LOCATION_LONGITUDE, 
							cursor.getString(cursor.getColumnIndex(LOCATION_LONGITUDE)));
					locationHashMap.put(LocationListsFragment.LOCATION_ADDRESS, 
							cursor.getString(cursor.getColumnIndex(LOCATION_ADDRESS)));

					break;
				}
			} while(cursor.moveToNext());
			
		}
		db.close();
		cursor.close();
		return locationHashMap;
	}*/
	/**
	 * returns the location json saved in the database
	 * @param taskUniversalId
	 * @return
	 */
	public String getLocationJson(String taskUniversalId) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_R_DETAILS, new String[]{LOCATION_JSON}, UNIVERSAL_REMINDER_ID+ "=?",
				new String[]{taskUniversalId}, null, null, null);
		String locationJson = "";
		if(cursor.moveToFirst()) {
			locationJson = cursor.getString(cursor.getColumnIndex(LOCATION_JSON));
		}
		db.close();
		cursor.close();
		return locationJson;
	}

	public String getLocationName(String locationId) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db
				.query(TABLE_LOCATION, new String[] { LOCATION_FEATURE_NAME },
						LOCATION_COL_ID + " = ?", new String[] { locationId },
						null, null, null);
		if (cursor.moveToFirst()) {
			return cursor.getString(cursor
					.getColumnIndex(LOCATION_FEATURE_NAME));
		}
		db.close();
		cursor.close();
		return null;
	}
	/**
	 * removes location id from task in tasks table
	 * when a location is deleted from locationlistfragment
	 * @param locationId
	 */
	public void resetLocationIdfromTask(String locationId) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LOCATION_ID, "0");
		values.put(LOCATION_JSON, "");
		Cursor cursor = db.query(TABLE_R_DETAILS, new String[]{UNIVERSAL_REMINDER_ID},
				LOCATION_ID +"=?", new String[]{locationId}, null, null, null);
		while(cursor.moveToNext()){
			db.update(TABLE_R_DETAILS, values, UNIVERSAL_REMINDER_ID + "=?",
					new String[]{cursor.getString(cursor.getColumnIndex(UNIVERSAL_REMINDER_ID))});
		}
		db.close();
	}

	/*
	 * Common Database Methods
	 */
	public Cursor getCursor(String Table) {
		String selectQuery = "SELECT " + "*" + " FROM " + Table;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	public int getHighestLocationID() {
		// String sql;
		int LocationID = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		String orderBy = "ABS(" + DatabaseHandler.LOCATION_COL_ID + ")";
		Cursor cursor = db.query(TABLE_LOCATION, new String[] { LOCATION_COL_ID },
				null, null, null, null, orderBy, null);
		if (cursor.moveToLast()) {
			LocationID = cursor.getInt(cursor.getColumnIndex(LOCATION_COL_ID));
		}
		db.close();
		cursor.close();
		return LocationID;
	}
	
	public String getLocationId(String universalID) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_R_DETAILS, new String[] { LOCATION_ID },
				UNIVERSAL_REMINDER_ID + "=?", new String[]{universalID}, null, null, null, null);
		String locationId = "0";
		if(cursor.moveToFirst()) {
			locationId =  cursor.getString(cursor.getColumnIndex(LOCATION_ID));
		}
		db.close();
		cursor.close();
		return locationId;
	}
	/**
	 * adds people to db
	 * @param people
	 */
	public void addPeopleToDb(People people) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PEOPLE_CONTACT_ID, people.getFbId());
		values.put(PEOPLE_EMAIL, people.getEmail());
		if(people.hasMeedo()) {
			values.put(PEOPLE_HASMEEDO, 1);
			
		} else values.put(PEOPLE_HASMEEDO, 0);
		
		values.put(PEOPLE_NAME, people.getName());
		values.put(PEOPLE_TASK_ID, people.getTaskId());
		values.put(PEOPLE_ACCEPTED, people.getAccepted());
		db.insertOrThrow(TABLE_PEOPLE, null, values);
		db.close();
	}
	/**
	 * first removes previous entry from db, then adds the current one
	 * @param people
	 */
	public void removePeople(String universal_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PEOPLE, PEOPLE_TASK_ID + "='" + universal_id + "'", null);
		db.close();
	}
	
	public void removeExceptCreator(Context context, String universal_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PEOPLE, PEOPLE_TASK_ID + "='" + universal_id + "' AND "+
				PEOPLE_CONTACT_ID + "!='" + StaticFunctions.getUserFbId(context) + "'", null);
		db.close();
	}
	
	public boolean checkIfPeopleExist(String taskID, String contactId) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		String sql = "SELECT * FROM " + TABLE_PEOPLE + " WHERE "+ PEOPLE_TASK_ID + "= '" + taskID + "'" + " AND "
				+ PEOPLE_CONTACT_ID + "= '" + contactId + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor.getCount()>0) {
			cursor.close();
			db.close();
			return true;
		} else {
			cursor.close();
			db.close();
			return false;
		}
	}
	
	public ArrayList<People> getPeoplesWithSyncValue(int syncValue, String universal_id) {
		ArrayList<People> peopleList = new ArrayList<People>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_PEOPLE, null, PEOPLE_TASK_ID + " = '"
				+ universal_id + "' AND " + PEOPLE_SYNCED + " = '" + syncValue + "'" , null, null, null, null);
		if(cursor.moveToFirst()) {
			do {
				People people = new People();
				int hasMeedo = cursor.getInt(cursor.getColumnIndex(PEOPLE_HASMEEDO));
				if(hasMeedo==0){
					people.setHasMeedo(false);
				}
				else {
					people.setHasMeedo(true);
				}
				people.setEmail(cursor.getString(cursor.getColumnIndex(PEOPLE_EMAIL)));
				people.setFbId(cursor.getString(cursor.getColumnIndex(PEOPLE_CONTACT_ID)));
				people.setName(cursor.getString(cursor.getColumnIndex(PEOPLE_NAME)));
				people.setAccepted(cursor.getInt(cursor.getColumnIndex(PEOPLE_ACCEPTED)));
				people.setSynced(cursor.getInt(cursor.getColumnIndex(PEOPLE_SYNCED)));
				people.setTaskId(universal_id);
			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
		
		return peopleList;
	}
	
	public ArrayList<People> getPeoplesWithSyncValue(int syncValue) {
		ArrayList<People> peopleList = new ArrayList<People>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_PEOPLE, null, PEOPLE_SYNCED + " = '" + syncValue + "'" , null, null, null, null);
		if(cursor.moveToFirst()) {
			do {
				People people = new People();
				int hasMeedo = cursor.getInt(cursor.getColumnIndex(PEOPLE_HASMEEDO));
				if(hasMeedo==0){
					people.setHasMeedo(false);
				}
				else {
					people.setHasMeedo(true);
				}
				people.setEmail(cursor.getString(cursor.getColumnIndex(PEOPLE_EMAIL)));
				people.setFbId(cursor.getString(cursor.getColumnIndex(PEOPLE_CONTACT_ID)));
				people.setName(cursor.getString(cursor.getColumnIndex(PEOPLE_NAME)));
				people.setAccepted(cursor.getInt(cursor.getColumnIndex(PEOPLE_ACCEPTED)));
				people.setSynced(cursor.getInt(cursor.getColumnIndex(PEOPLE_SYNCED)));
				people.setTaskId(cursor.getString(cursor.getColumnIndex(PEOPLE_TASK_ID)));
			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
		return peopleList;
	}
	/**
	 * checks values of accepted column with given universalid and contactId
	 * @param universalId
	 * @param contactId
	 * @return
	 */
	public int checkAcceptedOfPeople(String universalId, String contactId) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_PEOPLE, new String[]{PEOPLE_ACCEPTED}, 
				PEOPLE_TASK_ID + " = '" + universalId + "' AND " + PEOPLE_CONTACT_ID + " = '" + contactId + "'", null, null, null, null);
		int accepted = 0;
		if(cursor.moveToFirst()) {
			accepted = cursor.getInt(cursor.getColumnIndex(PEOPLE_ACCEPTED));
		}
		cursor.close();
		db.close();
		return accepted;
	}
	
	public ArrayList<People> getPeopleInvolved(Context context, String universalId) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_PEOPLE, null,
				UNIVERSAL_REMINDER_ID + " =?" , new String[]{universalId}, null, null, null);
		ArrayList<People> peoples = new ArrayList<People>();
		
		/*People thisPeople = new People(true, StaticFunctions.getName(context), 
				StaticFunctions.getUserEmail(context), StaticFunctions.getUserFbId(context));
		thisPeople.setTaskId(universalId);
		thisPeople.setAccepted(Task.TASK_ACKNOWLEDGMENT_REMOVED);
		thisPeople.setHasMeedo(true);*/
		
		if(cursor.moveToNext()) {
			do {
				People people = new People();
				int hasMeedo = cursor.getInt(cursor.getColumnIndex(PEOPLE_HASMEEDO));
				if(hasMeedo==0){
					people.setHasMeedo(false);
				}
				else {
					people.setHasMeedo(true);
				}
				people.setEmail(cursor.getString(cursor.getColumnIndex(PEOPLE_EMAIL)));
				people.setFbId(cursor.getString(cursor.getColumnIndex(PEOPLE_CONTACT_ID)));
				people.setName(cursor.getString(cursor.getColumnIndex(PEOPLE_NAME)));
				people.setTaskId(universalId);
				people.setAccepted(cursor.getInt(cursor.getColumnIndex(PEOPLE_ACCEPTED)));
				people.setSynced(cursor.getInt(cursor.getColumnIndex(PEOPLE_SYNCED)));
				peoples.add(people);
			} while (cursor.moveToNext());
		}
		
		/*if(!checkIfPeopleExist(thisPeople.getTaskId(), thisPeople.getFbId())){
			peoples.add(0,thisPeople);
		} else {}*/
		//Log.d("jsonjson", Integer.toString(peoples.size()));
		cursor.close();
		db.close();
		return peoples;
	}

	/**
	 * updates a column of People table with integer value
	 * @param taskId
	 * @param columnName
	 * @param columnValue
	 * @return true if successful
	 * @deprecated it updates all the rows with a particular task Id , thus a bad code
	 */
	public boolean updateAPeopleColumn(String taskId, String columnName, int columnValue) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(columnName, columnValue);
		int rows = db.update(TABLE_PEOPLE, values, PEOPLE_TASK_ID + "=?", new String[]{taskId});
		db.close();	
		if(rows!=0)
			return true;
		else return false;
	}
	/**
	 * updates a column of People table with integer value
	 * @param taskId
	 * @param columnName
	 * @param columnValue
	 * @param contactId
	 * @return true if successful
	 */
	public boolean updateAPeopleColumn(String taskId,String contactId, String columnName, int columnValue) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(columnName, columnValue);
		int rows = db.update(TABLE_PEOPLE, values,
				PEOPLE_TASK_ID +"='"+ taskId + "' AND " + PEOPLE_CONTACT_ID +"='"+ contactId + "'", null);
		db.close();	
		if(rows!=0)
			return true;
		else return false;
	}

	public int getTaskSync(String universalID) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_R_DETAILS, new String[] { TABLE_TASKS_SYNCED },
				UNIVERSAL_REMINDER_ID + "=?", new String[]{universalID}, null, null, null, null);
		int locationId = -1;
		if(cursor.moveToFirst()) {
			locationId =  cursor.getInt(cursor.getColumnIndex(TABLE_TASKS_SYNCED));
		}
		db.close();
		cursor.close();
		return locationId;
	}
	
	

	/*public void updateLocationTaskAssociated(int locationId, String taskID) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.query(TABLE_LOCATION, new String[]{LOCATION_COL_ID, LOCATION_ASSOCIATED_TASKS}, null,
				null, null, null, null);
		ContentValues values = new ContentValues();
		String associatedTasks = null;
		if(cursor.moveToFirst()) {
			do{
				associatedTasks = cursor.getString(cursor.getColumnIndex(LOCATION_ASSOCIATED_TASKS));
				//Log.d("assoc", associatedTasks);
				if(cursor.getInt(cursor.getColumnIndex(LOCATION_COL_ID)) != locationId){
						Log.d("yahan", "yahanna");
					if(associatedTasks!=null){
						if(associatedTasks.contains(taskID)) {
							Log.d("yahan1", "yahanna");
							String ass;
							ass = associatedTasks.replace("," + taskID, "");
							if(ass==associatedTasks){
								ass = associatedTasks.replace(taskID + ",", "");
								if(ass == associatedTasks) {
									ass = null;
								}
							}
							associatedTasks =ass;
							Log.d("assoc", associatedTasks, new NullPointerException());
						} else{
							associatedTasks = associatedTasks + "," + taskID;
						}
					}
				} else {
					if(associatedTasks!=null){
						if(!associatedTasks.contains(taskID)) 
							associatedTasks = associatedTasks + "," + taskID;
					} else associatedTasks = taskID;
				}
				values.put(LOCATION_ASSOCIATED_TASKS, associatedTasks);
				db.update(TABLE_LOCATION, values, LOCATION_COL_ID + "=?",
						new String[]{Integer.toString(cursor.getInt(cursor.getColumnIndex(LOCATION_COL_ID)))});
					
			} while(cursor.moveToNext());
		}
		Log.d("assoc2", associatedTasks, new NullPointerException());
		
		
		cursor.close();
		db.close();
	}*/
	
	/*public ArrayList<People> getPeopleInvolved(Context context, String reminderId) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_R_DETAILS, new String[] { MEMBERS_IDS },
				REMINDER_ID + " =?" , new String[]{reminderId}, null, null, null);
		ArrayList<People> peopleList = new ArrayList<People>();
		peopleList.add(0, new People(true, StaticFunctions.getUsername(context), 
				StaticFunctions.getUserEmail(context), StaticFunctions.getUserFbId(context)));
		
		if(cursor.moveToNext()) {
			String[] members;
			String[] peopleNames;
			if(cursor.getString(cursor.getColumnIndex(MEMBERS_IDS)).contains("@")) {
				peopleNames = getPeopleName(cursor.getString(cursor.getColumnIndex(MEMBERS_IDS)));
			}
			try {
				members = cursor.getString(cursor.getColumnIndex(MEMBERS_IDS)).split(",");
			} catch (NullPointerException e) {
				members = new String[]{cursor.getString(cursor.getColumnIndex(MEMBERS_IDS))};
			}
			if(cursor.getString(cursor.getColumnIndex(MEMBERS_IDS))!="") {
				for(int i=0; i<members.length; i++) {
					if(members[i].contains("@")) { // email i.e. hasMeedo is false
						peopleList.add(new People(false, peopleNames[1], members[i], peopleNames[0]));
					} else peopleList.add(new People(true, peopleNames[1], members[i], peopleNames[0]));
				}
			}
		}
		
		return peopleList;
	}
	protected String[] getPeopleName(String email) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_CONNECTIONS, new String[]{KEY_USER_FRIEND_ID,KEY_USER_FRIEND_NAME},
				KEY_USER_EMAIL+ "=?", new String[]{email}, null, null, null);
		if(cursor.moveToNext()) {
			return new String[]{cursor.getString(cursor.getColumnIndex(KEY_USER_FRIEND_ID)),
					cursor.getString(cursor.getColumnIndex(KEY_USER_FRIEND_NAME))};
		} else return null;
	}*/
}