package com.pretolesi.easydomotic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pretolesi.SQL.SQLContract;

import java.util.ArrayList;


/**
 * Settings Activity and Settings Navigation Drawer
 */
public class SettingsActivity extends BaseActivity implements
        SettingsNavigationDrawerFragment.NavigationDrawerCallbacks,
        SetNameAndOrientDialogFragment.SetNameAndOrientDialogFragmentCallbacks,
        RoomListFragment.ListRoomFragmentCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private SettingsNavigationDrawerFragment mNavigationDrawerFragment;
    private SetNameAndOrientDialogFragment m_sndf;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        mNavigationDrawerFragment = (SettingsNavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
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
    }

    @Override
    protected void  onSaveInstanceState (Bundle outState) {

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if(position == 0){
            m_sndf = SetNameAndOrientDialogFragment.newInstance(position, getString(R.string.settings_title_dialog_section_new_room), "", false);
            m_sndf.show(getSupportFragmentManager(), "");
        }

        if(position == 1){
            // Costruisco il frame...
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, RoomListFragment.newInstance(position + 1, position),getString(R.string.settings_title_section_open_room))
                    .commit();
        }

        if(position == 2){
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
            if(f instanceof BaseFragment){
                m_sndf = SetNameAndOrientDialogFragment.newInstance(position, getString(R.string.settings_title_dialog_section_add_switch), "", false);
                m_sndf.show(getSupportFragmentManager(), "");
            } else {
                Toast.makeText(getApplicationContext(), R.string.text_toast_room_add_not_exist, Toast.LENGTH_LONG).show();
            }
        }

        if(position == 7){
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
            if(f instanceof BaseFragment){
                BaseFragment bf = (BaseFragment)getSupportFragmentManager().findFragmentById(R.id.container);
                boolean bRes = true;
                if(!SQLContract.RoomEntry.save(getApplicationContext(), bf.getRoomFragmentData())){
                    bRes = false;
                }
                if(!SQLContract.LightSwitchEntry.save(getApplicationContext(),bf.getLightSwitchData())){
                    bRes = false;
                }
                if(bRes){
                    Toast.makeText(getApplicationContext(), R.string.text_toast_room_saved_ok, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.text_toast_room_saved_error, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.text_toast_room_save_not_exist, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onSetNameAndOrientDialogFragmentClickListener(int position, String strTitle, String strName, boolean bLandscape) {
        if(position == 0){
            // Verifico che il nome sia valido
            if(isTagRoomValid(strName)){
                // Controllo orientamento prima di costruire il frame....
                if(bLandscape){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }

                // Costruisco i dati...
                // Room
                RoomFragmentData rfd = new RoomFragmentData();
                rfd.setTag(strName);
                rfd.setLandscape(bLandscape);
                // LightSwitch
                ArrayList<LightSwitchData> allsd = new ArrayList<>();
                // Costruisco il frame...
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, RoomFragment.newInstance(position + 1, position, rfd, allsd), rfd.getTAG())
                        .commit();
            }
        }
        if(position == 2){
            BaseFragment rf = (BaseFragment)getSupportFragmentManager().findFragmentById(R.id.container);
            if(rf != null) {
                if(isTagSwitchValid(rf, strName)){
                    LightSwitchData lsd = new LightSwitchData(false, rf.getTag(), strName, 30, 30, 0, bLandscape);
                    rf.addLightSwitch(lsd);
                }
            }
        }
    }

    @Override
    public void onListRoomFragmentClickListener(int sectionNumber, int position, long id) {
        if(position == 1){
            // Prelevo i dati e TAG per Room
            RoomFragmentData rfd = SQLContract.RoomEntry.get(this, id);
            // Controllo orientamento prima di costruire il frame....
            if(rfd.getLandscape()){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            // Prelevo i dati per gli altri oggetti della Room
            ArrayList<LightSwitchData> allsd = SQLContract.LightSwitchEntry.get(this, rfd.getTAG());

            // update the main content by replacing fragments
            FragmentManager fragmentManager = getSupportFragmentManager();
            // Costruisco l'istanza
            fragmentManager.beginTransaction()
                    .replace(R.id.container, RoomFragment.newInstance(position + 1, id, rfd, allsd ), rfd.getTAG())
                    .commit();

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
        ActionBar actionBar = getSupportActionBar();
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



    /**
     * Room Fragment for build my custom fragment
     */
    public static class RoomFragment extends BaseFragment {

        /**
        * Returns a new instance of this fragment for the given section
        * number.
        */
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
                if(!SQLContract.RoomEntry.isTagPresent(getApplicationContext(), strTag)){
                    return true;
                } else {
                    Toast.makeText(getApplicationContext(), R.string.text_toast_room_name_already_exist, Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }
        Toast.makeText(getApplicationContext(), R.string.text_toast_room_name_not_valid, Toast.LENGTH_LONG).show();
        return false;
    }

    private boolean isTagSwitchValid(BaseFragment bf, String strTag) {
        if(bf != null && strTag != null){
            if(!strTag.equals("")){
                if(!bf.isLightSwitchTagPresent(strTag) && !SQLContract.LightSwitchEntry.isTagPresent(getApplicationContext(), bf.getTag(), strTag)){
                    return true;
                } else {
                    Toast.makeText(getApplicationContext(), R.string.text_toast_switch_already_exist, Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }
        Toast.makeText(getApplicationContext(), R.string.text_toast_switch_not_valid, Toast.LENGTH_LONG).show();
        return false;
    }

    public static Intent makeSettingsActivity(Context context)
    {
        Intent intent = new Intent();
        intent.setClass(context, SettingsActivity.class);
        return intent;
    }
}
