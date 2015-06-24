package com.pretolesi.easydomotic.CustomDataStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
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

    private DataInputStream m_dis;
    private byte m_byteData[];
    private short m_shDataLenght;
    private final ReentrantLock m_Lock = new ReentrantLock();
    private final Condition m_notFull  = m_Lock.newCondition();
    private final Condition m_notEmpty = m_Lock.newCondition();


    public ReadDataInputStream(DataInputStream dis, short shDataSize) {
        m_vReadDataInputStreamListener = new Vector<>();
        m_dis = dis;
        m_byteData = new byte[shDataSize];;
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
            bReadOk = false;

            try {
                // wait in case of too data
                while (m_shDataLenght == m_byteData.length)
                        m_notFull.await();

                // Get data
                byte byteData = m_dis.readByte();
                bReadOk = true;

                // Lock
                m_Lock.lock();

                // Put data on buffer
                m_byteData[m_shDataLenght] = byteData;
                m_shDataLenght = (short)(m_shDataLenght + 1);

            } catch (InterruptedException ex) {
            } catch (IOException ex) {
            }
            finally {
                if(m_Lock.isLocked()){
                    m_Lock.unlock();
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
        if(m_byteData == null){
            return null;
        }

        // Lock
        m_Lock.lock();

        byte[] byteData = null;

        try{
            byteData = Arrays.copyOf(m_byteData,m_shDataLenght);
            m_shDataLenght = 0;
            m_notFull.signal();
            vedere se mettere qui il return
        } catch (NegativeArraySizeException ex){

        }
        finally {
            if(m_Lock.isLocked()){
                m_Lock.unlock();
            }
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
