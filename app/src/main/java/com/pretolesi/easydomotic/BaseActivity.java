package com.pretolesi.easydomotic;

import android.support.v7.app.ActionBarActivity;

/**
 * Created by ricca_000 on 14/03/2015.
 */
public class BaseActivity extends ActionBarActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        // Log.d(TAG, "onStart()");
    }
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        // Log.d(TAG, "onResume()");
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        // Log.d(TAG, "onPause()");
    }
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
        // Log.d(TAG, "onStop()");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        // Log.d(TAG, "onDestroy()");
    }
}
