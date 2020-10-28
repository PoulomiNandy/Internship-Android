package com.poulomi.plateformeterritoire;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    //Intializing the variables of all type
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map;
    private MapController myMapController;
    private RequestQueue queue;
    double longitude_first,latitude_first,longitude_last,latitude_last;
    GeoPoint mark1,mark2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        queue = Volley.newRequestQueue(this);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Button buttonParse = findViewById(R.id.button_parse);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        myMapController = (MapController) map.getController();
        myMapController.setZoom(17);//setting camera zoom

        //Setting the camera focus on Saint-Etienne
        GeoPoint point = new GeoPoint(45.4397, 4.3872);
        myMapController.setCenter(point);
        buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParse();
            }
        });
        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
    }

    //Parsing the JSON from the server and plotting in the OSM
    private void jsonParse() {

        String url= "https://territoire.emse.fr/apps-library/osmhandler/edge-internship?city=stetienne&min_longitude=4.3820&min_latitude=45.4327&max_longitude=4.4004&max_latitude=45.4436";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                final String osm_id= jsonObject.getString("osm_id");
                                String naming = jsonObject.getString("name");
                                String highway= jsonObject.getString("highway");
                                final String speed = jsonObject.getString("maxspeed");
                                String way = jsonObject.getString("way");
                                JSONArray jsonArray = jsonObject.getJSONArray("jsongeoms");
                                JSONObject jsongeom = jsonArray.getJSONObject(0);// Choose all the first object of array of coordinates
                                ArrayList<GeoPoint> items = new ArrayList<GeoPoint>();
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject jsongeoms = jsonArray.getJSONObject(j);
                                    double longitude = jsongeoms.getDouble("longitude");
                                    double latitude = jsongeoms.getDouble("latitude");
                                    GeoPoint sainte = new GeoPoint(latitude, longitude);// all the coordinates
                                    longitude_first = jsongeom.getDouble("longitude");
                                    latitude_first = jsongeom.getDouble("latitude");
                                    JSONObject geojson= jsonArray.getJSONObject(jsonArray.length()-1);   // Choose all the last object of array of coordiantes
                                    longitude_last = geojson.getDouble("longitude");
                                    latitude_last = geojson.getDouble("latitude");
                                    ArrayList<GeoPoint> first_last= new ArrayList<GeoPoint>();      // added all the first and the last points in an array
                                    mark1= new GeoPoint(latitude_first,longitude_first);   // this var will have the coordinates of first object
                                    mark2= new GeoPoint(latitude_last,longitude_last);// this var will have the coordiantes of last object
                                    first_last.add(mark1);                                          // adding the all 1st coordinates in array
                                    first_last.add(mark2);// adding all last co ordinates in array
                                    Marker marker1 = new Marker(map);                                 //created marker for all first co-ordiantes
                                    marker1.setPosition(mark1);// marking all the first co-ordinates
                                    marker1.setTitle("Name: "+naming);
                                    marker1.setSubDescription("MaxSpeed:"+speed);
                                    marker1.setSnippet("Highway: "+highway+" "+"Way:"+way);
                                    marker1.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                    map.getOverlays().add(marker1);

                                    final Marker marker2 = new Marker(map);                              // created marker for all the last co-ordinates
                                    marker2.setPosition(mark2);// marking all the last co-ordinates
                                    marker2.setTitle("Name: "+naming);
                                    marker2.setSubDescription("MaxSpeed:"+speed);
                                    marker2.setSnippet("Highway: "+highway+" "+"Way:"+way);
                                    marker2.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                    map.getOverlays().add(marker2);

                                    items.add(sainte);
                                    //Clustering the markers by jsongeoms array
                                    Polyline line= new Polyline();
                                    line.setPoints(items);
                                    line.setOnClickListener(new Polyline.OnClickListener(){
                                        @Override
                                        public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                                            Toast.makeText(map.getContext(), "osm_id"+ osm_id+"maxspeed" + speed , Toast.LENGTH_LONG).show();

                                           // The dialog box when clicked on the polylines
                                            AlertDialog.Builder builder= new AlertDialog.Builder(MapActivity.this);
                                            builder.setTitle("ALERT");
                                            builder.setMessage("osm_id"+":"+osm_id +'\n'+"maxspeed"+":"+speed+'\n'+"Do you want to update maxspeed?");
                                            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(MapActivity.this,"YES click",Toast.LENGTH_LONG).show();
                                                    Intent intent= new Intent(MapActivity.this, OnlineUpdateActivity.class);
                                                    intent.putExtra("maxspeed",speed);
                                                    intent.putExtra("osm_id",osm_id);
                                                    startActivity(intent);

                                                }
                                            });
                                            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            AlertDialog dialog= builder.create();
                                            dialog.show();

                                            return false;

                                        }


                                    });
                                    map.getOverlayManager().add(line);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request);

    }

    //This section is for permissiom to use internet and map.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

}
