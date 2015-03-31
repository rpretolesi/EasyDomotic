package com.pretolesi.easydomotic;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import com.pretolesi.SQL.SQLContract;

public class MainActivity extends BaseActivity
        implements MainNavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private MainNavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mNavigationDrawerFragment = (MainNavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, long id) {

        // Prelevo i dati e TAG per Room
        String strTag = SQLContract.RoomEntry.getTag(this, id);
        if(strTag != null){
//            // Prelevo i dati per gli altri oggetti della Room
//            ArrayList<LightSwitchData> allsd = SQLContract.LightSwitchEntry.load(this, rfd.getID());
//            if(allsd != null){
                // update the main content by replacing fragments
                FragmentManager fragmentManager = getFragmentManager();
                // Costruisco l'istanza
                fragmentManager.beginTransaction()
                        .replace(R.id.container, RoomFragment.newInstance(position + 1, id ), strTag)
                        .commit();
//            }
        }
    }

    public void onSectionAttached(int number, long id) {
        if(id > 0){
            mTitle = SQLContract.RoomEntry.getTag(this, id);
        } else {
            mTitle = "";
        }
/*
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
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

            Intent intent = SettingsActivity.makeSettingsActivity(this);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER), getArguments().getLong(_ID));
        }
    }

}
