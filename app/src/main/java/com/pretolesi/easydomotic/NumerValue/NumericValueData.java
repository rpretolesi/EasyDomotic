package com.pretolesi.easydomotic.NumerValue;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class NumericValueData implements Parcelable {

    public static int TAGMinChar = 1;
    public static int TAGMaxChar = 128;
    public static String TAGDefaultValue = "My NumericValue Name";

    public static int ProtTcpIpClientValueIDMinValue = 0;
    public static int ProtTcpIpClientValueIDMaxValue = 32768;
    public static String ProtTcpIpClientValueIDDefaulValue = "1";

    public static int ProtTcpIpClientValueAddressMinValue = 0;
    public static int ProtTcpIpClientValueAddressMaxValue = 65535;
    public static String ProtTcpIpClientValueAddressDefaulValue = "10000";

    public static int ProtTcpIpClientValueDataTypeMinValue = 0;
    public static int ProtTcpIpClientValueDataTypeMaxValue = 5;
    public static int ProtTcpIpClientValueDataTypeDefaulValue = -1;

    public static int ProtTcpIpClientValueNrOfDecimalMinValue = 0;
    public static int ProtTcpIpClientValueNrOfDecimalMaxValue = 5;
    public static String ProtTcpIpClientValueNrOfDecimalDefaulValue = "0";

    public static int ProtTcpIpClientValueUMMinValue = 0;
    public static int ProtTcpIpClientValueUMMaxValue = 9;
    public static String ProtTcpIpClientValueUMDefaulValue = "";

    public static int ProtTcpIpClientValueMinNrCharToShowMinValue = 1;
    public static int ProtTcpIpClientValueMinNrCharToShowMaxValue = 9;
    public static String ProtTcpIpClientValueMinNrCharToShowDefaulValue = "3";

    public static int ProtTcpIpClientValueUpdateMillisMinValue = 100;
    public static int ProtTcpIpClientValueUpdateMillisMaxValue = 65535;
    public static String ProtTcpIpClientValueUpdateMillisDefaulValue = "1000";

    public static String DefaultValue = "#####";

    private long m_ID;
    private boolean m_bSaved;
    private boolean m_bDisp;
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
    private int m_iProtTcpIpClientValueNrOfDecimal;
    private String m_strProtTcpIpClientValueUM;
    private int m_iProtTcpIpClientValueMinNrCharToShow;
    private boolean m_bProtTcpIpClientValueReadOnly;
    private int m_iProtTcpIpClientValueUpdateMillis;
    private boolean m_bProtTcpIpClientSendDataOnChange;
    private boolean m_bProtTcpIpClientWaitAnswerBeforeSendNextData;

    public NumericValueData() {
        this.m_ID = -1;
        this.m_bSaved = false;
        this.m_bDisp = false;
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
        this.m_iProtTcpIpClientValueNrOfDecimal = 0;
        this.m_strProtTcpIpClientValueUM = "";
        this.m_iProtTcpIpClientValueMinNrCharToShow = 0;
        this.m_bProtTcpIpClientValueReadOnly = false;
        this.m_iProtTcpIpClientValueUpdateMillis = 1000;
        this.m_bProtTcpIpClientSendDataOnChange = false;
        this.m_bProtTcpIpClientWaitAnswerBeforeSendNextData = false;
    }

    public NumericValueData(long id, boolean bSaved, boolean bDisp, long lRoomID, String strTAG, float fPosX, float fPosY, float fPosZ, boolean bLandscape) {
        this.m_ID = id;
        this.m_bSaved = bSaved;
        this.m_bDisp = bDisp;
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
        this.m_iProtTcpIpClientValueNrOfDecimal = 0;
        this.m_strProtTcpIpClientValueUM = "";
        this.m_iProtTcpIpClientValueMinNrCharToShow = 0;
        this.m_bProtTcpIpClientValueReadOnly = false;
        this.m_iProtTcpIpClientValueUpdateMillis = 1000;
        this.m_bProtTcpIpClientSendDataOnChange = false;
        this.m_bProtTcpIpClientWaitAnswerBeforeSendNextData = false;
    }
/*
    public void update(NumericValueData lsd){
        if(lsd != null){
            this.m_ID = lsd.getID();
            this.m_bSaved = lsd.getSaved();
            this.m_bDisp = lsd.getDisp();
            this.m_lRoomID = lsd.getRoomID();
            this.m_strTAG = lsd.getTag();
            this.m_fPosX = lsd.getPosX();
            this.m_fPosY = lsd.getPosY();
            this.m_fPosZ = lsd.getPosZ();
            this.m_bLandscape = lsd.getLandscape();
         }
    }
*/
    public void setProtTcpIpClient(boolean bProtTcpIpClientEnable, long lProtTcpIpClientID, int iProtTcpIpClientValueID, int iProtTcpIpClientValueAddress, int iProtTcpIpClientValueDataType, int iProtTcpIpClientValueNrOfDecimal, String strProtTcpIpClientValueUM, int iProtTcpIpClientValueMinNrCharToShow, boolean bProtTcpIpClientValueReadOnly, int iProtTcpIpClientValueUpdateMillis, boolean bProtTcpIpClientSendDataOnChange, boolean bProtTcpIpClientWaitAnswerBeforeSendNextData) {
        this.m_bProtTcpIpClientEnable = bProtTcpIpClientEnable;
        this.m_lProtTcpIpClientID = lProtTcpIpClientID;
        this.m_iProtTcpIpClientValueID = iProtTcpIpClientValueID;
        this.m_iProtTcpIpClientValueAddress = iProtTcpIpClientValueAddress;
        this.m_iProtTcpIpClientValueDataType = iProtTcpIpClientValueDataType;
        this.m_iProtTcpIpClientValueNrOfDecimal = iProtTcpIpClientValueNrOfDecimal;
        this.m_strProtTcpIpClientValueUM = strProtTcpIpClientValueUM;
        this.m_iProtTcpIpClientValueMinNrCharToShow = iProtTcpIpClientValueMinNrCharToShow;
        this.m_bProtTcpIpClientValueReadOnly = bProtTcpIpClientValueReadOnly;
        this.m_iProtTcpIpClientValueUpdateMillis = iProtTcpIpClientValueUpdateMillis;
        this.m_bProtTcpIpClientSendDataOnChange = bProtTcpIpClientSendDataOnChange;
        this.m_bProtTcpIpClientWaitAnswerBeforeSendNextData = bProtTcpIpClientWaitAnswerBeforeSendNextData;
    }

    // Set Method
    public void setID(long id) {
        this.m_ID = id;
    }

    public void setSaved(boolean bSaved) { this.m_bSaved = bSaved; }

    public void setDisp(boolean bDisp) { this.m_bDisp = bDisp; }

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

    public void setProtTcpIpClientValueNrOfDecimal(int iProtTcpIpClientValueNrOfDecimal) {
        this.m_iProtTcpIpClientValueNrOfDecimal = iProtTcpIpClientValueNrOfDecimal;
    }

    public void setProtTcpIpClientValueUM(String strProtTcpIpClientValueUM) {
        this.m_strProtTcpIpClientValueUM = strProtTcpIpClientValueUM;
    }

    public void setProtTcpIpClientValueMinNrCharToShow(int iProtTcpIpClientValueMinNrCharToShow) {
        this.m_iProtTcpIpClientValueMinNrCharToShow = iProtTcpIpClientValueMinNrCharToShow;
    }

    public void setProtTcpIpClientValueReadOnly(boolean bProtTcpIpClientValueReadOnly) {
        this.m_bProtTcpIpClientValueReadOnly = bProtTcpIpClientValueReadOnly;
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

    // Get Method
    public long getID() { return m_ID; }

    public boolean getSaved() { return m_bSaved; }

    public boolean getDisp() { return m_bDisp; }

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

    public int getProtTcpIpClientValueNrOfDecimal() {
        return m_iProtTcpIpClientValueNrOfDecimal;
    }

    public String getProtTcpIpClientValueUM() {
        return m_strProtTcpIpClientValueUM;
    }

    public int getProtTcpIpClientValueMinNrCharToShow() {
        return m_iProtTcpIpClientValueMinNrCharToShow;
    }

    public boolean getProtTcpIpClientValueReadOnly() {
        return m_bProtTcpIpClientValueReadOnly;
    }

    public int getProtTcpIpClientValueUpdateMillis() {
        return m_iProtTcpIpClientValueUpdateMillis;
    }

    public boolean getProtTcpIpClientSendDataOnChange() { return m_bProtTcpIpClientSendDataOnChange; }

    public boolean getProtTcpIpClientWaitAnswerBeforeSendNextData() { return m_bProtTcpIpClientWaitAnswerBeforeSendNextData;  }

    protected NumericValueData(Parcel in) {
        m_ID = in.readLong();
        m_bSaved = in.readByte() != 0;
        m_bDisp = in.readByte() != 0;
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
        m_iProtTcpIpClientValueNrOfDecimal = in.readInt();
        m_strProtTcpIpClientValueUM = in.readString();
        m_iProtTcpIpClientValueMinNrCharToShow = in.readInt();
        m_bProtTcpIpClientValueReadOnly = in.readByte() != 0;
        m_iProtTcpIpClientValueUpdateMillis = in.readInt();
        m_bProtTcpIpClientSendDataOnChange = in.readByte() != 0;
        m_bProtTcpIpClientWaitAnswerBeforeSendNextData = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(m_ID);
        dest.writeByte((byte) (m_bSaved ? 1 : 0));
        dest.writeByte((byte) (m_bDisp ? 1 : 0));
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
        dest.writeInt(m_iProtTcpIpClientValueNrOfDecimal);
        dest.writeString(m_strProtTcpIpClientValueUM);
        dest.writeInt(m_iProtTcpIpClientValueMinNrCharToShow);
        dest.writeByte((byte) (m_bProtTcpIpClientValueReadOnly ? 1 : 0));
        dest.writeInt(m_iProtTcpIpClientValueUpdateMillis);
        dest.writeByte((byte) (m_bProtTcpIpClientSendDataOnChange ? 1 : 0));
        dest.writeByte((byte) (m_bProtTcpIpClientWaitAnswerBeforeSendNextData ? 1 : 0));
    }

    @SuppressWarnings("unused")
    public static final Creator<NumericValueData> CREATOR = new Creator<NumericValueData>() {
        @Override
        public NumericValueData createFromParcel(Parcel in) {
            return new NumericValueData(in);
        }

        @Override
        public NumericValueData[] newArray(int size) {
            return new NumericValueData[size];
        }
    };
}
