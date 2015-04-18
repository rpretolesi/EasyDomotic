package com.pretolesi.easydomotic.TcpIpClient;

import android.content.Context;

import java.util.List;
import java.util.Vector;

/**
 *
 */
public class TciIpClientHelper {

    private static TciIpClientHelper m_Instance;
    private static List<TCPIPClientData> m_lticd;
    private static List<TCPIPClient> m_ltic;

    private TciIpClientHelper(List<TCPIPClientData> lticd)
    {
        m_lticd = lticd;

        if ((m_ltic == null))
        {
            if(m_lticd != null && !m_lticd.isEmpty()) {
                m_ltic = new Vector<>();
                for(TCPIPClientData ticd : m_lticd){
                    TCPIPClient tic = new TCPIPClient();
                    tic.execute(ticd);
                    m_ltic.add(tic);
                }
            }
        }

    }

    /**
     * Get default instance of the class to keep it a singleton
     *
     * @param context
     *            the application context
     */
    public static TciIpClientHelper startInstance(Context context, List<TCPIPClientData> lticd)
    {
        if (context != null && lticd != null && m_Instance == null)
        {
            m_Instance = new TciIpClientHelper(lticd);
        }
        return m_Instance;
    }

    public static void stopInstance()
    {
        if (m_Instance != null)
        {
            // Initialize if not already done
            if(m_ltic != null && !m_ltic.isEmpty()) {
                 for(TCPIPClient tic : m_ltic){
                     tic.cancel(false);
                }
                m_ltic.clear();
            }
            m_ltic = null;

            if(m_lticd != null && !m_lticd.isEmpty()) {
                m_lticd.clear();
            }
            m_lticd = null;

            m_Instance = null;
        }
    }

    public static TciIpClientHelper getInstance()
    {
        return m_Instance;
    }

    public List<TCPIPClient> getTciIpClient()
    {
        // Initialize if not already done
        togliere questo e metterlo solo nello start.
        if ((m_ltic == null))
        {
            if(m_lticd != null && !m_lticd.isEmpty()) {
                m_ltic = new Vector<>();
                for(TCPIPClientData ticd : m_lticd){
                    TCPIPClient tic = new TCPIPClient();
                    tic.execute(ticd);
                    m_ltic.add(tic);
                }
            }
        }

        return m_ltic;
    }

}
