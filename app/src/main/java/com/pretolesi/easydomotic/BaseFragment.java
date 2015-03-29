package com.pretolesi.easydomotic;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easydomotic.LightSwitch.LightSwitch;
import com.pretolesi.easydomotic.LightSwitch.LightSwitchData;
import com.pretolesi.easydomotic.LoadersUtils.Loaders;
import com.pretolesi.easydomotic.dialogs.OkDialogFragment;
import com.pretolesi.easydomotic.dialogs.SetNameAndOrientDialogFragment;

import java.util.ArrayList;

/**
 *
 */
public class BaseFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OkDialogFragment.OkDialogFragmentCallbacks {

    private static final String TAG = "BaseFragment";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    protected static final String ARG_SECTION_NUMBER = "section_number";
    protected static final String POSITION = "position";
    protected static final String _ID = "id";
//    protected static final String ARG_ROOM_DATA = "Room_Data";
//    protected static final String ARG_LIGHT_SWITCH_DATA = "Room_Light_Switch_Data";
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

        Log.d(TAG, this.toString() + ": " + "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(m_rl != null && m_tvRoomName != null) {
            m_rl.addView(m_tvRoomName);
/*
            if(m_rfd != null){
                m_tvRoomName.setText(m_rfd.getTAG());
                m_rl.addView(m_tvRoomName);
            }
*/
            if(m_allsd != null){
                for(LightSwitchData lsd : m_allsd){
                    if(lsd != null){
                        newLightSwitch(lsd);
                    }
                }
            }
        }

        Log.d(TAG, this.toString() + ": " + "onCreateView()");
        return m_rl;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        m_rfd = getArguments().getParcelable(ARG_ROOM_DATA);
//        if(m_rfd != null){
//            updateRoom();
//        } else {
            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().initLoader(Loaders.ROOM_LOADER_ID, null, this);
//        }

//        m_allsd = getArguments().getParcelableArrayList(ARG_LIGHT_SWITCH_DATA);

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, this.toString() + ": " + "onCreateLoader() id:" + id);
        if(id == Loaders.ROOM_LOADER_ID){
            return new CursorLoader(getActivity()){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.RoomEntry.load(getContext(), getArguments().getLong(_ID, -1));
                }
            };
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // The list should now be shown.
        if(loader.getId() == Loaders.ROOM_LOADER_ID) {
            ArrayList<RoomFragmentData> alrfd = SQLContract.RoomEntry.get(cursor);
            if(alrfd != null && !alrfd.isEmpty()) {
                m_rfd = alrfd.get(0);
                if(m_rfd != null){
                    updateRoom();
                    // Ok
                    return;
                }
            }
        }
/*
        // No valid data, finish
        OkDialogFragment odf = OkDialogFragment.newInstance(1, getString(R.string.text_odf_title_room_data_not_present), getString(R.string.text_odf_message_room_data_not_present) , getString(R.string.text_odf_message_ok_button));
        odf.show(getFragmentManager(), "");
*/
/*
        if(loader.getId() == Loaders.LIGHT_SWITCH_LOADER_ID) {
            ArrayList<LightSwitchData> allsd = SQLContract.LightSwitchEntry.get(cursor);
            if(allsd != null && !allsd.isEmpty()){
                m_lsd = allsd.get(0);
            }
        }
*/


        Log.d(TAG, this.toString() + ": " + "onLoadFinished() id: " + loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == Loaders.ROOM_LOADER_ID) {

        }
        Log.d(TAG, this.toString() + ": " + "onLoaderReset() id: " + loader.getId());
    }

    @Override
    public void onOkDialogFragmentClickListener(int position) {
        if(position == 1){

        }
    }

    private void updateRoom(){
        if(m_rfd != null) {
            if(m_tvRoomName != null){
                m_tvRoomName.setText(m_rfd.getTAG());
            }
            // Controllo orientamento prima di costruire il frame....
            if(m_rfd.getLandscape()){
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }
    /*
        Add a new Light Switch
     */
    public void addLightSwitch(LightSwitchData lsd){
        if(m_allsd == null){
            m_allsd = new ArrayList<>();
        }
        // Define the switch
        if(newLightSwitch(lsd)){
            m_allsd.add(lsd);
        }
    }

    public RoomFragmentData getRoomFragmentData() {
        return m_rfd;
    }

    public boolean getDataSaved() {
        boolean bRes = true;
        if(m_rfd != null){
            if(!m_rfd.getSaved()) {
                bRes = false;
            }
        }
        if(m_allsd != null){
            for(LightSwitchData lsd : m_allsd){
                if(lsd != null){
                    if(!lsd.getSaved()) {
                        bRes = false;
                    }
                }
            }
        }

        return bRes;
    }

    public ArrayList<LightSwitchData> getLightSwitchData() {
/*
        LightSwitch ls;
        if(m_rl != null && m_allsd != null){
            for(LightSwitchData lsd : m_allsd) {
                if(lsd != null) {
                    ls = (LightSwitch) m_rl.findViewWithTag(lsd.getTag());
                    if (ls != null) {
                        lsd.update(ls.getLightSwitchData());
                    }
                }
            }
        }
*/
        Log.d(TAG, this.toString() + ": " + "getLightSwitchData()");

        return m_allsd;
    }
/*
    public boolean isLightSwitchTagPresent(String strLightSwitchTag) {
        boolean bRes = false;
        if(m_allsd != null && strLightSwitchTag != null){
            for(LightSwitchData lsd : m_allsd) {
                if(lsd != null) {
                    if (strLightSwitchTag.equals(lsd.getTag())) {
                        bRes = true;
                    }
                }
            }
        }
        return bRes;
    }
*/
    // Helper function
    private boolean newLightSwitch(LightSwitchData lsd){
        // Define the switch
        if(m_rl != null && lsd != null){
            LightSwitch ls = new LightSwitch(getActivity(), lsd);
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