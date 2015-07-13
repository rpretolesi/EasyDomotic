package com.pretolesi.easydomotic.TcpIpClient;

import android.content.Context;

import com.pretolesi.easydomotic.CommClientData.BaseCommClient;
import com.pretolesi.easydomotic.CommClientData.TranspProtocolData;
import com.pretolesi.easydomotic.Modbus.ModbusByteCountOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusCRCException;
import com.pretolesi.easydomotic.Modbus.ModbusPDULengthOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusMBAPLengthException;
import com.pretolesi.easydomotic.Modbus.ModbusProtocolOutOfRangeException;
import com.pretolesi.easydomotic.Modbus.ModbusUnitIdOutOfRangeException;
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
import java.util.Arrays;

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
            return true;
        }

        // Callbacks on UI
        publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.OFFLINE, ""));
        return false;
    }

    @Override
    protected boolean send() {
        super.send();
        TcpIpMsg tim = getMsgToSend();

        return sendMsg(tim);
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

        // Check for sent message
        TcpIpMsg tim = getMsgSent();

        // No message sent, nothing to receive
        if(tim == null) {
            return true;
        }

        // Modbus
        if((m_ticd.getCommProtocolType() == TranspProtocolData.CommProtocolType.MODBUS_ON_TCP_IP)) {
            try {
                Arrays.fill(m_byteMBAP, (byte) 0);
                m_dataInputStream.readFully(m_byteMBAP, 0, m_byteMBAP.length);
                // Rest of message
                short shLength;
                try {
                    ModbusMBAP mmbap = Modbus.getMBAP(m_context, m_byteMBAP);
                    if (mmbap != null) {
                        shLength = mmbap.getLength();
                        try {
                            Arrays.fill(m_bytePDU, (byte) 0);
                            m_dataInputStream.readFully(m_bytePDU, 0, shLength);
                            try {
                                ModbusPDU mpdu = Modbus.getPDU(m_context, m_bytePDU, shLength, false);
                                if (mpdu != null) {

                                    setOnLineProgressStatusBar();

                                    // Tutto Ok, rimuovo l'elemento
                                    TcpIpMsg timRemoved = removeMsg((short) mmbap.getTID(), mpdu.getUID());
                                    DataType dtDataType = null;
                                    if (timRemoved != null) {
                                        dtDataType = timRemoved.getDataType();
                                    }

                                    if (mpdu.getFEC() == 0x10 || mpdu.getFEC() == 0x90) {

                                        // Check Return code
                                        if (mpdu.getExCID() == 0) {
                                            publishProgress(new TcpIpClientWriteStatus(getID(), mmbap.getTID(), mpdu.getUID(), TcpIpClientWriteStatus.Status.OK, 0, ""));
                                        } else {
                                            publishProgress(new TcpIpClientWriteStatus(getID(), mmbap.getTID(), mpdu.getUID(), TcpIpClientWriteStatus.Status.ERROR, mpdu.getExCID(), ""));
                                        }
                                    }

                                    if (mpdu.getFEC() == 0x03 || mpdu.getFEC() == 0x83) {
                                        // Check Return code
                                        if (mpdu.getExCID() == 0 && dtDataType != null && mpdu.getPDUValue() != null) {
                                            publishProgress(new TcpIpClientReadStatus(getID(), mmbap.getTID(), mpdu.getUID(), TcpIpClientReadStatus.Status.OK, 0, "", getValue(dtDataType, mpdu.getPDUValue())));
                                        } else {
                                            publishProgress(new TcpIpClientReadStatus(getID(), mmbap.getTID(), mpdu.getUID(), TcpIpClientReadStatus.Status.ERROR, mpdu.getExCID(), "", null));
                                        }
                                    }

                                    return true;
                                }
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

                        } catch (SocketTimeoutException ex) {
                            // Callbacks on UI
                            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.TIMEOUT, ex.getMessage()));
                        } catch (EOFException ex) {
                            // Callbacks on UI
                            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                        } catch (IOException ex) {
                            // Callbacks on UI
                            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                        } catch (Exception ex) {
                            // Callbacks on UI
                            publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                        }
                    }

                } catch (ModbusProtocolOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                } catch (ModbusMBAPLengthException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
                    // Log.d(TAG, this.toString() + "receive()->" + "ModbusMBAPLengthException ex: " + ex.getMessage());
                } catch (ModbusPDULengthOutOfRangeException ex) {
                    // Callbacks on UI
                    publishProgress(new TcpIpClientStatus(getID(), getName(), TcpIpClientStatus.Status.ERROR, ex.getMessage()));
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
        }

        // The TCP/IP it's a rely protocol.
        // If an exception happens, i prefer to close and connect it again
        m_bRestartConnection = true;
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
}