package com.pretolesi.easydomotic.SensorValue;

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
import com.pretolesi.easydomotic.ValueUtils.ValueDataType;
import com.pretolesi.easydomotic.dialogs.DialogActionID;
import com.pretolesi.easydomotic.dialogs.DialogOriginID;
import com.pretolesi.easydomotic.dialogs.OkDialogFragment;
import com.pretolesi.easydomotic.dialogs.YesNoDialogFragment;

import java.util.ArrayList;

/**
 *
 */
public class SensorValuePropActivity extends Activity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OkDialogFragment.OkDialogFragmentCallbacks,
        YesNoDialogFragment.YesNoDialogFragmentCallbacks{
    private static final String TAG = "SensorValuePropAct";

    private static final String ROOM_ID = "Room_ID";
    private static final String SENSOR_VALUE_ID = "Sensor_Value_ID";
    private static final String SENSOR_VALUE_DATA = "Sensor_Value_Data";


    private Spinner m_id_spn_room;
    private SimpleCursorAdapter m_SCASpnRoom;

    private EDEditText m_id_et_name;
    private RadioButton m_id_rb_portrait;
    private RadioButton m_id_rb_landscape;
    private EditText m_id_et_position_x;
    private EditText m_id_et_position_y;
    private EditText m_id_et_position_z;

    private CheckBox m_id_nvpa_cb_enable_tcp_ip_client_protocol;
    private Spinner m_id_nvpa_spn_tcp_ip_client_protocol;
    private EDEditText m_id_nvpa_et_protocol_field_1;
    private EDEditText m_id_nvpa_et_protocol_field_2;
    private Spinner m_id_nvpa_spn_data_type;
    private EDEditText m_id_nvpa_et_update_millis;

    private EDEditText m_id_nvpa_et_min_nr_char_to_show;
    private EDEditText m_id_nvpa_et_nr_of_decimal;
    private EDEditText m_id_nvpa_et_um;

    private SensorValueData m_svd;
    private long m_lRoomIDParameter;
    private long m_lIDParameter;
    private SensorValueData m_svdParameter;
    private SimpleCursorAdapter m_TcpIpClientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_value_property_activity);

        m_id_spn_room = (Spinner) findViewById(R.id.id_spn_room);
        m_id_et_name = (EDEditText)findViewById(R.id.id_et_name);
        m_id_et_name.setInputLimit(SensorValueData.TAGMinChar, SensorValueData.TAGMaxChar);
        m_id_et_name.setText(SensorValueData.TAGDefaultValue);

        m_id_rb_portrait = (RadioButton)findViewById(R.id.id_rb_portrait);
        m_id_rb_portrait.setChecked(true);
        m_id_rb_landscape = (RadioButton)findViewById(R.id.id_rb_landscape);
        m_id_rb_landscape.setChecked(false);
        m_id_et_position_x = (EditText)findViewById(R.id.id_et_position_x);
        m_id_et_position_y = (EditText)findViewById(R.id.id_et_position_y);
        m_id_et_position_z = (EditText)findViewById(R.id.id_et_position_z);

        m_id_nvpa_cb_enable_tcp_ip_client_protocol = (CheckBox)findViewById(R.id.id_nvpa_cb_enable_tcp_ip_client_protocol);
        m_id_nvpa_cb_enable_tcp_ip_client_protocol.setEnabled(false);
        m_id_nvpa_spn_tcp_ip_client_protocol = (Spinner)findViewById(R.id.id_nvpa_spn_tcp_ip_client_protocol);
        m_id_nvpa_spn_tcp_ip_client_protocol.setEnabled(false);

        m_id_nvpa_et_protocol_field_1 = (EDEditText)findViewById(R.id.id_nvpa_et_protocol_field_1);
        m_id_nvpa_et_protocol_field_1.setInputLimit(SensorValueData.ProtTcpIpClientValueIDMinValue, SensorValueData.ProtTcpIpClientValueIDMaxValue);
        m_id_nvpa_et_protocol_field_1.setText(SensorValueData.ProtTcpIpClientValueIDDefaulValue);
        m_id_nvpa_et_protocol_field_1.setEnabled(false);
        m_id_nvpa_et_protocol_field_2 = (EDEditText)findViewById(R.id.id_nvpa_et_protocol_field_2);
        m_id_nvpa_et_protocol_field_2.setInputLimit(SensorValueData.ProtTcpIpClientValueAddressMinValue, SensorValueData.ProtTcpIpClientValueAddressMaxValue);
        m_id_nvpa_et_protocol_field_2.setText(SensorValueData.ProtTcpIpClientValueAddressDefaulValue);
        m_id_nvpa_et_protocol_field_2.setEnabled(false);
        m_id_nvpa_spn_data_type = (Spinner)findViewById(R.id.id_nvpa_spn_data_type);
        m_id_nvpa_spn_data_type.setSelection(SensorValueData.ProtTcpIpClientValueDataTypeDefaulValue);
        m_id_nvpa_et_update_millis = (EDEditText)findViewById(R.id.id_nvpa_et_update_millis);
        m_id_nvpa_et_update_millis.setInputLimit(SensorValueData.ProtTcpIpClientValueUpdateMillisMinValue, SensorValueData.ProtTcpIpClientValueUpdateMillisMaxValue);
        m_id_nvpa_et_update_millis.setText(SensorValueData.ProtTcpIpClientValueUpdateMillisDefaulValue);

        m_id_nvpa_et_min_nr_char_to_show = (EDEditText)findViewById(R.id.id_nvpa_et_min_nr_char_to_show);
        m_id_nvpa_et_min_nr_char_to_show.setInputLimit(SensorValueData.ValueMinNrCharToShowMinValue, SensorValueData.ValueMinNrCharToShowMaxValue);
        m_id_nvpa_et_min_nr_char_to_show.setText(SensorValueData.ValueMinNrCharToShowDefaulValue);
        m_id_nvpa_et_nr_of_decimal = (EDEditText)findViewById(R.id.id_nvpa_et_nr_of_decimal);
        m_id_nvpa_et_nr_of_decimal.setInputLimit(SensorValueData.ValueNrOfDecimalMinValue, SensorValueData.ValueNrOfDecimalMaxValue);
        m_id_nvpa_et_nr_of_decimal.setText(SensorValueData.ValueNrOfDecimalDefaulValue);
        m_id_nvpa_et_um = (EDEditText)findViewById(R.id.id_nvpa_et_um);
        m_id_nvpa_et_um.setInputLimit(SensorValueData.ValueUMMinValue, SensorValueData.ValueUMMaxValue);
        m_id_nvpa_et_um.setText(SensorValueData.ValueUMDefaulValue);

        m_id_nvpa_cb_enable_tcp_ip_client_protocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_id_nvpa_spn_tcp_ip_client_protocol.setEnabled(((CheckBox) v).isChecked());
                m_id_nvpa_et_protocol_field_1.setEnabled(((CheckBox) v).isChecked());
                m_id_nvpa_et_protocol_field_2.setEnabled(((CheckBox) v).isChecked());
            }
        });

        setActionBar();

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        Intent intent = getIntent();
        m_lIDParameter = -1;
        if(intent != null) {
            m_lRoomIDParameter = intent.getLongExtra(ROOM_ID, -1);
            m_lIDParameter = intent.getLongExtra(SENSOR_VALUE_ID, -1);
            m_svdParameter = intent.getParcelableExtra(SensorValuePropActivity.SENSOR_VALUE_DATA);
        }

        m_id_nvpa_spn_tcp_ip_client_protocol.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ValueDataType.DataType.values()));

        m_SCASpnRoom = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[] {SQLContract.RoomEntry.COLUMN_NAME_TAG},
                new int[] {android.R.id.text1}, 0);
        m_id_spn_room.setAdapter(m_SCASpnRoom);

        m_id_nvpa_spn_data_type.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ValueDataType.DataType.values()));

        m_TcpIpClientAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[] {SQLContract.TcpIpClientEntry.COLUMN_NAME_NAME},
                new int[] {android.R.id.text1}, 0);
        m_id_nvpa_spn_tcp_ip_client_protocol.setAdapter(m_TcpIpClientAdapter);

        // Primo
        getLoaderManager().initLoader(Loaders.ROOM_LOADER_ID, null, this);
    }

    public void setActionBar() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.settings_title_section_edit_numeric_value));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_sensor_value_property_activity, menu);

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
        if (m_svd != null) {
            if(!m_svd.getSaved()){
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

        if(id == Loaders.NUMERIC_VALUE_LOADER_ID){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    Cursor cursor = null;
                    if(m_svdParameter != null){
//                        cursor = SQLContract.NumericValueEntry.loadFromSensorValueData(m_svdParameter);
                    } else {
                        cursor = SQLContract.NumericValueEntry.load(m_lIDParameter, m_lRoomIDParameter);
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
            m_SCASpnRoom.swapCursor(cursor);
            if(m_id_spn_room != null) {
                long lRoomID;
                if(m_svdParameter != null){
                    lRoomID = m_svdParameter.getRoomID();
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
            getLoaderManager().initLoader(Loaders.NUMERIC_VALUE_LOADER_ID, null, this);
        }

        if(loader.getId() == Loaders.SENSOR_VALUE_LOADER_ID) {
/*
            ArrayList<SensorValueData> alnvd = SQLContract.NumericValueEntry.get(cursor);
            if(alnvd != null && !alnvd.isEmpty()){
                m_svd = alnvd.get(0);
            }
*/
            // Terzo
            getLoaderManager().initLoader(Loaders.TCP_IP_CLIENT_LOADER_ID, null, this);
        }

        if(loader.getId() == Loaders.TCP_IP_CLIENT_LOADER_ID) {
            m_TcpIpClientAdapter.swapCursor(cursor);
            updateNumericValue();
        }

        Log.d(TAG, this.toString() + ": " + "onLoadFinished() id: " + loader.getId());

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == Loaders.ROOM_LOADER_ID) {
            m_SCASpnRoom.swapCursor(null);
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
                    saveSensorValueData(iDialogOriginID);
                }
                if(bNo) {
                    // no action
                }
            }

            if(iDialogActionID == DialogActionID.DELETE_CONFIRM_ID) {
                if(bYes) {
                    // Delete
                    deleteSensorValueData(iDialogOriginID);                }
                if(bNo) {
                    // No action...
                }
            }
        }

        if(iDialogOriginID == DialogOriginID.ORIGIN_BACK_BUTTON_ID) {
            if(iDialogActionID == DialogActionID.SAVE_ITEM_ALREADY_EXSIST_CONFIRM_ID) {
                if(bYes) {
                    // Save ok, exit
                    saveSensorValueData(iDialogOriginID);
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

    private void updateNumericValue() {
        // Stato
        if(m_id_nvpa_spn_tcp_ip_client_protocol != null && m_id_nvpa_spn_tcp_ip_client_protocol.getCount() > 0){
            m_id_nvpa_cb_enable_tcp_ip_client_protocol.setEnabled(true);
        } else {
            m_id_nvpa_cb_enable_tcp_ip_client_protocol.setEnabled(false);
        }

        // Dati
        if (m_svd != null) {
            if (m_id_et_name != null) {
                m_id_et_name.setText(m_svd.getTag());
            }

            if (m_svd.getLandscape()) {
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
                m_id_et_position_x.setText(Float.toString(m_svd.getPosX()));
            }
            if (m_id_et_position_y != null) {
                m_id_et_position_y.setText(Float.toString(m_svd.getPosY()));
            }
            if (m_id_et_position_z != null) {
                m_id_et_position_z.setText(Float.toString(m_svd.getPosZ()));
            }
            if(m_id_nvpa_spn_data_type != null) {
                long lItem = -1;
                try{
                    lItem = m_svd.getProtTcpIpClientValueDataType();
                } catch (Exception ignore) { }
                m_id_nvpa_spn_data_type.setSelection((int)lItem);
            }
            if (m_id_nvpa_et_nr_of_decimal != null) {
                m_id_nvpa_et_nr_of_decimal.setText(Integer.toString(m_svd.getValueNrOfDecimal()));
            }
            if (m_id_nvpa_et_um != null) {
                m_id_nvpa_et_um.setText(m_svd.getValueUM());
            }
            if (m_id_nvpa_et_min_nr_char_to_show != null) {
                m_id_nvpa_et_min_nr_char_to_show.setText(Integer.toString(m_svd.getValueMinNrCharToShow()));
            }
            if (m_id_nvpa_et_update_millis != null) {
                m_id_nvpa_et_update_millis.setText(Integer.toString(m_svd.getProtTcpIpClientValueUpdateMillis()));
            }

            if (m_id_nvpa_spn_tcp_ip_client_protocol != null && m_id_nvpa_cb_enable_tcp_ip_client_protocol != null) {
                for (int i = 0; i < m_id_nvpa_spn_tcp_ip_client_protocol.getCount(); i++) {
                    Cursor value = (Cursor) m_id_nvpa_spn_tcp_ip_client_protocol.getItemAtPosition(i);
                    if (value != null) {
                        long id = value.getLong(value.getColumnIndex("_id"));
                        if (id == m_svd.getProtTcpIpClientID()) {
                            m_id_nvpa_cb_enable_tcp_ip_client_protocol.setChecked(m_svd.getProtTcpIpClientEnable());
                            m_id_nvpa_spn_tcp_ip_client_protocol.setSelection(i);

                            m_id_nvpa_spn_tcp_ip_client_protocol.setEnabled(m_svd.getProtTcpIpClientEnable());
                            m_id_nvpa_et_protocol_field_1.setEnabled(m_svd.getProtTcpIpClientEnable());
                            m_id_nvpa_et_protocol_field_2.setEnabled(m_svd.getProtTcpIpClientEnable());
                        }
                    }
                }
            }
            if (m_id_nvpa_et_protocol_field_1 != null) {
                m_id_nvpa_et_protocol_field_1.setText(Integer.toString(m_svd.getProtTcpIpClientValueID()));
            }
            if (m_id_nvpa_et_protocol_field_2 != null) {
                m_id_nvpa_et_protocol_field_2.setText(Integer.toString(m_svd.getProtTcpIpClientValueAddress()));
            }
        }
    }

    private void save(int iDialogOriginID) {
        if(!EDEditText.validateInputData(findViewById(android.R.id.content))){ return; }

        if(m_id_et_name != null){
            long lRoomID;
            if(m_svdParameter != null){
                lRoomID = m_svdParameter.getRoomID();
            } else {
                lRoomID = m_lRoomIDParameter;
            }

            if (!SQLContract.NumericValueEntry.isTagPresent(m_id_et_name.getText().toString(), lRoomID)) {
                saveSensorValueData(iDialogOriginID);
            } else {
                YesNoDialogFragment.newInstance(iDialogOriginID,
                        DialogActionID.SAVE_ITEM_ALREADY_EXSIST_CONFIRM_ID,
                        getString(R.string.text_yndf_title_numeric_value_name_already_exist),
                        getString(R.string.text_yndf_message_numeric_value_name_already_exist_confirmation),
                        getString(R.string.text_yndf_btn_yes),
                        getString(R.string.text_yndf_btn_no)
                ).show(getFragmentManager(), "");
            }
        }
    }

    private void saveSensorValueData(int iDialogOriginID){
        if (m_svd == null) {
            m_svd = new SensorValueData();
        }

        if((m_id_spn_room == null) || (m_id_spn_room.getSelectedItemId() == AdapterView.INVALID_ROW_ID)){
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.ROOM_ERROR_ID, getString(R.string.text_odf_title_room_data_not_present), getString(R.string.text_odf_message_room_data_not_present), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return ;
        }
        if (m_id_et_name == null || m_id_et_name.getText().toString().equals("")) {
            OkDialogFragment odf = OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.NUMERIC_VALUE_ERROR_NAME, getString(R.string.text_odf_title_numeric_value_name_error), getString(R.string.text_odf_message_numeric_value_name_not_valid), getString(R.string.text_odf_message_ok_button));
            odf.show(getFragmentManager(), "");
            return ;
        }

        if(getOrientation() == Orientation.UNDEFINED ) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.ORIENTATION_ERROR_ID, getString(R.string.text_odf_title_orientation_not_set), getString(R.string.text_odf_message_orientation_not_set), getString(R.string.text_odf_message_ok_button))
            .show(getFragmentManager(), "");
            return ;
        }

        // Set the selected Room
        m_svd.setRoomID(m_id_spn_room.getSelectedItemId());

        if (m_id_et_name != null) {
            m_svd.setTAG(m_id_et_name.getText().toString());
        }

        if(getOrientation() == Orientation.PORTRAIT ){
            m_svd.setLandscape(false);
        }
        if(getOrientation() == Orientation.LANDSCAPE ){
            m_svd.setLandscape(true);
        }
        try {
            if (m_id_et_position_x != null) {
                m_svd.setPosX(Float.parseFloat(m_id_et_position_x.getText().toString()));
            }
            if (m_id_et_position_y != null) {
                m_svd.setPosY(Float.parseFloat(m_id_et_position_y.getText().toString()));
            }
            if (m_id_et_position_z != null) {
                m_svd.setPosZ(Float.parseFloat(m_id_et_position_z.getText().toString()));
            }
        } catch (Exception ex) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.POSITION_ERROR_ID, getString(R.string.text_odf_title_position_not_valid), getString(R.string.text_odf_message_position_not_valid), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return ;
        }

        if(m_id_nvpa_spn_data_type != null) {
            m_svd.setProtTcpIpClientValueDataType((int) m_id_nvpa_spn_data_type.getSelectedItemId());
        }
        if (m_id_nvpa_et_nr_of_decimal != null) {
            m_svd.setValueNrOfDecimal(Integer.parseInt(m_id_nvpa_et_nr_of_decimal.getText().toString()));
        }
        if (m_id_nvpa_et_um != null) {
            m_svd.setValueUM(m_id_nvpa_et_um.getText().toString());
        }
        if (m_id_nvpa_et_min_nr_char_to_show != null) {
            m_svd.setValueMinNrCharToShow(Integer.parseInt(m_id_nvpa_et_min_nr_char_to_show.getText().toString()));
        }
        if (m_id_nvpa_et_update_millis != null) {
            m_svd.setProtTcpIpClientValueUpdateMillis(Integer.parseInt(m_id_nvpa_et_update_millis.getText().toString()));
        }

        if(m_id_nvpa_cb_enable_tcp_ip_client_protocol != null) {
            m_svd.setProtTcpIpClientEnable(m_id_nvpa_cb_enable_tcp_ip_client_protocol.isChecked());
        }

        if(m_svd.getProtTcpIpClientEnable()){
            if(m_id_nvpa_spn_tcp_ip_client_protocol != null) {
                m_svd.setProtTcpIpClientID(m_id_nvpa_spn_tcp_ip_client_protocol.getSelectedItemId());
            }

            try {
                if (m_id_nvpa_et_protocol_field_1 != null) {
                    m_svd.setProtTcpIpClientValueID(Integer.parseInt(m_id_nvpa_et_protocol_field_1.getText().toString()));
                }
                if (m_id_nvpa_et_protocol_field_2 != null) {
                    m_svd.setProtTcpIpClientValueAddress(Integer.parseInt(m_id_nvpa_et_protocol_field_2.getText().toString()));
                }
            } catch (Exception ex) {
                OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.POSITION_ERROR_ID, getString(R.string.text_odf_title_protocol_not_valid), getString(R.string.text_odf_message_protocol_not_valid), getString(R.string.text_odf_message_ok_button))
                        .show(getFragmentManager(), "");
                return ;
            }
        }

        m_svd.setProtTcpIpClientSendDataOnChange(true);
/*
        if(SQLContract.NumericValueEntry.save(m_svd)){
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_OK_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_ok), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
        } else {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
        }
*/
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
    private void deleteSensorValueData(int iDialogOriginID) {
        if(m_svd != null) {
            SQLContract.NumericValueEntry.delete(m_svd.getID(), m_svd.getRoomID());
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


    public static Intent makeNumericValuePropActivity(Context context, long lRoomID, long lID) {
        Intent intent = new Intent();
        intent.setClass(context, SensorValuePropActivity.class);
        intent.putExtra(SensorValuePropActivity.ROOM_ID, lRoomID);
        intent.putExtra(SensorValuePropActivity.SENSOR_VALUE_ID, lID);
        return intent;
    }

    public static Intent makeNumericValuePropActivity(Context context, SensorValueData nvd) {
        Intent intent = new Intent();
        intent.setClass(context, SensorValuePropActivity.class);
        intent.putExtra(SensorValuePropActivity.SENSOR_VALUE_DATA, nvd);
        return intent;
    }

}
