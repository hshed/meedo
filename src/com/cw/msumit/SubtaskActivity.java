package com.cw.msumit;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.cw.msumit.adapters.SubtaskAdapter;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.fragments.SubtaskFragment;
import com.cw.msumit.fragments.SubtaskFragment.OnSubtaskExitListener;
import com.cw.msumit.utils.StaticFunctions;

public class SubtaskActivity extends SherlockFragmentActivity implements OnSubtaskExitListener{

	ActionBar actionBar;
	ListView subtaskListView;
	EditText subtaskEditText;
	ImageButton addsubTaskButton;
	SubtaskAdapter adapter;
	ArrayList<HashMap<String, String>> Subtasks;
	public static int height, width;
	Intent intent;
	String reminderID;
	String[] subtaskArray;
	int Case;
	SubtaskFragment subtaskFragment;

	
	//this activity is started either by an intent to see the already added subtasks or to actually add the subtask
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subtask);
		DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        actionBar = getSupportActionBar();
        
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(45, 188,
				215)));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Subtasks");
		subtaskEditText = (EditText) findViewById(R.id.subtaskEditBox);
		addsubTaskButton = (ImageButton) findViewById(R.id.addSubTask);
		/* Getting the intent that started the activity */
		intent= getIntent();
		reminderID=new String();
		subtaskArray=new String[intent.getExtras().getStringArray("subtaskArray").length]; //sets the length and hence is 
		//not flexible
		reminderID= intent.getExtras().getString("ReminderID"); //would be 0 if started otherwise
		subtaskArray= intent.getExtras().getStringArray("subtaskArray");
		Log.d("Sent Data", subtaskArray[0]);//would be 0 and not null if started otherwise;
		Subtasks = new ArrayList<HashMap<String, String>>();
		//three cases
		
		//CASE 1
		if (reminderID.equals("0") && !subtaskArray[0].equals("0")){
			//this is the case when some unsaved data is there
			//it should return all the new values
			//open the keyboard
			//turn the array into arraylist
			getWindow().setSoftInputMode(
	        	    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			
			for(int i=0; i<subtaskArray.length; i++){
				
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("Subtask", subtaskArray[i]);
				
				Subtasks.add(map);
				
			}
			Case=1;
		}
		//CASE 2
		if (!reminderID.equals("0") && subtaskArray[0].equals("0")){
			//this is when the subtask has been opened for view
			//any change in data should be commited to database immediately in this case
			
			DatabaseHandler dbAdapter = new DatabaseHandler(getApplicationContext());
			Subtasks=dbAdapter.getArrayListOfSubtask(reminderID);
			if(Subtasks==null) {
				Subtasks = new ArrayList<HashMap<String, String>>();
			}
			getWindow().setSoftInputMode(
	        	    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			Case=2;
		}
		//CASE 3
		if(!reminderID.equals("0") && !subtaskArray[0].equals("0")){
			//this case is never being called as when reminder id is known
			//the subtask can automatically be created
			Case=3;
		}
		//CASE 4
		if (reminderID.equals("0") && subtaskArray[0].equals("0")) {
			//activity started from quickadd zero
			//open the keyboard
			getWindow().setSoftInputMode(
	        	    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			Case = 4;
		}
 /* Basic functions in this Activity */	
	if(savedInstanceState!=null) {
		subtaskFragment = (SubtaskFragment) getSupportFragmentManager().getFragment(savedInstanceState, "details");
		}
	else {
		// During initial setup, plug in the details fragment.
		subtaskFragment = SubtaskFragment.newInstance(Subtasks, Case, reminderID);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, subtaskFragment).commit();
	} 
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onExit(ArrayList<HashMap<String, String>> subtasks, ArrayList<Boolean> checked) {
		// TODO Auto-generated method stub
		//Log.d("OnBackpressed", "Called");
		if (subtasks != null && !subtasks.isEmpty()){
			String [] stringArray= StaticFunctions.changeListToStringArray(subtasks, "Subtask");
		
		switch (Case) {
		case 1:
			intent.putExtra("ResultArray", stringArray);
			setResult(RESULT_OK, intent);
			//finish();
			
			break;
		case 2:
			//data will be saved and the starting activity will be refreshed
			//save all the subtask data replacing the one that already exists
			//update subtask count in task table
			if(Subtasks!=null) {
			DatabaseHandler dbHandler=new DatabaseHandler(getApplicationContext());
			dbHandler.deleteAllSubtask(reminderID);
			dbHandler.addArrayListofSubtask(Subtasks, reminderID);
			dbHandler.updateSubtaskCount(reminderID, Subtasks.size());
			dbHandler.updateSubtaskBooleans(reminderID, checked,
					StaticFunctions.convertIntoSimpleArrayList(Subtasks));
			//here i need the arraylist from the fragment
			
			//finish();
			}
			break;
			
		case 3:
			//data will be saved and the starting activity will be refreshed
			//save all the subtask data replacing the one that already exists
			//update subtask count in task table
			if(Subtasks!=null) {
				DatabaseHandler dbHandler=new DatabaseHandler(getApplicationContext());
				dbHandler.deleteAllSubtask(reminderID);
				dbHandler.addArrayListofSubtask(Subtasks, reminderID);
				dbHandler.updateSubtaskCount(reminderID, Subtasks.size());
				//finish();
				}
				break;
			
		case 4:
			//data will be returned, null will be returned if no changes have been made
			intent.putExtra("ResultArray", stringArray);
			setResult(RESULT_OK, intent);
			break;

		default:
		}
		}
		else {
			//if subtask is null or empty
			String [] stringArray= new String [0];
			switch (Case) {
			case 1:
				intent.putExtra("ResultArray", stringArray);
				setResult(RESULT_OK, intent);

				break;
			case 2:
				DatabaseHandler dbHandler=new DatabaseHandler(getApplicationContext());
				dbHandler.deleteAllSubtask(reminderID);
				dbHandler.updateSubtaskCount(reminderID, 0);

				break;
			case 3:
				DatabaseHandler dbHandler1=new DatabaseHandler(getApplicationContext());
				dbHandler1.deleteAllSubtask(reminderID);
				dbHandler1.updateSubtaskCount(reminderID, 0);

				break;
			case 4:
				intent.putExtra("ResultArray", stringArray);
				setResult(RESULT_OK, intent);

				break;

			default:
				break;
			}
		}
	}
	
	

}
