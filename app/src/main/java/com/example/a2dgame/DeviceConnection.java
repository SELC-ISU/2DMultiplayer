package com.example.a2dgame;

public enum DeviceConnection {


    DEVICE_ATTEMPTING_CONNECTION(2), DEVICE_CONNECTED(1), DEVICE_CONNECTION_FAILED(0), DEVICE_CONNECTION_MESSAGE_FAILED(-1),
    DEVICE_NOT_CONNECTED(3), DEVICE_SEARCHING(4), DEVICE_DISCONNECTED(5);


    public final int value;

    private DeviceConnection(int value){

        this.value = value;

    }

    public static DeviceConnection enumOf(int val){

        for(DeviceConnection item:values()){
            if(item.value == val){
                return item;
            }
        }

        return null;

    }

}
