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
import com.sobremesa.birdwatching.database.SightingTable;

import java.util.HashMap;

/**
 * Created by omegatai on 2014-06-17.
 */
public class BAMContentProvider extends ContentProvider {

    private static final String TAG = BAMContentProvider.class.getSimpleName();

    public static final String SCHEME = "content";
    public static final String AUTHORITY = "com.sobremesa.app.providers.BAMContentProvider";

    public static final class Uris {

        public static final Uri SIGHTING_URI = Uri.parse(SCHEME + "://" + AUTHORITY + "/" + Paths.SIGHTINGS);

    }

    public static final class Paths {
        public static final String SIGHTINGS = "sightings";
    }

    private static final int SIGHTINGS_DIR = 0;
    private static final int SIGHTING_ID = 1;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, Paths.SIGHTINGS, SIGHTINGS_DIR);
        sURIMatcher.addURI(AUTHORITY, Paths.SIGHTINGS + "/#", SIGHTING_ID);
    }

    // database
    private BAMDatabaseHelper database;



    @Override
    public boolean onCreate() {
        database = new BAMDatabaseHelper(getContext());
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

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);

        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    // Not Used (using raw insertion)
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase dbConnection = database.getWritableDatabase();

        try {
            dbConnection.beginTransaction();

            switch (uriType) {
                case SIGHTINGS_DIR:
                case SIGHTING_ID:
                    final long categoryId = dbConnection.insertOrThrow(
                            SightingTable.TABLE_NAME, null, values);
                    final Uri newSighting = ContentUris.withAppendedId(
                            Uris.SIGHTING_URI, categoryId);
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

        final SQLiteDatabase dbConnection = database.getWritableDatabase();
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
        final SQLiteDatabase dbConnection = database.getWritableDatabase();
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