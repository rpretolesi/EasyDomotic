package com.pretolesi.easydomotic.BluetoothClient;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.pretolesi.easydomotic.LoadersUtils.Loaders;
import com.pretolesi.easydomotic.R;

/**
 * Created by ricca_000 on 07/06/2015.
 */
public class BluetoothClientConfiguration extends Activity{
    private static final String TAG = "BluetoothClientDataPropActivity";

    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_client_configuration_activity);

        usare una activity list e provare se e' sufficiente per avere i paired and i discovery....'
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Check if Bluetooth stack is present
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this,"Device does not support Bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }

        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
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
    }
}
