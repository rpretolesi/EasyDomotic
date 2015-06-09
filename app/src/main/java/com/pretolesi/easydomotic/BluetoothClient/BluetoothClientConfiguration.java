package com.pretolesi.easydomotic.BluetoothClient;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class BluetoothClientConfiguration extends ListActivity {
    private static final String TAG = "BluetoothClientConfiguration";

    private static final int REQUEST_ENABLE_BT = 1;
    //private android.widget.SimpleCursorAdapter m_Adapter;
    private BluetoothListAdapter m_blAdapter;
    private BluetoothAdapter m_BluetoothAdapter;

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver m_Receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if(context != null && intent != null) {
                String action = intent.getAction();
                if(action != null) {
                    // When discovery finds a device
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        // Get the BluetoothDevice object from the Intent
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if(device != null) {
                            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                                // Add the name and address to an array adapter to show in a ListView
                                if (m_blAdapter != null) {

                                    m_blAdapter.add(new BluetoothClientData(device.getName(),device.getAddress()));
                                }
                            }
                            }
                    }
                    if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        setProgressBarIndeterminateVisibility(false);
                        setTitle(" Discovering completed");
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Progress bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        // Get the Bluetooth reference
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (m_BluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this,"Device does not support Bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }

        // Register the BroadcastReceiver
        if(m_Receiver != null) {
            IntentFilter filter;
            filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(m_Receiver, filter);
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(m_Receiver, filter);
        }
        // Initialize the adapter
        List<BluetoothClientData> albcd = new ArrayList<>();
        m_blAdapter = new BluetoothListAdapter(this, albcd);
        setListAdapter(m_blAdapter);

/*
//        setContentView(R.layout.bluetooth_client_configuration_list_activity);
        // Query for all people contacts using the Contacts.People convenience class.
        // Put a managed wrapper around the retrieved cursor so we don't have to worry about
        // requerying or closing it as the activity changes state.
        mCursor = this.getContentResolver().query(People.CONTENT_URI, null, null, null, null);
        startManagingCursor(mCursor);

        // Now create a new list adapter bound to the cursor.
        // SimpleListAdapter is designed for binding to a Cursor.
        m_Adapter = new SimpleCursorAdapter(
                this, // Context.
                android.R.layout.two_line_list_item,  // Specify the row template to use (here, two columns bound to the two retrieved cursor
                rows).
                mCursor,                                              // Pass in the cursor to bind to.
        new String[] {People.NAME, People.COMPANY},           // Array of cursor columns to bind to.
                new int[] {android.R.id.text1, android.R.id.text2});  // Parallel array of which template objects to bind to those columns.

        // Bind to our new adapter.
*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (m_BluetoothAdapter != null) {
            if (!m_BluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else{
                getBluetoothDevice();
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == RESULT_OK){
                // Ok
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                Toast.makeText(this,"Device must turn on Bluetooth", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        if(m_blAdapter != null) {
            m_blAdapter.clear();
        }
        if (m_BluetoothAdapter.isDiscovering()) {
            m_BluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(m_Receiver != null) {
            unregisterReceiver(m_Receiver);
        }
    }

    private void getBluetoothDevice(){
        if(m_BluetoothAdapter != null && m_blAdapter != null){
            Set<BluetoothDevice> pairedDevices = m_BluetoothAdapter.getBondedDevices();
            if(pairedDevices != null){
                // If there are paired devices
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                            if(device != null){
                            // Add the name and address to an array adapter to show in a ListView
                                m_blAdapter.add(new BluetoothClientData(device.getName(),device.getAddress()));
                        }
                    }
                }
                // Enable Discovering
                if (m_BluetoothAdapter.isDiscovering()) {
                    m_BluetoothAdapter.cancelDiscovery();
                }
                // Request discover from BluetoothAdapter
                m_BluetoothAdapter.startDiscovery();

                // Progress bar
                setProgressBarIndeterminateVisibility(true);
                setTitle("Scanning");
            }
        }
    }
}



//Sample
//
//
//public class DeviceListActivity extends Activity {
//    // Debugging
//    private static final String TAG = "DeviceListActivity";
//    private static final boolean D = true;
//
//    // Return Intent extra
//    public static String EXTRA_DEVICE_ADDRESS = "device_address";
//
//    // Member fields
//    private BluetoothAdapter mBtAdapter;
//    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
//    private ArrayAdapter<String> mNewDevicesArrayAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Setup the window
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
//        setContentView(R.layout.activity_device_list);
//
//
//        // Initialize the button to perform device discovery
//        Button scanButton = (Button) findViewById(R.id.button_scan);
//        scanButton.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                doDiscovery();
//                v.setVisibility(View.GONE);
//            }
//        });
//
//        // Initialize array adapters. One for already paired devices and
//        // one for newly discovered devices
//        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
//        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
//
//        // Find and set up the ListView for paired devices
//        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
//        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
//        pairedListView.setOnItemClickListener(mDeviceClickListener);
//
//        // Find and set up the ListView for newly discovered devices
//        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
//        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
//        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
//
//        // Register for broadcasts when a device is discovered
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        this.registerReceiver(mReceiver, filter);
//
//        // Register for broadcasts when discovery has finished
//        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        this.registerReceiver(mReceiver, filter);
//
//        // Get the local Bluetooth adapter
//        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        // Get a set of currently paired devices
//        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
//
//        // If there are paired devices, add each one to the ArrayAdapter
//        if (pairedDevices.size() > 0) {
//            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
//            for (BluetoothDevice device : pairedDevices) {
//                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//            }
//        } else {
//            String noDevices = getResources().getText(R.string.none_paired).toString();
//            mPairedDevicesArrayAdapter.add(noDevices);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        // Make sure we're not doing discovery anymore
//        if (mBtAdapter != null) {
//            mBtAdapter.cancelDiscovery();
//        }
//         // Unregister broadcast listeners
//        this.unregisterReceiver(mReceiver);
//    }
//
//    /**
//     * Start device discover with the BluetoothAdapter
//     */
//private void doDiscovery() {
//        if (D) Log.d(TAG, "doDiscovery()");
//
//        // Indicate scanning in the title
//        setProgressBarIndeterminateVisibility(true);
//        setTitle(R.string.scanning);
//
//        // Turn on sub-title for new devices
//        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
//
//        // If we're already discovering, stop it
//        if (mBtAdapter.isDiscovering()) {
//        mBtAdapter.cancelDiscovery();
//        }
//        // Request discover from BluetoothAdapter
//        mBtAdapter.startDiscovery();
//        }
//
//// The on-click listener for all devices in the ListViews
//private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
//public void onItemClick(AdapterView<?> av, View v, int i, long l) {
//        // Cancel discovery because it's costly and we're about to connect
//        mBtAdapter.cancelDiscovery();
//
//        // Get the device MAC address, which is the last 17 chars in the View
//        String info = ((TextView) v).getText().toString();
//        String address = info.substring(info.length() - 17);
//
//
//        }
//        };
//
//// The BroadcastReceiver that listens for discovered devices and
//// changes the title when discovery is finished
//private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//@Override
//public void onReceive(Context context, Intent intent) {
//        String action = intent.getAction();
//
//        // When discovery finds a device
//        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//        // Get the BluetoothDevice object from the Intent
//        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//        // If it's already paired, skip it, because it's been listed already
//        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
//        mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//        }
//        // When discovery is finished, change the Activity title
//        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//        setProgressBarIndeterminateVisibility(false);
//        setTitle(R.string.select_device);
//        if (mNewDevicesArrayAdapter.getCount() == 0) {
//        String noDevices = getResources().getText(R.string.none_found).toString();
//        mNewDevicesArrayAdapter.add(noDevices);
//        }
//        }
//        }
//        };
//
//
//