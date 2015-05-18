package com.pretolesi.easydomotic.LightSwitch;

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
import com.pretolesi.easydomotic.BaseValue.BaseValueData;
import com.pretolesi.easydomotic.BaseValue.BaseValuePropActivity;
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
public class LightSwitchPropActivity extends BaseValuePropActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OkDialogFragment.OkDialogFragmentCallbacks,
        YesNoDialogFragment.YesNoDialogFragmentCallbacks{
    private static final String TAG = "LightSwitchPropAct";

    private static final String ROOM_ID = "Room_ID";
    private static final String LIGHT_SWITCH_ID = "Light_Switch_ID";
    private static final String LIGHT_SWITCH_DATA = "Light_Switch_Data";

 //   private CharSequence mTitle;

    private NumericEditText m_id_et_write_value_off;
    private NumericEditText m_id_et_write_value_off_on;
    private NumericEditText m_id_et_write_value_on_off;
    private NumericEditText m_id_et_write_value_on;

    private BaseValueData m_bvd;
    private long m_lRoomIDParameter;
    private long m_lIDParameter;
    private BaseValueData m_bvdParameter;
    private SimpleCursorAdapter m_TcpIpClientAdapter;

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
    protected void updateBaseValue() {
        super.updateBaseValue();

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

    @Override
    protected void save(int iDialogOriginID) {
        super.save(iDialogOriginID);
    }

    @Override
    protected void saveBaseData(int iDialogOriginID){
        finire qui
        if (m_bvd == null) {
            m_bvd = new BaseValueData();
        }

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
                return ;
            }
        }

        if(SQLContract.BaseValueEntry.save(m_bvd)){
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_OK_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_ok), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
        } else {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.SAVING_ERROR_ID, getString(R.string.text_odf_title_saving), getString(R.string.text_odf_message_saving_error), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
        }
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
    private void deleteLightSwitchData(int iDialogOriginID) {
        if(m_bvd != null) {
            SQLContract.LightSwitchEntry.delete(m_bvd.getID(), m_bvd.getRoomID());
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


    public static Intent makeLightSwitchPropActivity(Context context, long lRoomID, long lID) {
        Intent intent = new Intent();
        intent.setClass(context, LightSwitchPropActivity.class);
        intent.putExtra(LightSwitchPropActivity.ROOM_ID, lRoomID);
        intent.putExtra(LightSwitchPropActivity.LIGHT_SWITCH_ID, lID);
        return intent;
    }

    public static Intent makeLightSwitchPropActivity(Context context, LightSwitchData lsd) {
        Intent intent = new Intent();
        intent.setClass(context, LightSwitchPropActivity.class);
        intent.putExtra(LightSwitchPropActivity.LIGHT_SWITCH_DATA, lsd);
        return intent;
    }

}
