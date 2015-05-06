package com.pretolesi.easydomotic.SensorValue;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class SensorValueData implements Parcelable {

    public static int TAGMinChar = 1;
    public static int TAGMaxChar = 128;
    public static String TAGDefaultValue = "My SensorValue Name";

    public static int ProtTcpIpClientValueIDMinValue = 0;
    public static int ProtTcpIpClientValueIDMaxValue = 32768;
    public static String ProtTcpIpClientValueIDDefaulValue = "1";

    public static int ProtTcpIpClientValueAddressMinValue = 0;
    public static int ProtTcpIpClientValueAddressMaxValue = 65535;
    public static String ProtTcpIpClientValueAddressDefaulValue = "10000";

    public static int ProtTcpIpClientValueDataTypeMinValue = 0;
    public static int ProtTcpIpClientValueDataTypeMaxValue = 5;
    public static int ProtTcpIpClientValueDataTypeDefaulValue = -1;

    public static int ProtTcpIpClientValueUpdateMillisMinValue = 100;
    public static int ProtTcpIpClientValueUpdateMillisMaxValue = 65535;
    public static String ProtTcpIpClientValueUpdateMillisDefaulValue = "1000";


    public static int ValueMinNrCharToShowMinValue = 1;
    public static int ValueMinNrCharToShowMaxValue = 9;
    public static String ValueMinNrCharToShowDefaulValue = "3";

    public static int ValueNrOfDecimalMinValue = 0;
    public static int ValueNrOfDecimalMaxValue = 5;
    public static String ValueNrOfDecimalDefaulValue = "3";

    public static int ValueUMMinValue = 0;
    public static int ValueUMMaxValue = 9;
    public static String ValueUMDefaulValue = "";

    public static String DefaultValue = "#####";

    private long m_ID;
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
    private int m_iProtTcpIpClientValueUpdateMillis;
    private boolean m_bProtTcpIpClientSendDataOnChange;
    private boolean m_bProtTcpIpClientWaitAnswerBeforeSendNextData;

    // Format Value
    private int m_iValueMinNrCharToShow;
    private int m_iValueNrOfDecimal;
    private String m_strValueUM;
    private boolean m_bValueReadOnly;

    // Sensor
    private int m_iSensorID;
    private boolean m_bSensorEnableSimulation;

    public SensorValueData() {
        this.m_ID = -1;
        this.m_bSaved = false;
        this.m_lRoomID = -1;
        this.m_strTAG = "";
        this.m_fPosX = 0.0f;
        this.m_fPosY = 0.0f;
        this.m_fPosZ = 0.0f;
        this.m_bLandscape = false;

        this.m_bProtTcpIpClientEnable = false;
        this.m_lProtTcpIpClientID = -1;
        this.m_iProtTcpIpClientValueID = 0;
        this.m_iProtTcpIpClientValueAddress = 0;
        this.m_iProtTcpIpClientValueDataType = 0;
        this.m_iProtTcpIpClientValueUpdateMillis = 1000;
        this.m_bProtTcpIpClientSendDataOnChange = false;
        this.m_bProtTcpIpClientWaitAnswerBeforeSendNextData = false;

        this.m_iValueMinNrCharToShow = 0;
        this.m_iValueNrOfDecimal = 0;
        this.m_strValueUM = "";
        this.m_bValueReadOnly = false;

        this.m_iSensorID = -1;
        this.m_bSensorEnableSimulation = false;
    }

    public SensorValueData(long id, boolean bSaved, long lRoomID, String strTAG, float fPosX, float fPosY, float fPosZ, boolean bLandscape) {
        this.m_ID = id;
        this.m_bSaved = bSaved;
        this.m_lRoomID = lRoomID;
        this.m_strTAG = strTAG;
        this.m_fPosX = fPosX;
        this.m_fPosY = fPosY;
        this.m_fPosZ = fPosZ;
        this.m_bLandscape = bLandscape;
        this.m_bProtTcpIpClientEnable = false;
        this.m_lProtTcpIpClientID = 0;
        this.m_iProtTcpIpClientValueID = 0;
        this.m_iProtTcpIpClientValueAddress = 0;
        this.m_iProtTcpIpClientValueDataType = 0;
        this.m_iProtTcpIpClientValueUpdateMillis = 1000;
        this.m_bProtTcpIpClientSendDataOnChange = false;
        this.m_bProtTcpIpClientWaitAnswerBeforeSendNextData = false;

        this.m_iValueMinNrCharToShow = 0;
        this.m_iValueNrOfDecimal = 0;
        this.m_strValueUM = "";
        this.m_bValueReadOnly = false;

        this.m_iSensorID = -1;
        this.m_bSensorEnableSimulation = false;
    }
/*
    public void update(SensorValueData lsd){
        if(lsd != null){
            this.m_ID = lsd.getID();
            this.m_bSaved = lsd.getSaved();
            this.m_lRoomID = lsd.getRoomID();
            this.m_strTAG = lsd.getTag();
            this.m_fPosX = lsd.getPosX();
            this.m_fPosY = lsd.getPosY();
            this.m_fPosZ = lsd.getPosZ();
            this.m_bLandscape = lsd.getLandscape();
         }
    }
*/
    public void setProtTcpIpClient(boolean bProtTcpIpClientEnable, long lProtTcpIpClientID, int iProtTcpIpClientValueID, int iProtTcpIpClientValueAddress, int iProtTcpIpClientValueDataType, int iProtTcpIpClientValueUpdateMillis, boolean bProtTcpIpClientSendDataOnChange, boolean bProtTcpIpClientWaitAnswerBeforeSendNextData) {
        this.m_bProtTcpIpClientEnable = bProtTcpIpClientEnable;
        this.m_lProtTcpIpClientID = lProtTcpIpClientID;
        this.m_iProtTcpIpClientValueID = iProtTcpIpClientValueID;
        this.m_iProtTcpIpClientValueAddress = iProtTcpIpClientValueAddress;
        this.m_iProtTcpIpClientValueDataType = iProtTcpIpClientValueDataType;
        this.m_iProtTcpIpClientValueUpdateMillis = iProtTcpIpClientValueUpdateMillis;
        this.m_bProtTcpIpClientSendDataOnChange = bProtTcpIpClientSendDataOnChange;
        this.m_bProtTcpIpClientWaitAnswerBeforeSendNextData = bProtTcpIpClientWaitAnswerBeforeSendNextData;
    }

    public void setValueFormat(int iValueMinNrCharToShow, int iValueNrOfDecimal, String strValueUM, boolean bValueReadOnly) {
        this.m_iValueMinNrCharToShow = iValueMinNrCharToShow;
        this.m_iValueNrOfDecimal = iValueNrOfDecimal;
        this.m_strValueUM = strValueUM;
        this.m_bValueReadOnly = bValueReadOnly;
    }

    public void setSensorType(int iSensorID, boolean bSensorEnableSimulation) {
        this.m_iSensorID = iSensorID;
        this.m_bSensorEnableSimulation = bSensorEnableSimulation;
    }

    // Set Method
    public void setID(long id) {
        this.m_ID = id;
    }

    public void setSaved(boolean bSaved) { this.m_bSaved = bSaved; }

    public void setRoomID(long lRoomID) {
        this.m_lRoomID = lRoomID;
    }

    public void setTAG(String strTAG) {
        this.m_strTAG = strTAG;
    }

    public void setPosX(float fPosX) {
        this.m_fPosX = fPosX;
    }

    public void setPosY(float fPosY) { this.m_fPosY = fPosY; }

    public void setPosZ(float fPosZ) {
        this.m_fPosZ = fPosZ;
    }

    public void setLandscape(boolean bLandScape) {
        this.m_bLandscape = bLandScape;
    }

    // Protocol
    public void setProtTcpIpClientEnable(boolean bTcpIpClientEnable) { this.m_bProtTcpIpClientEnable = bTcpIpClientEnable; }

    public void setProtTcpIpClientID(long lProtTcpIpClientID) { this.m_lProtTcpIpClientID = lProtTcpIpClientID; }

    public void setProtTcpIpClientValueID(int iProtTcpIpClientValueID) { this.m_iProtTcpIpClientValueID = iProtTcpIpClientValueID; }

    public void setProtTcpIpClientValueAddress(int iProtTcpIpClientValueAddress) {
        this.m_iProtTcpIpClientValueAddress = iProtTcpIpClientValueAddress;
    }

    public void setProtTcpIpClientValueDataType(int iProtTcpIpClientValueDataType) {
        this.m_iProtTcpIpClientValueDataType = iProtTcpIpClientValueDataType;
    }

    public void setProtTcpIpClientValueUpdateMillis(int iProtTcpIpClientValueUpdateMillis) {
        this.m_iProtTcpIpClientValueUpdateMillis = iProtTcpIpClientValueUpdateMillis;
    }

    public void setProtTcpIpClientSendDataOnChange(boolean bProtTcpIpClientSendDataOnChange) {
        this.m_bProtTcpIpClientSendDataOnChange = bProtTcpIpClientSendDataOnChange;
    }

    public void setProtTcpIpClientWaitAnswerBeforeSendNextData(boolean bProtTcpIpClientWaitAnswerBeforeSendNextData) {
        this.m_bProtTcpIpClientWaitAnswerBeforeSendNextData = bProtTcpIpClientWaitAnswerBeforeSendNextData;
    }

    // Value Format
    public void setValueMinNrCharToShow(int iValueMinNrCharToShow) {
        this.m_iValueMinNrCharToShow = iValueMinNrCharToShow;
    }

    public void setValueNrOfDecimal(int iValueNrOfDecimal) {
        this.m_iValueNrOfDecimal = iValueNrOfDecimal;
    }

    public void setValueUM(String strValueUM) {
        this.m_strValueUM = strValueUM;
    }

    public void setValueReadOnly(boolean bValueReadOnly) {
        this.m_bValueReadOnly = bValueReadOnly;
    }

    // Sensor
    public void setSensorID(int iSensorID) {
        this.m_iSensorID = iSensorID;
    }

    public void setSensorEnableSimulation(boolean bSensorEnableSimulation) {
        this.m_bSensorEnableSimulation = bSensorEnableSimulation;
    }

    // Get Method
    public long getID() { return m_ID; }

    public boolean getSaved() { return m_bSaved; }

    public long getRoomID() {
        return m_lRoomID;
    }

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

    public boolean getLandscape() {
        return m_bLandscape;
    }

    // Protocol
    public boolean getProtTcpIpClientEnable() {
        return m_bProtTcpIpClientEnable;
    }

    public long getProtTcpIpClientID() {
        return m_lProtTcpIpClientID;
    }

    public int getProtTcpIpClientValueID() { return m_iProtTcpIpClientValueID; }

    public int getProtTcpIpClientValueAddress() { return m_iProtTcpIpClientValueAddress; }

    public int getProtTcpIpClientValueDataType() {
        return m_iProtTcpIpClientValueDataType;
    }

    public int getProtTcpIpClientValueUpdateMillis() {
        return m_iProtTcpIpClientValueUpdateMillis;
    }

    public boolean getProtTcpIpClientSendDataOnChange() { return m_bProtTcpIpClientSendDataOnChange; }

    public boolean getProtTcpIpClientWaitAnswerBeforeSendNextData() { return m_bProtTcpIpClientWaitAnswerBeforeSendNextData;  }

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

    public boolean getValueReadOnly() {
        return m_bValueReadOnly;
    }

    // Sensor
    public int getSensorID() {
        return m_iSensorID;
    }

    public boolean getSensorEnableSimulation() {
        return m_bSensorEnableSimulation;
    }

    protected SensorValueData(Parcel in) {
        m_ID = in.readLong();
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
        m_iProtTcpIpClientValueUpdateMillis = in.readInt();
        m_bProtTcpIpClientSendDataOnChange = in.readByte() != 0;
        m_bProtTcpIpClientWaitAnswerBeforeSendNextData = in.readByte() != 0;

        m_iValueMinNrCharToShow = in.readInt();
        m_iValueNrOfDecimal = in.readInt();
        m_strValueUM = in.readString();
        m_bValueReadOnly = in.readByte() != 0;

        m_iSensorID = in.readInt();
        m_bSensorEnableSimulation = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(m_ID);
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
        dest.writeInt(m_iProtTcpIpClientValueUpdateMillis);
        dest.writeByte((byte) (m_bProtTcpIpClientSendDataOnChange ? 1 : 0));
        dest.writeByte((byte) (m_bProtTcpIpClientWaitAnswerBeforeSendNextData ? 1 : 0));

        dest.writeInt(m_iValueMinNrCharToShow);
        dest.writeInt(m_iValueNrOfDecimal);
        dest.writeString(m_strValueUM);
        dest.writeByte((byte) (m_bValueReadOnly ? 1 : 0));

        dest.writeInt(m_iSensorID);
        dest.writeByte((byte) (m_bSensorEnableSimulation ? 1 : 0));
    }

    @SuppressWarnings("unused")
    public static final Creator<SensorValueData> CREATOR = new Creator<SensorValueData>() {
        @Override
        public SensorValueData createFromParcel(Parcel in) {
            return new SensorValueData(in);
        }

        @Override
        public SensorValueData[] newArray(int size) {
            return new SensorValueData[size];
        }
    };

    public enum SensorType {
        LEVEL(0, "Level"),
        LEVEL_FRONT(0, "Level Front"),
        LEVEL_REAR(1, "Level Rear"),
        LEVEL_LEFT(2, "Level Left"),
        LEVEL_RIGHT(3, "Level Right"),
        DOUBLE64(4, "Float with double precision IEEE 754 64 bit");

        private int m_iSensorTypeID;
        private String m_strSensorTypeName;

        SensorType(int iSensorTypeID, String strSensorTypelName) {

            m_iSensorTypeID = iSensorTypeID;
            m_strSensorTypeName = strSensorTypelName;
        }

        public static SensorType getSensorType(int iSensorTypeID) {
            if(iSensorTypeID == SensorType.LEVEL.m_iSensorTypeID) {
                return SensorType.LEVEL;
            }
            if(iSensorTypeID == SensorType.LEVEL_FRONT.m_iSensorTypeID) {
                return SensorType.LEVEL_FRONT;
            }
            if(iSensorTypeID == SensorType.LEVEL_REAR.m_iSensorTypeID) {
                return SensorType.LEVEL_REAR;
            }
            if(iSensorTypeID == SensorType.LEVEL_LEFT.m_iSensorTypeID) {
                return SensorType.LEVEL_LEFT;
            }
            if(iSensorTypeID == SensorType.LEVEL_RIGHT.m_iSensorTypeID) {
                return SensorType.LEVEL_RIGHT;
            }
            return null;
        }

        @Override
        public String toString() {
            return Integer.toString(m_iSensorTypeID) + "-" + m_strSensorTypeName;
        }
    }
}
