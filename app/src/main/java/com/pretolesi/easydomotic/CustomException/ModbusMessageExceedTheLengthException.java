package com.pretolesi.easydomotic.CustomException;

/**
 * Created by RPRETOLESI on 21/04/2015.
 */
public class ModbusMessageExceedTheLengthException extends Exception {

    public ModbusMessageExceedTheLengthException(String message) {
        super(message);
    }

    public ModbusMessageExceedTheLengthException(Throwable throwable) {
        super(throwable);
    }

    public ModbusMessageExceedTheLengthException(String message, Throwable throwable) {
        super(message, throwable);
    }
}