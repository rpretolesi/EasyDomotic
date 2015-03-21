package com.pretolesi.easydomotic;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
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
    protected static final String EDIT_MODE = "edit_mode";

    protected TextView m_tvRoomName;
    protected RelativeLayout m_rl;
    protected ArrayList<LightSwitch> m_alLightSwitch;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Log.d(TAG, "onAttach()");
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(m_rl == null){
            m_rl = new RelativeLayout(getActivity().getApplicationContext());
            //               RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            //               m_rl.setLayoutParams(rllp);
        }
        if(m_tvRoomName == null){
            m_tvRoomName = new TextView(getActivity().getApplicationContext());
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            m_tvRoomName.setLayoutParams(rlp);
        }

        // Log.d(TAG, "onCreate()");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Log.d(TAG, "onCreateView()");
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Log.d(TAG, "onActivityCreated()");
    }
    @Override
    public void onViewStateRestored (Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Log.d(TAG, "onViewStateRestored()");
    }
    @Override
    public void onStart () {
        super.onStart();
        // Log.d(TAG, "onStart()");
    }
    @Override
    public void onResume (){
        super.onResume();
        // Log.d(TAG, "onResume()");
    }
    @Override
    public void onPause() {
        super.onPause();
        // Log.d(TAG, "onPause()");
    }
    @Override
    public void onStop () {
        super.onStop();
        // Log.d(TAG, "onStop()");
    }
    @Override
    public void onDestroyView () {
        super.onDestroyView();
        // Log.d(TAG, "onDestroyView()");
    }
    @Override
    public void onDestroy () {
        super.onDestroy();
        // Log.d(TAG, "onDestroy()");
    }
    @Override
    public void onDetach () {
        super.onDetach();
        // Log.d(TAG, "onDetach()");
    }
}
