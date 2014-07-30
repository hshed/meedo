package com.cw.msumit.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.cw.msumit.LocationActivityNew;
import com.cw.msumit.R;
import com.cw.msumit.RemindersDetails;
import com.cw.msumit.SubtaskActivity;
import com.cw.msumit.adapters.AssignPeopleAdapter;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.objects.Actions;
import com.cw.msumit.objects.People;
import com.cw.msumit.objects.Reminder;
import com.cw.msumit.objects.SendReminder;
import com.cw.msumit.objects.Task;
import com.cw.msumit.services.NotifyService;
import com.cw.msumit.services.ScheduleClient;
import com.cw.msumit.services.SendActionIntentService;
import com.cw.msumit.utils.ReminderGeofence;
import com.cw.msumit.utils.ReminderGeofenceStore;
import com.cw.msumit.utils.StaticFunctions;
import com.facebook.Session;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;

public class TaskDetailsFragment extends SherlockFragment implements ConnectionCallbacks, 
					OnConnectionFailedListener, OnAddGeofencesResultListener, OnRemoveGeofencesResultListener{
	
	private DatabaseHandler dbAdapter;
	private RelativeLayout peopleBox;
	private LinearLayout peopleInvitedLinearLayout;
	private AutoCompleteTextView autoComplete;
	private LinearLayout.LayoutParams addedPeopleLayoutParams;
	LayoutInflater newPeoplelayoutInflater;
	private ImageButton addFriendButton, speakButton;
	private View dummyView, newPeopleView;
	private TextView listCategory, 
			addPersonOverlay, calendar, subtaskButton, location, Note;
	private OnClickListener myClickListener, pingClickListener, myClickListenerNew, removeClickListener,
							removeClickListenerNew, pingClickListenerNew;
	private String fontPath = "fonts/Roboto-Regular.ttf", fontPath1 = "fonts/Roboto-Condensed.ttf",
			reminderID, universalID, x, originalTitle;
	private AssetManager assetManager;
	private Typeface plain, plain1;
	/** Called when the activity is first created. */
	//private Facebook facebook = new Facebook("341800519237485");
	private Task task;
	private Intent intent;
	private EditText reminderText, commentText;
	private TextView important;
	private int Recreate=0, assigned;
	public ContentValues values=new ContentValues();
	public int isEditCategory=0;
	// Holds the location client
	private LocationClient mLocationClient;

	// Defines the allowable request types.
	public enum REQUEST_TYPE {
			ADD, REMOVE_INTENT, REMOVE_LIST
		}

	private REQUEST_TYPE mRequestType;
	// Flag that indicates if a request is underway.
	private boolean mInProgress = false, impOrNot = false, backButtonPressed = true,
			removePeopleOrNot = true, doAddDateAction=false, doAddNoteAction = false, doAddLocationAction = false;
	List<Geofence> mGeofenceList;
	List<String> mGeofencesToRemove;
	private ReminderGeofenceStore geofenceStore;
	ScheduleClient scheduleClient;
	//SendReminderClient sendReminderClient;
	private People people;
	private ArrayList<People> peopleList;
	private ArrayList<String> listOfemails;
	private ArrayList<Actions> actionsToAdd;
	private Cursor autoCompleteCursor;
	int acknowledgment = Task.TASK_ACKNOWLEDGMENT_ACCEPT;
	String actionType;
	Calendar oldCalendar, newCalendar;

	public static TaskDetailsFragment newInstance(int index) {
		TaskDetailsFragment f = new TaskDetailsFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);

		return f;
	}

	public int getShownIndex() {
		return getArguments().getInt("index", 0);
	}

	/**
	 * Access the task of this fragment
	 */
	protected Task getTask () {
		return this.task;
	}
	
	protected TextView getCalendarTextView () {
		return this.calendar;
	}
	
	protected void updateTask (String date, String time, String repeat) {
		task.setDate(date);
		task.setTime(time);
		task.setRepeat(repeat);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, "Save").setIcon(R.drawable.save_light)
		                      .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 1, 0, "Delete").setIcon(R.drawable.discard_light)
                              .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			saveReminderAndfinish();
			getActivity().finish();
			return (true);
		case 0:
			saveReminderAndfinish();
			getActivity().finish();
			return true;
		case 1:
			Reminder reminder = new Reminder(scheduleClient, task);
			reminder.remove();
			//DatabaseHandler handler2=new DatabaseHandler(getActivity());
			//update importance
			dbAdapter.deleteTask(task);
			removeGeofences();
			getActivity().finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void saveReminderAndfinish() {
		Reminder reminder = new Reminder(scheduleClient, task);
		SendReminder sendReminder = new SendReminder();
		backButtonPressed = false;
		//Toast.makeText(getSherlockActivity(), "Home", Toast.LENGTH_SHORT).show();
		if (!StaticFunctions.isWhiteSpace(reminderText.getText().toString())){
			backButtonPressed = false;
			if(task.getUniversalID().contains("NeedMeedoSignIn")) { // means task is not from some one else
				task.setUniversalID(StaticFunctions.generateUniversalId(getActivity()));
			}
			//update importance
			dbAdapter.updateAColumn(reminderID, "important", StaticFunctions.BooleanToInt(impOrNot));
			task.setImportant(StaticFunctions.BooleanToInt(impOrNot));
			dbAdapter.updateAColumn(reminderID, "title", reminderText.getText().toString());
			task.setTitle(reminderText.getText().toString());
			dbAdapter.updateAColumn(reminderID, "synced", 0);
			dbAdapter.updateAColumn(reminderID, "category", listCategory.getText().toString());
			task.setCategory(listCategory.getText().toString());
			//update date and time
			dbAdapter.updateAColumn(reminderID, "date", task.getDate());
			dbAdapter.updateAColumn(reminderID, "time", task.getTime());
			dbAdapter.updateAColumn(reminderID, "order_by", StaticFunctions.sortString(task.getDate(), 
						task.getTime(), StaticFunctions.BooleanToInt(impOrNot)));
			dbAdapter.updateAColumn(reminderID, "repeat", task.getRepeat());
			dbAdapter.updateAColumn(reminderID, "location_id", task.getLocationID());
			if(task.getCreatorID().equals("0")){//equal to 0 means this user has created the task
				task.setCreatorID(StaticFunctions.getUserFbId(getActivity()));
				dbAdapter.updateAColumn(reminderID, "creator_id", task.getCreatorID());
			}
			task.setNote(StaticFunctions.getNote(reminderID, getActivity()));
			
			addGeofences();
			// this location code should be put after addgeofence
			
			if(task.getLocationJson()!=null){
				dbAdapter.updateAColumn(task.getReminderID(), "location_json", task.getLocationJson());
			}
			boolean userIsInIt = false;
			// means user has signed in via facebook in which case only we can allow adding people to assign
			//if(!StaticFunctions.getUsername(getSherlockActivity()).equals("NeedMeedoSignIn")) {
			if(Session.getActiveSession()!=null) {
				dbAdapter.updateAColumn(reminderID, "universal_id", task.getUniversalID());
				//handler.updateAColumn(reminderID, "members_id", getPeopleToSave());
				if(removePeopleOrNot)
					dbAdapter.removePeople(task.getUniversalID());
				else dbAdapter.removeExceptCreator(getActivity(), task.getUniversalID());
				for(People people: peopleList) {
					people.setTaskId(task.getUniversalID());
					//people.setAccepted(0);
					if(!dbAdapter.checkIfPeopleExist(people.getTaskId(), people.getFbId())) {
						dbAdapter.addPeopleToDb(people);
					}
					if(people.getEmail().equals(StaticFunctions.getUserEmail(getActivity()))) {
						userIsInIt = true;
					}
				}

				if(peopleList.size()>1 && assigned!=2) { //means it has been shared
					dbAdapter.updateAColumn(reminderID, "assigned", "3");
				} else {
					dbAdapter.updateAColumn(reminderID, "assigned", "2");
				}
				if(userIsInIt && peopleList.size()==1) {
					dbAdapter.updateAColumn(reminderID, "assigned", "1");
				}
				if(!originalTitle.equals(task.getTitle()) && !intent.getBooleanExtra("isNew", false)) { // title has been changed
					addEditAction(task.getTitle());
				}
				if(doAddDateAction && !intent.getBooleanExtra("isNew", false)) {
					addDateAction(getActionType(), getOldCalendar(), getNewCalendar());
				}
				if(doAddNoteAction && !intent.getBooleanExtra("isNew", false)) {
					addNoteAction();
				}
				if(actionsToAdd!=null) {
					dbAdapter.addAllActions(actionsToAdd);
					sendActionToWeb(actionsToAdd);
				}
				sendReminder.send(getActivity(), task, peopleList, acknowledgment);
				
			} else {
				Toast.makeText(getActivity(),"You need to signin with facebook to be able to assign tasks to people", Toast.LENGTH_LONG).show();
				getSignInAlert().create().show();
			}
			TaskCategoryFragment.newCategory=null;
			//save the category if it doesn't already exists
			StaticFunctions.SaveCategory(listCategory.getText().toString(), getActivity());
			
			reminder.remind();
			
		}
		if(StaticFunctions.isWhiteSpace(reminderText.getText().toString()) && x.equals("0")) {
			//delete the existing reminder if there is white space and new reminder
			//code to remove reminder
			reminder.remove();
			
			dbAdapter.deleteTask(task);
			
			removeGeofences();
		}
		getActivity().finish();
		
	}

	/*private String getPeopleToSave() {
		// TODO Auto-generated method stub
		String [] ids = new String[peopleList.size()]; 
		for(People people: peopleList) {
			if(people.hasMeedo()) { 
				ids[peopleList.indexOf(people)] = people.getFbId();
			} else {
				ids[peopleList.indexOf(people)] = people.getEmail();
			}
		}
		return StaticFunctions.convertStringArrayToString(ids);
	}*/

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setHasOptionsMenu(true);
		//setRetainInstance(true); // calling this doesn't save arraylist in savedinstancebundle :(
		super.onActivityCreated(savedInstanceState);

	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
	    if(scheduleClient != null)
	    	scheduleClient.doUnbindService();
	    //if(sendReminderClient != null)
	    	//sendReminderClient.doUnbindService();
		super.onStop();
		}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
		outState.putSerializable("peopleList", peopleList);
		super.onSaveInstanceState(outState);
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {

			return null;
		}
		
		dbAdapter = new DatabaseHandler(getActivity());
		mGeofenceList = new ArrayList<Geofence>();
		geofenceStore = new ReminderGeofenceStore(getSherlockActivity());
		actionsToAdd = new ArrayList<Actions>();
		
		scheduleClient = new ScheduleClient(getSherlockActivity());
		scheduleClient.doBindService();
		
		peopleList = new ArrayList<People>();
		// instantiating the views
		View fragmentView = inflater.inflate(R.layout.details, container, false);
		assetManager = getSherlockActivity().getAssets();
		plain = Typeface.createFromAsset(assetManager, fontPath);
		plain1 = Typeface.createFromAsset(assetManager, fontPath1);
		
		TextView taskSummaryContainer = (TextView) fragmentView.findViewById(R.id.taskSummaryContainerText);
		taskSummaryContainer.setTypeface(plain1);
		TextView taskAssignedContainer = (TextView) fragmentView.findViewById(R.id.taskAssignedTo);
		taskAssignedContainer.setTypeface(plain1);
		autoComplete = (AutoCompleteTextView) fragmentView.findViewById(R.id.autoCompleteName);
		
		addFriendButton = (ImageButton) fragmentView.findViewById(R.id.addButton);
		
		peopleBox = (RelativeLayout) fragmentView.findViewById(R.id.addPeopleRelativeLayout);
		addPersonOverlay = (TextView) fragmentView.findViewById(R.id.addPersonOverlay);
		addPersonOverlay.setTypeface(plain1);
		final RelativeLayout addPersonOverlayContainer = (RelativeLayout)fragmentView.findViewById(R.id.addPersonOverlayContainer);
		
		addPersonOverlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addPersonOverlayContainer.setVisibility(View.GONE);
				peopleBox.setVisibility(View.VISIBLE);
				autoComplete.requestFocus();
				InputMethodManager inputMethodManager=(InputMethodManager) 
						getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			    inputMethodManager.toggleSoftInputFromWindow(autoComplete.getApplicationWindowToken(), 
			    		InputMethodManager.SHOW_FORCED, 0);
				
			}
		});
		
		
		reminderText=(EditText) fragmentView.findViewById(R.id.reminderText);
	
		calendar = (TextView) fragmentView.findViewById(R.id.calendarText);
		Note =(TextView) fragmentView.findViewById(R.id.note);
		subtaskButton = (TextView) fragmentView.findViewById(R.id.subtask);
		location = (TextView) fragmentView.findViewById(R.id.location);
		important=(TextView) fragmentView.findViewById(R.id.ratingStar);
		listCategory = (TextView) fragmentView.findViewById(R.id.category);
		listCategory.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//open this dialog only if Category Value = 3
				if (!StaticFunctions.ifCategoryValueThree(listCategory.getText().toString(), getActivity())) {
				SherlockDialogFragment newFragment = new TaskCategoryFragment();
				newFragment.setTargetFragment(TaskDetailsFragment.this, 4103);
				newFragment.show(getSherlockActivity().getSupportFragmentManager(), "task category");
				}
			}
		});
		speakButton = (ImageButton) fragmentView.findViewById(R.id.tts);
		
		intent=getActivity().getIntent();
		//get the reminderID
		reminderID=intent.getStringExtra("reminderID");
		universalID = intent.getStringExtra("universalID");
		
		if (intent.getBooleanExtra("isNew", false)) {
			//means the reminder is new
			((RemindersDetails) getActivity()).getSupportActionBar().
			setTitle("New Task");
			//fire up the keyboard
			getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		    
		} 
		
		x=reminderID;
		//if x=="0", means new id
		//initialize the main variables
		task=new Task();
		
		Log.d("TaskId", reminderID);
		//last reminder title
		//DatabaseHandler handler1= new DatabaseHandler(getActivity());
		Log.d("count", Integer.toString(StaticFunctions.getLastCount(getActivity())));
		//Task dummy= dbAdapter.getTask(Integer.toString(StaticFunctions.getLastReminder(getActivity())));
		//this code need to be changed
		//int Synced=dummy.getSynced();
		/*if (reminderID.equals("0") && Synced==1){
			
			// this code basically 
			task=dummy;
			reminderID=task.getReminderID();
		}*/
			//means existing new task is there
			if (reminderID.equals("0") ){
				task.setReminderID(Integer.toString(StaticFunctions.getLastReminder(getActivity())+1));
				task.setUniversalID(StaticFunctions.generateUniversalId(getActivity()));
				task.setTitle("");
				reminderID=task.getReminderID();
				//add in the database
				dbAdapter.addReminders(task);
				task= dbAdapter.getTask(reminderID);
				task.setUniversalID(StaticFunctions.generateUniversalId(getActivity()));
				task.setLocationJson("");
				task.setSynced(-1);
				reminderID=task.getReminderID();
			
			//task has been created in the database here
			//any changes therefore shall be commited
			}
			else {
				//get the task
				task=dbAdapter.getTask(reminderID);
				task.setUniversalID(universalID);
				task.setLocationID(dbAdapter.getLocationId(universalID));
				task.setLocationJson(dbAdapter.getLocationJson(universalID));
				task.setSynced(dbAdapter.getTaskSync(universalID));
				task.setRepeat(dbAdapter.getRepeat(reminderID));
			}
			
			// Assign Values here
			values.put("isEdit", isEditCategory);
			values.put("categoryValue", task.Category);
		
		// Set the UI of four buttons + Edit text
		//edittext
		reminderText.setText(task.getTitle());
		originalTitle = task.getTitle();
		// calendar
		
		if (task.getDate().equals("N-N-N")) {
			//means either there is no reminder or the reminder is set through timer
			calendar.setCompoundDrawablesWithIntrinsicBounds
			(null, getResources().getDrawable(R.drawable.bell), null, null);
			calendar.setText("Reminder");
		}
		if (!task.getDate().equals("N-N-N")) {
			//no timer and reminder is there
			calendar.setCompoundDrawablesWithIntrinsicBounds
			(null, getResources().getDrawable(R.drawable.bell_orange), null, null);
			
			//this needs to be change dto a better function
			calendar.setText(StaticFunctions.DateandTimeFromData(task.getDate(), task.getTime()));
		
		}
		/*
		 * Note
		 */
		if (!task.getNote().equals("")) {
			//means either there is a Note 
			Note.setCompoundDrawablesWithIntrinsicBounds
			(null, getResources().getDrawable(R.drawable.note_orange), null, null);
		}
		
		if (task.getNote().equals("")) {
			//no Note in this case
			Note.setCompoundDrawablesWithIntrinsicBounds
			(null, getResources().getDrawable(R.drawable.note), null, null);
		}
		/*
		 * subtask
		 */
		if (task.getSubtasks()==0) {
			subtaskButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.no_subtask), null, null);
			//BitmapDrawable bmDrawable = StaticFunctions.writeOnDrawable(R.drawable.no_subtask, "", getSherlockActivity(), plain2);
			//subtaskButton.setCompoundDrawablesWithIntrinsicBounds(null, bmDrawable, null, null);
		}
		if(task.getSubtasks()!=0) {
			
			//subtaskButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.subtask), null, null);
			//BitmapDrawable bmDrawable = StaticFunctions.writeOnDrawable(R.drawable.subtask, Integer.toString(task.getSubtasks()), getSherlockActivity(), plain2);
			Drawable bmDrawable = StaticFunctions.getSubtaskDrawable(task.getSubtasks(), getSherlockActivity());
			subtaskButton.setCompoundDrawablesWithIntrinsicBounds(null, bmDrawable, null, null);
		}
		/*
		 * location
		 */
		if(!task.getLocationJson().equals("")){
			location.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location, 0, 0, 0);
			try {
				location.setText(new JSONObject(task.getLocationJson()).getString(Task.LOCATION_TITLE));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*
		 * important
		 */
		if (task.getImportant()==1) {
			//important.setChecked(true);
			impOrNot = true;
			important.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.starre_d), null, null);
		}
		else {
			//important.setChecked(false);
			impOrNot = false;
			important.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.star), null, null);
		}
		
		/*
		 * category
		 */
		listCategory.setText(task.getCategory());		
		
		calendar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//save the title
				
				//getDateAndTimefromDb();
				//set this calendar values to values you have of this task from the database. 
				
				String rString = task.getRepeat();
				String[] dString = task.getDate().split("-");
				String[] tString = task.getTime().split(":");
				
				ArrayList<String> dateAndTime = new ArrayList<String>(5);
				dateAndTime.add(0, dString[0]);
				dateAndTime.add(1, dString[1]);
				dateAndTime.add(2, dString[2]);
				dateAndTime.add(3, tString[0]);
				dateAndTime.add(4, tString[1]);
			
				boolean noRepeat = StaticFunctions.noRepeatfromString(rString);
				boolean dailyRepeat = StaticFunctions.dailyRepeatfromString(rString);
				int monthlyRepeatDay = StaticFunctions.monthlyRepeatDayfromString(rString);
				boolean[] weeklyRepeatDays = StaticFunctions.weeklyRepeatDaysfromString(rString);
				
				Bundle repeatBundle = new Bundle();
				repeatBundle.putBoolean(CalendarFragment.REPEAT_NONE, noRepeat);
				repeatBundle.putBoolean(CalendarFragment.REPEAT_DAILY, dailyRepeat);
				repeatBundle.putBooleanArray(CalendarFragment.REPEAT_WEEKLY, weeklyRepeatDays);
				repeatBundle.putInt(CalendarFragment.REPEAT_MONTHLY, monthlyRepeatDay);
				//send the reminder ID too so that this reminder id gets updated.
				SherlockDialogFragment calendarFragment = CalendarFragment.newInstance(dateAndTime, repeatBundle);
				//set the target fragment
				calendarFragment.setTargetFragment(TaskDetailsFragment.this, 123);
				calendarFragment.show(getSherlockActivity().getSupportFragmentManager(), "TaskDetail");
			}
		});
		
		subtaskButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//DatabaseHandler dbHandler=new DatabaseHandler(getActivity());
				dbAdapter.updateAColumn(reminderID, "title", reminderText.getText().toString());
				dbAdapter.updateAColumn(reminderID, "important", StaticFunctions.BooleanToInt(impOrNot));
				Intent intent=new Intent(getActivity(), SubtaskActivity.class);
				intent.putExtra("ReminderID", reminderID);
				String[] subtasks={"0"};
				intent.putExtra("subtaskArray", subtasks);
				startActivity(intent);
				getSherlockActivity().overridePendingTransition( R.anim.slide_up_dialog, R.anim.slide_out_dialog );
				Recreate=1;
			}
		});
		
		important.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!impOrNot){
					important.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.starre_d), null, null);
					impOrNot = true;
				} else{	
					important.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.star), null, null);
					impOrNot = false;
				}
			}
		});

		location.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//DatabaseHandler dbHandler=new DatabaseHandler(getActivity());
				dbAdapter.updateAColumn(reminderID, "title", reminderText.getText().toString());
				dbAdapter.updateAColumn(reminderID, "important", StaticFunctions.BooleanToInt(impOrNot));
				Intent intent = new Intent(getActivity(), LocationActivityNew.class);
				intent.putExtra("taskId", task.getReminderID());
				intent.putExtra(ReminderGeofence.LOCATION_ID, task.getLocationID());
				startActivityForResult(intent, 8);
				getSherlockActivity().overridePendingTransition(R.anim.slide_up_dialog, R.anim.slide_out_dialog);
				//Recreate=1;
			}
		});
		
		Note.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SherlockDialogFragment newFragment = new NoteFragment();
				newFragment.setTargetFragment(TaskDetailsFragment.this, 4103);
				newFragment.show(getActivity().getSupportFragmentManager(), task.ReminderID);
			}
		});
/*
 * Assign Task to Someone Area
 */
		ImageButton activityTextView = (ImageButton) fragmentView.findViewById(R.id.taskActivityButton);
		activityTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TaskCommentsFragment taskCommentsFragment = TaskCommentsFragment.newInstance(task);
				taskCommentsFragment.show(getSherlockActivity().getSupportFragmentManager(), "activity");
			}
		});
		
		peopleInvitedLinearLayout = (LinearLayout) fragmentView.findViewById(R.id.peopleInvited);
		//LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		newPeoplelayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dummyView = newPeoplelayoutInflater.inflate(R.layout.people_dummy,peopleInvitedLinearLayout, false);
		peopleInvitedLinearLayout.addView(dummyView);
		
		listOfemails = new ArrayList<String>();
		//addedPeopleList = dbAdapter.getPeopleInvolved(getActivity(), universalID);
		
		if(savedInstanceState==null) {
			peopleList = dbAdapter.getPeopleInvolved(getActivity(), universalID);
			if(intent.getBooleanExtra("isNew", false)) { // new reminder, so show current user
				People thisPeople = new People(true, StaticFunctions.getName(getActivity()), 
						StaticFunctions.getUserEmail(getActivity()), StaticFunctions.getUserFbId(getActivity()));
				thisPeople.setTaskId(universalID);
				thisPeople.setAccepted(Task.TASK_ACKNOWLEDGMENT_ACCEPT);
				thisPeople.setHasMeedo(true);
				peopleList.add(0, thisPeople);
			}
		} else{
			peopleList = (ArrayList<People>) savedInstanceState.getSerializable("peopleList");
		}

		addedPeopleLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		showPeopleInvitedList(getActivity(), dummyView, peopleInvitedLinearLayout);
		
		autoCompleteCursor = dbAdapter.getAutoCompleteCursor("");
		
		final AssignPeopleAdapter assignPeopleAdapter = new AssignPeopleAdapter(getSherlockActivity(), autoCompleteCursor, 0);

		autoComplete.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		autoComplete.setAdapter(assignPeopleAdapter);
		autoComplete.setThreshold(1);
		addFriendButton.setEnabled(false);
		autoComplete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Session.getActiveSession()==null) // not signed in show alert dialog to sign in
					getSignInAlert().create().show();
			}
		});
		autoComplete.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(!s.toString().equals("")) {
					addFriendButton.setEnabled(true);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		
		autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				people = assignPeopleAdapter.getPeople(position);
				
			}
		});
		/**
		 * this array list contains emails of all people ever added to avoid duplicated additions
		 */
		
		
		addFriendButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(people!=null) {
					if(!listOfemails.contains(people.getEmail())){
						listOfemails.add(people.getEmail());
						peopleList.add(peopleList.size(), people);
						newPeopleView = newPeoplelayoutInflater.inflate(R.layout.people_assigned, peopleInvitedLinearLayout, false);

						TextView peopleName = (TextView) newPeopleView.findViewById(R.id.personAssigned);
						TextView contact = (TextView) newPeopleView.findViewById(R.id.assignedVia);
						TextView ping = (TextView) newPeopleView.findViewById(R.id.pingUser);
						TextView status = (TextView) newPeopleView.findViewById(R.id.userJoined);
						ImageButton removeUser = (ImageButton) newPeopleView.findViewById(R.id.removeUser);
						//peopleName.setText(autoComplete.getText().toString());
						peopleName.setText(people.getName());
						if(!people.hasMeedo()) {
							contact.setText(people.getEmail());
						}

						//newPeopleView.setId(peopleInvitedLinearLayout.getChildCount() + 1);
						newPeopleView.setTag(people);
						status.setText("waiting");
						
						ping.setTag(people);
						
						RemoveViewObject removeViewObject = new RemoveViewObject(newPeopleView, people);
						removeUser.setTag(removeViewObject);

						peopleName.setTypeface(plain);
						contact.setTypeface(plain);
						ping.setTypeface(plain);

						peopleInvitedLinearLayout.addView(newPeopleView,1, addedPeopleLayoutParams);
						newPeopleView.setOnClickListener(myClickListenerNew);
						ping.setOnClickListener(pingClickListenerNew);
						removeUser.setOnClickListener(removeClickListenerNew);
						new TouchDelegateRunnable((View) removeUser.getParent(), removeUser);
					}
						
					else Toast.makeText(getActivity(), "Already added!", Toast.LENGTH_SHORT).show();
					
				} else Toast.makeText(getActivity(), "Invalid action!", Toast.LENGTH_SHORT).show();
				
				autoComplete.setText("");
				
			}
		});
		
		myClickListenerNew = new OnClickListener() {

			@Override
			public void onClick(View v){

			}
		};
		pingClickListenerNew = new OnClickListener() {

			@Override
			public void onClick(View v) {
				People people = (People) v.getTag();
				Toast.makeText(getActivity(), "Pinged at " + ((People) v.getTag()).getEmail(),Toast.LENGTH_SHORT).show();
				getPingDialogBuilder(people).create().show();
			}
		};
		
		removeClickListenerNew = new OnClickListener() {

			@Override
			public void onClick(View v) {
				RemoveViewObject removeViewObject = (RemoveViewObject) v.getTag();
				//int position = removeViewObject.getIndex();
				//peopleList.remove(removeViewObject.getPeople());
				//peopleInvitedLinearLayout.removeView(removeViewObject.getView());
				getDeleteDialogBuilder(removeViewObject, false).create().show();
			}
		};


		PackageManager pm = getActivity().getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			speakButton.setEnabled(false);
			//commentspeakButton.setEnabled(false);
		}

		else {
			speakButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(
							RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					startActivityForResult(intent, 1);

				}

			});
			/**commentspeakButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					Intent intent = new Intent(
							RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					startActivityForResult(intent, 2);

				}
			});**/
		}

		return fragmentView;

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if (Recreate==1) {
			getActivity().finish();
			getActivity().startActivity(getActivity().getIntent());
		}
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == Activity.RESULT_OK) {
				ArrayList<String> matches = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				
				reminderText.setText(matches.get(0).toString());
			}

		}
		if (requestCode == 2) {
			if (resultCode == Activity.RESULT_OK) {
				ArrayList<String> matches = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				
				commentText.setText(matches.get(0).toString());
			}

		}
		if(requestCode == 8) {
			if(resultCode == Activity.RESULT_OK) {
				if(data.getBooleanExtra("ChangeInLocation", false)) {
					doAddLocationAction = true;
				}
				if(data.getBooleanExtra("NoLocation", true)){
					location.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location_grey, 0, 0, 0);
					location.setText("Location");
				} else {
					if(!data.getBooleanExtra("Removing", true)) {
						double[] latlng = data.getDoubleArrayExtra("LatLng");
						final double latitude = latlng[0];
						final double longitude = latlng[1];
						final int route = data.getIntExtra(ReminderGeofenceStore.KEY_TRANSITION_TYPE, Geofence.GEOFENCE_TRANSITION_ENTER);
						if(route!=0){
							ReminderGeofence geofence = new ReminderGeofence(reminderID, latitude, longitude, 500, 28800000, route);
							geofenceStore.setGeofence(reminderID, data.getIntExtra(ReminderGeofence.LOCATION_ID, 0), geofence);
						} else removeGeofences();
						location.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location,0, 0, 0);
						task.setLocationID(Integer.toString(data.getIntExtra(ReminderGeofence.LOCATION_ID, 0)));
						new AsyncTask<Void, Void, Void>(){
							String locationName;
							@Override
							protected Void doInBackground(Void... params) {
								task.setLocationJson(StaticFunctions.generateLocationJson(getActivity(), task.getLocationID(), route));
								try {
									locationName = new JSONObject(task.getLocationJson()).getString(Task.LOCATION_TITLE).toString();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								if(locationName!=null){
									location.setText(locationName.toString());
								}
								super.onPostExecute(result);
							}
							
						}.execute();
						
					} else {
						int selectedRemoveDeleted = data.getIntExtra("RemovedOrDeleted", -1);
						if(selectedRemoveDeleted==1 || selectedRemoveDeleted==2){ // means loc remove from db or location not selected at all
							removeGeofences(); // remove geofence if location removed from db
							task.setLocationID("0");
							task.setLocationJson("");
							location.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location_grey, 0, 0, 0);
							location.setText("Location");
						}
						
						if(selectedRemoveDeleted == 0) { // means a new location has been selected, hence remove previous geofence and add new one
							removeGeofences();
							task.setLocationID(data.getStringExtra(ReminderGeofence.LOCATION_ID));
		
							new AsyncTask<Void, Void, Void>(){
								String locationName;
								int route = Geofence.GEOFENCE_TRANSITION_ENTER;
								Cursor cursor;
								@Override
								protected Void doInBackground(Void... params) {
									task.setLocationJson(StaticFunctions.generateLocationJson(getActivity(), task.getLocationID(), route));
									try {
										locationName = new JSONObject(task.getLocationJson()).getString(Task.LOCATION_TITLE).toString();
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									cursor = dbAdapter.getLocationCursorfromId(task.getLocationID());
									return null;
								}

								@Override
								protected void onPostExecute(Void result) {
									if(locationName!=null){
										location.setText(locationName.toString());
										location.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location, 0, 0, 0);
										double latitude = cursor.getDouble(cursor.getColumnIndex(Task.LOCATION_LATITUDE));
										double longitude = cursor.getDouble(cursor.getColumnIndex(Task.LOCATION_LONGITUDE));
										ReminderGeofence geofence = new ReminderGeofence(reminderID, latitude, longitude, 500, 28800000, route);
										geofenceStore.setGeofence(reminderID, Integer.parseInt(task.getLocationID()), geofence);
									}
									super.onPostExecute(result);
								}
								
							}.execute();
						}
							
					}
					
				}
			
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * It adds child views inflated by the people invited in an event to the
	 * LinearLayout as a list of items
	 * @param context The context in which the method is being called
	 * @param view The child view inflated by person invited name
	 * @param layout The layout in which child view is being added
	 */

	private void showPeopleInvitedList(Context context, View view, final LinearLayout layout) {
		
		myClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		};
		
		removeClickListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RemoveViewObject removeViewObject = (RemoveViewObject) v.getTag();
				boolean creator = false;
				if(task.getCreatorID().equals(removeViewObject.getPeople().getFbId())) {
					creator = true;
				}
				getDeleteDialogBuilder(removeViewObject, creator).create().show();
			}
		};
		
		pingClickListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				People people = (People) v.getTag();
				Toast.makeText(getActivity(),"heelo " + ((People) v.getTag()).getEmail(),Toast.LENGTH_SHORT).show();
				getPingDialogBuilder(people).create().show();
			}
		};

		for (int i = 0; i < peopleList.size(); i++) {
			LayoutInflater inflator = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflator.inflate(R.layout.people_assigned, layout, false);
			TextView peopleName = (TextView) view.findViewById(R.id.personAssigned);
			TextView contact = (TextView) view.findViewById(R.id.assignedVia);
			final TextView ping = (TextView) view.findViewById(R.id.pingUser);
			final TextView status = (TextView) view.findViewById(R.id.userJoined);
			final ImageButton removeUser = (ImageButton) view.findViewById(R.id.removeUser);
			final People people = peopleList.get(i);
			listOfemails.add(people.getEmail());
			peopleName.setText(people.getName());
			if(!people.hasMeedo()) {
				contact.setText(people.getEmail());
			}
			peopleName.setTypeface(plain);
			contact.setTypeface(plain);
			ping.setTypeface(plain);

			//view.setId(layout.getChildCount() + 1);
			view.setTag(people);
			RemoveViewObject removeViewObject = new RemoveViewObject(view, people);
			removeUser.setTag(removeViewObject);
			removeUser.setOnClickListener(removeClickListener);
			ping.setTag(people);
			ping.setOnClickListener(pingClickListener);
			
			if(dbAdapter.checkAcceptedOfPeople(task.getUniversalID(), people.getFbId()) !=0 ){
				ping.setVisibility(View.VISIBLE);
				removeUser.setVisibility(View.GONE);
				status.setText("joined");
			} 
			if(people.getEmail().equals(StaticFunctions.getUserEmail(context)) && people.getAccepted()!=Task.TASK_ACKNOWLEDGMENT_REMOVED) {
				ping.setVisibility(View.GONE);
				removeUser.setVisibility(View.VISIBLE);
				status.setText("joined");
			} 
			if(people.getEmail().equals(StaticFunctions.getUserEmail(context)) && people.getAccepted()==Task.TASK_ACKNOWLEDGMENT_REMOVED) {
				ping.setVisibility(View.VISIBLE);
				ping.setText("join");
				assigned = 2;
				ping.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ping.setVisibility(View.GONE);
						removeUser.setVisibility(View.VISIBLE);
						status.setVisibility(View.VISIBLE);
						status.setText("joined");
						removePeopleOrNot = false;
						assigned = 3;
						dbAdapter.updateAPeopleColumn(task.getUniversalID(),
								StaticFunctions.getUserFbId(getActivity()), People.PEOPLE_HAS_ACCEPTED, Task.TASK_ACKNOWLEDGMENT_ACCEPT);
					}
				});
				removeUser.setVisibility(View.GONE);
				status.setVisibility(View.INVISIBLE);
			}
			
			view.setLayoutParams(addedPeopleLayoutParams);
			layout.addView(view,addedPeopleLayoutParams);
			view.setOnClickListener(myClickListener);
			View removeUserParent = (View) removeUser.getParent();
			new TouchDelegateRunnable(removeUserParent, removeUser).delegate();
		}
	}
	
	private class TouchDelegateRunnable {
		View parent;
		ImageButton viewToDelegate ;
		public TouchDelegateRunnable(View parent, ImageButton viewToDelegate) {
			this.parent = parent;
			this.viewToDelegate = viewToDelegate;
		}
		public void delegate() {
			parent.post(new Runnable() {
	            @Override
				public void run() {
	                // Post in the parent's message queue to make sure the parent
	                // lays out its children before we call getHitRect()
	                Rect delegateArea = new Rect();
	                ImageButton delegate = viewToDelegate;
	                delegate.getHitRect(delegateArea);
	                delegateArea.top -= 10;
	                delegateArea.bottom += 10;
	                delegateArea.right += 10;
	                delegateArea.left -=10;
	                TouchDelegate expandedArea = new TouchDelegate(delegateArea,
	                        delegate);
	                // give the delegate to an ancestor of the view we're
	                // delegating the
	                // area to
	                if (View.class.isInstance(delegate.getParent())) {
	                    ((View) delegate.getParent())
	                            .setTouchDelegate(expandedArea);
	                }
	            };
	        });
		}
	}

	public final class AnActionModeOfEpicProportions implements
			ActionMode.Callback {

		private final int viewId;
		private final View newView;

		public AnActionModeOfEpicProportions(View view) {
			newView = view;
			viewId = view.getId();
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Used to put dark icons on light action bar

			menu.add("Delete").setIcon(R.drawable.discard)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			Toast.makeText(getActivity(), "Got click: " + item,
					Toast.LENGTH_SHORT).show();
			if (item.getTitle() == "Delete") {
				peopleInvitedLinearLayout.removeView(newView.findViewById(viewId));
			}
			mode.finish();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
		}
	}
	
	@Override
	public void onPause () {
		// TODO Auto-generated method stub
		//if(StaticFunctions.isWhiteSpace(reminderText.getText().toString()) && intent.getBooleanExtra("isNew", false)) {
		if(backButtonPressed == true) {
			if(intent.getBooleanExtra("isNew", false)) {
				//delete the existing reminder if there is white space and new reminder
				dbAdapter.deleteTask(task);
			} /*else {
				boolean title = !originalTitle.equals(task.getTitle());
				boolean date = doAddDateAction ;
				//boolean note = doAddNoteAction && !intent.getBooleanExtra("isNew", false);
				//boolean location = 

				if(title || date){
					new AlertDialog.Builder(getActivity()).setMessage("Save Changes")
									.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											saveReminderAndfinish();
										}
									}).setNegativeButton("No", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											getActivity().finish();
										}
									}).create().show();
				}
			}*/
		} 
		super.onPause();
	}

	public void addGeofences() {

		mRequestType = REQUEST_TYPE.ADD;

		final ReminderGeofence geofence = geofenceStore.getGeofence(reminderID);
		//task.setLocationID(Integer.toString(geofenceStore.getLocationId(reminderID)));
		
		Log.d("loc1", task.getLocationID());
		if (geofence != null) {			
			
			mGeofenceList.add(geofence.toGeofence());
			
			if (!StaticFunctions.servicesConnected(getActivity())) {
				return;
			}
			
			mLocationClient = new LocationClient(getSherlockActivity(), this,
					this);

			// If a request is not already underway
			if (!mInProgress) {
				// Indicate that a request is underway
				mInProgress = true;
				// Request a connection from the client to Location Services
				mLocationClient.connect();
			} else {
				/*
				 * A request is already underway. You can handle this situation
				 * by disconnecting the client, re-setting the flag, and then
				 * re-trying the request.
				 */
			}
		}

	}

	/**
	 * Start a request to remove geofences by calling LocationClient.connect()
	 */
	public void removeGeofences() {
		// If Google Play services is unavailable, exit
		// Record the type of removal request
		mRequestType = REQUEST_TYPE.REMOVE_LIST;
		
		if (!StaticFunctions.servicesConnected(getSherlockActivity())) {
			return;
		}
		Geofence geofence;
		try {
			geofence = geofenceStore.getGeofence(reminderID).toGeofence();
		} catch (NullPointerException e) {
			// TODO: handle exception
			geofence = null;
		}
		if(geofence!=null) {
			geofenceStore.clearGeofence(reminderID);
			// Store the list of geofences to remove
			mGeofencesToRemove = new ArrayList<String>();
			mGeofencesToRemove.add(reminderID);
			mLocationClient = new LocationClient(getSherlockActivity(), this, this);
			// If a request is not already underway
			if (!mInProgress) {
				// Indicate that a request is underway
				mInProgress = true;
				// Request a connection from the client to Location Services
				mLocationClient.connect();
			} else {
				
			}
		}
		
	}

	@Override
	public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
		// TODO Auto-generated method stub
		mInProgress = false;
		mLocationClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub

		// Location loc = mLocationClient.getLastLocation();

		switch (mRequestType) {
		case ADD:
			// Get the PendingIntent for the request
			PendingIntent mTransitionPendingIntent = getTransitionPendingIntent();
			// Send a request to add the current geofences
			mLocationClient.addGeofences(mGeofenceList,
					mTransitionPendingIntent, this);
			
			break;

		case REMOVE_LIST:
			mLocationClient.removeGeofences(mGeofencesToRemove, this);
			break;
		}
	}

	private PendingIntent getTransitionPendingIntent() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getSherlockActivity(),
				NotifyService.class);
		intent.putExtra(NotifyService.NOTIFICATION_ID, Integer.parseInt(task.getReminderID()));
		intent.putExtra(NotifyService.TASK_TITLE, task.getTitle());

		// Return the PendingIntent
		return PendingIntent.getService(getSherlockActivity(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		// Turn off the request flag
		mInProgress = false;
		// Destroy the current location client
		mLocationClient = null;
	}

	@Override
	public void onRemoveGeofencesByPendingIntentResult(int statusCode,
			PendingIntent pendingIntent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemoveGeofencesByRequestIdsResult(int statusCode,
			String[] geofenceRequestIds) {
		// TODO Auto-generated method stub
		// If removing the geocodes was successful
		if (LocationStatusCodes.SUCCESS == statusCode) {

		} else {
			
		}
		// Indicate that a request is no longer in progress
		mInProgress = false;
		// Disconnect the location client
		mLocationClient.disconnect();

	}
	
	public void setDateActionValues(String actionType, Calendar oldCalendar, Calendar newCalendar) {
		this.doAddDateAction = true;
		setActionType(actionType);
		setOldCalendar(oldCalendar);
		setNewCalendar(newCalendar);
	}

	/**
	 * adds Action on changes in calendar
	 * @param actionType either {@link Actions#ACTION_TYPE_TIME} or {@link Actions#ACTION_TYPE_DATE}
	 * @param oldCalendar old calendar instance
	 * @param newCalendar new calendar instance
	 */
	public void addDateAction(String actionType, Calendar oldCalendar, Calendar newCalendar) {
		String statement = null;
		String newDateString = DateUtils.formatDateTime(getActivity(), newCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE)
				+ ", " + DateUtils.formatDateTime(getActivity(), newCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
		String newTimeString = DateUtils.formatDateTime(getActivity(), newCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME)
				+ " on " + DateUtils.formatDateTime(getActivity(), newCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE);
		Actions action = new Actions();
		action.setActionID("3"); // kisi kaam ka code nahi hai
		action.setActionTimeStamp(Calendar.getInstance().getTimeInMillis());
		action.setActionType(actionType);
		action.setTaskID(task.getUniversalID());
		action.setUniqueId(Actions.generateUniqueId(getActivity()));
		action.setUserID(StaticFunctions.getUserFbId(getActivity()));
		action.setUsername(StaticFunctions.getName(getActivity()));
		
		if(actionType.equals(Actions.ACTION_TYPE_TIME)) {
			statement = action.getUsername() + " changed the time of task to " + newTimeString;
		}
		if(actionType.equals(Actions.ACTION_TYPE_DATE)) {
			statement = action.getUsername() + " changed the date of task to " + newDateString;
		}
		action.setActionValue(statement);
		actionsToAdd.add(action);
	}
	/**
	 * sends action to web
	 * @param action
	 */
	protected void sendActionToWeb(ArrayList<Actions> actions) {
		Intent sendActionIntent = new Intent(getActivity(), SendActionIntentService.class);
		sendActionIntent.putExtra("actions", actions);
		getActivity().startService(sendActionIntent);
		
	}
	/**
	 * adds edit action
	 * @param newTitle
	 */
	public void addEditAction(String newTitle) {
		Actions action = new Actions();
		action.setActionID("3"); // kisi kaam ka code nahi hai
		action.setActionTimeStamp(Calendar.getInstance().getTimeInMillis());
		action.setActionType(Actions.ACTION_TYPE_EDIT);
		action.setTaskID(task.getUniversalID());
		action.setUniqueId(Actions.generateUniqueId(getActivity()));
		action.setUserID(StaticFunctions.getUserFbId(getActivity()));
		action.setUsername(StaticFunctions.getName(getActivity()));
		action.setActionValue(newTitle);
		if(!actionsToAdd.contains(action))
			actionsToAdd.add(action);
	}
	
	public void setNoteActionValues() {
		this.doAddNoteAction = true;
	}
	
	public void addNoteAction() {
		Actions action = new Actions();
		action.setActionID("3"); // kisi kaam ka code nahi hai
		action.setActionTimeStamp(Calendar.getInstance().getTimeInMillis());
		action.setActionType(Actions.ACTION_TYPE_NOTE);
		action.setTaskID(task.getUniversalID());
		action.setUniqueId(Actions.generateUniqueId(getActivity()));
		action.setUserID(StaticFunctions.getUserFbId(getActivity()));
		action.setUsername(StaticFunctions.getName(getActivity()));
		action.setActionValue(action.getUsername() + " changes the Note.");
		if(!actionsToAdd.contains(action))
			actionsToAdd.add(action);
	}
	/**
	 * Object to hold People and View 
	 * @deprecated Completely temporary code. exists because of the current poor implementation
	 */
	private class RemoveViewObject{
		private People people;
		private View view;
		/**
		 * @deprecated see {@link RemoveViewObject}
		 */
		public RemoveViewObject(View view, People people) {
			this.people = people;
			this.view = view;
		}
		public People getPeople() {
			return people;
		}
		public View getView() {
			return view;
		}
	}
	boolean delete= false;
	
	/**
     * delete alert dialog builder
     * @param himself user removing himself?
	 * @return delete alert dialog builder
	 */
	public AlertDialog.Builder getDeleteDialogBuilder(final RemoveViewObject removeViewObject, final boolean creator) {
		String title = "";
		People people = removeViewObject.getPeople();
		if(people.getEmail().equals(StaticFunctions.getUserEmail(getActivity())) && !creator) {
			title = "Removing yourself will delete the task. Continue?";
			delete = true;
		} else if(people.getEmail().equals(StaticFunctions.getUserEmail(getActivity())) && creator) {
			title = "Really remove yourself?";
		} else title = "Really remove the person?";
		return new AlertDialog.Builder(getActivity()).setTitle(title)
							.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if(delete){
										// delete the task,finish the activity and send acknowledgment
										dbAdapter.deleteTask(task);
										ReceivedTaskFragment.acknowledgeDelivery(getActivity(), task, Task.TASK_ACKNOWLEDGMENT_REMOVED);
										getActivity().finish();
									} else{
										removePeopleOrNot = false;
										peopleList.remove(removeViewObject.getPeople());
										if(!creator)
											peopleInvitedLinearLayout.removeView(removeViewObject.getView());	
										else {
											acknowledgment = Task.TASK_ACKNOWLEDGMENT_REMOVED;
											assigned = 2;
											dbAdapter.updateAPeopleColumn(task.getUniversalID(),
													StaticFunctions.getUserFbId(getActivity()), People.PEOPLE_HAS_ACCEPTED, Task.TASK_ACKNOWLEDGMENT_REMOVED);
											View view = removeViewObject.getView();
											final TextView join = (TextView) view.findViewById(R.id.pingUser);
											join.setVisibility(View.VISIBLE); join.setText("join");
											final ImageButton removeUser = (ImageButton) view.findViewById(R.id.removeUser);
										    removeUser.setVisibility(View.GONE);
											final TextView status = (TextView) view.findViewById(R.id.userJoined);
											status.setVisibility(View.INVISIBLE);
											join.setOnClickListener(new View.OnClickListener() {
												@Override
												public void onClick(View v) {
													removeUser.setVisibility(View.VISIBLE);
													status.setVisibility(View.VISIBLE); status.setText("joined");
													join.setVisibility(View.GONE);
													peopleList.add(0, removeViewObject.getPeople());
													assigned = 3;
													removePeopleOrNot = false;
													acknowledgment  = Task.TASK_ACKNOWLEDGMENT_ACCEPT;
													dbAdapter.updateAPeopleColumn(task.getUniversalID(),
															StaticFunctions.getUserFbId(getActivity()), People.PEOPLE_HAS_ACCEPTED, Task.TASK_ACKNOWLEDGMENT_ACCEPT);
													
												}
											});
											ReceivedTaskFragment.acknowledgeDelivery(getActivity(), task, acknowledgment);
										}
									}
								}})
							.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}});
														
	}
	/**
	 * ping alert dialog builder
	 * @return ping alert dialog builder
	 */
	public AlertDialog.Builder getPingDialogBuilder(People people){
		return new AlertDialog.Builder(getActivity()).setTitle("Ping " + people.getName())
							.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									//code to ping the people
								}})
							.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();									}});
	}
	/**
	 * Alert Dialog Builder to show alert in case of user no signed in
	 * @return {@link AlertDialog#Builder}
	 */
	public AlertDialog.Builder getSignInAlert(){
		return new AlertDialog.Builder(getActivity()).setMessage("You need to sign in with facebook to assign task")
							.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									//start facebook sign in
									dialog.cancel();
								}})
							.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
	}
	
	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public Calendar getOldCalendar() {
		return oldCalendar;
	}

	public void setOldCalendar(Calendar oldCalendar) {
		this.oldCalendar = oldCalendar;
	}

	public Calendar getNewCalendar() {
		return newCalendar;
	}

	public void setNewCalendar(Calendar newCalendar) {
		this.newCalendar = newCalendar;
	}
}
