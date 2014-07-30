package com.cw.msumit.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cw.msumit.R;
import com.cw.msumit.SubtaskActivity;
import com.cw.msumit.objects.Task;
import com.cw.msumit.utils.StaticFunctions;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class ReminderListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Task> data;
	private static LayoutInflater inflater = null;
	String fontPath1 = "fonts/Roboto-Bold.ttf";
	String fontPath = "fonts/Roboto-Regular.ttf";
	AssetManager assetManager;
	Typeface typeface, plain;
	Task reminders;
	String ActionTitle;
	public static List<String> list= new ArrayList<String>();
	
	
	//added by me
	CheckBox checkBox;

	// public ImageLoader imageLoader;

	public ReminderListAdapter(Context c, ArrayList<Task> d, String what) {
		context = c;
		data = d;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		assetManager = c.getAssets();
		typeface = Typeface.createFromAsset(assetManager, fontPath);
		plain = Typeface.createFromAsset(assetManager, fontPath1);
		ActionTitle=what;
		
		
		// imageLoader=new ImageLoader(activity.getApplicationContext());
	}

	/*
	 * public ReminderListAdapter( Context c, ArrayList<Reminders> r ) {
	 * //convert the array of reminders into Arraylist<Hashmap<String, String>>
	 * d ArrayList<HashMap<String, String>> d= new
	 * ArrayList<HashMap<String,String>>();
	 * 
	 * for(int i=0; i<=r.size(); i++) { Reminders reminders=r.get(i); //add all
	 * the data to a hashmap HashMap<String, String> map = new
	 * HashMap<String,String>(); map.put("title", r.Title); map.put("date",
	 * r.Date); map.put("time", r.Time); map.put("location_id", r.LocationID);
	 * map.put("location_reminder", r.LocationReminder); map.put("important",
	 * Integer.toString(r.Important)); map.put("repeat", r.Repeat);
	 * map.put("creator_id", r.creatorID); map.put("synced",
	 * Integer.toString(r.Synced));
	 * 
	 * d.add(map); } context = c; data = d; inflater = (LayoutInflater) context
	 * .getSystemService(Context.LAYOUT_INFLATER_SERVICE); assetManager =
	 * c.getAssets(); plain = Typeface.createFromAsset(assetManager, fontPath);
	 * 
	 * }
	 */
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void add(Task task, int position) {
		data.add(position, task);
	}
	
	//added by me. don't do anything to it
	private class ViewHolder {
        protected TextView titleOfReminder, personInvolved, 
        					reminderDate, reminderTime;
        protected ImageButton subtasks;
        protected ImageView Important;
        protected View root;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final ViewHolder viewHolder;
		
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.reminder_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.titleOfReminder = (TextView) convertView.findViewById(R.id.titleOfReminder);
			viewHolder.titleOfReminder.setTypeface(typeface);
			viewHolder.personInvolved = (TextView) convertView.findViewById(R.id.personInvolved);
			viewHolder.personInvolved.setTypeface(typeface);
			viewHolder.reminderDate = (TextView) convertView.findViewById(R.id.reminderDate);
			viewHolder.reminderDate.setTypeface(typeface);
			viewHolder.reminderTime = (TextView) convertView.findViewById(R.id.reminderTime);
			viewHolder.reminderTime.setTypeface(typeface);
			viewHolder.Important = (ImageView) convertView.findViewById(R.id.starredOrNot);
			viewHolder.subtasks = (ImageButton) convertView.findViewById(R.id.subtask_image);
			//viewHolder.subtaskCount = (TextView) convertView.findViewById(R.id.subtaskCount);
			//viewHolder.subtaskCount.setTypeface(typeface);
			
			viewHolder.root = (View) viewHolder.subtasks.getParent();
			
			convertView.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		
		reminders = new Task();
		reminders = data.get(position);
		// Setting all values in listview
		
					viewHolder.titleOfReminder.setText(reminders.Title);
					if (ActionTitle.equals("Completed Tasks")) {
						viewHolder.titleOfReminder.setPaintFlags
						(viewHolder.titleOfReminder.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
						viewHolder.titleOfReminder.setTextColor(Color.parseColor("#9C9C9C"));
					}
					viewHolder.personInvolved.setText(reminders.Category);
					//viewHolder.subtaskCount.setText(Integer.toString(reminders.Subtasks));
					//BitmapDrawable bmDrawable = StaticFunctions.writeOnDrawable(R.drawable.subtask, Integer.toString(reminders.Subtasks), context, plain);
					Drawable bmDrawable = StaticFunctions.getSubtaskDrawable(reminders.Subtasks, context);
					viewHolder.subtasks.setImageDrawable(bmDrawable);
					
					if(reminders.Date.equals("N-N-N")){
						viewHolder.reminderDate.setVisibility(View.GONE);
					}
					else {
						viewHolder.reminderDate.setVisibility(View.VISIBLE);
						viewHolder.reminderDate.setText(StaticFunctions.Date(reminders.Date));
					}
					if(reminders.Time.equals("N:N")){
						viewHolder.reminderTime.setVisibility(View.GONE);
					}
					else {
						viewHolder.reminderTime.setVisibility(View.VISIBLE);
						viewHolder.reminderTime.setText(StaticFunctions.timeWithoutComma(reminders.Time));
					}
					/*} else {
						//if it is completed task
						viewHolder.reminderTime.setVisibility(View.GONE);
						viewHolder.reminderTime.setVisibility(View.GONE);
						String d=StaticFunctions.Date(reminders.getCompletedOn());
						String c= "Completed on";
						if (d.equals("today")) {
							c="Completed";
						}
						String text=reminders.Category + " (" + c + " "+ d +")";
						viewHolder.personInvolved.setText(text);
					}*/
					
					if (reminders.Important == 0) {
						viewHolder.Important.setVisibility(View.INVISIBLE);
					} else {
						viewHolder.Important.setVisibility(View.VISIBLE);
					}
					if (reminders.Subtasks == 0) {
						viewHolder.subtasks.setVisibility(View.GONE);
						//viewHolder.subtaskCount.setVisibility(View.INVISIBLE);
					} else {
						viewHolder.subtasks.setVisibility(View.VISIBLE);
						//viewHolder.subtaskCount.setVisibility(View.VISIBLE);
					}
					
					viewHolder.subtasks.setClickable(true);
				
				viewHolder.subtasks.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(context, SubtaskActivity.class);
						intent.putExtra("ReminderID", data.get(position).ReminderID);
						Log.d("ReminderID",data.get(position).ReminderID);
						String[] subtasks={"0"};
						intent.putExtra("subtaskArray", subtasks);
						context.startActivity(intent);
						((SlidingFragmentActivity) context).overridePendingTransition( R.anim.slide_up_dialog, R.anim.slide_out_dialog );
						
					}
				});
				
				viewHolder.root.post(new Runnable() {
		            @Override
					public void run() {
		                // Post in the parent's message queue to make sure the parent
		                // lays out its children before we call getHitRect()
		                Rect delegateArea = new Rect();
		                ImageButton delegate = viewHolder.subtasks;
		                delegate.getHitRect(delegateArea);
		                delegateArea.top -= 10;
		                delegateArea.bottom += 10;
		                delegateArea.right += 10;
		                delegateArea.left -=10;
		                TouchDelegate expandedArea = new TouchDelegate(delegateArea,
		                        delegate);
		                // give the delegate to an ancestor of the view we're
		                // delegating the
		                // area to
		                if (View.class.isInstance(delegate.getParent())) {
		                    ((View) delegate.getParent())
		                            .setTouchDelegate(expandedArea);
		                }
		            };
		        });
				
		return convertView;
		
		
	}

	public void remove(Object item) {
		// TODO Auto-generated method stub
		data.remove(item);
	}
}
