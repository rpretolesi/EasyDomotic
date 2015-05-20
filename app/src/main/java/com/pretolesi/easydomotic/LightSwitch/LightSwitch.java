package com.pretolesi.easydomotic.LightSwitch;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pretolesi.easydomotic.BaseFragment;
import com.pretolesi.easydomotic.BaseValue.BaseValueData;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientWriteStatus;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClient;
import com.pretolesi.easydomotic.TcpIpClient.TciIpClientHelper;

/**
 *
 */
public class LightSwitch extends Switch implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        ToggleButton.OnCheckedChangeListener,
        TCPIPClient.TcpIpClientWriteStatusListener {

    private static final String TAG = "LightSwitch";
    private GestureDetectorCompat mDetector;

    private BaseValueData m_bvd;
    private int m_iMsgID;
    private int m_iTIDOFF;
    private int m_iTIDOFFON;
    private int m_iTIDONOFF;
    private int m_iTIDON;

    private float mLastTouchX;
    private float mLastTouchY;

    private boolean m_bEditMode;

    public LightSwitch(Context context) {
        super(context);
        this.m_bvd = null;
        this.m_iMsgID = -1;
        this.m_iTIDOFF = -1;
        this.m_iTIDOFFON = -1;
        this.m_iTIDONOFF = -1;
        this.m_iTIDON = -1;
        this.m_bEditMode = false;
    }

    public LightSwitch(Context context, BaseValueData bvd, int iMsgID, boolean bEditMode) {
        super(context);
        if(bvd != null) {
            this.m_bvd = bvd;
            this.m_iMsgID = iMsgID;
            this.m_iTIDOFF = m_iMsgID + 1;
            this.m_iTIDOFFON = m_iMsgID + 2;
            this.m_iTIDONOFF = m_iMsgID + 3;
            this.m_iTIDON = m_iMsgID + 4;
            this.setTag(bvd.getTag());
        }
        this.m_bEditMode = bEditMode;
    }

    /*
     * End
     * Timer variable and function
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Listener
        if(m_bvd != null){
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
            if(tic != null){
                tic.registerTcpIpClientWriteSwitchStatus(this);
            }
        }

        setOnCheckedChangeListener(this);

        // Log.d(TAG, this.toString() + ": " + "onAttachedToWindow()");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        setOnCheckedChangeListener(null);

        // Listener
        if(m_bvd != null){
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
            if(tic != null){
                tic.unregisterTcpIpClientWriteSwitchStatus(this);
            }
        }

        // Log.d(TAG, this.toString() + ": " + "onDetachedFromWindow()");
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        writeSwitchValue(isChecked);
    }

    private void writeSwitchValue(boolean bValue){
        if(m_bvd != null){
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
            if(tic != null){
                if(bValue) {
                    tic.writeShort(getContext(), m_iTIDON, m_bvd.getProtTcpIpClientValueID(), m_bvd.getProtTcpIpClientValueAddress(), m_bvd.getWriteValueON());
                } else {
                    tic.writeShort(getContext(), m_iTIDOFF, m_bvd.getProtTcpIpClientValueID(), m_bvd.getProtTcpIpClientValueAddress(), m_bvd.getWriteValueOFF());
                }
            }
        }
    }

    @Override
    public void onWriteValueStatusCallback(TcpIpClientWriteStatus ticws) {

        if(ticws != null && m_bvd != null){
            if(ticws.getTID() == m_iTIDOFF || ticws.getTID() == m_iTIDOFFON || ticws.getTID() == m_iTIDONOFF || ticws.getTID() == m_iTIDON) {
                if(ticws.getServerID() == m_bvd.getProtTcpIpClientID()) {
                    if(ticws.getStatus() == TcpIpClientWriteStatus.Status.OK){
                        setError(null);
                    } else {
                        Toast.makeText(this.getContext(), "Server ID: " + ticws.getServerID() + ", TID: " + ticws.getTID() + ", Status: " + ticws.getStatus().toString() + ", Error Code: " + ticws.getErrorCode() + ", Error Message: " + ticws.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        setError("");
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(m_bEditMode){
            if(mDetector == null){
                // Instantiate the gesture detector with the
                // application context and an implementation of
                // GestureDetector.OnGestureListener
                mDetector = new GestureDetectorCompat(getContext(),this);
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
                    if(m_bvd != null) {
                        m_bvd.setSaved(false);
                        Intent intent = LightSwitchPropActivity.makeBaseValuePropActivityByValueData(this.getContext(), LightSwitchPropActivity.class, m_bvd);
                        this.getContext().startActivity(intent);
                    }
                    break;
                }
            }

            this.mDetector.onTouchEvent(event);

            return true;
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
    public boolean onDown(MotionEvent event) {
        if(m_bvd != null) {
            m_bvd.setSaved(false);
        }

        if(this.getLayoutParams() instanceof RelativeLayout.LayoutParams){
            RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams)this.getLayoutParams();
            mLastTouchX = event.getRawX() - rllp.leftMargin;
            mLastTouchY = event.getRawY() - rllp.topMargin;
        }

//        // Log.d(TAG, this.toString() + ": " + "onTouchEvent: ACTION_DOWN mLastTouchX/mLastTouchY: " + mLastTouchX + "/" + mLastTouchY);

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
        final float dx = x - mLastTouchX;
        final float dy = y - mLastTouchY;

        if(m_bvd != null) {
            BaseFragment.setViewPosition(this, (int) dx, (int) dy);
            m_bvd.setPosX((int)dx);
            m_bvd.setPosY((int)dy);
        }

//        // Log.d(TAG, this.toString() + ": " + "onTouchEvent: ACTION_MOVE dx/dy: " + dx + "/" + dy + ", mLastTouchX/mLastTouchY: " + mLastTouchX + "/" + mLastTouchY + ", x/y: " + x + "/" + y);

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        return false;
    }
}
