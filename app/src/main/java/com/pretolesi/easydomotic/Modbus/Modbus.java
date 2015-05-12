package com.pretolesi.easydomotic.Modbus;

import android.content.Context;

import com.pretolesi.easydomotic.CustomControls.NumericEditText;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpMsg;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 *
 */
public class Modbus {

    public static synchronized TcpIpMsg writeShort(Context context, int iTID, int iUID, int iAddress, int iValue) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        // Value Ok Just 1 register
        int[] iaValue = new int[1];
        iaValue[0] = iValue;
        return writeMultipleRegisters(context, iTID, iUID, iAddress, iaValue, 1);
    }

    public static synchronized TcpIpMsg writeInteger(Context context, int iTID, int iUID, int iAddress, long lValue) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        // Value Ok Just 1 register
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(0, (int)lValue);
        int[] iaValue = new int[2];
        iaValue[0] = bb.getShort(0);
        iaValue[1] = bb.getShort(2);
        return writeMultipleRegisters(context, iTID, iUID, iAddress, iaValue, 2);
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
        return writeMultipleRegisters(context, iTID, iUID, iAddress, iaValue, 4);
    }

    public static synchronized TcpIpMsg writeFloat(Context context, int iTID, int iUID, int iAddress, float fValue) throws ModbusAddressOutOfRangeException, ModbusValueOutOfRangeException, ModbusTransIdOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusUnitIdOutOfRangeException {
        // Value Ok Just 1 register
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putFloat(0, fValue);
        int[] iaValue = new int[2];
        iaValue[0] = bb.getShort(0);
        iaValue[1] = bb.getShort(2);
        return writeMultipleRegisters(context, iTID, iUID, iAddress, iaValue, 2);
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

        return writeMultipleRegisters(context, iTID, iUID, iAddress, iaValue, 4);
    }

    private static synchronized TcpIpMsg writeMultipleRegisters(Context context, int iTID, int iUID, int iAddress, int[] iaValue, int iNrOfRegisters) throws ModbusTransIdOutOfRangeException, ModbusUnitIdOutOfRangeException, ModbusAddressOutOfRangeException, ModbusQuantityOfRegistersOutOfRange, ModbusValueOutOfRangeException {
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
            throw new ModbusUnitIdOutOfRangeException(context.getString(R.string.ModbusTransIdOutOfRangeException));
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
        bb.putShort((short)0);
        bb.putShort((short)(7 + byteByteCount));
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

    public static synchronized TcpIpMsg readHoldingRegisterss(Context context, int iTID, int iUID, int iStartingAddress, NumericEditText.DataType dtDataType) throws ModbusTransIdOutOfRangeException, ModbusUnitIdOutOfRangeException, ModbusAddressOutOfRangeException,  ModbusQuantityOfRegistersOutOfRange {
        short shTID;
        byte byteUID;
        short shAddress;
        short shNrOfRegisters;
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
        switch (dtDataType) {
            case SHORT16:
                shNrOfRegisters = 1;
                break;

            case INT32:
            case FLOAT32:
                shNrOfRegisters = 2;
                break;

            case LONG64:
            case DOUBLE64:
                shNrOfRegisters = 4;
                break;

            default:
                shNrOfRegisters = 0;
                break;
        }

        if(shNrOfRegisters < 1 || shNrOfRegisters > 125) {
            throw new ModbusQuantityOfRegistersOutOfRange(context.getString(R.string.ModbusValueArrayLengthOutOfRangeException));
        }

        ByteBuffer bb = ByteBuffer.allocate(12);
        bb.putShort(shTID);
        bb.putShort((short)0);
        bb.putShort((short)6);
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
            if(iLength < 5 || iLength > 254){
                throw new ModbusLengthOutOfRangeException(context.getString(R.string.ModbusLengthOutOfRangeException));
            }
            return new ModbusMBAP(iTID, iPID, iLength);
        }

        throw new ModbusMBAPLengthException(context.getString(R.string.ModbusMBAPLengthException));
    }

    public static synchronized ModbusPDU getPDU(Context context, long lProtTcpIpClientID, byte[] byteMBA, byte[] byteDATA) throws ModbusProtocolOutOfRangeException, ModbusLengthOutOfRangeException, ModbusMBAPLengthException, ModbusPDULengthException, ModbusByteCountOutOfRangeException {
        // Max total message length 260 byte
        ModbusPDU mpdu = null;

        int iTID = 0; // Transaction Identifier
        int iLength = 0;
        if(byteMBA != null && byteMBA.length == 6){
            ByteBuffer bb = ByteBuffer.wrap(byteMBA);
            iTID = bb.getShort(); // Transaction Identifier
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
            int iExceptionCode = 0;
            switch(iFEC) {
                case 0x10:
                    int iAddress = bb.getShort();
                    int iQuantityOfRegisters = bb.getShort();

                    mpdu = new ModbusPDU(iUI, iFEC, 0, 0, null);

                    break;

                case 0x90:
                    iExceptionCode = bb.get();

                    mpdu = new ModbusPDU(iUI, iFEC, iExceptionCode, 0, null);

                    break;

                case 0x03:
                    int iByteCount = bb.get() & 0xFF;
                    if(iByteCount < 1 || iLength > 125){
                        throw new ModbusByteCountOutOfRangeException(context.getString(R.string.ModbusByteCountOutOfRangeException));
                    }
                    byte[] byteBuffer = new byte[iByteCount];
                    for(int iIndice = 0; iIndice < iByteCount; iIndice++){
                        try{
                            byteBuffer[iIndice] = bb.get();
                        } catch(BufferUnderflowException ex) {
                            throw new ModbusByteCountOutOfRangeException(context.getString(R.string.ModbusByteCountOutOfRangeException));
                        }
                    }

                    mpdu = new ModbusPDU(iUI, iFEC, 0, iByteCount, byteBuffer);

                    break;

                case 0x83:
                    iExceptionCode = bb.get();

                    mpdu = new ModbusPDU(iUI, iFEC, iExceptionCode, 0, null);

                    break;
            }
        } else {
            throw new ModbusPDULengthException(context.getString(R.string.ModbusPDULengthException));
        }

        return mpdu;
    }
}
