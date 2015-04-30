package com.pretolesi.easydomotic.Modbus;

/**
 *
 */
public class ModbusPDU {
    private int m_iUI;
    private int m_iFEC;
    private int m_iExC;
    private int m_iByteCount;
    private byte[] m_aByteValue;

    public ModbusPDU(int iUI, int iFEC, int iExC, int iByteCount, byte[] aByteValue) {
        m_iUI = iUI;
        m_iFEC = iFEC;
        m_iExC = iExC;
        m_iByteCount = iByteCount;
        m_aByteValue = aByteValue;
    }

    public int getUI() { return m_iUI; }

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
