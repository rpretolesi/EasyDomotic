package com.pretolesi.easydomotic.ValueUtils;

/**
 *
 */
public class ValueDataType {
    public enum DataType {
        SHORT16(0, "Integer 16 bit"),
        INT32(1, "Integer 32 bit"),
        LONG64(2, "Integer 64 bit"),
        FLOAT32(3, "Float with single precision IEEE 754 32 bit"),
        DOUBLE64(4, "Float with double precision IEEE 754 64 bit");

        private int m_iDataTypeID;
        private String m_strDataTypeName;

        DataType(int iDataTypeID, String strDataTypelName) {

            m_iDataTypeID = iDataTypeID;
            m_strDataTypeName = strDataTypelName;
        }
/*
        public int getID() {
            return m_iDataTypeID;
        }
*/
        public static DataType getDataType(int iDataTypeID) {
            if(iDataTypeID == ValueDataType.DataType.SHORT16.m_iDataTypeID) {
                return ValueDataType.DataType.SHORT16;
            }
            if(iDataTypeID == ValueDataType.DataType.INT32.m_iDataTypeID) {
                return ValueDataType.DataType.INT32;
            }
            if(iDataTypeID == ValueDataType.DataType.LONG64.m_iDataTypeID) {
                return ValueDataType.DataType.LONG64;
            }
            if(iDataTypeID == ValueDataType.DataType.FLOAT32.m_iDataTypeID) {
                return ValueDataType.DataType.FLOAT32;
            }
            if(iDataTypeID == ValueDataType.DataType.DOUBLE64.m_iDataTypeID) {
                return ValueDataType.DataType.DOUBLE64;
            }
            return null;
        }

        @Override
        public String toString() {
            return Integer.toString(m_iDataTypeID) + "-" + m_strDataTypeName;
        }
    }
}
