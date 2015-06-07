package com.pretolesi.easydomotic.BluetoothClient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.pretolesi.easydomotic.CommClientData.BaseValueCommClientDataPropActivity;
import com.pretolesi.easydomotic.R;


/**
 * Created by ricca_000 on 03/06/2015.
 */
public class BluetoothClientDataPropActivity  extends BaseValueCommClientDataPropActivity {
    private static final String TAG = "BluetoothClientDataPropActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_id_tv_server_name.setText(R.string.text_tv_server_bt_name);
        m_id_tv_server_address.setText(R.string.text_tv_server_bt_address);
        m_id_et_server_address.setText(R.string.text_et_server_bt_address);
        m_id_et_server_address.setHint(R.string.hint_et_server_bt_address);
        m_id_stica_tv_server_port.setVisibility(View.GONE);
        m_id_stica_et_server_port.setVisibility(View.GONE);
        m_id_stica_tv_protocol_field_1.setVisibility(View.GONE);
        m_id_stica_et_protocol_field_1.setVisibility(View.GONE);
        m_id_stica_tv_protocol_field_2.setVisibility(View.GONE);
        m_id_stica_et_protocol_field_2.setVisibility(View.GONE);
        // Set Listener
        m_id_et_server_address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Intent intent = new Intent();
                    intent.setClass(getBaseContext(), BluetoothClientConfiguration.class);
                    startActivity(intent);
                }
            }
        });

    }

}
