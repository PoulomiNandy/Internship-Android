package com.poulomi.plateformeterritoire;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private Context context;
    //private Activity activity;
    private ArrayList osm_id, name, maxspeed, highway, way;

    CustomAdapter(Context context, ArrayList osm_id, ArrayList way, ArrayList highway, ArrayList name, ArrayList maxspeed){

        this.context= context;
        this.osm_id=osm_id;
        this.way=way;
        this.highway=highway;
        this.name= name;
        this.maxspeed= maxspeed;

    }

    @NonNull
    @Override//holding the values in an array
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder,final int position) {
        holder.osm_id_txt.setText(String.valueOf("OSM_ID:"+osm_id.get(position)));
        holder.way_txt.setText(String.valueOf("WAY:"+way.get(position)));
        holder.highway_txt.setText(String.valueOf("HIGHWAY:"+highway.get(position)));
        holder.nameline_txt.setText(String.valueOf("NAME:"+name.get(position)));
        holder.maxspeed_txt.setText(String.valueOf("MAX-SPEED:"+maxspeed.get(position)));
        //Recyclerview onClickListener
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateOffline.class);
                intent.putExtra("id", String.valueOf(osm_id.get(position)));// fetching position and sending to UpdateOffline
                intent.putExtra("Way", String.valueOf(way.get(position)));
                intent.putExtra("Highway", String.valueOf(highway.get(position)));
                intent.putExtra("Name", String.valueOf(name.get(position)));
                intent.putExtra("Maxspeed", String.valueOf(maxspeed.get(position)));
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return osm_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView osm_id_txt,way_txt,highway_txt,nameline_txt,maxspeed_txt;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            osm_id_txt= itemView.findViewById(R.id.osm_id_txt);
            way_txt= itemView.findViewById(R.id.way_txt);
            highway_txt= itemView.findViewById(R.id.highway_txt);
            nameline_txt= itemView.findViewById(R.id.nameline_txt);
            maxspeed_txt= itemView.findViewById(R.id.maxspeed_txt);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

}
