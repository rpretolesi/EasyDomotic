package com.pretolesi.easydomotic;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easydomotic.BaseValue.BaseValueData;
import com.pretolesi.easydomotic.BluetoothClient.BluetoothClientDataPropActivity;
import com.pretolesi.easydomotic.CommClientData.BaseValueCommClientData;
import com.pretolesi.easydomotic.CommClientData.BaseValueCommClientDataPropActivity;
import com.pretolesi.easydomotic.LightSwitch.LightSwitchPropActivity;
import com.pretolesi.easydomotic.NumericValue.NumericValuePropActivity;
import com.pretolesi.easydomotic.SensorValue.SensorValuePropActivity;
import com.pretolesi.easydomotic.TcpIpClient.TCPIPClientPropActivity;
import com.pretolesi.easydomotic.CommClientData.BaseValueCommClientListFragment;
import com.pretolesi.easydomotic.dialogs.DialogActionID;
import com.pretolesi.easydomotic.dialogs.DialogOriginID;
import com.pretolesi.easydomotic.dialogs.OkDialogFragment;
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
        BaseValueCommClientListFragment.BaseValueCommClientFragmentCallbacks,
        YesNoDialogFragment.YesNoDialogFragmentCallbacks{


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private SettingsNavigationDrawerFragment mNavigationDrawerFragment;
    private SetNameAndOrientDialogFragment m_sndf;

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
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, RoomListFragment.newInstance(position + 1, position),getString(R.string.settings_title_section_open_room))
                    .commit();
        }

        if(position == 2){
            Fragment f = getFragmentManager().findFragmentById(R.id.container);
            if(f != null && f instanceof BaseFragment){
                BaseFragment bf = (BaseFragment)f;
                RoomFragmentData rfd = bf.getRoomFragmentData();
                if(rfd != null) {
                    // Show title when close activity
                    onSectionSetTitle(getString(R.string.app_name));
                    restoreActionBar();

                    Intent intent = LightSwitchPropActivity.makeBaseValuePropActivityByRoomID(this, LightSwitchPropActivity.class, BaseValueData.TYPE_LIGHT_SWITCH, rfd.getID());
                    startActivity(intent);
                }else {
                    OkDialogFragment.newInstance(DialogOriginID.ORIGIN_NAVIGATION_DRAWER_ITEM_ID, DialogActionID.ROOM_ERROR_ID, getString(R.string.text_odf_title_room_data_not_present), getString(R.string.text_odf_message_room_data_not_present), getString(R.string.text_odf_message_ok_button))
                            .show(getFragmentManager(), "");
                }
            } else {
                OkDialogFragment.newInstance(DialogOriginID.ORIGIN_NAVIGATION_DRAWER_ITEM_ID, DialogActionID.ROOM_ERROR_ID, getString(R.string.text_odf_title_room_data_not_present), getString(R.string.text_odf_message_room_data_not_present), getString(R.string.text_odf_message_ok_button))
                        .show(getFragmentManager(), "");
            }
        }

        if(position == 3){
            Fragment f = getFragmentManager().findFragmentById(R.id.container);
            if(f != null && f instanceof BaseFragment){
                BaseFragment bf = (BaseFragment)f;
                RoomFragmentData rfd = bf.getRoomFragmentData();
                if(rfd != null) {
                    // Show title when close activity
                    onSectionSetTitle(getString(R.string.app_name));
                    restoreActionBar();

                    Intent intent = NumericValuePropActivity.makeBaseValuePropActivityByRoomID(this, NumericValuePropActivity.class, BaseValueData.TYPE_NUMERIC_VALUE, rfd.getID());
                    startActivity(intent);
                }else {
                    OkDialogFragment.newInstance(DialogOriginID.ORIGIN_NAVIGATION_DRAWER_ITEM_ID, DialogActionID.ROOM_ERROR_ID, getString(R.string.text_odf_title_room_data_not_present), getString(R.string.text_odf_message_room_data_not_present), getString(R.string.text_odf_message_ok_button))
                            .show(getFragmentManager(), "");
                }
            } else {
                OkDialogFragment.newInstance(DialogOriginID.ORIGIN_NAVIGATION_DRAWER_ITEM_ID, DialogActionID.ROOM_ERROR_ID, getString(R.string.text_odf_title_room_data_not_present), getString(R.string.text_odf_message_room_data_not_present), getString(R.string.text_odf_message_ok_button))
                        .show(getFragmentManager(), "");
            }
        }

        if(position == 4){
            Fragment f = getFragmentManager().findFragmentById(R.id.container);
            if(f != null && f instanceof BaseFragment){
                BaseFragment bf = (BaseFragment)f;
                RoomFragmentData rfd = bf.getRoomFragmentData();
                if(rfd != null) {
                    // Show title when close activity
                    onSectionSetTitle(getString(R.string.app_name));
                    restoreActionBar();

                    Intent intent = SensorValuePropActivity.makeBaseValuePropActivityByRoomID(this, SensorValuePropActivity.class, BaseValueData.TYPE_SENSOR_RAW_VALUE, rfd.getID());
                    startActivity(intent);
                }else {
                    OkDialogFragment.newInstance(DialogOriginID.ORIGIN_NAVIGATION_DRAWER_ITEM_ID, DialogActionID.ROOM_ERROR_ID, getString(R.string.text_odf_title_room_data_not_present), getString(R.string.text_odf_message_room_data_not_present), getString(R.string.text_odf_message_ok_button))
                            .show(getFragmentManager(), "");
                }
            } else {
                OkDialogFragment.newInstance(DialogOriginID.ORIGIN_NAVIGATION_DRAWER_ITEM_ID, DialogActionID.ROOM_ERROR_ID, getString(R.string.text_odf_title_room_data_not_present), getString(R.string.text_odf_message_room_data_not_present), getString(R.string.text_odf_message_ok_button))
                        .show(getFragmentManager(), "");
            }
        }

        if(position == 5){
            Fragment f = getFragmentManager().findFragmentById(R.id.container);
            if(f != null && f instanceof BaseFragment){
                BaseFragment bf = (BaseFragment)f;
                RoomFragmentData rfd = bf.getRoomFragmentData();
                if(rfd != null) {
                    // Show title when close activity
                    onSectionSetTitle(getString(R.string.app_name));
                    restoreActionBar();

                    Intent intent = SensorValuePropActivity.makeBaseValuePropActivityByRoomID(this, SensorValuePropActivity.class, BaseValueData.TYPE_SENSOR_CALIBR_VALUE, rfd.getID());
                    startActivity(intent);
                }else {
                    OkDialogFragment.newInstance(DialogOriginID.ORIGIN_NAVIGATION_DRAWER_ITEM_ID, DialogActionID.ROOM_ERROR_ID, getString(R.string.text_odf_title_room_data_not_present), getString(R.string.text_odf_message_room_data_not_present), getString(R.string.text_odf_message_ok_button))
                            .show(getFragmentManager(), "");
                }
            } else {
                OkDialogFragment.newInstance(DialogOriginID.ORIGIN_NAVIGATION_DRAWER_ITEM_ID, DialogActionID.ROOM_ERROR_ID, getString(R.string.text_odf_title_room_data_not_present), getString(R.string.text_odf_message_room_data_not_present), getString(R.string.text_odf_message_ok_button))
                        .show(getFragmentManager(), "");
            }
        }

        if(position == 6){
            // Show title when close activity
            onSectionSetTitle(getString(R.string.app_name));
            restoreActionBar();

            Intent intent = BaseValueCommClientDataPropActivity.makeBaseValueCommClientPropActivityByTranspProtocol(this, TCPIPClientPropActivity.class, BaseValueCommClientData.TraspProtocol.TCP_IP.getID());
            startActivity(intent);
        }
        if(position == 7){
            // Costruisco il frame...
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, BaseValueCommClientListFragment.newInstance(position + 1, position, BaseValueCommClientData.TraspProtocol.TCP_IP.getID()),getString(R.string.settings_title_section_open_room))
                    .commit();
        }

        if(position == 8){
            // Show title when close activity
            onSectionSetTitle(getString(R.string.app_name));
            restoreActionBar();

            Intent intent = BaseValueCommClientDataPropActivity.makeBaseValueCommClientPropActivityByTranspProtocol(this, BluetoothClientDataPropActivity.class, BaseValueCommClientData.TraspProtocol.BLUETOOTH.getID());
            startActivity(intent);
        }
        if(position == 9){
            // Costruisco il frame...
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, BaseValueCommClientListFragment.newInstance(position + 1, position, BaseValueCommClientData.TraspProtocol.BLUETOOTH.getID()),getString(R.string.settings_title_section_open_room))
                    .commit();
        }
    }

    @Override
    public void onSetNameAndOrientDialogFragmentClickListener(int position, String strTitle, String strName, boolean bLandscape) {
        if(position == 0){
            // Verifico che il nome sia valido
            if(isTagRoomValid(strName)){
                // Room Data
                RoomFragmentData rfd = new RoomFragmentData();
                rfd.setTag(strName);
                rfd.setLandscape(bLandscape);

                // Salvo
                long iRoomID = SQLContract.RoomEntry.save(rfd);
                if(iRoomID > 0){
                    // Costruisco il frame...
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, BaseFragment.newInstance(position + 1, iRoomID, true), rfd.getTAG())
                            .commit();
                    Toast.makeText(this, R.string.text_toast_room_saved_ok, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.text_toast_room_saved_error, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onListRoomFragmentCallbacksListener(int sectionNumber, int position, long id) {
        if(position == 1){
            // Prelevo i dati e TAG per Room
            Cursor cursor = SQLContract.RoomEntry.load(id);
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
                            .replace(R.id.container, BaseFragment.newInstance(position + 1, id, true), rfd.getTAG())
                            .commit();

                }
            }
        }
    }

    @Override
    public void onBaseValueCommClientFragmentCallbacksListener(int sectionNumber, int position, long id, int iType) {
        // Show title when close activity
        onSectionSetTitle(getString(R.string.app_name));
        restoreActionBar();
        if(position == 7){
            Intent intent = BaseValueCommClientDataPropActivity.makeBaseValueCommClientPropActivityByIDAndTranspProtocol(this, TCPIPClientPropActivity.class, id, iType);
            startActivity(intent);
        }
        if(position == 9){
            Intent intent = BaseValueCommClientDataPropActivity.makeBaseValueCommClientPropActivityByIDAndTranspProtocol(this, BluetoothClientDataPropActivity.class, id, iType);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            Fragment f = getFragmentManager().findFragmentById(R.id.container);
            if(f != null && f instanceof BaseFragment) {
                getMenuInflater().inflate(R.menu.menu_setting, menu);
                restoreActionBar();
                return true;
            } else {
                return super.onCreateOptionsMenu(menu);
            }
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
        if (id == R.id.id_item_menu_delete_room) {
            deleteRoom(DialogOriginID.ORIGIN_MENU_BUTTON_ID);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Helper method
    private boolean isTagRoomValid(String strTag) {
        if(strTag != null){
            if(!strTag.equals("")){
                if(!SQLContract.RoomEntry.isTagPresent(strTag)){
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

    private void deleteRoom(int iDialogOriginID){
        YesNoDialogFragment.newInstance(
                iDialogOriginID,
                DialogActionID.DELETE_CONFIRM_ID,
                getString(R.string.text_yndf_title_data_delete),
                getString(R.string.text_yndf_message_data_delete_confirmation),
                getString(R.string.text_yndf_btn_yes),
                getString(R.string.text_yndf_btn_no)
        ).show(getFragmentManager(), "");
    }

    @Override
    public void onYesNoDialogFragmentClickListener(int iDialogOriginID, int iDialogActionID, boolean bYes, boolean bNo) {
        if(iDialogOriginID == DialogOriginID.ORIGIN_MENU_BUTTON_ID) {
            if(iDialogActionID == DialogActionID.DELETE_CONFIRM_ID) {
                if(bYes) {
                    // Delete
                    deleteRoomValueData(iDialogOriginID);
                }

                if(bNo) {
                    // No action...
                }
            }
        }
    }

    private void deleteRoomValueData(int iDialogOriginID){
        Fragment f = getFragmentManager().findFragmentById(R.id.container);
        if(f != null && f instanceof BaseFragment){
            BaseFragment bf = (BaseFragment)f;
            RoomFragmentData rfd = bf.getRoomFragmentData();
            if(rfd != null) {
                // First, i delete all Controls in the room
                SQLContract.BaseValueEntry.deleteByRoomID(rfd.getID());
                // Ok, i can delete the room
                if(SQLContract.RoomEntry.deleteByID(rfd.getID())){
                    // Ok
                    OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.DELETING_OK_ID, getString(R.string.text_odf_title_deleting), getString(R.string.text_odf_message_deleting_ok), getString(R.string.text_odf_message_ok_button))
                            .show(getFragmentManager(), "");
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .remove(f)
                            .commit();
                    vedere qui perche' viene riconosciuto il fragment sebbene sia cancellato...'

                    return ;
                }
                OkDialogFragment.newInstance(iDialogOriginID, DialogActionID.DELETING_ERROR_ID, getString(R.string.text_odf_title_deleting), getString(R.string.text_odf_message_deleting_error), getString(R.string.text_odf_message_ok_button))
                        .show(getFragmentManager(), "");
                return;
            }
        }
        OkDialogFragment.newInstance(DialogOriginID.ORIGIN_NAVIGATION_DRAWER_ITEM_ID, DialogActionID.ROOM_ERROR_ID, getString(R.string.text_odf_title_room_data_not_present), getString(R.string.text_odf_message_room_data_not_present), getString(R.string.text_odf_message_ok_button))
                .show(getFragmentManager(), "");

    }

    public static Intent makeSettingsActivity(Context context)
    {
        Intent intent = new Intent();
        intent.setClass(context, SettingsActivity.class);
        return intent;
    }

}
