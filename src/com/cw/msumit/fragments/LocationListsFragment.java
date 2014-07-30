package com.cw.msumit.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.cw.msumit.R;
import com.cw.msumit.adapters.LocationItemListAdapter;
import com.cw.msumit.adapters.LocationSuggestionAdapter;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.databases.DatabaseMethods;
import com.cw.msumit.objects.LocationProps;
import com.cw.msumit.utils.ReminderGeofence;
import com.cw.msumit.utils.StaticFunctions;
import com.cw.msumit.views.LocationAutoCompleteTextView;

public class LocationListsFragment extends SherlockFragment{

	ListView locationListView;
	LocationAutoCompleteTextView locationAutoComplete;
	ArrayList<HashMap<String, String>> locationListItem;
	public static String LOCATION_TITLE = "locationTitle";
	public static String LOCATION_ADDRESS = "locationAddress";
	public static String LOCATION_LATITUDE = "locationLat";
	public static String LOCATION_LONGITUDE = "locationLong";
	private DatabaseHandler dbHandler;
	private LocationSuggestionAdapter locationSuggestionAdapter;
	LocationItemListAdapter locationItemListAdapter;
	Cursor cursor;
	public interface OnMapItemClicked{
		void onMapItemClick(Bundle locationData);
	}
	public interface OnTransitionTypeSelected{
		void setTransitionType(int type);
	}
	public interface OnNoLocation{
		/**
		 * No Location has been clicked, hence set location icon to black
		 */
		void onNoLocation();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setRetainInstance(true);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.location_new, null);
		locationListView = (ListView) view.findViewById(R.id.locationList);
		
		ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.locationSearchProgress);
		ImageView search = (ImageView) view.findViewById(R.id.locationSearch);
		
		dbHandler = new DatabaseHandler(getActivity());
		cursor = dbHandler.getLocationCursor();
		String locationId = getArguments().getString(ReminderGeofence.LOCATION_ID);
		locationItemListAdapter = new LocationItemListAdapter(getSherlockActivity(), cursor, 0, locationId, getActivity());
		locationListView.setAdapter(locationItemListAdapter);
		locationListView.setEmptyView(view.findViewById(R.id.location_emptyview));
		locationListView.setItemsCanFocus(true);
		locationListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				LocationProps lProps = locationItemListAdapter.getLocationPropItem(position);
				String selectedLocationName = lProps.getFeaturename();
				String selectedLocationAddress = lProps.getAddress();
				
				double selectedLocationLatitude = lProps.getLatitude();
				double selectedLocationLongitude = lProps.getLongitude();
				
				Bundle locationData = new Bundle();
				locationData.putString(LOCATION_TITLE, selectedLocationName);
				locationData.putString(LOCATION_ADDRESS, selectedLocationAddress);
				locationData.putDouble(LOCATION_LATITUDE, selectedLocationLatitude);
				locationData.putDouble(LOCATION_LONGITUDE, selectedLocationLongitude);
				
				OnMapItemClicked onMapItemClicked = (OnMapItemClicked) getSherlockActivity(); 
				onMapItemClicked.onMapItemClick(locationData);
			}
		});
		
		locationAutoComplete = (LocationAutoCompleteTextView) view.findViewById(R.id.searchLocationName);
		locationAutoComplete.setThreshold(1);
		locationAutoComplete.setLoadingIndicator(progressBar, search);
		
		locationSuggestionAdapter = new LocationSuggestionAdapter(getSherlockActivity());
		if(StaticFunctions.servicesConnected(getActivity())) { //only show suggestions if play services available
			locationAutoComplete.setAdapter(locationSuggestionAdapter);
		}
		locationAutoComplete.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				locationAutoComplete.setText("");
				String selectedLocationName = locationSuggestionAdapter.getItem(position).getFeatureName().toString();
				String selectedLocationAddress = DatabaseMethods.getAddress(locationSuggestionAdapter.getItem(position));
				
				double selectedLocationLatitude = locationSuggestionAdapter.getItem(position).getLatitude();
				double selectedLocationLongitude = locationSuggestionAdapter.getItem(position).getLongitude();
				
				Bundle locationData = new Bundle();
				locationData.putString(LOCATION_TITLE, selectedLocationName);
				locationData.putString(LOCATION_ADDRESS, selectedLocationAddress);
				locationData.putDouble(LOCATION_LATITUDE, selectedLocationLatitude);
				locationData.putDouble(LOCATION_LONGITUDE, selectedLocationLongitude);
				
				OnMapItemClicked onMapItemClicked = (OnMapItemClicked) getSherlockActivity(); 
				onMapItemClicked.onMapItemClick(locationData);
			}
		});
		
		//TextView doneLocation = (TextView) view.findViewById(R.id.locationDoneText);
		TextView noLocation = (TextView) view.findViewById(R.id.noLocationText);
		/*doneLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Return to Task Details 
				// with values of lat and long in a bundle
				
				//getSherlockActivity().getSupportFragmentManager();
				//SherlockFragment detailsFragment = TaskDetailsFragment.newInstance(0);
				getSherlockActivity().finish();			
			}
		});*/
		
		noLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				OnNoLocation onNoLocation = (OnNoLocation) getSherlockActivity();
				onNoLocation.onNoLocation();
				getSherlockActivity().finish();
			}
		});
		
		return view;
	}

	@Override
	public void onDestroy() {
		if(cursor!=null)
			cursor.close();
		super.onDestroy();
	}
}
