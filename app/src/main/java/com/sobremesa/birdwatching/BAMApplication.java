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

/**
 * Created by omegatai on 2014-06-17.
 */
public class BAMApplication extends Application {

    private static Context sContext;
    private static Handler sHandler;

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

//        Drawable placeHolder = getResources().getDrawable(R.drawable.fao_dogs_gray_800);
//
//        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
//                cacheInMemory(true).
//                cacheOnDisc(true).
//                showImageForEmptyUri(placeHolder).
//                showImageOnFail(placeHolder).
//                showImageOnLoading(placeHolder).build();
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(FAOApplication.getContext())
//                .defaultDisplayImageOptions(defaultOptions)
//                .build();
//
//        ImageLoader.getInstance().init(config);
    }



    public static void runOnUiThread(Runnable runnable) {
        sHandler.post(runnable);
    }

    public static final Context getContext() {
        return sContext;
    }

    public static SharedPreferences getSharedPreferences() {
        SharedPreferences prefs = sContext.getSharedPreferences(sContext.getPackageName(), Context.MODE_PRIVATE);
        return prefs;
    }

    public static String getResourceString(int resource) {
        return sContext.getResources().getString(resource);
    }

}
