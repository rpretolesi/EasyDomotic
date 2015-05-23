package com.pretolesi.easydomotic.NumerValue;

import android.os.Bundle;
import android.widget.CheckBox;

import com.pretolesi.easydomotic.BaseValue.BaseValueData;
import com.pretolesi.easydomotic.BaseValue.BaseValuePropActivity;
import com.pretolesi.easydomotic.CustomControls.NumericEditText;
import com.pretolesi.easydomotic.CustomControls.StringEditText;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.dialogs.DialogActionID;
import com.pretolesi.easydomotic.dialogs.OkDialogFragment;

/**
 *
 */
public class NumericValuePropActivity extends BaseValuePropActivity {
    private static final String TAG = "NumericValuePropAct";

    private NumericEditText m_id_et_min_nr_char_to_show;
    private NumericEditText m_id_et_nr_of_decimal;
    private StringEditText m_id_et_um;
    private NumericEditText m_id_et_update_millis;
    private CheckBox m_id_cb_read_only;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numeric_value_property_activity);

        // Base
        onBaseCreate();

        m_id_et_min_nr_char_to_show = (NumericEditText)findViewById(R.id.id_et_min_nr_char_to_show);
        m_id_et_min_nr_char_to_show.setInputLimit(BaseValueData.ValueMinNrCharToShowMinValue, BaseValueData.ValueMinNrCharToShowMaxValue);
        m_id_et_min_nr_char_to_show.setText(BaseValueData.ValueMinNrCharToShowDefaulValue);
        m_id_et_nr_of_decimal = (NumericEditText)findViewById(R.id.id_et_nr_of_decimal);
        m_id_et_nr_of_decimal.setInputLimit(BaseValueData.ValueNrOfDecimalMinValue, BaseValueData.ValueNrOfDecimalMaxValue);
        m_id_et_nr_of_decimal.setText(BaseValueData.ValueNrOfDecimalDefaulValue);
        m_id_et_um = (StringEditText)findViewById(R.id.id_et_um);
        m_id_et_um.setInputLimit(BaseValueData.ValueUMMinValue, BaseValueData.ValueUMMaxValue);
        m_id_et_um.setText(BaseValueData.ValueUMDefaulValue);
        m_id_et_update_millis = (NumericEditText)findViewById(R.id.id_et_update_millis);
        m_id_et_update_millis.setInputLimit(BaseValueData.ValueUpdateMillisMinValue, BaseValueData.ValueUpdateMillisMaxValue);
        m_id_et_update_millis.setText(BaseValueData.ValueUpdateMillisDefaulValue);
        m_id_cb_read_only = (CheckBox)findViewById(R.id.id_cb_read_only);

    }
    @Override
    protected void getBaseValue() {
        super.getBaseValue();

        // Dati
        if (m_bvd == null) {
            return ;
        }

        if (m_id_et_min_nr_char_to_show != null) {
            m_id_et_min_nr_char_to_show.setText(Integer.toString(m_bvd.getValueMinNrCharToShow()));
        }
        if (m_id_et_nr_of_decimal != null) {
            m_id_et_nr_of_decimal.setText(Integer.toString(m_bvd.getValueNrOfDecimal()));
        }
        if (m_id_et_um != null) {
            m_id_et_um.setText(m_bvd.getValueUM());
        }
        if (m_id_et_update_millis != null) {
            m_id_et_update_millis.setText(Integer.toString(m_bvd.getValueUpdateMillis()));
        }
        if (m_id_cb_read_only != null) {
            m_id_cb_read_only.setChecked(m_bvd.getValueReadOnly());
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

            if (m_id_et_min_nr_char_to_show != null) {
                m_bvd.setValueMinNrCharToShow(Integer.parseInt(m_id_et_min_nr_char_to_show.getText().toString()));
            }
            if (m_id_et_nr_of_decimal != null) {
                m_bvd.setValueNrOfDecimal(Integer.parseInt(m_id_et_nr_of_decimal.getText().toString()));
            }
            if (m_id_et_um != null) {
                m_bvd.setValueUM(m_id_et_um.getText().toString());
            }
            if (m_id_et_update_millis != null) {
                m_bvd.setValueUpdateMillis(Integer.parseInt(m_id_et_update_millis.getText().toString()));
            }
            if (m_id_cb_read_only != null) {
                m_id_cb_read_only.setChecked(m_bvd.getValueReadOnly());
            }

        } catch (Exception ex) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.VALUE_ERROR_ID, getString(R.string.text_odf_title_format_not_valid), getString(R.string.text_odf_message_format_not_valid), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return false;
        }

        return bRes;
    }
}
