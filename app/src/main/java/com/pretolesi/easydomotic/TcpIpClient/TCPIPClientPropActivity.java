package com.pretolesi.easydomotic.TcpIpClient;

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
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easydomotic.CustomControls.NumericEditText;
import com.pretolesi.easydomotic.CustomControls.StringEditText;
import com.pretolesi.easydomotic.LoadersUtils.Loaders;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.dialogs.DialogActionID;
import com.pretolesi.easydomotic.dialogs.DialogOriginID;
import com.pretolesi.easydomotic.dialogs.OkDialogFragment;
import com.pretolesi.easydomotic.dialogs.YesNoDialogFragment;

import java.util.ArrayList;

/**
 *
 */
public class TCPIPClientPropActivity extends Activity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OkDialogFragment.OkDialogFragmentCallbacks,
        YesNoDialogFragment.YesNoDialogFragmentCallbacks{

    private static final String TAG = "TCPIPClientPropAct";

    private static final String TCP_IP_CLIENT_ID = "TcpIpClientID";

    private StringEditText m_id_stica_et_server_name;
    private StringEditText m_id_stica_et_server_ip_address;
    private NumericEditText m_id_stica_et_server_port;
    private NumericEditText m_id_stica_et_timeout;
    private NumericEditText m_id_stica_et_comm_send_data_delay;
    private Spinner m_id_stica_spn_protocol;
    private NumericEditText m_id_stica_et_protocol_field_1;
    private NumericEditText m_id_stica_et_protocol_field_2;

    private SimpleCursorAdapter m_SCAdapter;
    private CommClientData m_ticd;
    private long m_lIDParameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_tcp_ip_client_activity);

        m_id_stica_et_server_name = (StringEditText)findViewById(R.id.id_stica_et_server_name);
        m_id_stica_et_server_name.setInputLimit(CommClientData.NameMinChar, CommClientData.NameMaxChar);
        m_id_stica_et_server_name.setText(CommClientData.NameDefaultValue);
        m_id_stica_et_server_ip_address = (StringEditText)findViewById(R.id.id_stica_et_server_ip_address);
        m_id_stica_et_server_ip_address.setInputLimit(CommClientData.AddressMinChar, CommClientData.AddressMaxChar);
        m_id_stica_et_server_ip_address.setText(CommClientData.AddressDefaultValue);
        m_id_stica_et_server_port = (NumericEditText)findViewById(R.id.id_stica_et_server_port);
        m_id_stica_et_server_port.setInputLimit(CommClientData.PortMinValue, CommClientData.PortMaxValue);
        m_id_stica_et_server_port.setText(CommClientData.PortDefaultValue);
        m_id_stica_et_timeout = (NumericEditText)findViewById(R.id.id_stica_et_timeout);
        m_id_stica_et_timeout.setInputLimit(CommClientData.TimeoutMinValue, CommClientData.TimeoutMaxValue);
        m_id_stica_et_timeout.setText(CommClientData.TimeouDefaultValue);
        m_id_stica_et_comm_send_data_delay = (NumericEditText)findViewById(R.id.id_stica_et_comm_send_data_delay);
        m_id_stica_et_comm_send_data_delay.setInputLimit(CommClientData.CommSendDelayDataMinValue, CommClientData.CommSendDelayDataMaxValue);
        m_id_stica_et_comm_send_data_delay.setText(CommClientData.CommSendDelayDataDefaultValue);
        m_id_stica_spn_protocol = (Spinner) findViewById(R.id.id_stica_spn_protocol);
        m_id_stica_spn_protocol.setSelection(CommClientData.ProtocolDefaulValue);
        m_id_stica_et_protocol_field_1 = (NumericEditText)findViewById(R.id.id_stica_et_protocol_field_1);
        m_id_stica_et_protocol_field_1.setInputLimit(CommClientData.HeadMinValue, CommClientData.HeadMaxValue);
        m_id_stica_et_protocol_field_1.setText(CommClientData.HeadDefaulValue);
        m_id_stica_et_protocol_field_2 = (NumericEditText)findViewById(R.id.id_stica_et_protocol_field_2);
        m_id_stica_et_protocol_field_2.setInputLimit(CommClientData.TailMinValue, CommClientData.TailMaxValue);
        m_id_stica_et_protocol_field_2.setText(CommClientData.TailDefaulValue);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        Intent intent = getIntent();
        if(intent != null) {
            m_lIDParameter = intent.getLongExtra(TCP_IP_CLIENT_ID, -1);
        }

        m_id_stica_spn_protocol.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, CommClientData.Protocol.values()));

        setActionBar();
    }

    public void setActionBar() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.settings_title_section_edit_tcp_ip_client));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().initLoader(Loaders.TCP_IP_CLIENT_LOADER_ID, null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getLoaderManager().destroyLoader(Loaders.TCP_IP_CLIENT_LOADER_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_setting_tcp_ip_client_activity, menu);

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
        if (m_ticd != null) {
            if(!m_ticd.getSaved()){
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
        if(id == Loaders.TCP_IP_CLIENT_LOADER_ID){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.TcpIpClientEntry.load(m_lIDParameter);
                }
            };
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(loader.getId() == Loaders.TCP_IP_CLIENT_LOADER_ID) {
            ArrayList<CommClientData> alticd = SQLContract.TcpIpClientEntry.get(cursor);
            if(alticd != null && !alticd.isEmpty()){
                m_ticd = alticd.get(0);
                m_ticd.setSaved(false);
                getValue();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onYesNoDialogFragmentClickListener(int iDialogOriginID, int iDialogActionID, boolean bYes, boolean bNo) {
        if(iDialogOriginID == DialogOriginID.ORIGIN_MENU_BUTTON_ID) {
            if(iDialogActionID == DialogActionID.SAVE_ITEM_ALREADY_EXSIST_CONFIRM_ID) {
                if(bYes) {
                    // Save ok, exit
                    if(setData(iDialogOriginID)){
                        if(SQLContract.TcpIpClientEntry.save(m_ticd)){
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
                    deleteTCPIPClientData(iDialogOriginID);                }
                if(bNo) {
                    // No action...
                }
            }
        }

        if(iDialogOriginID == DialogOriginID.ORIGIN_BACK_BUTTON_ID) {
            if(iDialogActionID == DialogActionID.SAVE_ITEM_ALREADY_EXSIST_CONFIRM_ID) {
                if(bYes) {
                    // Save ok, exit
                    if(setData(iDialogOriginID)){
                        if(SQLContract.TcpIpClientEntry.save(m_ticd)){
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

    private void getValue() {
        if (m_ticd == null) {
            return;
        }

        if(m_id_stica_et_server_name != null) {
            m_id_stica_et_server_name.setText(m_ticd.getName());
        }
        if(m_id_stica_et_server_ip_address != null) {
            m_id_stica_et_server_ip_address.setText(m_ticd.getAddress());
        }
        if(m_id_stica_et_server_port != null) {
            m_id_stica_et_server_port.setText(Integer.toString(m_ticd.getPort()));
        }
        if(m_id_stica_et_timeout != null) {
            m_id_stica_et_timeout.setText(Integer.toString(m_ticd.getTimeout()));
        }
        if(m_id_stica_et_comm_send_data_delay != null) {
            m_id_stica_et_comm_send_data_delay.setText(Integer.toString(m_ticd.getCommSendDelayData()));
        }
        if(m_id_stica_spn_protocol != null) {
            long lItem = -1;
            try{
                lItem = m_ticd.getProtocolID();
            } catch (Exception ignore) { }
            m_id_stica_spn_protocol.setEnabled(m_ticd.getEnable());
            m_id_stica_spn_protocol.setSelection((int)lItem);
        }
        if(m_id_stica_et_protocol_field_1 != null) {
            m_id_stica_et_protocol_field_1.setText(Integer.toString(m_ticd.getHead()));
        }
        if(m_id_stica_et_protocol_field_2 != null) {
            m_id_stica_et_protocol_field_2.setText(Integer.toString(m_ticd.getTail()));
        }
    }

    private void save(int iDialogOriginID) {
        if(!NumericEditText.validateInputData(findViewById(android.R.id.content))){ return; }
        if(!StringEditText.validateInputData(findViewById(android.R.id.content))){ return;  }

        if(m_id_stica_et_server_name != null) {

            if(!((m_ticd != null && (m_ticd.getID() > 0)) || (m_lIDParameter > 0))){
                if(setData(iDialogOriginID)){
                    if(SQLContract.TcpIpClientEntry.save(m_ticd)){
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

    private boolean setData(int iDialogOriginID){

        if (m_ticd == null) {
            m_ticd = new CommClientData();
            m_ticd.setEnable(true);
        }

        m_ticd.setName(m_id_stica_et_server_name.getText().toString());
        m_ticd.setAddress(m_id_stica_et_server_ip_address.getText().toString());
        m_ticd.setPort(Integer.parseInt(m_id_stica_et_server_port.getText().toString()));
        m_ticd.setTimeout(Integer.parseInt(m_id_stica_et_timeout.getText().toString()));
        m_ticd.setCommSendDelayData(Integer.parseInt(m_id_stica_et_comm_send_data_delay.getText().toString()));
        m_ticd.setProtocolID(m_id_stica_spn_protocol.getSelectedItemId());
        m_ticd.setHead(Integer.parseInt(m_id_stica_et_protocol_field_1.getText().toString()));
        m_ticd.setTail(Integer.parseInt(m_id_stica_et_protocol_field_2.getText().toString()));

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
    private void deleteTCPIPClientData(int iDialogOriginID) {
        if(m_ticd != null) {
            SQLContract.TcpIpClientEntry.delete(m_ticd.getID());
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.DELETING_OK_ID, getString(R.string.text_odf_title_deleting), getString(R.string.text_odf_message_deleting_ok), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
        }
    }

    public static Intent makeTCPIPClientPropActivity(Context context, long lID) {
        Intent intent = new Intent();
        intent.setClass(context, TCPIPClientPropActivity.class);
        intent.putExtra(TCPIPClientPropActivity.TCP_IP_CLIENT_ID, lID);
        return intent;
    }

}
