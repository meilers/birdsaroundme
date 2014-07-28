package com.sobremesa.birdwatching.tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Patterns;

import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.database.BirdDescriptionTable;
import com.sobremesa.birdwatching.database.SightingTable;
import com.sobremesa.birdwatching.managers.EbirdApiClientManager;
import com.sobremesa.birdwatching.models.remote.RemoteBirdDescription;
import com.sobremesa.birdwatching.models.remote.RemoteBirdImage;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;
import com.sobremesa.birdwatching.rest.DownloadSightingsClient;
import com.sobremesa.birdwatching.synchronizers.BirdDescriptionSynchronizer;
import com.sobremesa.birdwatching.synchronizers.SightingSynchronizer;
import com.sobremesa.birdwatching.util.SyncUtil;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.*;

import retrofit.RetrofitError;

/**
 * Created by omegatai on 2014-06-17.
 */
public class DownloadBirdDescriptionTask extends AsyncTask<RemoteSighting, Void, RemoteBirdDescription> {



    @Override
    protected RemoteBirdDescription doInBackground(RemoteSighting... params) {

        Context context = BAMApplication.getContext();


        String url = "";
        RemoteSighting bird = params[0];

        /* Wiki sciName url conventions: this might change */
        String newTitle = bird.getComName().replace(" ", "_");
        newTitle = newTitle.toLowerCase();
        newTitle = Character.toUpperCase(newTitle.charAt(0)) + newTitle.substring(1);


        // Sanitize url
        newTitle = Normalizer.normalize(newTitle, Normalizer.Form.NFKD);
        newTitle = newTitle.replace("&", "%26");

        url = "http://en.wikipedia.org/w/api.php?format=xml&action=query&prop=extracts&titles=" + newTitle + "&redirects=true&format=json";

        if(Patterns.WEB_URL.matcher(url).matches()) {
            String descriptionJson = getJsonResult(url);

            Log.d("descripton", descriptionJson);
            Pattern pattern1 = Pattern.compile("\"extract\":\"(.*)\"\\}\\}");
            Matcher matcher1 = pattern1.matcher(descriptionJson);


            if (matcher1.find()) {
                String descriptionStr = matcher1.group(1);
                ArrayList<RemoteBirdDescription> descriptions = new ArrayList<RemoteBirdDescription>(1);
                RemoteBirdDescription description = new RemoteBirdDescription();
                description.setDescription(descriptionStr);
                description.setSciName(bird.getSciName());
                descriptions.add(description);

                Cursor localBirdDescriptionCursor = context.getContentResolver().query(BAMContentProvider.Uris.BIRD_DESCRIPTIONS_URI, BirdDescriptionTable.ALL_COLUMNS, BirdDescriptionTable.SCI_NAME + "=?", new String[] {bird.getSciName()}, null);
                localBirdDescriptionCursor.moveToFirst();
                SyncUtil.synchronizeRemoteBirdDescriptions(descriptions, localBirdDescriptionCursor, localBirdDescriptionCursor.getColumnIndex(BirdDescriptionTable.DESCRIPTION), localBirdDescriptionCursor.getColumnIndex(BirdDescriptionTable.SCI_NAME),
                        new BirdDescriptionSynchronizer(context), null);
                localBirdDescriptionCursor.close();

                return description;
            }

        }

        return null;
    }


    private String getJsonResult(String url) {
        InputStream is = null;
        String json = "";

        // Making HTTP request
        try {
            // defaultHttpClient
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setContentCharset(params, "utf-8");

            HttpClient httpclient = new DefaultHttpClient(params);

            HttpGet httpPost = new HttpGet(url);

            HttpResponse httpResponse = httpclient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            json = EntityUtils.toString(httpEntity, HTTP.UTF_8);
//            json = "\"extract\":\"measure 24.8\\u00a0mm \\u00d7\\u00a017.55\\u00a0mm";
            json = json.replace("\\u00a0", "");
//            json = json.replace("\\u", "\\\\u");
            json = json.replace("\\n", "");
            json = StringEscapeUtils.unescapeJava(json);
//            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            URL url1 = new URL(url);
//            InputStreamReader inputStreamReader = new InputStreamReader((InputStream)fis, "UTF-8");
//
//            BufferedReader reader =  new BufferedReader(new InputStreamReader(url1.openStream(), "UTF-8"));
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//                Log.d("line", line);
//            }
//            is.close();
//            json = new String(sb.toString().getBytes("UTF-8"));
//
//        } catch (Exception e) {
//            Log.e("Buffer Error", "Error converting result " + e.toString());
//        }

        return json;
    }
}
