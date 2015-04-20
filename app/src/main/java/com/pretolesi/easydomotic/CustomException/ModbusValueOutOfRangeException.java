package com.pretolesi.easydomotic.CustomException;

/**
 *
 */
public class ModbusValueOutOfRangeException extends Exception {

    public ModbusValueOutOfRangeException(String message) {
        super(message);
    }

    public ModbusValueOutOfRangeException(Throwable throwable) {
        super(throwable);
    }

    public ModbusValueOutOfRangeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
