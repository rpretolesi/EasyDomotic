package com.pretolesi.easydomotic.CommClientData;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BaseValueCommClientData implements Parcelable {

//    public static final int TYPE_TCP_IP_CLIENT = 1;
//    public static final int TYPE_BLUETOOTH_CLIENT = 2;

    public static short NameMinChar = 1;
    public static short NameMaxChar = 128;
    public static String NameDefaultValue = "My Name";

    public static short AddressMinChar = 1;
    public static short AddressMaxChar = 50;
    public static String AddressDefaultValue = "";

    public static int PortMinValue = 1;
    public static int PortMaxValue = 65535;
    public static String PortDefaultValue = "502";

    public static int TimeoutMinValue = 1;
    public static int TimeoutMaxValue = 60000;
    public static String TimeouDefaultValue = "3000";

    public static int CommSendDelayDataMinValue = 1;
    public static int CommSendDelayDataMaxValue = 60000;
    public static String CommSendDelayDataDefaultValue = "3";

    public static int CommReceiveWaitDataMinValue = 1;
    public static int CommReceiveWaitDataMaxValue = 60000;
    public static String CommReceiveWaitDataDefaultValue = "10";

    public static int CommNrMaxOfErrMinValue = 0;
    public static int CommNrMaxOfErrMaxValue = 9;
    public static String CommNrMaxOfErrDefaultValue = "3";

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
    private long m_lTransProtocolID;
    private boolean m_bSaved;
    private boolean m_bEnable;
    private String m_strName;
    private String m_strAddress;
    private int m_iPort;
    private int m_iTimeout;
    private int m_iCommSendDelayData;
    private int m_iCommReceiveWaitData;
    private int m_iCommNrMaxOfErr;
    private long m_lProtocolID;
    private int m_iHead;
    private int m_iTail;

    public BaseValueCommClientData(int iType) {
        this.m_lTransProtocolID = iType;
        this.m_ID = -1;
        this.m_bSaved = false;
        this.m_bEnable = false;
        this.m_strName = null;
        this.m_strAddress = null;
        this.m_iPort = 0;
        this.m_iTimeout = 0;
        this.m_iCommSendDelayData = 0;
        this.m_iCommReceiveWaitData = 0;
        this.m_iCommNrMaxOfErr = 0;
        this.m_lProtocolID = -1;
        this.m_iHead = 0;
        this.m_iTail = 0;
    }

    public BaseValueCommClientData(long lTransProtocolID, long id, boolean bSaved, boolean bEnable, String strName, String strAddress, int iPort, int iTimeout, int iCommSendDelayData, int iCommReceiveWaitData, int iCommNrMaxOfErr, long lProtocolID, int iHead, int iTail) {
        this.m_lTransProtocolID = lTransProtocolID;
        this.m_ID = id;
        this.m_strName = strName;
        this.m_bSaved = bSaved;
        this.m_bEnable = bEnable;
        this.m_strAddress = strAddress;
        this.m_iPort = iPort;
        this.m_iTimeout = iTimeout;
        this.m_iCommSendDelayData = iCommSendDelayData;
        this.m_iCommReceiveWaitData = iCommReceiveWaitData;
        this.m_iCommNrMaxOfErr = iCommNrMaxOfErr;
        this.m_lProtocolID = lProtocolID;
        this.m_iHead = iHead;
        this.m_iTail = iTail;
    }

    public void setTranspProtocolID(long lTransProtocolID) { this.m_lTransProtocolID = lTransProtocolID; }

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

    public void setCommReceiveWaitData(int iCommReceiveWaitData) { this.m_iCommReceiveWaitData = iCommReceiveWaitData; }

    public void setCommNrMaxOfErr(int iCommNrMaxOfErr) { this.m_iCommNrMaxOfErr = iCommNrMaxOfErr; }

    public void setProtocol(Protocol p) {
        if (p == Protocol.MODBUS_ON_TCP_IP) {
            m_lProtocolID = Protocol.MODBUS_ON_TCP_IP.getID();
        }
        if (p == Protocol.MODBUS_ON_SERIAL) {
            m_lProtocolID = Protocol.MODBUS_ON_SERIAL.getID();
        }
    }

    public void setProtocolID(long lProtocolID) {
        m_lProtocolID = lProtocolID;
    }

    public void setHead(int iHead) {
        this.m_iHead = iHead;
    }

    public void setTail(int iTail) {
        this.m_iTail = iTail;
    }

    public long getTranspProtocolID() { return m_lTransProtocolID; }

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

    public int getCommReceiveWaitData() { return m_iCommReceiveWaitData; }

    public int getCommNrMaxOfErr() { return m_iCommNrMaxOfErr; }

    public Protocol getProtocol() {
        if (m_lProtocolID == Protocol.MODBUS_ON_TCP_IP.getID()) {
            return Protocol.MODBUS_ON_TCP_IP;
        }
        if (m_lProtocolID == Protocol.MODBUS_ON_SERIAL.getID()) {
            return Protocol.MODBUS_ON_SERIAL;
        }
        return null;
    }

    public long getProtocolID() { return m_lProtocolID; }

    public int getHead() { return m_iHead; }

    public int getTail() {
        return m_iTail;
    }

    protected BaseValueCommClientData(Parcel in) {
        m_lTransProtocolID = in.readLong();
        m_ID = in.readLong();
        m_bSaved = in.readByte() != 0;
        m_bEnable = in.readByte() != 0;
        m_strName = in.readString();
        m_strAddress = in.readString();
        m_iPort = in.readInt();
        m_iTimeout = in.readInt();
        m_iCommSendDelayData = in.readInt();
        m_iCommReceiveWaitData = in.readInt();
        m_iCommNrMaxOfErr = in.readInt();
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
        dest.writeLong(m_lTransProtocolID);
        dest.writeLong(m_ID);
        dest.writeByte((byte) (m_bSaved ? 1 : 0));
        dest.writeByte((byte) (m_bEnable ? 1 : 0));
        dest.writeString(m_strName);
        dest.writeString(m_strAddress);
        dest.writeInt(m_iPort);
        dest.writeInt(m_iTimeout);
        dest.writeInt(m_iCommSendDelayData);
        dest.writeInt(m_iCommReceiveWaitData);
        dest.writeInt(m_iCommNrMaxOfErr);
        dest.writeLong(m_lProtocolID);
        dest.writeInt(m_iHead);
        dest.writeInt(m_iTail);
    }

    @SuppressWarnings("unused")
    public static final Creator<BaseValueCommClientData> CREATOR = new Creator<BaseValueCommClientData>() {
        @Override
        public BaseValueCommClientData createFromParcel(Parcel in) {
            return new BaseValueCommClientData(in);
        }

        @Override
        public BaseValueCommClientData[] newArray(int size) {
            return new BaseValueCommClientData[size];
        }
    };

    public enum Protocol {
        MODBUS_ON_TCP_IP(0, "Modbus RTU ON TCP/IP", TraspProtocol.TCP_IP),
        MODBUS_ON_SERIAL(1, "Modbus RTU ON Serial or Bluetooth", TraspProtocol.BLUETOOTH);

        private int m_iProtocolID;
        private String m_strProtocolName;
        private TraspProtocol m_tp;

        Protocol(int IProtocolID, String strProtocolName, TraspProtocol tp) {

            m_iProtocolID = IProtocolID;
            m_strProtocolName = strProtocolName;
            m_tp = tp;
        }

        public static List<Protocol> getValues(TraspProtocol tp) {
            List<Protocol> al = new ArrayList<>(2);
            for (Protocol p : Protocol.values()){
                if (p != null && tp != null && p.m_tp == tp){
                    al.add(p);
                }
            }
            return al;
        }

        public int getID() {
            return m_iProtocolID;
        }

        @Override
        public String toString() {
            return Integer.toString(m_iProtocolID) + "-" + m_strProtocolName;
        }
    }

    public enum TraspProtocol {
        TCP_IP(0, "TCP/IP"),
        BLUETOOTH(1, "Bluetooth");

        private int m_iProtocolID;
        private String m_strProtocolName;

        TraspProtocol(int IProtocolID, String strProtocolName) {

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
