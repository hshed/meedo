package com.cw.msumit.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Style;

public class CalendarCell {
	//private static final String TAG = "Cell";
	private boolean mToday, mSelected, isInNextMonth = false, isInPrevMonth = false, 
			isInCurrentMonth = true;
	protected Rect mBound = null;
	protected int mDayOfMonth = 1;	// from 1 to 31
	protected Paint mPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
            |Paint.ANTI_ALIAS_FLAG);
	protected Paint mSelectedPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
            |Paint.ANTI_ALIAS_FLAG);
	protected Paint mTodayPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
            |Paint.ANTI_ALIAS_FLAG);
	protected Paint mDbPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
            |Paint.ANTI_ALIAS_FLAG);
	protected boolean dbday = false;
	protected Paint mSelectedBackgroundPaint = new Paint(), mTodayBackgroundPaint = new Paint();
	int dx, dy, dz;
	
	
	public CalendarCell(int dayOfMon, Rect rect, float textSize, boolean bold, boolean today, Typeface typeface) {
		
		mDayOfMonth = dayOfMon;
		mBound = rect;
		mSelected = false;
		mPaint.setTextSize(textSize);
		mPaint.setColor(Color.rgb(100, 100, 100));
		mPaint.setTypeface(typeface);
		mSelectedPaint.setTextSize(textSize/*26f*/);
		mSelectedPaint.setColor(Color.WHITE);
		mSelectedPaint.setTypeface(typeface);
		mSelectedBackgroundPaint.setColor(Color.rgb(45, 188, 215));
		mTodayPaint.setTextSize(textSize/*26f*/);
		mTodayPaint.setColor(Color.WHITE);
		mTodayPaint.setTypeface(typeface);
		mTodayBackgroundPaint.setColor(Color.argb(200, 180, 180, 180));
		if(bold) mPaint.setFakeBoldText(true);
		
		mToday = today;
		
		dx = (int) mPaint.measureText(String.valueOf(mDayOfMonth)) / 2;
		dy = (int) (-mPaint.ascent() + mPaint.descent()) / 2;
		dz = (int) mPaint.measureText(getDayOfWeek(1)) / 2;
	}
	
	public CalendarCell(int dayOfMon, Rect rect, float textSize, boolean today, Typeface typeface) {
		this(dayOfMon, rect, textSize, false, today, typeface);
	}
	
	
	protected void draw(Canvas canvas) {
		if(isSelected()){
			canvas.drawRect(mBound.left + 1.0F, mBound.top + dy/3 , mBound.right - 1.0F , mBound.bottom -dy/3 , mSelectedBackgroundPaint);
			canvas.drawText(String.valueOf(mDayOfMonth), mBound.centerX() - dx, mBound.centerY() + dy/2, mSelectedPaint);
		}
		else if(mToday && !isSelected()){
			canvas.drawRect(mBound.left + 1.0F , mBound.top + dy/3 , mBound.right - 1.0F , mBound.bottom -dy/3 , mTodayBackgroundPaint);
			canvas.drawText(String.valueOf(mDayOfMonth), mBound.centerX() - dx, mBound.centerY() + dy/2, mTodayPaint);
		}
		else {
			canvas.drawText(String.valueOf(mDayOfMonth), mBound.centerX() - dx, mBound.centerY() + dy/2, mPaint);
			if(dbday){
				mDbPaint.setStyle(Style.STROKE);
				mDbPaint.setColor(Color.rgb(45, 188, 215));
				mDbPaint.setStrokeWidth(0);
				canvas.drawRect(mBound.left + 1.0F , mBound.top + dy/3 , mBound.right - 1.0F , mBound.bottom -dy/3 , mDbPaint);
			}
			
		}
		
	}
	
	protected void drawDay(Canvas canvas, int day) {
			canvas.drawText(getDayOfWeek(day), mBound.centerX() - 2*dx , mBound.centerY() , mPaint);
	}
	
	private String getDayOfWeek(int day) {
		// TODO Auto-generated method stub
		String[] weekDayName = new String[] { "SU" ,"MO", "TU", "WE", "TH", "FR", "SA"};
		return weekDayName[day];
	}

	public int getDayOfMonth() {
		return mDayOfMonth;
	}
	
	public boolean hitTest(int x, int y) {
		return mBound.contains(x, y); 
	}
	public boolean hitTest(int left, int top, int right, int bottom ) {
		return mBound.contains(left, top, right, bottom); 
	}
	
	public Rect getBound() {
		return mBound;
	}
	public boolean isSelected() {
		return mSelected;
	}
	public void setSelected(boolean today) {
		mSelected = today;
	}
	
	@Override
	public String toString() {
		return String.valueOf(mDayOfMonth)+"("+mBound.toString()+")";
	}

	public boolean isInCurrentMonth() {
		return isInCurrentMonth;
	}
	public boolean isInNextMonth() {
		return isInNextMonth;
	}
	public boolean isInPrevMonth() {
		return isInPrevMonth;
	}
	
	public void setInCurrentMonth(boolean isInCurrentMonth) {
		// TODO Auto-generated method stub
		this.isInCurrentMonth = isInCurrentMonth;
	}
	public void setInNextMonth(boolean isInNextMonth) {
		// TODO Auto-generated method stub
		this.isInNextMonth = isInNextMonth;
	}
	public void setInPrevMonth(boolean isInPrevMonth) {
		// TODO Auto-generated method stub
		this.isInPrevMonth = isInPrevMonth;
	}
	
}

