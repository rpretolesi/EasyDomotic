package com.pretolesi.easydomotic.TcpIpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pretolesi.easydomotic.Modbus.ModbusAddressOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusByteCountOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusLengthOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusMBAPLengthException;
import com.pretolesi.easydomotic.Modbus.ModbusPDULengthException;
import com.pretolesi.easydomotic.Modbus.ModbusProtocolOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusQuantityOfRegistersOutOfRange;
import com.pretolesi.easydomotic.Modbus.ModbusTransIdOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusUnitIdOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusValueOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.Modbus;
import com.pretolesi.easydomotic.Modbus.ModbusMBAP;
import com.pretolesi.easydomotic.Modbus.ModbusPDU;
import com.pretolesi.easydomotic.NumerValue.NumericValueData;

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
    // Client
    private List<TcpIpClientStatusListener> m_vTcpIpClientStatusListener = null;
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
    private List<TcpIpClientWriteSwitchStatusListener> m_vTcpIpClientWriteSwitchStatusListener = null;
    // Imposto il listener
    public synchronized void registerTcpIpClientWriteSwitchStatus(TcpIpClientWriteSwitchStatusListener listener) {
        if(m_vTcpIpClientWriteSwitchStatusListener != null && !m_vTcpIpClientWriteSwitchStatusListener.contains(listener)){
            m_vTcpIpClientWriteSwitchStatusListener.add(listener);
        }
    }
    public synchronized void unregisterTcpIpClientWriteSwitchStatus(TcpIpClientWriteSwitchStatusListener listener) {
        if(m_vTcpIpClientWriteSwitchStatusListener != null && m_vTcpIpClientWriteSwitchStatusListener.contains(listener)){
            m_vTcpIpClientWriteSwitchStatusListener.remove(listener);
        }
    }

    // Read
    private List<TcpIpClientReadValueStatusListener> m_vTcpIpClientReadValueStatusListener = null;
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
        m_vTcpIpClientStatusListener = new Vector<>();
        m_vTcpIpClientWriteSwitchStatusListener = new Vector<>();
        m_vTcpIpClientReadValueStatusListener = new Vector<>();
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
        for (Iterator<TcpIpMsg> iterator = m_vtim.iterator(); iterator.hasNext();) {
            TcpIpMsg tim = iterator.next();
            if (tim != null) {
                if (System.currentTimeMillis() - tim.getSentTimeMS() >= m_ticd.getTimeout()) {
                    tim.setMsgTimeMSNow();
                    publishProgress(new TcpIpClientWriteStatus(getID(), (int) tim.getTID(), (int) tim.getUID(), TcpIpClientWriteStatus.Status.TIMEOUT, 0, ""));
                    publishProgress(new TcpIpClientReadStatus(getID(), (int) tim.getTID(), (int) tim.getUID(), TcpIpClientReadStatus.Status.TIMEOUT, 0, "", null));
                }
            }
        }

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
            boolean bMsgSent = false;
            for (Iterator<TcpIpMsg> iterator = m_vtim.iterator(); iterator.hasNext();) {
                TcpIpMsg tim = iterator.next();
                if (tim != null) {
                    if(tim.getMsgSent()) {
                        bMsgSent = true;
                    }
                }
            }

            if (bMsgSent) {
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
                                            for (Iterator<TcpIpMsg> iterator = m_vtim.iterator(); iterator.hasNext();) {
                                                TcpIpMsg tim = iterator.next();
                                                if (tim != null && tim.getTID() == mmbap.getTID() && tim.getUID() == mpdu.getUID()) {
                                                    iterator.remove();
                                                }
                                            }

                                            if(mpdu.getFEC() == 0x06 || mpdu.getFEC() == 0x86) {
                                                // Check Return code
                                                if (mpdu.getExC() == 0) {
                                                    publishProgress(new TcpIpClientWriteStatus(getID(), mmbap.getTID(), mpdu.getUID(), TcpIpClientWriteStatus.Status.OK, 0, ""));
                                                } else {
                                                    publishProgress(new TcpIpClientWriteStatus(getID(), mmbap.getTID(), mpdu.getUID(), TcpIpClientWriteStatus.Status.ERROR, mpdu.getExC(), ""));
                                                }
                                            }

                                            if(mpdu.getFEC() == 0x03 || mpdu.getFEC() == 0x83) {
                                                // Check Return code
                                                if (mpdu.getExC() == 0) {
                                                    publishProgress(new TcpIpClientReadStatus(getID(), mmbap.getTID(), mpdu.getUID(), TcpIpClientReadStatus.Status.OK, 0, "", mpdu.getByteValue()));
                                                } else {
                                                    publishProgress(new TcpIpClientReadStatus(getID(), mmbap.getTID(), mpdu.getUID(), TcpIpClientReadStatus.Status.ERROR, mpdu.getExC(), "", mpdu.getByteValue()));
                                                }
                                            }

                                            // m_timeMillisecondsGet = System.currentTimeMillis();
                                            Log.d(TAG, this.toString() + "receive() return true. Time(ms):" + (System.currentTimeMillis() - m_timeMillisecondsReceive));
                                            return true;
                                        }
                                    } catch (ModbusProtocolOutOfRangeException ex) {
                                        // Callbacks on UI
                                        publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                        Log.d(TAG, this.toString() + "receive()->" + "ModbusProtocolOutOfRangeException ex: " + ex.getMessage());
                                    } catch (ModbusMBAPLengthException ex) {
                                        // Callbacks on UI
                                        publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                        Log.d(TAG, this.toString() + "receive()->" + "ModbusMBAPLengthException ex: " + ex.getMessage());
                                    } catch (ModbusLengthOutOfRangeException ex) {
                                        // Callbacks on UI
                                        publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                        Log.d(TAG, this.toString() + "receive()->" + "ModbusLengthOutOfRangeException ex: " + ex.getMessage());
                                    } catch (ModbusPDULengthException ex) {
                                        // Callbacks on UI
                                        publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                        Log.d(TAG, this.toString() + "receive()->" + "ModbusPDULengthException ex: " + ex.getMessage());
                                    } catch (ModbusByteCountOutOfRangeException ex) {
                                        // Callbacks on UI
                                        publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                        Log.d(TAG, this.toString() + "receive()->" + "ModbusByteCountOutOfRangeException ex: " + ex.getMessage());
                                    }

                                } catch (SocketTimeoutException ex) {
                                    // Callbacks on UI
                                    publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.TIMEOUT, ex.getMessage()));
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
                            publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                            Log.d(TAG, this.toString() + "receive()->" + "ModbusProtocolOutOfRangeException ex: " + ex.getMessage());
                        } catch (ModbusMBAPLengthException ex) {
                            // Callbacks on UI
                            publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                            Log.d(TAG, this.toString() + "receive()->" + "ModbusMBAPLengthException ex: " + ex.getMessage());
                        } catch (ModbusLengthOutOfRangeException ex) {
                            // Callbacks on UI
                            publishProgress(new TcpIpClientStatus(getID(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
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
    public synchronized void writeSwitchValue(int iTID, int iUID, int iAddress, int iValue){
        if(m_ticd != null) {
            if(m_ticd.getProtocolID() == TCPIPClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                try {
                    TcpIpMsg tim = Modbus.writeSingleRegister(m_context, iTID, iUID,  iAddress, iValue);
                    if (m_vtim != null && tim != null) {
                        if(!m_vtim.contains(tim)){
                            m_vtim.add(tim);
                        }
                    }
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    Log.d(TAG, this.toString() + "ModbusTransIdOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    Log.d(TAG, this.toString() + "ModbusUnitIdOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    Log.d(TAG, this.toString() + "ModbusAddressOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientWriteStatus(getID(), iTID, iUID, TcpIpClientWriteStatus.Status.ERROR, 0, ex.getMessage()));
                    Log.d(TAG, this.toString() + "ModbusValueOutOfRangeException ex: " + ex.getMessage());
                }
            }
        }
    }

    public synchronized void readNumericValue(int iTID, int iUID, int iAddress, NumericValueData.DataType dtDataType){
        if(m_ticd != null) {
            if(m_ticd.getProtocolID() == TCPIPClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                TcpIpMsg tim = null;
                try {
                    switch (dtDataType) {
                        case SHORT16:
                            tim = Modbus.readHoldingRegisters(m_context, iTID, iUID, iAddress, 1);
                            break;

                        case INT32:
                        case FLOAT32:
                            tim = Modbus.readHoldingRegisters(m_context, iTID, iUID, iAddress, 2);
                            break;

                        case LONG64:
                        case DOUBLE64:
                            tim = Modbus.readHoldingRegisters(m_context, iTID, iUID, iAddress, 4);
                            break;
                    }
                    if (m_vtim != null && tim != null) {
                        if(!m_vtim.contains(tim)){
                            m_vtim.add(tim);
                        }
                    }
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                    Log.d(TAG, this.toString() + "ModbusTransIdOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                    Log.d(TAG, this.toString() + "ModbusUnitIdOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                    Log.d(TAG, this.toString() + "ModbusAddressOutOfRangeException ex: " + ex.getMessage());
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                    Log.d(TAG, this.toString() + "ModbusQuantityOfRegistersOutOfRange ex: " + ex.getMessage());
                }
            }
        }
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(Object... obj) {
        Log.d(TAG, this.toString() + "doInBackground() enter");

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

        Log.d(TAG, this.toString() + "doInBackground() return");
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
                if(m_vTcpIpClientWriteSwitchStatusListener != null) {
                    for (TcpIpClientWriteSwitchStatusListener ticwsl : m_vTcpIpClientWriteSwitchStatusListener) {
                        ticwsl.onWriteSwitchStatusCallback((TcpIpClientWriteStatus) obj[0]);
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
    public static interface TcpIpClientWriteSwitchStatusListener {
        /**
         * Callbacks
         */
        void onWriteSwitchStatusCallback(TcpIpClientWriteStatus ticws);
    }
    public static interface TcpIpClientReadValueStatusListener {
        /**
         * Callbacks
         */
        void onReadValueStatusCallback(TcpIpClientReadStatus ticrs);
    }
}