package com.pretolesi.easydomotic.TcpIpClient;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 */
public class TcpIpClientProtocol {

    private Protocol m_protocol;
    private byte[] m_bytedata;

    public TcpIpClientProtocol(Protocol protocol){
        m_protocol = protocol;
    }

    /**
     * Send a command with the specified Protocol
     * The value of the Fields field depends of the protocol
     * Protocol Modbus Over TCP/IP:
     * @param iField_1 Transaction Identifier (2 bytes);
     * @param iField_2 Protocol Identifier (2 bytes), must be 0;
     * @param iField_3 Length (2 bytes);
     * @param iField_4 Unit Identifier (1 byte);
     * @param iField_5 Address (2 byte);
     * @param iField_6 Value (2 byte);
     */
    public void WriteSingleRegister(int iField_1, int iField_2, int iField_3, int iField_4, int iField_5, int iField_6){
        switch(m_protocol){
            case MODBUS_ON_TCP_IP:
                ByteBuffer bb = ByteBuffer.allocate(6 + iField_3);
                bb.putShort((short)iField_1);
                bb.putShort((short)iField_2);
                bb.putShort((short)iField_3);
                bb.put((byte)iField_4);
                bb.putShort((short)iField_5);
                bb.putShort((short)iField_6);

                m_bytedata = bb.array();

                break;
        }
    }

    public byte[] getData(){
        return m_bytedata;
    }

    public static enum Protocol {
        MODBUS_ON_TCP_IP("Modbus TCP/IP RTU"),
        KNX_ON_TCP_IP("KNX TCP/IP");

        private String m_strProtocol;

        Protocol(String strProtocol) {
            m_strProtocol = strProtocol;
        }

        @Override
        public String toString() {
            return m_strProtocol;
        }
    }
}
