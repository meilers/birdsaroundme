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
import com.sobremesa.birdwatching.database.BirdSoundTable;
import com.sobremesa.birdwatching.models.remote.RemoteBirdSound;
import com.sobremesa.birdwatching.providers.BAMContentProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omegatai on 2014-06-17.
 */
public class BirdSoundSynchronizer extends BaseSynchronizer<RemoteBirdSound>{


    public BirdSoundSynchronizer(Context context) {
        super(context);
    }


    @Override
    protected void performSynchronizationOperations(Context context, List<RemoteBirdSound> inserts, List<RemoteBirdSound> updates, List<Long> deletions) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();


        if( inserts.size() > 0 )
        {
            ContentValues[] val = new ContentValues[inserts.size()];
            int i = 0;
            for (RemoteBirdSound w : inserts) {

                val[i] = this.getContentValuesForRemoteEntity(w);
                ++i;
            }

            doBulkInsertOptimised(val);
        }


        for (RemoteBirdSound w : updates) {
            ContentValues values = this.getContentValuesForRemoteEntity(w);

            if( values != null ) {
                ContentProviderOperation op = ContentProviderOperation.newUpdate(BAMContentProvider.Uris.BIRD_SOUNDS_URI).withSelection(BirdSoundTable.BIRD_SOUND_ID + " = ?",
                        new String[]{w.getIdentifier()})
                        .withValues(values).build();
                operations.add(op);
            }


        }

        for (Long id : deletions) {
            ContentProviderOperation op = ContentProviderOperation.newDelete(BAMContentProvider.Uris.BIRD_SOUNDS_URI).withSelection(BirdSoundTable.ID + " = ?", new String[] { String.valueOf(id) }).build();
            operations.add(op);
        }

        try {
            if( inserts.size() > 0 || operations.size() > 0 )
            {
                context.getContentResolver().applyBatch(BAMContentProvider.AUTHORITY, operations);
                context.getContentResolver().notifyChange(BAMContentProvider.Uris.BIRD_SOUNDS_URI, null);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected boolean isRemoteEntityNewerThanLocal(RemoteBirdSound remote, Cursor c) {
        return true;
    }

    @Override
    protected ContentValues getContentValuesForRemoteEntity(RemoteBirdSound remoteBirdSound) {
        ContentValues values = new ContentValues();

        values.put(BirdSoundTable.BIRD_SOUND_ID, remoteBirdSound.getBirdSoundId());
        values.put(BirdSoundTable.COM_NAME, remoteBirdSound.getComName());
        values.put(BirdSoundTable.GEN, remoteBirdSound.getGen());
        values.put(BirdSoundTable.SP, remoteBirdSound.getSp());
        values.put(BirdSoundTable.SSP, remoteBirdSound.getSsp());
        values.put(BirdSoundTable.EN, remoteBirdSound.getEn());
        values.put(BirdSoundTable.REC, remoteBirdSound.getRec());
        values.put(BirdSoundTable.CNT, remoteBirdSound.getCnt());
        values.put(BirdSoundTable.LOC, remoteBirdSound.getLoc());
        values.put(BirdSoundTable.LAT, remoteBirdSound.getLat());
        values.put(BirdSoundTable.LNG, remoteBirdSound.getLng());
        values.put(BirdSoundTable.TYPE, remoteBirdSound.getType());
        values.put(BirdSoundTable.FILE, remoteBirdSound.getFile());
        values.put(BirdSoundTable.LIC, remoteBirdSound.getLic());
        values.put(BirdSoundTable.URL, remoteBirdSound.getUrl());


        return values;


    }




    private int doBulkInsertOptimised(ContentValues values[]) {

        Context context = BAMApplication.getContext();
        BAMDatabaseHelper helper = BAMDatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        DatabaseUtils.InsertHelper inserter = new DatabaseUtils.InsertHelper(db, BirdSoundTable.TABLE_NAME);


        db.beginTransaction();
        int numInserted = 0;
        try {
            int len = values.length;
            for (int i = 0; i < len; i++) {
                inserter.prepareForInsert();


                String birdSoundId = (String)(values[i].get(BirdSoundTable.BIRD_SOUND_ID));
                inserter.bind(inserter.getColumnIndex(BirdSoundTable.BIRD_SOUND_ID), birdSoundId);

                String comName = (String)(values[i].get(BirdSoundTable.COM_NAME));
                inserter.bind(inserter.getColumnIndex(BirdSoundTable.COM_NAME), comName);

                String gen = (String)(values[i].get(BirdSoundTable.GEN));
                inserter.bind(inserter.getColumnIndex(BirdSoundTable.GEN), gen);

                String sp = (String)(values[i].get(BirdSoundTable.SP));
                inserter.bind(inserter.getColumnIndex(BirdSoundTable.SP), sp);

                String ssp = (String)(values[i].get(BirdSoundTable.SSP));
                inserter.bind(inserter.getColumnIndex(BirdSoundTable.SSP), ssp);

                String en = (String)(values[i].get(BirdSoundTable.EN));
                inserter.bind(inserter.getColumnIndex(BirdSoundTable.EN), en);

                String rec = (String)(values[i].get(BirdSoundTable.REC));
                inserter.bind(inserter.getColumnIndex(BirdSoundTable.REC), rec);

                String cnt = (String)(values[i].get(BirdSoundTable.CNT));
                inserter.bind(inserter.getColumnIndex(BirdSoundTable.CNT), cnt);

                String loc = (String)(values[i].get(BirdSoundTable.LOC));
                inserter.bind(inserter.getColumnIndex(BirdSoundTable.LOC), loc);

                String lat = (String)(values[i].get(BirdSoundTable.LAT));
                inserter.bind(inserter.getColumnIndex(BirdSoundTable.LAT), lat);

                String lng = (String)(values[i].get(BirdSoundTable.LNG));
                inserter.bind(inserter.getColumnIndex(BirdSoundTable.LNG), lng);

                String type = (String)(values[i].get(BirdSoundTable.TYPE));
                inserter.bind(inserter.getColumnIndex(BirdSoundTable.TYPE), type);

                String file = (String)(values[i].get(BirdSoundTable.FILE));
                inserter.bind(inserter.getColumnIndex(BirdSoundTable.FILE), file);

                String lic = (String)(values[i].get(BirdSoundTable.LIC));
                inserter.bind(inserter.getColumnIndex(BirdSoundTable.LIC), lic);

                String url = (String)(values[i].get(BirdSoundTable.URL));
                inserter.bind(inserter.getColumnIndex(BirdSoundTable.URL), url);

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

