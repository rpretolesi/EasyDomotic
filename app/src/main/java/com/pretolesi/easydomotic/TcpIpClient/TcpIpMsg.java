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
    public boolean getMsgSent(){
        return m_bMsgSent;
    }
    public long getSentTimeMS(){
        return m_lSentTimeMS;
    }

    public void setSentTimeMSNow(){
        m_bMsgSent = true;
        m_lSentTimeMS = System.currentTimeMillis();
    }
}
