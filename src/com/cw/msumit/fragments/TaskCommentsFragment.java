package com.cw.msumit.fragments;

import java.util.Calendar;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Window;
import com.cw.msumit.R;
import com.cw.msumit.adapters.TaskCommentsAdapter;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.objects.Actions;
import com.cw.msumit.objects.Task;
import com.cw.msumit.services.SendActionIntentService;
import com.cw.msumit.utils.StaticFunctions;

public class TaskCommentsFragment extends SherlockDialogFragment{

	Cursor cursor;
	DatabaseHandler dbHandler;
	Task task;
	TaskCommentsAdapter taskCommentsAdapter;
	
	public static TaskCommentsFragment newInstance(Task task) {
		TaskCommentsFragment f= new TaskCommentsFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("task", task);
		f.setArguments(bundle);
		return f;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (getDialog() == null)
			return;
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.comments_listview, null);
		ListView mListView = (ListView) view.findViewById(R.id.taskCommentsListView);
		getDialog().getWindow().requestFeature((int) Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_full_holo_light);
		
		mListView.setEmptyView(view.findViewById(R.id.empty_list_view));
		LoadCommentsDialog loadCommentsDialog =  new LoadCommentsDialog(mListView);
		loadCommentsDialog.execute();
		getDialog().getWindow().setLayout(
				LayoutParams.MATCH_PARENT, mListView.getHeight());
		
		this.task = (Task) getArguments().getSerializable("task");
		final EditText addCommentEditText = (EditText) view.findViewById(R.id.addCommentEditText);
		final ImageButton addCommentButton = (ImageButton) view.findViewById(R.id.addCommentButton);
		addCommentButton.setEnabled(false);
		addCommentEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(!s.toString().trim().equals("")) {
					addCommentButton.setEnabled(true);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		addCommentButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(StaticFunctions.getUserFbId(getActivity()).equals("NeedMeedoSignIn")) {
					Toast.makeText(getActivity(), "Please sign in", Toast.LENGTH_LONG).show();
				} else {
					Actions action = new Actions("1", task.getUniversalID(), StaticFunctions.getUserFbId(getActivity()),StaticFunctions.getName(getActivity()),
							Actions.ACTION_TYPE_COMMENT, addCommentEditText.getText().toString(), 0, Calendar.getInstance().getTimeInMillis());
					action.setUniqueId(Actions.generateUniqueId(getActivity()));
					dbHandler.addActions(action);
					UpdateCursor updateCursor = new UpdateCursor();
					updateCursor.execute();
					// code to send the comment.
					sendActionToWeb(action);
				}
				addCommentEditText.setText("");
			}
		});
		
		return view;
	}
	
	protected void sendActionToWeb(Actions action) {
		Intent sendActionIntent = new Intent(getActivity(), SendActionIntentService.class);
		sendActionIntent.putExtra("action", action);
		getActivity().startService(sendActionIntent);
		
	}

	private class LoadCommentsDialog extends AsyncTask<Void, Void, Void> {
		
		private ListView mListView;
		private TaskCommentsAdapter mAdapter;
		
		public LoadCommentsDialog(ListView listView) {
			mListView = listView;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			dbHandler = new DatabaseHandler(getSherlockActivity());
			cursor = dbHandler.getActionCursor("task_id", task.getUniversalID());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mAdapter = new TaskCommentsAdapter(getSherlockActivity(), cursor, 0);
			mListView.setAdapter(mAdapter);
			taskCommentsAdapter = mAdapter;
			super.onPostExecute(result);
		}
	}
	
	private class UpdateCursor extends AsyncTask<Void, Void, Void> {

		private Cursor cursor;
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			cursor = dbHandler.getActionCursor("task_id", task.getUniversalID());
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			taskCommentsAdapter.changeCursor(cursor);
			super.onPostExecute(result);
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		super.onDismiss(dialog);
	}
}
