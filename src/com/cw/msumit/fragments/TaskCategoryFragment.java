package com.cw.msumit.fragments;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Window;
import com.cw.msumit.R;
import com.cw.msumit.adapters.TaskCategoryAdapter;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.utils.StaticFunctions;

public class TaskCategoryFragment extends SherlockDialogFragment {

	ListView mListView;
	ArrayList<String> objects = new ArrayList<String>();// these objects have to retrieved from database
    static ArrayList<String> newCategoriesList = new ArrayList<String>(); 
    static String newCategory = null;   //CAUTION: Set it to null after saving to database
	TaskCategoryAdapter adapter;
	
	String itemSelected ; // this should be initialized with a value
										// from the database
	static String newitemSelected;
	Handler mHandler;

	public TaskCategoryFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_category, container);
		mListView = (ListView) view.findViewById(R.id.categoryList);
		getDialog().getWindow().requestFeature((int) Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(
				R.drawable.dialog_full_holo_light);
		// Show soft keyboard automatically
		// getDialog().getWindow().setSoftInputMode(
		// LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		DatabaseHandler handler = new DatabaseHandler(getActivity());
		String[] categories = handler.categoryArrayonValue("2");
		String[] categs = new String[categories.length-1];
		for(int i=0; i<categories.length; i++) {
			if(categories[i].equals("LISTS"))
				continue;
			else categs[i-1] = categories[i];
		}
		objects = StaticFunctions.stringArraytoArrayList(categs);
		TextView CategoryText = (TextView) getTargetFragment().getView()
				.findViewById(R.id.category);
		itemSelected = CategoryText.getText().toString();
		newitemSelected = itemSelected;
		/**
		 * adapter = new ArrayAdapter<String>(getSherlockActivity(),
		 * R.layout.list_category_item, R.id.categoryName, objects);
		 **/
		adapter = new TaskCategoryAdapter(getSherlockActivity(), objects, newCategory,
				new AdapterHandler(), getDialog());
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				if (position != 0) {
					newitemSelected = adapter.getItem(position).toString();
					getDialog().dismiss();
				}
			}
		});

		getDialog().getWindow().setLayout(
				LayoutParams.WRAP_CONTENT, mListView.getHeight());

		return view;
	}

	// define the interface

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		// Message msg = Message.obtain(mHandler);
		// msg.obj = newitemSelected;
		// mHandler.sendMessage(msg);

		TextView listCategory = (TextView) getTargetFragment().getView()
				.findViewById(R.id.category);
		listCategory.setText(newitemSelected);
		
		super.onDismiss(dialog);

	}

	private static class AdapterHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			newitemSelected = (String) msg.obj;
			//newCategoriesList.add(0, newitemSelected);
			newCategory = newitemSelected;
		}
	}

}
