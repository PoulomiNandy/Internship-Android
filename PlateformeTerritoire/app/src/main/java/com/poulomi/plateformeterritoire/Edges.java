package com.poulomi.plateformeterritoire;

public class Edges {   // It is a class for the adapter which will get and set the attributes.
    private String nameline;
    private String way;
    private String highway;
    private String maxspeed;
    private String osm_id;

    public  Edges(){}
    public Edges(String osm_id,String nameline,String way,String highway,String maxspeed){
        this.osm_id = osm_id;
        this.nameline = nameline;
        this.way = way;
        this.highway = highway;
        this.maxspeed = maxspeed;
    }

    public String getOsm_id() {
        return osm_id;
    }

    public void setOsm_id(String osm_id) {
        this.osm_id = osm_id;
    }

    public String getNameline() {
        return nameline;
    }

    public void setNameline(String nameline) {
        this.nameline = nameline;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public String getHighway() {
        return highway;
    }

    public void setHighway(String highway) {
        this.highway = highway;
    }

    public String getMaxspeed() {
        return maxspeed;
    }

    public void setMaxspeed(String maxspeed) {
        this.maxspeed = maxspeed;
    }
}

