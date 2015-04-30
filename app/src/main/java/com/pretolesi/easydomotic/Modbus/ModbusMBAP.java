package com.pretolesi.easydomotic.Modbus;

/**
 * Created by ricca_000 on 26/04/2015.
 */
public class ModbusMBAP {
    private int m_iTID;
    private int m_iPID;
    private int m_iLength;

    public ModbusMBAP(int iTID, int iPID, int iLength) {
        m_iTID = iTID;
        m_iPID = iPID;
        m_iLength = iLength;
    }

    public int getTID(){
        return m_iTID;
    }

    public int getPID(){
        return m_iPID;
    }

    public int getLength(){ return m_iLength; }

}
