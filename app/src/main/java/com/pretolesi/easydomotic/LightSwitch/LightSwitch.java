package com.pretolesi.easydomotic.LightSwitch;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.pretolesi.easydomotic.BaseFragment;
import com.pretolesi.easydomotic.CustomException.ModbusAddressOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusTransIdOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusUnitIdOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusValueOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.Modbus;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClient;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClientData;
import com.pretolesi.easydomotic.TcpIpClient.TciIpClientHelper;

/**
 *
 */
public class LightSwitch extends Switch implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        ToggleButton.OnCheckedChangeListener,
        Modbus.ModbusListener {

    private static final String TAG = "LightSwitch";
    private GestureDetectorCompat mDetector;

    private LightSwitchData m_lsd;

    private float mLastTouchX;
    private float mLastTouchY;

    private boolean m_bEditMode;

    public LightSwitch(Context context) {
        super(context);
        this.m_lsd = null;
        this.m_bEditMode = false;
    }

    public LightSwitch(Context context, LightSwitchData lsd, boolean bEditMode) {
        super(context);
        if(lsd != null) {
            this.m_lsd = lsd;
            this.setTag(lsd.getTag());
            this.setId((int)lsd.getID() + lsd.getProtTcpIpClientValueAddress());
        }
        this.m_bEditMode = bEditMode;

    }
    public LightSwitchData getLightSwitchData() {
        return m_lsd;
    }

    /*
     * Begin
     * Timer variable and function
     */
    Handler m_TimerHandler;
    public void setTimerHandler() {
        if(m_lsd != null) {
            m_TimerHandler = new Handler();
            m_TimerHandler.postDelayed(m_TimerRunnable, m_lsd.getProtTcpIpClientValueUpdateMillis());
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
            if(m_lsd != null && m_TimerHandler != null) {
                if(!m_lsd.getProtTcpIpClientSendDataOnChange()){
                    Switch s = (Switch)findViewById(getId());
                    if(s != null){
                        if(s.isChecked()){
                            sendRequest(m_lsd.getProtTcpIpClientValueON());
                        } else {
                            sendRequest(m_lsd.getProtTcpIpClientValueOFF());
                        }
                    }

                    m_TimerHandler.postDelayed(m_TimerRunnable, m_lsd.getProtTcpIpClientValueUpdateMillis());
                }
            }
        }
    };
    /*
     * End
     * Timer variable and function
     */
/*
    private TCPIPClient getTcpIpClient(){
        if(m_lsd != null){
            TciIpClientHelper tich = TciIpClientHelper.getInstance();
            if(tich != null){
                List<TCPIPClient> ltic = tich.getTciIpClient();
                if(ltic != null && !ltic.isEmpty()){
                    for(TCPIPClient tic : ltic){
                        if (tic != null && (tic.getID() == m_lsd.getID())){
                            return tic;
                        }
                    }
                }
            }
        }

        return null;
    }
*/
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
/*
        TCPIPClient tic = getTcpIpClient();
        if(tic != null){
            tic.registerListener(this);
        }
*/
        Modbus.registerListener(this);

        if(!m_bEditMode) {
            setTimerHandler();
        }

        Log.d(TAG, this.toString() + ": " + "onAttachedToWindow()");

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(!m_bEditMode) {
            resetTimerHandler();
        }

        Modbus.unregisterListener(this);
/*
        TCPIPClient tic = getTcpIpClient();
        if(tic != null){
            tic.unregisterListener(this);
        }
*/
        Log.d(TAG, this.toString() + ": " + "onDetachedFromWindow()");
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(m_lsd != null) {
            if(isChecked){
                sendRequest(m_lsd.getProtTcpIpClientValueON());
            } else {
                sendRequest(m_lsd.getProtTcpIpClientValueOFF());
            }
        }
    }

    private void sendRequest(int iStatusValue){
        TciIpClientHelper tich = TciIpClientHelper.getInstance();
        if(m_lsd != null && tich != null){
            TCPIPClient tic = tich.getTciIpClient(m_lsd.getProtTcpIpClientID());
            if(tic != null){
                byte[] byteToSend = null;
                if(tic.getProtocolID() == TCPIPClientData.Protocol.MODBUS_ON_TCP_IP.getID()){
                    try {
                        byteToSend = Modbus.writeSingleRegister(this.getContext(), getId(), 0,  m_lsd.getProtTcpIpClientValueAddress(), iStatusValue);
                    } catch (ModbusTransIdOutOfRangeException ignored) {
                    } catch (ModbusUnitIdOutOfRangeException ignored) {
                    } catch (ModbusAddressOutOfRangeException ignored) {
                    } catch (ModbusValueOutOfRangeException ignored) {
                    }
                }
                if(byteToSend != null) {
                    tic.sendMessage(byteToSend);
                }
            }
        }
    }


    @Override
    public void onWriteSingleRegisterCompletedCallback(int iTransactionIdentifier, int iFC, int iAddress, int iValue) {

    }

    @Override
    public void onWriteSingleRegisterExceptionCallback(int iTransactionIdentifier, int iEC, int iExC) {

    }

    @Override
    public void onTcpIpServerModbusOperationTimeoutCallback() {

    }

    @Override
    public void onTcpIpServerModbusStatusCallback(TCPIPClient.Status tics) {

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
                    if(m_lsd != null) {
                        m_lsd.setSaved(false);
                        Intent intent = LightSwitchPropActivity.makeLightSwitchPropActivity(this.getContext(), m_lsd);
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
        if(m_lsd != null) {
            m_lsd.setSaved(false);
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

        if(m_lsd != null) {
            BaseFragment.setViewPosition(this, (int) dx, (int) dy);
            m_lsd.setPosX((int)dx);
            m_lsd.setPosY((int)dy);
        }

//        Log.d(TAG, this.toString() + ": " + "onTouchEvent: ACTION_MOVE dx/dy: " + dx + "/" + dy + ", mLastTouchX/mLastTouchY: " + mLastTouchX + "/" + mLastTouchY + ", x/y: " + x + "/" + y);

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
/*
        if(m_lsd != null) {
            Intent intent = LightSwitchPropActivity.makeLightSwitchPropActivity(this.getContext(), m_lsd);
            this.getContext().startActivity(intent);
        }
*/
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        return false;
    }

}
