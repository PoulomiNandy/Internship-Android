package com.poulomi.plateformeterritoire;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;


public class OfflineMapview extends AppCompatActivity {
//Initializing the DB
    SQLiteDatabase db;
    private DbHelper DbHelper;


    MapView map = null;
    public static final GeoPoint st_ETIENNE = new GeoPoint(45.4189, 4.4130);
    Context ctx;
    IMapController mapController;



    public static File OSMDROID_PATH = new File(Environment.getExternalStorageDirectory(),"osmdroid"); // giving access to use the folder

    public static File TILE_PATH_BASE = new File(OSMDROID_PATH, "tiles");
    public static long TILE_MAX_CACHE_SIZE_BYTES = 600L * 1024 * 1024;
    public static long TILE_TRIM_CACHE_SIZE_BYTES = 500L * 1024 * 1024;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_mapview);

        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setOsmdroidBasePath(OSMDROID_PATH);
        Configuration.getInstance().setOsmdroidTileCache(TILE_PATH_BASE);


        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }
        else {
            Log.e("OSM","Read External Storage allowed");

        }

        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        else {
            Log.e("OSM","Write External Storage allowed");

        }
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
        }
        else {
            Log.e("OSM","Coarse Location allowed");

        }
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 4);
        }
        else {
            Log.e("OSM","Fine Location allowed");

        }
        map = findViewById(R.id.map);

        File umaps = new File(OSMDROID_PATH,"SaintEtienne_2020-07-17_112958.zip"+"SaintEtienne_2020-07-17_112129.zip"+"SaintEtienne_2020-07-17_112704.zip"
                +"SaintEtienne_2020-07-17_111132.zip"); // The zip files for osmroid
        Log.e("OSM","4uMaps read status = " + umaps.canRead());

        map.setUseDataConnection(false); //optional, but a good way to prevent loading from the network and test zip loading.
        map.setTileSource(new XYTileSource("4uMaps", 5, 15, 256, ".png", new String[]{"http://tileserver.4umaps.eu/"}));
        mapController = map.getController();
        mapController.setZoom(15);
        mapController.setCenter(new GeoPoint(45.43310940042114,4.3804358));
        try {
            DbHelper dbHelper = new DbHelper(this.getApplicationContext());
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(" Select * from edge_geoms, edge_attributes " +
                    "Where edge_attributes.osm_id = edge_geoms.osm_id ", null);// query to fetch the details from local DB


            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {

                        String id_osm= ((cursor.getString(cursor.getColumnIndex("osm_id"))));
                        final String name= ((cursor.getString(cursor.getColumnIndex("name"))));
                        String highway= ((cursor.getString(cursor.getColumnIndex("highway"))));
                        String way= ((cursor.getString(cursor.getColumnIndex("way"))));
                        String maxspeed= ((cursor.getString(cursor.getColumnIndex("maxspeed"))));
                        double lat = Double.parseDouble((cursor.getString(cursor.getColumnIndex("latitude"))));
                        double lng = Double.parseDouble((cursor.getString(cursor.getColumnIndex("longitude"))));
                        GeoPoint p = new GeoPoint(lat, lng);
                        final Marker marker = new Marker(map);
                        marker.setTitle("Nameline:"+name);
                        marker.setSnippet("Highway:"+highway+"   "+ "Way:"+way );
                        marker.setSubDescription("MaxSpeed:"+maxspeed);

                        //marker.setSubDescription("MaxSpeed:"+maxspeed);
                        marker.setPosition(p);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        map.getOverlays().add(marker);
//                        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener(){
//
//                            @Override
//                            public boolean onMarkerClick(Marker marker, MapView mapView) {
//                                String speed= marker.getTitle();
//
//                                            Intent i= new Intent(MainActivity.this,DetailActivity.class);
//                                            i.putExtra("maxspeed",speed);
//
//                                            startActivity(i);
//
//                                return false;
//                            }
//                        });



                    } while (cursor.moveToNext());

                }
            }


        } catch (Exception e){
            e.printStackTrace();
        }
        Polyline line= new Polyline(); // For connecting with lines
        Cursor c= db.rawQuery(" Select * from edge_geoms",null);
        while (c.moveToNext()) {
            line.addPoint(new GeoPoint(Double.parseDouble(c.getString(1)), Double.parseDouble(c.getString(2))));
        }
        map.getOverlayManager().add(line);
    }
}
