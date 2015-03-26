package com.pretolesi.easydomotic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.Switch;

/**
 * Created by RPRETOLESI on 17/03/2015.
 */
public class LightSwitch extends Switch implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    private static final String TAG = "LightSwitch";
    private GestureDetectorCompat mDetector;

    LightSwitchData m_lsd;

    private float mLastTouchX;
    private float mLastTouchY;

    public LightSwitch(Context context) {
        super(context);
        this.m_lsd = null;

        addGestureListener();
    }
    public LightSwitch(Context context, LightSwitchData lsd) {
        super(context);
        if(lsd != null) {
            this.m_lsd = lsd;
            this.setTag(lsd.getTag());
        }

        addGestureListener();
    }
    public LightSwitchData getLightSwitchData() {
        return m_lsd;
    }


/*
    public LightSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LightSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LightSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Let the ScaleGestureDetector inspect all events.
        this.mDetector.onTouchEvent(event);

        // Be sure to call the superclass implementation
        final int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if(m_lsd != null) {
                    m_lsd.setSaved(false);
                }

                if(this.getLayoutParams() instanceof RelativeLayout.LayoutParams){
                    RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams)this.getLayoutParams();
                    mLastTouchX = event.getRawX() - rllp.leftMargin;
                    mLastTouchY = event.getRawY() - rllp.topMargin;
                }

                Log.d(TAG, this.toString() + ": " + "onTouchEvent: ACTION_DOWN mLastTouchX/mLastTouchY: " + mLastTouchX + "/" + mLastTouchY);

                break;
            }

            case MotionEvent.ACTION_MOVE: {

                final float x = event.getRawX();
                final float y = event.getRawY();
                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                if(m_lsd != null && !m_lsd.getSelected()) {
                    BaseFragment.setViewPosition(this,(int)dx,(int)dy);
                    m_lsd.setPosX(dx);
                    m_lsd.setPosY(dy);
                }

                Log.d(TAG, this.toString() + ": " + "onTouchEvent: ACTION_MOVE dx/dy: " + dx + "/" + dy + ", mLastTouchX/mLastTouchY: " + mLastTouchX + "/" + mLastTouchY + ", x/y: " + x + "/" + y);

                break;
            }

            case MotionEvent.ACTION_UP: {
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                break;
            }
        }
        return true;
//        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if(m_lsd != null) {
            m_lsd.setSelected(false);
            this.setChecked(false);
        }
        return true;
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
    public boolean onDown(MotionEvent event) {
        return false;
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
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if(m_lsd != null) {
            m_lsd.setSelected(true);
            this.setChecked(true);
        }
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        return false;
    }

    private void addGestureListener(){
        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(getContext(),this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);
    }

}
