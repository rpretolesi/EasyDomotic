package com.pretolesi.easydomotic.TcpIpClient;

import android.content.Context;

import java.util.List;
import java.util.Vector;

/**
 *
 */
public class TciIpClientHelper {

    private static Context m_context;
    private static TciIpClientHelper m_Instance;
    private static List<TCPIPClient> m_ltic;

    private TciIpClientHelper(List<TCPIPClientData> lticd)
    {
        if ((m_ltic == null))
        {
            if(lticd != null && !lticd.isEmpty()) {
                m_ltic = new Vector<>();
                for(TCPIPClientData ticd : lticd){
                    TCPIPClient tic = new TCPIPClient(m_context);
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
    public synchronized static TciIpClientHelper startInstance(Context context, List<TCPIPClientData> lticd)
    {
        if (context != null && lticd != null && m_Instance == null)
        {
            m_Instance = new TciIpClientHelper(lticd);
        }
        return m_Instance;
    }

    public synchronized static void stopInstance()
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

            m_Instance = null;
        }
    }

    public synchronized static TciIpClientHelper getInstance()
    {
        return m_Instance;
    }

    public synchronized List<TCPIPClient> getTciIpClient()
    {
        // Initialize if not already done
        return m_ltic;
    }

    public synchronized TCPIPClient getTciIpClient(long lID)
    {
        if (m_Instance != null)
        {
            // Initialize if not already done
            if(m_ltic != null && !m_ltic.isEmpty()) {
                for(TCPIPClient tic : m_ltic){
                    if(tic.getID() == lID){
                        return tic;
                    }
                }
                m_ltic.clear();
            }
        }

        return null;
    }

}
