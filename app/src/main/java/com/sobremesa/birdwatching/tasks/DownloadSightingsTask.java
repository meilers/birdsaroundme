package com.sobremesa.birdwatching.tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.managers.SettingsManager;
import com.sobremesa.birdwatching.models.DateType;
import com.sobremesa.birdwatching.models.DistanceType;
import com.sobremesa.birdwatching.rest.DownloadSightingsClient;
import com.sobremesa.birdwatching.database.SightingTable;
import com.sobremesa.birdwatching.managers.EbirdApiClientManager;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;
import com.sobremesa.birdwatching.synchronizers.SightingSynchronizer;
import com.sobremesa.birdwatching.util.SyncUtil;

import java.util.ArrayList;
import java.util.Collections;

import retrofit.RetrofitError;

/**
 * Created by omegatai on 2014-06-17.
 */
public class DownloadSightingsTask extends AsyncTask<Double, Void, ArrayList<RemoteSighting>> {



    @Override
    protected ArrayList<RemoteSighting> doInBackground(Double... params) {

        Context context = BAMApplication.getContext();

        DownloadSightingsClient client = EbirdApiClientManager.INSTANCE.getClient(context, DownloadSightingsClient.class);

        try {
            int distance = 50;
            DistanceType distanceType = SettingsManager.INSTANCE.getSettings().getDistance();

            switch (distanceType)
            {
                case TWENTY_KM:
                    distance = 20;
                    break;

                case FIVE_KM:
                    distance = 5;
            }

            int date = 30;
            DateType dateType = SettingsManager.INSTANCE.getSettings().getDate();

            switch (dateType)
            {
                case SEVEN_DAYS:
                    Log.d("daaaaate", "7");
                    date = 7;
                    break;

                case ONE_DAY:
                    Log.d("daaaaate", "1");
                    date = 1;
            }

            ArrayList<RemoteSighting> sightings = client.downloadSightings(params[0], params[1], distance, date, "json");

            Log.d("daaaaaate size", sightings.size()+"");
            Log.d("daaaaaate size", sightings.size()+"");

            Log.d("daaaaaate size", sightings.size()+"");

            Cursor localSightingCursor = context.getContentResolver().query(BAMContentProvider.Uris.SIGHTINGS_URI, SightingTable.ALL_COLUMNS, null, null, null);
            localSightingCursor.moveToFirst();
            SyncUtil.synchronizeRemoteSightings(sightings, localSightingCursor,
                    localSightingCursor.getColumnIndex(SightingTable.SCI_NAME),
                    new SightingSynchronizer(context), null);
            localSightingCursor.close();

            return sightings;

        } catch (RetrofitError e) {

        }

        return null;
    }
}
