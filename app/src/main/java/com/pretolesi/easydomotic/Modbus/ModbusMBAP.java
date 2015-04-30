package com.pretolesi.easydomotic.Modbus;

/**
 * Created by ricca_000 on 26/04/2015.
 */
public class ModbusMBAP {
    private int m_iTI;
    private int m_iPI;
    private int m_iLength;

    public ModbusMBAP(int iTI, int iPI, int iLength) {
        m_iTI = iTI;
        m_iPI = iPI;
        m_iLength = iLength;
    }

    public int getTI(){
        return m_iTI;
    }

    public int getPI(){
        return m_iPI;
    }

    public int getLength(){ return m_iLength; }

}
