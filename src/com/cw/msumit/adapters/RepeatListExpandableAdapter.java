package com.cw.msumit.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cw.msumit.R;
import com.cw.msumit.fragments.CalendarFragment;
import com.cw.msumit.views.MonthlyRepeatCell;
import com.cw.msumit.views.MonthlyRepeatView;
import com.cw.msumit.views.MonthlyRepeatView.OnCellTouchListener;

public class RepeatListExpandableAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	private String[] repeatListItems= new String[]{"No Repeat", "Daily", "Weekly", "Monthly"};
	private List<MonthlyRepeatCell> cellsList = new ArrayList<MonthlyRepeatCell>();
	private Dialog mDialog;
	private boolean[] weeklyRepeatingDays;
	private boolean noRepeat, dailyRepeat;
	private int monthlyRepeatDay;
	private Handler mHandler;
	public static boolean dummyValue = false;
	private Typeface typeface;
	private AssetManager assetManager;
	private String fontpath = "fonts/Roboto-Condensed.ttf";
	boolean check = false;
	
	public RepeatListExpandableAdapter(Context context, Dialog dialog, Handler handler, Bundle bundle) {
		// TODO Auto-generated constructor stub
		mContext = context;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDialog = dialog;
		mHandler = handler;
		weeklyRepeatingDays = bundle.getBooleanArray(CalendarFragment.REPEAT_WEEKLY);
		monthlyRepeatDay = bundle.getInt(CalendarFragment.REPEAT_MONTHLY, 0);
		noRepeat = bundle.getBoolean(CalendarFragment.REPEAT_NONE);
		dailyRepeat = bundle.getBoolean(CalendarFragment.REPEAT_DAILY);
		assetManager = context.getAssets();
		typeface = Typeface.createFromAsset(assetManager, fontpath);
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition + groupPosition;
	}

	
	@Override
	public int getChildType(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		if(groupPosition==2)
			return 0;
		return 1;
	}

	@Override
	public int getChildTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(getChildType(groupPosition, childPosition) == 0){    //contents of Weekly
			if(convertView==null)
				convertView= inflater.inflate(R.layout.weekly_repeat, null);
			TextView okWeeklyTextView = (TextView) convertView.findViewById(R.id.okWeekly);
			TextView cancelWeeklyR = (TextView) convertView.findViewById(R.id.cancelWeeklyR);
			TextView monR = (TextView) convertView.findViewById(R.id.monRtext);
			monR.setTag("monR");
			TextView tueR = (TextView) convertView.findViewById(R.id.tueRtext);
			tueR.setTag("tueR");
			TextView wedR = (TextView) convertView.findViewById(R.id.wedRtext);
			wedR.setTag("wedR");
			TextView thuR = (TextView) convertView.findViewById(R.id.thuRtext);
			thuR.setTag("thuR");
			TextView friR = (TextView) convertView.findViewById(R.id.friRtext);
			friR.setTag("friR");
			TextView satR = (TextView) convertView.findViewById(R.id.satRtext);
			satR.setTag("satR");
			TextView sunR = (TextView) convertView.findViewById(R.id.sunRtext);
			sunR.setTag("sunR");
			
			TextView[] tViews = new TextView[] {monR, tueR, wedR,
					thuR, friR, satR, sunR};
			for(TextView tv : tViews){
				tv.setTypeface(typeface);
				tv.setCompoundDrawablesWithIntrinsicBounds(null, 
						weeklyRepeatingDays[getIndexofTextView(tv.getTag().toString())]?
								mContext.getResources().getDrawable(R.drawable.weekly_repeat_circle_blue) :
									mContext.getResources().getDrawable(R.drawable.weekly_repeat_circle), null, null);
			}
			
			View.OnClickListener dayRListener = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					TextView tView = (TextView)v;
					Log.d("g", tView.getTag().toString());
					if(weeklyRepeatingDays[getIndexofTextView(tView.getTag().toString())] == true){
						tView.setCompoundDrawablesWithIntrinsicBounds(null, 
								mContext.getResources().getDrawable(R.drawable.weekly_repeat_circle), null, null);
						weeklyRepeatingDays[getIndexofTextView(tView.getTag().toString())] = false;						
					} else {
						tView.setCompoundDrawablesWithIntrinsicBounds(null, 
								mContext.getResources().getDrawable(R.drawable.weekly_repeat_circle_blue), null, null);
						weeklyRepeatingDays[getIndexofTextView(tView.getTag().toString())] = true;
					}
				}
			};
			
			monR.setOnClickListener(dayRListener);
			tueR.setOnClickListener(dayRListener);
			wedR.setOnClickListener(dayRListener);
			thuR.setOnClickListener(dayRListener);
			friR.setOnClickListener(dayRListener);
			satR.setOnClickListener(dayRListener);
			sunR.setOnClickListener(dayRListener);
			okWeeklyTextView.setTypeface(typeface);
			cancelWeeklyR.setTypeface(typeface);
			okWeeklyTextView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					for(boolean booleans: weeklyRepeatingDays){ // if any week day is true, month repeat should be off
						if(booleans == true){
							monthlyRepeatDay = 0;
							noRepeat = false;
							dailyRepeat = false;
							check = true;
							break;
						} else {
							if(monthlyRepeatDay!=0){
								noRepeat = false;
								dailyRepeat = false;
							}else if(dailyRepeat){
								noRepeat = false;
								dailyRepeat = true;
								monthlyRepeatDay = 0;
							} else {
								noRepeat = true;
								dailyRepeat = false;
								monthlyRepeatDay = 0;
							}
							
						}
					}
					Message msg1 = new Message();
					if(monthlyRepeatDay==0 && !noRepeat && !dailyRepeat){
						msg1.what = 1;
						msg1.obj = weeklyRepeatingDays;
						mHandler.sendMessage(msg1);
					} else if(monthlyRepeatDay!=0 && !noRepeat && !dailyRepeat) {  //set monthly
						msg1.what = 2;
						msg1.obj = monthlyRepeatDay;   // similar to case 2 below
						mHandler.sendMessage(msg1);
					} else if(monthlyRepeatDay==0 && !noRepeat && dailyRepeat) {  // set daily
						mHandler.sendEmptyMessage(4);
					} else {
						mHandler.sendEmptyMessage(3);  // set no repeat
					}
					mDialog.dismiss();
				}
			});
			
			cancelWeeklyR.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mDialog.dismiss();
				}
			});
			
		} 
		else {   // contents of Monthly
			if(convertView==null){
				convertView = inflater.inflate(R.layout.monthly_repeat, null);
			}
			MonthlyRepeatView.setMonthlyRepeatDay(monthlyRepeatDay);
			final MonthlyRepeatView monthlyRepeatView = (MonthlyRepeatView) convertView.findViewById(R.id.monthlyRepeat);
			monthlyRepeatView.setOnCellTouchListener(new OnCellTouchListener() {
				
				@Override
				public void onTouch(MonthlyRepeatCell cell) {
					// TODO Auto-generated method stub
					if(!cellsList.isEmpty()){
						if(cell.isSelected()){
							cell.setSelected(false);
							monthlyRepeatView.invalidate(cell.getBound());	
							cellsList.remove(0);
						} else {
							cellsList.get(0).setSelected(false);
							monthlyRepeatView.invalidate(cellsList.get(0).getBound());	
							cellsList.remove(0);
							cell.setSelected(true);
							monthlyRepeatView.invalidate(cell.getBound());	
							cellsList.add(0, cell);
						}
						
					} else {
							cellsList.add(0, cell);	
							cell.setSelected(true);
							monthlyRepeatView.invalidate(cell.getBound());
						}
				}
			});
			TextView okMonthlyrepeat = (TextView) convertView.findViewById(R.id.okrepeat);
			TextView cancelMonthlyR = (TextView) convertView.findViewById(R.id.cancelMonthlyR);
			okMonthlyrepeat.setTypeface(typeface);
			cancelMonthlyR.setTypeface(typeface);
			okMonthlyrepeat.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Message msg2 = new Message();
					if(!cellsList.isEmpty()){
						if(cellsList.get(0)!=null){  // if a new day was indeed selected
							monthlyRepeatDay = cellsList.get(0).getDayOfMonth();
							msg2.obj = monthlyRepeatDay;
							msg2.what = 2;
							mHandler.sendMessage(msg2);
						}
					}else { // if no new day was selected
						msg2.obj = monthlyRepeatDay;
						msg2.what = 2;
						mHandler.sendMessage(msg2);
					}				
					mDialog.dismiss();
				}
			});
			
			cancelMonthlyR.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mDialog.dismiss();
				}
			});
			
		}
		return convertView;
	}

	
	/**
	 * returns an index of the textview
	 * @param tView TextView tag in String format i.e. textview.getTag().toString()
	 * @return index from 0 to 6
	 */
	public int getIndexofTextView(String tView) {
		if(tView == "monR")
			return 0;
		if(tView == "tueR")
			return 1;
		if(tView == "wedR")
			return 2;
		if(tView == "thuR")
			return 3;
		if(tView == "friR")
			return 4;
		if(tView == "satR")
			return 5;
		if(tView == "sunR")
			return 6;
		return -1;
	}
	
	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		if(groupPosition<=1)
			return 0;
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = inflater.inflate(R.layout.repeat_list_item, null);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.repeatType);
		tv.setTypeface(typeface);
		tv.setText(repeatListItems[groupPosition]);
		
		if(getGroupId(groupPosition) == getGroupPosition()) {
			//tv.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.repeat_selected), null, null, null);
			tv.setTextColor(Color.rgb(45, 188, 215));
		} else {
			//tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			tv.setTextColor(Color.rgb(130, 130, 130));
		}

		View ind = convertView.findViewById( R.id.expand);
		if( ind!=null ) {
			ImageView indicator = (ImageView)ind;
			if ( getChildrenCount( groupPosition ) == 0 ) {
			       indicator.setVisibility( View.INVISIBLE );
			    } else {
			       indicator.setVisibility( View.VISIBLE );
			       indicator.setImageResource( isExpanded ? R.drawable.collapse : R.drawable.expand );
			    }
		} 
		return convertView;
	}

	/**
	 * This method returns the group position of textview
	 * e.g. if daily repeat is true, the group position is 1, 
	 * so show tick mark at group position at 1
	 * @return group position
	 */
	protected int getGroupPosition() {
		if(noRepeat){
			return 0;
		} else {
			if(dailyRepeat){
				return 1;
			} else {
				if(monthlyRepeatDay!=0){
					return 3;
				} else {
					boolean temp = false;
					for(boolean bool: weeklyRepeatingDays){
						if(bool == true){
							break;
						}
						temp = bool;
					}
					if(temp){
						return 2;
					}
				}
			}
		}
		return 2;
	}
	
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}
	
}