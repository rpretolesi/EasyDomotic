package com.pretolesi.SQL;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import com.pretolesi.easydomotic.BaseValue.BaseValueData;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.RoomFragmentData;
import com.pretolesi.easydomotic.CommClientData.BaseValueCommClientData;

/**
 *
 */
public class SQLContract
{
    private static ReentrantLock m_LockCommandHolder = new ReentrantLock();

    public static final String DATABASE_NAME = "easydomotic.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INT";
//    public static final String REAL_TYPE = " REAL";
    public static final String IMAGE_TYPE = " BLOB";
    public static final String COMMA_SEP = ",";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private SQLContract()
    {
    }
    public enum SettingID
    {
        DEFAULT(R.string.text_tv_server_address, 1, "192.168.1.1", 7 ,15);
//        TCP_IP_CLIENT_ADDRESS(R.string.text_stica_tv_server_ip_address, 1, "192.168.1.1", 7 ,15),
//        TCP_IP_CLIENT_PORT(R.string.text_stica_tv_server_port, 2, "502", 1 ,65535),
//        TCP_IP_CLIENT_TIMEOUT(R.string.text_stica_tv_timeout, 3, "30000", 1 ,60000),
//        TCP_IP_CLIENT_COMM_SEND_DATA_DELAY(R.string.text_stica_tv_comm_send_data_delay, 4, "100", 1 ,60000),
//        TCP_IP_CLIENT_PROTOCOL(R.string.text_stica_tv_protocol, 5, "-1", 0 ,3);
//        SET_SENSOR_FEEDBACK_AMPL_K(10, "500.0", 0 ,0),
//        SET_SENSOR_LOW_PASS_FILTER_K(11, "0.5", 0 ,0),
//        SET_SENSOR_MAX_OUTPUT_VALUE(12, "250", 0 ,0),
//        SET_SENSOR_MIN_VALUE_START_OUTPUT(13, "10", 0 ,0),
//        SET_SENSOR_ORIENTATION_LANDSCAPE(14, "10", 0 ,0),
//        LAST_ROOM_TAG(R.string.text_stica_tv_comm_send_data_delay, 100, "", 0 ,0),
//        DEFAULT_ROOM_TAG(R.string.text_stica_tv_comm_send_data_delay, 100, "", 0 ,0);

        private int m_resDescript;
        private int m_value;
        private String m_defaultValue;
        private float m_fmin;
        private float m_fmax;

        private SettingID(int resDescript, int value, String defaultValue, float fmin, float fmax) {
            this.m_resDescript = resDescript;
            this.m_value = value;
            this.m_defaultValue = defaultValue;
            this.m_fmin = fmin;
            this.m_fmax = fmax;
        }

        public int getResDescript() {
            return m_resDescript;
        }

        public int getValue() {
            return m_value;
        }

        public String getDefaultValue() { return m_defaultValue; }

        public float getMinValue() {
            return m_fmin;
        }

        public float getMaxValue() {
            return m_fmax;
        }


    }
    /*
     * Parametri dell'app
    */
    public static abstract class Settings implements BaseColumns
    {
        public static final String TABLE_NAME = "Settings";
        public static final String COLUMN_NAME_PARAMETER_ID = "Parameter_ID";
        public static final String COLUMN_NAME_PARAMETER_VALUE = "Parameter_Value";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME +
                        " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_PARAMETER_ID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PARAMETER_VALUE + TEXT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static boolean set(SettingID pType, String strpValue)
        {
            try
            {
                m_LockCommandHolder.lock();

                ContentValues values = null;

                SQLiteDatabase db = SQLHelper.getInstance().getDB();

                if (db != null && pType != null && strpValue != null)
                {

                    values = new ContentValues();
                    values.put(COLUMN_NAME_PARAMETER_ID, pType.getValue());
                    values.put(COLUMN_NAME_PARAMETER_VALUE, strpValue);

                    String selection = COLUMN_NAME_PARAMETER_ID + " = ?";

                    String[] selectionArgs = {String.valueOf(pType.getValue())};

                    // Update the Parameter
                    if (db.update(TABLE_NAME, values, selection, selectionArgs) == 0)
                    {
                        // The Parameter doesn't exist, i will add it
                        if (db.insert(TABLE_NAME, null, values) > 0)
                        {
                            return true;
                        }
                    } else
                    {
                        return true;
                    }
                }
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }

            return false;
        }

        public static String get(SettingID pType)
        {
            m_LockCommandHolder.lock();

            Cursor cursor = null;
            String strRes = "";
            try
            {
                SQLiteDatabase db = SQLHelper.getInstance().getDB();

                if(db != null && pType != null)
                {

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                    COLUMN_NAME_PARAMETER_VALUE
                            };

                    String selection = COLUMN_NAME_PARAMETER_ID + " = ?";

                    String[] selectionArgs = { String.valueOf(pType.getValue())  };

                    String strDefaultValue = pType.getDefaultValue();

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    cursor = db.query(
                            TABLE_NAME,  // The table to query
                            projection,                               // The columns to return
                            selection,                                // The columns for the WHERE clause
                            selectionArgs,                            // The values for the WHERE clause
                            null,                                     // don't group the rows
                            null,                                     // don't filter by row groups
                            sortOrder                                 // The sort order
                    );

                    if((cursor != null) && (cursor.getCount() > 0))
                    {
                        cursor.moveToFirst();
                        strRes = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PARAMETER_VALUE));
                    }
                    else
                    {
                        strRes = strDefaultValue;
                    }

                }
            }
            catch (Exception ex)
            {

            }
            finally
            {
                if(cursor != null)
                {
                    cursor.close();
                }

                m_LockCommandHolder.unlock();
            }

            return strRes;
        }

        public static Cursor load(SettingID pType)
        {
            try
            {
                m_LockCommandHolder.lock();

                Cursor cursor = null;

                SQLiteDatabase db = SQLHelper.getInstance().getDB();

                if(db != null && pType != null)
                {

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                    COLUMN_NAME_PARAMETER_ID,
                                    COLUMN_NAME_PARAMETER_VALUE
                            };

                    String selection = COLUMN_NAME_PARAMETER_ID + " = ?";

                    String[] selectionArgs = { String.valueOf(pType.getValue())  };

                    String strDefaultValue = pType.getDefaultValue();

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    cursor = db.query(
                            TABLE_NAME,  // The table to query
                            projection,                               // The columns to return
                            selection,                                // The columns for the WHERE clause
                            selectionArgs,                            // The values for the WHERE clause
                            null,                                     // don't group the rows
                            null,                                     // don't filter by row groups
                            sortOrder                                 // The sort order
                    );
                }

                return cursor;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static String get(Cursor cursor, SettingID pType){

            try
            {
                m_LockCommandHolder.lock();

                String str = pType.getDefaultValue();

                if((cursor != null) && (cursor.getCount() > 0))
                {
                    for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
                    {
                        str = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PARAMETER_VALUE));
                    }
                }
                return str;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }
    }

    /* Inner class that defines the table contents */
    public static abstract class BaseValueEntry implements BaseColumns {
        public static final String TABLE_NAME = "BaseValue";
        public static final String COLUMN_NAME_TYPE = "Type";
        public static final String COLUMN_NAME_ROOM_ID = "Room_ID";
        public static final String COLUMN_NAME_TAG = "TAG";
        public static final String COLUMN_NAME_X = "X";
        public static final String COLUMN_NAME_Y = "Y";
        public static final String COLUMN_NAME_Z = "Z";
        public static final String COLUMN_NAME_LANDSCAPE = "Landscape";

        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE = "ProtTcpIpClientEnable";
        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_ID = "ProtTcpIpClientID";
        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID = "ProtTcpIpClientValueID";
        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS = "ProtTcpIpClientValueAddress";
        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_DATA_TYPE = "ProtTcpIpClientValueDataType";

        public static final String COLUMN_NAME_VALUE_MIN_NR_CHAR_TO_SHOW = "ValueMinNrCharToShow";
        public static final String COLUMN_NAME_VALUE_NR_OF_DECIMAL = "ValueNrOfDecimal";
        public static final String COLUMN_NAME_VALUE_UM = "ValueUM";
        public static final String COLUMN_NAME_VALUE_UPDATE_MILLIS = "ValueUpdateMillis";
        public static final String COLUMN_NAME_VALUE_READ_ONLY = "ValueReadOnly";

        public static final String COLUMN_NAME_WRITE_VALUE_OFF = "WriteValueOFF";
        public static final String COLUMN_NAME_WRITE_VALUE_OFF_ON = "WriteValueOFFON";
        public static final String COLUMN_NAME_WRITE_VALUE_ON_OFF = "WriteValueONOFF";
        public static final String COLUMN_NAME_WRITE_VALUE_ON = "WriteValueON";

        public static final String COLUMN_NAME_SENSOR_TYPE_ID = "SensorTypeID";
        public static final String COLUMN_NAME_SENSOR_VALUE_ID = "SensorValueID";
        public static final String COLUMN_NAME_SENSOR_ENABLE_SIMULATION = "SensorEnableSimulation";
        public static final String COLUMN_NAME_SENSOR_AMPL_K = "SensorAmplK";
        public static final String COLUMN_NAME_SENSOR_LOW_PASS_FILTER_K = "SensorLowPassFilterK";
        public static final String COLUMN_NAME_SENSOR_SAMPLE_TIME = "SensorSampleTime";
        public static final String COLUMN_NAME_SENSOR_WRITE_UPDATE_TIME = "SensorWriteUpdateTime";

        // Used only in MatrixCursor
        public static final String COLUMN_NAME_ORIGIN = "Origin";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME +
                        " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_TYPE + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_ROOM_ID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_TAG + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_X + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_Y + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_Z + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_LANDSCAPE + INT_TYPE + COMMA_SEP +

                        COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROT_TCP_IP_CLIENT_ID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_DATA_TYPE + INT_TYPE + COMMA_SEP +

                        COLUMN_NAME_VALUE_MIN_NR_CHAR_TO_SHOW + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VALUE_NR_OF_DECIMAL + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VALUE_UM + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VALUE_UPDATE_MILLIS + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_VALUE_READ_ONLY + INT_TYPE + COMMA_SEP +

                        COLUMN_NAME_WRITE_VALUE_OFF + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_WRITE_VALUE_OFF_ON + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_WRITE_VALUE_ON_OFF + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_WRITE_VALUE_ON + INT_TYPE + COMMA_SEP +

                        COLUMN_NAME_SENSOR_TYPE_ID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_SENSOR_VALUE_ID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_SENSOR_ENABLE_SIMULATION + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_SENSOR_AMPL_K + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_SENSOR_LOW_PASS_FILTER_K + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_SENSOR_SAMPLE_TIME + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_SENSOR_WRITE_UPDATE_TIME + INT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static boolean save(BaseValueData bve)  {

            boolean bRes = true;
            try
            {
                m_LockCommandHolder.lock();
                SQLiteDatabase db = SQLHelper.getInstance().getDB();
                if(db != null && bve != null) {

                    ContentValues values = new ContentValues();

                    values.put(COLUMN_NAME_TYPE, bve.getType());
                    values.put(COLUMN_NAME_ROOM_ID, bve.getRoomID());
                    values.put(COLUMN_NAME_TAG, String.valueOf(bve.getTag()));
                    values.put(COLUMN_NAME_X, Float.toString(bve.getPosX()));
                    values.put(COLUMN_NAME_Y, Float.toString(bve.getPosY()));
                    values.put(COLUMN_NAME_Z, Float.toString(bve.getPosZ()));
                    values.put(COLUMN_NAME_LANDSCAPE, Integer.valueOf(bve.getVertical() ? 1 : 0));

                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE, Integer.valueOf(bve.getProtTcpIpClientEnable() ? 1 : 0));
                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_ID, bve.getProtTcpIpClientID());
                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID, bve.getProtTcpIpClientValueID());
                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS, bve.getProtTcpIpClientValueAddress());
                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_DATA_TYPE, bve.getProtTcpIpClientValueDataType());

                    values.put(COLUMN_NAME_VALUE_MIN_NR_CHAR_TO_SHOW, bve.getValueMinNrCharToShow());
                    values.put(COLUMN_NAME_VALUE_NR_OF_DECIMAL, bve.getValueNrOfDecimal());
                    values.put(COLUMN_NAME_VALUE_UM, bve.getValueUM());
                    values.put(COLUMN_NAME_VALUE_UPDATE_MILLIS, bve.getValueUpdateMillis());
                    values.put(COLUMN_NAME_VALUE_READ_ONLY, Integer.valueOf(bve.getValueReadOnly() ? 1 : 0));

                    values.put(COLUMN_NAME_WRITE_VALUE_OFF, bve.getWriteValueOFF());
                    values.put(COLUMN_NAME_WRITE_VALUE_OFF_ON, bve.getWriteValueOFFON());
                    values.put(COLUMN_NAME_WRITE_VALUE_ON_OFF, bve.getWriteValueONOFF());
                    values.put(COLUMN_NAME_WRITE_VALUE_ON, bve.getWriteValueON());

                    values.put(COLUMN_NAME_SENSOR_TYPE_ID, bve.getSensorTypeID());
                    values.put(COLUMN_NAME_SENSOR_VALUE_ID, bve.getSensorValueID());
                    values.put(COLUMN_NAME_SENSOR_ENABLE_SIMULATION, Integer.valueOf(bve.getSensorEnableSimulation() ? 1 : 0));
                    values.put(COLUMN_NAME_SENSOR_AMPL_K, bve.getSensorAmplK());
                    values.put(COLUMN_NAME_SENSOR_LOW_PASS_FILTER_K, bve.getSensorLowPassFilterK());
                    values.put(COLUMN_NAME_SENSOR_SAMPLE_TIME, bve.getSensorSampleTimeMillis());
                    values.put(COLUMN_NAME_SENSOR_WRITE_UPDATE_TIME, bve.getSensorWriteUpdateTimeMillis());

                    String whereClause = _ID + " = ? ";

                    String[] whereArgs = {String.valueOf(bve.getID())};
                    long id = SQLContract.save(db, TABLE_NAME, values, whereClause, whereArgs, bve.getID());
                    // Update or Save
                    if (id > 0) {
                        bve.setID(id);
                        bve.setSaved(true);
                    } else {
                        bRes = false;
                    }
                }
            } finally {
                m_LockCommandHolder.unlock();
            }

            return bRes;

        }

        public static Cursor loadFromBaseValueData(BaseValueData bve)
        {
            try
            {
                m_LockCommandHolder.lock();

                MatrixCursor cursor = null;

                if(bve != null){

                    String[] columns = new String[] {
                            _ID,
                            COLUMN_NAME_TYPE,
                            COLUMN_NAME_ROOM_ID,
                            COLUMN_NAME_TAG,
                            COLUMN_NAME_X,
                            COLUMN_NAME_Y,
                            COLUMN_NAME_Z,
                            COLUMN_NAME_LANDSCAPE,

                            COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE,
                            COLUMN_NAME_PROT_TCP_IP_CLIENT_ID,
                            COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID,
                            COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS,
                            COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_DATA_TYPE,

                            COLUMN_NAME_VALUE_MIN_NR_CHAR_TO_SHOW,
                            COLUMN_NAME_VALUE_NR_OF_DECIMAL,
                            COLUMN_NAME_VALUE_UM,
                            COLUMN_NAME_VALUE_UPDATE_MILLIS,
                            COLUMN_NAME_VALUE_READ_ONLY,

                            COLUMN_NAME_WRITE_VALUE_OFF,
                            COLUMN_NAME_WRITE_VALUE_OFF_ON,
                            COLUMN_NAME_WRITE_VALUE_ON_OFF,
                            COLUMN_NAME_WRITE_VALUE_ON,

                            COLUMN_NAME_SENSOR_TYPE_ID,
                            COLUMN_NAME_SENSOR_VALUE_ID,
                            COLUMN_NAME_SENSOR_ENABLE_SIMULATION,
                            COLUMN_NAME_SENSOR_AMPL_K,
                            COLUMN_NAME_SENSOR_LOW_PASS_FILTER_K,
                            COLUMN_NAME_SENSOR_SAMPLE_TIME,
                            COLUMN_NAME_SENSOR_WRITE_UPDATE_TIME,

                            COLUMN_NAME_ORIGIN
                    };

                    cursor = new MatrixCursor(columns);
                    cursor.addRow(new Object[]{
                            bve.getID(),
                            bve.getType(),
                            bve.getRoomID(),
                            bve.getTag(),
                            bve.getPosX(),
                            bve.getPosY(),
                            bve.getPosZ(),
                            Integer.valueOf(bve.getVertical() ? 1 : 0),

                            Integer.valueOf(bve.getProtTcpIpClientEnable() ? 1 : 0),
                            bve.getProtTcpIpClientID(),
                            bve.getProtTcpIpClientValueID(),
                            bve.getProtTcpIpClientValueAddress(),
                            bve.getProtTcpIpClientValueDataType(),

                            bve.getValueMinNrCharToShow(),
                            bve.getValueNrOfDecimal(),
                            bve.getValueUM(),
                            bve.getValueUpdateMillis(),
                            Integer.valueOf(bve.getValueReadOnly() ? 1 : 0),

                            bve.getWriteValueOFF(),
                            bve.getWriteValueOFFON(),
                            bve.getWriteValueONOFF(),
                            bve.getWriteValueON(),

                            bve.getSensorTypeID(),
                            bve.getSensorValueID(),
                            Integer.valueOf(bve.getSensorEnableSimulation() ? 1 : 0),
                            bve.getSensorAmplK(),
                            bve.getSensorLowPassFilterK(),
                            bve.getSensorSampleTimeMillis(),
                            bve.getSensorWriteUpdateTimeMillis(),

                            0   // Origin
                    });

                }

                return cursor;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static Cursor load(int iType, long lRoomID)
        {
            try
            {
                m_LockCommandHolder.lock();

                Cursor cursor = null;

                SQLiteDatabase db = SQLHelper.getInstance().getDB();
                if(db != null) {

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                    _ID,
                                    COLUMN_NAME_TYPE,
                                    COLUMN_NAME_ROOM_ID,
                                    COLUMN_NAME_TAG,
                                    COLUMN_NAME_X,
                                    COLUMN_NAME_Y,
                                    COLUMN_NAME_Z,
                                    COLUMN_NAME_LANDSCAPE,

                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_ID,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_DATA_TYPE,

                                    COLUMN_NAME_VALUE_MIN_NR_CHAR_TO_SHOW,
                                    COLUMN_NAME_VALUE_NR_OF_DECIMAL,
                                    COLUMN_NAME_VALUE_UM,
                                    COLUMN_NAME_VALUE_UPDATE_MILLIS,
                                    COLUMN_NAME_VALUE_READ_ONLY,

                                    COLUMN_NAME_WRITE_VALUE_OFF,
                                    COLUMN_NAME_WRITE_VALUE_OFF_ON,
                                    COLUMN_NAME_WRITE_VALUE_ON_OFF,
                                    COLUMN_NAME_WRITE_VALUE_ON,

                                    COLUMN_NAME_SENSOR_TYPE_ID,
                                    COLUMN_NAME_SENSOR_VALUE_ID,
                                    COLUMN_NAME_SENSOR_ENABLE_SIMULATION,
                                    COLUMN_NAME_SENSOR_AMPL_K,
                                    COLUMN_NAME_SENSOR_LOW_PASS_FILTER_K,
                                    COLUMN_NAME_SENSOR_SAMPLE_TIME,
                                    COLUMN_NAME_SENSOR_WRITE_UPDATE_TIME

                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String whereClause = COLUMN_NAME_TYPE + " = ? AND " + COLUMN_NAME_ROOM_ID + " = ?" ;

                    String[] wherenArgs = { String.valueOf(iType), String.valueOf(lRoomID) };

                    cursor = db.query(
                            TABLE_NAME,                 // The table to query
                            projection,                 // The columns to return
                            whereClause,                  // The columns for the WHERE clause
                            wherenArgs,              // The values for the WHERE clause
                            null,                       // don't group the rows
                            null,                       // don't filter by row groups
                            sortOrder                   // The sort order
                    );

                }

                return cursor;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static Cursor loadByID(long lID)
        {
            try
            {
                m_LockCommandHolder.lock();

                Cursor cursor = null;

                SQLiteDatabase db = SQLHelper.getInstance().getDB();
                if(db != null) {

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                    _ID,
                                    COLUMN_NAME_TYPE,
                                    COLUMN_NAME_ROOM_ID,
                                    COLUMN_NAME_TAG,
                                    COLUMN_NAME_X,
                                    COLUMN_NAME_Y,
                                    COLUMN_NAME_Z,
                                    COLUMN_NAME_LANDSCAPE,

                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_ID,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_DATA_TYPE,

                                    COLUMN_NAME_VALUE_MIN_NR_CHAR_TO_SHOW,
                                    COLUMN_NAME_VALUE_NR_OF_DECIMAL,
                                    COLUMN_NAME_VALUE_UM,
                                    COLUMN_NAME_VALUE_UPDATE_MILLIS,
                                    COLUMN_NAME_VALUE_READ_ONLY,

                                    COLUMN_NAME_WRITE_VALUE_OFF,
                                    COLUMN_NAME_WRITE_VALUE_OFF_ON,
                                    COLUMN_NAME_WRITE_VALUE_ON_OFF,
                                    COLUMN_NAME_WRITE_VALUE_ON,

                                    COLUMN_NAME_SENSOR_TYPE_ID,
                                    COLUMN_NAME_SENSOR_VALUE_ID,
                                    COLUMN_NAME_SENSOR_ENABLE_SIMULATION,
                                    COLUMN_NAME_SENSOR_AMPL_K,
                                    COLUMN_NAME_SENSOR_LOW_PASS_FILTER_K,
                                    COLUMN_NAME_SENSOR_SAMPLE_TIME,
                                    COLUMN_NAME_SENSOR_WRITE_UPDATE_TIME

                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String whereClause = _ID + " = ? ";

                    String[] wherenArgs = { String.valueOf(lID) };

                    cursor = db.query(
                            TABLE_NAME,                 // The table to query
                            projection,                 // The columns to return
                            whereClause,                  // The columns for the WHERE clause
                            wherenArgs,              // The values for the WHERE clause
                            null,                       // don't group the rows
                            null,                       // don't filter by row groups
                            sortOrder                   // The sort order
                    );

                }

                return cursor;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static Cursor load(long lRoomID)
        {
            try
            {
                m_LockCommandHolder.lock();

                Cursor cursor = null;

                SQLiteDatabase db = SQLHelper.getInstance().getDB();
                if(db != null) {

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                    _ID,
                                    COLUMN_NAME_TYPE,
                                    COLUMN_NAME_ROOM_ID,
                                    COLUMN_NAME_TAG,
                                    COLUMN_NAME_X,
                                    COLUMN_NAME_Y,
                                    COLUMN_NAME_Z,
                                    COLUMN_NAME_LANDSCAPE,

                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_ID,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_DATA_TYPE,

                                    COLUMN_NAME_VALUE_MIN_NR_CHAR_TO_SHOW,
                                    COLUMN_NAME_VALUE_NR_OF_DECIMAL,
                                    COLUMN_NAME_VALUE_UM,
                                    COLUMN_NAME_VALUE_UPDATE_MILLIS,
                                    COLUMN_NAME_VALUE_READ_ONLY,

                                    COLUMN_NAME_WRITE_VALUE_OFF,
                                    COLUMN_NAME_WRITE_VALUE_OFF_ON,
                                    COLUMN_NAME_WRITE_VALUE_ON_OFF,
                                    COLUMN_NAME_WRITE_VALUE_ON,

                                    COLUMN_NAME_SENSOR_TYPE_ID,
                                    COLUMN_NAME_SENSOR_VALUE_ID,
                                    COLUMN_NAME_SENSOR_ENABLE_SIMULATION,
                                    COLUMN_NAME_SENSOR_AMPL_K,
                                    COLUMN_NAME_SENSOR_LOW_PASS_FILTER_K,
                                    COLUMN_NAME_SENSOR_SAMPLE_TIME,
                                    COLUMN_NAME_SENSOR_WRITE_UPDATE_TIME

                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String whereClause = COLUMN_NAME_ROOM_ID + " = ?" ;

                    String[] wherenArgs = { String.valueOf(lRoomID) };

                    cursor = db.query(
                            TABLE_NAME,                 // The table to query
                            projection,                 // The columns to return
                            whereClause,                  // The columns for the WHERE clause
                            wherenArgs,              // The values for the WHERE clause
                            null,                       // don't group the rows
                            null,                       // don't filter by row groups
                            sortOrder                   // The sort order
                    );

                }

                return cursor;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static boolean delete(long lID)
        {
            try
            {
                m_LockCommandHolder.lock();
                SQLiteDatabase db = SQLHelper.getInstance().getDB();
                if(db != null)
                {

                    // Which row to get based on WHERE
                    String whereClause = _ID + " = ? ";

                    String[] wherenArgs = { String.valueOf(lID) };

                    if(db.delete(TABLE_NAME, whereClause, wherenArgs) > 0)
                    {
                        return true;
                    }
                }

                return false;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static ArrayList<BaseValueData> get(Cursor cursor){
            try
            {
                m_LockCommandHolder.lock();

                BaseValueData bve = null;
                ArrayList<BaseValueData> albve = null;
                if((cursor != null) && (cursor.getCount() > 0))
                {
                    for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
                    {
                        if(albve == null){
                            albve = new ArrayList<>();
                        }
                        // Origin
                        boolean bSaved = true;
                        if(cursor.getColumnIndex(COLUMN_NAME_ORIGIN) > -1){
                            if(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ORIGIN)) == 0){
                                // Data come direct from LightSwithData Class
                                bSaved = false;
                            }
                        }
                        bve = new BaseValueData(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_TYPE)));
                        bve.setPositionValue(
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_TYPE)),
                                cursor.getLong(cursor.getColumnIndex(_ID)),
                                cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_ROOM_ID)),
                                cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TAG)),
                                Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_X))),
                                Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_Y))),
                                Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_Z))),
                                ((cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_LANDSCAPE)) == 0) ? false : true)
                        );

                        bve.setProtTcpIpClient(
                                ((cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE)) == 0) ? false : true),
                                cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_ID)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_DATA_TYPE))
                        );

                        bve.setFormatValue(
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_VALUE_MIN_NR_CHAR_TO_SHOW)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_VALUE_NR_OF_DECIMAL)),
                                cursor.getString(cursor.getColumnIndex(COLUMN_NAME_VALUE_UM)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_VALUE_UPDATE_MILLIS)),
                                ((cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_VALUE_READ_ONLY)) == 0) ? false : true)
                        );

                        bve.setSwitchValue(
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_WRITE_VALUE_OFF)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_WRITE_VALUE_OFF_ON)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_WRITE_VALUE_ON_OFF)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_WRITE_VALUE_ON))
                        );

                        bve.setSensorType(
                                cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_SENSOR_TYPE_ID)),
                                cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_SENSOR_VALUE_ID)),
                                ((cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_SENSOR_ENABLE_SIMULATION)) == 0) ? false : true),
                                Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SENSOR_AMPL_K))),
                                Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SENSOR_LOW_PASS_FILTER_K))),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_SENSOR_SAMPLE_TIME)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_SENSOR_WRITE_UPDATE_TIME))
                        );

                        albve.add(bve);
                    }
                }
                return albve;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

    }

    /* Inner class that defines the table contents */
    public static abstract class TcpIpClientEntry implements BaseColumns {
        public static final String TABLE_NAME = "TCPIPClient";
        public static final String COLUMN_NAME_TYPE = "Type";
        public static final String COLUMN_NAME_ENABLE = "Enable";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_ADDRESS = "Address";
        public static final String COLUMN_NAME_PORT = "Port";
        public static final String COLUMN_NAME_TIMEOUT = "Timeout";
        public static final String COLUMN_NAME_COMM_SEND_DATA_DELAY = "CommSendDataDelay";
        public static final String COLUMN_NAME_COMM_RECEIVE_WAIT_DATA = "CommReceiveWaitData";
        public static final String COLUMN_NAME_PROTOCOL = "Protocol";
        public static final String COLUMN_NAME_HEAD = "Head";
        public static final String COLUMN_NAME_TAIL = "Tail";

        // Used only in MatrixCursor
        public static final String COLUMN_NAME_ORIGIN = "Origin";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME +
                        " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_TYPE + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_ENABLE + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_ADDRESS + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PORT + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_TIMEOUT + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_COMM_SEND_DATA_DELAY + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_COMM_RECEIVE_WAIT_DATA + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROTOCOL + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_HEAD + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_TAIL + INT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static boolean save(BaseValueCommClientData ticd)  {

            boolean bRes = true;
            try
            {
                m_LockCommandHolder.lock();
                SQLiteDatabase db = SQLHelper.getInstance().getDB();
                if(db != null && ticd != null) {

                    ContentValues values = new ContentValues();
                    values.put(COLUMN_NAME_TYPE, ticd.getType());
                    values.put(COLUMN_NAME_ENABLE, Integer.valueOf(ticd.getEnable() ? 1 : 0));
                    values.put(COLUMN_NAME_NAME, ticd.getName());
                    values.put(COLUMN_NAME_ADDRESS, ticd.getAddress());
                    values.put(COLUMN_NAME_PORT, ticd.getPort());
                    values.put(COLUMN_NAME_TIMEOUT, ticd.getTimeout());
                    values.put(COLUMN_NAME_COMM_SEND_DATA_DELAY, ticd.getCommSendDelayData());
                    values.put(COLUMN_NAME_COMM_RECEIVE_WAIT_DATA, ticd.getCommReceiveWaitData());
                    values.put(COLUMN_NAME_PROTOCOL, ticd.getProtocolID());
                    values.put(COLUMN_NAME_HEAD, ticd.getHead());
                    values.put(COLUMN_NAME_TAIL, ticd.getTail());

                    String whereClause = _ID + " = ? ";

                    String[] whereArgs = {String.valueOf(ticd.getID())};
                    long id = SQLContract.save(db, TABLE_NAME, values, whereClause, whereArgs, ticd.getID());
                    // Update or Save
                    if (id > 0) {
                        ticd.setID(id);
                        ticd.setSaved(true);
                    } else {
                        bRes = false;
                    }
                }
            } finally {
                m_LockCommandHolder.unlock();
            }

            return bRes;

        }

        public static Cursor loadFromTCPIPClientData(BaseValueCommClientData ticd)
        {
            try
            {
                m_LockCommandHolder.lock();

                MatrixCursor cursor = null;

                if(ticd != null){

                    String[] columns = new String[] {
                            _ID,
                            COLUMN_NAME_TYPE,
                            COLUMN_NAME_ENABLE,
                            COLUMN_NAME_NAME,
                            COLUMN_NAME_ADDRESS,
                            COLUMN_NAME_PORT,
                            COLUMN_NAME_TIMEOUT,
                            COLUMN_NAME_COMM_SEND_DATA_DELAY,
                            COLUMN_NAME_COMM_RECEIVE_WAIT_DATA,
                            COLUMN_NAME_PROTOCOL,
                            COLUMN_NAME_HEAD,
                            COLUMN_NAME_TAIL,

                            COLUMN_NAME_ORIGIN
                    };

                    cursor = new MatrixCursor(columns);
                    cursor.addRow(new Object[] {
                            ticd.getID(),
                            ticd.getType(),
                            ticd.getEnable(),
                            ticd.getPort(),
                            ticd.getTimeout(),
                            ticd.getCommSendDelayData(),
                            ticd.getCommReceiveWaitData(),
                            ticd.getProtocol(),

                            0   // Origin
                    });

                }

                return cursor;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static Cursor load(long lID)
        {
            try
            {
                m_LockCommandHolder.lock();

                Cursor cursor = null;

                SQLiteDatabase db = SQLHelper.getInstance().getDB();
                if(db != null) {

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                    _ID,
                                    COLUMN_NAME_TYPE,
                                    COLUMN_NAME_ENABLE,
                                    COLUMN_NAME_NAME,
                                    COLUMN_NAME_ADDRESS,
                                    COLUMN_NAME_PORT,
                                    COLUMN_NAME_TIMEOUT,
                                    COLUMN_NAME_COMM_SEND_DATA_DELAY,
                                    COLUMN_NAME_COMM_RECEIVE_WAIT_DATA,
                                    COLUMN_NAME_PROTOCOL,
                                    COLUMN_NAME_HEAD,
                                    COLUMN_NAME_TAIL
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String whereClause = _ID + " = ? ";

                    String[] wherenArgs = { String.valueOf(lID) };

                    cursor = db.query(
                            TABLE_NAME,                 // The table to query
                            projection,                 // The columns to return
                            whereClause,                  // The columns for the WHERE clause
                            wherenArgs,              // The values for the WHERE clause
                            null,                       // don't group the rows
                            null,                       // don't filter by row groups
                            sortOrder                   // The sort order
                    );

                }

                return cursor;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static Cursor loadByType(int iType)
        {
            try
            {
                m_LockCommandHolder.lock();

                Cursor cursor = null;

                SQLiteDatabase db = SQLHelper.getInstance().getDB();
                if(db != null) {

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                    _ID,
                                    COLUMN_NAME_TYPE,
                                    COLUMN_NAME_ENABLE,
                                    COLUMN_NAME_NAME,
                                    COLUMN_NAME_ADDRESS,
                                    COLUMN_NAME_PORT,
                                    COLUMN_NAME_TIMEOUT,
                                    COLUMN_NAME_COMM_SEND_DATA_DELAY,
                                    COLUMN_NAME_COMM_RECEIVE_WAIT_DATA,
                                    COLUMN_NAME_PROTOCOL,
                                    COLUMN_NAME_HEAD,
                                    COLUMN_NAME_TAIL
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String whereClause = COLUMN_NAME_TYPE + " = ? ";

                    String[] wherenArgs = { String.valueOf(iType) };

                    cursor = db.query(
                            TABLE_NAME,                 // The table to query
                            projection,                 // The columns to return
                            whereClause,                  // The columns for the WHERE clause
                            wherenArgs,              // The values for the WHERE clause
                            null,                       // don't group the rows
                            null,                       // don't filter by row groups
                            sortOrder                   // The sort order
                    );

                }

                return cursor;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static Cursor load()
        {
            try
            {
                m_LockCommandHolder.lock();

                Cursor cursor = null;

                SQLiteDatabase db = SQLHelper.getInstance().getDB();
                if(db != null) {

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                    _ID,
                                    COLUMN_NAME_TYPE,
                                    COLUMN_NAME_ENABLE,
                                    COLUMN_NAME_NAME,
                                    COLUMN_NAME_ADDRESS,
                                    COLUMN_NAME_PORT,
                                    COLUMN_NAME_TIMEOUT,
                                    COLUMN_NAME_COMM_SEND_DATA_DELAY,
                                    COLUMN_NAME_COMM_RECEIVE_WAIT_DATA,
                                    COLUMN_NAME_PROTOCOL,
                                    COLUMN_NAME_HEAD,
                                    COLUMN_NAME_TAIL
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    cursor = db.query(
                            TABLE_NAME,                 // The table to query
                            projection,                 // The columns to return
                            null,                       // The columns for the WHERE clause
                            null,                       // The values for the WHERE clause
                            null,                       // don't group the rows
                            null,                       // don't filter by row groups
                            sortOrder                   // The sort order
                    );

                }

                return cursor;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static boolean delete(long lID)
        {
            try
            {
                m_LockCommandHolder.lock();
                SQLiteDatabase db = SQLHelper.getInstance().getDB();
                if(db != null)
                {

                    // Which row to get based on WHERE
                    String whereClause = _ID + " = ? ";

                    String[] wherenArgs = { String.valueOf(lID) };

                    if(db.delete(TABLE_NAME, whereClause, wherenArgs) > 0)
                    {
                        return true;
                    }
                }

                return false;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static ArrayList<BaseValueCommClientData> get(Cursor cursor){
            try
            {
                m_LockCommandHolder.lock();

                BaseValueCommClientData ticd = null;
                ArrayList<BaseValueCommClientData> alticd = null;
                if((cursor != null) && (cursor.getCount() > 0))
                {
                    for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
                    {
                        if(alticd == null){
                            alticd = new ArrayList<>();
                        }
                        // Origin
                        boolean bSaved = true;
                        if(cursor.getColumnIndex(COLUMN_NAME_ORIGIN) > -1){
                            if(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ORIGIN)) == 0){
                                // Data come direct from LightSwithData Class
                                bSaved = false;
                            }
                        }

                        ticd = new BaseValueCommClientData(
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_TYPE)),
                                cursor.getLong(cursor.getColumnIndex(_ID)),
                                bSaved,
                                ((cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ENABLE)) == 0) ? false : true),
                                cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NAME)),
                                cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ADDRESS)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PORT)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_TIMEOUT)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_COMM_SEND_DATA_DELAY)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_COMM_RECEIVE_WAIT_DATA)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PROTOCOL)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_HEAD)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_TAIL))

                        );
                        alticd.add(ticd);
                    }
                }
                return alticd;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

    }

    /* Inner class that defines the table contents */
    public static abstract class RoomEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "Room";
        public static final String COLUMN_NAME_TAG = "TAG";
        public static final String COLUMN_NAME_HOUSE_TAG = "House_TAG";
        public static final String COLUMN_NAME_X = "X";
        public static final String COLUMN_NAME_Y = "Y";
        public static final String COLUMN_NAME_Z = "Z";
        public static final String COLUMN_NAME_LANDSCAPE = "Landscape";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME +
                        " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_TAG + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_HOUSE_TAG + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_X + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_Y + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_Z + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_LANDSCAPE + INT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static long save(RoomFragmentData rfd)  {

            long id = -1;
            try
            {
                m_LockCommandHolder.lock();
                SQLiteDatabase db = SQLHelper.getInstance().getDB();

                if(db != null && rfd != null) {

                    ContentValues values = new ContentValues();
                    values.put(COLUMN_NAME_TAG, String.valueOf(rfd.getTAG()));
                    values.put(COLUMN_NAME_HOUSE_TAG, rfd.getHouseTAG());
                    values.put(COLUMN_NAME_X, Float.toString(rfd.getPosX()));
                    values.put(COLUMN_NAME_Y, Float.toString(rfd.getPosY()));
                    values.put(COLUMN_NAME_Z, Float.toString(rfd.getPosZ()));
                    values.put(COLUMN_NAME_LANDSCAPE, Integer.valueOf(rfd.getLandscape() ? 1 : 0));

                    String whereClause = _ID + " = ? AND " +  COLUMN_NAME_HOUSE_TAG + " = ?";

                    String[] whereArgs = {String.valueOf(rfd.getID()), String.valueOf(rfd.getHouseTAG())};
                    id = SQLContract.save(db, TABLE_NAME, values, whereClause, whereArgs, rfd.getID());
                    // Update or Save
                    if (id > 0) {
                        rfd.setID(id);
                        rfd.setSaved(true);
                    }
                }
            } finally {
                m_LockCommandHolder.unlock();
            }

            return id;
        }

        public static Cursor load()
        {
            try
            {
                m_LockCommandHolder.lock();

                Cursor cursor = null;

                SQLiteDatabase db = SQLHelper.getInstance().getDB();

                if(db != null) {

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                _ID,
                                COLUMN_NAME_TAG,
                                COLUMN_NAME_HOUSE_TAG,
                                COLUMN_NAME_X,
                                COLUMN_NAME_Y,
                                COLUMN_NAME_Z,
                                COLUMN_NAME_LANDSCAPE
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    cursor = db.query(
                            TABLE_NAME,                 // The table to query
                            projection,                 // The columns to return
                            null,                  // The columns for the WHERE clause
                            null,              // The values for the WHERE clause
                            null,                       // don't group the rows
                            null,                       // don't filter by row groups
                            sortOrder                   // The sort order
                    );

                }

                return cursor;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static Cursor load(long id) {
            try
            {
                m_LockCommandHolder.lock();

                Cursor cursor = null;

                SQLiteDatabase db = SQLHelper.getInstance().getDB();

                if(db != null) {

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                _ID,
                                COLUMN_NAME_TAG,
                                COLUMN_NAME_HOUSE_TAG,
                                COLUMN_NAME_X,
                                COLUMN_NAME_Y,
                                COLUMN_NAME_Z,
                                COLUMN_NAME_LANDSCAPE
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String whereClause = _ID + " = ?";

                    String[] whereArgs = {String.valueOf(id)};

                    cursor = db.query(
                            TABLE_NAME,  // The table to query
                            projection,                               // The columns to return
                            whereClause,                                      // The columns for the WHERE clause
                            whereArgs,                                      // The values for the WHERE clause
                            null,                                     // don't group the rows
                            null,                                     // don't filter by row groups
                            sortOrder                                 // The sort order
                    );

                }

                return cursor;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static String getTag(long id) {

            try
            {
                m_LockCommandHolder.lock();

                String strTAG = null;

                SQLiteDatabase db = SQLHelper.getInstance().getDB();
                if(db != null) {

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                    COLUMN_NAME_TAG
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String whereClause = _ID + " = ?";

                    String[] whereArgs = {String.valueOf(id)};

                    Cursor cursor = db.query(
                            TABLE_NAME,  // The table to query
                            projection,                               // The columns to return
                            whereClause,                                      // The columns for the WHERE clause
                            whereArgs,                                      // The values for the WHERE clause
                            null,                                     // don't group the rows
                            null,                                     // don't filter by row groups
                            sortOrder                                 // The sort order
                    );
                    if ((cursor != null) && (cursor.getCount() > 0)) {
                        cursor.moveToFirst();
                        strTAG = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TAG));

                        // Chiudo il cursore
                        cursor.close();
                    }

                }

                return strTAG;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static boolean isTagPresent(String strTag) {

            try
            {
                m_LockCommandHolder.lock();

                boolean bRes = false;

                SQLiteDatabase db = SQLHelper.getInstance().getDB();

                if(db != null) {

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                    _ID
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String whereClause = COLUMN_NAME_TAG + " = ?";

                    String[] whereArgs = {String.valueOf(strTag)};

                    Cursor cursor = db.query(
                            TABLE_NAME,  // The table to query
                            projection,                               // The columns to return
                            whereClause,                                      // The columns for the WHERE clause
                            whereArgs,                                      // The values for the WHERE clause
                            null,                                     // don't group the rows
                            null,                                     // don't filter by row groups
                            sortOrder                                 // The sort order
                    );
                    if ((cursor != null) && (cursor.getCount() > 0)) {
                        bRes = true;
                        // Chiudo il cursore
                        cursor.close();
                    }
                }

                return bRes;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static ArrayList<RoomFragmentData> get(Cursor cursor){
            RoomFragmentData rfd = null;
            ArrayList<RoomFragmentData> alrfd = null;
            if((cursor != null) && (cursor.getCount() > 0))
            {
                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
                {
                    if(alrfd == null){
                        alrfd = new ArrayList<>();
                    }
                    rfd = new RoomFragmentData(
                            true,
                            false,
                            cursor.getLong(cursor.getColumnIndex(_ID)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HOUSE_TAG)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TAG)),
                            Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_X))),
                            Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_Y))),
                            Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_Z))),
                            ((cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_LANDSCAPE)) == 0) ? false : true)
                    );
                    alrfd.add(rfd);
                }
            }
            return alrfd;
        }
    }

    public static long save(SQLiteDatabase db, String table, ContentValues values, String whereClause, String[] whereArgs, long lID ){
        long m_lID = -1;
        if(db != null) {
            if (db.update(table, values, whereClause, whereArgs) == 0) {
                // The Parameter doesn't exist, i will add it
                m_lID = db.insert(table, null, values);
            } else {
                m_lID = lID;
            }
        }
        return m_lID;
    }
}
