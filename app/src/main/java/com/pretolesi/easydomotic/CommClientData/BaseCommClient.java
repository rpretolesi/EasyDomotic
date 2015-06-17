package com.pretolesi.easydomotic.CommClientData;

import android.content.Context;
import android.os.AsyncTask;

import com.pretolesi.easydomotic.CustomControls.NumericDataType.DataType;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientReadStatus;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientStatus;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientWriteStatus;

import java.util.List;

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

    public synchronized boolean writeValue(Context context, int iTID, int iUID, int iAddress, Object objValue){
        return false;
    }

    public synchronized boolean writeValue(Context context, int iTID, int iUID, int iAddress, DataType dtDataType, String strValue){
        return false;
    }

    public synchronized void readValue(Context context, int iTID, int iUID, int iAddress, DataType dtDataType){
    }

    @Override
    protected Void doInBackground(Object... params) {
        return null;
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
