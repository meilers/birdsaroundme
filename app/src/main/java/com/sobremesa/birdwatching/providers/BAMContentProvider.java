package com.sobremesa.birdwatching.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.sobremesa.birdwatching.database.BAMDatabaseHelper;
import com.sobremesa.birdwatching.database.BirdImageTable;
import com.sobremesa.birdwatching.database.SightingTable;

import java.util.HashMap;

/**
 * Created by omegatai on 2014-06-17.
 */
public class BAMContentProvider extends ContentProvider {

    private static final String TAG = BAMContentProvider.class.getSimpleName();

    public static final String SCHEME = "content";
    public static final String AUTHORITY = "com.sobremesa.birdwatching.providers.BAMContentProvider";

    public static final class Uris {

        public static final Uri SIGHTINGS_URI = Uri.parse(SCHEME + "://" + AUTHORITY + "/" + Paths.SIGHTINGS);
        public static final Uri SIGHTINGS_GROUP_BY_BIRD_URI = Uri.parse(SCHEME + "://" + AUTHORITY + "/" + Paths.SIGHTINGS + "/" + "BIRD");
        public static final Uri BIRD_IMAGES_URI = Uri.parse(SCHEME + "://" + AUTHORITY + "/" + Paths.BIRD_IMAGES);

    }

    public static final class Paths {
        public static final String SIGHTINGS = "sightings";
        public static final String BIRD_IMAGES = "birdImages";
    }


    private static final int SIGHTINGS_DIR = 0;
    private static final int SIGHTING_ID = 1;
    private static final int SIGHTINGS_GROUP_BY_BIRD_DIR = 2;
    private static final int BIRD_IMAGES_DIR = 3;
    private static final int BIRD_IMAGE_ID = 4;


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static HashMap<String, String> sSightingsProjectionMap;
    private static HashMap<String, String> sBirdImagesProjectionMap;

    // mDatabase
    private BAMDatabaseHelper mDatabase;


    static {
        sURIMatcher.addURI(AUTHORITY, Paths.SIGHTINGS, SIGHTINGS_DIR);
        sURIMatcher.addURI(AUTHORITY, Paths.SIGHTINGS + "/BIRD", SIGHTINGS_GROUP_BY_BIRD_DIR);
        sURIMatcher.addURI(AUTHORITY, Paths.SIGHTINGS + "/#", SIGHTING_ID);
        sURIMatcher.addURI(AUTHORITY, Paths.BIRD_IMAGES, BIRD_IMAGES_DIR);
        sURIMatcher.addURI(AUTHORITY, Paths.BIRD_IMAGES + "/#", BIRD_IMAGE_ID);

        // projections
        sSightingsProjectionMap = new HashMap<String, String>();
        sSightingsProjectionMap.put(SightingTable.ID, SightingTable.FULL_ID);
        sSightingsProjectionMap.put(SightingTable.COM_NAME, SightingTable.FULL_COM_NAME);
        sSightingsProjectionMap.put(SightingTable.SCI_NAME, SightingTable.FULL_SCI_NAME);
        sSightingsProjectionMap.put(SightingTable.HOW_MANY, SightingTable.FULL_HOW_MANY);
        sSightingsProjectionMap.put(SightingTable.LAT, SightingTable.FULL_LAT);
        sSightingsProjectionMap.put(SightingTable.LNG, SightingTable.FULL_LNG);
        sSightingsProjectionMap.put(SightingTable.LOC_ID, SightingTable.FULL_LOC_ID);
        sSightingsProjectionMap.put(SightingTable.LOC_NAME, SightingTable.FULL_LOC_NAME);
        sSightingsProjectionMap.put(SightingTable.LOCATION_PRIVATE, SightingTable.FULL_LOCATION_PRIVATE);
        sSightingsProjectionMap.put(SightingTable.OBS_DT, SightingTable.FULL_OBS_DT);
        sSightingsProjectionMap.put(SightingTable.OBS_REVIEWED, SightingTable.FULL_OBS_REVIEWED);
        sSightingsProjectionMap.put(SightingTable.OBS_VALID, SightingTable.FULL_OBS_VALID);

        sBirdImagesProjectionMap = new HashMap<String, String>();
        sBirdImagesProjectionMap.put(BirdImageTable.ID, BirdImageTable.FULL_ID);
        sBirdImagesProjectionMap.put(BirdImageTable.IMAGE_URL, BirdImageTable.FULL_IMAGE_URL);
        sBirdImagesProjectionMap.put(BirdImageTable.SCI_NAME, BirdImageTable.FULL_SCI_NAME);
    }





    @Override
    public boolean onCreate() {
        mDatabase = new BAMDatabaseHelper(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {

        switch (sURIMatcher.match(uri)) {
            case SIGHTING_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + Paths.SIGHTINGS;
            case SIGHTINGS_DIR:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + Paths.SIGHTINGS;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        String groupBy = null;


        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case SIGHTING_ID:
                queryBuilder.appendWhere(SightingTable.ID + "="
                        + uri.getLastPathSegment());
            case SIGHTINGS_DIR:
                queryBuilder.setTables(SightingTable.TABLE_NAME);
                break;

            case SIGHTINGS_GROUP_BY_BIRD_DIR:
                groupBy = SightingTable.SCI_NAME;
                queryBuilder.setTables(SightingTable.TABLE_NAME);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDatabase.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, groupBy, null, sortOrder);

        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    // Not Used (using raw insertion)
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase dbConnection = mDatabase.getWritableDatabase();

        try {
            dbConnection.beginTransaction();

            switch (uriType) {
                case SIGHTINGS_DIR:
                case SIGHTING_ID:
                    final long categoryId = dbConnection.insertOrThrow(
                            SightingTable.TABLE_NAME, null, values);
                    final Uri newSighting = ContentUris.withAppendedId(
                            Uris.SIGHTINGS_URI, categoryId);
                    getContext().getContentResolver().notifyChange(newSighting,
                            null);
                    dbConnection.setTransactionSuccessful();
                    return newSighting;


                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        } catch (Exception e) {
            Log.e(TAG, "Insert Exception", e);
        } finally {
            dbConnection.endTransaction();
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);

        final SQLiteDatabase dbConnection = mDatabase.getWritableDatabase();
        int deleteCount = 0;

        try {
            dbConnection.beginTransaction();

            switch (uriType) {
                case SIGHTINGS_DIR :
                    deleteCount = dbConnection.delete(SightingTable.TABLE_NAME,
                            selection, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case SIGHTING_ID :
                    deleteCount = dbConnection.delete(SightingTable.TABLE_NAME,
                            SightingTable.ID + "=?", new String[]{uri
                                    .getPathSegments().get(1)});
                    dbConnection.setTransactionSuccessful();
                    break;
                default :
                    throw new IllegalArgumentException("Unsupported URI:" + uri);
            }
        } finally {
            dbConnection.endTransaction();
        }

        if (deleteCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        final SQLiteDatabase dbConnection = mDatabase.getWritableDatabase();
        int updateCount = 0;

        try {
            dbConnection.beginTransaction();

            switch (uriType) {

                case SIGHTINGS_DIR :
                    updateCount = dbConnection.update(SightingTable.TABLE_NAME,
                            values, selection, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;
                case SIGHTING_ID :
                    final Long categoryId = ContentUris.parseId(uri);
                    updateCount = dbConnection.update(
                            SightingTable.TABLE_NAME,
                            values,
                            SightingTable.ID
                                    + "="
                                    + categoryId
                                    + (TextUtils.isEmpty(selection)
                                    ? ""
                                    : " AND (" + selection + ")"),
                            selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    break;


                default :
                    throw new IllegalArgumentException("Unsupported URI:" + uri);
            }
        } finally {
            dbConnection.endTransaction();
        }

        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updateCount;
    }


}