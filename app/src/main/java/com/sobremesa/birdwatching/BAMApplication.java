package com.sobremesa.birdwatching;

import android.content.SharedPreferences;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by omegatai on 2014-06-17.
 */
public class BAMApplication extends Application {

    private static Context sContext;
    private static Handler sHandler;
    private static ImageLoader sImageLoader;

    private static boolean mActivityVisible;


    public static boolean isActivityVisible() {
        return mActivityVisible;
    }

    public static void activityResumed() {
        mActivityVisible = true;
    }

    public static void activityPaused() {
        mActivityVisible = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sHandler = new Handler();
        sContext = getApplicationContext();

        Drawable placeHolder = getResources().getDrawable(R.drawable.default_bird);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                cacheInMemory(true).
                cacheOnDisk(true).
                imageScaleType(ImageScaleType.EXACTLY).
                showImageForEmptyUri(placeHolder).
                showImageOnFail(placeHolder).
                showImageOnLoading(placeHolder).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(sContext)
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        sImageLoader = ImageLoader.getInstance();
        sImageLoader.init(config);
    }



    public static void runOnUiThread(Runnable runnable) {
        sHandler.post(runnable);
    }

    public static final Context getContext() {
        return sContext;
    }

    public static final ImageLoader getImageLoader() {
        return sImageLoader;
    }

    public static SharedPreferences getSharedPreferences() {
        SharedPreferences prefs = sContext.getSharedPreferences(sContext.getPackageName(), Context.MODE_PRIVATE);
        return prefs;
    }

    public static String getResourceString(int resource) {
        return sContext.getResources().getString(resource);
    }

}
