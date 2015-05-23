package com.pretolesi.easydomotic.BaseValue;

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
public class BaseValuePropActivity extends Activity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OkDialogFragment.OkDialogFragmentCallbacks,
        YesNoDialogFragment.YesNoDialogFragmentCallbacks{
    protected static final String TAG = "BaseValuePropActivity";

    protected static final String ROOM_ID = "Room_ID";
    protected static final String TYPE = "Type";
    protected static final String BASE_VALUE_ID = "Base_Value_ID";
    protected static final String BASE_VALUE_DATA = "Base_Value_Data";

 //   private CharSequence mTitle;

    protected Spinner m_id_spn_room;
    protected SimpleCursorAdapter m_SCAdapter;

    protected StringEditText m_id_et_name;
    protected EditText m_id_et_position_x;
    protected EditText m_id_et_position_y;
    protected EditText m_id_et_position_z;
    protected RadioButton m_id_rb_portrait;
    protected RadioButton m_id_rb_landscape;

    protected CheckBox m_id_cb_enable_tcp_ip_client_protocol;
    protected Spinner m_id_spn_tcp_ip_client_protocol;

    protected NumericEditText m_id_et_protocol_ui;
    protected NumericEditText m_id_et_protocol_addr_value;

    protected Spinner m_id_spn_protocol_data_type;

    protected BaseValueData m_bvd;
    protected int m_iTypeParameter;
    protected long m_lRoomIDParameter;
    protected long m_lIDParameter;
    protected BaseValueData m_bvdParameter;
    protected SimpleCursorAdapter m_TcpIpClientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void onBaseCreate() {
        m_id_spn_room = (Spinner) findViewById(R.id.id_spn_room_name);
        m_id_et_name = (StringEditText)findViewById(R.id.id_et_name);
        m_id_et_name.setInputLimit(BaseValueData.TAGMinChar, BaseValueData.TAGMaxChar);
        m_id_et_name.setText(BaseValueData.TAGDefaultValue);
        m_id_rb_portrait = (RadioButton)findViewById(R.id.id_rb_portrait);
        m_id_rb_portrait.setChecked(true);
        m_id_rb_landscape = (RadioButton)findViewById(R.id.id_rb_landscape);
        m_id_rb_landscape.setChecked(false);
        m_id_et_position_x = (EditText)findViewById(R.id.id_et_position_x);
        m_id_et_position_x.setText(BaseValueData.PosXDefaultValue);
        m_id_et_position_y = (EditText)findViewById(R.id.id_et_position_y);
        m_id_et_position_y.setText(BaseValueData.PosYDefaultValue);
        m_id_et_position_z = (EditText)findViewById(R.id.id_et_position_z);
        m_id_et_position_z.setText(BaseValueData.PosZDefaultValue);
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
                m_id_spn_protocol_data_type.setEnabled(((CheckBox) v).isChecked());
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
        m_id_spn_protocol_data_type.setEnabled(false);

        Intent intent = getIntent();
        if(intent != null) {
            m_iTypeParameter = intent.getIntExtra(TYPE, -1);
            m_lRoomIDParameter = intent.getLongExtra(ROOM_ID, -1);
            m_lIDParameter = intent.getIntExtra(BASE_VALUE_ID, -1);
            m_bvdParameter = intent.getParcelableExtra(BASE_VALUE_DATA);
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

        m_TcpIpClientAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[] {SQLContract.TcpIpClientEntry.COLUMN_NAME_NAME},
                new int[] {android.R.id.text1}, 0);
        m_id_spn_tcp_ip_client_protocol.setAdapter(m_TcpIpClientAdapter);

        m_id_spn_protocol_data_type.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, NumericDataType.DataType.values()));

        setActionBar();
    }

    private void setActionBar() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            int iTypeParameter;
            if(m_bvdParameter != null){
                iTypeParameter = m_bvdParameter.getType();
            } else {
                iTypeParameter = m_iTypeParameter;
            }
            switch (iTypeParameter){
                case BaseValueData.TYPE_LIGHT_SWITCH:
                    actionBar.setTitle(getString(R.string.settings_title_section_edit_switch));
                    break;
                case BaseValueData.TYPE_NUMERIC_VALUE:
                    actionBar.setTitle(getString(R.string.settings_title_section_edit_numeric_value));
                    break;
                case BaseValueData.TYPE_SENSOR_RAW_VALUE:
                case BaseValueData.TYPE_SENSOR_CALIBR_VALUE:
                    actionBar.setTitle(getString(R.string.settings_title_section_edit_sensor_value));
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Primo
        getLoaderManager().initLoader(Loaders.ROOM_LOADER_ID, null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getLoaderManager().destroyLoader(Loaders.ROOM_LOADER_ID);
        getLoaderManager().destroyLoader(Loaders.BASE_VALUE_LOADER_ID);
        getLoaderManager().destroyLoader(Loaders.TCP_IP_CLIENT_LOADER_ID);
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

        if(id == Loaders.BASE_VALUE_LOADER_ID){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    Cursor cursor = null;
                    if(m_bvdParameter != null){
                        cursor = SQLContract.BaseValueEntry.loadFromBaseValueData(m_bvdParameter);
                    } else if (m_lIDParameter > 0){
                        cursor = SQLContract.BaseValueEntry.loadByID(m_lIDParameter);
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

            // Secondo
            getLoaderManager().initLoader(Loaders.BASE_VALUE_LOADER_ID, null, this);
        }

        if(loader.getId() == Loaders.BASE_VALUE_LOADER_ID) {
            ArrayList<BaseValueData> albve = SQLContract.BaseValueEntry.get(cursor);
            if(albve != null && !albve.isEmpty()){
                m_bvd = albve.get(0);
            }
            if(m_id_spn_room != null) {
                long lRoomID;
                if(m_bvd != null) {
                    lRoomID = m_bvd.getRoomID();
                } else {
                    lRoomID = m_lRoomIDParameter;
                }
                for (int i = 0; i < m_id_spn_room.getCount(); i++) {
                    if (m_id_spn_room.getItemIdAtPosition(i) == lRoomID) {
                        m_id_spn_room.setSelection(i);
                    }
                }
            }

            // Terzo
            getLoaderManager().initLoader(Loaders.TCP_IP_CLIENT_LOADER_ID, null, this);
        }

        if(loader.getId() == Loaders.TCP_IP_CLIENT_LOADER_ID) {
            m_TcpIpClientAdapter.swapCursor(cursor);
            getBaseValue();
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
                    if(setBaseData(iDialogOriginID)){
                        if(SQLContract.BaseValueEntry.save(m_bvd)){
                            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_OK_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_ok), getString(R.string.text_odf_message_ok_button))
                                    .show(getFragmentManager(), "");
                        } else {
                            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                                    .show(getFragmentManager(), "");
                        }
                    }
                }
                if(bNo) {
                    // no action
                }
            }

            if(iDialogActionID == DialogActionID.DELETE_CONFIRM_ID) {
                if(bYes) {
                    // Delete
                    deleteBaseValueData(iDialogOriginID);                }
                if(bNo) {
                    // No action...
                }
            }
        }

        if(iDialogOriginID == DialogOriginID.ORIGIN_BACK_BUTTON_ID) {
            if(iDialogActionID == DialogActionID.SAVE_ITEM_ALREADY_EXSIST_CONFIRM_ID) {
                if(bYes) {
                    // Save ok, exit
                    if(setBaseData(iDialogOriginID)){
                        if(SQLContract.BaseValueEntry.save(m_bvd)){
                            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_OK_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_ok), getString(R.string.text_odf_message_ok_button))
                                    .show(getFragmentManager(), "");
                        } else {
                            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                                    .show(getFragmentManager(), "");
                        }
                    }
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

    protected void getBaseValue() {
        // Stato
        if(m_id_spn_tcp_ip_client_protocol != null && m_id_spn_tcp_ip_client_protocol.getCount() > 0){
            m_id_cb_enable_tcp_ip_client_protocol.setEnabled(true);
        } else {
            m_id_cb_enable_tcp_ip_client_protocol.setEnabled(false);
        }

        // Dati
        if (m_bvd == null) {
            return ;
        }
        if (m_id_et_name != null) {
            m_id_et_name.setText(m_bvd.getTag());
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

        if (m_id_et_position_x != null) {
            m_id_et_position_x.setText(Float.toString(m_bvd.getPosX()));
        }
        if (m_id_et_position_y != null) {
            m_id_et_position_y.setText(Float.toString(m_bvd.getPosY()));
        }
        if (m_id_et_position_z != null) {
            m_id_et_position_z.setText(Float.toString(m_bvd.getPosZ()));
        }

        if (m_id_spn_tcp_ip_client_protocol != null && m_id_cb_enable_tcp_ip_client_protocol != null) {
            for (int i = 0; i < m_id_spn_tcp_ip_client_protocol.getCount(); i++) {
                Cursor value = (Cursor) m_id_spn_tcp_ip_client_protocol.getItemAtPosition(i);
                if (value != null) {
                    long id = value.getLong(value.getColumnIndex("_id"));
                    if (id == m_bvd.getProtTcpIpClientID()) {
                        m_id_cb_enable_tcp_ip_client_protocol.setChecked(m_bvd.getProtTcpIpClientEnable());
                        m_id_spn_tcp_ip_client_protocol.setSelection(i);
                        if (m_id_et_protocol_ui != null) {
                            m_id_et_protocol_ui.setEnabled(true);
                        }
                        if (m_id_et_protocol_addr_value != null) {
                            m_id_et_protocol_addr_value.setEnabled(true);
                        }
                        if(m_id_spn_protocol_data_type != null) {
                            m_id_spn_protocol_data_type.setEnabled(true);
                        }
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
    }

    private void save(int iDialogOriginID) {
        if(!NumericEditText.validateInputData(findViewById(android.R.id.content))){ return; }
        if(!StringEditText.validateInputData(findViewById(android.R.id.content))){ return; }

        if(m_id_et_name != null){

            // Se non e' null, ed ha l'ID impostato,
            // oppure
            // Se l'ID e' maggiore di 0
            // il Record esiste Gia'
//            if (!SQLContract.BaseValueEntry.isTagPresent(m_id_et_name.getText().toString(), lRoomID)) {
            if(!(m_bvd != null && (m_bvd.getID() > 0)) || (m_lIDParameter > 0)){
                if(setBaseData(iDialogOriginID)){
                    if(SQLContract.BaseValueEntry.save(m_bvd)){
                        OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_OK_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_ok), getString(R.string.text_odf_message_ok_button))
                                .show(getFragmentManager(), "");
                    } else {
                        OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                                .show(getFragmentManager(), "");
                    }
                }
            } else {
                YesNoDialogFragment.newInstance(iDialogOriginID,
                        DialogActionID.SAVE_ITEM_ALREADY_EXSIST_CONFIRM_ID,
                        getString(R.string.text_yndf_title_base_value_name_already_exist),
                        getString(R.string.text_yndf_message_base_value_name_already_exist_confirmation),
                        getString(R.string.text_yndf_btn_yes),
                        getString(R.string.text_yndf_btn_no)
                ).show(getFragmentManager(), "");
            }
        }
    }

    protected boolean setBaseData(int iDialogOriginID){
        if (m_bvd == null) {
            m_bvd = new BaseValueData(m_iTypeParameter);
        }

        if((m_id_spn_room == null) || (m_id_spn_room.getSelectedItemId() == AdapterView.INVALID_ROW_ID)){
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.ROOM_ERROR_ID, getString(R.string.text_odf_title_room_data_not_present), getString(R.string.text_odf_message_room_data_not_present), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return false;
        }
        if (m_id_et_name == null || m_id_et_name.getText().toString().equals("")) {
            OkDialogFragment odf = OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.VALUE_ERROR_ID, getString(R.string.text_odf_title_name_error), getString(R.string.text_odf_message_name_not_valid), getString(R.string.text_odf_message_ok_button));
            odf.show(getFragmentManager(), "");
            return false;
        }

        if(getOrientation() == Orientation.UNDEFINED ) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.ORIENTATION_ERROR_ID, getString(R.string.text_odf_title_orientation_not_set), getString(R.string.text_odf_message_orientation_not_set), getString(R.string.text_odf_message_ok_button))
            .show(getFragmentManager(), "");
            return false;
        }

        // Set the selected Room
        m_bvd.setRoomID(m_id_spn_room.getSelectedItemId());

        if (m_id_et_name != null) {
            m_bvd.setTAG(m_id_et_name.getText().toString());
        }

        if(getOrientation() == Orientation.PORTRAIT ){
            m_bvd.setLandscape(false);
        }
        if(getOrientation() == Orientation.LANDSCAPE ){
            m_bvd.setLandscape(true);
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
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.VALUE_ERROR_ID, getString(R.string.text_odf_title_format_not_valid), getString(R.string.text_odf_message_format_not_valid), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return false;
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

            } catch (Exception ex) {
                OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.VALUE_ERROR_ID, getString(R.string.text_odf_title_format_not_valid), getString(R.string.text_odf_message_format_not_valid), getString(R.string.text_odf_message_ok_button))
                        .show(getFragmentManager(), "");
                return false;
            }
        }

        return true;
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
    private void deleteBaseValueData(int iDialogOriginID) {
        long lID = -1;
        if(m_bvd != null && m_bvd.getID() > 0) {
            lID = m_bvd.getID();
        } else {
            lID = m_lIDParameter;
        }
        if(lID > 0){
            if(SQLContract.BaseValueEntry.delete(lID)){
                OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.DELETING_OK_ID, getString(R.string.text_odf_title_deleting), getString(R.string.text_odf_message_deleting_ok), getString(R.string.text_odf_message_ok_button))
                        .show(getFragmentManager(), "");
                return;
            }
        }

        OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.DELETING_ERROR_ID, getString(R.string.text_odf_title_deleting), getString(R.string.text_odf_message_deleting_error), getString(R.string.text_odf_message_ok_button))
                .show(getFragmentManager(), "");

/*
        if(m_bvd != null) {
            SQLContract.BaseValueEntry.delete(m_bvd.getID());
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.DELETING_OK_ID, getString(R.string.text_odf_title_deleting), getString(R.string.text_odf_message_deleting_ok), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
        }
*/
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

    public static Intent makeBaseValuePropActivity(Context context, Class cls, int iType) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(BaseValuePropActivity.TYPE, iType);
        return intent;
    }

    public static Intent makeBaseValuePropActivityByRoomID(Context context, Class cls, int iType, long lRoomID) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(BaseValuePropActivity.TYPE, iType);
        intent.putExtra(BaseValuePropActivity.ROOM_ID, lRoomID);
        return intent;
    }

    public static Intent makeBaseValuePropActivityByValueID(Context context, Class cls, long lID) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(BaseValuePropActivity.BASE_VALUE_ID, lID);
        return intent;
    }

    public static Intent makeBaseValuePropActivityByValueData(Context context, Class cls, BaseValueData bvd) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(BaseValuePropActivity.BASE_VALUE_DATA, bvd);
        return intent;
    }
}
