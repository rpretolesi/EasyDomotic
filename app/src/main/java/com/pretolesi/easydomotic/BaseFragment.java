package com.pretolesi.easydomotic;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    protected static final String _ID = "id";
    protected static final String ARG_ROOM_DATA = "Room_Data";
    protected static final String ARG_LIGHT_SWITCH_DATA = "Room_Light_Switch_Data";
    protected static final String EDIT_MODE = "edit_mode";

    protected TextView m_tvRoomName;
    protected RelativeLayout m_rl;
    protected RoomFragmentData m_rfd;
    protected ArrayList<LightSwitchData> m_allsd;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BaseFragment newInstance(int sectionNumber, long id, RoomFragmentData rfd, ArrayList<LightSwitchData> allsd) {
        BaseFragment fragment = new BaseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putLong(_ID, id);
        args.putParcelable(ARG_ROOM_DATA, rfd);
        args.putParcelableArrayList(ARG_LIGHT_SWITCH_DATA, allsd);
        args.putBoolean(EDIT_MODE, false);
        fragment.setArguments(args);
        return fragment;
    }

    public BaseFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Log.d(TAG, "onAttach()");
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


        // Log.d(TAG, "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Log.d(TAG, "onCreateView()");
        if(m_rl != null & m_tvRoomName != null) {
            if(m_rfd != null){
                m_tvRoomName.setText(m_rfd.getTAG());
                m_rl.addView(m_tvRoomName);
            }
            if(m_allsd != null){
                for(LightSwitchData lsd : m_allsd){
                    LightSwitch ls = new LightSwitch(getActivity().getApplicationContext(), lsd);
                    m_rl.addView(ls);
                    ls.animate().translationX(lsd.getPosX()).translationY(lsd.getPosY()).setDuration(1000).start();
                }
            }
        }
        if(m_rfd != null){
            if(m_rfd.getLandscape()){
                getActivity().setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                getActivity().setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        return m_rl;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Log.d(TAG, "onActivityCreated()");
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Log.d(TAG, "onViewStateRestored()");
    }

    @Override
    public void onStart() {
        super.onStart();
        // Log.d(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();

        // Log.d(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        // Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        // Log.d(TAG, "onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Log.d(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Log.d(TAG, "onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Log.d(TAG, "onDetach()");
    }

    /*
        Add a new Light Switch
     */
    public void addLightSwitch(LightSwitchData lsd){
        // Define the switch
        if(m_rl != null && lsd != null){
            m_allsd.add(lsd);
            LightSwitch ls = new LightSwitch(getActivity().getApplicationContext(), lsd);
            m_rl.addView(ls);
            ls.animate().translationX(lsd.getPosX()).translationY(lsd.getPosY()).setDuration(0).start();
        }
    }

    public RoomFragmentData getRoomFragmentData() {
        return m_rfd;
    }

    public ArrayList<LightSwitchData> getLightSwitchData() {
        LightSwitch ls;
        if(m_rl != null && m_allsd != null){
            for(LightSwitchData lsd : m_allsd) {
                ls = (LightSwitch) m_rl.findViewWithTag(lsd.getTAG());
                if(ls != null){
                    lsd.update(ls.getLightSwitchData());
                }
            }
        }

        return m_allsd;
    }
/*
    public void setRoomFragmentData(RoomFragmentData rfd) {
        if(rfd != null & m_rl != null & m_tvRoomName != null) {
            m_tvRoomName.setText(rfd.getTAG());
        }
    }

    public void setLightSwitchData(ArrayList<LightSwitchData> alllsd) {
        for (LightSwitchData lsd : alllsd) {
            addLightSwitch(lsd);
        }
    }
*/
}