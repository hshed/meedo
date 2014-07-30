package com.cw.msumit.fragments;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.cw.msumit.R;
import com.cw.msumit.objects.Task;
import com.cw.msumit.utils.StaticFunctions;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

public class TaskReminderFragment extends SherlockFragment {
	/**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    private ArrayList<Task> tasks;
    Task currentTask;
    OnChangeTaskStatus changeTaskStatus;
    
    /**
     * Interface for changing status of the tasks
     */
    public interface OnChangeTaskStatus {
    	/**
    	 * task with completed status
    	 * @param taskToComplete
    	 */
    	void completeTask(Task taskToComplete);
    	/**
    	 * task with deleted status
    	 * @param taskToDelete
    	 */
    	void deleteTask(Task taskToDelete);
    	/**
    	 * task with later status
    	 * @param taskLater
    	 */
    	void remindLater(Task taskLater);
    	/**
    	 * status of task to change
    	 * @param undoTask
    	 */
    	void undoChange(Task undoTask);
    }

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    LinearLayout taskActions, taskActionsUndo ;
    //ViewStub laterStub;
    int remindLater = 0; //0 means remind later today and 1 means tmrw.
    public static TaskReminderFragment create(int pageNumber) {
        TaskReminderFragment fragment = new TaskReminderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public TaskReminderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
    	
    	//instantiate the tasks
    	tasks=StaticFunctions.getTasks("Active tasks", getActivity());
    	this.currentTask= tasks.get(getPageNumber());
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.task_reminder, container, false);
		
		Typeface mFont = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Roboto-Regular.ttf");
		Typeface mFont1 = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Roboto-Light.ttf");
		taskActions = (LinearLayout)rootView.findViewById(R.id.taskActionsLayout2);
		taskActionsUndo = (LinearLayout)rootView.findViewById(R.id.taskActionsLayout1);
		//laterStub = (ViewStub) rootView.findViewById(R.id.laterStub);
		final TextView taskTitle = (TextView) rootView.findViewById(R.id.taskTitle);
		//here task title is set as per the page number -- decode the page number to reminder ID
		taskTitle.setText(currentTask.getTitle());
		final TextView taskLocation = (TextView) rootView.findViewById(R.id.taskLocation);
		taskLocation.setText(getDetailsText());
		final TextView taskDone = (TextView) rootView.findViewById(R.id.taskDone);
		//final TextView taskLater = (TextView) rootView.findViewById(R.id.taskLater);
		final TextView taskDelete = (TextView) rootView.findViewById(R.id.taskDelete);
		final TextView taskUndo = (TextView) rootView.findViewById(R.id.taskStatusUndo);
		final TextView taskStatus = (TextView) rootView.findViewById(R.id.taskStatus);
		
		taskUndo.setTypeface(mFont);
		taskTitle.setTypeface(mFont1);
		taskLocation.setTypeface(mFont1);
		taskDone.setTypeface(mFont);
		//taskLater.setTypeface(mFont);
		taskDelete.setTypeface(mFont);
		
		changeTaskStatus = (OnChangeTaskStatus) getSherlockActivity();
		
		taskDone.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeTaskStatus.completeTask(currentTask);
				
				taskStatus.setText("Task Completed");
				crossFade(0);
			}
		});
		
		taskUndo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				changeTaskStatus.undoChange(currentTask);
				
				crossFade(1);
			}
		});
		
		taskDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeTaskStatus.deleteTask(currentTask);
				
				taskStatus.setText("Task Deleted.");
				crossFade(0);
			}
		});
		
		/*taskLater.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				currentTask.setTag(remindLater);
				changeTaskStatus.remindLater(currentTask);
				
				taskStatus.setText("When?");
				crossFade(2);
				
			}
		});*/
		

        return rootView;
    }

	private String getDetailsText() {
		String detailsText;
		String time = Calendar.getInstance().getTime().toString();
		detailsText = time;
		if(!this.currentTask.getLocationJson().equals("")){
			try {
				detailsText = detailsText + " at " + new JSONObject(this.currentTask.getLocationJson()).getString(Task.LOCATION_TITLE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return detailsText;
	}

	private void crossFade(int which) {
	   int mAnimationTime = getResources().getInteger(android.R.integer.config_longAnimTime);
	   if (which ==0){
		   com.nineoldandroids.view.ViewPropertyAnimator.animate(taskActions).alpha(0f).setDuration(mAnimationTime).setListener(new AnimatorListenerAdapter() {
				 @Override
				public void onAnimationEnd(Animator animation) {
	                  com.nineoldandroids.view.ViewPropertyAnimator.animate(taskActions).setListener(null);
	                  taskActions.setVisibility(View.INVISIBLE);
	              }
			});
		   com.nineoldandroids.view.ViewPropertyAnimator.animate(taskActionsUndo).alpha(1f).setDuration(mAnimationTime).setListener(new AnimatorListenerAdapter() {
				 @Override
				public void onAnimationEnd(Animator animation) {
		                com.nineoldandroids.view.ViewPropertyAnimator.animate(taskActionsUndo).setListener(null);
		                taskActionsUndo.setVisibility(View.VISIBLE);
		            }
				});
	   } else {
		   com.nineoldandroids.view.ViewPropertyAnimator.animate(taskActions).alpha(1f).setDuration(mAnimationTime).setListener(new AnimatorListenerAdapter() {
				 @Override
				public void onAnimationEnd(Animator animation) {
	                  com.nineoldandroids.view.ViewPropertyAnimator.animate(taskActions).setListener(null);
	                  taskActions.setVisibility(View.VISIBLE);
	              }
			});
		   com.nineoldandroids.view.ViewPropertyAnimator.animate(taskActionsUndo).alpha(0f).setDuration(mAnimationTime).setListener(new AnimatorListenerAdapter() {
				 @Override
				public void onAnimationEnd(Animator animation) {
		                com.nineoldandroids.view.ViewPropertyAnimator.animate(taskActionsUndo).setListener(null);
		                taskActionsUndo.setVisibility(View.INVISIBLE);
		            }
				});
	   } /*else {
		   com.nineoldandroids.view.ViewPropertyAnimator.animate(taskActions).alpha(0f).setDuration(mAnimationTime).setListener(new AnimatorListenerAdapter() {
				 @Override
				public void onAnimationEnd(Animator animation) {
	                  com.nineoldandroids.view.ViewPropertyAnimator.animate(taskActions).setListener(null);
	                  taskActions.setVisibility(View.INVISIBLE);
	              }
			});
		   com.nineoldandroids.view.ViewPropertyAnimator.animate(taskActionsUndo).alpha(1f).setDuration(mAnimationTime).setListener(new AnimatorListenerAdapter() {
				 @Override
				public void onAnimationEnd(Animator animation) {
		                com.nineoldandroids.view.ViewPropertyAnimator.animate(taskActionsUndo).setListener(null);
		                View inflated = laterStub.inflate();
		                TextView laterToday = (TextView) inflated.findViewById(R.id.laterToday);
		                TextView laterTmrw = (TextView) inflated.findViewById(R.id.laterTomorrow);
		                laterTmrw.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// remind tomorrow at the same time
								remindLater = 1;
							}
						});
		                laterToday.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// remind today after 3 hours
								remindLater = 0;
							}
						});
		                taskActionsUndo.setVisibility(View.VISIBLE);
		            }
				});
	   }*/
	   
	}

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
