package com.pretolesi.easydomotic.Modbus;

import android.content.Context;

import com.pretolesi.easydomotic.CustomException.ModbusAddressOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusLengthOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusMessageExceedTheLengthException;
import com.pretolesi.easydomotic.CustomException.ModbusProtocolOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusTransIdOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusUnitIdOutOfRangeException;
import com.pretolesi.easydomotic.CustomException.ModbusValueOutOfRangeException;
import com.pretolesi.easydomotic.R;

import java.nio.ByteBuffer;

/**
 *
 */
public class Modbus {

    public static byte[] writeSingleRegister(Context context, int iTransactionIdentifier, int iUnitIdentifier, int iAddress, int iValue) throws ModbusTransIdOutOfRangeException, ModbusUnitIdOutOfRangeException, ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException {
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

        ByteBuffer bb = ByteBuffer.allocate(5);
        bb.putShort(shTransactionIdentifier);
        bb.putShort((short)0);
        bb.putShort((short)6);
        bb.put(byteUnitIdentifier);
        bb.put((byte)0x06);
        bb.putShort(shAddress);
        bb.putShort(shValue);

        return bb.array();
    }

    public static synchronized int getMessageLengthFromMBAP(Context context, byte[] byteMsg) throws ModbusProtocolOutOfRangeException, ModbusLengthOutOfRangeException, ModbusMessageExceedTheLengthException {
        // Max message length 260 byte
        if(byteMsg != null && byteMsg.length == 10){
            ByteBuffer bb = ByteBuffer.wrap(byteMsg);
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

        throw new ModbusMessageExceedTheLengthException(context.getString(R.string.ModbusMessageExceedTheLengthException));
    }

    public static synchronized boolean getMessageDATA(Context context, byte[] byteMsg) throws ModbusProtocolOutOfRangeException, ModbusLengthOutOfRangeException, ModbusMessageExceedTheLengthException {
        // Max message length 260 byte
        if(byteMsg != null && byteMsg.length >= 10){
            ByteBuffer bb = ByteBuffer.wrap(byteMsg);
            if(byteMsg.length < 5 || byteMsg.length > 254){
                throw new ModbusLengthOutOfRangeException(context.getString(R.string.ModbusLengthOutOfRangeException));
            }
            // Unit Identifier

            finire qui...











            if(byteMsg.length < byteMsg.length + 6){
                return false;
            } else if(byteMsg.length == iLength + 6) {
                return true;
            } else {
                throw new ModbusMessageExceedTheLengthException(context.getString(R.string.ModbusMessageExceedTheLengthException));
            }
        }
        return false;
    }
}
