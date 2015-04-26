package com.pretolesi.easydomotic.TcpIpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pretolesi.easydomotic.Modbus.ModbusAddressOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusLengthOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusMBAPLengthException;
import com.pretolesi.easydomotic.Modbus.ModbusPDULengthException;
import com.pretolesi.easydomotic.Modbus.ModbusProtocolOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusTransIdOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusUnitIdOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusValueOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.Modbus;
import com.pretolesi.easydomotic.Modbus.ModbusMBAP;
import com.pretolesi.easydomotic.Modbus.ModbusPDU;
import com.pretolesi.easydomotic.Modbus.ModbusStatus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 *
 */
public class TCPIPClient extends AsyncTask<Object, Object, Void> {
    private static final String TAG = "TCPIPClient";

    // Listener e Callback
    private List<TCPIPClientListener> m_vListener = null;
    // Imposto il listener
    public synchronized void registerListener(TCPIPClientListener listener) {
        if(m_vListener != null && !m_vListener.contains(listener)){
            m_vListener.add(listener);
        }
    }
    public synchronized void unregisterListener(TCPIPClientListener listener) {
        if(m_vListener != null && m_vListener.contains(listener)){
            m_vListener.remove(listener);
        }
    }

    //
    private Context m_context = null;

    private Vector<TcpIpMsg> m_vtim = null;
    private TCPIPClientData m_ticd = null;

    private Socket m_clientSocket = null;
    private SocketAddress m_socketAddress = null;
    private DataOutputStream m_dataOutputStream = null;
    private DataInputStream m_dataInputStream = null;
    private int iProgressCounter;;

    private List<String> m_vstrMessageLog = null;
    private String m_strStatus = null;

    private long m_timeMillisecondsStart = 0;
    private long m_timeMillisecondsSend = 0;
    private long m_timeMillisecondsReceive = 0;

    public TCPIPClient (Context context){
        m_vListener = new Vector<>();
        m_context = context;
        m_vtim = new Vector<>();
        m_vstrMessageLog = new Vector<>();
        m_strStatus = "";
    }

    public synchronized long getID() {
        if (m_ticd != null) {
            return m_ticd.getID();
        }
        return 0;
    }

    public synchronized long getProtocolID() {
        if (m_ticd != null) {
            return m_ticd.getProtocolID();
        }
        return 0;
    }

    private synchronized boolean startConnection() {

        Log.d(TAG, this.toString() + "startConnection() enter");
        m_timeMillisecondsStart = System.currentTimeMillis();

        if (m_ticd != null) {
            // Callbacks on UI
            publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.CONNECTING, ""));
            try {
                m_socketAddress = new InetSocketAddress(m_ticd.getAddress(), m_ticd.getPort());
                if (m_clientSocket == null) {
                    m_clientSocket = new Socket();
                    m_clientSocket.setSoTimeout(m_ticd.getTimeout());
                    m_clientSocket.setTcpNoDelay(true);
                    m_clientSocket.setKeepAlive(true);
                    m_clientSocket.connect(m_socketAddress);
                    m_dataOutputStream = new DataOutputStream(m_clientSocket.getOutputStream());
                    m_dataInputStream = new DataInputStream(m_clientSocket.getInputStream());
                    iProgressCounter = 0;

                    // Restore the operations not completed
                    if(m_vtim != null) {
                        for (Iterator<TcpIpMsg> iterator = m_vtim.iterator(); iterator.hasNext();) {
                            TcpIpMsg tim = iterator.next();
                            if (tim != null) {
                                tim.setMsgAsSent(false);
                                tim.setMsgTimeMSNow();
                                // Remove the current element from the iterator and the list.
                                // iterator.remove();
                            }
                        }
                    }

                    // Callbacks on UI
                    publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ONLINE, "" ));

                    Log.d(TAG, this.toString() + "startConnection() return true. Time(ms):" + (System.currentTimeMillis() - m_timeMillisecondsStart));
                    return true;
                }
            } catch (Exception ex) {
                Log.d(TAG, this.toString() + "startConnection()->" + "Exception ex: " + ex.getMessage());
                // Callbacks on UI
                publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.OFFLINE, ex.getMessage()));
            }
        }

        Log.d(TAG, this.toString() + "startConnection() return false");

        return false;
    }

    private synchronized boolean isConnected() {
        if(m_clientSocket != null && m_dataInputStream != null && m_dataOutputStream != null && m_clientSocket.isConnected()){
            iProgressCounter = iProgressCounter + 1;
            if(iProgressCounter > 16) {
                iProgressCounter = 1;
            }
            String strProgress = "";
            for(int index = 0; index < iProgressCounter; index++){
                strProgress = strProgress + "-";
            }
           // Callbacks on UI
            publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ONLINE, strProgress ));
            return true;
        }

        // Callbacks on UI
        publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.OFFLINE, "" ));
        iProgressCounter = 0;
        return false;
    }

    private synchronized boolean send() {
        Log.d(TAG, this.toString() + "send() enter");

        m_timeMillisecondsSend = System.currentTimeMillis();

        if (m_dataOutputStream != null && m_ticd != null && m_vtim != null) {
            if (!m_vtim.isEmpty()) {
                try {
                    TcpIpMsg tim = m_vtim.firstElement();

                    if (tim != null && !tim.getMsgSent()) {
                        if (m_ticd.getProtocolID() == TCPIPClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                            int iIndex = m_vtim.lastIndexOf(tim);
                            if(iIndex != -1) {
                                tim.setMsgTimeMSNow();
                                tim.setMsgAsSent(true);
                                m_vtim.setElementAt(tim,iIndex);
                            }
                            m_dataOutputStream.write(tim.getMsgData(), 0, tim.getMsgData().length);
                        }
                    }

                    Log.d(TAG, this.toString() + "send() return true. Time(ms):" + (System.currentTimeMillis() - m_timeMillisecondsSend));
                    return true;
                } catch (EmptyStackException ESex) {
                    Log.d(TAG, this.toString() + "send() return true");
                    return true;
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                    // Close
                    stopConnection();
                    Log.d(TAG, this.toString() + "send()->" + "Exception ex: " + ex.getMessage());
                }
            } else {
                return true;
            }
        }

        Log.d(TAG, this.toString() + "send() return false");
        return false;
    }

    private synchronized boolean receive() {
        Log.d(TAG, this.toString() + "receive() enter");

        m_timeMillisecondsReceive = System.currentTimeMillis();

        if (m_dataInputStream != null && m_ticd != null && m_vtim != null) {

            // In case of timeout, i pute the message in queue again
            for (Iterator<TcpIpMsg> iterator = m_vtim.iterator(); iterator.hasNext();) {
                TcpIpMsg tim = iterator.next();
                if (tim != null) {
                    if(tim.getMsgSent() && (System.currentTimeMillis() - tim.getSentTimeMS() > m_ticd.getTimeout())){
                        tim.setMsgAsSent(false);
                        tim.setMsgTimeMSNow();
                        publishProgress(new ModbusStatus(getID(), (int)tim.getMsgID(), ModbusStatus.Status.TIMEOUT, 0, ""));
                   }
                }
            }

            if (!m_vtim.isEmpty()) {
                if (m_ticd.getProtocolID() == TCPIPClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                    // MBAP
                    byte[] byteMBAP = new byte[6];
                    try {
                        m_dataInputStream.readFully(byteMBAP, 0, 6);
                        // Rest of message
                        int iLength;
                        try {
                            ModbusMBAP mmbap = Modbus.getMBAP(m_context, byteMBAP);
                            if (mmbap != null) {
                                iLength = mmbap.getLength() - 6;
                                byte[] byteDATA = new byte[iLength];
                                try {
                                    m_dataInputStream.readFully(byteDATA, 0, iLength);
                                    try {
                                        ModbusPDU mpdu = Modbus.getPDU(m_context, m_ticd.getID(), byteMBAP, byteDATA);
                                        if (mpdu != null) {

                                            // Tutto Ok, rimuovo l'elemento
                                            TcpIpMsg timTemp = null;
                                            for(TcpIpMsg tim : m_vtim){
                                                if(tim != null){
                                                    if(tim.getMsgID() == mmbap.getTI()){
                                                        timTemp = tim;
                                                    }
                                                }
                                            }
                                            if(timTemp != null){
                                                m_vtim.remove(timTemp);
                                            }

                                            // Check Return code
                                            if(mpdu.getExC() == 0){
                                                publishProgress(new ModbusStatus(getID(), mmbap.getTI(), ModbusStatus.Status.OK, 0, ""));
                                            } else {
                                                publishProgress(new ModbusStatus(getID(), mmbap.getTI(), ModbusStatus.Status.ERROR, mpdu.getExC(), ""));
                                            }
                                            // m_timeMillisecondsGet = System.currentTimeMillis();
                                            Log.d(TAG, this.toString() + "receive() return true. Time(ms):" + (System.currentTimeMillis() - m_timeMillisecondsReceive));
                                            return true;
                                        }
                                    } catch (ModbusProtocolOutOfRangeException ex) {
                                        // Callbacks on UI
                                        publishProgress(new ModbusStatus(getID(), -1, ModbusStatus.Status.ERROR, 0, ex.getMessage()));
                                        Log.d(TAG, this.toString() + "receive()->" + "ModbusProtocolOutOfRangeException ex: " + ex.getMessage());
                                    } catch (ModbusMBAPLengthException ex) {
                                        // Callbacks on UI
                                        publishProgress(new ModbusStatus(getID(), -1, ModbusStatus.Status.ERROR, 0, ex.getMessage()));
                                        Log.d(TAG, this.toString() + "receive()->" + "ModbusMBAPLengthException ex: " + ex.getMessage());
                                    } catch (ModbusLengthOutOfRangeException ex) {
                                        // Callbacks on UI
                                        publishProgress(new ModbusStatus(getID(), -1, ModbusStatus.Status.ERROR, 0, ex.getMessage()));
                                        Log.d(TAG, this.toString() + "receive()->" + "ModbusLengthOutOfRangeException ex: " + ex.getMessage());
                                    } catch (ModbusPDULengthException ex) {
                                        // Callbacks on UI
                                        publishProgress(new ModbusStatus(getID(), -1, ModbusStatus.Status.ERROR, 0, ex.getMessage()));
                                        Log.d(TAG, this.toString() + "receive()->" + "ModbusPDULengthException ex: " + ex.getMessage());
                                    }

                                } catch (SocketTimeoutException ex) {
                                    //Modbus.callTcpIpServerModbusOperationTimeoutCallback(m_ticd.getID());
                                    Log.d(TAG, this.toString() + "receive() DATA->" + "SocketTimeoutException ex: " + ex.getMessage());
                                } catch (EOFException ex) {
                                    // Callbacks on UI
                                    publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                    Log.d(TAG, this.toString() + "receive() DATA->" + "EOFException ex: " + ex.getMessage());
                                } catch (IOException ex) {
                                    // Callbacks on UI
                                    publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                    Log.d(TAG, this.toString() + "receive() DATA->" + "IOException ex: " + ex.getMessage());
                                } catch (Exception ex) {
                                    // Callbacks on UI
                                    publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                    Log.d(TAG, this.toString() + "receive() DATA->" + "Exception ex: " + ex.getMessage());
                                }
                            }
                        } catch (ModbusProtocolOutOfRangeException ex) {
                            // Callbacks on UI
                            publishProgress(new ModbusStatus(getID(), -1, ModbusStatus.Status.ERROR, 0, ex.getMessage()));
                            Log.d(TAG, this.toString() + "receive()->" + "ModbusProtocolOutOfRangeException ex: " + ex.getMessage());
                        } catch (ModbusMBAPLengthException ex) {
                            // Callbacks on UI
                            publishProgress(new ModbusStatus(getID(), -1, ModbusStatus.Status.ERROR, 0, ex.getMessage()));
                            Log.d(TAG, this.toString() + "receive()->" + "ModbusMBAPLengthException ex: " + ex.getMessage());
                        } catch (ModbusLengthOutOfRangeException ex) {
                            // Callbacks on UI
                            publishProgress(new ModbusStatus(getID(), -1, ModbusStatus.Status.ERROR, 0, ex.getMessage()));
                            Log.d(TAG, this.toString() + "receive()->" + "ModbusLengthOutOfRangeException ex: " + ex.getMessage());
                        }

                    } catch (SocketTimeoutException ex) {
                        // Callbacks on UI
                        publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.TIMEOUT, ex.getMessage()));
                        Log.d(TAG, this.toString() + "receive() MBAP->" + "SocketTimeoutException ex: " + ex.getMessage());
                    } catch (EOFException ex) {
                        // Callbacks on UI
                        publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                        Log.d(TAG, this.toString() + "receive() MBAP->" + "EOFException ex: " + ex.getMessage());
                    } catch (IOException ex) {
                        // Callbacks on UI
                        publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                        Log.d(TAG, this.toString() + "receive() MBAP->" + "IOException ex: " + ex.getMessage());
                    } catch (Exception ex) {
                        // Callbacks on UI
                        publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                        Log.d(TAG, this.toString() + "receive() MBAP->" + "Exception ex: " + ex.getMessage());
                    }

                }
            } else {
                return true;
            }
        }

        Log.d(TAG, this.toString() + "receive() return false");
        return false;
    }

    private synchronized void stopConnection() {

        Log.d(TAG, this.toString() + "stopConnection() enter");

        if(m_ticd != null) {
            // Callbacks on UI
            publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.DISCONNECTING, ""));
        }

        m_socketAddress = null;

        // Chiudo il socket
        if (m_clientSocket != null) {
            try {
                m_clientSocket.close();
            } catch (IOException ioex_1) {
                Log.d(TAG, this.toString() + "stopConnection()->" + "IOException ioex_1: " + ioex_1.getMessage());
            }
        }

        m_clientSocket = null;

        // close Output stream
        if (m_dataOutputStream != null) {
            try {
                m_dataOutputStream.close();
            } catch (IOException ioex_2) {
                Log.d(TAG, this.toString() + "stopConnection()->" + "IOException ioex_2: " + ioex_2.getMessage());
            }
        }
        m_dataOutputStream = null;

        // close Input stream
        if (m_dataInputStream != null) {
            try {
                m_dataInputStream.close();
            } catch (IOException ioex_3) {
                Log.d(TAG, this.toString() + "stopConnection()->" + "IOException ioex_3: " + ioex_3.getMessage());
            }
        }
        m_dataInputStream = null;

        // Callbacks on UI
        publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.OFFLINE, "" ));

        Log.d(TAG, this.toString() + "stopConnection() return");
    }

    /*
     * Writing/Reading Function
     */
    public synchronized void writeSwitchValue(int lID, int iAddress, int iValue){
        if(m_ticd != null) {
            if(m_ticd.getProtocolID() == TCPIPClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                try {
                    TcpIpMsg tim = Modbus.writeSingleRegister(m_context, lID, 0,  iAddress, iValue);
                    if (m_vtim != null && tim != null) {
                        if(!m_vtim.contains(tim)){
                            m_vtim.add(tim);
                        }
                    }
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ModbusStatus(getID(), -1, ModbusStatus.Status.WRITING_ERROR, 0, ex.getMessage()));
                    Log.d(TAG, this.toString() + "ModbusTransIdOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ModbusStatus(getID(), -1, ModbusStatus.Status.WRITING_ERROR, 0, ex.getMessage()));
                    Log.d(TAG, this.toString() + "ModbusUnitIdOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ModbusStatus(getID(), -1, ModbusStatus.Status.WRITING_ERROR, 0, ex.getMessage()));
                    Log.d(TAG, this.toString() + "ModbusAddressOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new ModbusStatus(getID(), -1, ModbusStatus.Status.WRITING_ERROR, 0, ex.getMessage()));
                    Log.d(TAG, this.toString() + "ModbusValueOutOfRangeException ex: " + ex.getMessage());
                }
            }
        }

        // Callbacks
//        sendWriteSwitchValueCallback(lID, Status.WRITE_LIGTH_SWITCH_VALUE_ERROR);
    }
/*
    @Override
    public void onWriteSingleRegisterOkCallback(int iTransactionIdentifier) {
        this.publishProgress();
        sendWriteSwitchValueCallback(iTransactionIdentifier, Status.WRITE_LIGTH_SWITCH_VALUE_OK);
    }

    @Override
    public void onWriteSingleRegisterExceptionCallback(int iTransactionIdentifier, int iErrorCode) {
        sendWriteSwitchValueCallback(iTransactionIdentifier, Status.WRITE_LIGTH_SWITCH_VALUE_ERROR);
    }
*/
    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(Object... obj) {
        Log.d(TAG, this.toString() + "doInBackground() enter");

        // Listener
//        Modbus.registerListener(this);

        m_ticd = (TCPIPClientData) obj[0];

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
                    Log.d(TAG, this.toString() + "doInBackground()->" + "Exception ex: " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            Log.d(TAG, this.toString() + "doInBackground()->" + "Exception ex: " + ex.getMessage());
        }

        // Closing...
        stopConnection();

        // Listener
 //       Modbus.unregisterListener(this);

        Log.d(TAG, this.toString() + "doInBackground() return");
        return null;
    }

    @Override
    protected void onProgressUpdate(Object... obj) {
        super.onProgressUpdate(obj);
        // Aggiorno
        if(m_vListener != null) {
            if(obj != null){
                if(obj[0] instanceof TcpIpClientStatus){
                    for (TCPIPClientListener ticl : m_vListener) {
                        ticl.onTcpIpClientStatusCallback((TcpIpClientStatus) obj[0]);
                    }
                }
                if(obj[0] instanceof ModbusStatus){
                    for (TCPIPClientListener ticl : m_vListener) {
                        ticl.onModbusStatusCallback((ModbusStatus) obj[0]);
                    }
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Void result) {
    }

    /*
     * Send callbacks
     */
/*
    private void sendWriteSwitchValueCallback(int iTransactionIdentifier, Status sStatus){
        if(m_vListener != null) {
            for (TCPIPClientListener ticl : m_vListener) {
                ticl.onModbusStatusCallback(getID(), iTransactionIdentifier, sStatus);
            }
        }
    }
    private void sendTcpIpClientStatusCallback(TcpIpClientStatus tics){
        if(m_vListener != null) {
            for (TCPIPClientListener ticl : m_vListener) {
                ticl.onTcpIpClientStatusCallback(tics);
            }
        }
    }
*/
    /**
     * Callbacks interface.
     */
    public static interface TCPIPClientListener {
        /**
         * Callbacks
         */
        void onModbusStatusCallback(ModbusStatus ms);
        void onTcpIpClientStatusCallback(TcpIpClientStatus tics);
//        void onWriteSingleRegisterExceptionCallback(long lProtTcpIpClientID, int iTransactionIdentifier, int iEC, int iExC);
//        void onTcpIpServerModbusOperationTimeoutCallback(long lProtTcpIpClientID);
//        void onTcpIpServerModbusStatusCallback(long lProtTcpIpClientID, TCPIPClient.Status tics);
    }
}