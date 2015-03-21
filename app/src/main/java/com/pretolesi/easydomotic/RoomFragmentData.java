package com.pretolesi.easydomotic;

/**
 * Created by ricca_000 on 20/03/2015.
 */
public class RoomFragmentData {

    private String m_strHouseTAG;
    private String m_strTAG;
    private float m_fPosX;
    private float m_fPosY;


    public RoomFragmentData(String strHouseTAG, String strTAG, float fPosX, float fPosY) {
        this.m_strHouseTAG = strHouseTAG;
        this.m_strTAG = strTAG;
        this.m_fPosX = fPosX;
        this.m_fPosY = fPosY;
    }

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

}
