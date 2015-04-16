package com.pretolesi.easydomotic;

import android.app.ActionBar;
import android.app.Activity;
import android.util.Log;

import java.util.List;
import java.util.Vector;

/**
 *
 */
public class BaseActivity extends Activity {
    private static final String TAG = "BaseActivity";

    /**
     * Use to update
     */

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    protected CharSequence mTitle;

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        Log.d(TAG, this.toString() + ": " + "onStart()");
    }
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        Log.d(TAG, this.toString() + ": " + "onResume()");
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        Log.d(TAG, this.toString() + ": " + "onPause()");
    }
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
        Log.d(TAG, this.toString() + ": " + "onStop()");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        Log.d(TAG, this.toString() + ": " + "onDestroy()");
    }

    public void onSectionAttached(int number) {

        switch (number) {
            case 1:
                mTitle = getString(R.string.settings_title_section_new_room);
                break;
            case 2:
                mTitle = getString(R.string.settings_title_section_open_room);
                break;
            case 3:
                mTitle = getString(R.string.settings_title_section_add_switch);
                break;
            case 4:
                mTitle = getString(R.string.settings_title_section_disp);
                break;
            case 5:
                mTitle = getString(R.string.settings_title_section_save);
                break;
            case 6:
                mTitle = getString(R.string.settings_title_section_new_tcp_ip_client);
                break;
            case 7:
                mTitle = getString(R.string.settings_title_section_open_tcp_ip_client);
                break;
            case 8:
                mTitle = getString(R.string.settings_title_section_save);
                break;
        }

    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
}
