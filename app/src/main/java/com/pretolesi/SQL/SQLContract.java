package com.pretolesi.SQL;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import com.pretolesi.easydomotic.LightSwitch.LightSwitchData;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.RoomFragmentData;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClientData;

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
    public static final String IMAGE_TYPE = " BLOB";
    public static final String COMMA_SEP = ",";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private SQLContract()
    {
    }
    public enum SettingID
    {
        DEFAULT(R.string.text_stica_tv_server_ip_address, 1, "192.168.1.1", 7 ,15);
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
    public static abstract class TcpIpClientEntry implements BaseColumns {
        public static final String TABLE_NAME = "TCPIPClient";
        public static final String COLUMN_NAME_ENABLE = "Enable";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_ADDRESS = "Address";
        public static final String COLUMN_NAME_PORT = "Port";
        public static final String COLUMN_NAME_TIMEOUT = "Timeout";
        public static final String COLUMN_NAME_COMM_SEND_DATA_DELAY = "CommSendDataDelay";
        public static final String COLUMN_NAME_PROTOCOL = "Protocol";
        public static final String COLUMN_NAME_HEAD = "Head";
        public static final String COLUMN_NAME_TAIL = "Tail";

        // Used only in MatrixCursor
        public static final String COLUMN_NAME_ORIGIN = "Origin";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME +
                        " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_ENABLE + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_ADDRESS + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PORT + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_TIMEOUT + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_COMM_SEND_DATA_DELAY + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROTOCOL + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_HEAD + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_TAIL + INT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static boolean save(TCPIPClientData ticd)  {

            boolean bRes = true;
            try
            {
                m_LockCommandHolder.lock();
                SQLiteDatabase db = SQLHelper.getInstance().getDB();
                if(db != null && ticd != null) {

                    ContentValues values = new ContentValues();
                    values.put(COLUMN_NAME_ENABLE, Integer.valueOf(ticd.getEnable() ? 1 : 0));
                    values.put(COLUMN_NAME_NAME, ticd.getName());
                    values.put(COLUMN_NAME_ADDRESS, ticd.getAddress());
                    values.put(COLUMN_NAME_PORT, ticd.getPort());
                    values.put(COLUMN_NAME_TIMEOUT, ticd.getTimeout());
                    values.put(COLUMN_NAME_COMM_SEND_DATA_DELAY, ticd.getCommSendDelayData());
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

        public static Cursor loadFromTCPIPClientData(TCPIPClientData ticd)
        {
            try
            {
                m_LockCommandHolder.lock();

                MatrixCursor cursor = null;

                if(ticd != null){

                    String[] columns = new String[] {
                            _ID,
                            COLUMN_NAME_ENABLE,
                            COLUMN_NAME_NAME,
                            COLUMN_NAME_ADDRESS,
                            COLUMN_NAME_PORT,
                            COLUMN_NAME_TIMEOUT,
                            COLUMN_NAME_COMM_SEND_DATA_DELAY,
                            COLUMN_NAME_PROTOCOL,
                            COLUMN_NAME_HEAD,
                            COLUMN_NAME_TAIL,

                            COLUMN_NAME_ORIGIN
                    };

                    cursor = new MatrixCursor(columns);
                    cursor.addRow(new Object[] {
                            ticd.getID(),
                            ticd.getEnable(),
                            ticd.getPort(),
                            ticd.getTimeout(),
                            ticd.getCommSendDelayData(),
                            ticd.getProtocolID(),

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
                                    COLUMN_NAME_ENABLE,
                                    COLUMN_NAME_NAME,
                                    COLUMN_NAME_ADDRESS,
                                    COLUMN_NAME_PORT,
                                    COLUMN_NAME_TIMEOUT,
                                    COLUMN_NAME_COMM_SEND_DATA_DELAY,
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

        public static Cursor load(String strName)
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
                                    COLUMN_NAME_ENABLE,
                                    COLUMN_NAME_NAME,
                                    COLUMN_NAME_ADDRESS,
                                    COLUMN_NAME_PORT,
                                    COLUMN_NAME_TIMEOUT,
                                    COLUMN_NAME_COMM_SEND_DATA_DELAY,
                                    COLUMN_NAME_PROTOCOL,
                                    COLUMN_NAME_HEAD,
                                    COLUMN_NAME_TAIL
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String whereClause = _ID + " = ? ";

                    String[] wherenArgs = { String.valueOf(strName) };

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
                                    COLUMN_NAME_ENABLE,
                                    COLUMN_NAME_NAME,
                                    COLUMN_NAME_ADDRESS,
                                    COLUMN_NAME_PORT,
                                    COLUMN_NAME_TIMEOUT,
                                    COLUMN_NAME_COMM_SEND_DATA_DELAY,
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

        public static long isAlreadyStored(String strName) {

            try
            {
                m_LockCommandHolder.lock();

                long lID = -1;

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
                    String whereClause = COLUMN_NAME_NAME + " = ? ";

                    String[] whereArgs = { String.valueOf(strName) };

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

                        lID = cursor.getLong(cursor.getColumnIndex(_ID));

                        // Chiudo il cursore
                        cursor.close();
                    }
                }

                return lID;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static ArrayList<TCPIPClientData> get(Cursor cursor){
            try
            {
                m_LockCommandHolder.lock();

                TCPIPClientData ticd = null;
                ArrayList<TCPIPClientData> alticd = null;
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

                        ticd = new TCPIPClientData(
                                cursor.getLong(cursor.getColumnIndex(_ID)),
                                bSaved,
                                ((cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ENABLE)) == 0) ? false : true),
                                cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NAME)),
                                cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ADDRESS)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PORT)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_TIMEOUT)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_COMM_SEND_DATA_DELAY)),
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
    public static abstract class LightSwitchEntry implements BaseColumns {
        public static final String TABLE_NAME = "LightSwitch";
        public static final String COLUMN_NAME_TAG = "TAG";
        public static final String COLUMN_NAME_ROOM_ID = "Room_ID";
        public static final String COLUMN_NAME_X = "X";
        public static final String COLUMN_NAME_Y = "Y";
        public static final String COLUMN_NAME_Z = "Z";
        public static final String COLUMN_NAME_LANDSCAPE = "Landscape";
        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE = "ProtTcpIpClientEnable";
        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_ID = "ProtTcpIpClientID";
        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID = "ProtTcpIpClientValueID";
        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF = "ProtTcpIpClientValueOFF";
        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF_ON = "ProtTcpIpClientValueOFFON";
        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON_OFF = "ProtTcpIpClientValueONOFF";
        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON = "ProtTcpIpClientValueON";
        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS = "ProtTcpIpClientValueAddress";
        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_UPDATE_MILLIS = "ProtTcpIpClientValueUpdateMillis";
        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_SEND_DATA_ON_CHANGE = "ProtTcpIpClientSendDataOnChange";
        public static final String COLUMN_NAME_PROT_TCP_IP_CLIENT_WAIT_ANSWER_BEFORE_SEND_NEXT_DATA = "ProtTcpIpClientWaitAnswerBeforeSendNextData";

        // Used only in MatrixCursor
        public static final String COLUMN_NAME_ORIGIN = "Origin";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME +
                        " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_TAG + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_ROOM_ID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_X + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_Y + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_Z + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_LANDSCAPE + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROT_TCP_IP_CLIENT_ID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF_ON + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON_OFF + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_UPDATE_MILLIS + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROT_TCP_IP_CLIENT_SEND_DATA_ON_CHANGE + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PROT_TCP_IP_CLIENT_WAIT_ANSWER_BEFORE_SEND_NEXT_DATA + INT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;


        public static boolean save(ArrayList<LightSwitchData> allsd)  {

            boolean bRes = true;
            try
            {
                m_LockCommandHolder.lock();

                SQLiteDatabase db = SQLHelper.getInstance().getDB();

                if(db != null && allsd != null) {

                    ContentValues values = new ContentValues();
                    for(LightSwitchData lsdTemp:allsd){
                        if(lsdTemp != null) {
                            values.put(COLUMN_NAME_TAG, String.valueOf(lsdTemp.getTag()));
                            values.put(COLUMN_NAME_ROOM_ID, lsdTemp.getRoomID());
                            values.put(COLUMN_NAME_X, Float.toString(lsdTemp.getPosX()));
                            values.put(COLUMN_NAME_Y, Float.toString(lsdTemp.getPosY()));
                            values.put(COLUMN_NAME_Z, Float.toString(lsdTemp.getPosZ()));
                            values.put(COLUMN_NAME_LANDSCAPE, Integer.valueOf(lsdTemp.getLandscape() ? 1 : 0));

                            values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE, Integer.valueOf(lsdTemp.getProtTcpIpClientEnable() ? 1 : 0));
                            values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_ID, lsdTemp.getProtTcpIpClientID());
                            values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID, lsdTemp.getProtTcpIpClientValueID());
                            values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF, lsdTemp.getProtTcpIpClientValueOFF());
                            values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF_ON, lsdTemp.getProtTcpIpClientValueOFFON());
                            values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON_OFF, lsdTemp.getProtTcpIpClientValueONOFF());
                            values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON, lsdTemp.getProtTcpIpClientValueON());
                            values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS, lsdTemp.getProtTcpIpClientValueAddress());
                            values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_UPDATE_MILLIS, lsdTemp.getProtTcpIpClientValueUpdateMillis());
                            values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_SEND_DATA_ON_CHANGE, Integer.valueOf(lsdTemp.getProtTcpIpClientSendDataOnChange() ? 1 : 0));
                            values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_WAIT_ANSWER_BEFORE_SEND_NEXT_DATA, Integer.valueOf(lsdTemp.getProtTcpIpClientWaitAnswerBeforeSendNextData() ? 1 : 0));

                            String whereClause = _ID + " = ? AND " + COLUMN_NAME_ROOM_ID + " = ?";

                            String[] whereArgs = {String.valueOf(lsdTemp.getID()), String.valueOf(lsdTemp.getRoomID())};
                            long id = SQLContract.save(db, TABLE_NAME, values, whereClause, whereArgs, lsdTemp.getID());
                            // Update or Save
                            if (id > 0) {
                                lsdTemp.setID(id);
                                lsdTemp.setSaved(true);
                            } else {
                                bRes = false;
                            }
                        }
                    }
                }
            } finally {
                m_LockCommandHolder.unlock();
            }

            return bRes;

        }

        public static boolean save(LightSwitchData lsd)  {

            boolean bRes = true;
            try
            {
                m_LockCommandHolder.lock();
                SQLiteDatabase db = SQLHelper.getInstance().getDB();
                if(db != null && lsd != null) {

                    ContentValues values = new ContentValues();
                    values.put(COLUMN_NAME_TAG, String.valueOf(lsd.getTag()));
                    values.put(COLUMN_NAME_ROOM_ID, lsd.getRoomID());
                    values.put(COLUMN_NAME_X, Float.toString(lsd.getPosX()));
                    values.put(COLUMN_NAME_Y, Float.toString(lsd.getPosY()));
                    values.put(COLUMN_NAME_Z, Float.toString(lsd.getPosZ()));
                    values.put(COLUMN_NAME_LANDSCAPE, Integer.valueOf(lsd.getLandscape() ? 1 : 0));

                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE, Integer.valueOf(lsd.getProtTcpIpClientEnable() ? 1 : 0));
                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_ID, lsd.getProtTcpIpClientID());
                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID, lsd.getProtTcpIpClientValueID());
                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF, lsd.getProtTcpIpClientValueOFF());
                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF_ON, lsd.getProtTcpIpClientValueOFFON());
                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON_OFF, lsd.getProtTcpIpClientValueONOFF());
                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON, lsd.getProtTcpIpClientValueON());
                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS, lsd.getProtTcpIpClientValueAddress());
                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_UPDATE_MILLIS, lsd.getProtTcpIpClientValueUpdateMillis());
                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_SEND_DATA_ON_CHANGE, Integer.valueOf(lsd.getProtTcpIpClientSendDataOnChange() ? 1 : 0));
                    values.put(COLUMN_NAME_PROT_TCP_IP_CLIENT_WAIT_ANSWER_BEFORE_SEND_NEXT_DATA, Integer.valueOf(lsd.getProtTcpIpClientWaitAnswerBeforeSendNextData() ? 1 : 0));

                    String whereClause = _ID + " = ? AND " +  COLUMN_NAME_ROOM_ID + " = ?";

                    String[] whereArgs = {String.valueOf(lsd.getID()), String.valueOf(lsd.getRoomID())};
                    long id = SQLContract.save(db, TABLE_NAME, values, whereClause, whereArgs, lsd.getID());
                    // Update or Save
                    if (id > 0) {
                        lsd.setID(id);
                        lsd.setSaved(true);
                    } else {
                        bRes = false;
                    }
                }
            } finally {
                m_LockCommandHolder.unlock();
            }

            return bRes;

        }

        public static Cursor loadFromLightSwitchData(LightSwitchData lsd)
        {
            try
            {
                m_LockCommandHolder.lock();

                MatrixCursor cursor = null;

                if(lsd != null){

                    String[] columns = new String[] {
                            _ID,
                            COLUMN_NAME_TAG,
                            COLUMN_NAME_ROOM_ID,
                            COLUMN_NAME_X,
                            COLUMN_NAME_Y,
                            COLUMN_NAME_Z,
                            COLUMN_NAME_LANDSCAPE,

                            COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE,
                            COLUMN_NAME_PROT_TCP_IP_CLIENT_ID,
                            COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID,
                            COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF,
                            COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF_ON,
                            COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON_OFF,
                            COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON,
                            COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS,
                            COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_UPDATE_MILLIS,
                            COLUMN_NAME_PROT_TCP_IP_CLIENT_SEND_DATA_ON_CHANGE,
                            COLUMN_NAME_PROT_TCP_IP_CLIENT_WAIT_ANSWER_BEFORE_SEND_NEXT_DATA,

                            COLUMN_NAME_ORIGIN
                    };

                    cursor = new MatrixCursor(columns);
                    cursor.addRow(new Object[] {
                            lsd.getID(),
                            lsd.getTag(),
                            lsd.getRoomID(),
                            lsd.getPosX(),
                            lsd.getPosY(),
                            lsd.getPosZ(),
                            Integer.valueOf(lsd.getLandscape() ? 1 : 0),

                            Integer.valueOf(lsd.getProtTcpIpClientEnable() ? 1 : 0),
                            lsd.getProtTcpIpClientID(),
                            lsd.getProtTcpIpClientValueID(),
                            lsd.getProtTcpIpClientValueOFF(),
                            lsd.getProtTcpIpClientValueOFFON(),
                            lsd.getProtTcpIpClientValueONOFF(),
                            lsd.getProtTcpIpClientValueON(),
                            lsd.getProtTcpIpClientValueAddress(),
                            lsd.getProtTcpIpClientValueUpdateMillis(),
                            Integer.valueOf(lsd.getProtTcpIpClientSendDataOnChange() ? 1 : 0),
                            Integer.valueOf(lsd.getProtTcpIpClientWaitAnswerBeforeSendNextData() ? 1 : 0),

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

        public static Cursor load(long lID, long lRoomID)
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
                                    COLUMN_NAME_ROOM_ID,
                                    COLUMN_NAME_X,
                                    COLUMN_NAME_Y,
                                    COLUMN_NAME_Z,
                                    COLUMN_NAME_LANDSCAPE,

                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_ID,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF_ON,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON_OFF,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_UPDATE_MILLIS,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_SEND_DATA_ON_CHANGE,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_WAIT_ANSWER_BEFORE_SEND_NEXT_DATA
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String whereClause = _ID + " = ? AND " + COLUMN_NAME_ROOM_ID + " = ?" ;

                    String[] wherenArgs = { String.valueOf(lID), String.valueOf(lRoomID) };

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
                                    COLUMN_NAME_TAG,
                                    COLUMN_NAME_ROOM_ID,
                                    COLUMN_NAME_X,
                                    COLUMN_NAME_Y,
                                    COLUMN_NAME_Z,
                                    COLUMN_NAME_LANDSCAPE,

                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_ID,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF_ON,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON_OFF,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_UPDATE_MILLIS,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_SEND_DATA_ON_CHANGE,
                                    COLUMN_NAME_PROT_TCP_IP_CLIENT_WAIT_ANSWER_BEFORE_SEND_NEXT_DATA
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String whereClause = COLUMN_NAME_ROOM_ID + " = ?";

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

        public static boolean delete(long lID, long lRoomID)
        {
            try
            {
                m_LockCommandHolder.lock();
                SQLiteDatabase db = SQLHelper.getInstance().getDB();
                if(db != null)
                {

                    // Which row to get based on WHERE
                    String whereClause = _ID + " = ? AND " + COLUMN_NAME_ROOM_ID + " = ?" ;

                    String[] wherenArgs = { String.valueOf(lID), String.valueOf(lRoomID) };

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

        public static boolean isTagPresent(String strTag, long lRoomID) {

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
                    String whereClause = COLUMN_NAME_TAG + " = ? AND " + COLUMN_NAME_ROOM_ID + " = ?" ;

                    String[] whereArgs = { String.valueOf(strTag), String.valueOf(lRoomID) };

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

        public static ArrayList<LightSwitchData> get(Cursor cursor){
            try
            {
                m_LockCommandHolder.lock();

                LightSwitchData lsd = null;
                ArrayList<LightSwitchData> allsd = null;
                if((cursor != null) && (cursor.getCount() > 0))
                {
                    for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
                    {
                        if(allsd == null){
                            allsd = new ArrayList<>();
                        }
                        // Origin
                        boolean bSaved = true;
                        if(cursor.getColumnIndex(COLUMN_NAME_ORIGIN) > -1){
                            if(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ORIGIN)) == 0){
                                // Data come direct from LightSwithData Class
                                bSaved = false;
                            }
                        }
                        lsd = new LightSwitchData(
                                cursor.getLong(cursor.getColumnIndex(_ID)),
                                bSaved,
                                false,
                                cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_ROOM_ID)),
                                cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TAG)),
                                Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_X))),
                                Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_Y))),
                                Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_Z))),
                                ((cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_LANDSCAPE)) == 0) ? false : true)
                        );

                        lsd.setProtTcpIpClient(
                                ((cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_ENABLE)) == 0) ? false : true),
                                cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_ID)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ID)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_OFF_ON)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON_OFF)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ON)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_ADDRESS)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_VALUE_UPDATE_MILLIS)),
                                ((cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_SEND_DATA_ON_CHANGE)) == 0) ? false : true),
                                ((cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PROT_TCP_IP_CLIENT_WAIT_ANSWER_BEFORE_SEND_NEXT_DATA)) == 0) ? false : true)
                        );
                        allsd.add(lsd);
                    }
                }
                return allsd;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

    }

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
