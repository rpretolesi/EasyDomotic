package com.pretolesi.easydomotic.CommClientData;

import android.content.Context;
import android.os.AsyncTask;

import com.pretolesi.easydomotic.CustomControls.NumericDataType;
import com.pretolesi.easydomotic.CustomControls.NumericDataType.DataType;
import com.pretolesi.easydomotic.CommClientData.BaseValueCommClientData.Protocol;
import com.pretolesi.easydomotic.Modbus.Modbus;
import com.pretolesi.easydomotic.Modbus.ModbusAddressOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusQuantityOfRegistersOutOfRange;
import com.pretolesi.easydomotic.Modbus.ModbusTransIdOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusUnitIdOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusValueOutOfRangeException;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientReadStatus;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientStatus;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientWriteStatus;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpMsg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by RPRETOLESI on 16/06/2015.
 */
public class BaseCommClient extends AsyncTask<Object, Object, Void> {

    // Listener e Callback
    // Client
    protected List<TcpIpClientStatusListener> m_vTcpIpClientStatusListener = null;
    // Imposto il listener
    public synchronized void registerTcpIpClientStatus(TcpIpClientStatusListener listener) {
        if(m_vTcpIpClientStatusListener != null && !m_vTcpIpClientStatusListener.contains(listener)){
            m_vTcpIpClientStatusListener.add(listener);
        }
    }
    public synchronized void unregisterTcpIpClientStatus(TcpIpClientStatusListener listener) {
        if(m_vTcpIpClientStatusListener != null && m_vTcpIpClientStatusListener.contains(listener)){
            m_vTcpIpClientStatusListener.remove(listener);
        }
    }

    // Write
    protected List<TcpIpClientWriteStatusListener> m_vTcpIpClientWriteStatusListener = null;
    // Imposto il listener
    public synchronized void registerTcpIpClientWriteSwitchStatus(TcpIpClientWriteStatusListener listener) {
        if(m_vTcpIpClientWriteStatusListener != null && !m_vTcpIpClientWriteStatusListener.contains(listener)){
            m_vTcpIpClientWriteStatusListener.add(listener);
        }
    }
    public synchronized void unregisterTcpIpClientWriteSwitchStatus(TcpIpClientWriteStatusListener listener) {
        if(m_vTcpIpClientWriteStatusListener != null && m_vTcpIpClientWriteStatusListener.contains(listener)){
            m_vTcpIpClientWriteStatusListener.remove(listener);
        }
    }

    // Read
    protected List<TcpIpClientReadValueStatusListener> m_vTcpIpClientReadValueStatusListener = null;
    // Imposto il listener
    public synchronized void registerTcpIpClientReadValueStatus(TcpIpClientReadValueStatusListener listener) {
        if(m_vTcpIpClientReadValueStatusListener != null && !m_vTcpIpClientReadValueStatusListener.contains(listener)){
            m_vTcpIpClientReadValueStatusListener.add(listener);
        }
    }
    public synchronized void unregisterTcpIpClientReadValueStatus(TcpIpClientReadValueStatusListener listener) {
        if(m_vTcpIpClientReadValueStatusListener != null && m_vTcpIpClientReadValueStatusListener.contains(listener)){
            m_vTcpIpClientReadValueStatusListener.remove(listener);
        }
    }

    protected Context m_context = null;
    protected BaseValueCommClientData m_ticd = null;
    protected Vector<TcpIpMsg> m_vtim = null;

    protected byte[] m_byteInputMSG = null;

    protected DataOutputStream m_dataOutputStream = null;
    protected DataInputStream m_dataInputStream = null;

    protected int m_iProgressCounter = 0;

    protected boolean m_bRestartConnection = false;

    public BaseCommClient (Context context){
        m_vTcpIpClientStatusListener = new Vector<>();
        m_vTcpIpClientWriteStatusListener = new Vector<>();
        m_vTcpIpClientReadValueStatusListener = new Vector<>();
        m_context = context;
        m_byteInputMSG = new byte[256];
        m_vtim = new Vector<>();
    }


    public synchronized long getID() {
        if (m_ticd != null) {
            return m_ticd.getID();
        }
        return 0;
    }

    public synchronized String getName() {
        if (m_ticd != null) {
            return m_ticd.getName();
        }
        return "";
    }

    public synchronized String getAddress() {
        if (m_ticd != null) {
            return m_ticd.getAddress();
        }
        return "";
    }

    protected TcpIpMsg getMsgToSent(){
        boolean bNoMsgSent = true;

        if(m_vtim == null) {
            return null;
        }

        if(m_vtim.isEmpty()){
            return null;
        }

        for (Iterator<TcpIpMsg> iterator = m_vtim.iterator(); iterator.hasNext();) {
            TcpIpMsg tim = iterator.next();
            if (tim != null && tim.getMsgSent()) {
                bNoMsgSent = false;
            }
        }
        TcpIpMsg tim = null;

        if(bNoMsgSent) {
            tim = m_vtim.firstElement();
            if (tim != null && !tim.getMsgSent()) {
                int iIndex = m_vtim.lastIndexOf(tim);
                if (iIndex != -1) {
                    tim.setMsgTimeMSNow();
                    tim.setMsgAsSent(true);
                    m_vtim.setElementAt(tim, iIndex);
                }
            }
        }

        return tim;
    }

    protected void setAllMsgAsUnsent(){
        if(m_vtim != null) {
            for (Iterator<TcpIpMsg> iterator = m_vtim.iterator(); iterator.hasNext();) {
                TcpIpMsg tim = iterator.next();
                if (tim != null) {
                    tim.setMsgTimeMSNow();
                    tim.setMsgAsSent(false);
                 }
            }
        }
    }

    protected void checkTimeoutAndSetAllMsgAsUnsent(){
        for (Iterator<TcpIpMsg> iterator = m_vtim.iterator(); iterator.hasNext();) {
            TcpIpMsg tim = iterator.next();
            if (tim != null) {
                if (System.currentTimeMillis() - tim.getSentTimeMS() >= m_ticd.getTimeout()) {
                    tim.setMsgTimeMSNow();
                    tim.setMsgAsSent(false);
                    publishProgress(new TcpIpClientWriteStatus(getID(), (int) tim.getTID(), (int) tim.getUID(), TcpIpClientWriteStatus.Status.TIMEOUT, 0, ""));
                    publishProgress(new TcpIpClientReadStatus(getID(), (int) tim.getTID(), (int) tim.getUID(), TcpIpClientReadStatus.Status.TIMEOUT, 0, "", null));
                }
            }
        }
    }

    protected boolean startConnection() { return false; }

    protected boolean send() {

        if (m_dataOutputStream != null) {
            try {
                TcpIpMsg tim = getMsgToSent();
                if(tim != null){
                    m_dataOutputStream.write(tim.getMsgData(), 0, tim.getMsgData().length);
                }
                return true;

            } catch (EmptyStackException ex) {
                return true;
            } catch (Exception ex) {
                // Callbacks on UI
                publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                // Close
                stopConnection();
            }
        }

        return false;
    }

    protected boolean receive() {
        return false;
    }

    protected boolean isConnected() {
        return false;
    }

    protected void stopConnection() {
        if(m_ticd != null) {
            // Callbacks on UI
            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.DISCONNECTING, ""));
        }

        // close Output stream
        if (m_dataOutputStream != null) {
            try {
                m_dataOutputStream.close();
            } catch (IOException ioex_2) {
            }
        }
        m_dataOutputStream = null;

        // close Input stream
        if (m_dataInputStream != null) {
            try {
                m_dataInputStream.close();
            } catch (IOException ioex_3) {
            }
        }
        m_dataInputStream = null;
    }

    /*
     * Writing/Reading Function
     */
    protected void writeShort(Context context, int iTID, int iUID, int iAddress, int iValue){
        if(m_ticd != null) {
            if((m_ticd.getProtocolID() == Protocol.MODBUS_ON_TCP_IP.getID()) || (m_ticd.getProtocolID() == Protocol.MODBUS_ON_SERIAL.getID())) {
                try {
                    TcpIpMsg tim = null;
                    if(m_ticd.getProtocolID() == Protocol.MODBUS_ON_TCP_IP.getID()){
                        tim = Modbus.writeShort(context, iTID, iUID, iAddress, iValue, Protocol.MODBUS_ON_TCP_IP);
                    }
                    if(m_ticd.getProtocolID() == Protocol.MODBUS_ON_SERIAL.getID()) {
                        tim = Modbus.writeShort(context, iTID, iUID, iAddress, iValue, Protocol.MODBUS_ON_SERIAL);
                    }
                    if (m_vtim != null && tim != null) {
                        tim.setDataType(DataType.SHORT);
                        if(!m_vtim.contains(tim)){
                            m_vtim.add(tim);
                        }
                    }
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusUnitIdOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusAddressOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusValueOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusQuantityOfRegistersOutOfRange ex: " + ex.getMessage());
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusTransIdOutOfRangeException ex: " + ex.getMessage());
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "Exception ex: " + ex.getMessage());
                }
            }
        }
    }

    protected void writeInteger(Context context, int iTID, int iUID, int iAddress, long lValue){
        if(m_ticd != null) {
            if(m_ticd.getProtocolID() == BaseValueCommClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                try {
                    TcpIpMsg tim = Modbus.writeInteger(context, iTID, iUID, iAddress, lValue);
                    if (m_vtim != null && tim != null) {
                        tim.setDataType(DataType.INT);
                        if(!m_vtim.contains(tim)){
                            m_vtim.add(tim);
                        }
                    }
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusTransIdOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusUnitIdOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusAddressOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusValueOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusQuantityOfRegistersOutOfRange ex: " + ex.getMessage());
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "Exception ex: " + ex.getMessage());
                }
            }
        }
    }

    protected void writeLong(Context context, int iTID, int iUID, int iAddress, long lValue){
        if(m_ticd != null) {
            if(m_ticd.getProtocolID() == BaseValueCommClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                try {
                    TcpIpMsg tim = Modbus.writeLong(context, iTID, iUID, iAddress, lValue);
                    if (m_vtim != null && tim != null) {
                        tim.setDataType(DataType.LONG);
                        if(!m_vtim.contains(tim)){
                            m_vtim.add(tim);
                        }
                    }
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusTransIdOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusUnitIdOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusAddressOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusValueOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusQuantityOfRegistersOutOfRange ex: " + ex.getMessage());
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "Exception ex: " + ex.getMessage());
                }
            }
        }
    }

    protected void writeFloat(Context context, int iTID, int iUID, int iAddress, float fValue){
        if(m_ticd != null) {
            if(m_ticd.getProtocolID() == BaseValueCommClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                try {
                    TcpIpMsg tim = Modbus.writeFloat(context, iTID, iUID, iAddress, fValue);
                    if (m_vtim != null && tim != null) {
                        tim.setDataType(DataType.FLOAT);
                        if(!m_vtim.contains(tim)){
                            m_vtim.add(tim);
                        }
                    }
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusTransIdOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusUnitIdOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusAddressOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusValueOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusQuantityOfRegistersOutOfRange ex: " + ex.getMessage());
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "Exception ex: " + ex.getMessage());
                }
            }
        }
    }

    protected void writeDouble(Context context, int iTID, int iUID, int iAddress, double dblValue){
        if(m_ticd != null) {
            if(m_ticd.getProtocolID() == BaseValueCommClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                try {
                    TcpIpMsg tim = Modbus.writeDouble(context, iTID, iUID, iAddress, dblValue);
                    if (m_vtim != null && tim != null) {
                        tim.setDataType(DataType.DOUBLE);
                        if(!m_vtim.contains(tim)){
                            m_vtim.add(tim);
                        }
                    }
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusTransIdOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusUnitIdOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusAddressOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusValueOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "ModbusQuantityOfRegistersOutOfRange ex: " + ex.getMessage());
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "Exception ex: " + ex.getMessage());
                }
            }
        }
    }

    public synchronized boolean writeValue(Context context, int iTID, int iUID, int iAddress, Object objValue){
        if(objValue == null) {
            return false;
        }
        if(objValue instanceof Short){
            writeShort(context, iTID, iUID, iAddress, (Short)objValue);
            return true;
        }
        if(objValue instanceof Integer){
            writeInteger(context, iTID, iUID, iAddress, (Integer) objValue);
            return true;
        }
        if(objValue instanceof Long){
            writeLong(context, iTID, iUID, iAddress, (Long) objValue);
            return true;
        }
        if(objValue instanceof Float){
            writeFloat(context, iTID, iUID, iAddress, (Float) objValue);
            return true;
        }
        if(objValue instanceof Double){
            writeDouble(context, iTID, iUID, iAddress, (Double) objValue);
            return true;
        }
        return false;
    }

    public synchronized boolean writeValue(Context context, int iTID, int iUID, int iAddress, DataType dtDataType, String strValue){
        if(dtDataType != null){
            switch (dtDataType) {
                case SHORT:
                    short shValue;
                    try {
                        shValue = Short.parseShort(strValue);
                        writeShort(context, iTID, iUID, iAddress, shValue);

                        return true;

                    } catch (Exception ignore) {
                    }
                    break;

                case INT:
                    int iValue;
                    try {
                        iValue = Integer.parseInt(strValue);
                        writeInteger(context, iTID, iUID, iAddress, iValue);

                        return true;

                    } catch (Exception ignore) {
                    }
                    break;

                case LONG:
                    long lValue;
                    try {
                        lValue = Long.parseLong(strValue);
                        writeLong(context, iTID, iUID, iAddress, lValue);

                        return true;

                    } catch (Exception ignore) {
                    }
                    break;

                case FLOAT:
                    float fValue;
                    try {
                        fValue = Float.parseFloat(strValue);
                        writeFloat(context, iTID, iUID, iAddress, fValue);

                        return true;

                    } catch (Exception ignore) {
                    }
                    break;

                case DOUBLE:
                    double dblValue;
                    try {
                        dblValue = Double.parseDouble(strValue);
                        writeDouble(context, iTID, iUID, iAddress, dblValue);

                        return true;

                    } catch (Exception ignore) {
                    }
                    break;

            }
        }
        return false;
    }

    public synchronized void readValue(Context context, int iTID, int iUID, int iAddress, DataType dtDataType){
    }

    public synchronized Object getValue(DataType dtDataType, byte[] aByteValue){
        Object obj = null;

        if(dtDataType != null && aByteValue != null){
            switch (dtDataType) {
                case SHORT:
                    obj = NumericDataType.getShort(aByteValue);
                    break;

                case INT:
                    obj = NumericDataType.getInt(aByteValue);
                    break;

                case LONG:
                    obj = NumericDataType.getLong(aByteValue);
                    break;

                case FLOAT:
                    obj = NumericDataType.getFloat(aByteValue);
                    break;

                case DOUBLE:
                    obj = NumericDataType.getDouble(aByteValue);
                    break;

            }
        }
        return obj;
    }

    @Override
    protected Void doInBackground(Object... obj) {

        m_ticd = (BaseValueCommClientData) obj[0];

        try {
            while (!isCancelled() && m_ticd != null) {
                try {
                    if (!isConnected() || m_bRestartConnection) {
                        // Reset bit
                        m_bRestartConnection = false;

                        // Stop communication with Server
                        stopConnection();
                        // attendo per non sovraccaricare CPU
                        try {
                            Thread.sleep(3000, 0);
                        } catch (InterruptedException ignored) {

                        }

                        // Start communication with Server
                        startConnection();
                        // attendo per non sovraccaricare CPU
                        try {
                            Thread.sleep(3000, 0);
                        } catch (InterruptedException ignored) {
                        }
                    } else {
                        if (!send()){
                            try {
                                Thread.sleep(3000, 0);
                            } catch (InterruptedException ignored) {
                            }
                        }

                        if (!receive()) {
                            try {
                                Thread.sleep(3000, 0);
                            } catch (InterruptedException ignored) {
                            }
                        }
                        // Delay (depending of the protocol specific
                        try {
                            Thread.sleep(m_ticd.getCommSendDelayData(), 0);
                        } catch (InterruptedException ignored) {
                        }
                    }
                } catch (Exception ex) {
                }
            }
        } catch (Exception ex) {
        }

        // Closing...
        stopConnection();

        return null;
    }

    @Override
    protected void onProgressUpdate(Object... obj) {
        super.onProgressUpdate(obj);
        // Aggiorno
        if(obj != null){
            if(obj[0] instanceof TcpIpClientStatus){
                if(m_vTcpIpClientStatusListener != null) {
                    for (TcpIpClientStatusListener ticl : m_vTcpIpClientStatusListener) {
                        if(ticl != null) {
                            ticl.onTcpIpClientStatusCallback((TcpIpClientStatus) obj[0]);
                        }
                    }
                }
            }
            if(obj[0] instanceof TcpIpClientWriteStatus){
                if(m_vTcpIpClientWriteStatusListener != null) {
                    for (TcpIpClientWriteStatusListener ticwsl : m_vTcpIpClientWriteStatusListener) {
                        if(ticwsl != null) {
                            ticwsl.onWriteValueStatusCallback((TcpIpClientWriteStatus) obj[0]);
                        }
                    }
                }
            }
            if(obj[0] instanceof TcpIpClientReadStatus){
                if(m_vTcpIpClientReadValueStatusListener != null) {
                    for (TcpIpClientReadValueStatusListener ticrvsl : m_vTcpIpClientReadValueStatusListener) {
                        if(ticrvsl != null) {
                            ticrvsl.onReadValueStatusCallback((TcpIpClientReadStatus) obj[0]);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Void result) {
    }

    /**
     * Callbacks interface.
     */
    public interface TcpIpClientStatusListener {
        /**
         * Callbacks
         */
        void onTcpIpClientStatusCallback(TcpIpClientStatus tics);
    }
    public interface TcpIpClientWriteStatusListener {
        /**
         * Callbacks
         */
        void onWriteValueStatusCallback(TcpIpClientWriteStatus ticws);
    }
    public interface TcpIpClientReadValueStatusListener {
        /**
         * Callbacks
         */
        void onReadValueStatusCallback(TcpIpClientReadStatus ticrs);
    }
}
