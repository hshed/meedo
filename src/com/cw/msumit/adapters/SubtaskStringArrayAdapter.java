package com.cw.msumit.adapters;

import com.cw.msumit.R;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class SubtaskStringArrayAdapter extends BaseAdapter {

	private Context context;
	private String[] data;
	private static LayoutInflater inflater = null;
	TextView subtaskTitle;
	String fontPath = "fonts/Roboto-Light.ttf";
	AssetManager assetManager;
	Typeface plain;

	public SubtaskStringArrayAdapter(Context c, String[] subtasks) {
		context = c;
		data=subtasks;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		assetManager = c.getAssets();
		plain = Typeface.createFromAsset(assetManager, fontPath);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	 public void setFont(ViewGroup group, Typeface font) {
		    int count = group.getChildCount();
		    View v;
		    for(int i = 0; i < count; i++) {
		        v = group.getChildAt(i);
		        if(v instanceof TextView || v instanceof Button /*etc.*/)
		            ((TextView)v).setTypeface(font);
		        else if(v instanceof ViewGroup)
		            setFont((ViewGroup)v, font);
		    }
		}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.subtask_layout, null);
		subtaskTitle = (TextView) vi.findViewById(R.id.subtask);
		subtaskTitle.setTypeface(plain);
		
		String subtask = data[position];
		subtaskTitle.setText(subtask);
		//Typeface mFont = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
		//ViewGroup root = (ViewGroup) vi.findViewById(R.id.subtaskRoot);
		
		return vi;
	}
	
}