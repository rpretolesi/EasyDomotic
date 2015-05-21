package com.pretolesi.easydomotic.SensorValue;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import com.pretolesi.easydomotic.BaseValue.BaseValueData;
import com.pretolesi.easydomotic.BaseValue.BaseValueData.SensorTypeCalibr;
import com.pretolesi.easydomotic.BaseValue.BaseValuePropActivity;
import com.pretolesi.easydomotic.CustomControls.NumericEditText;
import com.pretolesi.easydomotic.CustomControls.StringEditText;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.dialogs.DialogActionID;
import com.pretolesi.easydomotic.dialogs.OkDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SensorValuePropActivity extends BaseValuePropActivity {
    private static final String TAG = "SensorValuePropAct";

    private NumericEditText m_id_et_min_nr_char_to_show;
    private NumericEditText m_id_et_nr_of_decimal;
    private StringEditText m_id_et_um;
    private NumericEditText m_id_et_update_millis;
    private CheckBox m_id_cb_read_only;

    // Sensor
    private Spinner m_id_spn_sensor_type;
    private Spinner m_id_spn_sensor_value;
    private CheckBox m_id_cb_sensor_enable_simulation;
    private NumericEditText m_id_et_sensor_ampl_k;
    private NumericEditText m_id_et_sensor_low_pass_filter_k;
    private NumericEditText m_id_et_sensor_sample_time_k;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_value_property_activity);

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

        // Sensor
        m_id_spn_sensor_type = (Spinner)findViewById(R.id.id_spn_sensor_type);
        m_id_spn_sensor_type.setSelection(-1);
        m_id_spn_sensor_value = (Spinner)findViewById(R.id.id_spn_sensor_value);
        m_id_spn_sensor_value.setSelection(-1);
        m_id_cb_sensor_enable_simulation = (CheckBox)findViewById(R.id.id_cb_sensor_enable_simulation);
        m_id_et_sensor_ampl_k = (NumericEditText)findViewById(R.id.id_et_sensor_ampl_k);
        m_id_et_sensor_ampl_k.setInputLimit(BaseValueData.SensorAmplKMinValue, BaseValueData.SensorAmplKMaxValue);
        m_id_et_sensor_ampl_k.setText(BaseValueData.SensorAmplKDefaulValue);
        m_id_et_sensor_low_pass_filter_k = (NumericEditText)findViewById(R.id.id_et_sensor_low_pass_filter_k);
        m_id_et_sensor_low_pass_filter_k.setInputLimit(BaseValueData.SensorLowPassFilterKMinValue, BaseValueData.SensorLowPassFilterKMaxValue);
        m_id_et_sensor_low_pass_filter_k.setText(BaseValueData.SensorLowPassFilterKDefaulValue);
        m_id_et_sensor_sample_time_k = (NumericEditText)findViewById(R.id.id_et_sensor_sample_time_k);
        m_id_et_sensor_sample_time_k.setInputLimit(BaseValueData.SensorSampleTimeMinValue, BaseValueData.SensorSampleTimeMaxValue);
        m_id_et_sensor_sample_time_k.setText(BaseValueData.SensorSampleTimeDefaulValue);

        // Get sensor list
        if(m_iTypeParameter == BaseValueData.TYPE_SENSOR_RAW_VALUE){
            SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if(sm != null){
                List<Sensor> ls = sm.getSensorList(Sensor.TYPE_ALL);
                m_id_spn_sensor_type.setAdapter(new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, ls));
            }
        } else if(m_iTypeParameter == BaseValueData.TYPE_SENSOR_CALIBR_VALUE){
            ArrayList<SensorTypeCalibr> alstc = SensorTypeCalibr.getListSensorTypeCalibr();
            m_id_spn_sensor_type.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, alstc));

        }
        m_id_spn_sensor_value.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, BaseValueData.SensorValue.values()));

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

        // Sensor
        if(m_id_spn_sensor_type != null) {
            long lItem = -1;
            try{
                lItem = m_bvd.getSensorTypeID();
            } catch (Exception ignore) { }
            m_id_spn_sensor_type.setSelection((int) lItem);
        }
        if(m_id_spn_sensor_value != null) {
            long lItem = -1;
            try{
                lItem = m_bvd.getSensorValueID();
            } catch (Exception ignore) { }
            m_id_spn_sensor_value.setSelection((int) lItem);
        }
        if(m_id_cb_sensor_enable_simulation != null) {
            m_id_cb_sensor_enable_simulation.setChecked(m_bvd.getSensorEnableSimulation());
        }
        if (m_id_et_sensor_ampl_k != null) {
            m_id_et_sensor_ampl_k.setText(Float.toString(m_bvd.getSensorAmplK()));
        }
        if (m_id_et_sensor_low_pass_filter_k != null) {
            m_id_et_sensor_low_pass_filter_k.setText(Float.toString(m_bvd.getSensorLowPassFilterK()));
        }
        if (m_id_et_sensor_sample_time_k != null) {
            m_id_et_sensor_sample_time_k.setText(Integer.toString(m_bvd.getSensorSampleTime()));
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

            // Sensor
            if(m_id_spn_sensor_type != null) {
                m_bvd.setSensorTypeID(m_id_spn_sensor_type.getSelectedItemId());
            }

            if(m_id_spn_sensor_value != null) {
                m_bvd.setSensorValueID(m_id_spn_sensor_value.getSelectedItemId());
            }
            if(m_id_cb_sensor_enable_simulation != null) {
                m_bvd.setSensorEnableSimulation(m_id_cb_sensor_enable_simulation.isChecked());
            }
            if (m_id_et_sensor_ampl_k != null) {
                m_bvd.setSensorAmplK(Float.parseFloat(m_id_et_sensor_ampl_k.getText().toString()));
            }
            if (m_id_et_sensor_low_pass_filter_k != null) {
                m_bvd.setSensorLowPassFilterK(Float.parseFloat(m_id_et_sensor_low_pass_filter_k.getText().toString()));
            }
            if (m_id_et_sensor_sample_time_k != null) {
                m_bvd.setSensorSampleTime(Integer.parseInt(m_id_et_sensor_sample_time_k.getText().toString()));
            }

        } catch (Exception ex) {
            OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.VALUE_ERROR_ID, getString(R.string.text_odf_title_format_not_valid), getString(R.string.text_odf_message_format_not_valid), getString(R.string.text_odf_message_ok_button))
                    .show(getFragmentManager(), "");
            return false;
        }

        return bRes;
    }
}
