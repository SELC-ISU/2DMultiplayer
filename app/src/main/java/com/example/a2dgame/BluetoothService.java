package com.example.a2dgame;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothService{

    private static final String TAG = "BLUETOOTH_SERVICE_TAG";
    private Handler handler;
    private ConnectedThread mmThread;

    private interface MessageConsts{

        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

    }

    public BluetoothService(BluetoothSocket socket){
        mmThread = new ConnectedThread(socket);
        Log.d(TAG, "We are connected and this is the bluetooth service constructor");
    }

    public void startSocket(){

        mmThread.start();

    }
    public void write(byte[] bytes){
        mmThread.write(bytes);
    }


    private class ConnectedThread extends Thread{

        private final BluetoothSocket mmSocket;
        private final InputStream inStream;
        private final OutputStream outStream;
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

            public void cancel(){
                try{
                    mmSocket.close();
                }catch (IOException e){
                    Log.e(TAG,"count not close the socket");
                }
            }
        }



}
