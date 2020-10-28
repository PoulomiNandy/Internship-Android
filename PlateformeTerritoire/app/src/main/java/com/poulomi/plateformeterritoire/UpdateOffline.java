package com.poulomi.plateformeterritoire;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class UpdateOffline extends AppCompatActivity {
    EditText way, highway, editText;
    Button update,view, send,del,dellall;
    TextView id_number;
    private RequestQueue requestQueue;

    String id, strT, old_value;//wayT, highwayT, name,

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_offline);
        view=findViewById(R.id.button_view_updates);
        editText = findViewById(R.id.maxspeed_input2);
        update = findViewById(R.id.update_button);
        del= findViewById(R.id.delete);
        dellall= findViewById(R.id.delete_all);
        send = findViewById(R.id.send);
        id_number = findViewById(R.id.osm_id_fetch);
        // First call the get and set function
        getAndSetIntentData();

        ActionBar ab = getSupportActionBar();
        ab.setTitle("MODIFY MAX SPEED OFFLINE");// the title bar

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper dbHelper = new DbHelper(UpdateOffline.this);
                strT = editText.getText().toString();
                id = id_number.getText().toString();

                Boolean checkinsertdata = dbHelper.insertuserdata(id, old_value, strT);
                if (checkinsertdata == true) {
                    Toast.makeText(getApplicationContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();

                }

                // After getting the previous value of max speed we will call update function to change the maxspeed value
//                Log.d("==================", id);
//                Log.d("====================", namelineT);
//                Log.d("==================", maxspeedT);
                long res = dbHelper.updateData(id, strT); // updating both the tables
                if (res >= 0) {
                    Toast.makeText(getApplicationContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();

                }

            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper dbHelper = new DbHelper(UpdateOffline.this);
                Cursor res = dbHelper.viewupdate();
                if (res.getCount() == 0) {
                    Toast.makeText(UpdateOffline.this, "No Update Exist", Toast.LENGTH_LONG).show();
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    buffer.append("Osm_id :" + res.getString(0) + "\n");
                    buffer.append("maxspeed :" + res.getString(1) + "\n");
                    buffer.append("requested_maxspeed :" + res.getString(2) + "\n\n");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateOffline.this); // for the pop up
                builder.setCancelable(true);
                builder.setTitle("User Updates");
                builder.setMessage(buffer.toString());
                builder.show();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O_MR1)
            @Override
            public void onClick(View v) {
                DbHelper dbHelper = new DbHelper(UpdateOffline.this);
                JSONArray res = dbHelper.getResults();

                userPOST(res.toString());// POST function on availability of internet


            }
        });
        dellall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper dbHelper= new DbHelper(UpdateOffline.this);
                dbHelper.deleteAllupdate();
                Toast.makeText(UpdateOffline.this, "All Records deleted!" , Toast.LENGTH_SHORT).show();
            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbHelper dbHelper = new DbHelper(UpdateOffline.this);
                String theId = id_number.getText().toString();
                Boolean checkudeletedata = dbHelper.deletedata(theId);
                if(checkudeletedata==true)
                    Toast.makeText(UpdateOffline.this, "Entry Deleted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(UpdateOffline.this, "Entry Not Deleted", Toast.LENGTH_SHORT).show();
            }        });
    }
    void getAndSetIntentData() { // getting data from the DB and setting the value
        if (getIntent().hasExtra("id") && getIntent().hasExtra("Way") && getIntent().hasExtra("Highway") &&
                getIntent().hasExtra("Name") && getIntent().hasExtra("Maxspeed")) {

            //Getting Data from Intent
            id = getIntent().getStringExtra("id");
//            wayT = getIntent().getStringExtra("Way");
//            highwayT = getIntent().getStringExtra("Highway");
//            name = getIntent().getStringExtra("Name");
            strT = getIntent().getStringExtra("Maxspeed");
            old_value = getIntent().getStringExtra("Maxspeed");

            //Setting String to Text
//            way.setText(wayT);
//            highway.setText(highwayT);
            editText.setText(strT);
            id_number.setText(id);
        } else {
            Toast.makeText(this, "No data", Toast.LENGTH_LONG).show();
        }
    }
    private void userPOST(String res) {
        final String savedata2 = res;
        String URL = "http://172.20.10.4:5001/post_edge_offline";  //10.42.3.241 for real device  //10.0.2.2 for emulator//192.168.1.51:5003

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres = new JSONObject(response);

                    Toast.makeText(getApplicationContext(), objres.toString(), Toast.LENGTH_LONG).show();


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();

                }
                //Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "ERROR:404" + error.getMessage(), Toast.LENGTH_SHORT).show();

                Log.v("VOLLEY-------------", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return savedata2 == null ? null : savedata2.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Log.d("--------", savedata2);
                    return null;
                }
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }
}
