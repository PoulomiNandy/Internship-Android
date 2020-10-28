package com.poulomi.plateformeterritoire;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.poulomi.plateformeterritoire.DbContract.EdgeEntry.COLUMN_ID;
import static com.poulomi.plateformeterritoire.DbContract.EdgeEntry.TABLE_NAME;
import static com.poulomi.plateformeterritoire.DbContract.EdgeEntry.TABLE_NAME_2;
import static com.poulomi.plateformeterritoire.DbContract.EdgeEntry.TABLE_NAME_3;

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();

    private Resources mResources; // Initializing for the JSON resource
    private static final String DATABASE_NAME = "edge_baba.db";//name of Database
    private static final int DATABASE_VERSION = 1;
    Context context;
    SQLiteDatabase db;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mResources = context.getResources();

        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ATTRI_TABLE = " CREATE TABLE " + TABLE_NAME + " (" + //1st table
                DbContract.EdgeEntry.COLUMN_ID + " TEXT PRIMARY KEY, " +
                DbContract.EdgeEntry.COLUMN_WAY + " TEXT NOT NULL, " +
                DbContract.EdgeEntry.COLUMN_HIGHWAY + " TEXT NOT NULL, " +
                DbContract.EdgeEntry.COLUMN_NAME + " TEXT NOT NULL ," +
                DbContract.EdgeEntry.COLUMN_MAX_SPEED + " TEXT NOT NULL , " +
                DbContract.EdgeEntry.COLUMN_REQUESTED_MAX_SPEED + " TEXT " + " );";

        final String SQL_CREATE_GEOMS_TABLE = " CREATE TABLE " + DbContract.EdgeEntry.TABLE_NAME_2 + " ( " + //2nd Table
                DbContract.EdgeEntry.COLUMN_ID + " TEXT NOT NULL, " +
                DbContract.EdgeEntry.COLUMN_LATITUDE + " TEXT NOT NULL ," +
                DbContract.EdgeEntry.COLUMN_LONGITUDE + " TEXT NOT NULL " + " ); ";

        final String SQL_CREATE_USER_UPDATES = " CREATE TABLE " + DbContract.EdgeEntry.TABLE_NAME_3 + " ( " + //3rd table
                DbContract.EdgeEntry.COLUMN_ID + " TEXT NOT NULL, " +
                DbContract.EdgeEntry.COLUMN_MAX_SPEED + " TEXT NOT NULL ," +
                DbContract.EdgeEntry.COLUMN_REQUESTED_MAXSPEED + " TEXT NOT NULL " + " ); ";

        db.execSQL(SQL_CREATE_ATTRI_TABLE);
        db.execSQL(SQL_CREATE_GEOMS_TABLE);
        db.execSQL(SQL_CREATE_USER_UPDATES);

        try {
            readDataToDb(db);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME + TABLE_NAME_2 + TABLE_NAME_3);
        onCreate(db);
    }
    public Boolean insertuserdata(String id , String old_value , String new_value) // inserting user updates ex: maxspeeds
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put("osm_id", id);
        cv.put("maxspeed", old_value);
        cv.put("requested_maxspeed", new_value);
        long results= db.insert(TABLE_NAME_3 , null, cv);
        if (results ==-1){
            return false;
        }
        else{
            return true;
        }
    }
    public Boolean deletedata (String osm_id)   // delete clicked updated data
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery(" Select * from "+ TABLE_NAME_3+" where osm_id=?", new String[]{osm_id});
        if (cursor.getCount() > 0) {
            long result = DB.delete(TABLE_NAME_3, "osm_id=?", new String[]{osm_id});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }
    public void deleteAllupdate ()  // delete all updates of user from local db
    {
        Cursor cursor= viewupdate();
        long row_id = cursor.getColumnIndexOrThrow(COLUMN_ID);
        if (cursor.moveToFirst()){
            do {
                deletedata(cursor.getString((int) row_id));
            }
            while (cursor.moveToNext());
        }
        cursor.close();

    }
    public Cursor viewupdate ()  // veiw update in a pop from local db
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery(" Select * from "+ TABLE_NAME_3,null);
        return cursor;


    }
    private void readDataToDb(SQLiteDatabase db) throws IOException, JSONException {   //reading json data and parsing
        final String MNU_ID = "osm_id";
        final String MNU_WAY = "way";
        final String MNU_HIGHWAY = "highway";
        final String MNU_NAME = "name";
        final String MNU_MAX_SPEED = "maxspeed";
        final String MNU_LATITUDE = "latitude";
        final String MNU_LONGITUDE = "longitude";
        final String MNU_GEOJSON = "jsongeoms";


        try {
            String jsonDataString = readJsonDataFromFile();
            JSONArray edgeItemsJsonArray = new JSONArray(jsonDataString);
            ContentValues edgeValues = new ContentValues();
            ContentValues geomValues = new ContentValues();

            for (int i = 0; i < edgeItemsJsonArray.length(); ++i) {
                String id;
                String way;
                String highway;
                String name;
                String maxspeed;
                String latitude = null;
                String longitude = null;


                JSONObject edgeItemObject = edgeItemsJsonArray.getJSONObject(i);


                id = edgeItemObject.getString(MNU_ID);
                way = edgeItemObject.getString(MNU_WAY);
                highway = edgeItemObject.getString(MNU_HIGHWAY);
                name = edgeItemObject.getString(MNU_NAME);
                maxspeed = edgeItemObject.getString(MNU_MAX_SPEED);

                edgeValues.put(DbContract.EdgeEntry.COLUMN_ID, id);  // saving the data in corresponding column
                edgeValues.put(DbContract.EdgeEntry.COLUMN_WAY, way);
                edgeValues.put(DbContract.EdgeEntry.COLUMN_HIGHWAY, highway);
                edgeValues.put(DbContract.EdgeEntry.COLUMN_NAME, name);
                edgeValues.put(DbContract.EdgeEntry.COLUMN_MAX_SPEED, maxspeed);

                db.insert(TABLE_NAME, null, edgeValues);

                ArrayList<GeoPoint> items = new ArrayList<GeoPoint>();

                JSONArray jsonArray = edgeItemObject.getJSONArray("jsongeoms");
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    latitude = jsonObject.getString(MNU_LATITUDE);
                    longitude = jsonObject.getString(MNU_LONGITUDE);

                    geomValues.put(DbContract.EdgeEntry.COLUMN_ID, id);
                    geomValues.put(DbContract.EdgeEntry.COLUMN_LATITUDE, latitude);
                    geomValues.put(DbContract.EdgeEntry.COLUMN_LONGITUDE, longitude);
                    db.insert(DbContract.EdgeEntry.TABLE_NAME_2, null, geomValues);

                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    private String readJsonDataFromFile() throws IOException {  // Reading JSON data from file in raw folder

        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try {
            String jsonDataString = null;
            inputStream = mResources.openRawResource(R.raw.edge_internship);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));
            while ((jsonDataString = bufferedReader.readLine()) != null) {
                builder.append(jsonDataString);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return new String(builder);
    }


    long updateData(String row_id,String maxspeed){ // update the maxspeed in attribute table
        db = this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put("maxspeed",maxspeed);

        long result = db.update(TABLE_NAME, cv,"osm_id=?", new String []{row_id});
        Log.d("hhhhhh","ffffffffff");
        return result;
    }

    Cursor readAllData(){//read all the data from the table attributes
        String query = " SELECT * FROM " + TABLE_NAME;
        db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }


    public JSONArray getResults() { // fetching the updates and sending as array.

        SQLiteDatabase db = this.getReadableDatabase();


        String searchQuery = " SELECT  * FROM " + TABLE_NAME_3 ;
        Cursor cursor = db.rawQuery(searchQuery, null);

        JSONArray resultSet = new JSONArray();
        JSONObject returnObj = new JSONObject();

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {

                    try {

                        if (cursor.getString(i) != null) {
                            Log.d("----------", cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch (Exception e) {
                        Log.d("**************", e.getMessage());
                    }
                }

            }

            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        Log.d("TAG_NAME", resultSet.toString());
        return resultSet;
    }

}
