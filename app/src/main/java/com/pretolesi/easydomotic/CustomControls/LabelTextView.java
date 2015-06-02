package com.pretolesi.easydomotic.CustomControls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 *
 */
public class LabelTextView extends TextView {

    RelativeLayout.LayoutParams m_rllp = null;

    public LabelTextView(Context context) {
        super(context);
        setGravity(Gravity.CENTER);
        setTextColor(Color.GREEN);
        setSingleLine();
        m_rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(m_rllp);
    }

    @Override
    protected void onLayout(boolean changed,  int l, int t, int r, int b) { super.onLayout(changed, l, t, r, b); }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setPosition(int l, int t, int r, int b, int h, int w, boolean bVertical){
        if(m_rllp != null) {
            m_rllp.leftMargin = l;
            if(!bVertical){
                m_rllp.topMargin = t - (h/2)-16;
            } else {
                m_rllp.topMargin = t - (w/2)-16;
            }
//            m_rllp.rightMargin = r;
//            m_rllp.bottomMargin = b;
            m_rllp.height = h;
            m_rllp.width = w;
        }
    }
}
