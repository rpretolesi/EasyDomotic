package com.pretolesi.easydomotic;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.Switch;

/**
 * Created by RPRETOLESI on 17/03/2015.
 */
public class LightSwitch extends Switch {

    private static final String TAG = "LightSwitch";

    private long m_id;
    private String m_strRoomTAG;

    private float mLastTouchX;
    private float mLastTouchY;

    public LightSwitch(Context context, LightSwitchData lsd) {
        super(context);
        this.m_id = lsd.getID();
        this.m_strRoomTAG = lsd.getRoomTAG();
        this.setTag(lsd.getTAG());
    }
    public LightSwitchData getLightSwitchData() {
        float fPosX = 0.0f;
        float fPosY = 0.0f;
        RelativeLayout.LayoutParams rllp =  (RelativeLayout.LayoutParams)this.getLayoutParams();
        if(rllp != null){
            fPosX = rllp.leftMargin;
            fPosY = rllp.topMargin;
        }
        return new LightSwitchData(m_id, m_strRoomTAG, (String)getTag(), fPosX, fPosY, 0.0f, false);
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
        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastTouchX = event.getRawX();
                mLastTouchY = event.getRawY();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float x = event.getRawX();
                final float y = event.getRawY();
                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;
/*
                ViewParent v = this.getParent();
                if(v instanceof RelativeLayout){
                    RelativeLayout rl = (RelativeLayout)v;
                    RelativeLayout.LayoutParams rllp =  (RelativeLayout.LayoutParams)rl.getLayoutParams();
                    float f =rllp.topMargin;

                }
*/
                this.animate().translationXBy(dx).translationYBy(dy).setDuration(0).start();

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;

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

}
