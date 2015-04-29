package com.pretolesi.easydomotic.NumerValue;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pretolesi.easydomotic.BaseFragment;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientWriteStatus;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClient;
import com.pretolesi.easydomotic.TcpIpClient.TciIpClientHelper;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientStatus;

import static com.pretolesi.easydomotic.NumerValue.NumericValueData.DataType.*;

/**
 *
 */
public class NumericValue extends EditText implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        TCPIPClient.TCPIPClientStatusListener {

    private static final String TAG = "NumericValue";
    private GestureDetectorCompat mDetector;

    private NumericValueData m_nvd;
    private int m_iTIDRead;
    private int m_iTIDWrite;

    private float mLastTouchX;
    private float mLastTouchY;

    private boolean m_bEditMode;

    public NumericValue(Context context) {
        super(context);
        this.m_nvd = null;
        this.m_iTIDRead = 0;
        this.m_iTIDWrite = 0;
        this.m_bEditMode = false;
    }

    public NumericValue(Context context, NumericValueData nvd, boolean bEditMode) {
        super(context);
        if(nvd != null) {
            this.m_nvd = nvd;
            this.m_iTIDRead = (int)m_nvd.getID() + m_nvd.getProtTcpIpClientValueID() + m_nvd.getProtTcpIpClientValueAddress() + 0;
            this.m_iTIDWrite = (int)m_nvd.getID() + m_nvd.getProtTcpIpClientValueID() + m_nvd.getProtTcpIpClientValueAddress() + 1;
            this.setTag(nvd.getTag());
            this.setId((int)nvd.getID());
        }
        this.m_bEditMode = bEditMode;
    }

    public NumericValueData getLightSwitchData() {
        return m_nvd;
    }

    /*
     * Begin
     * Timer variable and function
     */
    Handler m_TimerHandler;
    public void setTimerHandler() {
        if(m_nvd != null) {
            m_TimerHandler = new Handler();
            m_TimerHandler.postDelayed(m_TimerRunnable, m_nvd.getProtTcpIpClientValueUpdateMillis());
        }
    }

    public void resetTimerHandler() {
        if(m_TimerHandler != null){
            m_TimerHandler.removeCallbacks(m_TimerRunnable);
        }
    }

    private Runnable m_TimerRunnable = new Runnable() {
        @Override
        public void run() {
            if(m_nvd != null && m_TimerHandler != null) {
                NumericValueData.DataType dtDataType = SHORT16;
                if(m_nvd.getProtTcpIpClientValueDataType() == SHORT16.getID()) {
                    dtDataType = SHORT16;
                }
                if(m_nvd.getProtTcpIpClientValueDataType() == INT32.getID()) {
                    dtDataType = INT32;
                }
                if(m_nvd.getProtTcpIpClientValueDataType() == LONG64.getID()) {
                    dtDataType = LONG64;
                }
                if(m_nvd.getProtTcpIpClientValueDataType() == FLOAT32.getID()) {
                    dtDataType = FLOAT32;
                }
                if(m_nvd.getProtTcpIpClientValueDataType() == DOUBLE64.getID()) {
                    dtDataType = DOUBLE64;
                }

                // Read Request
                readNumericValue(m_iTIDRead, m_nvd.getProtTcpIpClientValueAddress(), dtDataType);

                m_TimerHandler.postDelayed(m_TimerRunnable, m_nvd.getProtTcpIpClientValueUpdateMillis());
            }
        }
    };
    /*
     * End
     * Timer variable and function
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Listener
        TciIpClientHelper tich = TciIpClientHelper.getInstance();
        if(m_nvd != null && tich != null){
            TCPIPClient tic = tich.getTciIpClient(m_nvd.getProtTcpIpClientID());
            if(tic != null){
                tic.registerTCPIPClientStatus(this);
            }
        }

        if(!m_bEditMode) {
            setTimerHandler();
        }

        setDefaultValue();

        Log.d(TAG, this.toString() + ": " + "onAttachedToWindow()");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(!m_bEditMode) {
            resetTimerHandler();
        }

        // Listener
        TciIpClientHelper tich = TciIpClientHelper.getInstance();
        if(m_nvd != null && tich != null){
            TCPIPClient tic = tich.getTciIpClient(m_nvd.getProtTcpIpClientID());
            if(tic != null){
                tic.unregisterTCPIPClientStatus(this);
            }
        }

        Log.d(TAG, this.toString() + ": " + "onDetachedFromWindow()");
    }

/*
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, this.toString() + ": " + "onCheckedChanged() enter Check Status: " + isChecked);

        if(m_nvd != null) {
            if(m_nvd.getProtTcpIpClientSendDataOnChange()){
                if(isChecked){
                    writeSwitchValue(m_iTIDON, m_nvd.getProtTcpIpClientValueAddress(), m_nvd.getProtTcpIpClientValueON());
                } else {
                    writeSwitchValue(m_iTIDOFF, m_nvd.getProtTcpIpClientValueAddress(), m_nvd.getProtTcpIpClientValueOFF());
                }
            }
        }

        Log.d(TAG, this.toString() + ": " + "onCheckedChanged() return Check Status: " + isChecked);
    }
*/

    private void readNumericValue(int iTI, int iAddress, NumericValueData.DataType dtDataType){
        TciIpClientHelper tich = TciIpClientHelper.getInstance();
        if(m_nvd != null && tich != null){
            TCPIPClient tic = tich.getTciIpClient(m_nvd.getProtTcpIpClientID());
            if(tic != null){
                tic.readNumericValue(iTI, iAddress, dtDataType);
            }
        }
    }

    private void setDefaultValue(){
        String strDefaultValue = "";
        if(m_nvd != null){
             for(int iIndice = m_nvd.getProtTcpIpClientValueMinNrCharToShow() + m_nvd.getProtTcpIpClientValueNrOfDecimal(); iIndice > 0; iIndice--){
                 if(iIndice == m_nvd.getProtTcpIpClientValueNrOfDecimal()){
                     strDefaultValue = strDefaultValue + ".";
                 }
                 strDefaultValue = strDefaultValue + "#";
             }
            if(m_nvd.getProtTcpIpClientValueUM() != null && !m_nvd.getProtTcpIpClientValueUM().equals("")){
                strDefaultValue = strDefaultValue + " " + m_nvd.getProtTcpIpClientValueUM();
            }
        } else {
            strDefaultValue = NumericValueData.DefaultValue;
        }
        setText(strDefaultValue);
    }

    @Override
    public void onWriteSwitchStatusCallback(TcpIpClientWriteStatus ms) {
        if(ms != null){
            if(ms.getTransactionID() == m_iTIDRead || ms.getTransactionID() == m_iTIDWrite) {
                Toast.makeText(this.getContext(), "Server ID: " + ms.getServerID() + " TID: " + ms.getTransactionID() + " Status: " + ms.getStatus().toString() + " Error Code: " + ms.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
            // Log.d(TAG, this.toString() + ": " + "onModbusStatusCallback() ID: " + ms.getServerID() + " TID: " + ms.getTransactionID() + " Status: " + ms.getStatus().toString());
        }
    }

    @Override
    public void onTcpIpClientStatusCallback(TcpIpClientStatus tics) {
        // Log.d(TAG, this.toString() + ": " + "onTcpIpClientStatusCallback() ID: " + tics.getID() + " Status: " + tics.getStatus().toString());
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
                    if(m_nvd != null) {
                        m_nvd.setSaved(false);
                        Intent intent = NumericValuePropActivity.makeNumericValuePropActivity(this.getContext(), m_nvd);
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
        if(m_nvd != null) {
            m_nvd.setSaved(false);
        }

        if(this.getLayoutParams() instanceof RelativeLayout.LayoutParams){
            RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams)this.getLayoutParams();
            mLastTouchX = event.getRawX() - rllp.leftMargin;
            mLastTouchY = event.getRawY() - rllp.topMargin;
        }

//        Log.d(TAG, this.toString() + ": " + "onTouchEvent: ACTION_DOWN mLastTouchX/mLastTouchY: " + mLastTouchX + "/" + mLastTouchY);

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

        if(m_nvd != null) {
            BaseFragment.setViewPosition(this, (int) dx, (int) dy);
            m_nvd.setPosX((int)dx);
            m_nvd.setPosY((int)dy);
        }

//        Log.d(TAG, this.toString() + ": " + "onTouchEvent: ACTION_MOVE dx/dy: " + dx + "/" + dy + ", mLastTouchX/mLastTouchY: " + mLastTouchX + "/" + mLastTouchY + ", x/y: " + x + "/" + y);

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
