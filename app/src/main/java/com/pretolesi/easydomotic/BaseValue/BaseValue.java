package com.pretolesi.easydomotic.BaseValue;


import android.content.Context;
import android.widget.TextView;

import com.pretolesi.easydomotic.CustomControls.NumericEditText;

/**
 * Created by ricca_000 on 10/05/2015.
 */
public class BaseValue extends TextView {
    protected NumericEditText.DataType m_netdt;

    public BaseValue(Context context) {
        super(context);
    }
}
