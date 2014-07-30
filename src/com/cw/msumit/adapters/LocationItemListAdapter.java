package com.cw.msumit.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.cw.msumit.R;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.objects.LocationProps;
import com.cw.msumit.utils.PopupMenu;

public class LocationItemListAdapter extends CursorAdapter {
	
	Context mContext;
	LayoutInflater inflater;
	String fontPath = "fonts/Roboto-Regular.ttf";
	private Typeface typeface;
	private AssetManager assetManager;
	private PopupMenu popupMenu;
	private ArrayList<LocationProps> listOfLocationProps;
	private DatabaseHandler dbHandler;
	private String locationId;
	OnLocationRemovedOrDeleted onLocationRemoveOrDeleted;

	public LocationItemListAdapter(Context context, Cursor c, int flags, String locationId, FragmentActivity activity) {
		super(context, c, flags);
		// TODO Auto-generated constructor stub
		mContext = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		assetManager = context.getAssets();
		typeface = Typeface.createFromAsset(assetManager, fontPath);
		listOfLocationProps = new ArrayList<LocationProps>();
		dbHandler = new DatabaseHandler(mContext);
		this.locationId = locationId;
		Log.d("itemview cons", this.locationId);
		onLocationRemoveOrDeleted = (OnLocationRemovedOrDeleted) activity;
	}

	public interface OnLocationRemovedOrDeleted{
		/**
		 * @param selected 0
		 * @param deleted 1
		 * @param removed 2
		 * @param locationId id of the location
		 */
		void onLocationRemovedOrDeleted(boolean selected, boolean deleted, boolean removed, String locationId);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return getCursor().getCount();
	}

	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
	}
	public LocationProps getLocationPropItem(int position) {
		// TODO Auto-generated method stub
		return listOfLocationProps.get(position);
	}
		
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private class ViewHolder{
		private TextView locationTitle, locationAddress;
		//public ImageView locationIcon;
		private ImageView locationExpand;
		private RelativeLayout locationMainContainer;
	}
	
	public int getItemViewType(Cursor mCursor) {
		String location_id = mCursor.getString(mCursor.getColumnIndex("_id"));
		Log.d("itemview", location_id);
		if(location_id.equals(this.locationId)) {
			return 1;
		} else return 0;
	}
	@Override
	public int getItemViewType(int position) {
		Cursor cursor = (Cursor) getItem(position);
		return getItemViewType(cursor);
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public void bindView(View view, final Context c, Cursor cursor) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		
		viewHolder.locationTitle.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.LOCATION_FEATURE_NAME)));
		viewHolder.locationTitle.setTypeface(typeface);
		viewHolder.locationAddress.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.LOCATION_ADDRESS)));
		viewHolder.locationAddress.setTypeface(typeface);
		
		//final RelativeLayout locationMainContainer = (RelativeLayout) view.getTag(R.id.locationMainContainer);
		final String location_id = mCursor.getString(mCursor.getColumnIndex("_id"));
		final int position = cursor.getPosition();
		if(getItemViewType(position)==1) {
			viewHolder.locationMainContainer.setBackgroundColor(Color.parseColor("#eeefef"));
		} else viewHolder.locationMainContainer.setBackgroundColor(Color.TRANSPARENT);
		

		Log.d("itemview bind " + Integer.toString(position), location_id);
		
		LocationProps lProps = new LocationProps();
		lProps.setAddress(viewHolder.locationAddress.getText().toString());
		lProps.setFeaturename(viewHolder.locationTitle.getText().toString());
		lProps.setLatitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.LOCATION_LATITUDE)));
		lProps.setLongitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.LOCATION_LONGITUDE)));
		
		lProps.setId(position);
		listOfLocationProps.add(position, lProps);
		//final RelativeLayout locationMainContainer = viewHolder.locationMainContainer;
		viewHolder.locationExpand.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupMenu = new PopupMenu(mContext, v);
				if(getItemViewType(position)==1) {
					popupMenu.getMenu().add(Menu.NONE, 2, 0, "Remove reminder");
				} else {
					popupMenu.getMenu().add(Menu.NONE, 0, 0, "Remind on this location");
				}
				popupMenu.getMenu().add(Menu.NONE, 1, 1, "Delete");
				popupMenu.show();
				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						switch (item.getItemId()) {
						case 0:
							//locationMainContainer.setBackgroundColor(Color.parseColor("#eeefef"));
							Log.d("itemview select re " + Integer.toString(position), location_id);
							onLocationRemoveOrDeleted.onLocationRemovedOrDeleted(true,false,false, location_id);
							Cursor cursor1 = dbHandler.getLocationCursor();
							locationId = location_id;
							changeCursor(cursor1);
							break;
						case 1:
							Log.d("itemview bind delete rem" + Integer.toString(position), location_id);
							dbHandler.resetLocationIdfromTask(location_id);
							dbHandler.deleteLocation(position);
							onLocationRemoveOrDeleted.onLocationRemovedOrDeleted(false,true,false, location_id);
							locationId = "0";
							Cursor cursor = dbHandler.getLocationCursor();
							changeCursor(cursor);
							break;
						case 2:
							Log.d("itemview bind remove re " + Integer.toString(position), location_id);
							dbHandler.resetLocationIdfromTask(location_id);
							onLocationRemoveOrDeleted.onLocationRemovedOrDeleted(false,false,true, location_id);
							locationId = "0";
							Cursor cursor2 = dbHandler.getLocationCursor();
							changeCursor(cursor2);
						default:
							break;
						}
						return true;
					}
				});
			}
		});
	}

	@Override
	public View newView(Context c, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = new ViewHolder();
		View convertView = inflater.inflate(R.layout.location_item_new, null);
		viewHolder.locationMainContainer = (RelativeLayout) convertView.findViewById(R.id.locationMainContainer);
		viewHolder.locationMainContainer.setTag(cursor.getPosition());
		viewHolder.locationTitle = (TextView) convertView.findViewById(R.id.locationName);
		viewHolder.locationAddress = (TextView) convertView.findViewById(R.id.locationDetails);
		//viewHolder.locationIcon = (ImageView) convertView.findViewById(R.id.locationIcon);
		viewHolder.locationExpand = (ImageView) convertView.findViewById(R.id.expand);
		//convertView.setTag(R.id.locationMainContainer, viewHolder.locationMainContainer);
		convertView.setTag(viewHolder);
		
		return convertView;
	}
	
}