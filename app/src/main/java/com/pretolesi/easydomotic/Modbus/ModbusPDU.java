package com.pretolesi.easydomotic.Modbus;

/**
 *
 */
public class ModbusPDU {
    private int m_iUID;
    private int m_iFEC;
    private int m_iExC;
    private int m_iByteCount;
    private byte[] m_aByteValue;

    public ModbusPDU(int iUID, int iFEC, int iExC, int iByteCount, byte[] aByteValue) {
        m_iUID = iUID;
        m_iFEC = iFEC;
        m_iExC = iExC;
        m_iByteCount = iByteCount;
        m_aByteValue = aByteValue;
    }

    public int getUID() { return m_iUID; }

    public int getFEC(){
        return m_iFEC;
    }

    public int getExC(){
        return m_iExC;
    }

    public int getByteCount(){ return m_iByteCount; }

    public byte[] getByteValue(){
        return m_aByteValue;
    }

}
