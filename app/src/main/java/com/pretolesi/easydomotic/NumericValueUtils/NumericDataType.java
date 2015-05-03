package com.pretolesi.easydomotic.NumericValueUtils;

/**
 * Created by ricca_000 on 03/05/2015.
 */
public class NumericDataType {
    public static enum DataType {
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
            if(iDataTypeID == NumericDataType.DataType.SHORT16.m_iDataTypeID) {
                return NumericDataType.DataType.SHORT16;
            }
            if(iDataTypeID == NumericDataType.DataType.INT32.m_iDataTypeID) {
                return NumericDataType.DataType.INT32;
            }
            if(iDataTypeID == NumericDataType.DataType.LONG64.m_iDataTypeID) {
                return NumericDataType.DataType.LONG64;
            }
            if(iDataTypeID == NumericDataType.DataType.FLOAT32.m_iDataTypeID) {
                return NumericDataType.DataType.FLOAT32;
            }
            if(iDataTypeID == NumericDataType.DataType.DOUBLE64.m_iDataTypeID) {
                return NumericDataType.DataType.DOUBLE64;
            }
            return null;
        }

        @Override
        public String toString() {
            return Integer.toString(m_iDataTypeID) + "-" + m_strDataTypeName;
        }
    }
}
