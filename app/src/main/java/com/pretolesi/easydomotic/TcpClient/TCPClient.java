package com.pretolesi.easydomotic.TcpClient;

import android.content.Context;
import android.util.Log;

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
public class TCPClient {
    private static final String TAG = "TCPClient";

    private Context m_context = null;
    private Socket m_clientSocket = null;
    private Protocol m_protocol;
    private SocketAddress m_socketAddress = null;
    private DataOutputStream m_dataOutputStream = null;
    private DataInputStream m_dataInputStream = null;

    private long m_timeMillisecondsSend = 0;
    private long m_timeMillisecondsGet = 0;

    public TCPClient(Context context, Protocol protocol){
        m_context = context;
        m_protocol = protocol;
    }

    public boolean startConnection(String strHost, int iPort, int iTimeout)
    {
        boolean bRes = false;
        try
        {
            // Prima chiudo la connessione
            closeConnection();

            m_socketAddress = new InetSocketAddress(strHost , iPort);
            if(m_clientSocket == null)
            {
                m_clientSocket = new Socket();
                m_clientSocket.setSoTimeout(iTimeout);
                m_clientSocket.connect(m_socketAddress);
                m_dataOutputStream = new DataOutputStream(m_clientSocket.getOutputStream());
                m_dataInputStream = new DataInputStream(m_clientSocket.getInputStream());

                m_timeMillisecondsSend = System.currentTimeMillis();
                m_timeMillisecondsGet = System.currentTimeMillis();

                bRes = true;
            }
/*
            m_byteInputStreamBuf = new byte[16];
            m_NrOfByteInInputStreamBuf = 0;
            m_bWaitingForData = false;
*/
        }
        catch (Exception ex)
        {
            Log.d(TAG,this.toString() + "startConnection()->" + "Exception ex: " + ex.getMessage());
            closeConnection();
        }

        Log.d(TAG,this.toString() + "startConnection()");

        return bRes;
    }

    public boolean isConnected() {
        boolean bRes = false;
        if (m_clientSocket != null && m_dataInputStream != null && m_dataOutputStream != null) {
            bRes = m_clientSocket.isConnected();
        }
        return bRes;
    }

    public boolean send(byte[] byteToSend)
    {
        boolean bRes = false;

        if (m_dataOutputStream != null)       {
            try
            {
                if(!m_bWaitingForData)
                {
                    if(byteToSend != null) {
                        m_dataOutputStream.write(byteToSend, 0, byteToSend.length);
                        m_bWaitingForData = true;
                    }
                }
                bRes = true;
            }
            catch (Exception ex) {
                Log.d(TAG,this.toString() + "send()->" + "Exception ex: " + ex.getMessage());
            }
        }

        m_timeMillisecondsSend = System.currentTimeMillis();

        return bRes;
    }
/*
    public byte[] getData()
    {
        if (m_dataInputStream != null)
        {
            try
            {
                int iByteRead = 0;

                iByteRead = m_dataInputStream.read(m_byteInputStreamBuf, m_NrOfByteInInputStreamBuf, m_byteInputStreamBuf.length - m_NrOfByteInInputStreamBuf);
                if(iByteRead > 0)
                {
                    m_NrOfByteInInputStreamBuf = m_NrOfByteInInputStreamBuf + iByteRead;
                    if(iByteRead != 16)
                    {
                        // Log.d(TAG, "getData->" + "(iByteRead > 0), iByteRead : " + iByteRead + ", m_NrOfByteInInputStreamBuf = " + m_NrOfByteInInputStreamBuf);
                    }

                    if(m_NrOfByteInInputStreamBuf == 16)
                    {
                        m_NrOfByteInInputStreamBuf = 0;
                        m_bWaitingForData = false;

                        if((m_byteInputStreamBuf[0] == ACK) && (m_byteInputStreamBuf[15] == EOT))
                        {
                            msg.setData(m_byteInputStreamBuf);
                            m_strLastError = "";
                            bRes = true;
                        }
                        else
                        {
                            // Error
                            // Log.d(TAG,"getData->" + "(m_byteInputStreamBuf[0] != ACK) || (m_byteInputStreamBuf[15] != EOT)");
                            m_strLastError = "Protocol Error.";
                            closeConnection(msg);
                        }
                        // Reset
                        Arrays.fill(m_byteInputStreamBuf, (byte) 0);
                    } else {
                        m_strLastError = "";
                        bRes = true;
                    }
                } else if(iByteRead < 0) {
                    // Log.d(TAG,"getData->" + "(iByteRead < 0)");
                    m_strLastError = "Stream closed";
                    bRes = false;
                    closeConnection(msg);
                } else {
                    // Log.d(TAG,"getData->" + "(iByteRead = 0)");
                    m_strLastError = "";
                    bRes = true;
                }

            } catch (SocketTimeoutException stex) {
                // Log.d(TAG,"getData->" + "SocketTimeoutException stex : " + stex.getMessage());
                m_strLastError = m_context.getString(R.string.comm_status_timeout);
                closeConnection(msg);
            } catch (EOFException eofex) {
                // Log.d(TAG,"getData->" + "EOFException eofex : " + eofex.getMessage());
                m_strLastError = m_context.getString(R.string.comm_status_eof);
                closeConnection(msg);
            } catch (Exception ex) {
                // Log.d(TAG,"getData->" + "Exception ex : " + ex.getMessage());
                m_strLastError = ex.getMessage();
                closeConnection(msg);
            }
        }

        m_timeMillisecondsGet = System.currentTimeMillis();

        return bRes;
    }
*/
    public void closeConnection()
    {

        m_socketAddress = null;

        // Chiudo il socket
        if(m_clientSocket != null)
        {
            try
            {
                m_clientSocket.close();
            } catch (IOException ioex_1)
            {
            }
        }
        m_clientSocket = null;

        // close Output stream
        if (m_dataOutputStream != null)
        {
            try
            {
                m_dataOutputStream.close();
            }
            catch (IOException ioex_2)
            {
            }
        }
        m_dataOutputStream = null;

        // close Input stream
        if (m_dataInputStream != null)
        {
            try
            {
                m_dataInputStream.close();
            }
            catch (IOException ioex_3)
            {
            }
        }
        m_dataInputStream = null;

        Log.d(TAG, this.toString() + "closeConnection()");
    }

    public static enum Protocol {
        TCP_IP_FREE,
        TCP_IP_MODBUS
    }

}
