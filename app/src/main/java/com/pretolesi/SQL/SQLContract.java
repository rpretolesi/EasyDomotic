package com.pretolesi.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import com.pretolesi.easydomotic.LightSwitch;
import com.pretolesi.easydomotic.LightSwitchData;
import com.pretolesi.easydomotic.RoomFragmentData;

/**
 * Created by RPRETOLESI on 28/01/2015.
 */
public class SQLContract
{
    private static ReentrantLock m_LockCommandHolder = new ReentrantLock();;

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
    public enum AppParameter
    {
        SCHEDULED_REMINDER_FREQUENCY(0, "0"),
        SCHEDULED_UPDATE_FREQUENCY(1, "0"),
        IP_ADDRESS(2, "192.168.1.1"),
        PORT(3, "502"),
        TIMEOUT(4, "30000"),
        COMM_FRAME_DELAY(5, "100"),
        SET_SENSOR_FEEDBACK_AMPL_K(10, "500.0"),
        SET_SENSOR_LOW_PASS_FILTER_K(11, "0.5"),
        SET_SENSOR_MAX_OUTPUT_VALUE(12, "250"),
        SET_SENSOR_MIN_VALUE_START_OUTPUT(13, "10"),
        SET_SENSOR_ORIENTATION_LANDSCAPE(14, "10"),
        LAST_ROOM_TAG(100, ""),
        DEFAULT_ROOM_TAG(100, "");

        private int value;
        private String defaultValue;

        private AppParameter(int value, String defaultValue) {
            this.value = value;
            this.defaultValue = defaultValue;
        }

        public int getValue() {
            return value;
        }

        public String getDefaultValue() {
            return defaultValue;
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

        public static boolean setParameter(Context context, AppParameter pType, String strpValue)
        {
            m_LockCommandHolder.lock();

            ContentValues values = null;
            try
            {
                if (context != null && pType != null && strpValue != null)
                {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

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
            catch (Exception ex)
            {

            }
            finally
            {
                if(values != null)
                {
                    values.clear();
                }

                m_LockCommandHolder.unlock();
            }

            return false;
        }

        public static String getParameter(Context context, AppParameter pType)
        {
            m_LockCommandHolder.lock();

            Cursor cursor = null;
            String strRes = "";
            try
            {
                if(context != null && pType != null)
                {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

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
    }

    /* Inner class that defines the table contents */
    public static abstract class LightSwitchEntry implements BaseColumns {
        public static final String TABLE_NAME = "LightSwitch";
        public static final String COLUMN_NAME_ROOM_TAG = "Room_TAG";
        public static final String COLUMN_NAME_TAG = "TAG";
        public static final String COLUMN_NAME_X = "X";
        public static final String COLUMN_NAME_Y = "Y";
        public static final String COLUMN_NAME_Z = "Z";
        public static final String COLUMN_NAME_LANDSCAPE = "Landscape";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME +
                        " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_ROOM_TAG + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_TAG + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_X + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_Y + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_Z + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_LANDSCAPE + INT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;


        public static boolean save(Context context, ArrayList<LightSwitchData> lsd)  {

            boolean bRes = true;
            try
            {
                m_LockCommandHolder.lock();
                if(context != null && lsd != null) {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

                    ContentValues values = new ContentValues();
                    for(LightSwitchData lsdTemp:lsd){
                        values.put(COLUMN_NAME_ROOM_TAG, lsdTemp.getRoomTAG());
                        values.put(COLUMN_NAME_TAG, String.valueOf(lsdTemp.getTAG()));
                        values.put(COLUMN_NAME_X, Float.toString(lsdTemp.getPosX()));
                        values.put(COLUMN_NAME_Y, Float.toString(lsdTemp.getPosY()));
                        values.put(COLUMN_NAME_Z, Float.toString(lsdTemp.getPosZ()));
                        values.put(COLUMN_NAME_LANDSCAPE, Integer.valueOf(lsdTemp.getLandscape() ? 1 : 0));
                        // Insert the new row, returning the primary key value of the new row
                        if(db.insert(TABLE_NAME, null, values) <= 0) {
                            bRes = false;
                        }
                    }
                }
            } finally {
                m_LockCommandHolder.unlock();
            }

            return bRes;

        }

        public static ArrayList<LightSwitchData> get(Context context, String strRoomTAG)
        {
            try
            {
                m_LockCommandHolder.lock();

                ArrayList<LightSwitchData> allsd = new ArrayList<>();

                if(context != null && strRoomTAG != null)
                {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

                    LightSwitchData lsd = null;

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                    _ID,
                                    COLUMN_NAME_ROOM_TAG,
                                    COLUMN_NAME_TAG,
                                    COLUMN_NAME_X,
                                    COLUMN_NAME_Y,
                                    COLUMN_NAME_Z,
                                    COLUMN_NAME_LANDSCAPE
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String selection = COLUMN_NAME_ROOM_TAG + " = ?";

                    String[] selectionArgs = { String.valueOf(strRoomTAG)};

                    Cursor cursor = db.query(
                            TABLE_NAME,                 // The table to query
                            projection,                 // The columns to return
                            selection,                  // The columns for the WHERE clause
                            selectionArgs,              // The values for the WHERE clause
                            null,                       // don't group the rows
                            null,                       // don't filter by row groups
                            sortOrder                   // The sort order
                    );
                    if((cursor != null) && (cursor.getCount() > 0))
                    {
                        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
                        {
                            lsd = new LightSwitchData(
                                    cursor.getLong(cursor.getColumnIndex(_ID)),
                                    cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ROOM_TAG)),
                                    cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TAG)),
                                    Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_X))),
                                    Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_Y))),
                                    Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_Z))),
                                    ((cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_LANDSCAPE)) == 0) ? false : true)
                            );
                            allsd.add(lsd);
                        }

                        // Chiudo il cursore
                        cursor.close();
                    }
                }
                return allsd;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }

        }

        public static boolean isTagPresent(Context context, String strRoomTag, String strTag) {

            try
            {
                m_LockCommandHolder.lock();

                boolean bRes = false;

                if(context != null) {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                    _ID
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String selection =
                            COLUMN_NAME_ROOM_TAG + " = ? AND " +
                            COLUMN_NAME_TAG + " = ?" ;

                    String[] selectionArgs = { String.valueOf(strRoomTag), String.valueOf(strTag) };

                    Cursor cursor = db.query(
                            TABLE_NAME,  // The table to query
                            projection,                               // The columns to return
                            selection,                                      // The columns for the WHERE clause
                            selectionArgs,                                      // The values for the WHERE clause
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
    }

    public static abstract class RoomEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "Room";
        public static final String COLUMN_NAME_HOUSE_TAG = "House_TAG";
        public static final String COLUMN_NAME_TAG = "TAG";
        public static final String COLUMN_NAME_X = "X";
        public static final String COLUMN_NAME_Y = "Y";
        public static final String COLUMN_NAME_Z = "Z";
        public static final String COLUMN_NAME_LANDSCAPE = "Landscape";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME +
                        " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_HOUSE_TAG + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_TAG + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_X + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_Y + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_Z + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_LANDSCAPE + INT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static boolean save(Context context, RoomFragmentData rfd)  {

            boolean bRes = false;
            try
            {
                m_LockCommandHolder.lock();
                if(context != null && rfd != null) {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

                    ContentValues values = new ContentValues();
                    values.put(COLUMN_NAME_HOUSE_TAG, rfd.getHouseTAG());
                    values.put(COLUMN_NAME_TAG, String.valueOf(rfd.getTAG()));
                    values.put(COLUMN_NAME_X, Float.toString(rfd.getPosX()));
                    values.put(COLUMN_NAME_Y, Float.toString(rfd.getPosY()));
                    values.put(COLUMN_NAME_Z, Float.toString(rfd.getPosZ()));
                    values.put(COLUMN_NAME_LANDSCAPE, Integer.valueOf(rfd.getLandscape() ? 1 : 0));

                    String selection = COLUMN_NAME_TAG + " = ?";

                    String[] selectionArgs = {String.valueOf(rfd.getTAG())};
                    // Update the Parameter
                    if (db.update(TABLE_NAME, values, selection, selectionArgs) == 0)
                    {
                        // The Parameter doesn't exist, i will add it
                        if (db.insert(TABLE_NAME, null, values) > 0)
                        {
                            bRes =  true;
                        }
                    } else
                    {
                        bRes =  true;
                    }
                }
            } finally {
                m_LockCommandHolder.unlock();
            }

            return bRes;
        }

        public static Cursor load(Context context)
        {
            try
            {
                m_LockCommandHolder.lock();

               Cursor cursor = null;

                if(context != null)
                {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

                    LightSwitch ls = null;

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                _ID,
                                COLUMN_NAME_HOUSE_TAG,
                                COLUMN_NAME_TAG,
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

        public static RoomFragmentData get(Context context, long id) {
            try
            {
                m_LockCommandHolder.lock();

                RoomFragmentData rfd = new RoomFragmentData();

                if(context != null) {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                _ID,
                                COLUMN_NAME_HOUSE_TAG,
                                COLUMN_NAME_TAG,
                                COLUMN_NAME_X,
                                COLUMN_NAME_Y,
                                COLUMN_NAME_Z,
                                COLUMN_NAME_LANDSCAPE
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String selection = _ID + " = ?";

                    String[] selectionArgs = {String.valueOf(id)};

                    Cursor cursor = db.query(
                            TABLE_NAME,  // The table to query
                            projection,                               // The columns to return
                            selection,                                      // The columns for the WHERE clause
                            selectionArgs,                                      // The values for the WHERE clause
                            null,                                     // don't group the rows
                            null,                                     // don't filter by row groups
                            sortOrder                                 // The sort order
                    );
                    if ((cursor != null) && (cursor.getCount() > 0)) {
                        cursor.moveToFirst();
                        rfd.setID(cursor.getLong(cursor.getColumnIndex(_ID)));
                        rfd.setHouseTAG(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HOUSE_TAG)));
                        rfd.setTag(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TAG)));
                        rfd.setPosX(Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_X))));
                        rfd.setPosY(Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_Y))));
                        rfd.setPosZ(Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_Z))));
                        rfd.setLandscape((cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_LANDSCAPE)) == 0) ? false : true);

                        // Chiudo il cursore
                        cursor.close();
                    }

                }

                return rfd;
            }
            finally
            {
                m_LockCommandHolder.unlock();
            }
        }

        public static String getTag(Context context, long id) {

            try
            {
                m_LockCommandHolder.lock();

                String strTAG = "";

                if(context != null) {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                    COLUMN_NAME_TAG
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String selection = _ID + " = ?";

                    String[] selectionArgs = {String.valueOf(id)};

                    Cursor cursor = db.query(
                            TABLE_NAME,  // The table to query
                            projection,                               // The columns to return
                            selection,                                      // The columns for the WHERE clause
                            selectionArgs,                                      // The values for the WHERE clause
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

        public static boolean isTagPresent(Context context, String strTag) {

            try
            {
                m_LockCommandHolder.lock();

                boolean bRes = false;

                if(context != null) {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                    _ID
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    // Which row to get based on WHERE
                    String selection = COLUMN_NAME_TAG + " = ?";

                    String[] selectionArgs = {String.valueOf(strTag)};

                    Cursor cursor = db.query(
                            TABLE_NAME,  // The table to query
                            projection,                               // The columns to return
                            selection,                                      // The columns for the WHERE clause
                            selectionArgs,                                      // The values for the WHERE clause
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

        public static String getFirstTAG(Context context) {

            try
            {
                m_LockCommandHolder.lock();

                String strTAG = "";

                if(context != null) {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

                    // Define a projection that specifies which columns from the database
                    // you will actually use after this query.
                    String[] projection =
                            {
                                    COLUMN_NAME_TAG
                            };

                    // How you want the results sorted in the resulting Cursor
                    String sortOrder = "";

                    Cursor cursor = db.query(
                            TABLE_NAME,  // The table to query
                            projection,                               // The columns to return
                            null,                                      // The columns for the WHERE clause
                            null,                                      // The values for the WHERE clause
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
    }
}
