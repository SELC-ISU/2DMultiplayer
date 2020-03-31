package com.example.a2dgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {


    private static final String TAG = "BLUETOOTH_TAG";
    public final int ENABLE_BT_REQUEST = 1;
    public final int ENABLE_DISCOVERABILITY_DURATION = 600;
    public final int ENABLE_DISCOVERABILITY = 2;

    public final int DEVICE_ATTEMPTING_CONNECTION = 2;
    public final int DEVICE_CONNECTED = 1;
    public final int DEVICE_CONNECTION_FAILED = 0;
    public final int DEVICE_CONNECTION_MESSAGE_FAILED = -1;

    StringBuilder builder;
    private AlertDialog dialog;

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothService service;  //declarations for bluetooth items

    public ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<>();
    public DeviceListAdapter deviceListAdapter;   //declarations for finding devices
    ListView lvNewDevices;

    ListView lvTextMessages;
    EditText msgBox;
    ArrayAdapter<String> lvTextMsgAdapter;  //declarations for message sending
    ArrayList<String> listTexts;

    DeviceConnection deviceConnected = DeviceConnection.DEVICE_NOT_CONNECTED;

    ScrollView sv;

    Button btnSend, btnDisconnect, btnJoin, btnHost;
    TextView txtAvailable;


    /**
     * This runs when the app is first started
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switchToMainLayout();

        switchToJoinHostLayout();

        enableBT();

        service = new BluetoothService(MainActivity.this);  //instantiates the bluetoothService object in order to connect to device and perform operations

        builder = new StringBuilder();  //this would be used to build longer strings from many read in messages
        discoveredDevices = new ArrayList<>();

        listTexts = new ArrayList<>();

        lvTextMsgAdapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listTexts);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver3, new IntentFilter("incomingMessage"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver4, new IntentFilter("deviceConnected"));

        sv = (ScrollView)findViewById(R.id.scrollView);
    }

    /**
     * Gathers all button clicks and determines which on it was
     * the parameter v is what layout it is from in order to work accross layouts
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnJoin:

                makeDiscoverable();

                break;


            case R.id.btnHost:

                commenceDiscovery(true);  //starts server discovery

                break;


            case R.id.btnDisconnect:

                closeSockets();  //disconnects any device
                Log.d(TAG,"DISCONNECTING from messageing screen");
                switchToJoinHostLayout();  //switches to the connect options screen
                deviceConnected = DeviceConnection.DEVICE_NOT_CONNECTED;

                break;

            case R.id.btnSend:

                String str = String.valueOf(msgBox.getText());

                Log.d(TAG,"Sending the Message: " + str);
                byte[] b = str.getBytes();
                service.write(b);
                msgBox.setText("");
                str = "Me: " + str;
                lvTextMsgAdapter.add(str);
                lvTextMessages.smoothScrollToPosition(lvTextMsgAdapter.getCount()-1);
                //this is where you will send the message in the edit box;

                break;


            default:
                break;
        }

    }

    /**
     *
     * This takes in a click on the ListView for the discovered devices
     * Once an item is clicked on it is sent to BluetoothService to attempt a connection
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG,"ITEM CLICK: you clicked on a device");

        bluetoothAdapter.cancelDiscovery();  //cancels this because it is a resource intensive action

        service.startSearchServer(discoveredDevices.get(position)); //starts searching for this device and what UUID they are putting out for a connection

    }

    /**
     * This executes when the app is closed ro "destroyed"
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();

        service.cancel();

    }

    /**
     * This calls BluetoothService's cancel method in order to close all connections
     */
    public void closeSockets() {

        Log.d(TAG, "Closing sockets");
        service.cancel();

    }


    /**
     * Makes the client device's bluetooth signiture open to see by other deivce and open for a connection
     */
    public void makeDiscoverable(){

        discoveredDevices.clear();
        bluetoothAdapter.cancelDiscovery();
        lvNewDevices.setVisibility(View.INVISIBLE);
        txtAvailable.setVisibility(View.INVISIBLE);

        Log.d(TAG,"Making device discoverable");
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, ENABLE_DISCOVERABILITY_DURATION);  //sets the amount of time to be discoverable, ENABLE_DISCOVERABILITY_DURATION

        startActivityForResult(discoverableIntent, ENABLE_DISCOVERABILITY); //starts the action to make discoverable

        IntentFilter intentFilter = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(receiver2, intentFilter);

        service.startSearchingClient(bluetoothAdapter);  // starts listening for the UUID connection from server

    }

    /**
     * Starts bluetooth discovery on the server device
     * takes in the parameter of a, this if false will stop the search
     * @param a
     */
    public void commenceDiscovery(boolean a){


        checkBTPermissions(); //checks to ensure that this permission is enabled on device
                                //will come up with permission request on phone
                                //only necessary on new android versios
        lvNewDevices.setVisibility(View.VISIBLE);
        txtAvailable.setVisibility(View.VISIBLE);

        if(bluetoothAdapter.isDiscovering()){
            discoveredDevices.clear();
            bluetoothAdapter.cancelDiscovery();  //cancels discovery if already going and restarts it
            Log.d(TAG,"BtnServer, Cancelling Discovery");
            bluetoothAdapter.startDiscovery();
            Log.d(TAG,"BtnServer, Strating Discovery");

        }
        else if (a) {
            discoveredDevices.clear();
            bluetoothAdapter.startDiscovery();    //clears the devices discovered already if any, and then starts discovery
            Log.d(TAG,"BtnDiscover, starting Discovery");
        }


        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);         //recieves any devices that have been connected

        if (!a) {
            Log.d(TAG,"BtnOFF, Cancelling Discovery");
            bluetoothAdapter.cancelDiscovery();             //if nessecary turns off discovery because it useses a lot of resources
        }

    }

    /**
     * This method checks if the required bluetooth permissions are enabled for this app on the phone
     * If they are not it will come up on the phone asking if they would like to enable them
     * This is only required on android versions above Lollipop
     */
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

    /**
     * This method enables the bluetooth on the device
     *
     * @return a boolean to tell you if bluetooth was already enabled(true) or if it was enabled before execution(false)
     */
    public boolean enableBT(){

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST);
            return true;
        }

        return false;

    }

    /**
     * Switches the focus of the app to the messaging screen
     */
    public void switchToMessageLayout(){
        setContentView(R.layout.message_layout);
        lvTextMessages = (ListView) findViewById(R.id.lvTextMessages);
        btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(this);
        btnSend= (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        msgBox = (EditText)findViewById(R.id.msgBox);
        lvTextMessages.setAdapter(lvTextMsgAdapter);

        msgBox.setOnClickListener(this);

    }

    /**
     * Switches the focus of the app to the connection screen
     */
    public void switchToJoinHostLayout(){
        setContentView(R.layout.join_host_layout);

        btnJoin = (Button) findViewById(R.id.btnJoin);
        btnJoin.setOnClickListener(this);

        btnHost = (Button) findViewById(R.id.btnHost);
        btnHost.setOnClickListener(this);

        txtAvailable= (TextView) findViewById(R.id.txtAvailable);
        txtAvailable.setVisibility(View.INVISIBLE);

        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);

        lvNewDevices.setOnItemClickListener(MainActivity.this);

        lvNewDevices.setVisibility(View.INVISIBLE);
    }

    /**
     * Switches the focus of the app to the main screen
     */
    public void switchToMainLayout(){
        setContentView(R.layout.activity_main);

    }


    /**
     * This reciever takes in any new devices found when discovery is on and puts them to the list of discovered devices
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG,"Discovering: ACTION FOUND");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {


                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discoveredDevices.add(device);

                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                Log.d(TAG, "Discovering: DeviceFound: " + deviceName + ", " + deviceHardwareAddress);

                deviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view,discoveredDevices);
                lvNewDevices.setAdapter(deviceListAdapter);

            }
        }
    };

    /**
     * This receiver takes in the action of the trying to enable discoverability on the client device
     * and determines if it was succesful and what state it is in now
     */
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

            }
        }
    };

    /**
     * This receiver takes in the messages read in from the other device sent here from the
     * BluetoothService class. The String text recieves this message. This is where any decisions on
     * what kind of message is being sent through should be determined and delt with
     */
    private final BroadcastReceiver receiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String text = intent.getStringExtra("theMessage");

            //builder.append(text + "\n");
            lvTextMsgAdapter.add(text);
            lvTextMessages.smoothScrollToPosition(lvTextMsgAdapter.getCount()-1);
            //now this can be set to the text view and scroll slowly to the new message;
        }
    };

    /**
     * This receiver takes in the message from the BluetoothService class on how successful the
     * device connection went and then sets this.deviceConnected accrodingly for later use to see
     * and check if a deivce is connected
     */
    private final BroadcastReceiver receiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            DeviceConnection check = DeviceConnection.enumOf(intent.getIntExtra("checkValue",-1));

            deviceConnected = check;

            if(deviceConnected == DeviceConnection.DEVICE_CONNECTED) {
                Log.d(TAG,"CLOSING AlertDialod");
                dismissMessage();

                switchToMessageLayout();
            }
            else if(deviceConnected == DeviceConnection.DEVICE_CONNECTION_FAILED) {
                Log.d(TAG, "DEVICE CONNECTION FAILED");
                showMessage("Connection Failed", 1);
            }
            else if(deviceConnected == DeviceConnection.DEVICE_CONNECTION_MESSAGE_FAILED)
                Log.d(TAG,"Intent Not Sent for device connection confirmation");
            else if(deviceConnected == DeviceConnection.DEVICE_ATTEMPTING_CONNECTION) {
                Log.d(TAG, "Device Connecting...");

               showMessage("Connecting...", 2);
            }
            else if(deviceConnected == DeviceConnection.DEVICE_DISCONNECTED){
                Log.d(TAG,"Other DEvice was Disconnected");
                closeSockets();
                switchToJoinHostLayout();
            }


        }
    };



    public void showMessage(String str, int check){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(str);

        if(check == 1){
            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dismissMessage();
                }
            });
        }

        if(check == 2){
            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dismissMessage();
                    closeSockets();
                }
            });
        }

        dialog = builder.create();
        dialog.show();

    }



    public void dismissMessage(){
        Log.d(TAG,"log box");
        if(dialog != null) {
            Log.d(TAG,"Dismissing the Dialog box");
            dialog.dismiss();
        }

    }

}
