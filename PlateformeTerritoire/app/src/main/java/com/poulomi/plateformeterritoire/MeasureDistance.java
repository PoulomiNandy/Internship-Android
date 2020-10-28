package com.poulomi.plateformeterritoire;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class MeasureDistance extends AppCompatActivity {//It means it is a activity i.e this class has a layout attached
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map;
    private MapController myMapController;
    String lat1,long1,lat2,long2;
    private RequestQueue queue;
    Button btn1;

    ArrayList<OverlayItem> items= new ArrayList<OverlayItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_measure_distance);
        btn1 = findViewById(R.id.show_distance);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        myMapController= (MapController) map.getController();
        myMapController.setZoom(14);//setting camera zoom

        //Setting the camera focus on Saint-Etienne
        final GeoPoint point= new GeoPoint(45.4189, 4.4130);
        myMapController.setCenter(point);
        //Adding the required co ordinates and description in ArrayList type OverlayItem

        items.add(new OverlayItem("INSTITUT SUPERIEUR DES TECHNIQUES DE LA PERFORMANCE",
                "ADDRESS:Rue de Copernic, 42100 Saint-Étienne\n"+
                        "Siren:381294024",new GeoPoint(45.4189, 4.4130)));
        items.add(new OverlayItem("INSTITUT MINES TELECOM","ADDRESS:158 Cours Fauriel 42100 Saint-Étienne\n"+
                "Siren:180092025",new GeoPoint(45.4233,  4.4078)));
        items.add(new OverlayItem("SAINT-ETIENNE METROPOLE","ADDRESS:2 Avenue Gruner 42000 Saint-Étienne\n"+
                "Siren:244200770",new GeoPoint(45.4398, 4.3977)));
        items.add(new OverlayItem("COMMUNE DE SAINT ETIENNE","ADDRESS:Place de l'Hotel de Ville 42000 Saint-Étienne\n" +
                "Siren:214202186",new GeoPoint(45.4396, 4.3878)));

        //Fetching and ploting the overlay item in the map with the details by clicking the item.
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {

                        //Toast.makeText(getBaseContext(),p.getLatitude() + " - "+p.getLongitude(),Toast.LENGTH_LONG).show();
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, ctx);
        mOverlay.setFocusItemsOnTap(true);
        map.getOverlays().add(mOverlay);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText= btn1.getText().toString();
                if (buttonText.equals("Distance")){
                    Toast.makeText(getApplicationContext(), "Please click 2 points in the map \n Select 1st point by long press \n 2nd point by single tap", Toast.LENGTH_SHORT).show();
// The pop that user will get
                    final MapEventsReceiver mReceive = new MapEventsReceiver() {   //map event listener will grab the clicks
                        @Override
                        public boolean longPressHelper(GeoPoint p) {
                            Toast.makeText(getBaseContext(), "1st Point Selected " + p.getLatitude() + " - " + p.getLongitude(), Toast.LENGTH_LONG).show();
                            lat1 = Double.toString(((double) p.getLatitude()));
                            long1 = Double.toString(((double) p.getLongitude()));
                            return true;
                        }

                        @Override
                        public boolean singleTapConfirmedHelper(GeoPoint p) {
                            Toast.makeText(getBaseContext(), "2nd Point Selected " + p.getLatitude() + " - " + p.getLongitude(), Toast.LENGTH_LONG).show();
                            lat2 = Double.toString(((double) p.getLatitude()));
                            long2 = Double.toString(((double) p.getLongitude()));
                            return true;

                        }
                    };

                    map.getOverlays().add(new MapEventsOverlay(mReceive));
                    btn1.setText("Show Distance");
                } else{
                    //sending the clicked points to the URL by API call
                    String url="https://territoire.emse.fr/apps-library/osmhandler/distance?latitude1="+lat1+"&longitude1="+long1+"&latitude2="+lat2+"&longitude2="+long2;
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String jsonResult) {
                                    try {
                                        JSONObject distObject = new JSONObject(jsonResult);
                                        String distance = distObject.getString("distance");
                                        Toast.makeText(getApplicationContext(),"The distance="+distance+"meter",Toast.LENGTH_LONG).show();
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            });
                    queue.add(stringRequest);
                    btn1.setText("Distance");
                }
            }

        });

        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
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
