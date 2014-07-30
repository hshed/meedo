package com.cw.msumit;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.cw.msumit.fragments.ReceivedTaskFragment;
import com.cw.msumit.fragments.TaskReminderFragment;
import com.cw.msumit.objects.People;
import com.cw.msumit.objects.Task;
import com.cw.msumit.utils.HeightWrappingViewPager;
import com.viewpagerindicator.CirclePageIndicator;

public class ReceivedTaskActivity extends SherlockFragmentActivity {
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
    Intent receivedIntent;
    ArrayList<Task> tasks = new ArrayList<Task>();
    ArrayList<People> peoples = new ArrayList<People>();

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutParams lp = this.getWindow().getAttributes();
	    lp.gravity = Gravity.CENTER;
        lp.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT; 
        lp.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
	    lp.dimAmount = 0;
		//getSupportActionBar().hide();
	    getWindow().setAttributes(lp);
		setContentView(R.layout.task_reminder_viewpager);

		receivedIntent= getIntent();
		tasks = (ArrayList<Task>) receivedIntent.getSerializableExtra(GCMIntentService.RECEIVE_TASK_INTENT);
		peoples = (ArrayList<People>) receivedIntent.getSerializableExtra(GCMIntentService.RECEIVE_PEOPLE_INTENT);

		NUM_PAGES = tasks.size();
		
		// Instantiate a ViewPager and a PagerAdapter.
        mPager = (HeightWrappingViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ReceivedTaskReminderAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(NUM_PAGES-1);
        
        CirclePageIndicator titleIndicator = (CirclePageIndicator)findViewById(R.id.circles);
        titleIndicator.setViewPager(mPager);
        
        final float density = getResources().getDisplayMetrics().density;
        titleIndicator.setRadius(4.5F);
        titleIndicator.setFillColor(Color.rgb(45 , 188,215));
        titleIndicator.setStrokeWidth(1 * density);
        titleIndicator.setBackgroundColor(Color.TRANSPARENT);
	}
	
	/**
     * A simple pager adapter that represents 5 {@link TaskReminderFragment} objects, in
     * sequence.
     */
    private class ReceivedTaskReminderAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public ReceivedTaskReminderAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public SherlockFragment getItem(int position) {
            return ReceivedTaskFragment.create(position, tasks.get(position), peoples);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }


	@Override
	protected void onStop() {
		// code to remove tasks and complete tasks
		super.onStop();
	}
	
}
