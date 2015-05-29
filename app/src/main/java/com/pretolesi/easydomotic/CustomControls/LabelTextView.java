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
    private View m_vRefView = null;
    private boolean m_bVertical = false;

    public LabelTextView(Context context, View vRefView, boolean bVertical) {
        super(context);
        m_vRefView = vRefView;
        m_bVertical = bVertical;
        setGravity(Gravity.CENTER);
        setTextColor(Color.GREEN);
        if(m_vRefView != null) {
            RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams) m_vRefView.getLayoutParams();
            setLayoutParams(rllp);
        }
    }

    @Override
    protected void onLayout(boolean changed,  int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(m_vRefView != null){
            if(m_bVertical){
                setY(t - m_vRefView.getWidth());
            } else {
                setY(t - m_vRefView.getHeight());
            }
        }
     }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
