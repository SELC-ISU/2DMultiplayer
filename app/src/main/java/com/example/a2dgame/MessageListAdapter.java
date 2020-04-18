package com.example.a2dgame;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MessageListAdapter extends ArrayAdapter<String> {

    private LayoutInflater layoutInflater;
    private ArrayList<String> messages;
    private String ownPhone;
    private int viewResourceId;

    public MessageListAdapter(Context context, int resourceId, ArrayList<String> mMessages) {
        super(context, resourceId, mMessages);

        this.messages = mMessages;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewResourceId = resourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        convertView = layoutInflater.inflate(viewResourceId,null);

        String str = messages.get(position);

        if(str != null){
            TextView listContent = (TextView)convertView.findViewById(R.id.listContent);
            TextView listContent2 = (TextView) convertView.findViewById(R.id.listContent2);


            listContent.setText(str.substring(0,str.indexOf(":")));
            listContent2.setText(str.substring(str.indexOf(":")+1));

        }
        return convertView;
    }

}
