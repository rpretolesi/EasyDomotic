package com.pretolesi.easydomotic.LightSwitch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ricca_000 on 20/03/2015.
 */
public class LightSwitchData implements Parcelable {

    private boolean m_bSaved;
    private boolean m_bSelected;
    private long m_ID;
    private String m_strRoomTAG;
    private String m_strTAG;
    private float m_fPosX;
    private float m_fPosY;
    private float m_fPosZ;
    private boolean m_bLandscape;


    public LightSwitchData(boolean bSaved, boolean bSelected, long id, String strRoomTAG, String strTAG, float fPosX, float fPosY, float fPosZ, boolean bLandscape) {
        this.m_bSaved = bSaved;
        this.m_bSelected = bSelected;
        this.m_ID = id;
        this.m_strRoomTAG = strRoomTAG;
        this.m_strTAG = strTAG;
        this.m_fPosX = fPosX;
        this.m_fPosY = fPosY;
        this.m_fPosZ = fPosZ;
        this.m_bLandscape = bLandscape;
    }

    public void update(LightSwitchData lsd){
        if(lsd != null){
            this.m_bSaved = lsd.getSaved();
            this.m_bSelected = lsd.getSelected();
            this.m_ID = lsd.getID();
            this.m_strRoomTAG = lsd.getRoomTAG();
            this.m_strTAG = lsd.getTag();
            this.m_fPosX = lsd.getPosX();
            this.m_fPosY = lsd.getPosY();
            this.m_fPosZ = lsd.getPosZ();
            this.m_bLandscape = lsd.getLandscape();
        }
    }

    public void setSaved(boolean bSaved) { this.m_bSaved = bSaved; }

    public void setSelected(boolean bSelected) { this.m_bSelected = bSelected; }

    public void setID(long id) {
        this.m_ID = id;
    }

    public void setRoomTAG(String strRoomTAG) {
        this.m_strRoomTAG = strRoomTAG;
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

    public boolean getSaved() { return m_bSaved; }

    public boolean getSelected() { return m_bSelected; }

    public long getID() { return m_ID; }

    public String getRoomTAG() {
        return m_strRoomTAG;
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

    protected LightSwitchData(Parcel in) {
        m_bSaved = in.readByte() != 0;
        m_bSelected = in.readByte() != 0;
        m_ID = in.readLong();
        m_strRoomTAG = in.readString();
        m_strTAG = in.readString();
        m_fPosX = in.readFloat();
        m_fPosY = in.readFloat();
        m_fPosZ = in.readFloat();
        m_bLandscape = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (m_bSaved ? 1 : 0));
        dest.writeByte((byte) (m_bSelected ? 1 : 0));
        dest.writeLong(m_ID);
        dest.writeString(m_strRoomTAG);
        dest.writeString(m_strTAG);
        dest.writeFloat(m_fPosX);
        dest.writeFloat(m_fPosY);
        dest.writeFloat(m_fPosZ);
        dest.writeByte((byte) (m_bLandscape ? 1 : 0));
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
