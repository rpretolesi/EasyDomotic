package com.pretolesi.easydomotic.Modbus;

import android.content.Context;

import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpMsg;

import java.nio.ByteBuffer;

/**
 *
 */
public class Modbus {
/*
    // Listener e Callback
    private static List<ModbusListener> m_vListener = new Vector<>();

    // Imposto il listener
    public static synchronized void registerListener(ModbusListener listener) {
        if(!m_vListener.contains(listener)){
            m_vListener.add(listener);
        }
    }
    public static synchronized void unregisterListener(ModbusListener listener) {
        if(m_vListener.contains(listener)){
            m_vListener.remove(listener);
        }
    }
*/
    public static synchronized TcpIpMsg writeSingleRegister(Context context, int iTransactionIdentifier, int iUnitIdentifier, int iAddress, int iValue) throws ModbusTransIdOutOfRangeException, ModbusUnitIdOutOfRangeException, ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException {
        short shTransactionIdentifier;
        byte byteUnitIdentifier;
        short shAddress;
        short shValue;
        if(iTransactionIdentifier >= 0 && iTransactionIdentifier <= 65535){
            shTransactionIdentifier = (short) iTransactionIdentifier;
        } else {
            throw new ModbusTransIdOutOfRangeException(context.getString(R.string.ModbusTransIdOutOfRangeException));
        }
        if(iUnitIdentifier >= 0 && iUnitIdentifier <= 255){
            byteUnitIdentifier = (byte) iUnitIdentifier;
        } else {
            throw new ModbusUnitIdOutOfRangeException(context.getString(R.string.ModbusTransIdOutOfRangeException));
        }
        if(iAddress >= 0 && iAddress <= 65535){
            shAddress = (short) iAddress;
        } else {
            throw new ModbusAddressOutOfRangeException(context.getString(R.string.ModbusAddressOutOfRangeException));
        }
        if(iValue >= 0 && iValue <= 65535){
            shValue = (short) iValue;
        } else {
            throw new ModbusValueOutOfRangeException(context.getString(R.string.ModbusValueOutOfRangeException));
        }

        ByteBuffer bb = ByteBuffer.allocate(12);
        bb.putShort(shTransactionIdentifier);
        bb.putShort((short)0);
        bb.putShort((short)6);
        bb.put(byteUnitIdentifier);
        bb.put((byte)0x06);
        bb.putShort(shAddress);
        bb.putShort(shValue);

        TcpIpMsg tim = new TcpIpMsg(iTransactionIdentifier, bb.array());

        return tim;
//        return bb.array();
    }

    public static synchronized ModbusMBAP getMBAP(Context context, byte[] byteMBA) throws ModbusProtocolOutOfRangeException, ModbusLengthOutOfRangeException, ModbusMBAPLengthException {
        // Max message length 260 byte
        if(byteMBA != null && byteMBA.length == 6){
            ByteBuffer bb = ByteBuffer.wrap(byteMBA);
            int iTI = bb.getShort(); // Transaction Identifier
            int iPI = bb.getShort(); // Protocol Identifier, must be 0
            if(iPI != 0){
                throw new ModbusProtocolOutOfRangeException(context.getString(R.string.ModbusProtocolOutOfRangeException));
            }
            int iLength = bb.getShort(); // Length
            if(iLength < 5 || iLength > 254){
                throw new ModbusLengthOutOfRangeException(context.getString(R.string.ModbusLengthOutOfRangeException));
            }
            return new ModbusMBAP(iTI, iPI, iLength);
        }

        throw new ModbusMBAPLengthException(context.getString(R.string.ModbusMBAPLengthException));
    }

    public static synchronized ModbusPDU getPDU(Context context, long lProtTcpIpClientID, byte[] byteMBA, byte[] byteDATA) throws ModbusProtocolOutOfRangeException, ModbusLengthOutOfRangeException, ModbusMBAPLengthException, ModbusPDULengthException {
        // Max total message length 260 byte
        ModbusPDU mpdu = null;

        int iTransactionIdentifier = 0; // Transaction Identifier
        int iLength = 0;
        if(byteMBA != null && byteMBA.length == 6){
            ByteBuffer bb = ByteBuffer.wrap(byteMBA);
            iTransactionIdentifier = bb.getShort(); // Transaction Identifier
            int iPI = bb.getShort(); // Protocol Identifier, must be 0
            if(iPI != 0){
                throw new ModbusProtocolOutOfRangeException(context.getString(R.string.ModbusProtocolOutOfRangeException));
            }
            iLength = bb.getShort(); // Length
            if(iLength < 5 || iLength > 254){
                throw new ModbusLengthOutOfRangeException(context.getString(R.string.ModbusLengthOutOfRangeException));
            }
        } else {
            throw new ModbusMBAPLengthException(context.getString(R.string.ModbusMBAPLengthException));
        }

        if(byteDATA != null && byteDATA.length == iLength - 6){
            ByteBuffer bb = ByteBuffer.wrap(byteDATA);
            if(byteDATA.length < 3 || byteDATA.length > 254){
                throw new ModbusLengthOutOfRangeException(context.getString(R.string.ModbusLengthOutOfRangeException));
            }
             // Unit Identifier
            int iUI = bb.get();
            // Function Code
            int iFEC = bb.get() & 0xFF;
            switch(iFEC) {
                case 0x06:
                    int iRegisterAddress = bb.getShort();
                    int iRegisterValue = bb.getShort();

                    mpdu = new ModbusPDU(iUI, iFEC, 0);

                    break;

                case 0x86:
                    int iExceptionCode = bb.get();

                    mpdu = new ModbusPDU(iUI, iFEC, iExceptionCode);

                    break;
            }
        } else {
            throw new ModbusPDULengthException(context.getString(R.string.ModbusPDULengthException));
        }

        return mpdu;
    }

    /*
      * Send callbacks
      */
/*
    private static void sendWriteSingleRegisterOkCallback(ModbusStatus ms){
        if(m_vListener != null) {
            for (ModbusListener ml : m_vListener) {
                ml.onWriteSingleRegisterOkCallback(ms);
            }
        }
    }

    private static void sendWriteSingleRegisterExceptionCallback(ModbusStatus ms){
        if(m_vListener != null) {
            for (ModbusListener ml : m_vListener) {
                ml.onWriteSingleRegisterExceptionCallback(ms);
            }
        }
    }
*/
    /**
     * Callbacks interface.
     */

//    public static interface ModbusListener {
        /**
         * Callbacks
         */
//        void onWriteSingleRegisterOkCallback(ModbusStatus ms);
//        void onWriteSingleRegisterExceptionCallback(ModbusStatus ms);
//    }
}
