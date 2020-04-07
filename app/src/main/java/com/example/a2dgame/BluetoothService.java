package com.example.a2dgame;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService{

    private static final String TAG = "BLUETOOTH_SERVICE_TAG";
    private final UUID MY_UUID = UUID.fromString("64a067b8-8af4-4748-b14f-b207afc5c843");  //Specific UUID for this app
    private final Intent deviceConnectedIntent = new Intent("deviceConnected");   //Intent to send message of connection status
    private ConnectedThread mmConnectedThread;
    private AcceptThread mmAccept;
    private ConnectThread mmConnect;
    private Context mContext;
    private BluetoothSocket mmSocket;
    private InputStream inStream;
    private OutputStream outStream;

    private boolean runnable = true;

    /**
     * The message consts will be used for easy identification of what kind of messages have been sent through
     * from the other device in order to do the right action
     */
    private interface MessageConsts{

        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

    }

    /**
     * Sets the context in order to know which class to send messages to
     * Accepts teh context of the classs calling this connstructor
     * @param context
     */
    public BluetoothService(Context context){
        mContext = context;
        Log.d(TAG,"Constructor Called");
    }

    /**
     * Starts the client to look for a connection with UUID
     * Accepts parameter of the bluetooth lvTextMsgAdapter of the device that will be the client
     * AKA the device running this program
     * @param btAdapt
     */
    public void startSearchingClient(BluetoothAdapter btAdapt){
        runnable = true;
        mmAccept = new AcceptThread(btAdapt);
        mmAccept.start();
        Log.d(TAG,"Starting Searching Client");
    }

    /**
     *
     * Starts the server to be open for connection with the same UUID
     * Accepts parameter of a bloothooth device to try to connect to
     * @param device
     */
    public void startSearchServer(BluetoothDevice device){
        runnable = true;
        mmConnect = new ConnectThread(device);
        mmConnect.start();
        Log.d(TAG, "Starting Search Server");
    }

    /**
     *
     * Starts reading from the socket once it is connected and allows other operations
     *
     */
    public void startReadSocket(){

        runnable = true;
        mmConnectedThread.start();
        Log.d(TAG, "The thread of CONNECTEDTHREAD has been started");

    }

    /**
     * Writes bytes, accepted by the parameter, to the other connected device
     * calls on the connected thread to do this
     * @param bytes
     */
    public void write(byte[] bytes){
        mmConnectedThread.write(bytes);
    }

    /**
     * This class is the handler of connected the host device to another device searching for
     * its UUID. This takes in a device to specifically search for in its constructor
     */
    public class ConnectThread extends Thread {

        private final BluetoothDevice mmDevice;  //device to attempt connection
        private final String TAG = "CONNECTING_THREAD";
        private boolean check = false;  //check to see if connection succesful
        private DeviceConnection checkEnum = DeviceConnection.DEVICE_NOT_CONNECTED;  //num to send through declaring connection status

        /**
         * Takes in a device to attempt a connection with
         * @param device
         */
        public ConnectThread(BluetoothDevice device) {

            BluetoothSocket temp = null;
            mmDevice = device;

            checkEnum = DeviceConnection.DEVICE_ATTEMPTING_CONNECTION;
            deviceConnectedIntent.putExtra("checkValue",checkEnum.value);  //sends out status of the connection to Main Activity
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(deviceConnectedIntent);

            try {

                temp = device.createRfcommSocketToServiceRecord(MY_UUID); //starts sending out the UUID to see if its a match on the accepting side

            } catch (IOException e) {
                Log.e(TAG, "Sockets create() method failed", e);
                checkEnum = DeviceConnection.DEVICE_CONNECTION_FAILED;
                deviceConnectedIntent.putExtra("checkValue",checkEnum.value);  //sends out status of the connection to Main Activity
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(deviceConnectedIntent);
            }
            mmSocket = temp;
        }

        /**
         * Creates connection
         * is a thread so the call of connect does not block program exection
         */
        public void run() {
            while (runnable) {
                if (!check) {
                    try {
                        mmSocket.connect();  //waits here for a connection from the accepting side
                        check = true;  //gets to these two lines if connected
                        checkEnum = DeviceConnection.DEVICE_CONNECTED;
                    } catch (IOException connectException) {
                        Log.d(TAG, "ERROR calling connect: closing socket");
                        try {
                            mmSocket.close();
                        } catch (IOException closeException) {
                            Log.e(TAG, "ERROR connection and then coundlt close");
                        }
                        return;
                    }

                    InputStream tempIn = null;
                    OutputStream tempOut = null;

                    try {
                        tempIn = mmSocket.getInputStream();
                    } catch (IOException e) {
                        Log.e(TAG, "Error from creating input stream");
                    }
                    try {
                        tempOut = mmSocket.getOutputStream();
                    } catch (IOException e) {
                        Log.e(TAG, "Error from creating output stream");
                    }

                    inStream = tempIn;
                    outStream = tempOut;

                    deviceConnectedIntent.putExtra("checkValue", checkEnum.value);  //sends out status of the connection to Main Activity
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(deviceConnectedIntent);

                }

                if (check) {
                    Log.d(TAG, "We are connected");
                    mmConnectedThread = new ConnectedThread(mmSocket);  //creates connected thread with the socket that was just succesfully connected
                    startReadSocket(); //starts reading from this socket
                    return;
                }

            }
            return;
        }

    }

    /**
     * Looks for and Accepts connection from the server side
     */
    public class AcceptThread extends Thread {

        private final BluetoothServerSocket serverSocket;
        private final String NAME = "RFCOMM Listener";
        private final String TAG = "ListeningActivity";
        DeviceConnection checkEnum = DeviceConnection.DEVICE_NOT_CONNECTED;

        /**
         * Starts listening for the UUID of this app that would be sent by server
         * Takes in the BT lvTextMsgAdapter of this device
         * @param btAdapter
         */
        public AcceptThread(BluetoothAdapter btAdapter){

            checkEnum = DeviceConnection.DEVICE_SEARCHING;
            deviceConnectedIntent.putExtra("checkValue",checkEnum.value);  //sends out status of the connection to Main Activity
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(deviceConnectedIntent);

            BluetoothServerSocket tempServer = null;
            try{
                tempServer = btAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch(IOException e){
                Log.e(TAG, "Socket's listen() method failed",e);
            }
            serverSocket = tempServer;
        }

        /**
         * Attempts a connection from the server
         * This is a thread because socket.accept() is a blocking call
         */
        public void run() {
            BluetoothSocket socket = null;

            while(true && runnable){
                try{

                    socket = serverSocket.accept();  //blocking call, wont stop until fail or succeed

                    checkEnum = DeviceConnection.DEVICE_ATTEMPTING_CONNECTION;
                    deviceConnectedIntent.putExtra("checkValue",checkEnum.value);  //sends out status of the connection to Main Activity
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(deviceConnectedIntent);

                }catch(IOException e){
                    Log.e(TAG, "Socket's accept() method failed", e);
                    checkEnum = DeviceConnection.DEVICE_CONNECTION_FAILED;
                    deviceConnectedIntent.putExtra("checkValue",checkEnum.value);  //sends out status of the connection to Main Activity
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(deviceConnectedIntent);
                    break;
                }

                if(socket!=null){


                    Log.d(TAG,"READY TO GOOOO");  //We are connected

                    checkEnum = DeviceConnection.DEVICE_CONNECTED;

                    mmSocket = socket;

                try {
                    serverSocket.close();
                }catch(IOException e){

                }

                    break;
                }
            }

            mmConnectedThread = new ConnectedThread(mmSocket);
            startReadSocket();  //creates connectedThread and starts it to reading
            deviceConnectedIntent.putExtra("checkValue",checkEnum.value);  //sends connection status to the MainActivity
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(deviceConnectedIntent);

            return;

        }

    }

    /**
     *
     * This is the class that manages the connection once made on either the client or server side
     *
     */
    private class ConnectedThread extends Thread{

        private byte[] buffer;  //buffer holds the incoming messages
        private final String TAG = "ConnectedThread";
        DeviceConnection checkEnum = DeviceConnection.DEVICE_NOT_CONNECTED;

        /**
         * Takes in the connected socket to read and write from/to
         * @param socket
         */
        public ConnectedThread(BluetoothSocket socket){

            mmSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try{
                tempIn = socket.getInputStream();
            }catch(IOException e){
                Log.e(TAG,"Error from creating input stream");
            }
            try{
                tempOut = socket.getOutputStream();
            }catch(IOException e){
                Log.e(TAG,"Error from creating output stream");
            }

            inStream = tempIn;  //sets the streams to write and read
            outStream = tempOut;

        }

        /**
         * This method continuously reads the stream from the connected device
         * is a thread so this can continue to read no matter what else is running
         */
        public void run(){

            buffer = new byte[1024];
            int numBytes;

            while(true && runnable){
                try{

                    numBytes = inStream.read(buffer);  //reads from the device
                    String incomingMessage = new String(buffer,0,numBytes);  //transfers it into a string
                    Intent incomingMessageIntent = new Intent("incomingMessage");
                    incomingMessageIntent.putExtra("theMessage",incomingMessage);  //sends the message to MainActivity
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);



                }catch(IOException e) {
                    Log.d(TAG, "Input stream disconnected USER",e);
                    checkEnum = DeviceConnection.DEVICE_DISCONNECTED;
                    deviceConnectedIntent.putExtra("checkValue",checkEnum.value);  //sends out status of the connection to Main Activity
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(deviceConnectedIntent);
                    break;
                }

                }
            return;
            }

        /**
         * Writes to the other device an array of bytes taken is a parameter
         * @param bytes
         */
        public void write(byte[] bytes){
                try{
                    outStream.write(bytes);

                }catch(IOException e){
                    Log.e(TAG,"Error sending data");


                }
            }


        }

    /**
     * closes the socket and cancels the connection to the other device
     * should be done when app closes and when the user wants to connect to different device
     */
    public void cancel(){
        cancelThreads();
        try{
            mmSocket.close();
        }catch (IOException e){
            Log.e(TAG,"count not close the socket");
        }
    }

    public void cancelThreads(){
        runnable = false;
    }


}
