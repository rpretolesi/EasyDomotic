package com.pretolesi.easydomotic.BaseValue;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class BaseValueData implements Parcelable {

    public static final int TYPE_LIGHT_SWITCH = 1;
    public static final int TYPE_NUMERIC_VALUE = 2;
    public static final int TYPE_SENSOR_VALUE = 3;

    // Graphic
    private long m_ID;
    private int m_iType;
    private boolean m_bSaved;
    private long m_lRoomID;
    private String m_strTAG;
    private float m_fPosX;
    private float m_fPosY;
    private float m_fPosZ;
    private boolean m_bLandscape;

    // Protocol Tcp Ip Client
    private boolean m_bProtTcpIpClientEnable;
    private long m_lProtTcpIpClientID;
    private int m_iProtTcpIpClientValueID;
    private int m_iProtTcpIpClientValueAddress;
    private int m_iProtTcpIpClientValueDataType;

    // Format Value
    private int m_iValueMinNrCharToShow;
    private int m_iValueNrOfDecimal;
    private String m_strValueUM;
    private int m_iValueUpdateMillis;

    // Switch
    private int m_iWriteValueOFF;
    private int m_iWriteValueOFFON;
    private int m_iWriteValueONOFF;
    private int m_iWriteValueON;


    public BaseValueData() {
        // Graphic
        m_ID = -1;
        m_iType = -1;
        m_bSaved = false;
        m_lRoomID = -1;
        m_strTAG = "";
        m_fPosX = 0;
        m_fPosY = 0;
        m_fPosZ = 0;
        m_bLandscape = false;

        // Protocol Tcp Ip Client
        m_bProtTcpIpClientEnable = false;
        m_lProtTcpIpClientID = -1;
        m_iProtTcpIpClientValueID = -1;
        m_iProtTcpIpClientValueAddress = 0;
        m_iProtTcpIpClientValueDataType = -1;

        // Format Value
        m_iValueMinNrCharToShow = 0;
        m_iValueNrOfDecimal = 0;
        m_strValueUM = "";
        m_iValueUpdateMillis = 1000;

        // Switch
        m_iWriteValueOFF = 0;
        m_iWriteValueOFFON = 0;
        m_iWriteValueONOFF = 0;
        m_iWriteValueON = 0;

        // Sensor
        m_lSensorTypeID = -1;
        m_lSensorValueID = -1;
        m_bSensorEnableSimulation = false;
        m_fSensorAmplK = 1.0f;
        m_fSensorLowPassFilterK = 1.0f;
        m_iSensorSampleTime = 100;
    }

    public BaseValueData(long id, int iType, boolean bSaved, long lRoomID, String strTAG, float fPosX, float fPosY, float fPosZ, boolean bLandscape) {
        // Graphic
        m_ID = id;
        m_iType = iType;
        m_bSaved = bSaved;
        m_lRoomID = lRoomID;
        m_strTAG = strTAG;
        m_fPosX = fPosX;
        m_fPosY = fPosY;
        m_fPosZ = fPosZ;
        m_bLandscape = bLandscape;

        // Protocol Tcp Ip Client
        m_bProtTcpIpClientEnable = false;
        m_lProtTcpIpClientID = -1;
        m_iProtTcpIpClientValueID = -1;
        m_iProtTcpIpClientValueAddress = 0;
        m_iProtTcpIpClientValueDataType = -1;

        // Format Value
        m_iValueMinNrCharToShow = 0;
        m_iValueNrOfDecimal = 0;
        m_strValueUM = "";
        m_iValueUpdateMillis = 1000;

        // Switch
        m_iWriteValueOFF = 0;
        m_iWriteValueOFFON = 0;
        m_iWriteValueONOFF = 0;
        m_iWriteValueON = 0;

    }

    public void setProtTcpIpClient(boolean bProtTcpIpClientEnable, long lProtTcpIpClientID, int iProtTcpIpClientValueID, int iProtTcpIpClientValueAddress, int iProtTcpIpClientValueDataType) {
        this.m_bProtTcpIpClientEnable = bProtTcpIpClientEnable;
        this.m_lProtTcpIpClientID = lProtTcpIpClientID;
        this.m_iProtTcpIpClientValueID = iProtTcpIpClientValueID;
        this.m_iProtTcpIpClientValueAddress = iProtTcpIpClientValueAddress;
        this.m_iProtTcpIpClientValueDataType = iProtTcpIpClientValueDataType;
    }

    public void setFormatValue(int iValueMinNrCharToShow, int iValueNrOfDecimal, String strValueUM, int iValueUpdateMillis) {
        // Format Value
        m_iValueMinNrCharToShow = iValueMinNrCharToShow;
        m_iValueNrOfDecimal = iValueNrOfDecimal;
        m_strValueUM = strValueUM;
        m_iValueUpdateMillis = iValueUpdateMillis;
    }

    // Set Method
    public void setID(long id) { this.m_ID = id; }

    public void setType(int iType) { this.m_iType = iType; }

    public void setSaved(boolean bSaved) { this.m_bSaved = bSaved; }

    public void setRoomID(long lRoomID) { this.m_lRoomID = lRoomID; }

    public void setTAG(String strTAG) { this.m_strTAG = strTAG; }

    public void setPosX(float fPosX) {  this.m_fPosX = fPosX; }

    public void setPosY(float fPosY) { this.m_fPosY = fPosY; }

    public void setPosZ(float fPosZ) { this.m_fPosZ = fPosZ; }

    public void setLandscape(boolean bLandScape) { this.m_bLandscape = bLandScape; }

    // Protocol
    public void setProtTcpIpClientEnable(boolean bTcpIpClientEnable) { this.m_bProtTcpIpClientEnable = bTcpIpClientEnable; }

    public void setProtTcpIpClientID(long lProtTcpIpClientID) { this.m_lProtTcpIpClientID = lProtTcpIpClientID; }

    public void setProtTcpIpClientValueID(int iProtTcpIpClientValueID) { this.m_iProtTcpIpClientValueID = iProtTcpIpClientValueID; }

    public void setProtTcpIpClientValueAddress(int iProtTcpIpClientValueAddress) { this.m_iProtTcpIpClientValueAddress = iProtTcpIpClientValueAddress;  }

    public void setProtTcpIpClientValueDataType(int iProtTcpIpClientValueDataType) { this.m_iProtTcpIpClientValueDataType = iProtTcpIpClientValueDataType; }

    // Value Format
    public void setValueMinNrCharToShow(int iValueMinNrCharToShow) { this.m_iValueMinNrCharToShow = iValueMinNrCharToShow; }

    public void setValueNrOfDecimal(int iValueNrOfDecimal) { this.m_iValueNrOfDecimal = iValueNrOfDecimal; }

    public void setValueUM(String strValueUM) { this.m_strValueUM = strValueUM; }

    public void setValueUpdateMillis(int iValueUpdateMillis) { this.m_iValueUpdateMillis = iValueUpdateMillis; }

    // Get Method
    public long getID() { return m_ID; }

    public long getType() { return m_iType; }

    public boolean getSaved() { return m_bSaved; }

    public long getRoomID() { return m_lRoomID; }

    public String getTag() {
        return m_strTAG;
    }

    public float getPosX() {
        return m_fPosX;
    }

    public float getPosY() { return m_fPosY; }

    public float getPosZ() {
        return m_fPosZ;
    }

    public boolean getLandscape() { return m_bLandscape; }

    // Protocol
    public boolean getProtTcpIpClientEnable() { return m_bProtTcpIpClientEnable; }

    public long getProtTcpIpClientID() {
        return m_lProtTcpIpClientID;
    }

    public int getProtTcpIpClientValueID() { return m_iProtTcpIpClientValueID; }

    public int getProtTcpIpClientValueAddress() { return m_iProtTcpIpClientValueAddress; }

    public int getProtTcpIpClientValueDataType() { return m_iProtTcpIpClientValueDataType; }

    // Format Number
    public int getValueMinNrCharToShow() {
        return m_iValueMinNrCharToShow;
    }

    public int getValueNrOfDecimal() {
        return m_iValueNrOfDecimal;
    }

    public String getValueUM() {
        return m_strValueUM;
    }

    public int getValueUpdateMillis() { return m_iValueUpdateMillis; }

    protected BaseValueData(Parcel in) {
        m_ID = in.readLong();
        m_iType = in.readInt();
        m_bSaved = in.readByte() != 0;
        m_lRoomID = in.readLong();
        m_strTAG = in.readString();
        m_fPosX = in.readFloat();
        m_fPosY = in.readFloat();
        m_fPosZ = in.readFloat();
        m_bLandscape = in.readByte() != 0;

        m_bProtTcpIpClientEnable = in.readByte() != 0;
        m_lProtTcpIpClientID = in.readLong();
        m_iProtTcpIpClientValueID = in.readInt();
        m_iProtTcpIpClientValueAddress = in.readInt();
        m_iProtTcpIpClientValueDataType = in.readInt();

        m_iValueMinNrCharToShow = in.readInt();
        m_iValueNrOfDecimal = in.readInt();
        m_strValueUM = in.readString();
        m_iValueUpdateMillis = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(m_ID);
        dest.writeInt(m_iType);
        dest.writeByte((byte) (m_bSaved ? 1 : 0));
        dest.writeLong(m_lRoomID);
        dest.writeString(m_strTAG);
        dest.writeFloat(m_fPosX);
        dest.writeFloat(m_fPosY);
        dest.writeFloat(m_fPosZ);
        dest.writeByte((byte) (m_bLandscape ? 1 : 0));

        dest.writeByte((byte) (m_bProtTcpIpClientEnable ? 1 : 0));
        dest.writeLong(m_lProtTcpIpClientID);
        dest.writeInt(m_iProtTcpIpClientValueID);
        dest.writeInt(m_iProtTcpIpClientValueAddress);
        dest.writeInt(m_iProtTcpIpClientValueDataType);

        dest.writeInt(m_iValueMinNrCharToShow);
        dest.writeInt(m_iValueNrOfDecimal);
        dest.writeString(m_strValueUM);
        dest.writeInt(m_iValueUpdateMillis);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BaseValueData> CREATOR = new Parcelable.Creator<BaseValueData>() {
        @Override
        public BaseValueData createFromParcel(Parcel in) {
            return new BaseValueData(in);
        }

        @Override
        public BaseValueData[] newArray(int size) {
            return new BaseValueData[size];
        }
    };
}
