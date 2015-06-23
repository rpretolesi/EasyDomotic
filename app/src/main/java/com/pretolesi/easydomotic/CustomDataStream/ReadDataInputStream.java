package com.pretolesi.easydomotic.CustomDataStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

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
    private static ReentrantLock m_LockCommandHolder;

    private DataInputStream m_dis;
    private List<Byte> m_abq;


    public ReadDataInputStream(DataInputStream dis, short shBufferSize) {
        m_LockCommandHolder = new ReentrantLock();
        m_dis = dis;
        if(shBufferSize > 0) {
            m_abq = new ArrayList<>(shBufferSize);
        }
    }

    @Override
    public void run() {
        //Code
        if(m_dis == null){
            return;
        }

        while(!isInterrupted()){
            try {
                byte b = m_dis.readByte();

                m_LockCommandHolder.lock();

                m_abq.add(b);
                if(m_vReadDataInputStreamListener != null) {
                    for (ReadDataInputStreamListener rdisl : m_vReadDataInputStreamListener) {
                        rdisl.onReadDataInputStreamCallback();
                    }
                }
            } catch (IOException ex) {

            }
            finally
            {
                m_LockCommandHolder.unlock();
            }

        }
    }

    public byte[] getData(){
        m_LockCommandHolder.lock();

        Byte byteData[];
        byteData = m_abq.toArray();
        m_abq.clear();
        m_LockCommandHolder.unlock();
        return byteData;
    }

    public interface ReadDataInputStreamListener {
        /**
         * Callbacks
         */
        void onReadDataInputStreamCallback();
    }
}
