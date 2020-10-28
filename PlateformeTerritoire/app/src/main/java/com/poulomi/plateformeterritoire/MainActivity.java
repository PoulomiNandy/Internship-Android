package com.poulomi.plateformeterritoire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.osmdroid.util.Distance;

public class MainActivity extends AppCompatActivity {//It means it is a activity i.e this class has a layout attached

    private Button btn_online;

    private Button btn_offline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_online= findViewById(R.id.online);  //initializing the id of the button

        btn_offline= findViewById(R.id.offline);

        btn_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent6= new Intent(MainActivity.this, OnlineServices.class);
                startActivity(intent6);
            }
        });



        btn_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent6= new Intent(MainActivity.this, OfflineOsm.class);
                startActivity(intent6);
            }
        });


    }
}
