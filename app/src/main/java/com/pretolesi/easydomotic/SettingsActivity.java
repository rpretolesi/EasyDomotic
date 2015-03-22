package com.pretolesi.easydomotic;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pretolesi.SQL.SQLContract;

import java.util.ArrayList;

/**
 * Settings Activity and Settings Navigation Drawer
 */
public class SettingsActivity extends BaseActivity implements SettingsNavigationDrawerFragment.NavigationDrawerCallbacks, SetNameDialogFragment.SetNameDialogFragmentCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private SettingsNavigationDrawerFragment mNavigationDrawerFragment;
    private SetNameDialogFragment m_sndf;
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
            m_sndf = SetNameDialogFragment.newInstance(position, getString(R.string.text_tv_room_name));
            m_sndf.show(getSupportFragmentManager(), "");
        }

        if(position == 1){
            m_sndf = SetNameDialogFragment.newInstance(position, getString(R.string.text_tv_lightswitch_name));
            m_sndf.show(getSupportFragmentManager(), "");
        }
        if(position == 2){
        }

        if(position == 7){
            RoomFragment rf = (RoomFragment)getSupportFragmentManager().findFragmentById(R.id.container);
            SQLContract.RoomEntry.save(getApplicationContext(), rf.getRoomFragmentData());
            SQLContract.LightSwitchEntry.save(getApplicationContext(),rf.getLightSwitchData());
        }
    }

    @Override
    public void onSetNameDialogFragmentClickListener(DialogFragment dialog, int position, String strTitle, String strName) {
        if(position == 0){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, RoomFragment.newInstance(position + 1, 0, true ), strName)
                    .commit();
        }
        if(position == 1){
            RoomFragment rf = (RoomFragment)getSupportFragmentManager().findFragmentById(R.id.container);
            if(rf != null) {
                LightSwitchData lsd = new
                rf.addLightSwitch(strName);
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
        public static RoomFragment newInstance(int sectionNumber, long id, boolean editMode) {
         RoomFragment fragment = new RoomFragment();
         Bundle args = new Bundle();
         args.putInt(ARG_SECTION_NUMBER, sectionNumber);
         args.putLong(_ID, id);
         args.putBoolean(EDIT_MODE, editMode);
         fragment.setArguments(args);
         return fragment;
        }

        public RoomFragment() {
        }

        @Override
        public void  onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            if(m_alLightSwitch == null){
                m_alLightSwitch = new ArrayList<>();
            }
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

    public static Intent makeSettingsActivity(Context context)
    {
        Intent intent = new Intent();
        intent.setClass(context, SettingsActivity.class);
        return intent;
    }

}
