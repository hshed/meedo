package com.cw.msumit.adapters;

import java.util.ArrayList;

import com.cw.msumit.R;
import com.cw.msumit.utils.StaticFunctions;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class TaskCategoryAdapter extends BaseAdapter {

	Context mContext;
	ArrayList<String> data;
	String newCategory;
	boolean isNewCategory = false;
	LayoutInflater inflater;
	Handler mHandler;
	Dialog mDialog;
	ContentValues values;

	public TaskCategoryAdapter(Context c, ArrayList<String> d, String newCategory, Handler handler,
			Dialog dialog) {
		mContext = c;
		data = d;
		this.newCategory = newCategory;
		if (this.newCategory!=null) {
			isNewCategory = true;
		}
		else {
			isNewCategory= false;
		}
		mHandler = handler;
		mDialog = dialog;
		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size() + 1;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0 - 1);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if (position == 0) {
			return 0;
		}
		return 1;
	}

	private class ViewHolder {
		protected TextView categoryName;
		protected EditText newCategoryEditText;
		protected FrameLayout newCategoryFrame;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if (getItemViewType(position) == 0) { // newList
			if (convertView == null) {
				viewHolder = new ViewHolder();
				//inflating the convert view with new category layout
				convertView = inflater.inflate(
						R.layout.list_category_first_item, null);
				
				//Instantiating the views
				viewHolder.categoryName = (TextView) convertView
						.findViewById(R.id.newCategoryName);
				viewHolder.newCategoryEditText = (EditText) convertView
						.findViewById(R.id.enterNewListName);
				if (isNewCategory)
				// means the edit text contains value

				{
					// set the visibilty of textview gone
					viewHolder.categoryName.setVisibility(View.GONE);
					viewHolder.newCategoryEditText.setVisibility(View.VISIBLE);
					viewHolder.newCategoryEditText.requestFocus();
					((InputMethodManager) mContext
							.getSystemService(Context.INPUT_METHOD_SERVICE))
							.showSoftInput(viewHolder.newCategoryEditText,
									InputMethodManager.SHOW_IMPLICIT);
					viewHolder.newCategoryEditText.setText(newCategory);
					
					//set the on edit listener
					viewHolder.newCategoryEditText.setOnEditorActionListener(new OnEditorActionListener() {
						
						@Override
						public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
							// TODO Auto-generated method stub
						if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
									|| (actionId == EditorInfo.IME_ACTION_DONE)) {
							//done clicked
							//send this data through handler and close the dialog
							if (!StaticFunctions.isWhiteSpace
									(viewHolder.newCategoryEditText.getText().toString())){
								Log.d("String is", viewHolder.newCategoryEditText.getText().toString());
										viewHolder.categoryName
												.setVisibility(View.VISIBLE);
										viewHolder.categoryName
												.setText(viewHolder.newCategoryEditText
														.getText().toString());
										viewHolder.categoryName
												.setCompoundDrawables(null,
														null, null, null);
										viewHolder.newCategoryEditText
												.setVisibility(View.GONE);

										// TODO add the new item into the
										// database of
										// list

										Message msg = Message.obtain(mHandler);
										msg.obj = viewHolder.categoryName
												.getText().toString();
										mHandler.sendMessage(msg);
										mDialog.dismiss();
							}
							
							else {
								Log.d("This shit", "Called");
								mDialog.dismiss();
							}
							
							}
							
							return false;
						}
					});
					// viewHolder.categoryName.setCompoundDrawables(null, null,
					// null, null);
				}
				
				
				
				//instantiating the framelayout
				viewHolder.newCategoryFrame = (FrameLayout) convertView
						.findViewById(R.id.enterNewListNameFrame);
				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
		
			
			if (!isNewCategory) {
				//means no value in the editText
				
			final ViewHolder tempViewHolder = viewHolder;

			tempViewHolder.newCategoryEditText.setVisibility(View.GONE);
			tempViewHolder.newCategoryFrame
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							tempViewHolder.categoryName
									.setVisibility(View.GONE);
							tempViewHolder.newCategoryEditText
									.setVisibility(View.VISIBLE);
							tempViewHolder.newCategoryEditText.requestFocus();
							((InputMethodManager) mContext
									.getSystemService(Context.INPUT_METHOD_SERVICE))
									.showSoftInput(
											tempViewHolder.newCategoryEditText,
											InputMethodManager.SHOW_IMPLICIT);
						}
					});

			tempViewHolder.newCategoryEditText
					.setOnEditorActionListener(new OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
									|| (actionId == EditorInfo.IME_ACTION_DONE)) {
								if (!StaticFunctions.isWhiteSpace
										(viewHolder.newCategoryEditText.getText().toString())){
								tempViewHolder.categoryName
										.setVisibility(View.VISIBLE);
								tempViewHolder.categoryName
										.setText(tempViewHolder.newCategoryEditText
												.getText().toString());
								tempViewHolder.categoryName
										.setCompoundDrawables(null, null, null,
												null);
								tempViewHolder.newCategoryEditText
										.setVisibility(View.GONE);

								// TODO add the new item into the database of
								// list

								Message msg = Message.obtain(mHandler);
								msg.obj = tempViewHolder.categoryName.getText()
										.toString();
								mHandler.sendMessage(msg);
								mDialog.dismiss();
								}
								else {
									Log.d("This shit", "Called");
									mDialog.dismiss();
								}

							}
							return false;
						}
					});
			}

		} else {
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.list_category_item,
						null);
				viewHolder.categoryName = (TextView) convertView
						.findViewById(R.id.categoryName);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final ViewHolder tempHolder = viewHolder;
			
			tempHolder.categoryName.setText(data.get(position - 1)); // -1
																		// because
																		// first
																		// position
																		// is
																		// for
																		// new
																		// List
																		// button
		}
		return convertView;
	}

}
