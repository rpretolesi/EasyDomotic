package com.pretolesi.easydomotic.TcpIpClient;

import com.pretolesi.easydomotic.R;

/**
 *
 */
public class TcpIpClientStatus {
    private static final String TAG = "TcpIpClientStatus";

    private long m_lID;
    private Status m_sStatus;
    private String m_strError;

    public TcpIpClientStatus(){
        m_lID = -1;
        m_sStatus = Status.IDLE;
        m_strError = "";
    }

    public TcpIpClientStatus(long lID, Status sStatus, String strError){
        m_lID = lID;
        m_sStatus = sStatus;
        m_strError = strError;
    }

    public void setData(long lID, Status sStatus, String strError){
        m_lID = lID;
        m_sStatus = sStatus;
        m_strError = strError;
    }

    public void setData(TcpIpClientStatus pud){
        m_lID = pud.getID();
        m_sStatus = pud.getStatus();
        m_strError = pud.getError();
    }

    public void resetData(){
        m_lID = -1;
        m_sStatus = Status.IDLE;
        m_strError = "";
    }

    public long getID(){
        return m_lID;
    }

    public Status getStatus(){
        return m_sStatus;
    }

    public String getError(){
        return m_strError;
    }

    public static enum Status
    {
        IDLE(0, R.string.tics_idle),
        OFFLINE(1, R.string.tics_offline),
        CONNECTING(2, R.string.tics_connecting),
        ONLINE(3, R.string.tics_online),
        ERROR(4, R.string.tics_error),
        DISCONNECTING(5, R.string.tics_disconnecting);

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
