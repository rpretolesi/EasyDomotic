package com.pretolesi.easydomotic.CustomControls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;

import com.pretolesi.easydomotic.R;

/**
 *  Custom EditText
 */
public class EDEditText extends EditText {

    private static final String TAG = "EDEditText";

    private float m_fmin, m_fmax;

    // Default Constructor
    public EDEditText(Context context) {
        super(context);
        m_fmin = 0.0f;
        m_fmax = 0.0f;
    }

    public EDEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_fmin = 0.0f;
        m_fmax = 0.0f;
    }

    public EDEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        m_fmin = 0.0f;
        m_fmax = 0.0f;
    }

    // Custom Constructor
    public EDEditText(Context context, float fmin, float fmax) {
        super(context);
        m_fmin = fmin;
        m_fmax = fmax;
    }

    public void setInputLimit(float fmin, float fmax) {
        m_fmin = fmin;
        m_fmax = fmax;
    }
    public void setInputLimit(int imin, int imax) {
        this.m_fmin = imin;
        this.m_fmax = imax;
    }

    public boolean validateInputLimit(){

        if((m_fmin == 0.0f) && (m_fmax == 0.0f)) {
            return true;
        }

        // Text or Number?
        float inputValue = 0.0f;
        int inputType = getInputType();
        if((inputType & InputType.TYPE_CLASS_NUMBER ) > 0) {
            try {
                inputValue = Float.parseFloat(this.getText().toString());
            }  catch (Exception ex){
                makeDialogAlert(getContext().getString(R.string.text_edad_dlg_message_value_not_valid));
                requestFocus();
                return false;
            }
         } else {
            inputValue = this.getText().length();
        }

        if (!isInRange(m_fmin, m_fmax, inputValue)) {
            makeDialogAlert(Float.toString(m_fmin) + " - " + Float.toString(m_fmax));
            requestFocus();
            return false;
        }
        return true;
    }

    private boolean isInRange(float a, float b, float c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
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
}
