package com.pretolesi.easydomotic.TcpIpClient;

import com.pretolesi.easydomotic.R;

/**
 *
 */

public class TcpIpClientReadStatus {
    private static final String TAG = "TcpIpClientWriteStatus";

    private long m_lServerID;
    private int m_iTransactionID;
    private Status m_sStatus;
    private int m_iErrorCode;
    private String m_strErrorMessage;
    private byte[] m_abyteValue;

    public TcpIpClientReadStatus(){
        m_lServerID = -1;
        m_iTransactionID = -1;
        m_sStatus = Status.IDLE;
        m_iErrorCode = 0;
        m_strErrorMessage = "";
        m_abyteValue = null;
    }

    public TcpIpClientReadStatus(long lServerID, int iTransactionID, Status sStatus, int iErrorCode, String strErrorMessage, byte[] abyteValue){
        m_lServerID = lServerID;
        m_iTransactionID = iTransactionID;
        m_sStatus = sStatus;
        m_iErrorCode = iErrorCode;
        m_strErrorMessage = strErrorMessage;
        m_abyteValue = abyteValue;
    }

    public void setData(long lServerID, int iTransactionID, Status sStatus, int iErrorCode, String strErrorMessage, byte[] abyteValue){
        m_lServerID = lServerID;
        m_iTransactionID = iTransactionID;
        m_sStatus = sStatus;
        m_iErrorCode = iErrorCode;
        m_strErrorMessage = strErrorMessage;
        m_abyteValue = abyteValue;
    }

    public void setData(TcpIpClientReadStatus ticrs){
        m_lServerID = ticrs.getServerID();
        m_iTransactionID = ticrs.getTransactionID();
        m_sStatus = ticrs.getStatus();
        m_iErrorCode = ticrs.getErrorCode();
        m_strErrorMessage = ticrs.getErrorMessage();
        m_abyteValue = ticrs.getValue();
    }

    public void resetData(){
        m_lServerID = -1;
        m_iTransactionID = -1;
        m_sStatus = Status.IDLE;
        m_iErrorCode = 0;
        m_strErrorMessage = "";
        m_abyteValue = null;
    }

    public void setServerID(long lServerID){
        m_lServerID = lServerID;
    }

    public void setTransactionID(int iTransactionID){ m_iTransactionID = iTransactionID; }

    public long getServerID(){
        return m_lServerID;
    }

    public int getTransactionID(){
        return m_iTransactionID;
    }

    public Status getStatus(){
        return m_sStatus;
    }

    public int getErrorCode(){
        return m_iErrorCode;
    }

    public String getErrorMessage(){
        return m_strErrorMessage;
    }

    public byte[] getValue(){
        return m_abyteValue;
    }

    public static enum Status
    {
        IDLE(0, R.string.ticwos_idle),
        OK(0, R.string.ticwos_ok),
        ERROR(1, R.string.ticwos_error),
        TIMEOUT(2, R.string.ticwos_timeout);

        private int ID;
        private int StringResID;

        private Status(int ID, int StringResID) {
            this.ID = ID;
            this.StringResID = StringResID;
        }

        public int getID() {
            return ID;
        }

        public int getStringResID() {
            return StringResID;
        }
    }
}
