package com.sobremesa.birdwatching.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Michael on 2014-03-10.
 */
public class BirdDescriptionTable {
    // Database table
    // Many Images to One Bird

    public static final String TABLE_NAME = "birdDescription";

    public static final String ID = "_id";
    public static final String DESCRIPTION = "description";
    public static final String SCI_NAME = "sciName";


    // Full names for disambiguation
    public static final String FULL_ID = TABLE_NAME + "." + ID;
    public static final String FULL_DESCRIPTION = TABLE_NAME + "." + DESCRIPTION;
    public static final String FULL_SCI_NAME = TABLE_NAME + "." + SCI_NAME;

    public static String[] ALL_COLUMNS = new String[]{ID, DESCRIPTION, SCI_NAME};
    public static String[] FULL_ALL_COLUMNS = new String[]{FULL_ID, FULL_DESCRIPTION, FULL_SCI_NAME};

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + ID + " integer primary key autoincrement, "
            + DESCRIPTION + " text not null, "
            + SCI_NAME + " integer not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(BirdDescriptionTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
