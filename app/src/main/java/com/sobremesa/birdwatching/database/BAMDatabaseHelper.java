package com.sobremesa.birdwatching.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Michael on 2014-03-10.
 */
public class BAMDatabaseHelper extends SQLiteOpenHelper {

    private static BAMDatabaseHelper mInstance = null;

    public static final String DATABASE_NAME = "birdsaroundme.db";
    private static final int DATABASE_VERSION = 3;

    public static BAMDatabaseHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new BAMDatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }


    private BAMDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        SightingTable.onCreate(database);
        BirdImageTable.onCreate(database);
        BirdDescriptionTable.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // BaseActivity.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        SightingTable.onUpgrade(database, oldVersion, newVersion);
        BirdImageTable.onUpgrade(database, oldVersion, newVersion);
        BirdDescriptionTable.onUpgrade(database, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}

