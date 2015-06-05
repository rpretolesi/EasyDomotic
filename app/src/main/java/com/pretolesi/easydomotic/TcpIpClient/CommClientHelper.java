package com.pretolesi.easydomotic.TcpIpClient;

import android.content.Context;
import android.os.AsyncTask;

import com.pretolesi.easydomotic.CommClientData.BaseValueCommClientData;

import java.util.List;
import java.util.Vector;

/**
 *
 */
public class CommClientHelper {

    private static Context m_context;
    private static CommClientHelper m_Instance;
    private static List<TCPIPClient> m_ltic;

    private CommClientHelper(List<BaseValueCommClientData> lticd)
    {
        if ((m_ltic == null))
        {
            if(lticd != null && !lticd.isEmpty()) {
                m_ltic = new Vector<>();
                for(BaseValueCommClientData ticd : lticd){
                    if(ticd.getType() == BaseValueCommClientData.TYPE_TCP_IP_CLIENT){
                        TCPIPClient tic = new TCPIPClient(m_context);
    //                    tic.execute(ticd);
                        tic.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ticd);
                                m_ltic.add(tic);
                    }
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
    public synchronized static CommClientHelper startInstance(Context context, List<BaseValueCommClientData> lticd)
    {
        if (context != null && lticd != null && m_Instance == null)
        {
            m_context = context;
            m_Instance = new CommClientHelper(lticd);
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
                     tic.cancel(true);
                }
                m_ltic.clear();
            }
            m_ltic = null;

            m_Instance = null;
        }
    }

    public synchronized static CommClientHelper getInstance()
    {
        return m_Instance;
    }

    public synchronized static List<TCPIPClient> getTciIpClient()
    {
        // Initialize if not already done
        return m_ltic;
    }

    public synchronized static TCPIPClient getTciIpClient(long lID)
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
            }
        }

        return null;
    }

}
