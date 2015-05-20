package com.pretolesi.easydomotic.SensorValue;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.pretolesi.easydomotic.BaseValue.BaseValue;
import com.pretolesi.easydomotic.BaseValue.BaseValueData;
import com.pretolesi.easydomotic.CustomControls.NumericDataType.DataType;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClient;
import com.pretolesi.easydomotic.TcpIpClient.TciIpClientHelper;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientWriteStatus;

import java.util.List;

/**
 *
 */
public class SensorValue extends BaseValue implements
        TCPIPClient.TcpIpClientWriteStatusListener,
        SensorEventListener {

    private static final String TAG = "SensorValue";

    private BaseValueData m_bvd;
    private int m_iMsgID;
    private int m_iTIDWrite;

    // Sensors & SensorManager
    private SensorManager m_SensorManager;
    private Sensor m_Sensor;
    private int m_iSensorType;

    // Storage for Sensor readings
    private float[] m_SensorValueNow;
    private float[] m_SensorValueFiltered;
    private float[] m_SensorValueOut;

    private long m_lTimeLast;

    // Rotation around the Z axis
//    private double m_RotationInDegress;


    public SensorValue(Context context) {
        super(context);
        this.m_bvd = null;
        this.m_iMsgID = -1;
        this.m_iTIDWrite = -1;

        // Sensors & SensorManager
        m_SensorManager = null;
        m_Sensor = null;
        m_iSensorType = 0;
        m_SensorValueNow = null;
        m_SensorValueFiltered = null;
        m_SensorValueOut = null;
    }

    public SensorValue(Context context, BaseValueData bvd, int iMsgID, boolean bEditMode) {
        super(context);
        if(bvd != null) {
            this.m_bvd = bvd;
            this.m_iMsgID = iMsgID;
            this.m_iTIDWrite = m_iMsgID + 1;
            this.setTag(bvd.getTag());

            setNumericDataType(DataType.getDataType(m_bvd.getProtTcpIpClientValueDataType()));
            setEditMode(bEditMode);
        }
        // Sensors & SensorManager
        m_SensorManager = null;
        m_Sensor = null;
        m_iSensorType = 0;
        m_SensorValueNow = null;
        m_SensorValueFiltered = null;
        m_SensorValueOut = null;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
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

        // Sensor
        boolean bSensorOk = false;
        if(m_bvd != null && !m_bvd.getSensorEnableSimulation()) {
            // Get reference to SensorManager
            m_SensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            if (m_SensorManager != null) {
                // Get reference to Sensor
                List<Sensor> ls = m_SensorManager.getSensorList(Sensor.TYPE_ALL);
                if(ls != null){
                    m_iSensorType = ls.get((int) m_bvd.getSensorTypeID()).getType();
                    if(m_iSensorType > 0){
                        m_Sensor = m_SensorManager.getDefaultSensor(m_iSensorType);
                        if (m_Sensor != null) {
                            // Create array for value
                            m_SensorValueNow = new float[6];
                            m_SensorValueFiltered = new float[6];
                            m_SensorValueOut = new float[6];

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

        // Log.d(TAG, this.toString() + ": " + "onDetachedFromWindow()");
    }

    @Override
    protected synchronized void OnWriteInputField(String strValue){
        super.OnWriteInputField(strValue);
        if(m_bvd != null){
            DataType dtDataType = DataType.getDataType(m_bvd.getProtTcpIpClientValueDataType());
            if(dtDataType != null){
                switch (dtDataType) {
                    case SHORT:
                        int iValue;
                        try {
                            iValue = Integer.parseInt(strValue);
                            // Write Request
                            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
                            if(tic != null){
                                tic.writeShort(getContext(), m_iTIDWrite, m_bvd.getProtTcpIpClientValueID(), m_bvd.getProtTcpIpClientValueAddress(), iValue);
                            }

                            return;

                        } catch (Exception ignored) {
                        }
                        break;

                    case INT:
                        long lValue;
                        try {
                            lValue = Long.parseLong(strValue);
                            // Write Request
                            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
                            if(tic != null){
                                tic.writeInteger(getContext(), m_iTIDWrite, m_bvd.getProtTcpIpClientValueID(), m_bvd.getProtTcpIpClientValueAddress(), lValue);
                            }

                            return;

                        } catch (Exception ignored) {
                        }
                        break;

                    case LONG:
                        long lValue64;
                        try {
                            lValue64 = Long.parseLong(strValue);
                            // Write Request
                            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
                            if(tic != null){
                                tic.writeLong(getContext(), m_iTIDWrite, m_bvd.getProtTcpIpClientValueID(), m_bvd.getProtTcpIpClientValueAddress(), lValue64);
                            }

                            return;

                        } catch (Exception ignored) {
                        }
                        break;

                    case FLOAT:
                        float fValue;
                        try {
                            fValue = Float.parseFloat(strValue);
                            // Write Request
                            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
                            if(tic != null){
                                tic.writeFloat(getContext(), m_iTIDWrite, m_bvd.getProtTcpIpClientValueID(), m_bvd.getProtTcpIpClientValueAddress(), fValue);
                            }

                            return;

                        } catch (Exception ignored) {
                        }
                        break;

                    case DOUBLE:
                        double dblValue;
                        try {
                            dblValue = Double.parseDouble(strValue);
                            // Write Request
                            TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_bvd.getProtTcpIpClientID());
                            if(tic != null){
                                tic.writeDouble(getContext(), m_iTIDWrite, m_bvd.getProtTcpIpClientValueID(), m_bvd.getProtTcpIpClientValueAddress(), dblValue);
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
                        // Write Ok, i can close the Input
                        closeInputField();                    }
                } else {
                    setErrorInputField(true);
                }
            }
        }
    }
fare la scrittura e verificare se e' il caso di visualizzare il valore in edit. provare ance la simulazione.'
    // Sensor
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(m_SensorValueOut != null && m_bvd != null){
            if(event.sensor.getType() == m_iSensorType){
                System.arraycopy(event.values, 0, m_SensorValueNow, 0, event.values.length);
                long lTimeNow = System.currentTimeMillis();
                if (lTimeNow - m_lTimeLast > m_bvd.getSensorSampleTime()) {
                    m_lTimeLast = lTimeNow;

                    // Apply low-pass filter
                    for(int i = 0; i < event.values.length; i++){
                        m_SensorValueFiltered[i] = lowPass(m_SensorValueNow[i], m_SensorValueFiltered[i], m_bvd.getSensorLowPassFilterK());
                        m_SensorValueOut[i] = m_SensorValueFiltered[i] * m_bvd.getSensorAmplK();
                    }
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
        if(m_bvd != null) {
            // Write Sensor Data
        }
    }

    // Deemphasize transient forces
    static float lowPass(float current, float last, float alpha) {
        return last * alpha + current * ((float)1.0 - alpha);
    }
}
