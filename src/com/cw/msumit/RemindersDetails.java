package com.cw.msumit;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.cw.msumit.fragments.TaskDetailsFragment;

public class RemindersDetails extends SherlockFragmentActivity {
	ActionBar actionBar;
	SherlockFragment details;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.setreminder);
		actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(45, 188,
				215)));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Task Details");


		/**if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			/*
			 * You can call finish() here if you don't want to support landscape
			 * orientation in mobile devices
			 */
			 //finish();
			/***TaskDetailsFragment details = new TaskDetailsFragment();
			details.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.replace(android.R.id.content, details).commit();
			return;
		}**/
		
		if(savedInstanceState!=null) {
			details = (SherlockFragment) getSupportFragmentManager().getFragment(savedInstanceState, "details");
		}

		else {
			// During initial setup, plug in the details fragment.
		
			android.support.v4.app.FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
			//ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
			ft.setCustomAnimations(android.R.anim.slide_in_left,
	                android.R.anim.slide_out_right);
			details = new TaskDetailsFragment();
			details.setArguments(getIntent().getExtras());
			ft.replace(android.R.id.content, details).commit();
		} 

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
		super.onSaveInstanceState(outState);
	}

}
