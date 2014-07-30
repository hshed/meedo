package com.cw.msumit.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cw.msumit.R;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.utils.StaticFunctions;

public class SideMenuListAdapter extends CursorAdapter {

	Context mContext;
	LayoutInflater inflater;
	String fontPath = "fonts/Roboto-Regular.ttf";
	Typeface plain;
	int ITEM_SECTION = 0, ITEM_REGULAR = 1;
	AssetManager assetManager;

	public SideMenuListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		// TODO Auto-generated constructor stub
		mContext = context;
		assetManager = context.getAssets();
		plain = Typeface.createFromAsset(assetManager, fontPath);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return getCursor().getCount();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return super.getItemId(position);
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if (position == 0) {
			return ITEM_SECTION;
		} else if (position > 0 && position <= 6) {
			return ITEM_REGULAR;
		} else if (position == 7) {
			return ITEM_SECTION;
		} else {
			return ITEM_REGULAR;
		}
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		if (position == 0 || position == 7)
			return false;
		return true;
	}

	private class ViewHolder {
		protected TextView itemName, count;
		protected ImageView itemImage;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		int position = cursor.getPosition();
		String categoryName = cursor.getString(cursor
				.getColumnIndex(DatabaseHandler.CATEGORY_NAME));

		if (getItemViewType(position) == ITEM_REGULAR) {
			int imageID = getImageResourceId(position);
			viewHolder.itemName.setText(categoryName);
			viewHolder.itemName.setTypeface(plain);
			String co = Integer.toString(StaticFunctions.getCountofCategory(
					categoryName, mContext));
			viewHolder.count.setText(co);
			// to be take from db
			viewHolder.count.setTypeface(plain);
			viewHolder.itemImage.setImageResource(imageID);
		} else {
			viewHolder.itemName.setText(categoryName);
		}
	}

	public int getImageResourceId(int position) {
		int id;
		switch (position) {
		case 1:
			id = R.drawable.active_task;
			break;
		case 2:
			id = R.drawable.today;
			break;
		case 3:
			id = R.drawable.assigned;
			break;
		case 4:
			id = R.drawable.star_sidemenu;
			break;
		case 5:
			id = R.drawable.tick_sidemenu;
			break;
		case 7:
			id = R.drawable.personal;
			break;
		case 8:
			id = R.drawable.home;
			break;
		case 9:
			id = R.drawable.work;
			break;
		case 10:
			id = R.drawable.shopping;
			break;

		default:
			id = R.drawable.list_sidemenu;
			break;
		}
		return id;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = new ViewHolder();;
		View convertView = null;
		int position = cursor.getPosition();
		if (getItemViewType(position) == ITEM_REGULAR) {
			convertView = inflater.inflate(R.layout.side_menu_list_item, null);
			viewHolder.itemName = (TextView) convertView
					.findViewById(R.id.listName);
			viewHolder.itemName.setTypeface(plain);
			viewHolder.count = (TextView) convertView
					.findViewById(R.id.listReminderCount);
			viewHolder.count.setTypeface(plain);
			viewHolder.itemImage = (ImageView) convertView
					.findViewById(R.id.listItemIcon);
			convertView.setTag(viewHolder);
		} else {

			convertView = inflater.inflate(R.layout.side_menu_list_separator,null);
			viewHolder.itemName = (TextView) convertView
					.findViewById(R.id.seperatorText);
			viewHolder.itemName.setTypeface(plain);
			convertView.setTag(viewHolder);
		}

		return convertView;
	}

}