package com.pretolesi.easydomotic.NumericValue;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;

import com.pretolesi.easydomotic.BaseValue.BaseValue;
import com.pretolesi.easydomotic.BaseValue.BaseValueData;
import com.pretolesi.easydomotic.CommClientData.BaseCommClient;
import com.pretolesi.easydomotic.CustomControls.NumericDataType.DataType;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientWriteStatus;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientReadStatus;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClient;
import com.pretolesi.easydomotic.TcpIpClient.CommClientHelper;

/**
 *
 */
public class NumericValue extends BaseValue implements
        TCPIPClient.TcpIpClientReadValueStatusListener,
        TCPIPClient.TcpIpClientWriteStatusListener {

    private static final String TAG = "NumericValue";
    private GestureDetectorCompat mDetector;

    private BaseValueData m_bvd;
    private int m_iMsgID;
    private int m_iTIDRead;
    private int m_iTIDWrite;

    public NumericValue(Context context) {
        this(context,  null, -1, false);
    }

    public NumericValue(Context context,  BaseValueData bvd, int iMsgID, boolean bEditMode) {
        super(context);
        if(bvd != null) {
            this.m_bvd = bvd;
            this.m_iMsgID = iMsgID;
            this.m_iTIDRead = m_iMsgID + 1;
            this.m_iTIDWrite = m_iMsgID + 2;
            this.setTag(bvd.getTag());

            setNumericDataType(DataType.getDataType(m_bvd.getProtTcpIpClientValueDataType()));
            setEditMode(bEditMode);
            setVertical(bvd.getVertical());
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Set default value
        setText(getDefaultValue());

        // Listener
        if(m_bvd != null){
            if(!getEditMode() && m_bvd.getProtTcpIpClientEnable()) {
                BaseCommClient bcc = CommClientHelper.getBaseCommClient(m_bvd.getProtTcpIpClientID());
                if(bcc != null){
                    bcc.registerTcpIpClientReadValueStatus(this);
                    bcc.registerTcpIpClientWriteSwitchStatus(this);
                }
                setTimer(m_bvd.getValueUpdateMillis());
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
        if(m_bvd != null){
            BaseCommClient bcc = CommClientHelper.getBaseCommClient(m_bvd.getProtTcpIpClientID());
            if(bcc != null){
                bcc.unregisterTcpIpClientReadValueStatus(this);
                bcc.unregisterTcpIpClientWriteSwitchStatus(this);
            }
        }

        // Log.d(TAG, this.toString() + ": " + "onDetachedFromWindow()");
    }

     private synchronized void readValue(){
        if(m_bvd != null && m_bvd.getProtTcpIpClientEnable()){
            BaseCommClient bcc = CommClientHelper.getBaseCommClient(m_bvd.getProtTcpIpClientID());
            if(bcc != null){
                bcc.readValue(getContext(), m_iTIDRead, m_bvd.getProtTcpIpClientValueID(), m_bvd.getProtTcpIpClientValueAddress(), getNumericDataType());
            }
        }
    }

    private String getDefaultValue(){
        String strDefaultValue = "";

        if(m_bvd != null){
            for(int iIndice = m_bvd.getValueMinNrCharToShow() + m_bvd.getValueNrOfDecimal(); iIndice > 0; iIndice--){
                if(iIndice == m_bvd.getValueNrOfDecimal()){
                    strDefaultValue = strDefaultValue + ".";
                }
                strDefaultValue = strDefaultValue + "#";
            }
            if(m_bvd.getValueUM() != null && !m_bvd.getValueUM().equals("")){
                strDefaultValue = strDefaultValue + " " + m_bvd.getValueUM();
            }
        } else {
            strDefaultValue = BaseValueData.ValueDefaulValue;
        }

        return strDefaultValue;
    }

    private String getEmptyValue(){
        String strDefaultValue = "";

        if(m_bvd != null){
            for(int iIndice = m_bvd.getValueMinNrCharToShow() + m_bvd.getValueNrOfDecimal(); iIndice > 0; iIndice--){
                if(iIndice == m_bvd.getValueNrOfDecimal()){
                    strDefaultValue = strDefaultValue + " ";
                }
                strDefaultValue = strDefaultValue + " ";
            }
            if(m_bvd.getValueUM() != null && !m_bvd.getValueUM().equals("")){
                strDefaultValue = strDefaultValue + " " + m_bvd.getValueUM();
            }
        } else {
            strDefaultValue = BaseValueData.ValueDefaulValue;
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
        if(ticrs != null && m_bvd != null){
            if(ticrs.getServerID() == m_bvd.getProtTcpIpClientID()){
                if(ticrs.getTID() == m_iTIDRead) {
                    Object obj = null;
                    String strValue = getDefaultValue();
                    if(ticrs.getStatus() == TcpIpClientReadStatus.Status.OK) {
                        if (ticrs.getValue() != null) {
                            if(ticrs.getValue() instanceof Short){
                                Short sh = (Short)ticrs.getValue();
                                strValue = String.format("%d %s", sh, m_bvd.getValueUM());
                                this.setError(null);
                            }
                            if(ticrs.getValue() instanceof Integer){
                                Integer i = (Integer)ticrs.getValue();
                                strValue = String.format("%d %s", i, m_bvd.getValueUM());
                                this.setError(null);
                            }
                            if(ticrs.getValue() instanceof Long){
                                Long l = (Long)ticrs.getValue();
                                strValue = String.format("%d %s", l, m_bvd.getValueUM());
                                this.setError(null);
                            }

                            if(ticrs.getValue() instanceof Float){
                                Float f = (Float)ticrs.getValue();
                                if(m_bvd.getValueMinNrCharToShow() > 0){
                                    strValue = String.format("% " + m_bvd.getValueMinNrCharToShow() + "." + m_bvd.getValueNrOfDecimal() + "f %s", f, m_bvd.getValueUM());
                                } else {
                                    strValue = String.format("%." + m_bvd.getValueNrOfDecimal() + "f %s", f, m_bvd.getValueUM());
                                }

                                this.setError(null);
                            }

                            if(ticrs.getValue() instanceof Double){
                                Double dbl = (Double)ticrs.getValue();
                                if(m_bvd.getValueMinNrCharToShow() > 0){
                                    strValue = String.format("% " + m_bvd.getValueMinNrCharToShow() + "." + m_bvd.getValueNrOfDecimal() + "f %s", dbl, m_bvd.getValueUM());
                                } else {
                                    strValue = String.format("%." + m_bvd.getValueNrOfDecimal() + "f %s", dbl, m_bvd.getValueUM());
                                }
                                this.setError(null);
                            }
                        } else {
                            this.requestFocus();
                            this.setError(ticrs.getErrorMessage());
                        }
                    } else if(ticrs.getStatus() == TcpIpClientReadStatus.Status.TIMEOUT) {
                        this.requestFocus();
                        this.setError(ticrs.getErrorMessage());
//                        strValue = getTimeoutValue();
//                        this.setError("");
                    } else {
                        this.requestFocus();
                        this.setError(ticrs.getErrorMessage());
//                        strValue = getErrorValue(ticrs.getErrorCode());
//                        this.setError("");
                    }

                    setText(strValue);
                }
            }
        }
    }

    @Override
    protected void OnWriteInputField(String strValue) {
        super.OnWriteInputField(strValue);
        if(m_bvd != null && m_bvd.getProtTcpIpClientEnable()) {
            BaseCommClient bcc = CommClientHelper.getBaseCommClient(m_bvd.getProtTcpIpClientID());
            if (bcc != null) {
                bcc.writeValue(getContext(), m_iTIDWrite, m_bvd.getProtTcpIpClientValueID(), m_bvd.getProtTcpIpClientValueAddress(), getNumericDataType(), strValue);
            }
        }
        // Read the value written...
        readValue();
    }

    @Override
    public void onWriteValueStatusCallback(TcpIpClientWriteStatus ticws) {
        if(ticws != null && m_bvd != null){
            if(ticws.getServerID() == m_bvd.getProtTcpIpClientID()){
                if(ticws.getTID() == m_iTIDWrite) {
                    if(ticws.getStatus() == TcpIpClientWriteStatus.Status.OK){
                        // Write Ok, i can close the Input
                        setErrorInputField("");
                        closeInputField();
                    }
                } else {
                    setErrorInputField(ticws.getErrorMessage());
                }
            }
        }
    }

    @Override
    protected void onTouchActionUp(boolean bEditMode){
        super.onTouchActionUp(bEditMode);
        if(m_bvd != null) {
            if(bEditMode) {
                m_bvd.setSaved(false);
                m_bvd.setPosX((int)getX());
                m_bvd.setPosY((int)getY());
                Intent intent = NumericValuePropActivity.makeBaseValuePropActivityByValueData(this.getContext(), NumericValuePropActivity.class, m_bvd);
                this.getContext().startActivity(intent);
            } else {
                if(!m_bvd.getValueReadOnly()){
                    openInputField(getEmptyValue());
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
