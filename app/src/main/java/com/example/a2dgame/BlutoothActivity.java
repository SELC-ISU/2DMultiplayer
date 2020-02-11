package com.example.a2dgame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;

public class BlutoothActivity extends Activity {

    //the resultid's to test and see if the action was completed correctly
    public final int ENABLE_BT_REQUEST = 1;
    public final int ENABLE_DISCOVERABILITY_DURATION = 180;
    public final int ENABLE_DISCOVERABILITY = 2;

    private TextView MainText = (TextView)findViewById(R.id.txtMain);
    private TextView SecondaryText = (TextView)findViewById(R.id.txtSecondary);
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public void enableBT(){

        if (bluetoothAdapter == null) {

            MainText.setText("No BlueTooth");
        }
        else if(!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST);
        }
        else{
            MainText.setText("Already Done!");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == ENABLE_BT_REQUEST) {
            if (resultCode == RESULT_OK) {
                // BT Was enabled.  Here we will just display it
                // to the user.
                SecondaryText.setText("IT WORKED");
            }
        }
        if (requestCode == ENABLE_DISCOVERABILITY) {
            if (resultCode > 0) {
                // BT Discoverability was set.  Here we will just display it
                // to the user.
                SecondaryText.setText("Discoverabities Worked");
            }
            else if(resultCode == RESULT_CANCELED){
                SecondaryText.setText("Discoverabities NO Worked");
            }
        }

    }

    public void commenceDiscovery(){

        bluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver,filter);

        bluetoothAdapter.cancelDiscovery(); //This turns off discovery because it uses
                                            //a lot of resources but discover is said to
                                            //go for 12 seconds with sacnning period so if
                                            //necessary add a delay timer here
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };

    public void destroyConnection(){
        unregisterReceiver(receiver);
    }

    public void makeDiscoverable(){

        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, ENABLE_DISCOVERABILITY_DURATION);
        startActivityForResult(discoverableIntent, ENABLE_DISCOVERABILITY);

    }
}
