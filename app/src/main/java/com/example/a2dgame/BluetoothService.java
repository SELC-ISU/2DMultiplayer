package com.example.a2dgame;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService{

    private static final String TAG = "BLUETOOTH_SERVICE_TAG";
    private Handler handler;
    private ConnectedThread mmThread;
    Context mContext;
    BluetoothSocket mmSocket;
    private InputStream inStream;
    private OutputStream outStream;

    private interface MessageConsts{

        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

    }

    public BluetoothService(BluetoothSocket socket, Context context){
        mContext = context;
        mmThread = new ConnectedThread(socket);
        Log.d(TAG, "We are connected and this is the bluetooth service constructor");
    }

    public void startSocket(){

        mmThread.start();
        Log.d(TAG, "The thread of CONNECTEDTHREAD has been started");

    }
    public void write(byte[] bytes){
        mmThread.write(bytes);
    }

    public class ConnectThread extends Thread {

        private Handler handler;
        private final BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("64a067b8-8af4-4748-b14f-b207afc5c843");
        private final String TAG = "CONNECTING_THREAD";
        private final BluetoothSocket mmSocket;
        private byte[] buffer;
        private boolean check = false;


        public ConnectThread(BluetoothDevice device) {

            BluetoothSocket temp = null;
            mmDevice = device;

            try {

                temp = device.createRfcommSocketToServiceRecord(MY_UUID);

            } catch (IOException e) {
                Log.e(TAG, "Sockets create() method failed", e);
            }
            mmSocket = temp;
        }

        public void run() {

            if (!check) {
                try {
                    mmSocket.connect();
                    check = true;
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



            }

            if(check == true) {
                MainActivity s = new MainActivity();

                s.setBluetoothService(mmSocket);
                return;

            }

        }

    }
    public class AcceptThread extends Thread {

        private final BluetoothServerSocket serverSocket;
        private final UUID MY_UUID = UUID.fromString("64a067b8-8af4-4748-b14f-b207afc5c843");
        private final String NAME = "RFCOMM Listener";
        private final String TAG = "ListeningActivity";

        public AcceptThread(BluetoothAdapter btAdapter){

            BluetoothServerSocket tempServer = null;
            try{
                tempServer = btAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch(IOException e){
                Log.e(TAG, "Socket's listen() method failed",e);
            }
            serverSocket = tempServer;
        }

        public void run() {
            BluetoothSocket socket = null;

            while(true){
                try{
                    socket = serverSocket.accept();
                }catch(IOException e){
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if(socket!=null){


                    Log.d(TAG,"READY TO GOOOO");

                    mmSocket = socket;

                try {
                    serverSocket.close();
                }catch(IOException e){

                }

                    break;
                }
            }
        }

    }

    private class ConnectedThread extends Thread{

        private byte[] buffer;

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

            inStream = tempIn;
            outStream = tempOut;

        }

        public void run(){

            buffer = new byte[1024];
            int numBytes;

            while(true){
                try{

                    numBytes = inStream.read(buffer);
                    String incomingMessage = new String(buffer,0,numBytes);

                    Intent incomingMessageIntent = new Intent("incomingMessage");
                    incomingMessageIntent.putExtra("theMessage",incomingMessage);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);

                    Message readMsg = handler.obtainMessage(MessageConsts.MESSAGE_READ, numBytes, -1, buffer);
                    readMsg.sendToTarget();

                }catch(IOException e) {
                    Log.d(TAG, "Input stream disconnected",e);
                    break;
                }

                }
            }

            public void write(byte[] bytes){
                try{
                    outStream.write(bytes);

                    Message writtenMsg = handler.obtainMessage(MessageConsts.MESSAGE_WRITE, -1,-1, buffer);
                    writtenMsg.sendToTarget();
                }catch(IOException e){
                    Log.e(TAG,"Error sending data");

                    Message writeErrorMsg = handler.obtainMessage(MessageConsts.MESSAGE_TOAST);
                    Bundle bundle = new Bundle();
                    bundle.putString("toast","Couldnt send data to other device");
                    writeErrorMsg.setData(bundle);
                    handler.sendMessage(writeErrorMsg);

                }
            }


        }

    public void cancel(){
        try{
            mmSocket.close();
        }catch (IOException e){
            Log.e(TAG,"count not close the socket");
        }
    }


}
