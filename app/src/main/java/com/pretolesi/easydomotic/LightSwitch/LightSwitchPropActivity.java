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
import com.pretolesi.easydomotic.LoadersUtils.Loaders;
import com.pretolesi.easydomotic.Orientation;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.dialogs.OkDialogFragment;
import com.pretolesi.easydomotic.dialogs.YesNoDialogFragment;

import java.util.ArrayList;

/**
 *
 */
public class LightSwitchPropActivity extends Activity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OkDialogFragment.OkDialogFragmentCallbacks,
        YesNoDialogFragment.YesNoDialogFragmentCallbacks{
    private static final String TAG = "LightSwitchPropActivity";

    private static final String ROOM_ID = "Room_ID";
    private long m_lRoomID;
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

        getLoaderManager().initLoader(Loaders.ROOM_LOADER_ID, null, this);
        getLoaderManager().initLoader(Loaders.LIGHT_SWITCH_LOADER_ID, null, this);
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

        switch (item.getItemId()) {

            // Respond to the action bar's Up/Home button
            case R.id.id_item_menu_delete:
                // Delete Data
                deleteLightSwitchData();
                return true;

            case R.id.id_item_menu_save:
                // Save Data
                saveLightSwitchData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, this.toString() + ": " + "onCreateLoader() id:" + id);
        if(id == Loaders.ROOM_LOADER_ID){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.RoomEntry.load(getContext());
                }
            };
        }

        if(id == Loaders.LIGHT_SWITCH_LOADER_ID){
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
        if(loader.getId() == Loaders.ROOM_LOADER_ID) {
            m_SCAdapter.swapCursor(cursor);
            for(int i = 0; i < m_id_spn_room.getCount(); i++){
                if(m_id_spn_room.getItemIdAtPosition(i) == m_lRoomID){
                    m_id_spn_room.setSelection(i);
                }
            }
         }
        if(loader.getId() == Loaders.LIGHT_SWITCH_LOADER_ID) {
            ArrayList<LightSwitchData> allsd = SQLContract.LightSwitchEntry.get(cursor);
            if(allsd != null && !allsd.isEmpty()){
                m_lsd = allsd.get(0);
                updateLightSwitch();
            }
        }

       Log.d(TAG, this.toString() + ": " + "onLoadFinished() id: " + loader.getId());

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == Loaders.ROOM_LOADER_ID) {
            m_SCAdapter.swapCursor(null);
        }

        Log.d(TAG, this.toString() + ": " + "onLoaderReset() id: " + loader.getId());
    }

    @Override
    public void onOkDialogFragmentClickListener(int dlgID) {
        if(dlgID == OkDialogFragment.SAVING_OK_ID){
            // Save ok, exit
            finish();
        }
        if(dlgID == OkDialogFragment.DELETING_OK_ID){
            // Save ok, exit
            finish();
        }
    }

    @Override
    public void onYesNoDialogFragmentClickListener(int dlgID, boolean bYes, boolean bNo) {
        if(dlgID == YesNoDialogFragment.SAVE_CONFIRM_ID || dlgID == YesNoDialogFragment.SAVE_CONFIRM_FROM_BACK_BUTTON_ID) {
            if(bYes) {
                // Save ok, exit
                saveLightSwitchData();
            }
            if(bNo) {
                if(dlgID == YesNoDialogFragment.SAVE_CONFIRM_ID) {
                    finish();
                }

                if(dlgID == YesNoDialogFragment.SAVE_CONFIRM_FROM_BACK_BUTTON_ID) {
                    super.onBackPressed();
                }
            }
        }
        if(dlgID == YesNoDialogFragment.DELETE_CONFIRM_ID) {
            if(bYes) {
                // Delete ok, exit
                SQLContract.LightSwitchEntry.delete(this, m_lID, m_lRoomID);
                OkDialogFragment.newInstance(OkDialogFragment.DELETING_OK_ID, getString(R.string.text_odf_title_deleting), getString(R.string.text_odf_message_deleting_ok), getString(R.string.text_odf_message_ok_button))
                        .show(getFragmentManager(), "");
            }
            if(bNo) {
                // No action...
            }
        }
    }

    @Override
    public void onBackPressed() {
        boolean bAskForSave = false;
        if (m_lsd != null) {
            if(!m_lsd.getSaved()){
                bAskForSave = true;
            }
        } else {
            bAskForSave = true;
        }
        if(bAskForSave){
            YesNoDialogFragment.newInstance(
                    YesNoDialogFragment.SAVE_CONFIRM_FROM_BACK_BUTTON_ID,
                    getString(R.string.text_yndf_title_data_not_saved),
                    getString(R.string.text_yndf_message_data_save_confirmation),
                    getString(R.string.text_yndf_btn_yes),
                    getString(R.string.text_yndf_btn_no)
            ).show(getFragmentManager(), "");
        } else {
            super.onBackPressed();
        }
    }

    private void updateLightSwitch() {
        if (m_lsd != null) {
            if (m_id_et_light_switch_name != null) {
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

    private void saveLightSwitchData(){
        if(!isLightSwitchTagValid()){
            return ;
        }
        if(getOrientation() == Orientation.UNDEFINED ) {
            OkDialogFragment.newInstance(2, getString(R.string.text_odf_title_orientation_not_set), getString(R.string.text_odf_message_orientation_not_set), getString(R.string.text_odf_message_ok_button))
            .show(getFragmentManager(), "");
            return ;
        }

        if (m_lsd == null) {
            m_lsd = new LightSwitchData();
        }
        if (m_id_et_light_switch_name != null) {
            m_lsd.setTAG(m_id_et_light_switch_name.getText().toString());
        }

        if(getOrientation() == Orientation.PORTRAIT ){
            m_lsd.setLandscape(false);
        }
        if(getOrientation() == Orientation.LANDSCAPE ){
            m_lsd.setLandscape(true);
        }
        if(SQLContract.LightSwitchEntry.save(this,m_lsd)){
            OkDialogFragment.newInstance(OkDialogFragment.SAVING_OK_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_ok), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
        } else {
            OkDialogFragment.newInstance(OkDialogFragment.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
        }
    }

    private void deleteLightSwitchData(){
        YesNoDialogFragment.newInstance(
                YesNoDialogFragment.DELETE_CONFIRM_ID,
                getString(R.string.text_yndf_title_data_delete),
                getString(R.string.text_yndf_message_data_delete_confirmation),
                getString(R.string.text_yndf_btn_yes),
                getString(R.string.text_yndf_btn_no)
        ).show(getFragmentManager(), "");
    }

    private boolean isLightSwitchTagValid() {
        if(m_lRoomID > 0) {
            if (m_id_et_light_switch_name != null && !m_id_et_light_switch_name.getText().toString().equals("")) {
                if (!SQLContract.LightSwitchEntry.isTagPresent(this, m_id_et_light_switch_name.getText().toString(), m_lRoomID)) {
                    return true;
                } else {
                    OkDialogFragment odf = OkDialogFragment.newInstance(2, getString(R.string.text_odf_title_light_switch_name_error), getString(R.string.text_odf_message_light_switch_name_already_exist), getString(R.string.text_odf_message_ok_button));
                    odf.show(getFragmentManager(), "");
                }
            } else {
                OkDialogFragment odf = OkDialogFragment.newInstance(2, getString(R.string.text_odf_title_light_switch_name_error), getString(R.string.text_odf_message_light_switch_name_not_valid), getString(R.string.text_odf_message_ok_button));
                odf.show(getFragmentManager(), "");
            }
        } else {
            OkDialogFragment odf = OkDialogFragment.newInstance(2, getString(R.string.text_odf_title_light_switch_name_error), getString(R.string.text_odf_message_light_switch_name_not_valid), getString(R.string.text_odf_message_ok_button));
            odf.show(getFragmentManager(), "");
        }
        return false;
    }

    private Orientation getOrientation() {
        if (m_id_rb_landscape != null && m_id_rb_portrait != null) {
            if(m_id_rb_landscape.isChecked() && !m_id_rb_portrait.isChecked()) {
                return Orientation.LANDSCAPE;
            }
            if(!m_id_rb_landscape.isChecked() && m_id_rb_portrait.isChecked()) {
                return Orientation.PORTRAIT;
            }
        }

        return Orientation.UNDEFINED;
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
