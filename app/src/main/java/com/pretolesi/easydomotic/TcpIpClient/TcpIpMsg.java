package com.pretolesi.easydomotic.TcpIpClient;

/**
 *
 */
public class TcpIpMsg {
    private long m_lMsgID;
    private byte[] m_byteMsgData;
    private boolean m_bMsgSent;
    private long m_lSentTimeMS;

    public TcpIpMsg(long lMsgID, byte[] byteMsgData){
        m_lMsgID = lMsgID;
        m_byteMsgData = byteMsgData;
        m_bMsgSent = false;
        m_lSentTimeMS = System.currentTimeMillis();
    }

    public long getMsgID(){
        return m_lMsgID;
    }

    public byte[] getMsgData(){
        return m_byteMsgData;
    }

    public boolean getMsgSent(){ return m_bMsgSent; }

    public long getSentTimeMS(){
        return m_lSentTimeMS;
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
        if (this.m_lMsgID != tim.m_lMsgID) {
            return false;
        }
        return true;
    }
}
