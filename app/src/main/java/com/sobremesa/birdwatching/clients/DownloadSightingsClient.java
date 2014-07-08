package com.sobremesa.birdwatching.clients;

import com.sobremesa.birdwatching.models.remote.RemoteSighting;

import java.util.ArrayList;
import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by omegatai on 2014-06-17.
 */
public interface DownloadSightingsClient {
    @GET("/recent")
    ArrayList<RemoteSighting> downloadSightings(@Query("lat") Double lat, @Query("lng") Double lng, @Query("dist") Integer dist, @Query("back") Integer back, @Query("fmt") String format);
}

