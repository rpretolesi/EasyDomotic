package com.pretolesi.easydomotic.Modbus;

import com.pretolesi.easydomotic.R;

/**
 *
 */
public class ModbusStatus {
    private static final String TAG = "ModbusStatus";

    private long m_lServerID;
    private int m_iTransactionID;
    private Status m_sStatus;
    private int m_iErrorCode;
    private String m_strErrorMessage;

    public ModbusStatus(){
        m_lServerID = -1;
        m_iTransactionID = -1;
        m_sStatus = Status.IDLE;
        m_iErrorCode = 0;
        m_strErrorMessage = "";
    }

    public ModbusStatus(long lServerID, int iTransactionID, Status sStatus, int iErrorCode, String strErrorMessage){
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

    public void setData(ModbusStatus ms){
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
        IDLE(0, R.string.ms_idle),
        OK(0, R.string.ms_ok),
        ERROR(1, R.string.ms_error),
        TIMEOUT(2, R.string.ms_reading_ok),
        READING_OK(3, R.string.ms_reading_ok),
        READING_ERROR(4, R.string.ms_reading_error),
        WRITING_OK(5, R.string.ms_writing_ok),
        WRITING_ERROR(6, R.string.ms_writing_error);

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
