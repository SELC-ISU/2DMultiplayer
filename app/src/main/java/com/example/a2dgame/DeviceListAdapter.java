package com.example.a2dgame;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater layoutInflater;
    private ArrayList<BluetoothDevice> devices;
    private String ownPhone;
    private int viewResourceId;

    public DeviceListAdapter(Context context, int resourceId, ArrayList<BluetoothDevice> mDevices) {
        super(context, resourceId, mDevices);

        this.devices = mDevices;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewResourceId = resourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        convertView = layoutInflater.inflate(viewResourceId,null);

        BluetoothDevice device = devices.get(position);

        if(device != null){
            TextView deviceName = (TextView)convertView.findViewById(R.id.txtDeviceName);
            TextView deviceAddress = (TextView) convertView.findViewById(R.id.txtDeviceAddress);

            if(deviceName != null){
                deviceName.setText(device.getName());
            }
            if(deviceAddress != null){
                deviceAddress.setText(device.getAddress());
            }
        }
        return convertView;
    }




}
