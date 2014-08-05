package com.sobremesa.birdwatching.services;

import android.app.IntentService;
import android.content.Intent;

import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.BAMConstants;
import com.sobremesa.birdwatching.database.BirdImageTable;
import com.sobremesa.birdwatching.database.SightingTable;
import com.sobremesa.birdwatching.models.remote.RemoteBirdImage;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;
import com.sobremesa.birdwatching.synchronizers.BirdImageSynchronizer;
import com.sobremesa.birdwatching.synchronizers.SightingSynchronizer;
import com.sobremesa.birdwatching.util.AnalyticsUtil;
import com.sobremesa.birdwatching.util.SyncUtil;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.util.Patterns;
import android.webkit.URLUtil;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


/**
 * Created by omegatai on 2014-07-02.
 */
public class BirdImageService extends IntentService {

    public final static class Extras
    {
        public static final String BIRDS = "birds";
    }


    public BirdImageService() {
        super("BirdImageService");
    }

    public BirdImageService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Context context = BAMApplication.getContext();

        if (intent.getAction().equals(Intent.ACTION_SYNC))
        {
            ArrayList<RemoteSighting> birds = intent.getParcelableArrayListExtra(Extras.BIRDS);
            ArrayList<RemoteSighting> birdsComputed = new ArrayList<RemoteSighting>();
            ArrayList<RemoteBirdImage> birdImages;

            String url = "";
            String imageUrl = "";
            int itr = 0;
            int i = 0;

            for( RemoteSighting bird : birds ) {
                birdImages = new ArrayList<RemoteBirdImage>();
                String title = bird.getComName();

                /* Wiki title url conventions: this might change */
                String newTitle = title.replace(" ", "_");
                newTitle = newTitle.toLowerCase();
                newTitle = Character.toUpperCase(newTitle.charAt(0)) + newTitle.substring(1);
                /* */

                // Sanitize url
                newTitle = Normalizer.normalize(newTitle, Normalizer.Form.NFKD);
                newTitle = newTitle.replace("&", "%26");

                url = "http://en.wikipedia.org/w/api.php?format=json&action=query&titles=" + newTitle + "&generator=images&gimlimit=100&prop=imageinfo&iiprop=url";

                if(Patterns.WEB_URL.matcher(url).matches())
                {
                    String imageJson = getJsonResult(url);

                    Pattern pattern1 = Pattern.compile("\"url\":\"[^,]*?(jpg|jpeg|png)\"", Pattern.CASE_INSENSITIVE);
                    Matcher matcher1 = pattern1.matcher(imageJson);

                    i = 0;

                    while (matcher1.find()) {
                        imageUrl = matcher1.group();
                        imageUrl = imageUrl.replace("\"url\":\"", "");
                        imageUrl = imageUrl.replace("\"", "");

                        RemoteBirdImage birdImage = new RemoteBirdImage();
                        birdImage.setImageUrl(imageUrl);
                        birdImage.setSciName(bird.getSciName());
                        birdImage.setPosition(i);
                        birdImages.add(birdImage);

                        ++i;
                    }

                    // If no images, send info to analytics
                    if( i == 0 )
                        AnalyticsUtil.sendEvent("", AnalyticsUtil.Categories.BIRD_IMAGES, AnalyticsUtil.Actions.SYNC, "Error: No Image for com name: " + bird.getComName() );

                    // Save
                    Cursor localBirdImageCursor = context.getContentResolver().query(BAMContentProvider.Uris.BIRD_IMAGES_URI, BirdImageTable.ALL_COLUMNS, BirdImageTable.SCI_NAME + " = ?",
                            new String[]{bird.getSciName()}, null);
                    localBirdImageCursor.moveToFirst();
                    SyncUtil.synchronizeRemoteBirdImages(birdImages, localBirdImageCursor,
                            localBirdImageCursor.getColumnIndex(BirdImageTable.IMAGE_URL),
                            new BirdImageSynchronizer(context), null);
                    localBirdImageCursor.close();

                    bird.setImages(birdImages);
                    birdsComputed.add(bird);


                    ++itr;

                    if( itr % 9 == 8 || itr == birds.size() )
                    {
                        Intent broadcastIntent = new Intent();
                        Bundle extras = new Bundle();
                        extras.putParcelableArrayList(BAMConstants.RELOAD_BIRD_IMAGES_BROADCAST_EXTRA, birdsComputed);

                        broadcastIntent.setAction(BAMConstants.RELOAD_BIRD_IMAGES_BROADCAST_ACTION);
                        broadcastIntent.putExtras(extras);

                        sendBroadcast(broadcastIntent);

                        birdsComputed.clear();
                    }

                }
                else
                {
                    AnalyticsUtil.sendEvent("", AnalyticsUtil.Categories.BIRD_IMAGES, AnalyticsUtil.Actions.SYNC, "Error: No Image for com name: " + bird.getComName() );
                }

            }

            stopSelf();


        }
    }


    private String getJsonResult(String url) {
        InputStream is = null;
        String json = "";

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpPost = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        return json;
    }
}
