package com.pretolesi.easydomotic.Modbus;

import android.content.Context;

import com.pretolesi.easydomotic.CustomException.ModbusAddressOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusLengthOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusMBAPLengthException;
import com.pretolesi.easydomotic.CustomException.ModbusProtocolOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusTransIdOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusUnitIdOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusValueOutOfRangeException;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Vector;

/**
 *
 */
public class Modbus {

    public static synchronized byte[] writeSingleRegister(Context context, int iTransactionIdentifier, int iUnitIdentifier, int iAddress, int iValue) throws ModbusTransIdOutOfRangeException, ModbusUnitIdOutOfRangeException, ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException {
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

        return bb.array();
    }

    public static synchronized int getMessageLengthFromMBAP(Context context, byte[] byteMBA) throws ModbusProtocolOutOfRangeException, ModbusLengthOutOfRangeException, ModbusMBAPLengthException {
        // Max message length 260 byte
        if(byteMBA != null && byteMBA.length == 10){
            ByteBuffer bb = ByteBuffer.wrap(byteMBA);
            bb.getShort(); // Transaction Identifier
            int iPI = bb.getShort(); // Protocol Identifier, must be 0
            if(iPI != 0){
                throw new ModbusProtocolOutOfRangeException(context.getString(R.string.ModbusProtocolOutOfRangeException));
            }
            int iLength = bb.getShort(); // Length
            if(iLength < 5 || iLength > 254){
                throw new ModbusLengthOutOfRangeException(context.getString(R.string.ModbusLengthOutOfRangeException));
            }
            return iLength;
        }

        throw new ModbusMBAPLengthException(context.getString(R.string.ModbusMBAPLengthException));
    }

    public static synchronized void getMessageDATA(Context context, long lProtTcpIpClientID, byte[] byteMBA, byte[] byteDATA) throws ModbusProtocolOutOfRangeException, ModbusLengthOutOfRangeException, ModbusMBAPLengthException {
        // Max total message length 260 byte
        int iTransactionIdentifier = 0; // Transaction Identifier
        if(byteMBA != null && byteMBA.length == 10){
            ByteBuffer bb = ByteBuffer.wrap(byteMBA);
            iTransactionIdentifier = bb.getShort(); // Transaction Identifier
            int iPI = bb.getShort(); // Protocol Identifier, must be 0
            if(iPI != 0){
                throw new ModbusProtocolOutOfRangeException(context.getString(R.string.ModbusProtocolOutOfRangeException));
            }
            int iLength = bb.getShort(); // Length
            if(iLength < 5 || iLength > 254){
                throw new ModbusLengthOutOfRangeException(context.getString(R.string.ModbusLengthOutOfRangeException));
            }
        } else {
            throw new ModbusMBAPLengthException(context.getString(R.string.ModbusMBAPLengthException));
        }

        if(byteDATA != null && byteDATA.length >= 10){
            ByteBuffer bb = ByteBuffer.wrap(byteDATA);
            if(byteDATA.length < 5 || byteDATA.length > 254){
                throw new ModbusLengthOutOfRangeException(context.getString(R.string.ModbusLengthOutOfRangeException));
            }
            // Unit Identifier
            int iUI = bb.get();
            // Function Code
            int iFEC = bb.get();
            switch(iFEC) {
                case 0x06:
                    int iRegisterAddress = bb.getShort();
                    int iRegisterValue = bb.getShort();
                    if(m_vMLListener != null) {
                        for (ModbusListener ml : m_vMLListener) {
                            ml.onWriteSingleRegisterCompletedCallback(lProtTcpIpClientID, iTransactionIdentifier, 0x06, iRegisterAddress, iRegisterValue);
                        }
                    }
                    break;
                case 0x86:
                    int iExceptionCodes = bb.getShort();
                    ModbusRequestException finire qui...
                    if(m_vMLListener != null) {
                        for (ModbusListener ml : m_vMLListener) {
                            ml.onWriteSingleRegisterExceptionCallback(lProtTcpIpClientID, iTransactionIdentifier, 0x86, iExceptionCodes);
                        }
                    }
                    break;
            }
        }
    }
}
