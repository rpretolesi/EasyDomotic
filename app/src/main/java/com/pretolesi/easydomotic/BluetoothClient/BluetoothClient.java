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

    public synchronized boolean writeValue(Context context, int iTID, int iUID, int iAddress, Object objValue){
        return super.writeValue(context, 0, iUID, iAddress, objValue);
    }
}