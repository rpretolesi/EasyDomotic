package com.pretolesi.easydomotic.LightSwitch;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easydomotic.BaseValue.BaseValueData;
import com.pretolesi.easydomotic.BaseValue.BaseValuePropActivity;
import com.pretolesi.easydomotic.CustomControls.NumericDataType;
import com.pretolesi.easydomotic.CustomControls.NumericEditText;
import com.pretolesi.easydomotic.Orientation;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.dialogs.DialogActionID;
import com.pretolesi.easydomotic.dialogs.OkDialogFragment;
import com.pretolesi.easydomotic.dialogs.YesNoDialogFragment;

/**
 *
 */
public class LightSwitchPropActivity extends BaseValuePropActivity {
    private static final String TAG = "LightSwitchPropAct";

    private NumericEditText m_id_et_update_millis;
    private CheckBox m_id_cb_read_only;
    private CheckBox m_id_cb_write_only;

    private NumericEditText m_id_et_write_value_off;
    private NumericEditText m_id_et_write_value_off_on;
    private NumericEditText m_id_et_write_value_on_off;
    private NumericEditText m_id_et_write_value_on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.light_switch_property_activity);

        // Base
        onBaseCreate();

        m_id_et_update_millis = (NumericEditText)findViewById(R.id.id_et_update_millis);
        m_id_et_update_millis.setInputLimit(BaseValueData.ValueUpdateMillisMinValue, BaseValueData.ValueUpdateMillisMaxValue);
        m_id_et_update_millis.setText(BaseValueData.ValueUpdateMillisDefaulValue);
        m_id_cb_read_only = (CheckBox)findViewById(R.id.id_cb_read_only);
        m_id_cb_write_only = (CheckBox)findViewById(R.id.id_cb_write_only);

        m_id_et_write_value_off = (NumericEditText)findViewById(R.id.id_et_write_value_off);
        m_id_et_write_value_off.setText(BaseValueData.WriteValueOFFDefault);
        m_id_et_write_value_off_on = (NumericEditText)findViewById(R.id.id_et_write_value_off_on);
        m_id_et_write_value_off_on.setText(BaseValueData.WriteValueOFFONDefault);
        m_id_et_write_value_off_on.setVisibility(View.INVISIBLE);
        m_id_et_write_value_on_off = (NumericEditText)findViewById(R.id.id_et_write_value_on_off);
        m_id_et_write_value_on_off.setText(BaseValueData.WriteValueONOFFDefault);
        m_id_et_write_value_on_off.setVisibility(View.INVISIBLE);
        m_id_et_write_value_on = (NumericEditText)findViewById(R.id.id_et_write_value_on);
        m_id_et_write_value_on.setText(BaseValueData.WriteValueONDefault);

        m_id_cb_read_only.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    m_id_cb_write_only.setChecked(false);
                }
            }
        });

        m_id_cb_write_only.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox) v).isChecked()){
                    m_id_cb_read_only.setChecked(false);
                }
            }
        });

    }

    @Override
    protected void getBaseValue() {
        super.getBaseValue();

        // set DataType as Short
        setDataType(NumericDataType.DataType.SHORT, false);

        // Dati
        if (m_bvd == null) {
            return ;
        }

        if (m_id_et_update_millis != null) {
            m_id_et_update_millis.setText(Integer.toString(m_bvd.getValueUpdateMillis()));
        }
        if (m_id_cb_read_only != null) {
            m_id_cb_read_only.setChecked(m_bvd.getValueReadOnly());
        }
        if (m_id_cb_write_only != null) {
            m_id_cb_write_only.setChecked(m_bvd.getValueWriteOnly());
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

    @Override
    protected boolean setBaseData(int iDialogOriginID){
        boolean bRes = super.setBaseData(iDialogOriginID);
        // Dati
        if (m_bvd == null) {
            return false;
        }

        try {

            if (m_id_et_update_millis != null) {
                m_bvd.setValueUpdateMillis(Integer.parseInt(m_id_et_update_millis.getText().toString()));
            }
            if (m_id_cb_read_only != null) {
                m_bvd.setValueReadOnly(m_id_cb_read_only.isChecked());
            }
            if (m_id_cb_write_only != null) {
                m_bvd.setValueWriteOnly(m_id_cb_write_only.isChecked());
            }
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
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.VALUE_ERROR_ID, getString(R.string.text_odf_title_protocol_not_valid), getString(R.string.text_odf_message_protocol_not_valid), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return false;
        }

        return bRes;
    }
}
