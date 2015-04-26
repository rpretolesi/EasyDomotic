package com.pretolesi.easydomotic.Modbus;

/**
 * Created by ricca_000 on 25/04/2015.
 */
public class ModbusPDULengthException  extends Exception {
    public ModbusPDULengthException(String message) {
        super(message);
    }

    public ModbusPDULengthException(Throwable throwable) {
        super(throwable);
    }

    public ModbusPDULengthException(String message, Throwable throwable) {
        super(message, throwable);
    }
}