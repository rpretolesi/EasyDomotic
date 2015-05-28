package com.pretolesi.easydomotic.CustomControls;

import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 *
 */
public class LabelTextView extends TextView {
    private RelativeLayout.LayoutParams m_rllp = null;

    public LabelTextView(Context context, RelativeLayout.LayoutParams rllp, Float fTextSize, int iColor) {
        super(context);
        ..
        if(rllp != null){
            setLayoutParams(rllp);
        }
        if(Float.compare(fTextSize, (float)0.0) > 0){
            this.setTextSize(fTextSize);
        }
        if(Float.compare(fTextSize, (float)0.0) > 0){
            this.setTextSize(fTextSize);
        }
        if(iColor != 0){
            this.setTextColor(iColor);
        }
    }
}
