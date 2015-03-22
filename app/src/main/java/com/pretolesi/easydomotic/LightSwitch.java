package com.pretolesi.easydomotic;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Switch;

/**
 * Created by RPRETOLESI on 17/03/2015.
 */
public class LightSwitch extends Switch {

    private static final String TAG = "LightSwitch";

    // Room TAG
    private String m_strRoomTAG;

    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;

    private float mLastTouchX;
    private float mLastTouchY;
    private float m_PosX;
    private float m_PosY;
    private float m_PosZ;

    public LightSwitch(Context context, String strRoomTAG, String strTAG) {
        super(context);
        this.m_strRoomTAG = strRoomTAG;
        this.setTag(strTAG);
    }
    public LightSwitch(Context context, LightSwitchData lsd) {
        super(context);
        this.m_strRoomTAG = lsd.getRoomTAG();
        this.setTag(lsd.getTAG());
        this.m_PosX = lsd.getPosX();
        this.m_PosY = lsd.getPosY();
//        this.m_PosZ = lsd.getPosZ();
    }

    public LightSwitchData getData() {
        return new LightSwitchData(m_strRoomTAG, (String)getTag(), m_PosX, m_PosY, m_PosZ);

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
        //mScaleDetector.onTouchEvent(ev);

        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float y = MotionEventCompat.getY(event, pointerIndex);

                // Remember where we started (for dragging)
                mLastTouchX = x;
                mLastTouchY = y;

                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(event, 0);

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position

                Log.d(TAG,"ACTION_MOVE: mActivePointerId: " + mActivePointerId);

                final int pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);

                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float y = MotionEventCompat.getY(event, pointerIndex);
                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                m_PosX += dx;
                m_PosY += dy;

                this.setX(m_PosX);
                this.setY(m_PosY);

                invalidate();

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;

                Log.d(TAG,"ACTION_MOVE: setX()/Finger X: " + getX() + "/" + x + " setY()/Finger Y: " + getY() + "/" + y);

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;

                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = MotionEventCompat.getX(event, newPointerIndex);
                    mLastTouchY = MotionEventCompat.getY(event, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
                }
                break;
            }
        }
        return true;
//        return super.onTouchEvent(event);
    }

    public String getRoomTAG(){
        return m_strRoomTAG;
    }
}
