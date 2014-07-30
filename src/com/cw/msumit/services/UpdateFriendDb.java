package com.cw.msumit.services;

import java.util.ArrayList;
import java.util.List;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.objects.Connection;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;

public class UpdateFriendDb extends IntentService {

	public UpdateFriendDb() {
		super("UpdateFriendDb");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		Cursor cursor = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		// returns a cursor with all the rows & columns in a contact table
		while (cursor.moveToNext()) {
			String name = "";
			//String phone = "";
			String emails = "";

			String contactId = cursor.getString(cursor
					.getColumnIndex(BaseColumns._ID)); // stores
																		// the
																		// contact
																		// ID as
																		// string
			/*String hasPhone = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
			if (Integer.parseInt(hasPhone) == 1) {

				// You know it has a number so now query it like this
				Cursor phones = getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
				// returns a row that contain phone numbers of given ID
				while (phones.moveToNext()) {
					if (phone == "") {
						phone = phones
								.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					} else {
						phone = phone
								+ ", "
								+ phones.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

					}

				}
				Log.d("phone", phone);
				phones.close();
			}*/
			
			

			Cursor emailCursor = getContentResolver().query(
					ContactsContract.CommonDataKinds.Email.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = "
							+ contactId, null, null);
			if (emailCursor.getCount()>0) {
			while (emailCursor.moveToNext()) {
				// This would allow you get several email addresses
				String emailAddress = emailCursor
						.getString(emailCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				if (emails != "") {
					emails = emails + ", " + emailAddress;

				} else {
					emails = emailAddress;

				}
			}
			}
			//Log.d("emails", emails);
			emailCursor.close();

			name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); //DISPLAY_NAME_PRIMARY

			DatabaseHandler dbHandler = new DatabaseHandler(
					getApplicationContext());
			if (dbHandler.IDExists(contactId) == false && !emails.equals("") && !name.contains("@")) {
				//Log.d("Added in db", name);
				Connection connection = new Connection(contactId, name,
						emails);
				addConnectiontoDb(connection);

			}

		}
		cursor.close();
		addAutoContactToDB(getApplicationContext());

	}

	void addConnectiontoDb(Connection connection) {
		DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
		dbHandler.addConnectionFromContacts(connection);// database closed

	}

	// creates table for autosuggestions
	public static void addAutoContactToDB(Context context) {
		DatabaseHandler dbHandler = new DatabaseHandler(context);
		List<Connection> contactList = new ArrayList<Connection>();
		contactList = dbHandler.getAllConnections();
		Log.d("connection_number", Integer.toString(contactList.size()));
		for (int i = 0; i < contactList.size(); i++) {

			Connection connection = contactList.get(i);
			// iterating through emails
			String emails = connection.emails;
			//String phoneNumbers = connection.phone_numbers;
			String empty = "";
			String zero="0";
			String[] email = emails.split(",");
			if (!emails.equals(zero) && !emails.equals(empty) && !emails.equals(null)) {
				for (String string : email) {
					Integer hasWires = connection._hasWires;
					//Log.d("AutoEmail", string);
					Integer valueDecode;
					if (hasWires == 1) {
						valueDecode = 3;
					} else {
						valueDecode = 1;
					}
					String name = connection.name;
					if (!dbHandler.checkIfValuesExists(name, string,
							valueDecode)) {
						dbHandler.addAutoContact(name, string, valueDecode);
					}

				}
			}
			// iterating through phonenumbers
		/*	String[] phones = phoneNumbers.split(",");

			if (!phoneNumbers.equals(zero) && !phoneNumbers.equals(empty) && !phoneNumbers.equals(null)) {
				for (String string : phones) {
					Integer valueDecode = 2;
					Log.d("AutoPhone", string);
					String name = connection.name;
					String contactValue = string;
					if (!dbHandler.checkIfValuesExists(name, contactValue,
							valueDecode)) {
						dbHandler.addAutoContact(name, contactValue,
								valueDecode);
					}
				}
			}*/

		}
		dbHandler.close();
	}

}