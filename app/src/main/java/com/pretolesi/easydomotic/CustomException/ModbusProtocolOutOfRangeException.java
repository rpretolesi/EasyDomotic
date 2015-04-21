package com.pretolesi.easydomotic.CustomException;

/**
 *
 */
public class ModbusProtocolOutOfRangeException extends Exception {

    public ModbusProtocolOutOfRangeException(String message) {
        super(message);
    }

    public ModbusProtocolOutOfRangeException(Throwable throwable) {
        super(throwable);
    }

    public ModbusProtocolOutOfRangeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}