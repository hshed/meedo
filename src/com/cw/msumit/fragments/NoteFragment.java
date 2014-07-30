package com.cw.msumit.fragments;

import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Window;
import com.cw.msumit.R;
import com.cw.msumit.databases.DatabaseHandler;
import com.cw.msumit.utils.StaticFunctions;

public class NoteFragment extends SherlockDialogFragment implements OnEditorActionListener{

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		 if (reminderID.equals("newtask")) {
		MyRemindersFragment mFragment= (MyRemindersFragment) getTargetFragment();
		mFragment.setNoteString(mEditText.getText().toString());
		
		//set the view in listreminder
		TextView noteTextView = (TextView) mFragment.getView().findViewById(R.id.note);
		if (!StaticFunctions.isWhiteSpace(mEditText.getText().toString())) {
			noteTextView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.note_orange), null, null);
		}
		else {
			noteTextView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.note), null, null);
		}
		 }
		super.onDismiss(dialog);
	}

	LinearLayout noteLayout;
	AssetManager assetManager;
	Typeface condensed, regular;
	String fontCondensed = "fonts/Roboto-Regular.ttf",
			fontRegular = "fonts/Roboto-Regular.ttf";
	String reminderID, note, originalNote;
    public interface NoteListener {
        void onFinishNoteDialog(String inputText);
    }

    private EditText mEditText;
    private TextView OkText, cancelText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note, container);
        reminderID=getTag();
        if (!reminderID.equals("newtask")) {
        //get the note
        note=StaticFunctions.getNote(reminderID, getActivity());
        }
        else {
			//get the note from the listreminder fragment
        	MyRemindersFragment mFragment= (MyRemindersFragment) getTargetFragment();
        	note=mFragment.getNoteString();
		}
        
        //find views
        mEditText = (EditText) view.findViewById(R.id.editNote);
        OkText=(TextView) view.findViewById(R.id.NoteOK);
        cancelText=(TextView) view.findViewById(R.id.NoteCancel);
        //onclickListeners
        mEditText.setText(note);
        originalNote = note;
        OkText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Save the note data and dismiss the dialog and change the note image
			  if (!reminderID.equals("newtask")) {
				DatabaseHandler handler=new DatabaseHandler(getActivity());
				handler.updateAColumn(reminderID, "note", mEditText.getText().toString());
				TextView noteView= (TextView) getTargetFragment().getView().
						findViewById(R.id.note);
				if (StaticFunctions.isWhiteSpace(mEditText.getText().toString())) {
					noteView.setCompoundDrawablesWithIntrinsicBounds
					(null, getResources().getDrawable(R.drawable.note), null, null);
				}
				else {
					noteView.setCompoundDrawablesWithIntrinsicBounds
					(null, getResources().getDrawable(R.drawable.note_orange), null, null);
					}
				 }
				if(!originalNote.equals(mEditText.getText().toString())) {
					((TaskDetailsFragment) getTargetFragment()).setNoteActionValues();
				}
				getDialog().dismiss();
			}
		});
        
		cancelText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Just dismiss without changing anything
				getDialog().dismiss();
			}
		});
        
        //windows
        getDialog().getWindow().requestFeature((int) Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(STYLE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_full_holo_light);
        
        // Show soft keyboard automatically
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        noteLayout = (LinearLayout) view.findViewById(R.id.noteLayout);
        
        //getDialog().getWindow().setLayout(500, 500);
        mEditText.setOnEditorActionListener(this);
        TextView noteTextView = (TextView) view.findViewById(R.id.noteText);
		ViewGroup okCancelGroup = (ViewGroup) view
				.findViewById(R.id.okCancelGroup);

		assetManager = getSherlockActivity().getAssets();
		condensed = Typeface.createFromAsset(assetManager, fontCondensed);
		regular = Typeface.createFromAsset(assetManager, fontRegular);
		noteTextView.setTypeface(condensed);
		View v;
		for (int i = 0; i < okCancelGroup.getChildCount(); i++) {
			v = okCancelGroup.getChildAt(i);
			if (v instanceof TextView)
				((TextView) v).setTypeface(regular);

		}
		noteTextView.setTypeface(condensed);


        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            NoteListener activity = (NoteListener) getSherlockActivity();
            activity.onFinishNoteDialog(mEditText.getText().toString());
            this.dismiss();
            return true;
        }
        return false;
    }
    
    
}