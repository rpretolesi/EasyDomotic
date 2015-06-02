package com.pretolesi.easydomotic.LightSwitch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pretolesi.easydomotic.BaseFragment;
import com.pretolesi.easydomotic.BaseValue.BaseValueData;
import com.pretolesi.easydomotic.CustomControls.LabelTextView;
import com.pretolesi.easydomotic.CustomControls.NumericDataType;
import com.pretolesi.easydomotic.CustomControls.NumericDataType.DataType;
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
    private NumericDataType.DataType m_dtDataType;

    private GestureDetectorCompat mDetector;
    private float mLastTouchX;
    private float mLastTouchY;

    private BaseValueData m_bvd;
    private int m_iMsgID;
    private int m_iTIDOFF;
    private int m_iTIDOFFON;
    private int m_iTIDONOFF;
    private int m_iTIDON;

    private boolean m_bEditMode;
    private boolean m_bVertical;

    // Label for Switch
    private LabelTextView m_LabelTextView;

    public LightSwitch(Context context) {
        super(context);
        m_LabelTextView = null;
        this.m_bvd = null;
        this.m_iMsgID = -1;
        this.m_iTIDOFF = -1;
        this.m_iTIDOFFON = -1;
        this.m_iTIDONOFF = -1;
        this.m_iTIDON = -1;
        setNumericDataType(DataType.SHORT);
        setEditMode(false);
        setVertical(false);
        setTextOff("0");
        setTextOn("1");
    }

    public LightSwitch(Context context, BaseValueData bvd, int iMsgID, boolean bEditMode) {
        super(context);
        m_LabelTextView = null;
        if(bvd != null) {
            this.m_bvd = bvd;
            this.m_iMsgID = iMsgID;
            this.m_iTIDOFF = m_iMsgID + 1;
            this.m_iTIDOFFON = m_iMsgID + 2;
            this.m_iTIDONOFF = m_iMsgID + 3;
            this.m_iTIDON = m_iMsgID + 4;
            this.setTag(bvd.getTag());
            setNumericDataType(DataType.SHORT);
            setEditMode(bEditMode);
            setVertical(m_bvd.getVertical());
        }
        setTextOff("0");
        setTextOn("1");
    }

    protected void setEditMode(boolean bEditMode){
        m_bEditMode = bEditMode;
    }

    protected void setNumericDataType(NumericDataType.DataType dtDataType){
        m_dtDataType = dtDataType;
    }

    public void setVertical(boolean bVertical) { this.m_bVertical = bVertical; }

    protected boolean getEditMode(){
        return m_bEditMode;
    }

    protected NumericDataType.DataType getNumericDataType(){
        return m_dtDataType;
    }

    public boolean getVertical() { return m_bVertical; }

    /*
     * End
     * Timer variable and function
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Label Text View
        ViewParent view = this.getParent();
        if(view != null && view instanceof RelativeLayout) {
            m_LabelTextView = new LabelTextView(getContext());
            ((RelativeLayout) view).addView(m_LabelTextView);
        }

        // Listener
        if(m_bvd != null){
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
            if(tic != null){
                tic.registerTcpIpClientWriteSwitchStatus(this);
            }
        }

        setOnCheckedChangeListener(this);
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

        // Delete
        m_LabelTextView = null;
    }

    @Override
    protected void onLayout(boolean changed,  int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b); }

    @Override
    protected void onDraw (Canvas canvas){
        super.onDraw(canvas);
        RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams)this.getLayoutParams();
        if(rllp != null) {
            if(m_LabelTextView != null) {
                m_LabelTextView.setPosition(rllp.leftMargin, rllp.topMargin, rllp.rightMargin, rllp.bottomMargin, canvas.getHeight(), canvas.getWidth(), getVertical());
                if(getTag() != null && getTag() instanceof String){
                    m_LabelTextView.setText((String) getTag());
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        writeSwitchValue(isChecked);
    }

    private void writeSwitchValue(boolean bValue){
        if(m_bvd != null){
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
            if(tic != null){

                Short sh;
                if(bValue) {
                    sh = (short)m_bvd.getWriteValueON();
                    tic.writeValue(getContext(), m_iTIDON, m_bvd.getProtTcpIpClientValueID(), m_bvd.getProtTcpIpClientValueAddress(), sh);
                 } else {
                    sh = (short)m_bvd.getWriteValueOFF();
                    tic.writeValue(getContext(), m_iTIDON, m_bvd.getProtTcpIpClientValueID(), m_bvd.getProtTcpIpClientValueAddress(), sh);
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
//                        Toast.makeText(this.getContext(), "Server ID: " + ticws.getServerID() + ", TID: " + ticws.getTID() + ", Status: " + ticws.getStatus().toString() + ", Error Code: " + ticws.getErrorCode() + ", Error Message: " + ticws.getErrorMessage(), Toast.LENGTH_SHORT).show();
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

        super.onTouchEvent(event);
        return true;
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
            m_bvd.setPosX((int) dx);
            m_bvd.setPosY((int) dy);

        }
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
