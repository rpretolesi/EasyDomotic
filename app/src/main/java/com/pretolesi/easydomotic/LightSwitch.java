package com.pretolesi.easydomotic;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.Switch;

/**
 * Created by RPRETOLESI on 17/03/2015.
 */
public class LightSwitch extends Switch {

    private static final String TAG = "LightSwitch";

    LightSwitchData m_lsd;

    private float mLastTouchX;
    private float mLastTouchY;

    public LightSwitch(Context context, LightSwitchData lsd) {
        super(context);
        this.m_lsd = lsd;
        this.setTag(lsd.getTag());
    }
    public LightSwitchData getLightSwitchData() {
/*
        float fPosX = 0.0f;
        float fPosY = 0.0f;

        if(this.getLayoutParams() instanceof RelativeLayout.LayoutParams){
            RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams)this.getLayoutParams();
            fPosX = rllp.leftMargin;
            fPosY = rllp.topMargin;
        }
*/
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
        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
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

                BaseFragment.setViewPosition(this,(int)dx,(int)dy);
                if(m_lsd != null) {
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

}
