package com.pretolesi.easydomotic.LightSwitch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class LightSwitchData implements Parcelable {

    public static int TAGMinChar = 1;
    public static int TAGMaxChar = 128;
    public static String TAGDefaultValue = "My Name";

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
    private int m_iProtTcpIpClientValueOFF;
    private int m_iProtTcpIpClientValueOFFON;
    private int m_iProtTcpIpClientValueONOFF;
    private int m_iProtTcpIpClientValueON;
    private boolean m_bProtTcpIpClientSendDataOnChange;
    private boolean m_bProtTcpIpClientWaitAnswerBeforeSendNextData;

    public LightSwitchData() {
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
        this.m_iProtTcpIpClientValueOFF = 0;
        this.m_iProtTcpIpClientValueOFFON = 0;
        this.m_iProtTcpIpClientValueONOFF = 0;
        this.m_iProtTcpIpClientValueON = 0;
        this.m_bProtTcpIpClientSendDataOnChange = false;
        this.m_bProtTcpIpClientWaitAnswerBeforeSendNextData = false;
    }

    public LightSwitchData(long id, boolean bSaved, boolean bDisp, long lRoomID, String strTAG, float fPosX, float fPosY, float fPosZ, boolean bLandscape) {
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
        this.m_iProtTcpIpClientValueOFF = 0;
        this.m_iProtTcpIpClientValueOFFON = 0;
        this.m_iProtTcpIpClientValueONOFF = 0;
        this.m_iProtTcpIpClientValueON = 0;
        this.m_bProtTcpIpClientSendDataOnChange = false;
        this.m_bProtTcpIpClientWaitAnswerBeforeSendNextData = false;
    }

    public void update(LightSwitchData lsd){
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

    public void setProtTcpIpClient(boolean bProtTcpIpClientEnable, long lProtTcpIpClientID, int iProtTcpIpClientValueID, int iProtTcpIpClientValueOFF, int iProtTcpIpClientValueOFFON, int iProtTcpIpClientValueONOFF, int iProtTcpIpClientValueON, boolean bProtTcpIpClientSendDataOnChange, boolean m_bProtTcpIpClientWaitAnswerBeforeSendNextData) {
        this.m_bProtTcpIpClientEnable = bProtTcpIpClientEnable;
        this.m_lProtTcpIpClientID = lProtTcpIpClientID;
        this.m_iProtTcpIpClientValueID = iProtTcpIpClientValueID;
        this.m_iProtTcpIpClientValueOFF = iProtTcpIpClientValueOFF;
        this.m_iProtTcpIpClientValueOFFON = iProtTcpIpClientValueOFFON;
        this.m_iProtTcpIpClientValueONOFF = iProtTcpIpClientValueONOFF;
        this.m_iProtTcpIpClientValueON = iProtTcpIpClientValueON;
        this.m_bProtTcpIpClientSendDataOnChange = bProtTcpIpClientSendDataOnChange;
        this.m_bProtTcpIpClientWaitAnswerBeforeSendNextData = m_bProtTcpIpClientWaitAnswerBeforeSendNextData;
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

    public void setProtTcpIpClientValueOFF(int iProtTcpIpClientValueOFF) {
        this.m_lProtTcpIpClientID = iProtTcpIpClientValueOFF;
    }

    public void setProtTcpIpClientValueOFFON(int iProtTcpIpClientValueOFFON) {
        this.m_iProtTcpIpClientValueOFFON = iProtTcpIpClientValueOFFON;
    }

    public void setProtTcpIpClientValueON(int iProtTcpIpClientValueON) {
        this.m_iProtTcpIpClientValueON = iProtTcpIpClientValueON;
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
    public boolean getTcpIpClientEnable() {
        return m_bProtTcpIpClientEnable;
    }

    public long getTcpIpClientID() {
        return m_lProtTcpIpClientID;
    }

    public int getProtTcpIpClientValueID() { return m_iProtTcpIpClientValueID; }

    public int getProtTcpIpClientValueOFF() {
        return m_iProtTcpIpClientValueOFF;
    }

    public int getProtTcpIpClientValueOFFON() {
        return m_iProtTcpIpClientValueOFFON;
    }

    public int getProtTcpIpClientValueONOFFN() {
        return m_iProtTcpIpClientValueONOFF;
    }

    public int getProtTcpIpClientValueON() {
        return m_iProtTcpIpClientValueON;
    }

    public boolean getProtTcpIpClientSendDataOnChange() {
        return m_bProtTcpIpClientSendDataOnChange;
    }

    public boolean getProtTcpIpClientWaitAnswerBeforeSendNextData() {
        return m_bProtTcpIpClientWaitAnswerBeforeSendNextData;
    }

    protected LightSwitchData(Parcel in) {
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
        m_iProtTcpIpClientValueOFF = in.readInt();
        m_iProtTcpIpClientValueOFFON = in.readInt();
        m_iProtTcpIpClientValueONOFF = in.readInt();
        m_iProtTcpIpClientValueON = in.readInt();
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
        dest.writeInt(m_iProtTcpIpClientValueOFF);
        dest.writeInt(m_iProtTcpIpClientValueOFFON);
        dest.writeInt(m_iProtTcpIpClientValueONOFF);
        dest.writeInt(m_iProtTcpIpClientValueON);
        dest.writeByte((byte) (m_bProtTcpIpClientSendDataOnChange ? 1 : 0));
        dest.writeByte((byte) (m_bProtTcpIpClientWaitAnswerBeforeSendNextData ? 1 : 0));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<LightSwitchData> CREATOR = new Parcelable.Creator<LightSwitchData>() {
        @Override
        public LightSwitchData createFromParcel(Parcel in) {
            return new LightSwitchData(in);
        }

        @Override
        public LightSwitchData[] newArray(int size) {
            return new LightSwitchData[size];
        }
    };

}
