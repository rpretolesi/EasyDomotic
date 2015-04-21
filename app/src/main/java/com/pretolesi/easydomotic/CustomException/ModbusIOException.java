package com.pretolesi.easydomotic.CustomException;

/**
 *
 */
public class ModbusIOException extends Exception {

    public ModbusIOException(String message) {
        super(message);
    }

    public ModbusIOException(Throwable throwable) {
        super(throwable);
    }

    public ModbusIOException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
