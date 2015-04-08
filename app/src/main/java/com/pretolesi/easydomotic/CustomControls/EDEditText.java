package com.pretolesi.easydomotic.CustomControls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
        float input = 0.0f;
        try {
            input = Float.parseFloat(this.getText().toString());
        }
        catch (Exception ignored){
            makeDialogAlert();
            requestFocus();
            return false;
        }

        if((m_fmin == 0.0f) && (m_fmax == 0.0f)) {
            return true;
        }
        if (!isInRange(m_fmin, m_fmax, input)) {
            makeDialogAlert();
            requestFocus();
            return false;
        }
        return true;
    }

    private boolean isInRange(float a, float b, float c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }

    private void makeDialogAlert() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setTitle(R.string.text_edad_title_value_out_of_range);
        builder.setMessage(Float.toString(m_fmin) + " - " + Float.toString(m_fmax));

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
