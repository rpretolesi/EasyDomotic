package com.pretolesi.easydomotic.TcpIpClient;

/**
 *
 */
public class TcpIpClientProtocol {

    private Protocol m_protocol;
    private byte[] m_data;

    public TcpIpClientProtocol(Protocol protocol,byte[] data ){
        m_protocol = protocol;
        m_data = data;
    }

    public static enum Protocol {
        FREE("Free"),
        MODBUS_RTU("Modbus RTU"),
        MODBUS_ASCII("Modbus ASCII"),
        KNX("KNX");

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
