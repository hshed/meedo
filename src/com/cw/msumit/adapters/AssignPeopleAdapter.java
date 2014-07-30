package com.cw.msumit.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cw.msumit.R;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.objects.People;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class AssignPeopleAdapter extends CursorAdapter {

	private DatabaseHandler dbHandler;
	private LayoutInflater inflater;
	public Bitmap[] imageArray;
	private int width;
	private int height;
	//FacebookImage facebookImage;
	Context mContext;
	Typeface typeface;
	String fontPath = "fonts/Roboto-Regular.ttf";
	private ImageLoader imageLoader;
	private DisplayImageOptions displayOptions;
	private HashMap<String, Bitmap> bitmapMap;
	private HashMap<String, String> emailMap;
	ArrayList<People> peopleList;

	public AssignPeopleAdapter(Context context, Cursor c, int flags) {
		
		super(context, c, flags);
		// TODO Auto-generated constructor stub
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.dbHandler = new DatabaseHandler(context);
		mContext = context;
		typeface = Typeface.createFromAsset(mContext.getAssets(), fontPath);

		switch (context.getResources().getDisplayMetrics().densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			width = 32;
			height = 32;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			width = 36;
			height = 36;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			width = 54;
			height = 54;
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			width = 72;
			height = 72;
			break;
		}
		
		displayOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc()
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showStubImage(R.drawable.anon)
				.showImageOnFail(R.drawable.anon)
				.build();
		
		
		imageLoader = ImageLoader.getInstance();
		
		bitmapMap = new HashMap<String, Bitmap>();
		emailMap = new HashMap<String, String>();
		peopleList = new ArrayList<People>();

	}

	private class ViewHolder {
		private TextView contactName, contactMeans;
		private ImageView contactImage;
	}

	public People getPeople(int position) {
		Cursor cursor = getCursor();
	    People people = null;
	    if(cursor.moveToPosition(position)) {
	        String name = cursor.getString(cursor.getColumnIndex("user_friend_name"));
			boolean hasMeedo = cursor.getString(cursor.getColumnIndex("hasWires")).equals("1");
			String email = cursor.getString(cursor.getColumnIndex("user_friend_emails"));
			String userId = cursor.getString(cursor.getColumnIndex("user_friend_id"));
	        people = new People(hasMeedo, name, email, userId);
	    }

	    return people;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		
		String name = cursor.getString(cursor.getColumnIndex("user_friend_name"));
		boolean hasMeedo = cursor.getString(cursor.getColumnIndex("hasWires")).equals("1");
		String email = cursor.getString(cursor.getColumnIndex("user_friend_emails"));
		
		viewHolder.contactName.setText(name.toString());
		viewHolder.contactName.setTypeface(typeface);
		viewHolder.contactMeans.setTypeface(typeface);
		viewHolder.contactImage.getLayoutParams().width = width;
    	viewHolder.contactImage.getLayoutParams().height = height;

		final String userId = cursor.getString(cursor.getColumnIndex("user_friend_id"));
		
		String picture = null;
		if (hasMeedo) {
			
			picture = "http://graph.facebook.com/" + userId
					+ "/picture?type=small";
			if (!emailMap.containsKey(userId)) {
				emailMap.put(userId, "Meedo User");
			}
			
		} else {
			if (!emailMap.containsKey(userId)) {
				emailMap.put(userId, email);
			}
			
			Uri uri = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI,
					Long.parseLong(userId));
			
			 picture = uri.toString();
		}
		
		if(!bitmapMap.containsKey(userId)){
			
			imageLoader.displayImage(picture, viewHolder.contactImage, displayOptions, new SimpleImageLoadingListener() { 
				@Override
			    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			        // Do whatever you want with Bitmap
			    	bitmapMap.put(userId, loadedImage);
			    	//viewHolder.contactImage.setImageBitmap(loadedImage);
			    }

				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					// TODO Auto-generated method stub
					viewHolder.contactImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.anon));
					super.onLoadingFailed(imageUri, view, failReason);
				}
			});
			
		}
		viewHolder.contactImage.setImageBitmap(bitmapMap.get(userId));
		viewHolder.contactMeans.setText(emailMap.get(userId));
		
	}
	

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = new ViewHolder();
		View view = inflater.inflate(R.layout.people_suggestion, null);
		viewHolder.contactImage = (ImageView) view
				.findViewById(R.id.contactImage);
		viewHolder.contactImage.setTag(cursor.getPosition());
		viewHolder.contactMeans = (TextView) view
				.findViewById(R.id.contactMeans);
		viewHolder.contactName = (TextView) view.findViewById(R.id.contactName);

		view.setTag(viewHolder);

		return view;
	}
	
	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		// TODO Auto-generated method stub
		if (getFilterQueryProvider() != null) {
			return getFilterQueryProvider().runQuery(constraint);
		}
		String filter = "";
		if (constraint != null) {
			filter = constraint.toString();
		}

		return dbHandler.getAutoCompleteCursor(filter);
	}

	@Override
	public CharSequence convertToString(Cursor cursor) {
		// TODO Auto-generated method stub
		return cursor.getString(cursor.getColumnIndex("user_friend_name"));
	}
}