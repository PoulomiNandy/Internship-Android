package com.poulomi.plateformeterritoire;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>  implements Filterable {

    //Intializing the array and inflater and also the click listener
    LayoutInflater inflater;
    List<Edges> allEdges;
    private List<Edges> exampleListFull;
    private RecyclerViewClickListener listener;

    public Adapter(Context ctx, List<Edges> allEdges, RecyclerViewClickListener listener){//adding everything in the adapter.
        this.inflater = LayoutInflater.from(ctx);
        this.allEdges= allEdges;
        this.listener= listener;
        exampleListFull= new ArrayList<>(allEdges);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // This holder holdes the description like name,maxspeed,osm_id,etc
        View view = inflater.inflate(R.layout.lview,parent,false); //l.view layout is made for the design of list text
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //bind data
        holder.nameT.setText(allEdges.get(position).getNameline());  //need the position when clicked
        holder.highwayT.setText(allEdges.get(position).getHighway());
        holder.wayT.setText(allEdges.get(position).getWay());
        holder.maxspeedT.setText(allEdges.get(position).getMaxspeed());
        holder.osm_idT.setText(allEdges.get(position).getOsm_id());


    }

    @Override
    public int getItemCount() {
        return allEdges.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) { // This is for searching by name
            List<Edges>filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Edges item : exampleListFull) {
                    if (item.getNameline().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) { // Filtering by name; required for search
            allEdges.clear();
            allEdges.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public interface RecyclerViewClickListener{
        void onClick(View v , int position);

    }

    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {
        TextView osm_idT,nameT,wayT,highwayT,maxspeedT;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nameT= itemView.findViewById(R.id.nameline_txt);
            osm_idT= itemView.findViewById(R.id.osm_id_txt);
            wayT= itemView.findViewById(R.id.way_txt);
            highwayT= itemView.findViewById(R.id.highway_txt);
            maxspeedT= itemView.findViewById(R.id.maxspeed_txt);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v , getAdapterPosition());

        }
    }
}
