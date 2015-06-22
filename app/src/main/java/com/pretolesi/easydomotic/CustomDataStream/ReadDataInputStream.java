package com.pretolesi.easydomotic.CustomDataStream;

import java.io.DataInputStream;
import java.util.List;

/**
 * Created by RPRETOLESI on 22/06/2015.
 */
public class ReadDataInputStream extends Thread {
    // Listener e Callback
    // Client
    protected List<ReadDataInputStreamListener> m_vReadDataInputStreamListener = null;
    // Imposto il listener
    public synchronized void registerReadDataInputStream(ReadDataInputStreamListener listener) {
        if(m_vReadDataInputStreamListener != null && !m_vReadDataInputStreamListener.contains(listener)){
            m_vReadDataInputStreamListener.add(listener);
        }
    }

    private DataInputStream m_dis;
    private byte[] m_byteInput;


    public ReadDataInputStream(DataInputStream dis, short shBufferSize) {
        m_dis = dis;
        if(shBufferSize > 0) {
            m_byteInput = new byte[shBufferSize];
        }
    }

    @Override
    public void run() {
        //Code
        if(m_dis == null){
            return;
        }

        while(isInterrupted()){

        }
    }

    public interface ReadDataInputStreamListener {
        /**
         * Callbacks
         */
        void onReadDataInputStreamCallback();
    }
}
