package com.cw.msumit.views;

import java.util.Calendar;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.MonthDisplayHelper;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class MonthlyRepeatView extends View {
	private int CELL_WIDTH;
	private int CELL_HEIGH;
	private float CELL_TEXT_SIZE;
	private Calendar mRightNow = null;
	private MonthlyRepeatCell[][] mCells = new MonthlyRepeatCell[5][7];
	private OnCellTouchListener mOnCellTouchListener = null;
	MonthDisplayHelper mHelper;
	Drawable mDecoration = null;
	RelativeLayout.LayoutParams params;

	String fontPath = "fonts/Roboto-Condensed.ttf";
	AssetManager assetManager;
	Typeface plain;
	Context mContext;
	Rect MONTH, PREVMONTH, NEXTMONTH;
	Calendar calendar = Calendar.getInstance();
	Bitmap prev, next;
	int dx,dy;
	int downXValue;
	int downYValue;
	public static int setDay, setMonth, setYear = 2012;
	public static MonthlyRepeatCell taskDbCell;
	private static int monthlyRepeatDay;

	public interface OnCellTouchListener {
		public void onTouch(MonthlyRepeatCell cell);
	}
	
	
	public MonthlyRepeatView(Context context) {
		this(context, null);

	}

	public MonthlyRepeatView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MonthlyRepeatView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// mDecoration = context.getResources().getDrawable(
		// R.drawable.calendar_today);
		Display localDisplay = ((WindowManager) context
				.getSystemService("window")).getDefaultDisplay();
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		localDisplay.getMetrics(localDisplayMetrics);
		mContext = context;
		assetManager = mContext.getAssets();
		plain = Typeface.createFromAsset(assetManager, fontPath);
		
		initCalendarView();
	}

	
	
	private void initCalendarView() {
		//mRightNow = Calendar.getInstance();
		//getResources();

		// CELL_WIDTH = 20;
		DisplayMetrics displayMetrics = new DisplayMetrics();
		displayMetrics = getResources().getDisplayMetrics();
		int width = displayMetrics.widthPixels;
		int height = displayMetrics.heightPixels;
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			CELL_WIDTH = height/10;
		} else	CELL_WIDTH = width / 9;
		CELL_HEIGH = (CELL_WIDTH);
		CELL_TEXT_SIZE = 0.50F * CELL_WIDTH;
	
	}

	
	
	private void initCells() {
		class _calendar {
			public int day;

			public _calendar(int d) {
				day = d;
				// this.prevMonth = prevMonth;
			}

		}
		;
		_calendar tmp[][] = new _calendar[5][7];

		int[][] weekDayName = new int[][] {{ 1 ,2, 3, 4, 5, 6, 7},
				{ 8 ,9, 10, 11, 12, 13, 14},
				{ 15 ,16, 17, 18, 19, 20, 21},
				{ 22 ,23, 24, 25, 26, 27, 28},
				{ 29 ,30, 31, 0, 0, 0, 0}};
		for (int i = 0; i < tmp.length; i++) {
			int n[] = weekDayName[i];
			for (int d = 0; d < n.length; d++) {
				
					tmp[i][d] = new _calendar(n[d]);
				
			}
		}

		Rect Bound = new Rect(0, 0, CELL_WIDTH, CELL_HEIGH);
		for (int week = 0; week < mCells.length; week++) {
			for (int day = 0; day < mCells[week].length; day++) {
				if(tmp[week][day].day ==  getMonthlyRepeatDay()){
					mCells[week][day] = new MonthlyRepeatCell(
						tmp[week][day].day, new Rect(Bound),
						CELL_TEXT_SIZE, true, plain);
					} else {
						mCells[week][day] = new MonthlyRepeatCell(
								tmp[week][day].day, new Rect(Bound),
								CELL_TEXT_SIZE, false, plain);
					}
				if(tmp[week][day].day ==0) {
					mCells[week][day].setInCurrentMonth(false);
				}
				Bound.offset(CELL_WIDTH, 0); // move to next column

			}
			Bound.offset(CELL_HEIGH / 2, CELL_HEIGH); // move to next row and
														// first column
			Bound.left = 0;
			Bound.right = CELL_WIDTH;
		}
	}

	// kisi kaam ka code nahi hai ye :(
	public MonthlyRepeatCell getMonthlyRepeatCell(int day) {
		int top = day/7;
		int left = (day-(top*7) - 1)*CELL_WIDTH;
		int right = left + CELL_WIDTH;
		int bottom = top + CELL_HEIGH;
		//Rect monthRect = new Rect(left, top, right, bottom);
		MonthlyRepeatCell[][] monthCells = new MonthlyRepeatCell[5][7];
		
		Rect monthRect = new Rect(0, 0, CELL_WIDTH, CELL_HEIGH);
		for (int week = 0; week < mCells.length; week++) {
			for (int i = 0; i < mCells[week].length; i++) {
					monthCells[week][i] = new MonthlyRepeatCell(
						day, new Rect(monthRect),
						CELL_TEXT_SIZE, false, plain);
				
				monthRect.offset(CELL_WIDTH, 0); // move to next column

			}
			monthRect.offset(CELL_HEIGH / 2, CELL_HEIGH); // move to next row and
														// first column
			monthRect.left = 0;
			monthRect.right = CELL_WIDTH;
		}
		
		for (MonthlyRepeatCell[] week : monthCells) {
			for (MonthlyRepeatCell dayCell : week) {
				if (dayCell.hitTest(left, top, right, bottom)) {
					return dayCell;
				}
			}
		}
		return null;
		
	}
	
	@Override
	public void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// Rect re = getDrawable().getBounds();

		initCells();
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			// result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
			// + getPaddingRight();
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				result = CELL_WIDTH*7;
			} else result = CELL_WIDTH * 7;
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}

		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// mAscent = (int) mTextPaint.ascent();
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			// result = (int) (-mAscent + mTextPaint.descent()) +
			// getPaddingTop()
			// + getPaddingBottom();
			result = CELL_HEIGH * 5;
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}



	public boolean firstDay(int day) {
		return day == 1;
	}

	public boolean lastDay(int day) {
		return mHelper.getNumberOfDaysInMonth() == day;
	}

	public void goToday() {
		Calendar cal = Calendar.getInstance();
		mHelper = new MonthDisplayHelper(cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH));
		initCells();
		invalidate();
	}

	public Calendar getDate() {
		return mRightNow;
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			downXValue = (int) event.getX();
			downYValue = (int) event.getY();

			return true;
		}
		case MotionEvent.ACTION_UP: {
			
			if (mOnCellTouchListener != null) {
				for (MonthlyRepeatCell[] week : mCells) {
					for (MonthlyRepeatCell day : week) {
						if (day.hitTest(downXValue, downYValue)) {
							if (day.isInCurrentMonth()) {
								mOnCellTouchListener.onTouch(day);
							}
						}
					}
				}
			}
			return true;
		}

		}
		return false;
	}

	public void setOnCellTouchListener(OnCellTouchListener p) {
		mOnCellTouchListener = p;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// draw background
		super.onDraw(canvas);

		
		// draw cells
		for (MonthlyRepeatCell[] week : mCells) {
			for (MonthlyRepeatCell day : week) {
				// draw today
				day.draw(canvas);
			}
		}

	}

	public static void setMonthlyRepeatDay(int day) {
		// TODO Auto-generated method stub
		monthlyRepeatDay = day;
	}
	public static int getMonthlyRepeatDay() {
		// TODO Auto-generated method stub
		return monthlyRepeatDay;
	}	

	
}
