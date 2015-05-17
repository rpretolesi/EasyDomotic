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
import com.pretolesi.easydomotic.BaseValue.BaseValueData;
import com.pretolesi.easydomotic.CustomControls.NumericDataType;
import com.pretolesi.easydomotic.CustomControls.NumericEditText;
import com.pretolesi.easydomotic.CustomControls.StringEditText;
import com.pretolesi.easydomotic.LoadersUtils.Loaders;
import com.pretolesi.easydomotic.Orientation;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClientData;
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
    private static final String TAG = "LightSwitchPropAct";

    private static final String ROOM_ID = "Room_ID";
    private static final String LIGHT_SWITCH_ID = "Light_Switch_ID";
    private static final String LIGHT_SWITCH_DATA = "Light_Switch_Data";

 //   private CharSequence mTitle;

    private Spinner m_id_spn_room;
    private SimpleCursorAdapter m_SCAdapter;

    private StringEditText m_id_et_light_switch_name;
    private EditText m_id_et_position_x;
    private EditText m_id_et_position_y;
    private EditText m_id_et_position_z;
    private RadioButton m_id_rb_portrait;
    private RadioButton m_id_rb_landscape;

    private CheckBox m_id_cb_enable_tcp_ip_client_protocol;
    private Spinner m_id_spn_tcp_ip_client_protocol;

    private NumericEditText m_id_et_protocol_ui;
    private NumericEditText m_id_et_protocol_addr_value;

    private Spinner m_id_spn_protocol_data_type;

    private NumericEditText m_id_et_write_value_off;
    private NumericEditText m_id_et_write_value_off_on;
    private NumericEditText m_id_et_write_value_on_off;
    private NumericEditText m_id_et_write_value_on;

    private BaseValueData m_bvd;
    private long m_lRoomIDParameter;
    private long m_lIDParameter;
    private BaseValueData m_bvdParameter;
    private SimpleCursorAdapter m_TcpIpClientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.light_switch_property_activity);

        m_id_spn_room = (Spinner) findViewById(R.id.id_spn_room);
        m_id_et_light_switch_name = (StringEditText)findViewById(R.id.id_et_name);
        m_id_et_light_switch_name.setInputLimit(BaseValueData.TAGMinChar, BaseValueData.TAGMaxChar);
        m_id_et_light_switch_name.setText(BaseValueData.TAGDefaultValue);

        m_id_rb_portrait = (RadioButton)findViewById(R.id.id_rb_portrait);
        m_id_rb_portrait.setChecked(true);
        m_id_rb_landscape = (RadioButton)findViewById(R.id.id_rb_landscape);
        m_id_rb_landscape.setChecked(false);
        m_id_et_position_x = (EditText)findViewById(R.id.id_et_position_x);
        m_id_et_position_y = (EditText)findViewById(R.id.id_et_position_y);
        m_id_et_position_z = (EditText)findViewById(R.id.id_et_position_z);
        m_id_cb_enable_tcp_ip_client_protocol = (CheckBox)findViewById(R.id.id_cb_enable_tcp_ip_client_protocol);
        m_id_cb_enable_tcp_ip_client_protocol.setEnabled(false);
        m_id_spn_tcp_ip_client_protocol = (Spinner)findViewById(R.id.id_spn_tcp_ip_client_protocol);
        m_id_spn_tcp_ip_client_protocol.setEnabled(false);

        m_id_cb_enable_tcp_ip_client_protocol.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                m_id_spn_tcp_ip_client_protocol.setEnabled(((CheckBox) v).isChecked());
                m_id_et_protocol_ui.setEnabled(((CheckBox) v).isChecked());
                m_id_et_protocol_addr_value.setEnabled(((CheckBox) v).isChecked());
                m_id_et_write_value_off.setEnabled(((CheckBox) v).isChecked());
                m_id_et_write_value_off_on.setEnabled(((CheckBox) v).isChecked());
                m_id_et_write_value_on_off.setEnabled(((CheckBox) v).isChecked());
                m_id_et_write_value_on.setEnabled(((CheckBox) v).isChecked());
            }
        });

        m_id_et_protocol_ui = (NumericEditText)findViewById(R.id.id_et_protocol_ui);
        m_id_et_protocol_ui.setInputLimit(BaseValueData.ProtTcpIpClientValueIDMinValue, BaseValueData.ProtTcpIpClientValueIDMaxValue);
        m_id_et_protocol_ui.setText(BaseValueData.ProtTcpIpClientValueIDDefaulValue);
        m_id_et_protocol_ui.setEnabled(false);
        m_id_et_protocol_addr_value = (NumericEditText)findViewById(R.id.id_et_protocol_addr_value);
        m_id_et_protocol_addr_value.setInputLimit(BaseValueData.ProtTcpIpClientValueAddressMin, BaseValueData.ProtTcpIpClientValueAddressMax);
        m_id_et_protocol_addr_value.setText(BaseValueData.ProtTcpIpClientValueAddressDefaul);
        m_id_et_protocol_addr_value.setEnabled(false);
        m_id_spn_protocol_data_type = (Spinner)findViewById(R.id.id_spn_protocol_data_type);
        m_id_spn_protocol_data_type.setSelection(BaseValueData.ProtTcpIpClientValueDataTypeDefaul);

        m_id_et_write_value_off = (NumericEditText)findViewById(R.id.id_et_write_value_off);
        m_id_et_write_value_off.setText(BaseValueData.WriteValueOFFDefault);
        m_id_et_write_value_off.setEnabled(false);
        m_id_et_write_value_off_on = (NumericEditText)findViewById(R.id.id_et_write_value_off_on);
        m_id_et_write_value_off_on.setText(BaseValueData.WriteValueOFFONDefault);
        m_id_et_write_value_off_on.setEnabled(false);
        m_id_et_write_value_on_off = (NumericEditText)findViewById(R.id.id_et_write_value_on_off);
        m_id_et_write_value_on_off.setText(BaseValueData.WriteValueONOFFDefault);
        m_id_et_write_value_on_off.setEnabled(false);
        m_id_et_write_value_on = (NumericEditText)findViewById(R.id.id_et_write_value_on);
        m_id_et_write_value_on.setText(BaseValueData.WriteValueONDefault);
        m_id_et_write_value_on.setEnabled(false);

        setActionBar();

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        Intent intent = getIntent();
        m_lIDParameter = -1;
        if(intent != null) {
            m_lRoomIDParameter = intent.getLongExtra(ROOM_ID, -1);
            m_lIDParameter = intent.getLongExtra(LIGHT_SWITCH_ID, -1);
            m_bvdParameter = intent.getParcelableExtra(LightSwitchPropActivity.LIGHT_SWITCH_DATA);
        }

        m_id_spn_tcp_ip_client_protocol.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, TCPIPClientData.Protocol.values()));

        m_SCAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[] {SQLContract.RoomEntry.COLUMN_NAME_TAG},
                new int[] {android.R.id.text1}, 0);
        m_id_spn_room.setAdapter(m_SCAdapter);

        m_id_spn_protocol_data_type.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, NumericDataType.DataType.values()));

        m_TcpIpClientAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[] {SQLContract.TcpIpClientEntry.COLUMN_NAME_NAME},
                new int[] {android.R.id.text1}, 0);
        m_id_spn_tcp_ip_client_protocol.setAdapter(m_TcpIpClientAdapter);

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
        if (m_bvd != null) {
            if(!m_bvd.getSaved()){
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
        // Log.d(TAG, this.toString() + ": " + "onCreateLoader() id:" + id);
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
                    if(m_bvdParameter != null){
                        cursor = SQLContract.BaseValueEntry.loadFromBaseValueData(m_bvdParameter);
                    } else {
                        cursor = SQLContract.BaseValueEntry.load(m_lIDParameter, m_lRoomIDParameter);
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
                if(m_bvdParameter != null){
                    lRoomID = m_bvdParameter.getRoomID();
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
            ArrayList<BaseValueData> albve = SQLContract.BaseValueEntry.get(cursor);
            if(albve != null && !albve.isEmpty()){
                m_bvd = albve.get(0);
            }

            // Terzo
            getLoaderManager().initLoader(Loaders.TCP_IP_CLIENT_LOADER_ID, null, this);
        }

        if(loader.getId() == Loaders.TCP_IP_CLIENT_LOADER_ID) {
            m_TcpIpClientAdapter.swapCursor(cursor);
            updateLightSwitch();
        }

        // Log.d(TAG, this.toString() + ": " + "onLoadFinished() id: " + loader.getId());

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == Loaders.ROOM_LOADER_ID) {
            m_SCAdapter.swapCursor(null);
        }
        if(loader.getId() == Loaders.TCP_IP_CLIENT_LOADER_ID) {
            m_TcpIpClientAdapter.swapCursor(null);
        }

        // Log.d(TAG, this.toString() + ": " + "onLoaderReset() id: " + loader.getId());
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
        // Stato
        if(m_id_spn_tcp_ip_client_protocol != null && m_id_spn_tcp_ip_client_protocol.getCount() > 0){
            m_id_cb_enable_tcp_ip_client_protocol.setEnabled(true);
        } else {
            m_id_cb_enable_tcp_ip_client_protocol.setEnabled(false);
        }

        // Dati
        if (m_bvd != null) {
            if (m_id_et_light_switch_name != null) {
                m_id_et_light_switch_name.setText(m_bvd.getTag());
            }

            if (m_id_et_position_x != null) {
                m_id_et_position_x.setText(Float.toString(m_bvd.getPosX()));
            }
            if (m_id_et_position_y != null) {
                m_id_et_position_y.setText(Float.toString(m_bvd.getPosY()));
            }
            if (m_id_et_position_z != null) {
                m_id_et_position_z.setText(Float.toString(m_bvd.getPosZ()));
            }
            if (m_bvd.getLandscape()) {
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

            if (m_id_spn_tcp_ip_client_protocol != null && m_id_cb_enable_tcp_ip_client_protocol != null) {
                for (int i = 0; i < m_id_spn_tcp_ip_client_protocol.getCount(); i++) {
                    Cursor value = (Cursor) m_id_spn_tcp_ip_client_protocol.getItemAtPosition(i);
                    if (value != null) {
                        long id = value.getLong(value.getColumnIndex("_id"));
                        if (id == m_bvd.getProtTcpIpClientID()) {
                            m_id_cb_enable_tcp_ip_client_protocol.setChecked(m_bvd.getProtTcpIpClientEnable());
                            m_id_spn_tcp_ip_client_protocol.setSelection(i);

                            m_id_spn_tcp_ip_client_protocol.setEnabled(m_bvd.getProtTcpIpClientEnable());
                            m_id_et_protocol_ui.setEnabled(m_bvd.getProtTcpIpClientEnable());
                            m_id_et_protocol_addr_value.setEnabled(m_bvd.getProtTcpIpClientEnable());
                            m_id_et_write_value_off.setEnabled(m_bvd.getProtTcpIpClientEnable());
                            m_id_et_write_value_off_on.setEnabled(m_bvd.getProtTcpIpClientEnable());
                            m_id_et_write_value_on_off.setEnabled(m_bvd.getProtTcpIpClientEnable());
                            m_id_et_write_value_on.setEnabled(m_bvd.getProtTcpIpClientEnable());
                        }
                    }
                }
            }
            if (m_id_et_protocol_ui != null) {
                m_id_et_protocol_ui.setText(Integer.toString(m_bvd.getProtTcpIpClientValueID()));
            }
            if (m_id_et_protocol_addr_value != null) {
                m_id_et_protocol_addr_value.setText(Integer.toString(m_bvd.getProtTcpIpClientValueAddress()));
            }
            if(m_id_spn_protocol_data_type != null) {
                long lItem = -1;
                try{
                    lItem = m_bvd.getProtTcpIpClientValueDataType();
                } catch (Exception ignore) { }
                m_id_spn_protocol_data_type.setSelection((int) lItem);
            }

            if (m_id_et_write_value_off != null) {
                m_id_et_write_value_off.setText(Integer.toString(m_bvd.getWriteValueOFF()));
            }
            if (m_id_et_write_value_off_on != null) {
                m_id_et_write_value_off_on.setText(Integer.toString(m_bvd.getWriteValueOFFON()));
            }
            if (m_id_et_write_value_on_off != null) {
                m_id_et_write_value_on_off.setText(Integer.toString(m_bvd.getWriteValueONOFF()));
            }
            if (m_id_et_write_value_on != null) {
                m_id_et_write_value_on.setText(Integer.toString(m_bvd.getWriteValueON()));
            }
        }
    }

    private void save(int iDialogOriginID) {
        if(!NumericEditText.validateInputData(findViewById(android.R.id.content))){ return; }
        if(!StringEditText.validateInputData(findViewById(android.R.id.content))){
            return;
        }

        if(m_id_et_light_switch_name != null){
            long lRoomID;
            if(m_bvdParameter != null){
                lRoomID = m_bvdParameter.getRoomID();
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
        if (m_bvd == null) {
            m_bvd = new BaseValueData();
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
        m_bvd.setRoomID(m_id_spn_room.getSelectedItemId());

        if (m_id_et_light_switch_name != null) {
            m_bvd.setTAG(m_id_et_light_switch_name.getText().toString());
        }

        try {
            if (m_id_et_position_x != null) {
                m_bvd.setPosX(Float.parseFloat(m_id_et_position_x.getText().toString()));
            }
            if (m_id_et_position_y != null) {
                m_bvd.setPosY(Float.parseFloat(m_id_et_position_y.getText().toString()));
            }
            if (m_id_et_position_z != null) {
                m_bvd.setPosZ(Float.parseFloat(m_id_et_position_z.getText().toString()));
            }
        } catch (Exception ex) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.POSITION_ERROR_ID, getString(R.string.text_odf_title_position_not_valid), getString(R.string.text_odf_message_position_not_valid), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return ;
        }
        if(getOrientation() == Orientation.PORTRAIT ){
            m_bvd.setLandscape(false);
        }
        if(getOrientation() == Orientation.LANDSCAPE ){
            m_bvd.setLandscape(true);
        }

        if(m_id_cb_enable_tcp_ip_client_protocol != null) {
            m_bvd.setProtTcpIpClientEnable(m_id_cb_enable_tcp_ip_client_protocol.isChecked());
        }

        if(m_bvd.getProtTcpIpClientEnable()){
            if(m_id_spn_tcp_ip_client_protocol != null) {
                m_bvd.setProtTcpIpClientID(m_id_spn_tcp_ip_client_protocol.getSelectedItemId());
            }

            try {
                if (m_id_et_protocol_ui != null) {
                    m_bvd.setProtTcpIpClientValueID(Integer.parseInt(m_id_et_protocol_ui.getText().toString()));
                }
                if (m_id_et_protocol_addr_value != null) {
                    m_bvd.setProtTcpIpClientValueAddress(Integer.parseInt(m_id_et_protocol_addr_value.getText().toString()));
                }
                if(m_id_spn_protocol_data_type != null) {
                    m_bvd.setProtTcpIpClientValueDataType((int) m_id_spn_protocol_data_type.getSelectedItemId());
                }

                if (m_id_et_write_value_off != null) {
                    m_bvd.setWriteValueOFF(Integer.parseInt(m_id_et_write_value_off.getText().toString()));
                }
                if (m_id_et_write_value_off_on != null) {
                    m_bvd.setWriteValueOFFON(Integer.parseInt(m_id_et_write_value_off_on.getText().toString()));
                }
                if (m_id_et_write_value_on_off != null) {
                    m_bvd.setWriteValueONOFF(Integer.parseInt(m_id_et_write_value_on_off.getText().toString()));
                }
                if (m_id_et_write_value_on != null) {
                    m_bvd.setWriteValueON(Integer.parseInt(m_id_et_write_value_on.getText().toString()));
                }
            } catch (Exception ex) {
                OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.POSITION_ERROR_ID, getString(R.string.text_odf_title_protocol_not_valid), getString(R.string.text_odf_message_protocol_not_valid), getString(R.string.text_odf_message_ok_button))
                        .show(getFragmentManager(), "");
                return ;
            }
        }

        if(SQLContract.BaseValueEntry.save(m_bvd)){
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
        if(m_bvd != null) {
            SQLContract.LightSwitchEntry.delete(m_bvd.getID(), m_bvd.getRoomID());
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
