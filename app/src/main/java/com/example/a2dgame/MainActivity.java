package com.example.a2dgame;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "BLUETOOTH";
    TextView mainText;
    public final int ENABLE_BT_REQUEST = 1;
    public final int ENABLE_DISCOVERABILITY_DURATION = 600;
    public final int ENABLE_DISCOVERABILITY = 2;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    AcceptThread acceptThread;
    ConnectThread connectThread;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        mainText = (TextView)findViewById(R.id.txtMain);




        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST);
        }

        setContentView(R.layout.secondary_layout);
        Button btnEnDiscov = (Button) findViewById(R.id.btnClient);
        btnEnDiscov.setOnClickListener(this); // calling onClick() method

        Button btnMakeDiscov = (Button) findViewById(R.id.btnServer);
        btnMakeDiscov.setOnClickListener(this);

        Button btnOff = (Button) findViewById(R.id.btnOff);
        btnOff.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnClient:

                makeDiscoverable();

                acceptThread = new AcceptThread(bluetoothAdapter);
                //acceptThread.run();
                //acceptThread.start();

                break;


            case R.id.btnServer:

                commenceDiscovery(true);

                break;

            case R.id.btnOff:

                commenceDiscovery(false);

                break;


            default:
                break;
        }

    }


    @Override
    protected void onDestroy(){
        super.onDestroy();



    }

    public void makeDiscoverable(){
    Log.d(TAG,"Making device discoverable");
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, ENABLE_DISCOVERABILITY_DURATION);

        startActivityForResult(discoverableIntent, ENABLE_DISCOVERABILITY);

        IntentFilter intentFilter = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

        registerReceiver(receiver2, intentFilter);

    }
    public void commenceDiscovery(boolean a){

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        mainText = (TextView)findViewById(R.id.txtMain);
        /*if (pairedDevices.size() > 0) {
            a = false;
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                mainText.append(deviceName+ " ");
                String deviceHardwareAddress = device.getAddress(); // MAC address
                //connectThread = new ConnectThread(device);
            }
        }
        else {*/
            if (a)
                bluetoothAdapter.startDiscovery();

            mainText.setText("here");
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, filter);

            if (!a)
                bluetoothAdapter.cancelDiscovery(); //This turns off discovery because it uses
            //a lot of resources but discover is said to
            //go for 12 seconds with sacnning period so if
            //necessary add a delay timer here
        }
//    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.

                bluetoothAdapter.cancelDiscovery();

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                mainText = (TextView)findViewById(R.id.txtMain);
                mainText.setText(deviceName);

                connectThread = new ConnectThread(device);
                connectThread.start();
            }
        }
    };

    private final BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (bluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "onRecieve: CONNECTABLE DISCOVERABLE");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG,"reciever2: CONNECTABLE");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG,"receiver2: SCAN NONE");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "receiver2: connecting");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG,"reciever2: connected");

                }
            }
        }
    };

    public void printStatement(String s){

        mainText = (TextView)findViewById(R.id.txtMain);
        mainText.setText(s);

    }


}
