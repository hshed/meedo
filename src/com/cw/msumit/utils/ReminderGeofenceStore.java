package com.cw.msumit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.cw.msumit.utils.ReminderGeofence;

public class ReminderGeofenceStore {

	// Keys for flattened geofences stored in SharedPreferences
	public static final String KEY_LATITUDE = "com.cw.msumit.geofence.KEY_LATITUDE";
	public static final String KEY_LONGITUDE = "com.cw.msumit.geofence.KEY_LONGITUDE";
	public static final String KEY_RADIUS = "com.cw.msumit.geofence.KEY_RADIUS";
	public static final String KEY_EXPIRATION_DURATION = "com.cw.msumit.geofence.KEY_EXPIRATION_DURATION";
	public static final String KEY_TRANSITION_TYPE = "com.cw.msumit.geofence.KEY_TRANSITION_TYPE";
	public static final String KEY_LOCATION_ID = "com.cw.msumit.geofence.KEY_LOCATION_ID";
	// The prefix for flattened geofence keys
	public static final String KEY_PREFIX = "com.cw.msumit.geofence.KEY";
	/*
	 * Invalid values, used to test geofence storage when retrieving geofences
	 */
	public static final long INVALID_LONG_VALUE = -999l;
	public static final float INVALID_FLOAT_VALUE = -999.0f;
	public static final int INVALID_INT_VALUE = -999;
	// The SharedPreferences object in which geofences are stored
	private final SharedPreferences mPrefs;
	// The name of the SharedPreferences
	private static final String SHARED_PREFERENCES = "Geofences";

	public ReminderGeofenceStore(Context context) {
		// initialize your database handler
		mPrefs = context.getSharedPreferences(SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
	}

	/**
	 * @param id used in saving the geofence
	 * @return returns the geofence corresponding to the id
	 */

	public ReminderGeofence getGeofence(String id) {

		/*
		 * Get the latitude for the geofence identified by id, or
		 * INVALID_FLOAT_VALUE if it doesn't exist
		 */
		double lat = mPrefs.getFloat(getGeofenceFieldKey(id, KEY_LATITUDE),
				INVALID_FLOAT_VALUE);
		/*
		 * Get the longitude for the geofence identified by id, or
		 * INVALID_FLOAT_VALUE if it doesn't exist
		 */
		double lng = mPrefs.getFloat(getGeofenceFieldKey(id, KEY_LONGITUDE),
				INVALID_FLOAT_VALUE);
		/*
		 * Get the radius for the geofence identified by id, or
		 * INVALID_FLOAT_VALUE if it doesn't exist
		 */
		float radius = mPrefs.getFloat(getGeofenceFieldKey(id, KEY_RADIUS),
				INVALID_FLOAT_VALUE);
		/*
		 * Get the expiration duration for the geofence identified by id, or
		 * INVALID_LONG_VALUE if it doesn't exist
		 */
		long expirationDuration = mPrefs.getLong(
				getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION),
				INVALID_LONG_VALUE);
		/*
		 * Get the transition type for the geofence identified by id, or
		 * INVALID_INT_VALUE if it doesn't exist
		 */
		int transitionType = mPrefs
				.getInt(getGeofenceFieldKey(id, KEY_TRANSITION_TYPE),
						INVALID_INT_VALUE);
		// If none of the values is incorrect, return the object
		if (lat != INVALID_FLOAT_VALUE && lng != INVALID_FLOAT_VALUE
				&& radius != INVALID_FLOAT_VALUE
				&& expirationDuration != INVALID_LONG_VALUE
				&& transitionType != INVALID_INT_VALUE) {

			// Return a true Geofence object
			return new ReminderGeofence(id, lat, lng, radius,
					expirationDuration, transitionType);
			// Otherwise, return null.
		} else {
			return null;
		}
	}

	/**
	 * set the values into shared preferences
	 * 
	 * @param id
	 * @param geofence
	 */
	public void setGeofence(String id, int locationId, ReminderGeofence geofence) {
		// save these values into database
		/*
		 * Get a SharedPreferences editor instance. Among other things,
		 * SharedPreferences ensures that updates are atomic and non-concurrent
		 */
		Editor editor = mPrefs.edit();
		// Write the Geofence values to SharedPreferences
		editor.putFloat(getGeofenceFieldKey(id, KEY_LATITUDE),
				(float) geofence.getLatitude());
		editor.putFloat(getGeofenceFieldKey(id, KEY_LONGITUDE),
				(float) geofence.getLongitude());
		editor.putFloat(getGeofenceFieldKey(id, KEY_RADIUS),
				geofence.getRadius());
		editor.putLong(getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION),
				geofence.getExpirationDuration());
		editor.putInt(getGeofenceFieldKey(id, KEY_TRANSITION_TYPE),
				geofence.getTransitionType());
		editor.putInt(getGeofenceFieldKey(id, KEY_LOCATION_ID), locationId);
		// Commit the changes
		editor.commit();

	}

	/**
	 * Remove a flattened geofence object from storage by removing all of its
	 * keys
	 */
	public void clearGeofence(String id) {
        Editor editor = mPrefs.edit();
        editor.remove(getGeofenceFieldKey(id, KEY_LATITUDE));
        editor.remove(getGeofenceFieldKey(id, KEY_LONGITUDE));
        editor.remove(getGeofenceFieldKey(id, KEY_RADIUS));
        editor.remove(getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION));
        editor.remove(getGeofenceFieldKey(id, KEY_TRANSITION_TYPE));
        editor.remove(getGeofenceFieldKey(id, KEY_LOCATION_ID));
        editor.commit();
	}

	/**
	 * Given a Geofence object's ID and the name of a field (for example,
	 * KEY_LATITUDE), return the key name of the object's values in
	 * SharedPreferences.
	 * 
	 * @param id
	 *            The ID of a Geofence object
	 * @param fieldName
	 *            The field represented by the key
	 * @return The full key name of a value in SharedPreferences
	 */
	private String getGeofenceFieldKey(String id, String fieldName) {
		return KEY_PREFIX + "_" + id + "_" + fieldName;
	}
	
	public int getLocationId(String id) {
		return mPrefs.getInt(getGeofenceFieldKey(id, KEY_LOCATION_ID),0);
	}

}
