package com.pretolesi.easydomotic.TcpIpClient;

import com.pretolesi.easydomotic.CustomControls.NumericEditText.DataType;

/**
 *
 */
public class TcpIpMsg {
    private long m_lTID;
    private long m_lUID;
    DataType m_dtDataType;
    private byte[] m_byteMsgData;
    private boolean m_bMsgSent;
    private long m_lSentTimeMS;

    public TcpIpMsg(long lTID, long lUID, byte[] byteMsgData){
        m_lTID = lTID;
        m_lUID = lUID;
        m_dtDataType = null;
        m_byteMsgData = byteMsgData;
        m_bMsgSent = false;
        m_lSentTimeMS = System.currentTimeMillis();
    }

    public long getTID(){
        return m_lTID;
    }

    public long getUID(){
        return m_lUID;
    }

    public DataType getDataType(){
        return m_dtDataType;
    }

    public byte[] getMsgData(){
        return m_byteMsgData;
    }

    public boolean getMsgSent(){ return m_bMsgSent; }

    public long getSentTimeMS(){
        return m_lSentTimeMS;
    }

    public void setDataType(DataType dtDataType){
        m_dtDataType = dtDataType;
    }

    public void setMsgAsSent(boolean bMsgSent ){ m_bMsgSent = bMsgSent; }

    public void setMsgTimeMSNow(){ m_lSentTimeMS = System.currentTimeMillis(); }

    @Override
    public boolean equals(Object obj) {
        // Return true if the objects are identical.
        // (This is just an optimization, not required for correctness.)
        if (this == obj) {
            return true;
        }

        // Return false if the other object has the wrong type.
        // This type may be an interface depending on the interface's specification.
        if (!(obj instanceof TcpIpMsg)) {
            return false;
        }

        final TcpIpMsg tim = (TcpIpMsg) obj;
        if (this.m_lTID != tim.m_lTID) {
            return false;
        }
        if (this.m_lUID != tim.m_lUID) {
            return false;
        }
        return true;
    }
}
