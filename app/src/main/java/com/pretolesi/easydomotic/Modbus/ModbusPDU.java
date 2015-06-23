package com.pretolesi.easydomotic.Modbus;

/**
 *
 */
public class ModbusPDU {
    private short m_shUID;
    private short m_shFEC;
    private short m_shExC;
    private byte[] m_aPDUValue;
    private short m_shPDULenght;

    public ModbusPDU(short shUID, short shFEC, short shExC,  byte[] aPDUValue, short shPDULenght) {
        m_shUID = shUID;
        m_shFEC = shFEC;
        m_shExC = shExC;
        m_aPDUValue = aPDUValue;
        m_shPDULenght = shPDULenght;
    }

    public short getUID() { return m_shUID; }

    public short getFEC(){
        return m_shFEC;
    }

    public short getExC(){
        return m_shExC;
    }

    public short getPDULenght(){ return m_shPDULenght; }

    public byte[] getPDUValue(){
        return m_aPDUValue;
    }

}
