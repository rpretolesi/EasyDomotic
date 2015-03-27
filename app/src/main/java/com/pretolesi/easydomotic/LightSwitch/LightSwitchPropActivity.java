package com.pretolesi.easydomotic.LightSwitch;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easydomotic.R;

/**
 *
 */
public class LightSwitchPropActivity extends Activity  implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "LightSwitchPropActivity";

    public static final String ROOM_TAG = "Room_TAG";
    public static final String LIGHT_SWITCH_TAG = "Light_Switch_TAG";

    private CharSequence mTitle;

    private EditText m_id_et_light_switch_name;
    private RadioButton m_id_rb_portrait;
    private RadioButton m_id_rb_landscape;
    private LightSwitchData m_lsd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.light_switch_property_activity);

        m_id_et_light_switch_name = (EditText)findViewById(R.id.id_et_light_switch_name);
        m_id_rb_portrait = (RadioButton)findViewById(R.id.id_rb_portrait);
        m_id_rb_landscape = (RadioButton)findViewById(R.id.id_rb_landscape);

        mTitle = getTitle();
        restoreActionBar();

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        boolean bNewLightSwitch = true;
        finire qui... e controllare il nome qui
        Intent intent = getIntent();
        if(intent != null)
        {
            String strRoomTAG = intent.getStringExtra(ROOM_TAG);
            String strLightSwitchTAG = intent.getStringExtra(LIGHT_SWITCH_TAG);
            if(!strRoomTAG.equals("")){
                bNewLightSwitch = false;
                getLoaderManager().initLoader(0, null, this);
            }
            if(!strRoomTAG.equals("") && !strLightSwitchTAG.equals("")){
                bNewLightSwitch = false;
                getLoaderManager().initLoader(0, null, this);
            }
        } else {
            Toast.makeText(this, R.string.text_toast_room_name_not_valid, Toast.LENGTH_LONG).show();
            finish();
        }
        if(bNewLightSwitch){
            LightSwitchData lsd = new LightSwitchData(false, false, -1, "", "", 30, 30, 0, false);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_light_switch_property_activity, menu);
//            restoreActionBar();

//        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*
        switch (item.getItemId()) {

                // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, this.toString() + ": " + "onCreateLoader()");
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                return SQLContract.RoomEntry.load(getContext());
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // The list should now be shown.
        m_lsd = getLightSwitchData(loader, cursor);
        updateLightSwitch(m_lsd);

        Log.d(TAG, this.toString() + ": " + "onLoadFinished()");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, this.toString() + ": " + "onLoaderReset()");
    }

    private LightSwitchData getLightSwitchData(Loader<Cursor> loader, Cursor cursor){
        LightSwitchData lsd = null;
        if(loader.getId() == 0) {
            if (cursor != null) {
                lsd = new LightSwitchData(
                        true,
                        false,
                        cursor.getLong(cursor.getColumnIndex(SQLContract.LightSwitchEntry._ID)),
                        cursor.getString(cursor.getColumnIndex(SQLContract.LightSwitchEntry.COLUMN_NAME_ROOM_TAG)),
                        cursor.getString(cursor.getColumnIndex(SQLContract.LightSwitchEntry.COLUMN_NAME_TAG)),
                        Float.parseFloat(cursor.getString(cursor.getColumnIndex(SQLContract.LightSwitchEntry.COLUMN_NAME_X))),
                        Float.parseFloat(cursor.getString(cursor.getColumnIndex(SQLContract.LightSwitchEntry.COLUMN_NAME_Y))),
                        Float.parseFloat(cursor.getString(cursor.getColumnIndex(SQLContract.LightSwitchEntry.COLUMN_NAME_Z))),
                        ((cursor.getInt(cursor.getColumnIndex(SQLContract.LightSwitchEntry.COLUMN_NAME_LANDSCAPE)) == 0) ? false : true));

            }
        }

        return lsd;
    }

    private void updateLightSwitch(LightSwitchData lsd){
        if(m_id_et_light_switch_name != null){
            m_id_et_light_switch_name.setText(lsd.getTag());
        }
        if(lsd.getLandscape()){
            if(m_id_rb_landscape != null){
                m_id_rb_landscape.setChecked(true);
            }
            if(m_id_rb_portrait != null){
                m_id_rb_portrait.setChecked(false);
            }
        } else {
            if(m_id_rb_landscape != null){
                m_id_rb_landscape.setChecked(false);
            }
            if(m_id_rb_portrait != null){
                m_id_rb_portrait.setChecked(true);
            }
        }
    }


    public static Intent makeLightSwitchPropActivity(Context context, String strRoomTAG, String strLightSwitchTAG)
    {
        Intent intent = new Intent();
        intent.setClass(context, LightSwitchPropActivity.class);
        intent.putExtra(LightSwitchPropActivity.ROOM_TAG, strRoomTAG);
        intent.putExtra(LightSwitchPropActivity.LIGHT_SWITCH_TAG, strLightSwitchTAG);
        return intent;
    }
}
