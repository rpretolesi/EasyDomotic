package com.pretolesi.easydomotic.CustomDataStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
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
    private byte[] m_byteData;
    private short m_shDataSize;
    private short m_shDataLenght;


    public ReadDataInputStream(DataInputStream dis, short shDatarSize) {
        m_vReadDataInputStreamListener = new Vector<>();
        m_LockCommandHolder = new ReentrantLock();
        m_dis = dis;
        if(shDatarSize > 0) {
            m_byteData = new byte[shDatarSize];
            m_shDataSize = shDatarSize;
        }
        m_shDataLenght = 0;
    }

    @Override
    public void run() {
        //Code
        if(m_dis == null){
            return;
        }

        while(!isInterrupted()){
            boolean bReadOk;

            try {
                byte byteData = m_dis.readByte();

                bReadOk = true;

                m_LockCommandHolder.lock();
migliorare questo, evitando la lettura e mettendo un tempo di attesa per non sovraccaricare la cpu

                        quello che non si riesce a fare metterlo nel TO DO e toglierlo dal codice
                if(m_shDataLenght < m_shDataSize) {
                    m_byteData[m_shDataLenght] = byteData;
                    m_shDataLenght = (short) (m_shDataLenght + 1);
                }
            } catch (IOException ex) {
                bReadOk = false;
            }
            finally
            {
                if(m_LockCommandHolder.isLocked()){
                    m_LockCommandHolder.unlock();
                }
            }
            if(bReadOk) {
                if (m_vReadDataInputStreamListener != null) {
                    for (ReadDataInputStreamListener rdisl : m_vReadDataInputStreamListener) {
                        if(rdisl != null) {
                            rdisl.onReadDataInputStreamCallback();
                        }
                    }
                }
            } else {
                if(!isInterrupted()) {
                    if (m_vReadDataInputStreamListener != null) {
                        for (ReadDataInputStreamListener rdisl : m_vReadDataInputStreamListener) {
                            if(rdisl != null) {
                                rdisl.onCloseReadDataInputStreamCallback();
                            }
                        }
                    }
                }
            }
        }
    }

    public byte[] getData(){
        byte[] byteData = null;

        m_LockCommandHolder.lock();

        try{
            byteData = Arrays.copyOf(m_byteData, m_shDataLenght);
        } catch (NegativeArraySizeException ex){

        }
        finally {
            m_shDataLenght = 0;

            m_LockCommandHolder.unlock();
        }

        return byteData;
    }

    public interface ReadDataInputStreamListener {
        /**
         * Callbacks
         */
        void onReadDataInputStreamCallback();
        void onCloseReadDataInputStreamCallback();
    }
}
