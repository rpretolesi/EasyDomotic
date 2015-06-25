package com.pretolesi.easydomotic.BluetoothClient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.pretolesi.easydomotic.CommClientData.BaseCommClient;
import com.pretolesi.easydomotic.CustomControls.NumericDataType.DataType;
import com.pretolesi.easydomotic.CustomDataStream.ReadDataInputStream;
import com.pretolesi.easydomotic.Modbus.Modbus;
import com.pretolesi.easydomotic.Modbus.ModbusByteCountOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusCRCException;
import com.pretolesi.easydomotic.Modbus.ModbusPDULengthOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusPDU;
import com.pretolesi.easydomotic.Modbus.ModbusUnitIdOutOfRangeException;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientReadStatus;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientStatus;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientWriteStatus;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpMsg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 *
 */

public class BluetoothClient extends BaseCommClient implements ReadDataInputStream.ReadDataInputStreamListener {
    private static final String TAG = "BluetoothClient";

    public static final UUID SSP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ReadDataInputStream m_rdis;
    protected boolean m_bDataInputStreamReady;
    protected long m_lDataInputStreamReadyTime;

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
            m_bDataInputStreamReady = false;

            m_iProgressCounter = 0;

            // Start Read
            m_rdis = new ReadDataInputStream(m_dataInputStream, (short)256);
            m_rdis.registerReadDataInputStream(this);
            m_rdis.start();

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

        checkTimeoutAndSetAllMsgAsUnsent();

        if(m_btSocket != null && m_dataInputStream != null && m_dataOutputStream != null && m_btSocket.isConnected()){
            m_iProgressCounter = m_iProgressCounter + 1;
            if(m_iProgressCounter > 16) {
                m_iProgressCounter = 1;
            }
            String strProgress = "";
            for(int index = 0; index < m_iProgressCounter; index++){
                strProgress = strProgress + "-";
            }
            // Callbacks on UI
            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ONLINE, strProgress ));
            return true;
        }

        // Callbacks on UI
        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.OFFLINE, "" ));
        m_iProgressCounter = 0;
        return false;
    }

    @Override
    public void onReadDataInputStreamCallback() {
        m_bDataInputStreamReady = true;
        m_lDataInputStreamReadyTime = System.currentTimeMillis();
    }

    @Override
    public void onCloseReadDataInputStreamCallback() {

        m_bRestartConnection = true;
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

        if(m_rdis == null ) {
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

        // No Data to read
        // Ora attendo un certo tempo dalla ricezione dei primi dati per assicurarmi che tutti il messaggio sia completo
        // Imposto un tempo di 4 ms
        if(!m_bDataInputStreamReady || (m_lDataInputStreamReadyTime + m_ticd.getCommSendDelayData() >= System.currentTimeMillis())) {
            return true;
        }

        try {
            byte[] bytePDU = m_rdis.getData();
            m_bDataInputStreamReady = false;

            ModbusPDU mpdu = Modbus.getPDU(m_context, bytePDU, (short)bytePDU.length, true);
            if (mpdu == null) {
                return false;
            }

            // Controllo l'indirizzo
            if(tim.getUID() != mpdu.getUID()){
                publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, m_context.getString(R.string.ModbusUnitIdNotMatchingException)));
                return false;
            }

            // Tutto Ok, rimuovo l'elemento
            DataType dtDataType = null;
            for (Iterator<TcpIpMsg> iterator = m_vtim.iterator(); iterator.hasNext(); ) {
                TcpIpMsg tim_temp = iterator.next();
                if (tim_temp != null && tim_temp.getUID() == mpdu.getUID()) {
                    dtDataType = tim_temp.getDataType();
                    iterator.remove();
                }
            }

            if (mpdu.getFEC() == 0x10 || mpdu.getFEC() == 0x90) {
                // Check Return code
                if (mpdu.getExC() == 0) {
                    publishProgress(new TcpIpClientWriteStatus(getID(), (short)tim.getTID(), mpdu.getUID(), TcpIpClientWriteStatus.Status.OK, 0, ""));
                } else {
                    publishProgress(new TcpIpClientWriteStatus(getID(), (short)tim.getTID(), mpdu.getUID(), TcpIpClientWriteStatus.Status.ERROR, mpdu.getExC(), ""));
                }
            }

            if (mpdu.getFEC() == 0x03 || mpdu.getFEC() == 0x83) {
                // Check Return code
                if (mpdu.getExC() == 0 && dtDataType != null && mpdu.getPDUValue() != null) {
                    publishProgress(new TcpIpClientReadStatus(getID(), (short)tim.getTID(), mpdu.getUID(), TcpIpClientReadStatus.Status.OK, 0, "", getValue(dtDataType, mpdu.getPDUValue())));
                } else {
                    publishProgress(new TcpIpClientReadStatus(getID(), (short)tim.getTID(), mpdu.getUID(), TcpIpClientReadStatus.Status.ERROR, mpdu.getExC(), "", null));
                }
            }

            return true;

        } catch (ModbusPDULengthOutOfRangeException ex) {
            // Callbacks on UI
            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
        } catch (ModbusCRCException ex) {
            // Callbacks on UI
            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
        } catch (ModbusUnitIdOutOfRangeException ex) {
            // Callbacks on UI
            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
        } catch (ModbusByteCountOutOfRangeException ex) {
            // Callbacks on UI
            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
        } catch (Exception ex) {
            // Callbacks on UI
            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
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
            m_btSocket = null;
        }

        // Attendo che il Thread di lettura si arresti
        if (m_rdis != null) {
            m_rdis.interrupt();
            try {
                m_rdis.join();
            } catch (InterruptedException e) {
            }
            m_rdis = null;
        }

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