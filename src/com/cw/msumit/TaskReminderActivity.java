package com.cw.msumit;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.fragments.TaskReminderFragment;
import com.cw.msumit.fragments.TaskReminderFragment.OnChangeTaskStatus;
import com.cw.msumit.objects.Reminder;
import com.cw.msumit.objects.Task;
import com.cw.msumit.services.NotifyService;
import com.cw.msumit.services.ScheduleClient;
import com.cw.msumit.utils.HeightWrappingViewPager;
import com.cw.msumit.utils.StaticFunctions;
import com.viewpagerindicator.CirclePageIndicator;

public class TaskReminderActivity extends SherlockFragmentActivity implements OnChangeTaskStatus{
	/**
     * The number of pages i.e. number of reminders. retrieved from db.
     */
    private int NUM_PAGES;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private ArrayList<Task> tasksToComplete, tasksToDelete, tasksToRemindLater;
    private DatabaseHandler dbHandler;
    private ScheduleClient scheduleClient;
    Intent receivedIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState); 
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		LayoutParams params = getWindow().getAttributes();  
        params.x = Gravity.LEFT;  
        params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT; 
        params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        params.y = displayMetrics.heightPixels - params.height;  
  
        getWindow().setAttributes(params);
        setContentView(R.layout.task_reminder_viewpager);
		ArrayList<Task> activeTasks = StaticFunctions.getTasks("Active tasks", TaskReminderActivity.this);
		NUM_PAGES = activeTasks.size();
		receivedIntent = getIntent();
		
		// Instantiate a ViewPager and a PagerAdapter.
        mPager = (HeightWrappingViewPager) findViewById(R.id.pager);
        mPagerAdapter = new TaskReminderAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(NUM_PAGES-1);
        
        CirclePageIndicator titleIndicator = (CirclePageIndicator)findViewById(R.id.circles);
        titleIndicator.setViewPager(mPager);
        for(int i=0; i<NUM_PAGES; i++) {
			if(activeTasks.get(i).getTitle().equals(receivedIntent.getStringExtra(NotifyService.TASK_TITLE)))
				titleIndicator.setCurrentItem(i);
		}
        
        final float density = getResources().getDisplayMetrics().density;
        titleIndicator.setRadius(4.5F);
        titleIndicator.setFillColor(Color.rgb(45 , 188,215));
        titleIndicator.setStrokeWidth(1 * density);
        titleIndicator.setBackgroundColor(Color.TRANSPARENT);
        
        
        dbHandler = new DatabaseHandler(TaskReminderActivity.this);
        scheduleClient = new ScheduleClient(TaskReminderActivity.this);
        scheduleClient.doBindService();
        tasksToComplete = new ArrayList<Task>();
        tasksToDelete = new ArrayList<Task>();
        tasksToRemindLater = new ArrayList<Task>();

	}
	
	/**
     * A simple pager adapter that represents 5 {@link TaskReminderFragment} objects, in
     * sequence.
     */
    private class TaskReminderAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public TaskReminderAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public SherlockFragment getItem(int position) {
            return TaskReminderFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

	@Override
	public void completeTask(Task taskToComplete) {
		tasksToComplete.add(taskToComplete);
	}

	@Override
	public void deleteTask(Task taskToDelete) {
		tasksToDelete.add(taskToDelete);
	}
	
	@Override
	public void remindLater(Task taskLater) {
		tasksToRemindLater.add(taskLater);
	}

	@Override
	public void undoChange(Task undoTask) {
		tasksToComplete.remove(undoTask);
		tasksToDelete.remove(undoTask);
		tasksToRemindLater.remove(undoTask);
	}
	

	@Override
	protected void onStop() {
		// code to remove tasks and complete tasks
		Reminder reminder;
		for(Task task: tasksToComplete) {
			reminder = new Reminder(scheduleClient, task);
			reminder.remove();
			dbHandler.updateAColumn(task.getReminderID(), "complete", 1);
			dbHandler.updateAColumn(task.getReminderID(), "completed_on",
					StaticFunctions.calendarIntoDateString(Calendar.getInstance()));
		}
		for(Task task: tasksToDelete) {
			reminder = new Reminder(scheduleClient, task);
			reminder.remove();
			//delete the task
			dbHandler.deleteTask(task);
		}
		for(Task task: tasksToRemindLater) {
			reminder = new Reminder(scheduleClient, task);
			reminder.remind(task.getTag());
		}
		scheduleClient.doUnbindService();
		super.onStop();
	}

	
}
