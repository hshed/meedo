package com.cw.msumit.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.cw.msumit.GCMIntentService;
import com.cw.msumit.ListOfReminders;
import com.cw.msumit.R;
import com.cw.msumit.ReceivedTaskActivity;
import com.cw.msumit.adapters.SideMenuListAdapter;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.objects.People;
import com.cw.msumit.objects.Task;
import com.cw.msumit.utils.PopupMenu;
import com.cw.msumit.utils.StaticFunctions;
import com.facebook.Session;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class SideMenuFragment extends SherlockFragment {

	ListView listView;
	ArrayList<HashMap<String, Object>> data;
	ImageButton speakButton;
	EditText newListEditText;
	ImageButton newListButton;
	SideMenuListAdapter adapter;
	DatabaseHandler dbHandler;
	Cursor cursor;
	PopupMenu popupMenu;
	private ImageLoader imageLoader;
	private DisplayImageOptions displayOptions;
	private int width, height;

	@Override
	public void onStart() {
		switch (getResources().getDisplayMetrics().densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			width = 48;
			height = 48;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			width = 48;
			height = 48;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			width = 72;
			height = 72;
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			width = 96;
			height = 96;
			break;
		}
		super.onStart();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		displayOptions = new DisplayImageOptions.Builder()
			.cacheOnDisc()
			.bitmapConfig(Bitmap.Config.RGB_565)
			.showStubImage(R.drawable.anon)
			.showImageOnFail(R.drawable.anon)
			.build();
	
	    imageLoader = ImageLoader.getInstance();
		View view = inflater.inflate(R.layout.side_menu_list, container, false);
		listView = (ListView) view.findViewById(R.id.sideMenuList);
		//dbHandler = new DatabaseHandler(getActivity());
		RelativeLayout headerView = (RelativeLayout) view.findViewById(R.id.side_menu_headerview);
		RelativeLayout.LayoutParams pLayoutParams = (LayoutParams) headerView.getLayoutParams();
		TypedValue tv = new TypedValue();
		int actionBarHeight = 0;
		if (getActivity().getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
		{
		    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
		}
		pLayoutParams.height = actionBarHeight;
		final TextView nameOfUser = (TextView) view.findViewById(R.id.meedoUserName);
		if(Session.getActiveSession()!=null) {
			nameOfUser.setText(StaticFunctions.getName(getActivity()));
			String picture = "http://graph.facebook.com/" + StaticFunctions.getUserFbId(getActivity())
					+ "/picture?type=small";
			imageLoader.loadImage(picture, displayOptions, new SimpleImageLoadingListener(){ 
				@Override
			    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			        // Do whatever you want with Bitmap
					BitmapDrawable bm = new BitmapDrawable(getResources(), loadedImage);
					bm.setBounds(0, 0, width, height);
			    	nameOfUser.setCompoundDrawablesWithIntrinsicBounds(bm, null, null, null);
			    }
			});
		} else nameOfUser.setText("Sign up");
		
		ImageView options = (ImageView) view.findViewById(R.id.meedoOptions);
		options.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupMenu = new PopupMenu(getSherlockActivity(), v);
				//WindowManager.LayoutParams lp = getSherlockActivity().getWindow().getAttributes();
				//popupMenu.set
				popupMenu.getMenu().add(Menu.NONE, 0, 0, "Sync");
				popupMenu.getMenu().add(Menu.NONE, 1, 1, "Settings");
				popupMenu.show();
				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					
					@Override
					public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
						// TODO Auto-generated method stub
						switch (item.getItemId()) {
						case 0:
							break;
						case 1:
							break;
						default:
							break;
						}
						return false;
					}
				});
			}
		});		
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				// TODO Auto-generated method stub

				TextView tView = (TextView) view.findViewById(R.id.listName);
				SherlockFragment newContent = null;
				if(!tView.getText().toString().equals("Pending Tasks")) {
					newContent = MyRemindersFragment.newInstance(tView.getText()
							.toString());
					if (newContent != null) {
						switchFragment(newContent);
					}
				} else {
					ArrayList<Task> pendingTasks = dbHandler.getPendingTasks();
					ArrayList<People> pendingPeoples = dbHandler.getPendingPeoples();
					Intent receivedTaskIntent = new Intent(getActivity(), ReceivedTaskActivity.class);
					receivedTaskIntent.putExtra(GCMIntentService.RECEIVE_TASK_INTENT, pendingTasks);
					receivedTaskIntent.putExtra(GCMIntentService.RECEIVE_PEOPLE_INTENT, pendingPeoples);
					startActivity(receivedTaskIntent);
				}
				
			}

		});

		registerForContextMenu(listView); // needed for showing context menu in listview

		newListEditText = (EditText) view.findViewById(R.id.enterNewListName);
		newListButton = (ImageButton) view.findViewById(R.id.createNewList);

		newListButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// add the category to database and to the adapter
				String category = newListEditText.getText().toString();
				if (!StaticFunctions.isWhiteSpace(category)) {
					if (!dbHandler.ifCategoryExists(category)) {
						// data.add(data.size(), map1);

						dbHandler.insertCategory(category, "2");
						// adapter.notifyDataSetChanged();
						UpdateCursor updateCursor = new UpdateCursor();
						updateCursor.execute();
						listView.setSelection(cursor.getCount());
					} else {
						Toast.makeText(getActivity(),
								"This list already exists!", Toast.LENGTH_LONG)
								.show();
					}
					newListEditText.setText("");

				}

			}
		});

		speakButton = (ImageButton) view.findViewById(R.id.tts1);

		// Text to speech implementation
		PackageManager pm = getSherlockActivity().getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			speakButton.setEnabled(false);
		}

		else {
			speakButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					startSpeechToText();

				}

				private void startSpeechToText() {
					// TODO Auto-generated method stub
					Intent intent = new Intent(
							RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					startActivityForResult(intent, 1);

				}

			});
		}

		return view;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		int selectedPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
		if (selectedPosition > 10) {
			menu.add(selectedPosition, 0, 0, "Edit");
			menu.add(selectedPosition, 1, 1, "Delete");
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final int position = item.getGroupId();
		Cursor mCursor = adapter.getCursor();
		mCursor.moveToPosition(position);
		final String category = mCursor.getString(mCursor
				.getColumnIndex(DatabaseHandler.CATEGORY_NAME));
		switch (item.getItemId()) {
		case 0:
			final EditText editText = new EditText(getSherlockActivity());
			// HashMap<String, Object> map= new HashMap<String, Object>();

			// final int pos=item.getGroupId()-2; //groupId is the
			// selectedPosition
			// map=data.get(pos);
			// String category=(String) map.get("categoryName");
			// editText.setText(category); // get the name of the list here

			editText.setText(category);

			new AlertDialog.Builder(getSherlockActivity())
					.setTitle("Edit")
					.setView(editText)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// save it into db
									if (!StaticFunctions.isWhiteSpace(editText
											.getText().toString())) {
										// HashMap<String, Object> map=new
										// HashMap<String, Object>();
										// map.put("categoryName",
										// editText.getText().toString());
										// map.put("imageID",
										// R.drawable.list_sidemenu);
										dbHandler.updateCategoryOnPosition(
												editText.getText().toString(),
												position);
										// data.set(pos, map);
										// adapter.notifyDataSetChanged();
										UpdateCursor updateCursor = new UpdateCursor();
										updateCursor.execute();
									}
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.dismiss();
								}
							}).show();
			break;

		case 1:

			new AlertDialog.Builder(getSherlockActivity())
					.setTitle("Tasks in this list will be deleted. Delete?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									// your code to delete it from db
									dbHandler.deleteCategoryOnPosition(position);
									dbHandler.deleteTaskOnCategory(category);
									// data.remove(pos1);
									// adapter.notifyDataSetChanged();
									UpdateCursor updateCursor = new UpdateCursor();
									updateCursor.execute();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.dismiss();
								}
							}).show();
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("OnActivityResult", "Called");
		if (data != null) {
			if (requestCode == 1) {
				if (resultCode == Activity.RESULT_OK) {
					ArrayList<String> matches = data
							.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

					newListEditText.setText(matches.get(0).toString());
				}

			}
		}
	}

	// the meat of switching the above fragment
	private void switchFragment(SherlockFragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof ListOfReminders) {
			ListOfReminders fca = (ListOfReminders) getActivity();
			fca.switchContent(fragment);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		LoadSideMenu loadSideMenu = new LoadSideMenu(listView);
		loadSideMenu.execute();
		
		super.onResume();
	}

	private class LoadSideMenu extends AsyncTask<Void, Void, Void> {
		private ListView mListView;
		private SideMenuListAdapter mAdapter;

		public LoadSideMenu(ListView listView) {
			mListView = listView;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			dbHandler = new DatabaseHandler(getSherlockActivity());
			cursor = dbHandler.getCategoryCursor();
			mAdapter = new SideMenuListAdapter(getSherlockActivity(), cursor, 0);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			mListView.setAdapter(mAdapter);
			adapter = mAdapter;
			super.onPostExecute(result);
		}
	}
	
	private class UpdateCursor extends AsyncTask<Void, Void, Void> {

		private Cursor cursor;
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			cursor = dbHandler.getCategoryCursor();
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			adapter.changeCursor(cursor);
			super.onPostExecute(result);
		}
	}

	/**
	 * updates the cursor thus redrawing this fragment
	 */
	public void invalidate() {
		UpdateCursor updateCursor = new UpdateCursor();
		updateCursor.execute();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
}