package com.cw.msumit.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.PaintDrawable;
import android.util.AttributeSet;
import android.view.View;

public class TileView extends View{
	
	Rect rect1, rect2, rect3, rect4;
	Paint paint1 = new Paint();
	Paint paint2 = new Paint();
	Paint paint3 = new Paint();
	Paint paint4 = new Paint();
	PaintDrawable paintDrawable = new PaintDrawable(Color.rgb(176, 182, 30));

	public TileView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TileView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// TODO Auto-generated constructor stub
	}

	public TileView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		paint1.setColor(Color.rgb(176, 182, 30));
		paint2.setColor(Color.rgb(247, 135, 0));
		paint3.setColor(Color.rgb(212, 46, 18));
		paint4.setColor(Color.rgb(239, 212, 61));
		canvas.drawRect(rect1, paint1);
		canvas.drawRect(rect2, paint2);
		canvas.drawRect(rect3, paint3);
		canvas.drawRect(rect4, paint4);
		
		
	}
	
	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom) {
		//Rect re = getDrawable().getBounds();
		//DisplayMetrics displayMetrics = new DisplayMetrics();
		//displayMetrics = getResources().getDisplayMetrics();
		//int width = displayMetrics.widthPixels;
		initViews();
		super.onLayout(changed, left, top, right, bottom);
	}

	private void initViews() {
		// TODO Auto-generated method stub
		rect1 = new Rect(0, 0, getWidth()/4, 4);
		rect2 = new Rect(getWidth()/4, 0, getWidth()/2, 4);
		rect3 = new Rect(getWidth()/2, 0, 3*getWidth()/4, 4);
		rect4 = new Rect(3*getWidth()/4, 0, getWidth(), 4);
		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(width, 5);
	}

	
}
