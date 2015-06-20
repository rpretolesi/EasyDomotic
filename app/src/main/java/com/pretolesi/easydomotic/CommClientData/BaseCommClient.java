package com.pretolesi.easydomotic.CommClientData;

import android.content.Context;
import android.os.AsyncTask;

import com.pretolesi.easydomotic.CustomControls.NumericDataType.DataType;
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

    protected DataOutputStream m_dataOutputStream = null;
    protected DataInputStream m_dataInputStream = null;

    protected int iProgressCounter;;

    public BaseCommClient (Context context){
        m_vTcpIpClientStatusListener = new Vector<>();
        m_vTcpIpClientWriteStatusListener = new Vector<>();
        m_vTcpIpClientReadValueStatusListener = new Vector<>();
        m_context = context;
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

    protected boolean startConnection() {
        return false;
    }

    protected boolean send() {

        if (m_dataOutputStream != null && m_ticd != null && m_vtim != null) {
            if (!m_vtim.isEmpty()) {
                try {
                    TcpIpMsg tim = m_vtim.firstElement();

                    if (tim != null && !tim.getMsgSent()) {
                        if (m_ticd.getProtocolID() == BaseValueCommClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                            int iIndex = m_vtim.lastIndexOf(tim);
                            if(iIndex != -1) {
                                tim.setMsgTimeMSNow();
                                tim.setMsgAsSent(true);
                                m_vtim.setElementAt(tim,iIndex);
                            }
                            m_dataOutputStream.write(tim.getMsgData(), 0, tim.getMsgData().length);
                        }
                    }

                    return true;
                } catch (EmptyStackException ESex) {
                    // Log.d(TAG, this.toString() + "send() return true");
                    return true;
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                    // Close
                    stopConnection();
                    // Log.d(TAG, this.toString() + "send()->" + "Exception ex: " + ex.getMessage());
                }
            } else {
                return true;
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
            if(m_ticd.getProtocolID() == BaseValueCommClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                try {
                    TcpIpMsg tim = Modbus.writeShort(context, iTID, iUID, iAddress, iValue);
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
        return false;
    }

    public synchronized void readValue(Context context, int iTID, int iUID, int iAddress, DataType dtDataType){
    }

    @Override
    protected Void doInBackground(Object... obj) {

        m_ticd = (BaseValueCommClientData) obj[0];

        try {
            while (!isCancelled() && m_ticd != null) {
                try {
                    if (!isConnected()) {
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
                    // Log.d(TAG, this.toString() + "doInBackground()->" + "Exception ex: " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            // Log.d(TAG, this.toString() + "doInBackground()->" + "Exception ex: " + ex.getMessage());
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
                        ticl.onTcpIpClientStatusCallback((TcpIpClientStatus) obj[0]);
                    }
                }
            }
            if(obj[0] instanceof TcpIpClientWriteStatus){
                if(m_vTcpIpClientWriteStatusListener != null) {
                    for (TcpIpClientWriteStatusListener ticwsl : m_vTcpIpClientWriteStatusListener) {
                        ticwsl.onWriteValueStatusCallback((TcpIpClientWriteStatus) obj[0]);
                    }
                }
            }
            if(obj[0] instanceof TcpIpClientReadStatus){
                if(m_vTcpIpClientReadValueStatusListener != null) {
                    for (TcpIpClientReadValueStatusListener ticrvsl : m_vTcpIpClientReadValueStatusListener) {
                        ticrvsl.onReadValueStatusCallback((TcpIpClientReadStatus) obj[0]);
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
    public static interface TcpIpClientStatusListener {
        /**
         * Callbacks
         */
        void onTcpIpClientStatusCallback(TcpIpClientStatus tics);
    }
    public static interface TcpIpClientWriteStatusListener {
        /**
         * Callbacks
         */
        void onWriteValueStatusCallback(TcpIpClientWriteStatus ticws);
    }
    public static interface TcpIpClientReadValueStatusListener {
        /**
         * Callbacks
         */
        void onReadValueStatusCallback(TcpIpClientReadStatus ticrs);
    }
}