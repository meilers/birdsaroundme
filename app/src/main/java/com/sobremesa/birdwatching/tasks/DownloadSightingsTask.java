package com.sobremesa.birdwatching.tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.managers.SettingsManager;
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

            Log.d("distance", distance + "");

            switch (distanceType)
            {
                case TWENTY_KM:
                    distance = 20;
                    break;

                case FIVE_KM:
                    distance = 5;
            }

            ArrayList<RemoteSighting> sightings = client.downloadSightings(params[0], params[1], distance, 30, "json");

            Cursor localSightingCursor = context.getContentResolver().query(BAMContentProvider.Uris.SIGHTINGS_URI, SightingTable.ALL_COLUMNS, null, null, null);
            localSightingCursor.moveToFirst();
            SyncUtil.synchronizeRemoteSightings(sightings, localSightingCursor,
                    localSightingCursor.getColumnIndex(SightingTable.SCI_NAME), localSightingCursor.getColumnIndex(SightingTable.LOC_ID), localSightingCursor.getColumnIndex(SightingTable.OBS_DT),
                    new SightingSynchronizer(context), null);
            localSightingCursor.close();

            return sightings;

        } catch (RetrofitError e) {

        }

        return null;
    }
}
