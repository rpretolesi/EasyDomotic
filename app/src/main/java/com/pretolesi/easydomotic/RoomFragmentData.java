package com.pretolesi.easydomotic;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ricca_000 on 20/03/2015.
 */
public class RoomFragmentData implements Parcelable {

    private long m_ID;
    private String m_strHouseTAG;
    private String m_strTAG;
    private float m_fPosX;
    private float m_fPosY;
    private float m_fPosZ;
    private boolean m_bLandscape;


    public RoomFragmentData(long id, String strHouseTAG, String strTAG, float fPosX, float fPosY, float fPosZ, boolean bLandscape) {
        this.m_ID = id;
        this.m_strHouseTAG = strHouseTAG;
        this.m_strTAG = strTAG;
        this.m_fPosX = fPosX;
        this.m_fPosY = fPosY;
        this.m_fPosZ = fPosZ;
        this.m_bLandscape = bLandscape;
    }

    public RoomFragmentData(String strHouseTAG, String strTAG, float fPosX, float fPosY, float fPosZ, boolean bLandscape) {
        this.m_ID = 0;
        this.m_strHouseTAG = strHouseTAG;
        this.m_strTAG = strTAG;
        this.m_fPosX = fPosX;
        this.m_fPosY = fPosY;
        this.m_fPosZ = fPosZ;
        this.m_bLandscape = bLandscape;
    }

    public RoomFragmentData() {
        this.m_ID = 0;
        this.m_strHouseTAG = "";
        this.m_strTAG = "";
        this.m_fPosX = 0;
        this.m_fPosY = 0;
        this.m_fPosZ = 0;
        this.m_bLandscape = false;
    }

    public void setID(long id) { this.m_ID = id; }

    public void setHouseTAG(String strHouseTAG) {
        this.m_strHouseTAG = strHouseTAG;
    }

    public void setTAG(String strTAG) {
        this.m_strTAG = strTAG;
    }

    public void setPosX(float fPosX) {
        this.m_fPosX = fPosX;
    }

    public void setPosY(float fPosY) {
        this.m_fPosY = fPosY;
    }

    public void setPosZ(float fPosZ) {
        this.m_fPosZ = fPosZ;
    }

    public void setLandscape(boolean bLandscape) {
        this.m_bLandscape = bLandscape;
    }

    public String getHouseTAG() {
        return m_strHouseTAG;
    }

    public String getTAG() {
        return m_strTAG;
    }

    public float getPosX() {
        return m_fPosX;
    }

    public float getPosY() {
        return m_fPosY;
    }

    public float getPosZ() {
        return m_fPosZ;
    }

    public boolean getLandscape() {
        return m_bLandscape;
    }

    protected RoomFragmentData(Parcel in) {
        m_ID = in.readLong();
        m_strHouseTAG = in.readString();
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
        dest.writeLong(m_ID);
        dest.writeString(m_strHouseTAG);
        dest.writeString(m_strTAG);
        dest.writeFloat(m_fPosX);
        dest.writeFloat(m_fPosY);
        dest.writeFloat(m_fPosZ);
        dest.writeByte((byte) (m_bLandscape ? 1 : 0));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RoomFragmentData> CREATOR = new Parcelable.Creator<RoomFragmentData>() {
        @Override
        public RoomFragmentData createFromParcel(Parcel in) {
            return new RoomFragmentData(in);
        }

        @Override
        public RoomFragmentData[] newArray(int size) {
            return new RoomFragmentData[size];
        }
    };
}