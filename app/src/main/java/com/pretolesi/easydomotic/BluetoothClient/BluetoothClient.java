package com.pretolesi.easydomotic.BluetoothClient;

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
import java.util.Vector;

/**
 *
 */

public class BluetoothClient extends BaseCommClient {
    private static final String TAG = "BluetoothClient";

    public BluetoothClient(Context context){
        super(context);
    }

    @Override
    protected boolean startConnection() {
        super.startConnection();
/*
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
*/
        return false;
    }

}