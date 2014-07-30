package com.cw.msumit.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cw.msumit.R;

public class SubtaskAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	TextView subtaskTitle;
	String fontPath = "fonts/Roboto-Regular.ttf";
	AssetManager assetManager;
	Typeface typeface;
	ArrayList<Boolean> checked;
	String reminderID;
	private Checkable mCheckable;
	
	public interface Checkable {
        void onComplete(int where, Boolean what);
    }

	public SubtaskAdapter(Context c, ArrayList<HashMap<String, String>> d, ArrayList<Boolean> isChecked
			, String ReminderID, Checkable ch) {
		context = c;
		data = d;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		assetManager = c.getAssets();
		typeface = Typeface.createFromAsset(assetManager, fontPath);
		checked = isChecked;
		reminderID=ReminderID;
		mCheckable=ch;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(data!=null){
		return data.size();
		}
		else {
			return 0;
		}
	}
	


	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(data!=null){
		return data.get(position);
		}
		
		else{
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	private class ViewHolder{
		private CheckBox checkBox;
		private TextView subtaskText;
		private RelativeLayout root;
	}
	 
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		final ViewHolder viewHolder;
		if (convertView==null) {
			convertView = inflater.inflate(R.layout.subtask_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.subtaskCheck);
			viewHolder.subtaskText = (TextView) convertView.findViewById(R.id.subtask);
			
			viewHolder.checkBox.setTag(position);
			viewHolder.root = (RelativeLayout) convertView.findViewById(R.id.subtaskRoot);
			
			convertView.setTag(viewHolder);
		} else{
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.checkBox.setTag(position);
		}

		Log.d("pos", Integer.toString(position));
		
		viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				checked.set((Integer) buttonView.getTag(), isChecked);
				if(isChecked){
					//if there exists a reminderID save the same in the database
					if (!reminderID.equals("0")) {
						Log.d("This", "Called");
						mCheckable.onComplete(position, true);
					/*DatabaseHandler handler= new DatabaseHandler(context);
					handler.updateSubtaskComplete(viewHolder.subtaskText.getText().toString(), reminderID, 1);*/
					}
					viewHolder.subtaskText.setPaintFlags(viewHolder.subtaskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					viewHolder.subtaskText.setTextColor(Color.parseColor("#9C9C9C"));
				} else {
					if (!reminderID.equals("0")) {
						mCheckable.onComplete(position, false);
						/*DatabaseHandler handler= new DatabaseHandler(context);
						handler.updateSubtaskComplete(viewHolder.subtaskText.getText().toString(), reminderID, 0);*/
						}
					viewHolder.subtaskText.setPaintFlags(viewHolder.subtaskText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
					viewHolder.subtaskText.setTextColor(Color.parseColor("#5d6061"));
				}
			}
		});
		
		
		viewHolder.checkBox.setChecked(checked.get(position));
		
		viewHolder.subtaskText.setTypeface(typeface);
		if (data!=null) {
			viewHolder.subtaskText.setText(data.get(position).get("Subtask"));
		}
		if(checked.get(position) == true){
			viewHolder.subtaskText.setPaintFlags(viewHolder.subtaskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			viewHolder.subtaskText.setTextColor(Color.parseColor("#9C9C9C"));
		} else {
			viewHolder.subtaskText.setPaintFlags(viewHolder.subtaskText.getPaintFlags() &(~ Paint.STRIKE_THRU_TEXT_FLAG));
			viewHolder.subtaskText.setTextColor(Color.parseColor("#5d6061"));
		}
		
		viewHolder.root.post(new Runnable() {
            @Override
			public void run() {
                // Post in the parent's message queue to make sure the parent
                // lays out its children before we call getHitRect()
                Rect delegateArea = new Rect();
                CheckBox delegate = viewHolder.checkBox;
                delegate.getHitRect(delegateArea);
                delegateArea.top -= 50;
                delegateArea.bottom += 50;
                delegateArea.left -= 50;
                delegateArea.right += 10;
                TouchDelegate expandedArea = new TouchDelegate(delegateArea,
                        delegate);
                // give the delegate to an ancestor of the view we're
                // delegating the
                // area to
                if (View.class.isInstance(delegate.getParent())) {
                    ((View) delegate.getParent())
                            .setTouchDelegate(expandedArea);
                }
            };
        });
		
		return convertView;
	}


	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}


	public void changeCheckList(int newPosition) {
		// TODO Auto-generated method stub
		checked.remove(newPosition);
	}


}