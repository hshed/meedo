package com.cw.msumit.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.cw.msumit.R;
import com.cw.msumit.databases.DatabaseMethods;

public class LocationSuggestionAdapter extends BaseAdapter implements
		Filterable {

	Context mContext;
	List<Address> listAddresses;
	ArrayList<String> locationTitlesList;
	Address addressLine;
	String locationTitle;
	Geocoder geocoder;
	LayoutInflater inflater;
	String fontPath = "fonts/Roboto-Regular.ttf";
	Typeface typeface;
	AssetManager assetManager;

	public LocationSuggestionAdapter(Context context) {
		mContext = context;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		geocoder = new Geocoder(mContext);

		assetManager = context.getAssets();
		typeface = Typeface.createFromAsset(assetManager, fontPath);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listAddresses.size();
	}

	@Override
	public Address getItem(int arg0) {
		// TODO Auto-generated method stub
		return listAddresses.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	private class ViewHolder {
		public TextView locationTitle;
		public TextView locationAddress;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.location_suggestion, null);
			viewHolder = new ViewHolder();
			viewHolder.locationTitle = (TextView) convertView
					.findViewById(R.id.titleSuggested);
			viewHolder.locationAddress = (TextView) convertView
					.findViewById(R.id.addressSuggested);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Address address = listAddresses.get(position);
		if (address != null) {
			viewHolder.locationTitle.setText(address.getFeatureName());
			viewHolder.locationTitle.setTypeface(typeface);
			viewHolder.locationAddress.setText(DatabaseMethods
					.getAddress(address));
			viewHolder.locationAddress.setTypeface(typeface);
		}

		return convertView;
	}

	@Override
	public Filter getFilter() {
		Filter myFilter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					try {
						listAddresses = geocoder.getFromLocationName(
								constraint.toString(), 10);
						for (int i = 0; i < listAddresses.size(); i++) {
							addressLine = listAddresses.get(i);
							locationTitle = DatabaseMethods
									.getAddress(addressLine);
							locationTitlesList.add(0, locationTitle);
						}
					} catch (Exception e) {
					}
					filterResults.values = locationTitlesList;
					filterResults.count = listAddresses.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence contraint,
					FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}

		};
		return myFilter;
	}

}
