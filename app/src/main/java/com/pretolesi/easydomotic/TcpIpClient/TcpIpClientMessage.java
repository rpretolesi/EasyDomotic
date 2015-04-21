package com.pretolesi.easydomotic.TcpIpClient;

/**
 * Created by RPRETOLESI on 21/04/2015.
 */
public class TcpIpClientMessage {
    private int m_iTransactionIdentifier
    private byte[] m_msg;

    public TcpIpClientMessage(int iTransactionIdentifier, byte[] msg){
        m_iTransactionIdentifier = iTransactionIdentifier;
        m_msg = msg;
    }
}
