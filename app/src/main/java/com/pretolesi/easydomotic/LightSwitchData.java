package com.pretolesi.easydomotic;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ricca_000 on 20/03/2015.
 */
public class LightSwitchData implements Parcelable {

    private long m_ID;
    private String m_strRoomTAG;
    private String m_strTAG;
    private float m_fPosX;
    private float m_fPosY;
    private float m_fPosZ;


    public LightSwitchData(long id, String strRoomTAG, String strTAG, float fPosX, float fPosY, float fPosZ) {
        this.m_ID = id;
        this.m_strRoomTAG = strRoomTAG;
        this.m_strTAG = strTAG;
        this.m_fPosX = fPosX;
        this.m_fPosY = fPosY;
        this.m_fPosZ = fPosZ;
    }

    public LightSwitchData(String strRoomTAG, String strTAG, float fPosX, float fPosY, float fPosZ) {
        this.m_ID = 0;
        this.m_strRoomTAG = strRoomTAG;
        this.m_strTAG = strTAG;
        this.m_fPosX = fPosX;
        this.m_fPosY = fPosY;
        this.m_fPosZ = fPosZ;
    }
verificare se c'e' un metodo piu' pulito per fare questa operazione'
    public void update(LightSwitchData lsd){
        this.m_ID = lsd.getID();
        this.m_strRoomTAG = lsd.getRoomTAG();
        this.m_strTAG = lsd.getTAG();
        this.m_fPosX = lsd.getPosX();
        this.m_fPosY = lsd.getPosY();
        this.m_fPosZ = lsd.getPosZ();
    }

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

    public long getID() { return m_ID; }

    public String getRoomTAG() {
        return m_strRoomTAG;
    }

    public String getTAG() {
        return m_strTAG;
    }

    public float getPosX() {
        return m_fPosX;
    }

    public float getPosY() { return m_fPosY; }

    public float getPosZ() {
        return m_fPosZ;
    }

    protected LightSwitchData(Parcel in) {
        m_ID = in.readLong();
        m_strRoomTAG = in.readString();
        m_strTAG = in.readString();
        m_fPosX = in.readFloat();
        m_fPosY = in.readFloat();
        m_fPosZ = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(m_ID);
        dest.writeString(m_strRoomTAG);
        dest.writeString(m_strTAG);
        dest.writeFloat(m_fPosX);
        dest.writeFloat(m_fPosY);
        dest.writeFloat(m_fPosZ);
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
