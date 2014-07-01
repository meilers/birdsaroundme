package com.sobremesa.birdwatching.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Michael on 2014-03-10.
 */
public class SightingTable {
    // Database table
    public static final String TABLE_NAME = "sighting";

    public static final String ID = "_id";
    public static final String COM_NAME = "comName";
    public static final String SCI_NAME = "sciName";
    public static final String HOW_MANY = "howMany";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String LOC_ID = "locID";
    public static final String LOC_NAME = "locName";
    public static final String LOCATION_PRIVATE = "locationPrivate";
    public static final String OBS_DT = "obsDt";
    public static final String OBS_REVIEWED = "obsReviewed";
    public static final String OBS_VALID = "obsValid";


    // Full names for disambiguation
    public static final String FULL_ID = TABLE_NAME + "." + ID;
    public static final String FULL_COM_NAME = TABLE_NAME + "." + COM_NAME;
    public static final String FULL_SCI_NAME = TABLE_NAME + "." + SCI_NAME;
    public static final String FULL_HOW_MANY = TABLE_NAME + "." + HOW_MANY;
    public static final String FULL_LAT = TABLE_NAME + "." + LAT;
    public static final String FULL_LNG = TABLE_NAME + "." + LNG;
    public static final String FULL_LOC_ID = TABLE_NAME + "." + LOC_ID;
    public static final String FULL_LOC_NAME = TABLE_NAME + "." + LOC_NAME;
    public static final String FULL_LOCATION_PRIVATE = TABLE_NAME + "." + LOCATION_PRIVATE;
    public static final String FULL_OBS_DT = TABLE_NAME + "." + OBS_DT;
    public static final String FULL_OBS_REVIEWED = TABLE_NAME + "." + OBS_REVIEWED;
    public static final String FULL_OBS_VALID = TABLE_NAME + "." + OBS_VALID;

    public static String[] ALL_COLUMNS = new String[]{ID, COM_NAME, SCI_NAME, HOW_MANY, LAT, LNG, LOC_ID, LOC_NAME, LOCATION_PRIVATE, OBS_DT, OBS_REVIEWED, OBS_VALID};
    public static String[] FULL_ALL_COLUMNS = new String[]{FULL_ID, FULL_COM_NAME, FULL_SCI_NAME, FULL_HOW_MANY, FULL_LAT, FULL_LNG, FULL_LOC_ID, FULL_LOC_NAME, FULL_LOCATION_PRIVATE, FULL_OBS_DT, FULL_OBS_REVIEWED, FULL_OBS_VALID};

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + ID + " integer primary key autoincrement, "
            + COM_NAME + " text not null, "
            + SCI_NAME + " text not null,"
            + HOW_MANY + " integer not null default 1,"
            + LAT + " real not null,"
            + LNG + " real not null,"
            + LOC_ID + " text not null,"
            + LOC_NAME + " text not null,"
            + LOCATION_PRIVATE + " integer not null,"
            + OBS_DT + " datetime not null default CURRENT_TIMESTAMP,"
            + OBS_REVIEWED + " integer not null,"
            + OBS_VALID + " integer not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(SightingTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
