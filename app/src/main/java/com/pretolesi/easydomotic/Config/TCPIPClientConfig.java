package com.pretolesi.easydomotic.Config;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easydomotic.CustomControls.EDEditText;
import com.pretolesi.easydomotic.LoadersUtils.Loaders;
import com.pretolesi.easydomotic.R;

/**
 *
 */
public class TCPIPClientConfig extends Activity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "TCPIPClientConfig";

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
        m_id_stica_tv_server_port = (TextView)findViewById(R.id.id_stica_tv_server_port);
        m_id_stica_et_server_port = (EDEditText)findViewById(R.id.id_stica_et_server_port);
        m_id_stica_tv_timeout = (TextView)findViewById(R.id.id_stica_tv_timeout);
        m_id_stica_et_timeout = (EDEditText)findViewById(R.id.id_stica_et_timeout);
        m_id_stica_tv_comm_send_data_delay = (TextView)findViewById(R.id.id_stica_tv_comm_send_data_delay);
        m_id_stica_et_comm_send_data_delay = (EDEditText)findViewById(R.id.id_stica_et_comm_send_data_delay);
        m_id_stica_spn_protocol = (Spinner) findViewById(R.id.id_stica_spn_protocol);

        setActionBar();
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.

        m_SCAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[] {SQLContract.RoomEntry.COLUMN_NAME_TAG},
                new int[] {android.R.id.text1}, 0);

        m_id_spn_room.setAdapter(m_SCAdapter);

        getLoaderManager().initLoader(Loaders.TCP_IP_CLIENT_PARAMETER_ID, null, this);
        getLoaderManager().initLoader(Loaders.LIGHT_SWITCH_LOADER_ID, null, this);
    }

    public void setActionBar() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
