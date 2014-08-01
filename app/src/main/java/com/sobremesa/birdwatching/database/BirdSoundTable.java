package com.sobremesa.birdwatching.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Michael on 2014-03-10.
 */
public class BirdSoundTable {
    // Database table
    // Many Sounds to One Bird (comName)

    public static final String TABLE_NAME = "birdSound";

    public static final String ID = "_id";
    public static final String BIRD_SOUND_ID = "birdSoundId";
    public static final String COM_NAME = "comName";
    public static final String GEN = "gen";
    public static final String SP = "sp";
    public static final String SSP = "ssp";
    public static final String EN = "en";
    public static final String REC = "rec";
    public static final String CNT = "cnt";
    public static final String LOC = "loc";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String TYPE = "type";
    public static final String FILE = "file";
    public static final String LIC = "lic";
    public static final String URL = "url";

    // Full names for disambiguation
    public static final String FULL_ID = TABLE_NAME + "." + ID;
    public static final String FULL_BIRD_SOUND_ID = TABLE_NAME + "." + BIRD_SOUND_ID;
    public static final String FULL_COM_NAME = TABLE_NAME + "." + COM_NAME;
    public static final String FULL_GEN = TABLE_NAME + "." + GEN;
    public static final String FULL_SP = TABLE_NAME + "." + SP;
    public static final String FULL_SSP = TABLE_NAME + "." + SSP;
    public static final String FULL_EN = TABLE_NAME + "." + EN;
    public static final String FULL_REC = TABLE_NAME + "." + REC;
    public static final String FULL_CNT = TABLE_NAME + "." + CNT;
    public static final String FULL_LOC = TABLE_NAME + "." + LOC;
    public static final String FULL_LAT = TABLE_NAME + "." + LAT;
    public static final String FULL_LNG = TABLE_NAME + "." + LNG;
    public static final String FULL_TYPE = TABLE_NAME + "." + TYPE;
    public static final String FULL_FILE = TABLE_NAME + "." + FILE;
    public static final String FULL_LIC = TABLE_NAME + "." + LIC;
    public static final String FULL_URL = TABLE_NAME + "." + URL;

    public static String[] ALL_COLUMNS = new String[]{ID, BIRD_SOUND_ID, COM_NAME, GEN, SP, SSP, EN, REC, CNT, LOC, LAT, LNG, TYPE, FILE, LIC, URL};
    public static String[] FULL_ALL_COLUMNS = new String[]{FULL_ID, FULL_BIRD_SOUND_ID, FULL_COM_NAME, FULL_GEN, FULL_SP, FULL_SSP, FULL_EN, FULL_REC, FULL_CNT, FULL_LOC, FULL_LAT, FULL_LNG, FULL_TYPE, FULL_FILE, FULL_LIC, FULL_URL};

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + ID + " integer primary key autoincrement, "
            + BIRD_SOUND_ID + " text not null, "
            + COM_NAME + " text not null,"
            + GEN + " text, "
            + SP + " text, "
            + SSP + " text, "
            + EN + " text, "
            + REC + " text,"
            + CNT + " text, "
            + LOC + " text,"
            + LAT + " text, "
            + LNG + " text,"
            + TYPE + " text, "
            + FILE + " text,"
            + LIC + " text, "
            + URL + " text"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(BirdSoundTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
