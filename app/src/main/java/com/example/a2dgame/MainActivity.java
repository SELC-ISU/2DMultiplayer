package com.example.a2dgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private BlutoothActivity activity1 = new BlutoothActivity();
    TextView MainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MainText = (TextView)findViewById(R.id.txtMain);
        MainText.setText(Integer.toString(activity1.enableBT()));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

       // activity1.destroyConnection();

    }

}
