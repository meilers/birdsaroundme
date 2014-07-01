package com.sobremesa.birdwatching.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Michael on 2014-03-10.
 */
public class BAMDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "birdsaroundme.db";
    private static final int DATABASE_VERSION = 1;

    public BAMDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        SightingTable.onCreate(database);
        BirdImageTable.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // BaseActivity.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        SightingTable.onUpgrade(database, oldVersion, newVersion);
        BirdImageTable.onUpgrade(database, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}

