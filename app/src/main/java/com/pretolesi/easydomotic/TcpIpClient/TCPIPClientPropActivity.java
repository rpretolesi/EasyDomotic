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
 *
 */
public class TCPIPClientPropActivity extends BaseValueCommClientDataPropActivity {

    private static final String TAG = "TCPIPClientPropAct";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_id_tv_server_name.setText(R.string.text_tv_server_ip_name);
        m_id_tv_server_address.setText(R.string.text_tv_server_ip_address);
        m_id_et_server_address.setText(R.string.text_et_server_ip_address);
        m_id_et_server_address.setHint(R.string.hint_et_server_ip_address);
        m_id_stica_tv_protocol_field_1.setVisibility(View.GONE);
        m_id_stica_et_protocol_field_1.setVisibility(View.GONE);
        m_id_stica_tv_protocol_field_2.setVisibility(View.GONE);
        m_id_stica_et_protocol_field_2.setVisibility(View.GONE);
    }

}
