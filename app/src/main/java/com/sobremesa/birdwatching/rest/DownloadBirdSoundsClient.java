package com.sobremesa.birdwatching.rest;

import com.sobremesa.birdwatching.models.remote.RemoteBirdRecordings;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;

import java.util.ArrayList;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by omegatai on 2014-06-17.
 */
public interface DownloadBirdSoundsClient {
    @GET("/recordings")
    RemoteBirdRecordings downloadBirdSounds(@Query("query") String query);
}

