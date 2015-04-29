package com.pretolesi.easydomotic.TcpIpClient;

import com.pretolesi.easydomotic.R;

/**
 *
 */

public class TcpIpClientWriteStatus {
    private static final String TAG = "TcpIpClientWriteStatus";

    private long m_lServerID;
    private int m_iTransactionID;
    private Status m_sStatus;
    private int m_iErrorCode;
    private String m_strErrorMessage;

    public TcpIpClientWriteStatus(){
        m_lServerID = -1;
        m_iTransactionID = -1;
        m_sStatus = Status.IDLE;
        m_iErrorCode = 0;
        m_strErrorMessage = "";
    }

    public TcpIpClientWriteStatus(long lServerID, int iTransactionID, Status sStatus, int iErrorCode, String strErrorMessage){
        m_lServerID = lServerID;
        m_iTransactionID = iTransactionID;
        m_sStatus = sStatus;
        m_iErrorCode = iErrorCode;
        m_strErrorMessage = strErrorMessage;
    }

    public void setData(long lServerID, int iTransactionID, Status sStatus, int iErrorCode, String strErrorMessage){
        m_lServerID = lServerID;
        m_iTransactionID = iTransactionID;
        m_sStatus = sStatus;
        m_iErrorCode = iErrorCode;
        m_strErrorMessage = strErrorMessage;
    }

    public void setData(TcpIpClientWriteStatus ms){
        m_lServerID = ms.getServerID();
        m_iTransactionID = ms.getTransactionID();
        m_sStatus = ms.getStatus();
        m_iErrorCode = ms.getErrorCode();
        m_strErrorMessage = ms.getErrorMessage();
    }

    public void resetData(){
        m_lServerID = -1;
        m_iTransactionID = -1;
        m_sStatus = Status.IDLE;
        m_iErrorCode = 0;
        m_strErrorMessage = "";
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
