package com.example.a2dgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {


    private final String CHANNEL_ID = "tictactoeCh12345324";
    private final int NOTIF_ID = 101011;
    private static final String ACTION_NOTIF = "action_notif";

    public final static String GAME_STR = "G";
    public final static String CHAT_STR = "C";
    public final static String MICELLANEOUS_STR = "M";

    private static final String TAG = "BLUETOOTH_TAG";
    public final int ENABLE_BT_REQUEST = 1;
    public final int ENABLE_DISCOVERABILITY_DURATION = 60;
    public final int ENABLE_DISCOVERABILITY = 2;


    private AlertDialog dialog;

    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothService service;  //declarations for bluetooth items

    private ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<>();
    private DeviceListAdapter deviceListAdapter;   //declarations for finding devices
    private ListView lvNewDevices;

    private ArrayList<String> newMessages = new ArrayList<>();
    private MessageListAdapter messageAdapter;
    private ListView lvTextMessages;
    private EditText msgBox;

    public DeviceConnection deviceConnected = DeviceConnection.DEVICE_NOT_CONNECTED;

    ScrollView sv;

    public Button btnSend, btnJoin, btnHost, btnSinglePlayer, btnTwoPlayer, btnSingleGame,
            btnThreeGames, btnFiveGames, btnChat, btnBackToStart, btnBack;

    //stuff Alex added
    public ImageView ONum1, ONum2, ONum3, ONum4, ONum5, ONum6, ONum7, ONum8, ONum9;
    public ImageView XNum1, XNum2, XNum3, XNum4, XNum5, XNum6, XNum7, XNum8, XNum9;

    public RadioButton btnRadio;

    private TextView txtAvailable;

    private ProgressBar pbar;
    private ProgressBar pbar2;

    public boolean newGameMessage = false;
    public String gameMessage = "";
    public boolean twoPlayer = false;

    public GameType gameType;

    public boolean isHost = false;


    private ActivityManager.RunningAppProcessInfo myProcess;
    private NotificationManagerCompat notificationManagerCompat;

    //More Alex stuff (need an instance of TicTacToe for the getSymbol method)
    TicTacToe ttt = new TicTacToe(MainActivity.this);


    /**
     * This runs when the app is first started
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);


        myProcess = new ActivityManager.RunningAppProcessInfo();

        switchToStartScreenLayout();
        createNotificationChannel();

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
                isHost = false;
                pbar2.setVisibility(View.VISIBLE);

                break;


            case R.id.btnHost:

                commenceDiscovery(true);  //starts server discovery
                isHost = true;
                pbar2.setVisibility(View.VISIBLE);

                break;


            case R.id.btnSend:

                String str = String.valueOf(msgBox.getText());

                write(str,CHAT_STR);
                msgBox.setText("");
                str = "Me: " + str;

                newMessages.add(str);

                messageAdapter.notifyDataSetChanged();

                lvTextMessages.smoothScrollToPosition(messageAdapter.getCount()-1);
                //this is where you will send the message in the edit box;

                break;

            case R.id.btnSinglePlayer:
                twoPlayer = false;
                switchToStartGameScreenLayout();
                break;

            case R.id.btnTwoPlayer:

                twoPlayer = true;
                switchToJoinHostLayout();

                break;

            case R.id.btnBackToStart:
                twoPlayer = false;
                closeSockets();
                switchToStartScreenLayout();
                cancelNotification();
                isHost = false;

                break;

            case R.id.btnChat1:
            case R.id.btnChat2:

                    switchToMessageLayout();
                    cancelNotification();

                break;

            case R.id.btnSingleGame:

                gameType = GameType.SINGLE;

                break;

            case R.id.btnThreeGames:

                gameType = GameType.BEST_OF_THREE;

                break;

            case R.id.btnFiveGames:

                gameType = GameType.BEST_OF_FIVE;

                break;

            case R.id.btnBack:

                if(isHost)
                    switchToStartGameScreenLayout();
                else
                    switchToWaitingLayout();

            //Beginning of Alex's stuff
            case R.id.ONum1:
            case R.id.XNum1:
                //determines which imageView in the cell should be set (X or O) and sets it to visible
                if(ttt.getSymbol().equals("O")){
                    ONum1.setVisibility(View.VISIBLE);
                }else{
                    XNum1.setVisibility((View.VISIBLE));
                }

                //sets the value in the string array to either X or O
                ttt.modifyGameTrackerClick(1);

                break;


            case R.id.ONum2:
            case R.id.XNum2:
                if(ttt.getSymbol().equals("O")){
                    ONum2.setVisibility(View.VISIBLE);
                }else{
                    XNum2.setVisibility((View.VISIBLE));
                }
                ttt.modifyGameTrackerClick(2);
                break;

            case R.id.ONum3:
            case R.id.XNum3:
                if(ttt.getSymbol().equals("O")){
                    ONum3.setVisibility(View.VISIBLE);
                }else{
                    XNum3.setVisibility((View.VISIBLE));
                }
                ttt.modifyGameTrackerClick(3);
                break;

            case R.id.ONum4:
            case R.id.XNum4:
                if(ttt.getSymbol().equals("O")){
                    ONum4.setVisibility(View.VISIBLE);
                }else{
                    XNum4.setVisibility((View.VISIBLE));
                }
                ttt.modifyGameTrackerClick(4);
                break;

            case R.id.ONum5:
            case R.id.XNum5:
                if(ttt.getSymbol().equals("O")){
                    ONum5.setVisibility(View.VISIBLE);
                }else{
                    XNum5.setVisibility((View.VISIBLE));
                }
                ttt.modifyGameTrackerClick(5);
                break;

            case R.id.ONum6:
            case R.id.XNum6:
                if(ttt.getSymbol().equals("O")){
                    ONum6.setVisibility(View.VISIBLE);
                }else{
                    XNum6.setVisibility((View.VISIBLE));
                }
                ttt.modifyGameTrackerClick(6);
                break;

            case R.id.ONum7:
            case R.id.XNum7:
                if(ttt.getSymbol().equals("O")){
                    ONum7.setVisibility(View.VISIBLE);
                }else{
                    XNum7.setVisibility((View.VISIBLE));
                }
                ttt.modifyGameTrackerClick(7);
                break;

            case R.id.ONum8:
            case R.id.XNum8:
                if(ttt.getSymbol().equals("O")){
                    ONum8.setVisibility(View.VISIBLE);
                }else{
                    XNum8.setVisibility((View.VISIBLE));
                }
                ttt.modifyGameTrackerClick(8);
                break;

            case R.id.XNum9:
            case R.id.ONum9:
                if(ttt.getSymbol().equals("O")){
                    ONum9.setVisibility(View.VISIBLE);
                }else{
                    XNum9.setVisibility((View.VISIBLE));
                }
                ttt.modifyGameTrackerClick(9);

                break;

            default:
                break;
        }



    }

    @Override
    public void onBackPressed() {

        if(findViewById(R.id.btnHost) != null){

            twoPlayer = false;
            closeSockets();
            switchToStartScreenLayout();
            isHost = false;
        }
        else if(findViewById(R.id.btnSinglePlayer) != null){
            showMessage("You are exiting the app...",3);
        }
        else if(findViewById(R.id.btnFiveGames) != null){
            twoPlayer = false;
            closeSockets();
            switchToStartScreenLayout();
            isHost = false;
        }
        else if(findViewById(R.id.pBar)!=null){
            twoPlayer = false;
            isHost = false;
            closeSockets();
        }
        else if(findViewById(R.id.btnBack)!=null){
            btnBack.performClick();
        }

        cancelNotification();
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
        lvNewDevices.setVisibility(View.INVISIBLE);
        txtAvailable.setVisibility(View.INVISIBLE);
        pbar2.setVisibility(View.INVISIBLE);

        showMessage("Connecting...", 2);

        service.startSearchServer(discoveredDevices.get(position)); //starts searching for this device and what UUID they are putting out for a connection

    }

    /**
     * This executes when the app is closed ro "destroyed"
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();

        cancelNotification();

        try {
            unregisterReceiver(receiver);
            unregisterReceiver(receiver2);
            unregisterReceiver(receiver3);
            unregisterReceiver(receiver4);
        }catch (IllegalArgumentException e){}

        service.cancel();

    }

    /**
     * This calls BluetoothService's cancel method in order to close all connections
     */
    public void closeSockets() {

        Log.d(TAG, "Closing sockets");

        if(deviceConnected == DeviceConnection.DEVICE_CONNECTED)
            deviceConnected = DeviceConnection.DEVICE_DISCONNECTED;
        else
            deviceConnected = DeviceConnection.DEVICE_NOT_CONNECTED;

        if(service != null) {
            service.cancel();
        }

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
    

    public void switchToWaitingLayout(){

        setContentView(R.layout.waiting_screen_layout);
        pbar = (ProgressBar)findViewById(R.id.pBar);
        btnChat = (Button)findViewById(R.id.btnChat1);
        btnChat.setOnClickListener(this);
        btnRadio = (RadioButton)findViewById(R.id.btnRadio1);

    }

    /**
     * Switches the focus of the app to the messaging screen
     */
    public void switchToMessageLayout(){
        setContentView(R.layout.message_layout);

        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        lvTextMessages = (ListView) findViewById(R.id.lvTextMessages);
        btnSend= (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        msgBox = (EditText)findViewById(R.id.msgBox);
        //lvTextMessages.setAdapter(lvTextMsgAdapter);
        msgBox.setOnClickListener(this);

        messageAdapter = new MessageListAdapter(MainActivity.this, R.layout.messages_adapter,newMessages);
        lvTextMessages.setAdapter(messageAdapter);

    }

    /**
     * Intializes all the objects needed for two player and also checks to see if enabling bluetooth is necessary
     * Switches the focus of the app to the connection screen
     */
    public void switchToJoinHostLayout(){

        enableBT();

        service = new BluetoothService(MainActivity.this);  //instantiates the bluetoothService object in order to connect to device and perform operations

        discoveredDevices = new ArrayList<>();

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver3, new IntentFilter("incomingMessage"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver4, new IntentFilter("deviceConnected"));

        sv = (ScrollView)findViewById(R.id.scrollView);

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

        pbar2 = (ProgressBar) findViewById(R.id.pBar2);
        pbar2.setVisibility(View.INVISIBLE);
    }

    /**
     * switches the focus of the app to the screen where you pick how many games/chat/go to start
     */
    public void switchToStartGameScreenLayout(){

        setContentView(R.layout.start_game_screen_layout);

        btnSingleGame = (Button)findViewById(R.id.btnSingleGame);
        btnSingleGame.setOnClickListener(this);

        btnThreeGames = (Button)findViewById(R.id.btnThreeGames);
        btnThreeGames.setOnClickListener(this);

        btnFiveGames = (Button)findViewById(R.id.btnFiveGames);
        btnFiveGames.setOnClickListener(this);

        btnChat = (Button)findViewById(R.id.btnChat2);
        btnChat.setOnClickListener(this);

        btnRadio = (RadioButton)findViewById(R.id.btnRadio2);
        btnRadio.setChecked(false);

        if(twoPlayer) {
            btnRadio.setVisibility(View.VISIBLE);
            btnChat.setVisibility(View.VISIBLE);
        }
        else {
            btnRadio.setVisibility(View.INVISIBLE);
            btnChat.setVisibility(View.INVISIBLE);
        }

        btnBackToStart = (Button)findViewById(R.id.btnBackToStart);
        btnBackToStart.setOnClickListener(this);


        showMessage("Connected",1);
        dismissMessage();


    }

    /**
     * Switches to the start screen that you would see when the app load
     * Initializes all the usable things on the layout
     */
    public void switchToStartScreenLayout(){

        setContentView(R.layout.start_screen_layout);

        btnSinglePlayer = (Button) findViewById(R.id.btnSinglePlayer);
        btnSinglePlayer.setOnClickListener(this);

        btnTwoPlayer = (Button) findViewById(R.id.btnTwoPlayer);
        btnTwoPlayer.setOnClickListener(this);

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
                if(device.getName()!=null && !discoveredDevices.contains(device))
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
                        if(deviceConnected != DeviceConnection.DEVICE_CONNECTED)
                            showMessage("You're device is not visible to the host, please try again.", 1);
                            if(pbar2 != null)
                                pbar2.setVisibility(View.INVISIBLE);
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG,"receiver2: Discoverability off, no able to recieve");
                        if(deviceConnected != DeviceConnection.DEVICE_CONNECTED)
                            showMessage("You're device is not visible to the host, please try again.", 1);
                        if(pbar2 != null)
                            pbar2.setVisibility(View.INVISIBLE);
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
            String checkStr = Character.toString(text.charAt(0));
            text = text.substring(1);

            if(checkStr.equals(CHAT_STR)) {
                text = "Opponent: " + text;  //this is just added temp. for the chat fucntion, later implementation will be in a different location
                newMessages.add(text);
                sendNotification(text);
                try {
                    messageAdapter.notifyDataSetChanged();
                    lvTextMessages.smoothScrollToPosition(messageAdapter.getCount() - 1);
                }catch(NullPointerException e){

                }
                if(findViewById(R.id.btnRadio2)!= null || findViewById(R.id.btnRadio1) != null){
                    btnRadio.setChecked(true);
                }

            }
            else if(checkStr.equals(GAME_STR)){
                newGameMessage = true;
                gameMessage = text;
            }
            else if(checkStr.equals(MICELLANEOUS_STR)){

            }
           //You can add more final Strings at the top to make more text options here just add an if
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

                if(isHost)
                    switchToStartGameScreenLayout();
                else
                    switchToWaitingLayout();



            }
            else if(deviceConnected == DeviceConnection.DEVICE_CONNECTION_FAILED) {
                Log.d(TAG, "DEVICE CONNECTION FAILED");
                showMessage("Connection Failed", 1);
            }
            else if(deviceConnected == DeviceConnection.DEVICE_CONNECTION_MESSAGE_FAILED)
                Log.d(TAG,"Intent Not Sent for device connection confirmation");
            else if(deviceConnected == DeviceConnection.DEVICE_ATTEMPTING_CONNECTION) {
                Log.d(TAG, "Device Connecting...");

            }
            else if(deviceConnected == DeviceConnection.DEVICE_DISCONNECTED){
                Log.d(TAG,"Other DEvice was Disconnected");
                closeSockets();
                dismissMessage();
                showMessage("Device was disconnected.",1);
                switchToStartScreenLayout();
            }


        }
    };


    /**
     * Creates an active AlertDialog box that will display a message to the user
     *
     * @param str  This is the message that will be displayed
     * @param check This is the check to determine which of the set layouts made below you want
     */
    public void showMessage(String str, int check){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(str);

        Log.d(TAG,"Showing AlertBox with this str: "+str);

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
        if(check == 3){
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    System.exit(0);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dismissMessage();
                }
            });
        }

        dialog = builder.create();
        dialog.show();

    }


    public void sendNotification(String gameMessage){

        Intent intent = new Intent(MainActivity.this, MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(ACTION_NOTIF);

        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.tictactoe_notif)
                .setContentTitle("New Message")
                .setContentText(gameMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        notificationManagerCompat = NotificationManagerCompat.from(this);

        if(!isAppActive()){
            notificationManagerCompat.notify(NOTIF_ID,builder.build());
        }

    }

    private void cancelNotification(){
        if(notificationManagerCompat != null){
            notificationManagerCompat.cancel(NOTIF_ID);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.cahnnel_name);
            String description = getString(R.string.cahnnel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public boolean isAppActive(){
        ActivityManager.getMyMemoryState(myProcess);
        return myProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
    }

    /**
     * Dismisses the active AlertDialogBox
     */
    public void dismissMessage(){
        Log.d(TAG,"log box");
        if(dialog != null) {
            Log.d(TAG,"Dismissing the Dialog box");
            dialog.dismiss();
            dialog = null;
        }

    }

    /**
     * This method makes it easy for any class to utilize the writing to the other device
     * @param msg   the message
     * @param checkStr  the identifier at the beginning of the message to tell what this message will be used for
     * @return
     */
    public boolean write(String msg, String checkStr){

        if(service != null){
            if(deviceConnected == DeviceConnection.DEVICE_CONNECTED){

                msg = checkStr + msg;
                Log.d(TAG,"Sending the Message: " + msg);
                byte[] b = msg.getBytes();
                service.write(b);
                return true;
            }
        }
        Log.d(TAG,"Sending message Failed");
        return false;

    }


    //More of Alex's stuff

    //Trying to make a method that switches to the game screen
    public void switchToGameScreenLayout(){

        setContentView(R.layout.start_screen_layout);

        XNum1 = findViewById(R.id.XNum1);
        XNum1.setOnClickListener(this);

        XNum2 = findViewById(R.id.XNum2);
        XNum2.setOnClickListener(this);

        XNum3 = findViewById(R.id.XNum3);
        XNum3.setOnClickListener(this);

        XNum4 = findViewById(R.id.XNum4);
        XNum4.setOnClickListener(this);

        XNum5 = findViewById(R.id.XNum5);
        XNum5.setOnClickListener(this);

        XNum6 = findViewById(R.id.XNum6);
        XNum6.setOnClickListener(this);

        XNum7 = findViewById(R.id.XNum7);
        XNum7.setOnClickListener(this);

        XNum8 = findViewById(R.id.XNum8);
        XNum8.setOnClickListener(this);

        XNum9 = findViewById(R.id.XNum9);
        XNum9.setOnClickListener(this);

        ONum1 = findViewById(R.id.ONum1);
        ONum1.setOnClickListener(this);

        ONum2 = findViewById(R.id.ONum2);
        ONum2.setOnClickListener(this);

        ONum3 = findViewById(R.id.ONum3);
        ONum3.setOnClickListener(this);

        ONum4 = findViewById(R.id.ONum4);
        ONum4.setOnClickListener(this);

        ONum5 = findViewById(R.id.ONum5);
        ONum5.setOnClickListener(this);

        ONum6 = findViewById(R.id.ONum6);
        ONum6.setOnClickListener(this);

        ONum7 = findViewById(R.id.ONum7);
        ONum7.setOnClickListener(this);

        ONum8 = findViewById(R.id.ONum8);
        ONum8.setOnClickListener(this);

        ONum9 = findViewById(R.id.ONum9);
        ONum9.setOnClickListener(this);

    }

}
