package com.cw.msumit.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.cw.msumit.R;
import com.cw.msumit.adapters.SubtaskAdapter;
import com.cw.msumit.adapters.SubtaskAdapter.Checkable;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.utils.StaticFunctions;

public class SubtaskFragment extends SherlockFragment implements Checkable {
	
	ArrayList<HashMap<String, String>> Subtasks;
	String reminderID;
	int Case;
	private static String SUBTASK_HASH_KEY = "Subtask";
	private static String CASE = "case";
	private static String REMINDER_ID= "reminderID";
	 SubtaskAdapter adapter;
	 ArrayList<Boolean> isChecked;
	public static SubtaskFragment newInstance(ArrayList<HashMap<String, String>> subtasks, int Case, String ReminderID){
		SubtaskFragment f = new SubtaskFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(SUBTASK_HASH_KEY, subtasks);
		bundle.putInt(CASE, Case);
		bundle.putString(REMINDER_ID, ReminderID);
		f.setArguments(bundle);
		return f;
	}
	
	public interface OnSubtaskExitListener{
		/**
		 * called from ondestroy of subtask fragment
		 * @param subtasks updated subtask array list
		 */
		void onExit(ArrayList<HashMap<String, String>> subtasks, ArrayList<Boolean> checked);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setRetainInstance(true);
		super.onActivityCreated(savedInstanceState);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setHasOptionsMenu(true);
		Subtasks = (ArrayList<HashMap<String, String>>) getArguments().getSerializable(SUBTASK_HASH_KEY);
		Case = getArguments().getInt(CASE);
		reminderID=getArguments().getString(REMINDER_ID);
		//get arraylist of boolean values
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.subtask, null);
		final EditText subtaskEditText = (EditText) view.findViewById(R.id.subtaskEditBox);
		subtaskEditText.requestFocus();
		ImageButton addsubTaskButton = (ImageButton) view.findViewById(R.id.addSubTask);
		
		final ListView subtaskListView = (ListView) view.findViewById(R.id.subTaskList);
		//this value comes from database
		isChecked=new ArrayList<Boolean>();
		if (Case !=2) {
		for (int i =0; i<Subtasks.size(); i++) {
			isChecked.add(i, false);
		}
		}
		else {
			//if it is equal to 2
			Log.d("ReminderID", reminderID);
			DatabaseHandler dbHandler= new DatabaseHandler(getActivity());
			isChecked=dbHandler.getSubtaskCheckedBooleans(reminderID);
		}
		adapter = new SubtaskAdapter(getSherlockActivity(), Subtasks, isChecked, reminderID, this);
		subtaskListView.setAdapter(adapter);

		addsubTaskButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String subtaskString = subtaskEditText.getText().toString();
				if (!StaticFunctions.isWhiteSpace(subtaskString)){
					
				HashMap<String, String> map = new HashMap<String, String>();

				// adding each child node to HashMap key =&gt; value
				map.put("Subtask", subtaskString);
				if (!Subtasks.contains(map)) {
				Subtasks.add(Subtasks.size(), map);
				isChecked.add(false);
				adapter.notifyDataSetChanged();
				subtaskEditText.setText("");
				subtaskListView.setSelection(Subtasks.size());
				}
				else {
					subtaskEditText.setText("");
					Toast.makeText(getActivity(), "This subtask already exists", Toast.LENGTH_LONG).show();
				}
				}
			}
		});

		subtaskListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				getSherlockActivity().startActionMode(new SubTaskActionMode(arg1, arg2, adapter));

			}
		});
		
		return view;
	}
	
	public final class SubTaskActionMode implements ActionMode.Callback {

		private final int newPosition;
		//private SubtaskAdapter adapter;

		public SubTaskActionMode(View view, int position, SubtaskAdapter subtaskAdapter) {
			newPosition = position;
			adapter = subtaskAdapter;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Used to put dark icons on light action bar

			menu.add("Delete").setIcon(R.drawable.discard)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add("Edit").setIcon(R.drawable.edit)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			if (item.getTitle() == "Delete") {
				Subtasks.remove(newPosition);
				adapter.changeCheckList(newPosition);
				adapter.notifyDataSetChanged();
				
			}
			if (item.getTitle() == "Edit") {
				showEditDialog(newPosition);
				adapter.notifyDataSetChanged();
			}
			mode.finish();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
		}
		
		
	}
	
	 
		private void showEditDialog(int Position) {
			FragmentManager fm = getSherlockActivity().getSupportFragmentManager();
			EditSubtaskDialogFragment editNameDialog = new EditSubtaskDialogFragment();
			editNameDialog.setTargetFragment(SubtaskFragment.this, 4103);
			HashMap<String, String> map=new HashMap<String, String>();
			map=Subtasks.get(Position);
			String stask=map.get("Subtask") + "," + Position; //eg apple,1
			editNameDialog.show(fm, stask);
		}
		
		


	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
		super.onPause();
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

	    case android.R.id.home:
	    	Log.d("tagger", "it isc a");
			callExit();
	        return super.onOptionsItemSelected(item);
	        

	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}


	/**
	 * update the array list with this new subtask text at the respective position 
	 * @param subtaskText
	 */
	protected void updateSubtask(int Position, String newValue) {
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("Subtask", newValue);
		Subtasks.set(Position, map);
		adapter.notifyDataSetChanged();
	}
	
	protected void callExit () {
		OnSubtaskExitListener onSubtaskExitListener = (OnSubtaskExitListener) getSherlockActivity();
		onSubtaskExitListener.onExit(Subtasks, isChecked);
		getSherlockActivity().finish();
	}

	@Override
	public void onComplete(int where, Boolean what) {
		// TODO Auto-generated method stub
		isChecked.set(where, what);
	}
	
}
