package com.pretolesi.easydomotic.NumerValue;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;

import com.pretolesi.easydomotic.BaseValue.BaseValue;
import com.pretolesi.easydomotic.BaseValue.BaseValueData;
import com.pretolesi.easydomotic.CustomControls.NumericDataType.DataType;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientWriteStatus;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientReadStatus;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClient;
import com.pretolesi.easydomotic.TcpIpClient.TciIpClientHelper;

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
        super(context);
        this.m_bvd = null;
        this.m_iMsgID = -1;
        this.m_iTIDRead = -1;
        this.m_iTIDWrite = -1;
    }

    public NumericValue(Context context, BaseValueData bvd, int iMsgID, boolean bEditMode) {
        super(context);
        if(bvd != null) {
            this.m_bvd = bvd;
            this.m_iMsgID = iMsgID;
            this.m_iTIDRead = m_iMsgID + 1;
            this.m_iTIDWrite = m_iMsgID + 2;
            this.setTag(bvd.getTag());

            setNumericDataType(DataType.getDataType(m_bvd.getProtTcpIpClientValueDataType()));
            setEditMode(bEditMode);
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
                TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
                if(tic != null){
                    tic.registerTcpIpClientReadValueStatus(this);
                    tic.registerTcpIpClientWriteSwitchStatus(this);
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
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
            if(tic != null){
                tic.unregisterTcpIpClientReadValueStatus(this);
                tic.unregisterTcpIpClientWriteSwitchStatus(this);
            }
        }

        // Log.d(TAG, this.toString() + ": " + "onDetachedFromWindow()");
    }

    private void readValue(){
        if(m_bvd != null && m_bvd.getProtTcpIpClientEnable()){
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
            if(tic != null){
                tic.readValue(getContext(), m_iTIDRead, m_bvd.getProtTcpIpClientValueID(), m_bvd.getProtTcpIpClientValueAddress(), getNumericDataType());
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
                    String strValue = "";
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
                                strValue = String.format("% " + m_bvd.getValueMinNrCharToShow() + ".f %s", f, m_bvd.getValueUM());
                                this.setError(null);
                            }
                            if(ticrs.getValue() instanceof Float || ticrs.getValue() instanceof Double){
                                Double dbl = (Double)ticrs.getValue();
                                strValue = String.format("% " + m_bvd.getValueMinNrCharToShow() + ".f %s", dbl, m_bvd.getValueUM());
                                this.setError(null);
                            }
                        } else {
                            strValue = getErrorValue(ticrs.getErrorCode());
                            this.setError("");
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
            }
        }
    }

    @Override
    protected synchronized void OnWriteInputField(String strValue) {
        super.OnWriteInputField(strValue);
        if(m_bvd != null && m_bvd.getProtTcpIpClientEnable()) {
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
            if (tic != null) {
                tic.writeValue(getContext(), m_iTIDWrite, m_bvd.getProtTcpIpClientValueID(), m_bvd.getProtTcpIpClientValueAddress(), getNumericDataType(), strValue);
            }
        }
    }

    @Override
    public void onWriteValueStatusCallback(TcpIpClientWriteStatus ticws) {
        if(ticws != null && m_bvd != null){
            if(ticws.getServerID() == m_bvd.getProtTcpIpClientID()){
                if(ticws.getTID() == m_iTIDWrite) {
                    if(ticws.getStatus() == TcpIpClientWriteStatus.Status.OK){
                        // Write Ok, i can close the Input
                        setErrorInputField(false);
                        closeInputField();
                    }
                } else {
                    setErrorInputField(true);
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
