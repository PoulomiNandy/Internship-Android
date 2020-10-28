package com.poulomi.plateformeterritoire;

import android.provider.BaseColumns;

public class DbContract {
    public static final class EdgeEntry implements BaseColumns {
        public static  final String TABLE_NAME= "edge_attributes";
        public static final String COLUMN_ID = "osm_id";
        public static final String COLUMN_WAY = "way";
        public static final String COLUMN_HIGHWAY = "highway";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_MAX_SPEED= "maxspeed";
        public static final String COLUMN_REQUESTED_MAX_SPEED = "";
        public static  final String TABLE_NAME_2= "edge_geoms";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE= "longitude";
        public static final String TABLE_NAME_3="user_updates";
        public static final String COLUMN_REQUESTED_MAXSPEED="requested_maxspeed";
    }
}
