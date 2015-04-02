package com.pretolesi.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import com.pretolesi.easydomotic.LightSwitch.LightSwitch;
import com.pretolesi.easydomotic.LightSwitch.LightSwitchData;
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
        public static final String COLUMN_NAME_TAG = "TAG";
        public static final String COLUMN_NAME_ROOM_ID = "Room_ID";
        public static final String COLUMN_NAME_X = "X";
        public static final String COLUMN_NAME_Y = "Y";
        public static final String COLUMN_NAME_Z = "Z";
        public static final String COLUMN_NAME_LANDSCAPE = "Landscape";

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
                        COLUMN_NAME_LANDSCAPE + INT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;


        public static boolean save(Context context, ArrayList<LightSwitchData> allsd)  {

            boolean bRes = true;
            try
            {
                m_LockCommandHolder.lock();
                if(context != null && allsd != null) {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

                    ContentValues values = new ContentValues();
                    for(LightSwitchData lsdTemp:allsd){
                        if(lsdTemp != null) {
                            values.put(COLUMN_NAME_TAG, String.valueOf(lsdTemp.getTag()));
                            values.put(COLUMN_NAME_ROOM_ID, lsdTemp.getRoomID());
                            values.put(COLUMN_NAME_X, Float.toString(lsdTemp.getPosX()));
                            values.put(COLUMN_NAME_Y, Float.toString(lsdTemp.getPosY()));
                            values.put(COLUMN_NAME_Z, Float.toString(lsdTemp.getPosZ()));
                            values.put(COLUMN_NAME_LANDSCAPE, Integer.valueOf(lsdTemp.getLandscape() ? 1 : 0));

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

        public static boolean save(Context context,LightSwitchData lsd)  {

            boolean bRes = true;
            try
            {
                m_LockCommandHolder.lock();
                if(context != null && lsd != null) {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

                    ContentValues values = new ContentValues();
                    values.put(COLUMN_NAME_TAG, String.valueOf(lsd.getTag()));
                    values.put(COLUMN_NAME_ROOM_ID, lsd.getRoomID());
                    values.put(COLUMN_NAME_X, Float.toString(lsd.getPosX()));
                    values.put(COLUMN_NAME_Y, Float.toString(lsd.getPosY()));
                    values.put(COLUMN_NAME_Z, Float.toString(lsd.getPosZ()));
                    values.put(COLUMN_NAME_LANDSCAPE, Integer.valueOf(lsd.getLandscape() ? 1 : 0));

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

        public static Cursor load(Context context, long lID, long lRoomID)
        {
            try
            {
                m_LockCommandHolder.lock();

                Cursor cursor = null;

                if(context != null)
                {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

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
                                    COLUMN_NAME_LANDSCAPE
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

        public static Cursor load(Context context, long lRoomID)
        {
            try
            {
                m_LockCommandHolder.lock();

                Cursor cursor = null;

                if(context != null)
                {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

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
                                    COLUMN_NAME_LANDSCAPE
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

        public static boolean delete(Context context, long lID, long lRoomID)
        {
            try
            {
                m_LockCommandHolder.lock();

                if(context != null)
                {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();


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

        public static boolean isTagPresent(Context context, String strTag, long lRoomID) {

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
                            bSaved,
                            false,
                            cursor.getLong(cursor.getColumnIndex(_ID)),
                            cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_ROOM_ID)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TAG)),
                            Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_X))),
                            Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_Y))),
                            Float.parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_Z))),
                            ((cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_LANDSCAPE)) == 0) ? false : true)
                    );
                    allsd.add(lsd);
                }
            }
            return allsd;
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

        public static long save(Context context, RoomFragmentData rfd)  {

            long id = -1;
            try
            {
                m_LockCommandHolder.lock();
                if(context != null && rfd != null) {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

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

        public static Cursor load(Context context)
        {
            try
            {
                m_LockCommandHolder.lock();

               Cursor cursor = null;

                if(context != null)
                {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

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

        public static Cursor load(Context context, long id) {
            try
            {
                m_LockCommandHolder.lock();

                Cursor cursor = null;

                if(context != null) {
                    SQLiteDatabase db = SQLHelper.getInstance(context).getDB();

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

        public static String getTag(Context context, long id) {

            try
            {
                m_LockCommandHolder.lock();

                String strTAG = null;

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
