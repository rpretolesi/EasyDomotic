package com.pretolesi.easydomotic.NumerValue;

import android.content.Context;
import android.content.Intent;
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
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientWriteStatus;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientReadStatus;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClient;
import com.pretolesi.easydomotic.TcpIpClient.TciIpClientHelper;

import java.nio.ByteBuffer;

/**
 *
 */
public class NumericValue extends BaseValue implements
        TCPIPClient.TcpIpClientReadValueStatusListener,
        TCPIPClient.TcpIpClientWriteSwitchStatusListener {

    private static final String TAG = "NumericValue";
    private GestureDetectorCompat mDetector;
    private float mLastTouchX;
    private float mLastTouchY;

    private NumericValueData m_nvd;
    private int m_iMsgID;
    private int m_iTIDRead;
    private int m_iTIDWrite;

    private NumericEditText m_edEditText;

    public NumericValue(Context context) {
        super(context);
        this.m_nvd = null;
        this.m_iMsgID = -1;
        this.m_iTIDRead = -1;
        this.m_iTIDWrite = -1;
    }

    public NumericValue(Context context, NumericValueData nvd, int iMsgID, boolean bEditMode) {
        super(context);
        if(nvd != null) {
            this.m_nvd = nvd;
            this.m_iMsgID = iMsgID;
            this.m_iTIDRead = m_iMsgID + 1;
            this.m_iTIDWrite = m_iMsgID + 2;
            this.setTag(nvd.getTag());

            setNumericDataType(DataType.getDataType(m_nvd.getProtTcpIpClientValueDataType()));
            setEditMode(bEditMode);
        }
    }

    public NumericValueData getLightSwitchData() {
        return m_nvd;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Set default value
        setText(getDefaultValue());

        // Listener
        if(m_nvd != null){
            if(!getEditMode() && m_nvd.getProtTcpIpClientEnable()) {
                TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_nvd.getProtTcpIpClientID());
                if(tic != null){
                    tic.registerTcpIpClientReadValueStatus(this);
                    tic.registerTcpIpClientWriteSwitchStatus(this);
                }
                setTimer(m_nvd.getProtTcpIpClientValueUpdateMillis());
            }
        }

        // Log.d(TAG, this.toString() + ": " + "onAttachedToWindow()");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if(!getEditMode()) {
            resetTimer();
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

    private void readValue(){
        if(m_nvd != null && m_nvd.getProtTcpIpClientEnable()){
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_nvd.getProtTcpIpClientID());
            if(tic != null){
                tic.readValue(getContext(), m_iTIDRead, m_nvd.getProtTcpIpClientValueID(), m_nvd.getProtTcpIpClientValueAddress(), getNumericDataType());
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
                        NumericEditText.DataType dtDataType = getNumericDataType();
                        if(dtDataType != null){
                            switch (dtDataType) {
                                case SHORT16:
                                    if(ticrs.getValue() != null && ticrs.getValue().length == 2) {
                                        short shValue = ByteBuffer.wrap(ticrs.getValue()).getShort();
                                        strValue = String.format("% " + m_nvd.getProtTcpIpClientValueMinNrCharToShow() + "." + m_nvd.getProtTcpIpClientValueNrOfDecimal() + "f %s", (double)shValue/Math.pow(10,m_nvd.getProtTcpIpClientValueNrOfDecimal()), m_nvd.getProtTcpIpClientValueUM());
                                    }
                                    break;

                                case INT32:
                                    if(ticrs.getValue() != null && ticrs.getValue().length == 4) {
                                        int iValue = ByteBuffer.wrap(ticrs.getValue()).getInt();
                                        strValue = String.format("% " + m_nvd.getProtTcpIpClientValueMinNrCharToShow() + "." + m_nvd.getProtTcpIpClientValueNrOfDecimal() + "f %s", (double)iValue/Math.pow(10,m_nvd.getProtTcpIpClientValueNrOfDecimal()), m_nvd.getProtTcpIpClientValueUM());
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
                // Log.d(TAG, this.toString() + ": " + "onModbusStatusCallback() ID: " + ms.getServerID() + " TID: " + ms.getTID() + " Status: " + ms.getStatus().toString());
            }
        }
    }

    @Override
    protected synchronized void OnWriteInputField(String strValue) {
        super.OnWriteInputField(strValue);
        if(m_nvd != null && m_nvd.getProtTcpIpClientEnable()) {
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_nvd.getProtTcpIpClientID());
            if (tic != null) {
                tic.writeValue(getContext(), m_iTIDWrite, m_nvd.getProtTcpIpClientValueID(), m_nvd.getProtTcpIpClientValueAddress(), getNumericDataType(), strValue);
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
    protected void onTouchActionUp(boolean bEditMode){
        super.onTouchActionUp(bEditMode);
        if(m_nvd != null) {
            if(bEditMode) {
                m_nvd.setSaved(false);
                m_nvd.setPosX((int)getX());
                m_nvd.setPosY((int)getY());
                Intent intent = NumericValuePropActivity.makeNumericValuePropActivity(this.getContext(), m_nvd);
                this.getContext().startActivity(intent);
            } else {
                if(!m_nvd.getProtTcpIpClientValueReadOnly()){
                    openInputField();
                }
            }
        }
    }

    @Override
    protected void onTimer(){
        super.onTimer();
        // Read
        readValue();
    }

}
