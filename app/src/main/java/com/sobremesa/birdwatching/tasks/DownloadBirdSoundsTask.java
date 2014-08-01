package com.sobremesa.birdwatching.tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.database.BirdSoundTable;
import com.sobremesa.birdwatching.managers.EbirdApiClientManager;
import com.sobremesa.birdwatching.managers.XenoCantoApiClientManager;
import com.sobremesa.birdwatching.models.remote.RemoteBirdRecordings;
import com.sobremesa.birdwatching.models.remote.RemoteBirdSound;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;
import com.sobremesa.birdwatching.rest.DownloadBirdSoundsClient;
import com.sobremesa.birdwatching.rest.DownloadSightingsClient;
import com.sobremesa.birdwatching.synchronizers.BirdSoundSynchronizer;
import com.sobremesa.birdwatching.synchronizers.SightingSynchronizer;
import com.sobremesa.birdwatching.util.SyncUtil;

import java.util.ArrayList;
import java.util.Collections;

import retrofit.RetrofitError;

/**
 * Created by omegatai on 2014-06-17.
 */
public class DownloadBirdSoundsTask extends AsyncTask<String, Void, ArrayList<RemoteBirdSound>> {



    @Override
    protected ArrayList<RemoteBirdSound> doInBackground(String... params) {

        Context context = BAMApplication.getContext();

        DownloadBirdSoundsClient client = XenoCantoApiClientManager.INSTANCE.getClient(context, DownloadBirdSoundsClient.class);

        try {
            String comName = params[0];

            RemoteBirdRecordings recordings = client.downloadBirdSounds(comName.toLowerCase());
            ArrayList<RemoteBirdSound> birdSounds = recordings.getRecordings();

            for( RemoteBirdSound sound : birdSounds )
            {
                sound.setComName(comName);
            }

            Cursor localBirdSoundCursor = context.getContentResolver().query(BAMContentProvider.Uris.BIRD_SOUNDS_URI, BirdSoundTable.ALL_COLUMNS, BirdSoundTable.COM_NAME + "=?", new String[] {comName}, null);
            localBirdSoundCursor.moveToFirst();
            SyncUtil.synchronizeRemoteBirdSounds(birdSounds, localBirdSoundCursor,
                    localBirdSoundCursor.getColumnIndex(BirdSoundTable.BIRD_SOUND_ID), new BirdSoundSynchronizer(context), null);
            localBirdSoundCursor.close();

            return birdSounds;

        } catch (RetrofitError e) {

        }
        catch (Exception e) {}

        return null;
    }
}
