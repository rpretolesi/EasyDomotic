package com.pretolesi.easydomotic.CustomException;

/**
 * Created by RPRETOLESI on 21/04/2015.
 */
public class ModbusLengthOutOfRangeException extends Exception {

    public ModbusLengthOutOfRangeException(String message) {
        super(message);
    }

    public ModbusLengthOutOfRangeException(Throwable throwable) {
        super(throwable);
    }

    public ModbusLengthOutOfRangeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}