package com.pretolesi.easydomotic.Modbus;

import android.content.Context;

import com.pretolesi.easydomotic.CommClientData.BaseValueCommClientData.Protocol;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpMsg;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 *
 */
public class Modbus {

    public static synchronized TcpIpMsg writeShort(Context context, int iTID, int iUID, int iAddress, int iValue, Protocol p) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        // Value Ok Just 1 register
        TcpIpMsg tim = null;
        int[] iaValue = new int[1];
        iaValue[0] = iValue;
        if(p == Protocol.MODBUS_ON_TCP_IP){
            tim = writeMultipleRegistersOnTcp(context, iTID, iUID, iAddress, iaValue, 1);
        }
        if(p == Protocol.MODBUS_ON_SERIAL){
            tim = writeMultipleRegistersOnSerial(context, iTID, iUID, iAddress, iaValue, 1);
        }

        return tim;
    }

    public static synchronized TcpIpMsg writeInteger(Context context, int iTID, int iUID, int iAddress, long lValue) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        // Value Ok Just 1 register
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(0, (int)lValue);
        int[] iaValue = new int[2];
        iaValue[0] = bb.getShort(0);
        iaValue[1] = bb.getShort(2);
        return writeMultipleRegistersOnTcp(context, iTID, iUID, iAddress, iaValue, 2);
    }

    public static synchronized TcpIpMsg writeLong(Context context, int iTID, int iUID, int iAddress, long lValue) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        // Value Ok Just 1 register
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(0, lValue);
        int[] iaValue = new int[4];
        iaValue[0] = bb.getShort(0);
        iaValue[1] = bb.getShort(2);
        iaValue[2] = bb.getShort(4);
        iaValue[3] = bb.getShort(6);
        return writeMultipleRegistersOnTcp(context, iTID, iUID, iAddress, iaValue, 4);
    }

    public static synchronized TcpIpMsg writeFloat(Context context, int iTID, int iUID, int iAddress, float fValue) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        // Value Ok Just 1 register
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putFloat(0, fValue);
        int[] iaValue = new int[2];
        iaValue[0] = bb.getShort(0);
        iaValue[1] = bb.getShort(2);
        return writeMultipleRegistersOnTcp(context, iTID, iUID, iAddress, iaValue, 2);
    }

    public static synchronized TcpIpMsg writeDouble(Context context, int iTID, int iUID, int iAddress, double dblValue) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        // Value Ok Just 1 register
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putDouble(0, dblValue);
        int[] iaValue = new int[4];
        iaValue[0] = bb.getShort(0);
        iaValue[1] = bb.getShort(2);
        iaValue[2] = bb.getShort(4);
        iaValue[3] = bb.getShort(6);

        return writeMultipleRegistersOnTcp(context, iTID, iUID, iAddress, iaValue, 4);
    }

    private static synchronized TcpIpMsg writeMultipleRegistersOnTcp(Context context, int iTID, int iUID, int iAddress, int[] iaValue, int iNrOfRegisters) throws ModbusTransIdOutOfRangeException, ModbusUnitIdOutOfRangeException, ModbusAddressOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusValueOutOfRangeException {
        short shTID;
        byte byteUID;
        short shAddress;
        short shNrOfRegisters;
        byte byteByteCount;

        if(iTID >= 0 && iTID <= 65535){
            shTID = (short) iTID;
        } else {
            throw new ModbusTransIdOutOfRangeException(context.getString(R.string.ModbusTransIdOutOfRangeException));
        }

        if(iUID >= 0 && iUID <= 255){
            byteUID = (byte) iUID;
        } else {
            throw new ModbusUnitIdOutOfRangeException(context.getString(R.string.ModbusUnitIdOutOfRangeException));
        }
        if(iAddress >= 0 && iAddress <= 65535){
            shAddress = (short) iAddress;
        } else {
            throw new ModbusAddressOutOfRangeException(context.getString(R.string.ModbusAddressOutOfRangeException));
        }
        if(iNrOfRegisters > 0 && iNrOfRegisters < 124) {
            shNrOfRegisters = (short) iNrOfRegisters;
            byteByteCount = (byte)(shNrOfRegisters * 2);
        } else {
            throw new ModbusQuantityOfRegistersOutOfRange(context.getString(R.string.ModbusValueArrayLengthOutOfRangeException));
        }

        ByteBuffer bb = ByteBuffer.allocate(13 + byteByteCount);
        bb.putShort(shTID);
        bb.putShort((short) 0);
        bb.putShort((short) (7 + byteByteCount));
        bb.put(byteUID);
        bb.put((byte)0x10);
        bb.putShort(shAddress);
        bb.putShort(shNrOfRegisters);
        bb.put(byteByteCount);

        if(iaValue != null && iaValue.length > 0){
            for (int iValue : iaValue) {
                if (iValue >= -32768  && iValue <= 32767 ) {
                    bb.putShort((short) iValue);
                } else {
                    throw new ModbusValueOutOfRangeException(context.getString(R.string.ModbusValueOutOfRangeException));
                }
            }
        } else {
            throw new ModbusValueOutOfRangeException(context.getString(R.string.ModbusValueOutOfRangeException));
        }

        return new TcpIpMsg(iTID, byteUID, bb.array());
    }

    private static synchronized TcpIpMsg writeMultipleRegistersOnSerial(Context context, int iTID, int iUID, int iAddress, int[] iaValue, int iNrOfRegisters) throws ModbusTransIdOutOfRangeException, ModbusUnitIdOutOfRangeException, ModbusAddressOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusValueOutOfRangeException {
        short shTID;
        byte byteUID;
        short shAddress;
        short shNrOfRegisters;
        byte byteByteCount;

        if(iTID >= 0 && iTID <= 65535){
            shTID = (short) iTID;
        } else {
            throw new ModbusTransIdOutOfRangeException(context.getString(R.string.ModbusTransIdOutOfRangeException));
        }

        if(iUID >= 0 && iUID <= 255){
            byteUID = (byte) iUID;
        } else {
            throw new ModbusUnitIdOutOfRangeException(context.getString(R.string.ModbusUnitIdOutOfRangeException));
        }
        if(iAddress >= 0 && iAddress <= 65535){
            shAddress = (short) iAddress;
        } else {
            throw new ModbusAddressOutOfRangeException(context.getString(R.string.ModbusAddressOutOfRangeException));
        }
        if(iNrOfRegisters > 0 && iNrOfRegisters < 124) {
            shNrOfRegisters = (short) iNrOfRegisters;
            byteByteCount = (byte)(shNrOfRegisters * 2);
        } else {
            throw new ModbusQuantityOfRegistersOutOfRange(context.getString(R.string.ModbusValueArrayLengthOutOfRangeException));
        }

        ByteBuffer bb = ByteBuffer.allocate(7 + byteByteCount + 2);
        bb.put(byteUID);
        bb.put((byte) 0x10);
        bb.putShort(shAddress);
        bb.putShort(shNrOfRegisters);
        bb.put(byteByteCount);

        if(iaValue != null && iaValue.length > 0){
            for (int iValue : iaValue) {
                if (iValue >= -32768  && iValue <= 32767 ) {
                    bb.putShort((short) iValue);
                } else {
                    throw new ModbusValueOutOfRangeException(context.getString(R.string.ModbusValueOutOfRangeException));
                }
            }
        } else {
            throw new ModbusValueOutOfRangeException(context.getString(R.string.ModbusValueOutOfRangeException));
        }

        bb.putShort(getCRC(bb.array(), bb.array().length - 2));

        return new TcpIpMsg(iTID, byteUID, bb.array());
    }

    public static synchronized TcpIpMsg readShort(Context context, int iTID, int iUID, int iAddress) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        return readHoldingRegisters(context, iTID, iUID, iAddress, (short) 1);
    }

    public static synchronized TcpIpMsg readInt(Context context, int iTID, int iUID, int iAddress) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        return readHoldingRegisters(context, iTID, iUID, iAddress, (short) 2);
    }

    public static synchronized TcpIpMsg readLong(Context context, int iTID, int iUID, int iAddress) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        return readHoldingRegisters(context, iTID, iUID, iAddress, (short) 4);
    }

    public static synchronized TcpIpMsg readFloat(Context context, int iTID, int iUID, int iAddress) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        return readHoldingRegisters(context, iTID, iUID, iAddress, (short) 2);
    }

    public static synchronized TcpIpMsg readDouble(Context context, int iTID, int iUID, int iAddress) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        return readHoldingRegisters(context, iTID, iUID, iAddress, (short) 4);
    }

    private static synchronized TcpIpMsg readHoldingRegisters(Context context, int iTID, int iUID, int iStartingAddress, short shNrOfRegisters) throws ModbusTransIdOutOfRangeException, ModbusUnitIdOutOfRangeException, ModbusAddressOutOfRangeException,  ModbusQuantityOfRegistersOutOfRange {
        short shTID;
        byte byteUID;
        short shAddress;

        if(iTID >= 0 && iTID <= 65535){
            shTID = (short) iTID;
        } else {
            throw new ModbusTransIdOutOfRangeException(context.getString(R.string.ModbusTransIdOutOfRangeException));
        }
        if(iUID >= 0 && iUID <= 255){
            byteUID = (byte) iUID;
        } else {
            throw new ModbusUnitIdOutOfRangeException(context.getString(R.string.ModbusTransIdOutOfRangeException));
        }
        if(iStartingAddress >= 0 && iStartingAddress <= 65535){
            shAddress = (short) iStartingAddress;
        } else {
            throw new ModbusAddressOutOfRangeException(context.getString(R.string.ModbusAddressOutOfRangeException));
        }

        if(shNrOfRegisters < 1 || shNrOfRegisters > 125) {
            throw new ModbusQuantityOfRegistersOutOfRange(context.getString(R.string.ModbusValueArrayLengthOutOfRangeException));
        }

        ByteBuffer bb = ByteBuffer.allocate(12);
        bb.putShort(shTID);
        bb.putShort((short) 0);
        bb.putShort((short) 6);
        bb.put(byteUID);
        bb.put((byte)0x03);
        bb.putShort(shAddress);
        bb.putShort(shNrOfRegisters);

        return new TcpIpMsg(iTID, byteUID, bb.array());
    }

    public static synchronized ModbusMBAP getMBAP(Context context, byte[] byteMBA) throws ModbusProtocolOutOfRangeException, ModbusLengthOutOfRangeException, ModbusMBAPLengthException {
        // Max message length 260 byte
        if(byteMBA != null && byteMBA.length == 6){
            ByteBuffer bb = ByteBuffer.wrap(byteMBA);
            int iTID = bb.getShort(); // Transaction Identifier
            int iPID = bb.getShort(); // Protocol Identifier, must be 0
            if(iPID != 0){
                throw new ModbusProtocolOutOfRangeException(context.getString(R.string.ModbusProtocolOutOfRangeException));
            }
            int iLength = bb.getShort(); // Length
            if(iLength < 3 || iLength > 254){
                throw new ModbusLengthOutOfRangeException(context.getString(R.string.ModbusLengthOutOfRangeException));
            }
            return new ModbusMBAP(iTID, iPID, iLength);
        }

        throw new ModbusMBAPLengthException(context.getString(R.string.ModbusMBAPLengthException));
    }

    public static synchronized ModbusPDU getPDU(Context context, byte[] bytePDUValue, short shPDULenght) throws ModbusProtocolOutOfRangeException, ModbusLengthOutOfRangeException, ModbusMBAPLengthException, ModbusPDULengthException, ModbusByteCountOutOfRangeException {
        // Max total message length 260 byte
        ModbusPDU mpdu = null;

        aggiungere controllo su indirizzo di ricezione

        if(bytePDUValue != null && bytePDUValue.length == shPDULenght){
            ByteBuffer bb = ByteBuffer.wrap(bytePDUValue);
            if(bytePDUValue.length < 3 || bytePDUValue.length > 254){
                throw new ModbusLengthOutOfRangeException(context.getString(R.string.ModbusLengthOutOfRangeException));
            }
             // Unit Identifier
            short shUI = bb.get();
            // Function Code
            short shFEC = (short)(bb.get() & 0xFF);
            short shExceptionCode = 0;
            switch(shFEC) {
                case 0x10:
                    short shAddress = bb.getShort();
                    short shQuantityOfRegisters = bb.getShort();

                    mpdu = new ModbusPDU(shUI, shFEC, 0, null, (short)0);

                    break;

                case 0x90:
                    shExceptionCode = bb.get();

                    mpdu = new ModbusPDU(shUI, shFEC, shExceptionCode, 0, null);

                    break;

                case 0x03:
                    short shByteCount = bb.get() & 0xFF;
                    if(shByteCount < 1 || shPDULenght > 125){
                        throw new ModbusByteCountOutOfRangeException(context.getString(R.string.ModbusByteCountOutOfRangeException));
                    }
                    byte[] byteBuffer = new byte[shByteCount];
                    for(short shIndice = 0; shIndice < shByteCount; shIndice++){
                        try{
                            byteBuffer[shIndice] = bb.get();
                        } catch(BufferUnderflowException ex) {
                            throw new ModbusByteCountOutOfRangeException(context.getString(R.string.ModbusByteCountOutOfRangeException));
                        }
                    }

                    mpdu = new ModbusPDU(shUI, shFEC, 0, shByteCount, byteBuffer);

                    break;

                case 0x83:
                    shExceptionCode = bb.get();

                    mpdu = new ModbusPDU(shUI, shFEC, shExceptionCode, 0, null);

                    break;
            }
        } else {
            throw new ModbusPDULengthException(context.getString(R.string.ModbusPDULengthException));
        }

        return mpdu;
    }

    private static short getCRC(byte[] buf, int len)
    {
        int crc = 0xFFFF;

        for (int pos = 0; pos < len; pos++) {
            crc ^= (int)buf[pos];          // XOR byte into least sig. byte of crc

            for (int i = 8; i != 0; i--) {    // Loop over each bit
                if ((crc & 0x0001) != 0) {      // If the LSB is set
                    crc >>= 1;                    // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                }
                else                            // Else LSB is not set
                    crc >>= 1;                    // Just shift right
            }
        }
        // Note, this number has low and high bytes swapped, so use it accordingly (or swap bytes)
        return (short)crc;
    }

}
