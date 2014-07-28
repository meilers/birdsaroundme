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
import com.sobremesa.birdwatching.database.BirdDescriptionTable;
import com.sobremesa.birdwatching.models.remote.RemoteBirdDescription;
import com.sobremesa.birdwatching.providers.BAMContentProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omegatai on 2014-06-17.
 */
public class BirdDescriptionSynchronizer extends BaseSynchronizer<RemoteBirdDescription>{


    public BirdDescriptionSynchronizer(Context context) {
        super(context);
    }


    @Override
    protected void performSynchronizationOperations(Context context, List<RemoteBirdDescription> inserts, List<RemoteBirdDescription> updates, List<Long> deletions) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();


        if( inserts.size() > 0 )
        {
            ContentValues[] val = new ContentValues[inserts.size()];
            int i = 0;
            for (RemoteBirdDescription w : inserts) {

                val[i] = this.getContentValuesForRemoteEntity(w);
                ++i;
            }

            doBulkInsertOptimised(val);
        }


        for (RemoteBirdDescription w : updates) {
            ContentValues values = this.getContentValuesForRemoteEntity(w);

            if( values != null ) {
                ContentProviderOperation op = ContentProviderOperation.newUpdate(BAMContentProvider.Uris.BIRD_DESCRIPTIONS_URI).withSelection(BirdDescriptionTable.DESCRIPTION + " = ? AND " + BirdDescriptionTable.SCI_NAME + " = ?",
                        new String[]{w.getDescription(), w.getSciName()})
                        .withValues(values).build();
                operations.add(op);
            }


        }

        for (Long id : deletions) {
            ContentProviderOperation op = ContentProviderOperation.newDelete(BAMContentProvider.Uris.BIRD_DESCRIPTIONS_URI).withSelection(BirdDescriptionTable.ID + " = ?", new String[] { String.valueOf(id) }).build();
            operations.add(op);
        }

        try {
            if( inserts.size() > 0 || operations.size() > 0 )
            {
                context.getContentResolver().applyBatch(BAMContentProvider.AUTHORITY, operations);
                context.getContentResolver().notifyChange(BAMContentProvider.Uris.BIRD_DESCRIPTIONS_URI, null);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected boolean isRemoteEntityNewerThanLocal(RemoteBirdDescription remote, Cursor c) {
        return true;
    }

    @Override
    protected ContentValues getContentValuesForRemoteEntity(RemoteBirdDescription remoteSighting) {
        ContentValues values = new ContentValues();

        values.put(BirdDescriptionTable.DESCRIPTION, remoteSighting.getDescription());
        values.put(BirdDescriptionTable.SCI_NAME, remoteSighting.getSciName());

        return values;


    }




    private int doBulkInsertOptimised(ContentValues values[]) {

        Context context = BAMApplication.getContext();
        BAMDatabaseHelper helper = BAMDatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        DatabaseUtils.InsertHelper inserter = new DatabaseUtils.InsertHelper(db, BirdDescriptionTable.TABLE_NAME);


        db.beginTransaction();
        int numInserted = 0;
        try {
            int len = values.length;
            for (int i = 0; i < len; i++) {
                inserter.prepareForInsert();


                String description = (String)(values[i].get(BirdDescriptionTable.DESCRIPTION));
                inserter.bind(inserter.getColumnIndex(BirdDescriptionTable.DESCRIPTION), description);

                String sciName = (String)(values[i].get(BirdDescriptionTable.SCI_NAME));
                inserter.bind(inserter.getColumnIndex(BirdDescriptionTable.SCI_NAME), sciName);

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

