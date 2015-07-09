package com.pretolesi.easydomotic.TcpIpClient;

import com.pretolesi.easydomotic.R;

/**
 *
 */

public class TcpIpClientReadStatus {
    private static final String TAG = "TcpIpClientReadStatus";

    private long m_lServerID;
    private int m_iTID;
    private int m_iUID;
    private Status m_sStatus;
    private int m_iErrorCode;
    private String m_strErrorMessage;
    private Object m_objValue;

    public TcpIpClientReadStatus(){
        m_lServerID = -1;
        m_iTID = -1;
        m_iUID = -1;
        m_sStatus = Status.IDLE;
        m_iErrorCode = 0;
        m_strErrorMessage = "";
        m_objValue = null;
    }

    public TcpIpClientReadStatus(long lServerID, int iTID, int iUID, Status sStatus, int iErrorCode, String strErrorMessage, Object objValue){
        m_lServerID = lServerID;
        m_iTID = iTID;
        m_iUID = iUID;
        m_sStatus = sStatus;
        m_iErrorCode = iErrorCode;
        m_strErrorMessage = strErrorMessage;
        m_objValue = objValue;
    }
/*
    public void setData(long lServerID, int iTID, int iUID, Status sStatus, int iErrorCode, String strErrorMessage, byte[] abyteValue){
        m_lServerID = lServerID;
        m_iTID = iTID;
        m_iUID = iUID;
        m_sStatus = sStatus;
        m_iErrorCode = iErrorCode;
        m_strErrorMessage = strErrorMessage;
        m_abyteValue = abyteValue;
    }

    public void setData(TcpIpClientReadStatus ticrs){
        m_lServerID = ticrs.getServerID();
        m_iTID = ticrs.getTID();
        m_iUID = ticrs.getUID();
        m_sStatus = ticrs.getStatus();
        m_iErrorCode = ticrs.getErrorCode();
        m_strErrorMessage = ticrs.getErrorMessage();
        m_abyteValue = ticrs.getValue();
    }

    public void resetData(){
        m_lServerID = -1;
        m_iTID = -1;
        m_iUID = -1;
        m_sStatus = Status.IDLE;
        m_iErrorCode = 0;
        m_strErrorMessage = "";
        m_abyteValue = null;
    }
*/
    public void setServerID(long lServerID){
        m_lServerID = lServerID;
    }

    public void setTID(int iTID){ m_iTID = iTID; }

    public void setUnitID(int iUnitID){ m_iUID = iUnitID; }

    public long getServerID(){
        return m_lServerID;
    }

    public int getTID(){
        return m_iTID;
    }

    public int getUID(){
        return m_iUID;
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

    public Object getValue(){
        return m_objValue;
    }
/*
    public byte[] getValue(){
        return m_abyteValue;
    }
*/
    public static enum Status
    {
        IDLE(0, R.string.ticwos_idle),
        OK(1, R.string.ticwos_ok),
        ERROR(2, R.string.ticwos_error),
        TIMEOUT(3, R.string.ticwos_timeout);

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
