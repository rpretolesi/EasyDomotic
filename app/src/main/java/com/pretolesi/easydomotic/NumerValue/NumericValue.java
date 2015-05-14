package com.pretolesi.easydomotic.NumerValue;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;

import com.pretolesi.easydomotic.BaseValue.BaseValue;
import com.pretolesi.easydomotic.CustomControls.NumericEditText;
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

    private NumericValueData m_nvd;
    private int m_iMsgID;
    private int m_iTIDRead;
    private int m_iTIDWrite;

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
                    Object obj = null;
                    String strValue = "";
                    if(ticrs.getStatus() == TcpIpClientReadStatus.Status.OK) {
                        if (ticrs.getValue() != null) {
                            if(ticrs.getValue() instanceof Short){
                                Short sh = (Short)ticrs.getValue();
                                strValue = String.format("%d %s", sh, m_nvd.getProtTcpIpClientValueUM());
                                this.setError(null);
                            }
                            if(ticrs.getValue() instanceof Integer){
                                Integer i = (Integer)ticrs.getValue();
                                strValue = String.format("%d %s", i, m_nvd.getProtTcpIpClientValueUM());
                                this.setError(null);
                            }
                            if(ticrs.getValue() instanceof Long){
                                Long l = (Long)ticrs.getValue();
                                strValue = String.format("%d %s", l, m_nvd.getProtTcpIpClientValueUM());
                                this.setError(null);
                            }
                            if(ticrs.getValue() instanceof Float){
                                Float f = (Float)ticrs.getValue();
                                strValue = String.format("% " + m_nvd.getProtTcpIpClientValueMinNrCharToShow() + ".f %s", f, m_nvd.getProtTcpIpClientValueUM());
                                this.setError(null);
                            }
                            if(ticrs.getValue() instanceof Float || ticrs.getValue() instanceof Double){
                                Double dbl = (Double)ticrs.getValue();
                                strValue = String.format("% " + m_nvd.getProtTcpIpClientValueMinNrCharToShow() + ".f %s", dbl, m_nvd.getProtTcpIpClientValueUM());
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
        if(m_nvd != null && m_nvd.getProtTcpIpClientEnable()) {
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_nvd.getProtTcpIpClientID());
            if (tic != null) {
                tic.writeValue(getContext(), m_iTIDWrite, m_nvd.getProtTcpIpClientValueID(), m_nvd.getProtTcpIpClientValueAddress(), getNumericDataType(), strValue);
            }
        }
    }

    @Override
    public void onWriteValueStatusCallback(TcpIpClientWriteStatus ticws) {
        if(ticws != null && m_nvd != null){
            if(ticws.getServerID() == m_nvd.getProtTcpIpClientID()){
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
