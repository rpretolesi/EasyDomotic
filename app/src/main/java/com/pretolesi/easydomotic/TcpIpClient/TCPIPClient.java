package com.pretolesi.easydomotic.TcpIpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

/**
 *
 */
public class TCPIPClient extends AsyncTask<Object, Void, Void> {
    private static final String TAG = "TCPIPClient";

    private Context m_context = null;
    private Stack<TcpIpClientProtocol> m_sticp = null;
    private TCPIPClientData m_ticd = null;
    private Socket m_clientSocket = null;
    private SocketAddress m_socketAddress = null;
    private DataOutputStream m_dataOutputStream = null;
    private DataInputStream m_dataInputStream = null;

    private TcpIpClientProtocol m_protocol;
    private int m_SendDataField_1;
    private int m_SendDataField_2;
    private int m_SendDataField_3;
    private int m_SendDataField_4;
    private int m_SendDataField_5;


    private long m_timeMillisecondsSend = 0;
    private long m_timeMillisecondsGet = 0;

    private boolean startConnection()
    {
        if(m_ticd == null){
            Log.d(TAG,this.toString() + "startConnection()->" + "m_ticd == null");
            return false;
        }
        try
        {
            // Prima chiudo la connessione
            stopConnection();

            m_socketAddress = new InetSocketAddress(m_ticd.getAddress() , m_ticd.getPort());
            if(m_clientSocket == null)
            {
                m_sticp = new Stack<>();
                m_clientSocket = new Socket();
                m_clientSocket.setSoTimeout(m_ticd.getTimeout());
                m_clientSocket.connect(m_socketAddress);
                m_dataOutputStream = new DataOutputStream(m_clientSocket.getOutputStream());
                m_dataInputStream = new DataInputStream(m_clientSocket.getInputStream());

                m_timeMillisecondsSend = System.currentTimeMillis();
                m_timeMillisecondsGet = System.currentTimeMillis();

                Log.d(TAG,this.toString() + "startConnection()");

                return true;
            }
        }
        catch (Exception ex)
        {
            Log.d(TAG,this.toString() + "startConnection()->" + "Exception ex: " + ex.getMessage());
            stopConnection();
        }

        return false;
    }

    private boolean isConnected() {
        boolean bRes = false;
        if (m_clientSocket != null && m_dataInputStream != null && m_dataOutputStream != null) {
            bRes = m_clientSocket.isConnected();
        }
        return bRes;
    }

    private boolean send(byte[] byteToSend)
    {
        boolean bRes = false;

        if (m_dataOutputStream != null && m_sticp != null)       {
            try
            {
                try {
                    m_sticp.pop();
                }

//                if(!m_bWaitingForData)
//                {
                    if(byteToSend != null) {
                        m_dataOutputStream.write(byteToSend, 0, byteToSend.length);
//                        m_bWaitingForData = true;
                    }
//                }
                bRes = true;
            }
            catch (Exception ex) {
                Log.d(TAG,this.toString() + "send()->" + "Exception ex: " + ex.getMessage());
            }
        }

        m_timeMillisecondsSend = System.currentTimeMillis();

        return bRes;
    }


    private byte[] receive()
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

    private void stopConnection()
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

    @Override
    protected Void doInBackground(Object...obj) {
        m_ticd = (TCPIPClientData) obj[0];

        try {
            while (!isCancelled() && m_ticd != null) {


                if (!isConnected()) {
                    // Start communication with Server
                    if(startConnection()){

                    } else {
                        // attendo per non sovraccaricare CPU
                        try {
                            Thread.sleep(3000, 0);
                        } catch (InterruptedException ignored) {
                        }
                    }
                } else {
                    if(send(null)) {
//                        lTime_1 = acs.getGetSendAnswerTimeMilliseconds();
                    } else {
                        try {
                            Thread.sleep(3000, 0);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    if(receive()) {
//                        lTime_1 = acs.getGetSendAnswerTimeMilliseconds();
                    } else {
                        try {
                            Thread.sleep(3000, 0);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }















                    }

                    try {
                        strIpAddress = SQLContract.Settings.getParameter(getApplicationContext(), SQLContract.Parameter.IP_ADDRESS);
                        iPort = Integer.parseInt(SQLContract.Settings.getParameter(getApplicationContext(), SQLContract.Parameter.PORT));
                        iTimeout = Integer.parseInt(SQLContract.Settings.getParameter(getApplicationContext(), SQLContract.Parameter.TIMEOUT));
                        iCommFrameDelay = Integer.parseInt(SQLContract.Settings.getParameter(getApplicationContext(), SQLContract.Parameter.COMM_FRAME_DELAY));
                    }
                    catch (Exception ignored) {
                    }
                    if(!strIpAddress.equals("") && iPort > 0 && iTimeout > 0) {
                        if (acs.connectToArduino(msg, strIpAddress, iPort, iTimeout)) {
                            pud.setData(ProgressUpdateData.Status.CONNECTED,"", true);
                            this.publishProgress(pud);
                        } else {
                            pud.setData(ProgressUpdateData.Status.ERROR,acs.getLastError(), false);
                            this.publishProgress(pud);

                            // attendo per non sovraccaricare CPU
                            try {
                                Thread.sleep(3000, 0);
                            } catch (InterruptedException ignored) {
                            }
                        }
                    }
                    else
                    {
                        pud.setData(ProgressUpdateData.Status.ERROR,getString(R.string.db_data_server_error), false);
                        this.publishProgress(pud);
                        // attendo per non sovraccaricare CPU
                        try {
                            Thread.sleep(3000, 0);
                        } catch (InterruptedException ignored) {
                        }
                    }
                } else {
                    long lTime_1;
                    long lTime_2;
                    if(acs.sendData(msg)) {
                        lTime_1 = acs.getGetSendAnswerTimeMilliseconds();

                        if(acs.getData(msg)) {
                            lTime_2 = acs.getSendGetAnswerTimeMilliseconds();
                            // Tutto Ok, posso leggere i dati ricevuti
                            // Verifico se visualizzare le informazioni di comunicazione

                            // Faccio avanzare una barra ad ogni frame
                            iCommFrame = iCommFrame + 1;
                            if(iCommFrame > 16) {
                                iCommFrame = 1;
                            }
                            strError = "";
                            for(int index = 0; index < 20; index++){
                                if(index < iCommFrame) {
                                    strError = strError + "-";
                                }
                                else
                                {
                                    strError = strError + " ";
                                }
                            }
                            strError = strError + "\n" + "Send -> Rec. Elapsed time(ms): " + String.valueOf(lTime_2) + "/" + String.valueOf(iTimeout);

                            // Log.i(TAG, "doInBackground->" + "Receive - Send Diff. Time (ms)" + lTime_1 + "Send - Receive Diff. Time (ms)" + lTime_2);
                            pud.setData(ProgressUpdateData.Status.ONLINE, strError, true);
                            this.publishProgress(pud);

                            // attendo per non sovraccaricare CPU
                            try {
                                if((iCommFrameDelay * 2) < 10)
                                {
                                    iCommFrameDelay = 10;
                                }
                                Thread.sleep((iCommFrameDelay * 2), 0);
                            } catch (InterruptedException ignored) {
                            }
                        } else {

                            pud.setData(ProgressUpdateData.Status.ERROR, acs.getLastError() , false);
                            this.publishProgress(pud);
                            // attendo per non sovraccaricare CPU
                            try {
                                Thread.sleep(3000, 0);
                            } catch (InterruptedException ignored) {
                            }
                        }
                    } else {
                        pud.setData(ProgressUpdateData.Status.ERROR, acs.getLastError() , false);
                        this.publishProgress(pud);
                        // attendo per non sovraccaricare CPU
                        try {
                            Thread.sleep(3000, 0);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
            strError = "";
        } catch (Exception ex) {
            strError = ex.getMessage();
        }

//            TcpIpClientProtocol
/*
            //Prendo i parametri
            ArduinoClientSocket acs = (ArduinoClientSocket) obj[0];
            Message msg = (Message) obj[1];
            String strError = "";
            int iCommFrame = 0;

            ProgressUpdateData pud = new ProgressUpdateData();

            // Dati di set
            String strIpAddress = "";
            int iPort = 0;
            int iTimeout = 0;
            int iCommFrameDelay = 0;

            try {
                while (!isCancelled() && acs != null && msg != null) {

                    if (!acs.isConnected()) {
                        // Pubblico i dati
                        pud.setData(ProgressUpdateData.Status.CONNECTING,"", false);
                        this.publishProgress(pud);

                        try {
                            strIpAddress = SQLContract.Settings.getParameter(getApplicationContext(), SQLContract.Parameter.IP_ADDRESS);
                            iPort = Integer.parseInt(SQLContract.Settings.getParameter(getApplicationContext(), SQLContract.Parameter.PORT));
                            iTimeout = Integer.parseInt(SQLContract.Settings.getParameter(getApplicationContext(), SQLContract.Parameter.TIMEOUT));
                            iCommFrameDelay = Integer.parseInt(SQLContract.Settings.getParameter(getApplicationContext(), SQLContract.Parameter.COMM_FRAME_DELAY));
                        }
                        catch (Exception ignored) {
                        }
                        if(!strIpAddress.equals("") && iPort > 0 && iTimeout > 0) {
                            if (acs.connectToArduino(msg, strIpAddress, iPort, iTimeout)) {
                                pud.setData(ProgressUpdateData.Status.CONNECTED,"", true);
                                this.publishProgress(pud);
                            } else {
                                pud.setData(ProgressUpdateData.Status.ERROR,acs.getLastError(), false);
                                this.publishProgress(pud);

                                // attendo per non sovraccaricare CPU
                                try {
                                    Thread.sleep(3000, 0);
                                } catch (InterruptedException ignored) {
                                }
                            }
                        }
                        else
                        {
                            pud.setData(ProgressUpdateData.Status.ERROR,getString(R.string.db_data_server_error), false);
                            this.publishProgress(pud);
                            // attendo per non sovraccaricare CPU
                            try {
                                Thread.sleep(3000, 0);
                            } catch (InterruptedException ignored) {
                            }
                        }
                    } else {
                        long lTime_1;
                        long lTime_2;
                        if(acs.sendData(msg)) {
                            lTime_1 = acs.getGetSendAnswerTimeMilliseconds();

                            if(acs.getData(msg)) {
                                lTime_2 = acs.getSendGetAnswerTimeMilliseconds();
                                // Tutto Ok, posso leggere i dati ricevuti
                                // Verifico se visualizzare le informazioni di comunicazione

                                // Faccio avanzare una barra ad ogni frame
                                iCommFrame = iCommFrame + 1;
                                if(iCommFrame > 16) {
                                    iCommFrame = 1;
                                }
                                strError = "";
                                for(int index = 0; index < 20; index++){
                                    if(index < iCommFrame) {
                                        strError = strError + "-";
                                    }
                                    else
                                    {
                                        strError = strError + " ";
                                    }
                                }
                                strError = strError + "\n" + "Send -> Rec. Elapsed time(ms): " + String.valueOf(lTime_2) + "/" + String.valueOf(iTimeout);

                                // Log.i(TAG, "doInBackground->" + "Receive - Send Diff. Time (ms)" + lTime_1 + "Send - Receive Diff. Time (ms)" + lTime_2);
                                pud.setData(ProgressUpdateData.Status.ONLINE, strError, true);
                                this.publishProgress(pud);

                                // attendo per non sovraccaricare CPU
                                try {
                                    if((iCommFrameDelay * 2) < 10)
                                    {
                                        iCommFrameDelay = 10;
                                    }
                                    Thread.sleep((iCommFrameDelay * 2), 0);
                                } catch (InterruptedException ignored) {
                                }
                            } else {

                                pud.setData(ProgressUpdateData.Status.ERROR, acs.getLastError() , false);
                                this.publishProgress(pud);
                                // attendo per non sovraccaricare CPU
                                try {
                                    Thread.sleep(3000, 0);
                                } catch (InterruptedException ignored) {
                                }
                            }
                        } else {
                            pud.setData(ProgressUpdateData.Status.ERROR, acs.getLastError() , false);
                            this.publishProgress(pud);
                            // attendo per non sovraccaricare CPU
                            try {
                                Thread.sleep(3000, 0);
                            } catch (InterruptedException ignored) {
                            }
                        }
                    }
                }
                strError = "";
            } catch (Exception ex) {
                strError = ex.getMessage();
            }

            // Pubblico i dati
            if(acs != null){
                acs.closeConnection(msg);
            }
            pud.setData(ProgressUpdateData.Status.CLOSED, "" , false);
            this.publishProgress(pud);

            // Log.d(TAG, "doInBackground()->return");
*/
        return null;
    }

    private class CommunicationTask extends AsyncTask<Object, Void, Void> {
        private static final String TAG = "CommunicationTask";
/*
        private List<ProgressUpdate> m_lCSListener = new Vector<>();
        private ProgressUpdateData m_pud = new ProgressUpdateData();

        // Imposto il listener
        public synchronized void registerListener(ProgressUpdate listener) {
            if(!m_lCSListener.contains(listener)){
                m_lCSListener.add(listener);
            }
        }
        public synchronized void unregisterListener(ProgressUpdate listener) {
            if(m_lCSListener.contains(listener)){
                m_lCSListener.remove(listener);
            }
        }

        // Funzione richiamata ogni volta che ci sono dei dati da aggiornare
        private void onUpdate(ProgressUpdateData[] pud) {

            // Check if the Listener was set, otherwise we'll get an Exception when we try to call it
            if(m_lCSListener != null) {
                for (ProgressUpdate cs : m_lCSListener) {
                    cs.onProgressUpdate(pud);
                    if(m_pud.isConnected() != pud[0].isConnected()){
                        // Log.d(TAG,"onUpdate->" + "onProgressUpdateConnectionChanged(pud)->isConnected : " + pud[0].isConnected() + ", Nr of Listener : " + m_lCSListener.size());
                        cs.onProgressUpdateConnectionChanged(pud);
                    }
                }
                m_pud.setData(pud[0]);
            } else {
                m_pud.resetData();
            }

        }
*/
        @Override
        protected Void doInBackground(Object...obj) {
//            TcpIpClientProtocol
/*
            //Prendo i parametri
            ArduinoClientSocket acs = (ArduinoClientSocket) obj[0];
            Message msg = (Message) obj[1];
            String strError = "";
            int iCommFrame = 0;

            ProgressUpdateData pud = new ProgressUpdateData();

            // Dati di set
            String strIpAddress = "";
            int iPort = 0;
            int iTimeout = 0;
            int iCommFrameDelay = 0;

            try {
                while (!isCancelled() && acs != null && msg != null) {

                    if (!acs.isConnected()) {
                        // Pubblico i dati
                        pud.setData(ProgressUpdateData.Status.CONNECTING,"", false);
                        this.publishProgress(pud);

                        try {
                            strIpAddress = SQLContract.Settings.getParameter(getApplicationContext(), SQLContract.Parameter.IP_ADDRESS);
                            iPort = Integer.parseInt(SQLContract.Settings.getParameter(getApplicationContext(), SQLContract.Parameter.PORT));
                            iTimeout = Integer.parseInt(SQLContract.Settings.getParameter(getApplicationContext(), SQLContract.Parameter.TIMEOUT));
                            iCommFrameDelay = Integer.parseInt(SQLContract.Settings.getParameter(getApplicationContext(), SQLContract.Parameter.COMM_FRAME_DELAY));
                        }
                        catch (Exception ignored) {
                        }
                        if(!strIpAddress.equals("") && iPort > 0 && iTimeout > 0) {
                            if (acs.connectToArduino(msg, strIpAddress, iPort, iTimeout)) {
                                pud.setData(ProgressUpdateData.Status.CONNECTED,"", true);
                                this.publishProgress(pud);
                            } else {
                                pud.setData(ProgressUpdateData.Status.ERROR,acs.getLastError(), false);
                                this.publishProgress(pud);

                                // attendo per non sovraccaricare CPU
                                try {
                                    Thread.sleep(3000, 0);
                                } catch (InterruptedException ignored) {
                                }
                            }
                        }
                        else
                        {
                            pud.setData(ProgressUpdateData.Status.ERROR,getString(R.string.db_data_server_error), false);
                            this.publishProgress(pud);
                            // attendo per non sovraccaricare CPU
                            try {
                                Thread.sleep(3000, 0);
                            } catch (InterruptedException ignored) {
                            }
                        }
                    } else {
                        long lTime_1;
                        long lTime_2;
                        if(acs.sendData(msg)) {
                            lTime_1 = acs.getGetSendAnswerTimeMilliseconds();

                            if(acs.getData(msg)) {
                                lTime_2 = acs.getSendGetAnswerTimeMilliseconds();
                                // Tutto Ok, posso leggere i dati ricevuti
                                // Verifico se visualizzare le informazioni di comunicazione

                                // Faccio avanzare una barra ad ogni frame
                                iCommFrame = iCommFrame + 1;
                                if(iCommFrame > 16) {
                                    iCommFrame = 1;
                                }
                                strError = "";
                                for(int index = 0; index < 20; index++){
                                    if(index < iCommFrame) {
                                        strError = strError + "-";
                                    }
                                    else
                                    {
                                        strError = strError + " ";
                                    }
                                }
                                strError = strError + "\n" + "Send -> Rec. Elapsed time(ms): " + String.valueOf(lTime_2) + "/" + String.valueOf(iTimeout);

                                // Log.i(TAG, "doInBackground->" + "Receive - Send Diff. Time (ms)" + lTime_1 + "Send - Receive Diff. Time (ms)" + lTime_2);
                                pud.setData(ProgressUpdateData.Status.ONLINE, strError, true);
                                this.publishProgress(pud);

                                // attendo per non sovraccaricare CPU
                                try {
                                    if((iCommFrameDelay * 2) < 10)
                                    {
                                        iCommFrameDelay = 10;
                                    }
                                    Thread.sleep((iCommFrameDelay * 2), 0);
                                } catch (InterruptedException ignored) {
                                }
                            } else {

                                pud.setData(ProgressUpdateData.Status.ERROR, acs.getLastError() , false);
                                this.publishProgress(pud);
                                // attendo per non sovraccaricare CPU
                                try {
                                    Thread.sleep(3000, 0);
                                } catch (InterruptedException ignored) {
                                }
                            }
                        } else {
                            pud.setData(ProgressUpdateData.Status.ERROR, acs.getLastError() , false);
                            this.publishProgress(pud);
                            // attendo per non sovraccaricare CPU
                            try {
                                Thread.sleep(3000, 0);
                            } catch (InterruptedException ignored) {
                            }
                        }
                    }
                }
                strError = "";
            } catch (Exception ex) {
                strError = ex.getMessage();
            }

            // Pubblico i dati
            if(acs != null){
                acs.closeConnection(msg);
            }
            pud.setData(ProgressUpdateData.Status.CLOSED, "" , false);
            this.publishProgress(pud);

            // Log.d(TAG, "doInBackground()->return");
*/
            return null;
        }
/*
        @Override
        protected void onProgressUpdate(ProgressUpdateData... pud) {
            super.onProgressUpdate(pud);
            // Aggiorno i dati
            onUpdate(pud);
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            // Log.d(TAG, "onPostExecute()");
        }
*/
    }

}
