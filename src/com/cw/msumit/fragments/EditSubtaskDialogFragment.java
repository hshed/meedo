package com.cw.msumit.fragments;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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

public class EditSubtaskDialogFragment extends SherlockDialogFragment implements OnEditorActionListener {

	LinearLayout editsubtaskLayout;
	AssetManager assetManager;
	Typeface condensed, regular;
	String fontCondensed = "fonts/Roboto-Regular.ttf",
			fontRegular = "fonts/Roboto-Regular.ttf";
	int Position;

    private EditText mEditText;

    public EditSubtaskDialogFragment() {
        // Empty constructor required for DialogFragment
    }
    @Override
	public void onStart() {
		super.onStart();

		if (getDialog() == null)
			return;
		getDialog().getWindow().setLayout(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

	}
    
    @Override
	public void onActivityCreated(Bundle arg0) {
		// TODO Auto-generated method stub
    	setRetainInstance(true);
		super.onActivityCreated(arg0);
	}
	@Override
	 public void onDestroyView() {
	     if (getDialog() != null && getRetainInstance())
	         getDialog().setDismissMessage(null);
	         super.onDestroyView();
	 }

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	String subtaskAndPositon=getTag();
    	String stask=subtaskAndPositon.split(",")[0];
    	Position=Integer.parseInt(subtaskAndPositon.split(",")[1]);
        View view = inflater.inflate(R.layout.subtask_edit, container);
        mEditText = (EditText) view.findViewById(R.id.edit_subtask);
        mEditText.setText(stask);
        getDialog().getWindow().requestFeature((int) Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(STYLE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_full_holo_light);

        // Show soft keyboard automatically
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        
        //getDialog().getWindow().setLayout(500, 500);
        mEditText.setOnEditorActionListener(this);
        
        TextView doneEditSubtask = (TextView) view.findViewById(R.id.doneEditSubtask);
        TextView cancelEditSubtask = (TextView) view.findViewById(R.id.cancelEditSubtask);
        doneEditSubtask.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SubtaskFragment fragment = (SubtaskFragment) getTargetFragment();
	            fragment.updateSubtask(Position, mEditText.getText().toString());
	            getDialog().dismiss();
			}
		});
        cancelEditSubtask.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getDialog().dismiss();
			}
		});
        
		TextView subtasktextview = (TextView) view.findViewById(R.id.editSubtaskText);
		LinearLayout okCancelGroup = (LinearLayout) view.findViewById(R.id.okCancelGroup);
		assetManager = getSherlockActivity().getAssets();
		condensed = Typeface.createFromAsset(assetManager, fontCondensed);
		regular = Typeface.createFromAsset(assetManager, fontRegular);
		subtasktextview.setTypeface(condensed);
		View v;
		for (int i = 0; i < okCancelGroup.getChildCount(); i++) {
			v = okCancelGroup.getChildAt(i);
			if (v instanceof TextView)
				((TextView) v).setTypeface(regular);

		}
		subtasktextview.setTypeface(condensed);
		
        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
        	SubtaskFragment fragment = (SubtaskFragment) getTargetFragment();
            fragment.updateSubtask(Position, mEditText.getText().toString());
            this.dismiss();
            return true;
        }
        return false;
    }
    
}
