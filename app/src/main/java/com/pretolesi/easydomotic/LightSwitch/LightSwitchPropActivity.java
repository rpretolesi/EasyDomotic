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
        m_id_et_write_value_off_on = (NumericEditText)findViewById(R.id.id_et_write_value_off_on);
        m_id_et_write_value_off_on.setText(BaseValueData.WriteValueOFFONDefault);
        m_id_et_write_value_on_off = (NumericEditText)findViewById(R.id.id_et_write_value_on_off);
        m_id_et_write_value_on_off.setText(BaseValueData.WriteValueONOFFDefault);
        m_id_et_write_value_on = (NumericEditText)findViewById(R.id.id_et_write_value_on);
        m_id_et_write_value_on.setText(BaseValueData.WriteValueONDefault);


        // Base
        onBaseCreate();

    }

    @Override
    protected void getBaseValue() {
        super.getBaseValue();

        // Dati
        if (m_bvd == null) {
            return ;
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

        // Type Light Switch
        m_bvd.setType(BaseValueData.TYPE_LIGHT_SWITCH);

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

        return bRes;
    }
}
