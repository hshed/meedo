package com.cw.msumit.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Window;
import com.cw.msumit.R;
import com.cw.msumit.adapters.RepeatListExpandableAdapter;
import com.cw.msumit.views.MonthlyRepeatCell;

public class RepeatDialogFragment extends SherlockDialogFragment{

	ExpandableListView exv;
	boolean isExpanded = false;
	private static boolean[] weeklyRepeatDays;
	private static boolean noRepeat, dailyRepeat;
	private static int monthlyRepeatDay;
	public static String repeatString = "Repeat: No Repeat";
	List<MonthlyRepeatCell> cellsList = new ArrayList<MonthlyRepeatCell>();
	
	public static RepeatDialogFragment newInstance(boolean[] weeklyRdays, int monthlyRday, boolean daily, boolean norepeat){
		RepeatDialogFragment fragment= new RepeatDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putBooleanArray(CalendarFragment.REPEAT_WEEKLY, weeklyRdays);
		bundle.putInt(CalendarFragment.REPEAT_MONTHLY, monthlyRday);
		bundle.putBoolean(CalendarFragment.REPEAT_DAILY, daily);
		bundle.putBoolean(CalendarFragment.REPEAT_NONE, norepeat);
		
		weeklyRepeatDays = weeklyRdays;
		monthlyRepeatDay = monthlyRday;
		dailyRepeat = daily;
		noRepeat = norepeat;
		fragment.setArguments(bundle);
		return fragment;
	}
	
	

	@Override
	public void onActivityCreated(Bundle arg0) {
		// TODO Auto-generated method stub
		setRetainInstance(true);
		super.onActivityCreated(arg0);
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.repeat_dialog, null);
		getDialog().getWindow().requestFeature((int) Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(STYLE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_full_holo_light);
		
		exv= (ExpandableListView) view.findViewById(R.id.repeatList);
		final RepeatListExpandableAdapter adapter = new RepeatListExpandableAdapter(getSherlockActivity(), 
				getDialog(), new RepeatDialogHandler(), getArguments());
		
		exv.setAdapter(adapter);
		exv.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// TODO Auto-generated method stub				
				switch (groupPosition) {
				case 0:   //no repeat
					noRepeat = true;
					dailyRepeat = false;
					monthlyRepeatDay = 0;
					weeklyRepeatDays = new boolean[] {false, false, false, false, false, false, false};
					setRepeatString("Repeat: No Repeat");
					getDialog().dismiss();
					break;
				case 1:  // daily
					noRepeat = false;
					dailyRepeat = true;
					monthlyRepeatDay = 0;
					weeklyRepeatDays = new boolean[] {false, false, false, false, false, false, false};
					setRepeatString("Repeat: Daily");
					getDialog().dismiss();
					break;
				case 2: // weekly
					
					break;
				case 3: // monthly
					
					break;
				default:
					break;
				}
					
				return false;
			}
		});
		exv.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {
				// TODO Auto-generated method stub
				int len = adapter.getGroupCount();
			    for (int i = 0; i < len; i++) {
			        if (i != groupPosition) {
			            exv.collapseGroup(i);
			        }
			    }
			}
		});
		
		
		return view;
	}

	
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Send repeat values here on dismiss of the dialog
		
		((CalendarFragment) getTargetFragment()).setWeeklyRepeatValue(weeklyRepeatDays);
		((CalendarFragment) getTargetFragment()).setMonthlyRepeatValue(monthlyRepeatDay);
		((CalendarFragment) getTargetFragment()).setNoRepeatValue(noRepeat);
		((CalendarFragment) getTargetFragment()).setDailyRepeatValue(dailyRepeat);
		TextView repeat;
		repeat = (TextView) ((CalendarFragment) getTargetFragment()).getView().findViewById(R.id.repeat);
		repeat.setText(getRepeatString());
		super.onDismiss(dialog);
	}
	
	@Override
	 public void onDestroyView() {
	     if (getDialog() != null && getRetainInstance())
	         getDialog().setDismissMessage(null);
	         super.onDestroyView();
	 }

	private static class RepeatDialogHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// DO not touch these unless really needed.
			//bahut khatarnaak cases hain
			switch (msg.what) {
			case 1:               // weekly 
				monthlyRepeatDay = 0;
				weeklyRepeatDays = (boolean[]) msg.obj;
				dailyRepeat = false;
				noRepeat = false;
				setRepeatString("Repeat: Weekly");
				break;
			case 2:               // monthly
				weeklyRepeatDays = new boolean[] {false, false, false, false, false, false, false};
				monthlyRepeatDay = (Integer) msg.obj;
				noRepeat = false;
				dailyRepeat = false;
				setRepeatString("Repeat: Monthly");
				break;
			case 3:
				noRepeat = true;
				monthlyRepeatDay = 0;
				weeklyRepeatDays = new boolean[] {false, false, false, false, false, false, false};
				dailyRepeat = false;
				setRepeatString("Repeat: No Repeat");
				break;
			case 4:
				noRepeat = false;
				dailyRepeat = true;
				monthlyRepeatDay = 0;
				weeklyRepeatDays = new boolean[] {false, false, false, false, false, false, false};
				setRepeatString("Repeat: Daily");
			default:
				break;
			}
			super.handleMessage(msg);
		}	
	}
	
	public static void setRepeatString(String rString){
		repeatString = rString;
	}
	protected String getRepeatString() {
		return repeatString;
	}
	
}
