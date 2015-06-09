package com.pretolesi.easydomotic.BluetoothClient;

/**
 * Created by RPRETOLESI on 09/06/2015.
 */
public class BluetoothClientData {

    private  String m_strName;
    private  String m_strAddress;

    public BluetoothClientData() {
    }

    public BluetoothClientData(String strName, String strAddress)
    {
        super();

        this.m_strName = strName;
        this.m_strAddress = strAddress;
    }

    public String getName() {
        return m_strName;
    }

    public void setName(String strName) {
        this.m_strName = strName;
    }

    public String getAddress() {
        return m_strAddress;
    }

    public void setAddress(String strAddress) {
        this.m_strAddress = strAddress;
    }

}
