package com.pretolesi.easydomotic;

import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easydomotic.LoadersUtils.Loaders;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClient;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClientData;
import com.pretolesi.easydomotic.TcpIpClient.TciIpClientHelper;

import java.util.ArrayList;


public class RunTimeActivity extends BaseActivity implements
        RunTimeNavigationDrawerFragment.NavigationDrawerCallbacks,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "RunTimeActivity";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private RunTimeNavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.run_time_activity);

        mNavigationDrawerFragment = (RunTimeNavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        getLoaderManager().initLoader(Loaders.TCP_IP_CLIENT_LOADER_ID, null, this);

        Log.d(TAG, this.toString() + ": " + "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        getLoaderManager().destroyLoader(Loaders.TCP_IP_CLIENT_LOADER_ID);

        Log.d(TAG, this.toString() + ": " + "onPause()");
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, long id) {

        // Prelevo i dati e TAG per Room
        String strTag = SQLContract.RoomEntry.getTag(id);
        if(strTag != null){
            // update the main content by replacing fragments
            FragmentManager fragmentManager = getFragmentManager();
            // Costruisco l'istanza
            fragmentManager.beginTransaction()
                    .replace(R.id.container, BaseFragment.newInstance(position + 1, id, false), strTag)
                    .commit();
        }
    }
/*
    public void onSectionAttached(int number, long id) {
        if(id > 0){
            mTitle = SQLContract.RoomEntry.getTag(this, id);
        } else {
            mTitle = "";
        }
*/
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
//    }
/*
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
*/

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, this.toString() + ": " + "onCreateLoader() id:" + id);
        if(id == Loaders.TCP_IP_CLIENT_LOADER_ID){
            return new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.TcpIpClientEntry.load();
                }
            };
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(loader.getId() == Loaders.TCP_IP_CLIENT_LOADER_ID) {
            ArrayList<TCPIPClientData> alticd = SQLContract.TcpIpClientEntry.get(cursor);
            TciIpClientHelper.startInstance(getApplicationContext(), alticd);
        }

        Log.d(TAG, this.toString() + ": " + "onLoadFinished() id: " + loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == Loaders.TCP_IP_CLIENT_LOADER_ID) {
            TciIpClientHelper.stopInstance();
            ricordarsi questo ordine di chiamata e poi fae i cmpi di lettura.
        }

    }
/*
    /**
     * A placeholder fragment containing a simple view.
     */
 //   public static class RoomFragment extends BaseFragment {

 //       /**
 //        * Returns a new instance of this fragment for the given section
 //        * number.
  //       */

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
/*
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
            ((RunTimeActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER), getArguments().getLong(_ID));
        }
    }
*/
}
