package com.pretolesi.easydomotic.BaseValue;


import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pretolesi.easydomotic.BaseFragment;
import com.pretolesi.easydomotic.CustomControls.NumericEditText;
import com.pretolesi.easydomotic.SensorValue.SensorValuePropActivity;

/**
 *
 */
public class BaseValue extends TextView implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private NumericEditText.DataType m_dtDataType;

    private GestureDetectorCompat mDetector;
    private float m_LastTouchX;
    private float m_LastTouchY;

    private Handler m_TimerHandler;
    private long m_lRepeatingTime;

    private boolean m_bEditMode;
    private NumericEditText m_edEditText;

    public BaseValue(Context context) {
        super(context);
        m_bEditMode = false;
        m_dtDataType = NumericEditText.DataType.SHORT16;
    }

    protected void setEditMode(boolean bEditMode){
        m_bEditMode = bEditMode;
    }

    protected void setNumericDataType(NumericEditText.DataType dtDataType){
        m_dtDataType = dtDataType;
    }

    protected boolean getEditMode(){
        return m_bEditMode;
    }

    protected NumericEditText.DataType getNumericDataType(){
        return m_dtDataType;
    }

    /*
     * Begin
     * Timer variable and function
     */
    protected void setTimer(long lRepeatingTime) {
        m_lRepeatingTime = lRepeatingTime;
        m_TimerHandler = new Handler();
        m_TimerHandler.postDelayed(m_TimerRunnable, lRepeatingTime);
    }

    protected void resetTimer() {
        if(m_TimerHandler != null){
            m_TimerHandler.removeCallbacks(m_TimerRunnable);
        }
    }

    private Runnable m_TimerRunnable = new Runnable() {
        @Override
        public void run() {
            if(m_TimerHandler != null) {
                onTimer();

                m_TimerHandler.postDelayed(m_TimerRunnable, m_lRepeatingTime);
            }
        }
    };

    protected void onTimer() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (m_bEditMode) {
            if (mDetector == null) {
                // Instantiate the gesture detector with the
                // application context and an implementation of
                // GestureDetector.OnGestureListener
                mDetector = new GestureDetectorCompat(getContext(), this);
                // Set the gesture detector as the double tap
                // listener.
                mDetector.setOnDoubleTapListener(this);
            }
            final int action = MotionEventCompat.getActionMasked(event);

            switch (action) {

                case MotionEvent.ACTION_DOWN: {
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    onTouchActionUp(m_bEditMode);
/*
                    if(m_svd != null) {
                        m_svd.setSaved(false);
                        Intent intent = SensorValuePropActivity.makeSensorValuePropActivity(this.getContext(), m_svd);
                        this.getContext().startActivity(intent);
                    }
*/
                    break;
                }
            }

            this.mDetector.onTouchEvent(event);

            return true;
        } else {
            final int action = MotionEventCompat.getActionMasked(event);

            switch (action) {
                case MotionEvent.ACTION_DOWN: {

                    return true;

                }
                case MotionEvent.ACTION_UP: {
                    onTouchActionUp(m_bEditMode);

/*
                    if(m_svd.getSensorEnableSimulation()){
                        openWriteInput();
                    }
*/
                    break;
                }
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (this.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams) this.getLayoutParams();
            m_LastTouchX = e.getRawX() - rllp.leftMargin;
            m_LastTouchY = e.getRawY() - rllp.topMargin;
        }

        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        final float x = e2.getRawX();
        final float y = e2.getRawY();
        // Calculate the distance moved
        final float dx = x - m_LastTouchX;
        final float dy = y - m_LastTouchY;

        BaseFragment.setViewPosition(this, (int) dx, (int) dy);

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    protected void onTouchActionUp(boolean bEditMode) {

    }

    protected void openInputField() {
        if(m_edEditText == null){
            // Create....
            m_edEditText = new NumericEditText(getContext());
            // Set Input Limit
            m_edEditText.setSingleLine();
            if(m_dtDataType != null) {
                switch (m_dtDataType) {
                    case SHORT16:
                        m_edEditText.setInputLimit(Short.MIN_VALUE, Short.MAX_VALUE);
                        break;
                    case INT32:
                        m_edEditText.setInputLimit(Integer.MIN_VALUE, Integer.MAX_VALUE);
                        break;
                    case LONG64:
                        m_edEditText.setInputLimit(Long.MIN_VALUE, Long.MAX_VALUE);
                        break;
                    case FLOAT32:
                        m_edEditText.setInputLimit(-Float.MAX_VALUE, Float.MAX_VALUE);
                        break;
                    case DOUBLE64:
                        m_edEditText.setInputLimit(-Double.MAX_VALUE, Double.MAX_VALUE);
                        break;
                }
            }
            // Visualizzo valore corrente
            m_edEditText.setHint(this.getText());
            // Creo i parametri per il layout
            ViewParent view = this.getParent();
            if(view != null && view instanceof RelativeLayout){
                RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams)this.getLayoutParams();
                m_edEditText.setLayoutParams(rllp);
                ((RelativeLayout) view).addView(m_edEditText);
                m_edEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null){
                    imm.showSoftInput(m_edEditText, InputMethodManager.SHOW_IMPLICIT);
                }
                this.setVisibility(GONE);

                // Set Listener
                m_edEditText.setOnKeyListener(new OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                            if (m_edEditText != null) {
                                m_edEditText.clearFocus();
                            }
                            return true;
                        }

                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                            if (m_edEditText != null) {
                                if (m_edEditText.validateInputLimit()) {
                                    OnWriteInputField(m_edEditText.getText().toString());
                                }
                            }
                            return true;
                        }

                        return false;
                    }
                });

                // Set Listener
                m_edEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            removeInputField();
                        }
                    }
                });
            }
        }
    }

    private synchronized void removeInputField() {
        if(m_edEditText != null){
            ViewParent view = m_edEditText.getParent();
            if (view instanceof RelativeLayout) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (m_edEditText.getWindowToken() != null) {
                    if(imm != null) {
                        imm.hideSoftInputFromWindow(m_edEditText.getWindowToken(), 0);
                    }
                    ((RelativeLayout) view).removeView(m_edEditText);
                }
            }
        }
        m_edEditText = null;

        this.setVisibility(VISIBLE);
    }

    protected synchronized void closeInputField(){
        if (m_edEditText != null) {
            m_edEditText.clearFocus();
        }
    }

    protected synchronized void setErrorInputField(boolean bError){
        if (m_edEditText != null) {
            if(bError){
                m_edEditText.setError("");
            } else {
                m_edEditText.setError(null);
            }
        }
    }

    protected synchronized void OnWriteInputField(String strValue) {

    }
}