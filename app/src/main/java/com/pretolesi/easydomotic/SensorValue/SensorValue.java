package com.pretolesi.easydomotic.SensorValue;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.pretolesi.easydomotic.BaseValue.BaseValue;
import com.pretolesi.easydomotic.CustomControls.NumericEditText;
import com.pretolesi.easydomotic.CustomControls.NumericEditText.DataType;
import com.pretolesi.easydomotic.NumerValue.NumericValueData;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClient;
import com.pretolesi.easydomotic.TcpIpClient.TciIpClientHelper;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientWriteStatus;

import java.util.List;

/**
 *
 */
public class SensorValue extends BaseValue implements
        TCPIPClient.TcpIpClientWriteSwitchStatusListener,
        SensorEventListener {

    private static final String TAG = "SensorValue";
//    private GestureDetectorCompat mDetector;

    private com.pretolesi.easydomotic.SensorValue.SensorValueData m_svd;
    private int m_iMsgID;
    private int m_iTIDWrite;

//    private float mLastTouchX;
//    private float mLastTouchY;

//    private boolean m_bEditMode;

//    private NumericEditText m_edEditText;

    // Sensors & SensorManager
    private SensorManager m_SensorManager;
    private Sensor m_Sensor;
    private int m_iSensorType;

    // Storage for Sensor readings
    private float[] m_SensorValue;

    // Rotation around the Z axis
//    private double m_RotationInDegress;


    public SensorValue(Context context) {
        super(context);
        this.m_svd = null;
        this.m_iMsgID = -1;
        this.m_iTIDWrite = -1;

        // Sensors & SensorManager
        m_SensorManager = null;
        m_Sensor = null;
        m_iSensorType = 0;
        m_SensorValue = null;
    }

    public SensorValue(Context context, com.pretolesi.easydomotic.SensorValue.SensorValueData svd, int iMsgID, boolean bEditMode) {
        super(context, bEditMode);
        if(svd != null) {
            this.m_svd = svd;
            this.m_iMsgID = iMsgID;
            this.m_iTIDWrite = m_iMsgID + 1;
            this.setTag(svd.getTag());
        }
        // Sensors & SensorManager
        m_SensorManager = null;
        m_Sensor = null;
        m_iSensorType = 0;
        m_SensorValue = null;
    }

    /*
     * Begin
     * Timer variable and function
     */
    Handler m_TimerHandler;
    public void setTimerHandler() {
        if(m_svd != null) {
            m_TimerHandler = new Handler();
            m_TimerHandler.postDelayed(m_TimerRunnable, m_svd.getProtTcpIpClientValueUpdateMillis());
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
            if(m_svd != null && m_TimerHandler != null) {
                if(!m_svd.getProtTcpIpClientSendDataOnChange()) {
                    // Write Sensor Data
                }

                m_TimerHandler.postDelayed(m_TimerRunnable, m_svd.getProtTcpIpClientValueUpdateMillis());
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
        // Set default value
        setText(getDefaultValue());

        // Listener
        if(!getEditMode()) {
            if(m_svd != null){
                TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_svd.getProtTcpIpClientID());
                if(tic != null){
                    tic.registerTcpIpClientWriteSwitchStatus(this);
                }
            }
            setTimerHandler();
        }

        // Sensor
        boolean bSensorOk = false;
        if(m_svd != null && !m_svd.getSensorEnableSimulation()) {
            // Get reference to SensorManager
            m_SensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            if (m_SensorManager != null) {
                // Get reference to Sensor
                List<Sensor> ls = m_SensorManager.getSensorList(Sensor.TYPE_ALL);
                if(ls != null){
                    m_iSensorType = ls.get((int)m_svd.getSensorTypeID()).getType();
                    if(m_iSensorType > 0){
                        m_Sensor = m_SensorManager.getDefaultSensor(m_iSensorType);
                        if (m_Sensor != null) {
                            // Create array for value
                            m_SensorValue = new float[6];

                            // Sensor Available
                            m_SensorManager.registerListener(this, m_Sensor, SensorManager.SENSOR_DELAY_UI);
                            bSensorOk = true;
                        }
                    }
                }
            }
        }
        if(bSensorOk){
            this.setError(null);
        } else {
            this.setError("");
        }

        // Log.d(TAG, this.toString() + ": " + "onAttachedToWindow()");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Sensor
        // Get reference to SensorManager
        if(m_SensorManager != null) {
            m_SensorManager.unregisterListener(this);
        }

        if(!!getEditMode()) {
            resetTimerHandler();
        }

        // Listener
        if(m_svd != null){
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_svd.getProtTcpIpClientID());
            if(tic != null){
                tic.unregisterTcpIpClientWriteSwitchStatus(this);
            }
        }

        // Log.d(TAG, this.toString() + ": " + "onDetachedFromWindow()");
    }

    @Override
    protected void writeWriteInputValue(String strValue){
        super.writeWriteInputValue(strValue);
        if(m_svd != null){
            DataType dtDataType = DataType.getDataType(m_svd.getProtTcpIpClientValueDataType());
            if(dtDataType != null){
                switch (dtDataType) {
                    case SHORT16:
                        int iValue;
                        try {
                            iValue = Integer.parseInt(strValue);
                            // Write Request
                            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_svd.getProtTcpIpClientID());
                            if(tic != null){
                                tic.writeShort(getContext(), m_iTIDWrite, m_svd.getProtTcpIpClientValueID(), m_svd.getProtTcpIpClientValueAddress(), iValue);
                            }

                            return;

                        } catch (Exception ignored) {
                        }
                        break;

                    case INT32:
                        long lValue;
                        try {
                            lValue = Long.parseLong(strValue);
                            // Write Request
                            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_svd.getProtTcpIpClientID());
                            if(tic != null){
                                tic.writeInteger(getContext(), m_iTIDWrite, m_svd.getProtTcpIpClientValueID(), m_svd.getProtTcpIpClientValueAddress(), lValue);
                            }

                            return;

                        } catch (Exception ignored) {
                        }
                        break;

                    case LONG64:
                        long lValue64;
                        try {
                            lValue64 = Long.parseLong(strValue);
                            // Write Request
                            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_svd.getProtTcpIpClientID());
                            if(tic != null){
                                tic.writeLong(getContext(), m_iTIDWrite, m_svd.getProtTcpIpClientValueID(), m_svd.getProtTcpIpClientValueAddress(), lValue64);
                            }

                            return;

                        } catch (Exception ignored) {
                        }
                        break;

                    case FLOAT32:
                        float fValue;
                        try {
                            fValue = Float.parseFloat(strValue);
                            // Write Request
                            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_svd.getProtTcpIpClientID());
                            if(tic != null){
                                tic.writeFloat(getContext(), m_iTIDWrite, m_svd.getProtTcpIpClientValueID(), m_svd.getProtTcpIpClientValueAddress(), fValue);
                            }

                            return;

                        } catch (Exception ignored) {
                        }
                        break;

                    case DOUBLE64:
                        double dblValue;
                        try {
                            dblValue = Double.parseDouble(strValue);
                            // Write Request
                            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_svd.getProtTcpIpClientID());
                            if(tic != null){
                                tic.writeDouble(getContext(), m_iTIDWrite, m_svd.getProtTcpIpClientValueID(), m_svd.getProtTcpIpClientValueAddress(), dblValue);
                            }

                            return;

                        } catch (Exception ignored) {
                        }
                        break;

                }
            }
        }
    }

    private String getDefaultValue(){
        String strDefaultValue = "";

        if(m_svd != null){
             for(int iIndice = m_svd.getValueMinNrCharToShow() + m_svd.getValueNrOfDecimal(); iIndice > 0; iIndice--){
                 if(iIndice == m_svd.getValueNrOfDecimal()){
                     strDefaultValue = strDefaultValue + ".";
                 }
                 strDefaultValue = strDefaultValue + "#";
             }
            if(m_svd.getValueUM() != null && !m_svd.getValueUM().equals("")){
                strDefaultValue = strDefaultValue + " " + m_svd.getValueUM();
            }
        } else {
            strDefaultValue = NumericValueData.DefaultValue;
        }

        return strDefaultValue;
    }
    private String getErrorValue(int iErrorCode){
        return "Error Code: " + iErrorCode;
    }

    private String getTimeoutValue(){
        return "Timeout";
    }
/*
    private void openWriteInput(){
        if(m_edEditText == null){
            m_edEditText = new NumericEditText(getContext());
            m_edEditText.setInputLimit(0,655353);
            m_edEditText.setText("");
            ViewParent view = this.getParent();
            if(view != null && view instanceof RelativeLayout){
                RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams)this.getLayoutParams();
                m_edEditText.setLayoutParams(rllp);
                m_edEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                m_edEditText.setSingleLine();
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
                        if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                            if(m_edEditText != null){
                                m_edEditText.clearFocus();
                            }
                            return true;
                        }

                        if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                            if(m_edEditText != null){
                                writeValue(m_edEditText.getText().toString());
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
                        if(!hasFocus){
                            closeWriteInput();
                        }
                    }
                });
            }
        }
    }

    private synchronized void closeWriteInput(){
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
*/
    @Override
    public void onWriteSwitchStatusCallback(TcpIpClientWriteStatus ticws) {
        if(ticws != null && m_svd != null && m_edEditText != null){
            if(ticws.getServerID() == m_svd.getProtTcpIpClientID()){
                if(ticws.getTID() == m_iTIDWrite) {
                    if(ticws.getStatus() == TcpIpClientWriteStatus.Status.OK){
                        // Write Ok, i can close the Input
                        m_edEditText.setError(null);
                        m_edEditText.clearFocus();
                    }
                } else {
                    m_edEditText.setError("");
                }
            }
        }
    }
/*
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
                    if(m_svd != null) {
                        m_svd.setSaved(false);
                        Intent intent = SensorValuePropActivity.makeSensorValuePropActivity(this.getContext(), m_svd);
                        this.getContext().startActivity(intent);
                    }
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

                    if(m_svd.getSensorEnableSimulation()){
                        openWriteInput();
                    }

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
    public boolean onDown(MotionEvent event) {
        if(m_svd != null) {
            m_svd.setSaved(false);
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

        if(m_svd != null) {
            BaseFragment.setViewPosition(this, (int) dx, (int) dy);
            m_svd.setPosX((int)dx);
            m_svd.setPosY((int)dy);
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
*/
    // Sensor
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(m_SensorValue != null && m_svd != null){
            if(event.sensor.getType() == m_iSensorType){
                System.arraycopy(event.values, 0, m_SensorValue, 0, event.values.length);
                String strValue = String.format("% " + m_svd.getValueMinNrCharToShow() + "." + m_svd.getValueNrOfDecimal() + "f %s", (double)m_SensorValue[(int)m_svd.getSensorValueID()], m_svd.getValueUM());
                setText(strValue);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onTouchActionUp(boolean bEditMode){
        super.onTouchActionUp(bEditMode);
        if(m_svd != null) {
            if(bEditMode) {
                m_svd.setSaved(false);
                m_svd.setPosX((int)getX());
                m_svd.setPosY((int)getY());
                Intent intent = SensorValuePropActivity.makeSensorValuePropActivity(this.getContext(), m_svd);
                this.getContext().startActivity(intent);
            } else {
                if(m_svd.getSensorEnableSimulation()){
                    openWriteInput();
                }
            }
        }
    }

}
