package com.pretolesi.easydomotic.Modbus;

/**
 *
 */
public class ModbusPDU {
    private int m_iUI;
    private int m_iFEC;
    private int m_iExC;

    public ModbusPDU(int iUI, int iFEC, int iExC) {
        m_iUI = iUI;
        m_iFEC = iFEC;
        m_iExC = iExC;
    }

    public int getUI() { return m_iUI; }

    public int getFEC(){
        return m_iFEC;
    }

    public int getExC(){
        return m_iExC;
    }
}
