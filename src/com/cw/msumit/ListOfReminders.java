package com.cw.msumit;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.cw.msumit.fragments.MyRemindersFragment;
import com.cw.msumit.fragments.MyRemindersFragment.OnTaskStatusChanged;
import com.cw.msumit.fragments.SideMenuFragment;
import com.cw.msumit.services.Utils;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class ListOfReminders extends SlidingFragmentActivity implements OnTaskStatusChanged{

	ActionBar actionBar;
	String fontPath = "fonts/Roboto-Medium.ttf";
	AssetManager assetManager;
	Typeface plain;
	SherlockFragment mContent;
	SlidingMenu sm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_reminders_frag_layout);

		actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(45, 188,
				215)));
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);

		actionBar.setTitle("  Active Tasks");
		actionBar.setIcon(getResources().getDrawable(R.drawable.menu));

		assetManager = getAssets();
		plain = Typeface.createFromAsset(assetManager, fontPath);
		
		
		
		if (findViewById(R.id.menu_frame) == null) {
			setBehindContentView(R.layout.menu_frame);
			getSlidingMenu().setSlidingEnabled(true);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			// show home as up so we can toggle
		} else {
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}

		// set the Above View Fragment
		if (savedInstanceState != null)
			mContent = (SherlockFragment) getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null) {
			
			mContent = MyRemindersFragment.newInstance("Active tasks");
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.replace(R.id.simple_fragment, mContent);
			// ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		}
		
		SideMenuFragment sideMenuFragment = new SideMenuFragment();
		// set the Behind View Fragment
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, sideMenuFragment).commit();
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();

		sm = getSlidingMenu();
		sm.setMode(SlidingMenu.LEFT);
		sm.setBehindOffset((int) (0.13*metrics.widthPixels));
		sm.setShadowWidth(50);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);

		//GCMRegistrar.setRegisteredOnServer(ListOfReminders.this, false);
		SignupActivity.registerDevice(this);
		Utils.notificationReceived = false;
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return (true);

			// more code here for other cases

		}

		return super.onOptionsItemSelected(item);
	}


	public void switchContent(SherlockFragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.simple_fragment, fragment).commit();
		getSlidingMenu().showContent();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onStatusChanged() {
		// TODO Auto-generated method stub
		SideMenuFragment menuFragment = (SideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.menu_frame);
		if(menuFragment!=null)
			menuFragment.invalidate();
	}

}
