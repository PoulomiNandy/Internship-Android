package com.poulomi.plateformeterritoire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OnlineServices extends AppCompatActivity {//It means it is a activity i.e this class has a layout attached

    private Button btn_1;
    private Button btn_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_services);
        btn_1= findViewById(R.id.service1);
        btn_2= findViewById(R.id.service2);

        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1= new Intent(OnlineServices.this, EdgeActivity.class);
                startActivity(intent1);
            }
        });
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2= new Intent(OnlineServices.this, MeasureDistance.class);
                startActivity(intent2);
            }
        });
    }
}
