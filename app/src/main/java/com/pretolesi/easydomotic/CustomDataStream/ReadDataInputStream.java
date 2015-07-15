package com.pretolesi.easydomotic.CustomDataStream;

import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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
    private static final String TAG = "ReadDataInputStream";
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
//    private final Condition m_notEmpty = m_Lock.newCondition();


    public ReadDataInputStream(DataInputStream dis, short shDataSize) {
        m_vReadDataInputStreamListener = new Vector<>();
        m_dis = dis;
        m_byteData = new byte[shDataSize];;
        m_shDataLenght = 0;
    }

    @Override
    public void run() {
        byte byteData = -111;

        //Code
        if(m_dis == null){
            return;
        }

        while(!isInterrupted()){
            boolean bReadOk;
            bReadOk = false;
            try {
//                // Get data
                byteData = m_dis.readByte();
                bReadOk = true;
            } catch (IOException ex) {
                int i = 0;
                i=i+1;
            } catch (Exception ex) {
                int i = 0;
                i=i+1;
            }

            if(!isInterrupted() && bReadOk){

                // Lock
                m_Lock.lock();

                try {
                    // wait in case of too data
                    while (m_shDataLenght == m_byteData.length) {
                        Log.d(TAG, "Before Await");
                        m_notFull.await();
                        Log.d(TAG, "After Await");
                    }

                    // Get data
    //                byteData = m_dis.readByte();
    //                bReadOk = true;

                    // Lock
    //                m_Lock.lock();

                    // Put data on buffer
                    m_byteData[m_shDataLenght] = byteData;
                    m_shDataLenght = (short)(m_shDataLenght + 1);

                } catch (InterruptedException ex) {
                } catch (Exception ex) {
                }
                finally {
                    if(m_Lock.isLocked()){
                        m_Lock.unlock();
                    }
                }

                if (m_vReadDataInputStreamListener != null) {
                    for (ReadDataInputStreamListener rdisl : m_vReadDataInputStreamListener) {
                        if(rdisl != null) {
                            rdisl.onReadDataInputStreamCallback();
                        }
                    }
                }
            }
        }

        if (m_vReadDataInputStreamListener != null) {
            for (ReadDataInputStreamListener rdisl : m_vReadDataInputStreamListener) {
                if(rdisl != null) {
                    rdisl.onCloseReadDataInputStreamCallback();
                }
            }
        }
        Log.d(TAG, "End Run");
    }

    public byte[] getData(boolean bDeleteData){
        // Lock
        m_Lock.lock();

        if(m_byteData == null){
            return null;
        }

        byte[] byteData = null;

        try{
            if(m_shDataLenght == 0){
                return null;
            }
            byteData = Arrays.copyOf(m_byteData,m_shDataLenght);
            if(bDeleteData){
                m_shDataLenght = 0;
                m_notFull.signal();
            }
            return byteData;

        } catch (NegativeArraySizeException ex){

        }
        finally {
            if(m_Lock.isLocked()){
                m_Lock.unlock();
            }
        }
        return null;
    }

    public interface ReadDataInputStreamListener {
        /**
         * Callbacks
         */
        void onReadDataInputStreamCallback();
        void onCloseReadDataInputStreamCallback();
    }
}
