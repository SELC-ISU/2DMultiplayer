package com.example.a2dgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView MainText = (TextView)findViewById(R.id.txtMain);
    private BlutoothActivity activity1 = new BlutoothActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity1.enableBT();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        activity1.destroyConnection();

    }

}
