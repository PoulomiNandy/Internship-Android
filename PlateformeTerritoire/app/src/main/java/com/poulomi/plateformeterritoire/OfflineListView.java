package com.poulomi.plateformeterritoire;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class OfflineListView extends AppCompatActivity {//It means it is a activity i.e this class has a layout attached

    DbHelper dbHelper;
    RecyclerView recyclerView; // For the listview
    ArrayList<String> osm_id, name, maxspeed, highway, way;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_list_view);
        recyclerView = findViewById(R.id.recyclerView);

        dbHelper = new DbHelper(OfflineListView.this);
        osm_id = new ArrayList<>();
        name = new ArrayList<>();
        maxspeed = new ArrayList<>();
        highway = new ArrayList<>();
        way = new ArrayList<>();
        storeDatainArrays();

        customAdapter = new CustomAdapter(OfflineListView.this, osm_id, way, highway, name, maxspeed);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(OfflineListView.this));
    }
    void storeDatainArrays(){ // fetching and storing data in an array.
        Cursor cursor= dbHelper.readAllData();
        if (cursor.getCount() == 0){
            Toast.makeText(this,"Empty",Toast.LENGTH_LONG).show();
        }else{
            while (cursor.moveToNext()){
                osm_id.add(cursor.getString(0));
                way.add(cursor.getString(1));
                highway.add(cursor.getString(2));
                name.add(cursor.getString(3));
                maxspeed.add(cursor.getString(4));
            }
        }

    }
}
