package com.pretolesi.easydomotic.TcpIpClient;

import android.content.Context;

import com.pretolesi.easydomotic.CommClientData.BaseCommClient;
import com.pretolesi.easydomotic.CommClientData.BaseValueCommClientData;
import com.pretolesi.easydomotic.CustomControls.NumericDataType;
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
import com.pretolesi.easydomotic.CustomControls.NumericDataType.DataType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Iterator;

/**
 *
 */

//sistemare tutte le variabili che non servono e lasciare solo quelle che servono, a partire dallo switch!!!!
public class TCPIPClient extends BaseCommClient {
    private static final String TAG = "TCPIPClient";

    private Socket m_clientSocket = null;
    private SocketAddress m_socketAddress = null;

    public TCPIPClient(Context context) {
        super(context);
    }

    @Override
    protected boolean startConnection() {
        super.startConnection();

        if (m_ticd != null) {
            // Callbacks on UI
            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.CONNECTING, ""));
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
                    setAllMsgAsUnsent();

                    // Callbacks on UI
                    publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ONLINE, "" ));

                    return true;
                }
            } catch (Exception ex) {
                // Log.d(TAG, this.toString() + "startConnection()->" + "Exception ex: " + ex.getMessage());
                // Callbacks on UI
                publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.OFFLINE, ex.getMessage()));
            }
        }

        // Log.d(TAG, this.toString() + "startConnection() return false");

        return false;
    }

    @Override
    protected boolean isConnected() {
/*
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
*/
        checkTimeoutAndSetAllMsgAsUnsent();

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
            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ONLINE, strProgress ));
            return true;
        }

        // Callbacks on UI
        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.OFFLINE, ""));
        iProgressCounter = 0;
        return false;
    }
/*
    @Override
    protected  boolean send() {
        super.send();

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

                    // Log.d(TAG, this.toString() + "send() return true. Time(ms):" + (System.currentTimeMillis() - m_timeMillisecondsSend));
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

        // Log.d(TAG, this.toString() + "send() return false");
        return false;
    }
*/
    @Override
    protected boolean receive() {
        super.receive();

        if (m_dataInputStream != null && m_ticd != null && m_vtim != null) {

            // In case of timeout, i put the message in queue again
            boolean bMsgSent = false;
            for (Iterator<TcpIpMsg> iterator = m_vtim.iterator(); iterator.hasNext();) {
                TcpIpMsg tim = iterator.next();
                if (tim != null) {
                    if(tim.getMsgSent()) {
                        bMsgSent = true;
                    }
                }
            }

            if (bMsgSent){
                if (m_ticd.getProtocolID() == BaseValueCommClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                    // MBAP
                    byte[] byteMBAP = new byte[6];
                    try {
                        m_dataInputStream.readFully(byteMBAP, 0, 6);
                        // Rest of message
                        int iLength;
                        try {
                            ModbusMBAP mmbap = Modbus.getMBAP(m_context, byteMBAP);
                            if (mmbap != null) {
                                iLength = mmbap.getLength();
                                byte[] byteDATA = new byte[iLength];
                                try {
                                    m_dataInputStream.readFully(byteDATA, 0, iLength);
                                    try {
                                        ModbusPDU mpdu = Modbus.getPDU(m_context, byteMBAP, byteDATA);
                                        if (mpdu != null) {

                                            // Tutto Ok, rimuovo l'elemento
                                            DataType dtDataType = null;
                                            for (Iterator<TcpIpMsg> iterator = m_vtim.iterator(); iterator.hasNext(); ) {
                                                TcpIpMsg tim = iterator.next();
                                                if (tim != null && tim.getTID() == mmbap.getTID() && tim.getUID() == mpdu.getUID()) {
                                                    dtDataType = tim.getDataType();
                                                    iterator.remove();
                                                }
                                            }

                                            if (mpdu.getFEC() == 0x10 || mpdu.getFEC() == 0x90) {

                                                // Check Return code
                                                if (mpdu.getExC() == 0) {
                                                    publishProgress(new TcpIpClientWriteStatus(getID(), mmbap.getTID(), mpdu.getUID(), TcpIpClientWriteStatus.Status.OK, 0, ""));
                                                } else {
                                                    publishProgress(new TcpIpClientWriteStatus(getID(), mmbap.getTID(), mpdu.getUID(), TcpIpClientWriteStatus.Status.ERROR, mpdu.getExC(), ""));
                                                }
                                            }

                                            if (mpdu.getFEC() == 0x03 || mpdu.getFEC() == 0x83) {
                                                // Check Return code
                                                if (mpdu.getExC() == 0 && dtDataType != null && mpdu.getPDUValue() != null) {
                                                    publishProgress(new TcpIpClientReadStatus(getID(), mmbap.getTID(), mpdu.getUID(), TcpIpClientReadStatus.Status.OK, 0, "",  getValue(dtDataType, mpdu.getPDUValue())));
                                                } else {
                                                    publishProgress(new TcpIpClientReadStatus(getID(), mmbap.getTID(), mpdu.getUID(), TcpIpClientReadStatus.Status.ERROR, mpdu.getExC(), "", null));
                                                }
                                            }

                                            // m_timeMillisecondsGet = System.currentTimeMillis();
                                            // Log.d(TAG, this.toString() + "receive() return true. Time(ms):" + (System.currentTimeMillis() - m_timeMillisecondsReceive));
                                            return true;
                                        }

                                    } catch (ModbusProtocolOutOfRangeException ex) {
                                        // Callbacks on UI
                                        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                        // Log.d(TAG, this.toString() + "receive()->" + "ModbusProtocolOutOfRangeException ex: " + ex.getMessage());
                                    } catch (ModbusMBAPLengthException ex) {
                                        // Callbacks on UI
                                        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                        // Log.d(TAG, this.toString() + "receive()->" + "ModbusMBAPLengthException ex: " + ex.getMessage());
                                    } catch (ModbusLengthOutOfRangeException ex) {
                                        // Callbacks on UI
                                        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                        // Log.d(TAG, this.toString() + "receive()->" + "ModbusLengthOutOfRangeException ex: " + ex.getMessage());
                                    } catch (ModbusPDULengthException ex) {
                                        // Callbacks on UI
                                        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                        // Log.d(TAG, this.toString() + "receive()->" + "ModbusPDULengthException ex: " + ex.getMessage());
                                    } catch (ModbusByteCountOutOfRangeException ex) {
                                        // Callbacks on UI
                                        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                        // Log.d(TAG, this.toString() + "receive()->" + "ModbusByteCountOutOfRangeException ex: " + ex.getMessage());
                                    } catch (Exception ex) {
                                        // Callbacks on UI
                                        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                        // Log.d(TAG, this.toString() + "receive()->" + "Exception ex: " + ex.getMessage());
                                    }

                                } catch (SocketTimeoutException ex) {
                                    // Callbacks on UI
                                    publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.TIMEOUT, ex.getMessage()));
                                    // Log.d(TAG, this.toString() + "receive() DATA->" + "SocketTimeoutException ex: " + ex.getMessage());
                                } catch (EOFException ex) {
                                    // Callbacks on UI
                                    publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                    // Log.d(TAG, this.toString() + "receive() DATA->" + "EOFException ex: " + ex.getMessage());
                                } catch (IOException ex) {
                                    // Callbacks on UI
                                    publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                    // Log.d(TAG, this.toString() + "receive() DATA->" + "IOException ex: " + ex.getMessage());
                                } catch (Exception ex) {
                                    // Callbacks on UI
                                    publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                                    // Log.d(TAG, this.toString() + "receive() DATA->" + "Exception ex: " + ex.getMessage());
                                }
                            }

                        } catch (ModbusProtocolOutOfRangeException ex) {
                            // Callbacks on UI
                            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                            // Log.d(TAG, this.toString() + "receive()->" + "ModbusProtocolOutOfRangeException ex: " + ex.getMessage());
                        } catch (ModbusMBAPLengthException ex) {
                            // Callbacks on UI
                            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                            // Log.d(TAG, this.toString() + "receive()->" + "ModbusMBAPLengthException ex: " + ex.getMessage());
                        } catch (ModbusLengthOutOfRangeException ex) {
                            // Callbacks on UI
                            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                            // Log.d(TAG, this.toString() + "receive()->" + "ModbusLengthOutOfRangeException ex: " + ex.getMessage());
                        } catch (Exception ex) {
                            // Callbacks on UI
                            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                            // Log.d(TAG, this.toString() + "receive() ->" + "Exception ex: " + ex.getMessage());
                        }

                    } catch (SocketTimeoutException ex) {
                        // Callbacks on UI
                        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.TIMEOUT, ex.getMessage()));
                        // Log.d(TAG, this.toString() + "receive() MBAP->" + "SocketTimeoutException ex: " + ex.getMessage());
                    } catch (EOFException ex) {
                        // Callbacks on UI
                        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                        // Log.d(TAG, this.toString() + "receive() MBAP->" + "EOFException ex: " + ex.getMessage());
                    } catch (IOException ex) {
                        // Callbacks on UI
                        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                        // Log.d(TAG, this.toString() + "receive() MBAP->" + "IOException ex: " + ex.getMessage());
                    } catch (Exception ex) {
                        // Callbacks on UI
                        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                        // Log.d(TAG, this.toString() + "receive() MBAP->" + "Exception ex: " + ex.getMessage());
                    }

                } else {
                    return true;
                }
            } else {
                return true;

            }
        }
        return false;
    }

    @Override
    protected void stopConnection() {
        super.stopConnection();

        // Chiudo il socket
        if (m_clientSocket != null) {
            try {
                m_clientSocket.close();
            } catch (IOException ioex_1) {
                // Log.d(TAG, this.toString() + "stopConnection()->" + "IOException ioex_1: " + ioex_1.getMessage());
            }
        }
        m_clientSocket = null;

        m_socketAddress = null;

        // Callbacks on UI
        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.OFFLINE, "" ));
    }

    @Override
    public synchronized boolean writeValue(Context context, int iTID, int iUID, int iAddress, Object objValue){
        return super.writeValue(context, iTID, iUID, iAddress, objValue);
    }

    @Override
    public synchronized boolean writeValue(Context context, int iTID, int iUID, int iAddress, DataType dtDataType, String strValue){
        return super.writeValue(context, iTID, iUID, iAddress, dtDataType, strValue);
    }
/*
    @Override
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
*/
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

    private void readShort(Context context, int iTID, int iUID, int iAddress){
        if(m_ticd != null) {
            if(m_ticd.getProtocolID() == BaseValueCommClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                try {
                    TcpIpMsg tim = Modbus.readShort(context, iTID, iUID, iAddress);
                    if (m_vtim != null && tim != null) {
                        tim.setDataType(DataType.SHORT);
                        if(!m_vtim.contains(tim)){
                            m_vtim.add(tim);
                        }
                    }
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                }
            }
        }
    }

    private void readInt(Context context, int iTID, int iUID, int iAddress){
        if(m_ticd != null) {
            if(m_ticd.getProtocolID() == BaseValueCommClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                try {
                    TcpIpMsg tim = Modbus.readInt(context, iTID, iUID, iAddress);
                    if (m_vtim != null && tim != null) {
                        tim.setDataType(DataType.INT);
                        if(!m_vtim.contains(tim)){
                            m_vtim.add(tim);
                        }
                    }
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                }
            }
        }
    }

    private void readLong(Context context, int iTID, int iUID, int iAddress){
        if(m_ticd != null) {
            if(m_ticd.getProtocolID() == BaseValueCommClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                try {
                    TcpIpMsg tim = Modbus.readLong(context, iTID, iUID, iAddress);
                    if (m_vtim != null && tim != null) {
                        tim.setDataType(DataType.LONG);
                        if(!m_vtim.contains(tim)){
                            m_vtim.add(tim);
                        }
                    }
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                }
            }
        }
    }

    private void readFloat(Context context, int iTID, int iUID, int iAddress){
        if(m_ticd != null) {
            if(m_ticd.getProtocolID() == BaseValueCommClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                try {
                    TcpIpMsg tim = Modbus.readFloat(context, iTID, iUID, iAddress);
                    if (m_vtim != null && tim != null) {
                        tim.setDataType(DataType.FLOAT);
                        if(!m_vtim.contains(tim)){
                            m_vtim.add(tim);
                        }
                    }
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                }
            }
        }
    }

    private void readDouble(Context context, int iTID, int iUID, int iAddress){
        if(m_ticd != null) {
            if(m_ticd.getProtocolID() == BaseValueCommClientData.Protocol.MODBUS_ON_TCP_IP.getID()) {
                try {
                    TcpIpMsg tim = Modbus.readDouble(context, iTID, iUID, iAddress);
                    if (m_vtim != null && tim != null) {
                        tim.setDataType(DataType.DOUBLE);
                        if(!m_vtim.contains(tim)){
                            m_vtim.add(tim);
                        }
                    }
                } catch (ModbusUnitIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusAddressOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusValueOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusQuantityOfRegistersOutOfRange ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (ModbusTransIdOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                } catch (Exception ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientReadStatus(getID(), iTID, iUID, TcpIpClientReadStatus.Status.ERROR, 0, ex.getMessage(), null));
                }
            }
        }
    }

    @Override
    public synchronized void readValue(Context context, int iTID, int iUID, int iAddress, DataType dtDataType){
        if(dtDataType != null){
            switch (dtDataType) {
                case SHORT:
                    readShort(context, iTID, iUID, iAddress);
                    break;

                case INT:
                    readInt(context, iTID, iUID, iAddress);
                    break;

                case LONG:
                    readLong(context, iTID, iUID, iAddress);
                    break;

                case FLOAT:
                    readFloat(context, iTID, iUID, iAddress);
                    break;

                case DOUBLE:
                    readDouble(context, iTID, iUID, iAddress);
                    break;

            }
        }
    }
/*
    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(Object... obj) {
        super.doInBackground(obj);

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

        // Log.d(TAG, this.toString() + "doInBackground() return");
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
*/
}