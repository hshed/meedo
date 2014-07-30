package com.cw.msumit.adapters;

import static com.nineoldandroids.view.ViewHelper.setAlpha;
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cw.msumit.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

public class UndoBarController {
    private View mBarView;
    private TextView mMessageView;
    //private ViewPropertyAnimator mBarAnimator;
    private com.nineoldandroids.view.ViewPropertyAnimator mBarAnimator;
    private Handler mHideHandler = new Handler();

    private UndoListener mUndoListener;
    private HideListener mHideListener;

    // State objects
    private Parcelable mUndoToken;
    private CharSequence mUndoMessage;
    int mWhere;

    public interface UndoListener {
        void onUndo(int where);
    }
     public interface HideListener {
    	 void onHide (int where);
     }
    

    public UndoBarController(View undoBarView, UndoListener undoListener, HideListener hideListener) {
        mBarView = undoBarView;
        
        //mBarAnimator = mBarView.animate();
        mBarAnimator = animate(mBarView);
        
        mUndoListener = undoListener;
        mHideListener= hideListener;

        mMessageView = (TextView) mBarView.findViewById(R.id.undobar_message);
        mBarView.findViewById(R.id.undobar_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideUndoBar(false);
                        //immediate is false which means it will disappear immediately
                        mUndoListener.onUndo(mWhere);
                    }
                });

        hideUndoBar(true);
       // mHideListener.onHide(mUndoToken);
    }

   

	public void showUndoBar(boolean immediate, CharSequence message, Parcelable undoToken, int where) {
        mUndoToken = undoToken;
        mBarView.bringToFront();
        mUndoMessage = message;
        Log.d("Text", mMessageView.getText().toString());
        mMessageView.setText(mUndoMessage);
        mWhere=where;
       /* if(mHideHandler.hasMessages(4103)) {
        	 mHideListener.onHide(mWhere);	
        	 Log.d("onhide", "called");
        }*/

        mHideHandler.removeCallbacks(mHideRunnable);
       
        mHideHandler.postDelayed(mHideRunnable, 3000);
        

        mBarView.setVisibility(View.VISIBLE);
        if (immediate) {
            //mBarView.setAlpha(1);
            setAlpha(mBarView, 1);
        } else {
            mBarAnimator.cancel();
            mBarAnimator
                    .alpha(1)
                    .setDuration(mBarView.getResources().getInteger(android.R.integer.config_shortAnimTime))
                    .setListener(null);
        }
    }

    public void hideUndoBar(boolean immediate) {
        mHideHandler.removeCallbacks(mHideRunnable);
        if (immediate) {
            mBarView.setVisibility(View.GONE);
            //mBarView.setAlpha(0);
            setAlpha(mBarView, 0);
            mUndoMessage = null;
            mUndoToken = null;

        } else {
            mBarAnimator.cancel();
            mBarAnimator
                    .alpha(0)
                    .setDuration(mBarView.getResources().getInteger(android.R.integer.config_shortAnimTime))
                    .setListener(new AnimatorListenerAdapter() {
                    	@Override
                        public void onAnimationEnd(Animator animation) {
                            mBarView.setVisibility(View.GONE);
                            mUndoMessage = null;
                            mUndoToken = null;
                        }
						
                    });
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence("undo_message", mUndoMessage);
        outState.putParcelable("undo_token", mUndoToken);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mUndoMessage = savedInstanceState.getCharSequence("undo_message");
            mUndoToken = savedInstanceState.getParcelable("undo_token");

            if (mUndoToken != null || !TextUtils.isEmpty(mUndoMessage)) {
                showUndoBar(true, mUndoMessage, mUndoToken, mWhere);
            }
        }
    }

    private Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hideUndoBar(false);
            mHideHandler.sendEmptyMessage(4103);
            mHideListener.onHide(mWhere);
        }
    };
}
