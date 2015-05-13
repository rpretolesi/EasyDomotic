package com.pretolesi.easydomotic.CustomControls;

/**
 * Created by RPRETOLESI on 13/05/2015.
 */
public class NumericDataType {

    public static getShort

    public enum DataType {
        SHORT(0, "Integer 16 bit"),
        INT32(1, "Integer 32 bit"),
        LONG64(2, "Integer 64 bit"),
        FLOAT32(3, "Float with single precision IEEE 754 32 bit"),
        DOUBLE64(4, "Float with double precision IEEE 754 64 bit");

        private int m_iDataTypeID;
        private String m_strDataTypeName;

        DataType(int iDataTypeID, String strDataTypeName) {

            m_iDataTypeID = iDataTypeID;
            m_strDataTypeName = strDataTypeName;
        }

        public static DataType getDataType(int iDataTypeID) {
            if(iDataTypeID == DataType.SHORT.m_iDataTypeID) {
                return DataType.SHORT;
            }
            if(iDataTypeID == DataType.INT32.m_iDataTypeID) {
                return DataType.INT32;
            }
            if(iDataTypeID == DataType.LONG64.m_iDataTypeID) {
                return DataType.LONG64;
            }
            if(iDataTypeID == DataType.FLOAT32.m_iDataTypeID) {
                return DataType.FLOAT32;
            }
            if(iDataTypeID == DataType.DOUBLE64.m_iDataTypeID) {
                return DataType.DOUBLE64;
            }
            return null;
        }

        @Override
        public String toString() {
            return Integer.toString(m_iDataTypeID) + "-" + m_strDataTypeName;
        }
    }
}
