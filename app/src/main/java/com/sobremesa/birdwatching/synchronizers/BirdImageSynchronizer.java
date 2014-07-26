package com.sobremesa.birdwatching.synchronizers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.RemoteException;

import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.database.BAMDatabaseHelper;
import com.sobremesa.birdwatching.database.BirdImageTable;
import com.sobremesa.birdwatching.database.SightingTable;
import com.sobremesa.birdwatching.models.remote.RemoteBirdImage;
import com.sobremesa.birdwatching.providers.BAMContentProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by omegatai on 2014-06-17.
 */
public class BirdImageSynchronizer extends BaseSynchronizer<RemoteBirdImage>{


    public BirdImageSynchronizer(Context context) {
        super(context);
    }


    @Override
    protected void performSynchronizationOperations(Context context, List<RemoteBirdImage> inserts, List<RemoteBirdImage> updates, List<Long> deletions) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();


        if( inserts.size() > 0 )
        {
            ContentValues[] val = new ContentValues[inserts.size()];
            int i = 0;
            for (RemoteBirdImage w : inserts) {

                val[i] = this.getContentValuesForRemoteEntity(w);
                ++i;
            }

            doBulkInsertOptimised(val);
        }


        for (RemoteBirdImage w : updates) {
            ContentValues values = this.getContentValuesForRemoteEntity(w);

            if( values != null ) {
                ContentProviderOperation op = ContentProviderOperation.newUpdate(BAMContentProvider.Uris.BIRD_IMAGES_URI).withSelection(BirdImageTable.IMAGE_URL + " = ? AND " + BirdImageTable.SCI_NAME + " = ?",
                        new String[]{w.getImageUrl(), w.getSciName()})
                        .withValues(values).build();
                operations.add(op);
            }


        }

        for (Long id : deletions) {
            ContentProviderOperation op = ContentProviderOperation.newDelete(BAMContentProvider.Uris.BIRD_IMAGES_URI).withSelection(BirdImageTable.ID + " = ?", new String[] { String.valueOf(id) }).build();
            operations.add(op);
        }

        try {
            if( inserts.size() > 0 || operations.size() > 0 )
            {
                context.getContentResolver().applyBatch(BAMContentProvider.AUTHORITY, operations);
                context.getContentResolver().notifyChange(BAMContentProvider.Uris.BIRD_IMAGES_URI, null);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected boolean isRemoteEntityNewerThanLocal(RemoteBirdImage remote, Cursor c) {
        return true;
    }

    @Override
    protected ContentValues getContentValuesForRemoteEntity(RemoteBirdImage remoteSighting) {
        ContentValues values = new ContentValues();

        values.put(BirdImageTable.IMAGE_URL, remoteSighting.getImageUrl());
        values.put(BirdImageTable.SCI_NAME, remoteSighting.getSciName());
        values.put(BirdImageTable.POSITION, remoteSighting.getPosition());

        return values;


    }




    private int doBulkInsertOptimised(ContentValues values[]) {

        Context context = BAMApplication.getContext();
        BAMDatabaseHelper helper = BAMDatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        DatabaseUtils.InsertHelper inserter = new DatabaseUtils.InsertHelper(db, BirdImageTable.TABLE_NAME);


        db.beginTransaction();
        int numInserted = 0;
        try {
            int len = values.length;
            for (int i = 0; i < len; i++) {
                inserter.prepareForInsert();


                String imageUrl = (String)(values[i].get(BirdImageTable.IMAGE_URL));
                inserter.bind(inserter.getColumnIndex(BirdImageTable.IMAGE_URL), imageUrl);

                String sciName = (String)(values[i].get(BirdImageTable.SCI_NAME));
                inserter.bind(inserter.getColumnIndex(BirdImageTable.SCI_NAME), sciName);

                int position = ((Number) values[i].get(BirdImageTable.POSITION)).intValue();
                inserter.bind(inserter.getColumnIndex(BirdImageTable.POSITION), position);

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

