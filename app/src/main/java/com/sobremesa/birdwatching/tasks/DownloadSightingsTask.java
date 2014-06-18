package com.sobremesa.birdwatching.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.clients.DownloadSightingsClient;
import com.sobremesa.birdwatching.managers.EbirdApiClientManager;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;

import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by omegatai on 2014-06-17.
 */
public class DownloadSightingsTask extends AsyncTask<Double, Void, Void> {
    @Override
    protected Void doInBackground(Double... params) {

        Context context = BAMApplication.getContext();

        DownloadSightingsClient client = EbirdApiClientManager.getInstance().getClient(context, DownloadSightingsClient.class);

        try {
            List<RemoteSighting> sightings = client.downloadSightings(params[0], params[1], 50, 30, "json");

            Log.d("worked,", "wor");





        } catch (RetrofitError e) {
            Log.d("worked,", "wor");

        }

        return null;
    }
}
