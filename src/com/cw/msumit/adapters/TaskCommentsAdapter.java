package com.cw.msumit.adapters;

import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cw.msumit.R;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.utils.StaticFunctions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class TaskCommentsAdapter extends CursorAdapter {

	Context mContext;
	Cursor mCursor;
	int mFlag;
	private int width, height;
	LayoutInflater inflater;
	Typeface typeface, typeface1;
	AssetManager assetManager;
	String fontPath = "fonts/Roboto-Regular.ttf";
	String fontPath1 = "fonts/Roboto-Bold.ttf";
	ImageLoader imageLoader;
	DisplayImageOptions displayOptions;
	private HashMap<String, Bitmap> bitmapMap;
	DatabaseHandler dbHandler;

	public TaskCommentsAdapter(Context context, Cursor cursor, int flags) {
		super(context, cursor, flags);
		mContext = context;
		mCursor = cursor;
		mFlag = flags;
		//mListView = listView;
		//mListView.setOnScrollListener(this);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		assetManager = context.getAssets();
		typeface = Typeface.createFromAsset(assetManager, fontPath);
		typeface1 = Typeface.createFromAsset(assetManager, fontPath1);

		switch (context.getResources().getDisplayMetrics().densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			width = 48;
			height = 48;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			width = 48;
			height = 48;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			width = 72;
			height = 72;
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			width = 96;
			height = 96;
			break;
		}
		displayOptions = new DisplayImageOptions.Builder().cacheOnDisc()
			.bitmapConfig(Bitmap.Config.RGB_565)
			.showStubImage(R.drawable.anon)
			.showImageOnFail(R.drawable.anon)
			.build();

		imageLoader = ImageLoader.getInstance();
		
		bitmapMap = new HashMap<String, Bitmap>();
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

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		// TODO Auto-generated method stub
		return super.runQueryOnBackgroundThread(constraint);
	}

	/**
	 * It returns item view type. Type 0 implies task activity not comments.
	 * Type 1 implies comment from friend. Type 2 implies comment from user.
	 * @param cursor
	 * @return type 0, 1 or 2
	 */
	private int getItemViewType(Cursor cursor) {
		return getUniqueId(cursor);
	}

	private int getUniqueId(Cursor cursor) {
		String actiontype = cursor.getString(cursor
				.getColumnIndex("action_type"));
		if (!actiontype.equals("Comment")) { // means it is other action types like
												// date etc.
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		// Cursor cursor = (Cursor) getItem(position);
		getCursor().moveToPosition(position);
		return getItemViewType(getCursor());
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	/**
	 * @param userName Name of the user
	 * @param commentTime Time of comment e.g. 2 mins ago
	 * @param commentContent The comment
	 * @param taskActivity Activity related to task like editing, changing dates etc.
	 * @param userImage Image of the user
	 */
	private class ViewHolder {
		private TextView userName, commentTime, commentContent, taskActivity;
		private ImageView userImage;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder = (ViewHolder) view.getTag();

		int itemType = getItemViewType(cursor.getPosition());
		
		if (itemType == 0) {
              viewHolder.taskActivity.setText(cursor.getString(cursor.getColumnIndex("action_value")));
               
		} else {
			final String userId = cursor.getString(cursor.getColumnIndex("user_id"));
			final String username = cursor.getString(cursor.getColumnIndex("user_name")) ;
			String picture = "http://graph.facebook.com/" + userId
					+ "/picture?type=small";
			
			viewHolder.userImage.getLayoutParams().width = width;
	    	viewHolder.userImage.getLayoutParams().height = height;

	    	if(StaticFunctions.hasInternet(mContext)) {
	    		if(!bitmapMap.containsKey(userId)){
					
					imageLoader.displayImage(picture, viewHolder.userImage, displayOptions, new SimpleImageLoadingListener() {

						@Override
					    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					        // Do whatever you want with Bitmap
					    	bitmapMap.put(userId, loadedImage);
					    }

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							viewHolder.userImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.anon));
							super.onLoadingFailed(imageUri, view, failReason);
						}
					});
					
				}
	    		viewHolder.userImage.setImageBitmap(bitmapMap.get(userId));
	    	} else viewHolder.userImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.anon));
			
			viewHolder.userName.setText(username);
			viewHolder.commentTime.setText(DateUtils.getRelativeDateTimeString(mContext, cursor.getLong(cursor.getColumnIndex("action_timestamp")),
					Calendar.getInstance().getTimeInMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));
            viewHolder.commentContent.setText(cursor.getString(cursor.getColumnIndex("action_value")));
            
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = new ViewHolder();
		View view = null;
		int itemType = getItemViewType(cursor.getPosition());
		if (itemType == 0) {
			view = inflater.inflate(R.layout.comment_activity, null);
			viewHolder.taskActivity = (TextView) view
					.findViewById(R.id.taskActivity);
			viewHolder.taskActivity.setTypeface(typeface);
		} else {
			view = inflater.inflate(R.layout.comment_new, null);
			viewHolder.userName = (TextView) view.findViewById(R.id.userName);
			viewHolder.userName.setTypeface(typeface1);
			viewHolder.userImage = (ImageView) view
					.findViewById(R.id.userImage);
			viewHolder.userImage.setTag(cursor.getPosition());
			viewHolder.commentTime = (TextView) view
					.findViewById(R.id.commentTime);
			viewHolder.commentTime.setTypeface(typeface);
			viewHolder.commentContent = (TextView) view
					.findViewById(R.id.commentContent);
			viewHolder.commentContent.setTypeface(typeface);
			

		}
		view.setTag(viewHolder);
		return view;
	}
	
	//Legacy codes - do not delete yet.
	/**
	private class LoadComments extends AsyncTask<Integer, Void, Cursor> {
		private Cursor cursor;

		@Override
		protected Cursor doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			int from = params[0];
			int limit = params[1];
			dbHandler = new DatabaseHandler(mContext);
			cursor = dbHandler.getActionCursor("task_id", "1", from, limit);
			return cursor;
		}

		@Override
		protected void onPostExecute(Cursor result) {
			// TODO Auto-generated method stub
			changeCursor(cursor);
			super.onPostExecute(result);
		}
		
	}

	private class FacebookImage extends AsyncTask<String, Void, Bitmap[]> {

		private ViewHolder mViewHolder;
		private Cursor mCursor;

		public FacebookImage(ViewHolder viewHolder, Cursor cursor) {
			mViewHolder = viewHolder;
			mCursor = cursor;
		}

		@Override
		protected Bitmap[] doInBackground(String... params) {
			// TODO Auto-generated method stub

			URL img_value = null;
			Bitmap mFb = null;
			// Bitmap mContact = null;
			Bitmap defaultPhoto = BitmapFactory.decodeResource(
					mContext.getResources(), R.drawable.wiresimage);

			try {
				if (mCursor.getString(mCursor.getColumnIndex("task_id"))
						.equals("1")) {
					String picture = "http://graph.facebook.com/" + params[0]
							+ "/picture?type=small";
					try {
						img_value = new URL(picture);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						mFb = BitmapFactory.decodeStream(img_value
								.openConnection().getInputStream());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI,
							Long.parseLong(params[0]));
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(
									mContext.getContentResolver(), uri);
					if (input != null) {
						mFb = BitmapFactory.decodeStream(input);
					} else {
						mFb = defaultPhoto;
					}
				}
			} catch (StaleDataException e) {
				// TODO: handle exception
				mCursor = getCursor();
			}
			//ImageLoader.getInstance().displayImage(imageUrl, imageView);
			return new Bitmap[] { mFb };
		}

		@Override
		protected void onPostExecute(Bitmap[] result) {
			Bitmap[] imageArray = result;
			mViewHolder.userImage.getLayoutParams().width = width;
			mViewHolder.userImage.getLayoutParams().height = height;

			mViewHolder.userImage.setImageBitmap(imageArray[0]);

		}
	}


	/*@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		//dbHandler = new DatabaseHandler(mContext);
		//mCursor = dbHandler.getActionCursor("task_id", "1", firstVisibleItem + visibleItemCount, visibleItemCount);
		//changeCursor(mCursor);	
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		//changeCursor(cursor);
		Cursor mCursor = getCursor();
		Log.d("Scrolling", "hell nope");

				switch (scrollState) {
		        case OnScrollListener.SCROLL_STATE_IDLE:
		            mBusy = false;
		            /*int first = view.getFirstVisiblePosition();
		            Log.d("First", Integer.toString(first));
		            int count = view.getChildCount();
		            Log.d("Count", Integer.toString(count));
		            for (int i=0; i<count; i++) {
		            	View item = view.getChildAt(i);
		            	int itemType = getItemViewType(first + i);
		            	TextView actent;
		        		if (itemType == 0) {
		        			actent = (TextView) item.findViewById(R.id.taskActivity);
		        		} else {
			                actent = (TextView)item.findViewById(R.id.commentContent);
			                
		        		}
		                if (actent.getTag() != null) {
		                	mCursor.moveToPosition(first + i);
		                    actent.setText(mCursor.getString(mCursor.getColumnIndex("action_value")));
		                    actent.setTag(null);
		                }
		            }*/
		       /*     
		            break;
		        case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
		        	if (view.getFirstVisiblePosition() <= 1) {
		    			Log.d("Scrolling", "hell yeah");
		    			loadComments = new LoadComments();
		    			loadComments.execute(new Integer[]{view.getFirstVisiblePosition(), 50});
		    		}
		            mBusy = true;
		            break;
		        case OnScrollListener.SCROLL_STATE_FLING:
		        	if (view.getFirstVisiblePosition() <= 1) {
		    			Log.d("Scrolling", "hell yeah");
		    			loadComments = new LoadComments();
		    			loadComments.execute(new Integer[]{view.getFirstVisiblePosition(), 50});
		    		}
		            mBusy = true;
		            break;
		        }
			}*/
			
}