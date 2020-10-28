package com.poulomi.plateformeterritoire;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class OnlineUpdateActivity extends AppCompatActivity { //It means it is a activity i.e this class has a layout attached
//initialize the buttons the edit text
    Button update;
    EditText editText;
    TextView id;
    private RequestQueue requestQueue;
    String maxspeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_update);
        update= findViewById(R.id.update_button);
        editText = findViewById(R.id.input2);
        id=findViewById(R.id.osm_id_fetch);
        maxspeed = getIntent().getExtras().getString("maxspeed"); // getting the value of maxspeed from the previous activity
        final String osm_id = getIntent().getExtras().getString("osm_id");
        id.setText(osm_id);

        if (editText != null) {
            editText.setText(maxspeed);
        } else {
            //Log.d("I am in settext ", "555");
        }
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("------------------","======");
                String data =   "["+"{"+  //The JSON data format sent as POST
                        "\"osm_id\"" +":"+ "\"" + id.getText().toString()+ "\","+
                        "\"maxspeed\"" +":"+ "\"" + maxspeed+ "\","+
                        "\"requested_maxspeed\"" +":"+ "\"" + editText.getText().toString() + "\""+
                        "}"+"]";
                Log.d("///////////////",data);
                mySync(data);

            }
        });
    }
    private void mySync(String data) {   //POSTING the data
        final String savedata= data;
        String URL="http://10.42.3.241:5004/post_edge";  //10.42.3.241 for real device  //10.0.2.2 for emulator

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres=new JSONObject(response);
                    String msg = objres.get("msg").toString();
                    if (!(msg.equals("Updated Successfully!") || msg.equals("ReUpdated!"))) //In case of the conflict comes up
                    {
                        AlertDialog.Builder builder= new AlertDialog.Builder(OnlineUpdateActivity.this);
                        builder.setTitle("ALERT");
                        builder.setMessage(msg);
                        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(OnlineUpdateActivity.this,"YES click",Toast.LENGTH_LONG).show();
                                String data2="["+"{"+
                                        "\"yes/no\"" +":"+ "\"" +"yes" + "\""+
                                        "}"+"]";
                                secondPOST(data2);
                            }
                        });
                        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String data2="["+"{"+
                                        "\"yes/no\"" +":"+ "\"" +"no" + "\""+
                                        "}"+"]";
                                secondPOST(data2);
                            }
                        }) ;
                        AlertDialog dialog= builder.create();
                        dialog.show();


                    }

                    else{
                        Toast.makeText(getApplicationContext(), objres.toString(), Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();

                }
                //Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "ERROR:404"+error.getMessage(), Toast.LENGTH_SHORT).show();

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
                    return savedata == null ? null : savedata.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Log.d("--------", savedata);
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

    private void secondPOST(String data2) { // If user selects YES or NO
        final String savedata2= data2;
        String URL="http://10.42.3.241:5004/yesORno";  //10.42.3.241 for real device  //10.0.2.2 for emulator//192.168.1.51:5003

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres=new JSONObject(response);

                    Toast.makeText(getApplicationContext(), objres.toString(), Toast.LENGTH_LONG).show();




                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "ERROR:404"+error.getMessage(), Toast.LENGTH_SHORT).show();
// when the IP address is wrong
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
