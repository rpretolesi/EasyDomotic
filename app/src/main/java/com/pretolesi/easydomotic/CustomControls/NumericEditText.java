package com.pretolesi.easydomotic.CustomControls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.CustomControls.NumericDataType.DataType;

import java.util.ArrayList;
import java.util.List;

/**
 *  Custom EditText
 */
public class NumericEditText extends EditText {

    private static final String TAG = "EDEditText";

    private DataType m_dt;
    private short m_shMin, m_shMax;
    private int m_iMin, m_iMax;
    private long m_lMin, m_lMax;
    private float m_fMin, m_fMax;
    private double m_dblMin, m_dblMax;

    // Default Constructor
    public NumericEditText(Context context) {
        super(context);
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    public NumericEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    public NumericEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    public void setInputLimit(short shMin, short shMax) {
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        m_dt = DataType.SHORT;
        m_shMin = shMin;
        m_shMax = shMax;
    }

    public void setInputLimit(int iMin, int iMax) {
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        m_dt = DataType.INT;
        m_iMin = iMin;
        m_iMax = iMax;
    }

    public void setInputLimit(long lMin, long lMax) {
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        m_dt = DataType.LONG;
        m_lMin = lMin;
        m_lMax = lMax;
    }

    public void setInputLimit(float fMin, float fMax) {
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        m_dt = DataType.FLOAT;
        m_fMin = fMin;
        m_fMax = fMax;
    }

    public void setInputLimit(double dblMin, double dblMax) {
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        m_dt = DataType.DOUBLE;
        m_dblMin = dblMin;
        m_dblMax = dblMax;
    }

    public boolean validateInputLimit(){
        if(m_dt != null) {
            switch (m_dt) {
                case SHORT:
                    try {
                        short shValue = Short.parseShort(this.getText().toString());
                        if (!isInRange(m_shMin, m_shMax, shValue)) {
                            makeDialogAlert(Short.toString(m_shMin) + " - " + Short.toString(m_shMax));
                            requestFocus();
                            return false;
                        }
                    }  catch (Exception ex){
                        makeDialogAlert(getContext().getString(R.string.text_edad_dlg_message_value_not_valid));
                        requestFocus();
                        return false;
                    }
                    break;
                case INT:
                    try {
                        int iValue = Integer.parseInt(this.getText().toString());
                        if (!isInRange(m_iMin, m_iMax, iValue)) {
                            makeDialogAlert(Integer.toString(m_iMin) + " - " + Integer.toString(m_iMax));
                            requestFocus();
                            return false;
                        }
                    }  catch (Exception ex){
                        makeDialogAlert(getContext().getString(R.string.text_edad_dlg_message_value_not_valid));
                        requestFocus();
                        return false;
                    }
                    break;
                case LONG:
                    try {
                        long lValue = Integer.parseInt(this.getText().toString());
                        if (!isInRange(m_lMin, m_lMax, lValue)) {
                            makeDialogAlert(Long.toString(m_lMin) + " - " + Long.toString(m_lMax));
                            requestFocus();
                            return false;
                        }
                    }  catch (Exception ex){
                        makeDialogAlert(getContext().getString(R.string.text_edad_dlg_message_value_not_valid));
                        requestFocus();
                        return false;
                    }
                    break;
                case FLOAT:
                    try {
                        float fValue = Float.parseFloat(this.getText().toString());
                        if (!isInRange(m_fMin, m_fMax, fValue)) {
                            makeDialogAlert(Float.toString(m_fMin) + " - " + Float.toString(m_fMax));
                            requestFocus();
                            return false;
                        }
                    }  catch (Exception ex){
                        makeDialogAlert(getContext().getString(R.string.text_edad_dlg_message_value_not_valid));
                        requestFocus();
                        return false;
                    }
                    break;
                case DOUBLE:
                    try {
                        double dblValue = Double.parseDouble(this.getText().toString());
                        if (!isInRange(m_dblMin, m_dblMax, dblValue)) {
                            makeDialogAlert(Double.toString(m_dblMin) + " - " + Double.toString(m_dblMax));
                            requestFocus();
                            return false;
                        }
                    }  catch (Exception ex){
                        makeDialogAlert(getContext().getString(R.string.text_edad_dlg_message_value_not_valid));
                        requestFocus();
                        return false;
                    }                    break;
            }
        }

        return true;
    }

    private boolean isInRange(short a, short b, short c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
    private boolean isInRange(long a, long b, long c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
    private boolean isInRange(float a, float b, float c) {
        int iac = Float.compare(c,a); // < 0 if c < a
        int ibc = Float.compare(c,b); // > 0 if c > b
        if(iac < 0 || ibc > 0){
            return false;
        }
        return true;
    }
    private boolean isInRange(double a, double b, double c) {
        int iac = Double.compare(c,a); // < 0 if c < a
        int ibc = Double.compare(c,b); // > 0 if c > b
        if(iac < 0 || ibc > 0){
            return false;
        }
        return true;
    }

    private void makeDialogAlert(String strMessage) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setTitle(R.string.text_edad_dlg_title_value_out_of_range);
        builder.setMessage(strMessage);

        builder.setPositiveButton(R.string.text_edad_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public static boolean validateInputData(View viev){
        List<View> visited = new ArrayList<>();
        List<View> unvisited = new ArrayList<>();
        unvisited.add(viev);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            visited.add(child);
            if (!(child instanceof ViewGroup)) continue;
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i=0; i<childCount; i++) unvisited.add(group.getChildAt(i));
        }

        for(View v : visited){
            if(v instanceof NumericEditText){
                if(!((NumericEditText)v).validateInputLimit())
                    return false;
            }

        }

        return true;
    }

}
