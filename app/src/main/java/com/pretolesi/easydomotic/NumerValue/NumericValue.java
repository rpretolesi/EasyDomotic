package com.pretolesi.easydomotic.NumerValue;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pretolesi.easydomotic.BaseFragment;
import com.pretolesi.easydomotic.CustomControls.EDEditText;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientWriteStatus;
import com.pretolesi.easydomotic.ValueUtils.ValueDataType;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientReadStatus;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClient;
import com.pretolesi.easydomotic.TcpIpClient.TciIpClientHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 */
public class NumericValue extends TextView implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        TCPIPClient.TcpIpClientReadValueStatusListener,
        TCPIPClient.TcpIpClientWriteSwitchStatusListener {

    private static final String TAG = "NumericValue";
    private GestureDetectorCompat mDetector;

    private NumericValueData m_nvd;
    private int m_iMsgID;
    private int m_iTIDRead;
    private int m_iTIDWrite;

    private float mLastTouchX;
    private float mLastTouchY;

    private boolean m_bEditMode;

    private EDEditText m_edEditText;

    public NumericValue(Context context) {
        super(context);
        this.m_nvd = null;
        this.m_iMsgID = -1;
        this.m_iTIDRead = -1;
        this.m_iTIDWrite = -1;
        this.m_bEditMode = false;
    }

    public NumericValue(Context context, NumericValueData nvd, int iMsgID, boolean bEditMode) {
        super(context);
        if(nvd != null) {
            this.m_nvd = nvd;
            this.m_iMsgID = iMsgID;
            this.m_iTIDRead = m_iMsgID + 1;
            this.m_iTIDWrite = m_iMsgID + 2;
            this.setTag(nvd.getTag());
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
                // Read Request
                readNumericValue();

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
        if(!m_bEditMode) {
            if(m_nvd != null){
                TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_nvd.getProtTcpIpClientID());
                if(tic != null){
                    tic.registerTcpIpClientReadValueStatus(this);
                    tic.registerTcpIpClientWriteSwitchStatus(this);
                }
            }
            setTimerHandler();
        }

        setText(getDefaultValue());

        // Log.d(TAG, this.toString() + ": " + "onAttachedToWindow()");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(!m_bEditMode) {
            resetTimerHandler();
        }

        // Listener
        if(m_nvd != null){
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_nvd.getProtTcpIpClientID());
            if(tic != null){
                tic.unregisterTcpIpClientReadValueStatus(this);
                tic.unregisterTcpIpClientWriteSwitchStatus(this);
            }
        }

        // Log.d(TAG, this.toString() + ": " + "onDetachedFromWindow()");
    }

    private void readNumericValue(){
        if(m_nvd != null){
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_nvd.getProtTcpIpClientID());
            ValueDataType.DataType dtDataType = ValueDataType.DataType.getDataType(m_nvd.getProtTcpIpClientValueDataType());
            if(tic != null && dtDataType != null){
                tic.readNumericValue(getContext(),m_iTIDRead, m_nvd.getProtTcpIpClientValueID(), m_nvd.getProtTcpIpClientValueAddress(), dtDataType);
            }
        }
    }

    private void writeValue(String strValue){
        if(m_nvd != null){
            ValueDataType.DataType dtDataType = ValueDataType.DataType.getDataType(m_nvd.getProtTcpIpClientValueDataType());
            if(dtDataType != null){
                switch (dtDataType) {
                    case SHORT16:
                        int iValue;
                        try {
                            iValue = Integer.parseInt(strValue);
                            // Write Request
                            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_nvd.getProtTcpIpClientID());
                            if(tic != null){
                                tic.writeShort(getContext(), m_iTIDWrite, m_nvd.getProtTcpIpClientValueID(), m_nvd.getProtTcpIpClientValueAddress(), iValue);
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
                            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_nvd.getProtTcpIpClientID());
                            if(tic != null){
                                tic.writeInteger(getContext(), m_iTIDWrite, m_nvd.getProtTcpIpClientValueID(), m_nvd.getProtTcpIpClientValueAddress(), lValue);
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
                            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_nvd.getProtTcpIpClientID());
                            if(tic != null){
                                tic.writeLong(getContext(), m_iTIDWrite, m_nvd.getProtTcpIpClientValueID(), m_nvd.getProtTcpIpClientValueAddress(), lValue64);
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
                            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_nvd.getProtTcpIpClientID());
                            if(tic != null){
                                tic.writeFloat(getContext(), m_iTIDWrite, m_nvd.getProtTcpIpClientValueID(), m_nvd.getProtTcpIpClientValueAddress(), fValue);
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
                            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_nvd.getProtTcpIpClientID());
                            if(tic != null){
                                tic.writeDouble(getContext(), m_iTIDWrite, m_nvd.getProtTcpIpClientValueID(), m_nvd.getProtTcpIpClientValueAddress(), dblValue);
                            }

                            return;

                        } catch (Exception ignored) {
                        }
                        break;

                }
            }
        }
        if(m_edEditText != null){
            m_edEditText.setError("");
        }
    }

    private String getDefaultValue(){
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

        return strDefaultValue;
    }
    private String getErrorValue(int iErrorCode){
        return "Error Code: " + iErrorCode;
    }

    private String getTimeoutValue(){
        return "Timeout";
    }

    private void openWriteInput(){
        if(m_edEditText == null){
            m_edEditText = new EDEditText(getContext());
            m_edEditText.setInputLimit(0,65);
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
                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                            if (m_edEditText != null) {
                                m_edEditText.clearFocus();
                            }
                            return true;
                        }

                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                            if (m_edEditText != null) {
                                if (m_edEditText.validateInputLimit()) {
                                    writeValue(m_edEditText.getText().toString());
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

    @Override
    public void onReadValueStatusCallback(TcpIpClientReadStatus ticrs) {
        if(ticrs != null && m_nvd != null){
            if(ticrs.getServerID() == m_nvd.getProtTcpIpClientID()){
                if(ticrs.getTID() == m_iTIDRead) {
                    String strValue = "";
                    if(ticrs.getStatus() == TcpIpClientReadStatus.Status.OK){
                        ValueDataType.DataType dtDataType = ValueDataType.DataType.getDataType(m_nvd.getProtTcpIpClientValueDataType());
                        if(dtDataType != null){
                            switch (dtDataType) {
                                case SHORT16:
                                    if(ticrs.getValue() != null && ticrs.getValue().length == 2) {
                                        short shValue = ByteBuffer.wrap(ticrs.getValue()).getShort();
                                        strValue = String.format("% " + m_nvd.getProtTcpIpClientValueMinNrCharToShow() + "." + m_nvd.getProtTcpIpClientValueNrOfDecimal() + "f %s", (double)shValue, m_nvd.getProtTcpIpClientValueUM());
                                    }
                                    break;

                                case INT32:
                                    if(ticrs.getValue() != null && ticrs.getValue().length == 4) {
                                        int iValue = ByteBuffer.wrap(ticrs.getValue()).getInt();
                                        strValue = String.format("% " + m_nvd.getProtTcpIpClientValueMinNrCharToShow() + "." + m_nvd.getProtTcpIpClientValueNrOfDecimal() + "f %s", (double)iValue, m_nvd.getProtTcpIpClientValueUM());
                                    }
                                    break;

                                case LONG64:
                                    if(ticrs.getValue() != null && ticrs.getValue().length == 8) {
                                        long lValue = ByteBuffer.wrap(ticrs.getValue()).getLong();
                                        strValue = String.format("% " + m_nvd.getProtTcpIpClientValueMinNrCharToShow() + "d %s", lValue, m_nvd.getProtTcpIpClientValueUM());
                                    }
                                    break;

                                case FLOAT32:
                                    if(ticrs.getValue() != null && ticrs.getValue().length == 4) {
                                        float fValue = ByteBuffer.wrap(ticrs.getValue()).getFloat();
                                        strValue = String.format("% " + m_nvd.getProtTcpIpClientValueMinNrCharToShow() + "." + m_nvd.getProtTcpIpClientValueNrOfDecimal() + "f %s", (double)fValue, m_nvd.getProtTcpIpClientValueUM());
                                    }
                                    break;

                                case DOUBLE64:
                                    if(ticrs.getValue() != null && ticrs.getValue().length == 8) {
                                        double dValue = ByteBuffer.wrap(ticrs.getValue()).getDouble();
                                        strValue = String.format("% " + m_nvd.getProtTcpIpClientValueMinNrCharToShow() + "." + m_nvd.getProtTcpIpClientValueNrOfDecimal() + "f %s", dValue, m_nvd.getProtTcpIpClientValueUM());
                                    }

                                    break;
                            }
                            this.setError(null);
                        }
                    } else if(ticrs.getStatus() == TcpIpClientReadStatus.Status.TIMEOUT) {
                        strValue = getTimeoutValue();
                        this.setError("");
                    } else {
                        strValue = getErrorValue(ticrs.getErrorCode());
                        this.setError("");
                    }

                    setText(strValue);
                }
                // // Log.d(TAG, this.toString() + ": " + "onModbusStatusCallback() ID: " + ms.getServerID() + " TID: " + ms.getTID() + " Status: " + ms.getStatus().toString());
            }
        }
    }

    @Override
    public void onWriteSwitchStatusCallback(TcpIpClientWriteStatus ticws) {
        if(ticws != null && m_nvd != null && m_edEditText != null){
            if(ticws.getServerID() == m_nvd.getProtTcpIpClientID()){
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
        } else {
            final int action = MotionEventCompat.getActionMasked(event);

            switch (action) {
                case MotionEvent.ACTION_DOWN: {

                    return true;

                }
                case MotionEvent.ACTION_UP: {

                    openWriteInput();

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
        if(m_nvd != null) {
            m_nvd.setSaved(false);
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

        if(m_nvd != null) {
            BaseFragment.setViewPosition(this, (int) dx, (int) dy);
            m_nvd.setPosX((int)dx);
            m_nvd.setPosY((int)dy);
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
