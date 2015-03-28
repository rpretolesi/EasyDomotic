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
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.RoomFragmentData;

/**
 *
 */
public class LightSwitchPropActivity extends Activity  implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "LightSwitchPropActivity";

    private static final int ROOM_LOADER_ID = 1;
    private static final String ROOM_ID = "Room_ID";
    private long m_lRoomID;
    private static final int LIGHT_SWITCH_LOADER_ID = 2;
    private static final String LIGHT_SWITCH_ID = "Light_Switch_ID";
    private long m_lID;

 //   private CharSequence mTitle;

    private Spinner m_id_spn_room;
    private SimpleCursorAdapter m_SCAdapter;

    private EditText m_id_et_light_switch_name;
    private RadioButton m_id_rb_portrait;
    private RadioButton m_id_rb_landscape;
    private LightSwitchData m_lsd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.light_switch_property_activity);
        m_id_spn_room = (Spinner) findViewById(R.id.id_spn_room);
        m_id_et_light_switch_name = (EditText)findViewById(R.id.id_et_light_switch_name);
        m_id_rb_portrait = (RadioButton)findViewById(R.id.id_rb_portrait);
        m_id_rb_landscape = (RadioButton)findViewById(R.id.id_rb_landscape);

        setActionBar();
/*
        mTitle = getTitle();
        restoreActionBar();
*/
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        Intent intent = getIntent();
        if(intent != null)
        {
            m_lRoomID = intent.getLongExtra(ROOM_ID, 0);
            m_lID = intent.getLongExtra(LIGHT_SWITCH_ID, 0);

        }

        m_SCAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[] {SQLContract.RoomEntry.COLUMN_NAME_TAG},
                new int[] {android.R.id.text1}, 0);

        m_id_spn_room.setAdapter(m_SCAdapter);

        getLoaderManager().initLoader(ROOM_LOADER_ID, null, this);
        getLoaderManager().initLoader(LIGHT_SWITCH_LOADER_ID, null, this);
    }

    public void setActionBar() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
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
        Log.d(TAG, this.toString() + ": " + "onCreateLoader() id:" + id);
        if(id == ROOM_LOADER_ID){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {

                    return SQLContract.RoomEntry.load(getContext());
                }
            };
        }

        if(id == LIGHT_SWITCH_LOADER_ID){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.LightSwitchEntry.load(getContext(), m_lID, m_lRoomID);
                }
            };
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // The list should now be shown.
        if(loader.getId() == ROOM_LOADER_ID) {
            m_SCAdapter.swapCursor(cursor);
        }
        if(loader.getId() == LIGHT_SWITCH_LOADER_ID) {
            m_lsd = SQLContract.LightSwitchEntry.get(cursor);
        }

        updateLightSwitch();

        Log.d(TAG, this.toString() + ": " + "onLoadFinished() id: " + loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == ROOM_LOADER_ID) {
            m_SCAdapter.swapCursor(null);
        }

        Log.d(TAG, this.toString() + ": " + "onLoaderReset() id: " + loader.getId());
    }

    private void updateLightSwitch(){
        if(m_lsd != null) {
            if(m_id_et_light_switch_name != null){
                m_id_et_light_switch_name.setText(m_lsd.getTag());
            }

            if (m_lsd.getLandscape()) {
                if (m_id_rb_landscape != null) {
                    m_id_rb_landscape.setChecked(true);
                }
                if (m_id_rb_portrait != null) {
                    m_id_rb_portrait.setChecked(false);
                }
            } else {
                if (m_id_rb_landscape != null) {
                    m_id_rb_landscape.setChecked(false);
                }
                if (m_id_rb_portrait != null) {
                    m_id_rb_portrait.setChecked(true);
                }
            }
        }
    }


    public static Intent makeLightSwitchPropActivity(Context context, long lRoomID, long lID)
    {
        Intent intent = new Intent();
        intent.setClass(context, LightSwitchPropActivity.class);
        intent.putExtra(LightSwitchPropActivity.ROOM_ID, lRoomID);
        intent.putExtra(LightSwitchPropActivity.LIGHT_SWITCH_ID, lID);
        return intent;
    }
}
