package com.cw.msumit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.cw.msumit.adapters.LocationItemListAdapter.OnLocationRemovedOrDeleted;
import com.cw.msumit.fragments.LocationListsFragment;
import com.cw.msumit.fragments.LocationListsFragment.OnMapItemClicked;
import com.cw.msumit.fragments.LocationListsFragment.OnNoLocation;
import com.cw.msumit.fragments.LocationMapFragment;
import com.cw.msumit.fragments.LocationMapFragment.OnLocationSaved;
import com.cw.msumit.utils.ReminderGeofence;
import com.cw.msumit.utils.ReminderGeofenceStore;
import com.google.android.gms.maps.model.LatLng;

public class LocationActivityNew extends SherlockFragmentActivity implements OnMapItemClicked, OnLocationSaved, OnNoLocation,
															OnLocationRemovedOrDeleted{

	ActionBar actionBar;
	LocationListsFragment locationListsFragment;
	LocationMapFragment locationMapFragment;
	Intent receivedIntent;
	String originalLocationId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		actionBar = getSupportActionBar();
		actionBar.hide();
		receivedIntent = getIntent();
		
		Bundle listFragmentData = new Bundle();
		listFragmentData.putString(ReminderGeofence.LOCATION_ID, receivedIntent.getStringExtra(ReminderGeofence.LOCATION_ID));
		originalLocationId = receivedIntent.getStringExtra(ReminderGeofence.LOCATION_ID);
		listFragmentData.putString("taskId", receivedIntent.getStringExtra("taskId"));
		
		if(savedInstanceState == null) {
			locationListsFragment = new LocationListsFragment();
			locationListsFragment.setArguments(listFragmentData);
			getSupportFragmentManager().beginTransaction().replace(android.R.id.content, locationListsFragment)
														  .commit();
		} else {
			if(locationListsFragment == null) {
				locationMapFragment = (LocationMapFragment) getSupportFragmentManager().getFragment(savedInstanceState, "location map");
			} else {
				locationListsFragment = (LocationListsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "location list");
			}
		}
	}
	
	@Override
	public void onMapItemClick(Bundle locationData) {
		// TODO Auto-generated method stub

		locationMapFragment = new LocationMapFragment();
		locationMapFragment.setArguments(locationData);

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.addToBackStack("ToLocationLists");
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	    ft.replace(android.R.id.content, locationMapFragment).commit();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDoneClick(LatLng latLng, int route, int locationId) {
		// TODO Auto-generated method stub
		double latitude = latLng.latitude;
		double longitude = latLng.longitude;
		receivedIntent.putExtra("LatLng", new double[] {latitude, longitude});
		receivedIntent.putExtra(ReminderGeofenceStore.KEY_TRANSITION_TYPE, route);
		receivedIntent.putExtra(ReminderGeofence.LOCATION_ID, locationId);
		if(locationId!=Integer.parseInt(originalLocationId)) { // location has been changed
			receivedIntent.putExtra("ChangeInLocation", true);
		}
		receivedIntent.putExtra("NoLocation", false);
		receivedIntent.putExtra("Removing", false);
		setResult(RESULT_OK, receivedIntent);
	}

	@Override
	public void onNoLocation() {
		receivedIntent.putExtra("NoLocation", true);
		receivedIntent.putExtra("ChangeInLocation", true);
		setResult(RESULT_OK, receivedIntent);
	}

	@Override
	public void onLocationRemovedOrDeleted(boolean selected, boolean deleted, boolean removed, String locationId) {
		int dataToSend;
		if(selected){
			dataToSend = 0;
		}
		else if(deleted)
			dataToSend = 1;
		else dataToSend = 2;
		receivedIntent.putExtra("RemovedOrDeleted", dataToSend);
		receivedIntent.putExtra(ReminderGeofence.LOCATION_ID, locationId);
		if(!locationId.equals(originalLocationId)) { // location has been changed
			receivedIntent.putExtra("ChangeInLocation", true);
		}
		receivedIntent.putExtra("NoLocation", false);
		receivedIntent.putExtra("Removing", true);
		setResult(RESULT_OK, receivedIntent);
	}
}
