package com.pretolesi.easydomotic.CustomControls;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 *
 */
public class LabelTextView extends TextView {
    private RelativeLayout.LayoutParams m_rllp = null;

    public LabelTextView(Context context) {
        super(context);
        m_rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);//(RelativeLayout.LayoutParams)this.getLayoutParams();
        setLayoutParams(m_rllp);
        this.setGravity(Gravity.CENTER);
        this.setTextColor(Color.GREEN);
    }

    public void setLayoutParam(int l, int t, int iWidthParent, int iHeight){
        if(m_rllp != null){
             m_rllp.leftMargin = l + ((iWidthParent - getWidth())/2);
             m_rllp.topMargin = t - iHeight;
        }
    }

}
