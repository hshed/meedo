package com.cw.msumit.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class LocationAutoCompleteTextView extends AutoCompleteTextView{

	public LocationAutoCompleteTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public LocationAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public LocationAutoCompleteTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	private ProgressBar mLoadingIndicator;
	private ImageView searchView;

	public void setLoadingIndicator(ProgressBar view, ImageView search) {
		mLoadingIndicator = view;
		searchView = search;
	}

	@Override
	protected void performFiltering(CharSequence text, int keyCode) {
		// the AutoCompleteTextview is about to start the filtering so show
		// the ProgressPager
		mLoadingIndicator.setVisibility(View.VISIBLE);
		searchView.setVisibility(View.INVISIBLE);
		super.performFiltering(text, keyCode);
	}

	@Override
	public void onFilterComplete(int count) {
		// the AutoCompleteTextView has done its job and it's about to show
		// the drop down so close/hide the ProgreeBar
		mLoadingIndicator.setVisibility(View.INVISIBLE);
		searchView.setVisibility(View.VISIBLE);
		super.onFilterComplete(count);
	}
}
