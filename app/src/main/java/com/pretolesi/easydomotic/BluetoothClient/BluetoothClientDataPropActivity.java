package com.pretolesi.easydomotic.BluetoothClient;

import android.app.ActionBar;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easydomotic.CommClientData.BaseValueCommClientData;
import com.pretolesi.easydomotic.CommClientData.BaseValueCommClientDataPropActivity;
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
 * Created by ricca_000 on 03/06/2015.
 */
public class BluetoothClientDataPropActivity  extends BaseValueCommClientDataPropActivity {
    private static final String TAG = "BluetoothClientDataPropActivity";

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
    private BaseValueCommClientData m_ticd;
    private long m_lIDParameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_id_stica_et_server_port.setVisibility(View.INVISIBLE);
        m_id_stica_et_protocol_field_1.setVisibility(View.INVISIBLE);
        m_id_stica_et_protocol_field_2.setVisibility(View.INVISIBLE);
    }

    public static Intent makeBluetoothClientDataPropActivity(Context context, long lID) {
        Intent intent = new Intent();
        intent.setClass(context, BaseValueCommClientDataPropActivity.class);
        intent.putExtra(BaseValueCommClientDataPropActivity.TCP_IP_CLIENT_ID, lID);
        return intent;
    }
}
