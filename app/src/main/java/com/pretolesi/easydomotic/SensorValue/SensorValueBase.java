package com.pretolesi.easydomotic.SensorValue;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.pretolesi.easydomotic.BaseValue.BaseValue;
import com.pretolesi.easydomotic.BaseValue.BaseValueData;
import com.pretolesi.easydomotic.BaseValue.BaseValueData.SensorTypeCalibr;
import com.pretolesi.easydomotic.CustomControls.NumericDataType.DataType;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClient;
import com.pretolesi.easydomotic.TcpIpClient.TciIpClientHelper;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientWriteStatus;

import java.util.List;

/**
 *
 */
public class SensorValueBase extends BaseValue implements
        TCPIPClient.TcpIpClientWriteStatusListener,
        SensorEventListener {

    private static final String TAG = "SensorValueBase";

    protected BaseValueData m_bvd;
    private int m_iMsgID;
    private int m_iTIDWrite;

    // Storage for Sensor readings
    private float[] m_SensorValueFiltered = null;
    private float[] m_SensorValueOut = null;

    private long m_lTimeLast;

    public SensorValueBase(Context context) {
        super(context);
        this.m_bvd = null;
        this.m_iMsgID = -1;
        this.m_iTIDWrite = -1;
    }

    public SensorValueBase(Context context, BaseValueData bvd, int iMsgID, boolean bEditMode) {
        super(context);
        if(bvd != null) {
            this.m_bvd = bvd;
            this.m_iMsgID = iMsgID;
            this.m_iTIDWrite = m_iMsgID + 1;
            this.setTag(bvd.getTag());

            setNumericDataType(DataType.getDataType(m_bvd.getProtTcpIpClientValueDataType()));
            setEditMode(bEditMode);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Create array for value
        m_SensorValueFiltered = new float[6];
        m_SensorValueOut = new float[6];

        // Set default value
        setText(getDefaultValue());

        // Listener
        if(m_bvd != null){
            if(!getEditMode()) {
                TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
                if(tic != null){
                    tic.registerTcpIpClientWriteSwitchStatus(this);
                }
                if(!m_bvd.getSensorEnableSimulation()) {
                    setTimer(m_bvd.getValueUpdateMillis());
                }
            }
        }
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
                tic.unregisterTcpIpClientWriteSwitchStatus(this);
            }
        }

        // Create array for value
        m_SensorValueFiltered = null;
        m_SensorValueOut = null;
    }

    @Override
    protected synchronized void OnWriteInputField(String strValue){
        super.OnWriteInputField(strValue);
        WriteInputField(strValue);
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
    public void onWriteValueStatusCallback(TcpIpClientWriteStatus ticws) {
        if(ticws != null && m_bvd != null){
            if(ticws.getServerID() == m_bvd.getProtTcpIpClientID()){
                if(ticws.getTID() == m_iTIDWrite) {
                    if(ticws.getStatus() == TcpIpClientWriteStatus.Status.OK){
                        if(m_bvd.getSensorEnableSimulation()){
                            // Write Ok, i can close the Input
                            closeInputField();
                        } else {
                            setError(null);
                        }
                    } else {
                        if(m_bvd.getSensorEnableSimulation()){
                            // Write Ok, i can close the Input
                            setErrorInputField(true);
                        } else {
                            setError("");
                        }
                    }
                }
            }
        }
    }

    // Sensor
    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onTouchActionUp(boolean bEditMode){
        super.onTouchActionUp(bEditMode);
        if(m_bvd != null) {
            if(bEditMode) {
                m_bvd.setSaved(false);
                m_bvd.setPosX((int)getX());
                m_bvd.setPosY((int)getY());
                Intent intent = SensorValuePropActivity.makeBaseValuePropActivityByValueData(this.getContext(), SensorValuePropActivity.class, m_bvd);
                this.getContext().startActivity(intent);
            } else {
                if(m_bvd.getSensorEnableSimulation()){
                    openInputField();
                }
            }
        }
    }

    @Override
    protected void onTimer(){
        super.onTimer();
        if(m_bvd != null && !m_bvd.getSensorEnableSimulation()) {
            // Write Sensor Data
//            String strData = Float.toString(m_SensorValueOut[(int) m_bvd.getSensorValueID()]);
            String strData = convertFormatValueToString(getNumericDataType(), m_SensorValueOut[(int) m_bvd.getSensorValueID()]);
            WriteInputField(strData);
        }
    }

    protected synchronized void WriteInputField(String strValue){
        if(m_bvd != null && m_bvd.getProtTcpIpClientEnable()) {
            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
            if (tic != null) {
                tic.writeValue(getContext(), m_iTIDWrite, m_bvd.getProtTcpIpClientValueID(), m_bvd.getProtTcpIpClientValueAddress(), getNumericDataType(), strValue);
            }
        }
    }

    protected void setOutputFilter(float[] afSensorValue ){
        long lTimeNow = System.currentTimeMillis();
        if(afSensorValue != null && m_bvd != null && m_SensorValueFiltered != null && m_SensorValueOut!= null) {
            if (lTimeNow - m_lTimeLast > m_bvd.getSensorSampleTimeMillis()) {
                m_lTimeLast = lTimeNow;

                // Apply low-pass filter
                for (int i = 0; i < afSensorValue.length; i++) {
                    m_SensorValueFiltered[i] = lowPass(afSensorValue[i], m_SensorValueFiltered[i], m_bvd.getSensorLowPassFilterK());
                    m_SensorValueOut[i] = m_SensorValueFiltered[i] * m_bvd.getSensorAmplK();
                }
                // Show Sensor Data
                String strValue = "";
                if(m_bvd.getValueMinNrCharToShow() > 0){
                    strValue = String.format("% " + m_bvd.getValueMinNrCharToShow() + "." + m_bvd.getValueNrOfDecimal() + "f %s", (double) m_SensorValueOut[(int) m_bvd.getSensorValueID()], m_bvd.getValueUM());
                } else {
                    strValue = String.format("%." + m_bvd.getValueNrOfDecimal() + "f %s", (double) m_SensorValueOut[(int) m_bvd.getSensorValueID()], m_bvd.getValueUM());
                }
                setText(strValue);
            }
        }
    }


    private String convertFormatValueToString(DataType dtDataType, double dblValueToConvert){
        String strValue = "0";
        if(dtDataType != null){
            long lValue = 0;
            try{
                lValue = Math.round(dblValueToConvert);
            } catch (Exception ignore){
            }

            switch (dtDataType) {
                case SHORT:
                    try {
                        strValue = Integer.toString((int)lValue);

                    } catch (Exception ignore) {
                    }
                    break;

                case INT:
                    try {
                        strValue = Integer.toString((int)lValue);
                    } catch (Exception ignore) {
                    }
                    break;

                case LONG:
                    try {
                        strValue = Long.toString(lValue);
                    } catch (Exception ignore) {
                    }
                    break;

                case FLOAT:
                    try {
                        strValue = Float.toString((float) dblValueToConvert);
                    } catch (Exception ignore) {
                    }
                    break;

                case DOUBLE:
                    try {
                        strValue = Double.toString(dblValueToConvert);
                    } catch (Exception ignore) {
                    }
                    break;

            }
        }

        return strValue;
    }

    // Deemphasize transient forces
    public static float lowPass(float current, float last, float alpha) {
        return last * alpha + current * ((float)1.0 - alpha);
    }
}
