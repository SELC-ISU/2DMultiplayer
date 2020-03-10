package com.example.a2dgame;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

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

                //manageMyConnectedSocket(socket);

                Log.d(TAG,"READY TO GOOOO");

                MainActivity.setBluetoothService(socket);

                /*try {
                    serverSocket.close();
                }catch(IOException e){

                }*/

                break;
            }
        }
    }

    public void cancel(){
        try{
            serverSocket.close();
        }catch(IOException e){
            Log.e(TAG,"Could not close the connect socket");
        }
    }
}
