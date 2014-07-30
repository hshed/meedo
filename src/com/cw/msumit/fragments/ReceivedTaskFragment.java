package com.cw.msumit.fragments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.cw.msumit.GCMIntentService;
import com.cw.msumit.R;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.objects.People;
import com.cw.msumit.objects.Reminder;
import com.cw.msumit.objects.Task;
import com.cw.msumit.services.AcknowledgeDeliveryIntentService;
import com.cw.msumit.services.NotifyService;
import com.cw.msumit.services.ScheduleClient;
import com.cw.msumit.utils.ReminderGeofence;
import com.cw.msumit.utils.ReminderGeofenceStore;
import com.cw.msumit.utils.StaticFunctions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ReceivedTaskFragment extends SherlockFragment implements ConnectionCallbacks, 
						OnConnectionFailedListener, OnAddGeofencesResultListener{
	/**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    Task currentTask;
    ArrayList<People> currentTaskPeoples;
    DisplayImageOptions displayOptions;
    ImageLoader imageLoader;
    Bitmap loadedBitmap;
    int width, height, transitionType;
	DatabaseHandler dbHandler;
    String locationTitle, locationAddress;
    ReminderGeofenceStore geofenceStore;
    double locationLat, locationLng;
	private LocationClient mLocationClient;
	private boolean mInProgress = false;
	List<Geofence> mGeofenceList;
	ScheduleClient scheduleClient;
	Reminder reminder;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    
    public static ReceivedTaskFragment create(int pageNumber,Task task, ArrayList<People> peoples) {
        ReceivedTaskFragment fragment = new ReceivedTaskFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putSerializable(GCMIntentService.RECEIVE_TASK_INTENT, task);
        args.putSerializable(GCMIntentService.RECEIVE_PEOPLE_INTENT, peoples);
        Log.d("received peoples", Integer.toString(peoples.size()));
        fragment.setArguments(args);
        return fragment;
    }

    public ReceivedTaskFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        switch (getResources().getDisplayMetrics().densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			width = 21;
			height = 21;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			width = 28;
			height = 28;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			width = 42;
			height = 42;
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			width = 56;
			height = 56;
			break;
		}
        setRetainInstance(true);
    }

    @SuppressWarnings("unchecked")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	displayOptions = new DisplayImageOptions.Builder()
			.cacheOnDisc()
			.bitmapConfig(Bitmap.Config.RGB_565)
			.showStubImage(R.drawable.anon)
			.showImageOnFail(R.drawable.anon)
			.build();
    	imageLoader = ImageLoader.getInstance();
    	
    	mGeofenceList = new ArrayList<Geofence>();
		geofenceStore = new ReminderGeofenceStore(getSherlockActivity());
		scheduleClient = new ScheduleClient(getActivity());
		scheduleClient.doBindService();
		
    	//instantiate the tasks
    	this.currentTask = (Task) getArguments().getSerializable(GCMIntentService.RECEIVE_TASK_INTENT);
    	
    	ArrayList<People> peoples = (ArrayList<People>) getArguments().getSerializable(GCMIntentService.RECEIVE_PEOPLE_INTENT);
    	this.currentTaskPeoples = new ArrayList<People>();
    	for(People peop: peoples) {
    		Log.d("received people id", peop.getTaskId());
    		if(peop.getTaskId().equals(this.currentTask.getUniversalID()))
    			this.currentTaskPeoples.add(peop);
    	}
    	String picture = "http://graph.facebook.com/" + "fallingbridges"+ "/picture?type=small";

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.received_task, container, false);
		
		Typeface mFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Condensed.ttf");
		Typeface mFont1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
		Typeface mFont2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
		
		final TextView taskReceivedFrom = (TextView) rootView.findViewById(R.id.receivedTaskFrom);
		taskReceivedFrom.setText("Task from " + currentTask.getTaskFrom());
		final TextView taskReceivedTitle = (TextView) rootView.findViewById(R.id.receivedTaskTitle);
		taskReceivedTitle.setText(currentTask.getTitle());
		final TextView taskReceivedDetails = (TextView) rootView.findViewById(R.id.receivedTaskDetails);
		taskReceivedDetails.setText(getTaskDetails(currentTask));
		
		imageLoader.loadImage(picture, displayOptions, new SimpleImageLoadingListener(){
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				loadedBitmap = Bitmap.createScaledBitmap(loadedImage, width, height, true);
				taskReceivedFrom.setCompoundDrawablesWithIntrinsicBounds(null, null, new BitmapDrawable(getResources(), loadedBitmap), null);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				// TODO Auto-generated method stub
				taskReceivedFrom.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.anon), null);
				super.onLoadingFailed(imageUri, view, failReason);
			}
	    });

		taskReceivedFrom.setTypeface(mFont);
		taskReceivedTitle.setTypeface(mFont1);
		taskReceivedDetails.setTypeface(mFont2);
		
		final TextView taskAccept = (TextView) rootView.findViewById(R.id.taskAccept);
		final TextView taskDecline = (TextView) rootView.findViewById(R.id.taskDecline);
		
		dbHandler =new DatabaseHandler(getActivity());
		//add task to db. if declined delete it
		currentTask.setReminderID(Integer.toString(StaticFunctions.getLastReminder(getActivity())+1));
		
		taskAccept.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//update importance
				dbHandler.addReminders(currentTask);
				/*dbHandler.updateAColumn(currentTask.getReminderID(), "important", currentTask.getImportant());
				dbHandler.updateAColumn(currentTask.getReminderID(), "title", currentTask.getTitle());
				dbHandler.updateAColumn(currentTask.getReminderID(), "synced", 0);*/
				dbHandler.updateAColumn(currentTask.getReminderID(), "category", "From " + currentTask.getTaskFrom());
				//update date and time
				/*dbHandler.updateAColumn(currentTask.getReminderID(), "date", currentTask.getDate());
				dbHandler.updateAColumn(currentTask.getReminderID(), "time", currentTask.getTime());*/
				dbHandler.updateAColumn(currentTask.getReminderID(), "order_by", StaticFunctions.sortString(currentTask.getDate(), 
							currentTask.getTime(), currentTask.getImportant()));
				/*dbHandler.updateAColumn(currentTask.getReminderID(), "repeat", currentTask.getRepeat());
				
				dbHandler.updateAColumn(currentTask.getReminderID(), "universal_id", currentTask.getUniversalID());*/
					
				if(currentTaskPeoples.size()>1) { //means it has been shared
						dbHandler.updateAColumn(currentTask.getReminderID(), "assigned", "3");
				}
				for(People people: currentTaskPeoples) {
					people.setTaskId(currentTask.getUniversalID());
					if(people.getFbId().equals(StaticFunctions.getUserFbId(getActivity())))
						people.setAccepted(1);
					if(!dbHandler.checkIfPeopleExist(people.getTaskId(), people.getFbId())) {
						dbHandler.addPeopleToDb(people);
					}
				}

				//save the category if it doesn't already exists
				StaticFunctions.saveReceivedCategory("From " + currentTask.getTaskFrom(), getActivity());
				
				if(!currentTask.getSubtask().equals("[]")){
					currentTask.setSubtasks(Integer.parseInt(getSubtaskCount(currentTask.getSubtask())));
					dbHandler.updateSubtaskCount(currentTask.getReminderID(), currentTask.getSubtasks());
					try {
						JSONObject subtaskObject = new JSONObject(currentTask.getSubtask());
						Iterator<String> keys = subtaskObject.keys();
						while(keys.hasNext()) {
							String key = keys.next();
							if(!dbHandler.checkIfSubtaskExists(key))
								dbHandler.addSubtasks(key, currentTask.getReminderID());
							dbHandler.updateSubtaskComplete(key, currentTask.getReminderID(), subtaskObject.getInt(key));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if(!currentTask.getLocationJson().equals("[]")){
					if(dbHandler.checkIfLocationExists(getLocationLat(), getLocationLng())==0) {
						dbHandler.insertLocation(getLocationLat(), getLocationLng(), getLocationTitle(), getLocationAddress());
					}
					if(getTransitionType()!=0){ //add geofence if transition type is not zero
						ReminderGeofence geofence = new ReminderGeofence(currentTask.getReminderID(), locationLat,
								locationLng, 500, 28800000, getTransitionType());
						geofenceStore.setGeofence(currentTask.getReminderID(), dbHandler.getHighestLocationID(), geofence);
						addGeofences();
					}
										
				} else {
					dbHandler.updateAColumn(currentTask.getReminderID(), "location_json", "");
				}

				reminder = new Reminder(scheduleClient, currentTask);
				reminder.remind();
				acknowledgeDelivery(getActivity(), currentTask, Task.TASK_ACKNOWLEDGMENT_ACCEPT);
				dbHandler.deletePendingTask(currentTask.getUniversalID());
				getActivity().finish();
			}
		});
		taskDecline.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				acknowledgeDelivery(getActivity(), currentTask, Task.TASK_ACKNOWLEDGMENT_DECLINE);
				dbHandler.deletePendingTask(currentTask.getUniversalID());
				getActivity().finish();
			}
		});

        return rootView;
    }

    protected void addGeofences() {
		// TODO Auto-generated method stub
    	ReminderGeofence geofence = geofenceStore.getGeofence(currentTask.getReminderID());
		currentTask.setLocationID(Integer.toString(geofenceStore.getLocationId(currentTask.getReminderID())));
		if (geofence != null) {
			mGeofenceList.add(geofence.toGeofence());
			
			if (!StaticFunctions.servicesConnected(getActivity())) {
				return;
			}
			
			mLocationClient = new LocationClient(getSherlockActivity(), this,this);

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
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
    
    /**
     * Converts task details into Spannable String in desired format
     * @return taskdetails 
     */
    protected SpannableStringBuilder getTaskDetails(Task task) {
    	SpannableStringBuilder resultBuilder = new SpannableStringBuilder();
    	
    	if(!task.getDate().equals("N-N-N")) { // i.e. there is a date
    		SpannableString scheduledAt = new SpannableString("Scheduled At: ");
    		scheduledAt.setSpan(new AbsoluteSizeSpan(20, true), 0, scheduledAt.length(), 0);
    		scheduledAt.setSpan(new ForegroundColorSpan(Color.rgb(45, 188, 215)), 0, scheduledAt.length(), 0);
			resultBuilder.append(scheduledAt);
			if(!task.getTime().equals("N:N"))
				resultBuilder.append(task.getDate() + ", "+ task.getTime());
			else resultBuilder.append(task.getDate());
			resultBuilder.append("\n\n");
    	}
    	if(!task.getNote().equals("") ) {
    		SpannableString note = new SpannableString("Note: ");
    		note.setSpan(new AbsoluteSizeSpan(20, true), 0, note.length(), 0);
    		note.setSpan(new ForegroundColorSpan(Color.rgb(45, 188, 215)), 0, note.length(), 0);
			resultBuilder.append(note);
			resultBuilder.append(task.getNote());
			resultBuilder.append("\n\n");
    	}
    	if(!task.getLocationJson().equals("[]")) {
    		SpannableString location = new SpannableString("Location: ");
    		location.setSpan(new AbsoluteSizeSpan(20, true), 0, location.length(), 0);
    		location.setSpan(new ForegroundColorSpan(Color.rgb(45, 188, 215)), 0, location.length(), 0);
			resultBuilder.append(location);
			resultBuilder.append(getLocation(task.getLocationJson()));			
			resultBuilder.append("\n\n");
    	}
    	if(!task.getSubtask().equals("[]")) {
    		String count = getSubtaskCount(task.getSubtask());
    		SpannableString subtask = new SpannableString(count + " Subtasks ");
    		subtask.setSpan(new AbsoluteSizeSpan(20, true), 0, subtask.length(), 0);
    		subtask.setSpan(new ForegroundColorSpan(Color.rgb(45, 188, 215)), 0, subtask.length(), 0);
			resultBuilder.append(subtask);
			resultBuilder.append("\n\n");
    	}
    	if(this.currentTaskPeoples.size()!=0){
    		SpannableString people = new SpannableString(Integer.toString(this.currentTaskPeoples.size()) + " People Involved ");
    		people.setSpan(new AbsoluteSizeSpan(20, true), 0, people.length(), 0);
    		people.setSpan(new ForegroundColorSpan(Color.rgb(45, 188, 215)), 0, people.length(), 0);
			resultBuilder.append(people);
			resultBuilder.append("\n");
			Log.d("received details", "people");
    	}
		return resultBuilder;
	}
    
    private String getLocation(String loc) {
    	String location = null;
    	try {
			JSONObject locationObject = new JSONObject(loc);
			setLocationTitle(locationObject.getString(Task.LOCATION_TITLE));
			setLocationAddress(locationObject.getString(Task.LOCATION_ADDRESS));
			location = getLocationTitle() + ", " + getLocationAddress();
			setLocationLat(locationObject.getDouble(Task.LOCATION_LATITUDE));
			setLocationLng(locationObject.getDouble(Task.LOCATION_LONGITUDE));
			setTransitionType(locationObject.getInt(Task.LOCATION_TRANSITION_TYPE));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return location;
    }
    
    private String getSubtaskCount(String sub) {
    	int count = 0;
    	try {
			JSONObject subtaskObject = new JSONObject(sub);
			count = subtaskObject.length();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return Integer.toString(count);
    }
    /**
     * sends a post request to server acknowledging status of the received task
     * @param acknowledgementType -1 for decline, 1 for accept and 2 for pending
     */
    public static void acknowledgeDelivery(Context context, Task task, int acknowledgementType) {
    	
    	Intent acknowledgeIntent = new Intent(context, AcknowledgeDeliveryIntentService.class);
		acknowledgeIntent.putExtra(Task.UNIVERSAL_ID, task.getUniversalID());
		acknowledgeIntent.putExtra(People.PEOPLE_CONTACT_ID, StaticFunctions.getUserFbId(context));
		acknowledgeIntent.putExtra(People.PEOPLE_HAS_ACCEPTED, acknowledgementType);
    	context.startService(acknowledgeIntent);
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
	
	private PendingIntent getTransitionPendingIntent() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getSherlockActivity(),
				NotifyService.class);
		intent.putExtra(NotifyService.NOTIFICATION_ID, Integer.parseInt(currentTask.getReminderID()));
		intent.putExtra(NotifyService.TASK_TITLE, currentTask.getTitle());

		// Return the PendingIntent
		return PendingIntent.getService(getSherlockActivity(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		PendingIntent mTransitionPendingIntent = getTransitionPendingIntent();
		// Send a request to add the current geofences
		mLocationClient.addGeofences(mGeofenceList,
				mTransitionPendingIntent, this);
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
	public void onStop() {
		if(scheduleClient!=null)
			scheduleClient.doUnbindService();
		super.onStop();
	}

	public String getLocationTitle() {
		return locationTitle;
	}

	public void setLocationTitle(String locationTitle) {
		this.locationTitle = locationTitle;
	}

	public String getLocationAddress() {
		return locationAddress;
	}

	public void setLocationAddress(String locationAddress) {
		this.locationAddress = locationAddress;
	}

	public double getLocationLat() {
		return locationLat;
	}

	public void setLocationLat(double locationLat) {
		this.locationLat = locationLat;
	}

	public double getLocationLng() {
		return locationLng;
	}

	public void setLocationLng(double locationLng) {
		this.locationLng = locationLng;
	}
	
	public int getTransitionType() {
		return transitionType;
	}

	public void setTransitionType(int transitionType) {
		this.transitionType = transitionType;
	}

}
