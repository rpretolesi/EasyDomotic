package com.pretolesi.easydomotic.CustomException;

/**
 * Created by RPRETOLESI on 23/04/2015.
 */
public class ModbusRequestException  extends Exception {

    public ModbusRequestException(String message) {
        super(message);
    }

    public ModbusRequestException(Throwable throwable) {
        super(throwable);
    }

    public ModbusRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
