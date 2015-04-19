package com.pretolesi.easydomotic.TcpIpClient;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class TCPIPClientData implements Parcelable {

    public static int NameMinChar = 1;
    public static int NameMaxChar = 128;
    public static String NameDefaultValue = "My Name";

    public static int AddressMinChar = 7;
    public static int AddressMaxChar = 15;
    public static String AddressDefaultValue = "192.168.1.1";

    public static int PortMinValue = 1;
    public static int PortMaxValue = 65535;
    public static String PortDefaultValue = "502";

    public static int TimeoutMinValue = 1;
    public static int TimeoutMaxValue = 60000;
    public static String TimeouDefaultValue = "3000";

    public static int CommSendDelayDataMinValue = 1;
    public static int CommSendDelayDataMaxValue = 60000;
    public static String CommSendDelayDataDefaultValue = "4";

    public static int ProtocolMinValue = 0;
    public static int ProtocolMaxValue = 3;
    public static int ProtocolDefaulValue = -1;

    public static int HeadMinValue = 0;
    public static int HeadMaxValue = 32768;
    public static String HeadDefaulValue = "0";

    public static int TailMinValue = 0;
    public static int TailMaxValue = 32768;
    public static String TailDefaulValue = "0";

    private long m_ID;
    private boolean m_bSaved;
    private boolean m_bEnable;
    private String m_strName;
    private String m_strAddress;
    private int m_iPort;
    private int m_iTimeout;
    private int m_iCommSendDelayData;
    private long m_lProtocolID;
    private int m_iHead;
    private int m_iTail;

    public TCPIPClientData() {
        this.m_ID = -1;
        this.m_bSaved = false;
        this.m_bEnable = false;
        this.m_strName = null;
        this.m_strAddress = null;
        this.m_iPort = 0;
        this.m_iTimeout = 0;
        this.m_iCommSendDelayData = 0;
        this.m_lProtocolID = -1;
        this.m_iHead = 0;
        this.m_iTail = 0;
    }

    public TCPIPClientData(long id, boolean bSaved, boolean bEnable, String strName, String strAddress, int iPort, int iTimeout, int iCommSendDelayData, long lProtocolID, int iHead, int iTail) {
        this.m_ID = id;
        this.m_strName = strName;
        this.m_bSaved = bSaved;
        this.m_bEnable = bEnable;
        this.m_strAddress = strAddress;
        this.m_iPort = iPort;
        this.m_iTimeout = iTimeout;
        this.m_iCommSendDelayData = iCommSendDelayData;
        this.m_lProtocolID = lProtocolID;
        this.m_iHead = iHead;
        this.m_iTail = iTail;
    }

    public void update(TCPIPClientData lsd){
        if(lsd != null){
            this.m_ID = lsd.getID();
            this.m_bSaved = lsd.getSaved();
            this.m_bEnable = lsd.getEnable();
            this.m_strName = lsd.getName();
            this.m_strAddress = lsd.getAddress();
            this.m_iPort = lsd.getPort();
            this.m_iTimeout = lsd.getTimeout();
            this.m_iCommSendDelayData = lsd.getCommSendDelayData();
            this.m_lProtocolID = lsd.getProtocolID();
            this.m_iHead = lsd.getHead();
            this.m_iTail = lsd.getTail();
        }
    }

    public void setID(long id) {
        this.m_ID = id;
    }

    public void setSaved(boolean bSaved) { this.m_bSaved = bSaved; }

    public void setEnable(boolean bEnable) { this.m_bEnable = bEnable; }

    public void setName(String strName) {
        this.m_strName = strName;
    }

    public void setAddress(String strAddress) {
        this.m_strAddress = strAddress;
    }

    public void setPort(int iPort) {
        this.m_iPort = iPort;
    }

    public void setTimeout(int iTimeout) {
        this.m_iTimeout = iTimeout;
    }

    public void setCommSendDelayData(int iCommSendDelayData) { this.m_iCommSendDelayData = iCommSendDelayData; }

    public void setProtocolID(long lProtocolID) {
        this.m_lProtocolID = lProtocolID;
    }

    public void setHead(int iHead) {
        this.m_iHead = iHead;
    }

    public void setTail(int iTail) {
        this.m_iTail = iTail;
    }

    public long getID() { return m_ID; }

    public boolean getSaved() { return m_bSaved; }

    public boolean getEnable() {
        return m_bEnable;
    }

    public String getName() {
        return m_strName;
    }

    public String getAddress() {
        return m_strAddress;
    }

    public int getPort() {
        return m_iPort;
    }

    public int getTimeout() {
        return m_iTimeout;
    }

    public int getCommSendDelayData() { return m_iCommSendDelayData; }

    public long getProtocolID() { return m_lProtocolID; }

    public int getHead() { return m_iHead; }

    public int getTail() {
        return m_iTail;
    }

    protected TCPIPClientData(Parcel in) {
        m_ID = in.readLong();
        m_bSaved = in.readByte() != 0;
        m_bEnable = in.readByte() != 0;
        m_strName = in.readString();
        m_strAddress = in.readString();
        m_iPort = in.readInt();
        m_iTimeout = in.readInt();
        m_iCommSendDelayData = in.readInt();
        m_lProtocolID = in.readLong();
        m_iHead = in.readInt();
        m_iTail = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(m_ID);
        dest.writeByte((byte) (m_bSaved ? 1 : 0));
        dest.writeByte((byte) (m_bEnable ? 1 : 0));
        dest.writeString(m_strName);
        dest.writeString(m_strAddress);
        dest.writeInt(m_iPort);
        dest.writeInt(m_iTimeout);
        dest.writeInt(m_iCommSendDelayData);
        dest.writeLong(m_lProtocolID);
        dest.writeInt(m_iHead);
        dest.writeInt(m_iTail);
    }

    @SuppressWarnings("unused")
    public static final Creator<TCPIPClientData> CREATOR = new Creator<TCPIPClientData>() {
        @Override
        public TCPIPClientData createFromParcel(Parcel in) {
            return new TCPIPClientData(in);
        }

        @Override
        public TCPIPClientData[] newArray(int size) {
            return new TCPIPClientData[size];
        }
    };

    public static enum Protocol {
        MODBUS_ON_TCP_IP(1, "Modbus TCP/IP RTU"),
        KNX_ON_TCP_IP(2, "KNX TCP/IP");

        private int m_iProtocolID;
        private String m_strProtocolName;

        Protocol(int IProtocolID, String strProtocolName) {

            m_iProtocolID = IProtocolID;
            m_strProtocolName = strProtocolName;
        }

        public int getID() {
            return m_iProtocolID;
        }

        @Override
        public String toString() {
            return Integer.toString(m_iProtocolID) + "-" + m_strProtocolName;
        }
    }
}
