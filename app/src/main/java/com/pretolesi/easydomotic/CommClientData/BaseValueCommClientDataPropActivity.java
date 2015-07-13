package com.pretolesi.easydomotic.CommClientData;

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
import android.widget.Spinner;
import android.widget.TextView;

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
public class BaseValueCommClientDataPropActivity extends Activity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OkDialogFragment.OkDialogFragmentCallbacks,
        YesNoDialogFragment.YesNoDialogFragmentCallbacks{

    private static final String TAG = "TCPIPClientPropAct";

    protected static final String TRANSP_PROTOCOL = "transp_protocol";
    protected static final String BASE_VALUE_COMM_CLIENT_ID = "Base_Value_Comm_Client_ID";

    protected TextView m_id_tv_server_name;
    protected StringEditText m_id_et_server_name;
    protected TextView m_id_tv_server_address;
    protected StringEditText m_id_et_server_address;
    protected TextView m_id_stica_tv_server_port;
    protected NumericEditText m_id_stica_et_server_port;
    protected NumericEditText m_id_stica_et_timeout;
    protected NumericEditText m_id_stica_et_comm_send_data_delay;
    protected TextView m_id_stica_tv_comm_receive_wait_data;
    protected NumericEditText m_id_stica_et_comm_receive_wait_data;
    protected TextView m_id_stica_tv_comm_nr_max_err;
    protected NumericEditText m_id_stica_et_comm_nr_max_err;
    protected Spinner m_id_stica_spn_protocol;
    protected TextView m_id_stica_tv_protocol_field_1;
    protected NumericEditText m_id_stica_et_protocol_field_1;
    protected TextView m_id_stica_tv_protocol_field_2;
    protected NumericEditText m_id_stica_et_protocol_field_2;

    protected BaseValueTranspProtocolClientData m_ticd;
    protected long m_lIDParameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_comm_client_activity);

        m_id_tv_server_name = (TextView)findViewById(R.id.id_tv_server_name);
        m_id_et_server_name = (StringEditText)findViewById(R.id.id_stica_et_server_name);
        m_id_et_server_name.setInputLimit(BaseValueTranspProtocolClientData.NameMinChar, BaseValueTranspProtocolClientData.NameMaxChar);
        m_id_et_server_name.setText(BaseValueTranspProtocolClientData.NameDefaultValue);
        m_id_tv_server_address = (TextView)findViewById(R.id.id_tv_server_address);
        m_id_et_server_address = (StringEditText)findViewById(R.id.id_et_server_address);
        m_id_et_server_address.setInputLimit(BaseValueTranspProtocolClientData.AddressMinChar, BaseValueTranspProtocolClientData.AddressMaxChar);
        m_id_et_server_address.setText(BaseValueTranspProtocolClientData.AddressDefaultValue);
        m_id_stica_tv_server_port = (TextView)findViewById(R.id.id_stica_tv_server_port);
        m_id_stica_et_server_port = (NumericEditText)findViewById(R.id.id_stica_et_server_port);
        m_id_stica_et_server_port.setInputLimit(BaseValueTranspProtocolClientData.PortMinValue, BaseValueTranspProtocolClientData.PortMaxValue);
        m_id_stica_et_server_port.setText(BaseValueTranspProtocolClientData.PortDefaultValue);
        m_id_stica_et_timeout = (NumericEditText)findViewById(R.id.id_stica_et_timeout);
        m_id_stica_et_timeout.setInputLimit(BaseValueTranspProtocolClientData.TimeoutMinValue, BaseValueTranspProtocolClientData.TimeoutMaxValue);
        m_id_stica_et_timeout.setText(BaseValueTranspProtocolClientData.TimeoutDefaultValue);
        m_id_stica_et_comm_send_data_delay = (NumericEditText)findViewById(R.id.id_stica_et_comm_send_data_delay);
        m_id_stica_et_comm_send_data_delay.setInputLimit(BaseValueTranspProtocolClientData.CommSendDelayDataMinValue, BaseValueTranspProtocolClientData.CommSendDelayDataMaxValue);
        m_id_stica_et_comm_send_data_delay.setText(BaseValueTranspProtocolClientData.CommSendDelayDataDefaultValue);
        m_id_stica_tv_comm_receive_wait_data = (TextView)findViewById(R.id.id_stica_tv_comm_receive_wait_data);
        m_id_stica_et_comm_receive_wait_data = (NumericEditText)findViewById(R.id.id_stica_et_comm_receive_wait_data);
        m_id_stica_et_comm_receive_wait_data.setInputLimit(BaseValueTranspProtocolClientData.CommReceiveWaitDataMinValue, BaseValueTranspProtocolClientData.CommReceiveWaitDataMaxValue);
        m_id_stica_et_comm_receive_wait_data.setText(BaseValueTranspProtocolClientData.CommReceiveWaitDataDefaultValue);
        m_id_stica_tv_comm_nr_max_err = (TextView)findViewById(R.id.id_stica_tv_comm_nr_max_err);
        m_id_stica_et_comm_nr_max_err = (NumericEditText)findViewById(R.id.id_stica_et_comm_nr_max_err);
        m_id_stica_et_comm_nr_max_err.setInputLimit(BaseValueTranspProtocolClientData.CommNrMaxOfErrMinValue, BaseValueTranspProtocolClientData.CommNrMaxOfErrMaxValue);
        m_id_stica_et_comm_nr_max_err.setText(BaseValueTranspProtocolClientData.CommNrMaxOfErrDefaultValue);
        m_id_stica_spn_protocol = (Spinner) findViewById(R.id.id_stica_spn_protocol);
        m_id_stica_spn_protocol.setSelection(BaseValueTranspProtocolClientData.ProtocolDefaulValue);
        m_id_stica_tv_protocol_field_1 = (TextView)findViewById(R.id.id_stica_tv_protocol_field_1);
        m_id_stica_et_protocol_field_1 = (NumericEditText)findViewById(R.id.id_stica_et_protocol_field_1);
        m_id_stica_et_protocol_field_1.setInputLimit(BaseValueTranspProtocolClientData.HeadMinValue, BaseValueTranspProtocolClientData.HeadMaxValue);
        m_id_stica_et_protocol_field_1.setText(BaseValueTranspProtocolClientData.HeadDefaulValue);
        m_id_stica_tv_protocol_field_2 = (TextView)findViewById(R.id.id_stica_tv_protocol_field_2);
        m_id_stica_et_protocol_field_2 = (NumericEditText)findViewById(R.id.id_stica_et_protocol_field_2);
        m_id_stica_et_protocol_field_2.setInputLimit(BaseValueTranspProtocolClientData.TailMinValue, BaseValueTranspProtocolClientData.TailMaxValue);
        m_id_stica_et_protocol_field_2.setText(BaseValueTranspProtocolClientData.TailDefaulValue);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        Intent intent = getIntent();
        if(intent != null) {
            m_lIDParameter = intent.getLongExtra(BASE_VALUE_COMM_CLIENT_ID, -1);
        }
    }

    protected void setActionBarTitle(String strTitle) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(strTitle);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().initLoader(Loaders.BASE_VALUE_COMM_CLIENT_LOADER_ID, null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getLoaderManager().destroyLoader(Loaders.BASE_VALUE_COMM_CLIENT_LOADER_ID);
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
        if(id == Loaders.BASE_VALUE_COMM_CLIENT_LOADER_ID){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.TranspProtocolClientEntry.load(m_lIDParameter);
                }
            };
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(loader.getId() == Loaders.BASE_VALUE_COMM_CLIENT_LOADER_ID) {
            ArrayList<BaseValueTranspProtocolClientData> alticd = SQLContract.TranspProtocolClientEntry.get(cursor);
            if(alticd != null && !alticd.isEmpty()){
                m_ticd = alticd.get(0);
                m_ticd.setSaved(false);
                getBaseValue();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onYesNoDialogFragmentClickListener(int iDialogOriginID, int iDialogActionID, boolean bYes, boolean bNo) {
        if(iDialogOriginID == DialogOriginID.ORIGIN_MENU_BUTTON_ID) {
            if(iDialogActionID == DialogActionID.SAVE_ITEM_ALREADY_EXIST_CONFIRM_ID) {
                if(bYes) {
                    // Save ok, exit
                    if(setBaseData(iDialogOriginID)){
                        if(SQLContract.TranspProtocolClientEntry.save(m_ticd)){
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
                    deleteBaseValueCommClientData(iDialogOriginID);                }
                if(bNo) {
                    // No action...
                }
            }
        }

        if(iDialogOriginID == DialogOriginID.ORIGIN_BACK_BUTTON_ID) {
            if(iDialogActionID == DialogActionID.SAVE_ITEM_ALREADY_EXIST_CONFIRM_ID) {
                if(bYes) {
                    // Save ok, exit
                    if(setBaseData(iDialogOriginID)){
                        if(SQLContract.TranspProtocolClientEntry.save(m_ticd)){
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

    private void getBaseValue() {
        if (m_ticd == null) {
            return;
        }

        if(m_id_et_server_name != null) {
            m_id_et_server_name.setText(m_ticd.getName());
        }
        if(m_id_et_server_address != null) {
            m_id_et_server_address.setText(m_ticd.getAddress());
        }
        if(m_id_stica_et_server_port != null) {
            m_id_stica_et_server_port.setText(Integer.toString(m_ticd.getPort()));
        }
        if(m_id_stica_et_timeout != null) {
            m_id_stica_et_timeout.setText(Integer.toString(m_ticd.getTimeout()));
        }
        if(m_id_stica_et_comm_send_data_delay != null) {
            m_id_stica_et_comm_send_data_delay.setText(Integer.toString(m_ticd.getSendDelayData()));
        }
        if(m_id_stica_et_comm_receive_wait_data != null) {
            m_id_stica_et_comm_receive_wait_data.setText(Integer.toString(m_ticd.getReceiveWaitData()));
        }
        if(m_id_stica_et_comm_nr_max_err != null) {
            m_id_stica_et_comm_nr_max_err.setText(Integer.toString(m_ticd.getNrMaxOfErr()));
        }
        if(m_id_stica_spn_protocol != null) {
            BaseValueTranspProtocolClientData.CommProtocolType p = m_ticd.getCommProtocolType();
            if(p != null){
                for (int position = 0; position < m_id_stica_spn_protocol.getCount(); position++)
                {
                    Object obj = m_id_stica_spn_protocol.getItemAtPosition(position);
                    if (obj != null && obj instanceof BaseValueTranspProtocolClientData.CommProtocolType && obj == p){
                        m_id_stica_spn_protocol.setSelection(position);
                        break;
                    }
                }
            }
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

        if(m_id_et_server_name != null) {

            if(!((m_ticd != null && (m_ticd.getID() > 0)) || (m_lIDParameter > 0))){
                if(setBaseData(iDialogOriginID)){
                    if(SQLContract.TranspProtocolClientEntry.save(m_ticd)){
                        OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_OK_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_ok), getString(R.string.text_odf_message_ok_button))
                                .show(getFragmentManager(), "");
                    } else {
                        OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                                .show(getFragmentManager(), "");
                    }
                }
            } else {
                YesNoDialogFragment.newInstance(iDialogOriginID,
                        DialogActionID.SAVE_ITEM_ALREADY_EXIST_CONFIRM_ID,
                        getString(R.string.text_yndf_title_base_value_name_already_exist),
                        getString(R.string.text_yndf_message_base_value_name_already_exist_confirmation),
                        getString(R.string.text_yndf_btn_yes),
                        getString(R.string.text_yndf_btn_no)
                ).show(getFragmentManager(), "");
            }
        }
    }

    protected boolean setBaseData(int iDialogOriginID){

        if (m_ticd == null) {
            return false;
        }

        m_ticd.setName(m_id_et_server_name.getText().toString());
        m_ticd.setAddress(m_id_et_server_address.getText().toString());
        m_ticd.setPort(Integer.parseInt(m_id_stica_et_server_port.getText().toString()));
        m_ticd.setTimeout(Integer.parseInt(m_id_stica_et_timeout.getText().toString()));
        m_ticd.setSendDelayData(Integer.parseInt(m_id_stica_et_comm_send_data_delay.getText().toString()));
        m_ticd.setReceiveWaitData(Integer.parseInt(m_id_stica_et_comm_receive_wait_data.getText().toString()));
        m_ticd.setNrMaxOfErr(Integer.parseInt(m_id_stica_et_comm_nr_max_err.getText().toString()));
        if(m_id_stica_spn_protocol != null && m_id_stica_spn_protocol.getSelectedItem() instanceof BaseValueTranspProtocolClientData.CommProtocolType){
            m_ticd.setCommProtocolType((BaseValueTranspProtocolClientData.CommProtocolType) m_id_stica_spn_protocol.getSelectedItem());
        }
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
    private void deleteBaseValueCommClientData(int iDialogOriginID) {
        long lID = -1;
//        if(m_bvd != null && m_bvd.getID() > 0) {
//            lID = m_bvd.getID();
//        } else {
            lID = m_lIDParameter;
//        }
        if(lID > 0){
            if(SQLContract.TranspProtocolClientEntry.delete(lID)){
                OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.DELETING_OK_ID, getString(R.string.text_odf_title_deleting), getString(R.string.text_odf_message_deleting_ok), getString(R.string.text_odf_message_ok_button))
                        .show(getFragmentManager(), "");
                return;
            }
        }

        OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.DELETING_ERROR_ID, getString(R.string.text_odf_title_deleting), getString(R.string.text_odf_message_deleting_error), getString(R.string.text_odf_message_ok_button))
                .show(getFragmentManager(), "");

    }

    public static Intent makeBaseValueCommClientPropActivityByTranspProtocol(Context context, Class cls, long lTranspProtocolType) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(BaseValueCommClientDataPropActivity.TRANSP_PROTOCOL, lTranspProtocolType);
        return intent;
    }

    public static Intent makeBaseValueCommClientPropActivityByIDAndTranspProtocol(Context context, Class cls, long lID, long lTranspProtocolType) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(BaseValueCommClientDataPropActivity.BASE_VALUE_COMM_CLIENT_ID, lID);
        intent.putExtra(BaseValueCommClientDataPropActivity.TRANSP_PROTOCOL, lTranspProtocolType);
        return intent;
    }
}
