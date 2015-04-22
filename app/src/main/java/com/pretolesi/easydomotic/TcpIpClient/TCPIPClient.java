package com.pretolesi.easydomotic.TcpIpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pretolesi.easydomotic.CustomException.ModbusLengthOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusMBAPLengthException;
import com.pretolesi.easydomotic.CustomException.ModbusProtocolOutOfRangeException;
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
public class TCPIPClient extends AsyncTask<Object, Void, Void> {
    private static final String TAG = "TCPIPClient";

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

    private long m_timeMillisecondsSend = 0;
    private long m_timeMillisecondsReceive = 0;

    public TCPIPClient (Context context){
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

    public synchronized void sendMessage(byte[] byteDATA) {
        if (m_sbyte != null && byteDATA != null) {
            m_sbyte.push(byteDATA);
        }
    }

    private boolean startConnection() {
        // Prima chiudo la connessione
        if(m_bSocketOpen){
            stopConnection();
        }

        if (m_ticd != null) {
            Modbus.callTcpIpServerModbusStatusCallback(Status.CONNECTING);
            try {
                m_socketAddress = new InetSocketAddress(m_ticd.getAddress(), m_ticd.getPort());
                if (m_clientSocket == null) {
                    m_sbyte = new Stack<>();
                    m_clientSocket = new Socket();
                    m_clientSocket.setSoTimeout(m_ticd.getTimeout());
                    m_clientSocket.connect(m_socketAddress);
                    m_dataOutputStream = new DataOutputStream(m_clientSocket.getOutputStream());
                    m_dataInputStream = new DataInputStream(m_clientSocket.getInputStream());

                    m_timeMillisecondsSend = System.currentTimeMillis();
                    m_timeMillisecondsReceive = System.currentTimeMillis();

                    Log.d(TAG, this.toString() + "startConnection()");

                    m_bSocketOpen = true;

                    Modbus.callTcpIpServerModbusStatusCallback(Status.ON_LINE);

                    return true;
                }
            } catch (Exception ex) {
                Log.d(TAG, this.toString() + "startConnection()->" + "Exception ex: " + ex.getMessage());
                stopConnection();
            }
        }

        return false;
    }

    private boolean isConnected() {
        return m_clientSocket != null && m_dataInputStream != null && m_dataOutputStream != null && m_clientSocket.isConnected();
    }

    private boolean send() {
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
                return true;
            } catch (EmptyStackException ESex) {
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

        return false;
    }

    private boolean receive() {
        m_timeMillisecondsReceive = System.currentTimeMillis();

        if (m_dataInputStream != null && m_ticd != null) {
            if (m_ticd.getProtocolID() == TCPIPClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                // MBAP
                byte[] byteMBAP = new byte[10];
                try {
                    m_dataInputStream.readFully(byteMBAP, 0, 10);
                    // Rest of message
                    int iLength;
                    try {
                        iLength = Modbus.getMessageLengthFromMBAP(m_context, byteMBAP);
                        byte[] byteDATA = new byte[iLength];
                        try {
                            m_dataInputStream.readFully(byteDATA, 0, iLength);
                            try {
                                Modbus.getMessageDATA(m_context, byteMBAP, byteDATA);

//                                m_timeMillisecondsGet = System.currentTimeMillis();

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

                        } catch (IOException ex) {
                            stopConnection();
                            Log.d(TAG, this.toString() + "receive()->" + "IOException ex: " + ex.getMessage());
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
                    Modbus.callTcpIpServerModbusOperationTimeoutCallback();
                    Log.d(TAG, this.toString() + "receive()->" + "SocketTimeoutException stex: " + stex.getMessage());
                } catch (EOFException eofex) {
                    Log.d(TAG, this.toString() + "receive()->" + "EOFException eofex: " + eofex.getMessage());
                } catch (IOException ioex) {
                    Log.d(TAG, this.toString() + "receive()->" + "IOException ioex: " + ioex.getMessage());
                } catch (Exception ex) {
                    Log.d(TAG, this.toString() + "receive()->" + "Exception ex: " + ex.getMessage());
                }

            }
        }

        return false;
    }

    private void stopConnection() {

        Modbus.callTcpIpServerModbusStatusCallback(Status.DISCONNECTING);

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

        Modbus.callTcpIpServerModbusStatusCallback(Status.OFF_LINE);

        m_bSocketOpen = false;

        Log.d(TAG, this.toString() + "stopConnection()");
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(Object... obj) {
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

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
    }

    public static enum Status {
        IDLE,
        OFF_LINE,
        CONNECTING,
        ON_LINE,
        DISCONNECTING;
    }
}