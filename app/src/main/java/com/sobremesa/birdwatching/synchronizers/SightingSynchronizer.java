package com.sobremesa.birdwatching.synchronizers;

import android.content.Context;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.RemoteException;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.database.BAMDatabaseHelper;
import com.sobremesa.birdwatching.database.SightingTable;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;

import java.util.List;
import java.util.Locale;

/**
 * Created by omegatai on 2014-06-17.
 */
public class SightingSynchronizer extends BaseSynchronizer<RemoteSighting>{


    public SightingSynchronizer(Context context) {
        super(context);
    }


    @Override
    protected void performSynchronizationOperations(Context context, List<RemoteSighting> inserts, List<RemoteSighting> updates, List<Long> deletions) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date;


        if( inserts.size() > 0 )
        {
            ContentValues[] val = new ContentValues[inserts.size()];
            int i = 0;
            for (RemoteSighting w : inserts) {

                val[i] = this.getContentValuesForRemoteEntity(w);
                ++i;
            }

            doBulkInsertOptimised(val);
        }


        for (RemoteSighting w : updates) {
            try {
                date = dateFormat.parse(w.getObsDt());

                ContentValues values = this.getContentValuesForRemoteEntity(w);

                if( values != null ) {
                    ContentProviderOperation op = ContentProviderOperation.newUpdate(BAMContentProvider.Uris.SIGHTINGS_URI).withSelection(SightingTable.SCI_NAME + " = ? AND " + SightingTable.LOC_ID + " = ? AND " + SightingTable.OBS_DT +  " = ?",
                            new String[]{w.getSciName(), w.getLocID(), dateFormat.format(date)})
                            .withValues(values).build();
                    operations.add(op);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        for (Long id : deletions) {
            ContentProviderOperation op = ContentProviderOperation.newDelete(BAMContentProvider.Uris.SIGHTINGS_URI).withSelection(SightingTable.ID + " = ?", new String[] { String.valueOf(id) }).build();
            operations.add(op);
        }

        try {
            if( inserts.size() > 0 || operations.size() > 0 )
            {
                context.getContentResolver().applyBatch(BAMContentProvider.AUTHORITY, operations);
                context.getContentResolver().notifyChange(BAMContentProvider.Uris.SIGHTINGS_URI, null);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected boolean isRemoteEntityNewerThanLocal(RemoteSighting remote, Cursor c) {
        return true;
    }

    @Override
    protected ContentValues getContentValuesForRemoteEntity(RemoteSighting remoteSighting) {
        ContentValues values = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(remoteSighting.getObsDt());

            values.put(SightingTable.COM_NAME, remoteSighting.getComName());
            values.put(SightingTable.SCI_NAME, remoteSighting.getSciName());
            values.put(SightingTable.HOW_MANY, remoteSighting.getHowMany());
            values.put(SightingTable.LAT, remoteSighting.getLat());
            values.put(SightingTable.LNG, remoteSighting.getLng());
            values.put(SightingTable.LOC_ID, remoteSighting.getLocID());
            values.put(SightingTable.LOC_NAME, remoteSighting.getLocName());
            values.put(SightingTable.LOCATION_PRIVATE, remoteSighting.getLocationPrivate() ? 1:0);
            values.put(SightingTable.OBS_DT, dateFormat.format(date));
            values.put(SightingTable.OBS_REVIEWED, remoteSighting.getObsReviewed() ? 1:0);
            values.put(SightingTable.OBS_VALID, remoteSighting.getObsValid() ? 1:0);

            return values;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;


    }




    private int doBulkInsertOptimised(ContentValues values[]) {

        Context context = BAMApplication.getContext();
        BAMDatabaseHelper helper = BAMDatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        DatabaseUtils.InsertHelper inserter = new DatabaseUtils.InsertHelper(db, SightingTable.TABLE_NAME);


        db.beginTransaction();
        int numInserted = 0;
        try {
            int len = values.length;
            for (int i = 0; i < len; i++) {
                inserter.prepareForInsert();


                String comName = (String)(values[i].get(SightingTable.COM_NAME));
                inserter.bind(inserter.getColumnIndex(SightingTable.COM_NAME), comName);

                String sciName = (String)(values[i].get(SightingTable.SCI_NAME));
                inserter.bind(inserter.getColumnIndex(SightingTable.SCI_NAME), sciName);

                int howMany = ((Number) values[i].get(SightingTable.HOW_MANY)).intValue();
                inserter.bind(inserter.getColumnIndex(SightingTable.HOW_MANY), howMany);

                double lat = ((Number) values[i].get(SightingTable.LAT)).doubleValue();
                inserter.bind(inserter.getColumnIndex(SightingTable.LAT), lat);

                double lng = ((Number) values[i].get(SightingTable.LNG)).doubleValue();
                inserter.bind(inserter.getColumnIndex(SightingTable.LNG), lng);

                String locId = (String)(values[i].get(SightingTable.LOC_ID));
                inserter.bind(inserter.getColumnIndex(SightingTable.LOC_ID), locId);

                String locName = (String)(values[i].get(SightingTable.LOC_NAME));
                inserter.bind(inserter.getColumnIndex(SightingTable.LOC_NAME), locName);

                int locPrivate = ((Number) values[i].get(SightingTable.LOCATION_PRIVATE)).intValue();
                inserter.bind(inserter.getColumnIndex(SightingTable.LOCATION_PRIVATE), locPrivate);

                String obsDt = (String)(values[i].get(SightingTable.OBS_DT));
                inserter.bind(inserter.getColumnIndex(SightingTable.OBS_DT), obsDt);

                int obsReviewed = ((Number) values[i].get(SightingTable.OBS_REVIEWED)).intValue();
                inserter.bind(inserter.getColumnIndex(SightingTable.OBS_REVIEWED), obsReviewed);

                int obsValid = ((Number) values[i].get(SightingTable.OBS_VALID)).intValue();
                inserter.bind(inserter.getColumnIndex(SightingTable.OBS_VALID), obsValid);

                inserter.execute();
            }
            numInserted = len;
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            inserter.close();
        }
        return numInserted;
    }

}

