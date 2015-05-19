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

    private com.pretolesi.easydomotic.SensorValue.SensorValueData m_svd;
    private int m_iMsgID;
    private int m_iTIDWrite;

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
        super(context);
        if(svd != null) {
            this.m_svd = svd;
            this.m_iMsgID = iMsgID;
            this.m_iTIDWrite = m_iMsgID + 1;
            this.setTag(svd.getTag());

            setNumericDataType(DataType.getDataType(m_svd.getProtTcpIpClientValueDataType()));
            setEditMode(bEditMode);
        }
        // Sensors & SensorManager
        m_SensorManager = null;
        m_Sensor = null;
        m_iSensorType = 0;
        m_SensorValue = null;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Set default value
        setText(getDefaultValue());

        // Listener
        if(m_svd != null){
            if(!getEditMode()) {
                TCPIPClient tic = TciIpClientHelper.getTciIpClient(m_svd.getProtTcpIpClientID());
                if(tic != null){
                    tic.registerTcpIpClientWriteSwitchStatus(this);
                }
                if(!m_svd.getSensorEnableSimulation() && !m_svd.getProtTcpIpClientSendDataOnChange()) {
                    setTimer(m_svd.getProtTcpIpClientValueUpdateMillis());
                }
            }
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

        if(!getEditMode()) {
            resetTimer();
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
    protected synchronized void OnWriteInputField(String strValue){
        super.OnWriteInputField(strValue);
        if(m_svd != null){
            DataType dtDataType = DataType.getDataType(m_svd.getProtTcpIpClientValueDataType());
            if(dtDataType != null){
                switch (dtDataType) {
                    case SHORT:
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

                    case INT:
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

                    case LONG:
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

                    case FLOAT:
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

                    case DOUBLE:
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
        if(ticws != null && m_svd != null){
            if(ticws.getServerID() == m_svd.getProtTcpIpClientID()){
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
                    openInputField();
                }
            }
        }
    }

    @Override
    protected void onTimer(){
        super.onTimer();
        if(m_svd != null && !m_svd.getProtTcpIpClientSendDataOnChange()) {
            // Write Sensor Data
        }
    }

}
