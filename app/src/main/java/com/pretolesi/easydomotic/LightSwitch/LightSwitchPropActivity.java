package com.pretolesi.easydomotic.LightSwitch;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SimpleCursorAdapter;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easydomotic.BaseValue.BaseValueData;
import com.pretolesi.easydomotic.BaseValue.BaseValuePropActivity;
import com.pretolesi.easydomotic.CustomControls.NumericEditText;
import com.pretolesi.easydomotic.Orientation;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.dialogs.DialogActionID;
import com.pretolesi.easydomotic.dialogs.OkDialogFragment;
import com.pretolesi.easydomotic.dialogs.YesNoDialogFragment;

/**
 *
 */
public class LightSwitchPropActivity extends BaseValuePropActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OkDialogFragment.OkDialogFragmentCallbacks,
        YesNoDialogFragment.YesNoDialogFragmentCallbacks{
    private static final String TAG = "LightSwitchPropAct";

    private NumericEditText m_id_et_write_value_off;
    private NumericEditText m_id_et_write_value_off_on;
    private NumericEditText m_id_et_write_value_on_off;
    private NumericEditText m_id_et_write_value_on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.light_switch_property_activity);

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


        // Base
        onBaseCreate();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_light_switch_property_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void getBaseValue() {
        super.getBaseValue();

        // Dati
        if (m_bvd != null) {
            if (m_id_spn_tcp_ip_client_protocol != null && m_id_cb_enable_tcp_ip_client_protocol != null) {
                for (int i = 0; i < m_id_spn_tcp_ip_client_protocol.getCount(); i++) {
                    Cursor value = (Cursor) m_id_spn_tcp_ip_client_protocol.getItemAtPosition(i);
                    if (value != null) {
                        long id = value.getLong(value.getColumnIndex("_id"));
                        if (id == m_bvd.getProtTcpIpClientID()) {
                            m_id_et_write_value_off.setEnabled(m_bvd.getProtTcpIpClientEnable());
                            m_id_et_write_value_off_on.setEnabled(m_bvd.getProtTcpIpClientEnable());
                            m_id_et_write_value_on_off.setEnabled(m_bvd.getProtTcpIpClientEnable());
                            m_id_et_write_value_on.setEnabled(m_bvd.getProtTcpIpClientEnable());
                        }
                    }
                }
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
/*
    @Override
    protected void save(int iDialogOriginID) {
        super.save(iDialogOriginID);
    }
*/
    @Override
    protected boolean setBaseData(int iDialogOriginID){
        boolean bRes = super.setBaseData(iDialogOriginID);
        if (m_bvd == null) {
            m_bvd = new BaseValueData();
        }

        // Type Light Switch
        m_bvd.setType(BaseValueData.TYPE_LIGHT_SWITCH);

        if(m_bvd.getProtTcpIpClientEnable()){
            if(m_id_spn_tcp_ip_client_protocol != null) {
                m_bvd.setProtTcpIpClientID(m_id_spn_tcp_ip_client_protocol.getSelectedItemId());
            }

            try {

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
                return false;
            }
        }

        return bRes;

    }

    public static Intent makeLightSwitchPropActivity(Context context, long lRoomID, long lID) {
        Intent intent = new Intent();
        intent.setClass(context, BaseValuePropActivity.class);
        intent.putExtra(BaseValuePropActivity.ROOM_ID, lRoomID);
        intent.putExtra(BaseValuePropActivity.BASE_VALUE_ID, lID);
        return intent;
    }

    public static Intent makeLightSwitchPropActivity(Context context, BaseValueData bvd) {
        Intent intent = new Intent();
        intent.setClass(context, BaseValuePropActivity.class);
        intent.putExtra(BaseValuePropActivity.BASE_VALUE_DATA, bvd);
        return intent;
    }
}
