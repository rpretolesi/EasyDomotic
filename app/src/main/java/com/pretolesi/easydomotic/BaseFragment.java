package com.pretolesi.easydomotic;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
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
    protected static final String EDIT_MODE = "edit_mode";

    protected TextView m_tvRoomName;
    protected RelativeLayout m_rl;
    protected RoomFragmentData m_rfd;
    protected ArrayList<LightSwitchData> m_allsd;

    public static BaseFragment newInstance(int sectionNumber, long id, boolean bEditMode) {
        BaseFragment fragment = new BaseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putLong(_ID, id);
        args.putBoolean(EDIT_MODE, bEditMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((BaseActivity) activity).onSectionAttached(getTag());
        ((BaseActivity) activity).restoreActionBar();

        Log.d(TAG, this.toString() + ": " + "onAttach()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, this.toString() + ": " + "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (m_rl == null) {
            m_rl = new RelativeLayout(getActivity());
        }

        Log.d(TAG, this.toString() + ": " + "onCreateView()");
        return m_rl;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        getLoaderManager().initLoader(Loaders.ROOM_LOADER_ID, null, this);
//        getLoaderManager().initLoader(Loaders.LIGHT_SWITCH_LOADER_ID, null, this);

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

        getLoaderManager().initLoader(Loaders.ROOM_LOADER_ID, null, this);
        getLoaderManager().initLoader(Loaders.LIGHT_SWITCH_LOADER_ID, null, this);

        Log.d(TAG, this.toString() + ": " + "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();

        // remove all object
        if(m_rl != null){
            m_rl.removeAllViews();
        }

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

        if(id == Loaders.LIGHT_SWITCH_LOADER_ID){
            return new CursorLoader(getActivity()){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.LightSwitchEntry.load(getContext(), getArguments().getLong(_ID, -1));
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
                }
            }
        }

        if(loader.getId() == Loaders.LIGHT_SWITCH_LOADER_ID) {
            m_allsd = SQLContract.LightSwitchEntry.get(cursor);
            updateLightSwitchs();
        }

        Log.d(TAG, this.toString() + ": " + "onLoadFinished() id: " + loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == Loaders.ROOM_LOADER_ID) {

        }
        Log.d(TAG, this.toString() + ": " + "onLoaderReset() id: " + loader.getId());
    }

    @Override
    public void onOkDialogFragmentClickListener(int iDialogOriginID, int iDialogActionID) {

    }

    private void updateRoom(){
        if(m_rl != null && m_rfd != null) {
            if (m_tvRoomName == null) {
                m_tvRoomName = new TextView(getActivity());

                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                m_tvRoomName.setLayoutParams(rlp);
            }

            m_tvRoomName.setText(m_rfd.getTAG());

            m_rl.addView(m_tvRoomName);

            // Controllo orientamento prima di costruire il frame....
            if(m_rfd.getLandscape()){
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    public RoomFragmentData getRoomFragmentData() {
        return m_rfd;
    }

    // Helper function
    private void updateLightSwitchs(){
        // Define the switch
        if(m_rl != null && m_allsd != null){
            for(LightSwitchData lsd : m_allsd){
                if(lsd != null){
                    LightSwitch ls = new LightSwitch(getActivity(), lsd, getArguments().getBoolean(EDIT_MODE, false));
                    if(lsd.getLandscape()){
                        ObjectAnimator.ofFloat(ls, "rotation", 0, 90).start();
                    }
                    setViewPosition(ls, lsd.getPosX(), lsd.getPosY());
                    m_rl.addView(ls);
                }
            }
        }
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
}