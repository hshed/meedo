package com.cw.msumit.utils;

import com.google.android.gms.location.Geofence;

public class ReminderGeofence {

	public static final String LOCATION_ID = "locationId";
	private final String mId;
	private final double mLatitude, mLongitude;
	private final float mRadius;
	private final long mExpirationDuration;
	private final int mTransitionType;

	/**
	 * constructor for a new geofence
	 * @param geofenceId reminderID
	 * @param latitude
	 * @param longitude
	 * @param radius in meters
	 * @param expiration in milliseconds
	 * @param transition GEOFENCE_TRANSITION_ENTER etc.
	 */
	public ReminderGeofence(String geofenceId, double latitude,
			double longitude, float radius, long expiration, int transition) {
		this.mId = geofenceId;
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mRadius = radius;
		this.mExpirationDuration = expiration;
		this.mTransitionType = transition;
	}

	public String getId() {
		return mId;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public float getRadius() {
		return mRadius;
	}

	public long getExpirationDuration() {
		return mExpirationDuration;
	}

	public int getTransitionType() {
		return mTransitionType;
	}

	/**
	 * Creates a Location Services Geofence object from a {@link ReminderGeofence}.
	 * @return A Geofence object
	 */
	public Geofence toGeofence() {
		// Build a new Geofence object
		return new Geofence.Builder().setRequestId(getId())
				.setTransitionTypes(mTransitionType)
				.setCircularRegion(getLatitude(), getLongitude(), getRadius())
				.setExpirationDuration(mExpirationDuration).build();
	}
}
