package com.pretolesi.easydomotic.TcpIpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pretolesi.easydomotic.CustomException.ModbusAddressOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusLengthOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusMBAPLengthException;
import com.pretolesi.easydomotic.CustomException.ModbusProtocolOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusTransIdOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusUnitIdOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusValueOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.Modbus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

/**
 *
 */
public class TCPIPClient extends AsyncTask<Object, Void, Void> implements Modbus.ModbusListener {
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

    private Stack<byte[]> m_sbyte = null;
    private TCPIPClientData m_ticd = null;

    private Socket m_clientSocket = null;
    private SocketAddress m_socketAddress = null;
    private DataOutputStream m_dataOutputStream = null;
    private DataInputStream m_dataInputStream = null;

    private boolean m_bSocketOpen = false;

    private List<String> m_vstrMessageLog = null;
    private String m_strStatus = null;

    private long m_timeMillisecondsStart = 0;
    private long m_timeMillisecondsSend = 0;
    private long m_timeMillisecondsReceive = 0;

    public TCPIPClient (Context context){
        m_vListener = new Vector<>();
        m_context = context;
        m_sbyte = new Stack<>();
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

        // Prima chiudo la connessione
        if(m_bSocketOpen){
            stopConnection();
        }

        if (m_ticd != null) {
            // Callbacks
            sendTcpIpClientStatusCallback(Status.SERVER_CONNECTING);
            try {
                m_socketAddress = new InetSocketAddress(m_ticd.getAddress(), m_ticd.getPort());
                if (m_clientSocket == null) {
                    m_clientSocket = new Socket();
                    m_clientSocket.setSoTimeout(m_ticd.getTimeout());
                    m_clientSocket.connect(m_socketAddress);
                    m_dataOutputStream = new DataOutputStream(m_clientSocket.getOutputStream());
                    m_dataInputStream = new DataInputStream(m_clientSocket.getInputStream());

                    m_bSocketOpen = true;

                    // Callbacks
                    sendTcpIpClientStatusCallback(Status.SERVER_ON_LINE);

                    Log.d(TAG, this.toString() + "startConnection() return true. Time(ms):" + (System.currentTimeMillis() - m_timeMillisecondsStart));
                    return true;
                }
            } catch (Exception ex) {
                Log.d(TAG, this.toString() + "startConnection()->" + "Exception ex: " + ex.getMessage());
                stopConnection();
            }
        }

        Log.d(TAG, this.toString() + "startConnection() return false");

        return false;
    }

    private synchronized boolean isConnected() {
        return m_clientSocket != null && m_dataInputStream != null && m_dataOutputStream != null && m_clientSocket.isConnected();
    }

    private boolean send() {
        Log.d(TAG, this.toString() + "send() enter");

        m_timeMillisecondsSend = System.currentTimeMillis();

        if (m_dataOutputStream != null && m_ticd != null && m_sbyte != null) {
            try {
                if (!m_sbyte.isEmpty()) {
                    byte[] byteDATA = m_sbyte.pop();
                    if(byteDATA != null) {
                        if (m_ticd.getProtocolID() == TCPIPClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                            m_dataOutputStream.write(byteDATA, 0, byteDATA.length);
                        }
                    }
                }

                Log.d(TAG, this.toString() + "send() return true. Time(ms):" + (System.currentTimeMillis() - m_timeMillisecondsSend));
                return true;
            } catch (EmptyStackException ESex) {
                Log.d(TAG, this.toString() + "send() return true");
                return true;
            } catch (Exception ex) {
                stopConnection();
                Log.d(TAG, this.toString() + "send()->" + "Exception ex: " + ex.getMessage());
            }
            finally {
                // Prelevare qui la differenza
                // System.currentTimeMillis() - m_timeMillisecondsSend;
            }
        }

        Log.d(TAG, this.toString() + "send() return false");
        return false;
    }

    private synchronized boolean receive() {
        Log.d(TAG, this.toString() + "receive() enter");

        m_timeMillisecondsReceive = System.currentTimeMillis();

        if (m_dataInputStream != null && m_ticd != null) {
            if (m_ticd.getProtocolID() == TCPIPClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                // MBAP
                byte[] byteMBAP = new byte[6];
                try {
                    m_dataInputStream.readFully(byteMBAP, 0, 6);
                    // Rest of message
                    int iLength;
                    try {
                        iLength = Modbus.getMessageLengthFromMBAP(m_context, byteMBAP);
                        iLength = iLength - 6;
                        byte[] byteDATA = new byte[iLength];
                        try {
                            m_dataInputStream.readFully(byteDATA, 0, iLength);
                            try {
                                Modbus.getMessageDATA(m_context, m_ticd.getID(), byteMBAP, byteDATA);

//                                m_timeMillisecondsGet = System.currentTimeMillis();
                                Log.d(TAG, this.toString() + "receive() return true. Time(ms):" + (System.currentTimeMillis() - m_timeMillisecondsReceive));
                                return true;

                            } catch (ModbusProtocolOutOfRangeException ex) {
                                stopConnection();
                                Log.d(TAG, this.toString() + "receive()->" + "ModbusProtocolOutOfRangeException ex: " + ex.getMessage());
                            } catch (ModbusMBAPLengthException ex) {
                                stopConnection();
                                Log.d(TAG, this.toString() + "receive()->" + "ModbusMBAPLengthException ex: " + ex.getMessage());
                            } catch (ModbusLengthOutOfRangeException ex) {
                                stopConnection();
                                Log.d(TAG, this.toString() + "receive()->" + "ModbusLengthOutOfRangeException ex: " + ex.getMessage());
                            }

                        } catch (SocketTimeoutException stex) {
                            //Modbus.callTcpIpServerModbusOperationTimeoutCallback(m_ticd.getID());
                            Log.d(TAG, this.toString() + "receive() DATA->" + "SocketTimeoutException stex: " + stex.getMessage());
                        } catch (EOFException eofex) {
                            Log.d(TAG, this.toString() + "receive() DATA->" + "EOFException eofex: " + eofex.getMessage());
                        } catch (IOException ioex) {
                            Log.d(TAG, this.toString() + "receive() DATA->" + "IOException ioex: " + ioex.getMessage());
                        } catch (Exception ex) {
                            Log.d(TAG, this.toString() + "receive() DATA->" + "Exception ex: " + ex.getMessage());
                        }

                    } catch (ModbusProtocolOutOfRangeException ex) {
                        stopConnection();
                        Log.d(TAG, this.toString() + "receive()->" + "ModbusProtocolOutOfRangeException ex: " + ex.getMessage());
                    } catch (ModbusMBAPLengthException ex) {
                        stopConnection();
                        Log.d(TAG, this.toString() + "receive()->" + "ModbusMBAPLengthException ex: " + ex.getMessage());
                    } catch (ModbusLengthOutOfRangeException ex) {
                        stopConnection();
                        Log.d(TAG, this.toString() + "receive()->" + "ModbusLengthOutOfRangeException ex: " + ex.getMessage());
                    }

                } catch (SocketTimeoutException stex) {
                    //Modbus.callTcpIpServerModbusOperationTimeoutCallback(m_ticd.getID());
                    Log.d(TAG, this.toString() + "receive() MBAP->" + "SocketTimeoutException stex: " + stex.getMessage());
                } catch (EOFException eofex) {
                    Log.d(TAG, this.toString() + "receive() MBAP->" + "EOFException eofex: " + eofex.getMessage());
                } catch (IOException ioex) {
                    Log.d(TAG, this.toString() + "receive() MBAP->" + "IOException ioex: " + ioex.getMessage());
                } catch (Exception ex) {
                    Log.d(TAG, this.toString() + "receive() MBAP->" + "Exception ex: " + ex.getMessage());
                }

            }
        }

        Log.d(TAG, this.toString() + "receive() return false");
        return false;
    }

    private synchronized void stopConnection() {

        Log.d(TAG, this.toString() + "stopConnection() enter");

        if(m_ticd != null) {
            // Callbacks
            sendTcpIpClientStatusCallback(Status.SERVER_DISCONNECTING);
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

        if(m_sbyte != null) {
            m_sbyte.clear();
        }

        if(m_ticd != null) {
            // Callbacks
            sendTcpIpClientStatusCallback(Status.SERVER_OFF_LINE);
        }

        m_bSocketOpen = false;

        Log.d(TAG, this.toString() + "stopConnection() return");
    }

    /*
     * Writing/Reading Function
     */
    public synchronized void writeSwitchValue(int lID, int iAddress, int iValue){
        if(m_ticd != null) {
            if(m_ticd.getProtocolID() == TCPIPClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                byte[] byteToSend = null;
                try {
                    byteToSend = Modbus.writeSingleRegister(m_context, lID, 0,  iAddress, iValue);
                    if (m_sbyte != null && byteToSend != null) {
                        m_sbyte.push(byteToSend);

                        return;
                    }
                } catch (ModbusTransIdOutOfRangeException mtioorex) {
                    Log.d(TAG, this.toString() + "ModbusTransIdOutOfRangeException mtioorex: " + mtioorex.getMessage());
                } catch (ModbusUnitIdOutOfRangeException muioorex) {
                    Log.d(TAG, this.toString() + "ModbusUnitIdOutOfRangeException muioorex: " + muioorex.getMessage());
                } catch (ModbusAddressOutOfRangeException maoorex) {
                    Log.d(TAG, this.toString() + "ModbusAddressOutOfRangeException maoorex: " + maoorex.getMessage());
                } catch (ModbusValueOutOfRangeException mvoorex) {
                    Log.d(TAG, this.toString() + "ModbusValueOutOfRangeException mvoorex: " + mvoorex.getMessage());
                }
            }
        }

        // Callbacks
        sendWriteSwitchValueCallback(lID, Status.WRITE_LIGTH_SWITCH_VALUE_ERROR);
    }

    @Override
    public void onWriteSingleRegisterOkCallback(int iTransactionIdentifier) {
        sendWriteSwitchValueCallback(iTransactionIdentifier, Status.WRITE_LIGTH_SWITCH_VALUE_OK);
    }

    @Override
    public void onWriteSingleRegisterExceptionCallback(int iTransactionIdentifier, int iErrorCode) {
        sendWriteSwitchValueCallback(iTransactionIdentifier, Status.WRITE_LIGTH_SWITCH_VALUE_ERROR);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(Object... obj) {
        Log.d(TAG, this.toString() + "doInBackground() enter");

        // Listener
        Modbus.registerListener(this);

        m_ticd = (TCPIPClientData) obj[0];

        try {
            while (!isCancelled() && m_ticd != null) {

                if (!isConnected()) {
                    // Start communication with Server
                    if (!startConnection()) {
                        // attendo per non sovraccaricare CPU
                        try {
                            Thread.sleep(3000, 0);
                        } catch (InterruptedException ignored) {
                        }
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
                }
            }
        } catch (Exception ex) {
            Log.d(TAG, this.toString() + "doInBackground()->" + "IOException ex: " + ex.getMessage());
        }

        // Closing...
        stopConnection();

        // Listener
        Modbus.unregisterListener(this);

        Log.d(TAG, this.toString() + "doInBackground() return");
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
    }

    public static enum Status {
        SERVER_IDLE,
        SERVER_OFF_LINE,
        SERVER_CONNECTING,
        SERVER_ON_LINE,
        SERVER_DISCONNECTING,
        WRITE_LIGTH_SWITCH_VALUE_OK,
        WRITE_LIGTH_SWITCH_VALUE_ERROR;
        ;
    }

    /*
     * Send callbacks
     */
    private void sendWriteSwitchValueCallback(int iTransactionIdentifier, Status sStatus){
        if(m_vListener != null) {
            for (TCPIPClientListener ticl : m_vListener) {
                ticl.onWriteSwitchValueCallback(iTransactionIdentifier, sStatus);
            }
        }
    }
    private void sendTcpIpClientStatusCallback(Status sStatus){
        if(m_vListener != null) {
            for (TCPIPClientListener ticl : m_vListener) {
                ticl.onTcpIpClientStatusCallback(sStatus);
            }
        }
    }

    /**
     * Callbacks interface.
     */
    public static interface TCPIPClientListener {
        /**
         * Callbacks
         */
        void onWriteSwitchValueCallback(int iTransactionIdentifier, Status sStatus);
        void onTcpIpClientStatusCallback(Status sStatus);
//        void onWriteSingleRegisterExceptionCallback(long lProtTcpIpClientID, int iTransactionIdentifier, int iEC, int iExC);
//        void onTcpIpServerModbusOperationTimeoutCallback(long lProtTcpIpClientID);
//        void onTcpIpServerModbusStatusCallback(long lProtTcpIpClientID, TCPIPClient.Status tics);
    }
}