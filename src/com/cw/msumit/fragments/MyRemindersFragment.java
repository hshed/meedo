package com.cw.msumit.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.cw.msumit.R;
import com.cw.msumit.RemindersDetails;
import com.cw.msumit.SubtaskActivity;
import com.cw.msumit.adapters.ReminderListAdapter;
import com.cw.msumit.adapters.SwipeDismissListViewTouchListener;
import com.cw.msumit.adapters.UndoBarController;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.objects.People;
import com.cw.msumit.objects.Reminder;
import com.cw.msumit.objects.Task;
import com.cw.msumit.services.ReceiveTaskClient;
import com.cw.msumit.services.ScheduleClient;
import com.cw.msumit.utils.ReminderGeofenceStore;
import com.cw.msumit.utils.StaticFunctions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;

public class MyRemindersFragment extends SherlockFragment implements UndoBarController.UndoListener,  UndoBarController.HideListener,
									ConnectionCallbacks, OnConnectionFailedListener, OnRemoveGeofencesResultListener{
	DatabaseHandler dbAdapter;
	String[] ENTRIES, fiveENTRIES;
	boolean isDualPane;
	int mCurCheckPosition = 0;
	ReminderListAdapter adapter;
	public static String KEY_REMINDER_TITLE = "reminderTitle";
	String KEY_NAME = "user_friend_name", KEY_PEOPLE = "people", KEY_DATE = "date", KEY_TIME = "time", 
			KEY_PLACE = "place",KEY_REMINDER_STATUS_IMAGE = "reminderStatusImage", Date, Time, Repeat,
			mList, noteString="", Category="Personal";
	private static String SUBTASK_HASH_KEY = "Subtask";
	ListView list;
	TextView titleOfReminder, personInvolved, reminderDate, reminderTime,calendarText,
			reminderPlace, calendar, note, subtaskButton, importantOrNot, subtaskNumber,
			noteText, subtaskText;
	String fontPath = "fonts/Roboto-Bold.ttf", fontPath2 = "fonts/Roboto-Medium.ttf";
	AssetManager assetManager;
	Typeface plain;
	EditText quickReminder;
	ArrayList<Task> myReminderItems, temptasks;
	Task newTask;
	final int CALENDAR = 5;
	final int SUBTASK = 2;
	final int IMPORTANT = 3;
	final int QUICKTIME = 4;
	Typeface medium;
	LinearLayout fourButtons;
	View listFragmentView;
	boolean impOrNot = false;
	ImageButton speakButton, addButton;
    ArrayList<HashMap<String, String>> Subtasks;
    CheckBox checkBox;
    RelativeLayout rLayout;
    private UndoBarController mUndoBarController;
    int LastPosition=-1;
    RelativeLayout quickRelativeLayout;
    //this value is to see if the undobar is visible or not and if yes from which swipe
    ContentValues values =new ContentValues();
    ScheduleClient scheduleClient;
    private OnTaskStatusChanged onTaskStatusChanged;
    ReceiveTaskClient receiveTaskClient;
    private LocationClient mLocationClient;
	// Flag that indicates if a request is underway.
	private boolean mInProgress = false;
	List<String> mGeofencesToRemove;
	private ReminderGeofenceStore geofenceStore;

    //Boolean isUndoBarVisible=false;
    
    public MyRemindersFragment() {
	    setRetainInstance(true);
	}
    
    public interface OnTaskStatusChanged {
    	void onStatusChanged();
    }
    
    @Override
	public void onStop() {
		// TODO Auto-generated method stub
    	if(scheduleClient != null)
            scheduleClient.doUnbindService();
    	//if(receiveTaskClient != null)
         //   receiveTaskClient.doUnbindService();
		super.onStop();
	}

	public static MyRemindersFragment newInstance(String list) { 
		MyRemindersFragment f= new MyRemindersFragment();
		Bundle bundle = new Bundle();
		bundle.putString("list", list);
		f.setArguments(bundle);
		return f;
	}
    
    @Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
    	//onTaskAddedListener = (OnTaskAddedListener) activity;
		super.onAttach(activity);
	}

	protected String  getNoteString() {
		return noteString;
	}
    
    protected void  setNoteString(String r) {
  		this.noteString=r;
  	}
    
    void updateTask (String date, String time, String repeat) {
    	newTask.setDate(date);
		newTask.setTime(time);
		newTask.setRepeat(repeat);
		this.Date=date;
		this.Time=time;
		this.Repeat=repeat;
    }

	public int getShownIndex() {
		return getArguments().getInt("index", 0);
	}
	
	public String getShownCategoryTask() {
		return getArguments().getString("index");
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, "New")
		.setIcon(R.drawable.content_new)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			//Toast.makeText(getSherlockActivity(), "Home", Toast.LENGTH_SHORT).show();
			return (true);

		case 0:
			//Toast.makeText(getSherlockActivity(), "New Reminder", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.setClass(getActivity(), RemindersDetails.class);
			//as per the index of the listitem determine the reminder ID
			intent.putExtra("reminderID", "0");
			intent.putExtra("universalID", StaticFunctions.generateUniversalId(getActivity()));
			intent.putExtra("isNew", true);
			startActivity(intent);
			return true;
	
			
		}
		return super.onOptionsItemSelected(item);
	}

	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setHasOptionsMenu(true);
		getSherlockActivity().getSupportActionBar().setTitle(" "+getArguments().getString("list"));
		if (getArguments().getString("list") != null) {
				
				}		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Create a new service client and bind our activity to this service
        scheduleClient = new ScheduleClient(getActivity());
        scheduleClient.doBindService();
		geofenceStore = new ReminderGeofenceStore(getSherlockActivity());
        
        onTaskStatusChanged = (OnTaskStatusChanged) getSherlockActivity();
        
        //Creating FragmentView
		if (container == null) {

			return null;
		}

		receiveTaskClient = new ReceiveTaskClient(getActivity());
		//receiveTaskClient.doBindService();
		receiveTaskClient.checkForTasks();
		
		//list fragment view is instantiated twice one in on resume and one in oncreateview
		listFragmentView = inflater.inflate(R.layout.reminder_list_view, container, false);
		assetManager = getActivity().getAssets();
		plain = Typeface.createFromAsset(assetManager, fontPath);
		medium=Typeface.createFromAsset(assetManager, fontPath2);
		fourButtons = (LinearLayout) listFragmentView
				.findViewById(R.id.fourButtons);
		fourButtons.setVisibility(View.GONE);
		
		addButton = (ImageButton) listFragmentView
				.findViewById(R.id.addQuickreminder);
		rLayout= (RelativeLayout) listFragmentView
				.findViewById(R.id.reminderRelativeLayout);
		
		mUndoBarController = new UndoBarController(listFragmentView.findViewById(R.id.undobar), this, this);
		
        rLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("onclickfragment", "called");
				if (!v.isClickable()) {
				fourButtons.setVisibility(View.GONE);
				}
			}
		});
		
		// Populating the listview with the data from the database this must be called on onresume
		 
		//Reminder Object to begin with
		newTask = new Task();
		Subtasks = new ArrayList<HashMap<String,String>>();
		Date="N-N-N";
		Time="N:N";
		Repeat="N";

		//Instantiating the fourbuttonviews
		calendar = (TextView) listFragmentView.findViewById(R.id.calendarText);
		note = (TextView) listFragmentView.findViewById(R.id.note);
		subtaskButton = (TextView) listFragmentView.findViewById(R.id.subtask);
		speakButton = (ImageButton) listFragmentView
				.findViewById(R.id.tts1);
		importantOrNot = (TextView) listFragmentView.findViewById(R.id.ratingStar);
		
		calendar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String rString = newTask.getRepeat();
				String[] dString = newTask.getDate().split("-");
				String[] tString = newTask.getTime().split(":");
				
				
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
				repeatBundle.putBoolean("noRepeat", noRepeat);
				repeatBundle.putBoolean("dailyRepeat", dailyRepeat);
				repeatBundle.putBooleanArray("weeklyRepeatDays", weeklyRepeatDays);
				repeatBundle.putInt("monthlyRepeatDay", monthlyRepeatDay);
				//send the reminder ID too so that this reminder id gets updated.
				SherlockDialogFragment calendarFragment = CalendarFragment.newInstance
						(dateAndTime, repeatBundle);
				//set the target fragment
				calendarFragment.setTargetFragment(MyRemindersFragment.this, 122);
				calendarFragment.show(getSherlockActivity().getSupportFragmentManager(), "myReminderList");
				

			}
		});
		
		note.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SherlockDialogFragment newFragment = new NoteFragment();
				newFragment.setTargetFragment(MyRemindersFragment.this, 4104);
				//here it needs to be changed somewhat
				newFragment.show(getActivity().getSupportFragmentManager(), "newtask");
			}
		});
		
		importantOrNot.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!impOrNot){
					importantOrNot.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.starre_d), null, null);
					impOrNot = true;
				} else{	
					importantOrNot.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.star), null, null);
					impOrNot = false;
				}
			}
		});
		
		subtaskButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(getActivity(), ReminderListAdapter.list.toString(), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getActivity(), SubtaskActivity.class);
				intent.putExtra("ReminderID", "0");
				if (Subtasks==null || Subtasks.isEmpty()) {
					String [] s= {"0"};
					intent.putExtra("subtaskArray", s);
				}
				else {
					String [] s = StaticFunctions.changeListToStringArray(Subtasks, SUBTASK_HASH_KEY);
					intent.putExtra("subtaskArray", s);
				}
				startActivityForResult(intent, SUBTASK);
				getSherlockActivity().overridePendingTransition( R.anim.slide_up_dialog, android.R.anim.slide_in_left );
			}
		});

		//Text to speech implementation
		PackageManager pm = getActivity().getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			speakButton.setEnabled(false);
		}

		else {
			speakButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					startSpeechToText();

				}

				private void startSpeechToText() {
					// TODO Auto-generated method stub
					Intent intent = new Intent(
							RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					startActivityForResult(intent, 1);

				}

			});
		}

		
//this is the place where the reminder would be added to the database as well as to the listview
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//here are the properties for the new task
				String reminderID= Integer.toString(StaticFunctions.getLastReminder(getActivity())+1);
				Log.d("New Reminder ID", reminderID);
				String Title = "0";
				int Subtask=0;
				
				String LocationID="0";
				String LocationJson = "";
				String LocationReminder="0";
				int Important=0;
				
				int Synced=-1;
				String creatorID=StaticFunctions.getUserFbId(getActivity());
				
				//Setting the properties
				//Title
				String q = quickReminder.getText().toString();
				if (!StaticFunctions.isWhiteSpace(q)) {
					Title = q;
				}
				
				//subtask
				if (Subtasks!=null && !Subtasks.isEmpty()) {
					Subtask= Subtasks.size();
				}
				
				//Important
				/**if (importantOrNot.isChecked()) {
					Important=1;
				}**/
				if(impOrNot) {
					Important = 1;
				}
				//New task should only be added if title is not null
				if (!Title.equals("0")) {
				newTask.setReminderID(reminderID);
				newTask.setUniversalID(StaticFunctions.generateUniversalId(getActivity()));
				newTask.setCreatorID(StaticFunctions.getUserFbId(getActivity()));
				newTask.setTitle(Title);
				newTask.setLocationJson(LocationJson);
				newTask.setSubtasks(Subtask);
				newTask.setDate(Date);
				newTask.setTime(Time);
				newTask.setNote(noteString);
				newTask.setLocationID(LocationID);
				newTask.setLocationReminder(LocationReminder);
				newTask.setImportant(Important);
				newTask.setCategory(Category);
				newTask.setCreatorID(creatorID);
				newTask.setSynced(Synced);
				newTask.setRepeat(Repeat);
				newTask.setOrderBy(StaticFunctions.sortString(Date, Time, Important));
				noteString="";
				//note.setImageDrawable(getResources().getDrawable(R.drawable.note));
				note.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.note), null, null);
				//newTask will be stored in Database
				//set notification for the given date and time
				
				Reminder reminder = new Reminder(scheduleClient, newTask);
				reminder.remind();
				
				DatabaseHandler dbHandler= new DatabaseHandler(getActivity());
				dbHandler.addReminders(newTask);
				People thisPeople = new People(true, StaticFunctions.getName(getActivity()), 
						StaticFunctions.getUserEmail(getActivity()), StaticFunctions.getUserFbId(getActivity()));
				thisPeople.setTaskId(newTask.getUniversalID());
				thisPeople.setAccepted(Task.TASK_ACKNOWLEDGMENT_ACCEPT);
				thisPeople.setHasMeedo(true);
				dbHandler.addPeopleToDb(thisPeople);
				//subtasks stored in subtaskJustAdded will go into Database here
				//but only if there are any subtasks
				if (Subtask != 0) {
					dbHandler.addArrayListofSubtask(Subtasks, reminderID);
				}
				myReminderItems.add(myReminderItems.size(), newTask);
				adapter.notifyDataSetChanged();
				//list.setSelection(myReminderItems.size());
				list.post( new Runnable() {
				    @Override
				    public void run() {
				    //call smooth scroll
				    list.smoothScrollToPosition(myReminderItems.size());
				    
				    }
				  });
				
				
				//nullify the task now
				newTask = new Task();
				//nullify the subtask
				Subtasks.clear();
				/*//nullify the calendar
				Date="";
				Time="0";
				Repeat="0-0-0-0-0-0-0";*/
				String what=getArguments().getString("list");
				//nullify the important if its not important
				if (!what.equals("Important")) {
					Important=0;
					//importantOrNot.setChecked(false);
					importantOrNot.setCompoundDrawablesWithIntrinsicBounds
					(null, getResources().getDrawable(R.drawable.star), null, null);
					impOrNot = false;
					//importantOrNot.setCompoundDrawables(null, getResources().getDrawable(R.drawable.star), null, null);
				}
				
				//refresh the view especially the four buttons and textview;
				//subtaskButton.setImageDrawable(getResources().getDrawable(R.drawable.no_subtask));
				subtaskButton.setCompoundDrawablesWithIntrinsicBounds
				(null, getResources().getDrawable(R.drawable.no_subtask), null, null);
				//subtaskNumber.setText("");
				quickReminder.setText("");
				if (!what.equals("Today")) {
				//calendar.setImageDrawable(getResources().getDrawable(R.drawable.bell));
				calendar.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.bell), null, null);
				calendar.setText("Reminder");
				}
			
				}
			 

			}
		});
		// textview to add
		quickReminder = (EditText) listFragmentView
				.findViewById(R.id.enterQuickreminder);
	
		quickReminder.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.length() != 0) {
					fourButtons.setVisibility(View.VISIBLE);
				} else {
					fourButtons.setVisibility(View.GONE);
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});

		return listFragmentView;

	}

//This is where all the data from different activities are obtained
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Log.d("OnActivityResult", data.toString());
		if (data !=null) {
		if (requestCode == 1) {
			if (resultCode == Activity.RESULT_OK) {
				ArrayList<String> matches = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				quickReminder = (EditText) getView().findViewById(
						R.id.enterQuickreminder);
				quickReminder.setText(matches.get(0).toString());
			}
		}
		
		if (requestCode==CALENDAR) {
			//change the view of calendar icon, add text below it and populate the reminder object 
			//with date and time
			String [] s= data.getStringArrayExtra("resultArray");
			Date=s[0]; Log.d("Date", Date);
			Time=s[1]; Log.d("Time", Time);
			Repeat=s[2]; Log.d("Repeat", Repeat);
			String dateTimeString= StaticFunctions.Date(Date) + StaticFunctions.time(Time);
			//change the calendar's UI
			if (Date.equals("0") && Time.equals("0") && Repeat.equals("0-0-0-0-0-0-0")){
				//no change in the UI
				//calendar.setImageDrawable(getResources().getDrawable(R.drawable.bell));
				calendar.setCompoundDrawables(null, getResources().getDrawable(R.drawable.bell), null, null);
				calendar.setText("Reminder");
			}
			
			else {
				//calendar.setImageDrawable(getResources().getDrawable(R.drawable.bell_orange));
				calendar.setCompoundDrawables(null, getResources().getDrawable(R.drawable.bell_orange), null, null);
				calendar.setText(dateTimeString);
				
			}
			
		}
		
		if (requestCode==SUBTASK) {
			//Store the subtask as a String array that is accessible to the overall class
			//store the number of subtask and populate the reminder object
			String[] s= data.getStringArrayExtra("ResultArray");
			/*Log.d("datata", s[0].toString());
			Log.d("datal", Integer.toString(s.length));*/
			
			if (s.length>0) {//this will go to database on clicking add button
			Subtasks = StaticFunctions.changeStringArrayintoList(s, SUBTASK_HASH_KEY);
			//change the image
			Drawable bmDrawable = StaticFunctions.getSubtaskDrawable(Subtasks.size(), getSherlockActivity());
			subtaskButton.setCompoundDrawablesWithIntrinsicBounds(null, bmDrawable, null, null);
			newTask.setSubtasks(Subtasks.size());
			}
			
			else {
				subtaskButton.setCompoundDrawablesWithIntrinsicBounds(null,
						getResources().getDrawable(R.drawable.no_subtask), null, null);
			}
		}
		}
		else {
			Log.d("Data", "is null");
		}
		
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("curChoice", mCurCheckPosition);
	}

	/**
	 * Helper function to show the details of a selected item, either by
	 * displaying a fragment in-place in the current UI, or starting a whole new
	 * activity in which it is displayed.
	 */
	
	
	//Function called to start show the details of a reminder;
	void showDetails(int index) {
		mCurCheckPosition = index;

		/*if (isDualPane) {
			// We can display everything in-place with fragments, so update
			// the list to highlight the selected item and show the data.
			// getListView().setItemChecked(index, true);

			// Check what fragment is currently shown, replace if needed.
			RemindersDetailsFragment details = (RemindersDetailsFragment) getSherlockActivity()
					.getSupportFragmentManager().findFragmentById(R.id.details);
			if (details == null || details.getShownIndex() != index) {
				// Make new fragment to show this selection.
				details = RemindersDetailsFragment.newInstance(index);

				// Execute a transaction, replacing any existing fragment
				// with this one inside the frame.
				FragmentTransaction ft = getActivity()
						.getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.details, details);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
			}
*/
		/*} else {*/
			// Otherwise we need to launch a new activity to display
			// the dialog fragment with selected text.
			Intent intent = new Intent();
			intent.setClass(getActivity(), RemindersDetails.class);
			//as per the index of the listitem determine the reminder ID
			intent.putExtra("reminderID", StaticFunctions.reminderIDOFOrderedTask(myReminderItems, index));
			intent.putExtra("universalID", StaticFunctions.universalIdOfTask(myReminderItems, index));
			//Toast.makeText(getActivity(), StaticFunctions.reminderIDOFOrderedTask(myReminderItems, index), Toast.LENGTH_SHORT).show();
			startActivity(intent);
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.d("OnResume", "Called");
		values.put("isUndoBarVisible", false);
        values.put("where", 0);
		
	/*	InputMethodManager inputMethodManager=(InputMethodManager) 
				getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    inputMethodManager.toggleSoftInputFromWindow(quickReminder.getApplicationWindowToken(), 
	    		InputMethodManager.HIDE_IMPLICIT_ONLY, 0);*/
		//new LoadTask().execute((String) null);
		
		/*myReminderItems = dbAdapter.getArrayofReminders("something", "0");
		adapter.notifyDataSetChanged();*/
        quickRelativeLayout= (RelativeLayout) listFragmentView.findViewById(R.id.quickReminder);
	    String what=getArguments().getString("list");
		myReminderItems = new ArrayList<Task>();
		dbAdapter = new DatabaseHandler(getActivity());
		//dbAdapter.deleteAllPending();
		/*
		 * Setting the properties
		 * 
		 * */
		if (what.equals("Active tasks")) {
			// do nothing
		}
			
		else if (what.equals("Completed Tasks")) {
			quickRelativeLayout.setVisibility(View.GONE);
			LinearLayout layout= (LinearLayout) listFragmentView.findViewById(R.id.undobar);
			layout.bringToFront();
		}

		else if (what.equals("Today")) {
			//calendar.setImageDrawable(getResources().getDrawable(R.drawable.bell_orange));
			calendar.setCompoundDrawables(null, getResources().getDrawable(R.drawable.bell_orange), null, null);
			calendar.setText("today");
			Date=StaticFunctions.getTodaysDate();

		} else if (what.equals("Assigned to someone")) {
			quickRelativeLayout.setVisibility(View.GONE);

		} else if (what.equals("Important")) {
			//importantOrNot.setChecked(true);
			impOrNot = true;
			importantOrNot.setCompoundDrawables(null, getResources().getDrawable(R.drawable.starre_d), null, null);

		} 
		/*else if (what.equals("Shopping")) {
			subtaskButton.setImageDrawable(getResources().getDrawable(R.drawable.shopping));
			subtaskText.setText("Items");
			quickReminder.setHint("List e.g. Grocery..");
		}*/
		
		else {
			Category=what;
		}
		
		
		
		//myReminderItems = dbAdapter.getArrayofReminders("something", "0");
		myReminderItems = StaticFunctions.getTasks(what, getActivity());
		//set the temporary tasks
		Log.d("code", "I'm here");
		temptasks=(ArrayList<Task>) myReminderItems.clone();
		//order the tasks here
		myReminderItems=StaticFunctions.OrderedTasks(myReminderItems);
		
		
		list = (ListView) listFragmentView.findViewById(R.id.reminderListView);
		
		// Getting adapter by passing xml data ArrayList
		adapter = new ReminderListAdapter(getSherlockActivity(),myReminderItems, what);
		// list.setBackgroundResource(R.drawable.rounded_listview);
		list.setAdapter(adapter);
		
		//list = (ListView) listFragmentView.findViewById(R.id.reminderListView);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			
            
			showDetails(position);

			}
		});
		
		CompleteSwipeDismissListener touchListener =
                new CompleteSwipeDismissListener(
                        list,
                        new CompleteSwipeDismissListener.OnDismissCallback() {
                        	
							@Override
							public void onSwipeLeft(ListView listView,
									int[] reverseSortedPositions) {
								// TODO Auto-generated method stub
								if (values.getAsBoolean("isUndoBarVisible")) {
									int where = values.getAsInteger("where");
									onHide(where);
								}
								for (int position : reverseSortedPositions) {
                                    adapter.remove(adapter.getItem(position));
                                    LastPosition=position;
                                    Log.d("position", Integer.toString(position));
                                }
                                adapter.notifyDataSetChanged();
                               
                                mUndoBarController.showUndoBar(
                                        false,
                                        "Task deleted.",
                                  null, 1);
                                values.put("isUndoBarVisible", true);
                                values.put("where", 1);
                              
                               
							}

							@Override
							public void onSwipeRight(ListView listView,
									int[] reverseSortedPositions) {
								// TODO Auto-generated method stub
								if (values.getAsBoolean("isUndoBarVisible")) {
									int where = values.getAsInteger("where");
									onHide(where);
								}
								 for (int position : reverseSortedPositions) {
	                                    adapter.remove(adapter.getItem(position));
	                                    LastPosition=position;
	                                    //Log.d("index", Integer.toString(position));
	                                }
	                                adapter.notifyDataSetChanged();
	                                mUndoBarController.showUndoBar(false, "Task re-added.", null, 3);
	                                values.put("isUndoBarVisible", true);
	                                values.put("where", 3);
	                              
							}
                        });
		SwipeDismissListViewTouchListener touchListenerComplete =
                new SwipeDismissListViewTouchListener(
                        list,
                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
                        	
							@Override
							public void onSwipeLeft(ListView listView,
									int[] reverseSortedPositions) {
								// TODO Auto-generated method stub
								if (values.getAsBoolean("isUndoBarVisible")) {
									int where = values.getAsInteger("where");
									onHide(where);
								}
								for (int position : reverseSortedPositions) {
                                    adapter.remove(adapter.getItem(position));
                                    LastPosition=position;
                                    Log.d("position", Integer.toString(position));
                                }
                                adapter.notifyDataSetChanged();
                               
                                mUndoBarController.showUndoBar(false,"Task deleted.", null, 1);
                                values.put("isUndoBarVisible", true);
                                values.put("where", 1);
							}

							@Override
							public void onSwipeRight(ListView listView,
									int[] reverseSortedPositions) {
								// TODO Auto-generated method stub
								if (values.getAsBoolean("isUndoBarVisible")) {
									int where = values.getAsInteger("where");
									onHide(where);
								}
								 for (int position : reverseSortedPositions) {
	                                    adapter.remove(adapter.getItem(position));
	                                    LastPosition=position;
	                                    //Log.d("index", Integer.toString(position));
	                                }
	                                adapter.notifyDataSetChanged();
	                                
	                                mUndoBarController.showUndoBar(false, "Task completed.", null, 2);
	                                values.put("isUndoBarVisible", true);
	                                values.put("where", 2);
							}
                        });

		
		if(!getArguments().getString("list").equals("Completed Tasks")) {
			list.setOnTouchListener(touchListenerComplete);
		} else {
			list.setOnTouchListener(touchListener);
		}
		list.setOnScrollListener(touchListener.makeScrollListener());
		//this is where the list view will be refreshed
		super.onResume();
	}

	@Override
	public void onUndo(int where) {
		// TODO Perform the Undo
		values.put("isUndoBarVisible", false);
        values.put("where", 0);
		if (LastPosition >= 0) {
			adapter.add(temptasks.get(LastPosition), LastPosition);
			adapter.notifyDataSetChanged();
			//Log.d("Size of temptasks", Integer.toString(temptasks.size()));
		}
	}
	
	@Override
	public void onHide (int where) {
		
		Reminder reminder;
		Task task;
		
		values.put("isUndoBarVisible", false);
        values.put("where", 0);
		switch (where) {
		case 1:
			task = temptasks.get(LastPosition);
			removeGeofences(task.getReminderID());
			reminder = new Reminder(scheduleClient, task);
			reminder.remove();
			//delete the task
			dbAdapter.deleteTask(task);
			onTaskStatusChanged.onStatusChanged();
			temptasks= (ArrayList<Task>) myReminderItems.clone();
			break;

		case 2:
			//complete
			task = temptasks.get(LastPosition);
			removeGeofences(task.getReminderID());
			reminder = new Reminder(scheduleClient, task);
			reminder.remove();
			dbAdapter.updateAColumn(task.getReminderID(), "complete", 1);
			dbAdapter.updateAColumn(task.getReminderID(), 
					"completed_on", StaticFunctions.calendarIntoDateString(Calendar.getInstance()));
			onTaskStatusChanged.onStatusChanged();
			temptasks= (ArrayList<Task>) myReminderItems.clone();
			break;
		case 3:
			//re-add
			task = temptasks.get(LastPosition);
			reminder = new Reminder(scheduleClient, task);
			reminder.remind();
			dbAdapter.updateAColumn(task.getReminderID(), "complete", 0);
			dbAdapter.updateAColumn(task.getReminderID(), "completed_on", "N");
			onTaskStatusChanged.onStatusChanged();
			temptasks= (ArrayList<Task>) myReminderItems.clone();
			break;
			
		}
	}
	
	public void removeGeofences(String reminderID) {
		// If Google Play services is unavailable, exit
		// Record the type of removal request
		
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
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub

			mLocationClient.removeGeofences(mGeofencesToRemove, this);
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
	

	private class CompleteSwipeDismissListener extends SwipeDismissListViewTouchListener {

		public CompleteSwipeDismissListener(ListView listView,
				OnDismissCallback callback) {
			super(listView, callback);
			leftToRightColor = Color.rgb(239, 212, 61);
			doneImage = listView.getContext().getResources().getDrawable(R.drawable.redo);
			// TODO Auto-generated constructor stub
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}
	
	
}