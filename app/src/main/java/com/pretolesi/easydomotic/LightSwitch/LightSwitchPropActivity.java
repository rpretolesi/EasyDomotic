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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easydomotic.CustomControls.EDEditText;
import com.pretolesi.easydomotic.LoadersUtils.Loaders;
import com.pretolesi.easydomotic.Orientation;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClient;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientProtocol;
import com.pretolesi.easydomotic.dialogs.DialogActionID;
import com.pretolesi.easydomotic.dialogs.DialogOriginID;
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
    private static final String LIGHT_SWITCH_ID = "Light_Switch_ID";
    private static final String LIGHT_SWITCH_DATA = "Light_Switch_Data";

 //   private CharSequence mTitle;

    private Spinner m_id_spn_room;
    private SimpleCursorAdapter m_SCAdapter;

    private EDEditText m_id_et_light_switch_name;
    private RadioButton m_id_rb_portrait;
    private RadioButton m_id_rb_landscape;
    private EditText m_id_et_position_x;
    private EditText m_id_et_position_y;
    private EditText m_id_et_position_z;
    private CheckBox m_id_lspa_cb_enable_tcp_ip_client_protocol;
    private Spinner m_id_lspa_spn_tcp_ip_client_protocol;

    private EDEditText m_id_lspa_et_protocol_field_1;
    private EDEditText m_id_lspa_et_protocol_field_2;
    private EDEditText m_id_lspa_et_protocol_field_3;
    private EDEditText m_id_lspa_et_protocol_field_4;
    private EDEditText m_id_lspa_et_protocol_field_5;

    private LightSwitchData m_lsd;
    private long m_lRoomIDParameter;
    private long m_lIDParameter;
    private LightSwitchData m_lsdParameter;
    private SimpleCursorAdapter m_TcpIpClientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.light_switch_property_activity);

        m_id_spn_room = (Spinner) findViewById(R.id.id_spn_room);
        m_id_et_light_switch_name = (EDEditText)findViewById(R.id.id_et_light_switch_name);
        m_id_et_light_switch_name.setInputLimit(LightSwitchData.TAGMinChar, LightSwitchData.TAGMaxChar);
        m_id_et_light_switch_name.setText(LightSwitchData.TAGDefaultValue);

        m_id_rb_portrait = (RadioButton)findViewById(R.id.id_rb_portrait);
        m_id_rb_landscape = (RadioButton)findViewById(R.id.id_rb_landscape);
        m_id_et_position_x = (EditText)findViewById(R.id.id_et_position_x);
        m_id_et_position_y = (EditText)findViewById(R.id.id_et_position_y);
        m_id_et_position_z = (EditText)findViewById(R.id.id_et_position_z);
        m_id_lspa_cb_enable_tcp_ip_client_protocol = (CheckBox)findViewById(R.id.id_lspa_cb_enable_tcp_ip_client_protocol);
        m_id_lspa_spn_tcp_ip_client_protocol = (Spinner)findViewById(R.id.id_lspa_spn_tcp_ip_client_protocol);
        m_id_lspa_spn_tcp_ip_client_protocol.setEnabled(false);

        m_id_lspa_cb_enable_tcp_ip_client_protocol.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
//                if (((CheckBox) v).isChecked()) {
//                }
                m_id_lspa_spn_tcp_ip_client_protocol.setEnabled(((CheckBox) v).isChecked());
                m_id_lspa_et_protocol_field_1.setEnabled(((CheckBox) v).isChecked());
                m_id_lspa_et_protocol_field_2.setEnabled(((CheckBox) v).isChecked());
                m_id_lspa_et_protocol_field_3.setEnabled(((CheckBox) v).isChecked());
                m_id_lspa_et_protocol_field_4.setEnabled(((CheckBox) v).isChecked());
                m_id_lspa_et_protocol_field_5.setEnabled(((CheckBox) v).isChecked());
            }
        });

        m_id_lspa_et_protocol_field_1 = (EDEditText)findViewById(R.id.id_lspa_et_protocol_field_1);
        m_id_lspa_et_protocol_field_1.setInputLimit(LightSwitchData.ProtTcpIpClientIDMinValue, LightSwitchData.ProtTcpIpClientIDMaxValue);
        m_id_lspa_et_protocol_field_1.setText(LightSwitchData.ProtTcpIpClientIDDefaulValue);
        m_id_lspa_et_protocol_field_1.setEnabled(false);
        m_id_lspa_et_protocol_field_2 = (EDEditText)findViewById(R.id.id_lspa_et_protocol_field_2);
        m_id_lspa_et_protocol_field_2.setInputLimit(LightSwitchData.ProtTcpIpClientValueOFFMinValue, LightSwitchData.ProtTcpIpClientValueOFFMaxValue);
        m_id_lspa_et_protocol_field_2.setText(LightSwitchData.ProtTcpIpClientValueOFFDefaulValue);
        m_id_lspa_et_protocol_field_2.setEnabled(false);
        m_id_lspa_et_protocol_field_3 = (EDEditText)findViewById(R.id.id_lspa_et_protocol_field_3);
        m_id_lspa_et_protocol_field_3.setInputLimit(LightSwitchData.ProtTcpIpClientValueOFFONMinValue, LightSwitchData.ProtTcpIpClientValueOFFONMaxValue);
        m_id_lspa_et_protocol_field_3.setText(LightSwitchData.ProtTcpIpClientValueOFFONDefaulValue);
        m_id_lspa_et_protocol_field_3.setEnabled(false);
        m_id_lspa_et_protocol_field_4 = (EDEditText)findViewById(R.id.id_lspa_et_protocol_field_4);
        m_id_lspa_et_protocol_field_4.setInputLimit(LightSwitchData.ProtTcpIpClientValueONOFFMinValue, LightSwitchData.ProtTcpIpClientValueONOFFMaxValue);
        m_id_lspa_et_protocol_field_4.setText(LightSwitchData.ProtTcpIpClientValueONOFFDefaulValue);
        m_id_lspa_et_protocol_field_4.setEnabled(false);
        m_id_lspa_et_protocol_field_5 = (EDEditText)findViewById(R.id.id_lspa_et_protocol_field_5);
        m_id_lspa_et_protocol_field_5.setInputLimit(LightSwitchData.ProtTcpIpClientValueONMinValue, LightSwitchData.ProtTcpIpClientValueONMaxValue);
        m_id_lspa_et_protocol_field_5.setText(LightSwitchData.ProtTcpIpClientValueONDefaulValue);
        m_id_lspa_et_protocol_field_5.setEnabled(false);

        setActionBar();

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        Intent intent = getIntent();
        m_lIDParameter = -1;
        if(intent != null) {
            m_lRoomIDParameter = intent.getLongExtra(ROOM_ID, -1);
            m_lIDParameter = intent.getLongExtra(LIGHT_SWITCH_ID, -1);
            m_lsdParameter = intent.getParcelableExtra(LightSwitchPropActivity.LIGHT_SWITCH_DATA);
        }

        m_id_lspa_spn_tcp_ip_client_protocol.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, TcpIpClientProtocol.Protocol.values()));

        m_SCAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[] {SQLContract.RoomEntry.COLUMN_NAME_TAG},
                new int[] {android.R.id.text1}, 0);

        m_id_spn_room.setAdapter(m_SCAdapter);

        m_TcpIpClientAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[] {SQLContract.TcpIpClientEntry.COLUMN_NAME_NAME},
                new int[] {android.R.id.text1}, 0);

        m_id_lspa_spn_tcp_ip_client_protocol.setAdapter(m_TcpIpClientAdapter);

        // Primo
        getLoaderManager().initLoader(Loaders.ROOM_LOADER_ID, null, this);
    }

    public void setActionBar() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.settings_title_section_edit_switch));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_light_switch_property_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Respond to the action bar's Up/Home button
            case R.id.id_item_menu_delete:
                // Delete Data
                delete(DialogOriginID.ORIGIN_MENU_BUTTON_ID);
                return true;

            case R.id.id_item_menu_save:
                // Save Data
                save(DialogOriginID.ORIGIN_MENU_BUTTON_ID);

                return true;
        }

        return super.onOptionsItemSelected(item);
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
            YesNoDialogFragment.newInstance(DialogOriginID.ORIGIN_BACK_BUTTON_ID,
                    DialogActionID.SAVE_ITEM_NOT_SAVED_CONFIRM_ID,
                    getString(R.string.text_yndf_title_data_not_saved),
                    getString(R.string.text_yndf_message_data_save_confirmation),
                    getString(R.string.text_yndf_btn_yes),
                    getString(R.string.text_yndf_btn_no)
            ).show(getFragmentManager(), "");

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, this.toString() + ": " + "onCreateLoader() id:" + id);
        if(id == Loaders.ROOM_LOADER_ID){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.RoomEntry.load();
                }
            };
        }

        if(id == Loaders.LIGHT_SWITCH_LOADER_ID){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    Cursor cursor;
                    if(m_lsdParameter != null){
                        cursor = SQLContract.LightSwitchEntry.loadFromLightSwitchData(m_lsdParameter);
                    } else {
                        cursor = SQLContract.LightSwitchEntry.load(m_lIDParameter, m_lRoomIDParameter);
                    }
                    return cursor;
                }
            };
        }

        if(id == Loaders.TCP_IP_CLIENT_LOADER_ID){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.TcpIpClientEntry.load();
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
            if(m_id_spn_room != null) {
                long lRoomID;
                if(m_lsdParameter != null){
                    lRoomID = m_lsdParameter.getRoomID();
                } else {
                    lRoomID = m_lRoomIDParameter;
                }
                for (int i = 0; i < m_id_spn_room.getCount(); i++) {
                    if (m_id_spn_room.getItemIdAtPosition(i) == lRoomID) {
                        m_id_spn_room.setSelection(i);
                        m_id_spn_room.setEnabled(false);
                    }
                }
            }
            // Secondo
            getLoaderManager().initLoader(Loaders.LIGHT_SWITCH_LOADER_ID, null, this);
        }

        if(loader.getId() == Loaders.LIGHT_SWITCH_LOADER_ID) {
            ArrayList<LightSwitchData> allsd = SQLContract.LightSwitchEntry.get(cursor);
            if(allsd != null && !allsd.isEmpty()){
                m_lsd = allsd.get(0);
            }

            // Terzo
            getLoaderManager().initLoader(Loaders.TCP_IP_CLIENT_LOADER_ID, null, this);
        }

        if(loader.getId() == Loaders.TCP_IP_CLIENT_LOADER_ID) {
            m_TcpIpClientAdapter.swapCursor(cursor);
            updateLightSwitch();
        }

        Log.d(TAG, this.toString() + ": " + "onLoadFinished() id: " + loader.getId());

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == Loaders.ROOM_LOADER_ID) {
            m_SCAdapter.swapCursor(null);
        }
        if(loader.getId() == Loaders.TCP_IP_CLIENT_LOADER_ID) {
            m_TcpIpClientAdapter.swapCursor(null);
        }

        Log.d(TAG, this.toString() + ": " + "onLoaderReset() id: " + loader.getId());
    }

    @Override
    public void onYesNoDialogFragmentClickListener(int iDialogOriginID, int iDialogActionID, boolean bYes, boolean bNo) {
        if(iDialogOriginID == DialogOriginID.ORIGIN_MENU_BUTTON_ID) {
            if(iDialogActionID == DialogActionID.SAVE_ITEM_ALREADY_EXSIST_CONFIRM_ID) {
                if(bYes) {
                    // Save ok, exit
                    saveLightSwitchData(iDialogOriginID);
                }
                if(bNo) {
                    // no action
                }
            }

            if(iDialogActionID == DialogActionID.DELETE_CONFIRM_ID) {
                if(bYes) {
                    // Delete
                    deleteLightSwitchData(iDialogOriginID);                }
                if(bNo) {
                    // No action...
                }
            }
        }

        if(iDialogOriginID == DialogOriginID.ORIGIN_BACK_BUTTON_ID) {
            if(iDialogActionID == DialogActionID.SAVE_ITEM_ALREADY_EXSIST_CONFIRM_ID) {
                if(bYes) {
                    // Save ok, exit
                    saveLightSwitchData(iDialogOriginID);
                }
                if(bNo) {
                    super.onBackPressed();
                }
            }
            if(iDialogActionID == DialogActionID.SAVE_ITEM_NOT_SAVED_CONFIRM_ID) {
                if(bYes) {
                    // Save
                    save(iDialogOriginID);
                }
                if(bNo) {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    public void onOkDialogFragmentClickListener(int iDialogOriginID, int iDialogActionID) {
        if(iDialogOriginID == DialogOriginID.ORIGIN_MENU_BUTTON_ID){
            if(iDialogActionID == DialogActionID.SAVING_OK_ID) {
                // Save ok, exit
                finish();
            }
            if(iDialogActionID == DialogActionID.DELETING_OK_ID){
                // Save ok, exit
                finish();
            }
        }
        if(iDialogOriginID == DialogOriginID.ORIGIN_BACK_BUTTON_ID){
            if(iDialogActionID == DialogActionID.SAVING_OK_ID) {
                // Save ok, exit
                super.onBackPressed();
            }
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
            if (m_id_et_position_x != null) {
                m_id_et_position_x.setText(Float.toString(m_lsd.getPosX()));
            }
            if (m_id_et_position_y != null) {
                m_id_et_position_y.setText(Float.toString(m_lsd.getPosY()));
            }
            if (m_id_et_position_z != null) {
                m_id_et_position_z.setText(Float.toString(m_lsd.getPosZ()));
            }
            if (m_id_lspa_spn_tcp_ip_client_protocol != null && m_id_lspa_cb_enable_tcp_ip_client_protocol != null) {
                for (int i = 0; i < m_id_lspa_spn_tcp_ip_client_protocol.getCount(); i++) {
                    Cursor value = (Cursor) m_id_lspa_spn_tcp_ip_client_protocol.getItemAtPosition(i);
                    if (value != null) {
                        long id = value.getLong(value.getColumnIndex("_id"));
                        if (id == m_lsd.getProtTcpIpClientID()) {
                            m_id_lspa_cb_enable_tcp_ip_client_protocol.setChecked(m_lsd.getProtTcpIpClientEnable());
                            m_id_lspa_spn_tcp_ip_client_protocol.setSelection(i);

                            m_id_lspa_spn_tcp_ip_client_protocol.setEnabled(m_lsd.getProtTcpIpClientEnable());
                            m_id_lspa_et_protocol_field_1.setEnabled(m_lsd.getProtTcpIpClientEnable());
                            m_id_lspa_et_protocol_field_2.setEnabled(m_lsd.getProtTcpIpClientEnable());
                            m_id_lspa_et_protocol_field_3.setEnabled(m_lsd.getProtTcpIpClientEnable());
                            m_id_lspa_et_protocol_field_4.setEnabled(m_lsd.getProtTcpIpClientEnable());
                            m_id_lspa_et_protocol_field_5.setEnabled(m_lsd.getProtTcpIpClientEnable());
                        }
                    }
                }
            }
            if (m_id_lspa_et_protocol_field_1 != null) {
                m_id_lspa_et_protocol_field_1.setText(Integer.toString(m_lsd.getProtTcpIpClientValueID()));
            }
            if (m_id_lspa_et_protocol_field_2 != null) {
                m_id_lspa_et_protocol_field_2.setText(Integer.toString(m_lsd.getProtTcpIpClientValueOFF()));
            }
            if (m_id_lspa_et_protocol_field_3 != null) {
                m_id_lspa_et_protocol_field_3.setText(Integer.toString(m_lsd.getProtTcpIpClientValueOFFON()));
            }
            if (m_id_lspa_et_protocol_field_4 != null) {
                m_id_lspa_et_protocol_field_4.setText(Integer.toString(m_lsd.getProtTcpIpClientValueONOFF()));
            }
            if (m_id_lspa_et_protocol_field_5 != null) {
                m_id_lspa_et_protocol_field_5.setText(Integer.toString(m_lsd.getProtTcpIpClientValueON()));
            }
        }
    }

    private void save(int iDialogOriginID) {
        if(!EDEditText.validateInputData(findViewById(android.R.id.content))){ return; }

        if(m_id_et_light_switch_name != null){
            long lRoomID;
            if(m_lsdParameter != null){
                lRoomID = m_lsdParameter.getRoomID();
            } else {
                lRoomID = m_lRoomIDParameter;
            }

            if (!SQLContract.LightSwitchEntry.isTagPresent(m_id_et_light_switch_name.getText().toString(), lRoomID)) {
                saveLightSwitchData(iDialogOriginID);
            } else {
                YesNoDialogFragment.newInstance(iDialogOriginID,
                        DialogActionID.SAVE_ITEM_ALREADY_EXSIST_CONFIRM_ID,
                        getString(R.string.text_yndf_title_light_switch_name_already_exist),
                        getString(R.string.text_yndf_message_light_switch_name_already_exist_confirmation),
                        getString(R.string.text_yndf_btn_yes),
                        getString(R.string.text_yndf_btn_no)
                ).show(getFragmentManager(), "");
            }
        }
    }

    private void saveLightSwitchData(int iDialogOriginID){
        if (m_lsd == null) {
            m_lsd = new LightSwitchData();
        }

        if((m_id_spn_room == null) || (m_id_spn_room.getSelectedItemId() == AdapterView.INVALID_ROW_ID)){
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.ROOM_ERROR_ID, getString(R.string.text_odf_title_room_data_not_present), getString(R.string.text_odf_message_room_data_not_present), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return ;
        }
        if (m_id_et_light_switch_name == null || m_id_et_light_switch_name.getText().toString().equals("")) {
            OkDialogFragment odf = OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.LIGHT_SWITCH_ERROR_NAME, getString(R.string.text_odf_title_light_switch_name_error), getString(R.string.text_odf_message_light_switch_name_not_valid), getString(R.string.text_odf_message_ok_button));
            odf.show(getFragmentManager(), "");
            return ;
        }

        if(getOrientation() == Orientation.UNDEFINED ) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.ORIENTATION_ERROR_ID, getString(R.string.text_odf_title_orientation_not_set), getString(R.string.text_odf_message_orientation_not_set), getString(R.string.text_odf_message_ok_button))
            .show(getFragmentManager(), "");
            return ;
        }

        // Set the selected Room
        m_lsd.setRoomID(m_id_spn_room.getSelectedItemId());

        if (m_id_et_light_switch_name != null) {
            m_lsd.setTAG(m_id_et_light_switch_name.getText().toString());
        }

        if(getOrientation() == Orientation.PORTRAIT ){
            m_lsd.setLandscape(false);
        }
        if(getOrientation() == Orientation.LANDSCAPE ){
            m_lsd.setLandscape(true);
        }
        try {
            if (m_id_et_position_x != null) {
                m_lsd.setPosX(Float.parseFloat(m_id_et_position_x.getText().toString()));
            }
            if (m_id_et_position_y != null) {
                m_lsd.setPosY(Float.parseFloat(m_id_et_position_y.getText().toString()));
            }
            if (m_id_et_position_z != null) {
                m_lsd.setPosZ(Float.parseFloat(m_id_et_position_z.getText().toString()));
            }
        } catch (Exception ex) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.POSITION_ERROR_ID, getString(R.string.text_odf_title_position_not_valid), getString(R.string.text_odf_message_position_not_valid), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return ;
        }

        if(m_id_lspa_cb_enable_tcp_ip_client_protocol != null) {
            m_lsd.setProtTcpIpClientEnable(m_id_lspa_cb_enable_tcp_ip_client_protocol.isChecked());
        }

        if(m_id_lspa_spn_tcp_ip_client_protocol != null) {
            m_lsd.setProtTcpIpClientID(m_id_lspa_spn_tcp_ip_client_protocol.getSelectedItemId());
        }

        try {
            if (m_id_lspa_et_protocol_field_1 != null) {
                m_lsd.setProtTcpIpClientValueID(Integer.parseInt(m_id_lspa_et_protocol_field_1.getText().toString()));
            }
            if (m_id_lspa_et_protocol_field_2 != null) {
                m_lsd.setProtTcpIpClientValueOFF(Integer.parseInt(m_id_lspa_et_protocol_field_2.getText().toString()));
            }
            if (m_id_lspa_et_protocol_field_3 != null) {
                m_lsd.setProtTcpIpClientValueOFFON(Integer.parseInt(m_id_lspa_et_protocol_field_3.getText().toString()));
            }
            if (m_id_lspa_et_protocol_field_4 != null) {
                m_lsd.setProtTcpIpClientValueONOFF(Integer.parseInt(m_id_lspa_et_protocol_field_4.getText().toString()));
            }
            if (m_id_lspa_et_protocol_field_5 != null) {
                m_lsd.setProtTcpIpClientValueON(Integer.parseInt(m_id_lspa_et_protocol_field_5.getText().toString()));
            }
        } catch (Exception ex) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.POSITION_ERROR_ID, getString(R.string.text_odf_title_protocol_not_valid), getString(R.string.text_odf_message_protocol_not_valid), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return ;
        }

        if(SQLContract.LightSwitchEntry.save(m_lsd)){
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_OK_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_ok), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
        } else {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
        }
    }

    private void delete(int iDialogOriginID){
        YesNoDialogFragment.newInstance(
                iDialogOriginID,
                DialogActionID.DELETE_CONFIRM_ID,
                getString(R.string.text_yndf_title_data_delete),
                getString(R.string.text_yndf_message_data_delete_confirmation),
                getString(R.string.text_yndf_btn_yes),
                getString(R.string.text_yndf_btn_no)
        ).show(getFragmentManager(), "");
    }
    private void deleteLightSwitchData(int iDialogOriginID) {
        if(m_lsd != null) {
            SQLContract.LightSwitchEntry.delete(m_lsd.getID(), m_lsd.getRoomID());
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.DELETING_OK_ID, getString(R.string.text_odf_title_deleting), getString(R.string.text_odf_message_deleting_ok), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
        }
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


    public static Intent makeLightSwitchPropActivity(Context context, long lRoomID, long lID) {
        Intent intent = new Intent();
        intent.setClass(context, LightSwitchPropActivity.class);
        intent.putExtra(LightSwitchPropActivity.ROOM_ID, lRoomID);
        intent.putExtra(LightSwitchPropActivity.LIGHT_SWITCH_ID, lID);
        return intent;
    }

    public static Intent makeLightSwitchPropActivity(Context context, LightSwitchData lsd) {
        Intent intent = new Intent();
        intent.setClass(context, LightSwitchPropActivity.class);
        intent.putExtra(LightSwitchPropActivity.LIGHT_SWITCH_DATA, lsd);
        return intent;
    }

}
