package com.pretolesi.easydomotic;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by RPRETOLESI on 17/03/2015.
 */
public class LightSwitch extends Switch {

    private static final String TAG = "LightSwitch";

    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;

    private float mLastTouchX;
    private float mLastTouchY;
    private float mPosX;
    private float mPosY;

    public LightSwitch(Context context) {
        super(context);
    }

    public LightSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LightSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
/*
    public LightSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Let the ScaleGestureDetector inspect all events.event
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

                Log.d(TAG, "ACTION_DOWN: Pos X: " + mPosX + "-Pos Y: " + mPosY);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex =
                        MotionEventCompat.findPointerIndex(event, mActivePointerId);

                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float y = MotionEventCompat.getY(event, pointerIndex);

                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                mPosX += dx;
                mPosY += dy;

 //               this.setX(mPosX);
 //               this.setY(mPosY);
                this.setPivotX(mPosX);
                this.setPivotY(mPosY);
                 invalidate();

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;

                Log.d(TAG, "ACTION_MOVE: Pos X: " + mPosX + "-Pos Y: " + mPosY);

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

                Log.d(TAG, "ACTION_POINTER_UP: Pos X: " + mPosX + "-Pos Y: " + mPosY);

                break;
            }
        }
//        return true;
        return super.onTouchEvent(event);
    }
}
