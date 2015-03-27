package com.pretolesi.easydomotic.LightSwitch;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.pretolesi.easydomotic.R;
import com.pretolesi.easydomotic.SettingsActivity;

/**
 *
 */
public class LightSwitchPropertyActivity extends Activity {
    private static final String TAG = "LightSwitchPropertyActivity";

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.light_switch_property_activity);
        setTitle("AAA");
        mTitle = getTitle();
        restoreActionBar();

    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_light_switch_property_activity, menu);
//            restoreActionBar();
            return true;
//        }

//        return super.onCreateOptionsMenu(menu);
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

    public static Intent makeLightSwitchPropertyActivity(Context context)
    {
        Intent intent = new Intent();
        intent.setClass(context, LightSwitchPropertyActivity.class);
        return intent;
    }
}
