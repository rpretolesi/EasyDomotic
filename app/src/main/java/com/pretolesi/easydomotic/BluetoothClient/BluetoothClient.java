package com.pretolesi.easydomotic.BluetoothClient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;

import com.pretolesi.easydomotic.CommClientData.BaseCommClient;
import com.pretolesi.easydomotic.CommClientData.BaseValueCommClientData;
import com.pretolesi.easydomotic.CustomControls.NumericDataType;
import com.pretolesi.easydomotic.CustomControls.NumericDataType.DataType;
import com.pretolesi.easydomotic.Modbus.Modbus;
import com.pretolesi.easydomotic.Modbus.ModbusAddressOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusByteCountOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusLengthOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusMBAP;
import com.pretolesi.easydomotic.Modbus.ModbusMBAPLengthException;
import com.pretolesi.easydomotic.Modbus.ModbusPDU;
import com.pretolesi.easydomotic.Modbus.ModbusPDULengthException;
import com.pretolesi.easydomotic.Modbus.ModbusProtocolOutOfRangeException;
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
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

/**
 *
 */

public class BluetoothClient extends BaseCommClient {
    private static final String TAG = "BluetoothClient";

    public static final UUID SSP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter m_BluetoothAdapter;
    private BluetoothDevice m_btDevice;
    private BluetoothSocket m_btSocket;

    public BluetoothClient(Context context){
        super(context);
    }

    @Override
    protected boolean startConnection() {
        super.startConnection();
        m_BluetoothAdapter = null;
        m_btDevice = null;
        m_btSocket = null;

        // Quering for paired device
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (m_BluetoothAdapter == null) {
            // Device does not support Bluetooth
            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, "Device does not support Bluetooth"));
            return false;
        }
        // Stop discovering
        if (m_BluetoothAdapter.isDiscovering()) {
            m_BluetoothAdapter.cancelDiscovery();
        }

        Set<BluetoothDevice> pairedDevices = m_BluetoothAdapter.getBondedDevices();
        if(pairedDevices != null){
            // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    if(device != null){
                        if(device.getAddress().equals(getAddress())){
                            m_btDevice = device;
                        }
                    }
                }
            }
        }

        if (m_btDevice == null) {
            // Device does not support Bluetooth
            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, "Device selected is not paired. Please try to pair before use it."));
            return false;
        }

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            m_btSocket = m_btDevice.createRfcommSocketToServiceRecord(BluetoothClient.SSP_UUID);
        } catch (IOException ex) {
            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, "Cannot create Bluetooth Stream." + " " + ex.getMessage()));
            return false;
        }

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            m_btSocket.connect();

            m_dataOutputStream = new DataOutputStream(m_btSocket.getOutputStream());
            m_dataInputStream = new DataInputStream(m_btSocket.getInputStream());

            iProgressCounter = 0;

        } catch (IOException ex_1) {
            // Unable to connect; close the socket and get out
            try {
                m_btSocket.close();
            } catch (IOException ex_2) {
            }
            m_btSocket = null;
            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.OFFLINE, "Cannot establish a connection with:" + getName() + ", " + getAddress() + ", " + ex_1.getMessage() + "."));
            return false;
        }

        // Restore the operations not completed
        setAllMsgAsUnsent();

        // Callbacks on UI
        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ONLINE, "" ));

        return true;
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

        if(m_btSocket != null && m_dataInputStream != null && m_dataOutputStream != null && m_btSocket.isConnected()){
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
        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.OFFLINE, "" ));
        iProgressCounter = 0;
        return false;
    }

    @Override
    protected boolean receive() {
        super.receive();

        if (m_dataInputStream == null){
            return false;
        }
        if (m_ticd == null){
            return false;
        }
        if (m_vtim == null) {
            return false;
        }

        TcpIpMsg tim = null;
        for (Iterator<TcpIpMsg> iterator = m_vtim.iterator(); iterator.hasNext();) {
            TcpIpMsg tim_temp = iterator.next();
            if (tim_temp != null) {
                if(tim_temp.getMsgSent()) {
                    tim = tim_temp;
                }
            }
        }

        // No message sent, nothing to receive
        if(tim == null) {
            return true;
        }
fire qui
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
                                        if (mpdu.getExC() == 0 && dtDataType != null && mpdu.getByteValue() != null) {
                                            publishProgress(new TcpIpClientReadStatus(getID(), mmbap.getTID(), mpdu.getUID(), TcpIpClientReadStatus.Status.OK, 0, "",  getValue(dtDataType, mpdu.getByteValue())));
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

        return false;
    }

    @Override
    protected void stopConnection() {
        super.stopConnection();

        // Chiudo il socket
        if (m_btSocket != null) {
            try {
                m_btSocket.close();
            } catch (IOException ioex_1) {
            }
        }
        m_btSocket = null;

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
}