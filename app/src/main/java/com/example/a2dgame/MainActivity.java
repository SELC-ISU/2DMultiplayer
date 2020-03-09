package com.example.a2dgame;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {


    private static final String TAG = "BLUETOOTH_TAGS";
    TextView mainText;
    public final int ENABLE_BT_REQUEST = 1;
    public final int ENABLE_DISCOVERABILITY_DURATION = 600;
    public final int ENABLE_DISCOVERABILITY = 2;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    AcceptThread acceptThread;
    ConnectThread connectThread;
    public ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<>();
    public DeviceListAdapter deviceListAdapter;
    ListView lvNewDevices;


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

        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        discoveredDevices = new ArrayList<>();

        lvNewDevices.setOnItemClickListener(MainActivity.this);

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

            case R.id.btnClose:

                    closeSockets();

                break;


            default:
                break;
        }

    }

    private void closeSockets() {

        Log.d(TAG, "Closing sockets");
        connectThread.cancel();
        acceptThread.cancel();

    }


    @Override
    protected void onDestroy(){
        super.onDestroy();

        connectThread.cancel();
        acceptThread.cancel();

    }

    public void makeDiscoverable(){
    Log.d(TAG,"Making device discoverable");
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, ENABLE_DISCOVERABILITY_DURATION);

        startActivityForResult(discoverableIntent, ENABLE_DISCOVERABILITY);

        IntentFilter intentFilter = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(receiver2, intentFilter);

        AcceptThread acceptThread = new AcceptThread(bluetoothAdapter);
        acceptThread.start();



    }
    public void commenceDiscovery(boolean a){

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        mainText = (TextView)findViewById(R.id.txtMain);
        checkBTPermissions();
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG,"BtnServer, Cancelling Discovery");
            bluetoothAdapter.startDiscovery();
            Log.d(TAG,"BtnServer, Strating Discovery");

        }
        else if (a) {
            discoveredDevices.clear();
            bluetoothAdapter.startDiscovery();
            Log.d(TAG,"BtnDiscover, starting Discovery");
        }

        mainText.setText("here");
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        if (!a) {
            Log.d(TAG,"BtnOFF, Cancelling Discovery");
            bluetoothAdapter.cancelDiscovery();
        }
            //This turns off dimaniscovery because it uses
        //a lot of resources but discover is said to
        //go for 12 seconds with sacnning period so if
        //necessary add a delay timer here
    }

    private void checkBTPermissions(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");

            if(permissionCheck != 0){
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1001);
            }
            else{
                Log.d(TAG,"checkBTPermissions: No need to check permissions, SK < LOLLIPOP");
            }

        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG,"Discovering: ACTION FOUND");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                bluetoothAdapter.cancelDiscovery();

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discoveredDevices.add(device);

                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                Log.d(TAG, "Discovering: DeviceFound: " + deviceName + ", " + deviceHardwareAddress);

                deviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view,discoveredDevices);
                lvNewDevices.setAdapter(deviceListAdapter);

                //connectThread = new ConnectThread(device);
                //connectThread.start();
            }
        }
    };

    private final BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (bluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, bluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "onRecieve: Discoverability Enabled");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG,"reciever2: Discoverability disabled, still able to recieve connections");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG,"receiver2: Discoverability off, no able to recieve");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "receiver2: connecting");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG,"reciever2: connected");

                }
                String deviceName = bluetoothAdapter.getName();
                String deviceHardwareAddress = bluetoothAdapter.getAddress();
                printStatement(deviceName + ", " + deviceHardwareAddress);
            }
        }
    };

    public void printStatement(String s){

        mainText = (TextView)findViewById(R.id.txtMain);
        mainText.setText(s);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG,"ITEM CLICK: you clicked on a device");
         ConnectThread connectThread= new ConnectThread(discoveredDevices.get(position));
         connectThread.start();
    }

}
