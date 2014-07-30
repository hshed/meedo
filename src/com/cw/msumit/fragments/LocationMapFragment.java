package com.cw.msumit.fragments;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.cw.msumit.R;
import com.cw.msumit.adapters.LocationSuggestionAdapter;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.databases.DatabaseMethods;
import com.cw.msumit.utils.PopupMenu;
import com.cw.msumit.utils.StaticFunctions;
import com.cw.msumit.views.LocationAutoCompleteTextView;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationMapFragment extends SherlockFragment{

	LocationAutoCompleteTextView locationAutoComplete;
	MapView mapView;
	GoogleMap googleMap;
	SupportMapFragment googleMapFragment;
	LatLng locationSource;
	private String receivedLocationTitle, receivedLocationAdd;
	private TextView locationName, locationAddressText;
	private ImageView popView;
	private PopupMenu popupMenu;
	AssetManager assetManager;
	Typeface typeface;
	String fontPath = "fonts/Roboto-Regular.ttf";
	double locationLatitude, locationLongitude;
	
	public interface OnLocationSaved {
		void onDoneClick(LatLng latLng, int route, int locationId);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//setRetainInstance(true);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.location_map, null);

		ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.locationSearchProgress);
		ImageView search = (ImageView) view.findViewById(R.id.locationSearch);
		assetManager = getSherlockActivity().getAssets();
		typeface = Typeface.createFromAsset(assetManager, fontPath);
		
		locationName = (TextView) view.findViewById(R.id.locationName);
		locationAddressText = (TextView) view.findViewById(R.id.locationDetails);
		popView = (ImageView) view.findViewById(R.id.popView);
		popView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupMenu = new PopupMenu(getSherlockActivity(), v);
				popupMenu.getMenu().add(Menu.NONE, 0, 0, "Remind on arrival");
				popupMenu.getMenu().add(Menu.NONE, 1, 1, "Remind on departure");
				popupMenu.getMenu().add(Menu.NONE, 2, 2, "No Reminder");
				popupMenu.show();
				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						switch (item.getItemId()) {
						case 0:
							setLocationAndGeofenceIfNeeded(Geofence.GEOFENCE_TRANSITION_ENTER, locationName.getText().toString());
							break;
						case 1:
							setLocationAndGeofenceIfNeeded(Geofence.GEOFENCE_TRANSITION_EXIT,  locationName.getText().toString());
							break;
						case 2:
							setLocationAndGeofenceIfNeeded(0,  locationName.getText().toString());
						default:
							break;
						}
						return false;
					}
				});
			}
		});
		
		if(savedInstanceState == null) {
			Log.d("getting", "retrieved");
			receivedLocationTitle = getArguments().getString(LocationListsFragment.LOCATION_TITLE);
			receivedLocationAdd = getArguments().getString(LocationListsFragment.LOCATION_ADDRESS);
			locationLatitude = getArguments().getDouble(LocationListsFragment.LOCATION_LATITUDE);
			locationLongitude = getArguments().getDouble(LocationListsFragment.LOCATION_LONGITUDE);
			locationSource = new LatLng(locationLatitude, locationLongitude);
		} else {
			Log.d("saved", "retrieved");
			receivedLocationTitle = savedInstanceState.getString("receivedLocationTitle");
			Log.d("saved", receivedLocationTitle);
			receivedLocationAdd = savedInstanceState.getString("receivedLocationAdd");
			Log.d("saved", receivedLocationAdd);
			locationLatitude = savedInstanceState.getDouble("locationLatitude");
			Log.d("saved", Double.toString(locationLatitude));
			locationLongitude = savedInstanceState.getDouble("locationLongitude");
			Log.d("saved", Double.toString(locationLongitude));
			locationSource = new LatLng(locationLatitude, locationLongitude);
		}
		
		CameraPosition locationPosition = new CameraPosition.Builder().target(locationSource)
				.zoom(15.5f).build();
		
		setUpMapIfNeeded(locationPosition);
		
		//auto complete codes
		locationAutoComplete = (LocationAutoCompleteTextView) view.findViewById(R.id.searchLocationName);
		locationAutoComplete.setThreshold(1);
		locationAutoComplete.setLoadingIndicator(progressBar, search);
		final LocationSuggestionAdapter locationSuggestionAdapter = new LocationSuggestionAdapter(getSherlockActivity());
		if(StaticFunctions.servicesConnected(getActivity())) { //only show suggestions if play services available
			locationAutoComplete.setAdapter(locationSuggestionAdapter);
		}
		locationAutoComplete.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				locationAutoComplete.setText("");
				receivedLocationTitle = locationSuggestionAdapter.getItem(position).getFeatureName().toString();
				receivedLocationAdd = DatabaseMethods.getAddress(locationSuggestionAdapter.getItem(position));
				
				double selectedLocationLatitude = locationSuggestionAdapter.getItem(position).getLatitude();
				double selectedLocationLongitude = locationSuggestionAdapter.getItem(position).getLongitude();
				locationLatitude = selectedLocationLatitude;
				locationLongitude = selectedLocationLongitude;
				locationSource = new LatLng(selectedLocationLatitude,selectedLocationLongitude);
				
				updateMap(locationSource, receivedLocationTitle, receivedLocationAdd);
				setLocationValues(locationSource, receivedLocationTitle, receivedLocationAdd);
				//setUpMapIfNeeded(locationPosition);
				
			}
		});
		
		TextView getDirection = (TextView) view.findViewById(R.id.getDirection);
		getDirection.setTypeface(typeface);
		TextView doneLocation = (TextView) view.findViewById(R.id.locationDoneText);
		doneLocation.setTypeface(typeface);
		TextView noLocation = (TextView) view.findViewById(R.id.noLocationText);
		noLocation.setTypeface(typeface);
		doneLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Return to Task Details 
				// with values of lat and long in a bundle
				setLocationAndGeofenceIfNeeded(Geofence.GEOFENCE_TRANSITION_ENTER, locationName.getText().toString());
			}
		});
		
		noLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getSherlockActivity().getSupportFragmentManager().popBackStack();
			}
		});
		
		getDirection.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String lat = Double.toString(getLocationSource().latitude);
				String lon = Double.toString(getLocationSource().longitude);
				// TODO Auto-generated method stub
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
					    Uri.parse("http://maps.google.com/maps?daddr="+lat +","+ lon));
				//intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
					startActivity(intent);
			}
		});
		
		return view;
	}
	
	public void setLocationAndGeofenceIfNeeded(int route, String locationName) {
		LatLng latLng = this.getLocationSource();
		//query if the location already exists in the database.
		//if exists don't do anything, else save into database.
		int added = DatabaseMethods.InsertLocation(getSherlockActivity(), latLng, locationName);
		//get the id of the last added location i.e. LOCATION ID
		int locationID = 0;
		if(added == 0){
			DatabaseHandler dbHandler = new DatabaseHandler(getSherlockActivity());
			locationID = dbHandler.getHighestLocationID();
		} else {
			locationID = added;
		}
		Log.d("loc2", Integer.toString(locationID));
		OnLocationSaved onLocationSaved = (OnLocationSaved) getSherlockActivity();
		onLocationSaved.onDoneClick(latLng, route, locationID);
		getSherlockActivity().finish();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Log.d("saving", "retrieved");
		Log.d("saving", receivedLocationTitle);
		Log.d("saving", receivedLocationAdd);
		Log.d("saving", Double.toString(locationLatitude));
		Log.d("saving", Double.toString(locationLongitude));
		outState.putDouble("locationLatitude", locationLatitude);
		outState.putDouble("locationLongitude", locationLongitude);
		outState.putString("receivedLocationTitle", receivedLocationTitle);
		outState.putString("receivedLocationAdd", receivedLocationAdd);
	}
	
	protected void updateMap(LatLng locationSource, String title, String address) {
		// TODO Auto-generated method stub
		googleMap.addMarker(new MarkerOptions().position(locationSource)
				.title(receivedLocationTitle).snippet("Click"));
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationSource,
				15));
		locationName.setText(title);
		locationAddressText.setText(address);
	}

	

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		SupportMapFragment f = (SupportMapFragment) getSherlockActivity().getSupportFragmentManager().findFragmentById(R.id.mapNew);
        if (f != null) 
            getSherlockActivity().getSupportFragmentManager().beginTransaction().remove(f).commit();
		super.onPause();
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}


	private void setUpMapIfNeeded(CameraPosition locationPosition) {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (StaticFunctions.servicesConnected(getActivity())) {
			if (googleMap == null) {
				// Try to obtain the map from the SupportMapFragment.
				googleMap = ((SupportMapFragment) getSherlockActivity().getSupportFragmentManager()
						.findFragmentById(R.id.mapNew)).getMap();
				// Check if we were successful in obtaining the map.
				if (googleMap != null) {
					setUpMap(locationPosition);
				}
			}
		}
		
	}

	private void setUpMap(CameraPosition locationPosition) {

		locationName.setText(receivedLocationTitle);
		locationAddressText.setText(receivedLocationAdd);
		googleMap.addMarker(new MarkerOptions().position(locationSource)
				.title(receivedLocationTitle).snippet("Click"));
		// googleMap.setLocationSource(locationSource);
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationSource,
				15));
		// Set listeners for marker events. See the bottom of this class for
		// their behavior.
		//googleMap.setOnMarkerClickListener(LocationMapFragment.this);
		googleMap.setMyLocationEnabled(true);
		

	}

	protected void setLocationValues(LatLng locationSource, String title, String address) {
		// TODO Auto-generated method stub
		receivedLocationTitle = title;
		receivedLocationAdd = address;
		this.locationSource = locationSource;
	}
	
	protected LatLng getLocationSource() {
		return locationSource;
	}
	
	protected String[] getLocationLatLong() {
		return new String[]{receivedLocationTitle, receivedLocationAdd};
	}


}
