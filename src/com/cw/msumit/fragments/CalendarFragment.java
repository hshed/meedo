package com.cw.msumit.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Window;
import com.cw.msumit.R;
import com.cw.msumit.objects.Actions;
import com.cw.msumit.utils.StaticFunctions;
import com.cw.msumit.views.CalendarCell;
import com.cw.msumit.views.CalendarView;
import com.cw.msumit.views.CalendarView.GrayCalendarCell;
import com.cw.msumit.views.CalendarView.OnCellTouchListener;

public class CalendarFragment extends SherlockDialogFragment {

	LinearLayout quickDateContainer;
	static TextView nineAm, twelve, six, three, noTime, customTime;
	TextView repeat, doneAndSave, noReminder;
	public static String setDay, setMonth, setYear;
	public static boolean isNoTime, isNoDate;
	static TextView currentTimeHighlighted = null;
	View.OnClickListener quickTimeClickListener;
	static Calendar universalCalendar = Calendar.getInstance();
	Calendar copyUniversalCalendar = Calendar.getInstance();
	static ArrayList<String> calendarDbData;
	CalendarView calendarView;
	CalendarCell mCell = null;
	List<CalendarCell> cellsList = new ArrayList<CalendarCell>();
	boolean[] weeklyRepeatDays;
	boolean noRepeat, dailyRepeat;
	int monthlyRepeatday;
	public static boolean dummyValue = false;
	boolean dateInCorrectForm = false, timeInCorrectForm = false;
	String date, time, norepeat, daily, weekString = "no week", monthString;
	
	public static final String REPEAT_NONE = "noRepeat";
	public static final String REPEAT_DAILY = "dailyRepeat";
	public static final String REPEAT_WEEKLY = "weeklyRepeatDays";
	public static final String REPEAT_MONTHLY = "monethlyRepeatDay";

	public static CalendarFragment newInstance(ArrayList<String> dateAndTime,
			Bundle bundle) {
		CalendarFragment calendarFragment = new CalendarFragment();
		Bundle args = new Bundle();
		// args.putInt("index", index);
		args.putStringArrayList("dateAndTime", dateAndTime);
		args.putBooleanArray(REPEAT_WEEKLY, bundle.getBooleanArray(REPEAT_WEEKLY));
		args.putInt(REPEAT_MONTHLY, bundle.getInt(REPEAT_MONTHLY));
		args.putBoolean(REPEAT_DAILY, bundle.getBoolean(REPEAT_DAILY));
		args.putBoolean(REPEAT_NONE, bundle.getBoolean(REPEAT_NONE));
		calendarFragment.setArguments(args);
		calendarDbData = dateAndTime;
		return calendarFragment;
	}


	@Override
	public void onActivityCreated(Bundle arg0) {
		// TODO Auto-generated method stub
		setRetainInstance(true);
		super.onActivityCreated(arg0);
	}


	@Override
	public void onStart() {
		super.onStart();

		if (getDialog() == null)
			return;
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

	}

	public static void setNodate(boolean bool) {
		isNoDate = bool;
	}

	public static boolean getnoDate() {
		return isNoDate;
	}

	public static String getDbYear() {
		return setYear = calendarDbData.get(2);
	}

	public static String getDbDay() {
		return setDay = calendarDbData.get(0);
	}

	public static String getDbMonth() {
		return setMonth = calendarDbData.get(1);
	}
	
	public String getDbHour() {
		return setMonth = calendarDbData.get(3);
	}
	
	public String getDbMinute() {
		return setMonth = calendarDbData.get(4);
	}
	
	protected int getInitDbDay() {
			return Integer.parseInt(getDbDay());
	}
	protected int getInitDbYear() {
			return Integer.parseInt(getDbYear());
	}
	protected int getInitDbMonth() {
			return Integer.parseInt(getDbMonth());
	}
	
	protected int getInitDbHour() {
			return Integer.parseInt(getDbHour());
	}
	protected int getInitDbMinutes() {
			return Integer.parseInt(getDbMinute());
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		universalCalendar = null;
		copyUniversalCalendar = null;
		super.onDismiss(dialog);
	}
	
	@Override
	 public void onDestroyView() {
	     if (getDialog() != null && getRetainInstance())
	         getDialog().setDismissMessage(null);
	         super.onDestroyView();
	 }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View view = inflater.inflate(R.layout.calendar_grid, null);
		getDialog().getWindow().requestFeature((int) Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		getDialog().getWindow().setBackgroundDrawableResource(
				R.drawable.dialog_full_holo_light);

		Typeface mFont = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Roboto-Condensed.ttf");
		ViewGroup root = (ViewGroup) view.findViewById(R.id.calendarLayout);
		setFont(root, mFont);
		// values from the bundle
		weeklyRepeatDays = getArguments().getBooleanArray(REPEAT_WEEKLY); 
		monthlyRepeatday = getArguments().getInt(REPEAT_MONTHLY, 0);
		noRepeat = getArguments().getBoolean(REPEAT_NONE);
		dailyRepeat = getArguments().getBoolean(REPEAT_DAILY);
		
		calendarView = (CalendarView) view
				.findViewById(R.id.customCalendarView);
		calendarView.setOnCellTouchListener(new OnCellTouchListener() {

			@Override
			public void onTouch(CalendarCell cell) {
				// TODO Auto-generated method stub
				setNodate(false);
				if (!cellsList.isEmpty()) {
					if (cell.isSelected()) {
						cell.setSelected(false);
						calendarView.invalidate(cell.getBound());
						cellsList.remove(0);
					} else {
						cellsList.get(0).setSelected(false);
						calendarView.invalidate(cellsList.get(0).getBound());
						cellsList.remove(0);
						cell.setSelected(true);
						calendarView.invalidate(cell.getBound());
						cellsList.add(0, cell);
					}

				} else {
					cellsList.add(0, cell);
					cell.setSelected(true);
					calendarView.invalidate(cell.getBound());
				}

				int year = calendarView.getYear();
				int month = calendarView.getMonth();
				int day = cell.getDayOfMonth();

				// FIX issue 6: make some correction on month and year
				if (cell instanceof GrayCalendarCell) {
					// oops, not pick current month...
					if (day < 15) {
						// pick one beginning day? then a next month day
						if (month == 11) {
							month = 0;
							year++;
						} else {
							month++;
						}

					} else {
						// otherwise, previous month
						if (month == 0) {
							month = 11;
							year--;
						} else {
							month--;
						}
					}
				}

				Calendar localCalendar = Calendar.getInstance();
				localCalendar.set(Calendar.DAY_OF_MONTH, day);
				localCalendar.set(Calendar.MONTH, month);
				localCalendar.set(Calendar.YEAR, year);
				setCalDate(localCalendar);

			}
		});

		
		nineAm = (TextView) view.findViewById(R.id.nineAm);
		twelve = (TextView) view.findViewById(R.id.twelve);
		three = (TextView) view.findViewById(R.id.three);
		six = (TextView) view.findViewById(R.id.six);
		noTime = (TextView) view.findViewById(R.id.notime);
		customTime = (TextView) view.findViewById(R.id.customTime);
		
		String timeHour = calendarDbData.get(3);
		String timeMinute = calendarDbData.get(4);
		
		selectTimeTextView(timeHour, timeMinute);
		checkIfDateIsInCorrectForm();
		universalCalendar = initUniversalCalendar();
		copyUniversalCalendar = initUniversalCalendar();
		Log.d("copyUniversalCalendar", DateUtils.formatDateTime(getActivity(), copyUniversalCalendar.getTimeInMillis(), 0));

		quickTimeClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (currentTimeHighlighted != null) {
					currentTimeHighlighted
							.setBackgroundColor(Color.TRANSPARENT);
					currentTimeHighlighted.setTextColor(Color
							.rgb(127, 127, 127));
				}
				TextView tView = (TextView) v;
				//tView.setBackgroundColor(Color.rgb(45, 188, 215));
				tView.setTextColor(Color.rgb(45, 188, 215));
				currentTimeHighlighted = tView;
				customTime.setText("Choose Custom");

				Calendar cal = Calendar.getInstance();
				if (getHour(tView) != -1) {   // -1 means not no time
					cal.set(Calendar.HOUR_OF_DAY, getHour(tView));
					cal.set(Calendar.MINUTE, 0);
					setCalTime(cal);
					isNoTime = false;
				} else {
					isNoTime = true;
				}
			}
		};

		nineAm.setOnClickListener(quickTimeClickListener);
		twelve.setOnClickListener(quickTimeClickListener);
		three.setOnClickListener(quickTimeClickListener);
		six.setOnClickListener(quickTimeClickListener);
		noTime.setOnClickListener(quickTimeClickListener);
		customTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SherlockDialogFragment newFragment = new TimePickerFragment();
				newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
			}
		});

		repeat = (TextView) view.findViewById(R.id.repeat);
		if (dailyRepeat) {
			repeat.setText("Repeat: Daily");
		}
		else if (noRepeat) {
			repeat.setText("Repeat: No Repeat");
		}
		else if (monthlyRepeatday != 0) {
			repeat.setText("Repeat: Monthly");
		} else {
			repeat.setText("Repeat: Weekly");
		}
		
		doneAndSave = (TextView) view.findViewById(R.id.doneText);
		noReminder=(TextView) view.findViewById(R.id.noReminderText);
		repeat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (dailyRepeat) {
					daily = "true";
				} else
					daily = "false";
				if (noRepeat) {
					norepeat = "true";
				} else
					norepeat = "false";
				if (monthlyRepeatday != 0) {
					monthString = "monthh";
				} else
					monthString = "no month";
				for (boolean week : weeklyRepeatDays) {
					if (week) {
						weekString = "week";
						break;
					}
				}
				
				//Log.d("hua", norepeat + " " + daily + " " + weekString + " "
					//	+ monthString);
				SherlockDialogFragment newFragment = RepeatDialogFragment.newInstance(weeklyRepeatDays, monthlyRepeatday,
								dailyRepeat, noRepeat);
				newFragment.setTargetFragment(CalendarFragment.this, 4103);
				newFragment.show(getActivity().getSupportFragmentManager(), "repeatDialog");
			}
		});
		
		doneAndSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String time;
				if(isNoTime) {
					//universalTimeString = "N:N";
					time = "N:N";
				} else {
					time = StaticFunctions.calendarIntoTimeString(universalCalendar);
				}
				String date;
				if(isNoDate) {
					date = "N-N-N";
				} else {
					date = StaticFunctions.calendarIntoDateString(universalCalendar);
				}
				String repeat = StaticFunctions.repeatInfoIntoString(noRepeat, dailyRepeat, monthlyRepeatday, weeklyRepeatDays);
				
				saveCalendarDataIntoDatabase(date, time, repeat);
				
				if (getTag().equals("TaskDetail")) {
				//send this data back to reminders detail to display instead of saving
				TaskDetailsFragment tFragment=(TaskDetailsFragment) getTargetFragment();
				//get the calendar textview
				TextView tView=(TextView) tFragment.getView().findViewById(R.id.calendarText);
				//update the view here
				Log.d("isNoDate", Boolean.toString(isNoDate));
				Log.d("isNoTime", Boolean.toString(isNoTime));
				updateTextView(tView, date, time, isNoDate, isNoTime);
				//set the calendar data to the task
				tFragment.updateTask(date, time, repeat);
				Log.d("differe", Integer.toString(copyUniversalCalendar.compareTo(universalCalendar)));
				if(copyUniversalCalendar.compareTo(universalCalendar)!=0) { // check if calendar dates have been changed
					Log.d("diff", "diif");
					int copyday = copyUniversalCalendar.get(Calendar.DAY_OF_MONTH);
					int day = universalCalendar.get(Calendar.DAY_OF_MONTH);
					int copymonth = copyUniversalCalendar.get(Calendar.MONTH);
					int month = universalCalendar.get(Calendar.MONTH);
					int copyyear = copyUniversalCalendar.get(Calendar.YEAR);
					int year = universalCalendar.get(Calendar.YEAR);
					if(copyday == day && copymonth == month && copyyear == year) { // only time has changed
						tFragment.setDateActionValues(Actions.ACTION_TYPE_TIME, copyUniversalCalendar, universalCalendar);
					} else  {
						tFragment.setDateActionValues(Actions.ACTION_TYPE_DATE, copyUniversalCalendar, universalCalendar);
					}
				}
				
				getDialog().dismiss();
				}
				
				if (getTag().equals("myReminderList")) {
					MyRemindersFragment mFragment=(MyRemindersFragment) getTargetFragment();
					//ImageButton calendarImage= (ImageButton) mFragment.getView().findViewById(R.id.calendar);
					TextView calendarTextView = (TextView) mFragment.getView().
							findViewById(R.id.calendarText);
					updateTextAndImage(calendarTextView, 
							date, time, isNoDate, isNoTime);
					mFragment.updateTask(date, time, repeat);
					getDialog().dismiss();
				}
			}
		});
		
		noReminder.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String time= "N:N";
				String date= "N-N-N";
	            String repeat="N"; 
				
				if (getTag().equals("TaskDetail")) {
				//send this data back to reminders detail to display instead of saving
				TaskDetailsFragment tFragment=(TaskDetailsFragment) getTargetFragment();
				//get the calendar textview
				TextView tView=(TextView) tFragment.getView().findViewById(R.id.calendarText);
				//update the view here
				updateTextView(tView, date, time, true, true);
				
				//set the calendar data to the task
				tFragment.updateTask(date, time, repeat);
				
				}
				
				if (getTag().equals("myReminderList")) {
					MyRemindersFragment mFragment=(MyRemindersFragment) getTargetFragment();
					
					TextView calendarTextView = (TextView) mFragment.getView().
							findViewById(R.id.calendarText);
					updateTextAndImage(calendarTextView, date, time, true, true);
					mFragment.updateTask(date, time, repeat);	
			}
				getDialog().dismiss();
			}
		});

		return view;
	}
	
	
	/**
	 * checks if date is in correct form
	 */
	private void checkIfDateIsInCorrectForm(){
		try {
			Integer.parseInt(getDbDay());
			dateInCorrectForm = true;
		} catch (NumberFormatException e) {
			// TODO: handle exception
			dateInCorrectForm = false;
		}
	}
	/**
	 * initialize the universal calendar 
	 * @return universal calendar
	 */
	private Calendar initUniversalCalendar() {
		// TODO Auto-generated method stub
		Calendar initUniversalCalendar = Calendar.getInstance();
		if(dateInCorrectForm){
			initUniversalCalendar.set(Calendar.DAY_OF_MONTH, getInitDbDay());
			initUniversalCalendar.set(Calendar.MONTH, getInitDbMonth());
			initUniversalCalendar.set(Calendar.YEAR, getInitDbYear());
		}
		if(timeInCorrectForm){
			initUniversalCalendar.set(Calendar.HOUR_OF_DAY, getInitDbHour());
			initUniversalCalendar.set(Calendar.MINUTE, getInitDbMinutes());
		} 
		return initUniversalCalendar;
	}

	//method to update calendar textView
	public void updateTextView (TextView textView, String date,
			String time, Boolean noDate, Boolean noTime) {
		String dateTime;
		if (noTime && !noDate) {
			dateTime=StaticFunctions.Date(date);
			textView.setCompoundDrawablesWithIntrinsicBounds
			(null, getResources().getDrawable(R.drawable.bell_orange), null, null);
		}
		else if (noDate) {
			dateTime="Reminder";
			textView.setCompoundDrawablesWithIntrinsicBounds
			(null, getResources().getDrawable(R.drawable.bell), null, null);
		}
		
		else {
			dateTime=StaticFunctions.DateandTimeFromData(date, time);
			textView.setCompoundDrawablesWithIntrinsicBounds
			(null, getResources().getDrawable(R.drawable.bell_orange), null, null);
		}
		textView.setText(dateTime);
		
	}
	
	//update text and imagebutton in my remnider list
	public void updateTextAndImage (TextView t, String date,
			String time, Boolean noDate, Boolean noTime) {
		String dateTime;
		if (noTime && !noDate) {
			dateTime=StaticFunctions.Date(date);
			t.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.bell_orange), null, null);
			//i.setImageDrawable(getResources().getDrawable(R.drawable.bell_orange));
		}
		else if (noDate) {
			dateTime="Reminder";
			t.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.bell), null, null);
			//i.setImageDrawable(getResources().getDrawable(R.drawable.bell));
		}
		
		else {
			dateTime=StaticFunctions.DateandTimeFromData(date, time);
			t.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.bell_orange), null, null);
			//i.setImageDrawable(getResources().getDrawable(R.drawable.bell_orange));
		}
		t.setText(dateTime);
	}
	
	/**
	 * show time
	 * @param timeHour
	 * @param timeMinute
	 */
	private void selectTimeTextView(String timeHour, String timeMinute) {
		// TODO Auto-generated method stub
		String savedTimeString;
		
		try {
			int localTimeHour = Integer.parseInt(timeHour);
			int localTimeMinute = Integer.parseInt(timeMinute);
			timeInCorrectForm = true;
			savedTimeString = Integer.toString(localTimeHour) + ":" + Integer.toString(localTimeMinute);
			if (getTimeTextView(savedTimeString) != null) {
				currentTimeHighlighted = getTimeTextView(savedTimeString);
				currentTimeHighlighted.setTextColor(Color.rgb(45, 188, 215));
				//currentTimeHighlighted.setBackgroundColor(Color.rgb(45, 188, 215));
			} else {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, localTimeHour);
				cal.set(Calendar.MINUTE, localTimeMinute);
				Date timeDate = cal.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("hh : mm aa", Locale.getDefault());
				String timeString = sdf.format(timeDate);
				customTime.setText(timeString);
				
			}
		} catch (NumberFormatException e) {
			// TODO: handle exception
			isNoTime = true;
			timeInCorrectForm = false;
			currentTimeHighlighted = noTime;
			currentTimeHighlighted.setTextColor(Color.rgb(45, 188, 215));
			//currentTimeHighlighted.setBackgroundColor(Color.rgb(45, 188, 215));
		}
		
	}

	/**
	 * Save the value of date, time and repeat into the database
	 * @param date  date String in the format 10-2-2013
	 * @param time  time string in the format 10:14
	 * @param repeat repeat string in the format W-1-2, or M-15 or D, or N
	 */
	protected void saveCalendarDataIntoDatabase(String date, String time, String repeat) {
		Log.d("caldb", time + " " + date + " " + repeat);
	}
	
	public static class TimePickerFragment extends SherlockDialogFragment
			implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, getCalHour(),
					getCalMinute(), false);
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE, minute);
			Date timeDate = cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("hh : mm aa", Locale.getDefault());
			String timeString = sdf.format(timeDate);
			isNoTime = false;
			customTime.setText(timeString);
			setCalTime(cal);

			if (currentTimeHighlighted != null) {
				currentTimeHighlighted.setTextColor(Color.rgb(127, 127, 127));
				currentTimeHighlighted.setBackgroundColor(Color.TRANSPARENT);
				if (getTimeTextView(timeString) != null
						&& getTimeTextView(timeString) != noTime) {
					getTimeTextView(timeString).setTextColor(Color.rgb(45, 188, 215));
					//getTimeTextView(timeString).setBackgroundColor(Color.rgb(45, 188, 215));
					currentTimeHighlighted = getTimeTextView(timeString);
				}

			} else {
				if (getTimeTextView(timeString) != null) {
					getTimeTextView(timeString).setTextColor(Color.rgb(45, 188, 215));
					//getTimeTextView(timeString).setBackgroundColor(Color.rgb(45, 188, 215));
					currentTimeHighlighted = getTimeTextView(timeString);
				}
			}
		}
	}

	
	/**
	 * get the textview from five tvs if matching.
	 * 
	 * @param time
	 * @return textview (one of 9 am, 12pm, 3pm, 6pm, and 9pm.
	 */
	protected static TextView getTimeTextView(String time) {
		ArrayList<TextView> listofTextViews = new ArrayList<TextView>();
		listofTextViews.add(nineAm);
		listofTextViews.add(twelve);
		listofTextViews.add(three);
		listofTextViews.add(six);
		for (int i = 0; i < listofTextViews.size(); i++) {
			//Calendar cal = Calendar.getInstance();

			//cal.set(Calendar.HOUR_OF_DAY, getHour(listofTextViews.get(i)));
			//cal.set(Calendar.MINUTE, 0);
			//Date timeDate = cal.getTime();
			//SimpleDateFormat sdf = new SimpleDateFormat("hh : mm");
			//String timeString = sdf.format(timeDate);
			String timeString = Integer.toString(getHour(listofTextViews.get(i))) + ":" + "0";
			if (timeString.equals(time)) {
				return listofTextViews.get(i);
			}

		}
		return null;
	}

	
	/**
	 * @param tv
	 *            textview
	 * @return the hour of the selected textview in the format 9,12,15,18 and 21
	 */
	static protected int getHour(TextView tv) {

		int hour = 0;

		if (tv == nineAm) {
			hour = 9;
		} else if (tv == twelve) {
			hour = 12;
		} else if (tv == three) {
			hour = 15;
		} else if (tv == six) {
			hour = 18;
		} else if (tv == noTime) {
			hour = -1;
		}

		return hour;

	}

	/**
	 * set the dates of universal calendar to the current one
	 * @param cal current calendar
	 */
	protected static void setCalDate(Calendar cal) {
		universalCalendar.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * set the time of universal calendar to the current one
	 * @param cal current calendar
	 */
	protected static void setCalTime(Calendar cal) {
		universalCalendar.set(Calendar.HOUR_OF_DAY,
				cal.get(Calendar.HOUR_OF_DAY));
		universalCalendar.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
		Log.d("cal", universalCalendar.get(Calendar.DAY_OF_MONTH) + " "
				+ universalCalendar.get(Calendar.MONTH) + " "
				+ universalCalendar.get(Calendar.YEAR) + " "
				+ universalCalendar.get(Calendar.HOUR) + " "
				+ universalCalendar.get(Calendar.MINUTE));

	}

	protected static int getCalDay() {
		return universalCalendar.get(Calendar.DAY_OF_MONTH);
	}

	protected static int getCalMonth() {
		return universalCalendar.get(Calendar.MONTH);
	}

	protected static int getCalYear() {
		return universalCalendar.get(Calendar.YEAR);
	}

	protected static int getCalHour() {
		return universalCalendar.get(Calendar.HOUR_OF_DAY);
	}

	protected static int getCalMinute() {
		return universalCalendar.get(Calendar.MINUTE);
	}

	public void setFont(ViewGroup group, Typeface font) {
		int count = group.getChildCount();
		View v;
		for (int i = 0; i < count; i++) {
			v = group.getChildAt(i);
			if (v instanceof TextView || v instanceof Button /* etc. */)
				((TextView) v).setTypeface(font);
			else if (v instanceof ViewGroup)
				setFont((ViewGroup) v, font);
		}
	}

	/**
	 * set the variable weekly repeat days
	 * 
	 * @param weeklyRDays
	 *            boolean array of size 7
	 */
	public void setWeeklyRepeatValue(boolean[] weeklyRDays) {
		// TODO Auto-generated method stub
		// on clicking of DONE in the calendarfragment,
		// save this value into db of weeklyrepeatdays from the getter
		weeklyRepeatDays = weeklyRDays;
	}

	/**
	 * set the variable monhtly repeat day
	 * 
	 * @param weeklyRDays
	 *            day from 1 to 31 int value
	 */
	public void setMonthlyRepeatValue(int monthlyRDays) {
		// TODO Auto-generated method stub
		// on clicking of DONE in the calendarfragment,
		// save this value into db of weeklyrepeatdays from the getter
		monthlyRepeatday = monthlyRDays;
	}

	public boolean[] getWeeklyRepeatValue() {
		return weeklyRepeatDays;
	}

	public int getMonthlyRepeatValue() {
		return monthlyRepeatday;
	}

	/**
	 * set the variable weekly repeat days
	 * 
	 * @param weeklyRDays
	 *            boolean array of size 7
	 */
	public void setNoRepeatValue(boolean nRepeat) {
		// TODO Auto-generated method stub
		// on clicking of DONE in the calendarfragment,
		// save this value into db of no repeat from the getter
		noRepeat = nRepeat;
	}

	/**
	 * set the variable monhtly repeat day
	 * 
	 * @param weeklyRDays
	 *            day from 1 to 31 int value
	 */
	public void setDailyRepeatValue(boolean daily) {
		// TODO Auto-generated method stub
		// on clicking of DONE in the calendarfragment,
		// save this value into db of weeklyrepeatdays from the getter
		dailyRepeat = daily;
	}

	public boolean getNoRepeatValue() {
		return noRepeat;
	}

	public boolean getDailyRepeatValue() {
		return dailyRepeat;
	}
}