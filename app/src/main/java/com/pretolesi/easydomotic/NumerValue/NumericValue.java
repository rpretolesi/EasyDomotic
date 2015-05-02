package com.pretolesi.easydomotic.NumerValue;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pretolesi.easydomotic.BaseFragment;
import com.pretolesi.easydomotic.CustomControls.EDEditText;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientReadStatus;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClient;
import com.pretolesi.easydomotic.TcpIpClient.TciIpClientHelper;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientStatus;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.pretolesi.easydomotic.NumerValue.NumericValueData.DataType.*;

/**
 *
 */
public class NumericValue extends TextView implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        TCPIPClient.TcpIpClientReadValueStatusListener {

    private static final String TAG = "NumericValue";
    private GestureDetectorCompat mDetector;

    private NumericValueData m_nvd;
    private int m_iTIDRead;
    private int m_iTIDWrite;

    private float mLastTouchX;
    private float mLastTouchY;

    private boolean m_bEditMode;

    private EDEditText m_edEditText;
    private TextView aaa;

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
                readNumericValue(m_iTIDRead, m_nvd.getProtTcpIpClientValueID(), m_nvd.getProtTcpIpClientValueAddress(), dtDataType);

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
                }
            }
            setTimerHandler();
        }


        this.setError("");
        setText(getDefaultValue());

        Log.d(TAG, this.toString() + ": " + "onAttachedToWindow()");
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
            }
        }

        Log.d(TAG, this.toString() + ": " + "onDetachedFromWindow()");
    }

    private void readNumericValue(int iTID, int iUID, int iAddress, NumericValueData.DataType dtDataType){
        if(m_nvd != null){
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_nvd.getProtTcpIpClientID());
            if(tic != null){
                tic.readNumericValue(iTID, iUID, iAddress, dtDataType);
            }
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

    @Override
    public void onReadValueStatusCallback(TcpIpClientReadStatus ticrs) {
        if(ticrs != null && m_nvd != null){
            if(ticrs.getServerID() == m_nvd.getProtTcpIpClientID()){
                if(ticrs.getTID() == m_iTIDRead) {
                    String strValue = "";
                    if(ticrs.getStatus() == TcpIpClientReadStatus.Status.OK){
                        if(m_nvd.getProtTcpIpClientValueDataType() == NumericValueData.DataType.SHORT16.getID()){
                            if(ticrs.getValue() != null && ticrs.getValue().length == 2) {
                                short shValue = ByteBuffer.wrap(ticrs.getValue()).order(ByteOrder.LITTLE_ENDIAN).getShort();
                                strValue = String.format("% " + m_nvd.getProtTcpIpClientValueMinNrCharToShow() + "." + m_nvd.getProtTcpIpClientValueNrOfDecimal() + "f %s", (double)shValue, m_nvd.getProtTcpIpClientValueUM());
                            }
                        }
                        if(m_nvd.getProtTcpIpClientValueDataType() == NumericValueData.DataType.INT32.getID()){
                            if(ticrs.getValue() != null && ticrs.getValue().length == 4) {
                                int iValue = ByteBuffer.wrap(ticrs.getValue()).order(ByteOrder.LITTLE_ENDIAN).getInt();
                                strValue = String.format("% " + m_nvd.getProtTcpIpClientValueMinNrCharToShow() + "." + m_nvd.getProtTcpIpClientValueNrOfDecimal() + "f %s", (double)iValue, m_nvd.getProtTcpIpClientValueUM());
                            }
                        }
                        if(m_nvd.getProtTcpIpClientValueDataType() == NumericValueData.DataType.FLOAT32.getID()){
                            if(ticrs.getValue() != null && ticrs.getValue().length == 4) {
                                float fValue = ByteBuffer.wrap(ticrs.getValue()).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                                strValue = String.format("% " + m_nvd.getProtTcpIpClientValueMinNrCharToShow() + "." + m_nvd.getProtTcpIpClientValueNrOfDecimal() + "f %s", (double)fValue, m_nvd.getProtTcpIpClientValueUM());
                            }
                        }
                        if(m_nvd.getProtTcpIpClientValueDataType() == NumericValueData.DataType.LONG64.getID()){
                            if(ticrs.getValue() != null && ticrs.getValue().length == 8) {
                                long lValue = ByteBuffer.wrap(ticrs.getValue()).order(ByteOrder.LITTLE_ENDIAN).getLong();
                                strValue = String.format("% " + m_nvd.getProtTcpIpClientValueMinNrCharToShow() + "d %s", lValue, m_nvd.getProtTcpIpClientValueUM());
                            }
                        }
                        if(m_nvd.getProtTcpIpClientValueDataType() == NumericValueData.DataType.DOUBLE64.getID()){
                            if(ticrs.getValue() != null && ticrs.getValue().length == 8) {
                                double dValue = ByteBuffer.wrap(ticrs.getValue()).order(ByteOrder.LITTLE_ENDIAN).getDouble();
                                strValue = String.format("% " + m_nvd.getProtTcpIpClientValueMinNrCharToShow() + "." + m_nvd.getProtTcpIpClientValueNrOfDecimal() + "f %s", dValue, m_nvd.getProtTcpIpClientValueUM());
                            }
                        }
                        this.setError(null);
                    } else if(ticrs.getStatus() == TcpIpClientReadStatus.Status.TIMEOUT) {
                        strValue = getTimeoutValue();
                        this.setError("");
                    } else {
                        strValue = getErrorValue(ticrs.getErrorCode());
                        this.setError("");
                    }

                    setText(strValue);
                }
                // Log.d(TAG, this.toString() + ": " + "onModbusStatusCallback() ID: " + ms.getServerID() + " TID: " + ms.getTID() + " Status: " + ms.getStatus().toString());
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
                    if(m_edEditText == null){
                        m_edEditText = new EDEditText(getContext());
                        m_edEditText.setText("Test");
                        ViewParent view = this.getParent();
                        if(view instanceof RelativeLayout){
                            RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams)this.getLayoutParams();
                            m_edEditText.setLayoutParams(rllp);
                            m_edEditText.setSingleLine();
                            m_edEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                            m_edEditText.setOnEditorActionListener(new OnEditorActionListener() {
                                @Override
                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                    boolean handled = false;
                                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                                        aaa.setVisibility(VISIBLE);
                                        ViewParent view = m_edEditText.getParent();
                                        if(view instanceof RelativeLayout){
                                            ((RelativeLayout) view).removeView(m_edEditText);
                                            m_edEditText = null;
                                        }

                                            handled = true;
                                    }
                                    return handled;
                                }
                            });
                            ((RelativeLayout) view).addView(m_edEditText);
                            this.setVisibility(GONE);
                            aaa = this;
                        }
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
