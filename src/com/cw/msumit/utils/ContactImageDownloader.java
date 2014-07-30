package com.cw.msumit.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.cw.msumit.R;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.IoUtils;

public class ContactImageDownloader extends BaseImageDownloader{

	private static final String SCHEME_CONTACT_IMAGE = "content";
    private static final String DB_URI_PREFIX = SCHEME_CONTACT_IMAGE + "://";
    private Context mContext;

    public ContactImageDownloader(Context context) {
    	super(context);
    	mContext = context;
    }

    @Override
	protected InputStream getStreamFromContent(String imageUri, Object extra)
			throws FileNotFoundException {
    	if (imageUri.startsWith(DB_URI_PREFIX)) {
            
			InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(
							mContext.getContentResolver(), Uri.parse(imageUri));
			
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			if (input!=null) {
				Log.d("Cont input", input.toString());
				try {
					IoUtils.copyStream(input, output);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Log.d("Cont input null", "nulle");
				Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.anon);
				bitmap.compress(CompressFormat.JPEG, 100, output);
			}

			byte[] imageData = output.toByteArray();

            return new ByteArrayInputStream(imageData);
        } else {
            try {
            	Log.d("Cont input", "else wala");
				return super.getStreamFromOtherSource(imageUri, extra);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
        }
	}

	/*@Override
    protected InputStream getStreamFromOtherSource(String imageUri, Object extra) throws IOException {
        if (imageUri.startsWith(DB_URI_PREFIX)) {
            
			InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(
							mContext.getContentResolver(), Uri.parse(imageUri));
			
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			if (input!=null) {
				Log.d("Cont input", input.toString());
				IoUtils.copyStream(input, output);
			} else {
				Log.d("Cont input null", "nulle");
				Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wiresimage);
				bitmap.compress(CompressFormat.JPEG, BUFFER_SIZE, output);
			}

			byte[] imageData = output.toByteArray();
			input.close();
            return new ByteArrayInputStream(imageData);
        } else {
            return super.getStreamFromOtherSource(imageUri, extra);
        }
    }
    
    public static Uri getPhotoUriFromID(String id, Context context) {
        try {
            Cursor cur = context.getContentResolver()
                    .query(ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.CONTACT_ID
                                    + "="
                                    + id
                                    + " AND "
                                    + ContactsContract.Data.MIMETYPE
                                    + "='"
                                    + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                                    + "'", null, null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
        return Uri.withAppendedPath(person,
                ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }*/
}
