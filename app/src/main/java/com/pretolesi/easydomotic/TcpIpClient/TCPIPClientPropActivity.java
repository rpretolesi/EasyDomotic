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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easydomotic.CustomControls.EDEditText;
import com.pretolesi.easydomotic.LoadersUtils.Loaders;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.dialogs.DialogActionID;
import com.pretolesi.easydomotic.dialogs.DialogOriginID;
import com.pretolesi.easydomotic.dialogs.OkDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TCPIPClientPropActivity extends Activity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OkDialogFragment.OkDialogFragmentCallbacks {

    private static final String TAG = "TCPIPClientPropActivity";

    private TextView m_id_stica_tv_title;
    private TextView m_id_stica_tv_server_ip_address;
    private EDEditText m_id_stica_et_server_ip_address;
    private TextView m_id_stica_tv_server_port;
    private EDEditText m_id_stica_et_server_port;
    private TextView m_id_stica_tv_timeout;
    private EDEditText m_id_stica_et_timeout;
    private TextView m_id_stica_tv_comm_send_data_delay;
    private EDEditText m_id_stica_et_comm_send_data_delay;
    private Spinner m_id_stica_spn_protocol;
    private SimpleCursorAdapter m_SCAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_tcp_ip_client_activity);

        m_id_stica_tv_title = (TextView)findViewById(R.id.id_stica_tv_title);
        m_id_stica_tv_server_ip_address = (TextView)findViewById(R.id.id_stica_tv_server_ip_address);
        m_id_stica_et_server_ip_address = (EDEditText)findViewById(R.id.id_stica_et_server_ip_address);
        m_id_stica_et_server_ip_address.setInputLimit(SQLContract.SettingID.TCP_IP_CLIENT_ADDRESS.getMinValue(), SQLContract.SettingID.TCP_IP_CLIENT_ADDRESS.getMaxValue());
        m_id_stica_tv_server_port = (TextView)findViewById(R.id.id_stica_tv_server_port);
        m_id_stica_et_server_port = (EDEditText)findViewById(R.id.id_stica_et_server_port);
        m_id_stica_et_server_port.setInputLimit(SQLContract.SettingID.TCP_IP_CLIENT_PORT.getMinValue(), SQLContract.SettingID.TCP_IP_CLIENT_PORT.getMaxValue());
        m_id_stica_tv_timeout = (TextView)findViewById(R.id.id_stica_tv_timeout);
        m_id_stica_et_timeout = (EDEditText)findViewById(R.id.id_stica_et_timeout);
        m_id_stica_et_timeout.setInputLimit(SQLContract.SettingID.TCP_IP_CLIENT_TIMEOUT.getMinValue(), SQLContract.SettingID.TCP_IP_CLIENT_TIMEOUT.getMaxValue());
        m_id_stica_tv_comm_send_data_delay = (TextView)findViewById(R.id.id_stica_tv_comm_send_data_delay);
        m_id_stica_et_comm_send_data_delay = (EDEditText)findViewById(R.id.id_stica_et_comm_send_data_delay);
        m_id_stica_et_comm_send_data_delay.setInputLimit(SQLContract.SettingID.TCP_IP_CLIENT_COMM_SEND_DATA_DELAY.getMinValue(), SQLContract.SettingID.TCP_IP_CLIENT_COMM_SEND_DATA_DELAY.getMaxValue());
        m_id_stica_spn_protocol = (Spinner) findViewById(R.id.id_stica_spn_protocol);

        setActionBar();
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.

        m_id_stica_spn_protocol.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, TCPIPClient.Protocol.values()));

        getLoaderManager().initLoader(Loaders.TCP_IP_CLIENT_PARAMETER_ID + SQLContract.SettingID.TCP_IP_CLIENT_ADDRESS.getValue(), null, this);
        getLoaderManager().initLoader(Loaders.TCP_IP_CLIENT_PARAMETER_ID + SQLContract.SettingID.TCP_IP_CLIENT_PORT.getValue(), null, this);
        getLoaderManager().initLoader(Loaders.TCP_IP_CLIENT_PARAMETER_ID + SQLContract.SettingID.TCP_IP_CLIENT_TIMEOUT.getValue(), null, this);
        getLoaderManager().initLoader(Loaders.TCP_IP_CLIENT_PARAMETER_ID + SQLContract.SettingID.TCP_IP_CLIENT_COMM_SEND_DATA_DELAY.getValue(), null, this);
        getLoaderManager().initLoader(Loaders.TCP_IP_CLIENT_PARAMETER_ID + SQLContract.SettingID.TCP_IP_CLIENT_PROTOCOL.getValue(), null, this);
    }

    public void setActionBar() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
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
//                delete(DialogOriginID.ORIGIN_MENU_BUTTON_ID);
                return true;

            case R.id.id_item_menu_save:
                // Save Data
                save(DialogOriginID.ORIGIN_MENU_BUTTON_ID);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, this.toString() + ": " + "onCreateLoader() id:" + id);
        if(id == Loaders.TCP_IP_CLIENT_PARAMETER_ID + SQLContract.SettingID.TCP_IP_CLIENT_ADDRESS.getValue()){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.Settings.load(SQLContract.SettingID.TCP_IP_CLIENT_ADDRESS);
                }
            };
        }
        if(id == Loaders.TCP_IP_CLIENT_PARAMETER_ID + SQLContract.SettingID.TCP_IP_CLIENT_PORT.getValue()){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.Settings.load(SQLContract.SettingID.TCP_IP_CLIENT_PORT);
                }
            };
        }
        if(id == Loaders.TCP_IP_CLIENT_PARAMETER_ID + SQLContract.SettingID.TCP_IP_CLIENT_TIMEOUT.getValue()){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.Settings.load(SQLContract.SettingID.TCP_IP_CLIENT_TIMEOUT);
                }
            };
        }
        if(id == Loaders.TCP_IP_CLIENT_PARAMETER_ID + SQLContract.SettingID.TCP_IP_CLIENT_COMM_SEND_DATA_DELAY.getValue()){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.Settings.load(SQLContract.SettingID.TCP_IP_CLIENT_COMM_SEND_DATA_DELAY);
                }
            };
        }
        if(id == Loaders.TCP_IP_CLIENT_PARAMETER_ID + SQLContract.SettingID.TCP_IP_CLIENT_PROTOCOL.getValue()){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.Settings.load(SQLContract.SettingID.TCP_IP_CLIENT_PROTOCOL);
                }
            };
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(loader.getId() == Loaders.TCP_IP_CLIENT_PARAMETER_ID + SQLContract.SettingID.TCP_IP_CLIENT_ADDRESS.getValue()) {
            if(m_id_stica_et_server_ip_address != null) {

                m_id_stica_et_server_ip_address.setText(SQLContract.Settings.get(cursor, SQLContract.SettingID.TCP_IP_CLIENT_ADDRESS));
            }
        }
        if(loader.getId() == Loaders.TCP_IP_CLIENT_PARAMETER_ID + SQLContract.SettingID.TCP_IP_CLIENT_PORT.getValue()) {
            if(m_id_stica_et_server_port != null) {

                m_id_stica_et_server_port.setText(SQLContract.Settings.get(cursor, SQLContract.SettingID.TCP_IP_CLIENT_PORT));
            }
        }
        if(loader.getId() == Loaders.TCP_IP_CLIENT_PARAMETER_ID + SQLContract.SettingID.TCP_IP_CLIENT_TIMEOUT.getValue()) {
            if(m_id_stica_et_timeout != null) {

                m_id_stica_et_timeout.setText(SQLContract.Settings.get(cursor, SQLContract.SettingID.TCP_IP_CLIENT_TIMEOUT));
            }
        }
        if(loader.getId() == Loaders.TCP_IP_CLIENT_PARAMETER_ID + SQLContract.SettingID.TCP_IP_CLIENT_COMM_SEND_DATA_DELAY.getValue()) {
            if(m_id_stica_et_comm_send_data_delay != null) {

                m_id_stica_et_comm_send_data_delay.setText(SQLContract.Settings.get(cursor, SQLContract.SettingID.TCP_IP_CLIENT_COMM_SEND_DATA_DELAY));
            }
        }
        if(loader.getId() == Loaders.TCP_IP_CLIENT_PARAMETER_ID + SQLContract.SettingID.TCP_IP_CLIENT_PROTOCOL.getValue()) {
            if(m_id_stica_spn_protocol != null) {
                int iItem = -1;
                try{
                    iItem = Integer.parseInt(SQLContract.Settings.get(cursor, SQLContract.SettingID.TCP_IP_CLIENT_PROTOCOL));
                } catch (Exception ignore) { }
                m_id_stica_spn_protocol.setSelection(iItem);
            }
        }

        Log.d(TAG, this.toString() + ": " + "onLoadFinished() id: " + loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onOkDialogFragmentClickListener(int iDialogOriginID, int iDialogActionID) {
        if(iDialogOriginID == DialogOriginID.ORIGIN_MENU_BUTTON_ID){
            if(iDialogActionID == DialogActionID.SAVING_OK_ID) {
                // Save ok, exit
                finish();
            }
        }
    }

    private void save(int iDialogOriginID) {
        if(!validateInputData(findViewById(android.R.id.content)))
            return;

        if(m_id_stica_et_server_ip_address == null || !SQLContract.Settings.set(SQLContract.SettingID.TCP_IP_CLIENT_ADDRESS, String.valueOf(m_id_stica_et_server_ip_address.getText().toString()))) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return;
        }
        if(m_id_stica_et_server_port == null || !SQLContract.Settings.set(SQLContract.SettingID.TCP_IP_CLIENT_PORT, String.valueOf(m_id_stica_et_server_port.getText().toString()))) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return;
        }
        if(m_id_stica_et_timeout == null || !SQLContract.Settings.set(SQLContract.SettingID.TCP_IP_CLIENT_TIMEOUT, String.valueOf(m_id_stica_et_timeout.getText().toString()))) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return;
        }
        if(m_id_stica_et_comm_send_data_delay == null || !SQLContract.Settings.set(SQLContract.SettingID.TCP_IP_CLIENT_COMM_SEND_DATA_DELAY, String.valueOf(m_id_stica_et_comm_send_data_delay.getText().toString()))) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return;
        }

        if(m_id_stica_spn_protocol == null || !SQLContract.Settings.set(SQLContract.SettingID.TCP_IP_CLIENT_PROTOCOL, String.valueOf(m_id_stica_spn_protocol.getSelectedItemPosition()))) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return;
        }

        OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_OK_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_ok), getString(R.string.text_odf_message_ok_button))
                .show(getFragmentManager(), "");

    }

    private static boolean validateInputData(View v){
        List<View> visited = new ArrayList<>();
        List<View> unvisited = new ArrayList<>();
        unvisited.add(v);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            visited.add(child);
            if (!(child instanceof ViewGroup)) continue;
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i=0; i<childCount; i++) unvisited.add(group.getChildAt(i));
        }

        for(View vet : visited){
            if(vet instanceof EDEditText){
                if(!((EDEditText)vet).validateInputLimit())
                    return false;
            }

        }

        return true;
    }

    public static Intent makeTCPIPClientConfigActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, TCPIPClientPropActivity.class);
        return intent;
    }

}
