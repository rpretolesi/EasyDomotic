package com.pretolesi.easydomotic.CommClientData;

import android.content.Context;
import android.os.AsyncTask;

import com.pretolesi.easydomotic.CustomControls.NumericDataType.DataType;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientReadStatus;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientStatus;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientWriteStatus;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpMsg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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

    protected boolean startConnection() {
        return false;
    }

    protected boolean send() {
        return false;
    }

    protected boolean receive() {
        return false;
    }

    protected boolean isConnected() {
        return false;
    }

    protected void stopConnection() {
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
