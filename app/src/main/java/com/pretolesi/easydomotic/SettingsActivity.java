package com.pretolesi.easydomotic;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easydomotic.LightSwitch.LightSwitchPropActivity;
import com.pretolesi.easydomotic.dialogs.SetNameAndOrientDialogFragment;
import com.pretolesi.easydomotic.dialogs.YesNoDialogFragment;

import java.util.ArrayList;


/**
 * Settings Activity and Settings Navigation Drawer
 */
public class SettingsActivity extends BaseActivity implements
        SettingsNavigationDrawerFragment.NavigationDrawerCallbacks,
        SetNameAndOrientDialogFragment.SetNameAndOrientDialogFragmentCallbacks,
        RoomListFragment.ListRoomFragmentCallbacks,
        YesNoDialogFragment.YesNoDialogFragmentCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private SettingsNavigationDrawerFragment mNavigationDrawerFragment;
    private SetNameAndOrientDialogFragment m_sndf;
    private YesNoDialogFragment m_yndf;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        mNavigationDrawerFragment = (SettingsNavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(m_sndf != null){
            m_sndf.dismiss();
        }
        if(m_yndf != null){
            m_yndf.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if(position == 0){
            m_sndf = SetNameAndOrientDialogFragment.newInstance(position, getString(R.string.settings_title_dialog_section_new_room), "", false);
            m_sndf.show(getFragmentManager(), "");
        }

        if(position == 1){
            // Costruisco il frame...
//            FragmentManager fragmentManager = getSupportFragmentManager();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, RoomListFragment.newInstance(position + 1, position),getString(R.string.settings_title_section_open_room))
                    .commit();
        }

        if(position == 2){
            BaseFragment rf = (BaseFragment)getFragmentManager().findFragmentById(R.id.container);
            if(rf != null) {
                RoomFragmentData rfd = rf.getRoomFragmentData();
                if(rfd != null) {
                    Intent intent = LightSwitchPropActivity.makeLightSwitchPropActivity(this, rfd.getID(), -1);
                    startActivity(intent);
/*
                    if (isLightSwitchTagValid(strName, rfd.getID())) {
                        LightSwitchData lsd = new LightSwitchData(false, false, -1, rfd.getID(), strName, 30, 30, 0, bLandscape);
                        rf.addLightSwitch(lsd);
                    }
*/
                }
            }

/*
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
            if(f instanceof BaseFragment){
                m_sndf = SetNameAndOrientDialogFragment.newInstance(position, getString(R.string.settings_title_dialog_section_add_switch), "", false);
                m_sndf.show(getSupportFragmentManager(), "");
            } else {
                Toast.makeText(getApplicationContext(), R.string.text_toast_room_add_not_exist, Toast.LENGTH_LONG).show();
            }
*/
        }

        if(position == 7){
            // Save
            saveRoomData();
        }
    }

    @Override
    public void onSetNameAndOrientDialogFragmentClickListener(int position, String strTitle, String strName, boolean bLandscape) {
        if(position == 0){
            // Verifico che il nome sia valido
            if(isTagRoomValid(strName)){
/*
                // Controllo orientamento prima di costruire il frame....
                if(bLandscape){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
*/
                // Room Data
                RoomFragmentData rfd = new RoomFragmentData();
                rfd.setTag(strName);
                rfd.setLandscape(bLandscape);

                // Salvo
                long iRoomID = SQLContract.RoomEntry.save(this, rfd);
                if(iRoomID > 0){
                    // Costruisco il frame...
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, RoomFragment.newInstance(position + 1, iRoomID), rfd.getTAG())
                            .commit();
                    Toast.makeText(this, R.string.text_toast_room_saved_ok, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.text_toast_room_saved_error, Toast.LENGTH_LONG).show();
                }
            }
        }
        if(position == 2){
/*
            BaseFragment rf = (BaseFragment)getSupportFragmentManager().findFragmentById(R.id.container);
            if(rf != null) {
                RoomFragmentData rfd = rf.getRoomFragmentData();
                if(rfd != null) {
                    if (isLightSwitchTagValid(strName, rfd.getID())) {
                        LightSwitchData lsd = new LightSwitchData(false, false, -1, rfd.getID(), strName, 30, 30, 0, bLandscape);
                        rf.addLightSwitch(lsd);
                    }
                }
            }
*/
        }
    }

    @Override
    public void onYesNoDialogFragmentClickListener(int position, boolean bYes, boolean bNo) {
        if(position == 7){
            if(bYes) {
                // Save
                saveRoomData();
                super.onBackPressed();
            }
            if(bNo) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onBackPressed() {
/*
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
        if(f instanceof BaseFragment) {
            BaseFragment bf = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            if(!bf.getDataSaved()){
                        m_yndf = YesNoDialogFragment.newInstance(
                        7,
                        getString(R.string.text_title_room_not_saved),
                        getString(R.string.text_message_room_save_confirmation),
                        getString(R.string.text_yndf_btn_yes),
                        getString(R.string.text_yndf_btn_no)
                );
                m_yndf.show(getSupportFragmentManager(), "");
            } else {
                super.onBackPressed();
            }
        }else {
            super.onBackPressed();
        }
*/
    }

    @Override
    public void onListRoomFragmentClickListener(int sectionNumber, int position, long id) {
        if(position == 1){
            // Prelevo i dati e TAG per Room
            Cursor cursor = SQLContract.RoomEntry.load(this, id);
            ArrayList<RoomFragmentData> alrfd = SQLContract.RoomEntry.get(cursor);
            if(alrfd != null && !alrfd.isEmpty()) {
                RoomFragmentData rfd = alrfd.get(0);
                // Controllo orientamento prima di costruire il frame....
                if (rfd != null) {
                    if (rfd.getLandscape()) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                    FragmentManager fragmentManager = getFragmentManager();
                    // Costruisco l'istanza
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, RoomFragment.newInstance(position + 1, id), rfd.getTAG())
                            .commit();

/*
                    // Prelevo i dati per gli altri oggetti della Room
                    ArrayList<LightSwitchData> allsd = SQLContract.LightSwitchEntry.load(this, rfd.getID());
                    if (allsd != null) {
                        // update the main content by replacing fragments
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        // Costruisco l'istanza
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, RoomFragment.newInstance(position + 1, id, rfd, allsd), rfd.getTAG())
                                .commit();

                    }
*/
                }
            }
        }
    }

    public void onSectionAttached(String strTitle) {
        mTitle = strTitle;
/*
        switch (number) {
            case 1:
                mTitle = getString(R.string.settings_title_section_new_room);
                break;
            case 2:
                mTitle = getString(R.string.settings_title_section_add_switch);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.settings_title_section_disp);
                break;
            case 5:
                mTitle = getString(R.string.settings_title_section_disp);
                break;
            case 6:
                mTitle = getString(R.string.settings_title_section_disp);
                break;
            case 7:
                mTitle = getString(R.string.settings_title_section_disp);
                break;
            case 8:
                mTitle = getString(R.string.settings_title_section_save);
                break;
        }
*/
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveRoomData() {
        Fragment f = getFragmentManager().findFragmentById(R.id.container);
        if(f instanceof BaseFragment){
            BaseFragment bf = (BaseFragment)getFragmentManager().findFragmentById(R.id.container);
            boolean bRes = true;
            if(SQLContract.RoomEntry.save(this, bf.getRoomFragmentData()) > 0){
                bRes = false;
            }
            if(!SQLContract.LightSwitchEntry.save(this,bf.getLightSwitchData())){
                bRes = false;
            }
            if(bRes){
                Toast.makeText(this, R.string.text_toast_room_saved_ok, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.text_toast_room_saved_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, R.string.text_toast_room_save_not_exist, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Room Fragment for build my custom fragment
     */
    public static class RoomFragment extends BaseFragment {

        /**
        * Returns a new instance of this fragment for the given section
        * number.
        */
/*
        public static RoomFragment newInstance(int sectionNumber, long id, RoomFragmentData rfd, ArrayList<LightSwitchData> allsd) {
            RoomFragment fragment = new RoomFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putLong(_ID, id);
            args.putParcelable(ARG_ROOM_DATA, rfd);
            args.putParcelableArrayList(ARG_LIGHT_SWITCH_DATA, allsd);
            args.putBoolean(EDIT_MODE, false);
            fragment.setArguments(args);
            return fragment;
        }
*/
        public static RoomFragment newInstance(int sectionNumber, long id) {
            RoomFragment fragment = new RoomFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putLong(_ID, id);
            args.putBoolean(EDIT_MODE, false);
            fragment.setArguments(args);
            return fragment;
        }

        public RoomFragment() {
        }

        @Override
        public void  onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((SettingsActivity) activity).onSectionAttached(getTag());
            ((SettingsActivity) activity).restoreActionBar();
        }

    }

    // Helper method
    private boolean isTagRoomValid(String strTag) {
        if(strTag != null){
            if(!strTag.equals("")){
                if(!SQLContract.RoomEntry.isTagPresent(this, strTag)){
                    return true;
                } else {
                    Toast.makeText(this, R.string.text_toast_room_name_already_exist, Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }
        Toast.makeText(this, R.string.text_toast_room_name_not_valid, Toast.LENGTH_LONG).show();
        return false;
    }

    public static Intent makeSettingsActivity(Context context)
    {
        Intent intent = new Intent();
        intent.setClass(context, SettingsActivity.class);
        return intent;
    }
}
