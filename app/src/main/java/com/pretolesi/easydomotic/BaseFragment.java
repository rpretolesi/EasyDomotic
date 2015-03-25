package com.pretolesi.easydomotic;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 *
 */
public class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    protected static final String ARG_SECTION_NUMBER = "section_number";
    protected static final String POSITION = "position";
    protected static final String _ID = "id";
    protected static final String ARG_ROOM_DATA = "Room_Data";
    protected static final String ARG_LIGHT_SWITCH_DATA = "Room_Light_Switch_Data";
    protected static final String EDIT_MODE = "edit_mode";

    protected TextView m_tvRoomName;
    protected RelativeLayout m_rl;
    protected RoomFragmentData m_rfd;
    protected ArrayList<LightSwitchData> m_allsd;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, this.toString() + ": " + "onAttach()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (m_rl == null) {
            m_rl = new RelativeLayout(getActivity().getApplicationContext());
            //               RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            //               m_rl.setLayoutParams(rllp);
        }
        if (m_tvRoomName == null) {
            m_tvRoomName = new TextView(getActivity().getApplicationContext());
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            m_tvRoomName.setLayoutParams(rlp);
        }
        try {
            m_rfd = getArguments().getParcelable(ARG_ROOM_DATA);
        } catch (Exception ex){
            m_rfd = new RoomFragmentData("PRETOLESI", getTag(), 0, 0, 0, false);
        }
        try {
            m_allsd = getArguments().getParcelableArrayList(ARG_LIGHT_SWITCH_DATA);
        } catch (Exception ex){
            m_allsd = new ArrayList<>();
        }


        Log.d(TAG, this.toString() + ": " + "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(m_rl != null && m_tvRoomName != null) {
            if(m_rfd != null){
                m_tvRoomName.setText(m_rfd.getTAG());
                m_rl.addView(m_tvRoomName);
            }
            if(m_allsd != null){
                for(LightSwitchData lsd : m_allsd){
                    newLightSwitch(lsd);
                }
            }
        }

        Log.d(TAG, this.toString() + ": " + "onCreateView()");
        return m_rl;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, this.toString() + ": " + "onActivityCreated()");
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, this.toString() + ": " + "onViewStateRestored()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, this.toString() + ": " + "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, this.toString() + ": " + "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, this.toString() + ": " + "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, this.toString() + ": " + "onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, this.toString() + ": " + "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, this.toString() + ": " + "onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, this.toString() + ": " + "onDetach()");
    }

    /*
        Add a new Light Switch
     */
    public boolean addLightSwitch(LightSwitchData lsd){
        // Define the switch
        if(m_allsd != null && newLightSwitch(lsd)){
            m_allsd.add(lsd);
            return true;
        }
        return false;
    }

    public RoomFragmentData getRoomFragmentData() {
        return m_rfd;
    }

    public ArrayList<LightSwitchData> getLightSwitchData() {
        LightSwitch ls;
        if(m_rl != null && m_allsd != null){
            for(LightSwitchData lsd : m_allsd) {
                ls = (LightSwitch) m_rl.findViewWithTag(lsd.getTag());
                if(ls != null){
                    lsd.update(ls.getLightSwitchData());
                }
            }
        }

        Log.d(TAG, this.toString() + ": " + "getLightSwitchData()");

        return m_allsd;
    }

    public boolean isLightSwitchTagPresent(String strLightSwitchTag) {
        boolean bRes = false;
        if(m_allsd != null && strLightSwitchTag != null){
            for(LightSwitchData lsd : m_allsd) {
                if(strLightSwitchTag.equals(lsd.getTag())){
                    bRes = true;
                }
            }
        }
        return bRes;
    }

    // Helper function
    private boolean newLightSwitch(LightSwitchData lsd){
        // Define the switch
        if(m_rl != null && lsd != null){
            LightSwitch ls = new LightSwitch(getActivity().getApplicationContext(), lsd);
            if(lsd.getLandscape()){
                ObjectAnimator.ofFloat(ls, "rotation", 0, 90).start();
            }
            setViewPosition(ls, lsd.getPosX(), lsd.getPosY());
            m_rl.addView(ls);

            return true;
        }
        return false;
    }

    // Static
    public static void setViewPosition(View view, float fPosX, float fPosY){
        RelativeLayout.LayoutParams rllp;
        if(view != null){
            if(view.getLayoutParams() ==  null){
                rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(rllp);
            }
            if(view.getLayoutParams() instanceof RelativeLayout.LayoutParams){
                rllp = (RelativeLayout.LayoutParams)view.getLayoutParams();
                if(rllp != null) {
                    rllp.leftMargin = (int) fPosX;
                    rllp.topMargin = (int) fPosY;
                    view.setLayoutParams(rllp);
                }
            }
        }
    }

    public static float getViewPositionX(View view){
        RelativeLayout.LayoutParams rllp;
        float fRes = 0.0f;
        if(view != null){
            if(view.getLayoutParams() ==  null){
                rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(rllp);
            }
            if(view.getLayoutParams() instanceof RelativeLayout.LayoutParams){
                rllp = (RelativeLayout.LayoutParams)view.getLayoutParams();
                if(rllp != null) {
                    fRes = rllp.leftMargin;
                }
            }
        }

        return fRes;
    }

    public static float getViewPositionY(View view){
        RelativeLayout.LayoutParams rllp;
        float fRes = 0.0f;
        if(view != null){
            if(view.getLayoutParams() ==  null){
                rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(rllp);
            }
            if(view.getLayoutParams() instanceof RelativeLayout.LayoutParams){
                rllp = (RelativeLayout.LayoutParams)view.getLayoutParams();
                if(rllp != null) {
                    fRes = rllp.topMargin;
                }
            }
        }

        return fRes;
    }
}