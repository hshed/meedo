package com.cw.msumit.views;

import java.util.Calendar;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.MonthDisplayHelper;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.cw.msumit.R;
import com.cw.msumit.fragments.CalendarFragment;

public class CalendarView extends View {
	private int CELL_WIDTH;
	private int CELL_HEIGH;
	private float CELL_TEXT_SIZE;
	private float density;

	private int leftArrowHeight;
	private int leftArrowWidth;
	private int leftArrowX = 0;
	private int rightArrowHeight;
	private int rightArrowWidth;
	private int rightArrowX = 0;
	private Calendar mRightNow = null;
	private CalendarCell[][] mCells = new CalendarCell[6][7];
	private OnCellTouchListener mOnCellTouchListener = null;
	private CalendarCell[][] dayCells = new CalendarCell[1][7];
	MonthDisplayHelper mHelper;
	Drawable mDecoration = null;
	RelativeLayout.LayoutParams params;

	String fontPath = "fonts/Roboto-Condensed.ttf";
	AssetManager assetManager;
	Typeface plain;
	Context mContext;
	int downXValue, downYValue;
	Paint monthPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
			| Paint.ANTI_ALIAS_FLAG);
	Paint monthCenterAlignLargePaint;
	Rect MONTH, PREVMONTH, NEXTMONTH;
	Calendar calendar = Calendar.getInstance();
	Bitmap prev, next;
	int dx,dy;
	public static int setDay, setMonth, setYear = 2012;
	private int leftArrowY;
	private int rightArrowY;
	public static CalendarCell taskDbCell;
	private boolean dateInCorrectForm = false;

	public interface OnCellTouchListener {
		public void onTouch(CalendarCell cell);
	}
	
	public CalendarView(Context context) {
		this(context, null);

	}

	public CalendarView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// mDecoration = context.getResources().getDrawable(
		// R.drawable.calendar_today);
		Display localDisplay = ((WindowManager) context
				.getSystemService("window")).getDefaultDisplay();
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		localDisplay.getMetrics(localDisplayMetrics);
		this.density = localDisplayMetrics.density;
		mContext = context;
		assetManager = mContext.getAssets();
		this.monthCenterAlignLargePaint = new Paint();
	    this.monthCenterAlignLargePaint.setAntiAlias(true);
	    this.monthCenterAlignLargePaint.setColor(Color.rgb(45, 188, 215));
	    this.monthCenterAlignLargePaint.setTextAlign(Paint.Align.CENTER);
	    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
	    	this.monthCenterAlignLargePaint.setTextSize(14.0F * this.density);
	    } else this.monthCenterAlignLargePaint.setTextSize(22.0F * this.density);
		plain = Typeface.createFromAsset(assetManager, fontPath);
		prev = ((BitmapDrawable)getResources().getDrawable(R.drawable.btn_radio_on_holo_light)).getBitmap();
		next = ((BitmapDrawable)getResources().getDrawable(R.drawable.btn_radio_on_holo_light)).getBitmap();

		//initCalendarView(setCalendar(CalendarFragment.setDay, CalendarFragment.setMonth, CalendarFragment.setYear));
		initCalendarView(setCalendar());
	}

	
	public Calendar setCalendar() {
		Calendar localCalendar;
		try {
			localCalendar = Calendar.getInstance();
			localCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(CalendarFragment.getDbDay()));
			localCalendar.set(Calendar.MONTH, Integer.parseInt(CalendarFragment.getDbMonth()));
			localCalendar.set(Calendar.YEAR, Integer.parseInt(CalendarFragment.getDbYear()));
			dateInCorrectForm = true;
		} catch (NumberFormatException e) {
			// TODO: handle exception			
			localCalendar = Calendar.getInstance();
			CalendarFragment.setNodate(true);
		}
		return localCalendar;
	}
	
	private void initCalendarView(Calendar setCalendar) {
		//mRightNow = Calendar.getInstance();
		mRightNow = setCalendar;
		//getResources();

		// CELL_WIDTH = 20;
		DisplayMetrics displayMetrics = new DisplayMetrics();
		displayMetrics = getResources().getDisplayMetrics();
		int width = displayMetrics.widthPixels;
		int height = displayMetrics.heightPixels;
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			CELL_WIDTH = height/9;
		} else	CELL_WIDTH = width / 8;
		CELL_HEIGH = (3 * CELL_WIDTH / 4);
		CELL_TEXT_SIZE = 0.40F * CELL_WIDTH;
		mHelper = new MonthDisplayHelper(mRightNow.get(Calendar.YEAR),
				mRightNow.get(Calendar.MONTH));

		monthPaint.setTextSize(2*CELL_HEIGH/3);
		monthPaint.setColor(Color.TRANSPARENT);
		monthPaint.setTypeface(plain);
		dx = (int) monthPaint.measureText(getMonthString()) / 2;
		dy = (int) (-monthPaint.ascent() + monthPaint.descent()) / 2;
		
		PREVMONTH = new Rect(0, dy, CELL_WIDTH, 2*dy);
		NEXTMONTH = new Rect(7*CELL_WIDTH - this.rightArrowX, CELL_HEIGH/3, 7*CELL_WIDTH - this.rightArrowWidth, this.rightArrowHeight);
	}

	protected int getDbDay() {
		if(dateInCorrectForm)
			return Integer.parseInt(CalendarFragment.getDbDay());
		return 32;
	}
	protected int getDbYear() {
		if(dateInCorrectForm)
			return Integer.parseInt(CalendarFragment.getDbYear());
		return 123456;
	}
	protected int getDbMonth() {
		if(dateInCorrectForm)
			return Integer.parseInt(CalendarFragment.getDbMonth());
		return 123456;
	}
	private void initCells() {
		class _calendar {
			public int day;
			public boolean thisMonth;
			public boolean nextMonth;

			// public boolean prevMonth;

			public _calendar(int d, boolean thisMonth, boolean nextMonth) {
				day = d;
				this.thisMonth = thisMonth;
				this.nextMonth = nextMonth;
				// this.prevMonth = prevMonth;
			}

		}
		;
		_calendar tmp[][] = new _calendar[6][7];

		for (int i = 0; i < tmp.length; i++) {
			int n[] = mHelper.getDigitsForRow(i);
			for (int d = 0; d < n.length; d++) {
				if (mHelper.isWithinCurrentMonth(i, d))
					tmp[i][d] = new _calendar(n[d], true, false);
				else {
					if (mHelper.getRowOf(n[d]) >= 4) { // means it is in
														// previous month
						tmp[i][d] = new _calendar(n[d], false, false);
					} else { // means it is in next month
						tmp[i][d] = new _calendar(n[d], false, true);
					}
				}
			}
		}

		Calendar today = Calendar.getInstance();
		int thisDay = 0;
		// mTodayCell = null;
		if (mHelper.getYear() == today.get(Calendar.YEAR)
				&& mHelper.getMonth() == today.get(Calendar.MONTH)) {
			thisDay = today.get(Calendar.DAY_OF_MONTH);
		}
		// build cells
		
		this.leftArrowHeight = (prev.getHeight());
	    this.leftArrowWidth = (prev.getWidth());
	    this.leftArrowX = 5;
	    this.rightArrowHeight = (next.getHeight());
	    this.rightArrowWidth = (next.getWidth());
	    this.rightArrowX = 5;
	    
		//MONTH = new Rect(3*CELL_WIDTH, CELL_HEIGH/3, 4*CELL_WIDTH, CELL_HEIGH);
	    
		Rect DAY = new Rect(0, CELL_HEIGH, CELL_WIDTH,5* CELL_HEIGH/2);
		Rect Bound = new Rect(0, 4*CELL_HEIGH/2, CELL_WIDTH, 3*CELL_HEIGH);

		for (int dayName = 0; dayName < dayCells[0].length; dayName++) {
			dayCells[0][dayName] = new CalendarCell(dayName, new Rect(DAY),
					CELL_TEXT_SIZE, false, plain);
			DAY.offset(CELL_WIDTH, 0);
		}

		for (int week = 0; week < mCells.length; week++) {
			for (int day = 0; day < mCells[week].length; day++) {
				if (tmp[week][day].thisMonth) {
					if (tmp[week][day].day == thisDay
							&& tmp[week][day].thisMonth) {
						mCells[week][day] = new CalendarCell(
								tmp[week][day].day, new Rect(Bound),
								CELL_TEXT_SIZE, true, plain);
						

					} else if (tmp[week][day].day == getDbDay() && getDbMonth() == mHelper.getMonth() 
							&& getDbYear()==mHelper.getYear()){
							mCells[week][day] = new StrokeCalendarCell(
									tmp[week][day].day, new Rect(Bound),
									CELL_TEXT_SIZE, false, plain);
						}
					else {
						mCells[week][day] = new CalendarCell(
								tmp[week][day].day, new Rect(Bound),
								CELL_TEXT_SIZE, false, plain);
						
					}
						
				} else {
					mCells[week][day] = new GrayCalendarCell(
							tmp[week][day].day, new Rect(Bound),
							CELL_TEXT_SIZE, false, plain);
					if (tmp[week][day].nextMonth) {
						mCells[week][day].setInNextMonth(true);
					} else {
						mCells[week][day].setInPrevMonth(true);
					}
					mCells[week][day].setInCurrentMonth(false);
					
				}

				Bound.offset(CELL_WIDTH, 0); // move to next column

				// get today

			}
			Bound.offset(CELL_HEIGH / 2, CELL_HEIGH); // move to next row and
														// first column
			Bound.left = 0;
			Bound.right = CELL_WIDTH;
		}
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
			result = CELL_HEIGH * 8;
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	public void setTimeInMillis(long milliseconds) {
		mRightNow.setTimeInMillis(milliseconds);
		initCells();
		this.invalidate();
	}

	public int getYear() {
		return mHelper.getYear();
	}
	public int getMonth() {
		return mHelper.getMonth();
	}

	public String getYearString() {
		return Integer.toString(mHelper.getYear());
	}
	
	public String getMonthString() {
		String[] monthName = new String[] { "January", "February", "March",
				"April", "May", "June", "July", "August", "September", "October",
				"November", "December" };
		return monthName[mHelper.getMonth()] + ", " + Integer.toString(mHelper.getYear());
	}

	public void nextMonth() {
		mHelper.nextMonth();
		initCells();
		invalidate();
	}

	public void previousMonth() {
		mHelper.previousMonth();
		initCells();
		invalidate();
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

			if(hitLeftArrow(downXValue, downYValue)){
				previousMonth();
			}
			else if(hitRightArrow(downXValue, downYValue)) {
				nextMonth();
			}
			return true;
		}
		case MotionEvent.ACTION_UP: {
			
			if (mOnCellTouchListener != null) {
				for (CalendarCell[] week : mCells) {
					for (CalendarCell day : week) {
						if (day.hitTest(downXValue, downYValue)) {
							if (day.isInCurrentMonth()) {
								mOnCellTouchListener.onTouch(day);
							} else {
								if (day.isInNextMonth()) {
									nextMonth();
								} else {
									previousMonth();
								}
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

		

		//canvas.drawText(getMonth(), MONTH. ,
			//	MONTH.top, monthPaint);
		
		
		//canvas.drawBitmap(prev, null, PREVMONTH, null);
		//canvas.drawBitmap(next, null, NEXTMONTH, null);
		
		
		canvas.drawRect(0.0F, 0.0F, getMeasuredWidth(), CELL_HEIGH, monthPaint);
	    RectF localRectF = new RectF();
	    float f1 = 32.0F * this.density;
	    localRectF.set(15.0F, 15.0F, -15 + getMeasuredWidth(), f1);
	    canvas.drawRoundRect(localRectF, 0.0F, 0.0F, monthPaint);
	    localRectF.set(16.0F, 16.0F, -16 + getMeasuredWidth(), f1 - 1.0F);
	    Bitmap localBitmap1 = ((BitmapDrawable)getResources().getDrawable(R.drawable.prev)).getBitmap();
	    this.leftArrowHeight = (int)(localBitmap1.getHeight()  / 1.0F);
	    this.leftArrowWidth = (int)(localBitmap1.getWidth()  / 1.0F);
	    this.leftArrowX = (int)(2.0F * this.density + 2.0F + localBitmap1.getWidth());
	    this.leftArrowY = (4 + (int)(f1 / 2.0F - this.leftArrowHeight / 2));
	    canvas.drawBitmap(localBitmap1, new Rect(0, 0, localBitmap1.getWidth(), localBitmap1.getHeight()), new Rect(this.leftArrowX, this.leftArrowY, this.leftArrowX + this.leftArrowWidth, this.leftArrowY + this.leftArrowHeight), null);
	    Bitmap localBitmap2 = ((BitmapDrawable)getResources().getDrawable(R.drawable.next)).getBitmap();
	    this.rightArrowHeight = (int)(localBitmap2.getHeight()  / 1.0F);
	    this.rightArrowWidth = (int)(localBitmap2.getWidth()  / 1.0F);
	    this.rightArrowX = (int)(getMeasuredWidth() - 2.0F * this.density - 10.0F - localBitmap2.getWidth());
	    this.rightArrowY = (4 + (int)(f1 / 2.0F - this.rightArrowHeight / 2));
	    canvas.drawBitmap(localBitmap2, new Rect(0, 0, localBitmap2.getWidth(), localBitmap2.getHeight()), new Rect(this.rightArrowX, this.rightArrowY, this.rightArrowX + this.rightArrowWidth, this.rightArrowY + this.rightArrowHeight), null);
	    
	    int k = getMeasuredWidth() / 2;
	    int j = (int)(7.5F + f1 / 2.0F);
	   
	    	canvas.drawText(getMonthString(), k, j, this.monthCenterAlignLargePaint);
		
	    
		// draw mon tue

		for (int i = 0; i < dayCells[0].length; i++) {
			// draw today
			dayCells[0][i].drawDay(canvas, i);
		}

		// draw cells
		for (CalendarCell[] week : mCells) {
			
			for (CalendarCell day : week) {
				// draw today
				Log.d("maatha", day.toString());
				day.draw(canvas);
			}
		}

	}
	
	protected boolean hitLeftArrow(int x, int y) {
		Rect leftRect = new Rect(this.leftArrowX - 2, this.leftArrowY -2, this.leftArrowX + this.leftArrowWidth + 2, this.leftArrowY + this.leftArrowHeight);
		if(leftRect.contains(x, y)){
			return true;
		}
		return false;
		
	}
	
	protected boolean hitRightArrow(int x, int y) {
		Rect rightRect = new Rect(this.rightArrowX - 2, this.rightArrowY - 2, this.rightArrowX + this.rightArrowWidth + 2, this.rightArrowY + this.rightArrowHeight);
		if(rightRect.contains(x, y)){
			return true;
		}
		return false;
		
	}

	public class GrayCalendarCell extends CalendarCell {
		public GrayCalendarCell(int dayOfMon, Rect rect, float s,
				boolean today, Typeface typeface) {
			super(dayOfMon, rect, s, false, typeface);

			mPaint.setColor(Color.LTGRAY);
		}
	}
	
	public class StrokeCalendarCell extends CalendarCell {
		public StrokeCalendarCell(int dayOfMon, Rect rect, float s,
				boolean today, Typeface typeface) {
			super(dayOfMon, rect, s, false, typeface);
			dbday = true;
		}
	}

	
	
}
