package com.pretolesi.easydomotic;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easydomotic.BaseValue.BaseValueData;
import com.pretolesi.easydomotic.LightSwitch.LightSwitch;
import com.pretolesi.easydomotic.LoadersUtils.Loaders;
import com.pretolesi.easydomotic.NumericValue.NumericValue;
import com.pretolesi.easydomotic.SensorValue.SensorValueCalibr;
import com.pretolesi.easydomotic.SensorValue.SensorValueRaw;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClient;
import com.pretolesi.easydomotic.CommClientData.BaseValueCommClientData;
import com.pretolesi.easydomotic.TcpIpClient.TciIpClientHelper;
import com.pretolesi.easydomotic.TcpIpClient.TcpIpClientStatus;
import com.pretolesi.easydomotic.dialogs.OkDialogFragment;

import java.util.ArrayList;

/**
 *
 */
public class BaseFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OkDialogFragment.OkDialogFragmentCallbacks,
        TCPIPClient.TcpIpClientStatusListener {

    private static final String TAG = "BaseFragment";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String POSITION = "position";
    protected static final String ROOM_ID = "id";
    protected static final String EDIT_MODE = "edit_mode";

    protected int m_iChildID;

    protected TextView m_tvRoomName;
    protected RelativeLayout m_rl;
    protected RoomFragmentData m_rfd;
    protected ArrayList<BaseValueData> m_albvd;

    protected HorizontalScrollView m_osvStatusTcpIpServer;
    protected LinearLayout m_llStatusTcpIpServer;

    public static BaseFragment newInstance(int sectionNumber, long id, boolean bEditMode) {
        BaseFragment fragment = new BaseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putLong(ROOM_ID, id);
        args.putBoolean(EDIT_MODE, bEditMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
/*
        // Show title and Action Bar
        if (getArguments().getBoolean(EDIT_MODE, false)) {
            ((BaseActivity) activity).onSectionAttached(getArguments().getInt(BaseFragment.ARG_SECTION_NUMBER));
            ((BaseActivity) activity).restoreActionBar();
        }
*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Log.d(TAG, this.toString() + ": " + "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Start ID for child
        m_iChildID = 0;

        if (m_rl == null) {
            m_rl = new RelativeLayout(getActivity());
        }
        if (m_osvStatusTcpIpServer == null) {
            m_osvStatusTcpIpServer = new HorizontalScrollView(getActivity());
        }
        if (m_llStatusTcpIpServer == null) {
            m_llStatusTcpIpServer = new LinearLayout(getActivity());
        }

        // Log.d(TAG, this.toString() + ": " + "onCreateView()");
        return m_rl;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Log.d(TAG, this.toString() + ": " + "onActivityCreated()");
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Log.d(TAG, this.toString() + ": " + "onViewStateRestored()");
    }

    @Override
    public void onStart() {
        super.onStart();
        // Log.d(TAG, this.toString() + ": " + "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();

        // Start the Server
        getLoaderManager().initLoader(Loaders.TCP_IP_CLIENT_LOADER_ID, null, this);

        // Log.d(TAG, this.toString() + ": " + "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        // remove all objects in the room
        if(m_llStatusTcpIpServer != null){
            m_llStatusTcpIpServer.removeAllViews();
        }
        if (m_osvStatusTcpIpServer != null) {
            m_osvStatusTcpIpServer.removeAllViews();
        }
        if(m_rl != null){
            m_rl.removeAllViews();
        }

        // Unregister Listener For Tcp Ip Server
        // Listener
        if(TciIpClientHelper.getInstance() != null) {
            for(TCPIPClient tic : TciIpClientHelper.getTciIpClient()){
                if(tic != null){
                    tic.unregisterTcpIpClientStatus(this);

                }
            }
        }

        TciIpClientHelper.stopInstance();

        getLoaderManager().destroyLoader(Loaders.TCP_IP_CLIENT_LOADER_ID);
        getLoaderManager().destroyLoader(Loaders.ROOM_LOADER_ID);
        getLoaderManager().destroyLoader(Loaders.BASE_VALUE_LOADER_ID);

        // Log.d(TAG, this.toString() + ": " + "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        // Log.d(TAG, this.toString() + ": " + "onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Log.d(TAG, this.toString() + ": " + "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Log.d(TAG, this.toString() + ": " + "onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Log.d(TAG, this.toString() + ": " + "onDetach()");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Log.d(TAG, this.toString() + ": " + "onCreateLoader() id:" + id);

        if(id == Loaders.TCP_IP_CLIENT_LOADER_ID){
            return new CursorLoader(getActivity()){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.TcpIpClientEntry.load();
                }
            };
        }

        if(id == Loaders.ROOM_LOADER_ID){
            return new CursorLoader(getActivity()){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.RoomEntry.load(getArguments().getLong(ROOM_ID, -1));
                }
            };
        }

        if(id == Loaders.BASE_VALUE_LOADER_ID){
            return new CursorLoader(getActivity()){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.BaseValueEntry.load(getArguments().getLong(ROOM_ID, -1));
                }
            };
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // The list should now be shown.
        if(loader.getId() == Loaders.TCP_IP_CLIENT_LOADER_ID) {
            ArrayList<BaseValueCommClientData> alticd = SQLContract.TcpIpClientEntry.get(cursor);

            // Start Only if not in edit mode
            if(!getArguments().getBoolean(EDIT_MODE, false)) {
                TciIpClientHelper.startInstance(getActivity(), alticd);

                // Register Listener For Tcp Ip Server
                // Listener
                if(TciIpClientHelper.getTciIpClient() != null) {
                    for(TCPIPClient tic : TciIpClientHelper.getTciIpClient()){
                        if(tic != null){
                            tic.registerTcpIpClientStatus(this);
                        }
                    }
                }
            }

            // Room
            getLoaderManager().initLoader(Loaders.ROOM_LOADER_ID, null, this);
        }

        if(loader.getId() == Loaders.ROOM_LOADER_ID) {
            ArrayList<RoomFragmentData> alrfd = SQLContract.RoomEntry.get(cursor);
            if(alrfd != null && !alrfd.isEmpty()) {
                m_rfd = alrfd.get(0);
                if(m_rfd != null){
                    updateRoom();

                    // Room's elements
                    getLoaderManager().initLoader(Loaders.BASE_VALUE_LOADER_ID, null, this);
                }
            }
        }

        if(loader.getId() == Loaders.BASE_VALUE_LOADER_ID) {
            m_albvd = SQLContract.BaseValueEntry.get(cursor);
            update();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // Log.d(TAG, this.toString() + ": " + "onLoaderReset() id: " + loader.getId());
    }

    @Override
    public void onOkDialogFragmentClickListener(int iDialogOriginID, int iDialogActionID) {

    }

    private void updateRoom(){
        if(m_rl != null && m_rfd != null) {
            if (m_tvRoomName == null) {
                m_tvRoomName = new TextView(getActivity());
                m_tvRoomName.setId(getChildID());

                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                m_tvRoomName.setLayoutParams(rlp);
            }
            m_tvRoomName.setTextColor(Color.MAGENTA);
            m_tvRoomName.setTextSize(30.0f);
            m_tvRoomName.setText(m_rfd.getTAG());

            m_rl.addView(m_tvRoomName);

            // Controllo orientamento prima di costruire il frame....
            if(m_rfd.getLandscape()){
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            // Aggiungo Lo Stato dei Server
            if(m_llStatusTcpIpServer != null && m_osvStatusTcpIpServer != null) {
                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                rlp.setMargins(16, 16, 16, 16);
                m_osvStatusTcpIpServer.setLayoutParams(rlp);
                m_rl.addView(m_osvStatusTcpIpServer);

                HorizontalScrollView.LayoutParams hsvp = new HorizontalScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                m_llStatusTcpIpServer.setLayoutParams(hsvp);
                m_osvStatusTcpIpServer.addView(m_llStatusTcpIpServer);
                TciIpClientHelper tich = TciIpClientHelper.getInstance();

                // Get configured Servers
                TextView tv;
                int iID = 0;
                int iMaxID = 0;
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                llp.setLayoutDirection(LinearLayout.HORIZONTAL);
                llp.weight = (float) 1.0;
                if (tich != null) {
                    for (TCPIPClient tic : TciIpClientHelper.getTciIpClient()) {
                        if (tic != null) {
                            tv = new TextView(getActivity());
                            iID = (int)tic.getID();
                            tv.setId(iID);
                            if(iID > iMaxID){
                                iMaxID = iID;
                            }
                            tv.setLayoutParams(llp);
                            m_llStatusTcpIpServer.addView(tv);
                            tv.setText("No Server Configured\nNo Server Configured");
                        }
                    }
                    // Start ID for child
                    m_iChildID = iMaxID;
                }
            }

            // Show title and Action Bar
//            if(!getArguments().getBoolean(EDIT_MODE, false)) {
                ((BaseActivity) getActivity()).onSectionSetTitle(m_rfd.getTAG());
                ((BaseActivity) getActivity()).restoreActionBar();
//            }

        }
    }

    public RoomFragmentData getRoomFragmentData() {
        return m_rfd;
    }

    // Helper function
    private void update(){
        // Define the switch
        if(m_rl != null && m_albvd != null){
            for(BaseValueData bvd : m_albvd){
                if(bvd != null){
                    switch (bvd.getType()){
                        case BaseValueData.TYPE_LIGHT_SWITCH:
                            LightSwitch ls = new LightSwitch(getActivity(), bvd, getChildID(), getArguments().getBoolean(EDIT_MODE, false));
                            if(bvd.getVertical()){
                                ObjectAnimator.ofFloat(ls, "rotation", 0, 90).start();
                            }
                            setViewPosition(ls, bvd.getPosX(), bvd.getPosY());
                            m_rl.addView(ls);

                            break;

                        case BaseValueData.TYPE_NUMERIC_VALUE:
                            NumericValue nv = new NumericValue(getActivity(), bvd, getChildID(), getArguments().getBoolean(EDIT_MODE, false));
                            if(bvd.getVertical()){
                                ObjectAnimator.ofFloat(nv, "rotation", 0, 90).start();
                            }
                            setViewPosition(nv, bvd.getPosX(), bvd.getPosY());
                            m_rl.addView(nv);

                            break;

                        case BaseValueData.TYPE_SENSOR_RAW_VALUE:
                            SensorValueRaw svr = new SensorValueRaw(getActivity(), bvd, getChildID(), getArguments().getBoolean(EDIT_MODE, false));
                            if(bvd.getVertical()){
                                ObjectAnimator.ofFloat(svr, "rotation", 0, 90).start();
                            }
                            setViewPosition(svr, bvd.getPosX(), bvd.getPosY());
                            m_rl.addView(svr);

                            break;

                        case BaseValueData.TYPE_SENSOR_CALIBR_VALUE:
                            SensorValueCalibr svc = new SensorValueCalibr(getActivity(), bvd, getChildID(), getArguments().getBoolean(EDIT_MODE, false));
                            if(bvd.getVertical()){
                                ObjectAnimator.ofFloat(svc, "rotation", 0, 90).start();
                            }
                            setViewPosition(svc, bvd.getPosX(), bvd.getPosY());
                            m_rl.addView(svc);

                            break;
                    }
                }
            }
        }
    }

    private int getChildID(){
        m_iChildID = m_iChildID + 10;
        return m_iChildID;
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

    @Override
    public void onTcpIpClientStatusCallback(TcpIpClientStatus tics) {
        TextView tv = (TextView)getActivity().findViewById((int)tics.getServerID());
        if(tv != null){
            tv.setText(tics.getServerName() + "-" + tics.getStatus().toString() + "\n" + tics.getError());
        }
    }
}